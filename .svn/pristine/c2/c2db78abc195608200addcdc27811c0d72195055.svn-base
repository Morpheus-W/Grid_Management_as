package com.cn7782.management.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.activity.adapter.SpinnerItem;
import com.cn7782.management.android.activity.bean.InfoBean;
import com.cn7782.management.android.activity.bean.MyReportBean;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.http.HttpClient;
import com.cn7782.management.http.MyAsyncHttpResponseHandler;
import com.cn7782.management.util.PictureUtil;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrajectoryActivity extends BaseActivity {
	private HorizontalScrollView mHsv;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LinearLayout mAddPhotoLayout;
	private RelativeLayout rl_nav;
	// 人名
	public static String[] tabTitle;
	// 初始化滑动下标的宽
	private int indicatorWidth;
	private Marker mMarkerA;
	private InfoWindow mInfoWindow;
	// 初始化全局 bitmap 信息，不用时及时 recycle
	BitmapDescriptor bdA = BitmapDescriptorFactory
			.fromResource(R.drawable.point);
	public ImageView headView;
	private List<InfoBean> list;
	private List<MyReportBean> mlist;
	private Spinner titlemenu;
	private SpinnerItem spinnerItem;
	private String userId;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_trajectory);

		getdata2();
		findViewById(R.id.title_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initView() {
		userId = list.get(0).getUserId();
		findViewById(R.id.trajectory_spinner).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(TrajectoryActivity.this,
								TrajectoryListActivity.class);
						intent.putExtra("user_id", userId);
						startActivity(intent);
					}
				});
		mAddPhotoLayout = (LinearLayout) findViewById(R.id.attachment_list);
		rl_nav = (RelativeLayout) findViewById(R.id.rl_nav);
		mHsv = (HorizontalScrollView) findViewById(R.id.mHsv);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		WindowManager wm = this.getWindowManager();
		int width = wm.getDefaultDisplay().getWidth();
		indicatorWidth = width / 5;
		mMapView.showZoomControls(false);
		mMapView.showScaleControl(false);
		mBaiduMap.setOnMapStatusChangeListener(null);

	}

	// 初始化头像列表
	private void initNavigationHSV() {
		mAddPhotoLayout.removeAllViews();
		mBaiduMap.clear();
		if (list == null)
			return;
		View firstview = null;
		for (int i = 0; i < list.size(); i++) {
			LayoutInflater mInflater = (LayoutInflater) this
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			final View rb = (View) mInflater
					.inflate(R.layout.head_layout, null);
			rb.setId(i);
			double Latitude = list.get(i).getLatitude();
			double Longitude = list.get(i).getLongitude();
			LatLng pt = new LatLng(Latitude, Longitude);
			CoordinateConverter converter = new CoordinateConverter();
			converter.from(CoordType.GPS);
			// sourceLatLng待转换坐标
			converter.coord(pt);
			LatLng desLatLng = converter.convert();
			rb.setTag(desLatLng);
			TextView rbt;
			final ImageView rbb;
			rbb = (ImageView) rb.findViewById(R.id.head_iamge);
			rbt = (TextView) rb.findViewById(R.id.head_text);
			rbt.setText(list.get(i).getName());

			rb.setLayoutParams(new LayoutParams(indicatorWidth,
					LayoutParams.MATCH_PARENT));
			final InfoBean id = list.get(i);
			rb.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					userId = id.getUserId();
					changeOverlay((LatLng) rb.getTag(), id);
				}
			});

			PictureUtil.ShowPicture(rbb, TrajectoryActivity.this, list.get(i)
					.getUrl());
			if (i == 0)
				firstview = rb;
			mAddPhotoLayout.addView(rb);
		}
		if (firstview != null)
			firstview.performClick();

	}

	private void getdata2() {
		String tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				TrajectoryActivity.this);
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		HttpClient.post(TrajectoryActivity.this, ActionUrl.HITTORY, param,
				new MyAsyncHttpResponseHandler(TrajectoryActivity.this,
						"请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("查询成功")) {
									JSONArray jo1 = jsonObject
											.getJSONArray("trajectoryPoint");
									if (list == null)
										list = new ArrayList<InfoBean>();
									list.clear();
									for (int i = 0; i < jo1.length(); i++) {
										JSONObject json = jo1.getJSONObject(i);
										InfoBean a = new InfoBean();
										a.setTime(json.isNull("log_time") ? ""
												: json.getString("log_time"));
										
										a.setUserId(json.isNull("user_id") ? ""
												: json.getString("user_id"));
										a.setWayId(json.isNull("way_id") ? ""
												: json.getString("way_id"));
										a.setLatitude(json.isNull("latitude") ? 0.0
												: json.getDouble("latitude"));
										a.setLongitude(json.isNull("longitude") ? 0.0
												: json.getDouble("longitude"));
										a.setName(json.isNull("user_name") ? ""
												: json.getString("user_name"));
										a.setUrl(json.isNull("user_url") ? ""
												: json.getString("user_url"));
										a.setAddress(json.isNull("adress") ? ""
												: json.getString("adress"));
										list.add(a);
									}
									initView();
									initNavigationHSV();
								}

							}
						} catch (JSONException e) {

						}
					}
				});
	}

	private void changeOverlay(final LatLng str, final InfoBean bean) {
		if (bean == null) {
			return;
		}
		mBaiduMap.clear();
		OverlayOptions ooA = new MarkerOptions().position(str).icon(bdA)
				.zIndex(9).draggable(true);
		MapStatus mMapStatus = new MapStatus.Builder().target(str).zoom(18)
				.build();
		// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mMapStatus);
		// 改变地图状态
		mBaiduMap.setMapStatus(mMapStatusUpdate);
		mBaiduMap.addOverlay(ooA);
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker arg0) {
				// TODO Auto-generated method stub

				return false;
			}
		});
		final LayoutInflater inflater = LayoutInflater
				.from(getApplicationContext());
		View convertView = inflater.inflate(R.layout.point_layout, null);

		final ImageView rbb;
		((TextView) convertView.findViewById(R.id.point_name)).setText(bean
				.getName());
		((TextView) convertView.findViewById(R.id.point_time)).setText(bean
				.getTime());
		((TextView) convertView.findViewById(R.id.point_address)).setText(bean
				.getAddress());
		headView = (ImageView) convertView.findViewById(R.id.point_head);
		PictureUtil.ShowPicture(headView, TrajectoryActivity.this,
				bean.getUrl());

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TrajectoryActivity.this,
						MapAcitivity.class);
				intent.putExtra("way_id", bean.getWayId());
				startActivity(intent);
			}
		});
		LatLng ll = str;
		mInfoWindow = new InfoWindow(convertView, ll, -47);
		mBaiduMap.showInfoWindow(mInfoWindow);
	}

	protected void onDestroy() {

		if (mMapView != null)
			mMapView.onDestroy();
		super.onDestroy();
	}

}
