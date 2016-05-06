package com.cn7782.management.android.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.ScheduleManagerActivity;

public class CancelDialog extends Dialog implements OnClickListener{

	private Context context;
	public CancelDialog(Context context,int theme){
		super(context,theme);
		this.context = context;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comp_dialog_layout);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		getWindow().setGravity(Gravity.BOTTOM);
		
		this.findViewById(R.id.no_cancel).setOnClickListener(this);
		this.findViewById(R.id.confirm_cancel).setOnClickListener(this);
		
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.no_cancel:
			dismiss();
			break;
		case R.id.confirm_cancel:
			dismiss();
			Intent intent = new Intent(context,ScheduleManagerActivity.class);
			context.startActivity(intent);
			break;
		}
	}
	
}

