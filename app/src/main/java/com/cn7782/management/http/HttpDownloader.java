package com.cn7782.management.http;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import com.cn7782.management.config.ConfigUtil;
import com.cn7782.management.util.ActivityUtil;
import com.cn7782.management.util.FileUtil;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class HttpDownloader {
	private URL url;
	public static final int MSG_DOWNING = 1; // 下载中
	public static final int MSG_FINISH = 2; // 下载完成
	public static final int MSG_FAILURE = 3;// 下载失败
	public static final int SDCARD_NO_SPACE = 4;// 下载失败
	public static final int FILE_EXISTER = 5;// 下载失败

	/**
	 * 
	 * @param destUrl
	 *            目标url
	 * @param storePath
	 *            存储路径
	 * @param handler
	 *            下载中1，下载完成 2，下载失败3,存储空间不足4，
	 * @return true，下载成功，false下载失败。
	 */
	public static boolean downLoadFile(String destUrl, String storePath,
			String fileName, Handler handler,long filesize) {
		File fileDest = null;
		if (destUrl != null && !"".equals(destUrl)) {
			FileOutputStream fos = null;
			BufferedInputStream bis = null;
			HttpURLConnection httpUrl = null;
			URL url = null;
			byte[] buf = new byte[10 * 1024];
			int size = 0;
			int fileSize = -1;
			int downFileSize = 0;
			int progress = 0;// 当前的进度。
			try {
				if (destUrl.getBytes().length == destUrl.length()) {
					url = new URL(destUrl);
				} else {
					url = new URL(URLEncoder.encode(destUrl, "UTF-8"));
				}
				httpUrl = (HttpURLConnection) url.openConnection();
				httpUrl.setConnectTimeout(10000);// jdk 1.5换成这个,连接超时
				httpUrl.setReadTimeout(10000);// jdk 1.5换成这个,读操作超时
				httpUrl.setDoInput(true);
				// 连接指定的资源
				httpUrl.connect();
				fileSize = httpUrl.getContentLength();
				Log.e("fileSize", "" + httpUrl.getContentLength());
				if (fileSize > ActivityUtil.getAvaliableSpaceAboutSdcard()) {
					System.out.println("sdcard 容量不足了，无法下载文件。");
					httpUrl.disconnect();
					handler.sendEmptyMessage(SDCARD_NO_SPACE);
					return false;
				}

				// 获取网络输入流
				bis = new BufferedInputStream(httpUrl.getInputStream());
				// 建立文件
				// int index = destUrl.lastIndexOf("/");
				// if (index != -1) {// 能够取到文件名字，
				// String TargetFileName = destUrl.substring(index +
				// 1).trim();// 获取文件的名字。
				File fileDir = new File(storePath);
				if (!fileDir.exists()) {
					fileDir.mkdirs();
				}
				fileDest = new File(fileDir, fileName);
				if (!fileDest.exists()) {
					fileDest.createNewFile();
				}

				fos = new FileOutputStream(fileDest);
				// 保存文件
				while ((size = bis.read(buf)) != -1) {
					fos.write(buf, 0, size);
					downFileSize += size;
					// 下载进度
					progress = (int) (downFileSize * 100.0 / filesize);
					if (downFileSize == (int)filesize) {
						// 下载完成
						Message msg = handler.obtainMessage();
						msg.what = MSG_FINISH;
						msg.arg1 = progress;
						// ProjectFinishAwardActivity.file2 = new
						// File(storePath, fileName);
						// if (ProjectFinishAwardActivity.file2.exists()) {
						handler.sendMessage(msg);
						System.out.println("下载完成");
						// }
					} else {
						Message msg = handler.obtainMessage();
						msg.what = MSG_DOWNING;
						msg.arg1 = progress;
						handler.sendMessage(msg);
					}
				}// while循环结束
				return true;

				// }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Message msg = handler.obtainMessage();
				msg.obj = e.toString();
				msg.what = 3;
				handler.sendMessage(msg);
				if (fileDest != null && fileDest.exists()) {
					fileDest.delete();
				}
				Log.e("中文", "" + e);
				return false;
			} finally {
				System.out.println("总是执行finally语句，来关闭流。");
				try {
					if (fos != null) {
						fos.close();
					}
					if (bis != null) {
						bis.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Message msg = handler.obtainMessage();
					msg.obj = "关闭操作流时出现异常：" + e.toString();
					msg.what = 3;
					handler.sendMessage(msg);
					if (fileDest != null && fileDest.exists()) {
						fileDest.delete();
					}
				}
				if (httpUrl != null) {
					httpUrl.disconnect();
				}
			}
		}
		return false;
	}
	FileUtil fileUtils=new FileUtil();
	public  int downfile(String urlStr,String path,String fileName)
	{
		if(fileUtils.isFileExist(path+fileName))
			{return 1;}
		else{
		
		try {
			InputStream input=null;
			input = getInputStream(urlStr+File.separator + fileName);
			File resultFile=fileUtils.write2SDFromInput(path, fileName, input);
			if(resultFile==null)
			{
				return -1;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
		return 0;
	}
	  //由于得到一个InputStream对象是所有文件处理前必须的操作，所以将这个操作封装成了一个方法
       public InputStream getInputStream(String urlStr) throws IOException
       {     
    	   InputStream is=null;
    	    try {
				url=new URL(urlStr);
				HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
				is=urlConn.getInputStream();
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
    	    return is;
       }
}
