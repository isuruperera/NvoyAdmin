package com.proximosolutions.nvoyadmin.Controller;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.proximosolutions.nvoyadmin.MainLogic.NvoyUser;
import com.proximosolutions.nvoyadmin.R;

/**
 * Created by Isuru Tharanga on 3/27/2017.
 */

public class FragmentUserProfile extends Fragment {
    View userProfile;
    private NvoyUser user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userProfile = inflater.inflate(R.layout.activity_user_profile,container,false);
        ((TextView)userProfile.findViewById(R.id.user_email)).setText(user.getUserID());
        return userProfile;
    }

    public NvoyUser getUser() {
        return user;
    }

    public void setUser(NvoyUser user) {
        this.user = user;
    }
}
