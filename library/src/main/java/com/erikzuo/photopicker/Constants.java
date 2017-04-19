package com.erikzuo.photopicker;

import android.graphics.Color;

import com.erikzuo.photopicker.R;

/**
 * Created by YifanZuo on 5/07/2016.
 */
class Constants {


    // Activity result code
    static final int TAKE_PHOTO_REQUEST_CODE = 1;

    // Permission code
    static final int REQUEST_STORAGE_PERMISSION = 2;
    static final int REQUEST_CAMERA_PERMISSION = 3;

    // Intent keys
    static final String KEY_UPDATED_PHOTO = "updated_photo";
    static final String KEY_UPDATED_PHOTO_POSITION = "updated_photo_position";
    static final String KEY_MAX_COUNT = "max_count";
    static final String KEY_NUM_OF_COLUMNS = "number_of_columns";
    static final String KEY_CAMERA_ICON_RES_ID = "camera_icon_res_id";
    static final String KEY_FILE_NAME = "file_name";
    static final String KEY_DIR_NAME = "dir_name";
    static final String KEY_PHOTO_PICKER_TITLE = "activity_title";
    static final String KEY_THUMBNAIL_SIZE = "thumbnail_size";
    static final String KEY_SHOW_CAMERA = "show_camera";
    static final String KEY_GRID_ITEM_MARGIN = "grid_item_margin";
    static final String KEY_FINISH_BTN_COLOR = "finish_btn_color";
    static final String KEY_FINISH_BTN_TXT_COLOR = "finish_btn_text_color";
    static final String KEY_PHOTO_URL_ARRAY = "photo_url_array";
    static final String KEY_ENABLE_SINGLE_PHOTO = "enable single photo";
    static final String KEY_CHECKBOX_RES_ID = "checkbox resource id";


    // Broadcast action
    static final String BROADCAST_PHOTO_STATUS = "Photo selection status changed";
    static final String BROADCAST_SELECTED_PHOTO_LIST = "Broadcast selected photo list";


    // Default values
    static final int DEFAULT_MAX_COUNT = 9;
    static final int DEFAULT_NUM_OF_COLUMNS = 3;
    static final int DEFAULT_THUMBNAIL_SIZE = 200;
    static final int DEFAULT_CAMERA_ICON_RES_ID = R.drawable.icon_camera;
    static final String DEFAULT_DIR_NAME = "pp";
    static final String DEFAULT_FILE_NAME = "pp-captured";
    static final String DEFAULT_PHOTO_PICKER_TITLE = "Select";
    static final boolean DEFAULT_SHOW_CAMERA = false;
    static final int DEFAULT_GRID_ITEM_MARGIN = 5;
    static final int DEFAULT_FINISH_BTN_COLOR = Color.GREEN;
    static final int DEFAULT_FINISH_BTN_TXT_COLOR = Color.WHITE;
    static final String DEFAULT_ALBUM_NAME = "All photos";
    static final boolean DEFAULT_ENABLE_SINGLE_PHOTO = false;
    static final int DEFAULT_CHECKBOX_RES_ID = R.drawable.pp_checkbox;


}
