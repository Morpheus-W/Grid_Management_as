package com.cn7782.management.android.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.activity.bean.ScheduleBean;
import com.cn7782.management.android.activity.datetimepickers.DateTimePickerDialog;
import com.cn7782.management.android.activity.datetimepickers.DateTimePickerDialog.OnDateTimeSetListener;
import com.cn7782.management.android.activity.dialog.CancelDialog;
import com.cn7782.management.android.activity.dialog.JobTypeDialog;
import com.cn7782.management.android.activity.dialog.JobTypeDialog.MyJobtype;
import com.cn7782.management.android.activity.dialog.StatusDialog;
import com.cn7782.management.android.activity.dialog.StatusDialog.MyStatus;
import com.cn7782.management.android.constants.GlobalConstant;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.http.HttpClient;
import com.cn7782.management.http.MyAsyncHttpResponseHandler;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * @author Administrator
 *
 */
public class UpdateScheduleActivity extends BaseActivity
		implements OnClickListener,MyJobtype,MyStatus{
	private TextView mCancelTv;
	private TextView mConfirmTv;
	
	private TextView title,content,jobType,begTime,endTime,status;
	
	
	private ScheduleBean schedule;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//通过代码设置应用主题：无标题，布局文件中设置导致DateTimePicker显示异常
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_update_schedule);
		
		Intent intent = getIntent();
		schedule = (ScheduleBean) intent.getSerializableExtra("schedule");
		
		findById();
		initView();
	}


	private void findById() {
		//返回
		mCancelTv = (TextView) this.findViewById(R.id.cancel_tv);
		//提交
		mConfirmTv = (TextView)this.findViewById(R.id.confirm_tv);
		//标题
		title = (TextView)this.findViewById(R.id.content_title);
		//日程内容
		content = (TextView)this.findViewById(R.id.plan_content);
		//工作类型
		jobType = (TextView)this.findViewById(R.id.jobtype_value);
		//日程开始时间
		begTime = (TextView)this.findViewById(R.id.plan_beg_time);
		//日程结束时间
		endTime = (TextView)this.findViewById(R.id.plan_end_time);
		//状态
		status = (TextView)this.findViewById(R.id.status_value);
		
		setOnClickListener();
		
	}

	private void initView() {
		if(schedule != null){
			title.setText(schedule.getTitle());
			content.setText(schedule.getContent());
			jobType.setText(GlobalConstant.getJobTypeMap().get(schedule.getJobType()));
			begTime.setText(schedule.getBegtime().substring(0,16));
			endTime.setText(schedule.getEndtime().substring(0,16));
			status.setText("0".equals(schedule.getStatus())?"未完成":"已完成");
		}
	}
	private void setOnClickListener() {
		mCancelTv.setOnClickListener(this);
		mConfirmTv.setOnClickListener(this);
		
		jobType.setOnClickListener(this);
		begTime.setOnClickListener(this);
		endTime.setOnClickListener(this);
		status.setOnClickListener(this);
		
		findViewById(R.id.choose_begTime).setOnClickListener(this);
		findViewById(R.id.choose_endTime).setOnClickListener(this);
		
		findViewById(R.id.choose_status).setOnClickListener(this);
		findViewById(R.id.choose_jobType).setOnClickListener(this);
		
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel_tv:
			showCancelDialogState(true);
			break;
		case R.id.no_cancel:
			showCancelDialogState(false);
			break;
		case R.id.confirm_cancel:
			if(schedule != null)
				startActivity(new Intent(this,ScheduleManagerActivity.class));
			else
				finish();
			break;
		case R.id.confirm_tv:
			if(schedule != null){
				schedule.setBegtime(begTime.getText().toString());
				schedule.setEndtime(endTime.getText().toString());
				schedule.setContent(content.getText().toString());
				schedule.setTitle(title.getText().toString());
				schedule.setStatus((status.getText().toString()).equals("未完成")?"0":"1");
				schedule.setJobType(GlobalConstant.getJobStrMap().get(jobType.getText().toString()));
				
				update(schedule);
				
			}
			break;
		case R.id.choose_begTime:
			chooseTime(0);
			break;
		case R.id.choose_endTime:
			chooseTime(1);
			break;
		case R.id.choose_jobType:
			showJobTypeDialog(true);
			break;
		case R.id.choose_status:
			showStatusDialog(true);
			break;
		}
	}
	
	private void update(ScheduleBean sch) {
		// TODO 修改日程,修改时需上传id
		String tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				UpdateScheduleActivity.this);
		String userId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.USER_ID,
				UpdateScheduleActivity.this);
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("id", sch.getScheduleId());
		param.put("title", sch.getTitle());
		param.put("context", sch.getContent());
		param.put("startTime", sch.getBegtime()+":00");
		param.put("endTime", sch.getEndtime()+":00");
		param.put("jobType", sch.getJobType());
		param.put("status", sch.getStatus());
		param.put("developUser", userId);
		param.put("developTime", getStringDate(System.currentTimeMillis())+":00");
		
		HttpClient.post(UpdateScheduleActivity.this, ActionUrl.SCHEDULEUPDATE, param,
				new MyAsyncHttpResponseHandler(UpdateScheduleActivity.this,
						"请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("保存成功")) {
									Toast.makeText(getApplicationContext(), "修改成功！", Toast.LENGTH_SHORT);
								}
								startActivity(new Intent(UpdateScheduleActivity.this,ScheduleManagerActivity.class));
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					@Override
					public void onFailure(Throwable arg0, String tipInfo) {
						super.onFailure(arg0, tipInfo);
					}
					
				});
		
	}

	public void chooseTime(int flag)
	{
		DateTimePickerDialog dialog  = null;
		if(flag == 0){
			dialog = new DateTimePickerDialog(this, System.currentTimeMillis(),"开始时间");
			dialog.setOnDateTimeSetListener(new OnDateTimeSetListener()
			{
				public void OnDateTimeSet(AlertDialog dialog, long date)
				{
					String time = getStringDate(date);
					begTime.setText(time);
				}
			});
		}else{
			dialog = new DateTimePickerDialog(this, System.currentTimeMillis(),"结束时间");
			dialog.setOnDateTimeSetListener(new OnDateTimeSetListener()
			{
				public void OnDateTimeSet(AlertDialog dialog, long date)
				{
					String time = getStringDate(date);
					endTime.setText(time);
				}
			});
		}
		dialog.show();
	}
	/**
	* 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	*
	*/
	public static String getStringDate(Long date) 
	{
		//取值只到分钟
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String dateString = formatter.format(date);
		
		return dateString;
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	showCancelDialogState(true);
             return true;
         }
         return super.onKeyDown(keyCode, event);
     }
	
	private void showCancelDialogState(boolean isVisable){
		if(isVisable){
			Dialog cancleDialog = new CancelDialog(this,R.style.myDialog);
			cancleDialog.show();
		}
	}
	private void showJobTypeDialog(boolean isVisable){
		if(isVisable){
			Dialog jobtypeDialog = new JobTypeDialog(this,R.style.myDialog);
			((JobTypeDialog)jobtypeDialog).initMyJobtype(this);
			jobtypeDialog.show();
		}
	}
	private void showStatusDialog(boolean isVisable){
		if(isVisable){
			Dialog statusDialog = new StatusDialog(this,R.style.myDialog);
			((StatusDialog)statusDialog).initMyStatus(this);
			statusDialog.show();
		}
	}
	@Override
	public void refreshActivity(String text) {
		//实现接口方法，更新工作类型
		jobType.setText(text);
	}


	@Override
	public void refreshStatus(String text) {
		// 实现接口方法，更新状态信息
		status.setText(text);
	}
}
