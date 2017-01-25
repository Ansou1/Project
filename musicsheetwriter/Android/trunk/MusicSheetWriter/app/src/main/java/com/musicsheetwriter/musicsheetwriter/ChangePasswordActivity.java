package com.musicsheetwriter.musicsheetwriter;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.musicsheetwriter.musicsheetwriter.formvalidator.PasswordValidator;
import com.musicsheetwriter.musicsheetwriter.network.MswApiAsyncConnectionResponseHandler;
import com.musicsheetwriter.musicsheetwriter.network.MswApiClient;
import com.musicsheetwriter.musicsheetwriter.network.MswApiException;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponse;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponseError;
import com.musicsheetwriter.musicsheetwriter.network.httputils.HttpException;

import java.net.ConnectException;

public class ChangePasswordActivity extends AppCompatActivity {

    public static final String ARG_ACCOUNT = "account";
    public static final String ARG_USER_ID = "user_id";

    private View mChangePasswordLayout;
    private EditText mCurrentPassword;
    private EditText mNewPassword;
    private Button mSubmit;
    private ContentLoadingProgressBar mProgressBar;

    private Account mAccount;
    private int mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mChangePasswordLayout = findViewById(R.id.change_password_layout);
        mCurrentPassword = (EditText) findViewById(R.id.current_password);
        mNewPassword = (EditText) findViewById(R.id.new_password);
        mSubmit = (Button) findViewById(R.id.button);
        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progressbar);

        mCurrentPassword.addTextChangedListener(new PasswordTextWatcher());
        mNewPassword.addTextChangedListener(new PasswordTextWatcher());
        
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Force hiding keyboard
                if (getCurrentFocus() != null) {
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                // Send request
                sendChangePasswordRequest();
            }
        });

        mAccount = getIntent().getParcelableExtra(ARG_ACCOUNT);
        mUserId = getIntent().getIntExtra(ARG_USER_ID, -1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendChangePasswordRequest() {
        if (checkPassword(mCurrentPassword, true) && checkPassword(mNewPassword, true)) {

            final String currentPassword = mCurrentPassword.getText().toString();
            final String newPassword = mNewPassword.getText().toString();

            new MswApiClient(ChangePasswordActivity.this, mAccount, null).changeAccountPassword(mUserId,
                    currentPassword, newPassword, new MswApiAsyncConnectionResponseHandler() {

                        @Override
                        protected void onStart() {
                            mProgressBar.setVisibility(View.VISIBLE);
                            mChangePasswordLayout.setVisibility(View.GONE);
                        }

                        @Override
                        protected void onFinish() {
                            mProgressBar.setVisibility(View.GONE);
                            mChangePasswordLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        protected void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                            Toast.makeText(ChangePasswordActivity.this, R.string.success_change_password,
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }

                        @Override
                        protected void onFailure(MswApiResponseError mswApiResponseError) throws MswApiException {
                            handleApiError(mswApiResponseError);
                        }

                        @Override
                        protected void onFailure(MswApiException mswApiException) {
                            handleApiException(mswApiException);
                        }
                    });
        }
    }

    private boolean checkPassword(EditText field, boolean setError) {
        String fieldValue = field.getText().toString();
        if (!new PasswordValidator().validate(fieldValue)) {
            if (setError) {
                field.setError(getString(R.string.error_password_format));
                field.requestFocus();
            }
            return false;
        }
        return true;
    }


    private void handleApiException(MswApiException e) {
        if (e.getCause() instanceof HttpException &&
                e.getCause().getCause() instanceof ConnectException) {
            Snackbar.make(findViewById(R.id.coordinator_layout),
                    getString(R.string.error_no_network),
                    Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(findViewById(R.id.coordinator_layout),
                    getString(R.string.error_unknown), Snackbar.LENGTH_LONG).show();
        }
    }

    private void handleApiError(MswApiResponseError error) {
        String errorCode = error.getApiErrorCode();

        // Invalid field error
        switch (errorCode) {
            case "USR-WRONGPASS":
                mCurrentPassword.setError(getString(R.string.error_wrong_password));
                mCurrentPassword.requestFocus();
                mCurrentPassword.setSelection(mCurrentPassword.getText().length());
                // Unknown Error
            default:
                Snackbar.make(findViewById(R.id.coordinator_layout),
                        getString(R.string.error_unknown), Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    private class PasswordTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mCurrentPassword.getText().length() != 0
                    && checkPassword(mCurrentPassword, false)
                    && mNewPassword.getText().length() != 0
                    && checkPassword(mNewPassword, false)) {
                mSubmit.setEnabled(true);
            } else {
                mSubmit.setEnabled(false);
            }
        }
    }
}
