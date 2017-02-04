package com.musicsheetwriter.musicsheetwriter.fragmenttab;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.musicsheetwriter.musicsheetwriter.R;
import com.musicsheetwriter.musicsheetwriter.authentication.ILoggingManager;
import com.musicsheetwriter.musicsheetwriter.model.UserPersonalData;
import com.musicsheetwriter.musicsheetwriter.network.MswApiAsyncConnectionResponseHandler;
import com.musicsheetwriter.musicsheetwriter.network.MswApiClient;
import com.musicsheetwriter.musicsheetwriter.network.MswApiException;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponse;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponseError;
import com.musicsheetwriter.musicsheetwriter.network.httputils.HttpException;

import org.json.JSONException;

import java.net.ConnectException;


public class UserAboutFragment extends Fragment {

    public static final String USER_ID = "user_id";

    private ILoggingManager mLoggingManager;
    private int mUserId;

    private View mAboutLayout;
    private View mInfoBox;

    private TextView mDescription;
    private TextView mSubscriberCount;
    private TextView mScoresCount;

    private UserPersonalData mPersonalData;

    private ProgressBar mProgressBar;
    private View mNoConnection;

    public UserAboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getInt(USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab_user_about, container, false);

        mAboutLayout = view.findViewById(R.id.about_layout);
        mInfoBox = view.findViewById(R.id.infobox);

        mDescription = (TextView) view.findViewById(R.id.description);
        mSubscriberCount = (TextView) view.findViewById(R.id.subscribers_count);
        mScoresCount = (TextView) view.findViewById(R.id.scores_count);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        mNoConnection = view.findViewById(R.id.layout_no_connection);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mPersonalData = savedInstanceState.getParcelable("user");
        }
        if (mPersonalData == null) {
            // No data to show yet
            hideLoadedDataLayouts();
            // Request for data
            sendGetUserInfo();
        } else {
            setValues();
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("progress_bar_visibility", mProgressBar.getVisibility());
        outState.putInt("no_connection_visibility", mNoConnection.getVisibility());
        outState.putParcelable("user", mPersonalData);
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

    public void setValues() {
        if (mPersonalData.getMessage() != null && !mPersonalData.getMessage().isEmpty()) {
            mDescription.setText(mPersonalData.getMessage());
        } else {
            mDescription.setText(R.string.no_description);
        }
        mSubscriberCount.setText(String.valueOf(mPersonalData.getNbSubscribers()));
        mScoresCount.setText(String.valueOf(mPersonalData.getNbScores()));
    }

    public void hideLoadedDataLayouts() {
        mProgressBar.setVisibility(View.VISIBLE);

        mNoConnection.setVisibility(View.GONE);
        mInfoBox.setVisibility(View.GONE);
        mAboutLayout.setVisibility(View.GONE);
        mInfoBox.setVisibility(View.GONE);
    }

    public void showLoadedDataLayouts() {
        mNoConnection.setVisibility(View.GONE);

        mInfoBox.setVisibility(View.VISIBLE);
        mAboutLayout.setVisibility(View.VISIBLE);
    }

    private void sendGetUserInfo() {
        new MswApiClient(getActivity(), mLoggingManager.getLoggedUserAccount(), mLoggingManager.getGetAuthTokenCallback(Bundle.class))
                .getAccountPersonalData(mUserId, new MswApiAsyncConnectionResponseHandler() {

                    @Override
                    public void onFinish() {
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                        UserPersonalData pd;
                        try {
                            pd = UserPersonalData.fromJson(mswApiResponse.getJSONObject());
                        } catch (JSONException e) {
                            throw new MswApiException(e);
                        }
                        mPersonalData = pd;
                        setValues();
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
                            mNoConnection.setVisibility(View.VISIBLE);
                        } else {
                            if (getView() != null) {
                                Snackbar.make(getView(),
                                        getString(R.string.error_unknown), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }
}
