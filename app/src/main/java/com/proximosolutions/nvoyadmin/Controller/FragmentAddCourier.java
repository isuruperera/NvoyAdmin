package com.proximosolutions.nvoyadmin.Controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proximosolutions.nvoyadmin.MainLogic.Courier;
import com.proximosolutions.nvoyadmin.R;

import java.util.concurrent.Executor;

import static com.proximosolutions.nvoyadmin.R.id.contact_no;
import static com.proximosolutions.nvoyadmin.R.id.email_address;
import static com.proximosolutions.nvoyadmin.R.id.expressCourier;
import static com.proximosolutions.nvoyadmin.R.id.firstName;
import static com.proximosolutions.nvoyadmin.R.id.lastName;
import static com.proximosolutions.nvoyadmin.R.id.national_ic;

/**
 * Created by Isuru Tharanga on 3/25/2017.
 */

public class FragmentAddCourier extends Fragment {
    View addCourier;
    Button addCourierBtn;
    FirebaseAuth mAuth;
    private View mLoginFormView;
    private View mProgressView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        addCourier = inflater.inflate(R.layout.add_courier,container,false);
        addCourierBtn = (Button)addCourier.findViewById(R.id.btn_add_courier);
        mLoginFormView = addCourier.findViewById(R.id.add_courier_frame);
        mProgressView = addCourier.findViewById(R.id.add_courier_progress);
        addCourierBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showProgress(true);
                if(validateInputs()){
                    Log.d("Add Courier","Inputs Valid");
                    Courier courier = new Courier();
                    courier.setUserID(EncodeString(((TextView)addCourier.findViewById(email_address)).getText().toString()));
                    courier.setFirstName(((TextView)addCourier.findViewById(firstName)).getText().toString());
                    courier.setLastName(((TextView)addCourier.findViewById(lastName)).getText().toString());
                    courier.setContactNumber(((TextView)addCourier.findViewById(contact_no)).getText().toString());
                    courier.setNic(((TextView)addCourier.findViewById(national_ic)).getText().toString());
                    courier.setExpressCourier(((CheckBox)addCourier.findViewById(expressCourier)).isChecked());
                    com.proximosolutions.nvoyadmin.MainLogic.Location location = new com.proximosolutions.nvoyadmin.MainLogic.Location();
                    location.setLongitude("");
                    location.setLatitude("");
                    courier.setActive(true);
                    addCourier(courier);
                    final Courier fCourier = courier;
                    mAuth = FirebaseAuth.getInstance();
                    Log.d("Add Courier","User Creation started in auth system");
                    mAuth.createUserWithEmailAndPassword(DecodeString(courier.getUserID()), courier.getNic())
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("Add Courier","User Creation Success");
                                    if(task.isSuccessful()){
                                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                        mDatabase.child("Couriers").child(fCourier.getUserID()).setValue(fCourier);
                                        mAuth.sendPasswordResetEmail(DecodeString(fCourier.getUserID()));
                                        ((TextView)addCourier.findViewById(email_address)).setText("");
                                        ((TextView)addCourier.findViewById(firstName)).setText("");
                                        ((TextView)addCourier.findViewById(lastName)).setText("");
                                        ((TextView)addCourier.findViewById(national_ic)).setText("");
                                        ((TextView)addCourier.findViewById(contact_no)).setText("");
                                        Toast.makeText(getActivity(), "Successfully added the user!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Log.d("Add Courier","User Creation failed. User already exist");
                                        ((TextView)addCourier.findViewById(email_address)).setError("Email already exists");
                                    }

                                }
                            });


                }else{
                    Log.d("Add Courier","Inputs Invalid");
                }
                showProgress(false);


            }
        });

        return addCourier;

    }

    private boolean validateInputs(){
        boolean isValid = true;
        if(!(((TextView)addCourier.findViewById(email_address)).getText()).toString().contains("@")){
            isValid = false;
            ((TextView)addCourier.findViewById(email_address)).setError("Invalid Email");
        }
        if((((TextView)addCourier.findViewById(firstName)).getText()).toString().equals("")){
            isValid = false;
            ((TextView)addCourier.findViewById(firstName)).setError("This cannot be empty");
        }
        if((((TextView)addCourier.findViewById(lastName)).getText()).toString().equals("")){
            isValid = false;
            ((TextView)addCourier.findViewById(lastName)).setError("This cannot be empty");
        }
        if((((TextView)addCourier.findViewById(national_ic)).getText()).toString().equals("")){
            isValid = false;
            ((TextView)addCourier.findViewById(national_ic)).setError("This cannot be empty");
        }
        if((((TextView)addCourier.findViewById(contact_no)).getText()).toString().equals("")){
            isValid = false;
            ((TextView)addCourier.findViewById(contact_no)).setError("This cannot be empty");
        }
        if(!(((TextView)addCourier.findViewById(national_ic)).getText()).toString().toUpperCase().endsWith("V")){
            isValid = false;
            ((TextView)addCourier.findViewById(national_ic)).setError("NIC is not valid");
        }
        if((((TextView)addCourier.findViewById(contact_no)).getText()).toString().length()!=10){
            isValid = false;
            ((TextView)addCourier.findViewById(contact_no)).setError("Contact number is not valid");
        }
        return isValid;
    }


    private boolean addCourier(final Courier courier){




        return  true;
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
