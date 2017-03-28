package com.proximosolutions.nvoyadmin.Controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proximosolutions.nvoyadmin.MainLogic.Courier;
import com.proximosolutions.nvoyadmin.MainLogic.Customer;
import com.proximosolutions.nvoyadmin.R;

import java.util.ArrayList;

public class MainWindow extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private FirebaseAuth mAuth;
    private Button addCourier;
    private SearchManager searchManager;
    private SearchManager searchManagerCustomer;
    private SearchView searchView;
    private SearchView searchViewCustomer;
    private ExpListAdapter expListAdapter;
    private ExpListAdapterCustomer expListAdapterCustomer;
    private ExpandableListView listView;
    private ExpandableListView listViewCustomer;
    private ArrayList<ParentRow> parentList = new ArrayList<ParentRow>();
    private ArrayList<ParentRow> parentListCustomer = new ArrayList<ParentRow>();
    private ArrayList<ParentRow> showTheseParentList = new ArrayList<ParentRow>();
    private ArrayList<ParentRow> showTheseParentListCustomer = new ArrayList<ParentRow>();
    private MenuItem searchItem;
    FragmentSearchCourier fragmentSearchCourier;
    FragmentSearchCustomer fragmentSearchCustomer;
    private boolean isCourier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_window);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fragmentSearchCourier = new FragmentSearchCourier();
        fragmentSearchCustomer = new FragmentSearchCustomer();
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

        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        parentList = new ArrayList<ParentRow>();
        showTheseParentList = new ArrayList<ParentRow>();

        searchManagerCustomer = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        parentListCustomer = new ArrayList<ParentRow>();
        showTheseParentListCustomer = new ArrayList<ParentRow>();

        /*View v = this.findViewById(R.id.action_search1);
        v.setVisibility(View.GONE);*/




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

        if (isCourier) {
            searchItem = menu.findItem(R.id.action_search1);

            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
            searchView.setOnQueryTextListener(this);
            searchView.setOnCloseListener(this);
            searchView.requestFocus();
            //searchItem.setVisible(false);
        } else {
            searchItem = menu.findItem(R.id.action_search1);
            searchViewCustomer = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchViewCustomer.setSearchableInfo(searchManagerCustomer.getSearchableInfo(getComponentName()));
            searchViewCustomer.setIconifiedByDefault(false);
            searchViewCustomer.setOnQueryTextListener(this);
            searchViewCustomer.setOnCloseListener(this);
            searchViewCustomer.requestFocus();
            //searchItem.setVisible(false);
        }



        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

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
            View v = this.findViewById(R.id.action_search1);
            v.setVisibility(View.GONE);

        } else if (id == R.id.nav_search_courier) {
            isCourier = true;
            fragmentSearchCourier.setExpListAdapter(expListAdapter);
            fragmentSearchCourier.setListView(listView);
            fragmentSearchCourier.setParentList(parentList);
            fragmentSearchCourier.setShowTheseParentList(showTheseParentList);
            fragmentManager.beginTransaction()
                    .replace(R.id.content_main_window, fragmentSearchCourier)
                    .commit();
            View v = this.findViewById(R.id.action_search1);
            v.setVisibility(View.VISIBLE);



        } else if (id == R.id.nav_customer_payments) {
            isCourier = false;
            fragmentSearchCustomer.setExpListAdapter(expListAdapterCustomer);
            fragmentSearchCustomer.setListView(listViewCustomer);
            fragmentSearchCustomer.setParentList(parentListCustomer);
            fragmentSearchCustomer.setShowTheseParentList(showTheseParentListCustomer);
            fragmentManager.beginTransaction()
                    .replace(R.id.content_main_window, fragmentSearchCustomer)
                    .commit();
            View v = this.findViewById(R.id.action_search1);
            v.setVisibility(View.VISIBLE);


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
                                        FirebaseAuth.getInstance().signOut();
                                        onDestroy();
                                        moveTaskToBack(true);
                                        android.os.Process.killProcess(android.os.Process.myPid());
                                        System.exit(1);
                                        finish();


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
        if (isCourier) {
            fragmentSearchCourier.getExpListAdapter().filterData("");
        } else {
            fragmentSearchCustomer.getExpListAdapter().filterData("");
        }

        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // expListAdapter.filterData(query);
        if (isCourier) {
            fragmentSearchCourier.getExpListAdapter().filterData(query);
            fragmentSearchCourier.expandAll();
        } else {
            fragmentSearchCustomer.getExpListAdapter().filterData(query);
            fragmentSearchCustomer.expandAll();
        }

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        if (isCourier) {
            fragmentSearchCourier.getExpListAdapter().filterData(newQuery);

            fragmentSearchCourier.expandAll();
        } else {
            fragmentSearchCustomer.getExpListAdapter().filterData(newQuery);

            fragmentSearchCustomer.expandAll();
        }

        return false;
    }


    private void expandAll() {
        if (isCourier) {
            int count = expListAdapter.getGroupCount();
            for (int i = 0; i < count; i++) {
                listView.expandGroup(i);
                //listView.collapseGroup(i);
            }
        } else {
            int count = expListAdapterCustomer.getGroupCount();
            for (int i = 0; i < count; i++) {
                listViewCustomer.expandGroup(i);
                //listView.collapseGroup(i);
            }
        }

    }


}


