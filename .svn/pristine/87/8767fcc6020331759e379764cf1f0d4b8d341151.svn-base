package com.cn7782.management.android.activity.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cn7782.management.android.activity.service.MessengerService;
import com.cn7782.management.android.constants.PreferenceConstant;
import com.cn7782.management.util.SharedPreferenceUtil;

public class MsgReceiver extends BroadcastReceiver {
    public MsgReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String msgBack = SharedPreferenceUtil.getValue(
                PreferenceConstant.MARK_ID_TABLE, PreferenceConstant.IS_MSG_BACK,
                context);
        if (msgBack != null && msgBack.equals("true")) {
            Intent sevice = new Intent(context, MessengerService.class);
            context.startService(sevice);
        }

    }
}
