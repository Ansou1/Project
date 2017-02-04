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
import android.widget.Toast;

import com.musicsheetwriter.musicsheetwriter.R;
import com.musicsheetwriter.musicsheetwriter.ScorePreviewActivity;
import com.musicsheetwriter.musicsheetwriter.authentication.ILoggingManager;
import com.musicsheetwriter.musicsheetwriter.listadapter.DividerItemDecoration;
import com.musicsheetwriter.musicsheetwriter.listadapter.OnScoreInteractionListener;
import com.musicsheetwriter.musicsheetwriter.listadapter.ScoreListAdapter;
import com.musicsheetwriter.musicsheetwriter.model.Score;
import com.musicsheetwriter.musicsheetwriter.network.MswApiAsyncConnectionResponseHandler;
import com.musicsheetwriter.musicsheetwriter.network.MswApiClient;
import com.musicsheetwriter.musicsheetwriter.network.MswApiException;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponse;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponseError;
import com.musicsheetwriter.musicsheetwriter.network.httputils.HttpException;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.ConnectException;
import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnScoreInteractionListener}
 * interface.
 */
public class SearchScoresFragment extends Fragment implements OnScoreInteractionListener {

    public static final String QUERY = "query";

    private RecyclerView mList;
    private SwipeRefreshLayout mSwipeRefresh;
    private ArrayList<Score> mScores;

    private DownloadManager mDownloadManager;
    private BroadcastReceiver mReceiver;
    private long mDownloadReference;

    private ILoggingManager mLoggingManager;
    private String mQuery;

    private ProgressBar mProgressBar;
    private View mNoConnection;
    private View mEmptyList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchScoresFragment() {
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
            mQuery = getArguments().getString(QUERY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_search_scores, container, false);

        mList = (RecyclerView) view.findViewById(R.id.list);
        mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setOnRefreshListener(new OnScoreListRefresh());

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        mNoConnection = view.findViewById(R.id.layout_no_connection);
        mEmptyList = view.findViewById(R.id.layout_empty_list);

        mList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mList.setItemAnimator(new DefaultItemAnimator());

        mList.setLayoutManager(new LinearLayoutManager(view.getContext()));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mScores = savedInstanceState.getParcelableArrayList("score_list");
        }

        if (mScores == null) {
            // No data to show yet
            hideLoadedDataLayouts();
            // Request for data
            sendGetScores();
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
        outState.putParcelableArrayList("score_list", mScores);
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
    public void onDeleteScore(Score item, View view) {
        // Favourite score cannot be deleted
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

    public void setListAdapter() {
        if (getView() != null) {
            if (!mScores.isEmpty()) {
                mEmptyList.setVisibility(View.GONE);
                mList.setVisibility(View.VISIBLE);
                mSwipeRefresh.setVisibility(View.VISIBLE);

                ScoreListAdapter adapter = new ScoreListAdapter(getContext(), mScores, this);
                adapter.setRemovable(false);
                adapter.setAuthorShown(true);

                mList.setAdapter(adapter);
            } else {
                mEmptyList.setVisibility(View.VISIBLE);
                mSwipeRefresh.setVisibility(View.GONE);
                mList.setVisibility(View.GONE);
            }
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

    private void sendGetScores() {
        new MswApiClient(getActivity(), mLoggingManager.getLoggedUserAccount(), mLoggingManager.getGetAuthTokenCallback(Bundle.class))
                .getScoreList(mQuery, new MswApiAsyncConnectionResponseHandler() {

                    @Override
                    public void onFinish() {
                        mProgressBar.setVisibility(View.GONE);
                        mSwipeRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                        ArrayList<Score> scores;
                        try {
                            JSONArray json = mswApiResponse.getJSONArray();
                            scores = new ArrayList<>(json.length());

                            // Set owned scores
                            for (int i = 0; i < json.length(); i++) {
                                Score newScore = Score.fromJson(json.getJSONObject(i));
                                scores.add(newScore);
                            }
                        } catch (JSONException e) {
                            throw new MswApiException(e);
                        }
                        mScores = scores;
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

    private void sendPutAsFavorite(final Score score) {
        new MswApiClient(getActivity(), mLoggingManager.getLoggedUserAccount(), mLoggingManager.getGetAuthTokenCallback(Bundle.class))
                .addAccountFavouriteScore(mLoggingManager.getLoggedUserId(), score.getId(), new MswApiAsyncConnectionResponseHandler() {

                    @Override
                    protected void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                        score.setIsFavourite(true);
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

    private void sendRemoveFromFavorite(final Score score) {
        new MswApiClient(getActivity(), mLoggingManager.getLoggedUserAccount(), mLoggingManager.getGetAuthTokenCallback(Bundle.class))
                .removeAccountFavouriteScore(mLoggingManager.getLoggedUserId(), score.getId(), new MswApiAsyncConnectionResponseHandler() {

                    @Override
                    protected void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                        score.setIsFavourite(false);
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

    private void refreshScoreList() {
        sendGetScores();
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

    private class OnScoreListRefresh implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            refreshScoreList();
        }
    }
}
