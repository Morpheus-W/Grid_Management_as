package com.cn7782.management.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.http.HttpClient;
import com.cn7782.management.http.MyAsyncHttpResponseHandler;
import com.cn7782.management.util.ActivityUtil;
import com.cn7782.management.util.PictureUtil;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyInformationActivity extends BaseActivity implements OnClickListener {
	private EditText address, telephone, mobile_phone, remarks;
	private ImageView infohead;

	private ProgressDialog progressDialog;
	private File recordFile = null;
	private String tokenId;
	private TextView tick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_information);

		initView();

		getdata();

		findViewById(R.id.title_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initView() {
		ImageLoader.getInstance().init(
				ImageLoaderConfiguration.createDefault(this));
		tick = (TextView) findViewById(R.id.title_tick);
		tick.setOnClickListener(this);
		infohead = (ImageView) findViewById(R.id.info_head);
		infohead.setOnClickListener(this);
		address = (EditText) findViewById(R.id.address);
		telephone = (EditText) findViewById(R.id.telephone);
		mobile_phone = (EditText) findViewById(R.id.mobile_phone);
		remarks = (EditText) findViewById(R.id.remarks);
	}

	private void getdata() {
		tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				MyInformationActivity.this);
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		HttpClient.post(MyInformationActivity.this, ActionUrl.INFO, param,
				new MyAsyncHttpResponseHandler(MyInformationActivity.this,
						"请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");

								if (msg.equals("查询成功")) {
									JSONObject jo1 = jsonObject
											.getJSONObject("return_info");
									String userName = jo1.isNull("userName") ? ""
											: jo1.getString("userName");
									String phone = jo1.isNull("phone") ? ""
											: jo1.getString("phone");
									String mobile = jo1.isNull("mobile") ? ""
											: jo1.getString("mobile");
									String remark = jo1.isNull("remark") ? ""
											: jo1.getString("remark");
									String email = jo1.isNull("email") ? ""
											: jo1.getString("email");
									String userPhoto = jo1.isNull("userPhoto") ? ""
											: jo1.getString("userPhoto");
									String createDate = jo1
											.isNull("createDate") ? "" : jo1
											.getString("createDate");
									String officeName = jo1
											.isNull("officeName") ? "" : jo1
											.getString("officeName");
									String area = jo1.isNull("area") ? "" : jo1
											.getString("area");
									String userRole = jo1.isNull("userRole") ? ""
											: jo1.getString("userRole");
									String gridNames = jo1.isNull("gridNames") ? ""
											: jo1.getString("gridNames");
									((TextView) findViewById(R.id.user_name))
											.setText(userName);
									((TextView) findViewById(R.id.office_name))
											.setText(officeName + "/"
													+ userRole);
									((TextView) findViewById(R.id.city))
											.setText(area + "/" + gridNames);
									((TextView) findViewById(R.id.last_time))
											.setText(createDate);
									address.setText(email);
									telephone.setText(phone);
									mobile_phone.setText(mobile);
									remarks.setText(remark);
									PictureUtil.ShowPicture(infohead,
											MyInformationActivity.this,
											userPhoto);

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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.info_head:
			ActivityUtil.startCamera(MyInformationActivity.this);
			break;
		case R.id.title_tick:
			if(checkInfo())
				uploaddata();
			break;
		}
	}

	private boolean checkInfo() {
		String email = address.getText().toString();
		String phone = mobile_phone.getText().toString();
		if(!"".equals(email)&&!isEmail(email)){
			Toast.makeText(MyInformationActivity.this, "邮箱格式错误!",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if(!"".equals(phone)&&!isMobileNO(phone)){
			Toast.makeText(MyInformationActivity.this, "手机格式错误!",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
		}
		switch (requestCode) {
		case ActivityUtil.TAKE_PHOTO_USING_CAMERA:
			try {
				// 启动gallery剪辑照片，否则极容易出现OOM(内存溢出)
				final Intent intent = ActivityUtil.getCropImageIntent(
						Uri.fromFile(ActivityUtil.mPhotoFile), 600, 600);
				startActivityForResult(intent,
						ActivityUtil.CROP_PHOTO_BY_SYSTEM);
			} catch (Exception e) {
				Toast.makeText(this, "找不到可以裁剪照片的应用", Toast.LENGTH_LONG).show();
			}
			break;
		case ActivityUtil.CROP_PHOTO_BY_SYSTEM: {
			if (null != ActivityUtil.mPhotoFile
					&& ActivityUtil.mPhotoFile.exists()) {
				recordFile = ActivityUtil.mPhotoFile;
				Bitmap bitmap = BitmapFactory.decodeFile(recordFile.getPath());
				infohead.setImageBitmap(ActivityUtil.toRoundBitmap(bitmap));
				try {
					Upheadphoto();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
			break;
		}

	}

	private void Upheadphoto() throws FileNotFoundException {

		RequestParams param = new RequestParams();
		if (recordFile != null) {
			if (recordFile.exists()) {
				param.put("file", recordFile);
			}
		}
		param.put("token_id", tokenId);
		showProgressDialog("上传头像中");
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.setTimeout(15 * 1000);
		httpClient.post(ActionUrl.UPDATEPHOTO, param,
				new JsonHttpResponseHandler() {

					public void onSuccess(int statusCode, JSONObject response) {
						super.onSuccess(statusCode, response);
						try {
							String msg = response.getString("msg");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Toast.makeText(MyInformationActivity.this, "上传成功!",
								Toast.LENGTH_SHORT).show();
						progressDialog.cancel();

					}

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						progressDialog.cancel();
						super.onFailure(arg0, arg1);
					}
				});
	}

	/***
	 * 
	 * @param message
	 */
	public void showProgressDialog(String message) {
		if (null == progressDialog) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setCanceledOnTouchOutside(false);
		}

		progressDialog.setMessage(message);
		progressDialog.setCancelable(false);
		progressDialog.show();

	}

	private void uploaddata() {
		RequestParams param = new RequestParams();
		param.put("email", address.getText().toString());
		param.put("phone", telephone.getText().toString());
		param.put("mobile", mobile_phone.getText().toString());
		param.put("remarks", remarks.getText().toString());
		param.put("token_id", tokenId);
		AsyncHttpClient httpClient = new AsyncHttpClient();
		showProgressDialog("提交中");
		httpClient.setTimeout(15 * 1000);
		httpClient.post(ActionUrl.CHANGEINFO, param,
				new JsonHttpResponseHandler() {

					public void onSuccess(int statusCode, JSONObject response) {
						super.onSuccess(statusCode, response);

						Toast.makeText(MyInformationActivity.this, "上传成功!",
								Toast.LENGTH_SHORT).show();
						progressDialog.cancel();

					}

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						progressDialog.cancel();
						super.onFailure(arg0, arg1);
					}
				});
	}

	// 判断手机格式是否正确
	public boolean isMobileNO(String mobiles) {
		String str = "^1[3|4|5|7|8][0-9]{9}$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(mobiles);

		return m.matches();
	}

	// 判断email格式是否正确
	public boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);

		return m.matches();
	}
}