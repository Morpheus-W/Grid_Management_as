package com.cn7782.management.android.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.activity.bean.LocationBean;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.android.db.DBHelper;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.http.HttpClient;
import com.cn7782.management.http.MyAsyncHttpResponseHandler;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.cn7782.management.util.TrackSumbitUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UploadedOfflineActivity extends BaseActivity {

	private UploadAdapter uploadAdapter;
	private Cursor cursor;
	private DBHelper dp;
	private List<LocationBean> list;
	private List<LocationBean> datalist;
	private String tablename;
	private String eventcon, eventid, starttime, endtime, duration;
	//发送起始位置，终点位置
	private String start_point = "", end_point = "";

	private double distanceLong = 0.0;
	// 记录走路路长和开车路长
	private int walking_road, driving_road;
	// 记录走路时长和开车时长
	private int driving_dur, walking_dur;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_upload_offline);
		initView();
	}

	private void initView() {
		findViewById(R.id.title_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		if (list == null)
			list = new ArrayList<LocationBean>();
		dp = new DBHelper(this);
		dp.deleteTable(3);
		cursor = dp.query("SELECT * FROM " + DBHelper.TABLENAME, null);
		int count = cursor.getCount();
		if (count == 0)
			return;
		for (int i = 0; i < count; i++) {
			LocationBean a = new LocationBean();
			cursor.moveToPosition(i);
			final String tableName = cursor.getString(0);
			final String describe = cursor.getString(3);
			final String time = cursor.getString(4);
			a.setTableName(tableName);
			a.setDescribe(describe);
			a.setTime(time);
			list.add(a);
		}
		uploadAdapter = new UploadAdapter(this, list);
		((ListView) findViewById(R.id.upload_list)).setAdapter(uploadAdapter);
	}

	private void refresh() {
		cursor = null;
		cursor = dp.query("SELECT * FROM " + DBHelper.TABLENAME
				+ " WHERE Artificial = 2", null);
		list.clear();
		int count = cursor.getCount();
		for (int i = 0; i < count; i++) {
			LocationBean a = new LocationBean();
			cursor.moveToPosition(i);
			final String tableName = cursor.getString(0);
			final String describe = cursor.getString(3);
			final String time = cursor.getString(4);
			a.setTableName(tableName);
			a.setDescribe(describe);
			a.setTime(time);
			list.add(a);
		}
		uploadAdapter.setdata(list);
		uploadAdapter.notifyDataSetInvalidated();
	}

	private void getdate(String name) {
		// address, event, starttime, endtime;
		Cursor cursor = null;
		cursor = dp.query("SELECT * FROM " + DBHelper.TABLENAME
				+ " WHERE tablename= " + "\"" + name + "\"", null);
		if (cursor.getCount() == 0)
			return;
		cursor.moveToPosition(0);
		eventid = cursor.getString(2);
		eventcon = cursor.getString(3);
		starttime = cursor.getString(4);
		endtime = cursor.getString(5);
		duration = cursor.getString(6);
	}

	private void updata(final String name) {
		if (datalist == null)
			datalist = new ArrayList<LocationBean>();
		datalist.clear();
		getdate(name);
		cursor = null;
		cursor = dp.query("SELECT * FROM " + name, null);
		List<LatLng> pts = new ArrayList<LatLng>();
		LocationBean a = null;
		double speedlist[] = new double[cursor.getCount()];
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			String time = cursor.getString(1);
			double speed = cursor.getDouble(2);
			double Latitude = cursor.getDouble(3);
			double Longitude = cursor.getDouble(4);
			int pathType = cursor.getInt(5);
			LatLng pt = new LatLng(Latitude, Longitude);
			pts.add(pt);
			speedlist[i] = speed;
			a = new LocationBean();
			a.setLatitude(Latitude);
			a.setLongitude(Longitude);
			a.setSpeed(speed);
			a.setTime(time);
			a.setPathType(pathType);
			if(i == 0){
				start_point = Longitude + "," + Latitude;
			}
			datalist.add(a);
		}
		end_point = a.getLongitude()+","+a.getLatitude();
		
		countDiatance(pts, speedlist);
		String jsondata = toJson2(datalist);
		RequestParams param = new RequestParams();
		String tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				UploadedOfflineActivity.this);
		param.put("token_id", tokenId);
		param.put("json", jsondata);
		HttpClient.post(UploadedOfflineActivity.this, ActionUrl.TRAFECTORY,
				param, new MyAsyncHttpResponseHandler(
						UploadedOfflineActivity.this, "上传中,请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("保存成功")) {
									Toast.makeText(
											UploadedOfflineActivity.this,
											"上传成功了", Toast.LENGTH_SHORT).show();
									dp.dropTable(name);
									dp.updateTable(3,name);

									refresh();
								} else {
									Toast.makeText(
											UploadedOfflineActivity.this,
											"上传失败", Toast.LENGTH_SHORT).show();
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

	private void countDiatance(List<LatLng> pts, double speed[]) {
		double abc = 0.0;
		for (int i = 0; i < pts.size() - 1; i++) {
			abc = abc + DistanceUtil.getDistance(pts.get(i), pts.get(i + 1));
			if (speed[i] > 2.0) {
				driving_road = driving_road
						+ (int) DistanceUtil.getDistance(pts.get(i),
								pts.get(i + 1));
				driving_dur = driving_dur + 1;
			} else {
				walking_road = walking_road
						+ (int) DistanceUtil.getDistance(pts.get(i),
								pts.get(i + 1));
				walking_dur = walking_dur + 1;
			}
		}
		distanceLong = distanceLong + abc;

	}
	public String toJson2(List<LocationBean> list) {
		String grid_id = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.GRID_ID,
				UploadedOfflineActivity.this);
		int dur = 0;
		try {
			dur = Integer.valueOf(duration).intValue();
		} catch (Exception e) {

		}
		String[] result = TrackSumbitUtil.getParamStr(list);
		StringBuffer paramsResult = new StringBuffer("{");
		
		paramsResult.append("'startStrTime':"+ "'" + starttime + "',");
		paramsResult.append("'endStrTime':"+ "'" + endtime + "',");
		paramsResult.append("'start_place':"+ "'" + start_point + "',");
		paramsResult.append("'end_place':"+ "'" + end_point + "',");
		paramsResult.append("'length':"+ "'" + result[1] + "',");
		paramsResult.append("'duration':"+ "'" + dur + "',");
		paramsResult.append("'remark':"+ "'" + eventcon + "',");
		paramsResult.append("'walking_road':"+ "'" + walking_road + "',");
		paramsResult.append("'walking_dur':"+ "'" + walking_dur + "',");
		paramsResult.append("'driving_road':"+ "'" + driving_road + "',");
		paramsResult.append("'driving_dur':"+ "'" + driving_dur + "',");
		paramsResult.append("'grid_id':"+ "'" + grid_id + "',");
		paramsResult.append("'event_id':"+ "'" + eventid + "',");
		
		String str = result[0];
		str = str.replaceAll("\"", "'");
		paramsResult.append("'pathList':"+ str);
		paramsResult.append("}");
		
		Log.i("record", paramsResult.toString());
		return paramsResult.toString();
	}
	class UploadAdapter extends BaseAdapter {
		private Context mcontext;
		private List<LocationBean> mlist;

		public UploadAdapter(Context context, List<LocationBean> list) {
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

		public void setdata(List<LocationBean> list) {
			this.mlist = list;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				final LayoutInflater inflater = LayoutInflater.from(mcontext);
				convertView = inflater.inflate(R.layout.upload_adapter, null);
				holder.upload_title = (TextView) convertView
						.findViewById(R.id.upload_title);
				holder.upload_time = (TextView) convertView
						.findViewById(R.id.upload_time);
				holder.upload_button = (Button) convertView
						.findViewById(R.id.upload_button);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.upload_title.setText(mlist.get(position).getDescribe());
			holder.upload_time.setText(mlist.get(position).getTime());
			holder.upload_button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					tablename = mlist.get(position).getTableName();
					updata(tablename);
				}
			});
			return convertView;
		}

		class ViewHolder {

			TextView upload_title, upload_time;
			Button upload_button;
		}
	}
//	@Override
//	protected void onDestroy() {
//		if(dp != null)
//			dp.close();
//		if(cursor != null)
//			cursor.close();
//
//		super.onDestroy();
//	}
	
}
