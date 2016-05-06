package com.cn7782.management.android.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.SlidingDrawer.OnDrawerScrollListener;
import android.widget.TextView;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.activity.adapter.CustomViewPagerAdapter;
import com.cn7782.management.android.activity.adapter.ScheduleAdapter;
import com.cn7782.management.android.activity.bean.CalendarViewBuilder;
import com.cn7782.management.android.activity.bean.CustomDate;
import com.cn7782.management.android.activity.bean.ScheduleBean;
import com.cn7782.management.android.activity.controller.CalendarViewPagerLisenter;
import com.cn7782.management.android.activity.tabview.CalendarView;
import com.cn7782.management.android.activity.tabview.CalendarView.CallBack;
import com.cn7782.management.android.activity.tabview.CircleTextView;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.http.HttpClient;
import com.cn7782.management.http.MyAsyncHttpResponseHandler;
import com.cn7782.management.util.DateUtil;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@SuppressWarnings("deprecation")
public class ScheduleManagerActivity extends BaseActivity implements OnClickListener, CallBack {

    private ViewPager viewPager;
    private CalendarView[] views;
    private TextView showYearView;
    private TextView showMonthView;
    private TextView showWeekView;
    private TextView monthCalendarView;
    private TextView weekCalendarView;
    private CalendarViewBuilder builder = new CalendarViewBuilder();
    private SlidingDrawer mSlidingDrawer;
    private View mContentPager;
    private CustomDate mClickDate;
    private CircleTextView mNowCircleView;
    private CircleTextView mAddCircleView;
    public static final String MAIN_ACTIVITY_CLICK_DATE = "main_click_date";

    private ListView contentlist;
    private TextView handlerTitle;
    private LinearLayout handlerText;
    private List<ScheduleBean> list = null;
    private List<ScheduleBean> listL = null;
    private Set<String> dateList = null;
    private ScheduleAdapter scheduleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendarmanager);
        findViewbyId();

        //保存本地库修改为从服务器请求获取数据，初始化时获取当日日程信息
        long time = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd ");
        Date date = new Date(time);
        String startTime = format.format(date) + " 00:00:00";
        String endTime = format.format(date) + " 23:59:59";
        getScheduleData(startTime, endTime);
    }

    private void getScheduleData(String startTime, String endTime) {

        // TODO 获取当天日程
        String tokenId = SharedPreferenceUtil.getValue(
                PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
                ScheduleManagerActivity.this);
        RequestParams param = new RequestParams();
        param.put("token_id", tokenId);
        param.put("findStartTime", startTime);
        param.put("findEndTime", endTime);
        HttpClient.post(ScheduleManagerActivity.this, ActionUrl.SCHEDULE, param,
                new MyAsyncHttpResponseHandler(ScheduleManagerActivity.this,
                        "请稍后...") {
                    public void onSuccess(String results) {
                        super.onSuccess(results);
                        try {
                            JSONObject jsonObject = new JSONObject(results);
                            if (jsonObject.has("msg")) {
                                String msg = jsonObject.isNull("msg") ? ""
                                        : jsonObject.getString("msg");
                                if (msg.equals("查询成功")) {
                                    JSONArray jo1 = jsonObject
                                            .getJSONArray("data");
                                    if (list == null)
                                        list = new ArrayList<ScheduleBean>();
                                    list.clear();
                                    for (int i = 0; i < jo1.length(); i++) {
                                        JSONObject json = jo1.getJSONObject(i);
                                        ScheduleBean sch = new ScheduleBean();
                                        sch.setScheduleId(json.isNull("id") ? ""
                                                : json.getString("id"));
                                        sch.setTitle(json.isNull("title") ? ""
                                                : json.getString("title"));
                                        sch.setBegtime(json.isNull("start") ? ""
                                                : json.getString("start"));
                                        sch.setEndtime(json.isNull("end") ? ""
                                                : json.getString("end"));

                                        list.add(sch);
                                    }
                                }
                            }
                            initCurrentDay();
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

    private void initCurrentDay() {
        if(list == null)
            return ;
        if (list.size()>0) {
            handlerTitle.setText("今日有日程安排");
        }else{
            handlerTitle.setText("今日无日程安排");
        }
        scheduleAdapter = new ScheduleAdapter(this, list);
        scheduleAdapter.notifyDataSetChanged();
        contentlist.setAdapter(scheduleAdapter);
        contentlist.setOnItemClickListener(new MyOnItemClickListener(list));

    }

    private void findViewbyId() {
        viewPager = (ViewPager) this.findViewById(R.id.viewpager);
        showMonthView = (TextView) this.findViewById(R.id.show_month_view);
        showYearView = (TextView) this.findViewById(R.id.show_year_view);
        showWeekView = (TextView) this.findViewById(R.id.show_week_view);

        monthCalendarView = (TextView) this.findViewById(R.id.month_calendar_button);
        weekCalendarView = (TextView) this.findViewById(R.id.week_calendar_button);
        mContentPager = this.findViewById(R.id.contentPager);
        mSlidingDrawer = (SlidingDrawer) this.findViewById(R.id.sildingDrawer);
        mNowCircleView = (CircleTextView) this.findViewById(R.id.now_circle_view);
        mAddCircleView = (CircleTextView) this.findViewById(R.id.add_circle_view);
        monthCalendarView.setOnClickListener(this);
        weekCalendarView.setOnClickListener(this);
        mNowCircleView.setOnClickListener(this);
        mAddCircleView.setOnClickListener(this);

        //日程列表
        contentlist = (ListView) findViewById(R.id.contentlist);
        handlerTitle = (TextView) findViewById(R.id.handlerTitle);
        handlerText = (LinearLayout)findViewById(R.id.handlerText);

        views = builder.createMassCalendarViews(ScheduleManagerActivity.this, 3, ScheduleManagerActivity.this);
        setViewPager();
        setOnDrawListener();
    }

    private void setViewPager() {
        CustomViewPagerAdapter<CalendarView> viewPagerAdapter = new CustomViewPagerAdapter<CalendarView>(views);
        viewPager.setAdapter(viewPagerAdapter);
        //当前索引，从0开始，最开始设置为0则无法右侧滑动
        viewPager.setCurrentItem(373);
        viewPager.setOnPageChangeListener(new CalendarViewPagerLisenter(viewPagerAdapter));
    }

    private void setOnDrawListener() {
        mSlidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

            @Override
            public void onDrawerOpened() {
                builder.swtichCalendarViewsStyle(CalendarView.WEEK_STYLE);

            }
        });
        mSlidingDrawer.setOnDrawerScrollListener(new OnDrawerScrollListener() {

            @Override
            public void onScrollStarted() {
                //开始滑动时 触发
                builder.swtichCalendarViewsStyle(CalendarView.MONTH_STYLE);
            }

            @Override
            public void onScrollEnded() {
            }
        });
        mSlidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

            @Override
            public void onDrawerClosed() {
                //开始滑动时 触发
                builder.swtichCalendarViewsStyle(CalendarView.MONTH_STYLE);
            }
        });
    }

    public void setShowDateViewText(int year, int month, int week) {
        showYearView.setText(year + "");
        showMonthView.setText(month + "月");
        if (week < 0) {
            week = 0;
        }
        showWeekView.setText(DateUtil.weekName[week - 1]);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.month_calendar_button:
                swtichBackgroundForButton(true);
                builder.swtichCalendarViewsStyle(CalendarView.MONTH_STYLE);
                mSlidingDrawer.close();
                break;
            case R.id.week_calendar_button:
                swtichBackgroundForButton(false);
                mSlidingDrawer.open();
                break;
            case R.id.now_circle_view:
                builder.backTodayCalendarViews();
                break;
            case R.id.add_circle_view:
                Intent i = new Intent(this, AddScheduleActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(MAIN_ACTIVITY_CLICK_DATE, mClickDate);
                i.putExtras(mBundle);
                //提交成功后刷新列表
                startActivityForResult(i, 10);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private void swtichBackgroundForButton(boolean isMonth) {
        if (isMonth) {
            monthCalendarView.setBackgroundResource(R.drawable.press_left_text_bg);
            weekCalendarView.setBackgroundColor(Color.TRANSPARENT);
        } else {
            weekCalendarView.setBackgroundResource(R.drawable.press_right_text_bg);
            monthCalendarView.setBackgroundColor(Color.TRANSPARENT);
        }
    }


    @Override
    public void onMesureCellHeight(int cellSpace) {
        mSlidingDrawer.getLayoutParams().height = mContentPager.getHeight() - cellSpace;
    }

    @Override
    public void clickDate(CustomDate date) {
        mClickDate = date;
        String startTime = date.toString() + " 00:00:00";
        String endTime = date.toString() + " 23:59:59";
        getScheduleData(startTime, endTime);
    }

    @Override
    public void changeDate(CustomDate date) {
        setShowDateViewText(date.year, date.month, date.week);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO 刷新列表
        String startTime = mClickDate.toString() + " 00:00:00";
        String endTime = mClickDate.toString() + " 23:59:59";
        getScheduleData(startTime, endTime);

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class MyOnItemClickListener implements OnItemClickListener {

        private List<ScheduleBean> sb ;
        public MyOnItemClickListener(List<ScheduleBean> sb){
            this.sb = sb;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // TODO item点击事件
            ScheduleBean schedule = sb.get(position);
            //进入活动详情
            Intent intent = new Intent(getApplicationContext(), ScheduleDetailActivity.class);
            //仅传递id即可查询
            intent.putExtra("id", schedule.getScheduleId());
            startActivityForResult(intent, 11);
        }

    }
}  



	


