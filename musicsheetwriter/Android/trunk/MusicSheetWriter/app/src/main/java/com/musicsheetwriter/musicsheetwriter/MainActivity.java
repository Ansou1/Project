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
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
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

import com.musicsheetwriter.musicsheetwriter.authentication.ILoggingManager;
import com.musicsheetwriter.musicsheetwriter.fragmenttab.ProfileFragment;
import com.musicsheetwriter.musicsheetwriter.fragmenttab.ProfileNoConnectionFragment;
import com.musicsheetwriter.musicsheetwriter.fragmenttab.UserFavouriteScoresFragment;
import com.musicsheetwriter.musicsheetwriter.fragmenttab.UserMyScoresFragment;
import com.musicsheetwriter.musicsheetwriter.fragmenttab.UserSubscriptionsFragment;
import com.musicsheetwriter.musicsheetwriter.network.MswApiAsyncConnectionResponseHandler;
import com.musicsheetwriter.musicsheetwriter.network.MswApiClient;
import com.musicsheetwriter.musicsheetwriter.network.MswApiException;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponse;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponseError;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ILoggingManager {

    //public static final int TAB_HOME = 0;
    public static final int TAB_MY_SCORE = 0;
    public static final int TAB_FAV_SCORE = 1;
    public static final int TAB_SUBSCRIPTIONS = 2;
    public static final int TAB_MY_PROFILE = 3;

    private int mLoggedUserId;
    private Account mLoggedUserAccount;

    // AppBar elements
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private SectionPageAdapter mPageAdapter;
    private ViewPager mViewPager;

    private FloatingActionButton mUploadScoreFab;
    private FabVisibilitySetter mFabVisibilitySetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.addOnPageChangeListener(new PageTitleSetter());

        mUploadScoreFab = (FloatingActionButton) findViewById(R.id.fab);
        mFabVisibilitySetter = new FabVisibilitySetter();

        setConnectedMode(loadConnectedUser());
    }

    public void setConnectedMode(boolean connectedMode) {
        // Recreate the options menu
        supportInvalidateOptionsMenu();
        // Recreate the tabs
        buildTabs(connectedMode);
        if (connectedMode) {
            setFabVisibility(mViewPager.getCurrentItem());
            mViewPager.addOnPageChangeListener(mFabVisibilitySetter);
        } else {
            mViewPager.removeOnPageChangeListener(mFabVisibilitySetter);
            mUploadScoreFab.hide();
        }
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

        mViewPager.setCurrentItem(savedInstanceState.getInt("tab", 0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuId;
        if (loadConnectedUser()) {
            // if the user is connected, the menu with the logout option is inflated
            menuId = R.menu.menu_main;
        } else {
            // otherwise, the menu with the login option is inflated
            menuId = R.menu.menu_main_no_connection;
        }
        getMenuInflater().inflate(menuId, menu);

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
            case R.id.action_login:
                requestConnectedUser();
                return true;
            case R.id.action_logout:
                sendLogoutRequest();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean requestConnectedUser() {
        AccountManager am = AccountManager.get(MainActivity.this);
        Account[] accounts = am.getAccountsByType(getString(R.string.am_account_type));
        if (accounts.length == 0) {
            // No account are stored
            Bundle b = new Bundle();
            b.putBoolean(LoginActivity.ARG_SHOW_SLASH, false);
            am.addAccount(getString(R.string.am_account_type), getString(R.string.am_auth_token_type),
                    null, b, MainActivity.this, getAddAccountCallback(Bundle.class), null);
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
        AccountManager am = AccountManager.get(MainActivity.this);
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
                am.removeAccount(ac, MainActivity.this, getRemoveAccountCallback(Bundle.class), null);
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
                        // set the activity in connected mode
                        setConnectedMode(loadConnectedUser());
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
                        // set the activity in guest mode
                        setConnectedMode(loadConnectedUser());
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
                        AccountManager am = AccountManager.get(MainActivity.this);
                        am.setAuthToken(getLoggedUserAccount(),
                                getString(R.string.am_auth_token_type),
                                getString(R.string.am_auth_token_none));
                    }
                }
            }
        };
    }

    public void setFabVisibility(int position) {
        switch (position) {
            /*case TAB_HOME: // HomeFragment
                mUploadScoreFab.show();
                break;*/
            case TAB_MY_SCORE: // MyScoreFragment
                mUploadScoreFab.show();
                break;
            default:
                mUploadScoreFab.hide();
        }
    }

    private void buildTabs(boolean connectedMode) {
        if (mPageAdapter != null) {
            mPageAdapter.clear();
        }
        mPageAdapter = new SectionPageAdapter(this.getSupportFragmentManager());


        Bundle arguments;
        if (connectedMode) {
            // if the user is connected, it can access to tabs like
            // "favourite score", subscriptions or his profile
            /*mPageAdapter.addTab(getString(R.string.tab_home), R.drawable.tab_home,
                    HomeFragment.class, null);*/

            arguments = new Bundle();
            arguments.putInt(UserMyScoresFragment.USER_ID, getLoggedUserId());
            arguments.putBoolean(UserMyScoresFragment.REMOVABLE_ITEM, true);
            mPageAdapter.addTab(getString(R.string.tab_my_scores), R.drawable.tab_scores,
                    UserMyScoresFragment.class, arguments);

            arguments = new Bundle();
            arguments.putInt(UserFavouriteScoresFragment.USER_ID, getLoggedUserId());
            mPageAdapter.addTab(getString(R.string.tab_favourite), R.drawable.tab_favorite_score,
                    UserFavouriteScoresFragment.class, arguments);

            arguments = new Bundle();
            arguments.putBoolean(UserSubscriptionsFragment.DISPLAY_IN_GRID, true);
            arguments.putInt(UserSubscriptionsFragment.USER_ID, getLoggedUserId());
            mPageAdapter.addTab(getString(R.string.tab_subscriptions), R.drawable.tab_subscriptions,
                    UserSubscriptionsFragment.class, arguments);

            mPageAdapter.addTab(getString(R.string.tab_profile), R.drawable.tab_my_profile,
                    ProfileFragment.class, null);
        } else {
            // otherwise, the only tab the user can access is the hom screen and the page
            // suggesting to him to connect
            /*mPageAdapter.addTab(getString(R.string.tab_home), R.drawable.tab_home,
                    HomeFragment.class, null);*/
            mPageAdapter.addTab(getString(R.string.tab_profile), R.drawable.tab_my_profile,
                    ProfileNoConnectionFragment.class, null);
        }

        mViewPager.setAdapter(mPageAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            if (mTabLayout.getTabAt(i) != null) {
                TabLayout.Tab t = mTabLayout.getTabAt(i);
                if (t != null) {
                    t.setText("");
                    t.setIcon(mPageAdapter.getIcon(i));
                }
            }
        }
        setTitle(mPageAdapter.getPageTitle(mViewPager.getCurrentItem()));
    }

    private void sendLogoutRequest() {
        new MswApiClient().logout(new MswApiAsyncConnectionResponseHandler() {

            private ProgressDialog loader;

            @Override
            public void onStart() {
                loader = new ProgressDialog(MainActivity.this);
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

    public class SectionPageAdapter extends FragmentStatePagerAdapter {

        private final ArrayList<TabInfo> mTabs = new ArrayList<>();

        private class TabInfo {
            private final String title;
            private final int resIcon;
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(String _title, int _resIcon, Class<?> _class, Bundle _args) {
                title = _title;
                resIcon = _resIcon;
                clss = _class;
                args = _args;
            }
        }

        public SectionPageAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addTab(String title, int resIcon, Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(title, resIcon, clss, args);
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
            return Fragment.instantiate(MainActivity.this, info.clss.getName(), info.args);
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public String getPageTitle(int position) {
            return mTabs.get(position).title;
        }

        public int getIcon(int position) {
            return mTabs.get(position).resIcon;
        }
    }

    public class FabVisibilitySetter extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            setFabVisibility(mViewPager.getCurrentItem());
        }

        @Override
        public void onPageSelected(int position) {
            setFabVisibility(mViewPager.getCurrentItem());
        }
    }

    public class PageTitleSetter extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            if (mViewPager.getAdapter() != null) {
                setTitle(mViewPager.getAdapter().getPageTitle(position));
            }
            mAppBarLayout.setExpanded(true, true);
        }
    }
}
