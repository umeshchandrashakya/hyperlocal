package com.hyperlocal.app.model;

/**
 * @author ${Umesh} on 08-06-2018.
 */

public class CurrentPager {
    private int openCurrentPage;
    private boolean isRegistrationCompleted;

    public boolean isRegistrationCompleted() {
        return isRegistrationCompleted;
    }

    public void setRegistrationCompleted(boolean registrationCompleted) {
        isRegistrationCompleted = registrationCompleted;
    }

    public int getOpenCurrentPage() {
        return openCurrentPage;
    }

    public void setOpenCurrentPage(int openCurrentPage) {
        this.openCurrentPage = openCurrentPage;
    }
}
