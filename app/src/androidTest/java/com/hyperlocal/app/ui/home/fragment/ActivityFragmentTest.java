package com.hyperlocal.app.ui.home.fragment;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.hyperlocal.app.R;
import com.hyperlocal.app.ui.home.HomeActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;

/**
 * @author ${Umesh} on 05-07-2018.
 */
public class ActivityFragmentTest {
    @Rule
    public ActivityTestRule<HomeActivity>rule = new ActivityTestRule<HomeActivity>(HomeActivity.class);

    HomeActivity homeActivity;

    @Before
    public void setUp() throws Exception {
        homeActivity = rule.getActivity();

    }

    @Test
    public void fragmentLunch(){
        FrameLayout frameLayout = homeActivity.findViewById(R.id.container);
        assertNotNull(frameLayout);
        ActivityFragment fragment = new ActivityFragment();
        homeActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(frameLayout.getId(),fragment).commit();
        getInstrumentation().waitForIdleSync();
        View view = fragment.getView().findViewById(R.id.recycler_view);
        assertNotNull(view);
    }

    @Test
    public void fragmentItemClick(){
        FrameLayout frameLayout = homeActivity.findViewById(R.id.container);
        ActivityFragment fragment = new ActivityFragment();
        homeActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(frameLayout.getId(),fragment).commit();
        getInstrumentation().waitForIdleSync();
        RecyclerView view = fragment.getView().findViewById(R.id.recycler_view);
        int i = view.getAdapter().getItemCount();
        if(i>0){
            Espresso.onView(withId(fragment.getView().findViewById(R.id.recycler_view))).perform(RecyclerViewActions.scrollToPosition(3));
            Espresso.onView(withId(fragment.getView().findViewById(R.id.recycler_view))).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        }
    }


    @After
    public void tearDown() throws Exception {

    }


}