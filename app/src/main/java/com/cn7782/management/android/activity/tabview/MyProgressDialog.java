package com.cn7782.management.android.activity.tabview;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn7782.management.R;


public class MyProgressDialog {
	private Context mContext;
	private String mTipInfo;
	private AlertDialog progress;
	private TextView tipInfoTV;
	private TextView backDown;

	public MyProgressDialog(Context mContext, String tipInfo) {
		super();
		this.mContext = mContext;
		mTipInfo = tipInfo;
		show();

	}

	public void show() {
		if (progress != null) {
			progress.show();
			return;
		}
		progress = new AlertDialog.Builder(mContext).create();
		progress.show();
		progress.setCanceledOnTouchOutside(false);
		progress.setCancelable(false);
		// progress.setOnKeyListener(onKeyListener)
		Window window = progress.getWindow();
		window.setContentView(R.layout.downloading);

		tipInfoTV = (TextView) window.findViewById(R.id.downloading_tip);
		tipInfoTV.setText(mTipInfo);

		//注册后台下载事件
		window.findViewById(R.id.back_down).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		/*
		 * ImageView loadIV = (ImageView) window.findViewById(R.id.loading);
		 * 
		 * // 旋转效果rotate Animation animation_rotate = new RotateAnimation(0,
		 * 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
		 * RotateAnimation.RELATIVE_TO_SELF, 0.5f); //
		 * 第一个参数fromDegrees为动画起始时的旋转角度 // 第二个参数toDegrees为动画旋转到的角度 //
		 * 第三个参数pivotXType为动画在X轴相对于物件位置类型 // 第四个参数pivotXValue为动画相对于物件的X坐标的开始位置
		 * // 第五个参数pivotXType为动画在Y轴相对于物件位置类型 //
		 * 第六个参数pivotYValue为动画相对于物件的Y坐标的开始位置
		 * animation_rotate.setRepeatCount(-1);
		 * animation_rotate.setDuration(2000);// 设置时间持续时间为 700毫秒
		 * 
		 * LinearInterpolator lin = new LinearInterpolator();
		 * animation_rotate.setInterpolator(lin);
		 * loadIV.setAnimation(animation_rotate);
		 */
		ImageView loadIV = (ImageView) window.findViewById(R.id.downloading);
		Animation animation_rotate = new RotateAnimation(0, 360,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		// 第一个参数fromDegrees为动画起始时的旋转角度
		// 第二个参数toDegrees为动画旋转到的角度
		// 第三个参数pivotXType为动画在X轴相对于物件位置类型
		// 第四个参数pivotXValue为动画相对于物件的X坐标的开始位置
		// 第五个参数pivotXType为动画在Y轴相对于物件位置类型
		// 第六个参数pivotYValue为动画相对于物件的Y坐标的开始位置
		animation_rotate.setRepeatCount(-1);
		animation_rotate.setDuration(1000);// 设置时间持续时间为 700毫秒

		LinearInterpolator lin = new LinearInterpolator();
		animation_rotate.setInterpolator(lin);
		loadIV.setAnimation(animation_rotate);

		// AnimationDrawable animationDrawable =
		// (AnimationDrawable)loadIV.getBackground();
		// if(!animationDrawable.isRunning()){
		// animationDrawable.start();
		// }

	}

	public void setMessage(String message) {
		if (tipInfoTV != null) {
			tipInfoTV.setText(message);
		}
	}

	public void dismiss() {
		if (progress != null) {
			progress.dismiss();
		}
	}
}
