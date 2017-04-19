package com.erikzuo.photopicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

/**
 * A photo picker with customisable view settings and camera integration.
 * <p/>
 * Use {@link #with(android.content.Context)} to construct an instance of {@link com.erikzuo.photopicker.PhotoPicker}.
 */
public class PhotoPicker {

    public static final int PHOTO_PICKER_ACTIVITY_REQUEST_CODE = 777;

    public interface PhotoResult {
        void onPhotoResult(ArrayList<String> photoUrls);
    }

    /**
     * The default {@link PhotoPicker} instance.
     * <p/>
     * This instance is automatically initialized with default configurations that are suitable to most implementations
     * <p/>
     * If you wish to customise your on photo picker configuration for your application, you can configure your own settings with full
     * control over a various of public functions.
     */
    public static IntentBuilder with(Context context) {
        return new IntentBuilder(context);
    }

    public static void onPhotoPickerResult(int requestCode, int resultCode, Intent data, PhotoResult photoResult) {

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PHOTO_PICKER_ACTIVITY_REQUEST_CODE:
                    ArrayList<String> photoUrls = data.getStringArrayListExtra(Constants.KEY_PHOTO_URL_ARRAY);
                    photoResult.onPhotoResult(photoUrls);
                    break;
                default:
                    break;
            }
        }
    }



    /**
     * An intent builder that construct all necessary configurations of the photo picker view.
     */
    public static final class IntentBuilder {
        private Context mContext;
        private int mMaxCount, mNumOfCol, mThumbnailSize, mGridItemMargin;
        private int mCameraIconResId, mFinishBtnColor, mFinishBtnTxtColor, mCheckboxResId;
        private String mFileName, mDirName, mPhotoPickerTitle;
        private boolean mShowCamera, mEnableSinglePhoto;


        public IntentBuilder(Context mContext) {
            this.mContext = mContext;
            this.mFileName = Constants.DEFAULT_FILE_NAME;
            this.mDirName = Constants.DEFAULT_DIR_NAME;
            this.mMaxCount = Constants.DEFAULT_MAX_COUNT;
            this.mNumOfCol = Constants.DEFAULT_NUM_OF_COLUMNS;
            this.mThumbnailSize = Constants.DEFAULT_THUMBNAIL_SIZE;
            this.mPhotoPickerTitle = Constants.DEFAULT_PHOTO_PICKER_TITLE;
            this.mShowCamera = Constants.DEFAULT_SHOW_CAMERA;
            this.mGridItemMargin = Constants.DEFAULT_GRID_ITEM_MARGIN;
            this.mFinishBtnColor = Constants.DEFAULT_FINISH_BTN_COLOR;
            this.mFinishBtnTxtColor = Constants.DEFAULT_FINISH_BTN_TXT_COLOR;
            this.mEnableSinglePhoto = Constants.DEFAULT_ENABLE_SINGLE_PHOTO;
            this.mCameraIconResId = Constants.DEFAULT_CAMERA_ICON_RES_ID;
            this.mCheckboxResId = Constants.DEFAULT_CHECKBOX_RES_ID;
        }

        /**
         * Set the maximum number of photos that can be selected.
         *
         * @param maxCount The max number of photos
         */
        public IntentBuilder setMaxCount(int maxCount) {
            this.mMaxCount = maxCount;
            return this;
        }

        /**
         * Set the number of columns that photos should be displayed in.
         *
         * @param numOfColumns The number of columns in the grid view
         */
        public IntentBuilder setNumOfColumns(int numOfColumns) {
            this.mNumOfCol = numOfColumns;
            return this;
        }

        /**
         * Set the placeholder image for the camera button that appears as the first item in the grid view.
         *
         * @param resId The drawable resource id of the placeholder image
         */
        public IntentBuilder setCameraIcon(int resId) {
            this.mCameraIconResId = resId;
            return this;
        }

        /**
         * Set the thumbnail size of each photo displayed in the grid view.
         *
         * @param thumbnailSize The width & height of the grid item
         */
        public IntentBuilder setThumbnailSize(int thumbnailSize) {
            this.mThumbnailSize = thumbnailSize;
            return this;
        }

        /**
         * Enables the camera function of photo picker.
         *
         * @param showCamera True if camera function is enabled. Default value is true
         */
        public IntentBuilder enableCamera(String fileName, String dirName) {
            this.mShowCamera = true;
            this.mFileName = fileName;
            this.mDirName = dirName;
            return this;
        }

        /**
         * Set the margin of each photo item in the grid view in pixel.
         *
         * @param gridItemMargin The margin in pixel.
         */
        public IntentBuilder setGridItemMargin(int gridItemMargin) {
            this.mGridItemMargin = gridItemMargin;
            return this;
        }

        /**
         * Set the finish button style.
         *
         * @param bgColor  The background color of the hutton.
         * @param txtColor The text color of the button.
         */
        public IntentBuilder setFinishBtnStyle(int bgColor, int txtColor) {
            this.mFinishBtnColor = bgColor;
            this.mFinishBtnTxtColor = txtColor;
            return this;
        }

        /**
         * Set the title that should be displayed on the actionbar of photo picker view.
         *
         * @param title The title of the photo picker page.
         */
        public IntentBuilder setPhotoPickerTitle(String title) {
            this.mPhotoPickerTitle = title;
            return this;
        }


        public IntentBuilder setCheckBoxIcon(int mCheckBoxResId){
            this.mCheckboxResId = mCheckBoxResId;
            return this;
        }

        public IntentBuilder enableSinglePhoto(boolean enableSinglePhoto){
            this.mEnableSinglePhoto = enableSinglePhoto;
            return this;
        }

        /**
         * When everything is ready, start the photo picker activity.
         */
        public void pickPhotos() {

            Intent photoPickerIntent = new Intent(mContext, PhotoPickerActivity.class);
            photoPickerIntent.putExtra(Constants.KEY_MAX_COUNT, this.mMaxCount);
            photoPickerIntent.putExtra(Constants.KEY_NUM_OF_COLUMNS, this.mNumOfCol);
            photoPickerIntent.putExtra(Constants.KEY_CAMERA_ICON_RES_ID, this.mCameraIconResId);
            photoPickerIntent.putExtra(Constants.KEY_FILE_NAME, this.mFileName);
            photoPickerIntent.putExtra(Constants.KEY_DIR_NAME, this.mDirName);
            photoPickerIntent.putExtra(Constants.KEY_PHOTO_PICKER_TITLE, this.mPhotoPickerTitle);
            photoPickerIntent.putExtra(Constants.KEY_SHOW_CAMERA, this.mShowCamera);
            photoPickerIntent.putExtra(Constants.KEY_THUMBNAIL_SIZE, this.mThumbnailSize);
            photoPickerIntent.putExtra(Constants.KEY_GRID_ITEM_MARGIN, this.mGridItemMargin);
            photoPickerIntent.putExtra(Constants.KEY_FINISH_BTN_COLOR, this.mFinishBtnColor);
            photoPickerIntent.putExtra(Constants.KEY_FINISH_BTN_TXT_COLOR, this.mFinishBtnTxtColor);
            photoPickerIntent.putExtra(Constants.KEY_ENABLE_SINGLE_PHOTO, this.mEnableSinglePhoto);
            photoPickerIntent.putExtra(Constants.KEY_CHECKBOX_RES_ID, mCheckboxResId);

            ((Activity) mContext).startActivityForResult(photoPickerIntent, PHOTO_PICKER_ACTIVITY_REQUEST_CODE);
        }

    }

}
