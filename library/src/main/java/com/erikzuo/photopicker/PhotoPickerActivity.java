package com.erikzuo.photopicker;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by YifanZuo on 5/07/2016.
 */
public class PhotoPickerActivity extends AppCompatActivity implements PhotoLoader.OnLoadPhotosListener, PhotoLoader.OnLoadAlbumsListener,
        AdapterView.OnItemClickListener {

    private GridView mGridView;
    private PhotoPickerAdapter mGridAdapter;
    private PhotoLoader mPhotoLoader;
    private File mPhotoFromCamera;
    private BroadcastReceiver mReceiver;
    private Button mFinishBtn, mAlbumBtn;
    private String mLastAlbumName;
    private AlbumPopup mAlbumPopup;

    // Global variable to store selected photos among different albums
    private ArrayList<Photo> mGlobalSelected;

    // Intent data
    private String mFileName, mDirName;
    private int mMaxCount, mNumOfCols, mThumbnailSize, mGridItemMargin;
    private int mCameraIconResId, mFinishBtnColor, mFinishBtnTxtColor, mCheckboxResId;
    private String mPhotoPickerTitle;
    private boolean mShowCamera, mEnableSinglePhoto;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pp_activity_photo_picker);


        registerReceiver();
        init();
    }

    private void init() {
        mFileName = getIntent().getStringExtra(Constants.KEY_FILE_NAME);
        mDirName = getIntent().getStringExtra(Constants.KEY_DIR_NAME);
        mMaxCount = getIntent().getIntExtra(Constants.KEY_MAX_COUNT, Constants.DEFAULT_MAX_COUNT);
        mNumOfCols = getIntent().getIntExtra(Constants.KEY_NUM_OF_COLUMNS, Constants.DEFAULT_NUM_OF_COLUMNS);
        mCameraIconResId = getIntent().getIntExtra(Constants.KEY_CAMERA_ICON_RES_ID, Constants.DEFAULT_CAMERA_ICON_RES_ID);
        mPhotoPickerTitle = getIntent().getStringExtra(Constants.KEY_PHOTO_PICKER_TITLE);
        mThumbnailSize = getIntent().getIntExtra(Constants.KEY_THUMBNAIL_SIZE, Constants.DEFAULT_THUMBNAIL_SIZE);
        mShowCamera = getIntent().getBooleanExtra(Constants.KEY_SHOW_CAMERA, Constants.DEFAULT_SHOW_CAMERA);
        mGridItemMargin = getIntent().getIntExtra(Constants.KEY_GRID_ITEM_MARGIN, Constants.DEFAULT_GRID_ITEM_MARGIN);
        mFinishBtnColor = getIntent().getIntExtra(Constants.KEY_FINISH_BTN_COLOR, Constants.DEFAULT_FINISH_BTN_COLOR);
        mFinishBtnTxtColor = getIntent().getIntExtra(Constants.KEY_FINISH_BTN_TXT_COLOR, Constants.DEFAULT_FINISH_BTN_TXT_COLOR);
        mEnableSinglePhoto = getIntent().getBooleanExtra(Constants.KEY_ENABLE_SINGLE_PHOTO, Constants.DEFAULT_ENABLE_SINGLE_PHOTO);
        mCheckboxResId = getIntent().getIntExtra(Constants.KEY_CHECKBOX_RES_ID, Constants.DEFAULT_CHECKBOX_RES_ID);

        // Photo grid view
        mGridView = (GridView) findViewById(R.id.photo_grid);

        if (mGridView != null) {
            mGridView.setNumColumns(mNumOfCols);
            mGridView.setHorizontalSpacing(mGridItemMargin);
            mGridView.setVerticalSpacing(mGridItemMargin);
        }

        mGlobalSelected = new ArrayList<>();
        mGridAdapter = new PhotoPickerAdapter(
                this,
                getLayoutInflater(),
                null);

        mGridView.setAdapter(mGridAdapter);
        mGridView.setOnItemClickListener(this);


        mLastAlbumName = Constants.DEFAULT_ALBUM_NAME;


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mPhotoPickerTitle);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mPhotoLoader != null) {
//            mPhotoLoader.getAllPhotoList(this);
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        mReceiver = null;

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pp_menu_photo_picker, menu);

        MenuItem finishItem = menu.findItem(R.id.action_finish);
        View v1 = MenuItemCompat.getActionView(finishItem);

        mFinishBtn = (Button) v1.findViewById(R.id.finish_btn);
        mFinishBtn.setBackgroundColor(mFinishBtnColor);
        mFinishBtn.setTextColor(mFinishBtnTxtColor);

        setFinishBtnText();

        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResultPhotoList();
                onBackPressed();
            }
        });


        MenuItem albumItem = menu.findItem(R.id.action_album);
        View v2 = MenuItemCompat.getActionView(albumItem);

        mAlbumBtn = (Button) v2.findViewById(R.id.album_btn);
        mAlbumBtn.setBackgroundColor(mFinishBtnColor);
        mAlbumBtn.setTextColor(mFinishBtnTxtColor);

        setAlbumBtnText();

        mAlbumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAlbumPopup != null) {
                    mAlbumPopup.show();
                }
            }
        });


        // Photo loader
        mPhotoLoader = new PhotoLoader(this);
        mPhotoLoader.getAllPhotoList(this);
        mPhotoLoader.getAlbumList(this);


        return true;
    }

    private void setAlbumBtnText() {
        mAlbumBtn.setText(mLastAlbumName);
    }

    public void setFinishBtnText() {
        String txt = "";
        int num = mGlobalSelected.size();
        if (num > 0) {
            txt = num + "/" + mMaxCount;
        }

        mFinishBtn.setText(getString(R.string.pp_btn_finish, "Done", txt));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_finish) {

            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void OnPhotosLoaded(ArrayList<Photo> photoList) {
        mGridAdapter.injectSelectedPhotos(mGlobalSelected, photoList);
        mGridAdapter.notifyDataSetChanged(photoList, true);
    }


    @Override
    public void onAlbumsLoaded(final ArrayList<Album> albumList) {
        albumList.get(0).setIsSelected(true);

        buildAlbumPopup(albumList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0 && mShowCamera) {
            if (mGlobalSelected.size() < mMaxCount) {
                mPhotoFromCamera = IOHelper.createFile(mFileName, mDirName);
                takePhoto(mPhotoFromCamera);
            } else {
                Toast.makeText(this, "You can select up to " + mMaxCount + " photos.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.TAKE_PHOTO_REQUEST_CODE:
                    mGridAdapter.addPhotoToSelected(new Photo(mPhotoFromCamera.getAbsolutePath()));
                    addImageToGallery(mPhotoFromCamera.getAbsolutePath(), this);

                    sendResultPhotoList();

                    onBackPressed();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case Constants.REQUEST_CAMERA_PERMISSION:
                if (PermissionUtils.isPermissionGranted(grantResults)) {
                    takePhoto(mPhotoFromCamera);
                } else {
                    Toast.makeText(this, "Need Camera & Storage Permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void buildAlbumPopup(final ArrayList<Album> albumList) {
        mAlbumPopup = new AlbumPopup(this, albumList);
        mAlbumPopup.build(new AlbumPopup.AlbumItemClickListener() {
            @Override
            public void onAlbumItemClick(int position) {
                mergeSelectedPhotos(mGridAdapter.getSelectedPhotos());


                // switch to the new chosen album
                mLastAlbumName = albumList.get(position).getName();

                setFinishBtnText();
                setAlbumBtnText();

                if (position == 0) {
                    mPhotoLoader.getAllPhotoList(PhotoPickerActivity.this);
                } else {
                    mPhotoLoader.getPhotoListFromAlbum(mLastAlbumName, PhotoPickerActivity.this);
                }

                mAlbumPopup.dismiss();
            }
        });
    }

    /**
     * Collect selected photos from the current album and add them to the global list
     *
     * @param selectedPhotosFromAlbum The photo list from a specific album
     */
    private void mergeSelectedPhotos(ArrayList<Photo> selectedPhotosFromAlbum) {
        Log.d("merge", mGlobalSelected.size() + "  photos have been previously selected");
        Log.d("merge", selectedPhotosFromAlbum.size() + "  photos have been selected from this album");

        int count = 0;

        for (Photo photoFromAlbum : selectedPhotosFromAlbum) {
            if (!isPhotoInList(photoFromAlbum, mGlobalSelected)) {
                mGlobalSelected.add(photoFromAlbum);
                count++;
            }
        }


        Log.d("merge", count + " photos are merged into the global list, total is now   " + mGlobalSelected.size());

    }

    private boolean isPhotoInList(Photo photo, ArrayList<Photo> list) {
        for (Photo p : list) {
            if (photo.getUrl().equals(p.getUrl())) {
                return true;
            }
        }

        return false;
    }

    private void sendResultPhotoList() {
        Intent resultIntent = new Intent();
        ArrayList<String> photoUrls = new ArrayList<>();

        for (Photo photo : mGlobalSelected) {
            photoUrls.add(photo.getUrl());
        }

        resultIntent.putExtra(Constants.KEY_PHOTO_URL_ARRAY, photoUrls);
        resultIntent.setAction(Constants.BROADCAST_SELECTED_PHOTO_LIST);

        setResult(RESULT_OK, resultIntent);
    }

    private void takePhoto(File file) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission
                .CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
            try {
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    FileProvider.getUriForFile(
                            this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            file
                    )
            );


            try {
                startActivityForResult(intent, Constants.TAKE_PHOTO_REQUEST_CODE);

            } catch (ActivityNotFoundException e) {

            }
        } else {//if the permission of writing external storage is not
            // granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    Constants.REQUEST_CAMERA_PERMISSION);

        }
    }

    private void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System
                .currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media
                .EXTERNAL_CONTENT_URI, values);
    }

    private void registerReceiver() {
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (intent.getAction()) {
                        case Constants.BROADCAST_PHOTO_STATUS:
                            Photo photo = intent.getParcelableExtra(Constants.KEY_UPDATED_PHOTO);
                            int position = intent.getIntExtra(Constants.KEY_UPDATED_PHOTO_POSITION, 0);


                            mGridAdapter.notifyDataChanged(photo, position);

                            break;
                        default:
                            break;
                    }

                }
            };
            LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(Constants.BROADCAST_PHOTO_STATUS));

        }
    }


    private class PhotoPickerAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<Photo> albumPhotoList, albumSelectedPhotos;
        private LayoutInflater mInflater;


        private static final int ITEM_CAMERA = 0;
        private static final int ITEM_PHOTO = 1;


        public PhotoPickerAdapter(Context context, LayoutInflater layoutInflater, ArrayList<Photo> photoList) {
            this.context = context;
            this.mInflater = layoutInflater;

            albumSelectedPhotos = new ArrayList<>();
            albumPhotoList = photoList == null ? new ArrayList<Photo>() : photoList;
        }

        @Override
        public int getCount() {
            return mShowCamera ? albumPhotoList.size() + 1 : albumPhotoList.size();
        }

        @Override
        public Photo getItem(int position) {

            if (mShowCamera && position == 0) {
                return null;
            }

            return mShowCamera ? albumPhotoList.get(position - 1) : albumPhotoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            ViewHolder viewHolder;

            if (convertView == null) {
                if (type == ITEM_CAMERA) {
                    convertView = mInflater.inflate(R.layout.pp_camera_item, parent, false);

                    ((ImageView) convertView.findViewById(R.id.camera_item)).setImageResource(mCameraIconResId);
                } else {
                    convertView = mInflater.inflate(R.layout.pp_photo_picker_item, parent, false);

                    viewHolder = new ViewHolder(convertView);
                    convertView.setTag(viewHolder);
                    bindViewWithData(viewHolder, position);
                }
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                bindViewWithData(viewHolder, position);
            }


            return convertView;
        }


        @Override
        public int getItemViewType(int position) {
            if (mShowCamera && position == 0) {
                return ITEM_CAMERA;
            } else {
                return ITEM_PHOTO;
            }
        }

        @Override
        public int getViewTypeCount() {
            return mShowCamera ? 2 : 1;
        }

        private void bindViewWithData(final ViewHolder viewHolder, final int position) {
            if (mShowCamera && position == 0) {
                return;
            }

            final Photo photo = getItem(position);

            Picasso.with(context)
                    .load("file://" + photo.getUrl())
                    .resize(mThumbnailSize, mThumbnailSize)
                    .centerCrop()
                    .error(new ColorDrawable(Color.DKGRAY))
                    .placeholder(new ColorDrawable(Color.BLACK))
                    .into(viewHolder.img);


            viewHolder.checkBox.setChecked(photo.isSelected());

            if (photo.isSelected()) {
                viewHolder.img.setColorFilter(Color.parseColor("#99000000"));
            } else {
                viewHolder.img.clearColorFilter();
            }

            viewHolder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mEnableSinglePhoto) {
                        Intent photoViewerIntent = new Intent(context, PhotoViewerActivity.class);
                        photoViewerIntent.putParcelableArrayListExtra("photo_list", albumPhotoList);
                        photoViewerIntent.putExtra("photo_position", mShowCamera ? position - 1 : position);
                        photoViewerIntent.putExtra("current_count", mGlobalSelected.size());
                        photoViewerIntent.putExtra("max_count", mMaxCount);

                        context.startActivity(photoViewerIntent);
                        ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        updateCheckStatus(viewHolder, photo, position);
                    }
                }
            });

            viewHolder.checkboxContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateCheckStatus(viewHolder, photo, position);
                }
            });
        }

        private void updateCheckStatus(ViewHolder viewHolder, Photo photo, int position) {
            if (mGlobalSelected.size() < mMaxCount || photo.isSelected()) {

                viewHolder.checkBox.toggle();

                boolean isChecked = viewHolder.checkBox.isChecked();
                photo.setSelected(isChecked);

                if (isChecked)
                    addPhotoToSelected(photo);
                else
                    removePhotoFromSelected(photo);

                albumPhotoList.remove(mShowCamera ? position - 1 : position);
                albumPhotoList.add(mShowCamera ? position - 1 : position, photo);

                Log.d("path", photo.getUrl());

                notifyDataSetChanged();
                setFinishBtnText();
            } else {
                Toast.makeText(context, "You can select up to " + mMaxCount + " photos.", Toast.LENGTH_SHORT).show();
            }
        }


        public void notifyDataSetChanged(ArrayList<Photo> photoList, boolean isRefresh) {
            if (isRefresh) {
                this.albumPhotoList.clear();
            }
            if (photoList == null || photoList.size() == 0) {
                this.notifyDataSetChanged();
                return;
            }

            albumSelectedPhotos.clear();


            this.albumPhotoList.addAll(photoList);
            this.notifyDataSetChanged();
        }


        public void addPhotoToSelected(Photo photo) {
            for (Photo p : mGlobalSelected) {
                if (p.getUrl().equals(photo.getUrl())) {
                    return;
                }
            }

            mGlobalSelected.add(photo);

            Log.d("add", "add   " + photo.getUrl());

        }

        public void removePhotoFromSelected(Photo photo) {
            Log.d("remove", "remove");

            for (Photo p : mGlobalSelected) {
                if (p.getUrl().equals(photo.getUrl())) {
                    mGlobalSelected.remove(p);

                    Log.d("remove", "remove  " + p.getUrl() + "    becasue of " + photo.getUrl());

                    return;
                }
            }
        }


        /**
         * Accept single data update from photo viewer activity.
         *
         * @param photo
         * @param position
         */
        public void notifyDataChanged(Photo photo, int position) {
            albumPhotoList.remove(position);
            albumPhotoList.add(position, photo);

            if (photo.isSelected()) {
                addPhotoToSelected(photo);
            } else {
                removePhotoFromSelected(photo);
            }

            setFinishBtnText();

            notifyDataSetChanged();
        }

        class ViewHolder {
            private ImageView img;
            private CheckBox checkBox;
            private RelativeLayout checkboxContainer;

            public ViewHolder(View v) {
                this.img = (ImageView) v.findViewById(R.id.img);
                this.checkBox = (CheckBox) v.findViewById(R.id.img_checkbox);
                this.checkboxContainer = (RelativeLayout) v.findViewById(R.id.img_checkbox_container);

                this.checkBox.setBackground(ContextCompat.getDrawable(context, R.drawable.pp_checkbox));
            }
        }

        /**
         * When new album is loaded, inject the global selected photo list into the album.
         * Set the state of any photo in the album that is included in the global selected photo list to 'selected'.
         *
         * @param globalSelectedPhotos
         * @param albumPhotos
         */
        public void injectSelectedPhotos(ArrayList<Photo> globalSelectedPhotos, ArrayList<Photo> albumPhotos) {
            for (Photo globalPhoto : globalSelectedPhotos) {
                for (Photo albumPhoto : albumPhotos) {
                    if (albumPhoto.getUrl().equals(globalPhoto.getUrl())) {
                        albumPhoto.setSelected(true);
                    }
                }
            }
        }

        public ArrayList<Photo> getSelectedPhotos() {
            return albumSelectedPhotos;
        }

    }
}