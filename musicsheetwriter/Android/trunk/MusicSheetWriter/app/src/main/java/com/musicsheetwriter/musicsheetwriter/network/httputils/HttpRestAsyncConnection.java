package com.musicsheetwriter.musicsheetwriter.network.httputils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.musicsheetwriter.musicsheetwriter.network.utils.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class HttpRestAsyncConnection extends AsyncTask<HttpRequest, Void, HttpResponse> {

    /**
     * The tag used for logging
     */
    public static final String TAG = "HttpRestAsyncConnection";

    private Context mContext;

    /**
     * The instance of the connection with the server
     */
    private HttpsURLConnection mUrlConnection;
    private IOException mException;

    public HttpRestAsyncConnection() { }

    public HttpRestAsyncConnection(Context context) {
        mContext = context;
    }

    public final IOException getException() {
        return this.mException;
    }

    @Override
    protected HttpResponse doInBackground(HttpRequest... requests) {
        mUrlConnection = null;
        HttpResponse response = null;

        try {
            if (requests == null || requests[0] == null) {
                Log.e(TAG, "The request is null");
                throw new IOException("The request to send is null");
            }
            HttpRequest request = requests[0];
            checkRequest(request);

            String charset;
            if (request.isPropertySet("charset")) {
                charset = request.getPropertyValue("charset");
            } else {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    charset = StandardCharsets.UTF_8.displayName();
                } else {
                    charset = "UTF-8";
                }
            }

            URL url = new URL(request.getUri() + StringUtils.getParamInQuery(request.getQueryParams(),
                    charset));

            // Initiate connection
            mUrlConnection = (HttpsURLConnection) url.openConnection();
            Log.v(TAG, "Connection instantiated {" + mUrlConnection.toString() + "}");

            mUrlConnection.setRequestMethod(request.getMethod().name());
            HashMap<String, String> requestProperties = request.getProperties();
            for (Map.Entry<String, String> entry : requestProperties.entrySet()) {
                mUrlConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            // Add body data and set header attributes
            HttpMethod method = request.getMethod();
            if (method == HttpMethod.POST ||
                    method == HttpMethod.PUT) {
                mUrlConnection.setDoOutput(true);
                mUrlConnection.setChunkedStreamingMode(0);

                byte[] requestBody = request.getBody();
                if (requestBody != null) {
                    OutputStream out = new BufferedOutputStream(mUrlConnection.getOutputStream());
                    out.write(requestBody);
                    out.close();
                }
            }

            // Store Http response
            response = new HttpResponse();
            response.setStatusCode(mUrlConnection.getResponseCode());

            Log.v(TAG, "Getting response {" + response + "}");

            if (response.isError()) {
                InputStream in = new BufferedInputStream(mUrlConnection.getErrorStream());
                response.setBody(StringUtils.iStreamToString(in));
                in.close();
            } else {
                InputStream in = new BufferedInputStream(mUrlConnection.getInputStream());
                response.setBody(StringUtils.iStreamToString(in));
                in.close();
            }

            Map<String, List<String>> headerFields = mUrlConnection.getHeaderFields();
            Set<String> headerFieldsKeys = mUrlConnection.getHeaderFields().keySet();
            for (String headerFieldKey : headerFieldsKeys) {
                response.setProperty(headerFieldKey, headerFields.get(headerFieldKey).get(0));
            }
        } catch (IOException e) {
            Log.e(TAG, e.getClass().getName() + " raised during connection: " + e.getMessage());
            response = null;
            setException(new HttpException(e));
        } finally {
            if (mUrlConnection != null) {
                mUrlConnection.disconnect();
            }
        }

        // Return the response
        return response;
    }

    @Override
    protected void onCancelled(HttpResponse mswApiResponse) {
        super.onCancelled(mswApiResponse);
        Log.w(TAG, "The connection has been canceled");
        if (mUrlConnection != null) {
            Log.v(TAG, "Close connection");
            mUrlConnection.disconnect();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (mUrlConnection != null) {
            mUrlConnection.disconnect();
        }
    }

    protected void checkRequest(HttpRequest request) throws IOException {
        if (request.getUri() == null) {
            throw new IOException("The URI of the request has not been set");
        }
        if (request.getMethod() == null) {
            throw new IOException("The method of the request has not been set");
        }
    }

    protected void setException(IOException exception) {
        this.mException = exception;
    }
}

