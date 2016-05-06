package com.cn7782.management.android.activity.controller;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.cn7782.management.util.PictureUtil;

import java.lang.ref.WeakReference;

/**
 * Created by tangweny on 2015/10/10.
 */
public class BitmapWorkerTask extends AsyncTask<String,Void,Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private String path = null;
    @Override
    protected Bitmap doInBackground(String... params) {
        path = params[0];
        return PictureUtil.getSmallBitmap(path);
    }

    public BitmapWorkerTask(ImageView imageView) {
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
