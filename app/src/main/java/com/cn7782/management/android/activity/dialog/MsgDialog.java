package com.cn7782.management.android.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.cn7782.management.R;
import com.cn7782.management.android.activity.ChatActivity;
import com.cn7782.management.android.activity.ConversationListActivity;
import com.easemob.easeui.EaseConstant;

public class MsgDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private Bundle bundle;
    public MsgDialog(Context context,int theme,Bundle bundle){
        super(context,theme);
        this.context = context;
        this.bundle = bundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg_dialog_layout);
        getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        TextView msgnum = (TextView)this.findViewById(R.id.noread_textview);
        msgnum.setText(bundle.getInt("num")+"");

        TextView lastcontent = (TextView)findViewById(R.id.newmsg_textview);
        lastcontent.setText(bundle.getString("lastContent"));
        lastcontent.setOnClickListener(this);

        TextView lasttime = (TextView)findViewById(R.id.lasttime_textview);
        lasttime.setText(bundle.getString("lastTime"));

        this.findViewById(R.id.close_imageview).setOnClickListener(this);
        this.findViewById(R.id.detail_layout).setOnClickListener(this);

        new MyCountDownTimer(5000,5000).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_imageview :
                dismiss();
                break;
            case R.id.newmsg_textview :
                Intent msgDetail = new Intent(context,ChatActivity.class);
                //该标志位表示如果Intent要启动的Activity在栈顶，则无须创建新的实例
                msgDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                msgDetail.putExtra(EaseConstant.EXTRA_USER_ID,
                        bundle.getString(EaseConstant.EXTRA_USER_ID));
                context.startActivity(msgDetail);
                dismiss();
                break;
            case R.id.detail_layout:
                Intent msg = new Intent(context, ConversationListActivity.class);
                //该标志位表示如果Intent要启动的Activity在栈顶，则无须创建新的实例
                msg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(msg);
                dismiss();
                break;
        }
    }
    private class MyCountDownTimer extends CountDownTimer{
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            dismiss();
            cancel();
        }
    }
}
