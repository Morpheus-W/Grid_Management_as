package com.cn7782.management.android.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.BaseApplication;
import com.cn7782.management.android.activity.controller.AppManager;
import com.cn7782.management.android.activity.service.MessengerService;
import com.cn7782.management.android.activity.service.TimingService;
import com.cn7782.management.android.activity.tabview.MyProgressDialog;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.config.ConfigUtil;
import com.cn7782.management.http.HttpClient;
import com.cn7782.management.http.HttpDownloader;
import com.cn7782.management.http.MyAsyncHttpResponseHandler;
import com.cn7782.management.util.ActivityUtil;
import com.cn7782.management.util.PictureUtil;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.cn7782.management.view.SlidingListView;
import com.easemob.EMCallBack;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.utils.EaseUserUtils;
import com.loopj.android.http.RequestParams;
import com.slidingmenu.lib.SlidingMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class MainActivity extends BaseActivity implements OnClickListener,EMEventListener {
    private View dataview, statisticsview, historyview, orderview, nateview,
            uploadview, scheduleview, contactview;
    private SlidingMenu mDisMenu;
    private SlidingListView slidinglistview;
    // 侧滑界面右侧边距
    public static final int SLIDING_MENU_RIGHT_MARGIN = 400;
    // 侧滑界面渐变角度
    public static final float SLIDING_MENU_FADE_DEGREE = 1f;
    // 屏幕宽度
    private int screenWidth_ = 0;
    // 屏幕高度
    private int screenHeight_ = 0;
    // 屏幕密度
    private float screenDensity_ = 0;
    public static boolean is = true;
    private ImageView point_head;
    private SDKReceiver mReceiver;
    private long exitTime;

    private String userName, userRole, userPhoto, urlimage;
    private String tokenId,roles;

    private TextView noReadItem;
    private BaseApplication base;
    //版本变量
    private static final String TAG = "Update";
    private MyProgressDialog myProgressDialog;
    public final static int UPDATE_APK = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //加入版本升级内容
        getServerVerCode();
        initview();
        getData();
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);
        // 绑定消息Service
        bindMsgService();
        //开启GPS定位服务
        startService();

        //获取系统用户名密码
        String userName = SharedPreferenceUtil.getValue(
                PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.ACCOUNT,
                MainActivity.this);
        String password = SharedPreferenceUtil.getValue(
                PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.PASSWORD,
                MainActivity.this);
        //进行环信登陆
        EMChatManager.getInstance().login(userName, password, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        EMGroupManager.getInstance().loadAllGroups();
                        EMChatManager.getInstance().loadAllConversations();
                        Log.d("main", "登陆聊天服务器成功！");
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("main", "登陆聊天服务器失败！");
            }
        });
    }

    public int getScreenWidth_() {
        if (screenWidth_ == 0) {
            getDisplayInfo(this);
        }
        return screenWidth_;
    }

    public void getDisplayInfo(Context context) {
        DisplayMetrics displayMetric = new DisplayMetrics();
        displayMetric = context.getResources().getDisplayMetrics();
        screenWidth_ = displayMetric.widthPixels;
        screenHeight_ = displayMetric.heightPixels;
        screenDensity_ = displayMetric.density;

    }

    private void initview() {
        tokenId = SharedPreferenceUtil.getValue(
                PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
                MainActivity.this);
        roles = SharedPreferenceUtil.getValue(
                PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.ROLES,
                MainActivity.this);
        LinearLayout root_roles = (LinearLayout)findViewById(R.id.root_roles);
        if (roles != null && !"".equals(roles)){
            root_roles.setVisibility(View.VISIBLE);
        }else{
            root_roles.setVisibility(View.GONE);
        }
        base = BaseApplication.getInstance();

        dataview = (View) findViewById(R.id.main_data);
        dataview.setOnClickListener(this);
        statisticsview = (View) findViewById(R.id.main_statistics);
        statisticsview.setOnClickListener(this);
        historyview = (View) findViewById(R.id.main_history);
        historyview.setOnClickListener(this);
        orderview = (View) findViewById(R.id.main_order);
        orderview.setOnClickListener(this);
        nateview = (View) findViewById(R.id.main_note);
        nateview.setOnClickListener(this);
        uploadview = (View) findViewById(R.id.main_updata);
        uploadview.setOnClickListener(this);
        //增加日程管理与通讯录
        scheduleview = findViewById(R.id.main_schedule);
        scheduleview.setOnClickListener(this);
        contactview = findViewById(R.id.main_contact);
        contactview.setOnClickListener(this);

        noReadItem = (TextView) findViewById(R.id.noRead_item_num);
        noReadItem.setOnClickListener(this);
        //一键求助
        findViewById(R.id.sos_iv).setOnClickListener(this);
        //外层触发个人信息
        findViewById(R.id.info_layout).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        initbackview();
                    }
                });
    }

    private void initbackview() {

        if (null == mDisMenu) {
            mDisMenu = new SlidingMenu(MainActivity.this);
            mDisMenu.setMode(SlidingMenu.RIGHT);
            mDisMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            mDisMenu.setTouchModeBehind(SlidingMenu.TOUCHMODE_MARGIN);
            //设置阴影
            mDisMenu.setShadowWidthRes(R.dimen.kcool_slidingmenu_shadowwidth);

            mDisMenu.setBehindOffsetRes(R.dimen.kcool_slidingmenu_offset);
            mDisMenu.setBehindWidth(getScreenWidth_() - getScreenWidth_() / 3);
            mDisMenu.setFadeEnabled(true);
            mDisMenu.setFadeDegree(SLIDING_MENU_FADE_DEGREE);
            mDisMenu.attachToActivity(MainActivity.this,
                    SlidingMenu.SLIDING_CONTENT);

        }
        if (null == slidinglistview) {
            slidinglistview = new SlidingListView(MainActivity.this,handler);
            slidinglistview.setOwner(mDisMenu);

        }

        slidinglistview.setdata(userName, userRole, urlimage);
        mDisMenu.setMenu(slidinglistview.getView());
        mDisMenu.toggle();

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            //公告
            case R.id.main_data:
                Intent intent = new Intent(MainActivity.this, NoticeActivity.class);
                startActivityForResult(intent, 1);
                break;
            //综治动态
            case R.id.main_statistics:
                Intent intent2 = new Intent(MainActivity.this,
                        DynamicActivity.class);
                startActivityForResult(intent2, 2);
                break;
            //上报
            case R.id.main_history:
                Intent intent3 = new Intent(MainActivity.this,
                        EventReportActivity.class);
                intent3.putExtra("isPatroling", "no");
                startActivity(intent3);
                break;
            //巡防
            case R.id.main_order:
                Intent intent4 = new Intent(MainActivity.this, PatrolActivity.class);
                startActivity(intent4);
                break;
            //轨迹
            case R.id.main_note:
                Intent intent5 = new Intent(MainActivity.this,
                        TrajectoryActivity.class);
                startActivity(intent5);
                break;
            //统计分析
            case R.id.main_updata:
                Intent intent6 = new Intent(MainActivity.this,
                        StatisticsActivity.class);
                startActivity(intent6);
                break;
            case R.id.main_schedule:
                Intent intent7 = new Intent(MainActivity.this, ScheduleManagerActivity.class);
                startActivity(intent7);
                break;
            case R.id.main_contact:
                Intent intent8 = new Intent(MainActivity.this, ContactManagerActivity.class);
                startActivity(intent8);
                break;
            case R.id.noRead_item_num:
                Intent intent9 = new Intent(MainActivity.this,
                        ConversationListActivity.class);
                startActivity(intent9);
                break;
            case R.id.sos_iv:{
                Intent sosIntent = new Intent(MainActivity.this,FallDownWarnActivity.class);
                startActivity(sosIntent);
                break;
            }
            default:
                break;
        }
    }

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();

            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.baiduKeyErr),
                        Toast.LENGTH_SHORT).show();

            } else if (s
                    .equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                Toast.makeText(getApplicationContext(), "网络出错",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消监听 SDK 广播
        unregisterReceiver(mReceiver);
        stopService();
    }

    @Override
    /** 退出提醒 */
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (5 == base.getIsLocation()) {
                Toast.makeText(MainActivity.this, getString(R.string.exitMsg), Toast.LENGTH_SHORT)
                        .show();
            } else {
                exitApp();
            }

        }
        return false;
    }

    public void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出应用", Toast.LENGTH_SHORT)
                    .show();
            exitTime = System.currentTimeMillis();
        } else {
            stopService();
            stopMsgService();
            this.finish();
            AppManager.getAppManager().finishAllActivity();
        }
    }

    private void startService() {
        Intent timingIntent = new Intent();
        timingIntent.setClass(MainActivity.this, TimingService.class);
        this.startService(timingIntent);
    }

    private void stopService() {
        Intent timingIntent = new Intent();
        timingIntent.setClass(MainActivity.this, TimingService.class);
        this.stopService(timingIntent);
    }

    private void bindMsgService() {
        Intent intent = new Intent(MainActivity.this, MessengerService.class);
        startService(intent);
    }

    private void stopMsgService() {
        String msgBack = SharedPreferenceUtil.getValue(
                PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.IS_MSG_BACK,
                MainActivity.this);
        if (msgBack == null || (msgBack != null && msgBack.equals("false"))) {
            Intent intent = new Intent(MainActivity.this, MessengerService.class);
            stopService(intent);
        }
    }

    private void getData() {
        RequestParams param = new RequestParams();
        param.put("token_id", tokenId);
        HttpClient.post(MainActivity.this, ActionUrl.NUMBER, param,
                new MyAsyncHttpResponseHandler(MainActivity.this, "请稍后...") {
                    public void onSuccess(String results) {
                        super.onSuccess(results);

                        try {
                            JSONObject jsonObject = new JSONObject(results);
                            if (jsonObject.has("msg")) {
                                String msg = jsonObject.isNull("msg") ? ""
                                        : jsonObject.getString("msg");
                                if (msg.equals("查询成功")) {
                                    String num = jsonObject.isNull("num") ? ""
                                            : jsonObject.getString("num");
                                    String nolook = jsonObject
                                            .isNull("informationNoLook") ? ""
                                            : jsonObject
                                            .getString("informationNoLook");
                                    TextView number = (TextView) findViewById(R.id.nofinish_item_num);
                                    if (num.equals("0")) {
                                        number.setVisibility(View.GONE);
                                    } else {
                                        number.setText(num);
                                    }
                                    TextView onumber = (TextView) findViewById(R.id.nolook_item_num);
                                    if (nolook.equals("0")) {
                                        onumber.setVisibility(View.GONE);
                                    } else {
                                        onumber.setText(nolook);
                                    }
                                    userName = jsonObject.isNull("userName") ? ""
                                            : jsonObject.getString("userName");
                                    String area = jsonObject.isNull("area") ? ""
                                            : jsonObject.getString("area");
                                    String gridNames = jsonObject
                                            .isNull("gridNames") ? ""
                                            : jsonObject.getString("gridNames");
                                    userRole = jsonObject.isNull("userRole") ? ""
                                            : jsonObject.getString("userRole");
                                    userPhoto = jsonObject.isNull("userPhoto") ? ""
                                            : jsonObject.getString("userPhoto");
                                    ((TextView) findViewById(R.id.user_name))
                                            .setText(userName);
                                    ((TextView) findViewById(R.id.city))
                                            .setText(area + "/" + gridNames);
                                    String a = ActionUrl.URL
                                            + "city_grid/mobile/announcement/showPic?token_id="
                                            + tokenId + "&path=" + userPhoto;
                                    urlimage = a;
                                    SharedPreferenceUtil
                                            .modify(PreferenceConstant.MARK_ID_TABLE,
                                                    PreferenceConstant.USER_PHOTO,
                                                    urlimage,
                                                    MainActivity.this);
                                    point_head = (ImageView) findViewById(R.id.point_head);
                                    PictureUtil.ShowPicture(point_head,
                                            MainActivity.this, userPhoto);

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    public void onFailure(Throwable arg0, String tipInfo) {
                        super.onFailure(arg0, tipInfo);
                    }
                });
    }

    private void setUpBaseView(String avatar) {
        String username = EMChatManager.getInstance().getCurrentUser();
        EaseUser user = EaseUserUtils.getUserInfo(username);
        user.setAvatar(avatar);
    }

    private void getServerVerCode() {
        RequestParams param = new RequestParams();
        HttpClient
                .post(MainActivity.this, ActionUrl.UPDATE_SERVER, param,
                        new MyAsyncHttpResponseHandler(MainActivity.this,
                                "正在检查更新...") {
                            public void onSuccess(String results) {
                                super.onSuccess(results);
                                try {
                                    JSONObject jsonObject = new JSONObject(
                                            results);
                                    if (jsonObject.has("resultCode")) {
                                        Long fileSize = jsonObject.isNull("fileSize") ? 0
                                                : jsonObject.getLong("fileSize");
                                        JSONObject json = jsonObject
                                                .getJSONObject("apkVersion");
                                        String apkUrl = json.isNull("apkUrl") ? ""
                                                : json.getString("apkUrl");
                                        double newVerCode = json.isNull("versionCode") ? 0
                                                : json.getDouble("versionCode");
                                        double vercode = Double.parseDouble(
                                                ConfigUtil.getVerName(MainActivity.this));
                                        if (newVerCode > vercode) {
                                            updateVersion(fileSize, apkUrl, vercode, newVerCode);
                                        }
                                    }
                                } catch (JSONException e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                        });
    }

    private void updateVersion(final Long fileSize,final String apkUrl,double vercode,double newVerCode) {
        double verCode = Double.parseDouble(ConfigUtil.getVerName(this));
        StringBuffer sb = new StringBuffer();
        sb.append("当前版本:");
        sb.append(verCode);
        sb.append(", 发现新版本:");
        sb.append(newVerCode);
        sb.append(", 是否更新?");
        Dialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("软件更新")
                .setMessage(sb.toString())
                        // 设置内容
                .setPositiveButton("更新",// 设置确定按钮
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                downFile(ActionUrl.UPDATE_SERVER2
                                        + apkUrl,fileSize,apkUrl);
                            }

                        })
                .setNegativeButton("暂不更新",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                // 点击"取消"按钮之后退出程序，仍能继续使用
//                               finish();
                            }
                        }).create();// 创建
        // 显示对话框
        dialog.show();
    }

    private void downFile(final String url,final Long fileSize, final String apkUrl) {
        final String apkName = apkUrl.substring(apkUrl.lastIndexOf("/"), apkUrl.length());
        File file = null;
        if (ActivityUtil.isSdcardExist()) {
//            final String store_path_dir = Environment
//                    .getExternalStorageDirectory() + apkUrl.substring(0, apkUrl.lastIndexOf("/"));
            final String store_path_dir = Environment
                    .getExternalStorageDirectory().getAbsolutePath()+"/Download";
            file = new File(store_path_dir,apkName);
            if (file.exists()) {
                // 文件已经存在，并且是下载完毕的
                if (file.length() == fileSize) {
                    Message msg = handler.obtainMessage();
                    msg.what = 5;
                    msg.obj = apkUrl;
                    handler.sendMessage(msg);
                    return;
                } else {// 文件没有下载完毕。
                    file.delete();
                }
            }
            if (myProgressDialog == null) {
                myProgressDialog = new MyProgressDialog(
                        MainActivity.this, "下载安装包中，请稍后...");
            }
            new Thread(new Runnable() {

                @Override
                public void run() {
                    HttpDownloader.downLoadFile(url, store_path_dir,
                            apkName, handler, fileSize);
                    //下载完之后进行安装
                    down(apkUrl);
                }
            }).start();
        } else {
            Toast.makeText(MainActivity.this, "您的设备存储卡，请装上存储卡再下载！",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void down(final String apkUrl) {
        handler.post(new Runnable() {
            public void run() {
                update(apkUrl);
            }
        });

    }

    private void update(String apkUrl) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(
                        Environment.getExternalStorageDirectory() + apkUrl.substring(0, apkUrl.lastIndexOf("/"))
                        , apkUrl.substring(apkUrl.lastIndexOf("/"), apkUrl.length()))),
                "application/vnd.android.package-archive");
        startActivity(intent);
        //close msg service
        Intent msgService = new Intent(MainActivity.this, MessengerService.class);
        stopService(msgService);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_APK:
                    getServerVerCode();
                    break;
                case 1:
                    int progress = msg.arg1;
                    if (myProgressDialog != null) {
                        myProgressDialog.setMessage("安装包下载中  " + progress + "%");
                    }
                    break;
                case 2:
                    if (myProgressDialog != null) {
                        myProgressDialog.dismiss();
                        myProgressDialog = null;
                    }

                    break;

                case 3:
                    Toast.makeText(MainActivity.this, "出错了，下载出现异常！",
                            Toast.LENGTH_LONG).show();
                    if (myProgressDialog != null) {
                        myProgressDialog.dismiss();
                        myProgressDialog = null;
                    }

                    break;
                case 4:
                    Toast.makeText(MainActivity.this, "存储卡空间不足，不能下载更新！",
                            Toast.LENGTH_LONG).show();
                    if (myProgressDialog != null) {
                        myProgressDialog.dismiss();
                        myProgressDialog = null;
                    }

                    break;
                case 5:
                    if (myProgressDialog != null) {
                        myProgressDialog.dismiss();
                        myProgressDialog = null;
                    }
                    isInstallApk(((String)msg.obj));
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(this,
                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventConversationListChanged});
        updateNoReadNum();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        RequestParams param = new RequestParams();
        param.put("token_id", tokenId);
        HttpClient.post(MainActivity.this, ActionUrl.NUMBER, param,
                new MyAsyncHttpResponseHandler(MainActivity.this, null) {
                    public void onSuccess(String results) {
                        super.onSuccess(results);

                        try {
                            JSONObject jsonObject = new JSONObject(results);
                            if (jsonObject.has("msg")) {
                                String msg = jsonObject.isNull("msg") ? ""
                                        : jsonObject.getString("msg");
                                if (msg.equals("查询成功")) {
                                    String num = jsonObject.isNull("num") ? ""
                                            : jsonObject.getString("num");
                                    String nolook = jsonObject
                                            .isNull("informationNoLook") ? ""
                                            : jsonObject
                                            .getString("informationNoLook");
                                    TextView number = (TextView) findViewById(R.id.nofinish_item_num);
                                    if (num.equals("0")) {
                                        number.setVisibility(View.GONE);
                                    } else {
                                        number.setText(num);
                                    }
                                    TextView onumber = (TextView) findViewById(R.id.nolook_item_num);
                                    if (nolook.equals("0")) {
                                        onumber.setVisibility(View.GONE);
                                    } else {
                                        onumber.setText(nolook);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    public void onFailure(Throwable arg0, String tipInfo) {
                        super.onFailure(arg0, tipInfo);
                    }
                });
    }
    protected void updateNoReadNum() {
        int unreadMsgCountTotal = 0;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
        for(EMConversation conversation:EMChatManager.getInstance().getAllConversations().values()){
            if(conversation.getType() == EMConversation.EMConversationType.ChatRoom)
                chatroomUnreadMsgCount=chatroomUnreadMsgCount+conversation.getUnreadMsgCount();
        }
        int num = unreadMsgCountTotal-chatroomUnreadMsgCount;
        if (num == 0) {
            noReadItem.setText("0");
            noReadItem.setBackgroundResource(R.drawable.list_read);
        } else {
            noReadItem.setText(num+"");
            noReadItem.setBackgroundResource(R.drawable.list_count);
        }
    }
    private void isInstallApk(final String apkUrl){
        StringBuffer sb = new StringBuffer();
        sb.append("最新版本已下载, 是否现在安装?");
        Dialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("软件安装")
                .setMessage(sb.toString())
                        // 设置内容
                .setPositiveButton("安装",// 设置确定按钮
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                update(apkUrl);
                            }

                        })
                .setNegativeButton("暂不安装",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                // 点击"取消"按钮之后退出程序，仍能继续使用
//                               finish();
                            }
                        }).create();// 创建
        // 显示对话框
        dialog.show();
    }
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage: // 普通消息
            {
                runOnUiThread(new Runnable() {
                    public void run() {
                        updateNoReadNum();
                    }

                });
                break;
            }

            case EventOfflineMessage: {
                break;
            }

            case EventConversationListChanged: {
                break;
            }

            default:
                break;
        }
    }
}
