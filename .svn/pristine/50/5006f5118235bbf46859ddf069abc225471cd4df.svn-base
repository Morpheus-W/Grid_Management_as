package com.cn7782.management.android.activity.bean;

import android.os.Parcel;
import android.os.Parcelable;


public class LocationBean implements Parcelable  {

	private String describe;
	private String tableName;
	private double Latitude;
	private double Longitude;
	private String time;
	private double speed;
	private int pathType;
	
	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public double getLatitude() {
		return Latitude;
	}

	public void setLatitude(double latitude) {
		Latitude = latitude;
	}

	public double getLongitude() {
		return Longitude;
	}

	public void setLongitude(double longitude) {
		Longitude = longitude;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public int getPathType() {
		return pathType;
	}

	public void setPathType(int pathType) {
		this.pathType = pathType;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeString(describe);
		dest.writeString(tableName);
		dest.writeDouble(Latitude);
		dest.writeDouble(Longitude);
		dest.writeString(time);
		dest.writeDouble(speed);
		dest.writeInt(pathType);
	}
	public static final Parcelable.Creator<LocationBean> CREATOR = new Parcelable.Creator<LocationBean>(){
		         public LocationBean createFromParcel(Parcel in) 
		         {
		             return new LocationBean(in);
		         }

		         public LocationBean[] newArray(int size) 
		         {
		             return new LocationBean[size];
		         }
     };
     
     private LocationBean(Parcel in) 
     {
    	 this.describe = in.readString();
    	 this.tableName = in.readString();
    	 this.Latitude = in.readDouble();
    	 this.Longitude = in.readDouble();
    	 this.time = in.readString();
    	 this.speed = in.readDouble();
    	 this.pathType = in.readInt();
     }
     public LocationBean(){
    	 
     }
}
