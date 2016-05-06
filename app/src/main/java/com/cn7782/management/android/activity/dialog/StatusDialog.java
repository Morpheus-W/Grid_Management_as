package com.cn7782.management.android.activity.dialog;

import com.cn7782.management.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager.LayoutParams;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class StatusDialog extends Dialog implements OnClickListener{

	private Context context;
	private MyStatus myStatus;
	public StatusDialog(Context context) {
		super(context);
	}
	public StatusDialog(Context context,int theme){
		super(context,theme);
		this.context = context;
	}
	public void initMyStatus(MyStatus myStatus){
		this.myStatus = myStatus;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status_dialog_layout);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		getWindow().setGravity(Gravity.BOTTOM);
		
		findViewById(R.id.unfinish).setOnClickListener(this);
		findViewById(R.id.finish).setOnClickListener(this);
		
	}
	public interface MyStatus{
		public void refreshStatus(String text);
	}
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.unfinish:
		case R.id.finish:
			myStatus.refreshStatus(((TextView)v).getText().toString());
			dismiss();
			break;
		}
	}
	
}

