package com.cn7782.management.android;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.app.Notification;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;
import com.cn7782.management.R;
import com.cn7782.management.android.activity.controller.CrashHandler;
import com.cn7782.management.android.constants.GlobalConstant;
import com.easemob.chat.EMChat;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

public class BaseApplication extends Application {
    public static final String LOCATION_RECEIVER_ACTION = "com.cn7782.loction";
    private static BaseApplication application;
    private static Context mcontext;

    // 摔落告警时，倒计时
    private int falldownCount = GlobalConstant.FALLDOWN_COUNT;
    // 摔落报警是否在前端对话框中正在倒计时
    private boolean isForegroundCountDown = false;
    //监听状态
    private static int isLocation = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
        application = this;
        mcontext = this.getApplicationContext();

		CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(mcontext));
        //初始化SDK
        EMChat.getInstance().init(application);
        //init demo helper
        DemoHelper.getInstance().init(application);
        //JPush init
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);


        //设置在此处起作用
        BasicPushNotificationBuilder builder = new
                CustomPushNotificationBuilder(mcontext,
                R.layout.customer_notitfication_layout,
                R.id.icon,
                R.id.title,
                R.id.text);
        //设置为自动消失
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;
        // 设置为铃声与震动都要
        builder.notificationDefaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
        // 指定定制的 Notification Layout
        builder.statusBarDrawable = R.drawable.ic_launcher;
        // 指定最顶层状态栏小图标
//        builder.layoutIconDrawable = R.drawable.ic_launcher;
        // 指定下拉状态栏时显示的通知图标
        JPushInterface.setDefaultPushNotificationBuilder(builder);
        
    }

    public synchronized static BaseApplication getInstance() {
        if (null == application) {
            application = new BaseApplication();
        }
        return application;
    }

    /**
     * 判断当前应用是否处于前台运行
     *
     * @param context
     * @return boolean
     */
    public boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }
            }
        }
        return false;
    }

    public void getContext(Context dp) {
        this.mcontext = dp;
    }

    public synchronized void setIsForegroundCountDown(boolean value) {
        isForegroundCountDown = value;
    }

    public synchronized boolean getIsForegroundCountDownn() {
        return isForegroundCountDown;
    }

    public synchronized void setFallDownCount(int value) {
        falldownCount = value;
    }

    // 离线service是否开启
    public synchronized int getFallDownCount() {
        return falldownCount;
    }

    public synchronized void setIsLocation(int value) {
        isLocation = value;
    }

    public synchronized int getIsLocation() {
        return isLocation;
    }
}
