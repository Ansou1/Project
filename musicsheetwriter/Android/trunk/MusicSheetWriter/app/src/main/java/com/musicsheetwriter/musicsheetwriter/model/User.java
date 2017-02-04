package com.musicsheetwriter.musicsheetwriter.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class User implements Parcelable {

    private boolean isSubscription;
    private UserPersonalData personalData;
    private Map<Integer, User> subscriptions;
    private Map<Integer, User> subscribers;
    private Map<Integer, Score> ownedScores;
    private Map<Integer, Score> favouriteScores;

    public User() {
        personalData = new UserPersonalData();
        subscriptions = new HashMap<>();
        subscribers = new HashMap<>();
        ownedScores = new HashMap<>();
        favouriteScores = new HashMap<>();
    }

    public User(int id, String username) {
        this();
        personalData.setId(id);
        personalData.setUsername(username);
    }

    public User(int id, String username, String photo, boolean isSubscription) {
        this(id, username);
        personalData.setPhoto(photo);
        this.isSubscription = isSubscription;
    }

    private User(Parcel in) {
        this();
        isSubscription = in.readInt() != 0;
        personalData = in.readParcelable(personalData.getClass().getClassLoader());
        int size = in.readInt();
        for(int i = 0; i < size; i++){
            int key = in.readInt();
            User value = in.readParcelable(User.class.getClassLoader());
            subscriptions.put(key,value);
        }
        size = in.readInt();
        for(int i = 0; i < size; i++){
            int key = in.readInt();
            User value = in.readParcelable(User.class.getClassLoader());
            subscribers.put(key,value);
        }
        size = in.readInt();
        for(int i = 0; i < size; i++){
            int key = in.readInt();
            Score value = in.readParcelable(Score.class.getClassLoader());
            ownedScores.put(key,value);
        }
        size = in.readInt();
        for(int i = 0; i < size; i++){
            int key = in.readInt();
            Score value = in.readParcelable(Score.class.getClassLoader());
            favouriteScores.put(key,value);
        }
    }

    public int getId() {
        return personalData.getId();
    }

    public void setId(int id) {
        personalData.setId(id);
    }

    public String getUsername() {
        return personalData.getUsername();
    }

    public void setUsername(String username) {
        personalData.setUsername(username);
    }

    public String getFirstName() {
        return personalData.getFirstName();
    }

    public void setFirstName(String firstName) {
        personalData.setFirstName(firstName);
    }

    public String getSurname() {
        return personalData.getSurname();
    }

    public void setSurname(String surname) {
        personalData.setSurname(surname);
    }

    public String getEmail() {
        return personalData.getEmail();
    }

    public void setEmail(String email) {
        personalData.setEmail(email);
    }

    public String getPhoto() {
        return personalData.getPhoto();
    }

    public void setPhoto(String photo) {
        personalData.setPhoto(photo);
    }

    public String getMessage() {
        return personalData.getMessage();
    }

    public void setMessage(String message) {
        personalData.setMessage(message);
    }

    public int getNbSubscriptions() {
        return personalData.getNbSubscriptions();
    }

    public void setNbSubscriptions(int nbSubscriptions) {
        personalData.setNbSubscriptions(nbSubscriptions);
    }

    public int getNbSubscribers() {
        return personalData.getNbSubscribers();
    }

    public void setNbSubscribers(int nbSubscribers) {
        personalData.setNbSubscribers(nbSubscribers);
    }

    public int getNbScores() {
        return personalData.getNbScores();
    }

    public void setNbScores(int nbScores) {
        personalData.setNbScores(nbScores);
    }

    public int getNbFavourites() {
        return personalData.getNbFavourites();
    }

    public void setNbFavourites(int nbFavourites) {
        personalData.setNbFavourites(nbFavourites);
    }

    public boolean isSubscription() {
        return isSubscription;
    }

    public void setIsSubscription(boolean isSubscription) {
        this.isSubscription = isSubscription;
    }

    public Map<Integer, User> getSubscriptions() {
        return subscriptions;
    }

    public boolean isASubscription(int id) {
        return subscriptions.containsKey(id);
    }

    public User getSubscription(int id) {
        return subscriptions.get(id);
    }

    public void addSubscription(User user) {
        subscriptions.put(user.getId(), user);
    }

    public void removeSubscription(int id) {
        subscriptions.remove(id);
    }

    public Map<Integer, User> getSubscribers() {
        return subscribers;
    }

    public boolean isASubscriber(int id) {
        return subscribers.containsKey(id);
    }

    public User getSubscriber(int id) {
        return subscribers.get(id);
    }

    public void addSubscriber(User user) {
        subscribers.put(user.getId(), user);
    }

    public void removeSubscriber(int id) {
        subscribers.remove(id);
    }

    public Map<Integer, Score> getOwnedScores() {
        return ownedScores;
    }

    public boolean isAOwnedScore(int id) {
        return ownedScores.containsKey(id);
    }

    public Score getOwnedScore(int id) {
        return ownedScores.get(id);
    }

    public void addOwnedScore(Score score) {
        ownedScores.put(score.getId(), score);
    }

    public void removeOwnedScore(int id) {
        ownedScores.remove(id);
    }

    public Map<Integer, Score> getFavouriteScores() {
        return favouriteScores;
    }


    public boolean isAFavouriteScore(int id) {
        return favouriteScores.containsKey(id);
    }

    public Score getFavouriteScore(int id) {
        return favouriteScores.get(id);
    }

    public void addFavouriteScore(Score score) {
        favouriteScores.put(score.getId(), score);
    }

    public void removeFavouriteScore(int id) {
        favouriteScores.remove(id);
    }

    public static User fromJson(JSONObject jsonUser) throws JSONException {
        User user = new User();

        user.setIsSubscription(jsonUser.getBoolean("is_subscription"));

        // Get parent objects
        JSONObject jsonPersonalData = jsonUser.getJSONObject("personal_data");
        JSONArray jsonSubscriptions = jsonUser.getJSONArray("subscription");
        JSONArray jsonSubscribers = jsonUser.getJSONArray("subscriber");
        JSONObject jsonScores = jsonUser.getJSONObject("score");
        JSONArray jsonOwnedScores = jsonScores.getJSONArray("own");
        JSONArray jsonFavouriteScores = jsonScores.getJSONArray("favourite");

        // Set personal data
        user.personalData = UserPersonalData.fromJson(jsonPersonalData);

        // Set subscriptions
        for (int i = 0; i < jsonSubscriptions.length(); i++) {
            JSONObject jsonUserSubscription = jsonSubscriptions.getJSONObject(i);
            User newSub = new User(jsonUserSubscription.getInt("id"),
                    jsonUserSubscription.getString("username"),
                    jsonUserSubscription.getString("photo"),
                    jsonUserSubscription.getBoolean("is_subscription"));
            newSub.setNbSubscribers(jsonUserSubscription.getInt("nb_subscribers"));
            newSub.setNbScores(jsonUserSubscription.getInt("nb_scores"));
            user.addSubscription(newSub);
        }

        // Set subscribers
        for (int i = 0; i < jsonSubscribers.length(); i++) {
            JSONObject jsonUserSubscriber = jsonSubscribers.getJSONObject(i);
            User newSub = new User(jsonUserSubscriber.getInt("id"),
                    jsonUserSubscriber.getString("username"),
                    jsonUserSubscriber.getString("photo"),
                    jsonUserSubscriber.getBoolean("is_subscription"));
            newSub.setNbSubscribers(jsonUserSubscriber.getInt("nb_subscribers"));
            newSub.setNbScores(jsonUserSubscriber.getInt("nb_scores"));
            user.addSubscriber(newSub);
        }

        // Set owned scores
        for (int i = 0; i < jsonOwnedScores.length(); i++) {
            JSONObject jsonOwnedScore = jsonOwnedScores.getJSONObject(i);
            JSONObject jsonOwnedScoreAuthor = jsonOwnedScore.getJSONObject("by");
            Score newScore = new Score(jsonOwnedScore.getInt("id"),
                    jsonOwnedScore.getString("title"),
                    new User(jsonOwnedScoreAuthor.getInt("id"), jsonOwnedScoreAuthor.getString("username")),
                    jsonOwnedScore.getString("location_project"),
                    jsonOwnedScore.getString("location_preview"),
                    jsonOwnedScore.getBoolean("is_favourite"));
            newScore.setNbFavourite(jsonOwnedScore.getInt("nb_favourites"));
            user.addOwnedScore(newScore);
        }

        // Set favourite scores
        for (int i = 0; i < jsonFavouriteScores.length(); i++) {
            JSONObject jsonFavouriteScore = jsonFavouriteScores.getJSONObject(i);
            JSONObject jsonFavouriteScoreAuthor = jsonFavouriteScore.getJSONObject("by");
            Score newScore = new Score(jsonFavouriteScore.getInt("id"),
                    jsonFavouriteScore.getString("title"),
                    new User(jsonFavouriteScoreAuthor.getInt("id"), jsonFavouriteScoreAuthor.getString("username")),
                    jsonFavouriteScore.getString("location_project"),
                    jsonFavouriteScore.getString("location_preview"),
                    jsonFavouriteScore.getBoolean("is_favourite"));
            newScore.setNbFavourite(jsonFavouriteScore.getInt("nb_favourites"));
            user.addFavouriteScore(newScore);
        }
        return user;
    }

    @Override
    public String toString() {
        return getUsername();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt((byte) (isSubscription ? 1 : 0));
        dest.writeParcelable(personalData, 0);
        dest.writeInt(subscriptions.size());
        for(Map.Entry<Integer,User> entry : subscriptions.entrySet()){
            dest.writeInt(entry.getKey());
            dest.writeParcelable(entry.getValue(), 0);
        }
        dest.writeInt(subscribers.size());
        for(Map.Entry<Integer,User> entry : subscribers.entrySet()){
            dest.writeInt(entry.getKey());
            dest.writeParcelable(entry.getValue(), 0);
        }
        dest.writeInt(ownedScores.size());
        for(Map.Entry<Integer,Score> entry : ownedScores.entrySet()){
            dest.writeInt(entry.getKey());
            dest.writeParcelable(entry.getValue(), 0);
        }
        dest.writeInt(favouriteScores.size());
        for(Map.Entry<Integer,Score> entry : favouriteScores.entrySet()){
            dest.writeInt(entry.getKey());
            dest.writeParcelable(entry.getValue(), 0);
        }
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
