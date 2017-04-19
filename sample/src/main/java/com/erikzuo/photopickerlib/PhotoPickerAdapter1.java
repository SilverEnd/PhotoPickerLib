package com.erikzuo.photopickerlib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.erikzuo.photopicker.Photo;
import com.erikzuo.photopicker.PhotoViewerActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YifanZuo on 5/07/2016.
 */
public class PhotoPickerAdapter1 extends BaseAdapter {

    private Context mContext;
    private ArrayList<Photo> mPhotoList, mSelectedPhotos;
    private LayoutInflater mInflater;
    private boolean mShowCamera;
    private int mMaxCount, mThumbnailSize;
    private int mCameraIconResId;

    private static final int ITEM_CAMERA = 0;
    private static final int ITEM_PHOTO = 1;




    public PhotoPickerAdapter1(Context context, LayoutInflater layoutInflater, ArrayList<Photo> photoList, boolean showCamera, int
            maxCount, int cameraIconResId, int thumbnailSize) {
        this.mContext = context;
        this.mInflater = layoutInflater;
        this.mShowCamera = showCamera;
        this.mMaxCount = maxCount;
        this.mCameraIconResId = cameraIconResId;
        this.mThumbnailSize = thumbnailSize;

        mSelectedPhotos = new ArrayList<>();

        mPhotoList = photoList == null ? new ArrayList<Photo>() : photoList;
    }

    public boolean isShowCamera() {
        return mShowCamera;
    }

    @Override
    public int getCount() {
        return mShowCamera ? mPhotoList.size() + 1 : mPhotoList.size();
    }

    @Override
    public Photo getItem(int position) {

        if (mShowCamera && position == 0) {
            return null;
        }

        return mShowCamera ? mPhotoList.get(position - 1) : mPhotoList.get(position);
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
                convertView = mInflater.inflate(com.erikzuo.photopicker.R.layout.pp_camera_item, parent, false);
//                (im)
            } else {
                convertView = mInflater.inflate(com.erikzuo.photopicker.R.layout.pp_photo_picker_item, parent, false);

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

    private void bindViewWithData(final ViewHolder viewHolder, final int position) {
        if (mShowCamera && position == 0) {
            return;
        }

        final Photo photo = getItem(position);

        Picasso.with(mContext)
                .load("file://" + photo.getUrl())
                .resize(mThumbnailSize, mThumbnailSize)
                .centerCrop()
                .error(new ColorDrawable(Color.DKGRAY))
                .placeholder(new ColorDrawable(Color.BLACK))
                .into(viewHolder.img);

        viewHolder.checkBox.setChecked(photo.isSelected());

        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoViewerIntent = new Intent(mContext, PhotoViewerActivity.class);
                photoViewerIntent.putParcelableArrayListExtra("photo_list", mPhotoList);
                photoViewerIntent.putExtra("photo_position", mShowCamera ? position - 1 : position);

                mContext.startActivity(photoViewerIntent);
                ((Activity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });
        viewHolder.checkboxContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (((PhotoPickerActivity) mContext).getGlobalSelectedCount() < mMaxCount || photo.isSelected()) {
//                    viewHolder.checkBox.toggle();
//
//                    try {
//                        ((PhotoPickerActivity) mContext).setFinishBtnText();
//                    } catch (Exception e) {
//                        Log.e("error", e.toString());
//                    }
//                } else {
//                    Toast.makeText(mContext, "You can select up to " + mMaxCount + " photos.", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                photo.setSelected(isChecked);

                if (isChecked) {
                    addPhotoToSelected(photo);
                } else {
                    removePhotoFromSelected(photo);
                }
            }
        });


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


    public void notifyDataSetChanged(ArrayList<Photo> photoList, boolean isRefresh) {
        if (isRefresh) {
            this.mPhotoList.clear();
        }
        if (photoList == null || photoList.size() == 0) {
            this.notifyDataSetChanged();
            return;
        }

        mSelectedPhotos.clear();


        this.mPhotoList.addAll(photoList);
        this.notifyDataSetChanged();
    }


    public void addPhotoToSelected(Photo photo) {
        for (Photo p : mSelectedPhotos) {
            if (p.getUrl().equals(photo.getUrl())) {
                return;
            }
        }

        mSelectedPhotos.add(photo);
    }

    public void removePhotoFromSelected(Photo photo) {
        Log.d("remove", "remove");

        for (Photo p : mSelectedPhotos) {
            if (p.getUrl().equals(photo.getUrl())) {
                mSelectedPhotos.remove(p);

                Log.d("remove", "remove  " + p.getUrl() + "    becasue of " + photo.getUrl());

                return;
            }
        }
    }


    public void notifyDataChanged(Photo photo, int position) {
        mPhotoList.remove(position);
        mPhotoList.add(position, photo);

        notifyDataSetChanged();
    }

    public static class ViewHolder {
        private ImageView img;
        private CheckBox checkBox;
        private RelativeLayout checkboxContainer;

        public ViewHolder(View v) {
            this.img = (ImageView) v.findViewById(com.erikzuo.photopicker.R.id.img);
            this.checkBox = (CheckBox) v.findViewById(com.erikzuo.photopicker.R.id.img_checkbox);
            this.checkboxContainer = (RelativeLayout) v.findViewById(com.erikzuo.photopicker.R.id.img_checkbox_container);
        }
    }

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
        return mSelectedPhotos;
    }


}
