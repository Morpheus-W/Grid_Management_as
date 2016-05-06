package com.cn7782.management.android.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.activity.bean.LocateRequest;
import com.cn7782.management.android.activity.bean.LocationBean;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.http.HttpClient;
import com.cn7782.management.http.MyAsyncHttpResponseHandler;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.cn7782.management.util.TrackSumbitUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapAcitivity extends BaseActivity implements OnClickListener {

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private String id;
	// 初始化全局 bitmap 信息，不用时及时 recycle
	BitmapDescriptor bdA = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_st);
	BitmapDescriptor bdB = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_en);
	private List<LocationBean> mlist;
	private String userName,historyTime,remark;
	private int length,timeconsuming;
	private CoordinateConverter converter = new CoordinateConverter();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_map);
		id = getIntent().getStringExtra("way_id");
		
		initView();
		getGrid();
	}

	private void getGrid() {
		String tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				MapAcitivity.this);
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("way_id", id);
		HttpClient.post(MapAcitivity.this, ActionUrl.HITTORY_DETAIL, param,
				new MyAsyncHttpResponseHandler(MapAcitivity.this, "请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("查询成功")) {
									if (mlist == null) {
										mlist = new ArrayList<LocationBean>();
									}
									userName = jsonObject.isNull("userName") ?
											"" : jsonObject.getString("userName");
									historyTime = jsonObject.isNull("start_time") ?
											"" : jsonObject.getString("start_time");
									timeconsuming = jsonObject
											.isNull("duration") ? 0 : jsonObject.getInt("duration");
									length = jsonObject.isNull("length") ?
											0 : jsonObject.getInt("length");
									remark = jsonObject.isNull("remark") ?
											"" : jsonObject.getString("remark");

									JSONArray myDetailList = jsonObject
											.getJSONArray("location");

									LatLng startPoint = null;
									LatLng endPoint = null;

									for (int i = 0; i < myDetailList.length(); i++) {
										JSONObject jo1 = null;
										try {
											jo1 = myDetailList.getJSONObject(i);
										} catch (JSONException e) {
											e.printStackTrace();
										}

										int pathType = jo1.optInt("pathType");
										String locList = jo1.optString("locList");

										Log.i("record", jo1.toString());

										LatLng[] latLngs = setList(locList);

										Log.i("coder", "===type=======:" + pathType);
										drawLine(latLngs, pathType);

										if (i == 0)
											startPoint = latLngs[0];

										if (i == myDetailList.length() - 1)
											endPoint = latLngs[latLngs.length - 1];

									}
									initOverlay(startPoint, bdA);
									initOverlay(endPoint, bdB);
									// 设定中心点坐标
									LatLng cenpt = endPoint;
									// 定义地图状态
									MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(18)
											.build();
									// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
									MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
											.newMapStatus(mMapStatus);
									// 改变地图状态
									mBaiduMap.setMapStatus(mMapStatusUpdate);

								}
							}
							initData();
						} catch (JSONException e) {
							Log.e("json", e.getMessage());
						}
					}
				});
	}

	private void initView() {

		findViewById(R.id.title_back).setOnClickListener(this);

		mMapView = (MapView) findViewById(R.id.bmapView);

		mBaiduMap = mMapView.getMap();

	}
	public void initOverlay(LatLng ll,BitmapDescriptor bd){
		//绘制起止点
		OverlayOptions oo = new MarkerOptions().position(ll).icon(bd)
				.zIndex(3).draggable(true);
		mBaiduMap.addOverlay(oo);
	}
	public void drawLine(LatLng[] lls,int type) {
		if (lls == null) {
			return;
		}
		int count = lls.length;
		if (count == 0) {
			return;
		}
		List<LatLng> pts = new ArrayList<LatLng>();
		for (int i = 0; i < count; i++) {
			pts.add(lls[i]);
		}
		
		OverlayOptions ooPolygon = null;
		if(type == 1){
			
			ooPolygon = new PolylineOptions().width(5)
					.color(0xAAFF0000).points(pts);
		}else{
			ooPolygon = new PolylineOptions().width(5)
					.color(0xAA00FF00).points(pts);
			
		}
		mBaiduMap.addOverlay(ooPolygon);
	}
	public void initData() {
		((TextView) findViewById(R.id.history_name)).setText(userName);
		((TextView) findViewById(R.id.history_time)).setText(historyTime);
		
		String duration = (timeconsuming<60?timeconsuming+"秒":
			(timeconsuming%60==0?timeconsuming/60+"分钟":timeconsuming/60+"分钟"+"|"+timeconsuming%60+"秒"));
		((TextView) findViewById(R.id.history_duration)).setText("耗时:"+ duration.replace('|', '\n'));
		
		((TextView) findViewById(R.id.history_distance)).setText("路长:"+length+"m");
		
		((TextView) findViewById(R.id.history_remark)).setText(remark);
	}
	private LatLng[] setList(String detailList){
		converter.from(CoordType.GPS);
		List<LocateRequest> list = TrackSumbitUtil.paraseLocateRequest(detailList);
		
		Log.i("record", "list: " + list.size());
		
		LatLng[] geoPoints = new LatLng[list.size()];
		for(int i = 0; i < list.size(); i++)
		{
			LocateRequest wtm = list.get(i);
			LatLng pt = new LatLng(wtm.getLatitude(), wtm.getLongitude());
			converter.coord(pt);
			LatLng desLatLng = converter.convert();
			geoPoints[i] = desLatLng;
		}
		
		return geoPoints;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			finish();
			break;
		}
	}

	@Override
	protected void onDestroy() {

		if (mMapView != null)
			mMapView.onDestroy();

		super.onDestroy();
		bdA.recycle();
		bdB.recycle();
	}
}
