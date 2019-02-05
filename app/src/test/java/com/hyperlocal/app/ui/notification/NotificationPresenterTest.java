package com.hyperlocal.app.ui.notification;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.hyperlocal.app.HyperLocalApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * @author ${Umesh} on 04-07-2018.
 */


@RunWith(MockitoJUnitRunner.class)
public class NotificationPresenterTest {

    @Mock
    NotificationPresenter notificationPresenter;

    @Mock
    NotificationView notificationView;
    @Mock
    NotificationFireBaseServcie servcie;
    @Mock
    NotificationFireBaseServcie.CallBack callBack;

    @Mock HyperLocalApp hyperLocalApp;

    @Mock
    DatabaseReference mockUsersDatabase;
    @Mock
    Context context;

    @Before
    public void setUp() {
        hyperLocalApp = new HyperLocalApp();
        notificationPresenter = new NotificationPresenter(notificationView,new NotificationFireBaseServcie(hyperLocalApp.getApplicationContext()));
       // servcie = new NotificationFireBaseServcie();

    }

    @Test
    public void onAcceptNotificationButtonClick() {
        NotificationAcceptRequestModel notificationAcceptRequestModel = new NotificationAcceptRequestModel();
        notificationAcceptRequestModel.setRequestType("invite");
        notificationAcceptRequestModel.setDevcieType("1");
        notificationAcceptRequestModel.setUsers("WEZIAggnukf7bqzbydjmHF5BLPz1,YuVnXDeL0PPNLPWUlGsDvkAdpB73");
        notificationAcceptRequestModel.setRequestDeviceToken("fyHaql7CLTs:APA91bG_xXbQKRPNMP2CNlE5lbkmcgEe5-XTK5dKPdS5SUisrE2eBvyHXVc1VG2jmi-5MPGmVSE3UtuC0MnFT_W0lflZj5bwiyCNryhJ59a7bwFfH5U7CHm-URZm5J9GL9zvz6G2xuNsl_D799OrP4LYhgxcUjm_1Q");
        notificationAcceptRequestModel.setRequestedUserId("sHb8dZzZMwSVkz9l4BQw1m2041N2A");
        notificationAcceptRequestModel.setRequestId("-LG_98YzpG6FCD21VNRA");
        notificationPresenter.acceptRequestButtonClick("ksdkjk", notificationAcceptRequestModel);
        when("").thenReturn("");
    }
}