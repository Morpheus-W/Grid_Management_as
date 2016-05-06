package com.cn7782.management.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.ChangeInformationActivity;
import com.cn7782.management.android.activity.ChangeMessageActivity;
import com.cn7782.management.android.activity.FallDownWarnActivity;
import com.cn7782.management.android.activity.FeedbackActivity;
import com.cn7782.management.android.activity.LoginActivity;
import com.cn7782.management.android.activity.MainActivity;
import com.cn7782.management.android.activity.MyInformationActivity;
import com.cn7782.management.android.activity.controller.ShakeListener;
import com.cn7782.management.android.activity.controller.ShakeListener.OnShakeListener;
import com.cn7782.management.android.activity.controller.SoundManager;
import com.cn7782.management.android.activity.service.CoreService;
import com.cn7782.management.android.activity.tabview.MyProgressDialog;
import com.cn7782.management.android.constants.GlobalConstant;
import com.cn7782.management.util.PictureUtil;
import com.cn7782.management.view.ToggleButton.OnToggleChanged;
import com.slidingmenu.lib.SlidingMenu;


public class SlidingListView extends View implements OnClickListener,View.OnTouchListener {
	private View slidinglistview;
	private Context mContext;
	private ImageView image;
	private SlidingMenu mOwner;
	private String name, address;
	
	private SoundManager mSoundManager;
	private Handler handler;
	private MyProgressDialog myProgressDialog;


	public SlidingListView(Context context,Handler handler) {
		super(context);
		this.mContext = context;
		this.handler = handler;
		slidinglistview = LayoutInflater.from(mContext).inflate(
				R.layout.fragment_more, null);
		initViews(slidinglistview);
	}

	public void setdata(String mname, String maddress, String userPhoto) {
		this.name = mname;
		this.address = maddress;
		((TextView) slidinglistview.findViewById(R.id.user_name))
				.setText(getname());
		((TextView) slidinglistview.findViewById(R.id.city))
				.setText(getaddress());
		ImageView point_head = (ImageView) slidinglistview
				.findViewById(R.id.point_head);
		PictureUtil.ShowPicture(point_head, mContext, userPhoto);
	}

	private String getname() {
		return name;
	}

	private String getaddress() {
		return address;
	}

	private void initViews(View v) {

		v.findViewById(R.id.personal_data_layout).setOnClickListener(this);
		v.findViewById(R.id.personal_data_layout).setOnTouchListener(this);
		v.findViewById(R.id.modify_password_layout).setOnClickListener(this);
		v.findViewById(R.id.modify_password_layout).setOnTouchListener(this);
		v.findViewById(R.id.message_center_layout).setOnClickListener(this);
		v.findViewById(R.id.message_center_layout).setOnTouchListener(this);
		v.findViewById(R.id.exit_login_layout).setOnClickListener(this);
		v.findViewById(R.id.exit_login_layout).setOnTouchListener(this);
		v.findViewById(R.id.package_name).setOnClickListener(this);//版本更新
		v.findViewById(R.id.package_name).setOnTouchListener(this);//版本更新
		v.findViewById(R.id.Feedback_layout).setOnClickListener(this);//意见反馈
		v.findViewById(R.id.Feedback_layout).setOnTouchListener(this);//意见反馈
		//摇一摇声音
		mSoundManager = new SoundManager();
		mSoundManager.initSounds(mContext);
		mSoundManager.addSound(1, R.raw.ring);
		
		//开启摇一摇告警
		ToggleButton shake = (ToggleButton) v.findViewById(R.id.shake_toggle);
		shake.setOnToggleChanged(new OnToggleChanged() {
			
			@Override
			public void onToggle(boolean on) {
				//利用单例保证开启和注销的是同一个对象的传感器
				ShakeListener mShakeListener = ShakeListener.getInstance(mContext);
				mShakeListener.setOnShakeListener(new Shake());
				if(on){
					mShakeListener.start();
				}else{
					mShakeListener.stop();
				}
			}
		});
		//开启摔落告警
		ToggleButton falldown = (ToggleButton) v.findViewById(R.id.falldown_toggle);
		falldown.setOnToggleChanged(new OnToggleChanged() {
			
			@Override
			public void onToggle(boolean on) {
				Intent coreIntent = new Intent(mContext, CoreService.class);
				coreIntent.putExtra("serviceName", "falldown");
				if(on){
					mContext.startService(coreIntent);
				}else{
					mContext.stopService(coreIntent);
				}
			}
		});
	}

	public void setOwner(SlidingMenu Owner) {
		mOwner = Owner;
	}

	public View getView() {
		return slidinglistview;
	}

	@Override
	public void onClick(View v) {
		// TODO 侧滑选项 
		switch (v.getId()) {
		case R.id.personal_data_layout:
			Intent intent = new Intent(mContext, MyInformationActivity.class);
			mContext.startActivity(intent);
			break;
		case R.id.modify_password_layout:
			Intent intent2 = new Intent(mContext,
					ChangeInformationActivity.class);
			mContext.startActivity(intent2);
			break;
		case R.id.message_center_layout:
			Intent intent3 = new Intent(mContext, ChangeMessageActivity.class);
			mContext.startActivity(intent3);
			break;
		case R.id.Feedback_layout:
			Intent intent4 = new Intent(mContext, FeedbackActivity.class);
			mContext.startActivity(intent4);
			break;
		case R.id.exit_login_layout:
			Intent intent5 = new Intent(mContext, LoginActivity.class);
			intent5.putExtra("isNeedBack", "true");
			mContext.startActivity(intent5);
			((Activity) mContext).finish();
			break;
		case R.id.package_name:
			Message msg = handler.obtainMessage();
			msg.what = MainActivity.UPDATE_APK;
			handler.sendMessage(msg);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				v.setBackgroundColor(getResources().getColor(R.color.fragment_more_bg));
				break;
			case MotionEvent.ACTION_UP:
				v.setBackgroundColor(getResources().getColor(R.color.white));
				break;
		}

		return false;
	}

	class Shake implements OnShakeListener
	{

		@Override
		public void onShake()
		{
			mSoundManager.playSound(1);
			Intent intent = new Intent(
					mContext.getApplicationContext(),
					FallDownWarnActivity.class);
			intent.putExtra("COUNT",
					GlobalConstant.FALLDOWN_COUNT);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
		}

	}
}
