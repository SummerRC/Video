package com.summerrc.com.video;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lvjun
 * @version 1.0
 * @create 2011-6-9 ÏÂÎç01:17:03
 * @see
 */

public class ThumbnailActivity extends Activity {
    public static String TAG = "Thumbnails";
    private GridView gridView;
    private ArrayList<HashMap<String, String>> list;
    private ContentResolver cr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainxp);
        findViews();
    }

    private void findViews() {
        gridView = (GridView) findViewById(R.id.gridview);
        list = new ArrayList<HashMap<String, String>>();
        cr = getContentResolver();
        String[] projection =
                {Thumbnails._ID, Thumbnails.IMAGE_ID, Thumbnails.DATA};
        Cursor cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection,
                null, null, null);
        getColumnData(cursor);

        String[] from =
                {"path"};
        int[] to =
                {R.id.image01};
        ListAdapter adapter = new GridAdapter(this, list, R.layout.img, from,
                to);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(listener);

    }

    private void getColumnData(Cursor cur) {
        if (cur.moveToFirst()) {
            int _id;
            int image_id;
            String image_path;
            int _idColumn = cur.getColumnIndex(Thumbnails._ID);
            int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
            int dataColumn = cur.getColumnIndex(Thumbnails.DATA);

            do {
                // Get the field values
                _id = cur.getInt(_idColumn);
                image_id = cur.getInt(image_idColumn);
                image_path = cur.getString(dataColumn);

                // Do something with the values.
                Log.i(TAG, _id + " image_id:" + image_id + " path:"
                        + image_path + "---");
                HashMap hash = new HashMap();
                hash.put("image_id", image_id + "");
                hash.put("path", image_path);
                list.add(hash);

            } while (cur.moveToNext());

        }
    }

    OnItemClickListener listener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // TODO Auto-generated method stub
            String image_id = list.get(position).get("image_id");
            Log.i(TAG, "---(^o^)----" + image_id);
            String[] projection =
                    {Media._ID, Media.DATA};
            Cursor cursor = cr.query(Media.EXTERNAL_CONTENT_URI, projection,
                    Media._ID + "=" + image_id, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                String path = cursor.getString(cursor
                        .getColumnIndex(Media.DATA));
                Intent intent = new Intent(ThumbnailActivity.this,
                        ImageViewers.class);
                intent.putExtra("path", path);
                startActivity(intent);
            } else {
                Toast.makeText(ThumbnailActivity.this, "Image doesn't exist!",
                        Toast.LENGTH_SHORT).show();
            }

        }
    };

    class GridAdapter extends SimpleAdapter {

        public GridAdapter(Context context,
                           List<? extends Map<String, ?>> data, int resource,
                           String[] from, int[] to) {
            super(context, data, resource, from, to);
            // TODO Auto-generated constructor stub
        }

        // set the imageView using the path of image
        public void setViewImage(ImageView v, String value) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(value);
                Bitmap newBit = Bitmap
                        .createScaledBitmap(bitmap, 100, 80, true);
                v.setImageBitmap(newBit);
            } catch (NumberFormatException nfe) {
                v.setImageURI(Uri.parse(value));
            }
        }
    }

}
