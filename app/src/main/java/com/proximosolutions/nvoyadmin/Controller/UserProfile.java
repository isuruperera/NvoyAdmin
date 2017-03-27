package com.proximosolutions.nvoyadmin.Controller;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proximosolutions.nvoyadmin.R;

import static com.proximosolutions.nvoyadmin.Controller.IntentLock.lockIntent;

public class UserProfile extends AppCompatActivity {

    //View

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //lockIntent = true;
            //this.setActionBar(new ActionBar());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
           // getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_user_profile);
        //String userName = (String)savedInstanceState.get("userName");
        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras != null){
                ((android.support.design.widget.CollapsingToolbarLayout)findViewById(R.id.user_profile_toolbar)).setTitle(extras.get("userName").toString());
                //((android.support.design.widget.CollapsingToolbarLayout)findViewById(R.id.user_profile_toolbar)).set
                //String s = extras.get("email").toString();
                ((TextView)findViewById(R.id.user_email)).setText(extras.get("email").toString());
            }
        }else{
            ((TextView)findViewById(R.id.user_email)).setText((String)savedInstanceState.getSerializable("email"));
        }

        //(new TextView(R.id.action_search1));

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
