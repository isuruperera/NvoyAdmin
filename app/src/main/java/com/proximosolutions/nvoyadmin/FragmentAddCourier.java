package com.proximosolutions.nvoyadmin;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Isuru Tharanga on 3/25/2017.
 */

public class FragmentAddCourier extends Fragment {
    View addCourier;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        addCourier = inflater.inflate(R.layout.add_courier,container,false);
        return addCourier;
    }
}
