package com.erikzuo.photopicker;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YifanZuo on 5/07/2016.
 */
class PhotoLoader {

    private static final int IMAGE_SIZE = 1024;


    private ContentResolver resolver;
    private Handler mHandler;

    public PhotoLoader(Context context) {
        this.resolver = context.getContentResolver();
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    public interface OnLoadPhotosListener {
        void OnPhotosLoaded(ArrayList<Photo> photoList);
    }

    public interface OnLoadAlbumsListener {
        void onAlbumsLoaded(ArrayList<Album> albumList);
    }

    public void getAllPhotoList(final OnLoadPhotosListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<Photo> photoList = fetchAllPhotos();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.OnPhotosLoaded(photoList);
                    }
                });
            }
        }).start();
    }


    public void getAlbumList(final OnLoadAlbumsListener listener) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<Album> albums = fetchAlbums();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onAlbumsLoaded(albums);
                    }
                });
            }
        }).start();
    }


    /**
     * Get photos from a particular album
     *
     * @param name     The name of the album
     * @param listener Listener for post-load purpose
     */
    public void getPhotoListFromAlbum(final String name, final OnLoadPhotosListener listener) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<Photo> photos = fetchPhotosFromAlbum(name);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.OnPhotosLoaded(photos);
                    }
                });
            }
        }).start();
    }


    private ArrayList<Photo> fetchAllPhotos() {
        ArrayList<Photo> photos = new ArrayList<>();

        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.ImageColumns.DATA},
                null,
                null,
                MediaStore.Images.ImageColumns.DATE_ADDED + " DESC"
        );

        if (cursor == null) {
            return photos;
        }

        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
            File file = new File(path);

            if (file.exists() && file.length() > 0) {
                photos.add(new Photo(path));
            }
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }


        return photos;
    }

    private ArrayList<Album> fetchAlbums() {
        ArrayList<Album> albumList = new ArrayList<>();
        Map<String, Album> map = new HashMap<String, Album>();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.ImageColumns.SIZE},
                null,
                null,
                null);

        if (cursor == null || !cursor.moveToNext()) {
            return albumList;
        }

        cursor.moveToLast();


        Album current = new Album(
                Constants.DEFAULT_ALBUM_NAME,
                getCoverPhotoPath(cursor),
                0);
        albumList.add(current);

        do {
            if (cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE)) < IMAGE_SIZE)
                continue;

            current.increaseCount();

            String folderName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));

            if (map.keySet().contains(folderName)) {
                map.get(folderName).increaseCount();
            } else {
                Album album = new Album(
                        folderName,
                        getCoverPhotoPath(cursor),
                        1);

                map.put(folderName, album);
                albumList.add(album);
            }
        } while (cursor.moveToPrevious());


        if (!cursor.isClosed()) {
            cursor.close();
        }

        return albumList;
    }

    private ArrayList<Photo> fetchPhotosFromAlbum(String name) {
        ArrayList<Photo> photos = new ArrayList<>();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.ImageColumns.DATE_ADDED,
                        MediaStore.Images.ImageColumns.SIZE},
                "bucket_display_name = ? AND _size > ? ",
                new String[]{name, IMAGE_SIZE + ""},
                MediaStore.Images.ImageColumns.DATE_ADDED + " DESC");

        if (cursor == null)
            return photos;



        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
            File file = new File(path);

            if (file.exists()) {
                photos.add(new Photo(path));
            }
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }

        return photos;
    }

    private String getCoverPhotoPath(Cursor cursor) {
//        File file;
//        int i = 0;
//        do {
//            String path =
//            file = new File(path);
//
//            i++;
//        } while (!file.exists());

        return cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
    }
}