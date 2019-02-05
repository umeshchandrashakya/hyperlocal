package com.hyperlocal.app.ui.home.fragment;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
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
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertNotNull;

/**
 * @author ${Umesh} on 05-07-2018.
 */
public class AskFragmentTest {
    @Rule
    public ActivityTestRule<HomeActivity>rule = new ActivityTestRule<HomeActivity>(HomeActivity.class);
    HomeActivity homeActivity =null;
    AskFragment askFragment;

    @Before
    public void setUp() throws Exception {
     homeActivity = rule.getActivity();
        askFragment = new AskFragment();
        FrameLayout frameLayout = homeActivity.findViewById(R.id.container);
        assertNotNull(frameLayout);
        homeActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(frameLayout.getId(),askFragment).commit();
        getInstrumentation().waitForIdleSync();
    }


    @Test
    public void testAskFragmentLabelTextMatchesOrNot(){
        Espresso.onView(withId(R.id.tv_ask_label)).check(matches(withText(R.string.ask_neighbors_for)));
    }

    @Test
    public void checkNextButtonText(){
        Espresso.onView(withId(R.id.btn_next)).check(matches(withText(R.string.send)));
    }

    @Test
    public void lunchAskFragment(){
            Espresso.onView(withId(R.id.edit_invite_neighbors)).perform(typeText("give me two egg"));
            Espresso.closeSoftKeyboard();
    }

    @Test
    public void enterSomeTextForAskRequestAndClickOnNextButtonTest(){
        Espresso.onView(withId(R.id.edit_invite_neighbors)).perform(typeText("give me two egg"));
        Espresso.closeSoftKeyboard();
        getInstrumentation().waitForIdleSync();
        Espresso.onView(withId(R.id.btn_next)).perform(click());
    }

    @After
    public void tearDown() throws Exception {
        homeActivity = null;
    }
}