package com.erikzuo.photopicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InterruptedIOException;
import java.util.ArrayList;

/**
 * Created by YifanZuo on 12/07/2016.
 */
class AlbumPopup {
    private ArrayList<Album> mAlbumList;
    private AlertDialog.Builder mDialogBuilder;
    private Dialog mDialog;
    private AlbumListAdapter mAdapter;
    private ListView mListView;
    private int mLastPosition = 0;

    private Context mContext;
    private LayoutInflater mInflater;

    public interface AlbumItemClickListener {
        public void onAlbumItemClick(int position);
    }

    public AlbumPopup(Context context, ArrayList<Album> albumList) {
        this.mAlbumList = albumList;
        this.mContext = context;

        mDialogBuilder = new AlertDialog.Builder(mContext);
        mInflater = ((Activity) mContext).getLayoutInflater();
    }

    public void build(final AlbumItemClickListener listener) {
        LinearLayout layout = (LinearLayout) mInflater.inflate(R.layout
                .pp_album_list_view, null);


        mAdapter = new AlbumListAdapter(mContext, mInflater, mAlbumList);


        mListView = (ListView) layout.findViewById(R.id.album_list_view);
        mDialogBuilder.setView(layout);
        mListView.setAdapter(mAdapter);
        mListView.setItemsCanFocus(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int
                    position, long id) {
                clearSelection();

                Album album = mAlbumList.get(position);
                album.setIsSelected(true);
                mAlbumList.remove(position);
                mAlbumList.add(position, album);

                mAdapter.notifyDataSetChanged();
                mLastPosition = position;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.dismiss();
                    }
                }, 300);

            }
        });


        mDialogBuilder.setCancelable(true);
        mDialog = mDialogBuilder.create();

        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                listener.onAlbumItemClick(mLastPosition);
            }
        });


    }

    public void show() {
        mDialog.show();

        Point size = new Point();
        mDialog.getWindow().getWindowManager().getDefaultDisplay().getSize(size);

        int width = size.x;
        int height = size.y;
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.width = width * 7 / 8;
        params.height = height * 6 / 7;
        params.gravity = Gravity.CENTER;

        mDialog.getWindow().setAttributes(params);
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    private void clearSelection(){
        for (int i = 0; i < mAlbumList.size(); i++){
            Album album = mAlbumList.get(i);
            if (album.isSelected()){
                mAlbumList.remove(i);
                album.setIsSelected(false);
                mAlbumList.add(i, album);
            }
        }
    }



}
