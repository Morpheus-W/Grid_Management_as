package com.cn7782.management.android.activity.adapter;

import java.util.List;
import com.cn7782.management.R;
import com.cn7782.management.android.activity.MainActivity;
import com.cn7782.management.android.activity.bean.NoticeBean;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.util.ActivityUtil;
import com.cn7782.management.util.PictureUtil;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

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

public class CommentAdapter extends BaseAdapter {
	private List<NoticeBean> mlist;
	private Context mcontext;
	public DisplayImageOptions mOptions;
	private String tokenId;
	Bitmap c = null;

	public CommentAdapter(Context context, List<NoticeBean> list) {
		this.mcontext = context;
		this.mlist = list;
		mOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_header)
				.showImageForEmptyUri(R.drawable.default_header)
				.showImageOnFail(R.drawable.default_header)
				.displayer(new RoundedBitmapDisplayer(8)).cacheInMemory(true)
				.cacheOnDisc(true).build();
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
			convertView = inflater.inflate(R.layout.comment_listview, null);
			holder.title = (TextView) convertView
					.findViewById(R.id.comment_name);
			holder.department = (TextView) convertView
					.findViewById(R.id.comment_department);
			holder.contect = (TextView) convertView
					.findViewById(R.id.comment_content);
			holder.time = (TextView) convertView
					.findViewById(R.id.comment_time);
			holder.message_image = (ImageView) convertView
					.findViewById(R.id.comment_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ImageLoader.getInstance().init(
				ImageLoaderConfiguration.createDefault(mcontext));
		holder.title.setText(mlist.get(position).getTitle());
		holder.department.setText(mlist.get(position).getDepartment());
		holder.contect.setText(mlist.get(position).getContent());
		holder.time.setText(mlist.get(position).getTime());
		PictureUtil.ShowPicture(holder.message_image,
				mcontext, mlist.get(position).getImageurl());
		String a = "";
		// if (mlist.get(position).getImageurl() == null) {
		// Drawable dd = mcontext.getResources().getDrawable(
		// R.drawable.head_img_1);
		// BitmapDrawable bd = (BitmapDrawable) dd;
		// Bitmap bm = ActivityUtil.toRoundBitmap(bd.getBitmap());
		// holder.message_image.setImageBitmap(bm);
		// } else {
		// a = ActionUrl.URL
		// + "city_grid/mobile/announcement/showPic?token_id="
		// + tokenId + "&path=" + mlist.get(position).getImageurl();
		//
		// ImageLoader.getInstance().loadImage(a,
		// new SimpleImageLoadingListener() {
		//
		// @Override
		// public void onLoadingComplete(String imageUri,
		// View view, Bitmap loadedImage) {
		// super.onLoadingComplete(imageUri, view, loadedImage);
		//
		// c = ActivityUtil.toRoundBitmap(loadedImage);
		// }
		//
		// });
		//
		// holder.message_image.setImageBitmap(c);
		//
		// }

		// ImageLoader.getInstance().displayImage(a, holder.message_image,
		// mOptions);
		// Bitmap bb = ImageLoader.getInstance().loadImageSync(a);
		// Bitmap c = ActivityUtil.toRoundBitmap(bb);
		// holder.message_image.setImageBitmap(c);
		return convertView;
	}

	class ViewHolder {
		public TextView title, department, contect, time;
		public ImageView message_image;

	}
}
