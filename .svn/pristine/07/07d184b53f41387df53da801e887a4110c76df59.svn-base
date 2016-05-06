package com.cn7782.management.android.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.activity.adapter.PatrolHisAdapter;
import com.cn7782.management.android.activity.bean.PatrolHisBean;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.http.HttpClient;
import com.cn7782.management.http.MyAsyncHttpResponseHandler;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PatrolActivity extends BaseActivity implements OnClickListener {
	private PatrolHisAdapter patrolhisadapter;
	private List<PatrolHisBean> list;
	private ListView listview;
	private String myEventCount,myWayCount;
	private int myWayAvgTime;
	private double myRoad, myWalkingRoad, myWayAvgLong, myDrivingRoad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_patrol);
		findViewById(R.id.add_patrol).setOnClickListener(this);
		findViewById(R.id.title_back).setOnClickListener(this);
		getdata();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.add_patrol:
			Intent intent = new Intent(PatrolActivity.this,
					AddPatrolActivity.class);
			startActivity(intent);
			break;
		case R.id.title_back:
			finish();
			break;
		default:
			break;
		}
	}

	private void getdata() {
		String tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				PatrolActivity.this);
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		Log.i("token_id-------", tokenId);
		HttpClient.post(PatrolActivity.this, ActionUrl.MYPATROL, param,
				new MyAsyncHttpResponseHandler(PatrolActivity.this, "请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("查询成功")) {
									
									myRoad = jsonObject.isNull("myRoad") ? 0
											: jsonObject.getDouble("myRoad");
									myRoad = myRoad/1000;
									myWayAvgTime = jsonObject
											.isNull("myWayAvgTime") ? 0
											: jsonObject
													.getInt("myWayAvgTime");
									myWayAvgTime = myWayAvgTime/60;
									myWalkingRoad = jsonObject
											.isNull("myWalkingRoad") ? 0
											: jsonObject
													.getDouble("myWalkingRoad");
									myWalkingRoad = myWalkingRoad/1000;
									myWayAvgLong = jsonObject
											.isNull("myWayAvgLong") ? 0
											: jsonObject
													.getDouble("myWayAvgLong");
									myWayAvgLong = myWayAvgLong/1000;
									
									myEventCount = jsonObject
											.isNull("myEventCount") ? ""
											: jsonObject
													.getString("myEventCount");
									myWayCount = jsonObject
											.isNull("myWayCount") ? ""
											: jsonObject
													.getString("myWayCount");
									myDrivingRoad = jsonObject
											.isNull("myDrivingRoad") ? 0
											: jsonObject
													.getDouble("myDrivingRoad");
									myDrivingRoad = myDrivingRoad/1000;
									JSONArray jo1 = jsonObject
											.getJSONArray("wayList");
									if (list == null)
										list = new ArrayList<PatrolHisBean>();
									for (int i = 0; i < jo1.length(); i++) {
										JSONObject json = jo1.getJSONObject(i);
										PatrolHisBean a = new PatrolHisBean();
										a.setDistance(json.isNull("length") ? 0
												: json.getInt("length"));
										a.setTimeconsuming(json
												.isNull("duration") ? 0 : json
												.getInt("duration"));
										a.setTime(json.isNull("start_time") ? ""
												: json.getString("start_time"));
										
										a.setEndPoint(json.isNull("end_place") ? ""
												: json.getString("end_place"));
										a.setStartPoint(json
												.isNull("start_place") ? ""
												: json.getString("start_place"));
										
										a.setId(json.isNull("id") ? "" : json
												.getString("id"));
										a.setRemark(json.isNull("remark") ? "" : json
												.getString("remark"));
										list.add(a);
									}
									initview();
								}

							}
						} catch (JSONException e) {

						}
					}

					public void onFailure(Throwable arg0, String tipInfo) {
						super.onFailure(arg0, tipInfo);

					}
				});
	}

	private void initview() {
		patrolhisadapter = new PatrolHisAdapter(this, list);
		listview = (ListView) findViewById(R.id.patrol_list);
		listview.setAdapter(patrolhisadapter);
		initdata();

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String Id = list.get(position).getId();
				Intent intent = new Intent(PatrolActivity.this,
						CheckMapActivity.class);
				intent.putExtra("patrol_id", Id);
				startActivity(intent);
			}
		});
	}

	private void initdata() {
		Typeface fontFace = Typeface.createFromAsset(getAssets(),
				"OpenSans-Light.ttf");
		((TextView) findViewById(R.id.myWayCount)).setText(myWayCount);
		((TextView) findViewById(R.id.myWayCount)).setTypeface(fontFace);
		((TextView) findViewById(R.id.myWayAvgTime)).setText(myWayAvgTime+"");
		((TextView) findViewById(R.id.myWayAvgTime)).setTypeface(fontFace);
		((TextView) findViewById(R.id.myWalkingRoad)).setText(myWalkingRoad+"");
		((TextView) findViewById(R.id.myWalkingRoad)).setTypeface(fontFace);
		((TextView) findViewById(R.id.myWayAvgLong)).setText(myWayAvgLong+"");
		((TextView) findViewById(R.id.myWayAvgLong)).setTypeface(fontFace);
		((TextView) findViewById(R.id.myEventCount)).setText(myEventCount);
		((TextView) findViewById(R.id.myEventCount)).setTypeface(fontFace);
		((TextView) findViewById(R.id.myDrivingRoad)).setText(myDrivingRoad+"");
		((TextView) findViewById(R.id.myDrivingRoad)).setTypeface(fontFace);
		((TextView) findViewById(R.id.myRoad)).setText(myRoad+"");
		((TextView) findViewById(R.id.myRoad)).setTypeface(fontFace);

		((TextView) findViewById(R.id.min)).setTypeface(fontFace);
		((TextView) findViewById(R.id.km1)).setTypeface(fontFace);
		((TextView) findViewById(R.id.km2)).setTypeface(fontFace);
		((TextView) findViewById(R.id.km3)).setTypeface(fontFace);
	}
}