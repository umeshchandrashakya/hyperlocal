package com.hyperlocal.app.ui.userdeatils;

import com.hyperlocal.app.utlity.Validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author ${Umesh} on 19-04-2018.
 */

@RunWith(MockitoJUnitRunner.class)
public class NameValidatorTest  {
    Validator validator;

    @Before
    public void setUp() {
        //validator = new Validator();
    }

  @Test
    public void shouldShowErrorMessageWhenNameIsEmpty() {
      assertThat(Validator.isValidName(""), is(false));
    }




    @Test
    public void checkUserNameIsValid() {
        assertThat(Validator.isValidName("umesh"), is(true));

    }

    @Test
    public void shouldShowErrorMessageWhenNameWithNumber() {
        assertThat(Validator.isValidName("umesh123"), is(false));
    }

    @Test
    public void shouldShowErrorMessageWhenNameContainsSpecialCharacter() {
        assertThat(Validator.isValidName("umesh@"), is(false));
    }

    @Test
    public void shouldShowErrorMessageWhenNameContainsAlphaNumericName() {
        assertThat(Validator.isValidName("umesh123"), is(false));
    }

    @Test
    public void shouldShowErrorMessageWhenNameIsLessThen3CharacterAndGraterThen30Character() {
        assertThat(Validator.isValidName("um"), is(false));
    }
}
