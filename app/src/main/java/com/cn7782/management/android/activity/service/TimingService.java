package com.cn7782.management.android.activity.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class TimingService extends Service {
	public Timer timer;
	private int timing = 180000;

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	// private LocationMode mCurrentMode;
	public String Latitude, Longitude, speed;
	public static double nLatitude, nLongitude;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.e("123", "ServiceDemo onBind");
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("123", "ServiceDemo onCreate");
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		//定时定位的时间间隔(ms)
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				//发送位置信息
				sendData();
			}
		};
		// 延迟每次延迟10 毫秒 隔5分钟执行一次
		timer.schedule(task, 10, timing);
	}
	
	@Override
	public void onDestroy() {
		if(timer != null){
			timer.cancel();
		}
		if(mLocClient != null){
			mLocClient.unRegisterLocationListener(myListener);
			mLocClient.stop();
		}
		super.onDestroy();
	}

	private void sendData() {
		String tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				TimingService.this);
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("longitude", Longitude);
		param.put("latitude", Latitude);
		param.put("location", "");
		param.put("speed", speed);

		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(ActionUrl.PRIVATE, param,
				new JsonHttpResponseHandler() {
					public void onSuccess(int statusCode, JSONObject response) {
						super.onSuccess(statusCode, response);
						Log.e("good", "good" + Longitude + "," + Latitude);
						Longitude = Latitude = null;
					}

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						super.onFailure(arg0, arg1);
					}
				});
	}

	public class MyLocationListenner implements BDLocationListener {


		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			Latitude = Double.toString(location.getLatitude());
			Longitude = "" + location.getLongitude();
			nLatitude = +location.getLatitude();
			nLongitude = location.getLongitude();
			speed = "" + location.getSpeed();
		}
	}

	
}
