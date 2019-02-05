package com.hyperlocal.app.ui.home.presenter;

/**
 * @author ${Umesh} on 05-07-2018.
 */

public interface AskView {
    void showProgressBar();
    void hideProgressBar();
    void showSnackBar(String message);
    String getAskRequest();
    void replaceFragment(String message);

}
