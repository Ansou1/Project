package com.musicsheetwriter.musicsheetwriter.network;

import android.accounts.Account;
import android.accounts.AccountManagerCallback;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.musicsheetwriter.musicsheetwriter.network.httputils.HttpMethod;
import com.musicsheetwriter.musicsheetwriter.network.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

@SuppressWarnings("unused")
public class MswApiClient {

    /**
     * The tag used for logging
     */
    public static final String TAG = "MswApiClient";


    /**
     * The base of the URL for MusicSheetWriter's API
     */
    private static final String BASE_URL = "https://musicsheetwriter.tk/api";

    private Activity mActivity;
    private Account mAccount;
    private AccountManagerCallback<Bundle> mCallback;

    /**
     * Constructor of a client able to send request which does not require any authentication
     */
    public MswApiClient() {

    }

    /**
     * Constructor of a client to send request with authentication
     * @param context The context of the application
     * @param account The account identifying the connected user or null if no user is connected
     * @param callback The callback used for re-authentication or null if no callback has to be called
     */
    public MswApiClient(@Nullable Activity context, @Nullable Account account, AccountManagerCallback<Bundle> callback) {
        mActivity = context;
        mAccount = account;
        mCallback = callback;
    }

    /**
     * <p><b>Route: </b>/login</p>
     * <p><b>Method: </b>POST</p>
     */
    public MswApiAsyncConnection login(String username, String password,
                             MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/login"), HttpMethod.POST);
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("login", username)
                    .put("password", password);
            request.setBody(requestBody);
            return execute(request, responseHandler);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p><b>Route: </b>/logout</p>
     * <p><b>Method: </b>POST</p>
     */
    public MswApiAsyncConnection logout(MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/logout"), HttpMethod.POST);
        return execute(request, responseHandler);
    }

    /**
     * <p><b>Route: </b>/forgotten_password</p>
     * <p><b>Method: </b>POST</p>
     */
    public MswApiAsyncConnection forgottenPassword(String email,
                                       MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/forgotten_password"), HttpMethod.POST);
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("email", email);
            request.setBody(requestBody);
            return execute(request, responseHandler);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p><b>Route: </b>/users</p>
     * <p><b>Method: </b>GET</p>
     */
    public MswApiAsyncConnection getAccountList(String uname, MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/users"), HttpMethod.GET);
        request.setQueryParam("uname", uname);
        return execute(request, responseHandler);
    }

    /**
     * <p><b>Route: </b>/users</p>
     * <p><b>Method: </b>POST</p>
     */
    public MswApiAsyncConnection createAccount(String username, String email, String password,
                                     MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/users"), HttpMethod.POST);
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("username", username)
                    .put("email", email)
                    .put("password", password);
            request.setBody(requestBody);
            return execute(request, responseHandler);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p><b>Route: </b>/users/{userId}</p>
     * <p><b>Method: </b>GET</p>
     */
    public MswApiAsyncConnection getAccount(Integer userId, MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/users/" + userId), HttpMethod.GET);
        return execute(request, responseHandler);
    }

    /**
     * <p><b>Route: </b>/users/{userId}</p>
     * <p><b>Method: </b>DELETE</p>
     */
    public MswApiAsyncConnection closeAccount(Integer userId, MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/users/" + userId), HttpMethod.DELETE);
        return execute(request, responseHandler);
    }

    /**
     * <p><b>Route: </b>/users/{userId}/password</p>
     * <p><b>Method: </b>PUT</p>
     */
    public MswApiAsyncConnection changeAccountPassword(Integer userId, String currentPassword, String newPassword,
                                                       MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/users/" + userId + "/password"), HttpMethod.PUT);
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("current_password", currentPassword)
                    .put("new_password", newPassword);
            request.setBody(requestBody);
            return execute(request, responseHandler);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p><b>Route: </b>/users/{userId}/personal_data</p>
     * <p><b>Method: </b>GET</p>
     */
    public MswApiAsyncConnection getAccountPersonalData(Integer userId, MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/users/" + userId + "/personal_data"), HttpMethod.GET);
        return execute(request, responseHandler);
    }

    /**
     * <p><b>Route: </b>/users/{userId}/personal_data</p>
     * <p><b>Method: </b>PUT</p>
     */
    public MswApiAsyncConnection editAccountPersonalData(Integer userId, String firstName, String surname, String email,
                                                         String message,
                                                         MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/users/" + userId + "/personal_data"), HttpMethod.PUT);
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("firstname", firstName)
                    .put("lastname", surname)
                    .put("email", email)
                    .put("message", message);

            request.setBody(requestBody);
            return execute(request, responseHandler);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p><b>Route: </b>/users/{userId}/photo</p>
     * <p><b>Method: </b>GET</p>
     */
    public MswApiAsyncConnection getAccountPicture(Integer userId, MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/users/" + userId + "/photo"), HttpMethod.GET);
        return execute(request, responseHandler);
    }

    /**
     * <p><b>Route: </b>/users/{userId}/photo</p>
     * <p><b>Method: </b>PUT</p>
     */
    public MswApiAsyncConnection editAccountPicture(Integer userId, InputStream content, String type,
                                            MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/users/" + userId + "/photo"), HttpMethod.PUT);
        request.setBody(StringUtils.iStreamToByteArray(content));
        request.setProperty("Content-type", MswApiRequestContentType.fromString(type).displayName());
        return execute(request, responseHandler);
    }

    /**
     * <p><b>Route: </b>/users/{userId}/subscriptions</p>
     * <p><b>Method: </b>GET</p>
     */
    public MswApiAsyncConnection getAccountSubscriptions(Integer userId, MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/users/" + userId + "/subscriptions"), HttpMethod.GET);
        return execute(request, responseHandler);
    }

    /**
     * <p><b>Route: </b>/users/{userId}/subscriptions</p>
     * <p><b>Method: </b>POST</p>
     */
    public MswApiAsyncConnection addAccountSubscription(Integer userId, Integer idToAdd,
                                                        MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/users/" + userId + "/subscriptions"), HttpMethod.POST);
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("id", idToAdd);

            request.setBody(requestBody);
            execute(request, responseHandler);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p><b>Route: </b>/users/{userId}/subscriptions/{subId}</p>
     * <p><b>Method: </b>DELETE</p>
     */
    public MswApiAsyncConnection removeAccountSubscription(Integer userId, Integer idToRemove,
                                                           MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/users/" + userId + "/subscriptions/" + idToRemove), HttpMethod.DELETE);
        return execute(request, responseHandler);
    }

    /**
     * <p><b>Route: </b>/users/{userId}/subscribers</p>
     * <p><b>Method: </b>GET</p>
     */
    public MswApiAsyncConnection getAccountSubscribers(Integer userId, MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/users/" + userId + "/subscribers"), HttpMethod.GET);
        return execute(request, responseHandler);
    }

    /**
     * <p><b>Route: </b>/users/{userId}/scores/own</p>
     * <p><b>Method: </b>GET</p>
     */
    public MswApiAsyncConnection getAccountOwnedScores(Integer userId, MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/users/" + userId + "/scores/own"), HttpMethod.GET);
        return execute(request, responseHandler);
    }

    /**
     * <p><b>Route: </b>/users/{userId}/scores/own</p>
     * <p><b>Method: </b>POST</p>
     */
    public MswApiAsyncConnection addAccountOwnedScore(Integer userId, InputStream content, String type,
                                                     MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/users/" + userId + "/scores/own"), HttpMethod.POST);
        request.setBody(StringUtils.iStreamToByteArray(content));
        request.setProperty("Content-type", MswApiRequestContentType.fromString(type).displayName());
        return execute(request, responseHandler);
    }

    /**
     * <p><b>Route: </b>/users/{userId}/scores/own/{scoreId}</p>
     * <p><b>Method: </b>PUT</p>
     */
    public MswApiAsyncConnection editAccountOwnedScore(Integer userId, Integer idToEdit, String title,
                                                       MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/users/" + userId + "/scores/own/" + idToEdit), HttpMethod.PUT);
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("title", title);
            request.setBody(requestBody);
            return execute(request, responseHandler);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p><b>Route: </b>/users/{userId}/scores/own/{scoreId}</p>
     * <p><b>Method: </b>DELETE</p>
     */
    public MswApiAsyncConnection removeAccountOwnedScore(Integer userId, Integer idToRemove,
                                                           MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/users/" + userId + "/scores/own/" + idToRemove), HttpMethod.DELETE);
        return execute(request, responseHandler);
    }

    /**
     * <p><b>Route: </b>/users/{userId}/scores/favourites</p>
     * <p><b>Method: </b>GET</p>
     */
    public MswApiAsyncConnection getAccountFavouriteScores(Integer userId, MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/users/" + userId + "/scores/favourites"), HttpMethod.GET);
        return execute(request, responseHandler);
    }

    /**
     * <p><b>Route: </b>/users/{userId}/scores/favourites</p>
     * <p><b>Method: </b>POST</p>
     */
    public MswApiAsyncConnection addAccountFavouriteScore(Integer userId, Integer idToAdd,
                                                           MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/users/" + userId + "/scores/favourites"), HttpMethod.POST);
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("id", idToAdd);

            request.setBody(requestBody);
            execute(request, responseHandler);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p><b>Route: </b>/users/{userId}/scores/favourites/{subId}</p>
     * <p><b>Method: </b>DELETE</p>
     */
    public MswApiAsyncConnection removeAccountFavouriteScore(Integer userId, Integer idToRemove,
                                                              MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/users/" + userId + "/scores/favourites/" + idToRemove), HttpMethod.DELETE);
        return execute(request, responseHandler);
    }

    /**
     * <p><b>Route: </b>/scores</p>
     * <p><b>Method: </b>GET</p>
     */
    public MswApiAsyncConnection getScoreList(String title, MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/scores"), HttpMethod.GET);
        request.setQueryParam("title", title);
        return execute(request, responseHandler);
    }

    /**
     * <p><b>Route: </b>/users/{userId}/scores/own/{scoreId}</p>
     * <p><b>Method: </b>DELETE</p>
     */
    public MswApiAsyncConnection removeScore(Integer scoreId, MswApiAsyncConnectionResponseHandler responseHandler) {
        MswApiRequest request = new MswApiRequest(getAbsoluteUri("/scores/" + scoreId), HttpMethod.DELETE);
        return execute(request, responseHandler);
    }

    /**
     * Instantiate the connection and execute the request
     * @param request the request to send
     * @param responseHandler the response handler to be attached to the connection
     * @return the connection instance
     */
    private MswApiAsyncConnection execute(MswApiRequest request, MswApiAsyncConnectionResponseHandler responseHandler) {
        Log.v(TAG, "Ready to execute request {" + request.toString() + "}");
        MswApiAsyncConnection connection = new MswApiAsyncConnection(mActivity, mAccount, mCallback, responseHandler);
        connection.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        return connection;
    }

    /**
     * Format the entire URI concatenating the base URL and the relative URI, from the entry point
     * to the end.
     * @param relativeUrl the URI to concatenate to the base URL
     * @return the full URI
    */
    private static String getAbsoluteUri(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
