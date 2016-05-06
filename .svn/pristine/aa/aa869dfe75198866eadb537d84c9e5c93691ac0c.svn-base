package com.cn7782.management.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.util.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;

public class HistoryListActivity extends BaseActivity {
	private ListView listview;
	private HistoryListdapter historydapter;
	private List<String> list;
	private EditText editText;
	public static final int DESCRIPTION_RETURNQ = 20;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_history_list);

		getStringData();

	}

	private void initView() {
		listview = (ListView) findViewById(R.id.history_list);
		historydapter = new HistoryListdapter(this, list);
		listview.setAdapter(historydapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String a = list.get(position);
				Intent data = new Intent();
				Bundle b = new Bundle();
				b.putString("history", a);
				data.putExtras(b);
				setResult(DESCRIPTION_RETURNQ, data);
				SaveData(a);
				finish();
			}
		});
		findViewById(R.id.title_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void getStringData() {
		editText = (EditText) findViewById(R.id.edit_submit);
		findViewById(R.id.notice_submit).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						String a = editText.getText().toString();
						Intent data = new Intent();
						Bundle b = new Bundle();
						b.putString("history", a);
						data.putExtras(b);
						setResult(DESCRIPTION_RETURNQ, data);
						SaveData(a);
						finish();
					}
				});
		if (list == null)
			list = new ArrayList<String>();

		String HistoryStr = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE,
				PreferenceConstant.EVENTREPORT, HistoryListActivity.this);
		if (TextUtils.isEmpty(HistoryStr))
			return;
		String str[] = HistoryStr.split("/");
		int count = HistoryStr.split("/").length;
		for (int i = 0; i < count; i++) {
			list.add(str[i]);
		}
		initView();
	}

	// 保存每段话后面添加“/”
	private void SaveData(String str) {
		//保存前查找是否存在该备注
		if(isExist(str)){
			return ;
		}
		String data = "";

		if (!TextUtils.isEmpty(str)) {
			data = str + "/";
			try {
				for (int i = 0; i < 4; i++) {
					data = data + list.get(i) + "/";
				}
			} catch (Exception e) {

			}
		} else {
			try {
				for (int i = 0; i < 4; i++) {
					data = data + list.get(i) + "/";
				}
			} catch (Exception e) {

			}
		}
		SharedPreferenceUtil.modify(PreferenceConstant.MARK_ID_TABLE,
				PreferenceConstant.EVENTREPORT, data, HistoryListActivity.this);

	}
	private boolean isExist(String str){
		boolean flag = false;
		if(list != null && list.size()>0){
			if(list.contains(str)){
				flag = true;
			}
		}
		return flag;
	}
	class HistoryListdapter extends BaseAdapter {
		private List<String> mlist;
		private Context mcontext;

		public HistoryListdapter(Context context, List<String> list) {
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
				convertView = inflater.inflate(R.layout.blank_history_adapter,
						null);
				holder.head_title = (TextView) convertView
						.findViewById(R.id.blank_string);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.head_title.setText(mlist.get(position));
			return convertView;
		}

		class ViewHolder {

			TextView head_title;
		}
	}

}
