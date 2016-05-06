package com.cn7782.management.android.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

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

public class FeedbackActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_feedback);
		
		initView();
		
		//返回
		findViewById(R.id.title_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initView() {
		
		final EditText editText = (EditText) findViewById(R.id.feedback_content);
		findViewById(R.id.send_feedback).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String str = editText.getText().toString();
						sendData(str);
					}
				});
	}

	private void sendData(String str) {
		if (TextUtils.isEmpty(str)) {
			Toast.makeText(FeedbackActivity.this, "评论不能为空", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		String tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				FeedbackActivity.this);
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("content", str);
		Log.i("token_id", "-----------"+tokenId);
		Log.i("content", "-----------"+str);
		
		HttpClient.post(FeedbackActivity.this, ActionUrl.FEEDBACK, param,
						new MyAsyncHttpResponseHandler(FeedbackActivity.this,
								"请稍后..."){
							public void onSuccess(String results) {
								super.onSuccess(results);
								try {
									JSONObject jsonObject = new JSONObject(
											results);
									if (jsonObject.has("msg")) {
										String msg = jsonObject.isNull("msg") ? ""
												: jsonObject.getString("msg");
										if (msg.equals("成功")) {
											Toast.makeText(
													FeedbackActivity.this,
													"提交成功", Toast.LENGTH_SHORT)
													.show();
											finish();
										} else {
											Toast.makeText(
													FeedbackActivity.this,
													"提交失败", Toast.LENGTH_SHORT)
													.show();
										}
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
	}
}