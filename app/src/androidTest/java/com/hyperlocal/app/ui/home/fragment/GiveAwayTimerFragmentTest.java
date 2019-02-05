package com.hyperlocal.app.ui.home.fragment;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import com.hyperlocal.app.R;
import com.hyperlocal.app.ui.home.HomeActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

/**
 * @author ${Umesh} on 05-07-2018.
 */
public class GiveAwayTimerFragmentTest {
    @Rule
    public ActivityTestRule<HomeActivity> rule = new ActivityTestRule<HomeActivity>(HomeActivity.class);

    HomeActivity homeActivity;
    GiveAwayTimerFragment fragment;


    @Before
    public void setUp() throws Exception {
        homeActivity = rule.getActivity();
        fragment = new GiveAwayTimerFragment();
        FrameLayout frameLayout = homeActivity.findViewById(R.id.container);
        assertNotNull(frameLayout);
        homeActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(frameLayout.getId(),fragment).commit();
        getInstrumentation().waitForIdleSync();
    }

    @Test
    public void fragmentLunch(){
        FrameLayout frameLayout = homeActivity.findViewById(R.id.container);
        assertNotNull(frameLayout);
        GiveAwayTimerFragment fragment = new GiveAwayTimerFragment();
        homeActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(frameLayout.getId(),fragment).commit();
        getInstrumentation().waitForIdleSync();
        View view = fragment.getView().findViewById(R.id.tv_item_text);
        assertNotNull(view);
    }

   @Test
   public void extendTimeButtonClickTest(){
       Espresso.onView(withId(R.id.btn_extend)).perform(click());
       getInstrumentation().waitForIdleSync();
   }

   @Test
   public void doneButtonClickTest(){
       Espresso.onView(withId(R.id.btn_extend)).perform(click());
       getInstrumentation().waitForIdleSync();
       Espresso.onView(withId(R.id.btn_done)).perform(click());
       getInstrumentation().waitForIdleSync();
   }

    @After
    public void tearDown() throws Exception {

    }
}