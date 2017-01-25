package com.musicsheetwriter.musicsheetwriter.fragmenttab;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.musicsheetwriter.musicsheetwriter.ChangePasswordActivity;
import com.musicsheetwriter.musicsheetwriter.MainActivity;
import com.musicsheetwriter.musicsheetwriter.R;
import com.musicsheetwriter.musicsheetwriter.SubscribersActivity;
import com.musicsheetwriter.musicsheetwriter.UserProfileActivity;
import com.musicsheetwriter.musicsheetwriter.authentication.ILoggingManager;
import com.musicsheetwriter.musicsheetwriter.formvalidator.EmailValidator;
import com.musicsheetwriter.musicsheetwriter.formvalidator.ImageValidator;
import com.musicsheetwriter.musicsheetwriter.model.UserPersonalData;
import com.musicsheetwriter.musicsheetwriter.network.MswApiAsyncConnectionResponseHandler;
import com.musicsheetwriter.musicsheetwriter.network.MswApiClient;
import com.musicsheetwriter.musicsheetwriter.network.MswApiException;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponse;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponseError;
import com.musicsheetwriter.musicsheetwriter.network.httputils.HttpException;
import com.musicsheetwriter.musicsheetwriter.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {

    private static final int PICK_PICTURE_SOURCE = 0;

    private static final int EDIT_PROFILE_PANEL_OPEN = 0;
    private static final int EDIT_PROFILE_PANEL_CLOSE = 1;

    private ILoggingManager mLoggingManager;

    private DownloadManager mDownloadManager;
    private BroadcastReceiver mReceiver;
    private long mDownloadReference;

    private FloatingActionButton mEditFab;
    private View mInfoBox;
    private View mEditLayout;
    private View mPresLayout;
    private View mPasswordLayout;
    private int mEditLayoutState;

    private ImageView mPicture;
    private TextView mUsername;
    private TextView mSubscriberCount;
    private TextView mFirstName;
    private TextView mFamilyName;
    private TextView mEmail;
    private TextView mDescription;

    private EditText mFirstNameEdit;
    private EditText mFamilyNameEdit;
    private EditText mEmailEdit;
    private EditText mDescriptionEdit;
    private Uri mOutputFileUri;

    private Button mCancelEdit;
    private Button mSubmit;
    private Button mPasswordChange;

    private UserPersonalData mPersonalData;

    // MainActivity Viewpager
    private ViewPager mMainPager;
    private FabVisibilitySetter mEditFabVisibilitySetter;

    private ProgressBar mProgressBar;
    private View mNoConnection;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDownloadManager = (DownloadManager) getActivity().getSystemService(Activity.DOWNLOAD_SERVICE);
        //set filter to only when download is complete and register broadcast receiver
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mReceiver = new OnDownloadComplete();
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab_profile, container, false);

        // Edit Panel set up
        mEditFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mInfoBox = view.findViewById(R.id.infobox);
        mEditLayout = view.findViewById(R.id.personal_info_edit);
        mPresLayout = view.findViewById(R.id.personal_info_pres);
        mPasswordLayout = view.findViewById(R.id.password_layout);
        mEditLayoutState = EDIT_PROFILE_PANEL_CLOSE;
        mEditFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditLayoutState(EDIT_PROFILE_PANEL_OPEN, true);
            }
        });

        mPicture = (ImageView) view.findViewById(R.id.picture);
        mPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pickPicture();
                } catch (IOException e) {
                    e.printStackTrace();
                    if (getView() != null) {
                        Snackbar.make(getView(),
                                getString(R.string.error_edit_picture_unable), Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });

        mUsername = (TextView) view.findViewById(R.id.username);
        if (mLoggingManager.getLoggedUserAccount() != null) {
            mUsername.setText(mLoggingManager.getLoggedUserAccount().name);
        }
        mSubscriberCount = (TextView) view.findViewById(R.id.subscribers_count);
        mFirstName = (TextView) view.findViewById(R.id.first_name);
        mFamilyName = (TextView) view.findViewById(R.id.family_name);
        mEmail = (TextView) view.findViewById(R.id.email);
        mDescription = (TextView) view.findViewById(R.id.description);

        mFirstNameEdit = (EditText) view.findViewById(R.id.first_name_edit);
        mFamilyNameEdit = (EditText) view.findViewById(R.id.family_name_edit);
        mEmailEdit = (EditText) view.findViewById(R.id.edit);
        mEmail.addTextChangedListener(new EditProfileTextWatcher());
        mDescriptionEdit = (EditText) view.findViewById(R.id.description_edit);

        mCancelEdit = (Button) view.findViewById(R.id.cancel_edit);
        mCancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditLayoutState(EDIT_PROFILE_PANEL_CLOSE, true);
            }
        });
        mSubmit = (Button) view.findViewById(R.id.submit_edit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Force hiding keyboard
                if (getActivity().getCurrentFocus() != null) {
                    InputMethodManager inputManager = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                // Send request
                sendEditProfile();
            }
        });
        mPasswordChange = (Button) view.findViewById(R.id.change_password);
        mPasswordChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                Bundle b = new Bundle();
                b.putParcelable(ChangePasswordActivity.ARG_ACCOUNT, mLoggingManager.getLoggedUserAccount());
                b.putInt(ChangePasswordActivity.ARG_USER_ID, mLoggingManager.getLoggedUserId());
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        mMainPager = (ViewPager) getActivity().findViewById(R.id.pager);
        mEditFabVisibilitySetter = new FabVisibilitySetter();
        mMainPager.addOnPageChangeListener(mEditFabVisibilitySetter);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        mNoConnection = view.findViewById(R.id.layout_no_connection);


        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SubscribersActivity.class);
                Bundle b = new Bundle();
                b.putInt(SubscribersActivity.ARG_USER_ID, mPersonalData.getId());
                intent.putExtras(b);
                getActivity().startActivity(intent);
            }
        };
        view.findViewById(R.id.infobox).setOnClickListener(clickListener);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mPersonalData = savedInstanceState.getParcelable("user");
        }
        Picasso.with(getActivity())
                .load(R.drawable.default_avatar)
                .noFade()
                .fit()
                .centerCrop()
                .transform(new CircleTransform())
                .into(mPicture);
        if (mPersonalData == null) {
            // No data to show yet
            hideLoadedDataLayouts();
            // Request for data
            sendGetUserInfo();
        } else {
            setValues();
            mEditFab.show();
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
        outState.putInt("edit_layout_state", mEditLayoutState);
        outState.putInt("progress_bar_visibility", mProgressBar.getVisibility());
        outState.putInt("no_connection_visibility", mNoConnection.getVisibility());
        outState.putParcelable("user", mPersonalData);
    }

    @Override
    @SuppressWarnings("ResourceType")
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getInt("edit_layout_state") == EDIT_PROFILE_PANEL_OPEN) {
                setEditLayoutState(EDIT_PROFILE_PANEL_OPEN, false);
            }
            mProgressBar.setVisibility(savedInstanceState.getInt("progress_bar_visibility", View.GONE));
            mNoConnection.setVisibility(savedInstanceState.getInt("no_connection_visibility", View.GONE));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
        mMainPager.removeOnPageChangeListener(mEditFabVisibilitySetter);
    }

    public void setEditLayoutState(int state, boolean animated) {
        setEditLayoutViewEnabled(state == EDIT_PROFILE_PANEL_OPEN);
        setEditLayoutVisible(state == EDIT_PROFILE_PANEL_OPEN, animated);
        mEditLayoutState = state;
    }

    public void setValues() {
        Picasso.with(getActivity())
                .load(mPersonalData.getPhoto())
                .noPlaceholder()
                .fit()
                .centerCrop()
                .transform(new CircleTransform())
                .into(mPicture);
        setTextValues();
        setEditPanelValues();
    }

    public void setTextValues() {
        mFirstName.setText(mPersonalData.getFirstName());
        mFamilyName.setText(mPersonalData.getSurname());
        mEmail.setText(mPersonalData.getEmail());
        mDescription.setText(mPersonalData.getMessage());
        // TODO Replace by the value in Personal Data
        mSubscriberCount.setText(String.valueOf(mPersonalData.getNbSubscribers()));
    }

    public void setEditPanelValues() {
        mFirstNameEdit.setText(mPersonalData.getFirstName());
        mFamilyNameEdit.setText(mPersonalData.getSurname());
        mEmailEdit.setText(mPersonalData.getEmail());
        mDescriptionEdit.setText(mPersonalData.getMessage());
    }

    public void hideLoadedDataLayouts() {
        mProgressBar.setVisibility(View.VISIBLE);

        mNoConnection.setVisibility(View.GONE);
        mInfoBox.setVisibility(View.GONE);
        mPresLayout.setVisibility(View.GONE);
        mPasswordLayout.setVisibility(View.GONE);
        mEditFab.hide();
    }

    public void showLoadedDataLayouts() {
        mNoConnection.setVisibility(View.GONE);

        mInfoBox.setVisibility(View.VISIBLE);
        mPresLayout.setVisibility(View.VISIBLE);
        mPasswordLayout.setVisibility(View.VISIBLE);
        setEditLayoutState(mEditLayoutState, false);
    }

    public void pickPicture() throws IOException {
        // Determine Uri of camera image to save.
        final File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "MusicSheetWriter" + File.separator);
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();
        final String fname = "msw-profile-picture.jpg";
        final File sdImageMainDirectory = new File(dir, fname);
        mOutputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.select_picture_source));

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        // Launch the picker
        startActivityForResult(chooserIntent, PICK_PICTURE_SOURCE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_PICTURE_SOURCE) {
                decodePicture(data);
            }
        }
    }

    private void sendGetUserInfo() {
        new MswApiClient(getActivity(), mLoggingManager.getLoggedUserAccount(), mLoggingManager.getGetAuthTokenCallback(Bundle.class))
                .getAccountPersonalData(mLoggingManager.getLoggedUserId(),
                        new MswApiAsyncConnectionResponseHandler() {

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

    private void sendEditProfile() {
        if (checkEmail(mEmailEdit, true)) {

            String firstname = mFirstNameEdit.getText().toString();
            String surname = mFamilyNameEdit.getText().toString();
            String email = mEmailEdit.getText().toString();
            String description = mDescriptionEdit.getText().toString();

            new MswApiClient(getActivity(), mLoggingManager.getLoggedUserAccount(), mLoggingManager.getGetAuthTokenCallback(Bundle.class))
                    .editAccountPersonalData(mLoggingManager.getLoggedUserId(), firstname, surname,
                            email, description, new MswApiAsyncConnectionResponseHandler() {

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
                                    setEditLayoutState(EDIT_PROFILE_PANEL_CLOSE, true);
                                    Toast.makeText(getActivity(), R.string.edit_profile_success,
                                            Toast.LENGTH_LONG).show();
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
    }

    private void sendEditPicture(String path) {
        if (path == null) {
            return;
        }

        InputStream is;
        try {
            // Try to upload the picture not contained in the content resolver
            is = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            try {
                is = getActivity().getContentResolver().openInputStream(Uri.parse(path));
            } catch (FileNotFoundException e1) {
                // The file is not found
                e1.printStackTrace();
                if (getView() != null) {
                    Snackbar.make(getView(),
                            getString(R.string.error_no_network), Snackbar.LENGTH_LONG).show();
                }
                return;
            }
        }

        new MswApiClient(getActivity(), mLoggingManager.getLoggedUserAccount(), mLoggingManager.getGetAuthTokenCallback(Bundle.class))
                .editAccountPicture(mLoggingManager.getLoggedUserId(), is, "image/png", new MswApiAsyncConnectionResponseHandler() {

                    @Override
                    public void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                        try {
                            mPersonalData.setPhoto(mswApiResponse.getJSONObject().getString("filename"));
                        } catch (JSONException e) {
                            throw new MswApiException(e);
                        }
                        Picasso.with(getActivity())
                                .load(mPersonalData.getPhoto())
                                .noPlaceholder()
                                .fit()
                                .centerCrop()
                                .transform(new CircleTransform())
                                .into(mPicture);
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

    private void setEditLayoutViewEnabled(boolean enabled) {
        mSubmit.setEnabled(enabled);
        mCancelEdit.setEnabled(enabled);
    }

    private void setEditLayoutVisible(boolean visible, boolean animated) {
        // get the center for the clipping circle
        int cx = mEditLayout.getWidth() -
                getResources().getDimensionPixelSize(R.dimen.fab_margin) -
                mEditFab.getWidth() / 2;
        int cy = 0;

        if (visible) {
            if (animated &&
                    android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                // get the final radius for the clipping circle
                float finalRadius = (float) Math.hypot(cx * 2, cy * 2);

                // create the animator for this view (the start radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(mEditLayout, cx, cy, 0, finalRadius);

                // make the view visible and start the animation
                mEditLayout.setVisibility(View.VISIBLE);
                anim.start();

                // set fab visibility
                mEditFab.hide();
            } else {
                // only make the view visible
                mEditLayout.setVisibility(View.VISIBLE);
                mEditFab.hide();
            }
        } else {
            if (animated &&
                    android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                // get the initial radius for the clipping circle
                float initialRadius = (float) Math.hypot(cx * 2, cy * 2);

                // create the animation (the final radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(mEditLayout, cx, cy, initialRadius, 0);
                // make the view invisible when the animation is done
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);

                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mEditLayout.setVisibility(View.INVISIBLE);
                        // set fab visibility
                        mEditFab.show();
                    }
                });
                // start the animation
                anim.start();
            } else {
                // only make the view invisible
                mEditLayout.setVisibility(View.INVISIBLE);
                mEditFab.show();
            }
        }
    }

    private void decodePicture(Intent data) {
        final boolean isCamera;
        if (data == null) {
            isCamera = true;
        } else {
            final String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }

        Uri selectedImageUri;
        if (isCamera) {
            selectedImageUri = mOutputFileUri;
        } else {
            selectedImageUri = data.getData();
        }

        final String filename = "msw-profile-picture.jpg";
        final String newPath = new ImageValidator().transformAndStore(getActivity(), selectedImageUri, filename);

        if (newPath != null
                && (newPath.startsWith("http://")
                || newPath.startsWith("https://"))) {

            // Download Picture
            Uri Download_Uri = Uri.parse(newPath);
            DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
            //Set the local destination for the downloaded file to a path within the application's external files directory
            request.setDestinationInExternalFilesDir(getActivity(), Environment.DIRECTORY_DOWNLOADS, filename);
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

            //Enqueue a new download
            mDownloadReference = mDownloadManager.enqueue(request);

        } else {
            if (newPath != null) {
                sendEditPicture(newPath);
            } else {
                sendEditPicture(selectedImageUri.getPath());
            }
        }
    }

    private boolean checkEmail(EditText field, boolean setError) {
        String fieldValue = field.getText().toString();
        if (!new EmailValidator().validate(fieldValue)) {
            if (setError) {
                field.setError(getString(R.string.error_email_format));
                field.requestFocus();
            }
            return false;
        }
        return true;
    }

    private class FabVisibilitySetter extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mPersonalData != null && mEditLayoutState == EDIT_PROFILE_PANEL_CLOSE) {
                switch (mMainPager.getCurrentItem()) {
                    case MainActivity.TAB_MY_PROFILE: // MyProfileFragment
                        mEditFab.show();
                        break;
                    default:
                        mEditFab.hide();
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mPersonalData != null && mEditLayoutState == EDIT_PROFILE_PANEL_CLOSE) {
                switch (position) {
                    case MainActivity.TAB_MY_PROFILE: // MyProfileFragment
                        mEditFab.show();
                        break;
                    default:
                        mEditFab.hide();
                }
            }
        }
    }

    private class EditProfileTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            if (checkEmail(mEmailEdit, false)) {
                mSubmit.setEnabled(true);
            } else {
                mSubmit.setEnabled(false);
            }
        }
    }

    private class OnDownloadComplete extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (referenceId == mDownloadReference) {
                Cursor q = mDownloadManager.query(new DownloadManager.Query().setFilterById(referenceId));
                if (q != null) {
                    q.moveToFirst();
                    String newPath = q.getString(q.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));

                    if (newPath != null) {
                        sendEditPicture(newPath);
                    }
                    return;
                }
                if (getView() != null) {
                    Snackbar.make(getView(),
                            getString(R.string.error_edit_picture_unable), Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }
}
