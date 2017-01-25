package com.musicsheetwriter.musicsheetwriter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.musicsheetwriter.musicsheetwriter.authentication.ILoggingManager;
import com.musicsheetwriter.musicsheetwriter.fragmenttab.UserAboutFragment;
import com.musicsheetwriter.musicsheetwriter.fragmenttab.UserHomeFragment;
import com.musicsheetwriter.musicsheetwriter.fragmenttab.UserMyScoresFragment;
import com.musicsheetwriter.musicsheetwriter.fragmenttab.UserSubscriptionsFragment;
import com.musicsheetwriter.musicsheetwriter.listadapter.OnUserInteractionListener;
import com.musicsheetwriter.musicsheetwriter.model.User;
import com.musicsheetwriter.musicsheetwriter.network.MswApiAsyncConnectionResponseHandler;
import com.musicsheetwriter.musicsheetwriter.network.MswApiClient;
import com.musicsheetwriter.musicsheetwriter.network.MswApiException;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponse;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponseError;
import com.musicsheetwriter.musicsheetwriter.network.httputils.HttpException;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity implements ILoggingManager,
        OnUserInteractionListener {

    public static final String ARG_USER = "user";

    public static final int TAB_HOME = 0;
    public static final int TAB_SCORES = 1;
    public static final int TAB_SUBSCRIPTIONS = 2;
    public static final int TAB_ABOUT = 3;

    private int mLoggedUserId;
    private Account mLoggedUserAccount;

    // AppBar elements
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private SectionPageAdapter mPageAdapter;
    private ViewPager mViewPager;

    private User mUser;

    private ImageView mPicture;
    private TextView mSubscriberCount;
    private TextView mScoreCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mUser = getIntent().getParcelableExtra(ARG_USER);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(mUser.getUsername());
        }

        loadConnectedUser();

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        mPicture = (ImageView) findViewById(R.id.picture);
        mSubscriberCount = (TextView) findViewById(R.id.subscribers_count);
        mScoreCount = (TextView) findViewById(R.id.scores_count);

        Picasso.with(this)
                .load(mUser.getPhoto())
                .fit()
                .centerCrop()
                .noFade()
                .error(R.drawable.default_avatar)
                .into(mPicture);

        mSubscriberCount.setText(String.valueOf(mUser.getNbSubscribers()));
        mScoreCount.setText(String.valueOf(mUser.getNbScores()));

        buildTabs();
    }

    private void buildTabs() {
        if (mPageAdapter != null) {
            mPageAdapter.clear();
        }
        int currentPage = mViewPager.getCurrentItem();

        mPageAdapter = new SectionPageAdapter(this.getSupportFragmentManager());

        Bundle arguments = new Bundle();
        arguments.putInt(UserHomeFragment.USER_ID, mUser.getId());
        mPageAdapter.addTab(getString(R.string.tab_home), UserHomeFragment.class, arguments);

        arguments = new Bundle();
        arguments.putInt(UserMyScoresFragment.USER_ID, mUser.getId());
        arguments.putBoolean(UserMyScoresFragment.REMOVABLE_ITEM, false);
        mPageAdapter.addTab(getString(R.string.tab_scores), UserMyScoresFragment.class, arguments);

        arguments = new Bundle();
        arguments.putInt(UserSubscriptionsFragment.USER_ID, mUser.getId());
        arguments.putBoolean(UserSubscriptionsFragment.DISPLAY_IN_GRID, false);
        mPageAdapter.addTab(getString(R.string.tab_subscriptions),
                UserSubscriptionsFragment.class, arguments);

        arguments = new Bundle();
        arguments.putInt(UserAboutFragment.USER_ID, mUser.getId());
        mPageAdapter.addTab(getString(R.string.tab_about_user), UserAboutFragment.class, arguments);

        mViewPager.setAdapter(mPageAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(currentPage);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        outState.putInt("tab", viewPager.getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mViewPager.setCurrentItem(savedInstanceState.getInt("tab", 0));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuId;
        if (loadConnectedUser()) {
            // if the user is connected, the menu with the logout option is inflated
            menuId = R.menu.menu_user;
        } else {
            // otherwise, the menu with the login option is inflated
            menuId = R.menu.menu_user_no_connection;
        }
        getMenuInflater().inflate(menuId, menu);

        // Set the favourite checkbox
        MenuItem item = menu.findItem(R.id.action_subscribe);
        item.setChecked(mUser.isSubscription());
        if (item.isChecked()) {
            item.setIcon(R.drawable.toolbar_subscribe_checked_white);
            item.setTitle(R.string.action_unsubscribe);
        } else {
            item.setIcon(R.drawable.toolbar_subscribe_white);
            item.setTitle(R.string.action_subscribe);
        }

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.onActionViewCollapsed();
                // Return false to keep the default implentation of submit queries
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_subscribe:
                item.setChecked(!item.isChecked());

                if (item.isChecked()) {
                    onSubscribe(mUser, item.getActionView());
                    item.setIcon(R.drawable.toolbar_subscribe_checked_white);
                    item.setTitle(R.string.action_unsubscribe);
                } else {
                    onUnsubscribe(mUser, item.getActionView());
                    item.setIcon(R.drawable.toolbar_subscribe_white);
                    item.setTitle(R.string.action_subscribe);
                }
                return true;
            case R.id.action_login:
                requestConnectedUser();
                return true;
            case R.id.action_logout:
                sendLogoutRequest();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mAppBarLayout.setExpanded(true, false);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mAppBarLayout.setExpanded(false, false);
        }
    }

    @Override
    public boolean requestConnectedUser() {
        AccountManager am = AccountManager.get(UserProfileActivity.this);
        Account[] accounts = am.getAccountsByType(getString(R.string.am_account_type));
        if (accounts.length == 0) {
            // No account are stored
            Bundle b = new Bundle();
            b.putBoolean(LoginActivity.ARG_SHOW_SLASH, false);
            am.addAccount(getString(R.string.am_account_type), getString(R.string.am_auth_token_type),
                    null, b, UserProfileActivity.this, getAddAccountCallback(Bundle.class), null);
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
        AccountManager am = AccountManager.get(UserProfileActivity.this);
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
        AccountManager am = AccountManager.get(this);
        am.invalidateAuthToken(getString(R.string.am_account_type), null);
        Account[] accounts = am.getAccountsByType(getString(R.string.am_account_type));
        for (Account ac : accounts) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                am.removeAccount(ac, UserProfileActivity.this, getRemoveAccountCallback(Bundle.class), null);
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
                        Intent i = new Intent(UserProfileActivity.this, MainActivity.class);
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
                        Intent i = new Intent(UserProfileActivity.this, MainActivity.class);
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
                        AccountManager am = AccountManager.get(UserProfileActivity.this);
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
        Intent intent = new Intent(UserProfileActivity.this, UserProfileActivity.class);
        Bundle b = new Bundle();
        b.putParcelable(UserProfileActivity.ARG_USER, item);
        intent.putExtras(b);
        startActivity(intent);
    }

    private void sendLogoutRequest() {
        new MswApiClient().logout(new MswApiAsyncConnectionResponseHandler() {

            private ProgressDialog loader;

            @Override
            public void onStart() {
                loader = new ProgressDialog(UserProfileActivity.this);
                loader.setTitle(R.string.logging_out);
                loader.show();
            }

            @Override
            public void onFinish() {
                if (loader != null) {
                    // dismiss the dialog
                    loader.dismiss();
                }
            }

            @Override
            public void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                removeConnectedUser();
            }

            @Override
            public void onFailure(MswApiResponseError mswApiResponseError) throws MswApiException {
                // Even if the logout has failed, the user is disconnected on the phone
                removeConnectedUser();
            }

            @Override
            public void onFailure(MswApiException mswApiException) {
                // Even if the logout has failed, the user is disconnected on the phone
                removeConnectedUser();
            }
        });
    }


    private void sendAddSubscription(final User user) {
        new MswApiClient(UserProfileActivity.this, getLoggedUserAccount(), getGetAuthTokenCallback(Bundle.class))
                .addAccountSubscription(getLoggedUserId(), user.getId(), new MswApiAsyncConnectionResponseHandler() {

                    @Override
                    protected void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                        user.setIsSubscription(true);
                    }

                    @Override
                    public void onFailure(MswApiResponseError mswApiResponseError) throws MswApiException {
                        supportInvalidateOptionsMenu();

                        Snackbar.make(findViewById(R.id.coordinator_layout),
                                getString(R.string.error_message), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(MswApiException mswApiException) {
                        supportInvalidateOptionsMenu();

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
        new MswApiClient(UserProfileActivity.this, getLoggedUserAccount(), getGetAuthTokenCallback(Bundle.class))
                .removeAccountSubscription(getLoggedUserId(), user.getId(), new MswApiAsyncConnectionResponseHandler() {

                    @Override
                    protected void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                        user.setIsSubscription(false);
                    }

                    @Override
                    public void onFailure(MswApiResponseError mswApiResponseError) throws MswApiException {
                        supportInvalidateOptionsMenu();

                        Snackbar.make(findViewById(R.id.coordinator_layout),
                                getString(R.string.error_message), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(MswApiException mswApiException) {
                        supportInvalidateOptionsMenu();

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

    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between pages.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the SectionPageAdapter supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct paged in the ViewPager whenever the selected
     * tab changes.
     */
    public class SectionPageAdapter extends FragmentStatePagerAdapter {

        private final ArrayList<TabInfo> mTabs = new ArrayList<>();

        private class TabInfo {
            private final String title;
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(String _title, Class<?> _class, Bundle _args) {
                title = _title;
                clss = _class;
                args = _args;
            }
        }

        public SectionPageAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addTab(String title, Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(title, clss, args);
            mTabs.add(info);
            notifyDataSetChanged();
        }

        public void clear() {
            mTabs.clear();
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(UserProfileActivity.this, info.clss.getName(), info.args);
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public String getPageTitle(int position) {
            return mTabs.get(position).title;
        }
    }
}
