package com.hyperlocal.app.utlity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author ${Umesh} on 02-04-2018.
 */

public class Validator {

    private Validator(){

    }

      /**
         * This method will check if mobile number is valid or not.
         * Params:- password : String
         * Return validation: Boolean
         */
        public static boolean isValidPhoneNumber(String phoneNumber) {
        return !(phoneNumber.length() < 8 || phoneNumber.length() > 15) &&
                !phoneNumber.matches("^[0]+$") &&
                phoneNumber.matches("[0-9]+") &&
                phoneNumber.length() > 2;
     }


    /**
     * This method will check if mobile number is valid or not.
     * Params:- password : String
     * Return validation: Boolean
     */
    public static boolean emailValidator(final String mailAddress) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(mailAddress);
        return matcher.matches();
    }


    /**
     * This method will check if name is valid or not.
     * Params:- password : String
     * Return validation: Boolean
     */
    public static boolean isValidName(String name){
        if(name.length()<3 || name.length()>30)
            return false;
        else {
            String regular  = "^[\\p{L} .'-]+$";
            Pattern pattern = Pattern.compile(regular);
            Matcher matcher = pattern.matcher(name);
            return matcher.matches();
        }
    }

    /**
     * This method will check if password is valid or not.
     * Params:- password : String
     * Return validation: Boolean
     */
    public static boolean isValidPassword(String str) {
        if(str.contains(" "))
            return false;
        else {
            String patternRex = "((?=.*\\d)(?=.*[a-z]).{8,20})";
            Pattern  pattern = Pattern.compile(patternRex);
            Matcher matcher = pattern.matcher(str);
            return matcher.matches();
        }
    }

}
