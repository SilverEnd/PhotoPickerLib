package com.erikzuo.photopickerlib;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.erikzuo.photopicker.Photo;
import com.erikzuo.photopicker.PhotoPicker;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_STORAGE_PERMISSION = 0;
    public static final int MULTI_PHOTO_REQUEST_CODE = 0;


    private Button mBtn;
    private GridView mImgGrid;
    private PhotoPickerAdapter1 mAdapter;
    private ArrayList<String> mPhotoUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtn = (Button) findViewById(R.id.btn);
        mImgGrid = (GridView) findViewById(R.id.grid_img_display);


        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                        .READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


                    PhotoPicker.
                            with(MainActivity.this).
                            setMaxCount(4).
                            setNumOfColumns(4).
                            setFinishBtnStyle(Color.BLACK, Color.WHITE).
                            setGridItemMargin(10).
                            setThumbnailSize(150).
                            setPhotoPickerTitle("Choose").
                            enableCamera("pp-sample", "pp-sample-dir").
                            pickPhotos();

                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_STORAGE_PERMISSION);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        PhotoPicker.onPhotoPickerResult(requestCode, resultCode, data, new PhotoPicker.PhotoResult() {
            @Override
            public void onPhotoResult(ArrayList<String> photoUrls) {
                displaySelectedImg(photoUrls);
            }
        });
    }

    private void displaySelectedImg(ArrayList<String> photoUrls) {
        ArrayList<Photo> photos = new ArrayList<>();
        mPhotoUrls = photoUrls;

        for (String url : photoUrls) {
            photos.add(new Photo(url));
        }

        mAdapter = new PhotoPickerAdapter1(this, getLayoutInflater(), photos, false, 0, 0, 200);
        mImgGrid.setAdapter(mAdapter);
    }
}
