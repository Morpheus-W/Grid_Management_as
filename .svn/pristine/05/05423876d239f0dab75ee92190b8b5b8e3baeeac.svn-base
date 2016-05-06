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

public class DynamicDetailActivity extends BaseActivity {
	private String tokenId;
	private String id;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dynamicdetail);

		getdata();

		findViewById(R.id.search_back).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
					}
				});

		// getReadData(); 

	}

	private void getdata() {
		id = getIntent().getStringExtra("dynamic_id");
		tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				DynamicDetailActivity.this);
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("id", id);
		HttpClient.post(DynamicDetailActivity.this, ActionUrl.DYNAMIC_DETAIL,
				param, new MyAsyncHttpResponseHandler(
						DynamicDetailActivity.this, "请求中,请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								// read_count
								if (msg.equals("查询成功")) {
									String content = jsonObject
											.isNull("content") ? ""
											: jsonObject.getString("content");
									String read_count = jsonObject
											.isNull("read_count") ? ""
											: jsonObject
													.getString("read_count");
									((TextView) findViewById(R.id.noticedetail_count))
											.setText("浏览人数 " + read_count);
									String log_time = jsonObject
											.isNull("log_time") ? ""
											: jsonObject.getString("log_time");
									String title = jsonObject.isNull("title") ? ""
											: jsonObject.getString("title");
									String department = jsonObject
											.isNull("department") ? ""
											: jsonObject
													.getString("department");
									final String objId = jsonObject
											.isNull("id") ? "" : jsonObject
											.getString("id");
									((TextView) findViewById(R.id.notice_title))
											.setText(title);
									((TextView) findViewById(R.id.notice_department))
											.setText(department);
									((TextView) findViewById(R.id.notice_time))
											.setText(log_time);
									((TextView) findViewById(R.id.noticedetail_content))
											.setText(content);
									// getReadData();
									findViewById(R.id.view_count)
											.setOnClickListener(
													new OnClickListener() {

														@Override
														public void onClick(
																View v) {
															Intent intent = new Intent(
																	DynamicDetailActivity.this,
																	HaveReadActivity.class);
															intent.putExtra(
																	"readtype",
																	HaveReadActivity.DYNAMICREAD);
															intent.putExtra(
																	"objId",
																	objId);
															startActivity(intent);
														}
													});
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

	private void getReadData() {
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("objId", id);
		HttpClient.post(DynamicDetailActivity.this, ActionUrl.DYNAMIC_READ,
				param, new MyAsyncHttpResponseHandler(
						DynamicDetailActivity.this, "请求中,请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");

								if (msg.equals("查询成功")) {
									String read = jsonObject.isNull("ret") ? ""
											: jsonObject.getString("ret");

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
}
