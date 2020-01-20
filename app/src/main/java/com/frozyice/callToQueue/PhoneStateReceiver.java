package com.frozyice.callToQueue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class PhoneStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                if (incomingNumber != null) {
                    Intent local = new Intent();
                    local.setAction("service.to.activity.transfer");
                    local.putExtra("number", incomingNumber);
                    context.sendBroadcast(local);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}

