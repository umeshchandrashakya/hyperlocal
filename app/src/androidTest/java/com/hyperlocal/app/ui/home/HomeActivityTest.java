package com.hyperlocal.app.ui.home;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.hyperlocal.app.R;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author ${Umesh} on 05-07-2018.
 */
public class HomeActivityTest {
    @Rule
    public ActivityTestRule<HomeActivity> activityActivityTestRule = new ActivityTestRule<HomeActivity>(HomeActivity.class);

    private HomeActivity homeActivity=null;


    @Before
    public void setUp() throws Exception {
        homeActivity = activityActivityTestRule.getActivity();
    }

    // Run on real device because firebase auth do not work on amulator

    @Test
    public void testHomeActivityLunchORNot(){
        View view = homeActivity.findViewById(R.id.linear_layout);
        Assert.assertNotNull(view);
    }

    @After
    public void tearDown() throws Exception {
        homeActivity = null;
    }

}