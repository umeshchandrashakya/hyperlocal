package com.hyperlocal.app.ui.home.presenter;

import android.support.v7.widget.RecyclerView;

import com.hyperlocal.app.MySharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * @author ${Umesh} on 05-07-2018.
 */

@RunWith(MockitoJUnitRunner.class)

public class AskRequestPresenterTest {

    @Mock AskView askView;

    @Mock AskRequestPresenter presenter;

    @Mock
    MySharedPreferences sharedPreferences;
    private String uid="";

    @Before
    public void setUp(){
        presenter = new AskRequestPresenter(askView,sharedPreferences,uid);
    }

    @Test
    public void shouldShowErrorMessageWhenRequestIsEmpty() {
        when(askView.getAskRequest()).thenReturn("");
        presenter.nextButtonClick("fcmToke");
        //verify(askView).showSnackBar();
    }


}