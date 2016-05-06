package com.cn7782.management.android.activity;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.activity.adapter.ExpandableListAdapter;
import com.cn7782.management.android.activity.bean.DepartmentBean;
import com.cn7782.management.android.activity.bean.EmployeeBean;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.android.db.DBHelper;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.http.HttpClient;
import com.cn7782.management.http.MyAsyncHttpResponseHandler;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ContactManagerActivity extends BaseActivity implements OnQueryTextListener,OnClickListener {
	
	
	private DBHelper dp;
	private Cursor cursor;
	private String sql;
	
	private List<DepartmentBean> deplist;
	private List<EmployeeBean> emplist;
	private Map<Integer,List<EmployeeBean>> empAll;
	
	private ExpandableListAdapter depAdapter;
	private ExpandableListView expandableListView;
	private SearchView sv;
	
	private View searchResult;
	private ListView searchList;
	
	private View callANDsms;
	private TextView callTitle;
	//保存电话号码
	private String number = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_contactmanager);
		
		
		dp = new DBHelper(this);
		initView();
		//若不存在则请求查询并保存到数据,存在则直接数据库查询
		if(!dp.isTableExists(DBHelper.DEPARTMENTTABLE)){
			//通过接口获取数据
			getDepartmentData();
		}else{
			initListView();
		}
	}

	private void initView() {
		findViewById(R.id.search_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		expandableListView = (ExpandableListView) findViewById(R.id.contact_list);
		sv = (SearchView) findViewById(R.id.sv);
		// 设置该SearchView默认是否自动缩小为图标
		sv.setIconifiedByDefault(false);
		// 为该SearchView组件设置事件监听器
		sv.setOnQueryTextListener(this);
		// 设置该SearchView显示搜索按钮
		sv.setSubmitButtonEnabled(true);
		// 设置该SearchView内默认显示的提示文本
		sv.setQueryHint("搜索");
		//取消默认获取光标
		sv.setFocusable(false);
		searchResult = findViewById(R.id.search_result);
		searchList = (ListView) findViewById(R.id.result_list);
		//初始化打电话发短信
		callANDsms = findViewById(R.id.callANDsms);
	    findViewById(R.id.call_phone).setOnClickListener(this);
	    findViewById(R.id.send_sms).setOnClickListener(this);
	    findViewById(R.id.callsms_cancel).setOnClickListener(this);
	    callTitle = (TextView) findViewById(R.id.callsms_title);
	    //刷新通讯录
	    findViewById(R.id.refreshContact).setOnClickListener(this);
	}

	private void queryDepartmentList() {
		if(deplist != null)
			//初始化前先清空
			deplist.clear();
		else
			deplist = new ArrayList<DepartmentBean>();
		//日程列表
		sql = "select * from department_table ";
		cursor = dp.query(sql, null);
		if(cursor.moveToNext()){
			int count = cursor.getCount();
			if (count == 0)
				return;
			for (int i = 0; i < count; i++) {
				cursor.moveToPosition(i);
				Integer departmentId = cursor.getInt(0);
				String depName = cursor.getString(1);
				Integer parentId = cursor.getInt(2);
				
				DepartmentBean dep = new DepartmentBean(departmentId,depName,parentId);
				deplist.add(dep);
			}
		}
	}
	//根据部门id查询员工信息
	private void queryEmployeeList(Integer departmentId) {
		if(emplist != null)
			//初始化前先清空
			emplist.clear();
		else
			emplist = new ArrayList<EmployeeBean>();
		//日程列表
		sql = "select * from "+DBHelper.EMPLOYEETABLE+" where officeId = ? ";
		cursor = dp.query(sql, new String[]{departmentId+""});
		if(cursor.moveToNext()){
			int count = cursor.getCount();
			if (count == 0)
				return;
			for (int i = 0; i < count; i++) {
				cursor.moveToPosition(i);
				String contactId = cursor.getString(0);
				Integer officeId = cursor.getInt(1);
				String officeName = cursor.getString(2);
				String parentId = cursor.getString(3);
				String empName = cursor.getString(4);
				String email = cursor.getString(5);
				String fax = cursor.getString(6);
				String homeAddr = cursor.getString(7);
				String mobile = cursor.getString(8);
				String mobile2 = cursor.getString(9);
				String officeAddr = cursor.getString(10);
				String remark = cursor.getString(11);
				
				EmployeeBean emp = new EmployeeBean(contactId,officeId,officeName,
						parentId,empName,email,fax,homeAddr,mobile,mobile2,officeAddr,remark);
				emplist.add(emp);
			}
		}
	}

	private void getDepartmentData() {
		// TODO 获取通讯录数据
		String tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				ContactManagerActivity.this);
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		HttpClient.post(ContactManagerActivity.this, ActionUrl.DEPARTMENT, param,
				new MyAsyncHttpResponseHandler(ContactManagerActivity.this,
						"请稍后...") {
					public void onSuccess(String results) {
						super.onSuccess(results);
						try {
							JSONObject jsonObject = new JSONObject(results);
							if (jsonObject.has("msg")) {
								String msg = jsonObject.isNull("msg") ? ""
										: jsonObject.getString("msg");
								if (msg.equals("查询成功")) {
									JSONArray jo1 = jsonObject
											.getJSONArray("office");
									if (deplist == null) {
										deplist = new ArrayList<DepartmentBean>();
									}
									deplist.clear();
									for (int i = 0; i < jo1.length(); i++) {
										JSONObject json = jo1.getJSONObject(i);
										DepartmentBean dep = new DepartmentBean();
										dep.setDepartmentId(json.isNull("id") ? 0
												: json.getInt("id"));
										
										dep.setDepName(json.isNull("name") ? ""
												: json.getString("name"));
										
										deplist.add(dep);
									}
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						getEmployeeData();
					}

					@Override
					public void onFailure(Throwable arg0, String tipInfo) {
						super.onFailure(arg0,tipInfo);
					}
					
				});
	}
	private void getEmployeeData() {
		// TODO 获取通讯录数据
		String tokenId = SharedPreferenceUtil.getValue(
				PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
				ContactManagerActivity.this);
		RequestParams param = new RequestParams();
		param.put("token_id", tokenId);
		HttpClient.post(ContactManagerActivity.this, ActionUrl.EMPLOYEE, param,
				new MyAsyncHttpResponseHandler(ContactManagerActivity.this,
						"请稍后...") {
			public void onSuccess(String results) {
				super.onSuccess(results);
				try {
					JSONObject jsonObject = new JSONObject(results);
					if (jsonObject.has("msg")) {
						String msg = jsonObject.isNull("msg") ? ""
								: jsonObject.getString("msg");
						if (msg.equals("查询成功")) {
							JSONArray jo1 = jsonObject
									.getJSONArray("phonebook");
							if (emplist == null) {
								emplist = new ArrayList<EmployeeBean>();
							}
							emplist.clear();
							for (int i = 0; i < jo1.length(); i++) {
								JSONObject json = jo1.getJSONObject(i);
								EmployeeBean emp = new EmployeeBean();
								
								emp.setContactId(json.isNull("id") ? ""
										: json.getString("id"));
								emp.setOfficeId(json.isNull("officeId") ? 0
										: json.getInt("officeId"));
								emp.setEmpName(json.isNull("name") ? ""
										: json.getString("name"));
								emp.setOfficeName(json.isNull("officeName") ? ""
										: json.getString("officeName"));
								emp.setEmail(json.isNull("email") ? ""
										: json.getString("email"));
								emp.setFax(json.isNull("fax") ? ""
										: json.getString("fax"));
								emp.setHomeAddr(json.isNull("homeFax") ? ""
										: json.getString("homeFax"));
								emp.setMobile(json.isNull("mobile1") ? ""
										: json.getString("mobile1"));
								emp.setMobile2(json.isNull("mobile2") ? ""
										: json.getString("mobile2"));
								emp.setOfficeAddr(json.isNull("officeAddr") ? ""
										: json.getString("officeAddr"));
								emp.setRemark(json.isNull("remark") ? ""
										: json.getString("remark"));
								emp.setOfficeTel(json.isNull("officeTel") ? ""
										: json.getString("officeTel"));
								
								emplist.add(emp);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				saveData2db();
			}
		});
	}
	private void saveData2db() {
		// TODO 保存到数据库
		dp.creatDepartmentTable();
		dp.creatEmployeeTable();

		ArrayList<ContentValues> depArr = new ArrayList<>();
		ArrayList<ContentValues> empArr = new ArrayList<>();

		if(deplist != null){
			ContentValues cv ;
			for(DepartmentBean dep : deplist){
				cv = new ContentValues();
				cv.put("departmentId", dep.getDepartmentId());
				cv.put("depName", dep.getDepName());
				depArr.add(cv);
			}
			dp.bulkInsert(DBHelper.DEPARTMENTTABLE,depArr);
		}
		if(emplist != null){
			ContentValues cv ;
			for(EmployeeBean emp : emplist){
				cv = new ContentValues();
				cv.put("contactId", emp.getContactId());
				cv.put("officeId", emp.getOfficeId());
				cv.put("officeName", emp.getOfficeName());
				cv.put("empName", emp.getEmpName());
				cv.put("email", emp.getEmail());
				cv.put("fax", emp.getFax());
				cv.put("homeAddr", emp.getHomeAddr());
				cv.put("mobile", emp.getMobile());
				cv.put("mobile2", emp.getMobile2());
				cv.put("officeAddr", emp.getOfficeAddr());
				cv.put("remark", emp.getRemark());
				cv.put("officeTel", emp.getOfficeTel());
				empArr.add(cv);
			}
			dp.bulkInsert(DBHelper.EMPLOYEETABLE,empArr);
		}
		initListView();
	}

	private void initListView() {
		//查询所有部门信息
		queryDepartmentList();
		//查询所有员工信息，二维数组
		queryEmployeeListAll();
		depAdapter = new ExpandableListAdapter(this,deplist,empAll);
		//去除自定义的箭头图标
		expandableListView.setGroupIndicator(null);
		expandableListView.setAdapter(depAdapter);
		//设置item点击的监听器
	    expandableListView.setOnChildClickListener(new OnChildClickListener() {
	
	        @Override
	        public boolean onChildClick(ExpandableListView parent, View v,
	                int groupPosition, int childPosition, long id) {
	        	EmployeeBean employee = (EmployeeBean) depAdapter.getChild(groupPosition, childPosition);
	            //弹出电话短信对话框,即设置可见
	            callANDsms.setVisibility(View.VISIBLE);
	            callTitle.setText(employee.getEmpName());
	            
	            number = employee.getMobile();
	            return false;
	        }
	
	    });
	    
	}

	private void queryEmployeeListAll() {
		empAll = new HashMap<Integer,List<EmployeeBean>>();
		int index = 0;
		//根据所有部门编号，迭代查询员工表
		String sql = "select * from employee_table where officeId = ?";
		List<EmployeeBean> list = null;
		EmployeeBean emp = null;
		Integer departmentId = null;
		for(DepartmentBean dep : deplist){
			departmentId = dep.getDepartmentId();
			list = new ArrayList<EmployeeBean>();
			//获取对应部门的所有员工
			cursor = dp.query(sql, new String[]{departmentId+""});
			if(cursor.moveToNext()){
				int count = cursor.getCount();
				if (count == 0)
					return;
				for (int i = 0; i < count; i++) {
					cursor.moveToPosition(i);
					String name = cursor.getString(3);
					String mobile = cursor.getString(7);
					emp = new EmployeeBean();
					emp.setEmpName(name);
					emp.setMobile(mobile);
					list.add(emp);
				}
			}
			empAll.put(index++, list);
		}
	}

	@Override
	protected void onDestroy() {
		if(cursor != null){
			cursor.close();
		}
		if(dp != null){
			dp.close();
		}
		super.onDestroy();
	}

	//用户输入字符时触发该方法
	@Override
	public boolean onQueryTextChange(String newText ) {
		if (TextUtils.isEmpty(newText)) {
			// 输入框内容为空时，返回通讯录
			searchResult.setVisibility(View.GONE);
		} 
		return true;
	}
	//单击搜索按钮时触发该方法
	@Override
	public boolean onQueryTextSubmit(String query) {
		
		// 实际应用中应该在该方法内执行实际查询
		// 此处仅使用Toast显示用户输入的查询内容
		//先查询数据集，对姓名电话进行模糊查询
		List<EmployeeBean> emps = queryEmployees(query);
		//搜索时，结果可见
		searchResult.setVisibility(View.VISIBLE);
		searchList.setAdapter(new SearchAdapter(this,emps));
		searchList.setOnItemClickListener(new SearchOnClickListener());
		
		return false;
	}
	private List<EmployeeBean> queryEmployees(String query) {
		String sql = "select * from employee_table where empName like ? or mobile like ?";
		List<EmployeeBean> list = null;
		EmployeeBean emp = null;
			list = new ArrayList<EmployeeBean>();
			//获取对应部门的所有员工
			String args = "%"+query+"%";
			cursor = dp.query(sql, new String[]{args,args});
			if(cursor.moveToNext()){
				int count = cursor.getCount();
				if (count == 0)
					return null;
				for (int i = 0; i < count; i++) {
					cursor.moveToPosition(i);
					String name = cursor.getString(3);
					String mobile = cursor.getString(7);
					emp = new EmployeeBean();
					emp.setEmpName(name);
					emp.setMobile(mobile);
					list.add(emp);
				}
			}
		return list;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.send_sms:
			//发送短信
			sendSms(number);
			break;
		case R.id.call_phone:
			//打电话
			callBody(number);
			break;
		case R.id.callsms_cancel:
			callANDsms.setVisibility(View.GONE);
			break;
		case R.id.refreshContact:
			//清空表数据
			dp.deleteTable();
			getDepartmentData();
			break;

		}
	}
	private void callBody(String number) {
		Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+number));
		startActivity(intent);
	}

	private void sendSms(String number) {
		Uri smsToUri = Uri.parse("smsto:"+number);
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		startActivity(intent);
	}
	private class SearchOnClickListener implements OnItemClickListener{


		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			TextView contactName = (TextView) view.findViewById(R.id.contact_name);
			TextView contact_tel = (TextView) view.findViewById(R.id.contact_tel);
			callTitle.setText(contactName.getText());
            number = contact_tel.getText().toString();
            //弹出电话短信对话框
            callANDsms.setVisibility(View.VISIBLE);
		}
		
	}
	private class SearchAdapter extends BaseAdapter{
		
		private Context mcontext;
		private List<EmployeeBean> emplist;
		public SearchAdapter(Context context,List<EmployeeBean> emplist){
			this.mcontext = context;
			this.emplist = emplist;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return emplist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return emplist.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final LayoutInflater inflater = LayoutInflater.from(mcontext);
			convertView = inflater.inflate(R.layout.shortcontact_listview, null);
			
			TextView contactName = (TextView) convertView.findViewById(R.id.contact_name);
			contactName.setText(emplist.get(position).getEmpName());
			TextView contactTel = (TextView) convertView.findViewById(R.id.contact_tel);
			contactTel.setText(emplist.get(position).getMobile());
			return convertView;
		}
		
	}
}  



	


