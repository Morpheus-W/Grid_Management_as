package com.cn7782.management.android.offlinemaps;

import android.content.Context;



public class GpsLocationClientFactory {
	public static final int CLIENTTYPE_MOBILE_GPS = 1;
	public static final int CLIENTTYPE_BAIDU = 2;
	
	public static GpsLocationClient getGpsLocationClient(int gpsClientType, Context context){
	
		switch(gpsClientType){
			case CLIENTTYPE_BAIDU : 
			//	return new BaiduGpsLocationClient(context);
			case CLIENTTYPE_MOBILE_GPS :
			default : 
				return new MobileGpsLocationClient(context);
		}
	}
}
