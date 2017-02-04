package com.musicsheetwriter.musicsheetwriter.network.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class StringUtils {

    /**
     * Encode a map representing the parameters to put into an HTTP URI
     * @param params the map containing the parameters to encode
     * @param charset the charset used for encoding
     * @return the encoded string
     * @throws UnsupportedEncodingException if the charset is not correct
     */
    public static String getParamInQuery(HashMap<String, String> params, String charset)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first) {
                first = false;
                result.append("?");
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode(entry.getKey(), charset));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), charset));
        }

        return result.toString();
    }

    /**
     * Convert an input stream into a string. The content of the stream is put into a string
     * @param is the stream to convert
     * @return the content of the stream
     */
    public static String iStreamToString(InputStream is)
    {
        BufferedReader rd = new BufferedReader(new InputStreamReader(is), 4096);
        String line;
        StringBuilder sb =  new StringBuilder();
        try {
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Convert an input stream into a byte array. The content of the stream is put into a byte array
     * @param is the stream to convert
     * @return the content of the stream
     */
    public static byte[] iStreamToByteArray(InputStream is)
    {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        try {
            while ((len = is.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteBuffer.toByteArray();
    }
}
