package com.cn7782.management.android.activity.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.bean.NoticeBean;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SearchAdapter extends BaseAdapter {
	private List<NoticeBean> mlist;
	private Context mcontext;
	public DisplayImageOptions mOptions;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private Drawable img_yes, img_no;

	public SearchAdapter(Context context, List<NoticeBean> list) {
		this.mlist = list;
		this.mcontext = context;
		mOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_image)
				.showImageForEmptyUri(R.drawable.default_image)
				.showImageOnFail(R.drawable.onfail_image)
				.displayer(new RoundedBitmapDisplayer(8)).cacheInMemory(true)
				.cacheOnDisc(true).build();

		Resources res = context.getResources();
		img_yes = res.getDrawable(R.drawable.look);
		img_no = res.getDrawable(R.drawable.look2);
		//调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		img_yes.setBounds(0, 0, img_yes.getMinimumWidth(), img_yes.getMinimumHeight());
		img_no.setBounds(0, 0, img_no.getMinimumWidth(), img_no.getMinimumHeight());
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

	public void setdata(List<NoticeBean> list) {
		this.mlist = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		
		if (convertView == null) {
			holder = new ViewHolder();
			final LayoutInflater inflater = LayoutInflater.from(mcontext);
			convertView = inflater.inflate(R.layout.search_listview, null);

			holder.title = (TextView) convertView
					.findViewById(R.id.notice_title);
			holder.department = (TextView) convertView
					.findViewById(R.id.notice_department);
			holder.contect = (TextView) convertView
					.findViewById(R.id.notice_content);
			holder.time = (TextView) convertView.findViewById(R.id.notice_time);
			holder.look = (TextView) convertView.findViewById(R.id.notice_look);
			holder.comment = (TextView) convertView
					.findViewById(R.id.notice_comment);
			holder.message_image = (ImageView) convertView
					.findViewById(R.id.notice_image);
			holder.label_layout = (LinearLayout) convertView
					.findViewById(R.id.notice_image_layout);
			holder.isTop_iv = (ImageView)convertView
					.findViewById(R.id.isTop_iv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		NoticeBean notice = mlist.get(position);
		holder.title.setText(notice.getTitle());
		holder.department.setText(notice.getDepartment());
		holder.contect.setText(ToDBC(notice.getContent()));
		holder.time.setText(notice.getTime());
		if (notice.getComment() == null) {
			holder.comment.setVisibility(View.GONE);
		} else {
			holder.comment.setVisibility(View.VISIBLE);
		}
		holder.look.setText(notice.getLook());
		//是否未读
		if ("2".equals(notice.getIsRead())){
			holder.look.setCompoundDrawables(img_no,null,null,null);
		}else{
			holder.look.setCompoundDrawables(img_yes,null,null,null);
		}
		//是否置顶
		if ("1".equals(notice.getIsTop())){
			holder.isTop_iv.setVisibility(View.VISIBLE);
		}else{
			holder.isTop_iv.setVisibility(View.GONE);
		}
		holder.comment.setText(notice.getComment());
		if (notice.getImageurl() == null
				|| mlist.get(position).getImageurl().equals("")) {
			holder.label_layout.setVisibility(View.GONE);
		} else {
			String tokenId = SharedPreferenceUtil.getValue(
					PreferenceConstant.MARK_ID_TABLE,
					PreferenceConstant.MARK_ID, mcontext);
			String a = ActionUrl.URL
					+ "city_grid/mobile/announcement/showPic?token_id="
					+ tokenId + "&path=" + notice.getImageurl();
			holder.label_layout.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(a, holder.message_image,
					mOptions, animateFirstListener);
		}
		return convertView;
	}

	/**
	 * 图片加载第一次显示监听器
	 * 
	 * @author Administrator
	 * 
	 */
	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				// 是否第一次显示
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					// 图片淡入效果
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	class ViewHolder {
		public TextView title, department, contect, time, look, comment;
		public ImageView message_image,isTop_iv;
		public LinearLayout label_layout;
	}

	// 将textview中的字符全角化
	public String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}
}
