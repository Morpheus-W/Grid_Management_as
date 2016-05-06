package com.cn7782.management.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
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

public class LoginActivity extends BaseActivity {
	private View login_button;
	private GestureDetector gestureDetector_;
	private EditText username, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		ActionUrl.changeIP(LoginActivity.this);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		float density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
		int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
		// Log.e("屏幕密度", "" + density); 108
		// Log.e("屏幕密度DPI", "" + densityDpi);

		gestureDetector_ = new GestureDetector(this, new LoginGestureDetector(
				this));

		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		login_button = (View) findViewById(R.id.login_button);

		String ETpassword = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.PASSWORD,
				LoginActivity.this);
		if (!TextUtils.isEmpty(ETpassword))
			password.setText(ETpassword);
		String ETaccount = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.ACCOUNT,
				LoginActivity.this);
		if (!TextUtils.isEmpty(ETaccount))
			username.setText(ETaccount);
		login_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initData();
			}
		});

	}

	protected void initData() {
		
		RequestParams param = new RequestParams();
		String str = username.getText().toString();
		String str2 = password.getText().toString();
		if (!TextUtils.isEmpty(str)) {
			param.put("username", str);
		} else {
			Toast.makeText(LoginActivity.this, "账号不能为空",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (!TextUtils.isEmpty(str2)) {
			param.put("password", str2);
		} else {
			Toast.makeText(LoginActivity.this, "密码不能为空",
					Toast.LENGTH_SHORT).show();
			return;
		}
		HttpClient.post(LoginActivity.this, ActionUrl.LOGIN_URL, param,
				new MyAsyncHttpResponseHandler(LoginActivity.this,
						"登录中,请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(
									results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("登陆成功")) {
									JSONObject jsonObject2 = jsonObject
											.getJSONObject("return_info");
									String token_id = jsonObject2
											.isNull("tokenId") ? ""
											: jsonObject2
													.getString("tokenId");
									String grid_id = jsonObject2
											.isNull("grid") ? ""
											: jsonObject2
													.getString("grid");
									String user_id = jsonObject2
											.isNull("id") ? ""
													: jsonObject2
													.getString("id");
									String roles = jsonObject2
											.isNull("roles") ? ""
													: jsonObject2
													.getString("roles");
									SharedPreferenceUtil
											.modify(PreferenceConstant.MARK_ID_TABLE,
													PreferenceConstant.MARK_ID,
													token_id,
													LoginActivity.this);
									SharedPreferenceUtil
											.modify(PreferenceConstant.MARK_ID_TABLE,
													PreferenceConstant.GRID_ID,
													grid_id,
													LoginActivity.this);
									SharedPreferenceUtil
											.modify(PreferenceConstant.MARK_ID_TABLE,
													PreferenceConstant.USER_ID,
													user_id,
													LoginActivity.this);
									SharedPreferenceUtil
											.modify(PreferenceConstant.MARK_ID_TABLE,
													PreferenceConstant.PASSWORD,
													password.getText()
															.toString(),
													LoginActivity.this);
									SharedPreferenceUtil
											.modify(PreferenceConstant.MARK_ID_TABLE,
													PreferenceConstant.ACCOUNT,
													username.getText()
															.toString(),
													LoginActivity.this);
									SharedPreferenceUtil
											.modify(PreferenceConstant.MARK_ID_TABLE,
													PreferenceConstant.ROLES,
													roles,
													LoginActivity.this);
									Intent intent = new Intent(
											LoginActivity.this,
											MainActivity.class);
									startActivity(intent);
									finish();
								} else{
									 Toast.makeText(LoginActivity.this, "登陆失败，用户或密码错误",
									 Toast.LENGTH_SHORT).show();
								}

							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					public void onFailure(Throwable arg0, String tipInfo) {
						super.onFailure(arg0,tipInfo);
					}
				});
	}


	class LoginGestureDetector extends SimpleOnGestureListener {
		private Context mContext;
		private int mTapCount;

		LoginGestureDetector(Context context) {
			mContext = context;
			mTapCount = 0;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			mTapCount++;
			if (mTapCount == 5) {
				mTapCount = 0;
				try {
					Intent intent = new Intent(mContext,
							LoginSettingActivity.class);
					mContext.startActivity(intent);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			return super.onSingleTapUp(e);
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
		return gestureDetector_.onTouchEvent(event);
	}
}
