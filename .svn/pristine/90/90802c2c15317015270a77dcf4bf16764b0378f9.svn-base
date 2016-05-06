package com.cn7782.management.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;

public class DescriptionActivity extends BaseActivity {
	private EditText edittext;
	public static final int DESCRIPTION_RETURNQ = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_description);
		edittext = (EditText) findViewById(R.id.notice_edit_submit);
		findViewById(R.id.notice_submit).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						String a = edittext.getText().toString();
						Intent data = new Intent();
						Bundle b = new Bundle();
						b.putString("name", a);
						data.putExtras(b);
						setResult(DESCRIPTION_RETURNQ, data);
						finish();
					}
				});
	}
}
