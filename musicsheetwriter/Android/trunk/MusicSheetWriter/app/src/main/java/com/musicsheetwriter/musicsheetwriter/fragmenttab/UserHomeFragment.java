package com.musicsheetwriter.musicsheetwriter.fragmenttab;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.musicsheetwriter.musicsheetwriter.R;
import com.musicsheetwriter.musicsheetwriter.ScorePreviewActivity;
import com.musicsheetwriter.musicsheetwriter.UserProfileActivity;
import com.musicsheetwriter.musicsheetwriter.authentication.ILoggingManager;
import com.musicsheetwriter.musicsheetwriter.listadapter.OnScoreInteractionListener;
import com.musicsheetwriter.musicsheetwriter.listadapter.OnUserInteractionListener;
import com.musicsheetwriter.musicsheetwriter.listadapter.ScoreListAdapter;
import com.musicsheetwriter.musicsheetwriter.listadapter.UserListAdapter;
import com.musicsheetwriter.musicsheetwriter.model.Score;
import com.musicsheetwriter.musicsheetwriter.model.User;
import com.musicsheetwriter.musicsheetwriter.network.MswApiAsyncConnectionResponseHandler;
import com.musicsheetwriter.musicsheetwriter.network.MswApiClient;
import com.musicsheetwriter.musicsheetwriter.network.MswApiException;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponse;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponseError;
import com.musicsheetwriter.musicsheetwriter.network.httputils.HttpException;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnScoreInteractionListener}
 * interface.
 */
public class UserHomeFragment extends Fragment implements OnScoreInteractionListener, OnUserInteractionListener {

    public static final String USER_ID = "user_id";

    private static final int NB_DISPLAYED_SCORES = 5;
    private static final int NB_DISPLAYED_SUBSCRIPTIONS = 5;

    private User mUser;

    private DownloadManager mDownloadManager;
    private BroadcastReceiver mReceiver;
    private long mDownloadReference;

    private ILoggingManager mLoggingManager;
    private int mUserId;

    private ViewGroup mScoreLayout;
    private ViewGroup mSubscriptionLayout;
    private Button mMoreScores;
    private Button mMoreSubscriptions;

    private ProgressBar mProgressBar;
    private View mNoConnection;
    private View mEmptyList;

    private ViewPager mMainPager;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserHomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDownloadManager = (DownloadManager) getActivity().getSystemService(Activity.DOWNLOAD_SERVICE);
        //set filter to only when download is complete and register broadcast receiver
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mReceiver = new OnDownloadComplete();
        getActivity().registerReceiver(mReceiver, filter);

        if (getArguments() != null) {
            mUserId = getArguments().getInt(USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_user_home, container, false);

        mMainPager = (ViewPager) getActivity().findViewById(R.id.pager);

        mScoreLayout = (ViewGroup) view.findViewById(R.id.score_layout);
        mSubscriptionLayout = (ViewGroup) view.findViewById(R.id.subscription_layout);
        mMoreScores = (Button) view.findViewById(R.id.more_scores);
        mMoreScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainPager.setCurrentItem(UserProfileActivity.TAB_SCORES);
            }
        });
        mMoreSubscriptions = (Button) view.findViewById(R.id.more_subscriptions);
        mMoreSubscriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainPager.setCurrentItem(UserProfileActivity.TAB_SUBSCRIPTIONS);
            }
        });

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        mNoConnection = view.findViewById(R.id.layout_no_connection);
        mEmptyList = view.findViewById(R.id.layout_empty_list);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mUser = savedInstanceState.getParcelable("user");
        }

        if (mUser == null) {
            // No data to show yet
            hideLoadedDataLayouts();
            // Request for data
            sendGetUser();
        } else {
            setListAdapter();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("user", mUser);
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
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onViewScorePreview(Score item, View view) {
        Intent intent = new Intent(getActivity(), ScorePreviewActivity.class);
        Bundle b = new Bundle();
        b.putParcelable(ScorePreviewActivity.ARG_SCORE, item);
        intent.putExtras(b);
        getActivity().startActivity(intent);
    }

    @Override
    public void onDeleteScore(final Score item, View view) {
        // Score cannot be deleted from home

    }

    @Override
    public void onDownloadScorePreview(Score item, View view) {
        downloadFile(item.getPreviewLocation(), item, item.getTitle() + "_preview.png");
    }

    @Override
    public void onDownloadScoreProject(Score item, View view) {
        downloadFile(item.getProjectLocation(), item, item.getTitle() + "_project.png");
    }

    @Override
    public void onPutAsFavourite(Score item, View view) {
        sendPutAsFavorite(item);
    }

    @Override
    public void onRemoveFromFavourite(Score item, View view) {
        sendRemoveFromFavorite(item);
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
        if (mUser.getOwnedScores().isEmpty() && mUser.getSubscriptions().isEmpty()) {
            mEmptyList.setVisibility(View.VISIBLE);
            mScoreLayout.setVisibility(View.GONE);
            mSubscriptionLayout.setVisibility(View.GONE);
        } else {
            setScoreList();
            setSubscriptionList();
        }
    }

    public void setScoreList() {
        Collection<Score> scores = mUser.getOwnedScores().values();

        if (!scores.isEmpty()) {
            mScoreLayout.setVisibility(View.VISIBLE);

            if (scores.size() < NB_DISPLAYED_SCORES) {
                mMoreScores.setVisibility(View.GONE);
            } else {
                mMoreScores.setVisibility(View.VISIBLE);
            }

            ScoreListAdapter adapter = new ScoreListAdapter(getActivity(), new ArrayList<>(scores), this);
            adapter.setRemovable(false);
            adapter.setAuthorShown(false);
            for (int i = 0; i < NB_DISPLAYED_SCORES && i < adapter.getItemCount(); i++) {
                ScoreListAdapter.ViewHolder holder = adapter.onCreateViewHolder(mScoreLayout, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                mScoreLayout.addView(holder.itemView, i + 1);
            }
        } else {
            mScoreLayout.setVisibility(View.GONE);
        }
    }

    public void setSubscriptionList() {
        Collection<User> subscriptions = mUser.getSubscriptions().values();

        if (!subscriptions.isEmpty()) {
            mSubscriptionLayout.setVisibility(View.VISIBLE);

            if (subscriptions.size() < NB_DISPLAYED_SUBSCRIPTIONS) {
                mMoreSubscriptions.setVisibility(View.GONE);
            } else {
                mMoreSubscriptions.setVisibility(View.VISIBLE);
            }

            UserListAdapter adapter = new UserListAdapter(getActivity(), new ArrayList<>(subscriptions), this);
            for (int i = 0; i < NB_DISPLAYED_SUBSCRIPTIONS && i < adapter.getItemCount(); i++) {
                UserListAdapter.ViewHolder holder = adapter.onCreateViewHolder(mSubscriptionLayout, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                mSubscriptionLayout.addView(holder.itemView, i + 1);
            }
        } else {
            mSubscriptionLayout.setVisibility(View.GONE);
        }
    }

    public void hideLoadedDataLayouts() {
        mProgressBar.setVisibility(View.VISIBLE);

        mScoreLayout.setVisibility(View.GONE);
        mSubscriptionLayout.setVisibility(View.GONE);
    }

    private void clearLayout(ViewGroup layout) {
        // While it remains items in the layout (excluding header an footer)
        while (layout.getChildCount() > 2) {
            layout.removeViewAt(1);
        }
    }

    private void sendGetUser() {
        new MswApiClient(getActivity(), mLoggingManager.getLoggedUserAccount(), mLoggingManager.getGetAuthTokenCallback(Bundle.class))
                .getAccount(mUserId, new MswApiAsyncConnectionResponseHandler() {

                    @Override
                    public void onFinish() {
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                        User user;
                        try {
                            JSONObject json = mswApiResponse.getJSONObject();
                            user = User.fromJson(json);
                        } catch (JSONException e) {
                            throw new MswApiException(e);
                        }
                        mUser = user;
                        setListAdapter();

                        mNoConnection.setVisibility(View.GONE);
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
                            if (mScoreLayout.getVisibility() != View.VISIBLE &&
                                    mSubscriptionLayout.getVisibility() != View.VISIBLE) {
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

    private void sendPutAsFavorite(final Score score) {
        new MswApiClient(getActivity(), mLoggingManager.getLoggedUserAccount(), mLoggingManager.getGetAuthTokenCallback(Bundle.class))
                .addAccountFavouriteScore(mLoggingManager.getLoggedUserId(), score.getId(), new MswApiAsyncConnectionResponseHandler() {

                    @Override
                    protected void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                        score.setIsFavourite(true);
                    }

                    @Override
                    public void onFailure(MswApiResponseError mswApiResponseError) throws MswApiException {
                        clearLayout(mScoreLayout);
                        setScoreList();
                        if (getView() != null) {
                            Snackbar.make(getView(),
                                    getString(R.string.error_message), Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(MswApiException mswApiException) {
                        clearLayout(mScoreLayout);
                        setScoreList();
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

    private void sendRemoveFromFavorite(final Score score) {
        new MswApiClient(getActivity(), mLoggingManager.getLoggedUserAccount(), mLoggingManager.getGetAuthTokenCallback(Bundle.class))
                .removeAccountFavouriteScore(mLoggingManager.getLoggedUserId(), score.getId(), new MswApiAsyncConnectionResponseHandler() {

                    @Override
                    protected void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                        score.setIsFavourite(false);
                    }

                    @Override
                    public void onFailure(MswApiResponseError mswApiResponseError) throws MswApiException {
                        clearLayout(mScoreLayout);
                        setScoreList();
                        if (getView() != null) {
                            Snackbar.make(getView(),
                                    getString(R.string.error_message), Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(MswApiException mswApiException) {
                        clearLayout(mScoreLayout);
                        setScoreList();
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

    private void sendAddSubscription(final User user) {
        new MswApiClient(getActivity(), mLoggingManager.getLoggedUserAccount(), mLoggingManager.getGetAuthTokenCallback(Bundle.class))
                .addAccountSubscription(mLoggingManager.getLoggedUserId(), user.getId(), new MswApiAsyncConnectionResponseHandler() {

                    @Override
                    protected void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                        user.setIsSubscription(true);
                    }

                    @Override
                    public void onFailure(MswApiResponseError mswApiResponseError) throws MswApiException {
                        clearLayout(mSubscriptionLayout);
                        setSubscriptionList();
                        if (getView() != null) {
                            Snackbar.make(getView(),
                                    getString(R.string.error_message), Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(MswApiException mswApiException) {
                        clearLayout(mSubscriptionLayout);
                        setSubscriptionList();
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
                        clearLayout(mSubscriptionLayout);
                        setSubscriptionList();
                        if (getView() != null) {
                            Snackbar.make(getView(),
                                    getString(R.string.error_message), Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(MswApiException mswApiException) {
                        clearLayout(mSubscriptionLayout);
                        setSubscriptionList();
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

    private void downloadFile(String uri, Score item, String filename) {
        try {
            if (uri != null) {
                Uri Download_Uri = Uri.parse(uri);
                DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
                request.setTitle(item.getTitle());
                request.setDescription(String.format(getString(R.string.download_description), item.getTitle(), item.getAuthor()));
                //Set the local destination for the downloaded file to a path within the application's external files directory
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                //Enqueue a new download
                mDownloadReference = mDownloadManager.enqueue(request);
            } else {
                if (getView() != null) {
                    Snackbar.make(getView(),
                            getString(R.string.download_error), Snackbar.LENGTH_LONG).show();
                }
            }
        } catch (IllegalArgumentException e) {
            if (getView() != null) {
                Snackbar.make(getView(),
                        getString(R.string.download_error), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private class OnDownloadComplete extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (referenceId == mDownloadReference) {
                Toast.makeText(context, R.string.download_complete, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
