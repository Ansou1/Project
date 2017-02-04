package com.musicsheetwriter.musicsheetwriter.network;


public abstract class MswApiAsyncConnectionResponseHandler {

    MswApiAsyncConnection mConnection;

    /**
     * This function is invoked before sending the request
     */
    protected void onStart() {}

    /**
     * This function is invoked after the request has ended.
     * It is called after the other routine onCancel, onSuccess, onFailure
     */
    protected void onFinish() {}

    /**
     * This function is invoked if the response is not an error
     * @param mswApiResponse The response from the API
     * @throws MswApiException if the response has some problems
     */
    protected void onSuccess(MswApiResponse mswApiResponse) throws MswApiException {}

    /**
     * This function is invoked if the response is an error returned by the API
     * @param mswApiResponseError The response from the API
     * @throws MswApiException if the reponse has some problems
     */
    protected void onFailure(MswApiResponseError mswApiResponseError) throws MswApiException {}

    /**
     * This function is invoked if an error occurs during or after sending the request
     * @param mswApiException the exception containing the cause of th failure
     */
    protected void onFailure(MswApiException mswApiException) {}

    /**
     * This function is invoked if the request has been canceled
     */
    protected void onCancel() {}

    protected final void setConnection(MswApiAsyncConnection connection) {
        mConnection = connection;
    }

    protected final MswApiAsyncConnection getConnection() {
        return mConnection;
    }

}
