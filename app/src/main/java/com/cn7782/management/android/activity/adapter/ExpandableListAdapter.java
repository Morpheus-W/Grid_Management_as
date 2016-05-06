package com.cn7782.management.android.activity.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.bean.DepartmentBean;
import com.cn7782.management.android.activity.bean.EmployeeBean;
import com.cn7782.management.android.activity.bean.SearchBean;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private List<DepartmentBean> deplist;
	private Context mcontext;
	private Map<Integer,List<EmployeeBean>> empAll ;
	
	public ExpandableListAdapter(Context context, List<DepartmentBean> deplist,Map<Integer,List<EmployeeBean>> empAll) {
		this.mcontext = context;
		this.deplist = deplist;
		this.empAll = empAll;
	}
    
    //重写ExpandableListAdapter中的各个方法
    @Override
    public int getGroupCount() {
        return deplist.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return deplist.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return empAll.get(groupPosition).size();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return empAll.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        
        LayoutInflater inflater = LayoutInflater.from(mcontext);
		convertView = inflater.inflate(R.layout.shortdepartment_listview, null);
        DepartmentBean dep = (DepartmentBean)getGroup(groupPosition);
		TextView departmentName = (TextView) convertView.findViewById(R.id.department_name);
		departmentName.setText(dep.getDepName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
    	LayoutInflater inflater = LayoutInflater.from(mcontext);
		convertView = inflater.inflate(R.layout.shortcontact_listview, null);
		
        EmployeeBean emp = (EmployeeBean)getChild(groupPosition, childPosition);
		TextView contactName = (TextView) convertView.findViewById(R.id.contact_name);
		contactName.setText(emp.getEmpName());
		TextView contactTel = (TextView) convertView.findViewById(R.id.contact_tel);
		contactTel.setText(emp.getMobile());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition,
            int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }

}
