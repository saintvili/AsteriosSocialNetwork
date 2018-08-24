package ru.asterios.open.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import ru.asterios.open.constants.Constants;


public class Item extends Application implements Constants, Parcelable {

    private long id, fromUserId, rePostId, groupId, groupAuthor;
    private int createAt, likesCount, commentsCount, rePostsCount, allowComments, groupAllowComments, accessMode, fromUserVerify;
    private String timeAgo, date, post, imgUrl, fromUserUsername, fromUserFullname, fromUserPhotoUrl, area, country, city, youTubeVideoImg, youTubeVideoCode, youTubeVideoUrl;
    private Double lat = 0.000000, lng = 0.000000;
    private Boolean myLike, myRePost;

    private String urlPreviewTitle, urlPreviewImage, urlPreviewLink, urlPreviewDescription;

    private String previewVideoImgUrl, videoUrl;

    private String rePostTimeAgo, rePostDate, rePostPost, rePostImgUrl, rePostFromUserUsername, rePostFromUserFullname, rePostFromUserPhotoUrl;
    private int fromRePostUserVerify, rePostRemoveAt;
    private long rePostFromUserId;

    private String reVideoUrl;
    private String reYouTubeVideoImg, reYouTubeVideoCode, reYouTubeVideoUrl;
    private String reUrlPreviewTitle, reUrlPreviewImage, reUrlPreviewLink, reUrlPreviewDescription;

    private int ad = 0;

    public Item() {

    }

    public Item(JSONObject jsonData) {

        try {

            if (!jsonData.getBoolean("error")) {

                this.setId(jsonData.getLong("id"));
                this.setRePostId(jsonData.getLong("rePostId"));
                this.setGroupId(jsonData.getLong("groupId"));
                this.setFromUserId(jsonData.getLong("fromUserId"));
                this.setAccessMode(jsonData.getInt("accessMode"));
                this.setFromUserVerify(jsonData.getInt("fromUserVerify"));
                this.setFromUserUsername(jsonData.getString("fromUserUsername"));
                this.setFromUserFullname(jsonData.getString("fromUserFullname"));
                this.setFromUserPhotoUrl(jsonData.getString("fromUserPhoto"));
                this.setPost(jsonData.getString("post"));
                this.setImgUrl(jsonData.getString("imgUrl"));
                this.setArea(jsonData.getString("area"));
                this.setCountry(jsonData.getString("country"));
                this.setCity(jsonData.getString("city"));
                this.setAllowComments(jsonData.getInt("allowComments"));
                this.setGroupAllowComments(jsonData.getInt("groupAllowComments"));
                this.setGroupAuthor(jsonData.getLong("groupAuthor"));
                this.setCommentsCount(jsonData.getInt("commentsCount"));
                this.setLikesCount(jsonData.getInt("likesCount"));
                this.setRePostsCount(jsonData.getInt("rePostsCount"));
                this.setMyLike(jsonData.getBoolean("myLike"));
                this.setMyRePost(jsonData.getBoolean("myRePost"));
                this.setLat(jsonData.getDouble("lat"));
                this.setLng(jsonData.getDouble("lng"));
                this.setCreateAt(jsonData.getInt("createAt"));
                this.setDate(jsonData.getString("date"));
                this.setTimeAgo(jsonData.getString("timeAgo"));

                this.setYouTubeVideoImg(jsonData.getString("YouTubeVideoImg"));
                this.setYouTubeVideoCode(jsonData.getString("YouTubeVideoCode"));
                this.setYouTubeVideoUrl(jsonData.getString("YouTubeVideoUrl"));

                this.setUrlPreviewTitle(jsonData.getString("urlPreviewTitle"));
                this.setUrlPreviewImage(jsonData.getString("urlPreviewImage"));
                this.setUrlPreviewLink(jsonData.getString("urlPreviewLink"));
                this.setUrlPreviewDescription(jsonData.getString("urlPreviewDescription"));

                this.setVideoUrl(jsonData.getString("videoUrl"));
                this.setPreviewVideoImgUrl(jsonData.getString("previewVideoImgUrl"));

                if (jsonData.has("rePost")) {

                    JSONArray rePostArray = jsonData.getJSONArray("rePost");

                    if (rePostArray.length() > 0) {

                        JSONObject rePostObj = (JSONObject) rePostArray.get(0);

                        this.setRePostFromUserId(jsonData.getLong("fromUserId"));
                        this.setRePostFromUserFullname(rePostObj.getString("fromUserFullname"));
                        this.setRePostFromUserUsername(rePostObj.getString("fromUserUsername"));
                        this.setRePostFromUserPhotoUrl(rePostObj.getString("fromUserPhoto"));
                        this.setRePostPost(rePostObj.getString("post"));
                        this.setRePostImgUrl(rePostObj.getString("imgUrl"));
                        this.setRePostTimeAgo(rePostObj.getString("timeAgo"));
                        this.setRePostFromUserVerify(rePostObj.getInt("fromUserVerify"));
                        this.setRePostFromUserId(rePostObj.getLong("fromUserId"));
                        this.setRePostRemoveAt(rePostObj.getInt("removeAt"));

                        if (rePostObj.has("videoUrl")) this.setReVideoUrl(rePostObj.getString("videoUrl"));

                        if (rePostObj.has("YouTubeVideoImg")) this.setReYouTubeVideoImg(rePostObj.getString("YouTubeVideoImg"));
                        if (rePostObj.has("YouTubeVideoCode")) this.setReYouTubeVideoCode(rePostObj.getString("YouTubeVideoCode"));
                        if (rePostObj.has("YouTubeVideoUrl")) this.setReYouTubeVideoUrl(rePostObj.getString("YouTubeVideoUrl"));

                        if (rePostObj.has("urlPreviewTitle")) this.setReUrlPreviewTitle(rePostObj.getString("urlPreviewTitle"));
                        if (rePostObj.has("urlPreviewImage")) this.setReUrlPreviewImage(rePostObj.getString("urlPreviewImage"));
                        if (rePostObj.has("urlPreviewLink")) this.setReUrlPreviewLink(rePostObj.getString("urlPreviewLink"));
                        if (rePostObj.has("urlPreviewDescription")) this.setReUrlPreviewDescription(rePostObj.getString("urlPreviewDescription"));
                    }
                }
            }

        } catch (Throwable t) {

            Log.e("Item", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("Item", jsonData.toString());
        }
    }

    public int getAd() {

        return this.ad;
    }

    public void setAd(int ad) {

        this.ad = ad;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRePostId() {

        return this.rePostId;
    }

    public void setRePostId(long rePostId) {

        this.rePostId = rePostId;
    }

    public long getFromUserId() {

        return fromUserId;
    }

    public void setFromUserId(long fromUserId) {

        this.fromUserId = fromUserId;
    }

    public long getGroupId() {

        return this.groupId;
    }

    public void setGroupId(long groupId) {

        this.groupId = groupId;
    }

    public long getGroupAuthor() {

        return this.groupAuthor;
    }

    public void setGroupAuthor(long groupAuthor) {

        this.groupAuthor = groupAuthor;
    }

    public long getRePostFromUserId() {

        return rePostFromUserId;
    }

    public void setRePostFromUserId(long rePostFromUserId) {

        this.rePostFromUserId = rePostFromUserId;
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

    public int getRePostFromUserVerify() {

        return fromRePostUserVerify;
    }

    public void setRePostFromUserVerify(int fromRePostUserVerify) {

        this.fromRePostUserVerify = fromRePostUserVerify;
    }

    public int getRePostRemoveAt() {

        return rePostRemoveAt;
    }

    public void setRePostRemoveAt(int rePostRemoveAt) {

        this.rePostRemoveAt = rePostRemoveAt;
    }

    public int getAllowComments() {

        return allowComments;
    }

    public void setAllowComments(int allowComments) {
        this.allowComments = allowComments;
    }

    public int getGroupAllowComments() {

        return this.groupAllowComments;
    }

    public void setGroupAllowComments(int groupAllowComments) {
        this.groupAllowComments = groupAllowComments;
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

    public int getRePostsCount() {

        return this.rePostsCount;
    }

    public void setRePostsCount(int rePostsCount) {
        this.rePostsCount = rePostsCount;
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

    public String getYouTubeVideoImg() {

        if (this.youTubeVideoImg == null) {

            this.youTubeVideoImg = "";
        }

        return this.youTubeVideoImg;
    }

    public void setYouTubeVideoImg(String youTubeVideoImg) {

        this.youTubeVideoImg = youTubeVideoImg;
    }

    public String getYouTubeVideoCode() {

        if (this.youTubeVideoCode == null) {

            this.youTubeVideoCode = "";
        }

        return this.youTubeVideoCode;
    }

    public void setYouTubeVideoCode(String youTubeVideoCode) {

        this.youTubeVideoCode = youTubeVideoCode;
    }

    public String getYouTubeVideoUrl() {

        if (this.youTubeVideoUrl == null) {

            this.youTubeVideoUrl = "";
        }

        return this.youTubeVideoUrl;
    }

    public void setYouTubeVideoUrl(String youTubeVideoUrl) {

        this.youTubeVideoUrl = youTubeVideoUrl;
    }




    public String getVideoUrl() {

        if (this.videoUrl == null) {

            this.videoUrl = "";
        }

        return this.videoUrl;
    }

    public void setVideoUrl(String videoUrl) {

        this.videoUrl = videoUrl;
    }

    public String getPreviewVideoImgUrl() {

        if (this.previewVideoImgUrl == null) {

            this.previewVideoImgUrl = "";
        }

        return this.previewVideoImgUrl;
    }

    public void setPreviewVideoImgUrl(String previewVideoImgUrl) {

        this.previewVideoImgUrl = previewVideoImgUrl;
    }




    public String getUrlPreviewTitle() {

        return urlPreviewTitle;
    }

    public void setUrlPreviewTitle(String urlPreviewTitle) {

        this.urlPreviewTitle = urlPreviewTitle;
    }

    public String getUrlPreviewImage() {

        if (this.urlPreviewImage == null) {

            this.urlPreviewImage = "";
        }

        return this.urlPreviewImage;
    }

    public void setUrlPreviewImage(String urlPreviewImage) {

        this.urlPreviewImage = urlPreviewImage;
    }

    public String getUrlPreviewLink() {

        if (this.urlPreviewLink == null) {

            this.urlPreviewLink = "";
        }

        return this.urlPreviewLink;
    }

    public void setUrlPreviewLink(String urlPreviewLink) {

        this.urlPreviewLink = urlPreviewLink;
    }

    public String getUrlPreviewDescription() {

        if (this.urlPreviewDescription == null) {

            this.urlPreviewDescription = "";
        }

        return this.urlPreviewDescription;
    }

    public void setUrlPreviewDescription(String urlPreviewDescription) {

        this.urlPreviewDescription = urlPreviewDescription;
    }

    public void setRePostTimeAgo(String rePostTimeAgo) {
        this.rePostTimeAgo = rePostTimeAgo;
    }

    public String getRePostTimeAgo() {
        return rePostTimeAgo;
    }

    public String getPost() {

        if (this.post == null) {

            this.post = "";
        }

        return this.post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getRePostPost() {
        return rePostPost;
    }

    public void setRePostPost(String rePostPost) {
        this.rePostPost = rePostPost;
    }


    public String getFromUserUsername() {
        return fromUserUsername;
    }

    public void setFromUserUsername(String fromUserUsername) {
        this.fromUserUsername = fromUserUsername;
    }

    public String getRePostFromUserUsername() {
        return rePostFromUserUsername;
    }

    public void setRePostFromUserUsername(String rePostFromUserUsername) {
        this.rePostFromUserUsername = rePostFromUserUsername;
    }

    public String getFromUserFullname() {
        return fromUserFullname;
    }

    public void setFromUserFullname(String fromUserFullname) {
        this.fromUserFullname = fromUserFullname;
    }

    public String getRePostFromUserFullname() {
        return rePostFromUserFullname;
    }

    public void setRePostFromUserFullname(String rePostFromUserFullname) {
        this.rePostFromUserFullname = rePostFromUserFullname;
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

    public String getRePostFromUserPhotoUrl() {

        if (rePostFromUserPhotoUrl == null) {

            rePostFromUserPhotoUrl = "";
        }

        return rePostFromUserPhotoUrl;
    }

    public void setRePostFromUserPhotoUrl(String rePostFromUserPhotoUrl) {
        this.rePostFromUserPhotoUrl = rePostFromUserPhotoUrl;
    }


    public Boolean isMyLike() {
        return myLike;
    }

    public void setMyLike(Boolean myLike) {

        this.myLike = myLike;
    }

    public Boolean isMyRePost() {
        return myRePost;
    }

    public void setMyRePost(Boolean myRePost) {

        this.myRePost = myRePost;
    }

    public String getImgUrl() {

        if (this.imgUrl == null) {

            this.imgUrl = "";
        }

        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getRePostImgUrl() {
        return rePostImgUrl;
    }

    public void setRePostImgUrl(String rePostImgUrl) {

        if (this.rePostImgUrl == null) {

            this.rePostImgUrl = "";
        }

        this.rePostImgUrl = rePostImgUrl;
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

    public String getReYouTubeVideoImg() {

        return reYouTubeVideoImg;
    }

    public void setReYouTubeVideoImg(String reYouTubeVideoImg) {

        this.reYouTubeVideoImg = reYouTubeVideoImg;
    }

    public String getReYouTubeVideoCode() {

        return reYouTubeVideoCode;
    }

    public void setReYouTubeVideoCode(String reYouTubeVideoCode) {

        this.reYouTubeVideoCode = reYouTubeVideoCode;
    }

    public String getReYouTubeVideoUrl() {

        return reYouTubeVideoUrl;
    }

    public void setReYouTubeVideoUrl(String reYouTubeVideoUrl) {

        this.reYouTubeVideoUrl = reYouTubeVideoUrl;
    }

    public String getReVideoUrl() {

        if (this.reVideoUrl == null) {

            this.reVideoUrl = "";
        }

        return reVideoUrl;
    }

    public void setReVideoUrl(String reVideoUrl) {

        this.reVideoUrl = reVideoUrl;
    }

    public String getReUrlPreviewTitle() {

        return reUrlPreviewTitle;
    }

    public void setReUrlPreviewTitle(String reUrlPreviewTitle) {

        this.reUrlPreviewTitle = reUrlPreviewTitle;
    }

    public String getReUrlPreviewImage() {

        return reUrlPreviewImage;
    }

    public void setReUrlPreviewImage(String reUrlPreviewImage) {

        this.reUrlPreviewImage = reUrlPreviewImage;
    }

    public String getReUrlPreviewLink() {

        return reUrlPreviewLink;
    }

    public void setReUrlPreviewLink(String reUrlPreviewLink) {

        this.reUrlPreviewLink = reUrlPreviewLink;
    }

    public String getReUrlPreviewDescription() {

        return reUrlPreviewDescription;
    }

    public void setReUrlPreviewDescription(String reUrlPreviewDescription) {

        this.reUrlPreviewDescription = reUrlPreviewDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.fromUserId);
        dest.writeLong(this.rePostId);
        dest.writeLong(this.groupId);
        dest.writeLong(this.groupAuthor);
        dest.writeInt(this.createAt);
        dest.writeInt(this.likesCount);
        dest.writeInt(this.commentsCount);
        dest.writeInt(this.rePostsCount);
        dest.writeInt(this.allowComments);
        dest.writeInt(this.groupAllowComments);
        dest.writeInt(this.accessMode);
        dest.writeInt(this.fromUserVerify);
        dest.writeString(this.timeAgo);
        dest.writeString(this.date);
        dest.writeString(this.post);
        dest.writeString(this.imgUrl);
        dest.writeString(this.fromUserUsername);
        dest.writeString(this.fromUserFullname);
        dest.writeString(this.fromUserPhotoUrl);
        dest.writeString(this.area);
        dest.writeString(this.country);
        dest.writeString(this.city);
        dest.writeString(this.youTubeVideoImg);
        dest.writeString(this.youTubeVideoCode);
        dest.writeString(this.youTubeVideoUrl);
        dest.writeValue(this.lat);
        dest.writeValue(this.lng);
        dest.writeValue(this.myLike);
        dest.writeValue(this.myRePost);
        dest.writeString(this.urlPreviewTitle);
        dest.writeString(this.urlPreviewImage);
        dest.writeString(this.urlPreviewLink);
        dest.writeString(this.urlPreviewDescription);
        dest.writeString(this.previewVideoImgUrl);
        dest.writeString(this.videoUrl);
        dest.writeString(this.rePostTimeAgo);
        dest.writeString(this.rePostDate);
        dest.writeString(this.rePostPost);
        dest.writeString(this.rePostImgUrl);
        dest.writeString(this.rePostFromUserUsername);
        dest.writeString(this.rePostFromUserFullname);
        dest.writeString(this.rePostFromUserPhotoUrl);
        dest.writeInt(this.fromRePostUserVerify);
        dest.writeInt(this.rePostRemoveAt);
        dest.writeLong(this.rePostFromUserId);
        dest.writeString(this.reVideoUrl);
        dest.writeString(this.reYouTubeVideoImg);
        dest.writeString(this.reYouTubeVideoCode);
        dest.writeString(this.reYouTubeVideoUrl);
        dest.writeString(this.reUrlPreviewTitle);
        dest.writeString(this.reUrlPreviewImage);
        dest.writeString(this.reUrlPreviewLink);
        dest.writeString(this.reUrlPreviewDescription);
        dest.writeInt(this.ad);
    }

    protected Item(Parcel in) {
        this.id = in.readLong();
        this.fromUserId = in.readLong();
        this.rePostId = in.readLong();
        this.groupId = in.readLong();
        this.groupAuthor = in.readLong();
        this.createAt = in.readInt();
        this.likesCount = in.readInt();
        this.commentsCount = in.readInt();
        this.rePostsCount = in.readInt();
        this.allowComments = in.readInt();
        this.groupAllowComments = in.readInt();
        this.accessMode = in.readInt();
        this.fromUserVerify = in.readInt();
        this.timeAgo = in.readString();
        this.date = in.readString();
        this.post = in.readString();
        this.imgUrl = in.readString();
        this.fromUserUsername = in.readString();
        this.fromUserFullname = in.readString();
        this.fromUserPhotoUrl = in.readString();
        this.area = in.readString();
        this.country = in.readString();
        this.city = in.readString();
        this.youTubeVideoImg = in.readString();
        this.youTubeVideoCode = in.readString();
        this.youTubeVideoUrl = in.readString();
        this.lat = (Double) in.readValue(Double.class.getClassLoader());
        this.lng = (Double) in.readValue(Double.class.getClassLoader());
        this.myLike = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.myRePost = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.urlPreviewTitle = in.readString();
        this.urlPreviewImage = in.readString();
        this.urlPreviewLink = in.readString();
        this.urlPreviewDescription = in.readString();
        this.previewVideoImgUrl = in.readString();
        this.videoUrl = in.readString();
        this.rePostTimeAgo = in.readString();
        this.rePostDate = in.readString();
        this.rePostPost = in.readString();
        this.rePostImgUrl = in.readString();
        this.rePostFromUserUsername = in.readString();
        this.rePostFromUserFullname = in.readString();
        this.rePostFromUserPhotoUrl = in.readString();
        this.fromRePostUserVerify = in.readInt();
        this.rePostRemoveAt = in.readInt();
        this.rePostFromUserId = in.readLong();
        this.reVideoUrl = in.readString();
        this.reYouTubeVideoImg = in.readString();
        this.reYouTubeVideoCode = in.readString();
        this.reYouTubeVideoUrl = in.readString();
        this.reUrlPreviewTitle = in.readString();
        this.reUrlPreviewImage = in.readString();
        this.reUrlPreviewLink = in.readString();
        this.reUrlPreviewDescription = in.readString();
        this.ad = in.readInt();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
