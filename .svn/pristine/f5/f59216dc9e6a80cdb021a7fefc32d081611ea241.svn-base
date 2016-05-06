package com.cn7782.management.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.activity.service.MessengerService;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.cn7782.management.view.ToggleButton;

public class ChangeMessageActivity extends BaseActivity {
	
	private ToggleButton msgToogleBack,msgToogleSound,msgToogleVibrator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_change_message);
		findViewById(R.id.title_back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        //开启后台接收消息
        msgToogleBack = (ToggleButton) findViewById(R.id.msg_toggle_back);
        msgToogleBack.setOnToggleChanged(new ToggleButton.OnToggleChanged() {

            @Override
            public void onToggle(boolean on) {
                if(on){
                    //先取消绑定，再重新启动服务
                    Intent msgService = new Intent(ChangeMessageActivity.this,MessengerService.class);
                    startService(msgService);
                    SharedPreferenceUtil
                            .modify(PreferenceConstant.MARK_ID_TABLE,
                                    PreferenceConstant.IS_MSG_BACK,
                                    "true",
                                    ChangeMessageActivity.this);
                }else{
                    SharedPreferenceUtil
                            .modify(PreferenceConstant.MARK_ID_TABLE,
                                    PreferenceConstant.IS_MSG_BACK,
                                    "false",
                                    ChangeMessageActivity.this);
                }
            }
        });
        //开启提示音,默认开启
        msgToogleSound = (ToggleButton) findViewById(R.id.msg_toggle_sound);
        msgToogleSound.setOnToggleChanged(new ToggleButton.OnToggleChanged() {

            @Override
            public void onToggle(boolean on) {
                if (on) {
                    SharedPreferenceUtil
                            .modify(PreferenceConstant.MARK_ID_TABLE,
                                    PreferenceConstant.IS_MSG_SOUND,
                                    "true",
                                    ChangeMessageActivity.this);
                } else {
                    SharedPreferenceUtil
                            .modify(PreferenceConstant.MARK_ID_TABLE,
                                    PreferenceConstant.IS_MSG_SOUND,
                                    "false",
                                    ChangeMessageActivity.this);
                }
            }
        });
        //开启震动,默认开启
        msgToogleVibrator = (ToggleButton) findViewById(R.id.msg_toggle_vibrator);
        msgToogleVibrator.setOnToggleChanged(new ToggleButton.OnToggleChanged() {

            @Override
            public void onToggle(boolean on) {
                if(on){
                    SharedPreferenceUtil
                            .modify(PreferenceConstant.MARK_ID_TABLE,
                                    PreferenceConstant.IS_MSG_VIBRATOR,
                                    "true",
                                    ChangeMessageActivity.this);
                }else{
                    SharedPreferenceUtil
                            .modify(PreferenceConstant.MARK_ID_TABLE,
                                    PreferenceConstant.IS_MSG_VIBRATOR,
                                    "false",
                                    ChangeMessageActivity.this);
                }
            }
        });
        initToggleButton();
	}

    private void initToggleButton() {
        String msgBack = SharedPreferenceUtil.getValue(
                PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.IS_MSG_BACK,
                ChangeMessageActivity.this);
        String msgSound = SharedPreferenceUtil.getValue(
                PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.IS_MSG_SOUND,
                ChangeMessageActivity.this);
        String msgVibrator = SharedPreferenceUtil.getValue(
                PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.IS_MSG_VIBRATOR,
                ChangeMessageActivity.this);
        if (msgBack != null && msgBack.equals("true")){
            msgToogleBack.setToggleOn();
        }else{
            msgToogleBack.setToggleOff();
        }
        if (msgSound == null || (msgSound != null && msgSound.equals("true"))){
            msgToogleSound.setToggleOn();
        }else{
            msgToogleSound.setToggleOff();
        }
        if (msgVibrator == null || (msgVibrator != null && msgVibrator.equals("true"))){
            msgToogleVibrator.setToggleOn();
        }else{
            msgToogleVibrator.setToggleOff();
        }
    }
}
