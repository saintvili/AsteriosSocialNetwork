package ru.asterios.open.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import ru.asterios.open.constants.Constants;

public class Comment extends Application implements Constants, Parcelable {

    private long id, postId, fromUserId, replyToUserId;
    private int fromUserState, fromUserVerify, createAt;
    private String comment, fromUserUsername, fromUserFullname, replyToUserUsername, replyToUserFullname, fromUserPhotoUrl, timeAgo;

    public Comment() {

    }

    public Comment(JSONObject jsonData) {

        try {

            this.setId(jsonData.getLong("id"));
            this.setFromUserId(jsonData.getLong("fromUserId"));
            this.setFromUserState(jsonData.getInt("fromUserState"));
            this.setFromUserVerify(jsonData.getInt("fromUserVerify"));
            this.setFromUserUsername(jsonData.getString("fromUserUsername"));
            this.setFromUserFullname(jsonData.getString("fromUserFullname"));
            this.setFromUserPhotoUrl(jsonData.getString("fromUserPhotoUrl"));
            this.setReplyToUserId(jsonData.getLong("replyToUserId"));
            this.setReplyToUserUsername(jsonData.getString("replyToUserUsername"));
            this.setReplyToUserFullname(jsonData.getString("replyToFullname"));
            this.setText(jsonData.getString("comment"));
            this.setTimeAgo(jsonData.getString("timeAgo"));
            this.setCreateAt(jsonData.getInt("createAt"));

            if (jsonData.has("postId")) {

                this.setItemId(jsonData.getLong("postId"));
            }

            if (jsonData.has("imageId")) {

                this.setItemId(jsonData.getLong("imageId"));
            }

            if (jsonData.has("videoId")) {

                this.setItemId(jsonData.getLong("videoId"));
            }

        } catch (Throwable t) {

            Log.e("Comment", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("Comment", jsonData.toString());
        }
    }

    public void setId(long id) {

        this.id = id;
    }

    public long getId() {

        return this.id;
    }

    public void setItemId(long postId) {

        this.postId = postId;
    }

    public long getItemId() {

        return this.postId;
    }

    public void setFromUserId(long fromUserId) {

        this.fromUserId = fromUserId;
    }

    public long getFromUserId() {

        return this.fromUserId;
    }

    public void setReplyToUserId(long replyToUserId) {

        this.replyToUserId = replyToUserId;
    }

    public long getReplyToUserId() {

        return this.replyToUserId;
    }

    public void setFromUserState(int fromUserState) {

        this.fromUserState = fromUserState;
    }

    public int getFromUserState() {

        return this.fromUserState;
    }

    public void setFromUserVerify(int fromUserVerify) {

        this.fromUserVerify = fromUserVerify;
    }

    public int getFromUserVerify() {

        return this.fromUserVerify;
    }

    public void setText(String comment) {

        this.comment = comment;
    }

    public String getText() {

        if (this.comment == null) {

            this.comment = "";
        }

        return this.comment;
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

    public void setReplyToUserUsername(String replyToUserUsername) {

        this.replyToUserUsername = replyToUserUsername;
    }

    public String getReplyToUserUsername() {

        return this.replyToUserUsername;
    }

    public void setFromUserFullname(String fromUserFullname) {

        this.fromUserFullname = fromUserFullname;
    }

    public String getFromUserFullname() {

        return this.fromUserFullname;
    }

    public void setReplyToUserFullname(String replyToUserFullname) {

        this.replyToUserFullname = replyToUserFullname;
    }

    public String getReplyToUserFullname() {

        return this.replyToUserFullname;
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
        dest.writeLong(this.postId);
        dest.writeLong(this.fromUserId);
        dest.writeLong(this.replyToUserId);
        dest.writeInt(this.fromUserState);
        dest.writeInt(this.fromUserVerify);
        dest.writeInt(this.createAt);
        dest.writeString(this.comment);
        dest.writeString(this.fromUserUsername);
        dest.writeString(this.fromUserFullname);
        dest.writeString(this.replyToUserUsername);
        dest.writeString(this.replyToUserFullname);
        dest.writeString(this.fromUserPhotoUrl);
        dest.writeString(this.timeAgo);
    }

    protected Comment(Parcel in) {
        this.id = in.readLong();
        this.postId = in.readLong();
        this.fromUserId = in.readLong();
        this.replyToUserId = in.readLong();
        this.fromUserState = in.readInt();
        this.fromUserVerify = in.readInt();
        this.createAt = in.readInt();
        this.comment = in.readString();
        this.fromUserUsername = in.readString();
        this.fromUserFullname = in.readString();
        this.replyToUserUsername = in.readString();
        this.replyToUserFullname = in.readString();
        this.fromUserPhotoUrl = in.readString();
        this.timeAgo = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
