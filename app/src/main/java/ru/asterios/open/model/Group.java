package ru.asterios.open.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import ru.asterios.open.constants.Constants;


public class Group extends Application implements Constants, Parcelable {

    private long id, authorId;

    private int state, year, month, day, verify, itemsCount, followersCount, allowComments, allowPosts, category;

    private String username, fullname, lowPhotoUrl, bigPhotoUrl, normalPhotoUrl, location, webPage, bio;

    private Boolean follow = false;

    public Group() {


    }

    public Group(JSONObject jsonData) {

        try {

            if (!jsonData.getBoolean("error")) {

                this.setId(jsonData.getLong("id"));
                this.setAuthorId(jsonData.getLong("accountAuthor"));
                this.setCategory(jsonData.getInt("accountCategory"));
                this.setState(jsonData.getInt("state"));
                this.setYear(jsonData.getInt("year"));
                this.setMonth(jsonData.getInt("month"));
                this.setDay(jsonData.getInt("day"));
                this.setUsername(jsonData.getString("username"));
                this.setFullname(jsonData.getString("fullname"));
                this.setLocation(jsonData.getString("location"));
                this.setWebPage(jsonData.getString("my_page"));
                this.setBio(jsonData.getString("status"));
                this.setVerify(jsonData.getInt("verify"));

                this.setLowPhotoUrl(jsonData.getString("lowPhotoUrl"));
                this.setNormalPhotoUrl(jsonData.getString("normalPhotoUrl"));
                this.setBigPhotoUrl(jsonData.getString("bigPhotoUrl"));

                this.setFollowersCount(jsonData.getInt("followersCount"));
                this.setItemsCount(jsonData.getInt("postsCount"));

                this.setAllowComments(jsonData.getInt("allowComments"));
                this.setAllowPosts(jsonData.getInt("allowPosts"));

                this.setFollow(jsonData.getBoolean("follow"));
            }

        } catch (Throwable t) {

            Log.e("Group", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("Group", jsonData.toString());
        }
    }

    public void setId(long profile_id) {

        this.id = profile_id;
    }

    public long getId() {

        return this.id;
    }

    public void setAuthorId(long authorId) {

        this.authorId = authorId;
    }

    public long getAuthorId() {

        return this.authorId;
    }

    public void setCategory(int category) {

        this.category = category;
    }

    public int getCategory() {

        return this.category;
    }

    public void setState(int profileState) {

        this.state = profileState;
    }

    public int getState() {

        return this.state;
    }

    public void setYear(int year) {

        this.year = year;
    }

    public int getYear() {

        return this.year;
    }

    public void setMonth(int month) {

        this.month = month;
    }

    public int getMonth() {

        return this.month;
    }

    public void setDay(int day) {

        this.day = day;
    }

    public int getDay() {

        return this.day;
    }

    public void setVerify(int profileVerify) {

        this.verify = profileVerify;
    }

    public int getVerify() {

        return this.verify;
    }

    public Boolean isVerify() {

        if (this.verify > 0) {

            return true;
        }

        return false;
    }

    public void setUsername(String profile_username) {

        this.username = profile_username;
    }

    public String getUsername() {

        return this.username;
    }

    public void setFullname(String profile_fullname) {

        this.fullname = profile_fullname;
    }

    public String getFullname() {

        return this.fullname;
    }

    public void setLocation(String location) {

        this.location = location;
    }

    public String getLocation() {

        if (this.location == null) {

            this.location = "";
        }

        return this.location;
    }

    public void setWebPage(String webPage) {

        this.webPage = webPage;
    }

    public String getWebPage() {

        return this.webPage;
    }

    public void setBio(String bio) {

        this.bio = bio;
    }

    public String getBio() {

        return this.bio;
    }

    public void setLowPhotoUrl(String lowPhotoUrl) {

        this.lowPhotoUrl = lowPhotoUrl;
    }

    public String getLowPhotoUrl() {

        return this.lowPhotoUrl;
    }

    public void setBigPhotoUrl(String bigPhotoUrl) {

        this.bigPhotoUrl = bigPhotoUrl;
    }

    public String getBigPhotoUrl() {

        return this.bigPhotoUrl;
    }

    public void setNormalPhotoUrl(String normalPhotoUrl) {

        this.normalPhotoUrl = normalPhotoUrl;
    }

    public String getNormalPhotoUrl() {

        return this.normalPhotoUrl;
    }

    public void setFollowersCount(int followersCount) {

        this.followersCount = followersCount;
    }

    public int getFollowersCount() {

        return this.followersCount;
    }

    public void setItemsCount(int itemsCount) {

        this.itemsCount = itemsCount;
    }

    public int getItemsCount() {

        return this.itemsCount;
    }

    public void setAllowComments(int allowComments) {

        this.allowComments = allowComments;
    }

    public int getAllowComments() {

        return this.allowComments;
    }

    public void setAllowPosts(int allowPosts) {

        this.allowPosts = allowPosts;
    }

    public int getAllowPosts() {

        return this.allowPosts;
    }

    public void setFollow(Boolean follow) {

        this.follow = follow;
    }

    public Boolean isFollow() {

        return this.follow;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.authorId);
        dest.writeInt(this.state);
        dest.writeInt(this.year);
        dest.writeInt(this.month);
        dest.writeInt(this.day);
        dest.writeInt(this.verify);
        dest.writeInt(this.itemsCount);
        dest.writeInt(this.followersCount);
        dest.writeInt(this.allowComments);
        dest.writeInt(this.allowPosts);
        dest.writeInt(this.category);
        dest.writeString(this.username);
        dest.writeString(this.fullname);
        dest.writeString(this.lowPhotoUrl);
        dest.writeString(this.bigPhotoUrl);
        dest.writeString(this.normalPhotoUrl);
        dest.writeString(this.location);
        dest.writeString(this.webPage);
        dest.writeString(this.bio);
        dest.writeValue(this.follow);
    }

    protected Group(Parcel in) {
        this.id = in.readLong();
        this.authorId = in.readLong();
        this.state = in.readInt();
        this.year = in.readInt();
        this.month = in.readInt();
        this.day = in.readInt();
        this.verify = in.readInt();
        this.itemsCount = in.readInt();
        this.followersCount = in.readInt();
        this.allowComments = in.readInt();
        this.allowPosts = in.readInt();
        this.category = in.readInt();
        this.username = in.readString();
        this.fullname = in.readString();
        this.lowPhotoUrl = in.readString();
        this.bigPhotoUrl = in.readString();
        this.normalPhotoUrl = in.readString();
        this.location = in.readString();
        this.webPage = in.readString();
        this.bio = in.readString();
        this.follow = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
}
