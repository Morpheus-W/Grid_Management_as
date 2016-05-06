package com.cn7782.management.util;
import android.text.TextUtils;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.cn7782.management.android.activity.bean.LocateRequest;
import com.cn7782.management.android.activity.bean.LocateRequestBean;
import com.cn7782.management.android.activity.bean.LocationBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrackSumbitUtil
{
	// 分成好多个小组
	public static List<List<LocationBean>> getGeoPointList(List<LocationBean> list)
	{
		List<List<LocationBean>> lists = new ArrayList<List<LocationBean>>();

		for (int i = 0; i < list.size(); i++)
		{
			List<LocationBean> tempList = new ArrayList<LocationBean>();

			for (int j = i; j < list.size(); j++)
			{
				if (list.get(i).getPathType() == list.get(j).getPathType())
				{
					tempList.add(list.get(j));

					if (j == list.size() - 1)
					{
						lists.add(tempList);
						return lists;
					}
				} else
				{
					tempList.add(list.get(j));

					i = j - 1;
					break;
				}
			}
			lists.add(tempList);
		}
		return lists;
	}

	// 拼接提交的参数
	public static String[] getParamStr(List<LocationBean> list3)
	{
		String[] result = new String[2];
		List<List<LocationBean>> list = getGeoPointList(list3);
		int distance = 0;
		StringBuffer sb = new StringBuffer("[");

		for (int i = 0; i < list.size(); i++)
		{
			LocateRequestBean lrb = new LocateRequestBean();
			List<LocateRequest> tempList = new ArrayList<LocateRequest>();

			// 需要的参数
			String startTime = "";
			String endStrTime = "";
			int length = 0;
			String duration = "";
			String pathType = "";
			int offest = 0;
			boolean isWhile = true;
			List<LocationBean> list2 = list.get(i);
			do {
				startTime = list2.get(offest).getTime();
				if(startTime!=null && startTime.length()>4){
					isWhile  =false;
				}else{
					isWhile = true;
					offest++;
				}
			} while (isWhile);
			offest = list2.size()-1;
			do {
				endStrTime = list2.get(offest).getTime();
				if(endStrTime!=null && endStrTime.length()>4){
					isWhile  =false;
				}else{
					isWhile = true;
					offest--;
				}
			} while (isWhile);
			for (int j = 0; j < list2.size(); j++)
			{
				LocationBean loc = list2.get(j);
				double lat = loc.getLatitude();
				double lng = loc.getLongitude();
				String time = loc.getTime();
				double speed = loc.getSpeed();
				LocateRequest lr = new LocateRequest();
				lr.setLatitude(lat);
				lr.setLongitude(lng);
				lr.setLogTime(time);
				lr.setSpeed(speed);
				tempList.add(lr);
				// 距离,计算每两个点之间的距离
				if (j < list2.size() - 1){
					LatLng arg0 = new LatLng(loc.getLatitude(),loc.getLongitude());
					LatLng arg1 = new LatLng(list2.get(j + 1).getLatitude(),list2.get(j + 1).getLongitude());
					length += DistanceUtil.getDistance(arg0,arg1);
				}
					
			}
			duration =counTime(startTime, endStrTime);
			pathType = list2.get(0).getPathType() + "";

			lrb.setLocList(tempList);
			lrb.setStartStrTime(startTime);
			lrb.setEndStrTime(endStrTime);
			lrb.setDuration(duration);
			lrb.setLength(length + "");
			lrb.setPathType(pathType);

			Gson gson = new Gson();

			sb.append(gson.toJson(lrb) + ",");
			distance = distance + length;
		}
		String paramStr = sb.toString();

		if (paramStr.substring(paramStr.length() - 1).equals(","))
		{
			paramStr = paramStr.substring(0, paramStr.length() - 1);
		}
		result[0] = paramStr + "]";
		result[1] = distance + "";
		return result;
	}
	
	private static String counTime(String start,String end){
		SimpleDateFormat myFormatter = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss");
		Date startData,endData;
		try {
			startData = myFormatter.parse( start);
			endData= myFormatter.parse(end);
			float day=(endData.getTime()-startData.getTime())/(1000*60);
			if(day<0){
				return 0+"";
			}
			return day+"";
		} catch (ParseException e) {
			e.printStackTrace();
			return 0+"";
		}
	}
	public static List<LocateRequest> paraseLocateRequest(String jsonArrayStr) {

		List<LocateRequest> resultList = new ArrayList<LocateRequest>();
		if (TextUtils.isEmpty(jsonArrayStr)) {
			return resultList;
		}
		Gson gson = new Gson();

		resultList = gson.fromJson(jsonArrayStr,
				new TypeToken<List<LocateRequest>>() {
				}.getType());

		return resultList;
	}
}
