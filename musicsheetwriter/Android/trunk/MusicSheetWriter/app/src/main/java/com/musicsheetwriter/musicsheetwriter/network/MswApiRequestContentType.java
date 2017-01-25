package com.musicsheetwriter.musicsheetwriter.network;


public enum MswApiRequestContentType {

    AUD_MIDI("audio/midi"),
    APP_JSON("application/json"),
    APP_FORM("application/x-www-form-urlencoded"),
    APP_MXL("application/vnd.recordare.musicxml"),
    APP_MUSICXML("application/vnd.recordare.musicxml+xml"),
    APP_XML("appication/xml"),
    IMG_JPEG("image/jpeg"),
    IMG_ICON("image/x-icon"),
    IMG_GIF("image/gif"),
    IMG_PNG("image/png"),
    TEXT_HTML("text/html"),
    TEXT_PLAIN("text/plain")
        ;

    private String displayName;

    MswApiRequestContentType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return this.displayName;
    }

    public static MswApiRequestContentType fromString(String displayName) {
        if (displayName != null) {
            for (MswApiRequestContentType c : MswApiRequestContentType.values()) {
                if (displayName.equalsIgnoreCase(c.displayName)) {
                    return c;
                }
            }
        }
        return null;
    }
}
