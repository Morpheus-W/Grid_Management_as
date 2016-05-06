package com.cn7782.management.android.activity.adapter;

import java.util.List;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.bean.ScheduleBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ScheduleAdapter extends BaseAdapter {

	private Context mcontext;
	private List<ScheduleBean> mlist;

	public ScheduleAdapter(Context context, List<ScheduleBean> list) {
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

	public void setdata(List<ScheduleBean> list) {
		this.mlist = list;
	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup parent) {
		// TODO 获取单个视图
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			final LayoutInflater inflater = LayoutInflater.from(mcontext);
			convertView = inflater.inflate(R.layout.content_listview, null);
			
			holder.shortContent = (TextView) convertView
					.findViewById(R.id.shortContent);
			holder.showDuration = (TextView) convertView
					.findViewById(R.id.showDuration);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.shortContent.setText(mlist.get(position).getTitle());
		holder.showDuration.setText(mlist.get(position).getBegtime().substring(11, 16)+" - "+
				mlist.get(position).getEndtime().substring(11, 16));

		return convertView;
	}
	

	class ViewHolder {

		TextView shortContent, showDuration;
	}

}
