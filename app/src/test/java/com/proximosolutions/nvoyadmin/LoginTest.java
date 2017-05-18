package com.proximosolutions.nvoyadmin;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Test;
import java.util.regex.Pattern;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Isuru Tharanga on 3/20/2017.
 */

public class LoginTest {
    @Test
    public void authTest(){
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword("isuru.trgz@gmail.com","11111111");
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                assertTrue(auth.getCurrentUser()!=null);
            }
        });

    }


}
