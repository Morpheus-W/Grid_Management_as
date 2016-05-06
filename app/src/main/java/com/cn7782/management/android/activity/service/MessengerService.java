package com.cn7782.management.android.activity.service;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Vibrator;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.MainActivity;
import com.cn7782.management.android.activity.MsgNotificationActivity;
import com.cn7782.management.android.activity.controller.SoundManager;
import com.cn7782.management.android.activity.dialog.MsgDialog;
import com.cn7782.management.util.TimeUtil;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.utils.EaseCommonUtils;

public class MessengerService extends Service implements EMEventListener {

    private NotificationManager nm;

    private SoundManager mSoundManager;
    private Vibrator vibrator;
    // 停止 开启 停止 开启
    private static long[] pattern = {100, 400, 100, 400};

    public static boolean mBound = false;
    //声明键盘管理器
    KeyguardManager mKeyguardManager = null;
    //声明电源管理器
    private PowerManager pm;
    private Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        //初始化通知管理器
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //初始化声音
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mSoundManager = new SoundManager();
        mSoundManager.initSounds(getApplicationContext());
        mSoundManager.addSound(1, R.raw.msg);
        //初始化震动
         /*
         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
         * */
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //获取电源的服务
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //获取系统服务
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(this,
                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventConversationListChanged});

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //该方法每次开启服务都会执行
//        requestMsg();
        //START_STICKY 该返回值，若服务意外终止，系统会尝试重新创建
        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("宜章网格化系统")
                        .setContentText("调度消息");
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        /*
        Notification notification = new Notification(R.drawable.ic_launcher,
                "", System.currentTimeMillis());
        PendingIntent pendingintent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        notification.setLatestEventInfo(this, "宜章网格化系统", "调度消息",
                pendingintent);
        */
        startForeground(0x111, mBuilder.build());
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 当Activity绑定Service的时候，通过这个方法返回一个IBinder，Activity用这个IBinder创建出的Messenger，就可以与Service的Handler进行通信了
     */
    @Override
    public IBinder onBind(Intent intent) {
        mBound = true;
        return mBind;
    }

    private MsgBind mBind = new MsgBind();

    @Override
    public void onEvent(EMNotifierEvent event) {

        EMMessage message = (EMMessage) event.getData();
        Bundle bundle = new Bundle();
        bundle.putInt("num", getUnreadMsgCountTotal());
        String lastTime = TimeUtil.getDateTime(message.getMsgTime());
        //根据消息内容和消息类型获取消息内容提示
        String lastContent = EaseCommonUtils.getMessageDigest(message,context);
        bundle.putString("lastTime",lastTime);
        bundle.putString("lastContent", lastContent);
        bundle.putString(EaseConstant.EXTRA_USER_ID,message.getUserName());

        switch (event.getEvent()){
            case EventNewMessage:{
                showMsgDialog(bundle);
                showMsgWakeLock(bundle);
                break;
            }
            case EventOfflineMessage:{
                break;
            }
            case EventConversationListChanged:{
                break;
            }
            default:
                break;
        }

    }

    public class MsgBind extends Binder {
        public MessengerService getService() {
            return MessengerService.this;
        }
    }

    /*展示消息对话框*/
    private void showMsgDialog(final Bundle bundle) {
        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean flag = mKeyguardManager.inKeyguardRestrictedInputMode();

        //判断是否在锁屏状态下
        if (!flag) {

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    MsgDialog msgDialog = new MsgDialog(getApplicationContext(), R.style.msgDialog, bundle);
                    msgDialog.show();
                }
            });
        }
    }

    private void showMsgWakeLock(Bundle bundle) {
        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean flag = mKeyguardManager.inKeyguardRestrictedInputMode();

        //判断是否在锁屏状态下
        if (flag) {

            Intent intent = new Intent(context, MsgNotificationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("data", bundle);
            context.startActivity(intent);
        }
    }

    public void onDestroy() {
        mBound = false;
        stopForeground(true);
        //当用户主动销毁服务时，再次启动
        Intent intent = new Intent("com.cn7782.management.messengerservice.destroy");
        sendBroadcast(intent);
        //注销事件
        EMChatManager.getInstance().unregisterEventListener(this);
    }
    /**
     * 获取未读消息数
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
        for(EMConversation conversation:EMChatManager.getInstance().getAllConversations().values()){
            if(conversation.getType() == EMConversation.EMConversationType.ChatRoom)
                chatroomUnreadMsgCount=chatroomUnreadMsgCount+conversation.getUnreadMsgCount();
        }
        return unreadMsgCountTotal-chatroomUnreadMsgCount;
    }

}
