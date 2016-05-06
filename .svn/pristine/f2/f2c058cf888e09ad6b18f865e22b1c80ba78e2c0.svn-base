package com.cn7782.management.android.activity.adapter;

import java.math.BigDecimal;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.cn7782.management.R;
import com.cn7782.management.android.activity.bean.NoticeBean;

public class FileListAdapter extends BaseAdapter {
	private List<NoticeBean> mlist;
	private Context mcontext;

	public FileListAdapter(Context context, List<NoticeBean> list) {
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
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			final LayoutInflater inflater = LayoutInflater.from(mcontext);
			convertView = inflater.inflate(R.layout.file_listview, null);
			holder.down = (ImageView) convertView.findViewById(R.id.down);
			holder.contect = (TextView) convertView.findViewById(R.id.content);
			holder.pic = (TextView) convertView.findViewById(R.id.file_size);
			holder.line = (View) convertView.findViewById(R.id.line);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == mlist.size()-1) {
			holder.line.setVisibility(View.GONE);
		} else {
			holder.line.setVisibility(View.VISIBLE);
		}
//		holder.contect.setText(mlist.get(position).getName());
//		holder.pic.setText(ChangeSize(mlist.get(position).getFilesize()));
		return convertView;
	}

	class ViewHolder {
		TextView contect, pic;
		ImageView down;
		View line;
	}

	public String changeKb(String str) {
		try {

			return "";
		} catch (Exception e) {
			return "";
		}

	}

	private String ChangeSize(String str) {
		String b = "";
		double size = 0;
		try {
			size = Double.parseDouble(str);
			if (size < 1024) {
				b = size + "b";
				return b;
			}
			String size2 = "" + size / 1024;
			b = new BigDecimal(size2).setScale(0, BigDecimal.ROUND_HALF_UP)
					+ " KB";
		} catch (Exception e) {
			b = "";
		}
		return b;
	}
}
