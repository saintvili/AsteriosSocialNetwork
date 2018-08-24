package ru.asterios.open.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import ru.asterios.open.constants.Constants;


public class Guest extends Application implements Constants, Parcelable {

    private long id, guestTo, guestUserId;

    private int verify, vip;

    private String guestUserUsername, guestUserFullname, guestUserPhoto, timeAgo;

    private Boolean online = false;

    public Guest() {


    }

    public Guest(JSONObject jsonData) {

        try {

            if (!jsonData.getBoolean("error")) {

                this.setId(jsonData.getLong("id"));
                this.setGuestUserId(jsonData.getLong("guestUserId"));
                this.setGuestUserVerify(jsonData.getInt("guestUserVerify"));
                this.setGuestUserUsername(jsonData.getString("guestUserUsername"));
                this.setGuestUserFullname(jsonData.getString("guestUserFullname"));
                this.setGuestUserPhotoUrl(jsonData.getString("guestUserPhoto"));
                this.setGuestTo(jsonData.getLong("guestTo"));
                this.setTimeAgo(jsonData.getString("timeAgo"));
                this.setOnline(jsonData.getBoolean("guestUserOnline"));
            }

        } catch (Throwable t) {

            Log.e("Guest", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("Guest", jsonData.toString());
        }
    }

    public void setId(long id) {

        this.id = id;
    }

    public long getId() {

        return this.id;
    }

    public void setGuestTo(long guestTo) {

        this.guestTo = guestTo;
    }

    public long getGuestTo() {

        return this.guestTo;
    }

    public void setGuestUserId(long guestUserId) {

        this.guestUserId = guestUserId;
    }

    public long getGuestUserId() {

        return this.guestUserId;
    }

    public void setGuestUserVip(int guestUserVip) {

        this.vip = guestUserVip;
    }

    public int getGuestUserVip() {

        return this.vip;
    }

    public void setGuestUserVerify(int guestUserVerify) {

        this.verify = guestUserVerify;
    }

    public int getGusetUserVerify() {

        return this.verify;
    }

    public Boolean isVerify() {

        if (this.verify > 0) {

            return true;
        }

        return false;
    }

    public void setGuestUserUsername(String guestUserUsername) {

        this.guestUserUsername = guestUserUsername;
    }

    public String getGuestUserUsername() {

        return this.guestUserUsername;
    }

    public void setGuestUserFullname(String guestUserFullname) {

        this.guestUserFullname = guestUserFullname;
    }

    public String getGuestUserFullname() {

        return this.guestUserFullname;
    }

    public void setGuestUserPhotoUrl(String guestUserPhoto) {

        this.guestUserPhoto = guestUserPhoto;
    }

    public String getGuestUserPhotoUrl() {

        return this.guestUserPhoto;
    }

    public void setTimeAgo(String ago) {

        this.timeAgo = ago;
    }

    public String getTimeAgo() {

        return this.timeAgo;
    }

    public void setOnline(Boolean online) {

        this.online = online;
    }

    public Boolean isOnline() {

        return this.online;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.guestTo);
        dest.writeLong(this.guestUserId);
        dest.writeInt(this.verify);
        dest.writeInt(this.vip);
        dest.writeString(this.guestUserUsername);
        dest.writeString(this.guestUserFullname);
        dest.writeString(this.guestUserPhoto);
        dest.writeString(this.timeAgo);
        dest.writeValue(this.online);
    }

    protected Guest(Parcel in) {
        this.id = in.readLong();
        this.guestTo = in.readLong();
        this.guestUserId = in.readLong();
        this.verify = in.readInt();
        this.vip = in.readInt();
        this.guestUserUsername = in.readString();
        this.guestUserFullname = in.readString();
        this.guestUserPhoto = in.readString();
        this.timeAgo = in.readString();
        this.online = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<Guest> CREATOR = new Creator<Guest>() {
        @Override
        public Guest createFromParcel(Parcel source) {
            return new Guest(source);
        }

        @Override
        public Guest[] newArray(int size) {
            return new Guest[size];
        }
    };
}
