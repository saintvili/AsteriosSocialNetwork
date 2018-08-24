package ru.asterios.open.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import ru.asterios.open.constants.Constants;


public class Gift extends Application implements Constants, Parcelable {

    private long id, giftFrom, giftTo;
    private int createAt, giftFromUserVip, giftFromUserVerify, giftAnonymous, giftId;
    private String timeAgo, date, message, imgUrl, giftFromUserUsername, giftFromUserFullname, giftFromUserPhoto;

    public Gift() {

    }

    public Gift(JSONObject jsonData) {

        try {

            if (!jsonData.getBoolean("error")) {

                this.setId(jsonData.getLong("id"));
                this.setGiftFromUserId(jsonData.getLong("giftFrom"));
                this.setGiftToUserId(jsonData.getLong("giftTo"));
                this.setGiftId(jsonData.getInt("giftId"));
                this.setGiftFromUserVerify(jsonData.getInt("giftFromUserVerify"));
                this.setGiftFromUserVip(jsonData.getInt("giftFromUserVerify"));
                this.setGiftAnonymous(jsonData.getInt("giftAnonymous"));

                this.setGiftFromUserUsername(jsonData.getString("giftFromUserUsername"));
                this.setGiftFromUserFullname(jsonData.getString("giftFromUserFullname"));
                this.setGiftFromUserPhotoUrl(jsonData.getString("giftFromUserPhoto"));

                this.setMessage(jsonData.getString("message"));
                this.setImgUrl(jsonData.getString("imgUrl"));
                this.setCreateAt(jsonData.getInt("createAt"));
                this.setDate(jsonData.getString("date"));
                this.setTimeAgo(jsonData.getString("timeAgo"));
            }

        } catch (Throwable t) {

            Log.e("Gift", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("Gift", jsonData.toString());
        }
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGiftFromUserId() {

        return giftFrom;
    }

    public void setGiftFromUserId(long giftFrom) {

        this.giftFrom = giftFrom;
    }

    public long getGiftToUserId() {

        return giftTo;
    }

    public void setGiftToUserId(long giftTo) {

        this.giftTo = giftTo;
    }

    public int getGiftAnonymous() {

        return giftAnonymous;
    }

    public void setGiftAnonymous(int giftAnonymous) {

        this.giftAnonymous = giftAnonymous;
    }

    public int getGiftId() {

        return giftId;
    }

    public void setGiftId(int giftId) {

        this.giftId = giftId;
    }

    public int getGiftFromUserVerify() {

        return giftFromUserVerify;
    }

    public void setGiftFromUserVerify(int giftFromUserVerify) {

        this.giftFromUserVerify = giftFromUserVerify;
    }

    public int getGiftFromUserVip() {

        return giftFromUserVip;
    }

    public void setGiftFromUserVip(int giftFromUserVip) {

        this.giftFromUserVip = giftFromUserVip;
    }

    public int getCreateAt() {

        return createAt;
    }

    public void setCreateAt(int createAt) {
        this.createAt = createAt;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public String getMessage() {

        if (this.message == null) {

            this.message = "";
        }

        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getGiftFromUserUsername() {
        return giftFromUserUsername;
    }

    public void setGiftFromUserUsername(String giftFromUserUsername) {

        this.giftFromUserUsername = giftFromUserUsername;
    }

    public String getGiftFromUserFullname() {

        return giftFromUserFullname;
    }

    public void setGiftFromUserFullname(String giftFromUserFullname) {

        this.giftFromUserFullname = giftFromUserFullname;
    }

    public String getGiftFromUserPhotoUrl() {

        if (this.giftFromUserPhoto == null) {

            this.giftFromUserPhoto = "";
        }

        return this.giftFromUserPhoto;
    }

    public void setGiftFromUserPhotoUrl(String giftFromUserPhoto) {

        this.giftFromUserPhoto = giftFromUserPhoto;
    }

    public String getImgUrl() {

        if (this.imgUrl == null) {

            this.imgUrl = "";
        }

        return this.imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.giftFrom);
        dest.writeLong(this.giftTo);
        dest.writeInt(this.createAt);
        dest.writeInt(this.giftFromUserVip);
        dest.writeInt(this.giftFromUserVerify);
        dest.writeInt(this.giftAnonymous);
        dest.writeInt(this.giftId);
        dest.writeString(this.timeAgo);
        dest.writeString(this.date);
        dest.writeString(this.message);
        dest.writeString(this.imgUrl);
        dest.writeString(this.giftFromUserUsername);
        dest.writeString(this.giftFromUserFullname);
        dest.writeString(this.giftFromUserPhoto);
    }

    protected Gift(Parcel in) {
        this.id = in.readLong();
        this.giftFrom = in.readLong();
        this.giftTo = in.readLong();
        this.createAt = in.readInt();
        this.giftFromUserVip = in.readInt();
        this.giftFromUserVerify = in.readInt();
        this.giftAnonymous = in.readInt();
        this.giftId = in.readInt();
        this.timeAgo = in.readString();
        this.date = in.readString();
        this.message = in.readString();
        this.imgUrl = in.readString();
        this.giftFromUserUsername = in.readString();
        this.giftFromUserFullname = in.readString();
        this.giftFromUserPhoto = in.readString();
    }

    public static final Creator<Gift> CREATOR = new Creator<Gift>() {
        @Override
        public Gift createFromParcel(Parcel source) {
            return new Gift(source);
        }

        @Override
        public Gift[] newArray(int size) {
            return new Gift[size];
        }
    };
}
