package com.cn7782.management.android.activity;



import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.BaseApplication;
import com.cn7782.management.android.activity.controller.FallDownController;
import com.cn7782.management.android.constants.GlobalConstant;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.http.HttpClient;
import com.cn7782.management.http.MyAsyncHttpResponseHandler;
import com.cn7782.management.util.MedieaPlayerUtil;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.cn7782.management.util.ToastUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class FallDownWarnActivity extends BaseActivity implements OnClickListener {
	
	private static final String TAG = "FallDownWarnAct";
	private Button falldownCallBtn;
	private Button falldownCancelBtn;
	private TextView countdownTv;
	private int currentCount = GlobalConstant.FALLDOWN_COUNT;
	
	private TimeCount timeCount ;
	
	private ReceiverMyLocation receiverMyLocation;
	private double longitude = 0;
	private double latitude = 0;
	
	MedieaPlayerUtil medieaPlayerUtil;
	protected AlertDialog mAlertDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		medieaPlayerUtil=new MedieaPlayerUtil(this);
		setContentView(R.layout.falldown_act);
		
		initLogic();
		
		InitUI();
		
		regReceiver();
		
		//开始30s倒计时
		if(currentCount > 1){
			timeCount = new TimeCount(currentCount*1000, 1000);
			timeCount.start();	
   	     	BaseApplication.getInstance().setIsForegroundCountDown(true);
		}else{
			this.finish();
		}
		
		NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(GlobalConstant.FALL_DOWN_NOTIFICATION_ID);
		
		
	}

	private void initLogic() {
		FallDownController.getInstance().setContext(this);
		FallDownController.getInstance().setHandler(handler);
		currentCount = BaseApplication.getInstance().getFallDownCount();
	}

	private void InitUI() {
		falldownCallBtn = (Button)findViewById(R.id.falldownCallBtn);
		falldownCallBtn.setOnClickListener(this);
		falldownCancelBtn = (Button)findViewById(R.id.falldownCancelBtn);
		falldownCancelBtn.setOnClickListener(this);
		countdownTv = (TextView)findViewById(R.id.countdownTv);
		countdownTv.setText("--");
	}
	/***
	 * 注册BroadcastReceiver.
	 */
	private void regReceiver() {

		receiverMyLocation = new ReceiverMyLocation();

		IntentFilter mylocationFilter = new IntentFilter(
				BaseApplication.LOCATION_RECEIVER_ACTION);
		registerReceiver(receiverMyLocation, mylocationFilter);

	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.falldownCallBtn:
			timeCount.cancel();
			FallDownController.getInstance().callFallDownWarn(false);
   	     	BaseApplication.getInstance().setIsForegroundCountDown(false);
   	     	BaseApplication.getInstance().setFallDownCount(GlobalConstant.FALLDOWN_COUNT);
   	     	//立即求助，发送求助信息
			sendHelpInfo();
			break;
			
		case R.id.falldownCancelBtn:
			this.finish();
			timeCount.cancel();
   	     	BaseApplication.getInstance().setIsForegroundCountDown(false);
   	     	BaseApplication.getInstance().setFallDownCount(GlobalConstant.FALLDOWN_COUNT);
			
			break;
			
		default:
			break;
		}

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK || keyCode == event.KEYCODE_HOME) {
    		Log.i(TAG, "onKeyDown KEYCODE_BACK");
			timeCount.cancel();
   	     	BaseApplication.getInstance().setIsForegroundCountDown(false);
   	     	BaseApplication.getInstance().setFallDownCount(GlobalConstant.FALLDOWN_COUNT);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	class TimeCount extends CountDownTimer {
    	public TimeCount(long millisInFuture, long countDownInterval) {
    		super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
    	}
    	@Override
    	public void onFinish() {//计时完毕时触发
    		Log.i(TAG, "countdown finished!");
    		FallDownController.getInstance().callFallDownWarn(false);
   	     	BaseApplication.getInstance().setIsForegroundCountDown(false);
   	     	BaseApplication.getInstance().setFallDownCount(GlobalConstant.FALLDOWN_COUNT);
   	     	//计时结束发送请求
   	     	sendHelpInfo();
    	}
    	@Override
    	public void onTick(long millisUntilFinished){//计时过程显示
    		currentCount = (int) (millisUntilFinished/1000);
    		countdownTv.setText(""+currentCount);
    		long[] pattern = {800, 50, 400, 30};
    		FallDownController.getInstance().Vibrate(pattern, false);

			Log.i(TAG, "on tick:"+currentCount);
    	}
    	
    }
	private void sendHelpInfo(){
		RequestParams param = new RequestParams();
		String tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				FallDownWarnActivity.this);
		param.put("token_id", tokenId);
		param.put("longitude", longitude+"");
		param.put("latitude", latitude+"");
		HttpClient.post(FallDownWarnActivity.this, ActionUrl.SOS, param,
				new MyAsyncHttpResponseHandler(FallDownWarnActivity.this,
						"发送中,请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("提交成功")) {
									showFallDownCallSuccessDialog();
								} else {
									falldownCallFailed("");
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					public void onFailure(Throwable arg0, String tipInfo) {
						falldownCallFailed("");
						super.onFailure(arg0, tipInfo);
					}
				});
	}
	private void showFallDownCallSuccessDialog(){
		createDialog("温馨提示", "您的求助信息已发送到您的同事，请保持电话畅通！");
		mAlertDialog.setButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				FallDownWarnActivity.this.finish();
			}
		});		
		mAlertDialog.show();
		
		ToastUtil.showMessage(this, "您的求助信息已发送到您的同事，请保持电话畅通！");
		FallDownWarnActivity.this.finish();
		
		
	}
	
	private void falldownCallFailed(String msg){
		
		boolean sendMsgSuc = FallDownController.getInstance().sendMsgInBg(msg);
		
		if(!sendMsgSuc){//响铃提醒
			medieaPlayerUtil.	playAlarmRing();
			createDialog("响铃求助", "求助失败，拨打急救电话112！");
		    mAlertDialog.setButton("确定", fallDownDialogListener);
		    mAlertDialog.setButton2("取消", fallDownDialogListener);
		    mAlertDialog.show();
		}else{
			createDialog(getString(R.string.dialog_title), "您已发送求助信息到其他同事");
			mAlertDialog.setButton2("确定", fallDownDialogListener);			
			mAlertDialog.show();
			
		}
	}
	
	protected DialogInterface.OnClickListener fallDownDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON1:{
				
//				if(FallDownWarnActivity.this.getmMediaPlayer()!=null){					
					medieaPlayerUtil.	stopPlay();					
//				}
							
				 Uri uri = Uri.parse("tel:112");  
	             Intent intent = new Intent(Intent.ACTION_CALL, uri);  
	             startActivity(intent);
	             
	             FallDownWarnActivity.this.finish();
				
				break;
			}
			case DialogInterface.BUTTON2: {
             				
//              if(FallDownWarnActivity.this.getmMediaPlayer()!=null){
				medieaPlayerUtil.stopPlay();					
//              }
	          FallDownWarnActivity.this.finish();
			  dialog.cancel();
			   break;
			}

			 default:
				   break;
			
			}

		}
	};
	public void createDialog(String title, String msg) {

		mAlertDialog = new AlertDialog.Builder(this).create();
		mAlertDialog.setTitle(title);
		mAlertDialog.setIcon(R.drawable.ic_launcher);
		mAlertDialog.setMessage(msg);
		mAlertDialog.setCancelable(false);
	}
	
	@Override
	protected void onDestroy() {
		// TODO 注销广播接受者
		if (null != receiverMyLocation) {
			unregisterReceiver(receiverMyLocation);
		}
		super.onDestroy();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.obj != null) {
			}
		};
	};
	class ReceiverMyLocation extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO 广播接收者重写父类方法
			if (intent.getAction().equals(
					BaseApplication.LOCATION_RECEIVER_ACTION)) {
				try {
					latitude = intent.getDoubleExtra("lat", 0);
					longitude = intent.getDoubleExtra("lng", 0);
					
				} catch (Exception e) {
					Toast.makeText(FallDownWarnActivity.this, "数据出错",
							Toast.LENGTH_SHORT).show();
				}
			}

		}
	}

}
