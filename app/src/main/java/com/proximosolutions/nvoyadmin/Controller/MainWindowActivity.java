package com.proximosolutions.nvoyadmin.Controller;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proximosolutions.nvoyadmin.MainLogic.Courier;
import com.proximosolutions.nvoyadmin.MainLogic.NvoyUser;
import com.proximosolutions.nvoyadmin.R;

import java.util.ArrayList;

public class MainWindowActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

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
        setContentView(R.layout.activity_main_window2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        parentList = new ArrayList<ParentRow>();
        showTheseParentList = new ArrayList<ParentRow>();



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
                expListAdapter = new ExpListAdapter(MainWindowActivity.this,parentList);
                listView.setAdapter(expListAdapter);
                //displayList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //expandAll();

    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_window, menu);

        searchItem = menu.findItem(R.id.action_search1);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        searchView.requestFocus();


        return true;
    }

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

        ArrayList<NvoyUser> Couriers = new ArrayList<>();
        ArrayList<NvoyUser> Customers = new ArrayList<>();







        childRows.add(new ChildRow("AAAAAAAAA",R.drawable.ic_menu_courier_payments));
        childRows.add(new ChildRow("AAAABBBBBBBB",R.drawable.ic_menu_courier_payments));
        parentRow = new ParentRow("First Group", childRows);
        parentList.add(parentRow);

        parentRow = null;
        childRows = null;
        childRows = new ArrayList<ChildRow>();
        childRows.add(new ChildRow("KKKKKKKK",R.drawable.ic_menu_courier_payments));
        childRows.add(new ChildRow("KKKKKKBBBBBBBB",R.drawable.ic_menu_courier_payments));
        parentRow = new ParentRow("Second Group", childRows);
        parentList.add(parentRow);


    }

    private void expandAll(){
        int count = expListAdapter.getGroupCount();
        for(int i = 0;i<count;i++){
            listView.expandGroup(i);
        }
    }

    private void displayList(){
        //loadData();
        listView = (ExpandableListView)findViewById(R.id.expandable_list_search);
        expListAdapter = new ExpListAdapter(MainWindowActivity.this,parentList);
        listView.setAdapter(expListAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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


                return true;
            }

            //return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
