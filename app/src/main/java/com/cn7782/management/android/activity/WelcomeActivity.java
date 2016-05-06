package com.cn7782.management.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;

public class WelcomeActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//全屏设置隐藏所有装饰
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		new CountDownTimer(1000,1000){
			 
			@Override
			public void onTick(long millisUntilFinished) {
			}
			@Override
			public void onFinish() {
				Intent intent = new Intent(	WelcomeActivity.this,LoginActivity.class);
				startActivity(intent);

				int VERSION=Integer.parseInt(android.os.Build.VERSION.SDK);
				if(VERSION >= 5){
					WelcomeActivity.this.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
				}
				WelcomeActivity.this.finish();
			}
		}.start();
	}
}
