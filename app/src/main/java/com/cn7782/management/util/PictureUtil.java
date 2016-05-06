package com.cn7782.management.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.cn7782.management.R;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PictureUtil {

    /**
     * sd卡的根目录
     */
    private static String mSdRootPath = Environment
            .getExternalStorageDirectory().getPath();
    /**
     * 手机的缓存根目录
     */
    private static String mDataRootPath = null;
    /**
     * 保存Image的目录名 Sus 为公司名 Management 为项目名称 GridMangephoto 为保存头像文件夹
     */
    private final static String FOLDER_NAME = "/TuoJiang/Management/GridMangephoto";

    /**
     * 获取储存Image的目录
     *
     * @return
     */
    public static String getStorageDirectory() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) ? mSdRootPath + FOLDER_NAME
                : mDataRootPath + FOLDER_NAME;
    }

    /**
     * 创建文件夹
     */
    public static void creatfile() {
        String path = getStorageDirectory();
        File folderFile = new File(path);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
    }

    /**
     * 把下载的图片保存到手机上
     *
     * @param bitName
     * @param mBitmap
     * @throws IOException
     */
    public static void saveMyBitmap(String bitName, Bitmap mBitmap)
            throws IOException {
        creatfile();
        File f = new File(bitName);
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取头像，本地缓存有就取本地，没有就取网络
     *
     * @param headview
     * @param context
     * @param url
     */
    public static void ShowPicture(final ImageView headview, Context context,
                                   String url) {
        String tokenId = SharedPreferenceUtil.getValue(
                PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
                context);
        try {
            // 分裂后台传过来url
            String str[] = url.split("/");
            int count = url.split("/").length;
            final String headurl = str[count - 1];
            File file = new File(getStorageDirectory() + File.separator
                    + headurl);
            if (file.exists()) {
                headview.setImageBitmap(ActivityUtil
                        .toRoundBitmap(getBitmap(getStorageDirectory()
                                + File.separator + headurl)));
            } else {

                String a = ActionUrl.URL
                        + "city_grid/mobile/announcement/showPic?token_id="
                        + tokenId + "&path=" + url;

                headview.setBackgroundDrawable(context.getResources()
                        .getDrawable(R.drawable.default_header));
                ImageLoader.getInstance().loadImage(a,
                        new SimpleImageLoadingListener() {

                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view,
                                        loadedImage);

                                try {
                                    saveMyBitmap(getStorageDirectory()
                                                    + File.separator + headurl,
                                            loadedImage);
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }

                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据文件地址解析bitmap，OOM时对bitmap进行压缩输出(压缩至1/4或者1/16)
     *
     * @param filePath :文件地址
     * @return
     */
    public static Bitmap getBitmap(String filePath) {
        Bitmap it = null;
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File fileSrc = new File(filePath);
        if (null != fileSrc && fileSrc.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inPurgeable = true;// 同时设置才会有效 当系统内存不足就回收
            options.inInputShareable = true;
            it = BitmapFactory.decodeFile(filePath, options);
        }
        return it;
    }

    public static Bitmap getSmallBitmap(String filePath) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);

    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * 根据路径删除图片
     *
     * @param path
     */
    public static void deleteTempFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 添加到图库
     */
    public static void galleryAddPic(Context context, String path) {
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * 获取保存图片的目录
     *
     * @return
     */
    public static File getAlbumDir() {
        File dir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                getAlbumName());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 获取保存 隐患检查的图片文件夹名称
     *
     * @return
     */
    public static String getAlbumName() {
        return "Grid";
    }
}
