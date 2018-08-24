package ru.asterios.open.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import ru.asterios.open.constants.Constants;

public class MarketItem extends Application implements Constants, Parcelable {

    private long id, fromUserId;
    private int createAt, fromUserVerified, likesCount, commentsCount, allowComments, price;
    private String timeAgo, date, itemTitle, itemDescription, itemContent, imgUrl, fromUserUsername, fromUserFullname, fromUserPhotoUrl, area, country, city;
    private Double lat = 0.000000, lng = 0.000000;

    public MarketItem() {

    }

    public MarketItem(JSONObject jsonData) {

        try {

            if (!jsonData.getBoolean("error")) {

                this.setId(jsonData.getLong("id"));
                this.setFromUserId(jsonData.getLong("fromUserId"));
                this.setFromUserUsername(jsonData.getString("fromUserUsername"));
                this.setFromUserFullname(jsonData.getString("fromUserFullname"));
                this.setFromUserPhotoUrl(jsonData.getString("fromUserPhoto"));
                this.setFromUserVerified(jsonData.getInt("fromUserVerified"));
                this.setContent(jsonData.getString("itemContent"));
                this.setTitle(jsonData.getString("itemTitle"));
                this.setDescription(jsonData.getString("itemDesc"));
                this.setPrice(jsonData.getInt("price"));
                this.setImgUrl(jsonData.getString("imgUrl"));
                this.setArea(jsonData.getString("area"));
                this.setCountry(jsonData.getString("country"));
                this.setCity(jsonData.getString("city"));
                this.setAllowComments(jsonData.getInt("allowComments"));
                this.setCommentsCount(jsonData.getInt("commentsCount"));
                this.setLikesCount(jsonData.getInt("likesCount"));
                this.setLat(jsonData.getDouble("lat"));
                this.setLng(jsonData.getDouble("lng"));
                this.setCreateAt(jsonData.getInt("createAt"));
                this.setDate(jsonData.getString("date"));
                this.setTimeAgo(jsonData.getString("timeAgo"));

            }

        } catch (Throwable t) {

            Log.e("MarketItem", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("MarketItem", jsonData.toString());
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

    public int getFromUserVerified() {

        return this.fromUserVerified;
    }

    public void setFromUserVerified(int fromUserVerified) {

        this.fromUserVerified = fromUserVerified;
    }

    public int getAllowComments() {

        return allowComments;
    }

    public void setAllowComments(int allowComments) {
        this.allowComments = allowComments;
    }


    public int getCommentsCount() {

        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getLikesCount() {

        return this.likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getPrice() {

        return this.price;
    }

    public void setPrice(int price) {

        this.price = price;
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


    public String getContent() {

        return itemContent;
    }

    public void setContent(String itemContent) {

        this.itemContent = itemContent;
    }

    public String getTitle() {

        if (this.itemTitle == null) {

            this.itemTitle = "";
        }

        return this.itemTitle;
    }

    public void setTitle(String itemTitle) {

        this.itemTitle = itemTitle;
    }

    public String getDescription() {

        if (this.itemDescription == null) {

            this.itemDescription = "";
        }

        return this.itemDescription;
    }

    public void setDescription(String itemDescription) {

        this.itemDescription = itemDescription;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.fromUserId);
        dest.writeInt(this.createAt);
        dest.writeInt(this.fromUserVerified);
        dest.writeInt(this.likesCount);
        dest.writeInt(this.commentsCount);
        dest.writeInt(this.allowComments);
        dest.writeInt(this.price);
        dest.writeString(this.timeAgo);
        dest.writeString(this.date);
        dest.writeString(this.itemTitle);
        dest.writeString(this.itemDescription);
        dest.writeString(this.itemContent);
        dest.writeString(this.imgUrl);
        dest.writeString(this.fromUserUsername);
        dest.writeString(this.fromUserFullname);
        dest.writeString(this.fromUserPhotoUrl);
        dest.writeString(this.area);
        dest.writeString(this.country);
        dest.writeString(this.city);
        dest.writeValue(this.lat);
        dest.writeValue(this.lng);
    }

    protected MarketItem(Parcel in) {
        this.id = in.readLong();
        this.fromUserId = in.readLong();
        this.createAt = in.readInt();
        this.fromUserVerified = in.readInt();
        this.likesCount = in.readInt();
        this.commentsCount = in.readInt();
        this.allowComments = in.readInt();
        this.price = in.readInt();
        this.timeAgo = in.readString();
        this.date = in.readString();
        this.itemTitle = in.readString();
        this.itemDescription = in.readString();
        this.itemContent = in.readString();
        this.imgUrl = in.readString();
        this.fromUserUsername = in.readString();
        this.fromUserFullname = in.readString();
        this.fromUserPhotoUrl = in.readString();
        this.area = in.readString();
        this.country = in.readString();
        this.city = in.readString();
        this.lat = (Double) in.readValue(Double.class.getClassLoader());
        this.lng = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Creator<MarketItem> CREATOR = new Creator<MarketItem>() {
        @Override
        public MarketItem createFromParcel(Parcel source) {
            return new MarketItem(source);
        }

        @Override
        public MarketItem[] newArray(int size) {
            return new MarketItem[size];
        }
    };
}
