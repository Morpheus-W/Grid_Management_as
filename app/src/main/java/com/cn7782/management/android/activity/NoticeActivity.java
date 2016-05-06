package com.cn7782.management.android.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.activity.adapter.SearchAdapter;
import com.cn7782.management.android.activity.bean.NoticeBean;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.http.HttpClient;
import com.cn7782.management.http.MyAsyncHttpResponseHandler;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.cn7782.management.view.PullToRefreshView;
import com.cn7782.management.view.PullToRefreshView.OnFooterRefreshListener;
import com.cn7782.management.view.PullToRefreshView.OnHeaderRefreshListener;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NoticeActivity extends BaseActivity {
	private SearchAdapter searchadapter;
	private List<NoticeBean> list = new ArrayList<NoticeBean>();
	private ListView listview;
	private View back;
	String tokenId;
	String type = "1";
	private PopupWindow popup;
	private int pagecount = 1;
	private int count = 0;
	private PullToRefreshView mPullToRefreshView;
	private OnHeaderRefreshListener mOnHeaderRefreshListener;
	private OnFooterRefreshListener mOnFooterRefreshListener;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_fragment);

		initView();

		getdata();
		// 下拉刷新监听
		mOnHeaderRefreshListener = new OnHeaderRefreshListener() {
			public void onHeaderRefresh(PullToRefreshView view) {
				// TODO Auto-generated method stub
				startRefresh();
				return;
			}
		};
		// 点击下拉菜单底部的"更多"按钮，加载更多讨论列表
		mOnFooterRefreshListener = new OnFooterRefreshListener() {
			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				getMoreData();
				return;
			}
		};
	}

	private void initView() {
		final View view = this.getLayoutInflater().inflate(
				R.layout.layout_dialog_operationselect, null);

		popup = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		popup.setFocusable(true);
		popup.setOutsideTouchable(true);
		popup.setBackgroundDrawable(new BitmapDrawable());
		WindowManager wm = this.getWindowManager();
		final int width = wm.getDefaultDisplay().getWidth();

		findViewById(R.id.kcool_title_back).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						int xoff = width
								/ 3
								- ((View) findViewById(R.id.kcool_title_back))
										.getWidth() / 2;
						popup.update();
						popup.showAsDropDown(
								findViewById(R.id.kcool_title_back), -xoff, 0);
						// popup.showAtLocation(
						// findViewById(R.id.kcool_title_back),
						// Gravity.CENTER_VERTICAL, 0, 0);
					}
				});
		((View) view.findViewById(R.id.popup_notice))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						type = "2";
						((TextView) findViewById(R.id.kcool_title_back))
								.setText("通知");
						getdata();
						popup.dismiss();
					}
				});
		((View) view.findViewById(R.id.popup_administrative))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						type = "1";
						((TextView) findViewById(R.id.kcool_title_back))
								.setText("行政公告");
						getdata();
						popup.dismiss();
					}
				});
		back = (View) findViewById(R.id.search_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		findViewById(R.id.add_notice).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(NoticeActivity.this,
						SendNoticeActivity.class);
				startActivity(intent);
			}
		});
	}

	private void initListView() {
		listview = (ListView) findViewById(R.id.search_list);
		searchadapter = new SearchAdapter(this, list);
		listview.setAdapter(searchadapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String Id = list.get(position).getId();
				Intent intent = new Intent(NoticeActivity.this,
						NoticeDetailActivity.class);
				intent.putExtra("id", Id);
//				intent.putExtra("tokenId", tokenId);
//				startActivity(intent);
				startActivityForResult(intent,position);
			}
		});
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(mOnHeaderRefreshListener);
		mPullToRefreshView.setOnFooterRefreshListener(mOnFooterRefreshListener);
	}

	// 刷新
	private void startRefresh() {
		pagecount = 1;
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("pageNo", "1");
		param.put("pageSize", "10");
		param.put("type", type);
		HttpClient.post(NoticeActivity.this, ActionUrl.NOTICE, param,
				new MyAsyncHttpResponseHandler(NoticeActivity.this, "请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);

						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");

								if (msg.equals("查询成功")) {
									JSONObject jo1 = jsonObject
											.getJSONObject("data");
									//更新总数
									count = jo1.getInt("count");
									mPullToRefreshView.StartFootView();
									JSONArray jo2 = jo1.getJSONArray("list");
									list.clear();
									for (int i = 0; i < jo2.length(); i++) {
										JSONObject json = jo2.getJSONObject(i);
										NoticeBean a = new NoticeBean();
										a.setContent(json.isNull("content") ? ""
												: json.getString("content"));
										a.setId(json.isNull("id") ? "" : json
												.getString("id"));
										a.setTime(json.isNull("log_time") ? ""
												: json.getString("log_time"));
										a.setTitle(json.isNull("title") ? ""
												: json.getString("title"));
										a.setDepartment(json.isNull("typeName") ? ""
												: json.getString("typeName"));
										a.setImageurl(json.isNull("url") ? ""
												: json.getString("url"));
										a.setLook(json.isNull("read_count") ? ""
												: json.getString("read_count"));
										a.setComment(json
												.isNull("comment_count") ? ""
												: json.getString("comment_count"));
										//增加是否已读,置顶
										a.setIsRead(json.optString("isRead"));
										a.setIsTop(json.optString("isTop"));
										list.add(a);
									}
									searchadapter.setdata(list);
									searchadapter.notifyDataSetInvalidated();
									mPullToRefreshView
											.onHeaderRefreshComplete();
									mPullToRefreshView
											.onFooterRefreshComplete();
								}
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					public void onFailure(Throwable arg0, String tipInfo) {
						super.onFailure(arg0, tipInfo);
					}
				});
	}

	// 加载更多
	private void getMoreData() {
		if (list.size() == count) {
			mPullToRefreshView.StopFootView();
			searchadapter.setdata(list);
			searchadapter.notifyDataSetInvalidated();
			mPullToRefreshView
					.onHeaderRefreshComplete();
			mPullToRefreshView
					.onFooterRefreshComplete();
			Toast.makeText(NoticeActivity.this,
					"已加载到最后一页", Toast.LENGTH_SHORT)
					.show();
			return ;
		}
		pagecount++;
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("pageNo", "" + pagecount);
		param.put("pageSize", "10");
		param.put("type", type);
		HttpClient.post(NoticeActivity.this, ActionUrl.NOTICE, param,
				new MyAsyncHttpResponseHandler(NoticeActivity.this, "请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);

						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");

								if (msg.equals("查询成功")) {
									JSONObject jo1 = jsonObject
											.getJSONObject("data");
									count = jo1.getInt("count");

									JSONArray jo2 = jo1.getJSONArray("list");

									for (int i = 0; i < jo2.length(); i++) {
										JSONObject json = jo2.getJSONObject(i);
										NoticeBean a = new NoticeBean();
										a.setContent(json.isNull("content") ? ""
												: json.getString("content"));
										a.setId(json.isNull("id") ? "" : json
												.getString("id"));
										a.setTime(json.isNull("log_time") ? ""
												: json.getString("log_time"));
										a.setTitle(json.isNull("title") ? ""
												: json.getString("title"));
										a.setDepartment(json.isNull("typeName") ? ""
												: json.getString("typeName"));
										a.setImageurl(json.isNull("url") ? ""
												: json.getString("url"));
										a.setLook(json.isNull("read_count") ? ""
												: json.getString("read_count"));
										a.setComment(json
												.isNull("comment_count") ? ""
												: json.getString("comment_count"));
										//增加是否已读,置顶
										a.setIsRead(json.optString("isRead"));
										a.setIsTop(json.optString("isTop"));
										list.add(a);
									}
									searchadapter.setdata(list);
									searchadapter.notifyDataSetInvalidated();
									mPullToRefreshView
											.onHeaderRefreshComplete();
									mPullToRefreshView
											.onFooterRefreshComplete();
								}
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					public void onFailure(Throwable arg0, String tipInfo) {
						super.onFailure(arg0, tipInfo);
					}
				});
	}

	private void getdata() {
		tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				NoticeActivity.this);
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		param.put("pageNo", "" + pagecount);
		param.put("pageSize", "10");
		param.put("type", type);
		HttpClient.post(NoticeActivity.this, ActionUrl.NOTICE, param,
				new MyAsyncHttpResponseHandler(NoticeActivity.this, "请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);

						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");

								if (msg.equals("查询成功")) {
									list.clear();
									JSONObject jo1 = jsonObject
											.getJSONObject("data");
									//更新记录总数
									count = jo1.getInt("count");
									JSONArray jo2 = jo1.getJSONArray("list");
									for (int i = 0; i < jo2.length(); i++) {
										JSONObject json = jo2.getJSONObject(i);
										NoticeBean a = new NoticeBean();
										a.setContent(json.isNull("content") ? ""
												: json.getString("content"));
										a.setId(json.isNull("id") ? "" : json
												.getString("id"));
										a.setTime(json.isNull("log_time") ? ""
												: json.getString("log_time"));
										a.setTitle(json.isNull("title") ? ""
												: json.getString("title"));
										a.setDepartment(json.isNull("typeName") ? ""
												: json.getString("typeName"));
										a.setImageurl(json.isNull("url") ? ""
												: json.getString("url"));
										a.setLook(json.isNull("read_count") ? ""
												: json.getString("read_count"));
										a.setComment(json
												.isNull("comment_count") ? ""
												: json.getString("comment_count"));
										//增加是否已读,置顶
										a.setIsRead(json.optString("isRead"));
										a.setIsTop(json.optString("isTop"));
										list.add(a);
									}

								}
							}
							initListView();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					public void onFailure(Throwable arg0, String tipInfo) {
						super.onFailure(arg0, tipInfo);
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//更新集合数据
		list.get(requestCode).setIsRead("1");
		//刷新页面
		searchadapter.notifyDataSetChanged();
	}
}