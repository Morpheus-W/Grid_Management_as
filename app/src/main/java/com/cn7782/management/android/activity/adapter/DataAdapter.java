package com.cn7782.management.android.activity.adapter;

import java.util.List;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.adapter.SpinnerItem.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DataAdapter extends BaseAdapter {
	private List<String> name;
	private List<String> number;
	private Context mcontext;

	public DataAdapter(Context context, List<String> mlist, List<String> nlist) {
		this.mcontext = context;
		this.name = mlist;
		this.number = nlist;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return name.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return name.get(position);
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
			convertView = inflater.inflate(R.layout.data_layout, null);
			holder.initview(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position % 2 == 0) {
			holder.layout.setBackgroundColor(mcontext.getResources().getColor(
					R.color.listview_color_up));
		} else {
			holder.layout.setBackgroundColor(mcontext.getResources().getColor(
					R.color.listview_color_down));
		}
		String[] m = name.get(position).split(",");
		String[] n = number.get(position).split(",");
		String a, b, c, d = "";
		try {
			a = m[0];
			b = m[1];
			c = n[0];
			d = n[1];
		} catch (Exception e) {
			a = "";
			b = "";
			c = "";
			d = "";
			e.printStackTrace();
		}
		holder.head_title.setText(a);
		holder.number.setText(b);
		holder.head_title2.setText(c);
		holder.number2.setText(d);

		return convertView;
	}

	class ViewHolder {
		View layout;
		TextView head_title, head_title2, number, number2;

		public void initview(View v) {
			layout = (View) v.findViewById(R.id.data_layout);
			head_title = (TextView) v.findViewById(R.id.head_title);
			head_title2 = (TextView) v.findViewById(R.id.head_title2);
			number = (TextView) v.findViewById(R.id.number);
			number2 = (TextView) v.findViewById(R.id.number2);
		}
	}
}
