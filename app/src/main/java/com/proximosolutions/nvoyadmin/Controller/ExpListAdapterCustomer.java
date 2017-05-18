package com.proximosolutions.nvoyadmin.Controller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proximosolutions.nvoyadmin.MainLogic.Customer;
import com.proximosolutions.nvoyadmin.MainLogic.NvoyUser;
import com.proximosolutions.nvoyadmin.R;

import java.util.ArrayList;

/**
 * Created by Isuru Tharanga on 3/26/2017.
 */

public class ExpListAdapterCustomer extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<ParentRow> parentRowList;
    private ArrayList<ParentRow> originalList;

    public ExpListAdapterCustomer(Context context, ArrayList<ParentRow> originalList) {
        this.context = context;
        this.parentRowList = new ArrayList<>();
        this.parentRowList.addAll(originalList);
        this.originalList = new ArrayList<>();
        this.originalList.addAll(originalList);
    }

    @Override
    public int getGroupCount() {
        return parentRowList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentRowList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return parentRowList.get(groupPosition).getChild();
    }

    @Override
    public long getGroupId(int groupID) {
        return groupID;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean b, View convertView, ViewGroup viewGroup) {
        ParentRow parentRow = (ParentRow)getGroup(groupPosition);

        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater)context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.parent_row,null);
        }

        TextView heading = (TextView)convertView.findViewById(R.id.parent_text);
        heading.setText(parentRow.getName().trim());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup viewGroup) {
        ChildRow childRow = (ChildRow)getChild(groupPosition,childPosition);
        System.out.println(groupPosition+" "+childPosition);
        if(convertView==null){
            LayoutInflater layoutInflater = (LayoutInflater)context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_row,null);
        }


        ImageView childIcon = (ImageView)convertView.findViewById(R.id.child_icon);
        childIcon.setImageResource(R.drawable.ic_menu_courier_payments);

        final View childText = (View)convertView.findViewById(R.id.child_text_container);
        String text = childRow.getText().trim();
        System.out.println(text);
        ((TextView)convertView.findViewById(R.id.child_text)).setText(text);
        ((TextView)convertView.findViewById(R.id.child_text_state)).setText(childRow.getStatus().trim());

        final View finalConvertView = convertView;

        childText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*Toast.makeText( finalConvertView.getContext()
                                ,childText.getText()
                                ,Toast.LENGTH_SHORT).show();*/

                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        final DatabaseReference databaseReference = firebaseDatabase.getReference();

                        //android.app.FragmentManager fragmentManager = MainWindow.getFragmentManager();
                        //final ValueEventListener valueEventListener =
                        String str = ((TextView)childText.findViewById(R.id.child_text)).getText().toString();

                       databaseReference.child("Customers").child(EncodeString(str.trim())).addValueEventListener(new ValueEventListener(){


                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {
                                   /*FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                   DatabaseReference databaseReference = firebaseDatabase.getReference();*/

                                   Intent userProfile = new Intent(finalConvertView.getContext(),UserProfile.class);
                                   Customer user = dataSnapshot.getValue(Customer.class);

                                   //Iterable<DataSnapshot> snap = dataSnapshot.getChildren();
//                                   System.out.println((user.getFirstName()+" "+user.getLastName()));
                                   userProfile.putExtra("userName",(user.getFirstName()+" "+user.getLastName()));
                                   userProfile.putExtra("email",DecodeString(user.getUserID()));
                                   userProfile.putExtra("contact_no",user.getContactNumber());
                                   userProfile.putExtra("nic",user.getNic());
                                   userProfile.putExtra("isActive",user.isActive());
                                   userProfile.putExtra("isCourier",false);
                                   //userProfile.putExtra("type",user.isExpressCourier());
                                   //lockActivity = false;
                                   databaseReference.child("Customers").child(EncodeString(((TextView)childText.findViewById(R.id.child_text)).getText().toString().trim())).removeEventListener(this);
                                   context.startActivity(userProfile);
                               Log.d("Database","Data Change listener detached from Couriers node");



                           }

                           @Override
                           public void onCancelled(DatabaseError databaseError) {

                           }



                       });
                        Log.d("Database","Data Change listener attached to Customers node");


                    }
                }

        );


        //System.out.println(((TextView)convertView.findViewById(R.id.child_text)).getText().toString());
        return convertView;
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public void filterData(String query){
        query = query.toLowerCase();
        parentRowList.clear();
        if(query.isEmpty() || query.equals("")){
            parentRowList.addAll(originalList);
        }else{
            for(ParentRow parentRow:originalList){
                if(parentRow.getChild().getText().toLowerCase().contains(query)){
                    parentRowList.add(parentRow);
                }



                /*C childList = parentRow.getChildList();
                ArrayList<ChildRow> newList = new ArrayList<ChildRow>();

                for(ChildRow childRow:childList){
                    if(childRow.getText().toLowerCase().contains(query)){
                        newList.add(childRow);
                    }
                }

                if(newList.size()>0){
                    ParentRow nParentRow = new ParentRow(parentRow.getName(),newList);
                    parentRowList.add(nParentRow);

                }*/
            }

        }
        notifyDataSetChanged();

    }



}
