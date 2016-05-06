package com.cn7782.management.android.activity.adapter;

import java.util.List;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.adapter.ChartinglistAdapter.ViewHolder;
import com.cn7782.management.android.activity.bean.ChartingBean;
import com.cn7782.management.android.activity.bean.HistoryBean;
import com.cn7782.management.android.activity.bean.InfoBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TrajectoryAdapter extends BaseAdapter {

	private Context mcontext;
	private List<HistoryBean> mlist;

	public TrajectoryAdapter(Context context, List<HistoryBean> list) {
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
			convertView = inflater.inflate(R.layout.trajectory_history_list,
					null);
			holder.name = (TextView) convertView
					.findViewById(R.id.history_name);
			holder.time = (TextView) convertView
					.findViewById(R.id.history_time);
			holder.duration = (TextView) convertView
					.findViewById(R.id.history_duration);
			holder.distance = (TextView) convertView
					.findViewById(R.id.history_distance);
			holder.remark = (TextView) convertView
					.findViewById(R.id.history_remark);
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
		holder.name.setText(mlist.get(position).getName());
		holder.time.setText(mlist.get(position).getTime());
		
		int timeconsuming = mlist.get(position).getDuration();
		String time = (timeconsuming<60?timeconsuming+"秒":
			(timeconsuming%60==0?timeconsuming/60+"分钟":timeconsuming/60+"分钟"+"|"+timeconsuming%60+"秒"));
		holder.duration.setText("耗时:"+time.replace('|', '\n'));
		holder.distance.setText("路长:"+mlist.get(position).getDistance()+"m");
		
		holder.remark.setText(mlist.get(position).getRemark());
		return convertView;
	}

	class ViewHolder {

		TextView name, time, duration, distance, remark;
		View topline, bottomline;
	}
}
