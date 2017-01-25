package com.musicsheetwriter.musicsheetwriter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.musicsheetwriter.musicsheetwriter.authentication.ILoggingManager;
import com.musicsheetwriter.musicsheetwriter.listadapter.DividerItemDecoration;
import com.musicsheetwriter.musicsheetwriter.listadapter.OnUserInteractionListener;
import com.musicsheetwriter.musicsheetwriter.listadapter.UserListAdapter;
import com.musicsheetwriter.musicsheetwriter.model.User;
import com.musicsheetwriter.musicsheetwriter.network.MswApiAsyncConnectionResponseHandler;
import com.musicsheetwriter.musicsheetwriter.network.MswApiClient;
import com.musicsheetwriter.musicsheetwriter.network.MswApiException;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponse;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponseError;
import com.musicsheetwriter.musicsheetwriter.network.httputils.HttpException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

public class SubscribersActivity extends AppCompatActivity implements ILoggingManager,
        OnUserInteractionListener {

    public static final String ARG_USER_ID = "user_id";

    private int mLoggedUserId;
    private Account mLoggedUserAccount;

    private RecyclerView mList;
    private SwipeRefreshLayout mSwipeRefresh;
    private ArrayList<User> mUsers;

    private int mUserId;

    private ProgressBar mProgressBar;
    private View mNoConnection;
    private View mEmptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribers);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        loadConnectedUser();

        mUserId = getIntent().getIntExtra(ARG_USER_ID, 0);

        mList = (RecyclerView) findViewById(R.id.list);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setOnRefreshListener(new OnUserListRefresh());

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mNoConnection = findViewById(R.id.layout_no_connection);
        mEmptyList = findViewById(R.id.layout_empty_list);

        mList.setItemAnimator(new DefaultItemAnimator());

        mList.addItemDecoration(new DividerItemDecoration(SubscribersActivity.this, DividerItemDecoration.VERTICAL_LIST));
        mList.setLayoutManager(new LinearLayoutManager(SubscribersActivity.this));

        if (savedInstanceState != null) {
            mUsers = savedInstanceState.getParcelableArrayList("user_list");
        }

        if (mUsers == null) {
            // No data to show yet
            hideLoadedDataLayouts();
            // Request for data
            sendGetUsers();
        } else {
            setListAdapter();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_refresh:
                mSwipeRefresh.setRefreshing(true);
                refreshUserList();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("user_list", mUsers);
        outState.putInt("progress_bar_visibility", mProgressBar.getVisibility());
        outState.putInt("no_connection_visibility", mNoConnection.getVisibility());
    }

    @Override
    @SuppressWarnings("ResourceType")
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mProgressBar.setVisibility(savedInstanceState.getInt("progress_bar_visibility", View.GONE));
            mNoConnection.setVisibility(savedInstanceState.getInt("no_connection_visibility", View.GONE));
        }
        refreshUserList();
    }

    @Override
    public boolean requestConnectedUser() {
        AccountManager am = AccountManager.get(SubscribersActivity.this);
        Account[] accounts = am.getAccountsByType(getString(R.string.am_account_type));
        if (accounts.length == 0) {
            // No account are stored
            Bundle b = new Bundle();
            b.putBoolean(LoginActivity.ARG_SHOW_SLASH, false);
            am.addAccount(getString(R.string.am_account_type), getString(R.string.am_auth_token_type),
                    null, b, SubscribersActivity.this, getAddAccountCallback(Bundle.class), null);
            return false;
        } else {
            // An account is stored
            Account account = accounts[0];
            int userId = Integer.valueOf(am.getUserData(accounts[0], getString(R.string.am_user_data_user_id)));

            mLoggedUserAccount = account;
            mLoggedUserId = userId;
            return true;
        }
    }

    @Override
    public Account getLoggedUserAccount() {
        return mLoggedUserAccount;
    }

    @Override
    public int getLoggedUserId() {
        return mLoggedUserId;
    }

    @Override
    public boolean loadConnectedUser() {
        AccountManager am = AccountManager.get(SubscribersActivity.this);
        Account[] accounts = am.getAccountsByType(getString(R.string.am_account_type));
        if (accounts.length == 0) {
            // No account are stored
            return false;
        } else {
            // An account is stored
            Account account = accounts[0];
            int userId = Integer.valueOf(am.getUserData(accounts[0], getString(R.string.am_user_data_user_id)));

            mLoggedUserAccount = account;
            mLoggedUserId = userId;
            return true;
        }
    }

    @Override
    public void removeConnectedUser() {
        // Remove the account from the accountManager
        AccountManager am = AccountManager.get(SubscribersActivity.this);
        am.invalidateAuthToken(getString(R.string.am_account_type), null);
        Account[] accounts = am.getAccountsByType(getString(R.string.am_account_type));
        for (Account ac : accounts) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                am.removeAccount(ac, SubscribersActivity.this, getRemoveAccountCallback(Bundle.class), null);
            } else {
                //noinspection deprecation
                am.removeAccount(ac, getRemoveAccountCallback(Boolean.class), null);
            }
            mLoggedUserAccount = null;
            mLoggedUserId = 0;
        }
    }

    @Override
    public <T> AccountManagerCallback<T> getAddAccountCallback(Class<T> type) {
        return new AccountManagerCallback<T>() {
            @Override
            public void run(AccountManagerFuture<T> future) {
                if (future.isDone()) {
                    try {
                        future.getResult();
                        // Back to the main activity
                        Intent i = new Intent(SubscribersActivity.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    @Override
    public <T> AccountManagerCallback<T> getRemoveAccountCallback(Class<T> type) {
        return new AccountManagerCallback<T>() {
            @Override
            public void run(AccountManagerFuture<T> future) {
                if (future.isDone()) {
                    try {
                        future.getResult();
                        // Back to the main activity
                        Intent i = new Intent(SubscribersActivity.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    @Override
    public <T> AccountManagerCallback<T> getGetAuthTokenCallback(Class<T> type) {
        return new AccountManagerCallback<T>() {
            @Override
            public void run(AccountManagerFuture<T> future) {
                if (future.isDone()) {
                    try {
                        future.getResult();
                    } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                        e.printStackTrace();
                        // Set Default token
                        AccountManager am = AccountManager.get(SubscribersActivity.this);
                        am.setAuthToken(getLoggedUserAccount(),
                                getString(R.string.am_auth_token_type),
                                getString(R.string.am_auth_token_none));
                    }
                }
            }
        };
    }

    @Override
    public void onSubscribe(User item, View view) {
        sendAddSubscription(item);
    }

    @Override
    public void onUnsubscribe(User item, View view) {
        sendRemoveSubscription(item);
    }

    @Override
    public void onViewUserProfile(User item, View view) {
        Intent intent = new Intent(SubscribersActivity.this, UserProfileActivity.class);
        Bundle b = new Bundle();
        b.putParcelable(UserProfileActivity.ARG_USER, item);
        intent.putExtras(b);
        startActivity(intent);

    }

    public void setListAdapter() {
        if (!mUsers.isEmpty()) {
            mEmptyList.setVisibility(View.GONE);
            mList.setVisibility(View.VISIBLE);
            mSwipeRefresh.setVisibility(View.VISIBLE);

            mList.setAdapter(new UserListAdapter(SubscribersActivity.this, mUsers, SubscribersActivity.this));
        } else {
            mEmptyList.setVisibility(View.VISIBLE);
            mSwipeRefresh.setVisibility(View.GONE);
            mList.setVisibility(View.GONE);
        }
    }

    public void hideLoadedDataLayouts() {
        mProgressBar.setVisibility(View.VISIBLE);

        mSwipeRefresh.setVisibility(View.GONE);
        mList.setVisibility(View.GONE);
        mNoConnection.setVisibility(View.GONE);
    }

    public void showLoadedDataLayouts() {
        mNoConnection.setVisibility(View.GONE);

        mList.setVisibility(View.VISIBLE);
        mSwipeRefresh.setVisibility(View.VISIBLE);
    }

    private void sendGetUsers() {
        new MswApiClient(SubscribersActivity.this, getLoggedUserAccount(), getGetAuthTokenCallback(Bundle.class))
                .getAccountSubscribers(mUserId, new MswApiAsyncConnectionResponseHandler() {

                    @Override
                    public void onFinish() {
                        mProgressBar.setVisibility(View.GONE);
                        mSwipeRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                        ArrayList<User> users;
                        try {
                            JSONArray json = mswApiResponse.getJSONArray();
                            users = new ArrayList<>(json.length());

                            // Set subscriptions
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonUserSubscription = json.getJSONObject(i);
                                User user = new User(jsonUserSubscription.getInt("id"),
                                        jsonUserSubscription.getString("username"),
                                        jsonUserSubscription.getString("photo"),
                                        jsonUserSubscription.getBoolean("is_subscription"));

                                user.setNbSubscribers(jsonUserSubscription.getInt("nb_subscribers"));
                                user.setNbScores(jsonUserSubscription.getInt("nb_scores"));

                                users.add(user);
                            }
                        } catch (JSONException e) {
                            throw new MswApiException(e);
                        }
                        mUsers = users;
                        setListAdapter();
                        showLoadedDataLayouts();
                    }

                    @Override
                    public void onFailure(MswApiResponseError mswApiResponseError) throws MswApiException {
                        Snackbar.make(findViewById(R.id.coordinator_layout),
                                getString(R.string.error_message), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(MswApiException mswApiException) {
                        if (mswApiException.getCause() instanceof HttpException &&
                                mswApiException.getCause().getCause() instanceof ConnectException) {
                            if (mList.getVisibility() != View.VISIBLE) {
                                mNoConnection.setVisibility(View.VISIBLE);
                            } else {
                                Snackbar.make(findViewById(R.id.coordinator_layout),
                                        getString(R.string.error_no_network), Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            Snackbar.make(findViewById(R.id.coordinator_layout),
                                    getString(R.string.error_unknown), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendAddSubscription(final User user) {
        new MswApiClient(SubscribersActivity.this, getLoggedUserAccount(), getGetAuthTokenCallback(Bundle.class))
                .addAccountSubscription(getLoggedUserId(), user.getId(), new MswApiAsyncConnectionResponseHandler() {

                    @Override
                    protected void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                        user.setIsSubscription(true);
                    }

                    @Override
                    public void onFailure(MswApiResponseError mswApiResponseError) throws MswApiException {
                        mList.getAdapter().notifyDataSetChanged();

                        Snackbar.make(findViewById(R.id.coordinator_layout),
                                getString(R.string.error_message), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(MswApiException mswApiException) {
                        mList.getAdapter().notifyDataSetChanged();

                        if (mswApiException.getCause() instanceof HttpException &&
                                mswApiException.getCause().getCause() instanceof ConnectException) {
                            Snackbar.make(findViewById(R.id.coordinator_layout),
                                    getString(R.string.error_no_network), Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(findViewById(R.id.coordinator_layout),
                                    getString(R.string.error_unknown), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendRemoveSubscription(final User user) {
        new MswApiClient(SubscribersActivity.this, getLoggedUserAccount(), getGetAuthTokenCallback(Bundle.class))
                .removeAccountSubscription(getLoggedUserId(), user.getId(), new MswApiAsyncConnectionResponseHandler() {

                    @Override
                    protected void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                        user.setIsSubscription(false);
                    }

                    @Override
                    public void onFailure(MswApiResponseError mswApiResponseError) throws MswApiException {
                        mList.getAdapter().notifyDataSetChanged();

                        Snackbar.make(findViewById(R.id.coordinator_layout),
                                getString(R.string.error_message), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(MswApiException mswApiException) {
                        mList.getAdapter().notifyDataSetChanged();

                        if (mswApiException.getCause() instanceof HttpException &&
                                mswApiException.getCause().getCause() instanceof ConnectException) {
                            Snackbar.make(findViewById(R.id.coordinator_layout),
                                    getString(R.string.error_no_network), Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(findViewById(R.id.coordinator_layout),
                                    getString(R.string.error_unknown), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void refreshUserList() {
        sendGetUsers();
    }

    private class OnUserListRefresh implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            refreshUserList();
        }
    }

}
