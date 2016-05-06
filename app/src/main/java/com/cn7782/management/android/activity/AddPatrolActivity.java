package com.cn7782.management.android.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.BaseApplication;
import com.cn7782.management.android.activity.service.TimingService;
import com.cn7782.management.android.db.DBHelper;

import java.text.SimpleDateFormat;

public class AddPatrolActivity extends BaseActivity implements OnClickListener,
		OnGetGeoCoderResultListener {
	private TextView edittext;
	private TextView timer;
	private TextView address;
	GeoCoder mSearch = null; // 搜索模块
	private Cursor cursor;
	private DBHelper dp;
	private String tablename;
	private Button submit;
	private BaseApplication base;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_patrol);
		initView();
	}

	private void initView() {
		base = BaseApplication.getInstance();
		
		submit = (Button)findViewById(R.id.submit_btn);
		submit.setOnClickListener(this);
		
		findViewById(R.id.my_report).setOnClickListener(this);
		findViewById(R.id.choose_address).setOnClickListener(this);
		findViewById(R.id.title_back).setOnClickListener(this);
		findViewById(R.id.add_history).setOnClickListener(this);
		edittext = (TextView) findViewById(R.id.patrol_content);
		SimpleDateFormat aDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String day = aDateFormat.format(new java.util.Date());
		timer = (TextView) findViewById(R.id.patrol_time);
		address = (TextView) findViewById(R.id.patrol_address);
		timer.setText(day);
		double Latitude = TimingService.nLatitude;
		double Longitude = TimingService.nLongitude;
		LatLng pt = new LatLng(Latitude, Longitude);
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		// 反Geo搜索
		mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(pt));
		dp = new DBHelper(this);
		
		isRestorePatro();
		
	}
	@Override
	protected void onResume() {
		isContinuePatro();
		super.onResume();
	}

	private void isContinuePatro() {
		if (base.getIsLocation() == 5) {
			submit.setText("正在记录");
		}else{
			submit.setText("开始记录");
		}
	}

	private void isRestorePatro() {
		cursor = dp.query("SELECT * FROM " + DBHelper.TABLENAME
				+ " WHERE Artificial = 1", null);
		int i = cursor.getCount();
		if (i > 0) {
			cursor.moveToPosition(0);
			tablename = cursor.getString(0);
			// final String Artificial = cursor.getString(1);
			final String address = cursor.getString(2);
			final String event = cursor.getString(3);
			final String time = cursor.getString(4);
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("是否恢复" + time + "巡防");
			alert.setPositiveButton("取消并删除记录",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dp.dropTable(tablename);
							dp.deleteTable(1);
							dialog.dismiss();

						}
					});
			alert.setNegativeButton("恢复",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							Intent intent = new Intent(AddPatrolActivity.this,
									PatrolingActivity.class);
							intent.putExtra("add_time", time);
							intent.putExtra("add_address", address);
							intent.putExtra("add_content", event);
							intent.putExtra("issecond", "yes");
							startActivity(intent);
						}
					});
			alert.create().show();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO 触发按钮
		switch (v.getId()) {
		case R.id.title_back:
			finish();
			break;
		case R.id.submit_btn:
			gotoIntent(((Button)v).getText().toString());
			break;
		case R.id.my_report:
			Intent intent2 = new Intent(AddPatrolActivity.this,
					UploadedOfflineActivity.class);
			startActivity(intent2);
			break;
		case R.id.choose_address:
			Intent intent3 = new Intent(AddPatrolActivity.this,
					ChooseAddressActivity.class);
			startActivityForResult(intent3, 1);
			break;
		case R.id.add_history:
			Intent intent4 = new Intent(AddPatrolActivity.this,
					HistoryListActivity.class);
			startActivityForResult(intent4, 1);
			break;
		default:
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case ChooseAddressActivity.ADDRESS:
			Bundle abc = data.getExtras();
			String maddress = abc.getString("address");
			address.setText(maddress);
			break;
		case HistoryListActivity.DESCRIPTION_RETURNQ:
			Bundle ab = data.getExtras();
			String name = ab.getString("history");
			edittext.setText(name);
			break;
		}
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(AddPatrolActivity.this, "抱歉，未能找到结果",
					Toast.LENGTH_LONG).show();
			return;
		}
		address.setText(result.getAddress());
//		Toast.makeText(AddPatrolActivity.this, result.getAddress(),
//				Toast.LENGTH_LONG).show();

	}

	@Override
	protected void onDestroy() {
		if(dp != null)
			dp.close();
		if(cursor != null)
			cursor.close();
		mSearch.destroy();
		super.onDestroy();
	}

	private void gotoIntent(String text) {
		if(text.equals("正在记录")){
			Intent intent = new Intent(AddPatrolActivity.this,
					PatrolingActivity.class);
			startActivity(intent);
		}else{
			String timer2 = timer.getText().toString();
			String address2 = address.getText().toString();
			String edittext2 = edittext.getText().toString();
			if (timer2.equals("")) {
				Toast.makeText(AddPatrolActivity.this, "时间不能为空", Toast.LENGTH_SHORT)
						.show();
				return;
			}
	//		if (address2.equals("")) {
	//			Toast.makeText(AddPatrolActivity.this, "地址不能为空", Toast.LENGTH_SHORT)
	//					.show();
	//			return;
	//		}
			if (edittext2.equals("")) {
				Toast.makeText(AddPatrolActivity.this, "备注不能为空", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			LocationManager	locationManager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);
	
			//检查是否开启GPS
			if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				Toast.makeText(
						BaseApplication.getInstance().getApplicationContext(),
						"请开启GPS...", Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent = new Intent(AddPatrolActivity.this,
					PatrolingActivity.class);
			intent.putExtra("add_time", timer2);
			intent.putExtra("add_address", address2);
			intent.putExtra("add_content", edittext2);
			startActivity(intent);
		}
	}

}
