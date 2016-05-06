package com.cn7782.management.android.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ListView;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.activity.adapter.HaveReadAdapter;
import com.cn7782.management.android.activity.bean.NoticeBean;
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

public class HaveReadActivity extends BaseActivity {
	private HaveReadAdapter havereadadapter1, havereadadapter2;
	private ListView weeklist, weekagolist;
	private View weekAgo, oneWeek;
	private View back;
	public static final String NOTICEREAD = "notice";
	public static final String DYNAMICREAD = "dynamic";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_have_read);

		weekAgo = (View) findViewById(R.id.week_ago);
		oneWeek = (View) findViewById(R.id.one_week);

		back = (View) findViewById(R.id.search_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		String data = getIntent().getStringExtra("readtype");
		if (data.equals(NOTICEREAD)) {
			getdata();
		} else {
			getdata2();
		}

	}

	private void getdata() {

		String id = getIntent().getStringExtra("objId");
		String tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				HaveReadActivity.this);
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("objId", id);

		HttpClient.post(HaveReadActivity.this, ActionUrl.HAVE_READ, param,
				new MyAsyncHttpResponseHandler(HaveReadActivity.this,
						"登录中,请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							// 本周
							List<NoticeBean> list = new ArrayList<NoticeBean>();
							// 一周以前
							List<NoticeBean> mlist = new ArrayList<NoticeBean>();
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("查询成功")) {
									JSONArray jo1 = jsonObject
											.getJSONArray("list");
									for (int i = 0; i < jo1.length(); i++) {
										NoticeBean a = new NoticeBean();
										JSONObject json = jo1.getJSONObject(i);
										a.setTime(json.isNull("read_time") ? ""
												: json.getString("read_time"));
										a.setDepartment(json
												.isNull("department") ? ""
												: json.getString("department"));
										a.setName(json.isNull("user_name") ? ""
												: json.getString("user_name"));
										a.setImageurl(json.isNull("user_url") ? ""
												: json.getString("user_url"));
										String week = json.isNull("week") ? ""
												: json.getString("week");
										if (week.equals("1")) {
											list.add(a);
										} else if (week.equals("2")) {
											mlist.add(a);
										} else {

										}
									}
									initListView(list, mlist);
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

	private void getdata2() {
		String id = getIntent().getStringExtra("objId");
		String tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				HaveReadActivity.this);
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("objId", id);
		HttpClient.post(HaveReadActivity.this, ActionUrl.DYNAMIC_READ, param,
				new MyAsyncHttpResponseHandler(HaveReadActivity.this,
						"登录中,请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							// 本周
							List<NoticeBean> list = new ArrayList<NoticeBean>();
							// 一周以前
							List<NoticeBean> mlist = new ArrayList<NoticeBean>();
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("查询成功")) {
									JSONArray jo1 = jsonObject
											.getJSONArray("list");
									for (int i = 0; i < jo1.length(); i++) {
										NoticeBean a = new NoticeBean();
										JSONObject json = jo1.getJSONObject(i);
										a.setTime(json.isNull("read_time") ? ""
												: json.getString("read_time"));
										a.setDepartment(json
												.isNull("department") ? ""
												: json.getString("department"));
										a.setName(json.isNull("user_name") ? ""
												: json.getString("user_name"));
										a.setImageurl(json.isNull("user_url") ? ""
												: json.getString("user_url"));
										String week = json.isNull("week") ? ""
												: json.getString("week");
										if (week.equals("1")) {
											list.add(a);
										} else if (week.equals("2")) {
											mlist.add(a);
										} else {

										}
									}
									initListView(list, mlist);
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

	/**
	 * list为本周，mlist为一周以前
	 */
	private void initListView(List<NoticeBean> list, List<NoticeBean> mlist) {
		if (list == null || list.size() == 0) {
			oneWeek.setVisibility(View.GONE);
		} else {
			weeklist = (ListView) findViewById(R.id.have_read_list);
			havereadadapter1 = new HaveReadAdapter(this, list);
			weeklist.setAdapter(havereadadapter1);
		}
		if (mlist == null || mlist.size() == 0) {
			weekAgo.setVisibility(View.GONE);
		} else {
			weekagolist = (ListView) findViewById(R.id.have_read_list2);
			havereadadapter2 = new HaveReadAdapter(this, mlist);
			weekagolist.setAdapter(havereadadapter2);
		}
	}
}
