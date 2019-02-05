package com.hyperlocal.app.ui.home;

import android.app.Activity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hyperlocal.app.R;

/**
 * @Author ${Umesh} on 04-04-2018.
 */

public class EndDrawerToggle implements DrawerLayout.DrawerListener {
    private DrawerLayout drawerLayout;
    private DrawerArrowDrawable arrowDrawable;
    private AppCompatImageButton toggleButton;
    private String openDrawerContentDesc;
    private String closeDrawerContentDesc;
    private Activity activity;

    public EndDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar,
                           int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        this.activity = activity;
        this.drawerLayout = drawerLayout;
        this.openDrawerContentDesc = activity.getString(openDrawerContentDescRes);
        this.closeDrawerContentDesc = activity.getString(closeDrawerContentDescRes);

        arrowDrawable = new DrawerArrowDrawable(toolbar.getContext());
        arrowDrawable.setDirection(DrawerArrowDrawable.ARROW_DIRECTION_END);

        toggleButton = new AppCompatImageButton(toolbar.getContext(), null,
                R.attr.toolbarNavigationButtonStyle);
        toolbar.addView(toggleButton, new Toolbar.LayoutParams(GravityCompat.END));
        toggleButton.setImageDrawable(arrowDrawable);
        toggleButton.setOnClickListener(v -> toggle());
    }

    public void syncState() {
        setPosition(drawerLayout.isDrawerOpen(GravityCompat.END) ? 1f : 0f);
    }

    public void toggle() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END))
            drawerLayout.closeDrawer(GravityCompat.END);
        else drawerLayout.openDrawer(GravityCompat.END);
    }

    public void setPosition(float position) {
        if (position == 1f) {
            arrowDrawable.setVerticalMirror(true);
            toggleButton.setContentDescription(closeDrawerContentDesc);
        }
        else if (position == 0f) {
            arrowDrawable.setVerticalMirror(false);
            toggleButton.setContentDescription(openDrawerContentDesc);
        }
        arrowDrawable.setProgress(position);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        setPosition(Math.min(1f, Math.max(0, slideOffset)));

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                // Do something for lollipop and above versions

                Window window = activity.getWindow();

                // clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                // finally change the color to any color with transparency

                window.setStatusBarColor(activity.getResources().getColor(R.color.bg_red));}

        } catch (Exception e) {
           e.printStackTrace();
        }

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        setPosition(1f);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        setPosition(0f);
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                // Do something for lollipop and above versions

                Window window = activity.getWindow();

                // clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                // finally change the color again to dark
                window.setStatusBarColor(activity.getResources().getColor(R.color.bg_red_alpha));
            }
        } catch (Exception e) {
    e.printStackTrace();
        }

    }

    @Override
    public void onDrawerStateChanged(int newState) {
     System.out.println();
    }

}
