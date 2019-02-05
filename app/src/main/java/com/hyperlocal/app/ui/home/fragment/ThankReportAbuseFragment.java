package com.hyperlocal.app.ui.home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyperlocal.app.R;
import com.hyperlocal.app.ui.home.HomeActivity;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author ${Umesh} on 20-04-2018.
 */


public class ThankReportAbuseFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.thank_report_abuse_fragment,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @OnClick(R.id.btn_next)
    public void doneButtonClick(){
       FragmentManager manager = getActivity().getSupportFragmentManager();
        List<Fragment> fragments = manager.getFragments();
        for (Fragment ch : fragments) {
            FragmentManager childFragmentManager = ch.getChildFragmentManager();
            List<Fragment>childFragmentList = childFragmentManager.getFragments();
            for (Fragment child:childFragmentList){
                childFragmentManager.popBackStackImmediate();
            }

        }
    }
}
