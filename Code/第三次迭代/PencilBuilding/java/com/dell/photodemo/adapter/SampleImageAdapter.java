package com.dell.photodemo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by DELL on 2015/9/11.
 */
public class SampleImageAdapter extends BaseAdapter {
    private int[] photoList;
    private Context context;

    public SampleImageAdapter(int[] photoList, Context context) {
        this.photoList = photoList;
        this.context = context;
    }
    public int getCount() {
        return photoList.length;
    }

    public Object getItem(int position) {
        return photoList[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView image = new ImageView(context);
        image.setImageResource(photoList[position]);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setLayoutParams(new Gallery.LayoutParams(100, 100));
        return image;

    }

}
