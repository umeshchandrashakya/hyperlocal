package com.hyperlocal.app.ui.password;


import com.hyperlocal.app.utlity.Validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @Author ${Umesh} on 22-03-2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class PasswordPresenterTest {

    @Mock
   Validator validator;



    @Before
    public void setUp() {
    }


    @Test
    public void shouldShowErrorMessageWhenPasswordIsEmpty() {
        assertThat(Validator.isValidPassword(""), is(false));
    }

    @Test
    public void shouldShowErrorMessageWhenPasswordLengthIsLessThanSixCharacter(){

        assertThat(Validator.isValidPassword("Ab@12"), is(false));
    }

    @Test
    public void shouldShowErrorMessageWhenPasswordDoesNotContainsSpecialCharacter(){
        assertThat(Validator.isValidPassword("Ab@12"), is(false));
    }

    @Test
    public void shouldShowErrorMessageWhenPasswordDoesNotContainsNumber(){
        assertThat(Validator.isValidPassword("Abc@djksdj"), is(false));
    }

    @Test
    public void shouldShowErrorMessageWhenPasswordLengthIs_GraterThan_30Character(){
        assertThat(Validator.isValidPassword("Abc@djksdj12hdhiofierhhdsrhjdhhfiiejfdwhjh"), is(false));
    }

    @Test
    public void shouldShowErrorMessageWhenSpaceBetweenCharacter(){
        assertThat(Validator.isValidPassword("Abc@123 abd"), is(false));
    }

    @Test
    public void startNewActivityWhenPasswordIsValid(){
        assertThat(Validator.isValidPassword("Abc@123abd"), is(false));

    }
    @Test
    public void startNewActivityWhenPasswordIsValid1(){
        assertThat(Validator.isValidPassword("Aa23dgdsgd"), is(true));

    }
}