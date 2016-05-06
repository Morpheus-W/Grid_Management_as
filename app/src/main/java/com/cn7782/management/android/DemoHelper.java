package com.cn7782.management.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.cn7782.management.android.activity.MainActivity;
import com.cn7782.management.android.constants.Constant;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.easeui.controller.EaseUI;
import com.easemob.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.model.EaseNotifier;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;

import java.util.List;

public class DemoHelper {


    protected static final String TAG = "DemoHelper";
    
	private EaseUI easeUI;
	
    /**
     * EMEventListener
     */
    protected EMEventListener eventListener = null;

	private static DemoHelper instance = null;

    
    private boolean alreadyNotified = false;


	private String username;

    private Context appContext;

    private EMConnectionListener connectionListener;

    private LocalBroadcastManager broadcastManager;

    private boolean isGroupAndContactListenerRegisted;
    private String tokenId;

	private DemoHelper() {
	}

	public synchronized static DemoHelper getInstance() {
		if (instance == null) {
			instance = new DemoHelper();
		}
		return instance;
	}

	/**
	 * init helper
	 * 
	 * @param context
	 *            application context
	 */
	public void init(Context context) {
		if (EaseUI.getInstance().init(context)) {
		    appContext = context;
		    //设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
		    EMChat.getInstance().setDebugMode(true);
		    //get easeui instance
		    easeUI = EaseUI.getInstance();
		    //调用easeui的api设置providers
		    setEaseUIProviders();
			//设置全局监听
//			setGlobalListeners();
		}
	}

    protected void setEaseUIProviders() {
        //需要easeui库显示用户头像和昵称设置此provider
        easeUI.setUserProfileProvider(new EaseUserProfileProvider() {

            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });
    }
    
    /**
     * 设置全局事件监听
     */
    protected void setGlobalListeners(){
        
        // create the global connection listener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                if (error == EMError.USER_REMOVED) {
                    onCurrentAccountRemoved();
                }else if (error == EMError.CONNECTION_CONFLICT) {
                    onConnectionConflict();
                }
            }

            @Override
            public void onConnected() {
            }
        };
        //注册连接监听
        EMChatManager.getInstance().addConnectionListener(connectionListener);
        //注册消息事件监听
        registerEventListener();
    }
    
    /**
     * 账号在别的设备登录
     */
    protected void onConnectionConflict(){
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.ACCOUNT_CONFLICT, true);
        appContext.startActivity(intent);
    }
    
    /**
     * 账号被移除
     */
    protected void onCurrentAccountRemoved(){
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.ACCOUNT_REMOVED, true);
        appContext.startActivity(intent);
    }
	
	private EaseUser getUserInfo(String username){
        tokenId = SharedPreferenceUtil.getValue(
                PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
                appContext);
        EaseUser user = new EaseUser(username);
        String avatar = null;
        if(username.equals(EMChatManager.getInstance().getCurrentUser())) {
            avatar = SharedPreferenceUtil.getValue(
                    PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.USER_PHOTO, appContext);

        }else{
            //根据会话获取发送方头像
            //获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
            EMConversation conversation = EMChatManager.getInstance().getConversation(username);
            EMMessage message = conversation.getLastMessage();
            try {
                avatar = message.getStringAttribute("photoPath");
                avatar = ActionUrl.URL
                        + "city_grid/mobile/announcement/showPic?token_id="
                        + tokenId + "&path=" + avatar;
            } catch (EaseMobException e) {
//                e.printStackTrace();
            }
        }
        user.setAvatar(avatar);
        return user;
	}
	
	 /**
     * 全局事件监听
     * 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理
     * activityList.size() <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
     */
    protected void registerEventListener() {
        eventListener = new EMEventListener() {
            private BroadcastReceiver broadCastReceiver = null;
            
            @Override
            public void onEvent(EMNotifierEvent event) {
                EMMessage message = null;
                if(event.getData() instanceof EMMessage){
                    message = (EMMessage)event.getData();
                    EMLog.d(TAG, "receive the event : " + event.getEvent() + ",id : " + message.getMsgId());
                }
                
                switch (event.getEvent()) {
                case EventNewMessage:
                    //应用在后台，不需要刷新UI,通知栏提示新消息
                    if(!easeUI.hasForegroundActivies()){
                        getNotifier().onNewMsg(message);
                    }
                    break;
                case EventOfflineMessage:
                    if(!easeUI.hasForegroundActivies()){
                        EMLog.d(TAG, "received offline messages");
                        List<EMMessage> messages = (List<EMMessage>) event.getData();
                        getNotifier().onNewMesg(messages);
                    }
                    break;
                // below is just giving a example to show a cmd toast, the app should not follow this
                // so be careful of this
                case EventNewCMDMessage:
                { 
                    
                    EMLog.d(TAG, "收到透传消息");
                    //获取消息body
                    CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
                    final String action = cmdMsgBody.action;//获取自定义action
                    
                    //获取扩展属性 此处省略
                    //message.getStringAttribute("");
                    EMLog.d(TAG, String.format("透传消息：action:%s,message:%s", action, message.toString()));
                    final String str = appContext.getString(com.easemob.easeui.R.string.receive_the_passthrough);
                    
                    final String CMD_TOAST_BROADCAST = "easemob.demo.cmd.toast";
                    IntentFilter cmdFilter = new IntentFilter(CMD_TOAST_BROADCAST);
                    
                    if(broadCastReceiver == null){
                        broadCastReceiver = new BroadcastReceiver(){

                            @Override
                            public void onReceive(Context context, Intent intent) {
                                // TODO Auto-generated method stub
                                Toast.makeText(appContext, intent.getStringExtra("cmd_value"), Toast.LENGTH_SHORT).show();
                            }
                        };
                        
                      //注册广播接收者
                        appContext.registerReceiver(broadCastReceiver,cmdFilter);
                    }

                    Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
                    broadcastIntent.putExtra("cmd_value", str+action);
                    appContext.sendBroadcast(broadcastIntent, null);
                    
                    break;
                }
                case EventDeliveryAck:
                    message.setDelivered(true);
                    break;
                case EventReadAck:
                    message.setAcked(true);
                    break;
                // add other events in case you are interested in
                default:
                    break;
                }
                
            }
        };
        
        EMChatManager.getInstance().registerEventListener(eventListener);
    }

	/**
	 * 是否登录成功过
	 * 
	 * @return
	 */
	public boolean isLoggedIn() {
		return EMChat.getInstance().isLoggedIn();
	}

	/**
	 * 退出登录
	 * 
	 * @param unbindDeviceToken
	 *            是否解绑设备token(使用GCM才有)
	 * @param callback
	 *            callback
	 */
	public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
		EMChatManager.getInstance().logout(unbindDeviceToken, new EMCallBack() {

			@Override
			public void onSuccess() {
			    reset();
				if (callback != null) {
					callback.onSuccess();
				}

			}

			@Override
			public void onProgress(int progress, String status) {
				if (callback != null) {
					callback.onProgress(progress, status);
				}
			}

			@Override
			public void onError(int code, String error) {
				if (callback != null) {
					callback.onError(code, error);
				}
			}
		});
	}
	
	/**
	 * 获取消息通知类
	 * @return
	 */
	public EaseNotifier getNotifier(){
	    return easeUI.getNotifier();
	}
	
    synchronized void reset(){
        
        alreadyNotified = false;
        isGroupAndContactListenerRegisted = false;
    }
}
