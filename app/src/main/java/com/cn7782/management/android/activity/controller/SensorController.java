package com.cn7782.management.android.activity.controller;

import java.util.LinkedList;
import java.util.List;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseApplication;
import com.cn7782.management.android.activity.FallDownWarnActivity;
import com.cn7782.management.android.constants.GlobalConstant;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SensorController {
	private static final String TAG = "FALLDOWN";
	private static final int LIST_SIZE = 10;

	private Context mContext;
	private static SensorController sensorCtl;
	private float localGValue = GlobalConstant.G_VALUE;
	// private boolean inProcess = false;//是否正在处理变化值，防止数据变化太快
	// 重力感应相关变量
	private List<Sensor> sensors;
	private Sensor sensor;
	private SensorManager sensorManager;

	private float acceleration;
	private LinkedList<Float> gqueue = new LinkedList<Float>();
	public static boolean control = true;// 控制是否触发摔倒

	private CountDownTimer countTimer;
	private boolean isBgCounting = false;

	// 得到本地当前的重力加速度值相关变量
	private boolean isLocalGValueGot = false;
	private LinkedList<Float> gvalueList = new LinkedList<Float>();

	// last sensor event time
	private long lastEventTime = 0;
	private float g;
	private boolean isInCheck = false;


	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.obj != null) {
			}
		};
	};

	/**
	 * 检查传感器得到的值是否有效，防止有些手机传感器出了问题，可能导致的一直报警，正常的重力加速度在0~2g之间
	 * 
	 * @param event
	 * @return
	 */
	private boolean checkValue(SensorEvent event) {
		if (event == null)
			return false;
		//异常情况
		if (Math.abs(event.values[0]) > 50 || Math.abs(event.values[1]) > 50
				|| Math.abs(event.values[2]) > 50) {
			for (int i = 0; i < gqueue.size(); i++) {
				gqueue.remove(i);
			}
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 传感器监听 摔落模型匹配逻辑： 1、100ms采集一次数据，保存相邻的10个数据 2、10个数据中头两个和最后两个为正常g值正负1范围内
	 * 3、10个数据中（中间的6个数据中）出现最小的g值不大于2.5 4、4中出现的最小g值，至少出现1次
	 * 5、最后一个最小值后面第一个或第二个需要大于正常(g-0.5)
	 */
	private SensorEventListener sensorListener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			//该时间戳是基于开机时间
			long nowTime = event.timestamp;
			lastEventTime = nowTime;
			if (!checkValue(event)) {
				Log.i(TAG, "\nvalues invalide!\n");
				return;
			}
			//这里判断用户是否登录
//			boolean isNotLogin = BaseApplication.getInstance().preferences
//					.getString(PreferenceConstant.TOKEN_ID, "").equals("");
			
			boolean isNotLogin = false;
			boolean isForgroundCounting = BaseApplication.getInstance().getIsForegroundCountDownn();

			if (event.sensor.getType() ==	Sensor.TYPE_ACCELEROMETER
					&& !isForgroundCounting && !isBgCounting && !isNotLogin) {

				acceleration = (event.values[0] * event.values[0])
						+ (event.values[1] * event.values[1])
						+ (event.values[2] * event.values[2]);

				g = (float) Math.sqrt(acceleration);
				if (!isLocalGValueGot
						&& Math.abs(g - GlobalConstant.G_VALUE) < 1) {
					getLocalGValue(g);
				}
				gqueue.add(g);
				if(!isInCheck && gqueue.size() > 50){
					if(newFalldownCheck()){
						if (BaseApplication.getInstance().isAppOnForeground(
								mContext.getApplicationContext())) {
							Intent intent = new Intent(
									mContext.getApplicationContext(),
									FallDownWarnActivity.class);
							intent.putExtra("COUNT",
									GlobalConstant.FALLDOWN_COUNT);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							mContext.startActivity(intent);
						} else {
							sendMsgToNotificationCenter();
						}
					}
				}
				
			} else {
				Log.i(TAG, "event.sensor.getType():" + event.sensor.getType());
			}

		}

		/**
		 * 得到当地的g值
		 * 
		 * @param g
		 */
		private void getLocalGValue(float g) {
			if (gvalueList.size() < 10) {
				gvalueList.add(g);
			} else {
				float sum = 0;
				for (int i = 0; i < gvalueList.size(); i++) {
					sum += gvalueList.get(i);
				}
				localGValue = sum / 10;
				isLocalGValueGot = true;
			}
		}

		/**
		 * 采集到的数据串中开始和结尾数据校验
		 * 
		 * @return
		 */
		private boolean beginAndEndDataCheck() {
			if (Math.abs(gqueue.get(0) - localGValue) < 2
					&& Math.abs(gqueue.get(1) - localGValue) < 2
					&& Math.abs(gqueue.get(LIST_SIZE - 2) - localGValue) < 2
					&& Math.abs(gqueue.get(LIST_SIZE - 1) - localGValue) < 2) {

				return true;
			} else {
				return false;
			}
		}
		
		/**
		 * 是否符合摔倒模型核心逻辑处理   新
		 * @return
		 */
		private boolean newFalldownCheck() {
						
			isInCheck = true;
			List<Float> sourceList = gqueue.subList(0, 50);
			int minIndex = -1;
			double minStandValue = 2.5;
			int minValueShowTimes = 0;
			double bigValue = 0;
			double secondBigValue = 0;
			

			
			 if (Math.abs(sourceList.get(0) - localGValue) > 2
						|| Math.abs(sourceList.get(1) - localGValue) > 2
						|| Math.abs(sourceList.get(48) - localGValue) > 2
						|| Math.abs(sourceList.get(49) - localGValue) > 2) {
					isInCheck = false;
					 Log.i("FALLDOWN_T", "begin end check fail!");
					 gqueue.remove(0);
					return false;
			}

			 boolean fitFallDown = false;
			 boolean fitUp = false;
			for (int i = 2; i < sourceList.size(); i++) {
				if(!fitFallDown && sourceList.get(i) < localGValue - 2){
					if(sourceList.get(i+1) < sourceList.get(i)
							&& sourceList.get(i+2) < sourceList.get(i+1) 
							&& sourceList.get(i+3) < sourceList.get(i+2) 
							&& sourceList.get(i+4) < sourceList.get(i+3)){
						fitFallDown = true;
						i += 3;
						Log.i("NEW", "出现递减");
					}
				}
				
				if (fitFallDown && !fitUp && sourceList.get(i) < minStandValue) {
					minIndex = i;
					minValueShowTimes++;
					if(minValueShowTimes >= 4){
						if (i < sourceList.size() - 1 && bigValue < sourceList.get(i + 1)) {
							bigValue = sourceList.get(i + 1);
						}
						if (i < sourceList.size() - 2
								&& secondBigValue < sourceList.get(i + 2)) {
							secondBigValue = sourceList.get(i + 2);
						}						
						if(minIndex != -1 && (bigValue > (localGValue - 1) || secondBigValue > (localGValue - 1))){
							fitUp = true;
							break;
						}
					}
				}
			}

			if (fitUp) {
//				Log.i("NEW", "FALL DOWN");
				gqueue.clear();
				isInCheck = false;
				return true;
			} else {
				gqueue.remove(0);
				isInCheck = false;
				return false;
			}
		}

		/**
		 * 是否符合摔倒模型核心逻辑处理
		 * 
		 * @return
		 */
		private boolean falldownCheck() {
			isInCheck = true;

			int minIndex = -1;
			double minStandValue = 2.5;
			int minValueShowTimes = 0;
			double bigValue = 0;
			double secondBigValue = 0;

			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < gqueue.size(); i++) {
				String temp = "" + gqueue.get(i);
				buf.append(" " + temp);
			}
			 
			if (!beginAndEndDataCheck()) {
				isInCheck = false;
				return false;
			}

			for (int i = 0; i < gqueue.size(); i++) {
				if (gqueue.get(i) < minStandValue) {
					minIndex = i;
					minValueShowTimes++;
					if (i < gqueue.size() - 1 && bigValue < gqueue.get(i + 1)) {
						bigValue = gqueue.get(i + 1);
					}
					if (i < gqueue.size() - 2
							&& secondBigValue < gqueue.get(i + 2)) {
						secondBigValue = gqueue.get(i + 2);
					}
				}
			}

			if (minIndex != -1 && minValueShowTimes >= 2
					&& (bigValue > (localGValue - 1) || secondBigValue > (localGValue - 1))) {


				for (int i = 0; i < gqueue.size(); i++) {
					gqueue.remove(i);
				}

				isInCheck = false;
				return true;
			} else {

				isInCheck = false;
				return false;
			}
		}

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// Log.i("SENDOR", "onAccuracyChanged type:"+arg0.getType());
		}
	};


	// 发送提醒到消息中心
	private void sendMsgToNotificationCenter() {
		NotificationManager mNotificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(
				R.drawable.ic_launcher,
				"系统提醒您：您的手机疑似发生摔落，10秒后向您的同事发起求助,请确认！",
				System.currentTimeMillis());
		// 定义下拉通知栏时要展现的内容信息
		CharSequence contentTitle = "摔落告警";
		CharSequence contentText = "系统提醒您：您的手机疑似发生了摔落，10秒后向同事发起求助,请确认！";
		//该位置发出信息
		Intent notificationIntent = new Intent(mContext,FallDownWarnActivity.class);
		notificationIntent.putExtra("COUNT", GlobalConstant.FALLDOWN_COUNT);
		BaseApplication.getInstance().setFallDownCount(
				GlobalConstant.FALLDOWN_COUNT);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
				notificationIntent, 0);
//		notification.setLatestEventInfo(mContext, contentTitle, contentText,
//				contentIntent);
		mNotificationManager.notify(GlobalConstant.FALL_DOWN_NOTIFICATION_ID,
				notification);
		countTimer = new CountDownTimer(GlobalConstant.FALLDOWN_COUNT * 1000,
				1000) {
			@Override
			public void onTick(long millisUntilFinished) {

				if (!BaseApplication.getInstance().getIsForegroundCountDownn()) {
					BaseApplication.getInstance().setFallDownCount(
							(int) (millisUntilFinished / 1000));
				} else {
					countTimer.cancel();
					isBgCounting = false;
				}
			}

			@Override
			public void onFinish() {
				if (BaseApplication.getInstance().getFallDownCount() < 3) {
					FallDownController.getInstance().setContext(mContext);
					FallDownController.getInstance().setHandler(mHandler);
					FallDownController.getInstance().callFallDownWarn(true);
					isBgCounting = false;
				}
			}
		};
		countTimer.start();
		isBgCounting = true;
	}


	public synchronized static SensorController getInstance() {
		if (sensorCtl == null) {
			sensorCtl = new SensorController();
		}
		return sensorCtl;
	}

	public void setContext(Context context) {
		this.mContext = context;
	}

	public void openSensorListiner() {
		sensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		//从传感器管理器中获得全部的传感器列表  
		sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
		for (Sensor s : sensors) {
		}

		//加速度传感器
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(sensorListener, sensor,
				SensorManager.SENSOR_DELAY_FASTEST);

	}

	public void closeSensor() {
		// 注销感应器监听
		if (sensorManager != null) {
			sensorManager.unregisterListener(sensorListener);
		}
	}
}
