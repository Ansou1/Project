package com.musicsheetwriter.musicsheetwriter.test.loginactivity;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.musicsheetwriter.musicsheetwriter.ForgottenPasswordActivity;
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
import static com.musicsheetwriter.musicsheetwriter.test.loginactivity.LoginActivityUtils.Credentials.goodEmail;
import static com.musicsheetwriter.musicsheetwriter.test.loginactivity.LoginActivityUtils.Credentials.unknownEmail;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)
public class LoginActivityTest_ForgottenPassword {

    @Rule
    public ActivityTestRule<ForgottenPasswordActivity> mActivityRule = new ActivityTestRule<>(ForgottenPasswordActivity.class);


    @Before
    public void setUp() throws Exception {
        mActivityRule.getActivity();
    }

    @Test
    public void testForgottenPassword_emptyField() {
        onView(withId(R.id.edit)).perform(typeText(""));
        onView(withId(R.id.button)).check(matches(not(isEnabled())));
    }

    @Test
    public void testForgottenPassword_badEmail() {
        onView(withId(R.id.edit)).perform(typeText(badEmail));
        onView(withId(R.id.button)).check(matches(not(isEnabled())));
    }

    @Test
    public void testForgottenPassword_unknownEmail() throws InterruptedException {
        onView(withId(R.id.edit)).perform(typeText(unknownEmail));
        onView(withId(R.id.button)).perform(click());

        Thread.sleep(5000);

        String expectedError = InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.error_no_such_email);
        onView(withId(R.id.edit)).check(matches(hasErrorText(expectedError)));
    }

    @Test
    public void testFail_noNetwork() throws ClassNotFoundException, NoSuchMethodException,
            NoSuchFieldException, IllegalAccessException,
            InvocationTargetException, InterruptedException {
        NetworkUtils.saveState(mActivityRule.getActivity());
        NetworkUtils.setWifiEnabled(mActivityRule.getActivity(), false);

        onView(withId(R.id.edit)).perform(typeText(goodEmail));
        onView(withId(R.id.button)).perform(click());

        NetworkUtils.restoreState(mActivityRule.getActivity());

        String assertedString = InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.error_unknown);
        onView(withText(assertedString)).check(matches(isDisplayed()));

        Thread.sleep(5000);
    }

    @Test
    public void testForgottenPassword_success() {
        onView(withId(R.id.edit)).perform(typeText(goodEmail));
        onView(withId(R.id.button)).perform(click());

        String assertedString = InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.success_forgotten_password_title);
        onView(withText(assertedString)).check(matches(isDisplayed()));
    }
}
