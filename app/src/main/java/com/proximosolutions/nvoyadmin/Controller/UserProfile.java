package com.proximosolutions.nvoyadmin.Controller;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proximosolutions.nvoyadmin.R;

import static com.proximosolutions.nvoyadmin.Controller.IntentLock.lockIntent;

public class UserProfile extends AppCompatActivity {

    Button suspendBtn;
    Button viewTransactionsBtn;
    String currentUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_user_profile);

        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras != null){
                ((android.support.design.widget.CollapsingToolbarLayout)findViewById(R.id.user_profile_toolbar)).setTitle(extras.get("userName").toString());
                ((TextView)findViewById(R.id.user_email)).setText(extras.get("email").toString());
                ((TextView)findViewById(R.id.user_contact_no)).setText(extras.get("contact_no").toString());
                ((TextView)findViewById(R.id.user_nic)).setText(extras.get("nic").toString());


                if(extras.get("isActive").equals(true)){
                    ((TextView)findViewById(R.id.user_is_active)).setText("Active");
                    ((Button)findViewById(R.id.btn_suspend_courier)).setText("Suspend");
                }else{
                    ((TextView)findViewById(R.id.user_is_active)).setText("Suspended");
                    ((Button)findViewById(R.id.btn_suspend_courier)).setText("Activate");
                }
                if(extras.get("isCourier").equals(true)){
                    if(extras.get("type").equals(true)){
                        ((TextView)findViewById(R.id.user_type)).setText("Express Courier");
                    }else{
                        ((TextView)findViewById(R.id.user_type)).setText("Regular Courier");
                    }

                }else{
                    ((TextView)findViewById(R.id.user_type)).setText("Customer");

                }

            }
        }else{
            ((TextView)findViewById(R.id.user_email)).setText((String)savedInstanceState.getSerializable("email"));
            ((TextView)findViewById(R.id.contact_no)).setText((String)savedInstanceState.getSerializable("contact_no"));
            ((TextView)findViewById(R.id.user_nic)).setText((String)savedInstanceState.getSerializable("nic"));
            if(savedInstanceState.getSerializable("isActive").equals(true)){
                ((TextView)findViewById(R.id.user_is_active)).setText("Active");
                ((Button)findViewById(R.id.btn_suspend_courier)).setText("Suspend");
            }else{
                ((TextView)findViewById(R.id.user_is_active)).setText("Suspended");
                ((Button)findViewById(R.id.btn_suspend_courier)).setText("Activate");
            }

            if(savedInstanceState.getSerializable("isCourier").equals(true)){
                if(savedInstanceState.getSerializable("type").equals(true)){
                    ((TextView)findViewById(R.id.user_type)).setText("Express Courier");
                }else{
                    ((TextView)findViewById(R.id.user_type)).setText("Regular Courier");
                }
            }else{
                ((TextView)findViewById(R.id.user_type)).setText("Customer");
            }




        }
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference();
        suspendBtn = (Button) findViewById(R.id.btn_suspend_courier);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        Log.d("Suspend User","Started");
        suspendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialogBuilder.setTitle("Change user status?");
                alertDialogBuilder
                        .setMessage("Click yes to change!")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if((((TextView)findViewById(R.id.user_type)).getText()).equals("Customer")){
                                            currentUserType = "Customers";
                                            Log.d("Suspend User","Customer detected");
                                        }else{
                                            Log.d("Suspend User","Courier detected");
                                            currentUserType = "Couriers";
                                        }


                                        if((((TextView)findViewById(R.id.user_is_active)).getText()).equals("Active")){
                                            databaseReference.child(currentUserType).child(EncodeString(((TextView)findViewById(R.id.user_email)).getText().toString())).child("active").setValue(false);
                                            Toast.makeText(UserProfile.this, "User Suspended",
                                                    Toast.LENGTH_LONG).show();

                                            ((TextView)findViewById(R.id.user_is_active)).setText("Suspended");
                                            ((Button)findViewById(R.id.btn_suspend_courier)).setText("Activate");
                                        }else{
                                            databaseReference.child(currentUserType).child(EncodeString(((TextView)findViewById(R.id.user_email)).getText().toString())).child("active").setValue(true);
                                            Toast.makeText(UserProfile.this, "User Activated",
                                                    Toast.LENGTH_LONG).show();
                                            ((TextView)findViewById(R.id.user_is_active)).setText("Active");
                                            ((Button)findViewById(R.id.btn_suspend_courier)).setText("Suspend");
                                        }
                                        Log.d("Suspend User","Status Changed");
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
        });

    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                //lockIntent = false;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        //System.out.println(getBaseContext().find);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lockIntent = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        lockIntent = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        lockIntent = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lockIntent = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        lockIntent = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //lockIntent = false;
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }
}
