package com.dell.photodemo.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class HistoryImageAdapter extends BaseAdapter {
    private List<String> photoList; // Õº∆¨µÿ÷∑list
    private Context context;

    public HistoryImageAdapter(List<String> photoList, Context context) {
        this.photoList = photoList;
        this.context = context;
    }

    public void addImage(String imageUrl) {
        photoList.add(imageUrl);
    }

    public int getCount() {
        return photoList.size();
    }

    public Object getItem(int position) {
        return photoList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView image = new ImageView(context);
        Bitmap bm = BitmapFactory.decodeFile(photoList.get(position));
        image.setImageBitmap(bm);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setLayoutParams(new Gallery.LayoutParams(100, 100));
        return image;

    }

}
