package com.cn7782.management.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.activity.adapter.BlankAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectNoticeTypeActivity extends BaseActivity {
	private ListView listview;
	private List<String> list;
	private BlankAdapter blankadapter;
	public static final int NOTICETYPE = 1001;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_type);
		list = new ArrayList<String>();
		list.add("通知");
		list.add("行政公告");
		listview = (ListView) findViewById(R.id.select_type_list);
		blankadapter = new BlankAdapter(this, list);
		listview.setAdapter(blankadapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent data = new Intent();
				Bundle b = new Bundle();
				b.putString("name", list.get(position));
				data.putExtras(b);
				setResult(NOTICETYPE, data);
				finish();
			}
		});
		findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

}
