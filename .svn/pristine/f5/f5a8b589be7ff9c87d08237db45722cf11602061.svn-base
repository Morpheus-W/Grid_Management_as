package com.cn7782.management.android.activity;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.cn7782.management.R;
import com.cn7782.management.android.activity.bean.SearchBean;
import com.cn7782.management.android.activity.service.TimingService;
import com.cn7782.management.fragment.ChooseAddress;
import com.cn7782.management.fragment.ChooseAddress.MyListener;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ChooseAddressActivity extends FragmentActivity implements
		MyListener, OnGetGeoCoderResultListener, OnGetPoiSearchResultListener,
		OnGetSuggestionResultListener,OnClickListener {
	public String[] tabTitle = { "全部", "住宅", "公司", "小卖部" }; // 标题
	public final String ARGUMENTS_NAME = "arg";
	private ViewPager mViewPager;
	private HorizontalScrollView mHsv;
	// 初始化滑动下标的宽
	private int indicatorWidth;
	TabFragmentPagerAdapter mAdapter;
	private RadioGroup rg_nav_content;
	private int currentIndicatorLeft = 0;
	private ImageView iv_nav_indicator;
	private GeoCoder mSearch = null; // 搜索模块
	// 记录地址
	private String address = "";
	// 搜索功能
	private PoiSearch mPoiSearch = null;

	private ArrayList<List<SearchBean>> mlist;
	private int mcount = 0;
	private List<ChooseAddress> framlist;

	// 地图
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	// 初始化全局 bitmap 信息，不用时及时 recycle
	BitmapDescriptor bdA = BitmapDescriptorFactory
			.fromResource(R.drawable.point);
	BitmapDescriptor bdB = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_geo);
	private LatLng mpt;

	private ProgressDialog progressDialog;

	public static final int ADDRESS = 3001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choose_address);
		findViewById(R.id.title_back).setOnClickListener(this);
		
		progressDialog = new ProgressDialog(this);
		showProgressDialog("加载中,请稍后...");
		mlist = new ArrayList<List<SearchBean>>();

		double Latitude = TimingService.nLatitude;
		double Longitude = TimingService.nLongitude;
		LatLng pt = new LatLng(Latitude, Longitude);
		mpt = pt;
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		// 初始化搜索模块，注册搜索事件监听
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		// 反Geo搜索
		mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(pt));

	}

	private void initView() {
		findViewById(R.id.submit_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent data = new Intent();
				Bundle b = new Bundle();
				b.putString("address", address);
				data.putExtras(b);
				setResult(ADDRESS, data);
				finish();
			}
		});
		iv_nav_indicator = (ImageView) findViewById(R.id.iv_nav_indicator);
		LayoutParams cursor_Params = iv_nav_indicator.getLayoutParams();
		rg_nav_content = (RadioGroup) findViewById(R.id.rg_nav_content);
		mViewPager = (ViewPager) findViewById(R.id.mViewPager);
		mHsv = (HorizontalScrollView) findViewById(R.id.mHsv);
		WindowManager wm = this.getWindowManager();
		int width = wm.getDefaultDisplay().getWidth();
		indicatorWidth = width / 4;
		framlist = new ArrayList<ChooseAddress>();
		ChooseAddress a = new ChooseAddress();
		ChooseAddress b = new ChooseAddress();
		ChooseAddress c = new ChooseAddress();
		ChooseAddress d = new ChooseAddress();
		// a.getData(mlist.get(0));
		// b.getData(mlist.get(1));
		// c.getData(mlist.get(2));
		// d.getData(mlist.get(3));
		framlist.add(a);
		framlist.add(b);
		framlist.add(c);
		framlist.add(d);
		mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mAdapter);
		cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
		iv_nav_indicator.setLayoutParams(cursor_Params);
		initNavigationHSV();
		setListener();
		initOverlay();
		progressDialog.cancel();
	}

	public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

		public TabFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			Fragment ft = null;
			switch (arg0) {
			case 0:
				ft = framlist.get(0);
				break;
			case 1:
				ft = framlist.get(1);
				break;
			case 2:
				ft = framlist.get(2);
				break;
			case 3:
				ft = framlist.get(3);
				break;
			default:
				ft = new ChooseAddress();
				Bundle args = new Bundle();
				args.putString(ARGUMENTS_NAME, tabTitle[arg0]);
				ft.setArguments(args);
				break;
			}
			return ft;
		}

		@Override
		public int getCount() {

			return tabTitle.length;
		}

	}

	private void initNavigationHSV() {

		rg_nav_content.removeAllViews();

		for (int i = 0; i < tabTitle.length; i++) {
			LayoutInflater mInflater = (LayoutInflater) this
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			RadioButton rb = (RadioButton) mInflater.inflate(
					R.layout.nav_radiogroup_item, null);
			rb.setId(i);
			rb.setText(tabTitle[i]);
			rb.setLayoutParams(new LayoutParams(indicatorWidth,
					LayoutParams.MATCH_PARENT));

			rg_nav_content.addView(rb);
		}

	}

	// 互相监听
	private void setListener() {

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {

				if (rg_nav_content != null
						&& rg_nav_content.getChildCount() > position) {
					((RadioButton) rg_nav_content.getChildAt(position))
							.performClick();
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		rg_nav_content
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {

						if (rg_nav_content.getChildAt(checkedId) != null) {

							TranslateAnimation animation = new TranslateAnimation(
									currentIndicatorLeft,
									((RadioButton) rg_nav_content
											.getChildAt(checkedId)).getLeft(),
									0f, 0f);
							animation.setInterpolator(new LinearInterpolator());
							animation.setDuration(100);
							animation.setFillAfter(true);

							// 执行位移动画
							iv_nav_indicator.startAnimation(animation);

							mViewPager.setCurrentItem(checkedId); // ViewPager
																	// 跟随一起 切换

							// 记录当前 下标的距最左侧的 距离
							currentIndicatorLeft = ((RadioButton) rg_nav_content
									.getChildAt(checkedId)).getLeft();

							mHsv.smoothScrollTo(
									(checkedId > 1 ? ((RadioButton) rg_nav_content
											.getChildAt(checkedId)).getLeft()
											: 0)
											- ((RadioButton) rg_nav_content
													.getChildAt(1)).getLeft(),
									0);
						}
					}
				});

	}

	@Override
	public void showMessage(LatLng ld, String str) {
		address = str;
		mBaiduMap.clear();
		OverlayOptions ooB = new MarkerOptions().position(ld).icon(bdB)
				.zIndex(9).draggable(true);
		OverlayOptions ooA = new MarkerOptions().position(mpt).icon(bdA)
				.zIndex(9).draggable(true);
		MapStatus mMapStatus = new MapStatus.Builder().target(mpt).zoom(18)
				.build();
		// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mMapStatus);
		// 改变地图状态
		mBaiduMap.setMapStatus(mMapStatusUpdate);
		mBaiduMap.addOverlay(ooA);
		mBaiduMap.addOverlay(ooB);
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(ChooseAddressActivity.this, "抱歉，未能找到结果",
					Toast.LENGTH_LONG).show();
			// ((TextView) findViewById(R.id.end_time2)).setText("重新刷新");
			return;
		}
		address = result.getAddress();
		initView();
		searchdata(address, mcount);

		Toast.makeText(ChooseAddressActivity.this, result.getAddress(),
				Toast.LENGTH_SHORT).show();
	}

	private void searchdata(String str, int i) {
		String cs = "";
		if (i != 0) {
			cs = tabTitle[i];
		} else {
			cs = "街道";
		}
		// mPoiSearch.searchInCity((new PoiCitySearchOption()).city("广州")
		// .keyword(cs).pageNum(6));
		mPoiSearch.searchNearby((new PoiNearbySearchOption()).location(mpt)
				.radius(50).pageNum(0).keyword(cs).pageCapacity(10));
	}

	@Override
	public void onGetSuggestionResult(SuggestionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		// TODO Auto-generated method stub
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(ChooseAddressActivity.this, "未找到结果",
					Toast.LENGTH_SHORT).show();
			List<SearchBean> list = new ArrayList<SearchBean>();

			SearchBean a = new SearchBean();
			if (mcount == 0) {
				a.setContent(address);
				a.setLt(mpt);
				a.setTitle("[当前位置]");
				list.add(a);
			} else {
				a.setContent("不好意思，没有结果");
				list.add(a);
			}
			mlist.add(list);
			framlist.get(mcount).getData(list);
			mcount++;
			if (mcount > tabTitle.length - 1) {
				// initView();
			} else {
				searchdata(address, mcount);
			}
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			int count = result.getAllPoi().size();
			if (count > 10) {
				count = 10;
			}
			List<SearchBean> list = new ArrayList<SearchBean>();
			if (mcount == 0) {
				SearchBean b = new SearchBean();
				b.setContent(address);
				b.setLt(mpt);
				b.setTitle("[当前位置]");
				list.add(b);
			}
			for (int i = 0; i < count; i++) {
				SearchBean a = new SearchBean();
				a.setTitle(result.getAllPoi().get(i).name);
				a.setContent(result.getAllPoi().get(i).address);
				a.setLt(result.getAllPoi().get(i).location);
				list.add(a);
			}
			mlist.add(list);
			framlist.get(mcount).getData(list);
			mcount++;
			if (mcount > tabTitle.length - 1) {
				// initView();
			} else {
				searchdata(address, mcount);
			}
		}
	}

	private void initOverlay() {
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		OverlayOptions ooA = new MarkerOptions().position(mpt).icon(bdA)
				.zIndex(9).draggable(true);
		MapStatus mMapStatus = new MapStatus.Builder().target(mpt).zoom(20)
				.build();
		// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mMapStatus);
		// 改变地图状态
		mBaiduMap.setMapStatus(mMapStatusUpdate);
		mBaiduMap.addOverlay(ooA);
	}
	@Override
	protected void onDestroy() {
		mSearch.destroy();
		mPoiSearch.destroy();
		if (mMapView != null) {
			mMapView.onDestroy();
		}
		super.onDestroy();
	}

	/***
	 * 
	 * @param message
	 */
	public void showProgressDialog(String message) {
		if (null == progressDialog) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setCanceledOnTouchOutside(false);
		}

		progressDialog.setMessage(message);
//		progressDialog.setCancelable(false);
		progressDialog.setCancelable(true);
		progressDialog.show();

	}
//	//终止当前进度条显示，获取交互焦点
//	public void onBackPressed() {
//		if(progressDialog != null){
//			progressDialog.dismiss();
//		}
//		Intent data = new Intent();
//		Bundle b = new Bundle();
//		b.putString("address", address);
//		data.putExtras(b);
//		setResult(ADDRESS, data);
//		finish();
//		}

	@Override
	public void onClick(View v) {
		// TODO 返回
		switch (v.getId()) {
		case R.id.title_back:
			finish();
			break;
		}
	}
}
