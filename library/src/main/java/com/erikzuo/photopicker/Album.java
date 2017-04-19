package com.erikzuo.photopicker;

/**
 * Created by YifanZuo on 5/07/2016.
 */
class Album {
    private String mName, mCoverPhotoUrl;
    private boolean mIsSelected;
    private int mPhotoCount;

    public Album(String mName, String mCoverPhotoUrl, int photoCount) {
        this.mName = mName;
        this.mCoverPhotoUrl = mCoverPhotoUrl;
        this.mPhotoCount = photoCount;
    }

    public String getName() {
        return mName;
    }

    public String getCoverPhotoUrl() {
        return mCoverPhotoUrl;
    }

    public int getPhotoCount() {
        return mPhotoCount;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.mIsSelected = isSelected;
    }

    public void increaseCount(){
        mPhotoCount++;
    }
}
