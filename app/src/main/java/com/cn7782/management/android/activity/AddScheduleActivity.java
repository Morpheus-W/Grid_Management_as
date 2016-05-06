package com.cn7782.management.android.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.activity.bean.CustomDate;
import com.cn7782.management.android.activity.bean.ScheduleBean;
import com.cn7782.management.android.activity.datetimepickers.DateTimePickerDialog;
import com.cn7782.management.android.activity.datetimepickers.DateTimePickerDialog.OnDateTimeSetListener;
import com.cn7782.management.android.activity.dialog.CancelDialog;
import com.cn7782.management.android.activity.dialog.JobTypeDialog;
import com.cn7782.management.android.activity.dialog.JobTypeDialog.MyJobtype;
import com.cn7782.management.android.constants.GlobalConstant;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.http.HttpClient;
import com.cn7782.management.http.MyAsyncHttpResponseHandler;
import com.cn7782.management.util.DateUtil;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * @author Administrator
 */
public class AddScheduleActivity extends BaseActivity implements OnClickListener, MyJobtype {
    private TextView mCancelTv;
    private TextView mConfirmTv;
    private CustomDate mCustomDate;

    private TextView title, content, jobType, begTime, endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //通过代码设置应用主题：无标题，布局文件中设置导致DateTimePicker显示异常
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_schedule);

        findById();
        //获取时间
        getCustomDate();

        setTextData();
    }


    private void findById() {
        //返回
        mCancelTv = (TextView) this.findViewById(R.id.cancel_tv);
        //提交
        mConfirmTv = (TextView) this.findViewById(R.id.confirm_tv);
        //标题
        title = (TextView) this.findViewById(R.id.content_title);
        //日程内容
        content = (TextView) this.findViewById(R.id.plan_content);
        //工作类型
        jobType = (TextView) this.findViewById(R.id.jobtype_value);
        //日程开始时间
        begTime = (TextView) this.findViewById(R.id.plan_beg_time);
        //日程结束时间
        endTime = (TextView) this.findViewById(R.id.plan_end_time);

        setOnClickListener();
    }

    private void setTextData() {
        StringBuffer str = new StringBuffer();
        str.append(mCustomDate.toString() + " ");
        int hour1 = DateUtil.getHour();
        str.append(hour1 < 10 ? "0" + hour1 : hour1);
        str.append(":");
        int minute1 = DateUtil.getMinute();
        str.append(minute1 < 10 ? "0" + minute1 : minute1);
        begTime.setText(str.toString());

        str = new StringBuffer();
        str.append(mCustomDate.toString() + " ");
        int hour2 = DateUtil.getHour() + 1;
        str.append(hour2 < 10 ? "0" + hour2 : hour2);
        str.append(":");
        int minute2 = DateUtil.getMinute();
        str.append(minute2 < 10 ? "0" + minute2 : minute2);
        endTime.setText(str.toString());

    }

    private void setOnClickListener() {
        mCancelTv.setOnClickListener(this);
        mConfirmTv.setOnClickListener(this);

        jobType.setOnClickListener(this);
        begTime.setOnClickListener(this);
        endTime.setOnClickListener(this);

        findViewById(R.id.choose_begTime).setOnClickListener(this);
        findViewById(R.id.choose_endTime).setOnClickListener(this);

        findViewById(R.id.choose_jobType).setOnClickListener(this);

    }


    private void getCustomDate() {
        mCustomDate = (CustomDate) getIntent()
                .getSerializableExtra(ScheduleManagerActivity.MAIN_ACTIVITY_CLICK_DATE);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_tv:
                showCancelDialogState(true);
                break;
            case R.id.confirm_tv:
                String con = content.getText().toString();
                String tt = title.getText().toString();
                if ("".equals(tt)) {
                    Toast.makeText(AddScheduleActivity.this, "标题不能为空！", Toast.LENGTH_SHORT).show();
                    break;
                }
                if ("".equals(con)) {
                    Toast.makeText(AddScheduleActivity.this, "内容不能为空！", Toast.LENGTH_SHORT).show();
                    break;
                }
                //日程提交
                ScheduleBean sch = new ScheduleBean();
                sch.setBegtime(begTime.getText().toString());
                sch.setEndtime(endTime.getText().toString());
                sch.setContent(con);
                sch.setTitle(tt);
                sch.setStatus("0");
                sch.setJobType(GlobalConstant.getJobStrMap().get(jobType.getText().toString()));

                submit(sch);
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
        }
    }

    private void submit(ScheduleBean sch) {
        // TODO 增加日程
        String tokenId = SharedPreferenceUtil.getValue(
                PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
                AddScheduleActivity.this);
        String userId = SharedPreferenceUtil.getValue(
                PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.USER_ID,
                AddScheduleActivity.this);
        RequestParams param = new RequestParams();
        param.put("token_id", tokenId);
        param.put("title", sch.getTitle());
        param.put("context", sch.getContent());
        param.put("startTime", sch.getBegtime() + ":00");
        param.put("endTime", sch.getEndtime() + ":00");
        param.put("jobType", sch.getJobType());
        param.put("status", sch.getStatus());

        param.put("developUser", userId);
        param.put("developTime", getStringDate(System.currentTimeMillis()) + ":00");

        HttpClient.post(AddScheduleActivity.this, ActionUrl.SCHEDULEUPDATE, param,
                new MyAsyncHttpResponseHandler(AddScheduleActivity.this,
                        "请稍后...") {
                    public void onSuccess(String results) {
                        super.onSuccess(results);
                        try {
                            JSONObject jsonObject = new JSONObject(results);
                            if (jsonObject.has("msg")) {
                                String msg = jsonObject.isNull("msg") ? ""
                                        : jsonObject.getString("msg");
                                if (msg.equals("保存成功")) {
                                    Toast.makeText(AddScheduleActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
                                }
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable arg0, String tipInfo) {
                        super.onFailure(arg0,tipInfo);
                    }

                });

    }

    public void chooseTime(int flag) {
        DateTimePickerDialog dialog = null;
        if (flag == 0) {
            dialog = new DateTimePickerDialog(this, System.currentTimeMillis(), "开始时间");
            dialog.setOnDateTimeSetListener(new OnDateTimeSetListener() {
                public void OnDateTimeSet(AlertDialog dialog, long date) {
                    String time = getStringDate(date);
                    begTime.setText(time);
                }
            });
        } else {
            dialog = new DateTimePickerDialog(this, System.currentTimeMillis(), "结束时间");
            dialog.setOnDateTimeSetListener(new OnDateTimeSetListener() {
                public void OnDateTimeSet(AlertDialog dialog, long date) {
                    String time = getStringDate(date);
                    endTime.setText(time);
                }
            });
        }
        dialog.show();
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate(Long date) {
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

    private void showCancelDialogState(boolean isVisable) {
        if (isVisable) {
            Dialog cancleDialog = new CancelDialog(this, R.style.myDialog);
            cancleDialog.show();
        }
    }

    private void showJobTypeDialog(boolean isVisable) {
        if (isVisable) {
            Dialog jobtypeDialog = new JobTypeDialog(this, R.style.myDialog);
            ((JobTypeDialog) jobtypeDialog).initMyJobtype(AddScheduleActivity.this);
            jobtypeDialog.show();
        }
    }

    @Override
    public void refreshActivity(String text) {
        //实现接口方法，更新工作类型
        jobType.setText(text);
    }
}
