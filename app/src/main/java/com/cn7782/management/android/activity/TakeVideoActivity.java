package com.cn7782.management.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn7782.management.R;
import com.cn7782.management.view.MovieRecorderView;

public class TakeVideoActivity extends Activity implements View.OnClickListener {

    private MovieRecorderView mRecorderView;
    private TextView mShootBtn;
    private boolean isFinish = true;

    private ImageView mProgressBar;
    private Animation scaleAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takevideo);
        //声音模式 RINGER_MODE_NORMAL
        //静音模式 RINGER_MODE_SILENT
        //静音模式下对华为机型并不能取消摄像提示音
        //震动模式 RINGER_MODE_VIBRATE
//        AudioManager mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        mRecorderView = (MovieRecorderView) findViewById(R.id.movieRecorderView);
        mShootBtn = (TextView) findViewById(R.id.shoot_button);
        mShootBtn.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //设置动画时间
                    mProgressBar.startAnimation(scaleAnimation);
                    mRecorderView.record(new MovieRecorderView.OnRecordFinishListener() {
                        @Override
                        public void onRecordFinish() {
                            handler.sendEmptyMessage(1);
                        }
                    });
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (mRecorderView.getTimeCount() > 1)
//                        handler.sendEmptyMessage(1);
//                    else {
                        if (mRecorderView.getmVecordFile() != null)
                            mRecorderView.getmVecordFile().delete();
                        mRecorderView.stop();
                        scaleAnimation.cancel();
//                    }
                }
                return true;
            }
        });
        findViewById(R.id.title_back).setOnClickListener(this);

        mProgressBar = (ImageView) findViewById(R.id.myprogress);
        //初始化，中间向两边伸
        scaleAnimation = new ScaleAnimation(
                1.0f,//x轴 开始尺寸
                0.0f,//x轴 结束尺寸
                1.0f,//y轴 开始尺寸
                1.0f,//y轴 结束尺寸
                Animation.RELATIVE_TO_SELF, //后面四个参数确立中心点，开始或结束的中心点
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        scaleAnimation.setDuration(10000);
    }


    @Override
    public void onResume() {
        super.onResume();
        isFinish = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isFinish = false;
        mRecorderView.stop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            finishActivity();
        }
    };
    public static final int VIDEOPATH = 2001;
    private void finishActivity() {
        if (isFinish) {
            scaleAnimation.cancel();
            mProgressBar.setVisibility(View.GONE);
            mRecorderView.stop();
            Intent data = new Intent();
            Bundle b = new Bundle();
            b.putString("videoPath", mRecorderView.getmVecordFile().toString());
            data.putExtras(b);
            setResult(VIDEOPATH, data);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 录制完成回调
     *
     * @author liuyinjun
     *
     * @date 2015-2-9
     */
    public interface OnShootCompletionListener {
        public void OnShootSuccess(String path, int second);
        public void OnShootFailure();
    }
}