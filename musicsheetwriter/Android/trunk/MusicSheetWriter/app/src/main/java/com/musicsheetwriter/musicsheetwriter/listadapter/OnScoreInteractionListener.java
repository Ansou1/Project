package com.musicsheetwriter.musicsheetwriter.listadapter;

import android.view.View;

import com.musicsheetwriter.musicsheetwriter.model.Score;


public interface OnScoreInteractionListener {
    void onViewScorePreview(Score item, View view);
    void onDeleteScore(Score item, View view);
    void onDownloadScoreProject(Score item, View view);
    void onDownloadScorePreview(Score item, View view);
    void onPutAsFavourite(Score item, View view);
    void onRemoveFromFavourite(Score item, View view);
}
