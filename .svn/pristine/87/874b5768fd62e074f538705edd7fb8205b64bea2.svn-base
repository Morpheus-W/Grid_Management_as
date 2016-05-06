package com.cn7782.management.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.activity.datetimepickers.DatePickerDialog;
import com.cn7782.management.android.activity.datetimepickers.DatePickerDialog.OnDateSetListener;
import com.cn7782.management.util.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ChooseDateActivity extends BaseActivity implements OnClickListener {
	private TextView startime, endtime;
	private int oneday;
	View back;
	private final Calendar mCalendar = Calendar.getInstance();
	public static final int DATATIME = 1001;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choose_date);
		initView();
	}

	private void initView() {
		back = (View) findViewById(R.id.search_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		findViewById(R.id.sumbit_btn).setOnClickListener(this);
		startime = (TextView) findViewById(R.id.start_time);
		endtime = (TextView) findViewById(R.id.end_time);
		View v1 = (View) findViewById(R.id.same_day);
		View v2 = (View) findViewById(R.id.last_week);
		View v3 = (View) findViewById(R.id.one_week);
		View v4 = (View) findViewById(R.id.last_month);
		View v5 = (View) findViewById(R.id.one_month);
		View v6 = (View) findViewById(R.id.nearly_march);
		View v7 = (View) findViewById(R.id.last_quarter);
		View v8 = (View) findViewById(R.id.first_half_year);
		View v9 = (View) findViewById(R.id.one_year);
		v1.setOnClickListener(this);
		v2.setOnClickListener(this);
		v3.setOnClickListener(this);
		v4.setOnClickListener(this);
		v5.setOnClickListener(this);
		v6.setOnClickListener(this);
		v7.setOnClickListener(this);
		v8.setOnClickListener(this);
		v9.setOnClickListener(this);
		// 默认为本月一号
		SimpleDateFormat aDateFormat = new SimpleDateFormat("dd");
		String day = aDateFormat.format(new java.util.Date());
		oneday = Integer.valueOf(day);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, oneday);
		
		startime.setText(getIntent().getStringExtra("starttime"));
		endtime.setText(getIntent().getStringExtra("endtime"));
//		startime.setText(DateUtils.getFormatDate_yyyy_MM_dd(calendar.getTime()));
//		endtime.setText(DateUtils.getFormatDate_yyyy_MM_dd(calendar.getTime()));
		final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
				new OnDateSetListener() {

					public void onDateSet(DatePickerDialog datePickerDialog,
							int year, int month, int day) {
						startime.setText(new StringBuilder().append(pad(year))
								.append("-").append(pad(month + 1)).append("-")
								.append(pad(day)));

					}

				}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
				mCalendar.get(Calendar.DAY_OF_MONTH));
		findViewById(R.id.btnChangeDate).setOnClickListener(
				new OnClickListener() {

					private String tag;

					@Override
					public void onClick(View v) {
						datePickerDialog.show(getFragmentManager(), tag);
						;
					}
				});
		final DatePickerDialog datePickerDialog1 = DatePickerDialog
				.newInstance(new OnDateSetListener() {

					public void onDateSet(DatePickerDialog datePickerDialog,
							int year, int month, int day) {
						endtime.setText(new StringBuilder().append(pad(year))
								.append("-").append(pad(month + 1)).append("-")
								.append(pad(day)));

					}

				}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
						mCalendar.get(Calendar.DAY_OF_MONTH));
		findViewById(R.id.image).setOnClickListener(new OnClickListener() {

			private String tag;

			@Override
			public void onClick(View v) {
				datePickerDialog1.show(getFragmentManager(), tag);
				;
			}
		});
	}

	@Override
	public void onClick(View v) {
		Calendar calendar = Calendar.getInstance();
		switch (v.getId()) {
		// 当天
		case R.id.same_day:
			calendar.set(Calendar.DAY_OF_MONTH, oneday);
			endtime.setText(DateUtils.getFormatDate_yyyy_MM_dd(calendar
					.getTime()));
			startime.setText(DateUtils.getFormatDate_yyyy_MM_dd(calendar
					.getTime()));
			break;
		// 上周
		case R.id.last_week:
			endtime.setText(DateUtils.getFormatDate_yyyy_MM_dd(calendar
					.getTime()));
			calendar.add(Calendar.DAY_OF_YEAR,
					-calendar.get(Calendar.DAY_OF_WEEK) - 1);
			startime.setText(DateUtils.getFormatDate_yyyy_MM_dd(calendar
					.getTime()));
			break;
		// 本周
		case R.id.one_week:
			endtime.setText(DateUtils.getFormatDate_yyyy_MM_dd(calendar
					.getTime()));
			calendar.add(Calendar.DAY_OF_YEAR,
					-calendar.get(Calendar.DAY_OF_WEEK) + 1);
			startime.setText(DateUtils.getFormatDate_yyyy_MM_dd(calendar
					.getTime()));
			break;
		// 上个月
		case R.id.last_month:
			calendar.add(Calendar.MONTH, -1);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			startime.setText(DateUtils.getFormatDate_yyyy_MM_dd(calendar
					.getTime()));
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			endtime.setText(DateUtils.getFormatDate_yyyy_MM_dd(calendar
					.getTime()));
			break;
		// 本月
		case R.id.one_month:
			endtime.setText(DateUtils.getFormatDate_yyyy_MM_dd(calendar
					.getTime()));
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			startime.setText(DateUtils.getFormatDate_yyyy_MM_dd(calendar
					.getTime()));
			break;
		// 近三月
		case R.id.nearly_march:
			calendar.add(Calendar.MONTH, -3);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			startime.setText(DateUtils.getFormatDate_yyyy_MM_dd(calendar
					.getTime()));
			calendar.add(Calendar.MONTH, 2);
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			endtime.setText(DateUtils.getFormatDate_yyyy_MM_dd(calendar
					.getTime()));
			break;
		// 上季度
		case R.id.last_quarter:
			endtime.setText(DateUtils.getFormatDate_yyyy_MM_dd(calendar
					.getTime()));
			break;
		// 上半年
		case R.id.first_half_year:
			if (calendar.get(Calendar.MONTH) > Calendar.JUNE) {
				calendar.set(Calendar.MONTH, Calendar.JUNE);
				calendar.set(Calendar.DAY_OF_MONTH,
						calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			}
			endtime.setText(DateUtils.getFormatDate_yyyy_MM_dd(calendar
					.getTime()));
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.JANUARY);
			startime.setText(DateUtils.getFormatDate_yyyy_MM_dd(calendar
					.getTime()));
			break;
		// 本年
		case R.id.one_year:
			endtime.setText(DateUtils.getFormatDate_yyyy_MM_dd(calendar
					.getTime()));
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.JANUARY);
			startime.setText(DateUtils.getFormatDate_yyyy_MM_dd(calendar
					.getTime()));
			break;
		case R.id.sumbit_btn:
			String start = startime.getText().toString();
			String end = endtime.getText().toString();
			Intent data = new Intent();
			Bundle b = new Bundle();
			b.putString("startime", start);
			b.putString("endtime", end);
			data.putExtras(b);
			setResult(DATATIME, data);
			finish();
			break;
		default:
			break;
		}
	}

	private String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}
}
