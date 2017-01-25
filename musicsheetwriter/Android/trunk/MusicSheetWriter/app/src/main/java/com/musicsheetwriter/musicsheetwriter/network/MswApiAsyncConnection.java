package com.musicsheetwriter.musicsheetwriter.network;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.musicsheetwriter.musicsheetwriter.R;
import com.musicsheetwriter.musicsheetwriter.network.httputils.HttpRequest;
import com.musicsheetwriter.musicsheetwriter.network.httputils.HttpResponse;
import com.musicsheetwriter.musicsheetwriter.network.httputils.HttpRestAsyncConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class MswApiAsyncConnection extends HttpRestAsyncConnection {

    /**
     * The tag used for logging
     */
    public static final String TAG = "MswApiAsyncConnection";

    /**
     * The response handler used by this connection instance
     */
    private MswApiAsyncConnectionResponseHandler mResponseHandler;

    /**
     * Activity used to access the AccountManager to relog the user if needed
     */
    private Activity mActivity;

    /**
     * The account attempting sending the request
     */
    private Account mAccount;

    /**
     * The callback used after getting authToken
     */
    private AccountManagerCallback<Bundle> mCallback;

    /**
     * Construct a new connection to the API to send a request which requires an authentication
     * @param activity The calling activity
     * @param account The account making the request
     * @param callback The callback used for re-authentication
     * @param responseHandler The callbacks of the request
     */
    public MswApiAsyncConnection(@Nullable Activity activity, @Nullable Account account,
                                 AccountManagerCallback<Bundle> callback,
                                 MswApiAsyncConnectionResponseHandler responseHandler) {
        super(activity);
        mActivity = activity;
        mAccount = account;
        mCallback = callback;
        mResponseHandler = responseHandler;
        if (responseHandler != null) {
            responseHandler.setConnection(this);
        }
    }

    protected void onPreExecute() {
        super.onPreExecute();
        if (mResponseHandler == null) {
            Log.w(TAG, "No response handler is defined for the connection {" + toString() + "}");
        }
        onStart();
    }

    @Override
    protected HttpResponse doInBackground(HttpRequest... requests) {
        HttpResponse response;

        try {
            if (requests == null || requests[0] == null) {
                Log.e(TAG, "The request is null");
                throw new IOException("The request to send is null");
            }

            // If this call requires an authentication...
            if (mAccount != null && mActivity != null) {
                Log.i(TAG, "The request will be sent with a user authentication [name=" +
                        mAccount.name + "]");

                // ...get the account manager instance
                final AccountManager am = AccountManager.get(mActivity);

                // Request for the authToken
                AccountManagerFuture<Bundle> getAuthTokenFuture = am.getAuthToken(mAccount,
                        mActivity.getString(R.string.am_auth_token_type), null, mActivity, mCallback, null);
                // Get the authToken
                String authToken = getAuthTokenFuture.getResult().getString(AccountManager.KEY_AUTHTOKEN);

                // Set cookie in the request
                requests[0].setProperty("Cookie", "PHPSESSID="+authToken);

                // Execute request
                response = super.doInBackground(requests);

                // If the authentication token has expired
                if (response != null && response.isError()) {
                    // Get the MswApiResponse to get the error code
                    MswApiResponse mswApiresponse = parseHttpResponse(response);

                    // If the request has failed because of failed authentication...
                    if (((MswApiResponseError) mswApiresponse).getApiErrorCode().equals("GLO-UNAUTHORIZED")) {
                        Log.i(TAG, "The authentication token is not valid anymore [name=" +
                                mAccount.name + " token=" + authToken + "]");

                        // ...renew the authToken
                        // Invalidate token
                        am.invalidateAuthToken(mActivity.getString(R.string.am_account_type), authToken);
                        // Get the ID of the user
                        String userData = am.getUserData(mAccount, mActivity.getString(R.string.am_user_data_user_id));
                        Bundle options = new Bundle();
                        options.putString(AccountManager.KEY_USERDATA, userData);

                        // Request for a new authToken
                        String tokenType = mActivity.getString(R.string.am_auth_token_type);
                        Log.v(TAG, "Request for a new authentication token [name=" +
                                mAccount.name + " tokenType=" + tokenType + "]");
                        getAuthTokenFuture = am.getAuthToken(mAccount, tokenType, options, mActivity, mCallback, null);

                        // Get the new authToken
                        authToken = getAuthTokenFuture.getResult().getString(AccountManager.KEY_AUTHTOKEN);

                        Log.v(TAG, "The new token is now [name=" + mAccount.name + " token=" +
                                authToken + "]");

                        // Set cookie in the request
                        requests[0].setProperty("Cookie", "PHPSESSID=" + authToken);

                        // Re-execute the request
                        response = super.doInBackground(requests);
                    }
                }
            } else {
                Log.i(TAG, "The request will be sent without any user authentication");

                // ...execute request
                response = super.doInBackground(requests);
            }


        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
            setException(new MswApiException(e));
            response = null;
        }
        return response;
    }

    @Override
    protected final void onCancelled(HttpResponse mswApiResponse) {
        super.onCancelled(mswApiResponse);
        onCancel();
        onFinish();
    }

    @Override
    protected final void onCancelled() {
        super.onCancelled();
    }

    @Override
    public void onPostExecute(HttpResponse response) {
        super.onPostExecute(response);
        try {
            if (response == null) {
                // A exception has been raised as MswApiException
                throw new MswApiException(getException());
            }

            MswApiResponse apiResponse = getResponse();
            if (!apiResponse.isError()) {
                Log.v(TAG, "Handle API response " + apiResponse.toString());
                onSuccess(apiResponse);
            } else {
                Log.v(TAG, "Handle received API error " + apiResponse.toString());
                onFailure((MswApiResponseError) apiResponse);
            }

        } catch (MswApiException e) {
            e.printStackTrace();
            onFailure(e);
        } finally {
            onFinish();
        }
    }

    public MswApiResponse getResponse() throws MswApiException {
        try {
            return parseHttpResponse(get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new MswApiException(e);
        }
    }

    /**
     * Parse an HTTP response and instantiate a new response from MusicSheetWriter's API. The
     * information from the HTTP response are copied inside API response. If the HTTP response
     * is an error, then the returned object is a error from MusicSheetWriter's API. In this case,
     * all information regarding the error are set.
     * @param response the HTTP response to parse
     * @return a MswApiResponse or a MswApiError if the HTTP response is an error.
     */
    public static MswApiResponse parseHttpResponse(HttpResponse response) throws MswApiException {
        if (response == null)
            throw new MswApiException("Unable to parse an null response");

        MswApiResponse apiResponse;

        if (response.isError()) {
            try {
                JSONObject error = new JSONObject(response.getBody());
                apiResponse = new MswApiResponseError(response);
                ((MswApiResponseError) apiResponse).setApiErrorCode(error.getString("shortcode"));
                ((MswApiResponseError) apiResponse).setApiErrorMessage(error.getString("message"));
                ((MswApiResponseError) apiResponse).setApiErrorData(error.optJSONObject("data"));
            } catch (JSONException e) {
                e.printStackTrace();
                throw new MswApiException("Unable to parse the response returned by the API", e);
            }
        } else {
            apiResponse = new MswApiResponse(response);
        }
        return apiResponse;
    }

    /**
     * Call the onStart method of the response handler if it has been set
     */
    private void onStart() {
        if (mResponseHandler != null) {
            Log.v(TAG, "Calling onStart() of the responseHandler {" +
                    mResponseHandler.toString() + " for the connection {" + toString() + "}}");
            mResponseHandler.onStart();
        }
    }

    /**
     * Call the onFinish method of the response handler if it has been set
     */
    private void onFinish() {
        if (mResponseHandler != null) {
            Log.v(TAG, "Calling onFinish() of the responseHandler {" +
                    mResponseHandler.toString() + " for the connection {" + toString() + "}}");
            mResponseHandler.onFinish();
        }
    }

    /**
     * Call the onSuccess method of the response handler if it has been set
     * @param mswApiResponse Argument to pass to the onSuccess method
     * @throws MswApiException if the responseHandler.onStart throw an exception
     */
    private void onSuccess(@NonNull MswApiResponse mswApiResponse) throws MswApiException {
        if (mResponseHandler != null) {
            Log.v(TAG, "Calling onSuccess(MswApiResponse) of the responseHandler {" +
                    mResponseHandler.toString() + " for the connection {" + toString() + "}}");
            mResponseHandler.onSuccess(mswApiResponse);
        }
    }

    /**
     * Call the onFailure method of the response handler if it has been set
     * @param mswApiResponseError Argument to pass to the onFailure method
     * @throws MswApiException if the responseHandler.onFailure throw an exception
     */
    private void onFailure(@NonNull MswApiResponseError mswApiResponseError) throws MswApiException {
        if (mResponseHandler != null) {
            Log.v(TAG, "Calling onFailure(MswApiError) of the responseHandler {" +
                    mResponseHandler.toString() + " for the connection {" + toString() + "}}");
            mResponseHandler.onFailure(mswApiResponseError);
        }
    }

    /**
     * Call the onFailure method of the response handler if it has been set
     * @param mswApiException Argument to pass to the onFailure method
     */
    private void onFailure(@NonNull MswApiException mswApiException) {
        if (mResponseHandler != null) {
            Log.v(TAG, "Calling onFailure(MswApiException) of the responseHandler {" +
                    mResponseHandler.toString() + " for the connection {" + toString() + "}}");
            mResponseHandler.onFailure(mswApiException);
        }
    }

    /**
     * Call the onCancel method of the response handler if it has been set
     */
    private void onCancel() {
        if (mResponseHandler != null) {
            Log.v(TAG, "Calling onCancel(MswApiException) of the responseHandler {" +
                    mResponseHandler.toString() + " for the connection {" + toString() + "}}");
            mResponseHandler.onCancel();
        }
    }

}
