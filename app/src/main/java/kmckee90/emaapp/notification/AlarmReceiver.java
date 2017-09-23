package kmckee90.emaapp.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent 
 * and then starts the IntentService {@code SchedulingService} to do some work.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
  //private PendingIntent alarmIntent1, alarmIntent2, alarmIntent3;
    private PendingIntent alarmIntentA, alarmIntentB, alarmIntentC;


    // The pending intent that is triggered when the alarm fires.
    //private PendingIntent alarmIntent;


    //Alarm time. Probably should have this set in main activity.
    public int min = 0;
    public int hour = 0;

    public void setAlarmTime(int h, int m){
        hour = h;
        min = m;
    }
    @Override
    public void onReceive(Context context, Intent intent) {   
        // BEGIN_INCLUDE(alarm_onreceive)
        /* 
         * If your receiver intent includes extras that need to be passed along to the
         * service, use setComponent() to indicate that the service should handle the
         * receiver's intent. For example:
         * 
         * ComponentName comp = new ComponentName(context.getPackageName(), 
         *      MyService.class.getName());
         *
         * // This intent passed in this call will include the wake lock extra as well as 
         * // the receiver intent contents.
         * startWakefulService(context, (intent.setComponent(comp)));
         * 
         * In this example, we simply create a new intent to deliver to the service.
         * This intent holds an extra identifying the wake lock.
         */

        Intent service = new Intent(context, SchedulingService.class);
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, service);
        // END_INCLUDE(alarm_onreceive)
    }

    // BEGIN_INCLUDE(set_alarm)
    /**
     * Sets a repeating alarm that runs once a day at approximately 8:30 a.m. When the
     * alarm fires, the app broadcasts an Intent to this WakefulBroadcastReceiver.
     * @param context
     */
    public void setAlarm(Context context) {

        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
/*
        alarmIntent1 = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmIntent2 = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmIntent3 = PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_CANCEL_CURRENT);
*/
        alarmIntentA = PendingIntent.getBroadcast(context, 3, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmIntentB = PendingIntent.getBroadcast(context, 4, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmIntentC = PendingIntent.getBroadcast(context, 5, intent, PendingIntent.FLAG_CANCEL_CURRENT);


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());



        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntentA);

        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntentB);

        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntentC);


/*
        calendar1.add(Calendar.SECOND, 5);
        alarmMgr.set(AlarmManager.RTC_WAKEUP,
                calendar1.getTimeInMillis(), alarmIntent1);

        calendar1.add(Calendar.SECOND, 10);
        alarmMgr.set(AlarmManager.RTC_WAKEUP,
                calendar1.getTimeInMillis(), alarmIntent2);

        calendar1.add(Calendar.SECOND, 15);
        alarmMgr.set(AlarmManager.RTC_WAKEUP,
                calendar1.getTimeInMillis(), alarmIntent3);
*/


        /* Wake up the device to fire a one-time alarm in one minute.
         * alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
         *         SystemClock.elapsedRealtime() +
         *         60*1000, alarmIntent);
         *        
         * Wake up the device to fire the alarm in 30 minutes, and every 30 minutes
         * after that.
         alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                 AlarmManager.INTERVAL_HALF_HOUR,
                 AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);


       alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
         */






/*         alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);
*/
        // Enable {@code BootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
    // END_INCLUDE(set_alarm)

    /**
     * Cancels the alarm.
     * @param context
     */
    // BEGIN_INCLUDE(cancel_alarm)
    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmMgr!= null) {
/*          alarmMgr.cancel(alarmIntent1);
            alarmMgr.cancel(alarmIntent2);
            alarmMgr.cancel(alarmIntent3);
*/
            alarmMgr.cancel(alarmIntentA);
            alarmMgr.cancel(alarmIntentB);
            alarmMgr.cancel(alarmIntentC);
        }

        // Disable {@code BootReceiver} so that it doesn't automatically restart the
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
    // END_INCLUDE(cancel_alarm)


}
