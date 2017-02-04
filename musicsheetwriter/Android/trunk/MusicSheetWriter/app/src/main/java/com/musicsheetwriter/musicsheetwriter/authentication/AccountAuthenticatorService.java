package com.musicsheetwriter.musicsheetwriter.authentication;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import com.musicsheetwriter.musicsheetwriter.LoginActivity;
import com.musicsheetwriter.musicsheetwriter.network.MswApiAsyncConnection;
import com.musicsheetwriter.musicsheetwriter.network.MswApiClient;
import com.musicsheetwriter.musicsheetwriter.network.MswApiException;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponse;

/**
 * Authenticator service that returns a subclass of AbstractAccountAuthenticator in onBind()
 */
public class AccountAuthenticatorService extends Service {
    private static AccountAuthenticatorImpl sAccountAuthenticator = null;

    public AccountAuthenticatorService() {
        super();
    }

    public IBinder onBind(Intent intent) {
        IBinder ret = null;
        if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT))
            ret = getAuthenticator().getIBinder();
        return ret;
    }

    private AccountAuthenticatorImpl getAuthenticator() {
        if (sAccountAuthenticator == null)
            sAccountAuthenticator = new AccountAuthenticatorImpl(this);
        return sAccountAuthenticator;
    }

    private static class AccountAuthenticatorImpl extends AbstractAccountAuthenticator {
        private Context mContext;

        public AccountAuthenticatorImpl(Context context) {
            super(context);
            mContext = context;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                                 String authTokenType, String[] requiredFeatures, Bundle options)
                throws NetworkErrorException {

            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.putExtra(LoginActivity.ARG_ACCOUNT_TYPE, accountType);
            intent.putExtra(LoginActivity.ARG_AUTH_TYPE, authTokenType);
            intent.putExtra(LoginActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
            intent.putExtra(LoginActivity.ARG_SHOW_SLASH,
                    options.getBoolean(LoginActivity.ARG_SHOW_SLASH, true));
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

            Bundle bundle = new Bundle();
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
            return bundle;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                                   String authTokenType, Bundle options)
                throws NetworkErrorException {
            // Extract the username and password from the Account Manager, and ask
            // the server for an appropriate AuthToken.
            final AccountManager am = AccountManager.get(mContext);

            String authToken = am.peekAuthToken(account, authTokenType);

            // Lets give another try to authenticate the user
            if (TextUtils.isEmpty(authToken)) {
                // The reconnection can be made only if the context is an activity
                final String password = am.getPassword(account);
                if (password != null) {
                    // Send request
                    MswApiAsyncConnection connection = new MswApiClient().login(account.name, password, null);
                    // Wait for the end of the request
                    MswApiResponse loginResponse;
                    try {
                        loginResponse = connection.getResponse();
                    } catch (MswApiException e) {
                        e.printStackTrace();
                        throw new NetworkErrorException("Unable to get a new authenticate token", e);
                    }
                    // If the login is OK...
                    if (!loginResponse.isError()) {
                        // ...get auth Token
                        try {
                            authToken = loginResponse.getAuthToken();
                        } catch (MswApiException e) {
                            throw new NetworkErrorException("Unable to get a new authenticate token", e);
                        }
                    } else {
                        // ...reset the authToken to empty string
                        authToken = "";
                    }
                }
            }

            // If we get an authToken - we return it
            if (!TextUtils.isEmpty(authToken)) {
                final Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                return result;
            }

            // If we get here, then we couldn't access the user's password - so we
            // need to re-prompt them for their credentials. We do that by creating
            // an intent to display our AuthenticatorActivity.
            final Intent intent = new Intent(mContext, LoginActivity.class);
            intent.putExtra(LoginActivity.ARG_ACCOUNT_TYPE, account.type);
            intent.putExtra(LoginActivity.ARG_AUTH_TYPE, authTokenType);
            intent.putExtra(LoginActivity.ARG_IS_ADDING_NEW_ACCOUNT, false);
            intent.putExtra(LoginActivity.ARG_ACCOUNT_NAME, account.name);
            String userId = options.getString(AccountManager.KEY_USERDATA);
            if (userId != null) {
                intent.putExtra(LoginActivity.ARG_ACCOUNT_ID,
                        Integer.valueOf(userId));
            }
            intent.putExtra(LoginActivity.ARG_SHOW_SLASH, false);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

            final Bundle bundle = new Bundle();
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
            return bundle;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
                                         Bundle options) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getAuthTokenLabel(String authTokenType) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
                                  String[] features) throws NetworkErrorException {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
                                        String authTokenType, Bundle options) {
            return null;
        }
    }
}