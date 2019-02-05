package com.hyperlocal.app.ui.registration.countrypicker;

/**
 * @Author ${Umesh} on 28-03-2018.
 */

public class CountryDetails {
    private String countryCode;
    private String countryName;
    private String dialCode;

    public CountryDetails(String countryName, String countryCode, String dialCode) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.dialCode = dialCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getDialCode() {
        return dialCode;
    }

    public void setDialCode(String dialCode) {
        this.dialCode = dialCode;
    }
}
