package com.cn7782.management.android.activity.adapter;

import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.cn7782.management.R;
import com.cn7782.management.android.activity.bean.NoticeBean;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.util.ActivityUtil;
import com.cn7782.management.util.PictureUtil;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class HaveReadAdapter extends BaseAdapter {
	private List<NoticeBean> mlist;
	private Context mcontext;
	private String tokenId;

	public HaveReadAdapter(Context context, List<NoticeBean> list) {
		this.mcontext = context;
		this.mlist = list;
		tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				mcontext);
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
			convertView = inflater.inflate(R.layout.have_read_layout, null);
			holder.department = (TextView) convertView
					.findViewById(R.id.have_department);
			holder.name = (TextView) convertView.findViewById(R.id.have_name);
			holder.time = (TextView) convertView.findViewById(R.id.have_time);
			holder.headview = (ImageView) convertView
					.findViewById(R.id.have_head);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		PictureUtil.ShowPicture(holder.headview, mcontext, mlist.get(position)
				.getImageurl());
		// if (mlist.get(position).getImageurl() == null) {
		// Drawable dd = mcontext.getResources().getDrawable(
		// R.drawable.head_img_1);
		// BitmapDrawable bd = (BitmapDrawable) dd;
		// Bitmap bm = ActivityUtil.toRoundBitmap(bd.getBitmap());
		// holder.headview.setImageBitmap(bm);
		// } else {
		// a = ActionUrl.URL
		// + "city_grid/mobile/announcement/showPic?token_id="
		// + tokenId + "&path=" + mlist.get(position).getImageurl();
		// Bitmap bb = ImageLoader.getInstance().loadImageSync(a, mOptions);
		// Bitmap c = ActivityUtil.toRoundBitmap(bb);
		// holder.headview.setImageBitmap(c);
		// }
		holder.department.setText(mlist.get(position).getDepartment());
		holder.name.setText(mlist.get(position).getName());
		holder.time.setText(mlist.get(position).getTime());
		return convertView;
	}

	class ViewHolder {
		public TextView department, name, time;
		public ImageView headview;
	}
}
