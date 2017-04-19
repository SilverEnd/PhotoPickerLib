package com.erikzuo.photopicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by YifanZuo on 12/07/2016.
 */
class AlbumListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Album> mAlbumList;

    private ViewHolder holder;

    public AlbumListAdapter(Context mContext, LayoutInflater mInflater, ArrayList<Album> mAlbumList) {
        this.mContext = mContext;
        this.mInflater = mInflater;
        this.mAlbumList = mAlbumList;
    }

    @Override
    public int getCount() {
        return mAlbumList.size();
    }

    @Override
    public Album getItem(int position) {
        return mAlbumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Album album = mAlbumList.get(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.pp_album_list_item, null);

            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleTv.setText(album.getName() + "(" + album.getPhotoCount() + ")");
        holder.radioButton.setChecked(album.isSelected());
        Picasso.with(mContext)
                .load("file://" + album.getCoverPhotoUrl())
                .error(android.R.drawable.ic_menu_camera)
                .resize(200, 200)
                .centerCrop()
                .into(holder.iconIv);


        return convertView;
    }

    private class ViewHolder {
        TextView titleTv;
        ImageView iconIv;
        RadioButton radioButton;

        public ViewHolder(View view) {
            this.titleTv = (TextView) view.findViewById(R.id.album_title);
            this.iconIv = (ImageView) view.findViewById(R.id.album_item_img);
            this.radioButton = (RadioButton) view.findViewById(R.id.album_radio_btn);
        }
    }


}
