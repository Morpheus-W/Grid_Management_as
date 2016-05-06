package com.cn7782.management.android.activity.dialog;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.AddScheduleActivity;
import com.cn7782.management.android.activity.ScheduleManagerActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.view.Gravity;
import android.view.WindowManager.LayoutParams;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class JobTypeDialog extends Dialog implements OnClickListener{

	private Context context;
	private MyJobtype myJobtype;
	public JobTypeDialog(Context context) {
		super(context);
	}
	public JobTypeDialog(Context context,int theme){
		super(context,theme);
		this.context = context;
	}
	public void initMyJobtype(MyJobtype myJobtype){
		this.myJobtype = myJobtype;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jobtype_dialog_layout);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		getWindow().setGravity(Gravity.BOTTOM);
		
		findViewById(R.id.job).setOnClickListener(this);
		findViewById(R.id.event).setOnClickListener(this);
		findViewById(R.id.conference).setOnClickListener(this);
		findViewById(R.id.task).setOnClickListener(this);
		findViewById(R.id.business).setOnClickListener(this);
		findViewById(R.id.work).setOnClickListener(this);
		
	}
	public interface MyJobtype{
		public void refreshActivity(String text);
	}
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.job:
		case R.id.event:
		case R.id.task:
		case R.id.business:
		case R.id.conference:
		case R.id.work:
			myJobtype.refreshActivity(((TextView)v).getText().toString());
			dismiss();
			break;
		}
	}
	
}

