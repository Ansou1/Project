package com.musicsheetwriter.musicsheetwriter.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unused")
public class UserPersonalData implements Parcelable {
    protected int id;
    protected String username;
    protected String firstName;
    protected String surname;
    protected String email;
    protected String photo;
    protected String message;

    private int nbSubscriptions;
    private int nbSubscribers;
    private int nbScores;
    private int nbFavourites;

    public UserPersonalData() {
        // Empty ctor
    }

    private UserPersonalData(Parcel in) {
        id = in.readInt();
        username = in.readString();
        firstName = in.readString();
        surname = in.readString();
        email = in.readString();
        photo = in.readString();
        message = in.readString();
        nbSubscriptions = in.readInt();
        nbSubscribers = in.readInt();
        nbScores = in.readInt();
        nbFavourites = in.readInt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getNbSubscriptions() {
        return nbSubscriptions;
    }

    public void setNbSubscriptions(int nbSubscriptions) {
        this.nbSubscriptions = nbSubscriptions;
    }

    public int getNbSubscribers() {
        return nbSubscribers;
    }

    public void setNbSubscribers(int nbSubscribers) {
        this.nbSubscribers = nbSubscribers;
    }

    public int getNbScores() {
        return nbScores;
    }

    public void setNbScores(int nbScores) {
        this.nbScores = nbScores;
    }

    public int getNbFavourites() {
        return nbFavourites;
    }

    public void setNbFavourites(int nbFavourites) {
        this.nbFavourites = nbFavourites;
    }

    public static UserPersonalData fromJson(JSONObject jsonPersonalData) throws JSONException {
        UserPersonalData personalData = new UserPersonalData();
        personalData.setId(jsonPersonalData.getInt("id"));
        personalData.setUsername(jsonPersonalData.getString("username"));
        personalData.setFirstName(jsonPersonalData.getString("firstname"));
        personalData.setSurname(jsonPersonalData.getString("lastname"));
        personalData.setEmail(jsonPersonalData.getString("email"));
        personalData.setMessage(jsonPersonalData.getString("message"));
        personalData.setPhoto(jsonPersonalData.getString("photo"));
        personalData.setNbSubscriptions(jsonPersonalData.getInt("nb_subscriptions"));
        personalData.setNbSubscribers(jsonPersonalData.getInt("nb_subscribers"));
        personalData.setNbScores(jsonPersonalData.getInt("nb_scores"));
        personalData.setNbFavourites(jsonPersonalData.getInt("nb_favourites"));
        return personalData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(username);
        dest.writeString(firstName);
        dest.writeString(surname);
        dest.writeString(email);
        dest.writeString(photo);
        dest.writeString(message);
        dest.writeInt(nbSubscriptions);
        dest.writeInt(nbSubscribers);
        dest.writeInt(nbScores);
        dest.writeInt(nbFavourites);
    }

    public static final Parcelable.Creator<UserPersonalData> CREATOR
            = new Parcelable.Creator<UserPersonalData>() {
        public UserPersonalData createFromParcel(Parcel in) {
            return new UserPersonalData(in);
        }

        public UserPersonalData[] newArray(int size) {
            return new UserPersonalData[size];
        }
    };
}
