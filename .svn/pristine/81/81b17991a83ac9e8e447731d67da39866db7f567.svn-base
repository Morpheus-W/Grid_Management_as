package com.cn7782.management.android.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.activity.adapter.ChartingAdapter;
import com.cn7782.management.android.activity.bean.StatisticsBean;
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

public class ChartingActivity extends BaseActivity {
	private ListView listCharting;
	private ChartingAdapter chartingAdapter;
	private List<StatisticsBean> list;
	View back;

	// 请求参数
	private String tokenId, type, end, sta;
	private TextView date_head, date_tail;
	//标题
	private TextView title;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_charting);

		initdata();
	}

	private void initdata() {
		tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				ChartingActivity.this);
		type = getIntent().getStringExtra(StatisticsActivity.STATISTICS);
		end = getIntent().getStringExtra("endtime");
		sta = getIntent().getStringExtra("starttime");

		//改变公用activity的标题
		title = (TextView) findViewById(R.id.kcool_title_back);
		//type:1为上报，2为统计
		if (type.equals("1")) {
			title.setText(R.string.reportStat);
			getdata();
		} else {
			title.setText(R.string.patrolStat);
			getData();
		}
		//初始化日期
		date_head = (TextView) findViewById(R.id.date_head);
		date_tail = (TextView) findViewById(R.id.date_tail);
		date_head.setText(sta);
		date_tail.setText(end);
	}

	private void getData() {
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("startTime", sta);
		param.put("endTime", end);

		HttpClient
				.post(ChartingActivity.this, ActionUrl.WAYREPORT, param,
						new MyAsyncHttpResponseHandler(ChartingActivity.this,
								"请稍后...") {
							public void onSuccess(String results) {
								super.onSuccess(results);
								if (list == null)
									list = new ArrayList<StatisticsBean>();
								try {
									JSONObject jsonObject = new JSONObject(
											results);
									if (jsonObject.has("msg")) {
										String msg = jsonObject.isNull("msg") ? ""
												: jsonObject.getString("msg");
										if (msg.equals("查询成功")) {
											JSONArray jo1 = jsonObject
													.getJSONArray("countWay");
											for (int i = 0; i < jo1.length(); i++) {
												JSONObject json = jo1
														.getJSONObject(i);
												StatisticsBean a = new StatisticsBean();
												a.setType("countWay");
												a.setGrid1(json.isNull("grid1") ? ""
														: json.getString("grid1"));
												a.setGrid2(json.isNull("grid2") ? ""
														: json.getString("grid2"));
												a.setGrid3(json.isNull("grid3") ? ""
														: json.getString("grid3"));
												a.setGrid4(json.isNull("grid4") ? ""
														: json.getString("grid4"));
												list.add(a);
											}
											JSONArray jo2 = jsonObject
													.getJSONArray("roadLongWay");
											for (int i = 0; i < jo2.length(); i++) {
												JSONObject json = jo2
														.getJSONObject(i);
												StatisticsBean b = new StatisticsBean();
												b.setType("roadLongWay");
												b.setGrid1(json.isNull("grid1") ? ""
														: json.getString("grid1"));
												b.setGrid2(json.isNull("grid2") ? ""
														: json.getString("grid2"));
												b.setGrid3(json.isNull("grid3") ? ""
														: json.getString("grid3"));
												b.setGrid4(json.isNull("grid4") ? ""
														: json.getString("grid4"));
												list.add(b);
											}
											JSONArray jo3 = jsonObject
													.getJSONArray("timeWay");
											for (int i = 0; i < jo3.length(); i++) {
												JSONObject json = jo3
														.getJSONObject(i);
												StatisticsBean c = new StatisticsBean();
												c.setType("timeWay");
												c.setGrid1(json.isNull("grid1") ? ""
														: json.getString("grid1"));
												c.setGrid2(json.isNull("grid2") ? ""
														: json.getString("grid2"));
												c.setGrid3(json.isNull("grid3") ? ""
														: json.getString("grid3"));
												c.setGrid4(json.isNull("grid4") ? ""
														: json.getString("grid4"));
												list.add(c);
											}
										}
									}
									initview();
								} catch (JSONException e) {

								}
							}

						});
	}

	private void getdata() {

		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("startTime", sta);
		param.put("endTime", end);
		HttpClient
				.post(ChartingActivity.this, ActionUrl.EVENTREPORT, param,
						new MyAsyncHttpResponseHandler(ChartingActivity.this,
								"请稍后...") {
							public void onSuccess(String results) {
								super.onSuccess(results);
								if (list == null)
									list = new ArrayList<StatisticsBean>();
								try {
									JSONObject jsonObject = new JSONObject(
											results);
									if (jsonObject.has("msg")) {
										String msg = jsonObject.isNull("msg") ? ""
												: jsonObject.getString("msg");
										if (msg.equals("查询成功")) {
											JSONArray jo1 = jsonObject
													.getJSONArray("gridType");
											for (int i = 0; i < jo1.length(); i++) {
												JSONObject json = jo1
														.getJSONObject(i);
												StatisticsBean a = new StatisticsBean();
												a.setType("gridType");
												a.setGrid1(json.isNull("grid1") ? ""
														: json.getString("grid1"));
												a.setGrid2(json.isNull("grid2") ? ""
														: json.getString("grid2"));
												a.setGrid3(json.isNull("grid3") ? ""
														: json.getString("grid3"));
												a.setGrid4(json.isNull("grid4") ? ""
														: json.getString("grid4"));
												list.add(a);
											}
											JSONArray jo2 = jsonObject
													.getJSONArray("reportType");
											for (int i = 0; i < jo2.length(); i++) {
												JSONObject json = jo2
														.getJSONObject(i);
												StatisticsBean b = new StatisticsBean();
												b.setType("reportType");
												b.setProtectRoadLine(json
														.isNull("ProtectRoadLine") ? ""
														: json.getString("ProtectRoadLine"));
												b.setContradictionEvent(json
														.isNull("ContradictionEvent") ? ""
														: json.getString("ContradictionEvent"));
												b.setSocialSecurity(json
														.isNull("SocialSecurity") ? ""
														: json.getString("SocialSecurity"));
												list.add(b);
											}
											JSONArray jo3 = jsonObject
													.getJSONArray("progType");
											for (int i = 0; i < jo3.length(); i++) {
												JSONObject json = jo3
														.getJSONObject(i);
												StatisticsBean c = new StatisticsBean();
												c.setType("progType");
												c.setChuzhi(json
														.isNull("chuzhi") ? ""
														: json.getString("chuzhi"));
												c.setZongCount(json
														.isNull("zongCount") ? ""
														: json.getString("zongCount"));
												c.setXiaoan(json
														.isNull("xiaoan") ? ""
														: json.getString("xiaoan"));
												c.setDuban(json.isNull("duban") ? ""
														: json.getString("duban"));
												c.setBaosong(json
														.isNull("baosong") ? ""
														: json.getString("baosong"));
												c.setHecha(json.isNull("hecha") ? ""
														: json.getString("hecha"));
												list.add(c);
											}
										}
									}
									initview();
								} catch (JSONException e) {

								}
							}

						});
	}

	private void initview() {
		back = (View) findViewById(R.id.search_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		listCharting = (ListView) findViewById(R.id.charting_list);
		chartingAdapter = new ChartingAdapter(this, list);
		listCharting.setAdapter(chartingAdapter);
	}
}
