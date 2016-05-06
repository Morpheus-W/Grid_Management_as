package com.cn7782.management.android.activity.adapter;

import java.util.ArrayList;
import java.util.List;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.bean.ChartingBean;
import com.cn7782.management.android.activity.bean.StatisticsBean;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChartinglistAdapter extends BaseAdapter {
	private Context mcontext;
	private List<ChartingBean> mlist;
	private int layout_color[] = { R.color.vordiplom_1, R.color.vordiplom_2,
			R.color.vordiplom_3, R.color.vordiplom_4, R.color.vordiplom_5 };

	public ChartinglistAdapter(Context context, List<ChartingBean> list) {
		this.mcontext = context;
		this.mlist = list;
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
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			final LayoutInflater inflater = LayoutInflater.from(mcontext);
			convertView = inflater.inflate(R.layout.chartinglist_adapter, null);
			holder.chart_grid = (TextView) convertView
					.findViewById(R.id.chart_grid);
			holder.chart_count = (TextView) convertView
					.findViewById(R.id.chart_count);
			holder.chart_color = (TextView) convertView
					.findViewById(R.id.chart_color);
			holder.percentage = (TextView) convertView
					.findViewById(R.id.percentage);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Log.e("dkajfbkljeawhkfhak", "" + 1);
		holder.chart_grid.setText(mlist.get(position).getName());
		holder.chart_count.setText(mlist.get(position).getCount());
		holder.chart_color.setBackgroundColor(layout_color[position]);
		holder.percentage.setText(mlist.get(position).getPercentage());

		return convertView;
	}

	class ViewHolder {

		TextView chart_grid, chart_count, chart_color, percentage;

	}

}
