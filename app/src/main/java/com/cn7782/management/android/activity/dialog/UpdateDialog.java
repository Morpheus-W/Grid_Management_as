package com.cn7782.management.android.activity.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.cn7782.management.R;
import com.cn7782.management.config.ConfigUtil;

/**
 * Created by tangweny on 2015/12/17.
 */
public class UpdateDialog extends Dialog {

    private Context context;
    private Bundle bundle;
    private double newVerCode = 0;
    private long fileSize = 0;
    private String apkUrl = "";

    public UpdateDialog(Context context,int theme,Bundle bundle){
        super(context,theme);
        this.context = context;
        this.bundle = bundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg_dialog_layout);
        newVerCode = bundle.getDouble("versionCode");
        fileSize = bundle.getLong("fileSize");
        apkUrl = bundle.getString("apkUrl");
    }
    private void notNewVersionShow() {
        int verCode = ConfigUtil.getVerCode(context);
        String verName = ConfigUtil.getVerName(context);
        StringBuffer sb = new StringBuffer();
        sb.append("当前版本:");
        sb.append(verName);
        sb.append(" Code:");
        sb.append(verCode);
        sb.append(",\n已是最新版,无需更新!");

        Dialog dialog = new AlertDialog.Builder(context)
                .setTitle("软件更新").setMessage(sb.toString())// 设置内容
                .setPositiveButton("确定",// 设置确定按钮
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
//                                                            finish();
                            }

                        }).create();// 创建
        // 显示对话框
        dialog.show();
    }

}
