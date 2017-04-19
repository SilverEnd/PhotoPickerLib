package com.erikzuo.photopicker;

/**
 * Created by YifanZuo on 5/07/2016.
 */

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable {

    private String url;
    private boolean selected;

    public Photo(String path) {
        this.url = path;
        this.selected = false;
    }

    public Photo(Parcel source){
        this.url = source.readString();
        this.selected = source.readInt() != 0;
    }

    public String getUrl() {
        return url;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeInt(this.selected ? 1 : 0);
    }

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
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
