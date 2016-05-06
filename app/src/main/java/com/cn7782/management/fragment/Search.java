package com.cn7782.management.fragment;

import java.util.ArrayList;
import java.util.List;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.adapter.SearchAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class Search extends Fragment {
	private SearchAdapter searchadapter;
	private List<String> list = new ArrayList<String>();
	private ListView listview;
	private List<Integer> mlist = new ArrayList<Integer>();
	private List<String> hlist = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initdata();

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.search_fragment, container,
				false);
		listview = (ListView) rootView.findViewById(R.id.search_list);
		//searchadapter = new SearchAdapter(getActivity(), list, mlist, hlist);
		listview.setAdapter(searchadapter);
		return rootView;
	}

	private void initdata() {
		// TODO Auto-generated method stub
		list.add("今天中午吃什么菜好呢？");
		list.add("2014年保险业十大事件");
		list.add("2015年大家新年快乐啊！");
		list.add("今天天气好晴朗");
		list.add("车险一个车型一个价即将执行");
		mlist.add(R.drawable.head_img_1);
		
		hlist.add("小张");
		hlist.add("小明");
		hlist.add("小红");
		hlist.add("小花");
		hlist.add("小海");
	}
}
