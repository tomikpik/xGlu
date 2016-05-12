package cz.eclub.xglu.xglu;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import cz.eclub.xglu.xglu.Database.MeasuringRecord;

/**
 * Created by Tom on 6. 2. 2016.
 */
public class Test extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int number = intent.getIntExtra("OGTT", -1);
        Log.d("OGTT", "onReceive: " + number);
        if(number==-1)return;

        if(number==-4){
            SharedPreferences sharedPref = context.getSharedPreferences("OGTT", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("OGTTactive",number);
            editor.commit();
            return;
        }
        Intent i = new Intent(context,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("OGTT", number);

        PendingIntent pi = PendingIntent.getActivity(context,(int) System.currentTimeMillis(), i, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(number+". OGTT measurement alarm")
                        .setContentText("Take measurement as soon as possible.")
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(Notification.CATEGORY_REMINDER)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                        .setContentIntent(pi)
                        .setAutoCancel(true);




        Notification n = mBuilder.build();


        n.defaults |= Notification.DEFAULT_VIBRATE;
        n.defaults |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, n);

        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
    }
}
