package com.cn7782.management.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.activity.adapter.TrajectoryAdapter;
import com.cn7782.management.android.activity.bean.HistoryBean;
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

public class TrajectoryListActivity extends BaseActivity implements OnClickListener {
	private TrajectoryAdapter trajectoryAdapter;
	private ListView listview;
	private List<HistoryBean> mlist;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_trajectory_list);
		findViewById(R.id.title_back).setOnClickListener(this);
		getdata();
	}

	private void getdata() {
		String id = getIntent().getStringExtra("user_id");
		String tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				TrajectoryListActivity.this);
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("userId", id);
		HttpClient.post(TrajectoryListActivity.this,
				ActionUrl.TRAJECTORY_HITTORY, param,
				new MyAsyncHttpResponseHandler(TrajectoryListActivity.this,
						"请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("查询成功")) {
									if (mlist == null) {
										mlist = new ArrayList<HistoryBean>();
									}
									JSONObject jo1 = jsonObject
											.getJSONObject("data");
									JSONArray jo2 = jo1.getJSONArray("wayList");
									for (int i = 0; i < jo2.length(); i++) {
										JSONObject json = jo2.getJSONObject(i);
										HistoryBean a = new HistoryBean();
										
										a.setDistance(json.isNull("length") ? 0
												: json.getInt("length"));
										a.setDuration(json.isNull("duration") ? 0
												: json.getInt("duration"));
										a.setId(json.isNull("id") ? "" : json
												.getString("id"));
										a.setName(json.isNull("user_name") ? ""
												: json.getString("user_name"));
										a.setTime(json.isNull("start_time") ? ""
												: json.getString("start_time"));
										a.setRemark(json.isNull("remark") ? ""
												: json.getString("remark"));
										mlist.add(a);
									}
								}
								initView();
							}
						} catch (JSONException e) {

						}
					}
				});
	}

	private void initView() {
		listview = (ListView) findViewById(R.id.trajectory_list);
		trajectoryAdapter = new TrajectoryAdapter(this, mlist);
		listview.setAdapter(trajectoryAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TrajectoryListActivity.this,
						MapAcitivity.class);
				intent.putExtra("way_id", mlist.get(position).getId());
				startActivity(intent);
			}
		});
	}

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
