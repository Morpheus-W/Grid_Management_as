package com.cn7782.management.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

public class ActivityUtil {
	public static File mPhotoFile;
	public static String mfolder;
	public static final int PICK_PHOTO_FROM_SYSTEM = 1000;
	public static final int TAKE_PHOTO_USING_CAMERA = 1001;
	public static final int CROP_PHOTO_BY_SYSTEM = 1002;
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
	 * 保存Image的目录名
	 */
	private final static String FOLDER_NAME = "/TuoJiang/Management/Gridphoto";

	public ActivityUtil(Context context) {
		mDataRootPath = context.getCacheDir().getPath();
	}

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
	 * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录
	 * 
	 * @param fileName
	 * @param bitmap
	 * @throws IOException
	 */
	public void savaBitmap(String fileName, Bitmap bitmap) throws IOException {
		if (bitmap == null) {
			return;
		}
		String path = getStorageDirectory();
		File folderFile = new File(path);
		if (!folderFile.exists()) {
			folderFile.mkdir();
		}
		File file = new File(path + File.separator + fileName);
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		bitmap.compress(CompressFormat.JPEG, 100, fos);
		fos.flush();
		fos.close();
	}

	/**
	 * 根据文件地址解析bitmap，OOM时对bitmap进行压缩输出(压缩至1/4或者1/16)
	 * 
	 * @param filePath
	 *            :文件地址
	 * @return
	 */
	public static Bitmap getBitmap(String filePath) {
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
			return BitmapFactory.decodeFile(filePath, options);
		}
		return null;
	}

	public static void startCamera(Activity cx) {
		try {
			creatfile();
			mPhotoFile = new File(getStorageDirectory(), getPhotoFileName());
			final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE,
					null);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
			cx.startActivityForResult(intent, TAKE_PHOTO_USING_CAMERA);
		} catch (Exception e) {
			Toast.makeText(cx, "找不到可以拍照的设备", Toast.LENGTH_LONG).show();
		}
		return;
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
	 * 
	 * 给图片添加时间年月日
	 * 
	 * @return
	 */
	public static String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss",
				Locale.getDefault());
		return dateFormat.format(date) + ".png";
	}

	/**
	 * 
	 * 给文件夹添加时间年月日
	 * 
	 * @return
	 */
	public static String setFolderName(String foldername) {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss",
				Locale.getDefault());
		return dateFormat.format(date) + foldername;
	}

	/**
	 * 获取调用照片裁剪的Intent.
	 * <p>
	 * 现代手机的相片尺寸都比较大， 为了增强体验效果，根据android源码分析，采用大尺寸裁剪；
	 * 
	 * @see com.android.camera.CropImage
	 * @param photoUri
	 *            ：输出照片的URI
	 * @param outputX
	 *            ：输出照片的长度;
	 * @param outputY
	 *            ：输出照片的宽度;
	 * @return 裁剪照片的Intent
	 */
	public static Intent getCropImageIntent(Uri photoUri, int outputX,
			int outputY) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("noFaceDetection", false);
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
		return intent;
	}

	/**
	 * 启动手机中安装的程序选取照片
	 * 
	 * @see #getCropImageIntent(Uri, int, int)
	 * @param cx
	 *            :启动程序的上下文；
	 * @param outputX
	 *            :输出图片长度；
	 * @param outputy
	 *            :输出图片宽度；
	 */
	public static void selectPhoto(Activity cx, int outputX, int outputy) {
		if (null == cx) {
			return;
		}
		try {
			Intent mIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
			mIntent.setType("image/*");
			mIntent.putExtra("crop", "true");
			mIntent.putExtra("aspectX", 1);
			mIntent.putExtra("aspectY", 1);
			mIntent.putExtra("outputX", outputX);
			mIntent.putExtra("outputY", outputy);
			mIntent.putExtra("return-data", true);
			cx.startActivityForResult(mIntent, PICK_PHOTO_FROM_SYSTEM);
		} catch (Exception e) {

		}
		return;
	}

	/**
	 * 检测当前是否有活动网络
	 * 
	 * @param mActivity
	 * @return
	 */
	public static boolean isNetworkAvailable(Context me) {
		if (null == me) {
			return false;
		}

		ConnectivityManager connectivity = (ConnectivityManager) me
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		}

		NetworkInfo[] info = connectivity.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获取当前时间
	 * 
	 */
	public static String getTimeNow() {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy年MM月dd日    HH:mm:ss     ");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		return str;
	}

	/**
	 * 图片变圆处理
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	/**
	 * return 返回sdcard的可用空间,单位是bytes。
	 */
	public static Long getAvaliableSpaceAboutSdcard() {
		if (isSdcardExist()) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long blockSize = sf.getBlockSize(); // 每块的大小。
			long availCount = sf.getAvailableBlocks();// 剩余的块数。

			// long blockCount = sf.getBlockCount(); //总的块数。
			// data目录的空间
			// StatFs sf = new StatFs("/data");
			// long blockSize = sf.getBlockSize();
			// long availCount = sf.getAvailableBlocks();

			long size = availCount * blockSize;
			System.out.println("内存卡的剩余空间为：" + (size / 1024 / 1024) + "M");
			return size;
		} else {
			return 0L;
		}
	}

	/**
	 * 判断sdcard是否存在。
	 * 
	 * @return
	 */
	public static boolean isSdcardExist() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {// sdcard存在
			return true;
		} else {
			return false;
		}
	}
}
