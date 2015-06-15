package com.assignment1mg.rishabmangla.messagefor1mg.Receiver;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.assignment1mg.rishabmangla.messagefor1mg.MessageActivity;
import com.assignment1mg.rishabmangla.messagefor1mg.R;

public class MessageReceiver extends BroadcastReceiver {
    Context mContext;

    public MessageReceiver() {
    }

    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            if (sms != null) {
                String smsMessageStr = "";
                String smsBody = "";
                String address = "";
                for (int i = 0; i < sms.length; ++i) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                    smsBody = smsMessage.getMessageBody().toString();
                    address = smsMessage.getOriginatingAddress();

                    smsMessageStr += address + "\n";
                    smsMessageStr += smsBody + "\n";
                }
                notify(address, smsBody);
                Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();
            }
        }



        //updating list logic yet to be written
    }
    public void notify(String address, String text){
        Intent intent = new Intent(mContext, MessageActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(address)
                        .setContentText(text)
                .setContentIntent(pIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.i("rishab", "notify ");
        mNotificationManager.notify(0, mBuilder.build());
    }
}
