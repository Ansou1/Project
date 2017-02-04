package com.musicsheetwriter.musicsheetwriter.network.httputils;

import java.util.HashMap;

@SuppressWarnings("unused")
public class HttpRequest {

    /**
     * The URI of the request
     */
    private String mUri;

    /**
     * The HTTP method of the request
     */
    private HttpMethod mMethod;

    /**
     * The properties to put in the request header
     */
    private HashMap<String, String> mProperties;

    /**
     * The parameters to put in the query of the request
     */
    private HashMap<String, String> mQueryParams;

    /**
     * The content to put in the body of the request
     */
    private byte[] mBody;

    public HttpRequest() {
        this.mUri = null;
        this.mMethod = null;
        this.mProperties = new HashMap<>();
        this.mQueryParams = new HashMap<>();
        this.mBody = null;
    }

    public HttpRequest(String uri, HttpMethod method) {
        this.mUri = uri;
        this.mMethod = method;
        this.mProperties = new HashMap<>();
        this.mQueryParams = new HashMap<>();
        this.mBody = null;
    }

    public String getUri() {
        return mUri;
    }

    public HttpRequest setUri(String uri) {
        this.mUri = uri;
        return this;
    }

    public HttpMethod getMethod() {
        return mMethod;
    }

    public HttpRequest setMethod(HttpMethod method) {
        this.mMethod = method;
        return this;
    }

    public HashMap<String, String> getProperties() {
        return mProperties;
    }

    public HttpRequest setProperty(String name, String value) {
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

    public HashMap<String, String> getQueryParams() {
        return mQueryParams;
    }

    public HttpRequest setQueryParam(String name, String value) {
        mQueryParams.put(name, value);
        return this;
    }

    public boolean isQueryParamSet(String name) {
        return mQueryParams.containsKey(name);
    }

    public String getQueryParamValue(String name) {
        return mQueryParams.get(name);
    }

    public void removeQueryParam(String name) {
        mQueryParams.remove(name);
    }

    public byte[] getBody() {
        return mBody;
    }

    public HttpRequest setBody(String body) {
        return setBody(body.getBytes());
    }

    public HttpRequest setBody(byte[] body) {
        this.mBody = body.clone();
        setProperty("Content-Type", "application/x-www-form-urlencoded");
        setProperty("Content-Length", Integer.toString(body.length));
        return this;
    }

    @Override
    public String toString() {
        return super.toString()
                + " uri=" + getUri()
                + " method=" + getMethod().toString();
    }
}
