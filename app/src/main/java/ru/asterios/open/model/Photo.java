package ru.asterios.open.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import ru.asterios.open.constants.Constants;


public class Photo extends Application implements Constants, Parcelable {

    private long id, fromUserId;
    private int createAt, likesCount, commentsCount, accessMode, fromUserVerify;
    private String timeAgo, date, comment, imgUrl, previewImgUrl, originImgUrl, fromUserUsername, fromUserFullname, fromUserPhotoUrl, area, country, city;
    private Double lat = 0.000000, lng = 0.000000;
    private Boolean myLike = false;

    public Photo() {

    }

    public Photo(JSONObject jsonData) {

        try {

            if (!jsonData.getBoolean("error")) {

                this.setId(jsonData.getLong("id"));
                this.setFromUserId(jsonData.getLong("fromUserId"));
                this.setAccessMode(jsonData.getInt("accessMode"));
                this.setFromUserVerify(jsonData.getInt("fromUserVerify"));
                this.setFromUserUsername(jsonData.getString("fromUserUsername"));
                this.setFromUserFullname(jsonData.getString("fromUserFullname"));
                this.setFromUserPhotoUrl(jsonData.getString("fromUserPhoto"));
                this.setComment(jsonData.getString("comment"));
                this.setImgUrl(jsonData.getString("imgUrl"));
                this.setPreviewImgUrl(jsonData.getString("previewImgUrl"));
                this.setOriginImgUrl(jsonData.getString("originImgUrl"));
                this.setArea(jsonData.getString("area"));
                this.setCountry(jsonData.getString("country"));
                this.setCity(jsonData.getString("city"));
                this.setCommentsCount(jsonData.getInt("commentsCount"));
                this.setLikesCount(jsonData.getInt("likesCount"));
                this.setLat(jsonData.getDouble("lat"));
                this.setLng(jsonData.getDouble("lng"));
                this.setCreateAt(jsonData.getInt("createAt"));
                this.setDate(jsonData.getString("date"));
                this.setTimeAgo(jsonData.getString("timeAgo"));
            }

        } catch (Throwable t) {

            Log.e("Photo", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("Photo", jsonData.toString());
        }
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFromUserId() {

        return fromUserId;
    }

    public void setFromUserId(long fromUserId) {

        this.fromUserId = fromUserId;
    }

    public int getAccessMode() {

        return accessMode;
    }

    public void setAccessMode(int accessMode) {

        this.accessMode = accessMode;
    }

    public int getFromUserVerify() {

        return fromUserVerify;
    }

    public void setFromUserVerify(int fromUserVerify) {

        this.fromUserVerify = fromUserVerify;
    }

    public int getCommentsCount() {

        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getLikesCount() {

        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
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

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public String getFromUserUsername() {
        return fromUserUsername;
    }

    public void setFromUserUsername(String fromUserUsername) {
        this.fromUserUsername = fromUserUsername;
    }

    public String getFromUserFullname() {
        return fromUserFullname;
    }

    public void setFromUserFullname(String fromUserFullname) {
        this.fromUserFullname = fromUserFullname;
    }

    public String getFromUserPhotoUrl() {

        if (fromUserPhotoUrl == null) {

            fromUserPhotoUrl = "";
        }

        return fromUserPhotoUrl;
    }

    public void setFromUserPhotoUrl(String fromUserPhotoUrl) {
        this.fromUserPhotoUrl = fromUserPhotoUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPreviewImgUrl() {
        return previewImgUrl;
    }

    public void setPreviewImgUrl(String previewImgUrl) {
        this.previewImgUrl = previewImgUrl;
    }

    public String getOriginImgUrl() {
        return originImgUrl;
    }

    public void setOriginImgUrl(String originImgUrl) {
        this.originImgUrl = originImgUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getArea() {

        if (this.area == null) {

            this.area = "";
        }

        return this.area;
    }

    public void setArea(String area) {

        this.area = area;
    }

    public String getCountry() {

        if (this.country == null) {

            this.country = "";
        }

        return this.country;
    }

    public void setCountry(String country) {

        this.country = country;
    }

    public String getCity() {

        if (this.city == null) {

            this.city = "";
        }

        return this.city;
    }

    public void setCity(String city) {

        this.city = city;
    }

    public Double getLat() {

        return this.lat;
    }

    public void setLat(Double lat) {

        this.lat = lat;
    }

    public Double getLng() {

        return this.lng;
    }

    public void setLng(Double lng) {

        this.lng = lng;
    }

    public String getLink() {

        return WEB_SITE + this.getFromUserUsername() + "/post/" + Long.toString(this.getId());
    }

    public Boolean isMyLike() {
        return myLike;
    }

    public void setMyLike(Boolean myLike) {

        this.myLike = myLike;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.fromUserId);
        dest.writeInt(this.createAt);
        dest.writeInt(this.likesCount);
        dest.writeInt(this.commentsCount);
        dest.writeInt(this.accessMode);
        dest.writeInt(this.fromUserVerify);
        dest.writeString(this.timeAgo);
        dest.writeString(this.date);
        dest.writeString(this.comment);
        dest.writeString(this.imgUrl);
        dest.writeString(this.previewImgUrl);
        dest.writeString(this.originImgUrl);
        dest.writeString(this.fromUserUsername);
        dest.writeString(this.fromUserFullname);
        dest.writeString(this.fromUserPhotoUrl);
        dest.writeString(this.area);
        dest.writeString(this.country);
        dest.writeString(this.city);
        dest.writeValue(this.lat);
        dest.writeValue(this.lng);
        dest.writeValue(this.myLike);
    }

    protected Photo(Parcel in) {
        this.id = in.readLong();
        this.fromUserId = in.readLong();
        this.createAt = in.readInt();
        this.likesCount = in.readInt();
        this.commentsCount = in.readInt();
        this.accessMode = in.readInt();
        this.fromUserVerify = in.readInt();
        this.timeAgo = in.readString();
        this.date = in.readString();
        this.comment = in.readString();
        this.imgUrl = in.readString();
        this.previewImgUrl = in.readString();
        this.originImgUrl = in.readString();
        this.fromUserUsername = in.readString();
        this.fromUserFullname = in.readString();
        this.fromUserPhotoUrl = in.readString();
        this.area = in.readString();
        this.country = in.readString();
        this.city = in.readString();
        this.lat = (Double) in.readValue(Double.class.getClassLoader());
        this.lng = (Double) in.readValue(Double.class.getClassLoader());
        this.myLike = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
