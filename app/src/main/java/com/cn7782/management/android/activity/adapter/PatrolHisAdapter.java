package com.cn7782.management.android.activity.adapter;

import java.util.List;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.bean.PatrolHisBean;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PatrolHisAdapter extends BaseAdapter {
	private Context mcontext;
	private List<PatrolHisBean> mlist;

	public PatrolHisAdapter(Context context, List<PatrolHisBean> list) {
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
			convertView = inflater.inflate(R.layout.patrol_his_adapter, null);
			holder.time = (TextView) convertView.findViewById(R.id.patrol_time);
			holder.timeconsuming = (TextView) convertView
					.findViewById(R.id.patrol_timeconsuming);
			holder.distance = (TextView) convertView
					.findViewById(R.id.patrol_distance);
			holder.describe = (TextView) convertView
					.findViewById(R.id.patrol_describe);
			
			holder.topline = (View) convertView.findViewById(R.id.top_line);
			holder.bottomline = (View) convertView
					.findViewById(R.id.bottom_line);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == 0) {
			holder.topline.setVisibility(View.INVISIBLE);
		} else {
			holder.topline.setVisibility(View.VISIBLE);
		}
		if (position == mlist.size() - 1) {
			holder.bottomline.setVisibility(View.INVISIBLE);
		} else {
			holder.bottomline.setVisibility(View.VISIBLE);
		}
		holder.time.setText(mlist.get(position).getTime());
		
		int timeconsuming = mlist.get(position).getTimeconsuming();
		String time = (timeconsuming<60?timeconsuming+"秒":
			(timeconsuming%60==0?timeconsuming/60+"分钟":timeconsuming/60+"分钟"+"|"+timeconsuming%60+"秒"));
		holder.timeconsuming.setText("耗时:"+time.replace('|', '\n'));
		
		holder.distance.setText("路长:"+mlist.get(position).getDistance()+"m");
		
		holder.describe.setText(mlist.get(position).getRemark());
		return convertView;
	}

	class ViewHolder {
		TextView startPoint, endPoint, time, distance, timeconsuming,describe;
		ImageView message_image;
		View topline, bottomline;
	}
}