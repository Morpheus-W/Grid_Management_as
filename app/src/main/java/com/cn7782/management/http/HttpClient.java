package com.cn7782.management.http;

import android.content.Context;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpClient {
	private static AsyncHttpClient client = new AsyncHttpClient();

	public static AsyncHttpClient httpClient;

	public static void post(Context context, String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		if (params == null)
			params = new RequestParams();
		//请求超时，默认为10s，这里设置为8s
		client.setTimeout(8 * 1000);

		client.addHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");

		client.addHeader("Cookie", "JSESSIONID=" + "");
		client.setUserAgent("Android");
		client.post(url, params, responseHandler);

	}

	/**
	 * 单例模式*上传文件
	 * 
	 * @return
	 */
	public synchronized static AsyncHttpClient getInstance() {
		if (null == httpClient) {

			httpClient = new AsyncHttpClient();

		}

		return httpClient;
	}

}
