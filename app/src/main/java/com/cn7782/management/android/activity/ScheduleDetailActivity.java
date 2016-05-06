package com.cn7782.management.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.activity.bean.ScheduleBean;
import com.cn7782.management.android.constants.GlobalConstant;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.android.db.DBHelper;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.http.HttpClient;
import com.cn7782.management.http.MyAsyncHttpResponseHandler;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class ScheduleDetailActivity extends BaseActivity implements OnClickListener{
	
	private TextView title;
	private TextView content;
	private TextView time;
	private TextView jobType;
	private TextView status;
	
	
	private View noticeView;
	private View deleteShowView;
	private View deleteDialogView;
	
	private ScheduleBean schedule;
	private String scheduleId;
	private DBHelper dp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_schedule_detail);
		Intent intent = getIntent();
//		schedule = (ScheduleBean) intent.getSerializableExtra("schedule");
		scheduleId = intent.getStringExtra("id");
		initView();
		//初始化,通过id查询请求
		querySchedule();
		
	}

	private void initView() {
		title = (TextView)findViewById(R.id.schedule_title);
		content = (TextView) findViewById(R.id.schedule_content);
		time = (TextView) findViewById(R.id.scheduletime);
		status = (TextView) findViewById(R.id.status_value);
		jobType = (TextView) findViewById(R.id.jobtype_value);
		//返回
		findViewById(R.id.back).setOnClickListener(this);
		//编辑
		findViewById(R.id.edit_circle_view).setOnClickListener(this);
		//删除
		findViewById(R.id.delete_circle_view).setOnClickListener(this);
		//取消删除
		findViewById(R.id.no_delete).setOnClickListener(this);
		//确定删除
		findViewById(R.id.confirm_delete).setOnClickListener(this);
		
		//确认是否删除
		deleteShowView = this.findViewById(R.id.delete_show_layout);
		deleteDialogView = this.findViewById(R.id.delete_dialog_layout);
		
	}

	private void querySchedule() {
		
		// TODO 获取日程详情
		String tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				ScheduleDetailActivity.this);
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("id", scheduleId);
		HttpClient.post(ScheduleDetailActivity.this, ActionUrl.SCHEDULEDETAIL, param,
				new MyAsyncHttpResponseHandler(ScheduleDetailActivity.this,
						"请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("查询成功")) {
									if(schedule == null){
										schedule = new ScheduleBean();
									}
									schedule.setScheduleId(scheduleId);
									schedule.setTitle(jsonObject.isNull("title") ? ""
											: jsonObject.getString("title"));
									schedule.setContent(jsonObject.isNull("context") ? ""
											: jsonObject.getString("context"));
									schedule.setBegtime(jsonObject.isNull("start") ? ""
											: jsonObject.getString("start"));
									schedule.setEndtime(jsonObject.isNull("end") ? ""
											: jsonObject.getString("end"));
									schedule.setStatus(jsonObject.isNull("status") ? ""
											: jsonObject.getString("status"));
									schedule.setJobType(jsonObject.isNull("job_type") ? ""
											: jsonObject.getString("job_type"));
									
									schedule.setDevelop_user(jsonObject.isNull("develop_user") ? ""
											: jsonObject.getString("develop_user"));
									schedule.setDevelop_time(jsonObject.isNull("develop_time") ? ""
											: jsonObject.getString("develop_time"));
									
									initSchedule();
								}
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

	protected void initSchedule() {
		
		title.setText(schedule.getTitle());
		content.setText(schedule.getContent());
		time.setText(schedule.getBegtime().substring(5, 16)+"-"+schedule.getEndtime().substring(5, 16));
		jobType.setText(GlobalConstant.getJobTypeMap().get(schedule.getJobType()));
		status.setText("0".equals(schedule.getStatus())?"未完成":"已完成");

	}

	@Override
	public void onClick(View v) {
		// TODO 统一处理监听
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.edit_circle_view:
			Intent intent = new Intent(this,UpdateScheduleActivity.class);
			//利用Bundle传递对象
			Bundle bundle = new Bundle();
			bundle.putSerializable("schedule", schedule);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		case R.id.delete_circle_view:
			showDeleteDialog(true);
			break;
		case R.id.no_delete:
			showDeleteDialog(false);
			break;
		case R.id.confirm_delete:
			//删除该日程
			deleteSchedule(scheduleId);
			break;
		}
		
	}

	private void deleteSchedule(String scheduleId2) {
		// TODO 删除对应日程
		String tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				ScheduleDetailActivity.this);
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("id", scheduleId);
		HttpClient.post(ScheduleDetailActivity.this, ActionUrl.SCHEDULEDELETE, param,
				new MyAsyncHttpResponseHandler(ScheduleDetailActivity.this,
						"请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("删除成功")) {
									Toast.makeText(getApplicationContext(), "已删除！", Toast.LENGTH_SHORT);
									finish();
								}
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

	private void showDeleteDialog(boolean isVisable) {
		// TODO 弹出是否删除选择
		Animation anim = null;
		if(isVisable){
			anim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_to_up);
			deleteDialogView.setAnimation(anim);
			deleteShowView.setVisibility(View.VISIBLE);
			
		}else{
			anim = AnimationUtils.loadAnimation(this, R.anim.slide_up_to_bottom);
			deleteDialogView.setAnimation(anim);
			deleteShowView.setVisibility(View.GONE);
		}
	}
}
