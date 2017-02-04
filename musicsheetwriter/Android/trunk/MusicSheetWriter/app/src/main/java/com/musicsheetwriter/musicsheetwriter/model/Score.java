package com.musicsheetwriter.musicsheetwriter.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unused")
public class Score implements Parcelable {

    private int id;
    private String title;
    private User author;
    private boolean isFavourite;
    private String projectLocation;
    private String previewLocation;
    private int nbFavourite;

    public Score() {
        author = new User();
    }

    public Score(int id, String title, User author) {
        this();
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public Score(int id, String title, User author, String projectLocation, String previewLocation,
                 boolean isFavourite) {
        this(id, title, author);
        this.projectLocation = projectLocation;
        this.previewLocation = previewLocation;
        this.isFavourite = isFavourite;
    }

    private Score(Parcel in) {
        this();
        id = in.readInt();
        title = in.readString();
        author = in.readParcelable(author.getClass().getClassLoader());
        isFavourite = in.readInt() != 0;
        projectLocation = in.readString();
        previewLocation = in.readString();
        nbFavourite = in.readInt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    public String getProjectLocation() {
        return projectLocation;
    }

    public void setProjectLocation(String projectLocation) {
        this.projectLocation = projectLocation;
    }

    public String getPreviewLocation() {
        return previewLocation;
    }

    public void setPreviewLocation(String previewLocation) {
        this.previewLocation = previewLocation;
    }

    public int getNbFavourite() {
        return nbFavourite;
    }

    public void setNbFavourite(int nbFavourite) {
        this.nbFavourite = nbFavourite;
    }

    public static Score fromJson(JSONObject jsonScore) throws JSONException {
        Score score = new Score();

        score.setId(jsonScore.getInt("id"));
        score.setTitle(jsonScore.getString("title"));
        score.setProjectLocation(jsonScore.getString("location_project"));
        score.setPreviewLocation(jsonScore.getString("location_preview"));
        score.setNbFavourite(jsonScore.getInt("nb_favourites"));
        score.setIsFavourite(jsonScore.getBoolean("is_favourite"));

        // Get Author
        JSONObject jsonAuthor = jsonScore.getJSONObject("by");
        score.setAuthor(new User(jsonAuthor.getInt("id"), jsonAuthor.getString("username")));
        return score;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeParcelable(author, 0);
        dest.writeInt((byte) (isFavourite ? 1 : 0));
        dest.writeString(projectLocation);
        dest.writeString(previewLocation);
        dest.writeInt(nbFavourite);
    }

    public static final Parcelable.Creator<Score> CREATOR
            = new Parcelable.Creator<Score>() {
        public Score createFromParcel(Parcel in) {
            return new Score(in);
        }

        public Score[] newArray(int size) {
            return new Score[size];
        }
    };
}
