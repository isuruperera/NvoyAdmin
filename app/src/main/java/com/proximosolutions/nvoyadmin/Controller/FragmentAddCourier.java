package com.proximosolutions.nvoyadmin.Controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
                Courier courier = new Courier();
                courier.setUserID(EncodeString(((TextView)addCourier.findViewById(email_address)).getText().toString()));
                courier.setFirstName(((TextView)addCourier.findViewById(firstName)).getText().toString());
                courier.setLastName(((TextView)addCourier.findViewById(lastName)).getText().toString());
                courier.setContactNumber(((TextView)addCourier.findViewById(contact_no)).getText().toString());
                courier.setNic(((TextView)addCourier.findViewById(national_ic)).getText().toString());
                courier.setExpressCourier(((CheckBox)addCourier.findViewById(expressCourier)).isChecked());
                addCourier(courier);
                ((TextView)addCourier.findViewById(email_address)).setText("");
                ((TextView)addCourier.findViewById(firstName)).setText("");
                ((TextView)addCourier.findViewById(lastName)).setText("");
                ((TextView)addCourier.findViewById(national_ic)).setText("");
                ((TextView)addCourier.findViewById(contact_no)).setText("");
                Toast.makeText(getActivity(), "Successfully added the user!", Toast.LENGTH_SHORT).show();
                showProgress(false);


            }
        });

        return addCourier;

    }


    public void addCourier(Courier courier){
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(DecodeString(courier.getUserID()), courier.getNic());


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Couriers").child(courier.getUserID()).setValue(courier);

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
