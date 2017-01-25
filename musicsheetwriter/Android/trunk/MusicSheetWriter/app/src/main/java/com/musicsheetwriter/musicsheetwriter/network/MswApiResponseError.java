package com.musicsheetwriter.musicsheetwriter.network;

import com.musicsheetwriter.musicsheetwriter.network.httputils.HttpResponse;

import org.json.JSONObject;

public class MswApiResponseError extends MswApiResponse {

    private String apiErrorCode;
    private String apiErrorMessage;
    private JSONObject apiErrorData;

    public MswApiResponseError() {
        super();
    }

    public MswApiResponseError(HttpResponse copy) {
        super(copy);
    }

    public String getApiErrorCode() {
        return apiErrorCode;
    }

    public void setApiErrorCode(String apiErrorCode) {
        this.apiErrorCode = apiErrorCode;
    }

    public String getApiErrorMessage() {
        return apiErrorMessage;
    }

    public void setApiErrorMessage(String apiErrorMessage) {
        this.apiErrorMessage = apiErrorMessage;
    }

    public JSONObject getApiErrorData() {
        return apiErrorData;
    }

    public void setApiErrorData(JSONObject apiErrorData) {
        this.apiErrorData = apiErrorData;
    }

    @Override
    public String toString() {
        return super.toString()
                + " errorCode=" + getApiErrorCode()
                + " message" + getApiErrorMessage();
    }

}
