package com.cn7782.management.android.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.cn7782.management.R;
import com.cn7782.management.android.BaseActivity;
import com.cn7782.management.android.activity.controller.BitmapWorkerTask;
import com.cn7782.management.android.activity.service.TimingService;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.config.ActionUrl;
import com.cn7782.management.util.ActivityUtil;
import com.cn7782.management.util.MedieaPlayerUtil;
import com.cn7782.management.util.PictureUtil;
import com.cn7782.management.util.SharedPreferenceUtil;
import com.cn7782.management.util.SoundMeter;
import com.cn7782.management.util.TimeUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class EventReportActivity2 extends BaseActivity implements OnClickListener,
        OnGetGeoCoderResultListener {
    // 添加图片布局
    private LinearLayout mAddPhotoLayout;
    private String PhotoPath;
    private long startVoiceT;
    private int secNum = 0;// 已经开始的秒数
    // 录音文件
    private String voiceName = "";
    private File recordFile;
    private SoundMeter mSensor;
    ImageView r_volume, l_volume;
    //图片输入流集合
    private ArrayList<InputStream> photos = null;
    private ProgressDialog progressDialog;
    // 选择
    public static final int EVENT_TYPE_EVENT = 1002;
    public static final int EVENT_TYPE_DISPUTES = 1003;
    public static final int EVENT_TYPE_ROAD = 1004;
    // 1为社会治安 2为矛盾纠纷 3线路
    private int title_type = 1;

    private TextView title, eventTypetext, timeNow, address, process;

    private int type = 1;

    // 定时器
    private Timer timer;
    private TextView textTime;

    private String eventType = "";
    ;
    private TextView textTime2;

    Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    adapter.notifyDataSetChanged();
                    break;
            }
        }

        ;
    };
    GeoCoder mSearch = null; // 搜索模块
    private boolean ischoose = true;
    private MedieaPlayerUtil medieaPlayerUtil;
    //选择上报类型
    private PopupWindow popup;
    //拍照或从相册中选择
    private GridView noScrollgridview;
    private GridAdapter adapter;
    private View parentView;

    public static final int REQUEST_IMAGE = 1000;

    private ArrayList<String> paths = new ArrayList<String>();
    //增加录制视频上报
    private ImageView videoContent;
    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_event_report2);

        findId();
        initView();
        setOnClick();
        initPopView();
    }

    private void findId() {

        title = (TextView) findViewById(R.id.title_text);
        eventTypetext = (TextView) findViewById(R.id.event_type);
        timeNow = (TextView) findViewById(R.id.time_now);
        textTime = (TextView) findViewById(R.id.timer_now);
        address = (TextView) findViewById(R.id.address);
        process = (TextView) findViewById(R.id.process);
        videoContent = (ImageView) findViewById(R.id.chatting_content_iv);
    }

    private void initView() {
        medieaPlayerUtil = new MedieaPlayerUtil(this);
        double Latitude = TimingService.nLatitude;
        double Longitude = TimingService.nLongitude;
        LatLng pt = new LatLng(Latitude, Longitude);
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        // 反Geo搜索
        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(pt));
        progressDialog = new ProgressDialog(this);
        mSensor = new SoundMeter();
        SimpleDateFormat aDateFormat = new SimpleDateFormat(
                "yyyy/MM/dd HH:mm:ss");
        String day = aDateFormat.format(new java.util.Date());
        timeNow.setText(day);

        final View view = this.getLayoutInflater().inflate(
                R.layout.layout_dialog_eventtype, null);

        popup = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, true);
        popup.setFocusable(true);
        popup.setOutsideTouchable(true);
        popup.setBackgroundDrawable(new BitmapDrawable());
        WindowManager wm = this.getWindowManager();
        final int width = wm.getDefaultDisplay().getWidth();
        String preference = SharedPreferenceUtil.getValue(
                PreferenceConstant.MARK_ID_TABLE,
                PreferenceConstant.EVENTREPORT, EventReportActivity2.this);
        String isPatroling = getIntent().getStringExtra("isPatroling");

        if (!TextUtils.isEmpty(preference)) {
            if (preference.endsWith("1")) {
                title_type = 1;
                ((TextView) findViewById(R.id.title_text))
                        .setText("社会治安及重点地区整治");
            } else if (preference.endsWith("2")) {
                ((TextView) findViewById(R.id.title_text)).setText("矛盾纠纷排查");
                title_type = 2;
            } else if (preference.endsWith("3")) {
                ((TextView) findViewById(R.id.title_text)).setText("线路案(事)件");
                title_type = 3;
            }
        }
        //yes为巡防定位中跳进该页面
        if (!TextUtils.isEmpty(isPatroling)) {
            if (isPatroling.equals("yes")) {
                ((TextView) findViewById(R.id.title_text)).setText("线路案(事)件");
                title_type = 3;
            }
        }
        findViewById(R.id.title_text).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                int xoff = width / 3
                        - ((TextView) findViewById(R.id.title_text)).getWidth()
                        / 2;
                popup.update();
                popup.showAsDropDown(findViewById(R.id.title_text), -xoff, 0);
            }
        });
        ((View) view.findViewById(R.id.zhian))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        ((TextView) findViewById(R.id.title_text))
                                .setText("社会治安及重点地区整治");
                        title_type = 1;
                        eventTypetext.setText("选择事件类型");
                        SharedPreferenceUtil.modify(
                                PreferenceConstant.MARK_ID_TABLE,
                                PreferenceConstant.EVENTREPORT, "1",
                                EventReportActivity2.this);
                        popup.dismiss();
                    }
                });
        ((View) view.findViewById(R.id.maodun))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        ((TextView) findViewById(R.id.title_text))
                                .setText("矛盾纠纷排查");
                        title_type = 2;
                        eventTypetext.setText("选择事件类型");
                        SharedPreferenceUtil.modify(
                                PreferenceConstant.MARK_ID_TABLE,
                                PreferenceConstant.EVENTREPORT, "2",
                                EventReportActivity2.this);
                        popup.dismiss();
                    }
                });
        ((View) view.findViewById(R.id.shijian))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        ((TextView) findViewById(R.id.title_text))
                                .setText("线路案(事)件");
                        title_type = 3;
                        eventTypetext.setText("选择事件类型");
                        SharedPreferenceUtil.modify(
                                PreferenceConstant.MARK_ID_TABLE,
                                PreferenceConstant.EVENTREPORT, "3",
                                EventReportActivity2.this);
                        popup.dismiss();
                    }
                });
    }

    private void setOnClick() {
        findViewById(R.id.event_delete).setOnClickListener(this);
        findViewById(R.id.choose_address).setOnClickListener(this);
        findViewById(R.id.take_photo).setOnClickListener(this);
        findViewById(R.id.sumbit_btn).setOnClickListener(this);
        findViewById(R.id.event_play).setOnClickListener(this);
        findViewById(R.id.event_recording).setOnClickListener(this);
        findViewById(R.id.my_report).setOnClickListener(this);
        findViewById(R.id.title_back).setOnClickListener(this);
        findViewById(R.id.choose_event_type).setOnClickListener(this);
        findViewById(R.id.take_video).setOnClickListener(this);
    }

    private void initPopView() {

        parentView = getLayoutInflater().inflate(R.layout.activity_event_report, null);

        noScrollgridview = (GridView) findViewById(R.id.scrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                //添加预览
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            switch (resultCode) {
                case SelectTypeActivity.EVENT_TYPE_RETURNQ:
                    Bundle b = data.getExtras();
                    eventType = b.getString("eventType");
                    String name = b.getString("name");
                    if (name.equals("矛盾纠纷排查")) {
                        title_type = 2;
                    } else if (name.equals("社会治安及重点地区整治")) {
                        title_type = 1;
                    } else if (name.equals("线路案(事)件")) {
                        title_type = 3;
                    }
                    eventTypetext.setText("选择事件类型");
                    title.setText(name);
                    break;
                case SelectTypeActivity.EVENT_TYPE_RETURNA:
                    Bundle ab = data.getExtras();
                    String mname = ab.getString("name");
                    eventType = ab.getString("eventType");
                    eventTypetext.setText(mname);
                    break;
                case ChooseAddressActivity.ADDRESS:
                    Bundle abc = data.getExtras();
                    String maddress = abc.getString("address");
                    address.setText(maddress);
                    break;
                case TakeVideoActivity.VIDEOPATH:
                    Bundle abcd = data.getExtras();
                    videoPath = abcd.getString("videoPath");
                    //播放该视频
//                    MedieaPlayerUtil.playVideo(EventReportActivity2.this,videoPath);
                    MedieaPlayerUtil.showVideoThumbView(videoPath, videoContent);
                    break;
            }
            return;
        }
        if (resultCode == RESULT_OK) {
            paths = data.getStringArrayListExtra("data");
            adapter.update();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.event_delete:
                if (recordFile != null && recordFile.exists()) {
                    recordFile.delete();
                    secNum = 0;
                    textTime.setText("00:00:00");
                }
                break;
            case R.id.event_play:
                if (null == medieaPlayerUtil) {
                    medieaPlayerUtil = new MedieaPlayerUtil(this);
                }
                if (recordFile != null && recordFile.exists()) {
                    String url = recordFile.getPath();
                    medieaPlayerUtil.playFilepath(url);
                }
                break;
            case R.id.title_back:
                onKeyDown(KeyEvent.KEYCODE_BACK, null);
                break;
            case R.id.take_photo:
                //启动
                Intent intent0 = new Intent(EventReportActivity2.this, MediaChoseActivity.class);
                intent0.putExtra("chose_mode", 1);
                if (paths.size() > 0) {
                    intent0.putStringArrayListExtra("paths", paths);
                }
                startActivityForResult(intent0, REQUEST_IMAGE);
                break;
            case R.id.sumbit_btn:
                try {
                    submit();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.event_recording:
                showRecoredDialg();
                break;
            case R.id.title_text:
                Intent intent = new Intent(EventReportActivity2.this,
                        SelectTypeActivity.class);
                intent.putExtra("event_type", 0);
                startActivityForResult(intent, 1);
                break;
            case R.id.my_report:
                Intent intent2 = new Intent(EventReportActivity2.this,
                        MyReportActivity.class);
                startActivity(intent2);
                break;
            case R.id.choose_event_type:
                Intent intent3 = new Intent(EventReportActivity2.this,
                        SelectTypeActivity.class);
                if (title_type == 1) {
                    intent3.putExtra("event_type", EVENT_TYPE_EVENT);
                } else if (title_type == 2) {
                    intent3.putExtra("event_type", EVENT_TYPE_DISPUTES);
                } else if (title_type == 3) {
                    intent3.putExtra("event_type", EVENT_TYPE_ROAD);
                }
                startActivityForResult(intent3, 1);
                break;
            case R.id.choose_address:
                Intent intent4 = new Intent(EventReportActivity2.this,
                        ChooseAddressActivity.class);
                startActivityForResult(intent4, 1);
                break;
            case R.id.take_video:
                Intent takeVideo = new Intent(EventReportActivity2.this,
                        TakeVideoActivity.class);
                startActivityForResult(takeVideo, 1);
                break;
            default:
                break;
        }

    }

    private Map<String, String> changetomap() {
        Map<String, String> mapParams = new HashMap<String, String>();
        String tokenId = SharedPreferenceUtil.getValue(
                PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.MARK_ID,
                EventReportActivity2.this);
        String grid_id = SharedPreferenceUtil.getValue(
                PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.GRID_ID,
                EventReportActivity2.this);
        String str = title.getText().toString();

        String time = timeNow.getText().toString();
        String maddress = address.getText().toString();
        String mprocess = process.getText().toString();
        type = returnValue(str);
        mapParams.put("token_id", tokenId);
        mapParams.put("gridId", grid_id);
        if (eventType.equals("")) {
            ischoose = false;
        } else {
            ischoose = true;
        }
        if (type == 1) {
            mapParams.put("place", maddress);
            mapParams.put("securityName", eventType);
            mapParams.put("scope", mprocess);
        } else if (type == 2) {
            mapParams.put("eventName", "");
            mapParams.put("occurLocation", maddress);
            mapParams.put("eventType", eventType);
            mapParams.put("eventRemark", mprocess);
            mapParams.put("isSerious", "");
        } else if (type == 3) {
            mapParams.put("roadLineType", eventType);
            mapParams.put("areaName", maddress);
            mapParams.put("scope", mprocess);
        }
        //增加上传经纬度参数
        mapParams.put("latitude", TimingService.nLatitude + "");
        mapParams.put("longitude", TimingService.nLongitude + "");
        return mapParams;
    }

    // 从滑动View获取照片文件
    public void GetViewPhoto() {

        if (photos == null) {
            photos = new ArrayList<InputStream>();
        }
        photos.clear();
        if (paths.size() > 0) {
            ByteArrayOutputStream baos = null;
            ByteArrayInputStream isBm = null;
            String filePath = null;
            Bitmap image = null;
            for (int i = 0; i < paths.size(); i++) {
                baos = new ByteArrayOutputStream();
                filePath = paths.get(i);
                //将图片缩小为原来的  1/size ,不然图片很大时会报内存溢出错误
                image = PictureUtil.getSmallBitmap(filePath);
                //将bitmap一字节流输出 Bitmap.CompressFormat.PNG 压缩格式，100：压缩率，baos：字节流
                image.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] buffer = baos.toByteArray();
                // 把压缩后的数据baos存放到ByteArrayInputStream中
                isBm = new ByteArrayInputStream(buffer);
                photos.add(isBm);

                try {
                    isBm.close();
                    baos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            filePath = null;
            image = null;
        }
    }

    private void submit() throws FileNotFoundException {
        Map<String, String> mapParams = changetomap();
        if (!ischoose) {
            Toast.makeText(EventReportActivity2.this, "选择类型不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        //并未立即弹出对话框
        showProgressDialog("上传中");
        GetViewPhoto();
        RequestParams param = new RequestParams(mapParams);
        int b = 0;
        if (photos != null) {
            for (int i = 0; i < photos.size(); i++) {
                int a = i + 1;
                b = a;
                param.put("file" + a, photos.get(i));
            }
        }
        //添加录音内容，通过文件后缀识别
        if (recordFile != null) {
            if (recordFile.exists()) {
                param.put("file" + (++b), recordFile);
            }
        }
        //添加视频内容
        if (videoPath != null) {
            File video = new File(videoPath);
            if (video.exists()) {
                param.put("file" + (++b), video);
            }
        }
        String url = "";
        if (type == 1) {
            url = ActionUrl.PUPLIC_SECURITY;
        } else if (type == 2) {
            url = ActionUrl.DISPUTE;
        } else if (type == 3) {
            url = ActionUrl.PROTECT;
        }

        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setTimeout(120 * 1000);
        httpClient.post(url, param, new JsonHttpResponseHandler() {

            public void onSuccess(int statusCode, JSONObject response) {
                super.onSuccess(statusCode, response);
                if (statusCode == 200) {
                    Toast.makeText(EventReportActivity2.this, "上报成功!",
                            Toast.LENGTH_SHORT).show();
                }
                //清除录音
                if (recordFile != null && recordFile.exists()) {
                    recordFile.delete();
                    secNum = 0;
                    textTime.setText("00:00:00");
                }
                //清除文字
                process.setText("");
                //上传成功后清除图片缓存
                paths.clear();
                progressDialog.cancel();
                String isPatroling = getIntent().getStringExtra("isPatroling");
                String id = "";
                try {
                    if (response.has("msg")) {
                        String msg = response.isNull("msg") ? "" : response
                                .getString("msg");
                        if (msg.equals("登陆成功")) {
                            id = response.isNull("id") ? "" : response
                                    .getString("id");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(isPatroling)
                        && isPatroling.equals("yes")) {
                    Intent data = new Intent();
                    Bundle b = new Bundle();
                    b.putString("event_id", id);
                    data.putExtras(b);
                    setResult(PatrolingActivity.ISPATROLING, data);
                    finish();
                } else {
                    Intent intent2 = new Intent(EventReportActivity2.this,
                            MyReportActivity.class);
                    startActivity(intent2);
                    finish();
                }
            }

            @Override
            public void onFailure(Throwable arg0, String arg1) {
                progressDialog.cancel();
                super.onFailure(arg0, arg1);
            }
        });
    }

    /***
     * 弹出录音对话框
     */
    private void showRecoredDialg() {

        final Dialog dialog = new Dialog(this, R.style.MenuDialogStyle);

        View addView = LayoutInflater.from(this).inflate(
                R.layout.record_voice_dialog_layout, null);
        // 设置它的ContentView
        dialog.setContentView(addView);

        WindowManager.LayoutParams localLayoutParams = dialog.getWindow()
                .getAttributes();
        localLayoutParams.x = 0;
        localLayoutParams.y = -1000;
        localLayoutParams.gravity = 80;
        addView.setMinimumWidth(10000);

        dialog.onWindowAttributesChanged(localLayoutParams);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);

        dialog.show();

        Button btnOK, btnCancel;

        btnOK = (Button) addView.findViewById(R.id.btnOK);
        btnCancel = (Button) addView.findViewById(R.id.btnCancel);
        l_volume = (ImageView) addView.findViewById(R.id.l_volume);
        r_volume = (ImageView) addView.findViewById(R.id.r_volume);
        textTime2 = (TextView) addView.findViewById(R.id.timerr_now);
        if (recordFile != null && recordFile.exists()) {
            recordFile.delete();
            secNum = 0;
        }
        startVoiceT = System.currentTimeMillis();
        voiceName = startVoiceT + ".amr";
        ActivityUtil.creatfile();
        recordFile = new File(ActivityUtil.getStorageDirectory()
                + File.separator + voiceName);
        if (recordFile.exists()) {
            recordFile.delete();
        }
        start(voiceName);
        initTimer();
        btnOK.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                timer.cancel();
                // 在这里判断时间是否太短
                dialog.dismiss();
                stop();

            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                voiceName = "";
                dialog.dismiss();
                stop();
                timer.cancel();
            }
        });
    }

    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getAmplitude();
            updateDisplay(amp);
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);

        }
    };
    private static final int POLL_INTERVAL = 300;

    private void start(String name) {
        mSensor.start(name);
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }

    private void stop() {
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();
        l_volume.setImageResource(R.drawable.amp1);
        r_volume.setImageResource(R.drawable.amp_r_1);
    }

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            stop();
        }
    };

    /***
     * @param signalEMA
     */
    private void updateDisplay(double signalEMA) {

        switch ((int) signalEMA) {
            case 0:
                l_volume.setImageResource(R.drawable.amp1);
                r_volume.setImageResource(R.drawable.amp_r_1);
                break;
            case 1:
                l_volume.setImageResource(R.drawable.amp2);
                r_volume.setImageResource(R.drawable.amp_r_2);
                break;
            case 2:
                l_volume.setImageResource(R.drawable.amp3);
                r_volume.setImageResource(R.drawable.amp_r_3);
                break;
            case 3:
            case 4:
                l_volume.setImageResource(R.drawable.amp4);
                r_volume.setImageResource(R.drawable.amp_r_4);
                break;
            case 5:
                l_volume.setImageResource(R.drawable.amp5);
                r_volume.setImageResource(R.drawable.amp_r_5);
                break;
            case 6:
                l_volume.setImageResource(R.drawable.amp6);
                r_volume.setImageResource(R.drawable.amp_r_6);
                break;
            case 7:
                l_volume.setImageResource(R.drawable.amp7);
                r_volume.setImageResource(R.drawable.amp_r_7);
                break;
            case 8:
            case 9:
                l_volume.setImageResource(R.drawable.amp8);
                r_volume.setImageResource(R.drawable.amp_r_8);
                break;
            case 10:
            case 11:
                l_volume.setImageResource(R.drawable.amp9);
                r_volume.setImageResource(R.drawable.amp_r_9);
                break;
            default:
                l_volume.setImageResource(R.drawable.amp10);
                r_volume.setImageResource(R.drawable.amp_r_10);
                break;
        }
    }

    /***
     * @param message
     */
    public void showProgressDialog(String message) {
        if (null == progressDialog) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    private int returnValue(String str) {
        if (str.equals("社会治安及重点地区整治")) {
            return 1;
        } else if (str.equals("矛盾纠纷排查")) {
            return 2;
        } else if (str.equals("线路案(事)件")) {
            return 3;
        } else {
            return 0;
        }
    }

    // 计算时间
    private void excuteTask() {

        secNum++;
        textTime.setText("" + secNum);
        textTime.setText(TimeUtil.formatTimeTextDisplay(secNum));
        textTime2.setText("" + secNum);
        textTime2.setText(TimeUtil.formatTimeTextDisplay(secNum));
    }

    private void initTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        excuteTask();
                    }
                });
            }
        }, 1000, 1000);
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult arg0) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(EventReportActivity2.this, "抱歉，未能找到结果",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        ((TextView) findViewById(R.id.address)).setText(result.getAddress());

    }

    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            return paths.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //压缩加载进内存
//			holder.image.setImageBitmap(PictureUtil.getSmallBitmap(paths.get(position)));
            BitmapWorkerTask task = new BitmapWorkerTask(holder.image);
            task.execute(paths.get(position));
            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        public void loading() {
            Message message = new Message();
            message.what = 1;
            mHandler.sendMessage(message);
        }
    }

    @Override
    protected void onResume() {
        adapter.update();
        super.onResume();
    }

    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    private static final int TAKE_PICTURE = 0x000001;

    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            /*
			for(int i=0;i< PublicWay.activityList.size();i++){
				if (null != PublicWay.activityList.get(i)) {
					PublicWay.activityList.get(i).finish();
				}
			}
			*/
            finish();
        }
        return true;
    }
}