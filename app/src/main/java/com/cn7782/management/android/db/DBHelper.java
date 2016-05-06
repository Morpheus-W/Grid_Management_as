package com.cn7782.management.android.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.cn7782.management.util.LogUtil;

import java.util.ArrayList;

/*
 *    数据库表逻辑为TABLENAME为唯一的表名用来记录巡访所有的表名，包括表名字，时间，地点，事件过程。
 *    Creatnewtable(String tableName)为创建的新表，用来记录获取经纬度，速度和时间。
 *    表名为当前时间。
 * 
 */
public class DBHelper extends SQLiteOpenHelper {
	public static String sqlite = "Test42";
	public static int factory = 1;
	public String tablename;
	private Context mcontext;
	// 不能删除的表 用来记录所有表名
	public static final String TABLENAME = "record_tablename";
	//通讯录中部门表，员工表
	public static final String DEPARTMENTTABLE = "department_table";
	public static final String EMPLOYEETABLE = "employee_table";

	public DBHelper(Context context) {
		super(context, sqlite, null, factory);
		this.mcontext = context;
	}

	public void settablename(String name) {
		this.tablename = name;
	}

	public String gettablename() {
		return tablename;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer sqlname = new StringBuffer();
		// Artificial,2为人工记录,1为自动记录（每段时间执行一次 防止崩溃而数据丢失）
		sqlname.append("create table");
		sqlname.append(" ");
		sqlname.append(TABLENAME);
		sqlname.append("(tablename string,Artificial string,eventid string,event string,time string,endtime string,duration string)");
		String b = sqlname.toString();
		db.execSQL(b);

	}

	@SuppressLint("ShowToast")
	public void dropTable(String name) {
		SQLiteDatabase db = this.getWritableDatabase();
		if (!name.equals("") && !name.equals(TABLENAME)) {
			try {
				db.execSQL("DROP TABLE " + name);
			} catch (Exception e) {
				LogUtil.e("DROP TABLE ", e.getMessage());
			}
		}
	}

	public void deleteTable(String name) {
		SQLiteDatabase db = this.getWritableDatabase();
		if (!name.equals("") && !name.equals(TABLENAME)) {
			try {
				db.execSQL("delete from " + TABLENAME + " WHERE tablename= " + "\""
						+ name + "\"");
			} catch (Exception e) {
				LogUtil.e("删除表中名 ", e.getMessage());
			}
		}
	}

	/**
	 * 删除Artificial为 1或3的表
	 */
	public void deleteTable(int artificial) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.execSQL("delete from " + TABLENAME + " WHERE Artificial=" + artificial);
		} catch (Exception e) {
			LogUtil.e("删除表中名 ", e.getMessage());
		}
	}


	/**
	 * 插入表名，人工输入，id，事件过程，开始时间，结束时间，耗时
	 */
	public void insert(String tablename, String Artificial, String eventid,
			String event, String time, String endtime, String duration) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("tablename", tablename);
		cv.put("Artificial", Artificial);
		cv.put("eventid", eventid);
		cv.put("event", event);
		cv.put("time", time);
		cv.put("endtime", endtime);
		cv.put("duration", duration);
		db.insert(TABLENAME, null, cv);
		LogUtil.e("插入表", "sucess");
	}

	/**
	 * 插入标题，时间，速度，纬度，经度，表名
	 */
	public void insertTalbe(String title, String time, double speed,
			double Latitude, double Longitude, int pathType, String tablename) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("title", title);
		cv.put("time", time);
		cv.put("speed", speed);
		cv.put("Latitude", Latitude);
		cv.put("Longitude", Longitude);
		cv.put("path_type", pathType);
		db.insert(tablename, null, cv);
	}

	// 自动转为人工，该状态用于区分是否恢复记录
	public void updateTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.execSQL("UPDATE " + TABLENAME
					+ " SET Artificial = 2 WHERE Artificial=1 or Artificial=5");
		} catch (Exception e) {
			LogUtil.e("更改表数据 ", e.getMessage());
		}
	}

	// 转为3，该状态表示：已离线提交成功，或正常提交
	// 转为5，该状态表示：返回监听中
	public void updateTable(int artificial,String name) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.execSQL("UPDATE " + TABLENAME
					+ " SET Artificial = "+ artificial+" WHERE tablename=" + "\"" + name
					+ "\"");
		} catch (Exception e) {
			LogUtil.e("更改表数据 ", e.getMessage());
		}
	}

	/**
	 * 改变eventId
	 * 
	 * @param tablename
	 * @param eventId
	 */
	public void updataEventId(String tablename, String eventId) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.execSQL("UPDATE " + TABLENAME + " SET eventid = " + "\""
					+ eventId + "\"" + " WHERE tablename=" + "\"" + tablename
					+ "\"");
		} catch (Exception e) {
			LogUtil.e("更改表数据 ", e.getMessage());
		}
	}

	/**
	 * 结束时间
	 * 
	 * @param tablename
	 * @param endtime
	 */
	public void updateEndTime(String tablename, String endtime) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.execSQL("UPDATE " + TABLENAME + " SET endtime = " + "\""
					+ endtime + "\"" + " WHERE tablename=" + "\"" + tablename
					+ "\"");
		} catch (Exception e) {
			LogUtil.e("更改表数据 ", e.getMessage());
		}
	}
	/*
	 * 通用查询方法
	 */
	public Cursor query(String sql, String[] args) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, args);
		return cursor;
	}

	/**
	 * 创建新表记录轨迹数据
	 * 
	 */
	public void creatNewTable(String tableName) {
		SQLiteDatabase db = this.getWritableDatabase();
		if (tableName.equals("") || tableName == null) {
			Toast.makeText(mcontext, "表名不存在", Toast.LENGTH_SHORT).show();
			return;
		}
		settablename(tableName);
		StringBuffer sqlname = new StringBuffer();
		sqlname.append("create table");
		sqlname.append(" ");
		sqlname.append(tableName);
		sqlname.append("(title string,time string, speed double,Latitude double,Longitude double,path_type number)");
		String b = sqlname.toString();
		try {
			db.execSQL(b);
			LogUtil.e("创建轨迹", "" + tableName);
		} catch (Exception e) {
			LogUtil.e("创建轨迹", "" + e.getMessage());
		}

	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db = this.getWritableDatabase();
		StringBuffer sqlname = new StringBuffer();
		sqlname.append("create table");
		sqlname.append(" ");
		sqlname.append(gettablename());
		sqlname.append("(title string,time string, name string,Latitude string,Longitude double)");
		String b = sqlname.toString();
		try {
			db.execSQL(b);
		} catch (Exception e) {
			Toast.makeText(mcontext, "表名重复", Toast.LENGTH_SHORT).show();
		}
	}

	public void updataDuration(String tablename, int secNum) {
		// TODO 更改时间值
		SQLiteDatabase db = this.getWritableDatabase();
		String duration = secNum + "";
		try {
			db.execSQL("UPDATE " + TABLENAME + " SET duration = " + "\""
					+ duration + "\"" + " WHERE tablename=" + "\"" + tablename
					+ "\"");
		} catch (Exception e) {
			LogUtil.e("更改表数据 ", e.getMessage());
		}
	}

	/**
	 * 创建部门表,表名：department
	 * 
	 */
	public void creatDepartmentTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		StringBuffer sqlname = new StringBuffer();
		sqlname.append("create table if not exists ");
		sqlname.append(" ");
		sqlname.append(DEPARTMENTTABLE);
		sqlname.append(
				"(departmentId    INTEGER PRIMARY KEY," +
						"depName   string," +
						"parentId    INTEGER)");
		String b = sqlname.toString();
		try {
			db.execSQL(b);
			LogUtil.e("创建部门", "" + DEPARTMENTTABLE);
		} catch (Exception e) {
			LogUtil.e("创建部门", "" + e.getMessage());
		}
		
	}
	/*
	批量插入数据，开启事务
	 */
	public void bulkInsert(String tableName,
						   ArrayList<ContentValues> valuesArr){
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();
		for (ContentValues val : valuesArr){
			db.insert(tableName,null,val);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}

	//清空部门表和员工表
	public void deleteTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.execSQL("drop table " + DEPARTMENTTABLE);
			db.execSQL("drop table " + EMPLOYEETABLE);
		} catch (Exception e) {
			LogUtil.e("更改表数据 ", e.getMessage());
		}
	}
	/**
	 * 创建员工表,表名：employee
	 * 
	 */
	public void creatEmployeeTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		StringBuffer sqlname = new StringBuffer();
		sqlname.append("create table if not exists ");
		sqlname.append(" ");
		sqlname.append(EMPLOYEETABLE);
		sqlname.append(
				"(contactId    string PRIMARY KEY," +
						"officeId   INTEGER," +
						"officeName   string," +
						"empName   string," +
						"email   string," +
						"fax   string," +
						"homeAddr   string," +
						"mobile   string," +
						"mobile2   string," +
						"officeAddr   string," +
						"remark   string," +
						"officeTel    string)");
		String b = sqlname.toString();
		try {
			db.execSQL(b);
			LogUtil.e("创建员工表", "" + EMPLOYEETABLE);
		} catch (Exception e) {
			LogUtil.e("创建员工表", "" + e.getMessage());
		}
		
	}

	//数据库中是否创建轨迹表
	public boolean isTableExists(String tableName) {
		SQLiteDatabase db = this.getReadableDatabase();

	    Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
	    if(cursor!=null) {
	        if(cursor.getCount()>0) {
	            cursor.close();
	            return true;
	        }
	        cursor.close();
	    }
	    return false;
	}
	//轨迹表中是否存在记录
	public boolean isRecordExists(String tableName) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("select * from  '"+tableName+"'", null);
		if(cursor!=null) {
			if(cursor.getCount()>0) {
				cursor.close();
				return true;
			}
			cursor.close();
		}
		return false;
	}
}
