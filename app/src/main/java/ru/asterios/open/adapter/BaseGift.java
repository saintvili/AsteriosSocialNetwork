package ru.asterios.open.adapter;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import ru.asterios.open.constants.Constants;


public class BaseGift extends Application implements Constants, Parcelable {

    private long id;
    private int createAt, cost, category;
    private String timeAgo, date, imgUrl;

    public BaseGift() {

    }

    public BaseGift(JSONObject jsonData) {

        try {

            if (!jsonData.getBoolean("error")) {

                this.setId(jsonData.getLong("id"));
                this.setCost(jsonData.getInt("cost"));
                this.setCategory(jsonData.getInt("category"));
                this.setImgUrl(jsonData.getString("imgUrl"));
                this.setCreateAt(jsonData.getInt("createAt"));
                this.setDate(jsonData.getString("date"));
                this.setTimeAgo(jsonData.getString("timeAgo"));
            }

        } catch (Throwable t) {

            Log.e("BaseGift", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("BaseGift", jsonData.toString());
        }
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCost() {

        return this.cost;
    }

    public void setCost(int cost) {

        this.cost = cost;
    }

    public int getCategory() {

        return this.category;
    }

    public void setCategory(int category) {

        this.category = category;
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

    public String getImgUrl() {
        return imgUrl;
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

    }
    public static final Creator CREATOR = new Creator() {

        public BaseGift createFromParcel(Parcel in) {

            return new BaseGift();
        }

        public BaseGift[] newArray(int size) {
            return new BaseGift[size];
        }
    };
}
