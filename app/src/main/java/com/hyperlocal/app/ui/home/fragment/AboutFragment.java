package com.hyperlocal.app.ui.home.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyperlocal.app.R;

import butterknife.ButterKnife;

/**
 *  @Author ${Umesh} on 05-04-2018.
 */

public class AboutFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragament_about,container,false);
        ButterKnife.bind(this,view);
        return view;
    }


}
