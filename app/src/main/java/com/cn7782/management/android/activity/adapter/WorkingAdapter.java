package com.cn7782.management.android.activity.adapter;

import java.util.List;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.adapter.ChartingAdapter.ViewHolder;
import com.cn7782.management.android.activity.bean.MyReportBean;
import com.cn7782.management.android.activity.charting.PieChart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WorkingAdapter extends BaseAdapter {
	private List<MyReportBean> mlist;
	private Context mcontext;

	public WorkingAdapter(Context context, List<MyReportBean> list) {
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
			convertView = inflater.inflate(R.layout.working_adapter, null);
			holder.title = (TextView) convertView
					.findViewById(R.id.working_title);
			holder.content = (TextView) convertView
					.findViewById(R.id.working_content);
			holder.time = (TextView) convertView
					.findViewById(R.id.working_time);
			holder.position = (TextView) convertView
					.findViewById(R.id.working_position);
			holder.image = (View) convertView.findViewById(R.id.work_state);
			holder.text1 = (TextView) convertView.findViewById(R.id.text1);
			holder.text2 = (TextView) convertView.findViewById(R.id.text2);
			holder.text3 = (TextView) convertView.findViewById(R.id.text3);
			holder.text4 = (TextView) convertView.findViewById(R.id.text4);
			holder.text5 = (TextView) convertView.findViewById(R.id.text5);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.time.setText(mlist.get(position).getTime());
		holder.title.setText(mlist.get(position).getTitle());
		holder.content.setText(mlist.get(position).getContent());
		holder.position.setText(mlist.get(position).getAddress());
		holder.image.setBackgroundDrawable(mcontext.getResources().getDrawable(
				getimage(mlist.get(position).getState())));
		if (mlist.get(position).getState().equals("S01")) {
			holder.text1.setTextColor(mcontext.getResources().getColor(
					R.color.white));
		} else if (mlist.get(position).getState().equals("S02")) {
			holder.text2.setTextColor(mcontext.getResources().getColor(
					R.color.white));
		} else if (mlist.get(position).getState().equals("S03")) {
			holder.text3.setTextColor(mcontext.getResources().getColor(
					R.color.white));
		} else if (mlist.get(position).getState().equals("S04")) {
			holder.text4.setTextColor(mcontext.getResources().getColor(
					R.color.white));
		} else if (mlist.get(position).getState().equals("S05")) {
			holder.text5.setTextColor(mcontext.getResources().getColor(
					R.color.white));
		}else{
			holder.text1.setTextColor(mcontext.getResources().getColor(
					R.color.white));
			holder.text2.setTextColor(mcontext.getResources().getColor(
					R.color.white));
			holder.text3.setTextColor(mcontext.getResources().getColor(
					R.color.white));
			holder.text4.setTextColor(mcontext.getResources().getColor(
					R.color.white));
			holder.text5.setTextColor(mcontext.getResources().getColor(
					R.color.white));
			
		}
		return convertView;
	}

	class ViewHolder {

		TextView title, content, time, position;
		View image;
		TextView text1, text2, text3, text4, text5;
	}

	/**
	 * 
	 * "S01"：报送 "S02"： 处置 "S03"：督办 "S04"： 销案 "S05"： 核查
	 * 
	 * @param str
	 * @return
	 */
	private int getimage(String str) {
		if (str.equals("S01")) {
			return R.drawable.one;
		} else if (str.equals("S02")) {
			return R.drawable.two;
		} else if (str.equals("S03")) {
			return R.drawable.three;
		} else if (str.equals("S04")) {
			return R.drawable.four;
		} else if (str.equals("S05")) {
			return R.drawable.five;
		} else {
			return R.drawable.six;
		}

	}

	/**
	 * 
	 * "S01"：报送 "S02"： 处置 "S03"：督办 "S04"： 销案 "S05"： 核查
	 * 
	 * @param str
	 * @return
	 */
	private int getcolor(String str) {
		if (str.equals("S01")) {
			return R.drawable.one;
		} else if (str.equals("S02")) {
			return R.drawable.two;
		} else if (str.equals("S03")) {
			return R.drawable.three;
		} else if (str.equals("S04")) {
			return R.drawable.four;
		} else if (str.equals("S05")) {
			return R.drawable.five;
		} else {
			return R.drawable.one;
		}

	}
}
