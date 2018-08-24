package ru.asterios.open.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import ru.asterios.open.constants.Constants;


public class Sticker extends Application implements Constants, Parcelable {

    private long id;
    private int cost, category;
    private String imgUrl;

    public Sticker() {

    }

    public Sticker(JSONObject jsonData) {

        try {

            if (!jsonData.getBoolean("error")) {

                this.setId(jsonData.getLong("id"));
                this.setCost(jsonData.getInt("cost"));
                this.setCategory(jsonData.getInt("category"));

                this.setImgUrl(jsonData.getString("imgUrl"));
            }

        } catch (Throwable t) {

            Log.e("Sticker", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("Sticker", jsonData.toString());
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

    public String getImgUrl() {

        if (this.imgUrl == null) {

            this.imgUrl = "";
        }

        return this.imgUrl;
    }

    public void setImgUrl(String imgUrl) {

        this.imgUrl = imgUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.cost);
        dest.writeInt(this.category);
        dest.writeString(this.imgUrl);
    }

    protected Sticker(Parcel in) {
        this.id = in.readLong();
        this.cost = in.readInt();
        this.category = in.readInt();
        this.imgUrl = in.readString();
    }

    public static final Creator<Sticker> CREATOR = new Creator<Sticker>() {
        @Override
        public Sticker createFromParcel(Parcel source) {
            return new Sticker(source);
        }

        @Override
        public Sticker[] newArray(int size) {
            return new Sticker[size];
        }
    };
}
