package com.musicsheetwriter.musicsheetwriter.listadapter;

import android.view.View;

import com.musicsheetwriter.musicsheetwriter.model.User;


public interface OnUserInteractionListener {
    void onViewUserProfile(User item, View view);
    void onSubscribe(User item, View view);
    void onUnsubscribe(User item, View view);
}
