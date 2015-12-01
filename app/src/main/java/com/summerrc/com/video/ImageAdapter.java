package com.summerrc.com.video;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Map<Bitmap, String>> thumbnailList;
    private int mGalleryItemBackground;

    public ImageAdapter(Context c, ArrayList<Map<Bitmap, String>> thumbnail) {
        context = c;
        thumbnailList = thumbnail;
        TypedArray typArray = c.obtainStyledAttributes(R.styleable.MediaTestActivity);
        mGalleryItemBackground = typArray.getResourceId(R.styleable.MediaTestActivity_android_galleryItemBackground, 0);
        typArray.recycle();
    }

    @Override
    public int getCount() {
        return thumbnailList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        ImageView image = new ImageView(context);
        Set<Entry<Bitmap, String>> set = thumbnailList.get(arg0).entrySet();

        for (Entry<Bitmap, String> entryObj : set) {
            image.setImageBitmap(entryObj.getKey());
        }
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setLayoutParams(new Gallery.LayoutParams(272, 176));
        image.setBackgroundResource(mGalleryItemBackground);
        return image;
    }

}
