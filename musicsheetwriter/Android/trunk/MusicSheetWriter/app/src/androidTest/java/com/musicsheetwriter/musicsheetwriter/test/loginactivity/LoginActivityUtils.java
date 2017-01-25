package com.musicsheetwriter.musicsheetwriter.test.loginactivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.musicsheetwriter.musicsheetwriter.LoginActivity;
import com.musicsheetwriter.musicsheetwriter.R;


public class LoginActivityUtils {

    public static class Credentials {

        // Main user's ID
        public static final int goodUserId = 10000;

        // Good credentials
        public static final String goodUsername = "test_android_main";
        public static final String goodPassword = "azerty123";
        // Not activated
        public static final String usernameNotActivated = "test_android_not_activated";
        // Closed
        public static final String usernameClosed = "test_android_closed";
        // Unknown
        public static final String usernameUnknown = "test_android_unknown";
        // BadPassword
        public static final String badPassword = "no_NumbersInside";

        // Good new user info
        public static final String validNewUserUsername = "test_android_created";
        public static final String validNewUserEmail = "test_android_created@msw.com";
        public static final String validNewUserPassword = "azerty123";
        // Bad Email
        public static final String badEmail = "NotAnEmail.fr";
        // Existing values
        public static final String existingUsername = "test_android_main";
        public static final String existingEmail = "test_android_main@msw.com";

        // Forgotten password
        public static final String goodEmail = "test_android_main@msw.com";
        // Unknown
        public static final String unknownEmail = "unknownemail@msw.com";

        // Reconnection
        public static final String wrongPassword = "Qwerty456";

    }

    public enum LayoutConf {
        SPLASH_SCREEN,
        LOGIN_LAYOUT,
        CREATE_ACCOUNT_LAYOUT,
        RECONNECTION_LAYOUT
    }

    public static Intent createLoginActivityIntent(String accountType, String authType,
                                            boolean isAddingNewAccount, String accountName,
                                            int accountId, boolean showSplash) {
        Intent intent = new Intent();
        intent.putExtra(LoginActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(LoginActivity.ARG_AUTH_TYPE, authType);
        intent.putExtra(LoginActivity.ARG_IS_ADDING_NEW_ACCOUNT, isAddingNewAccount);
        intent.putExtra(LoginActivity.ARG_ACCOUNT_NAME, accountName);
        intent.putExtra(LoginActivity.ARG_ACCOUNT_ID, accountId);
        intent.putExtra(LoginActivity.ARG_SHOW_SLASH, showSplash);
        return intent;
    }

/*    public static void assertLayoutConfVisibility(LayoutConf conf) {
        switch (conf) {
            case SPLASH_SCREEN:
                onView(withId(R.id.msw_logo_image_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                onView(withId(R.id.splash_screen_app_title)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                onView(withId(R.id.splash_screen_login)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
                onView(withId(R.id.splash_screen_create_account)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
                break;
            case LOGIN_LAYOUT:
                onView(withId(R.id.msw_logo_image_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                onView(withId(R.id.splash_screen_app_title)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
                onView(withId(R.id.splash_screen_login)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                onView(withId(R.id.splash_screen_login_username_layout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                onView(withId(R.id.splash_screen_login_reconnection_layout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
                onView(withId(R.id.splash_screen_create_account)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
                break;
            case CREATE_ACCOUNT_LAYOUT:
                onView(withId(R.id.msw_logo_image_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
                onView(withId(R.id.splash_screen_app_title)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
                onView(withId(R.id.splash_screen_login)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
                onView(withId(R.id.splash_screen_create_account)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                break;
            case RECONNECTION_LAYOUT:
                onView(withId(R.id.msw_logo_image_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                onView(withId(R.id.splash_screen_app_title)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
                onView(withId(R.id.splash_screen_login)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                onView(withId(R.id.splash_screen_login_username_layout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
                onView(withId(R.id.splash_screen_login_reconnection_layout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                onView(withId(R.id.splash_screen_create_account)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
                break;
        }
    }
*/
/*    public static void waitForSplashScreenEnd() throws InterruptedException {
        Thread.sleep(getTimeSplashScreen() + getTimeMoveUpAnimation());
    }
*/

    public static void addAccountInAccountManager(Context context, int userId, String username,
                                                  String password) {
        final Account account = new Account(username, context.getString(R.string.am_account_type));

        Bundle userData = new Bundle();
        userData.putString(context.getString(R.string.am_user_data_user_id), String.valueOf(userId));

        AccountManager accountManager = AccountManager.get(context);
        accountManager.addAccountExplicitly(account, password, userData);
    }

    public static void removeAccountFromAccountManager(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        accountManager.invalidateAuthToken(context.getString(R.string.am_account_type), null);
        Account[] accounts = accountManager.getAccountsByType(context.getString(R.string.am_account_type));
        for (Account ac : accounts) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                accountManager.removeAccount(ac, null, null, null);
            } else {
                accountManager.removeAccount(ac, null, null);
            }
        }
    }
/*
    public static int getTimeSplashScreen() {
        return LoginActivity.TIME_BEFORE_ANIM;
    }

    public static int getTimeMoveUpAnimation() {
        return 400;
    }

    public static int getTimeFadeAnimation() {
        return 400;
    }
*/
}
