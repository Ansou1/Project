package com.musicsheetwriter.musicsheetwriter.test.loginactivity;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.musicsheetwriter.musicsheetwriter.LoginActivity;
import com.musicsheetwriter.musicsheetwriter.R;
import com.musicsheetwriter.musicsheetwriter.test.utils.NetworkUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.musicsheetwriter.musicsheetwriter.test.loginactivity.LoginActivityUtils.Credentials.badEmail;
import static com.musicsheetwriter.musicsheetwriter.test.loginactivity.LoginActivityUtils.Credentials.badPassword;
import static com.musicsheetwriter.musicsheetwriter.test.loginactivity.LoginActivityUtils.Credentials.existingEmail;
import static com.musicsheetwriter.musicsheetwriter.test.loginactivity.LoginActivityUtils.Credentials.existingUsername;
import static com.musicsheetwriter.musicsheetwriter.test.loginactivity.LoginActivityUtils.Credentials.validNewUserEmail;
import static com.musicsheetwriter.musicsheetwriter.test.loginactivity.LoginActivityUtils.Credentials.validNewUserPassword;
import static com.musicsheetwriter.musicsheetwriter.test.loginactivity.LoginActivityUtils.Credentials.validNewUserUsername;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)
public class LoginActivityTest_CreateAccount {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<LoginActivity>(LoginActivity.class) {

        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            Intent intent = new Intent(targetContext, LoginActivity.class);
            intent.putExtra(LoginActivity.ARG_ACCOUNT_TYPE, targetContext.getString(R.string.am_account_type));
            intent.putExtra(LoginActivity.ARG_AUTH_TYPE, targetContext.getString(R.string.am_account_type));
            intent.putExtra(LoginActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
            intent.putExtra(LoginActivity.ARG_SHOW_SLASH, false);

            return intent;
        }
    };

    @Before
    public void setUp() {
        mActivityRule.getActivity();

        onView(withId(R.id.to_create_account)).perform(click());
    }

    @Test
    public void testFail_emptyField() {
        onView(withId(R.id.create_account_username)).perform(typeText(""));
        onView(withId(R.id.create_account_email)).perform(typeText(validNewUserEmail));
        onView(withId(R.id.create_account_password)).perform(typeText(validNewUserPassword));
        onView(withId(R.id.action_create_account)).check(matches(not(isEnabled())));
    }

    @Test
    public void testFail_badEmail() {
        onView(withId(R.id.create_account_username)).perform(typeText(validNewUserUsername));
        onView(withId(R.id.create_account_email)).perform(typeText(badEmail));
        onView(withId(R.id.create_account_password)).perform(typeText(validNewUserPassword));
        onView(withId(R.id.action_create_account)).check(matches(not(isEnabled())));
    }

    @Test
    public void testFail_badPassword() {
        onView(withId(R.id.create_account_username)).perform(typeText(validNewUserUsername));
        onView(withId(R.id.create_account_email)).perform(typeText(validNewUserEmail));
        onView(withId(R.id.create_account_password)).perform(typeText(badPassword));
        onView(withId(R.id.action_create_account)).check(matches(not(isEnabled())));
    }

    @Test
    public void testFail_noNetwork() throws ClassNotFoundException, NoSuchMethodException,
            NoSuchFieldException, IllegalAccessException,
            InvocationTargetException, InterruptedException {
        NetworkUtils.saveState(mActivityRule.getActivity());
        NetworkUtils.setWifiEnabled(mActivityRule.getActivity(), false);

        onView(withId(R.id.create_account_username)).perform(typeText(validNewUserUsername));
        onView(withId(R.id.create_account_email)).perform(typeText(validNewUserEmail));
        onView(withId(R.id.create_account_password)).perform(typeText(validNewUserPassword));
        onView(withId(R.id.action_create_account)).perform(click());

        NetworkUtils.restoreState(mActivityRule.getActivity());

        String assertedString = InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.error_unknown);
        onView(withText(assertedString)).check(matches(isDisplayed()));

        Thread.sleep(5000);
    }

    @Test
    public void testFail_valuesAlreadyUsed() throws InterruptedException {
        onView(withId(R.id.create_account_username)).perform(typeText(existingUsername));
        onView(withId(R.id.create_account_email)).perform(typeText(existingEmail));
        onView(withId(R.id.create_account_password)).perform(typeText(validNewUserPassword));
        onView(withId(R.id.action_create_account)).perform(click());

        Thread.sleep(1000);

        String expectedError = InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.error_username_already_used);
        onView(withId(R.id.create_account_username)).check(matches(hasErrorText(expectedError)));
        expectedError = InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.error_email_already_used);
        onView(withId(R.id.create_account_email)).check(matches(hasErrorText(expectedError)));
    }

    @Test
    public void testSuccess() {
        onView(withId(R.id.create_account_username)).perform(typeText(validNewUserUsername));
        onView(withId(R.id.create_account_email)).perform(typeText(validNewUserEmail));
        onView(withId(R.id.create_account_password)).perform(typeText(validNewUserPassword));
        onView(withId(R.id.action_create_account)).perform(click());

        String assertedString = InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.success_creation_account_title);
        onView(withText(assertedString)).check(matches(isDisplayed()));
    }
}
