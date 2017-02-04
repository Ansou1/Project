package com.musicsheetwriter.musicsheetwriter.network;

import com.musicsheetwriter.musicsheetwriter.network.httputils.HttpMethod;
import com.musicsheetwriter.musicsheetwriter.network.httputils.HttpRequest;

import org.json.JSONObject;


public class MswApiRequest extends HttpRequest {

    public MswApiRequest(String uri, HttpMethod method) {
        super(uri, method);
    }

    @Override
    public MswApiRequest setUri(String uri) {
        return (MswApiRequest) super.setUri(uri);
    }

    @Override
    public MswApiRequest setMethod(HttpMethod method) {
        return (MswApiRequest) super.setMethod(method);
    }

    @Override
    public MswApiRequest setProperty(String name, String value) {
        return (MswApiRequest) super.setProperty(name, value);
    }

    @Override
    public MswApiRequest setQueryParam(String name, String value) {
        return (MswApiRequest) super.setQueryParam(name, value);
    }

    @Override
    public MswApiRequest setBody(String body) {
        return (MswApiRequest) super.setBody(body);
    }

    @Override
    public MswApiRequest setBody(byte[] body) {
        return (MswApiRequest) super.setBody(body);
    }

    public MswApiRequest setContentType(MswApiRequestContentType contentType) {
        return setProperty("Content-Type", contentType.displayName());
    }

    public MswApiRequest setBody(JSONObject body) {
        setBody(body.toString());
        setContentType(MswApiRequestContentType.APP_JSON);
        return this;
    }

}
