package com.cn7782.management.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	
	private static Toast mToast;

	/**
	 * 显示消息
	 * 
	 * @param context
	 * @param message
	 */
	public static void showMessage(Context context, String message)
	{
		if (mToast == null)
		{
			mToast = Toast.makeText(context, message, 2000);
		} else
		{
			mToast.setText(message);
			mToast.setDuration(2000);
		}
		mToast.show();
	}

	public static void cancelToast()
	{
		if (mToast != null)
		{
			mToast.cancel();
		}
	}

	public static void showMessage(Context context, int id) {

		Toast.makeText(context, context.getResources().getString(id),
				Toast.LENGTH_LONG).show();
	}

}