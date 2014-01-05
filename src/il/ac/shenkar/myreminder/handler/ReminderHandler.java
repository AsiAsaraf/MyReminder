package il.ac.shenkar.myreminder.handler;


import il.ac.shenkar.myreminder.entities.TaskItem;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;

public class ReminderHandler
{
	// define extra information
	public final static String EXTRA_NAME = "il.ac.shenkar.myreminder.NAME";	
		public final static String EXTRA_ID = "il.ac.shenkar.myreminder.ID";
		public final static String EXTRA_ENTERING = "il.ac.shenkar.myreminder.ENTERING";
		
	// define the interval time between requestLocationUpdates()
		public final static long MIN_TIME = 1000 * 360 * 60;	
	// define the minimum distance for requestLocationUpdates()
		public final static float MIN_DISTANCE = 300;		
		
		// intent for a reminder by time
		public final static String ALARM_INTENT = "il.ac.shenkar.myreminder.reminderByTime_broadcast";
		// intent for a reminder by location
		public final static String LOCATION_INTENT = "il.ac.shenkar.myreminder.reminderByLocation_broadcast";
		
	// create time reminder
		public static void setTimeReminder(Context context, TaskItem taskItem)
		{
			Intent intent = new Intent(ALARM_INTENT);
			intent.putExtra(EXTRA_NAME, taskItem.getName());
			intent.putExtra(EXTRA_ID, (int) taskItem.getID());
	
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) taskItem.getID(), intent, 0);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC_WAKEUP, taskItem.getReminderDate().getTimeInMillis(), pendingIntent);
		}
	
	// cancel time reminder
		public static void cancelTimeReminder(Context context, long id)
		{
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id, new Intent(ALARM_INTENT), 0);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(pendingIntent);		
		}
	
	// set proximity alert
		public static void setProximityAlert(Context context, TaskItem taskItem, double latitude, double longitude)
		{
			Intent intent = new Intent(LOCATION_INTENT);
			intent.putExtra(EXTRA_NAME, taskItem.getName());
			intent.putExtra(EXTRA_ID, (int) taskItem.getID());
			intent.putExtra(EXTRA_ENTERING, taskItem.getArriveFlag());
			
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ((int)taskItem.getID()), intent, 0);
			LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			
			if(taskItem.getArriveFlag())
			{ // set the notification to when the device arrives at the location
				locationManager.addProximityAlert(latitude, longitude, 100, -1, pendingIntent);
			}
			
			if(taskItem.getLeaveFlag())
			{ // set the notification to when the device leaves the location
				String provider = locationManager.getBestProvider(new Criteria(), false);
				locationManager.requestLocationUpdates(provider, MIN_TIME, MIN_DISTANCE, pendingIntent);
			}	
		}
		
	// cancel proximity alert
		public static void cancelProximityAlert(Context context, long id)
		{
			LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE); 
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ((int)id), new Intent(LOCATION_INTENT), 0);
			locationManager.removeProximityAlert(pendingIntent);
		}	
}