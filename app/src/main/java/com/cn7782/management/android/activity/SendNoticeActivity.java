package com.cn7782.management.android.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.util.ActivityUtil;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.cn7782.management.view.ToggleButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendNoticeActivity extends BaseActivity {
	private String PhotoPath;
	// 添加图片布局
	private LinearLayout mAddPhotoLayout;
	private List<File> fileurl = null;
	private EditText edittitle, editcontent;
	private ProgressDialog progressDialog;
	private ToggleButton button;
	private String type = "0";

	// private PopupWindow popup;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_send_notice);
		initview();
		findViewById(R.id.title_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void initview() {
		findViewById(R.id.add_type).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SendNoticeActivity.this,
						SelectNoticeTypeActivity.class);
				startActivityForResult(intent, 1);
			}

		});
		progressDialog = new ProgressDialog(this);
		edittitle = (EditText) findViewById(R.id.notice_title);
		editcontent = (EditText) findViewById(R.id.notice_content);
		button = (ToggleButton) findViewById(R.id.notice_button);

		findViewById(R.id.take_photo).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ActivityUtil.startCamera(SendNoticeActivity.this);
			}
		});
		findViewById(R.id.login_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						try {
							submit();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			switch (resultCode) {
			case SelectNoticeTypeActivity.NOTICETYPE:
				Bundle b = data.getExtras();
				String name = b.getString("name");
				type = b.getString("type");
				if (name.equals("通知")) {
					type = "2";
				} else {
					type = "1";
				}
				((TextView) findViewById(R.id.address)).setText(name);

				break;
			}
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
				// fileurl.add(ActivityUtil.mPhotoFile);
				PhotoPath = ActivityUtil.mPhotoFile.getAbsolutePath();
				try {
					addPhoto(PhotoPath);
				} catch (Exception e) {
					Toast.makeText(this, "找不到照片", Toast.LENGTH_LONG).show();
				}
			}
		}
			break;

		}
	}

	// 动态添加一个photo到水平滚动条
	private void addPhoto(String filePath) {
		if (null == mAddPhotoLayout) {
			mAddPhotoLayout = (LinearLayout) findViewById(R.id.attachment_list);
		}
		// 限制8张图片 防止内存溢出
		if (mAddPhotoLayout.getChildCount() > 7) {
			Toast.makeText(this, "照片限制为8张", Toast.LENGTH_LONG).show();
			return;
		}
		// 整体布局,由2个ImageView叠加而成
		final View addPhoto = LayoutInflater.from(this).inflate(
				R.layout.addphoto_layout_view, null);
		addPhoto.setTag(filePath);

		// 获取的图片信息
		ImageView content = (ImageView) addPhoto
				.findViewById(R.id.kcool_addphoto_photo);
		Bitmap photo = ActivityUtil.getBitmap(filePath);

		if (null != photo) {
			content.setImageBitmap(photo);
		} else {
			// 其他文件显示
		}

		// 删除图片按钮
		ImageView remover = (ImageView) addPhoto
				.findViewById(R.id.kcool_addphoto_remover);
		remover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mAddPhotoLayout.removeView(addPhoto);
				return;
			}
		});

		// 动态添加到水平滚动条上
		LinearLayout.LayoutParams lp = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(8, 0, 8, 0);
		addPhoto.setLayoutParams(lp);
		mAddPhotoLayout.addView(addPhoto);
		return;
	}

	private void submit() throws FileNotFoundException {
		GetViewPhoto();
		Map<String, String> mapParams = new HashMap<String, String>();
		String tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				SendNoticeActivity.this);
		String title = edittitle.getText().toString();
		if (TextUtils.isEmpty(title)) {
			Toast.makeText(this, "标题不能为空", Toast.LENGTH_LONG).show();
			return;
		}
		String content = editcontent.getText().toString();
		if (TextUtils.isEmpty(content)) {
			Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		String top = "2";
		boolean abc = button.getCheck();
		if (abc) {
			top = "1";
		} else {
			top = "2";
		}
		if (type.equals("0")) {
			Toast.makeText(this, "类型不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		mapParams.put("token_id", tokenId);
		mapParams.put("title", title);
		mapParams.put("content", content);
		mapParams.put("top", top);
		mapParams.put("type", type);
		RequestParams param = new RequestParams(mapParams);
		if (fileurl != null) {
			for (int i = 0; i < fileurl.size(); i++) {
				int a = i + 1;

				param.put("file" + a, fileurl.get(i));
			}
		}
		showProgressDialog("发帖中");
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.setTimeout(380 * 1000);
		httpClient.post(ActionUrl.SENDNOTICE, param,
				new JsonHttpResponseHandler() {

					public void onSuccess(int statusCode, JSONObject response) {
						super.onSuccess(statusCode, response);
						Toast.makeText(SendNoticeActivity.this, "添加成功!",
								Toast.LENGTH_LONG).show();
						progressDialog.cancel();
						finish();
					}

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						progressDialog.cancel();
						super.onFailure(arg0, arg1);
					}
				});
	}

	// 从滑动View获取照片文件
	public void GetViewPhoto() {
		if (fileurl == null) {
			fileurl = new ArrayList<File>();
		}
		if (mAddPhotoLayout != null) {
			fileurl.clear();
			if (mAddPhotoLayout.getChildCount() > 0) {
				for (int i = 0; i < mAddPhotoLayout.getChildCount(); i++) {
					View photo = mAddPhotoLayout.getChildAt(i);
					String file = (String) photo.getTag();
					File files = new File(file);
					if (files.exists()) {
						fileurl.add(files);
					}
				}
			}
		}
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

}
