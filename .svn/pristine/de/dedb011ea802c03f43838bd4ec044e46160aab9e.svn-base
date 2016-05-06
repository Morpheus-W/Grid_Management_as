package com.cn7782.management.android.activity;

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

public class MessageDetailActivity extends BaseActivity {
	private String tokenId;
	private String id;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_msgdetail);

		getdata();

		findViewById(R.id.search_back).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
					}
				});
	}

	private void getdata() {
		id = getIntent().getStringExtra("msg_id");
		tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				MessageDetailActivity.this);
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("id", id);
		HttpClient.post(MessageDetailActivity.this, ActionUrl.MESSAGE_DETAIL,
				param, new MyAsyncHttpResponseHandler(
						MessageDetailActivity.this, "请求中,请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("请求成功")) {
									if(!jsonObject.isNull("message")){
										JSONObject json =  jsonObject.getJSONObject("message");
										
										String content = json
												.isNull("content") ? ""
														: json.getString("content");
										String sendName = json
												.isNull("senderName") ? ""
														: json.getString("senderName");
										
										String logTime = json
												.isNull("logTime") ? ""
														: json.getString("logTime");
										
										((TextView) MessageDetailActivity.this.findViewById(R.id.msg_title))
										.setText(sendName);
										((TextView) MessageDetailActivity.this.findViewById(R.id.msg_time))
										.setText(logTime);
										((TextView) MessageDetailActivity.this.findViewById(R.id.msgdetail_content))
										.setText(content);
									}
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
