package com.musicsheetwriter.musicsheetwriter.network.httputils;

import java.io.IOException;


public class HttpException extends IOException {

    public HttpException() {
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(Throwable cause) {
        super(cause);
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }

}
