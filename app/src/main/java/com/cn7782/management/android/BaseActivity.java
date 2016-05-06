package com.cn7782.management.android;

import android.app.Activity;
import android.os.Bundle;

import com.cn7782.management.android.activity.controller.AppManager;
import com.umeng.analytics.MobclickAgent;

import cn.jpush.android.api.JPushInterface;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
	}
	
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		JPushInterface.onResume(this);
	}

	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		JPushInterface.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getAppManager().finishActivity(this);

	}

}
