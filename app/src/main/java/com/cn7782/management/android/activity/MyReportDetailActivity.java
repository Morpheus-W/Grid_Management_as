package com.cn7782.management.android.activity;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.activity.adapter.ReportAdapter;
import com.cn7782.management.android.activity.bean.MyReportBean;
import com.cn7782.management.android.activity.bean.NoticeBean;
import com.cn7782.management.android.activity.tabview.ChangeListView;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.http.HttpClient;
import com.cn7782.management.http.MyAsyncHttpResponseHandler;
import com.cn7782.management.util.MedieaPlayerUtil;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyReportDetailActivity extends BaseActivity {
	private ChangeListView listview;
	private ReportAdapter reportAdapter;
	private List<MyReportBean> list;
	private List<NoticeBean> nlist;
	String tokenId;
	private String title, content, time, address;
	private LinearLayout mAddPhotoLayout;
	public DisplayImageOptions mOptions;
	private int indicatorWidth;
	Bitmap bb = null;
	private MedieaPlayerUtil medieaPlayerUtil;

	private boolean isPlay = true;
	private ScrollView scrollView;
	private VideoView videoView;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_myreport_detail);

		getdata();
		
		//返回
		findViewById(R.id.tile_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	private void getdata() {
		String id = getIntent().getStringExtra("working_id");
		tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				MyReportDetailActivity.this);
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("id", id);
		HttpClient.post(MyReportDetailActivity.this, ActionUrl.MYREPORT_DETAIL,
				param, new MyAsyncHttpResponseHandler(
						MyReportDetailActivity.this, "请求中...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							if (list == null)
								list = new ArrayList<MyReportBean>();
							if (nlist == null)
								nlist = new ArrayList<NoticeBean>();
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("查询成功")) {
									String entity = jsonObject.isNull("entity") ? ""
											: jsonObject.getString("entity")
											+ " > ";
									String event_type = jsonObject
											.isNull("event_type") ? ""
											: jsonObject
											.getString("event_type");
									title = entity + event_type;
									content = jsonObject.isNull("remark") ? ""
											: jsonObject.getString("remark");
									time = jsonObject.isNull("create_time") ? ""
											: jsonObject
											.getString("create_time");
									address = jsonObject.isNull("place") ? ""
											: jsonObject.getString("place");
									JSONArray jo1 = jsonObject
											.getJSONArray("idear");
									for (int i = 0; i < jo1.length(); i++) {
										JSONObject json = jo1.getJSONObject(i);
										MyReportBean a = new MyReportBean();
										a.setUser(json.isNull("app_user") ? ""
												: json.getString("app_user"));
										a.setState(json.isNull("app_state") ? ""
												: json.getString("app_state"));
										a.setContent(json.isNull("app_content") ? ""
												: json.getString("app_content"));
										a.setTime(json.isNull("app_time") ? ""
												: json.getString("app_time"));
										list.add(a);
									}
									JSONArray jo2 = jsonObject
											.getJSONArray("files");
									for (int i = 0; i < jo2.length(); i++) {
										JSONObject json = jo2.getJSONObject(i);
										NoticeBean a = new NoticeBean();
										a.setFilesize(json.isNull("url_size") ? ""
												: json.getString("url_size"));
										a.setImageurl(json.isNull("url") ? "" : json.getString("url"));
										a.setUrl_id(json.isNull("url_id") ? "" : json.getString("url_id"));
										nlist.add(a);
									}
									initView();
								}
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void initView() {
		medieaPlayerUtil = new MedieaPlayerUtil(this);
		listview = (ChangeListView) findViewById(R.id.myreport_list);
		listview.setDivider(null);
		reportAdapter = new ReportAdapter(this, list);
		listview.setAdapter(reportAdapter);
		mAddPhotoLayout = (LinearLayout) findViewById(R.id.attachment_list);
		((TextView) findViewById(R.id.detail_title)).setText(title);
		((TextView) findViewById(R.id.detail_content)).setText(content);
		((TextView) findViewById(R.id.working_time)).setText(time);
		((TextView) findViewById(R.id.working_position)).setText(address);
		initNavigationHSV();
		scrollView = (ScrollView) findViewById(R.id.report_scroll);
		scrollView.fullScroll(ScrollView.FOCUS_DOWN);
	}

	private void initNavigationHSV() {

		WindowManager wm = this.getWindowManager();
		int width = wm.getDefaultDisplay().getWidth();
		indicatorWidth = width / 5;
		mOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.head_img_1)
				.showImageForEmptyUri(R.drawable.head_img_1)
				.showImageOnFail(R.drawable.head_img_1)
				.displayer(new RoundedBitmapDisplayer(8)).cacheInMemory(true)
				.cacheOnDisc(true).build();
		mAddPhotoLayout.removeAllViews();
		ImageLoader.getInstance().init(
				ImageLoaderConfiguration.createDefault(this));

		for (int i = 0; i < nlist.size(); i++) {

			LayoutInflater mInflater = (LayoutInflater) this
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			final View rb = (View) mInflater
					.inflate(R.layout.head_layout, null);
			rb.setId(i);
			TextView rbt;
			final ImageView rbb;
			rbb = (ImageView) rb.findViewById(R.id.head_iamge);
			rbt = (TextView) rb.findViewById(R.id.head_text);
			//判断语音
			final String a = ActionUrl.URL
					+ "city_grid/mobile/announcement/showPic?token_id="
//						+ tokenId + "&path=" + nlist.get(i).getImageurl();
					+ tokenId + "&url_id=" + nlist.get(i).getUrl_id();
			if ("mp3".equals(getsuffix(nlist.get(i).getImageurl()))) {
				rbb.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.broadcast));
				rbb.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						excutePlay(a);
					}
				});
			}
			//判断视频
			else if("mp4".equals(getsuffix(nlist.get(i).getImageurl()))) {
				rbb.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.videoplay));
				rbb.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						excutePlayVideo(a);
					}
				});
			}
			//其他为图片
			else{
				rbb.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.default_image));
				ImageLoader.getInstance().loadImage(a,
						new SimpleImageLoadingListener() {

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								super.onLoadingComplete(imageUri, view,	loadedImage);
								rbb.setImageBitmap(loadedImage);
							}

						});
			}
			rbt.setVisibility(View.GONE);
			rb.setLayoutParams(new LayoutParams(indicatorWidth, indicatorWidth));
			mAddPhotoLayout.addView(rb);
		}
	}

	private boolean Getsuffix(String str) {
		boolean a = true;
		String[] m = str.split("\\.");

		String b = "";
		try {
			b = m[1];
			if (b.equals("mp3")) {
				a = false;
			} else {
				a = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			a = false;
		}

		return a;
	}
	private String getsuffix(String str){
		String suffix = "";
		String[] m = str.split("\\.");
		String b = "";
		if (m.length > 1){
			b = m[1];
			return b;
		}
		return suffix;
	}
	private void excutePlay(String path){
		if(isPlay) {
			isPlay = false;
			medieaPlayerUtil.playURL(path);
		}else {
			if(medieaPlayerUtil.getmMediaPlayer().isPlaying()){
				medieaPlayerUtil.getmMediaPlayer().pause();
			}else{
				medieaPlayerUtil.getmMediaPlayer().start();
			}
		}
	}
	private void excutePlayVideo(String path){
		Uri uri = Uri.parse(path);
		videoView = (VideoView)this.findViewById(R.id.mVideoView);
		videoView.setVisibility(View.VISIBLE);
		scrollView.setVisibility(View.GONE);
		videoView.setMediaController(new MediaController(this));
		videoView.setVideoURI(uri);
		videoView.start();
		videoView.requestFocus();
		videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				videoView.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	protected void onDestroy() {
		medieaPlayerUtil.stopPlay();
		super.onDestroy();
	}
}
