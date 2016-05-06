package com.cn7782.management.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.easemob.util.ImageUtils;
import com.easemob.util.PathUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MedieaPlayerUtil {
	Context context;
	MediaPlayer mMediaPlayer = null;

	public MediaPlayer getmMediaPlayer() {
		return mMediaPlayer;
	}

	public MedieaPlayerUtil(Context context) {

		this.context = context;
		mMediaPlayer = new MediaPlayer();
	}

	/**
	 * 
	 * 播放求助音乐
	 */
	public void playAlarmRing() {

		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		try {

			AssetManager assets = context.getAssets();
			// mMediaPlayer = create(this, assets.open("alarm.wav"));
			final AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			// mMediaPlayer = MediaPlayer.create(context, R.raw.alarm);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
					1);

			mMediaPlayer.setLooping(true);
			mMediaPlayer.prepare();

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (null != mMediaPlayer) {
			mMediaPlayer.start();

		}
	}

	/**
	 * 停止播放
	 */
	public void stopPlay() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	/**
	 * 
	 * 播放到达的音乐音乐
	 */
	public void playArriveAlarmRing() {

		// playRing(R.raw.alarm);

	}

	public void playRing(int resid) {

		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		try {

			AssetManager assets = context.getAssets();
			// mMediaPlayer = create(this, assets.open("alarm.wav"));
			final AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			mMediaPlayer = MediaPlayer.create(context, resid);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
					1);

			mMediaPlayer.setLooping(false);
			mMediaPlayer.prepare();

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (null != mMediaPlayer) {

			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}

			mMediaPlayer.start();

		}
	}

	/****
	 * 
	 * @param url
	 */
	public void playURL(String url) {
		// 通过Uri解析一个网络地址
		Uri uri = Uri.parse(url);
		try {
			mMediaPlayer.setDataSource(context, uri);
			mMediaPlayer.prepare();
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

			final AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
					1);

			mMediaPlayer.setLooping(false);
//			if (mMediaPlayer.isPlaying()) {
//				mMediaPlayer.stop();
//			}
			mMediaPlayer.start();

		} catch (Exception e) {

		}

	}

	public void playFilepath(String url) {
		// 通过文件路径
		try {
			mMediaPlayer.setDataSource(url);
			mMediaPlayer.prepare();
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

			final AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
					1);

			mMediaPlayer.setLooping(false);

			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}

			mMediaPlayer.start();

		} catch (Exception e) {

		}

	}
	public static Bitmap getVideoThumbnail(String path) {
			Bitmap bitMap = null;
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		try {
			mmr.setDataSource(path);
			String title = new File(path).getName();
			String mataDataStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

			int seconds = 0;
			if(mataDataStr!=null){
				seconds = Integer.valueOf(mataDataStr);
			}
			if (seconds > 1) {
				bitMap = mmr.getFrameAtTime(1 * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitMap;
	}
	public static void showVideoThumbView(String videoPath, final ImageView iv) {
		File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
		try {
			FileOutputStream fos = new FileOutputStream(file);
			Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
			ThumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.close();
//            iv.setImageBitmap(PictureUtil.getSmallBitmap(file.getAbsolutePath()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		final String localThumb = file.getAbsolutePath();
		new AsyncTask<Void, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Void... params) {
				if (new File(localThumb).exists()) {
					return ImageUtils.decodeScaleImage(localThumb, 160, 160);
				} else {
					return null;
				}
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				if (result != null) {
					iv.setImageBitmap(result);
				}
			}
		}.execute();
	}
	public static void playVideo(Context context,String path){
		Uri uri = Uri.parse(path);
		//调用系统自带的播放器
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Log.v("URI:::::::::", uri.toString());
		intent.setDataAndType(uri, "video/mp4");
		context.startActivity(intent);
	}
}
