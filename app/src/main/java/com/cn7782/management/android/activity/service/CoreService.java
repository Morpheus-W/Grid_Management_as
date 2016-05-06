package com.cn7782.management.android.activity.service;


import com.cn7782.management.android.activity.controller.SensorController;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CoreService extends Service {
	private static final String TAG = "CoreService";
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		SensorController.getInstance().setContext(this);
		SensorController.getInstance().openSensorListiner();
		Log.i(TAG, "on start()");
	}


	@Override
	public void onDestroy() {
		Log.i(TAG, "on destory()");
		// 注销感应器监听
		SensorController.getInstance().closeSensor();
		super.onDestroy();

	}
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
