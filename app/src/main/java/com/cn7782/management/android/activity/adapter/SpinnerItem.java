package com.cn7782.management.android.activity.adapter;

import java.util.List;
import com.cn7782.management.R;
import com.cn7782.management.android.activity.bean.MyReportBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SpinnerItem extends BaseAdapter {
	private List<MyReportBean> mlist;
	private Context mContext;

	public SpinnerItem(Context context, List<MyReportBean> list) {
		this.mlist = list;
		this.mContext = context;

	}

	public void setData(List<MyReportBean> list) {
		mlist = list;
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
			final LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.spinner_layout, null);

			holder.objNumber = (TextView) convertView
					.findViewById(R.id.textview);
			holder.layout = (LinearLayout) convertView
					.findViewById(R.id.spinner_layout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == 0) {
//			holder.layout.setBackgroundDrawable(mContext.getResources()
//					.getDrawable(R.drawable.shang));
		} else if (position == mlist.size()) {
//			holder.layout.setBackgroundDrawable(mContext.getResources()
//					.getDrawable(R.drawable.xia));
		} else {
//			holder.layout.setBackgroundDrawable(mContext.getResources()
//					.getDrawable(R.drawable.zhong));
		}
		holder.objNumber.setText(mlist.get(position).getTitle());

		return convertView;
	}

	class ViewHolder {
		TextView objNumber;
		LinearLayout layout;
	}

	protected void resetViewHolder(ViewHolder p_ViewHolder) {
		p_ViewHolder.objNumber.setText(null);

	}

}
