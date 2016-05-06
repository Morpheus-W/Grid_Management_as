package com.cn7782.management.android.activity.controller;

import android.app.Activity;

import java.util.Stack;


/**
 * Activity管理类
 * 
 * @author coder
 * 
 */
public class AppManager {

	// 自定义的Activity堆栈
	private static Stack<Activity> activityStack;

	private static AppManager appManager;

	/**
	 * 单例模式 将构造方法私有化，然后再向外公共一个获取实例的方法
	 */
	private AppManager() {

	}

	/**
	 * 单例模式 获取当前实例
	 */
	public static AppManager getAppManager() {

		if (appManager == null) {
			appManager = new AppManager();
		}
		return appManager;
	}

	/***
	 * 添加Activity到堆栈
	 * 
	 * @param activity
	 */
	public void addActivity(Activity activity) {

		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 * 
	 * @return
	 */
	public Activity getCurrentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		if (activity != null) {
			
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定的Activity
	 * 
	 * @param activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 * 
	 * @param cls
	 */
	public void finishActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}

}
