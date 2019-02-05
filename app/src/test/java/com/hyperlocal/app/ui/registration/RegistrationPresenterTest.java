package com.hyperlocal.app.ui.registration;


import com.hyperlocal.app.utlity.Validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This is a "Unit Test",which uses Mockito to test the app behaviour/logic.
 * Created by umesh chandra  on 20-03-2018.
 */

@RunWith(MockitoJUnitRunner.class)

public class RegistrationPresenterTest {

    /**
     * Why mock? mocks aren't a real implementation of class.instead
     * they can do things like record which method was called on the
     * mock,and they can return predefined response by setting up
     * mockito.when().return()clause(see onLoginButtonClick for a direct example)
     */


    @Before()
    public void setUp() throws Exception {


    }

    @Test
    public void shouldShowErrorMessageWhenPhoneNumberIsEmpty() throws Exception {
        assertThat(Validator.emailValidator("name@email.com"), is(true));
    }



    @Test
    public void shouldShowErrorMessageWhenPhoneNumberContainsAlphabets() throws Exception {
        assertThat(Validator.isValidPassword("121221nbjknk"), is(false));
    }

    @Test
    public void shouldShowErrorMessageWhenPhoneNumberLengthIsGraterThan15Character(){
        assertThat(Validator.isValidPassword("72389174891279047"), is(false));
    }

    @Test
    public void shouldShowErrorMessageWhenPhoneNumberContainsOnlyZero(){
        assertThat(Validator.isValidPassword("000000000"), is(false));
    }


    @Test
    public void shouldShowErrorMessageWhenPhoneNumberLengthIsLessThan8(){
        assertThat(Validator.isValidPassword("123456"), is(false));
    }
}