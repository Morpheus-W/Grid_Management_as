package com.cn7782.management.android.activity.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

public class AudioReceiver extends BroadcastReceiver {
    public AudioReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
            AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            final int ringerMode = am.getRingerMode();
            switch (ringerMode) {
                case AudioManager.RINGER_MODE_NORMAL:
                    //normal
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    //vibrate
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    //silent
                    break;
            }
        }
    }
}
