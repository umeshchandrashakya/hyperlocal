package com.hyperlocal.app.ui.home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyperlocal.app.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author ${Umesh} on 17-04-2018.
 */

public class SpreadTheWordFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgament_spread_the_word,container,false);
        ButterKnife.bind(this,view);

        return view;
    }

    @OnClick(R.id.btn_share)
   public void buttonClick(){
      shareTheAppLink();
   }

    private void shareTheAppLink() {
        final String appPackageName = getActivity().getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + appPackageName);
        sendIntent.setType("text/plain");
        getActivity().startActivity(sendIntent);
    }
}
