package com.proximosolutions.nvoyadmin.Controller;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proximosolutions.nvoyadmin.MainLogic.Courier;
import com.proximosolutions.nvoyadmin.MainLogic.Nvoy;
import com.proximosolutions.nvoyadmin.R;

import java.util.ArrayList;

import static com.proximosolutions.nvoyadmin.R.id.btn_add_courier;
import static com.proximosolutions.nvoyadmin.R.id.contact_no;
import static com.proximosolutions.nvoyadmin.R.id.email_address;
import static com.proximosolutions.nvoyadmin.R.id.email_sign_in_button;
import static com.proximosolutions.nvoyadmin.R.id.firstName;
import static com.proximosolutions.nvoyadmin.R.id.lastName;
import static com.proximosolutions.nvoyadmin.R.id.national_ic;

public class MainWindow extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private FirebaseAuth mAuth;
    private Button addCourier;

    private SearchManager searchManager;
    private SearchView searchView;
    private ExpListAdapter expListAdapter;
    private ExpandableListView listView;
    private ArrayList<ParentRow> parentList = new ArrayList<ParentRow>();
    private ArrayList<ParentRow> showTheseParentList = new ArrayList<ParentRow>();
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_window);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*//FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer.openDrawer(GravityCompat.START);

        searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        parentList = new ArrayList<ParentRow>();
        showTheseParentList = new ArrayList<ParentRow>();


        //
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.child("Couriers").addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                parentList = new ArrayList<ParentRow>();
                Iterable<DataSnapshot> courierList = dataSnapshot.getChildren();
                for(DataSnapshot courier:courierList){
                    Courier tempCourier = courier.getValue(Courier.class);
                    String name = tempCourier.getFirstName() + " " + tempCourier.getLastName();
                    String email = tempCourier.getUserID();
                    email = DecodeString(email);

                    ArrayList<ChildRow> childRows = new ArrayList<ChildRow>();
                    ParentRow parentRow = null;

                    childRows.add(new ChildRow(email,R.drawable.ic_menu_courier_payments));
                    //childRows.add(new ChildRow("AAAABBBBBBBB",R.drawable.ic_menu_courier_payments));
                    parentRow = new ParentRow(name, childRows);
                    parentList.add(parentRow);



                }
                listView = (ExpandableListView)findViewById(R.id.expandable_list_search);
                expListAdapter = new ExpListAdapter(MainWindow.this,parentList);
                listView.setAdapter(expListAdapter);
                //displayList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //



        displayList();

        expandAll();

    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_window, menu);

        searchItem = menu.findItem(R.id.action_search1);
        searchView = (SearchView)MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        searchView.requestFocus();


        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.app.FragmentManager fragmentManager = getFragmentManager();
        if (id == R.id.nav_add_courier) {

            fragmentManager.beginTransaction()
                    .replace(R.id.content_main_window, new FragmentAddCourier())

                    .commit();
        } else if (id == R.id.nav_suspend_courier) {

        } else if (id == R.id.nav_suspend_customer) {

        } else if (id == R.id.nav_courier_payments) {

        } else if (id == R.id.nav_customer_payments) {

        } else if (id == R.id.nav_log_out) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Exit Application?");
                alertDialogBuilder
                        .setMessage("Click yes to exit!")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        moveTaskToBack(true);
                                        android.os.Process.killProcess(android.os.Process.myPid());
                                        System.exit(1);
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onClose() {
        expListAdapter.filterData("");
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        expListAdapter.filterData(query);
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        expListAdapter.filterData(newQuery);
        expandAll();
        return false;
    }

    private void loadData(){
        ArrayList<ChildRow> childRows = new ArrayList<ChildRow>();
        ParentRow parentRow = null;

        childRows.add(new ChildRow("AAAAAAAAA",R.mipmap.ic_launcher));
        childRows.add(new ChildRow("AAAABBBBBBBB",R.mipmap.ic_launcher));
        parentRow = new ParentRow("First Group", childRows);
        parentList.add(parentRow);

        childRows = new ArrayList<ChildRow>();
        childRows.add(new ChildRow("KKKKKKKK",R.mipmap.ic_launcher));
        childRows.add(new ChildRow("KKKKKKBBBBBBBB",R.mipmap.ic_launcher));
        parentRow = new ParentRow("Second Group", childRows);
        parentList.add(parentRow);


    }

    private void expandAll(){
        int count = expListAdapter.getGroupCount();
        for(int i = 0;i<count;i++){
            //listView.expandGroup(i);
            listView.collapseGroup(i);
        }
    }

    private void displayList(){
        loadData();

        listView = (ExpandableListView)findViewById(R.id.expandable_list_search);
        expListAdapter = new ExpListAdapter(MainWindow.this,parentList);
        listView.setAdapter(expListAdapter);
    }
}
