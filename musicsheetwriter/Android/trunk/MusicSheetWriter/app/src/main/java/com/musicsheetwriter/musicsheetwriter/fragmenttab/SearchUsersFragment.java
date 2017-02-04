package com.musicsheetwriter.musicsheetwriter.fragmenttab;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.musicsheetwriter.musicsheetwriter.R;
import com.musicsheetwriter.musicsheetwriter.UserProfileActivity;
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

import java.net.ConnectException;
import java.util.ArrayList;


public class SearchUsersFragment extends Fragment implements OnUserInteractionListener {

    public static final String QUERY = "query";

    private RecyclerView mList;
    private SwipeRefreshLayout mSwipeRefresh;
    private ArrayList<User> mUsers;

    private ILoggingManager mLoggingManager;
    private String mQuery;

    private ProgressBar mProgressBar;
    private View mNoConnection;
    private View mEmptyList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuery = getArguments().getString(QUERY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_search_users, container, false);

        mList = (RecyclerView) view.findViewById(R.id.list);
        mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setOnRefreshListener(new OnScoreListRefresh());

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        mNoConnection = view.findViewById(R.id.layout_no_connection);
        mEmptyList = view.findViewById(R.id.layout_empty_list);

        mList.setItemAnimator(new DefaultItemAnimator());

        mList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mList.setLayoutManager(new LinearLayoutManager(view.getContext()));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                mSwipeRefresh.setRefreshing(true);
                refreshScoreList();
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
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mProgressBar.setVisibility(savedInstanceState.getInt("progress_bar_visibility", View.GONE));
            mNoConnection.setVisibility(savedInstanceState.getInt("no_connection_visibility", View.GONE));
        }
        refreshScoreList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mLoggingManager = (ILoggingManager) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ILoggingManager");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mLoggingManager = null;
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
        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
        Bundle b = new Bundle();
        b.putParcelable(UserProfileActivity.ARG_USER, item);
        intent.putExtras(b);
        getActivity().startActivity(intent);

    }

    public void setListAdapter() {
        if (getView() != null) {
            if (!mUsers.isEmpty()) {
                mEmptyList.setVisibility(View.GONE);
                mList.setVisibility(View.VISIBLE);
                mSwipeRefresh.setVisibility(View.VISIBLE);

                mList.setAdapter(new UserListAdapter(getContext(), mUsers, this));
            } else {
                mEmptyList.setVisibility(View.VISIBLE);
                mSwipeRefresh.setVisibility(View.GONE);
                mList.setVisibility(View.GONE);            }
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
        new MswApiClient(getActivity(), mLoggingManager.getLoggedUserAccount(), mLoggingManager.getGetAuthTokenCallback(Bundle.class))
                .getAccountList(mQuery, new MswApiAsyncConnectionResponseHandler() {

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

                                user.setNbSubscriptions(jsonUserSubscription.getInt("nb_subscriptions"));
                                user.setNbSubscribers(jsonUserSubscription.getInt("nb_subscribers"));
                                user.setNbScores(jsonUserSubscription.getInt("nb_scores"));
                                user.setNbFavourites(jsonUserSubscription.getInt("nb_favourites"));

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
                        if (getView() != null) {
                            Snackbar.make(getView(),
                                    getString(R.string.error_message), Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(MswApiException mswApiException) {
                        if (mswApiException.getCause() instanceof HttpException &&
                                mswApiException.getCause().getCause() instanceof ConnectException) {
                            if (mList.getVisibility() != View.VISIBLE) {
                                mNoConnection.setVisibility(View.VISIBLE);
                            } else {
                                if (getView() != null) {
                                    Snackbar.make(getView(),
                                            getString(R.string.error_no_network), Snackbar.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            if (getView() != null) {
                                Snackbar.make(getView(),
                                        getString(R.string.error_unknown), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void sendAddSubscription(final User user) {
        new MswApiClient(getActivity(), mLoggingManager.getLoggedUserAccount(), mLoggingManager.getGetAuthTokenCallback(Bundle.class))
                .addAccountSubscription(mLoggingManager.getLoggedUserId(), user.getId(), new MswApiAsyncConnectionResponseHandler() {

                    @Override
                    protected void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                        user.setIsSubscription(true);
                    }

                    @Override
                    public void onFailure(MswApiResponseError mswApiResponseError) throws MswApiException {
                        mList.getAdapter().notifyDataSetChanged();

                        if (getView() != null) {
                            Snackbar.make(getView(),
                                    getString(R.string.error_message), Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(MswApiException mswApiException) {
                        mList.getAdapter().notifyDataSetChanged();

                        if (mswApiException.getCause() instanceof HttpException &&
                                mswApiException.getCause().getCause() instanceof ConnectException) {
                            if (getView() != null) {
                                Snackbar.make(getView(),
                                        getString(R.string.error_no_network), Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            if (getView() != null) {
                                Snackbar.make(getView(),
                                        getString(R.string.error_unknown), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void sendRemoveSubscription(final User user) {
        new MswApiClient(getActivity(), mLoggingManager.getLoggedUserAccount(), mLoggingManager.getGetAuthTokenCallback(Bundle.class))
                .removeAccountSubscription(mLoggingManager.getLoggedUserId(), user.getId(), new MswApiAsyncConnectionResponseHandler() {

                    @Override
                    protected void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                        user.setIsSubscription(false);
                    }

                    @Override
                    public void onFailure(MswApiResponseError mswApiResponseError) throws MswApiException {
                        mList.getAdapter().notifyDataSetChanged();

                        if (getView() != null) {
                            Snackbar.make(getView(),
                                    getString(R.string.error_message), Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(MswApiException mswApiException) {
                        mList.getAdapter().notifyDataSetChanged();

                        if (mswApiException.getCause() instanceof HttpException &&
                                mswApiException.getCause().getCause() instanceof ConnectException) {
                            if (getView() != null) {
                                Snackbar.make(getView(),
                                        getString(R.string.error_no_network), Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            if (getView() != null) {
                                Snackbar.make(getView(),
                                        getString(R.string.error_unknown), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void refreshScoreList() {
        sendGetUsers();
    }

    private class OnScoreListRefresh implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            refreshScoreList();
        }
    }
}
