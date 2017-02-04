package com.musicsheetwriter.musicsheetwriter;

import android.app.AlertDialog;
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

import com.musicsheetwriter.musicsheetwriter.formvalidator.EmailValidator;
import com.musicsheetwriter.musicsheetwriter.network.MswApiAsyncConnectionResponseHandler;
import com.musicsheetwriter.musicsheetwriter.network.MswApiClient;
import com.musicsheetwriter.musicsheetwriter.network.MswApiException;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponse;
import com.musicsheetwriter.musicsheetwriter.network.MswApiResponseError;
import com.musicsheetwriter.musicsheetwriter.network.httputils.HttpException;

import java.net.ConnectException;

public class ForgottenPasswordActivity extends AppCompatActivity {

    private View mForgottenPasswordLayout;
    private EditText mEmail;
    private Button mSubmit;
    private ContentLoadingProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mForgottenPasswordLayout = findViewById(R.id.forgotten_password_layout);
        mEmail = (EditText) findViewById(R.id.edit);
        mSubmit = (Button) findViewById(R.id.button);
        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progressbar);

        mEmail.addTextChangedListener(new EmailTextWatcher());

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
                sendForgottenPasswordRequest();
            }
        });
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

    private void sendForgottenPasswordRequest() {
        if (checkEmail(mEmail, true)) {

            final String emailValue = mEmail.getText().toString();

            new MswApiClient()
                    .forgottenPassword(emailValue, new MswApiAsyncConnectionResponseHandler() {
                        @Override
                        protected void onStart() {
                            mProgressBar.setVisibility(View.VISIBLE);
                            mForgottenPasswordLayout.setVisibility(View.GONE);
                        }

                        @Override
                        protected void onFinish() {
                            mProgressBar.setVisibility(View.GONE);
                            mForgottenPasswordLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        protected void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {
                            new AlertDialog.Builder(ForgottenPasswordActivity.this, R.attr.alertDialogTheme)
                                    .setTitle(R.string.success_forgotten_password_title)
                                    .setMessage(R.string.success_forgotten_password_message)
                                    .show();
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
            case "FOR-EMAILNOTFOUND":
                mEmail.setError(getString(R.string.error_no_such_email));
                mEmail.requestFocus();
                mEmail.setSelection(mEmail.getText().length());
            // Unknown Error
            default:
                Snackbar.make(findViewById(R.id.coordinator_layout),
                        getString(R.string.error_unknown), Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    private class EmailTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            if (mEmail.getText().length() != 0
                    && checkEmail(mEmail, false)) {
                mSubmit.setEnabled(true);
            } else {
                mSubmit.setEnabled(false);
            }
        }
    }

}
