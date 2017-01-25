package com.musicsheetwriter.musicsheetwriter.test.loginactivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.musicsheetwriter.musicsheetwriter.LoginActivity;
import com.musicsheetwriter.musicsheetwriter.R;
import com.musicsheetwriter.musicsheetwriter.test.utils.NetworkUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.musicsheetwriter.musicsheetwriter.test.loginactivity.LoginActivityUtils.Credentials.badPassword;
import static com.musicsheetwriter.musicsheetwriter.test.loginactivity.LoginActivityUtils.Credentials.goodPassword;
import static com.musicsheetwriter.musicsheetwriter.test.loginactivity.LoginActivityUtils.Credentials.goodUserId;
import static com.musicsheetwriter.musicsheetwriter.test.loginactivity.LoginActivityUtils.Credentials.goodUsername;
import static com.musicsheetwriter.musicsheetwriter.test.loginactivity.LoginActivityUtils.Credentials.wrongPassword;
import static com.musicsheetwriter.musicsheetwriter.test.loginactivity.LoginActivityUtils.addAccountInAccountManager;
import static com.musicsheetwriter.musicsheetwriter.test.loginactivity.LoginActivityUtils.removeAccountFromAccountManager;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest_Reconnection {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<LoginActivity>(LoginActivity.class) {

        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            Intent intent = new Intent(targetContext, LoginActivity.class);
            intent.putExtra(LoginActivity.ARG_ACCOUNT_TYPE, targetContext.getString(R.string.am_account_type));
            intent.putExtra(LoginActivity.ARG_AUTH_TYPE, targetContext.getString(R.string.am_account_type));
            intent.putExtra(LoginActivity.ARG_IS_ADDING_NEW_ACCOUNT, false);
            intent.putExtra(LoginActivity.ARG_SHOW_SLASH, false);

            intent.putExtra(LoginActivity.ARG_ACCOUNT_NAME, goodUsername);
            intent.putExtra(LoginActivity.ARG_ACCOUNT_ID, goodUserId);

            return intent;
        }
    };

    @Before
    public void setUp() {
        removeAccountFromAccountManager(InstrumentationRegistry.getInstrumentation().getTargetContext());
        addAccountInAccountManager(InstrumentationRegistry.getInstrumentation().getTargetContext(), goodUserId, goodUsername, goodPassword);

        mActivityRule.getActivity();
    }

    @Test
    public void testFail_emptyField() {
        onView(withId(R.id.login_password)).perform(typeText(""));
        onView(withId(R.id.action_login)).check(matches(not(isEnabled())));
    }

    @Test
    public void testFail_badPassword() {
        onView(withId(R.id.login_password)).perform(typeText(badPassword));
        onView(withId(R.id.action_login)).check(matches(not(isEnabled())));
    }

    @Test
    public void testFail_noNetwork() throws ClassNotFoundException, NoSuchMethodException,
            NoSuchFieldException, IllegalAccessException, InvocationTargetException, InterruptedException {
        NetworkUtils.saveState(mActivityRule.getActivity());
        NetworkUtils.setWifiEnabled(mActivityRule.getActivity(), false);

        onView(withId(R.id.login_password)).perform(typeText(goodPassword));
        onView(withId(R.id.action_login)).perform(click());

        NetworkUtils.restoreState(mActivityRule.getActivity());

        String assertedString = InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.error_unknown);
        onView(withText(assertedString)).check(matches(isDisplayed()));

        Thread.sleep(5000);
    }

    @Test
    public void testFail_badCredentials() {
        onView(withId(R.id.login_password)).perform(typeText(wrongPassword));
        onView(withId(R.id.action_login)).perform(click());

        String assertedString = InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.error_bad_credentials_title);
        onView(withText(assertedString)).check(matches(isDisplayed()));
    }

    @Test
    public void testSuccess() throws InterruptedException {
        onView(withId(R.id.login_password)).perform(typeText(goodPassword));
        onView(withId(R.id.action_login)).perform(click());

        // wait for the account is correctly set
        Thread.sleep(5000);

        AccountManager accountManager = AccountManager.get(mActivityRule.getActivity());
        Account[] accounts = accountManager.getAccountsByType(mActivityRule.getActivity().getString(R.string.am_account_type));

        assertThat(accounts.length, not(equalTo(0)));

        for (Account ac : accounts) {
            ViewMatchers.assertThat(ac.name, is(equalTo(goodUsername)));
        }
    }

    @After
    public void tearDown() {
        removeAccountFromAccountManager(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }

}
