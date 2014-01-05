package il.ac.shenkar.myreminder.utils;

import il.ac.shenkar.myreminder.R;
import il.ac.shenkar.myreminder.activity.CreateTaskActivity;
import il.ac.shenkar.myreminder.activity.MainActivity;
import il.ac.shenkar.myreminder.handler.ReminderHandler;
import android.app.*;
import android.content.*;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class ReminderBroadcastReceiver extends BroadcastReceiver
{
	
	// on receive method
		public void onReceive(Context context, Intent intent)
		{
			Intent myIntent = new Intent(context, MainActivity.class);
			int id = intent.getIntExtra(CreateTaskActivity.EXTRA_ID, -1);
			String name = intent.getStringExtra(CreateTaskActivity.EXTRA_NAME);
			String action = intent.getAction();
			PendingIntent pendingIntent = null;
			LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			
			if (action.equals("il.ac.shenkar.myreminder.reminderByTime_broadcast"))
			{ // set time reminder
				pendingIntent = PendingIntent.getActivity(context, id, myIntent, 0);
			}
	
			if (action.equals("il.ac.shenkar.myreminder.reminderByLocation_broadcast"))
			{ // set location reminder
				final String key = LocationManager.KEY_PROXIMITY_ENTERING;	// enter = true	,	exit = false
				final Boolean entering = intent.getBooleanExtra(ReminderHandler.EXTRA_ENTERING, false); // what the user chose = Arrive or Leave
	
				if (key.equals("entering") && entering)
				{ // the reminder will take place when device enters the location 
					Toast.makeText(context, "entering", Toast.LENGTH_SHORT).show();
					pendingIntent = PendingIntent.getActivity(context, id, myIntent, 0);
					locationManager.removeUpdates(pendingIntent);
				} 
				else
				{  // the reminder will take place when device exits the location 
					Toast.makeText(context, "exiting", Toast.LENGTH_SHORT).show();
				}
				
			}
			// build a notification
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new NotificationCompat.Builder(context).setContentTitle("MyReminder").setContentText(name).setSmallIcon(R.drawable.ic_little_megaphone).setContentIntent(pendingIntent).build();
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;
			notificationManager.notify(id, notification);	
		}
}