package com.hyperlocal.app.ui.profile;

/**
 * @author ${Umesh} on 04-05-2018.
 */

public class UserProfileDataModel {

    private String name;
    private String email;
    private String mobile;
    private String address;
    private String countryCode;
    private long timeStamp;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if(this==o){
            return true;
        }else if(o instanceof UserProfileDataModel) {
            UserProfileDataModel request = (UserProfileDataModel)o;
            return this.name.equals(request.name)&&
                    this.email.equals(request.email)&&
                    this.address.equals(request.address)&&
                    this.mobile.equals(request.mobile)&&
                    this.countryCode.equals(request.countryCode)&&
                    this.timeStamp==request.timeStamp;
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (int) Math.random();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
