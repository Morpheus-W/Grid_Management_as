package com.cn7782.management.android.offlinemaps;

import java.util.Iterator;

import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MobileGpsLocationClient implements GpsLocationClient {

	private static final String TAG = MobileGpsLocationClient.class
			.getSimpleName();

	private LocationManager locationManager;
	private Context context;
	private Location location;

	private static final int MINTIME = 10;// 10秒更新一次

	/**
	 * 
	 */
	public MobileGpsLocationClient(Context context) {
		this.context = context;
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		init();
	}

	public boolean gpsIsOpen() {
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return false;
		}
		return true;
	}

	public LocationManager getLocationManager() {
		return locationManager;
	}

	private void init() {
		Toast.makeText(this.context, "gps 初始化", Toast.LENGTH_SHORT).show();

		// 判断GPS是否正常启动
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(context, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
			// 返回开启GPS导航设置界面
			return;
		}

		// 为获取地理位置信息时设置查询条件
		Criteria criteria = new Criteria();
		// String bestProvider = locationManager.getBestProvider(getCriteria(),
		// true);
		String bestProvider = locationManager.getBestProvider(criteria, true);

		if (bestProvider != null && !bestProvider.equals("")) {

			// 获取位置信息
			// 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
			Location location = locationManager
					.getLastKnownLocation(bestProvider);
			updateLocation(location);
			// 监听状态
			locationManager.addGpsStatusListener(listener);
			// 绑定监听，有4个参数
			// 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
			// 参数2，位置信息更新周期，单位毫秒
			// 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
			// 参数4，监听
			// 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新

			// 1秒更新一次，或最小位移变化超过1米更新一次；
			// 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, MINTIME, 1, locationListener);

		} else {
			Toast.makeText(this.context, "No Provider Found",
					Toast.LENGTH_SHORT).show();
		}

	}

	// 状态监听
	GpsStatus.Listener listener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			switch (event) {
			// 第一次定位
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				Log.i(TAG, "第一次定位");
				break;
			// 卫星状态改变
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				Log.i(TAG, "卫星状态改变");
				// 获取当前状态
				GpsStatus gpsStatus = locationManager.getGpsStatus(null);
				// 获取卫星颗数的默认最大值
				int maxSatellites = gpsStatus.getMaxSatellites();
				// 创建一个迭代器保存所有卫星
				Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
						.iterator();
				int count = 0;
				while (iters.hasNext() && count <= maxSatellites) {
					GpsSatellite s = iters.next();
					count++;
				}
				Log.i(TAG, "搜索到：" + count + "颗卫星");
				break;
			// 定位启动
			case GpsStatus.GPS_EVENT_STARTED:
				Log.i(TAG, "定位启动");
				break;
			// 定位结束
			case GpsStatus.GPS_EVENT_STOPPED:
				Log.i(TAG, "定位结束");
				break;
			}
		};
	};

	private void updateLocation(Location location) {
		this.location = location;
	}

	public GpsLocation getLocation() {
		if (location == null) {
			return null;
		}
		GpsLocation gpsLocation = new GpsLocation(location);
		return gpsLocation;
	}

	// 位置监听
	private LocationListener locationListener = new LocationListener() {

		/**
		 * 位置信息变化时触发
		 */
		public void onLocationChanged(Location location) {
			updateLocation(location);
			Log.i(TAG, "时间：" + location.getTime());
			Log.i(TAG, "经度：" + location.getLongitude());
			Log.i(TAG, "纬度：" + location.getLatitude());
			Log.i(TAG, "海拔：" + location.getAltitude());
		}

		/**
		 * GPS状态变化时触发
		 */
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			// GPS状态为可见时
			case LocationProvider.AVAILABLE:
				Log.i(TAG, "当前GPS状态为可见状态");
				break;
			// GPS状态为服务区外时
			case LocationProvider.OUT_OF_SERVICE:
				Log.i(TAG, "当前GPS状态为服务区外状态");
				break;
			// GPS状态为暂停服务时
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Log.i(TAG, "当前GPS状态为暂停服务状态");
				break;
			}
		}

		/**
		 * GPS开启时触发
		 */
		public void onProviderEnabled(String provider) {
			Location location = locationManager.getLastKnownLocation(provider);
			updateLocation(location);
		}

		/**
		 * GPS禁用时触发
		 */
		public void onProviderDisabled(String provider) {
			updateLocation(null);
		}

	};

}
