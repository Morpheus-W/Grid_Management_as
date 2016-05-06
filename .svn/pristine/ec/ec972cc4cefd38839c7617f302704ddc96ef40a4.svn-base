package com.cn7782.management.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.http.HttpClient;
import com.cn7782.management.http.MyAsyncHttpResponseHandler;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StatisticsActivity extends BaseActivity {

	private String chuzhi, xiaoan, baosong, zongCount, duban, xiaoanlv;
	private String useTime, count, roadLong, averageTime, drivingRoad,
			walkingRoad;
	public static final String STATISTICS = "statistics";
	// 查询时间
	private String starttime = "";
	private String endtime = "";
	private TextView date_head, date_tail;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_statistics);

		initview();

		getdataI();

	}

	private void initview() {
		SimpleDateFormat aDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String day = aDateFormat.format(new java.util.Date());
		Calendar c = Calendar.getInstance();  
        c.add(Calendar.DATE, - 7);  
        Date monday = c.getTime();
        String preMonday = aDateFormat.format(monday);
		starttime = preMonday;
		endtime = day;
		date_head = (TextView) findViewById(R.id.date_head);
		date_tail = (TextView) findViewById(R.id.date_tail);
		date_head.setText(starttime);
		date_tail.setText(endtime);
		findViewById(R.id.search_back).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
		findViewById(R.id.gotott).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent5 = new Intent(StatisticsActivity.this,
						ChartingActivity.class);
				intent5.putExtra(STATISTICS, "1");
				intent5.putExtra("starttime", starttime);
				intent5.putExtra("endtime", endtime);
				startActivity(intent5);
			}
		});
		findViewById(R.id.statistics).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(StatisticsActivity.this,
						ChartingActivity.class);
				intent.putExtra(STATISTICS, "2");
				intent.putExtra("starttime", starttime);
				intent.putExtra("endtime", endtime);
				startActivity(intent);
			}
		});
		findViewById(R.id.search_date).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(StatisticsActivity.this,
								ChooseDateActivity.class);
						//传递开始时间，结束时间
						intent.putExtra("starttime", starttime);
						intent.putExtra("endtime", endtime);
						startActivityForResult(intent, 1);
					}
				});
	}

	private void getdataI() {
		String tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				StatisticsActivity.this);
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("startTime", starttime);
		param.put("endTime", endtime);
		HttpClient.post(StatisticsActivity.this, ActionUrl.ZONGWAY, param,
				new MyAsyncHttpResponseHandler(StatisticsActivity.this,
						"请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("查询成功")) {
									useTime = jsonObject.isNull("useTime") ? ""
											: jsonObject.getString("useTime");
									count = jsonObject.isNull("count") ? ""
											: jsonObject.getString("count");
									roadLong = jsonObject.isNull("roadLong") ? ""
											: jsonObject.getString("roadLong");
									averageTime = jsonObject
											.isNull("averageTime") ? ""
											: jsonObject
													.getString("averageTime");
									drivingRoad = jsonObject
											.isNull("drivingRoad") ? ""
											: jsonObject
													.getString("drivingRoad");
									walkingRoad = jsonObject
											.isNull("walkingRoad") ? ""
											: jsonObject
													.getString("walkingRoad");
									getdata();
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}
	private void getdata() {
		String tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				StatisticsActivity.this);
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("startTime", starttime);
		param.put("endTime", endtime);
		HttpClient.post(StatisticsActivity.this, ActionUrl.ZONGEVENT_REPORY,
				param, new MyAsyncHttpResponseHandler(StatisticsActivity.this,
						"请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("查询成功")) {
									chuzhi = jsonObject.isNull("chuzhi") ? ""
											: jsonObject.getString("chuzhi");
									xiaoan = jsonObject.isNull("xiaoan") ? ""
											: jsonObject.getString("xiaoan");
									baosong = jsonObject.isNull("baosong") ? ""
											: jsonObject.getString("baosong");
									zongCount = jsonObject.isNull("zongCount") ? ""
											: jsonObject.getString("zongCount");
									duban = jsonObject.isNull("duban") ? ""
											: jsonObject.getString("duban");
									xiaoanlv = jsonObject.isNull("xiaoanlv") ? ""
											: jsonObject.getString("xiaoanlv");

								}
								initdata();
							}
						} catch (JSONException e) {

						}
					}
				});
	}

	private void initdata() {
		((TextView) findViewById(R.id.zongCount)).setText(zongCount);
		((TextView) findViewById(R.id.xiaoan)).setText(xiaoan);
		((TextView) findViewById(R.id.baosong)).setText(baosong);
		((TextView) findViewById(R.id.chuzhi)).setText(chuzhi);
		((TextView) findViewById(R.id.duban)).setText(duban);
		((TextView) findViewById(R.id.xiaoanlv)).setText(xiaoanlv);

		((TextView) findViewById(R.id.useTime)).setText(useTime);
		((TextView) findViewById(R.id.count)).setText(count);
		((TextView) findViewById(R.id.roadLong)).setText(roadLong);
		((TextView) findViewById(R.id.averageTime)).setText(averageTime);
		((TextView) findViewById(R.id.drivingRoad)).setText(drivingRoad);
		((TextView) findViewById(R.id.walkingRoad)).setText(walkingRoad);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			switch (resultCode) {
			case ChooseDateActivity.DATATIME:
				Bundle a = data.getExtras();
				starttime = a.getString("startime");
				endtime = a.getString("endtime");
				date_head.setText(starttime);
				date_tail.setText(endtime);
				getdataI();
				break;
			}
		}
	}
	
}
