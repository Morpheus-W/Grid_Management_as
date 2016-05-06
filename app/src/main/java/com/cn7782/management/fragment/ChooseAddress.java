package com.cn7782.management.fragment;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.cn7782.management.R;
import com.cn7782.management.android.activity.ChooseAddressActivity;
import com.cn7782.management.android.activity.adapter.ChooseAdapter;
import com.cn7782.management.android.activity.bean.SearchBean;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ChooseAddress extends Fragment {
	private MyListener myListener;
	private List<SearchBean> mlist = new ArrayList<SearchBean>();
	private ListView listview;
	// 搜索功能
	private PoiSearch mPoiSearch = null;
	// public String[] tabTitle = { "全部", "写字楼", "小区", "美食" }; // 标题

	private String address = "";

	private ChooseAdapter adapter;
	private boolean isfirst = true;
	private boolean havestr = false;
	private TextView text;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 初始化搜索模块，注册搜索事件监听

	}

	public interface MyListener {
		public void showMessage(LatLng ld, String str);
	}

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		myListener = (MyListener) activity;
	}

	public void getString(String str) {
		havestr = true;
		this.address = str;
	}

	public void getData(List<SearchBean> list) {
		havestr = true;
		this.mlist = list;
		if (adapter == null)
			return;
		adapter.setData(mlist);
		adapter.notifyDataSetInvalidated();
		text.setVisibility(View.GONE);
		// if (havestr) {
		// adapter = new ChooseAdapter(getActivity(), mlist);
		// listview.setAdapter(adapter);
		// text.setVisibility(View.GONE);
		// listview.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// // TODO Auto-generated method stub
		// if (mlist.get(position).getContent().equals("不好意思，没有结果"))
		// return;
		// myListener.showMessage(mlist.get(position).getLt(), mlist
		// .get(position).getContent());
		// // listview.setSelector(position);
		// }
		// });
		// }
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.mypatrol_layout, container,
				false);
		listview = (ListView) rootView.findViewById(R.id.choose_list);
		text = (TextView) rootView.findViewById(R.id.text);
		// {
		adapter = new ChooseAdapter(getActivity(), mlist);
		listview.setAdapter(adapter);
		if (havestr)
			text.setVisibility(View.GONE);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (mlist.get(position).getContent().equals("不好意思，没有结果"))
					return;
				myListener.showMessage(mlist.get(position).getLt(),
						mlist.get(position).getContent());
				// listview.setSelector(position);

			}
		});
		// }

		return rootView;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

}
