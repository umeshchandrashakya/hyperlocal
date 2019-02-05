package com.hyperlocal.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.List;

/**
 * @Author ${Umesh} on 24-08-2017.
 */

public class NonSwipeViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragmentList;


    public NonSwipeViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList == null ? 0 : mFragmentList.size();
    }

    public void setFragments(List<Fragment> fragments) {
        mFragmentList = fragments;

    }

    @Override
    public CharSequence getPageTitle(int position) {

        return "";
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }



}