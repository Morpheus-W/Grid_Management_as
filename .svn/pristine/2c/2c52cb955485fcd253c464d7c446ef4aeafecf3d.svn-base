package com.cn7782.management.android.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.util.SharedPreferenceUtil;

public class LoginSettingActivity extends BaseActivity {
	
	private EditText IP, PORT;
	private Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_loginsetting);
		IP = (EditText) findViewById(R.id.ip);
		PORT = (EditText) findViewById(R.id.port);
		String ip = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, "ip",
				LoginSettingActivity.this);
		String port = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, "port",
				LoginSettingActivity.this);
		if (ip == null) {
			ip = "";
		}
		if (port == null) {
			port = "";
		}
		if (ip.equals("")) {
			IP.setText(ActionUrl.IP);
		} else {
			IP.setText(ip);
		}
		if (port.equals("")) {
			PORT.setText(ActionUrl.PORT);
		} else {
			PORT.setText(port);
		}

		button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferenceUtil.modify(PreferenceConstant.MARK_ID_TABLE,
						"ip", IP.getText().toString(),
						LoginSettingActivity.this);
				SharedPreferenceUtil.modify(PreferenceConstant.MARK_ID_TABLE,
						"port", PORT.getText().toString(),
						LoginSettingActivity.this);
				ActionUrl.changeIP(LoginSettingActivity.this);
				finish();
			}
		});
	}
}
