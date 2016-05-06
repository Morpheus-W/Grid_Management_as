package com.cn7782.management.android.offlinemaps;

import android.location.Location;

import com.baidu.location.BDLocation;

public class GpsLocation {
	public static GpsLocation LASTESTLOCATION;
	/**
	 * 经度
	 */
	private double longitude;
	/**
	 * 纬度
	 */
	private double latitude;

	private String address;

	public GpsLocation(Location location) {
		longitude = location.getLongitude();
		latitude = location.getLatitude();

		LASTESTLOCATION = this;
	}

	public GpsLocation(BDLocation location) {
		longitude = location.getLongitude();
		latitude = location.getLatitude();
		address = location.getAddrStr();

		LASTESTLOCATION = this;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public String getAddress() {
		if (address == null) {
			return "";
		}
		return address;
	}

}
