package com.cn7782.management.android.activity.tabview;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.bean.CustomDate;
import com.cn7782.management.util.DateUtil;
import com.cn7782.management.util.Lunar;

import java.util.Calendar;

public class CalendarView extends View {

	private static final String TAG = "CalendarView";
	/**
	 * 两种模式 （月份和星期）
	 */
	public static final int MONTH_STYLE = 0;
	public static final int WEEK_STYLE = 1;

	private static final int TOTAL_COL = 7;
	private static final int TOTAL_ROW = 6;
	//选中背景
	private Paint mCirclePaint;
	//非当日选中背景
	private Paint oCirclePaint;
	//存在日程的背景
//	private Paint sCirclePaint;
	//一般背景
	private Paint nCirclePaint;
	//选中字体颜色
	private Paint mTextPaint;
	private Paint mTextOPaint;
	private int mViewWidth;
	private int mViewHight;
	private int mCellSpace;
	private Row rows[] = new Row[TOTAL_ROW];
	private static CustomDate mShowDate;//自定义的日期  包括year month day
	public static int style = MONTH_STYLE;
	private static final int WEEK = 7;
	private CallBack mCallBack;//回调
	private int touchSlop;
	private boolean callBackCellSpace;
	//获取全部存在日程的日期
//	private Set<String> dateList;

	public interface CallBack {

		void clickDate(CustomDate date);//回调点击的日期

		void onMesureCellHeight(int cellSpace);//回调cell的高度确定slidingDrawer高度

		void changeDate(CustomDate date);//回调滑动viewPager改变的日期
	}

	public CalendarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);

	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

	}

	public CalendarView(Context context) {
		super(context);
		init(context);
	}

	public CalendarView(Context context, int style, CallBack mCallBack) {
		super(context);
		CalendarView.style = style;
		this.mCallBack = mCallBack;
		init(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (int i = 0; i < TOTAL_ROW; i++) {
			if (rows[i] != null)
				rows[i].drawCells(canvas);
		}
	}

	private void init(Context context) {
		//阳历日期画笔
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		//农历日期画笔
		mTextOPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		//圆的背景为蓝色
		mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCirclePaint.setStyle(Paint.Style.FILL);
		mCirclePaint.setColor(getResources().getColor(R.color.mCirclePaint));
		//圆的背景为浅蓝
		oCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		oCirclePaint.setStyle(Paint.Style.FILL);
		oCirclePaint.setColor(getResources().getColor(R.color.oCirclePaint));
		//圆的背景为浅灰
		nCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		nCirclePaint.setStyle(Paint.Style.FILL);
		nCirclePaint.setColor(getResources().getColor(R.color.nCirclePaint));

		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		initDate();

	}

	private void initDate() {
		if (style == MONTH_STYLE) {
			mShowDate = new CustomDate();
			if(mClickCell != null)
				mClickCell.date = mShowDate;
		} else if(style == WEEK_STYLE ) {
			mShowDate = DateUtil.getNextSunday();
			mShowDate.week = new CustomDate().week;
			if(mClickCell != null)
				mClickCell.date = new CustomDate();
		}
		fillDate();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mViewWidth = w;
		mViewHight = h;
		mCellSpace = Math.min(mViewHight / TOTAL_ROW, mViewWidth / TOTAL_COL);
		if (!callBackCellSpace) {
			mCallBack.onMesureCellHeight(mCellSpace);
			callBackCellSpace = true;
		}
		mTextPaint.setTextSize(mCellSpace / 3);
		mTextOPaint.setTextSize(mCellSpace / 4);
	}

	private static Cell mClickCell = null;
	private float mDownX;
	private float mDownY;
/*
 * 
 * 触摸事件为了确定点击的位置日期
 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownX = event.getX();
			mDownY = event.getY();
			break;
		case MotionEvent.ACTION_UP:
			float disX = event.getX() - mDownX;
			float disY = event.getY() - mDownY;
			if (Math.abs(disX) < touchSlop && Math.abs(disY) < touchSlop) {
				int col = (int) (mDownX / mCellSpace);
				int row = (int) (mDownY / mCellSpace);
				measureClickCell(col, row);
			}
			break;
		}
		return true;
	}

	private void measureClickCell(int col, int row) {
		//对当前日选中特殊处理
		CustomDate date = rows[row].cells[col].date;
		//当月当日
		int monthDay = DateUtil.getCurrentMonthDay();
		//选中日
		int sameDay = date.day;
		boolean isCurrentMonth = false;
		//判断是否为当前月
		if (DateUtil.isCurrentMonth(date)) {
			isCurrentMonth = true;
		}
		
		if (col >= TOTAL_COL || row >= TOTAL_ROW)
			return;
		if (rows[row] != null && mClickCell != null) {
			//判断点击非当月日期，直接滑动
			if(rows[row].cells[col].state == State.NEXT_MONTH_DAY){
				mClickCell.date = date;
				rightSilde();
				return ;
			}else if(rows[row].cells[col].state == State.PAST_MONTH_DAY){
				mClickCell.date = date;
				leftSilde();
				return ;
			}
			
			//非当月,也非选中日;非当月,选中日无变化;
			if(!isCurrentMonth && mClickCell.date.day != rows[row].cells[col].date.day){
				
				if(style == WEEK_STYLE)
					rows[0].cells[mClickCell.i].state = State.NORMAL;
				else{
					rows[mClickCell.j].cells[mClickCell.i].state = State.NORMAL;
				}
				rows[row].cells[col].state = State.SAMEDAY;
			}
			//当月,非当日,对周模式而言,当月当日与当月非当日在同一行或不同行	
			else if(isCurrentMonth && monthDay != sameDay ){
				if(style == WEEK_STYLE && 
						rows[0].cells[mClickCell.i].state == State.CLICK_DAY
						){
					rows[0].cells[mClickCell.i].state = State.TODAY;
				}else if(style == WEEK_STYLE){
					rows[0].cells[mClickCell.i].state = State.NORMAL;
				}else if(style == MONTH_STYLE &&
						rows[mClickCell.j].cells[mClickCell.i].state == State.CLICK_DAY){
					rows[mClickCell.j].cells[mClickCell.i].state = State.TODAY;
				}else{
					rows[mClickCell.j].cells[mClickCell.i].state = State.NORMAL;
				}
				rows[row].cells[col].state = State.SAMEDAY;
			}
			//当月,当日
			else if(isCurrentMonth && monthDay == sameDay){
				if(style == WEEK_STYLE)
					rows[0].cells[mClickCell.i].state = State.NORMAL;
				else
					rows[mClickCell.j].cells[mClickCell.i].state = State.NORMAL;
				rows[row].cells[col].state = State.CLICK_DAY;
				
			}
			//选中后重新赋值
			mClickCell = new Cell(rows[row].cells[col].date,
					rows[row].cells[col].state, rows[row].cells[col].i,
					rows[row].cells[col].j);
			
			date.week = DateUtil.getWeekFromDate(date.year,date.month,date.day);
			mCallBack.clickDate(date);
			mCallBack.changeDate(date);
			invalidate();
		}
	}

	// 组
	class Row {
		public int j;

		Row(int j) {
			this.j = j;
		}

		public Cell[] cells = new Cell[TOTAL_COL];

		public void drawCells(Canvas canvas) {
			for (int i = 0; i < cells.length; i++) {
				if (cells[i] != null)
					cells[i].drawSelf(canvas);
			}

		}
	}

	// 单元格
	class Cell {
		public CustomDate date;
		public State state;
		public int i;
		public int j;

		public Cell(CustomDate date, State state, int i, int j) {
			super();
			this.date = date;
			this.state = state;
			this.i = i;
			this.j = j;
		}


		// 绘制一个单元格 如果颜色需要自定义可以修改
		public void drawSelf(Canvas canvas) {
			switch (state) {
			case CURRENT_MONTH_DAY:
				mTextPaint.setColor(Color.parseColor("#80000000"));
				mTextOPaint.setColor(Color.parseColor("#80000000"));
				break;
			case NEXT_MONTH_DAY:
			case PAST_MONTH_DAY:
				mTextPaint.setColor(Color.parseColor("#40000000"));
				mTextOPaint.setColor(Color.parseColor("#40000000"));
				break;
			case SAMEDAY://选中的同一天，字体是白色，背景是浅蓝色

				mTextPaint.setColor(Color.parseColor("#80000000"));
				mTextOPaint.setColor(Color.parseColor("#80000000"));

				canvas.drawCircle((float) (mCellSpace * (i + 0.5)),
						(float) ((j + 0.5) * mCellSpace), mCellSpace / 2,
						oCirclePaint);
				break;
			case TODAY:
				mTextPaint.setColor(Color.parseColor("#26b6f9"));// F24949
				mTextOPaint.setColor(Color.parseColor("#80000000"));
				break;
			case CLICK_DAY:
				//字体是白色
				mTextPaint.setColor(Color.parseColor("#fffffe"));
				mTextOPaint.setColor(Color.parseColor("#80000000"));
				//背景是蓝色
				canvas.drawCircle((float) (mCellSpace * (i + 0.5)),
						(float) ((j + 0.5) * mCellSpace), mCellSpace / 2,
						mCirclePaint);
				break;
			case NORMAL:
				//字体是黑色
				mTextPaint.setColor(Color.parseColor("#80000000"));
				mTextOPaint.setColor(Color.parseColor("#80000000"));
				//背景是灰色
				canvas.drawCircle((float) (mCellSpace * (i + 0.5)),
						(float) ((j + 0.5) * mCellSpace), mCellSpace / 2,
						nCirclePaint);
				break;
			}
			// 绘制文字
			String content = date.day+"";
			canvas.drawText(content,
					(float) ((i+0.5) * mCellSpace - mTextPaint.measureText(content)/2),
					(float) ((j + 0.6) * mCellSpace - mTextPaint.measureText(
							content, 0, 1) / 2), mTextPaint);
			//绘制农历
			Calendar clr = Calendar.getInstance();
			int month = 1 ;
			if(date.month > 12){
				month = 1;
			}else if(date.month <1){
				month = 12;
			}else{
				month = date.month -1;
			}
			clr.set(date.year,month,date.day);
			Lunar lunar = new Lunar(clr.getTimeInMillis());
			String lunarString = lunar.getLunarDayString();
			if(lunarString.equals("初一")){
				lunarString = lunar.getLunarMonthString()+"月";
			}
			canvas.drawText(lunarString,
					(float) ((i + 0.5) * mCellSpace - mTextOPaint.measureText(lunarString) / 2),
					(float) ((j + 1.0) * mCellSpace - mTextOPaint.measureText(
							lunarString, 0, 1) / 2), mTextOPaint);
		}
	}
/**
 * 
 * @author huang
 * cell的state
 *当前月日期，过去的月的日期，下个月的日期，今天，点击的日期
 *
 */
	enum State {
		CURRENT_MONTH_DAY, PAST_MONTH_DAY, NEXT_MONTH_DAY, SAMEDAY,TODAY,NORMAL, CLICK_DAY,SCHEDULE_DAY;
	}

	/**
	 * 填充日期的数据
	 */
	private void fillDate() {
		if (style == MONTH_STYLE) {
			fillMonthDate();
		} else if(style == WEEK_STYLE) {
			fillWeekDate();
		}
		mCallBack.changeDate(mShowDate);
	}

	/**
	 * 填充星期模式下的数据 默认通过当前日期得到所在星期天的日期，然后依次填充日期
	 */
	private void fillWeekDate() {
		int lastMonthDays = DateUtil.getMonthDays(mShowDate.year, mShowDate.month-1);
		int currentMonthDays = DateUtil.getMonthDays(mShowDate.year, mShowDate.month);
		rows[0] = new Row(0);
		int day = mShowDate.day;
		for (int i = TOTAL_COL -1; i >= 0 ; i--) {
			day -= 1;
			CustomDate date = CustomDate.modifiDayForObject(mShowDate, day);
			//当前月日与上月日在同行
			if (day < 1) {
				day = lastMonthDays;
				date = new CustomDate(mShowDate.year ,mShowDate.month -1,day);
			}
			//当前月日与下月日在同行
			if(date.day > currentMonthDays && date.month == mShowDate.month){
				date.day = date.day - currentMonthDays;
				date.month = mShowDate.month+1;
				rows[0].cells[i] = new Cell(date, State.CURRENT_MONTH_DAY,i, 0);
				continue;
			}
			if (DateUtil.isToday(date) && mClickCell.date.day == date.day) {//当月当日被选
//				mClickCell = new Cell(date, State.TODAY, i, 0);
				date.week = i;
				mCallBack.clickDate(date);
				rows[0].cells[i] =  new Cell(date, State.CLICK_DAY, i, 0);
				continue;
			}else if (DateUtil.isToday(date)) {//当月当日未被选
				date.week = i;
				mCallBack.clickDate(date);
				rows[0].cells[i] =  new Cell(date, State.TODAY, i, 0);
				continue;
			}else if(mClickCell.date.day == date.day){//仅被选
				date.week = i;
				mCallBack.clickDate(date);
				rows[0].cells[i] =  new Cell(date, State.SAMEDAY, i, 0);
				continue;
				
			}
			rows[0].cells[i] = new Cell(date, State.CURRENT_MONTH_DAY,i, 0);
		}
	}

	/**
	 * 填充月份模式下数据 通过getWeekDayFromDate得到一个月第一天是星期几就可以算出所有的日期的位置 然后依次填充
	 * 这里最好重构一下
	 */
	private void fillMonthDate() {
		//当前日
		int monthDay = DateUtil.getCurrentMonthDay();
		//获取展示月上个月的天数
		int lastMonthDays = DateUtil.getMonthDays(mShowDate.year, mShowDate.month - 1);
		//获取展示月的天数
		int currentMonthDays = DateUtil.getMonthDays(mShowDate.year, mShowDate.month);
		//展示月第一天属于第几周的下标
		int firstDayWeek = DateUtil.getWeekDayFromDate(mShowDate.year, mShowDate.month);
		boolean isCurrentMonth = false;
		//判断展示月是否为当前月
		if (DateUtil.isCurrentMonth(mShowDate)) {
			isCurrentMonth = true;
		}
		int day = 0;
		for (int j = 0; j < TOTAL_ROW; j++) {
			rows[j] = new Row(j);
			for (int i = 0; i < TOTAL_COL; i++) {
				int postion = i + j * TOTAL_COL;
				if (postion >= firstDayWeek
						&& postion < firstDayWeek + currentMonthDays) {
					day++;
					if (isCurrentMonth && day == monthDay) {//当前月，当前日
						CustomDate date = CustomDate.modifiDayForObject(mShowDate, day);
						//初始化选中单元格
						if(mClickCell == null || (mClickCell != null &&mClickCell.date.day == day)){//当前日被选中
							mClickCell = new Cell(date,State.TODAY, i,j);
							date.week = i;
							mCallBack.clickDate(date);
							rows[j].cells[i] = new Cell(date,State.CLICK_DAY, i,j);
						}else{//当前日未被选中
							rows[j].cells[i] = new Cell(date,State.TODAY, i,j);
						}

						//默认选中当月当日
						continue;
					}else if(isCurrentMonth && mClickCell != null &&mClickCell.date.day == day){//当前月，选中日
						CustomDate date = CustomDate.modifiDayForObject(mShowDate, day);
						mClickCell = new Cell(date,State.SAMEDAY, i,j);
						mShowDate.week = DateUtil.getWeekFromDate(date.year ,date.month,day);
						//默认选中当月当日
						mCallBack.clickDate(date);
						rows[j].cells[i] = new Cell(date,State.SAMEDAY, i,j);
						continue;
					}else if(!isCurrentMonth && day == mClickCell.date.day){//非当前月，选中日
						CustomDate date = CustomDate.modifiDayForObject(mShowDate, day);
						mClickCell = new Cell(date,State.SAMEDAY, i,j);
						mShowDate.week = i+1;
						//默认选中当月当日
						mCallBack.clickDate(date);
						rows[j].cells[i] = new Cell(date,State.SAMEDAY, i,j);
						continue;
					}else if(!isCurrentMonth && day == currentMonthDays && currentMonthDays < mClickCell.date.day){//非当前月，展示月天数少于滑动前月天数
						CustomDate date = CustomDate.modifiDayForObject(mShowDate, day);
						mClickCell = new Cell(date,State.SAMEDAY, i,j);
						mShowDate.week = i+1;
						//默认选中当月当日
						mCallBack.clickDate(date);
						rows[j].cells[i] = new Cell(date,State.SAMEDAY, i,j);
						continue;
						
					}
					rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(mShowDate, day),
							State.CURRENT_MONTH_DAY, i, j);
				} else if (postion < firstDayWeek) {
					rows[j].cells[i] = new Cell(new CustomDate(mShowDate.year, mShowDate.month-1, lastMonthDays - (firstDayWeek- postion - 1)), State.PAST_MONTH_DAY, i, j);
				} else if (postion >= firstDayWeek + currentMonthDays) {
					rows[j].cells[i] = new Cell((new CustomDate(mShowDate.year, mShowDate.month+1, postion - firstDayWeek - currentMonthDays + 1)), State.NEXT_MONTH_DAY, i, j);
				}
			}
		}
	}

	public void update() {
		fillDate();
		//请求重新绘制view，即调用onDraw方法
		invalidate();
	}
	
	public void backToday(){
		initDate();
		invalidate();
	}
	//切换style
	public void switchStyle(int style) {
		CalendarView.style = style;
		if (style == MONTH_STYLE) {
			update();
		} else if (style == WEEK_STYLE) {
			//选中的日期是星期几
			CustomDate date = mClickCell.date;
			//第一天星期几
			int day = 0;
			int firstDayWeek = 0;
			if(date.day<8 && mClickCell.j==0){
				firstDayWeek = DateUtil.getWeekDayFromDate(mShowDate.year,mShowDate.month);
				day =  1 + WEEK - firstDayWeek;
			}else {
				firstDayWeek = DateUtil.getWeekFromDate(date.year,date.month,date.day);
				day =  date.day + 1 + WEEK - firstDayWeek;
				
			}
			mShowDate.day = day;
			
			update();
		}
		
	}
//向右滑动
	public void rightSilde() {
		if (style == MONTH_STYLE) {
			
			if (mShowDate.month == 12) {
				mShowDate.month = 1;
				mShowDate.year += 1;
			} else {
				mShowDate.month += 1;
			}
			
		} else if (style == WEEK_STYLE) {
			int currentMonthDays = DateUtil.getMonthDays(mShowDate.year, mShowDate.month);
			if (mShowDate.day + WEEK > currentMonthDays) {
				if (mShowDate.month == 12) {
					mShowDate.month = 1;
					mShowDate.year += 1;
				} else {
					mShowDate.month += 1;
				}
				mShowDate.day = WEEK - currentMonthDays + mShowDate.day;	
			}else{
				mShowDate.day += WEEK;
			
			}
		}
		update();
	}
//向左滑动
	public void leftSilde() {
		
		if (style == MONTH_STYLE) {
			if (mShowDate.month == 1) {
				mShowDate.month = 12;
				mShowDate.year -= 1;
			} else {
				mShowDate.month -= 1;
			}
			
		} else if (style == WEEK_STYLE) {
			int lastMonthDays = DateUtil.getMonthDays(mShowDate.year, mShowDate.month);
			if (mShowDate.day - WEEK < 1) {
				if (mShowDate.month == 1) {
					mShowDate.month = 12;
					mShowDate.year -= 1;
				} else {
					mShowDate.month -= 1;
				}
				mShowDate.day = lastMonthDays - WEEK + mShowDate.day;
				
			}else{
				mShowDate.day -= WEEK;
			}
//			Log.i(TAG, "leftSilde"+mShowDate.toString());
		}
		update();
	}
}
