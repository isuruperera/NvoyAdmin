package com.proximosolutions.nvoyadmin.Controller;

import android.app.Fragment;
import android.app.SearchManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proximosolutions.nvoyadmin.MainLogic.Courier;
import com.proximosolutions.nvoyadmin.R;

import java.util.ArrayList;

/**
 * Created by Isuru Tharanga on 3/27/2017.
 */

public class FragmentSuspendCourier extends Fragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
    View suspendCourier;

    //
    private SearchManager searchManager;
    private SearchView searchView;
    private ExpListAdapter expListAdapter;
    private ExpandableListView listView;
    private ArrayList<ParentRow> parentList = new ArrayList<ParentRow>();
    private ArrayList<ParentRow> showTheseParentList = new ArrayList<ParentRow>();
    private MenuItem searchItem;
    //private SuspendCourier
    //


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.child("Couriers").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setParentList(new ArrayList<ParentRow>());
                Iterable<DataSnapshot> courierList = dataSnapshot.getChildren();


                for (DataSnapshot courier : courierList) {
                    Courier tempCourier = courier.getValue(Courier.class);
                    String name = tempCourier.getFirstName() + " " + tempCourier.getLastName();
                    String email = tempCourier.getUserID();
                    email = DecodeString(email);

                    ChildRow childRow = new ChildRow(email,R.drawable.ic_menu_courier_payments);
                    ParentRow parentRow = null;
                    //childRows.add();
                    //childRows.add(new ChildRow("AAAABBBBBBBB",R.drawable.ic_menu_courier_payments));
                    parentRow = new ParentRow(name, childRow);
                    getParentList().add(parentRow);


                }
                setListView((ExpandableListView) getView().findViewById(R.id.expandable_list_search));
                setExpListAdapter(new ExpListAdapter(suspendCourier.getContext(), getParentList()));
                getListView().setAdapter(getExpListAdapter());
                //displayList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        suspendCourier = inflater.inflate(R.layout.suspend_courier,container,false);
        return suspendCourier;

    }
            public String EncodeString(String string) {
                return string.replace(".", ",");
            }

            public String DecodeString(String string) {
                return string.replace(",", ".");
            }


    @Override
    public boolean onQueryTextSubmit(String query) {
        getExpListAdapter().filterData(query);
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        getExpListAdapter().filterData(newQuery);
        expandAll();
        return false;
    }

    public void expandAll(){
        int count = getExpListAdapter().getGroupCount();
        for(int i = 0;i<count;i++){
            getListView().expandGroup(i);
        }
    }

    @Override
    public boolean onClose() {
        getExpListAdapter().filterData("");
        expandAll();
        return false;
    }

    public ExpListAdapter getExpListAdapter() {
        return expListAdapter;
    }

    public void setExpListAdapter(ExpListAdapter expListAdapter) {
        this.expListAdapter = expListAdapter;
    }

    public ExpandableListView getListView() {
        return listView;
    }

    public void setListView(ExpandableListView listView) {
        this.listView = listView;
    }

    public ArrayList<ParentRow> getParentList() {
        return parentList;
    }

    public void setParentList(ArrayList<ParentRow> parentList) {
        this.parentList = parentList;
    }

    public ArrayList<ParentRow> getShowTheseParentList() {
        return showTheseParentList;
    }

    public void setShowTheseParentList(ArrayList<ParentRow> showTheseParentList) {
        this.showTheseParentList = showTheseParentList;
    }



}
