package ru.asterios.open.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import ru.asterios.open.constants.Constants;

public class BlacklistItem extends Application implements Constants, Parcelable {

    private long id, blockedUserId;
    private int blockedUserState, createAt, blockedUserVerify;
    private String blockedUserUsername, blockedUserFullname, blockedUserPhotoUrl, reason, timeAgo;

    public BlacklistItem() {

    }

    public BlacklistItem(JSONObject jsonData) {

        try {

            this.setId(jsonData.getLong("id"));
            this.setBlockedUserId(jsonData.getLong("blockedUserId"));
            this.setBlockedUserState(jsonData.getInt("blockedUserState"));
            this.setBlockedUserVerify(jsonData.getInt("blockedUserVerify"));
            this.setBlockedUserUsername(jsonData.getString("blockedUserUsername"));
            this.setBlockedUserFullname(jsonData.getString("blockedUserFullname"));
            this.setBlockedUserPhotoUrl(jsonData.getString("blockedUserPhotoUrl"));
            this.setReason(jsonData.getString("reason"));
            this.setTimeAgo(jsonData.getString("timeAgo"));
            this.setCreateAt(jsonData.getInt("createAt"));

        } catch (Throwable t) {

            Log.e("Blacklist Item", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("Blacklist Item", jsonData.toString());
        }
    }

    public void setId(long id) {

        this.id = id;
    }

    public long getId() {

        return this.id;
    }

    public void setBlockedUserId(long blockedUserId) {

        this.blockedUserId = blockedUserId;
    }

    public long getBlockedUserId() {

        return this.blockedUserId;
    }

    public void setBlockedUserState(int blockedUserState) {

        this.blockedUserState = blockedUserState;
    }

    public int getBlockedUserVerify() {

        return this.blockedUserVerify;
    }

    public void setBlockedUserVerify(int blockedUserVerify) {

        this.blockedUserVerify = blockedUserVerify;
    }

    public int getBlockedUserState() {

        return this.blockedUserState;
    }


    public void setReason(String reason) {

        this.reason = reason;
    }

    public String getReason() {

        return this.reason;
    }

    public void setTimeAgo(String timeAgo) {

        this.timeAgo = timeAgo;
    }

    public String getTimeAgo() {

        return this.timeAgo;
    }

    public void setBlockedUserUsername(String blockedUserUsername) {

        this.blockedUserUsername = blockedUserUsername;
    }

    public String getBlockedUserUsername() {

        return this.blockedUserUsername;
    }

    public void setBlockedUserFullname(String blockedUserFullname) {

        this.blockedUserFullname = blockedUserFullname;
    }

    public String getBlockedUserFullname() {

        return this.blockedUserFullname;
    }

    public void setBlockedUserPhotoUrl(String blockedUserPhotoUrl) {

        this.blockedUserPhotoUrl = blockedUserPhotoUrl;
    }

    public String getBlockedUserPhotoUrl() {

        return this.blockedUserPhotoUrl;
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

    }

    public static final Creator CREATOR = new Creator() {

        public BlacklistItem createFromParcel(Parcel in) {

            return new BlacklistItem();
        }

        public BlacklistItem[] newArray(int size) {
            return new BlacklistItem[size];
        }
    };
}
