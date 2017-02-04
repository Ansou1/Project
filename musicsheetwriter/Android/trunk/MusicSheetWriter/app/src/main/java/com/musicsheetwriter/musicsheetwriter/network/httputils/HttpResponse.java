package com.musicsheetwriter.musicsheetwriter.network.httputils;


import java.util.HashMap;

@SuppressWarnings("unused")
public class HttpResponse {

    /**
     * The returned status code returned by the called server
     */
    private int mStatusCode;

    /**
     * The returned properties in the response header
     */
    private HashMap<String, String> mProperties;

    /**
     * The returned mBody in the response body returned by the called server
     */
    private String mBody;


    public HttpResponse() {
        this.mProperties= new HashMap<>();
    }

    public HttpResponse(int statusCode, HashMap<String, String> properties, String body) {
        this.mStatusCode = statusCode;
        this.mProperties = new HashMap<>(properties);
        this.mBody = body;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public HttpResponse setStatusCode(int statusCode) {
        this.mStatusCode = statusCode;
        return this;
    }

    public HashMap<String, String> getProperties() {
        return mProperties;
    }

    public HttpResponse setProperty(String name, String value) {
        mProperties.put(name, value);
        return this;
    }

    public boolean isPropertySet(String name) {
        return mProperties.containsKey(name);
    }

    public String getPropertyValue(String name) {
        return mProperties.get(name);
    }

    public void removeProperty(String name) {
        mProperties.remove(name);
    }

    public String getBody() {
        return mBody;
    }

    public HttpResponse setBody(String body) {
        this.mBody = body;
        return this;
    }

    /**
     * Determine whether the response is an error. All response with a status code like 4XX or 5XX
     * are errors
     * @return whether the response is an error or not.
     */
    public boolean isError() {
        return mStatusCode / 100 == 4 ||
                mStatusCode / 100 == 5;
    }

    @Override
    public String toString() {
        return super.toString()
                + " statusCode=" + getStatusCode();
    }
}
