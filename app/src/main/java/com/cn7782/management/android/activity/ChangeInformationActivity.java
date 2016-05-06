package com.cn7782.management.android.activity;

import android.os.Bundle;
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

public class ChangeInformationActivity extends BaseActivity {
	private EditText oldword, newword, againword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_change_information);

		initview();

	}

	private void initview() {
		oldword = (EditText) findViewById(R.id.old_password);
		newword = (EditText) findViewById(R.id.new_password);
		againword = (EditText) findViewById(R.id.again_password);
		findViewById(R.id.title_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		findViewById(R.id.sumbit_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String a = oldword.getText().toString();
				String b = newword.getText().toString();
				String c = againword.getText().toString();
				sumbit(a, b, c);
			}
		});
	}

	private void sumbit(String old, String ew, String again) {
		if (old.equals("")) {
			Toast.makeText(ChangeInformationActivity.this, "旧密码不能为空",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (ew.equals("")) {
			Toast.makeText(ChangeInformationActivity.this, "新密码不能为空",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (again.equals("")) {
			Toast.makeText(ChangeInformationActivity.this, "确认密码不能为空",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (!again.equals(ew)) {
			Toast.makeText(ChangeInformationActivity.this, "新密码和确认密码必须一致",
					Toast.LENGTH_SHORT).show();
			return;
		}
		RequestParams param = new RequestParams();
		String tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				ChangeInformationActivity.this);
		param.put("token_id", tokenId);
		param.put("oldPassword", old);
		param.put("newPassword", ew);
		HttpClient.post(ChangeInformationActivity.this,
				ActionUrl.CHANGE_PASSWORD, param,
				new MyAsyncHttpResponseHandler(ChangeInformationActivity.this,
						"登录中,请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("修改密码成功")) {
									Toast.makeText(
											ChangeInformationActivity.this,
											"修改密码成功", Toast.LENGTH_SHORT)
											.show();
									finish();
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
