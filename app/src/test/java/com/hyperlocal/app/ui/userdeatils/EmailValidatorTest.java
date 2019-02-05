package com.hyperlocal.app.ui.userdeatils;


import com.hyperlocal.app.utlity.Validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @Author ${Umesh} on 23-03-2018.
 */

@RunWith(MockitoJUnitRunner.class)
public class EmailValidatorTest {


    @Before
    public void setUp() {

    }

    @Test
    public void shouldShowErrorMessageWhenEmailIsEmpty() throws Exception {
        assertThat(Validator.emailValidator(""), is(false));

    }

    @Test
    public void emailShouldContainsDotInTheAddressField() {
        assertThat(Validator.emailValidator("umesh.chandra@gmail.com"), is(true));
    }

    @Test
    public void shouldShowErrorMessageWhenEmailContainsMultipleDots() {
        assertThat(Validator.emailValidator("joei@gmail...com"), is(false));
    }

    @Test
    public void shouldShowErrorMessageWhenDotWebConsidered() {
        assertThat(Validator.emailValidator("jack@gmail.web"), is(false));
    }

    @Test
    public void shouldShowErrorMessageWhenMissingTopLevelDomain(){
        assertThat(Validator.emailValidator("jack@"), is(false));
    }

    @Test
    public void shouldShowErrorMessageWhenLeadingDashInFrontOfDomainIsConsideredToBeInvalid(){
        assertThat(Validator.emailValidator("jack@ -yahoo.com"), is(false));
    }
    @Test
    public void shouldShowErrorMessageWhenTextFollowedInEmailIsNotAllowed(){
        assertThat(Validator.emailValidator("jack@gmail.com(Jack Smith)"), is(false));
    }

    @Test
    public void shouldShowErrorMessageWhenMultipleDotInEmail(){
        assertThat(Validator.emailValidator("abcjack....@gmail.com"), is(false));
    }

    @Test
    public void shouldShowErrorMessageTrailingDotInAddress(){
        assertThat(Validator.emailValidator("abcjack.@gmail.com"), is(false));
    }

    @Test
    public void shouldShowErrorMessageWhenLeadingDotInAddressIsConsidered(){
        assertThat(Validator.emailValidator(".jack@gmail.com"), is(false));
    }

    @Test
    public void shouldShowErrorMessageWhenTwoSpecialCharacterInEmail(){
        assertThat(Validator.emailValidator("abc@gmail@gmail.com"), is(false));
    }


    @Test
    public void shouldShowErrorMessageWhenEncodedHtmlWithinEmailIsConsideredAsInValid(){
        assertThat(Validator.emailValidator("<abc@gmail.com>"), is(false));
    }

    @Test
    public void shouldShowErrorMessageWhenMissingUserNameInEmail(){
        assertThat(Validator.emailValidator("@gmail.com"), is(false));
    }

    @Test
    public void shouldShowErrorMessageWhenAtTheRateSignIsMissing(){
        assertThat(Validator.emailValidator("jackipu.ac.in"), is(false));
    }

    @Test
    public void dotInTopLevelDomainInNameAlsoConsideredValidAsAEmail(){
        assertThat(Validator.emailValidator("jack@ipu.ac.in"), is(false));
    }
    @Test
    public void nameIsValidTopLevelDomainNameAsAEmail(){
        assertThat(Validator.emailValidator("email@domain.name"), is(false));
    }
    @Test
    public void underscoreInTheAddressFieldIsValidAsAEmailId(){
        assertThat(Validator.emailValidator("umesh_abc@mobilecoderz-one.com" + ""), is(true));
    }
    @Test
    public void dashInDomainNameIsValidAsAEmailId(){
        assertThat(Validator.emailValidator("email@mobilecoderz-one.com" + ""), is(true));
    }

    @Test
    public void digitsInAddressAreValidAsAEmailAddress() {
        assertThat(Validator.emailValidator("123456@gmail.com"), is(true));
    }
    @Test
    public void quotesAroundEmailIsConsideredValid() {
        assertThat(Validator.emailValidator("mobilecoderz" + "@gmail.com"), is(true));
    }
    @Test
    public void plusSignIsConsideredValidCharacterAsInEmail() {
        assertThat(Validator.emailValidator("umesh+mobilecoderz@gmail.com"), is(true));
    }

    @Test
    public void emailContainDotWithSubDomain() {
        assertThat(Validator.emailValidator("mobilecoderz@gmail.com"), is(true));
    }


}












