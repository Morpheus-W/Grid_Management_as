package com.cn7782.management.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.activity.adapter.CommentAdapter;
import com.cn7782.management.android.activity.adapter.FileListAdapter;
import com.cn7782.management.android.activity.bean.NoticeBean;
import com.cn7782.management.android.activity.tabview.MyProgressDialog;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.http.HttpClient;
import com.cn7782.management.http.HttpDownloader;
import com.cn7782.management.http.MyAsyncHttpResponseHandler;
import com.cn7782.management.util.ActivityUtil;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NoticeDetailActivity extends BaseActivity {
	private NoticeBean bean;
	private List<NoticeBean> list = new ArrayList<NoticeBean>();
	private TextView title, department, content, look, time;
	private ListView listfile, listcomment;
	private CommentAdapter commentadapter;
	private FileListAdapter filelistadapter;
	private String tokenId;
	private View noticeSubmit, viewCount;
	private EditText edittext;
	private MyProgressDialog myProgressDialog;
	private File file;
	private View back;
	private List<String> imagelist;
	private LinearLayout mAddPhotoLayout;
	private int indicatorWidth;
	public DisplayImageOptions mOptions;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_noticedetail);
		// 第一步请求数据
		getdata();

		findViewById(R.id.search_back).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
	}

	private void initView() {

		mAddPhotoLayout = (LinearLayout) findViewById(R.id.attachment_list);
		back = (View) findViewById(R.id.search_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		listfile = (ListView) findViewById(R.id.noticedetail_file);
		filelistadapter = new FileListAdapter(this, list);
		listfile.setAdapter(filelistadapter);
		title = (TextView) findViewById(R.id.notice_title);
		department = (TextView) findViewById(R.id.notice_department);
		content = (TextView) findViewById(R.id.noticedetail_content);
		look = (TextView) findViewById(R.id.noticedetail_count);
		time = (TextView) findViewById(R.id.notice_time);
		title.setText(bean.getTitle());
		department.setText(bean.getDepartment());
		content.setText(ToDBC(bean.getContent()));
		look.setText("浏览人数 " + bean.getLook());
		time.setText(bean.getTime());
		edittext = (EditText) findViewById(R.id.notice_edit_submit);
		noticeSubmit = (View) findViewById(R.id.notice_submit);
		viewCount = (View) findViewById(R.id.view_count);
		viewCount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NoticeDetailActivity.this,
						HaveReadActivity.class);
				intent.putExtra("objId", bean.getId());
				intent.putExtra("readtype", HaveReadActivity.NOTICEREAD);
				startActivity(intent);
			}
		});
		noticeSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				submitComment();
			}
		});
		// 第二步请求数据
		getcommentdata();
		listfile.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					String downurl = ActionUrl.URL
							+ "city_grid/mobile/announcement/downloadFile?token_id="
							+ tokenId + "&path="
							+ list.get(position).getImageurl() + "&url_id="
							+ list.get(position).getUrl_id();
					String name = list.get(position).getName();
					long filesize = Long.parseLong(list.get(position)
							.getFilesize());
					downLoadFile(downurl, name, name, filesize);
				} catch (Exception e) {

				}
			}
		});
		initNavigationHSV();
	}

	// 第二步请求评论列表数据
	private void getcommentdata() {
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("objId", bean.getId());
		HttpClient.post(NoticeDetailActivity.this,
				ActionUrl.NOTICE_COMMENT_LIST, param,
				new MyAsyncHttpResponseHandler(NoticeDetailActivity.this,
						"请求中,请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						List<NoticeBean> mlist = new ArrayList<NoticeBean>();
						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("查询成功")) {
									JSONObject jo1 = jsonObject
											.getJSONObject("data");
									JSONArray jo2 = jo1.getJSONArray("list");
									for (int i = 0; i < jo2.length(); i++) {
										JSONObject json = jo2.getJSONObject(i);
										NoticeBean a = new NoticeBean();
										a.setContent(json.isNull("content") ? ""
												: json.getString("content"));
										a.setDepartment(json
												.isNull("department") ? ""
												: json.getString("department"));
										a.setTitle(json.isNull("user_name") ? ""
												: json.getString("user_name"));
										a.setTime(json.isNull("log_time") ? ""
												: json.getString("log_time"));
										a.setImageurl(json.isNull("user_url") ? ""
												: json.getString("user_url"));
										mlist.add(a);
									}
								}
							}
							initCommentList(mlist);
						} catch (JSONException e) {

						}
					}

					public void onFailure(Throwable arg0, String tipInfo) {
						super.onFailure(arg0, tipInfo);

					}
				});
	}

	// 第一步请求数据
	private void getdata() {
		bean = new NoticeBean();
		tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				NoticeDetailActivity.this);
		String id = getIntent().getStringExtra("id");
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("id", id);
		HttpClient.post(NoticeDetailActivity.this, ActionUrl.NOTICE_DETAIL,
				param, new MyAsyncHttpResponseHandler(
						NoticeDetailActivity.this, "请求中,请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (imagelist == null) {
									imagelist = new ArrayList<String>();
								}
								if (msg.equals("查询成功")) {
									JSONObject jo1 = jsonObject
											.getJSONObject("data");
									bean.setId(jo1.isNull("id") ? "" : jo1
											.getString("id"));
									bean.setContent(jo1.isNull("content") ? ""
											: jo1.getString("content"));
									bean.setTitle(jo1.isNull("title") ? ""
											: jo1.getString("title"));
									bean.setTime(jo1.isNull("log_time") ? ""
											: jo1.getString("log_time"));
									bean.setDepartment(jo1.isNull("department") ? ""
											: jo1.getString("department"));
									bean.setLook(jo1.isNull("read_count") ? ""
											: jo1.getString("read_count"));
									JSONArray jo2 = jo1.getJSONArray("pic");
									for (int i = 0; i < jo2.length(); i++) {
										JSONObject json = jo2.getJSONObject(i);
										String name = json.isNull("url_name") ? ""
												: json.getString("url_name");
										if (!Getsuffix(name)) {
											imagelist.add(json.isNull("url") ? ""
													: json.getString("url"));
										} else {
											NoticeBean a = new NoticeBean();
											String url = json.isNull("url") ? ""
													: json.getString("url");
											String filesize = json
													.isNull("url_size") ? ""
													: json.getString("url_size");
											String url_id = json
													.isNull("url_id") ? ""
													: json.getString("url_id");
											a.setUrl_id(url_id);
											a.setName(name);
											a.setImageurl(url);
											a.setFilesize(filesize);
											list.add(a);
										}
									}
								}
							}
							initView();
						} catch (JSONException e) {

						}
					}

					public void onFailure(Throwable arg0, String tipInfo) {
						super.onFailure(arg0, tipInfo);

					}
				});
	}

	private void initCommentList(List<NoticeBean> list) {
		if (list == null) {
			return;
		}
		commentadapter = new CommentAdapter(this, list);
		listcomment = (ListView) findViewById(R.id.noticedetail_list);
		listcomment.setAdapter(commentadapter);
	}

	private void submitComment() {
		String str = edittext.getText().toString();
		if (str.equals("")) {
			Toast.makeText(this, "评论不能为空", Toast.LENGTH_LONG).show();
			return;
		}
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("objId", bean.getId());
		param.put("content", str);
		HttpClient.post(NoticeDetailActivity.this, ActionUrl.SUBMIT_COMMENT,
				param, new MyAsyncHttpResponseHandler(
						NoticeDetailActivity.this, "提交评论中,请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("评论成功")) {
									Toast.makeText(NoticeDetailActivity.this,
											"评论成功", Toast.LENGTH_LONG).show();
									edittext.setText("");
									getdata();
								} else {
									Toast.makeText(NoticeDetailActivity.this,
											"评论失败", Toast.LENGTH_LONG).show();
								}
							}
						} catch (JSONException e) {
							Toast.makeText(NoticeDetailActivity.this, "评论失败",
									Toast.LENGTH_LONG).show();
						}
					}

					public void onFailure(Throwable arg0, String tipInfo) {
						super.onFailure(arg0, tipInfo);
					}
				});
	}

	private Handler updataHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				int progress = msg.arg1;
				if (myProgressDialog != null) {
					myProgressDialog.setMessage("附件下载中  " + progress + "%");
					System.out.println("附件下载中  " + progress + "%");
				}
				break;
			case 2:
				if (myProgressDialog != null) {
					myProgressDialog.dismiss();
					myProgressDialog = null;
				}

				break;

			case 3:
				Toast.makeText(NoticeDetailActivity.this, "出错了，下载出现异常！",
						Toast.LENGTH_LONG).show();
				if (myProgressDialog != null) {
					myProgressDialog.dismiss();
					myProgressDialog = null;
				}

				break;
			case 4:
				Toast.makeText(NoticeDetailActivity.this, "存储卡空间不足，不能下载更新！",
						Toast.LENGTH_LONG).show();
				if (myProgressDialog != null) {
					myProgressDialog.dismiss();
					myProgressDialog = null;
				}

				break;
			case 5:
				Toast.makeText(NoticeDetailActivity.this, "已下载了！",
						Toast.LENGTH_LONG).show();
				if (myProgressDialog != null) {
					myProgressDialog.dismiss();
					myProgressDialog = null;
				}

				break;
			}
		}
	};

	public void downLoadFile(final String targetUrl, final String name,
			final String fileName, final long fileSize) {
		file = null;
		if (ActivityUtil.isSdcardExist()) {
			final String store_path_dir = Environment
					.getExternalStorageDirectory()
					+ File.separator
					+ "WangGe"
					+ File.separator + name;
			// file = new File(store_path_dir, fileName);
			file = new File(store_path_dir + "/" + fileName);
			if (file.exists()) {

				if (file.length() == fileSize) {// 文件已经存在，并且是下载完毕的
					updataHandler.sendEmptyMessage(5);
					return;
				} else {// 文件没有下载完毕。
					file.delete();
				}
			}
			//
			if (myProgressDialog == null)
				myProgressDialog = new MyProgressDialog(
						NoticeDetailActivity.this, "下载附件中，请稍后...");

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					HttpDownloader.downLoadFile(targetUrl, store_path_dir,
							fileName, updataHandler, fileSize);
				}
			}).start();

		} else {
			Toast.makeText(NoticeDetailActivity.this, "您的设备存储卡，请装上存储卡在下载！",
					Toast.LENGTH_LONG).show();
			if (myProgressDialog != null) {
				myProgressDialog.dismiss();
				myProgressDialog = null;
			}
		}

	}

	// 将textview中的字符全角化
	public String ToDBC(String input) {
		if (TextUtils.isEmpty(input))
			return "";
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

	private boolean Getsuffix(String str) {
		boolean a = false;
		String[] m = str.split("\\.");
		String b = "";
		try {
			b = m[1];
			if (b.equals("jpg") || b.equals("png")) {
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

	private void initNavigationHSV() {
		listfile = (ListView) findViewById(R.id.noticedetail_file);
		filelistadapter = new FileListAdapter(this, list);
		listfile.setAdapter(filelistadapter);

		mAddPhotoLayout = (LinearLayout) findViewById(R.id.attachment_list);
		WindowManager wm = this.getWindowManager();
		int width = wm.getDefaultDisplay().getWidth();
		indicatorWidth = width / 5;
		mOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_image)
				.showImageForEmptyUri(R.drawable.default_image)
				.showImageOnFail(R.drawable.default_image)
				.displayer(new RoundedBitmapDisplayer(8)).cacheInMemory(true)
				.cacheOnDisc(true).build();
		mAddPhotoLayout.removeAllViews();
		ImageLoader.getInstance().init(
				ImageLoaderConfiguration.createDefault(this));

		if (imagelist.size() == 0) {
			mAddPhotoLayout.setVisibility(View.GONE);
		}

		for (int i = 0; i < imagelist.size(); i++) {
			LayoutInflater mInflater = (LayoutInflater) this
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			final View rb = (View) mInflater
					.inflate(R.layout.head_layout, null);
			rb.setId(i);
			TextView rbt;
			final ImageView rbb;
			rbb = (ImageView) rb.findViewById(R.id.head_iamge);
			rbt = (TextView) rb.findViewById(R.id.head_text);
			String a = ActionUrl.URL
					+ "city_grid/mobile/announcement/showPic?token_id="
					+ tokenId + "&path=" + imagelist.get(i);
			ImageLoader.getInstance().displayImage(a, rbb, mOptions);
			// rbb.setBackgroundDrawable(getResources().getDrawable(
			// R.drawable.default_image));
			ImageLoader.getInstance().loadImage(a,
					new SimpleImageLoadingListener() {

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							super.onLoadingComplete(imageUri, view, loadedImage);

							rbb.setImageBitmap(loadedImage);

						}

					});
			rbt.setVisibility(View.GONE);
			// rbb.setImageBitmap(null);
			rb.setLayoutParams(new LayoutParams(indicatorWidth, indicatorWidth));

			mAddPhotoLayout.addView(rb);

		}
	}
}
