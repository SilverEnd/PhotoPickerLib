package com.erikzuo.photopicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;

/**
 * Created by YifanZuo on 6/07/2016.
 */
public class PhotoViewerActivity extends AppCompatActivity {

    private SubsamplingScaleImageView mPhoto;
    private ArrayList<Photo> mPhotoList;
    private int mPhotoPosition;
    private ViewPager mViewPager;
    private PhotoViewerAdapter mAdapter;
    private CheckBox mCheckBox;
    private LinearLayout mCheckboxContainer;
    private int mCurrentCount, mMaxCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pp_activity_photo_viewer);

        initView();
        setContent();
        setListener();
    }


    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.photo_view_pager);
        mPhoto = (SubsamplingScaleImageView) findViewById(R.id.photo);

        mCheckBox = (CheckBox) findViewById(R.id.single_photo_view_checkbox);
        mCheckboxContainer = (LinearLayout) findViewById(R.id.single_photo_view_checkbox_container);
    }

    private void setContent() {
        mPhotoList = getIntent().getParcelableArrayListExtra("photo_list");
        mPhotoPosition = getIntent().getIntExtra("photo_position", 0);
        mCurrentCount = getIntent().getIntExtra("current_count", 0);
        mMaxCount = getIntent().getIntExtra("max_count", 1);

        if (mPhotoList == null) {
            mPhotoList = new ArrayList<>();
        }

        mAdapter = new PhotoViewerAdapter(this, mPhotoList, getLayoutInflater(), true);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mPhotoPosition);
        mViewPager.setOffscreenPageLimit(3);

        mCheckBox.setChecked(mPhotoList.get(mViewPager.getCurrentItem()).isSelected());


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle((mPhotoPosition + 1) + "/" + mPhotoList.size());
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

    }


    private void setListener() {
        mCheckboxContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("count", mCurrentCount + "");

                if (mCurrentCount < mMaxCount) {
                    mCheckBox.toggle();

                    if (mCheckBox.isChecked()) {
                        mCurrentCount++;
                    } else {
                        mCurrentCount--;
                    }
                } else {
                    Toast.makeText(PhotoViewerActivity.this, "You can select up to " + mMaxCount + " photos.", Toast.LENGTH_SHORT).show();

                }
            }
        });

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Photo photo = mPhotoList.get(mViewPager.getCurrentItem());


                photo.setSelected(isChecked);
                notifyPhotoPickerDataChanged(photo, mViewPager.getCurrentItem());
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCheckBox.setChecked(mPhotoList.get(position).isSelected());

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle((position + 1) + "/" + mPhotoList.size());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, android.R.anim.fade_out);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    private void notifyPhotoPickerDataChanged(Photo photo, int position) {
        Intent intent = new Intent();
        intent.putExtra(Constants.KEY_UPDATED_PHOTO, photo);
        intent.putExtra(Constants.KEY_UPDATED_PHOTO_POSITION, position);
        intent.setAction(Constants.BROADCAST_PHOTO_STATUS);


        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
