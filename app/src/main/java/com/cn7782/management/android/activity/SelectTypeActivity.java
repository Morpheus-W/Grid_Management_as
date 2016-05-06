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

public class SelectTypeActivity extends BaseActivity implements OnClickListener {
	private ListView listview;
	private List<String> list;
	private BlankAdapter blankadapter;
	public static final int EVENT_TYPE_RETURNQ = 20;
	public static final int EVENT_TYPE_RETURNA = 30;
	private int type;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_type);
		type = getIntent().getIntExtra("event_type", 1);
		list = new ArrayList<String>();
		listview = (ListView) findViewById(R.id.select_type_list);
		list = initdata(type);
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
				b.putString("eventType", getType(list.get(position)));
				data.putExtras(b);
				if (type == 0) {
					setResult(EVENT_TYPE_RETURNQ, data);
				} else {
					setResult(EVENT_TYPE_RETURNA, data);
				}
				finish();

			}
		});
		findViewById(R.id.cancel).setOnClickListener(this);
		findViewById(R.id.top_empty).setOnClickListener(this);
	}
	@Override
	public void onClick(View view){
		switch (view.getId()){
			case R.id.cancel:
			case R.id.top_empty:
				finish();
				break;
		}
	}
	private List<String> initdata(int type) {

		List<String> eventtypelist = new ArrayList<String>();
		if (type == EventReportActivity.EVENT_TYPE_EVENT) {
			eventtypelist.add("入室盗窃");
			eventtypelist.add("抢劫抢夺");
			eventtypelist.add("电信诈骗");
			eventtypelist.add("拎包扒窃");
			eventtypelist.add("偷查机动车");
			eventtypelist.add("卖淫嫖娼");
			eventtypelist.add("聚众赌博和地下六合彩");
			eventtypelist.add("拐卖妇女儿童");
			eventtypelist.add("制假贩假");
			eventtypelist.add("制黄贩黄");
			eventtypelist.add("高利贷");
			eventtypelist.add("强买强卖");
			eventtypelist.add("交通秩序混乱");
			eventtypelist.add("吸毒、贩毒");
			eventtypelist.add("黑恶势力");
			eventtypelist.add("其他");
		} else if (type == EventReportActivity.EVENT_TYPE_DISPUTES) {
			eventtypelist.add("婚姻纠纷");
			eventtypelist.add("邻里纠纷");
			eventtypelist.add("环境生态");
			eventtypelist.add("建筑工程");
			eventtypelist.add("物业管理");
			eventtypelist.add("企业改制");
			eventtypelist.add("司法活动");
			eventtypelist.add("干部作风");
			eventtypelist.add("劳资纠纷");
			eventtypelist.add("征地拆迁");
			eventtypelist.add("军人安置");
			eventtypelist.add("房屋和宅基地");
			eventtypelist.add("农村三资");
			eventtypelist.add("农民负担");
			eventtypelist.add("院校问题");
			eventtypelist.add("民族宗教");
			eventtypelist.add("经济活动");
			eventtypelist.add("山林土地");
			eventtypelist.add("医疗纠纷");
			eventtypelist.add("村（社区）务管理");
			eventtypelist.add("行政执法活动");
			eventtypelist.add("海事渔事");
			eventtypelist.add("交通及生产安全");
			eventtypelist.add("计划生育");
			eventtypelist.add("合同履行");
			eventtypelist.add("库区移民安置");
			eventtypelist.add("其他");
		} else if (type == EventReportActivity.EVENT_TYPE_ROAD) {
			eventtypelist.add("铁路");
			eventtypelist.add("公路");
			eventtypelist.add("水路");
			eventtypelist.add("电力设施");
			eventtypelist.add("电信设施");
			eventtypelist.add("广播电视设施");
			eventtypelist.add("输油气管道");
		} else if (type == 0) {
			eventtypelist.add("社会治安及重点地区整治");
			eventtypelist.add("矛盾纠纷排查");
			eventtypelist.add("线路案(事)件");
		}
		return eventtypelist;
	}

	private String getType(String str) {
		String name = "";
		if (str.equals("入室盗窃")) {
			name = "1";
		} else if (str.equals("抢劫抢夺")) {
			name = "2";
		} else if (str.equals("电信诈骗")) {
			name = "3";
		} else if (str.equals("拎包扒窃")) {
			name = "4";
		} else if (str.equals("偷查机动车")) {
			name = "5";
		} else if (str.equals("卖淫嫖娼")) {
			name = "6";
		} else if (str.equals("聚众赌博和地下六合彩")) {
			name = "7";
		} else if (str.equals("拐卖妇女儿童")) {
			name = "8";
		} else if (str.equals("制假贩假")) {
			name = "9";
		} else if (str.equals("制黄贩黄")) {
			name = "10";
		} else if (str.equals("高利贷")) {
			name = "11";
		} else if (str.equals("强买强卖")) {
			name = "12";
		} else if (str.equals("交通秩序混乱")) {
			name = "13";
		} else if (str.equals("吸毒、贩毒")) {
			name = "14";
		} else if (str.equals("黑恶势力")) {
			name = "15";
		} else if (str.equals("其他")) {
			name = "16";
		} else if (str.equals("婚姻纠纷")) {
			name = "1";
		} else if (str.equals("邻里纠纷")) {
			name = "2";
		} else if (str.equals("环境生态")) {
			name = "3";
		} else if (str.equals("建筑工程")) {
			name = "4";
		} else if (str.equals("物业管理")) {
			name = "5";
		} else if (str.equals("企业改制")) {
			name = "6";
		} else if (str.equals("司法活动")) {
			name = "7";
		} else if (str.equals("干部作风")) {
			name = "8";
		} else if (str.equals("劳资纠纷")) {
			name = "9";
		} else if (str.equals("征地拆迁")) {
			name = "10";
		} else if (str.equals("军人安置")) {
			name = "11";
		} else if (str.equals("房屋和宅基地")) {
			name = "12";
		} else if (str.equals("农村三资")) {
			name = "13";
		} else if (str.equals("农民负担")) {
			name = "14";
		} else if (str.equals("院校问题")) {
			name = "15";
		} else if (str.equals("民族宗教")) {
			name = "16";
		} else if (str.equals("经济活动")) {
			name = "17";
		} else if (str.equals("山林土地")) {
			name = "18";
		} else if (str.equals("医疗纠纷")) {
			name = "19";
		} else if (str.equals("村（社区）务管理")) {
			name = "20";
		} else if (str.equals("行政执法活动")) {
			name = "21";
		} else if (str.equals("海事渔事")) {
			name = "22";
		} else if (str.equals("交通及生产安全")) {
			name = "23";
		} else if (str.equals("计划生育")) {
			name = "24";
		} else if (str.equals("合同履行")) {
			name = "25";
		} else if (str.equals("库区移民安置")) {
			name = "26";
		} else if (str.equals("其他")) {
			name = "27";
		} else if (str.equals("铁路")) {
			name = "1";
		} else if (str.equals("公路")) {
			name = "2";
		} else if (str.equals("水路")) {
			name = "3";
		} else if (str.equals("电力设施")) {
			name = "4";
		} else if (str.equals("电信设施")) {
			name = "5";
		} else if (str.equals("广播电视设施")) {
			name = "6";
		} else if (str.equals("输油气管道")) {
			name = "99";
		} else {
			name = "";
		}
		return name;
	}
}
