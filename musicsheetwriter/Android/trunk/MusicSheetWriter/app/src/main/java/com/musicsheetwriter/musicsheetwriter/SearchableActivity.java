package com.musicsheetwriter.musicsheetwriter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.view.inputmethod.InputMethodManager;

import com.musicsheetwriter.musicsheetwriter.authentication.ILoggingManager;
import com.musicsheetwriter.musicsheetwriter.fragmenttab.SearchScoresFragment;
import com.musicsheetwriter.musicsheetwriter.fragmenttab.SearchUsersFragment;

import java.io.IOException;
import java.util.ArrayList;

public class SearchableActivity extends AppCompatActivity implements ILoggingManager {

    private int mLoggedUserId;
    private Account mLoggedUserAccount;

    // AppBar elements
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private SectionPageAdapter mPageAdapter;
    private ViewPager mViewPager;

    private String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        loadConnectedUser();

        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getString("query");
        }

        if (mQuery == null) {
            // Get the intent, verify the action and get the query
            Intent intent = getIntent();
            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                mQuery = intent.getStringExtra(SearchManager.QUERY);
            }
        }
    }

    private void buildTabs() {
        if (mPageAdapter != null) {
            mPageAdapter.clear();
        }
        int currentPage = mViewPager.getCurrentItem();

        mPageAdapter = new SectionPageAdapter(this.getSupportFragmentManager());

        Bundle arguments = new Bundle();
        arguments.putString(SearchScoresFragment.QUERY, mQuery);
        mPageAdapter.addTab(getString(R.string.tab_scores), R.drawable.ic_library_music_black,
                SearchScoresFragment.class, arguments);
        arguments = new Bundle();
        arguments.putString(SearchUsersFragment.QUERY, mQuery);
        mPageAdapter.addTab(getString(R.string.tab_users), R.drawable.ic_people_black,
                SearchUsersFragment.class, arguments);

        mViewPager.setAdapter(mPageAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            if (mTabLayout.getTabAt(i) != null) {
                TabLayout.Tab t = mTabLayout.getTabAt(i);
                if (t != null) {
                    t.setIcon(mPageAdapter.getIcon(i));
                }
            }
        }
        mViewPager.setCurrentItem(currentPage);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        outState.putInt("tab", viewPager.getCurrentItem());
        outState.putString("query", mQuery);
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
        int menuId = R.menu.menu_searchable;
        getMenuInflater().inflate(menuId, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(new OnSearchViewTextListener());
        searchView.setQuery(mQuery, true);
        searchView.clearFocus();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean requestConnectedUser() {
        AccountManager am = AccountManager.get(SearchableActivity.this);
        Account[] accounts = am.getAccountsByType(getString(R.string.am_account_type));
        if (accounts.length == 0) {
            // No account are stored
            Bundle b = new Bundle();
            b.putBoolean(LoginActivity.ARG_SHOW_SLASH, false);
            am.addAccount(getString(R.string.am_account_type), getString(R.string.am_auth_token_type),
                    null, b, SearchableActivity.this, getAddAccountCallback(Bundle.class), null);
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
        AccountManager am = AccountManager.get(SearchableActivity.this);
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
                am.removeAccount(ac, SearchableActivity.this, getRemoveAccountCallback(Bundle.class), null);
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
                        Intent i = new Intent(SearchableActivity.this, MainActivity.class);
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
                        Intent i = new Intent(SearchableActivity.this, MainActivity.class);
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
                        AccountManager am = AccountManager.get(SearchableActivity.this);
                        am.setAuthToken(getLoggedUserAccount(),
                                getString(R.string.am_auth_token_type),
                                getString(R.string.am_auth_token_none));
                    }
                }
            }
        };
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
            return Fragment.instantiate(SearchableActivity.this, info.clss.getName(), info.args);
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

    private class OnSearchViewTextListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            // Force hiding keyboard
            if (getCurrentFocus() != null) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
            mQuery = query;
            buildTabs();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            mQuery = newText;
            return true;
        }

    }

}
