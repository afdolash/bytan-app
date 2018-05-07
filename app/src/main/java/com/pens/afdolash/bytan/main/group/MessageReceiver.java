package com.pens.afdolash.bytan.main.group;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.abemart.wroup.common.messages.MessageWrapper;
import com.pens.afdolash.bytan.R;

/**
 * Created by afdol on 5/4/2018.
 */

public class MessageReceiver extends BroadcastReceiver {
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_DEVICE = "device";
    public static final String EXTRA_TYPE = "device";
    public static final String TYPE_NORMAL = "type-normal";
    public static final String TYPE_WARNING = "type-warning";
    private final int NOTIF_ID_NORMAL = 100;
    private final int NOTIF_ID_WARNING = 101;

    public MessageReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String device = intent.getStringExtra(EXTRA_DEVICE);
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        String type = intent.getStringExtra(EXTRA_TYPE);
        int notifId = type.equalsIgnoreCase(TYPE_NORMAL) ? NOTIF_ID_NORMAL : NOTIF_ID_WARNING;
        showMessageNotification(context, device, message, notifId);
    }

    private void showMessageNotification(Context context, String device, String message, int notifId){
        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(device)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound);
        notificationManagerCompat.notify(notifId, builder.build());
    }

    public void setMessage(Context context, MessageWrapper messageWrapper, String type){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MessageReceiver.class);
        intent.putExtra(EXTRA_DEVICE, messageWrapper.getWroupDevice().getDeviceName().toString());
        intent.putExtra(EXTRA_MESSAGE, messageWrapper.getMessage().toString());
        intent.putExtra(EXTRA_TYPE, type);

        int requestCode = type.equalsIgnoreCase(TYPE_NORMAL) ? NOTIF_ID_NORMAL : NOTIF_ID_WARNING;
        PendingIntent pendingIntent =  PendingIntent.getBroadcast(context, requestCode, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP,0, pendingIntent);
        Toast.makeText(context, "Sended.", Toast.LENGTH_SHORT).show();
    }
}
