package com.cn7782.management.android.activity.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.bean.ChartingBean;
import com.cn7782.management.android.activity.bean.StatisticsBean;
import com.cn7782.management.android.activity.charting.ColorTemplate;
import com.cn7782.management.android.activity.charting.Entry;
import com.cn7782.management.android.activity.charting.PieChart;
import com.cn7782.management.android.activity.charting.PieData;
import com.cn7782.management.android.activity.charting.PieDataSet;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ChartingAdapter extends BaseAdapter {

	private Context mcontext;

	private List<StatisticsBean> mlist;

	private int zon = 0;

	private TreeMap<String, String> mapParams;

	private List<ChartingBean> Chartlist = new ArrayList<ChartingBean>();

	private List<TextView> alist;

	private int colors[] = { R.color.vordiplom_1, R.color.vordiplom_2,
			R.color.vordiplom_3, R.color.vordiplom_4, R.color.vordiplom_5,
			R.color.vordiplom_6 };

	private List<Integer> co = new ArrayList<Integer>();

	public ChartingAdapter(Context context, List<StatisticsBean> list) {

		this.mcontext = context;
		this.mlist = list;
		mapParams = new TreeMap<String, String>();
		alist = new ArrayList<TextView>();
		co.add(R.color.vordiplom_1);
		co.add(R.color.vordiplom_2);
		co.add(R.color.vordiplom_3);
		co.add(R.color.vordiplom_4);
		co.add(R.color.vordiplom_5);
		co.add(R.color.vordiplom_6);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mlist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		ArrayList<Integer> list = getintlist(mlist.get(position),
				mlist.get(position).getType());

		if (convertView == null) {
			holder = new ViewHolder();
			final LayoutInflater inflater = LayoutInflater.from(mcontext);
			convertView = inflater.inflate(R.layout.charting_listview, null);
			holder.mchart = (PieChart) convertView.findViewById(R.id.chart);
			holder.head_title = (TextView) convertView
					.findViewById(R.id.chart_title);
			holder.listview = (LinearLayout) convertView
					.findViewById(R.id.charting_list);

			initchar(holder.mchart);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (mlist.get(position).getType().equals("reportType")) {
			holder.head_title.setText("分类");
		} else if (mlist.get(position).getType().equals("progType")) {
			holder.head_title.setText("进度");
		} else if (mlist.get(position).getType().equals("gridType")) {
			holder.head_title.setText("社区");
		} else if (mlist.get(position).getType().equals("countWay")) {
			holder.head_title.setText("次数");
		} else if (mlist.get(position).getType().equals("roadLongWay")) {
			holder.head_title.setText("路长");
		} else {//timeWay
			holder.head_title.setText("耗时");
		}

		getdataChart(holder.mchart, list, "" + zon);
		holder.listview.removeAllViews();
		for (int i = 0; i < Chartlist.size(); i++) {
			final LayoutInflater minflater = LayoutInflater.from(mcontext);
			View rb = (View) minflater.inflate(R.layout.chartinglist_adapter,
					null);
			TextView name, count, percentage, color;
			color = (TextView) rb.findViewById(R.id.chart_color);
			name = (TextView) rb.findViewById(R.id.chart_grid);
			count = (TextView) rb.findViewById(R.id.chart_count);
			percentage = (TextView) rb.findViewById(R.id.percentage);

			color.setBackgroundResource(co.get(i));
			name.setText(Chartlist.get(i).getName());
			count.setText(Chartlist.get(i).getCount());

			percentage.setText(Chartlist.get(i).getPercentage());
			holder.listview.addView(rb);
		}
		return convertView;
	}

	class ViewHolder {
		PieChart mchart;
		LinearLayout listview;
		TextView head_title;
	}

	// 初始化饼行图
	public void initchar(PieChart mChart) {

		Typeface tf = Typeface.createFromAsset(mcontext.getAssets(),
				"OpenSans-Regular.ttf");
		mChart.setValueTypeface(tf);
		mChart.setCenterTextTypeface(Typeface.createFromAsset(
				mcontext.getAssets(), "OpenSans-Light.ttf"));
		mChart.setCenterTextSize(20);
		// 设置中心圆半径
		mChart.setHoleRadius(60f);

		mChart.setDescription("");
		// 设置是否在圆边画数据
		mChart.setDrawYValues(false);
		// 设置是否在圆中心加文字
		mChart.setDrawCenterText(true);
		// 设置是否集抽孔使
		mChart.setDrawHoleEnabled(true);
		// 设置初始角度
		mChart.setRotationAngle(0);

		mChart.setDrawXValues(true);
		// 是否可以转动
		mChart.setRotationEnabled(true);

		mChart.setUsePercentValues(true);

		mChart.animateXY(1500, 1500);
		// mChart.spin(2000, 0, 360);

		// Legend l = mChart.getLegend();
		// l.setPosition(LegendPosition.RIGHT_OF_CHART);
		// l.setXEntrySpace(7f);
		// l.setYEntrySpace(5f);
	}

	// 饼形图填数据
	private void getdataChart(PieChart mChart, ArrayList<Integer> mlist,
			String center) {
		int count = mlist.size();
		ArrayList<Entry> yVals1 = new ArrayList<Entry>();
		float mune[] = new float[count];
		for (int i = 0; i < count; i++) {
			mune[i] = (float) mlist.get(i);
		}
		for (int i = 0; i < mune.length; i++) {

			yVals1.add(new Entry(mune[i], i));
		}
		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 0; i < count + 1; i++) {
			xVals.add("");
		}
		PieDataSet set1 = new PieDataSet(yVals1, "");
		// 饼内数据间隔
		set1.setSliceSpace(3f);
		// 饼行图外背景色
		set1.setColors(ColorTemplate.createColors(
				mcontext.getApplicationContext(),
				ColorTemplate.VORDIPLOM_COLORS));
		PieData data = new PieData(xVals, set1);
		mChart.setData(data);
		mChart.highlightValues(null);
		mChart.setCenterText(center);
		mChart.invalidate();
	}

	private ArrayList<Integer> getintlist(StatisticsBean nlist, String str) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		Chartlist.clear();
		try {
			if (str.equals("reportType")) {
				ChartingBean bean = new ChartingBean();
				ChartingBean bean2 = new ChartingBean();
				ChartingBean bean3 = new ChartingBean();
				int a = Integer.valueOf(nlist.getProtectRoadLine());
				int b = Integer.valueOf(nlist.getContradictionEvent());
				int c = Integer.valueOf(nlist.getSocialSecurity());
				list.add(a);
				list.add(b);
				list.add(c);
				zon = a + b + c;
				bean.setName("护路护线");
				bean.setCount("" + a);
				bean.setPercentage(getpercentage("" + a, "" + zon));
				Chartlist.add(bean);
				bean2.setName("矛盾纠纷");
				bean2.setCount("" + b);
				bean2.setPercentage(getpercentage("" + b, "" + zon));
				Chartlist.add(bean2);
				bean3.setName("社会治安");
				bean3.setCount("" + c);
				bean3.setPercentage(getpercentage("" + c, "" + zon));
				Chartlist.add(bean3);
				zon = a + b + c;
			} else if (str.equals("progType")) {
				ChartingBean bean = new ChartingBean();
				ChartingBean bean2 = new ChartingBean();
				ChartingBean bean3 = new ChartingBean();
				ChartingBean bean4 = new ChartingBean();
				ChartingBean bean5 = new ChartingBean();
				ChartingBean bean6 = new ChartingBean();
				int a = Integer.valueOf(nlist.getChuzhi());
				int b = Integer.valueOf(nlist.getZongCount());
				int c = Integer.valueOf(nlist.getXiaoan());
				int d = Integer.valueOf(nlist.getDuban());
				int e = Integer.valueOf(nlist.getBaosong());
				int f = Integer.valueOf(nlist.getHecha());
				list.add(a);
				list.add(b);
				list.add(c);
				list.add(d);
				list.add(e);
				list.add(f);
				zon = a + b + c + d + e + f;
				bean.setName("处置");
				bean.setCount("" + a);
				bean.setPercentage(getpercentage("" + a, "" + zon));
				Chartlist.add(bean);
				bean2.setName("总数");
				bean2.setCount("" + b);
				bean2.setPercentage(getpercentage("" + b, "" + zon));
				Chartlist.add(bean2);
				bean3.setName("销案");
				bean3.setCount("" + c);
				bean3.setPercentage(getpercentage("" + c, "" + zon));
				Chartlist.add(bean3);
				bean4.setName("督办");
				bean4.setCount("" + d);
				bean4.setPercentage(getpercentage("" + d, "" + zon));
				Chartlist.add(bean4);
				bean5.setName("报送");
				bean5.setCount("" + e);
				bean5.setPercentage(getpercentage("" + e, "" + zon));
				Chartlist.add(bean5);
				bean6.setName("核查");
				bean6.setCount("" + f);
				bean6.setPercentage(getpercentage("" + f, "" + zon));
				Chartlist.add(bean6);
			} else {
				ChartingBean bean = new ChartingBean();
				ChartingBean bean2 = new ChartingBean();
				ChartingBean bean3 = new ChartingBean();
				ChartingBean bean4 = new ChartingBean();
				int a = Integer.valueOf(nlist.getGrid1());
				int b = Integer.valueOf(nlist.getGrid2());
				int c = Integer.valueOf(nlist.getGrid3());
				int d = Integer.valueOf(nlist.getGrid4());
				list.add(a);
				list.add(b);
				list.add(c);
				list.add(d);
				zon = a + b + c + d;
				bean.setName("一区");
				bean.setCount("" + a);
				bean.setPercentage(getpercentage("" + a, "" + zon));
				Chartlist.add(bean);
				bean2.setName("二区");
				bean2.setCount("" + b);
				bean2.setPercentage(getpercentage("" + b, "" + zon));
				Chartlist.add(bean2);
				bean3.setName("三区");
				bean3.setCount("" + c);
				bean3.setPercentage(getpercentage("" + c, "" + zon));
				Chartlist.add(bean3);
				bean4.setName("四区");
				bean4.setCount("" + d);
				bean4.setPercentage(getpercentage("" + d, "" + zon));
				Chartlist.add(bean4);

			}
		} catch (Exception e) {

		}
		return list;
	}

	private String getpercentage(String str, String count) {
		double dd = Double.valueOf(str);
		double ddd = Double.valueOf(count);
		if (ddd == 0) {
			return "0%";
		}
		NumberFormat formatter = new DecimalFormat("0%");
		Double x = new Double(dd / ddd);
		String xx = formatter.format(x);
		return xx;
	}

	private ArrayList<ChartingBean> Transformation() {
		ArrayList<ChartingBean> list = new ArrayList<ChartingBean>();

		for (String key : mapParams.keySet()) {
			ChartingBean bean = new ChartingBean();
			bean.setCount(mapParams.get(key));
			bean.setName(key);
			bean.setPercentage(getpercentage(mapParams.get(key), "" + zon));
			list.add(bean);
		}

		return list;
	}

	class Bean {
		public String name;
		public String count;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCount() {
			return count;
		}

		public void setCount(String count) {
			this.count = count;
		}

	}
}