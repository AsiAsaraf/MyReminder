package il.ac.shenkar.myreminder.handler;

import il.ac.shenkar.myreminder.entities.TaskItem;
import il.ac.shenkar.myreminder.utils.UtilitiesFunctions;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	// Database Version
		private static final int DATABASE_VERSION = 1;
	// Database Name
		private static final String DATABASE_NAME = "tasksManager";
	// Task Items table name
		private static final String TABLE_TASKS = "tasks";

	// Task Items Table Column name
		private static final String	KEY_ID							= "id";
		private static final String KEY_NAME						= "name";
		private static final String KEY_DESCRIPTION					= "description";
		private static final String KEY_CREATION_DATE				= "creationDate";
		private static final String	KEY_REMINDER_DATE				= "reminderDate";
		private static final String	KEY_LOCATION					= "location";
		
		private static final String	KEY_TIME_REMINDER_FLAG			= "timeReminderFlag";
		private static final String	KEY_LOCATION_REMINDER_FLAG		= "locationReminderFlag";
		private static final String	KEY_ARRIVE_FLAG					= "arriveFlag";
		private static final String	KEY_LEAVE_FLAG					= "leaveFlag";
		private static final String KEY_DONE_FLAG					= "doneFlag";
	
	// Task Items Table Column index
		private static final int COL_ID							= 0;
		private static final int COL_NAME						= 1;
		private static final int COL_DESCRIPTION				= 2;
		private static final int COL_CREATION_DATE				= 3;
		private static final int COL_REMINDER_DATE				= 4;
		private static final int COL_LOCATION					= 5;
		
		private static final int COL_TIME_REMINDER_FLAG			= 6;
		private static final int COL_LOCATION_REMINDER_FLAG		= 7;
		private static final int COL_ARRIVE_FLAG				= 8;
		private static final int COL_LEAVE_FLAG					= 9;
		private static final int COL_DONE_FLAG					= 10;
		
	// Constructor	
		public DatabaseHandler(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
	
	// Creating Tables
		public void onCreate(SQLiteDatabase db) {
			String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + "(" 
					+ KEY_ID						+	" INTEGER PRIMARY KEY," 
					+ KEY_NAME 						+ 	" TEXT NOT NULL,"
					+ KEY_DESCRIPTION 				+ 	" TEXT,"
					+ KEY_CREATION_DATE  			+ 	" INTEGER,"
					+ KEY_REMINDER_DATE 			+	" INTEGER,"
					+ KEY_LOCATION					+	" TEXT,"
					
					+ KEY_TIME_REMINDER_FLAG		+ 	" TEXT,"
					+ KEY_LOCATION_REMINDER_FLAG	+	" TEXT,"
					+ KEY_ARRIVE_FLAG				+ 	" TEXT,"
					+ KEY_LEAVE_FLAG				+	" TEXT," 
					+ KEY_DONE_FLAG   				+ 	" TEXT"
					+ ")";
			db.execSQL(CREATE_TASKS_TABLE);
		}

	// Upgrading database
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Drop older table if existed
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
	
			// Create tables again
			onCreate(db);
		}

	// Adding new taskItem
		public long addTaskItem(TaskItem taskItem) {
			SQLiteDatabase db = this.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			values.put(KEY_NAME, taskItem.getName());
			values.put(KEY_DESCRIPTION, taskItem.getDescription());
			values.put(KEY_CREATION_DATE, taskItem.getCreationDate().getTimeInMillis());
			values.put(KEY_REMINDER_DATE, taskItem.getReminderDate().getTimeInMillis());
			values.put(KEY_LOCATION, taskItem.getLocation());
			
			values.put(KEY_TIME_REMINDER_FLAG, String.valueOf(taskItem.getTimeReminderFlag()));			
			values.put(KEY_LOCATION_REMINDER_FLAG, String.valueOf(taskItem.getLocationReminderFlag()));
			values.put(KEY_ARRIVE_FLAG, String.valueOf(taskItem.getArriveFlag()));
			values.put(KEY_LEAVE_FLAG, String.valueOf(taskItem.getLeaveFlag()));
			values.put(KEY_DONE_FLAG, String.valueOf(taskItem.getDoneFlag()));
			
			long generatedID = db.insert(TABLE_TASKS, null, values);
			db.close();
			
			return generatedID;
		}

	// Getting single taskItem
		TaskItem getTaskItem(int id) {
			SQLiteDatabase db = this.getReadableDatabase();
			
			Cursor cursor = db.query(TABLE_TASKS,
									 new String[] { KEY_ID,
													KEY_NAME,
													KEY_DESCRIPTION, 
													KEY_CREATION_DATE, 
													KEY_REMINDER_DATE,
													KEY_LOCATION,
													
													KEY_TIME_REMINDER_FLAG, 
													KEY_LOCATION_REMINDER_FLAG,
													KEY_ARRIVE_FLAG,
													KEY_LEAVE_FLAG,
													KEY_DONE_FLAG
													},
									 KEY_ID + "=?",
									 new String[] {String.valueOf(id)}, null, null, null, null);
			if(cursor != null)
			{
				cursor.moveToFirst();
			}
			
			TaskItem taskItem = new TaskItem();
			taskItem.setID(cursor.getLong(COL_ID));
			taskItem.setName(cursor.getString(COL_NAME));
			taskItem.setDescription(cursor.getString(COL_DESCRIPTION));
			taskItem.setCreationDate(cursor.getLong(COL_CREATION_DATE));
			taskItem.setReminderDate(cursor.getLong(COL_REMINDER_DATE));
			taskItem.setLocation(cursor.getString(COL_LOCATION));
			
			taskItem.setTimeReminderFlag(UtilitiesFunctions.stringToBoolean(cursor.getString(COL_TIME_REMINDER_FLAG)));
			taskItem.setLocationReminderFlag(UtilitiesFunctions.stringToBoolean(cursor.getString(COL_LOCATION_REMINDER_FLAG)));
			taskItem.setArriveFlag(UtilitiesFunctions.stringToBoolean(cursor.getString(COL_ARRIVE_FLAG)));
			taskItem.setLeaveFlag(UtilitiesFunctions.stringToBoolean(cursor.getString(COL_LEAVE_FLAG)));	
			taskItem.setDoneFlag(UtilitiesFunctions.stringToBoolean(cursor.getString(COL_DONE_FLAG)));
			
			cursor.close();
			db.close();
			return taskItem;
		}
	
	// Getting All Task Items
		public ArrayList<TaskItem> getAllTaskItems() 
		{
			ArrayList<TaskItem> tasksList = new ArrayList<TaskItem>();
			
			String selectQuery = "SELECT * FROM " + TABLE_TASKS;
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			
			// looping trough all raws and adding to list
			if(cursor.moveToFirst())
			{
				do
				{
					TaskItem taskItem = taskFromCursor(cursor);
					if (!taskItem.getDoneFlag())
						tasksList.add(taskItem);
				} while(cursor.moveToNext());
			}	
			cursor.close();
			db.close();
			return tasksList;
		}

	// Getting All Done Task Items
		public ArrayList<TaskItem> getAllTasksDoneItems() 
		{			
			ArrayList<TaskItem> tasksDoneList = new ArrayList<TaskItem>();
			
			String selectQuery = "SELECT * FROM " + TABLE_TASKS;
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			
			// looping trough all raws and adding to list
			if(cursor.moveToFirst())
			{
				do
				{
					TaskItem taskItem = taskFromCursor(cursor);
					if (taskItem.getDoneFlag())
						tasksDoneList.add(taskItem);
				} while(cursor.moveToNext());
			}	
			cursor.close();
			db.close();
			return tasksDoneList;
		}	
		
	// Updating single task Item
		public int updateTaskItem(TaskItem taskItem) 
		{
			SQLiteDatabase db = this.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			values.put(KEY_NAME, taskItem.getName());
			values.put(KEY_DESCRIPTION, taskItem.getDescription());
			values.put(KEY_CREATION_DATE, taskItem.getCreationDate().getTimeInMillis());
			values.put(KEY_REMINDER_DATE, taskItem.getReminderDate().getTimeInMillis());
			values.put(KEY_LOCATION, taskItem.getLocation());
			
			values.put(KEY_TIME_REMINDER_FLAG, String.valueOf(taskItem.getTimeReminderFlag()));
			values.put(KEY_LOCATION_REMINDER_FLAG, String.valueOf(taskItem.getLocationReminderFlag()));
			values.put(KEY_ARRIVE_FLAG, String.valueOf(taskItem.getArriveFlag()));
			values.put(KEY_LEAVE_FLAG, String.valueOf(taskItem.getLeaveFlag()));
			values.put(KEY_DONE_FLAG, String.valueOf(taskItem.getDoneFlag()));
			
			// updating raw
			int numOfAffectedRows =  db.update(TABLE_TASKS, values, KEY_ID + " = ?",
	                new String[] { String.valueOf(taskItem.getID()) });
			db.close();
			
			return numOfAffectedRows;
		}

	//Deleting Task Item
		public void removeTask(TaskItem taskItem)
		{
			SQLiteDatabase db = this.getWritableDatabase();
			
			db.delete(TABLE_TASKS, KEY_ID + " =?",
					new String[] {String.valueOf(taskItem.getID())});
			db.close();
		}
		
		private TaskItem taskFromCursor(Cursor cursor) {
			
			TaskItem taskItem = new TaskItem();
			taskItem.setID(cursor.getLong(COL_ID));
			taskItem.setName(cursor.getString(COL_NAME));
			taskItem.setDescription(cursor.getString(COL_DESCRIPTION));
			taskItem.setCreationDate(cursor.getLong(COL_CREATION_DATE));
			taskItem.setReminderDate(cursor.getLong(COL_REMINDER_DATE));
			taskItem.setLocation(cursor.getString(COL_LOCATION));
			
			taskItem.setTimeReminderFlag(UtilitiesFunctions.stringToBoolean(cursor.getString(COL_TIME_REMINDER_FLAG)));
			taskItem.setLocationReminderFlag(UtilitiesFunctions.stringToBoolean(cursor.getString(COL_LOCATION_REMINDER_FLAG)));
			taskItem.setArriveFlag(UtilitiesFunctions.stringToBoolean(cursor.getString(COL_ARRIVE_FLAG)));
			taskItem.setLeaveFlag(UtilitiesFunctions.stringToBoolean(cursor.getString(COL_LEAVE_FLAG)));
			taskItem.setDoneFlag(UtilitiesFunctions.stringToBoolean(cursor.getString(COL_DONE_FLAG)));
			
			return taskItem;
		}

}