package com.cn7782.management.android.activity.adapter;

import java.util.List;
import com.cn7782.management.R;
import com.cn7782.management.android.activity.bean.SearchBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChooseAdapter extends BaseAdapter {
	private List<SearchBean> mlist;
	private Context mcontext;

	public ChooseAdapter(Context context, List<SearchBean> list) {
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

	public void setData(List<SearchBean> list) {
		mlist = list;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			final LayoutInflater inflater = LayoutInflater.from(mcontext);
			convertView = inflater.inflate(R.layout.choose_layout, null);
			holder.head_title = (TextView) convertView
					.findViewById(R.id.choose_title);
			holder.head_content = (TextView) convertView
					.findViewById(R.id.choose_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.head_title.setText(mlist.get(position).getTitle());
		holder.head_content.setText(mlist.get(position).getContent());

		return convertView;
	}

	class ViewHolder {

		TextView head_title, head_content;
	}
}
