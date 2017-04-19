package com.erikzuo.photopicker;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;

/**
 * Created by YifanZuo on 6/07/2016.
 */
class PhotoViewerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<Photo> mPhotoList;
    private LayoutInflater mLayoutInflater;
    private boolean mShowCamera;

    public PhotoViewerAdapter(Context context, ArrayList<Photo> photos, LayoutInflater inflater, boolean showCamera) {
        this.mContext = context;
        this.mPhotoList = photos;
        this.mLayoutInflater = inflater;
        this.mShowCamera = showCamera;
    }

    @Override
    public int getCount() {
        return mPhotoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        View itemView = mLayoutInflater.inflate(R.layout.pp_single_photo_view, container, false);
        SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) itemView.findViewById(R.id.photo);

        imageView.setImage(ImageSource.uri("file://" + mPhotoList.get(position).getUrl()));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) mContext).onBackPressed();
            }
        });

        imageView.setMinimumDpi(0);

        imageView.setMaxScale(30);
        imageView.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        imageView.setDoubleTapZoomScale(2);
        imageView.setZoomEnabled(true);
        imageView.setPanEnabled(true);
        imageView.setMinimumScaleType(SubsamplingScaleImageView
                .SCALE_TYPE_CENTER_INSIDE);


        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }


}
