package com.cn7782.management.android.activity.bean;

import com.baidu.mapapi.model.LatLng;

public class SearchBean {
	private String title;
	private String content;
	private LatLng lt;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LatLng getLt() {
		return lt;
	}

	public void setLt(LatLng lt) {
		this.lt = lt;
	}

}
