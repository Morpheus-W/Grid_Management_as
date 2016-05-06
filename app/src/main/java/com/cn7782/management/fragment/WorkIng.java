package com.cn7782.management.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.LoginActivity;
import com.cn7782.management.android.activity.MainActivity;
import com.cn7782.management.android.activity.MyReportDetailActivity;
import com.cn7782.management.android.activity.NoticeActivity;
import com.cn7782.management.android.activity.adapter.WorkingAdapter;
import com.cn7782.management.android.activity.bean.MyReportBean;
import com.cn7782.management.android.activity.bean.NoticeBean;
import com.cn7782.management.android.constants.GlobalConstant;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.http.HttpClient;
import com.cn7782.management.http.MyAsyncHttpResponseHandler;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

@SuppressLint("ValidFragment")
public class WorkIng extends Fragment {
	private WorkingAdapter workingadapter;
	private List<MyReportBean> list;
	private ListView listview;
	String tokenId;
	private String state;
	private boolean isfirst = true;

	public WorkIng(){
	}
	public WorkIng(String state){
		this.state = state;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_working, container,
				false);
		listview = (ListView) rootView.findViewById(R.id.work_list);
		if (isfirst) {
			if(state!=null && GlobalConstant.APPLSTATE.equals(state)){
				
				getdata2();
			}else{
				
				getdata();
			}
		} else {
			initView();
		}
		return rootView;
	}

	private void initView() {
		
		workingadapter = new WorkingAdapter(getActivity(), list);
		listview.setAdapter(workingadapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						MyReportDetailActivity.class);
				intent.putExtra("working_id", list.get(position).getId());
				startActivity(intent);
			}
		});
	}

	private void getdata() {
		tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				getActivity());
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		HttpClient.post(getActivity(), ActionUrl.MYREPORT, param,
				new MyAsyncHttpResponseHandler(getActivity(), "请求中...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							if (list == null)
								list = new ArrayList<MyReportBean>();
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("查询成功")) {

									JSONArray jo2 = jsonObject
											.getJSONArray("list");
									for (int i = 0; i < jo2.length(); i++) {
										JSONObject json = jo2.getJSONObject(i);
										MyReportBean a = new MyReportBean();

										String state = json.isNull("state") ? ""
												: json.getString("state");
										if(!state.equals(GlobalConstant.APPLSTATE)){
											String mtitle = json.isNull("entity") ? ""
													: json.getString("entity");
											String ntitle = json
													.isNull("event_type") ? ""
															: json.getString("event_type");
											String title = mtitle + " > " + ntitle;
											a.setContent(json.isNull("content") ? ""
													: json.getString("content"));
											a.setTime(json.isNull("date") ? ""
													: json.getString("date"));
											a.setTitle(title);
											a.setContent(json.isNull("remark") ? ""
													: json.getString("remark"));
											a.setAddress(json.isNull("place") ? ""
													: json.getString("place"));
											a.setState(json.isNull("state") ? ""
													: json.getString("state"));
											a.setId(json.isNull("id") ? "" : json
													.getString("id"));
											list.add(a);
										}
									}
								}
							}
							isfirst = false;
							initView();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}
	private void getdata2() {
		tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				getActivity());
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		HttpClient.post(getActivity(), ActionUrl.MYREPORT, param,
				new MyAsyncHttpResponseHandler(getActivity(), "请求中...") {
			public void onSuccess(String results) {
				super.onSuccess(results);
				try {
					if (list == null)
						list = new ArrayList<MyReportBean>();
					JSONObject jsonObject = new JSONObject(results);
					if (jsonObject.has("msg")) {
						String msg = jsonObject.isNull("msg") ? ""
								: jsonObject.getString("msg");
						if (msg.equals("查询成功")) {
							
							JSONArray jo2 = jsonObject
									.getJSONArray("list");
							for (int i = 0; i < jo2.length(); i++) {
								JSONObject json = jo2.getJSONObject(i);
								MyReportBean a = new MyReportBean();
								
								String state = json.isNull("state") ? ""
										: json.getString("state");
								if(state.equals(GlobalConstant.APPLSTATE)){
									String mtitle = json.isNull("entity") ? ""
											: json.getString("entity");
									String ntitle = json
											.isNull("event_type") ? ""
													: json.getString("event_type");
									String title = mtitle + " > " + ntitle;
									a.setContent(json.isNull("content") ? ""
											: json.getString("content"));
									a.setTime(json.isNull("date") ? ""
											: json.getString("date"));
									a.setTitle(title);
									a.setContent(json.isNull("remark") ? ""
											: json.getString("remark"));
									a.setAddress(json.isNull("place") ? ""
											: json.getString("place"));
									a.setState(json.isNull("state") ? ""
											: json.getString("state"));
									a.setId(json.isNull("id") ? "" : json
											.getString("id"));
									list.add(a);
								}
							}
						}
					}
					isfirst = false;
					initView();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
