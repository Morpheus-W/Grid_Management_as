package com.cn7782.management.android.activity.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.DynamicActivity;
import com.cn7782.management.android.activity.NoticeActivity;
import com.cn7782.management.util.LogUtil;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 *
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 */
public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "MyReceiver";
    private static final String NOTICE = "Announcement";
    private static final String DYNAMIC = "Information";

    private static int NOTICE_ID ;
    private NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Bundle bundle = intent.getExtras();

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            LogUtil.d(TAG, "JPush用户注册成功");

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            LogUtil.d(TAG, "接受到推送下来的自定义消息");
            processCustomMessage(context,bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            LogUtil.d(TAG, "接受到推送下来的通知");

            receivingNotification(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            LogUtil.d(TAG, "用户点击打开了通知");

            openNotification(context, bundle);

        } else {
            LogUtil.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    private void processCustomMessage(Context context, Bundle bundle) {
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        String myValue = "";
        JSONObject extrasJson ;
        try {
            extrasJson = new JSONObject(extras);
            myValue = extrasJson.optString("type");
        } catch (Exception e) {
            LogUtil.w(TAG, "Unexpected: extras is not a valid json", e);
            return;
        }
        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        Notification.Builder mBuilder =
                new Notification.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true);//点击后消失
        mBuilder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE);
        PendingIntent pendingIntent = null;
        Intent mIntent = null;
        //公告
        if (NOTICE.equals(myValue)) {
            mIntent = new Intent(context, NoticeActivity.class);
            mIntent.putExtras(bundle);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            NOTICE_ID = 1;
        } else if (DYNAMIC.equals(myValue)){//动态
            mIntent = new Intent(context, DynamicActivity.class);
            mIntent.putExtras(bundle);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            NOTICE_ID = 2;
        }else{
            String latlon = extrasJson.optString("latlon");
            String name = extrasJson.optString("name");
            String phone = extrasJson.optString("phone");
            mBuilder.setContentTitle("收到紧急求救信息");
            mBuilder.setContentText(name + "," + phone);
            LogUtil.e("latlon", "-----" + latlon);
            NOTICE_ID = 3;
        }
        pendingIntent = PendingIntent.getActivity(context,0,mIntent,0);
        mBuilder.setContentIntent(pendingIntent);
        nm.notify(NOTICE_ID,mBuilder.build());
    }

    private void receivingNotification(Context context, Bundle bundle){
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        LogUtil.d(TAG, " title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        LogUtil.d(TAG, "message : " + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        LogUtil.d(TAG, "extras : " + extras);

    }

    private void openNotification(Context context, Bundle bundle){
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        String myValue = "";
        try {
            JSONObject extrasJson = new JSONObject(extras);
            myValue = extrasJson.optString("type");
        } catch (Exception e) {
            LogUtil.w(TAG, "Unexpected: extras is not a valid json", e);
            return;
        }
        //公告
        if (NOTICE.equals(myValue)) {
            Intent mIntent = new Intent(context, NoticeActivity.class);
            mIntent.putExtras(bundle);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);
        } else if (DYNAMIC.equals(myValue)){//动态
            Intent mIntent = new Intent(context, DynamicActivity.class);
            mIntent.putExtras(bundle);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);
        }
    }
}
