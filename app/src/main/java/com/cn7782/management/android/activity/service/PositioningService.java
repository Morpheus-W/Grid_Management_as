package com.cn7782.management.android.activity.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.cn7782.management.android.BaseApplication;
import com.cn7782.management.android.activity.bean.LocationBean;
import com.cn7782.management.android.constants.GlobalConstant;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.android.db.DBHelper;
import com.cn7782.management.util.SharedPreferenceUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PositioningService extends Service {

	private List<LocationBean> mlist;
	private Context context;
	private Handler mHandler;
	private LocationManager locationManager;
	/**
	 * 是否开始录制轨迹
	 * */
	private boolean isStartTrack = false;
	/**
	 * 是否开启定位开关
	 * */
	public boolean isOpenTrack = true;
	protected static final String TAG = "GPSTrackManager";
	
	int count = 0; // 总次数
	// 临界的速度值或者临界的次数
	int criticalWalkValue = 6;
	
	private String tablename;
	private DBHelper dp;
	public Cursor cursor;
	
	private List<LatLng> pts = new ArrayList<LatLng>();
	private CoordinateConverter converter = new CoordinateConverter();
	
	// 记录走路路长和开车路长
	private int walking_road, driving_road;
	// 记录走路时长和开车时长
	private  int driving_dur, walking_dur;
	private  double distanceLong = 0.0;
	private int secNum = 0;// 已经开始的秒数
	// 定时器
	private Timer timer;
	private TimerTask task;
	private Timer timer2;
	private TimerTask task2;
	private ArrayList<LocationBean> locations = new ArrayList<LocationBean>();
	public static boolean  mBound = false ; 
	
	private final static int ACCURACY = 8;
	private BigDecimal lastLatitude;
    private BigDecimal lastLongitude;
    
	//该服务作为常驻服务，绑定后实时记录巡防轨迹信息，更新轨迹表数据
	//轨迹记录信息包括：路程，时间
	@Override
	public IBinder onBind(Intent intent) {
		mBound = true;
		return mBind;
	}
	private MsgBind mBind = new MsgBind();
	public class MsgBind extends Binder{
		public PositioningService getService(){
			return PositioningService.this;
		}
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//未解除绑定，再次绑定只会调用开启方法
		mBound = true;
//		if(isStartTrack){
//			RefreshOverlay();
//		}
		return super.onStartCommand(intent, flags, startId);
	}

	public void initService(Context context,DBHelper dp, String mtablename){
		this.context = context;
		this.dp = dp;
		this.tablename = mtablename;
		locationManager = (LocationManager)this.context.getSystemService(Context.LOCATION_SERVICE);
	}
	public void initHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}
	/**
	 * 轨迹录制
	 * */
	public void tracklocations() {
		//判断开关是否开启
		if (!isOpenTrack) {          
			return ;			
		}
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//判断GPS是否正常启动        
				if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
					Toast.makeText(context, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
					return ;
				}
				isStartTrack = true;
				// 设置监听器，自动更新的最小时间为间隔N秒或最小位移变化超过N米
//				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1 * 1000, 0,locationListener);
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1 * 1000, 0,locationListener);	
			}
		}, 0);
	}
	public void initTimer() {
		
		//定时器执行定时任务,更新时间和距离
		if(timer == null)
			timer = new Timer();
		if(task == null){
			task = new TimerTask() {
				@Override
				public void run() {
					secNum++;
					Message msg = mHandler.obtainMessage();
					msg.arg1 = secNum;
					msg.arg2 = (int) distanceLong;
					msg.what = 1;
					mHandler.sendMessage(msg);
				}
			};
			timer.schedule(task, 1000, 1000);
		}
		//定时器执行定时任务，更新定位
		if(timer2 == null)
			timer2 = new Timer();
		if(task2 == null){
			task2 = new TimerTask() {
				@Override
				public void run() {
						tracklocations();
				}
			};
			timer2.schedule(task2, 3000, 1000);
		}
	}
	private boolean filter(Location location) {
        BigDecimal longitude = (new BigDecimal(location.getLongitude()))
            .setScale(ACCURACY, BigDecimal.ROUND_DOWN);

        BigDecimal latitude = (new BigDecimal(location.getLatitude()))
            .setScale(ACCURACY, BigDecimal.ROUND_DOWN);

        if (latitude.equals(lastLatitude) && longitude.equals(lastLongitude)) {
            return false;
        }

        lastLatitude = latitude;
        lastLongitude = longitude;
        return true;
    }
	private int ratio = 2;
	/**
	 * 位置监听
	 * */
	private LocationListener locationListener = new LocationListener() {                
		/**         
		 * * 位置信息变化时触发         
		 * */
		@Override
		public void onLocationChanged(Location location) {
			LocationBean loc = null;
			if(location != null && filter(location) ){
				double latitude = location.getLatitude();
				double longitude = location.getLongitude();
				double speed = location.getSpeed();
				// 时间
				long gettime = location.getTime();
				Date d = new Date(gettime);
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String timeData = sdf.format(d);
				loc = initLocate(latitude, longitude, speed,timeData);
				locations.add(loc);
				if(locations.size() == 5){
					//-------异常点筛选
					int[] range = new int[4];
					LatLng ll1 = null;
					LatLng ll2 = null;
					for (int i=0;i<locations.size()-1;i++){
						ll1 = new LatLng(locations.get(i).getLatitude(),locations.get(i).getLongitude());
						ll2 = new LatLng(locations.get(i+1).getLatitude(),locations.get(i+1).getLongitude());
						range[i] = (int)DistanceUtil.getDistance(ll1,ll2);
					}
					int[] result = getArrayResult(range);
					if(result[1]>(ratio * result[0])){
						for (int i=0;i<range.length;i++){
							if (range[i]==result[1]){
								locations.remove(i);
								locations.remove(i);
							}
						}
					}
					ll1 = ll2 = null;
					//----------------------
					ArrayList<LocationBean> ls = new ArrayList<LocationBean>();
					for(LocationBean lb : locations){
						ls.add(lb);
						artificial(tablename, lb);
					}
					//超过4个点，发送消息
					Message msg = mHandler.obtainMessage();
					msg.what = 2;
					Bundle data = new Bundle();
					data.putParcelableArrayList("locations",  ls);
					msg.setData(data);
					mHandler.sendMessage(msg);
					
					//计算距离放入服务中 ls locations
					countDiatance(ls, speed);
					locations.clear();
					ls = null;
				}
			}
			if(locationListener != null){
				locationManager.removeUpdates(locationListener);
			}
		}                
		/**         
		 * * GPS状态变化时触发         
		 * */   
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {            
			switch (status) {            
			//GPS状态为可见时            
			case LocationProvider.AVAILABLE:                
				Log.i(TAG, "当前GPS状态为可见状态");                
				break;            
			//GPS状态为服务区外时            
			case LocationProvider.OUT_OF_SERVICE:                
				Log.i(TAG, "当前GPS状态为服务区外状态");                
				break;            
			//GPS状态为暂停服务时            
			case LocationProvider.TEMPORARILY_UNAVAILABLE:                
				Log.i(TAG, "当前GPS状态为暂停服务状态");                
				break;            
				}        
			}            
		/**         
		 * * GPS开启时触发         
		 * */   
		@Override
		public void onProviderEnabled(String provider) {     
			}            
		/**         
		 * * GPS禁用时触发         
		 * */ 
		@Override
		public void onProviderDisabled(String provider) {       
			
		}        
	};

	private int[] getArrayResult(int[] range) {
		int[] result = new int[2];
		Arrays.sort(range);
		result[0] = range[range.length/2];
		result[1] = range[range.length - 1];
		return result;
	}

	private LocationBean initLocate(double lat,double lng,double speed,String timeData){
		LocationBean loc = new LocationBean();

		loc.setLatitude(lat);
		loc.setLongitude(lng);
		loc.setTime(timeData);
		loc.setSpeed(speed);
		
		loc.setDescribe("");
		loc.setTableName("");
		
		getCriticalPostion(speed);
		boolean pathStatus = SharedPreferenceUtil.getBooleanDataByKey(this, PreferenceConstant.COLOR_STATUS_KEY, false);
		if(pathStatus){
			loc.setPathType(1);
		}
		else{
			loc.setPathType(2);
		}
		return loc;
	}
	/***
	 * 判断临界点
	 * @param speed
	 */
	public boolean getCriticalPostion(double speed) {
		if (speed >= GlobalConstant.DRIVESPEED) {// 已经开车了
										// isSuccess = true;
			count = 0;// 立即切换
			SharedPreferenceUtil.saveBooleanDataToSharePreference(this,PreferenceConstant.COLOR_STATUS_KEY, true);
		} else {
			count++;
			if (count >= criticalWalkValue) {
				SharedPreferenceUtil.saveBooleanDataToSharePreference(this,PreferenceConstant.COLOR_STATUS_KEY, false);
			}
		}
		return false;
	}
	public void artificial(String tablename, LocationBean bean) {
		if (dp == null) {
			Toast.makeText(
					BaseApplication.getInstance().getApplicationContext(),
					"数据库异常", Toast.LENGTH_SHORT).show();
			return;
		}
		
		dp.insertTalbe("", bean.getTime(), bean.getSpeed(), bean.getLatitude(),
				bean.getLongitude(),bean.getPathType(), tablename);
	}
	private void countDiatance(List<LocationBean> locs, double speed) {

		double abc = 0.0;
		LatLng pt1 = null;
		LatLng pt2 = null;
		for(int i=0;i<locs.size()-1;i++){
			LocationBean loc1 = locs.get(i);
			LocationBean loc2 = locs.get(i+1);
			pt1 = new LatLng(loc1.getLatitude(), loc1.getLongitude());
			pt2 = new LatLng(loc2.getLatitude(), loc2.getLongitude());
			abc = abc + DistanceUtil.getDistance(pt1,pt2);
		}
		distanceLong = distanceLong + abc;

		if (speed > GlobalConstant.DRIVESPEED) {
			driving_road = driving_road + (int) abc;
			driving_dur = driving_dur + 5;
		} else {
			walking_road = walking_road + (int) abc;
			walking_dur = walking_dur + 5;
		}
	}
	@Override
	public void onDestroy() {
		if(dp != null)
			dp.close();
		if(cursor != null)
			cursor.close();
		if(timer != null){
			timer.cancel();
		}
		if(timer2 != null){
			timer2.cancel();
		}
		if(locationListener != null){
			locationManager.removeUpdates(locationListener);
		}
		super.onDestroy();
	}
	
}
