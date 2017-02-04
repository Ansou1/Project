package com.musicsheetwriter.musicsheetwriter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.musicsheetwriter.musicsheetwriter.authentication.ILoggingManager;
import com.musicsheetwriter.musicsheetwriter.listadapter.OnScoreInteractionListener;
import com.musicsheetwriter.musicsheetwriter.model.Score;
import com.musicsheetwriter.musicsheetwriter.network.MswApiAsyncConnectionResponseHandler;
import com.musicsheetwriter.musicsheetwriter.network.MswApiClient;
import com.musicsheetwriter.musicsheetwriter.network.MswApiException;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponse;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponseError;
import com.musicsheetwriter.musicsheetwriter.network.httputils.HttpException;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.net.ConnectException;

public class ScorePreviewActivity extends AppCompatActivity implements ILoggingManager,
        OnScoreInteractionListener  {

    public static final String ARG_SCORE = "score";

    private int mLoggedUserId;
    private Account mLoggedUserAccount;

    private DownloadManager mDownloadManager;
    private BroadcastReceiver mReceiver;
    private long mDownloadReference;

    private Score mScore;

    private SubsamplingScaleImageView mPreviewImageView;
    private ImageViewState mImageViewState;
    private FloatingActionsMenu mFab;

    private ProgressBar mProgressBar;
    private View mNoConnection;

    private OnLoadPreviewBitmap mTarget;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_preview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mScore = getIntent().getParcelableExtra(ARG_SCORE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(mScore.getTitle());
            getSupportActionBar().setSubtitle(mScore.getAuthor().getUsername());
        }

        mDownloadManager = (DownloadManager) getSystemService(Activity.DOWNLOAD_SERVICE);
        //set filter to only when download is complete and register broadcast receiver
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mReceiver = new OnDownloadComplete();
        registerReceiver(mReceiver, filter);

        mFab = (FloatingActionsMenu) findViewById(R.id.fam);
        mFab.setVisibility(View.GONE);
        FloatingActionButton fabPreview = (FloatingActionButton) mFab.findViewById(R.id.fab_image);
        fabPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDownloadScorePreview(mScore, v);
            }
        });
        FloatingActionButton fabProject = (FloatingActionButton) mFab.findViewById(R.id.fab_project);
        fabProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDownloadScoreProject(mScore, v);
            }
        });

        loadConnectedUser();

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.VISIBLE);

        mNoConnection = findViewById(R.id.layout_no_connection);

        mPreviewImageView = (SubsamplingScaleImageView) findViewById(R.id.score);
        mPreviewImageView.setVisibility(View.GONE);


        if (savedInstanceState != null) {
            mImageViewState = savedInstanceState.getParcelable("score_preview_state");
        } else {
            mImageViewState = null;
        }

        // Load Preview
        mTarget = new OnLoadPreviewBitmap();
        Picasso.with(this).load(mScore.getPreviewLocation()).into(mTarget);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuId;
        if (loadConnectedUser()) {
            // if the user is connected, the menu with the logout option is inflated
            menuId = R.menu.menu_score;
        } else {
            // otherwise, the menu with the login option is inflated
            menuId = R.menu.menu_score_no_connection;
        }
        getMenuInflater().inflate(menuId, menu);

        // Set the favourite checkbox
        MenuItem item = menu.findItem(R.id.action_favorite);
        item.setChecked(mScore.isFavourite());
        if (item.isChecked()) {
            item.setIcon(R.drawable.toolbar_favorite_checked_white);
            item.setTitle(R.string.action_rem_favorite);
        } else {
            item.setIcon(R.drawable.toolbar_favorite_white);
            item.setTitle(R.string.action_put_favorite);
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
            case R.id.action_favorite:
                item.setChecked(!item.isChecked());

                if (item.isChecked()) {
                    onPutAsFavourite(mScore, item.getActionView());
                    item.setIcon(R.drawable.toolbar_favorite_checked_white);
                    item.setTitle(R.string.action_rem_favorite);
                } else {
                    onRemoveFromFavourite(mScore, item.getActionView());
                    item.setIcon(R.drawable.toolbar_favorite_white);
                    item.setTitle(R.string.action_put_favorite);
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
    public void onSaveInstanceState(Bundle outState) {
        SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) findViewById(R.id.score);
        ImageViewState state = imageView.getState();
        if (state != null) {
            outState.putSerializable("score_preview_state", imageView.getState());
        }
        outState.putInt("progress_bar_visibility", mProgressBar.getVisibility());
        outState.putInt("no_connection_visibility", mNoConnection.getVisibility());
        outState.putInt("preview_image_visibility", mPreviewImageView.getVisibility());
    }

    @Override
    @SuppressWarnings("ResourceType")
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mProgressBar.setVisibility(savedInstanceState.getInt("progress_bar_visibility", View.GONE));
            mNoConnection.setVisibility(savedInstanceState.getInt("no_connection_visibility", View.GONE));
            mPreviewImageView.setVisibility(savedInstanceState.getInt("preview_image_visibility"));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean requestConnectedUser() {
        AccountManager am = AccountManager.get(ScorePreviewActivity.this);
        Account[] accounts = am.getAccountsByType(getString(R.string.am_account_type));
        if (accounts.length == 0) {
            // No account are stored
            Bundle b = new Bundle();
            b.putBoolean(LoginActivity.ARG_SHOW_SLASH, false);
            am.addAccount(getString(R.string.am_account_type), getString(R.string.am_auth_token_type),
                    null, b, ScorePreviewActivity.this, getAddAccountCallback(Bundle.class), null);
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
        AccountManager am = AccountManager.get(ScorePreviewActivity.this);
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
                am.removeAccount(ac, ScorePreviewActivity.this, getRemoveAccountCallback(Bundle.class), null);
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
                        Intent i = new Intent(ScorePreviewActivity.this, MainActivity.class);
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
                        Intent i = new Intent(ScorePreviewActivity.this, MainActivity.class);
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
                        AccountManager am = AccountManager.get(ScorePreviewActivity.this);
                        am.setAuthToken(getLoggedUserAccount(),
                                getString(R.string.am_auth_token_type),
                                getString(R.string.am_auth_token_none));
                    }
                }
            }
        };
    }

    @Override
    public void onViewScorePreview(Score item, View view) {
        Intent intent = new Intent(ScorePreviewActivity.this, ScorePreviewActivity.class);
        Bundle b = new Bundle();
        b.putParcelable(ScorePreviewActivity.ARG_SCORE, item); //Your id
        intent.putExtras(b); //Put your id to your next Intent
        ScorePreviewActivity.this.startActivity(intent);
    }

    @Override
    public void onDeleteScore(final Score item, View view) {
        // Score cannot be deleted from preview screen
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

    private void sendPutAsFavorite(final Score score) {
        new MswApiClient(ScorePreviewActivity.this, getLoggedUserAccount(), getGetAuthTokenCallback(Bundle.class))
                .addAccountFavouriteScore(getLoggedUserId(), score.getId(), new MswApiAsyncConnectionResponseHandler() {

                    @Override
                    protected void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                        score.setIsFavourite(true);
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

    private void sendRemoveFromFavorite(final Score score) {
        new MswApiClient(ScorePreviewActivity.this, getLoggedUserAccount(), getGetAuthTokenCallback(Bundle.class))
                .removeAccountFavouriteScore(getLoggedUserId(), score.getId(), new MswApiAsyncConnectionResponseHandler() {

                    @Override
                    protected void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                        score.setIsFavourite(false);
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

    private void sendLogoutRequest() {
        new MswApiClient().logout(new MswApiAsyncConnectionResponseHandler() {

            private ProgressDialog loader;

            @Override
            public void onStart() {
                loader = new ProgressDialog(ScorePreviewActivity.this);
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
                Snackbar.make(findViewById(R.id.coordinator_layout),
                        getString(R.string.error_unknown), Snackbar.LENGTH_LONG).show();
            }
        } catch (IllegalArgumentException e) {
            Snackbar.make(findViewById(R.id.coordinator_layout),
                    getString(R.string.error_unknown), Snackbar.LENGTH_LONG).show();
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

    private class OnLoadPreviewBitmap implements Target {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mProgressBar.setVisibility(View.GONE);
            mPreviewImageView.setVisibility(View.VISIBLE);
            mFab.setVisibility(View.VISIBLE);

            if (mImageViewState != null) {
                mPreviewImageView.setImage(ImageSource.bitmap(bitmap), mImageViewState);
            } else {
                mPreviewImageView.setImage(ImageSource.bitmap(bitmap));
            }
            mPreviewImageView.setZoomEnabled(true);
            mPreviewImageView.setDoubleTapZoomScale(4);
            mPreviewImageView.setPanEnabled(true);
            mPreviewImageView.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_INSIDE);
            mPreviewImageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mFab.isExpanded()) {
                        mFab.collapse();
                    }

                    return mPreviewImageView.onTouchEvent(event);
                }
            });
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            mProgressBar.setVisibility(View.GONE);

            Snackbar.make(findViewById(R.id.coordinator_layout),
                    getString(R.string.error_load_preview_score), Snackbar.LENGTH_LONG).show();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }

}
