package com.musicsheetwriter.musicsheetwriter;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.musicsheetwriter.musicsheetwriter.formvalidator.EmailValidator;
import com.musicsheetwriter.musicsheetwriter.formvalidator.PasswordValidator;
import com.musicsheetwriter.musicsheetwriter.network.MswApiAsyncConnectionResponseHandler;
import com.musicsheetwriter.musicsheetwriter.network.MswApiClient;
import com.musicsheetwriter.musicsheetwriter.network.MswApiException;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponse;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponseError;
import com.musicsheetwriter.musicsheetwriter.network.httputils.HttpException;
import com.musicsheetwriter.musicsheetwriter.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;


public class LoginActivity extends AccountAuthenticatorActivity {

    /*
     * Extra keys
     */
    public static final String ARG_ACCOUNT_TYPE = "accountType";
    public static final String ARG_AUTH_TYPE = "authType";
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "isAddingNewAccount";
    public static final String ARG_ACCOUNT_NAME = "accountName";
    public static final String ARG_ACCOUNT_ID = "accountId";
    public static final String ARG_SHOW_SLASH = "showSplash";

    /**
     * The duration of the splash screen, before credentials forms appear
     */
    public static final int TIME_BEFORE_ANIM = 3000;

    private AccountManager mAccountManager;
    private String mAccountType;
    private String mAuthTokenType;

    private View mSplashLayout;
    private View mTopLayout;
    private View mLoginLayout;
    private View mCreateAccountLayout;

    private ImageView mReconnectionProfilePicture;
    private TextView mChangeAccount;
    private EditText mLoginUsername;
    private EditText mLoginPassword;
    private TextView mLoginForgottenPassword;
    private EditText mCreateAccountUsername;
    private EditText mCreateAccountEmail;
    private EditText mCreateAccountPassword;
    private Button mActionLoginButton;
    private Button mToCreateAccountButton;
    private Button mActionCreateAccountButton;
    private Button mToLoginButton;

    private ContentLoadingProgressBar mProgressBar;

    private int mCrossfaceDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAccountManager = AccountManager.get(this);
        mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        if (mAccountType == null) {
            mAccountType = getString(R.string.am_account_type);
        }
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null) {
            mAuthTokenType = getString(R.string.am_auth_token_type);
        }

        // Assign layouts
        mSplashLayout = findViewById(R.id.splash_layout);
        mTopLayout = findViewById(R.id.top_layout);
        mLoginLayout = findViewById(R.id.login_layout);
        mCreateAccountLayout = findViewById(R.id.create_account_layout);

        // Assign views
        mLoginUsername = (EditText) findViewById(R.id.login_username);
        mLoginPassword = (EditText) findViewById(R.id.login_password);
        mLoginForgottenPassword = (TextView) findViewById(R.id.button);
        mReconnectionProfilePicture = (ImageView) findViewById(R.id.reconnecting_user_picture);
        mChangeAccount = (TextView) findViewById(R.id.action_change_account);
        mCreateAccountUsername = (EditText) findViewById(R.id.create_account_username);
        mCreateAccountEmail = (EditText) findViewById(R.id.create_account_email);
        mCreateAccountPassword = (EditText) findViewById(R.id.create_account_password);
        mActionLoginButton = (Button) findViewById(R.id.action_login);
        mToCreateAccountButton = (Button) findViewById(R.id.to_create_account);
        mActionCreateAccountButton = (Button) findViewById(R.id.action_create_account);
        mToLoginButton = (Button) findViewById(R.id.to_login);
        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progressbar);

        // Retrieve and cache the system's default "short" animation time.
        mCrossfaceDuration = getResources().getInteger(android.R.integer.config_longAnimTime);

        // Assign the behavior to adopt when the user click
        setClickListeners();

        // Assign edit text listener
        LoginTextWatcher loginWatcher = new LoginTextWatcher();
        CreateAccountTextWatcher createAccountTextWatcher = new CreateAccountTextWatcher();

        mLoginUsername.addTextChangedListener(loginWatcher);
        mLoginPassword.addTextChangedListener(loginWatcher);
        mCreateAccountUsername.addTextChangedListener(createAccountTextWatcher);
        mCreateAccountEmail.addTextChangedListener(createAccountTextWatcher);
        mCreateAccountPassword.addTextChangedListener(createAccountTextWatcher);

        // Disable the automatic focus on the EditText
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Show or skip the splash screen
        if (getIntent().getBooleanExtra(ARG_SHOW_SLASH, false)) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    crossfadeViews(mLoginLayout, mSplashLayout);
                    mTopLayout.setAlpha(0f);
                    mTopLayout.setVisibility(View.VISIBLE);
                    mTopLayout.animate()
                            .alpha(1f)
                            .setDuration(mCrossfaceDuration)
                            .setListener(null);
                }

            }, TIME_BEFORE_ANIM); // 3000ms delay
            getIntent().putExtra(ARG_SHOW_SLASH, false);
        } else {
            mSplashLayout.setVisibility(View.GONE);
            mTopLayout.setVisibility(View.VISIBLE);
            mLoginLayout.setVisibility(View.VISIBLE);
        }


        // Show login or reconnection layout
        if (!getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, true)) {
            showReconnectionLayout();
        }
    }

    /**
     * Apply fade in and fade out at once to two difference layout.
     * @param toShow the layout to fade in
     * @param toHide the layout to fade out
     */
    private void crossfadeViews(final View toShow, final View toHide) {
        // Set the "show" view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        toShow.setAlpha(0f);
        toShow.setVisibility(View.VISIBLE);

        // Animate the "show" view to 100% opacity, and clear any animation listener set on
        // the view. Remember that listeners are not limited to the specific animation
        // describes in the chained method calls. Listeners are set on the
        // ViewPropertyAnimator object for the view, which persists across several
        // animations.
        toShow.animate()
                .alpha(1f)
                .setDuration(mCrossfaceDuration)
                .setListener(null);

        // Animate the "hide" view to 0% opacity. After the animation ends, set its visibility
        // to GONE as an optimization step (it won't participate in layout passes, etc.)
        toHide.animate()
                .alpha(0f)
                .setDuration(mCrossfaceDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toHide.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * Assign the click listeners for each button
     */
    private void setClickListeners() {
        mActionLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Force hiding keyboard
                if (getCurrentFocus() != null) {
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                // Send request
                sendLoginRequest();
            }
        });

        mToCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle(getString(R.string.title_activity_login_sign_up));
                crossfadeViews(mCreateAccountLayout, mLoginLayout);
            }
        });

        mActionCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Force hiding keyboard
                if (getCurrentFocus() != null) {
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                // Send request
                sendCreateAccountRequest();
            }
        });

        mToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle(getString(R.string.title_activity_login_sign_in));
                crossfadeViews(mLoginLayout, mCreateAccountLayout);
            }
        });

        mLoginForgottenPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgottenPasswordActivity.class);
                startActivity(intent);
            }
        });

        mChangeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSavedAccount();
                // turn off the reconnection elements
                mReconnectionProfilePicture.setVisibility(View.GONE);
                mChangeAccount.setVisibility(View.GONE);
                // empty the 'username' edit text
                mLoginUsername.setText("");
                // enable it back
                mLoginUsername.setEnabled(true);
                // allow the user to create an account
                mToCreateAccountButton.setVisibility(View.VISIBLE);
                // save the current state as 'adding new account'
                getIntent().putExtra(ARG_IS_ADDING_NEW_ACCOUNT, true);
            }
        });
    }

    /**
     * Display the login layout but instead of the EditText username, the TextView username and
     * the profile picture of the user are shown
     */
    private void showReconnectionLayout() {
        // make the user's profile picture and the button to change the account visible
        mReconnectionProfilePicture.setVisibility(View.VISIBLE);
        mChangeAccount.setVisibility(View.VISIBLE);
        // fill the username edit text with the username
        mLoginUsername.setText(getIntent().getStringExtra(ARG_ACCOUNT_NAME));
        // prevent the user from changing the username
        mLoginUsername.setEnabled(false);
        // prevent the user to go to the account creation layout
        mToCreateAccountButton.setVisibility(View.GONE);
        // fill the user picture
        sendGetProfilePictureRequest();
    }

    /**
     * Attempt to send a login request
     */
    private void sendLoginRequest() {
        if (checkPassword(mLoginPassword, true)) {

            final String usernameValue = mLoginUsername.getText().toString();
            final String passwordValue = mLoginPassword.getText().toString();

            new MswApiClient()
                    .login(usernameValue, passwordValue, new MswApiAsyncConnectionResponseHandler() {

                        @Override
                        protected void onStart() {
                            mProgressBar.setVisibility(View.VISIBLE);
                            mLoginLayout.setVisibility(View.GONE);
                        }

                        @Override
                        protected void onFinish() {
                            mProgressBar.setVisibility(View.GONE);
                            mLoginLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        protected void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                            int userId;
                            try {
                                userId = mswApiResponse.getJSONObject().getInt("id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                throw new MswApiException(e);
                            }
                            String authToken = mswApiResponse.getAuthToken();
                            connection(usernameValue, passwordValue, authToken, userId);
                        }

                        @Override
                        protected void onFailure(MswApiResponseError mswApiResponseError) throws MswApiException {
                            handleApiErrorLogin(mswApiResponseError);
                        }

                        @Override
                        protected void onFailure(MswApiException mswApiException) {
                            handleApiException(mswApiException);
                        }
                    });
        }
    }

    /**
     * Attempt to send a createAccount request
     */
    private void sendCreateAccountRequest() {

        if (checkEmail(mCreateAccountEmail, true) && checkPassword(mCreateAccountPassword, true)) {

            final String usernameValue = mCreateAccountUsername.getText().toString();
            final String emailValue = mCreateAccountEmail.getText().toString();
            final String passwordValue = mCreateAccountPassword.getText().toString();

            new MswApiClient().createAccount(usernameValue, emailValue,
                    passwordValue, new MswApiAsyncConnectionResponseHandler() {

                        @Override
                        protected void onStart() {
                            mProgressBar.setVisibility(View.VISIBLE);
                            mCreateAccountLayout.setVisibility(View.GONE);
                        }

                        @Override
                        protected void onFinish() {
                            mProgressBar.setVisibility(View.GONE);
                            mCreateAccountLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        protected void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle(R.string.success_creation_account_title)
                                    .setMessage(R.string.success_creation_account_message)
                                    .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }

                        @Override
                        protected void onFailure(MswApiResponseError mswApiResponseError) throws MswApiException {
                            handleApiErrorCreateAccount(mswApiResponseError);
                        }

                        @Override
                        protected void onFailure(MswApiException mswApiException) {
                            handleApiException(mswApiException);
                        }
                    });
        }
    }

    private void sendGetProfilePictureRequest() {
        new MswApiClient()
                .getAccountPicture(getIntent().getIntExtra(ARG_ACCOUNT_ID, -1),
                        new MswApiAsyncConnectionResponseHandler() {
                            @Override
                            protected void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                                try {
                                    Picasso.with(LoginActivity.this)
                                            .load(mswApiResponse.getJSONObject().getString("photo"))
                                            .fit()
                                            .transform(new CircleTransform())
                                            .into(mReconnectionProfilePicture);
                                } catch (JSONException e) {
                                    throw new MswApiException(e);
                                }
                            }
                        });
    }

    private boolean checkEmail(EditText field, boolean setError) {
        String fieldValue = field.getText().toString();
        if (!new EmailValidator().validate(fieldValue)) {
            if (setError) {
                field.setError(getString(R.string.error_email_format));
                field.requestFocus();
            }
            return false;
        }
        return true;
    }

    private boolean checkPassword(EditText field, boolean setError) {
        String fieldValue = field.getText().toString();
        if (!new PasswordValidator().validate(fieldValue)) {
            if (setError) {
                field.setError(getString(R.string.error_password_format));
                field.requestFocus();
            }
            return false;
        }
        return true;
    }


    private void connection(String username, String password, String authToken, int userId) {
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
        intent.putExtra(AccountManager.KEY_PASSWORD, password);

        // Store the account in AccountManager and Preferences
        saveAccount(username, authToken, password, userId);
        setAccountAuthenticatorResult(intent.getExtras());

        setResult(RESULT_OK, intent);

        // Finish the LoginActivity
        finish();
    }

    /**
     * Store the account into the account manager and store the userId as his/her user data
     * @param username the username of the user
     * @param password the password of the user
     * @param userId the userId of the user
     */
    private void saveAccount(String username, String authToken, String password, int userId) {

        final Account account = new Account(username, mAccountType);
        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            String authTokenType = mAuthTokenType;
            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            Bundle userData = new Bundle();
            userData.putString(getString(R.string.am_user_data_user_id), String.valueOf(userId));

            mAccountManager.addAccountExplicitly(account, password, userData);
            mAccountManager.setAuthToken(account, authTokenType, authToken);
        } else {
            mAccountManager.setPassword(account, password);
            mAccountManager.setUserData(account, getString(R.string.am_user_data_user_id), String.valueOf(userId));
        }
    }

    /**
     * Remove the user from the AccountManager and the shared preferences
     */
    private void removeSavedAccount() {
        // Remove the account from the accountManager
        mAccountManager.invalidateAuthToken(mAccountType, null);
        Account[] accounts = mAccountManager.getAccountsByType(mAccountType);
        for (Account ac : accounts) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                mAccountManager.removeAccount(ac, LoginActivity.this, null, null);
            } else {
                //noinspection deprecation
                mAccountManager.removeAccount(ac, null, null);
            }
        }
    }

    /**
     * Handle Exception that can be throw either by the APIClient class or that can happen during
     * the connection displaying an appropriate message
     */
    private void handleApiException(MswApiException e) {
        if (e.getCause() instanceof HttpException &&
                e.getCause().getCause() instanceof ConnectException) {
            Snackbar.make(findViewById(R.id.coordinator_layout),
                    getString(R.string.error_no_network),
                    Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(findViewById(R.id.coordinator_layout),
                    getString(R.string.error_unknown), Snackbar.LENGTH_LONG).show();
        }
    }

    private void handleApiErrorLogin(MswApiResponseError error) {
        String errorCode = error.getApiErrorCode();

        // Invalid field error
        switch (errorCode) {
            case "GLO-BADFIELD":
                EditText viewToFocus = null;

                JSONObject data = error.getApiErrorData();
                if (data.has("username")) {
                    mLoginUsername.setError(getString(R.string.error_username_format));
                    viewToFocus = mLoginUsername;
                }
                if (data.has("password")) {
                    mLoginPassword.setError(getString(R.string.error_password_format));
                    viewToFocus = (viewToFocus == null) ? mLoginPassword : viewToFocus;
                }

                if (viewToFocus != null) {
                    viewToFocus.requestFocus();
                    viewToFocus.setSelection(viewToFocus.getText().length());
                }
                break;
            // Bad credentials
            case "LOG-BADCREDENTIALS":
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(R.string.error_bad_credentials_title)
                        .setMessage(R.string.error_bad_credentials_message)
                        .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            // Closed account
            case "LOG-CLOSEDACC":
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(R.string.error_close_account_title)
                        .setMessage(R.string.error_close_account_message)
                        .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            // Not activated account
            case "LOG-ACCNOTACTIVE":
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(R.string.error_not_activated_account_title)
                        .setMessage(R.string.error_not_activated_account_message)
                        .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            // Unknown Error
            default:
                Snackbar.make(findViewById(R.id.coordinator_layout),
                        getString(R.string.error_unknown), Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    private void handleApiErrorCreateAccount(MswApiResponseError error) {
        String errorCode = error.getApiErrorCode();

        // Invalid field error
        switch (errorCode) {
            case "GLO-BADFIELD": {
                EditText viewToFocus = null;

                JSONObject data = error.getApiErrorData();
                if (data.has("username")) {
                    mCreateAccountUsername.setError(getString(R.string.error_username_format));
                    viewToFocus = mCreateAccountUsername;
                }
                if (data.has("email")) {
                    mCreateAccountEmail.setError(getString(R.string.error_email_format));
                    viewToFocus = (viewToFocus == null) ? mCreateAccountEmail : viewToFocus;
                }
                if (data.has("password")) {
                    mCreateAccountPassword.setError(getString(R.string.error_password_format));
                    viewToFocus = (viewToFocus == null) ? mCreateAccountPassword : viewToFocus;
                }
                if (viewToFocus != null) {
                    viewToFocus.requestFocus();
                    viewToFocus.setSelection(viewToFocus.getText().length());
                }
                break;
            }
            // Value already used
            case "REG-EALRUSED": {
                EditText viewToFocus = null;

                JSONObject data = error.getApiErrorData();
                if (data.has("username")) {
                    mCreateAccountUsername.setError(getString(R.string.error_username_already_used));
                    viewToFocus = mCreateAccountUsername;
                }
                if (data.has("email")) {
                    mCreateAccountEmail.setError(getString(R.string.error_email_already_used));
                    viewToFocus = (viewToFocus == null) ? mCreateAccountEmail : viewToFocus;
                }
                if (viewToFocus != null) {
                    viewToFocus.requestFocus();
                    viewToFocus.setSelection(viewToFocus.getText().length());
                }
                break;
            }
            // Unknown Error
            default:
                Snackbar.make(findViewById(R.id.coordinator_layout),
                        getString(R.string.error_unknown), Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    private class LoginTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            if (mLoginUsername.getText().length() != 0
                    && mLoginPassword.getText().length() != 0
                    && checkPassword(mLoginPassword, false)) {
                mActionLoginButton.setEnabled(true);
            } else {
                mActionLoginButton.setEnabled(false);
            }
        }
    }


    private class CreateAccountTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            if (mCreateAccountUsername.getText().length() != 0
                    && mCreateAccountEmail.getText().length() != 0
                    && checkEmail(mCreateAccountEmail, false)
                    && mCreateAccountPassword.getText().length() != 0
                    && checkPassword(mCreateAccountPassword, false)) {
                mActionCreateAccountButton.setEnabled(true);
            } else {
                mActionCreateAccountButton.setEnabled(false);
            }
        }
    }
}
