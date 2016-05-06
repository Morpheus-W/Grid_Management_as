package com.cn7782.management.android.activity.adapter;

import java.util.List;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.adapter.ChartingAdapter.ViewHolder;
import com.cn7782.management.android.activity.charting.PieChart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BlankAdapter extends BaseAdapter {

	private List<String> mlist;
	private Context mcontext;

	public BlankAdapter(Context context, List<String> list) {
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
			convertView = inflater.inflate(R.layout.blank_adapter, null);
			holder.head_title = (TextView) convertView
					.findViewById(R.id.blank_string);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.head_title.setText(mlist.get(position));
		return convertView;
	}

	class ViewHolder {

		TextView head_title;
	}
}
