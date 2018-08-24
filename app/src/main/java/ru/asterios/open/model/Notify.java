package ru.asterios.open.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import ru.asterios.open.constants.Constants;

public class Notify extends Application implements Constants, Parcelable {

    private long id, itemId, fromUserId;
    private int fromUserState, createAt, type;
    private String fromUserUsername, fromUserFullname, fromUserPhotoUrl, timeAgo;

    public Notify() {

    }

    public Notify(JSONObject jsonData) {

        try {

            this.setId(jsonData.getLong("id"));
            this.setType(jsonData.getInt("type"));
            this.setItemId(jsonData.getLong("postId"));
            this.setFromUserId(jsonData.getLong("fromUserId"));
            this.setFromUserState(jsonData.getInt("fromUserState"));
            this.setFromUserUsername(jsonData.getString("fromUserUsername"));
            this.setFromUserFullname(jsonData.getString("fromUserFullname"));
            this.setFromUserPhotoUrl(jsonData.getString("fromUserPhotoUrl"));
            this.setTimeAgo(jsonData.getString("timeAgo"));
            this.setCreateAt(jsonData.getInt("createAt"));

        } catch (Throwable t) {

            Log.e("Notify", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("Notify", jsonData.toString());
        }
    }

    public void setId(long id) {

        this.id = id;
    }

    public long getId() {

        return this.id;
    }

    public void setType(int type) {

        this.type = type;
    }

    public int getType() {

        return this.type;
    }

    public void setItemId(long itemId) {

        this.itemId = itemId;
    }

    public long getItemId() {

        return this.itemId;
    }

    public void setFromUserId(long fromUserId) {

        this.fromUserId = fromUserId;
    }

    public long getFromUserId() {

        return this.fromUserId;
    }

    public void setFromUserState(int fromUserState) {

        this.fromUserState = fromUserState;
    }

    public int getFromUserState() {

        return this.fromUserState;
    }

    public void setTimeAgo(String timeAgo) {

        this.timeAgo = timeAgo;
    }

    public String getTimeAgo() {

        return this.timeAgo;
    }

    public void setFromUserUsername(String fromUserUsername) {

        this.fromUserUsername = fromUserUsername;
    }

    public String getFromUserUsername() {

        return this.fromUserUsername;
    }

    public void setFromUserFullname(String fromUserFullname) {

        this.fromUserFullname = fromUserFullname;
    }

    public String getFromUserFullname() {

        return this.fromUserFullname;
    }

    public void setFromUserPhotoUrl(String fromUserPhotoUrl) {

        this.fromUserPhotoUrl = fromUserPhotoUrl;
    }

    public String getFromUserPhotoUrl() {

        return this.fromUserPhotoUrl;
    }

    public void setCreateAt(int createAt) {

        this.createAt = createAt;
    }

    public int getCreateAt() {

        return this.createAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.itemId);
        dest.writeLong(this.fromUserId);
        dest.writeInt(this.fromUserState);
        dest.writeInt(this.createAt);
        dest.writeInt(this.type);
        dest.writeString(this.fromUserUsername);
        dest.writeString(this.fromUserFullname);
        dest.writeString(this.fromUserPhotoUrl);
        dest.writeString(this.timeAgo);
    }

    protected Notify(Parcel in) {
        this.id = in.readLong();
        this.itemId = in.readLong();
        this.fromUserId = in.readLong();
        this.fromUserState = in.readInt();
        this.createAt = in.readInt();
        this.type = in.readInt();
        this.fromUserUsername = in.readString();
        this.fromUserFullname = in.readString();
        this.fromUserPhotoUrl = in.readString();
        this.timeAgo = in.readString();
    }

    public static final Creator<Notify> CREATOR = new Creator<Notify>() {
        @Override
        public Notify createFromParcel(Parcel source) {
            return new Notify(source);
        }

        @Override
        public Notify[] newArray(int size) {
            return new Notify[size];
        }
    };
}
