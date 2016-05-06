package com.cn7782.management.http;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.LoginActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class MyAsyncHttpResponseHandler extends AsyncHttpResponseHandler {
	Context mContext;
	String mTipInfo;
	AlertDialog progress;

	public MyAsyncHttpResponseHandler(Context context, String tipInfo) {
		mContext = context;
		mTipInfo = tipInfo;
	}

	@Override
	public void onStart() {
		if (mTipInfo != null)
			openLoading();
		super.onStart();
	}

	@Override
	public void onSuccess(String response) {
		if (mContext != null && !((Activity) mContext).isFinishing()) {
			super.onSuccess(response);
			onTokenTimeOut(response);
			if (mTipInfo != null)
				closeLoading();
		}
	}

	private void onTokenTimeOut(String response) {
		try {
			JSONObject json = new JSONObject(response);

			if ("TOKEN_TIMEOUT".equals(json.getString("common_return"))) {
				/*
				 * // 移除token_id SharedPreferenceUtil
				 * .removePrefrerence(PreferenceConstant.TOKEN_ID);
				 */
				// 跳转到登录页面

				Toast.makeText(mContext, "请求超时，请重新登陆继续刚才操作!",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(mContext, LoginActivity.class);
				intent.putExtra("isNeedBack", "true");
				mContext.startActivity(intent);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	private void openLoading() {
		if (progress == null || !progress.isShowing()) {
			loadingLayout();
		}
	}

	@Override
	public void onFailure(Throwable arg0, String tipInfo) {
		if (mContext != null && !((Activity) mContext).isFinishing()) {
			if (progress != null && progress.isShowing()) {
				closeLoading();
			}
			super.onFailure(arg0, tipInfo);
		}
	}

	public void loadingLayout() {
		if (mContext == null) {
			return;
		}
		progress = new AlertDialog.Builder(mContext).create();
		progress.show();
		progress.setCanceledOnTouchOutside(false);
		Window window = progress.getWindow();
		window.setContentView(R.layout.loading);

		TextView tipInfoTV = (TextView) window.findViewById(R.id.loading_tip);
		tipInfoTV.setText(mTipInfo);

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
		ImageView loadIV = (ImageView) window.findViewById(R.id.loading);
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

		progress.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				onFinish();
			}
		});
	}

	private void closeLoading() {
		if (mContext != null && !((Activity) mContext).isFinishing()
				&& progress != null && progress.isShowing()) {
			progress.dismiss();
		}
	}
}
