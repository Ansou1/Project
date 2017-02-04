package com.musicsheetwriter.musicsheetwriter.authentication;

import android.accounts.Account;
import android.accounts.AccountManagerCallback;
import android.support.annotation.Nullable;

public interface ILoggingManager {
    /**
     * Return the account of the connected user
     * @return the account of the connected User, null if no user is connected
     */
    @Nullable
    Account getLoggedUserAccount();

    /**
     * Return the ID of the connected user
     * @return the ID of the connected User, -1 if no user is connected
     */
    int getLoggedUserId();

    /**
     * Gets the accounts from the account manager and select the Msw Account.
     * @return True if the account exists, false otherwise
     */
    boolean loadConnectedUser();

    /**
     * Gets the accounts from the account manager and select the Msw Account. If no such account
     * exists, the LoginActivity is launched.
     * @return True if the account exists, false otherwise.
     */
    boolean requestConnectedUser();

    /**
     * Remove the user from the AccountManager
     */
    void removeConnectedUser();

    /**
     * Return the callbacks to be used after adding an account in the accountManager
     * @param type This value contained in variable is unused
     * @param <T> The type of the AccountManagerFuture
     * @return The callback instance
     */
    <T> AccountManagerCallback<T> getAddAccountCallback(Class<T> type);

    /**
     * Return the callbacks to be used after removing an account in the accountManager
     * @param type This value contained in variable is unused
     * @param <T> The type of the AccountManagerFuture
     * @return The callback instance
     */
    <T> AccountManagerCallback<T> getRemoveAccountCallback(Class<T> type);

    /**
     * Return the callbacks to be used after getting a new authentication token for an account
     * @param type This value contained in variable is unused
     * @param <T> The type of the AccountManagerFuture
     * @return The callback instance
     */
    <T> AccountManagerCallback<T> getGetAuthTokenCallback(Class<T> type);
}
