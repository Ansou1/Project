package com.musicsheetwriter.musicsheetwriter.network;

import com.musicsheetwriter.musicsheetwriter.network.httputils.HttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpCookie;
import java.util.List;

public class MswApiResponse extends HttpResponse {

    public MswApiResponse() {
        super();
    }

    public MswApiResponse(HttpResponse copy) {
        super(copy.getStatusCode(), copy.getProperties(), copy.getBody());
    }

    public JSONObject getJSONObject() throws MswApiException {
        try {
            return new JSONObject(getBody());
        } catch (JSONException e) {
            throw new MswApiException("The response cannot be mapped to a JSONObject", e);
        }
    }

    public JSONArray getJSONArray() throws MswApiException {
        try {
            return new JSONArray(getBody());
        } catch (JSONException e) {
            throw new MswApiException("The response cannot be mapped to a JSONArray", e);
        }
    }

    /**
     * Parse the header and return the value of the cookie called PHPSESSID.
     * @return the value of the cookie called PHPSESSID.
     * @throws MswApiException if no such cookie is found.
     */
    public String getAuthToken() throws MswApiException {
        if (!isPropertySet("Set-Cookie")) {
            throw new MswApiException("The response does not contain 'Set-Cookie' header field");
        }
        List<HttpCookie> cookies = HttpCookie.parse(getPropertyValue("Set-Cookie"));
        for (HttpCookie cookie : cookies) {
            if (cookie.getName().equals("PHPSESSID")) {
                return cookie.getValue();
            }
        }
        throw new MswApiException("The response does not contain any cookie called 'PHPSESSID'");
    }

}
