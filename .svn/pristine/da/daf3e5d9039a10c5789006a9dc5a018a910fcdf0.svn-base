package com.cn7782.management.config;

import java.io.File;

import android.os.Environment;

public class PhotoConfig {
	
	
	public static String HEAD_CACHE_FILE_DIR="/safe_head_cache";

	// 图片的根目录
	public final static File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	
	
	
	public final static String PHOTO_UPLOAD_TEMP_DIR = Environment
			.getExternalStorageDirectory() + "/szpower/upload/";
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public static File getUploadFile(String fileName) {
		File dirFile = new File(PHOTO_UPLOAD_TEMP_DIR);
		
		
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		File photo = new File(dirFile, fileName);
		
		return photo;
	}
}
