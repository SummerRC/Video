package com.summerrc.com.video;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

/**
 * @author Andrew.Lee
 * @version 1.0
 * @create 2011-6-8 ÏÂÎç03:11:13
 * @see
 */

public class ImageViewers extends Activity {
    private static String TAG = "ImageView";
    private ImageView imageView;
    private Intent intent;
    private Drawable drawable;
    private String imagePath;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.item);
        findViews();
    }

    public void findViews() {
        imageView = (ImageView) findViewById(R.id.image);
        intent = this.getIntent();
        imagePath = intent.getStringExtra("path");
        Log.i(TAG, "image path:" + imagePath + "======");
        drawable = Drawable.createFromPath(imagePath);
        imageView.setImageDrawable(drawable);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add("process");
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getTitle().equals("process")) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix m = new Matrix();
            m.setRotate(45);
            bitmap = Bitmap.createBitmap(bitmap, (width - 100) / 2,
                    (height - 100) / 2, 100, 100, m, true);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setImageBitmap(bitmap);

        }
        return super.onOptionsItemSelected(item);
    }
}