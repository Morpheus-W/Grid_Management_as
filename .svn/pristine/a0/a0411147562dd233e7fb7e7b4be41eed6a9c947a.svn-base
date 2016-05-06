package com.cn7782.management.android.activity.adapter;

import java.util.List;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.adapter.WorkingAdapter.ViewHolder;
import com.cn7782.management.android.activity.bean.MyReportBean;
import com.cn7782.management.android.activity.bean.NoticeBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ReportAdapter extends BaseAdapter {
	private List<MyReportBean> mlist;
	private Context mcontext;
	// 记录是否重复
	private String Repeat = "";

	public ReportAdapter(Context context, List<MyReportBean> list) {
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
			convertView = inflater.inflate(R.layout.report_adapter, null);
			holder.head_title = (TextView) convertView
					.findViewById(R.id.report_title);
			holder.department = (TextView) convertView
					.findViewById(R.id.report_department);
			holder.time = (TextView) convertView.findViewById(R.id.report_time);
			holder.content = (TextView) convertView
					.findViewById(R.id.report_content);
			holder.image = (ImageView) convertView
					.findViewById(R.id.report_position);
			holder.top_line = (TextView) convertView
					.findViewById(R.id.top_line);
			holder.bottom_line = (TextView) convertView
					.findViewById(R.id.bottom_line);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.head_title.setText(getstr(mlist.get(position).getState()));
		holder.department.setText(mlist.get(position).getUser());
		holder.time.setText(mlist.get(position).getTime());
		holder.content.setText(mlist.get(position).getContent());

		holder.image.setImageDrawable(mcontext.getResources().getDrawable(
				getimage(mlist.get(position).getState())));
		if (position == 0) {
			getimage(mlist.get(position).getState(), holder.image, true);
		} else {
			getimage(mlist.get(position).getState(), holder.image, false);
		}
		if (position == mlist.size() - 1) {
			holder.top_line.setVisibility(View.VISIBLE);
			holder.bottom_line.setVisibility(View.INVISIBLE);
		} else if (position == 0) {
			holder.top_line.setVisibility(View.INVISIBLE);
			holder.bottom_line.setVisibility(View.VISIBLE);
		}

		if (1 == mlist.size()) {
			holder.image.setVisibility(View.VISIBLE);
			holder.image.setImageDrawable(mcontext.getResources().getDrawable(
					R.drawable.report_one));
			holder.top_line.setVisibility(View.INVISIBLE);
			holder.bottom_line.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView image;
		TextView head_title, department, time, content;
		TextView top_line, bottom_line;
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
			return R.drawable.report_one;
		} else if (str.equals("S02")) {
			return R.drawable.report_two;
		} else if (str.equals("S03")) {
			return R.drawable.report_three;
		} else if (str.equals("S04")) {
			return R.drawable.report_four;
		} else if (str.equals("S05")) {
			return R.drawable.report_five;
		} else if (str.equals("S06")) {
			return R.drawable.report_six;
		} else if (str.equals("S07")) {
			return R.drawable.report_seven;
		} else {
			return R.drawable.report_one;
		}
	}

	private void getimage(String str, ImageView image, boolean first) {
		image.setVisibility(View.VISIBLE);
		if (Repeat.equals(str)) {
			image.setVisibility(View.GONE);
			return;
		}
		if (str.equals("S01")) {
			Repeat = str;
			if (first) {
				image.setImageDrawable(mcontext.getResources().getDrawable(
						R.drawable.report_one));
			} else {
				image.setImageDrawable(mcontext.getResources().getDrawable(
						R.drawable.report_grey_one));
			}
		} else if (str.equals("S02")) {
			Repeat = str;
			if (first) {
				image.setImageDrawable(mcontext.getResources().getDrawable(
						R.drawable.report_two));
			} else {
				image.setImageDrawable(mcontext.getResources().getDrawable(
						R.drawable.report_grey_two));
			}
		} else if (str.equals("S03")) {
			Repeat = str;
			if (first) {
				image.setImageDrawable(mcontext.getResources().getDrawable(
						R.drawable.report_three));
			} else {
				image.setImageDrawable(mcontext.getResources().getDrawable(
						R.drawable.report_grey_three));
			}
		} else if (str.equals("S04")) {
			Repeat = str;
			if (first) {
				image.setImageDrawable(mcontext.getResources().getDrawable(
						R.drawable.report_four));
			} else {
				image.setImageDrawable(mcontext.getResources().getDrawable(
						R.drawable.report_grey_four));
			}
		} else if (str.equals("S05")) {
			Repeat = str;
			if (first) {
				image.setImageDrawable(mcontext.getResources().getDrawable(
						R.drawable.report_five));
			} else {
				image.setImageDrawable(mcontext.getResources().getDrawable(
						R.drawable.report_grey_five));
			}
		} else if (str.equals("S06")) {
			Repeat = str;
			if (first) {
				image.setImageDrawable(mcontext.getResources().getDrawable(
						R.drawable.report_six));
			} else {
				image.setImageDrawable(mcontext.getResources().getDrawable(
						R.drawable.report_grey_six));
			}
		} else if (str.equals("S07")) {
			Repeat = str;
			image.setImageDrawable(mcontext.getResources().getDrawable(
					R.drawable.report_seven));
		} else {
			// image.setImageDrawable(mcontext.getResources().getDrawable(
			// R.drawable.report_one));
		}
	}

	/**
	 * 
	 * "S01"：报送 "S02"： 处置 "S03"：督办 "S04"： 销案 "S05"： 核查
	 * 
	 * @param str
	 * @return
	 */
	private String getstr(String str) {
		if (str.equals("S01")) {
			return "报送";
		} else if (str.equals("S02")) {
			return "处置";
		} else if (str.equals("S03")) {
			return "督办";
		} else if (str.equals("S04")) {
			return "销案";
		} else if (str.equals("S05")) {
			return "核查";
		} else if (str.equals("S06")) {
			return "转发";
		} else if (str.equals("S07")) {
			return "完结";
		} else {
			return " ";
		}
	}
}
