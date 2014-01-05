package il.ac.shenkar.myreminder.dao;


import il.ac.shenkar.myreminder.entities.TaskItem;
import il.ac.shenkar.myreminder.handler.DatabaseHandler;
import il.ac.shenkar.myreminder.handler.ReminderHandler;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.util.Log;
import android.widget.BaseAdapter;

public class TasksDataBase {

	// define
		private static TasksDataBase instance = null;
		private ArrayList<TaskItem>	tasksList;
		private ArrayList<TaskItem>	tasksDoneList;
		private BaseAdapter dataBaseAdapter;
		private Context	context;
		private DatabaseHandler	dataBaseHandler;

	// private constructor
		private TasksDataBase(Context context) 
		{
			this.setContext(context);
			this.dataBaseHandler = new DatabaseHandler(context);
			this.initTasksList();
			this.initTasksDoneList();
		}

	// getting instance of tasks data base class
		public static TasksDataBase getInstance(Context context)
		{
			if (instance == null)
				instance = new TasksDataBase(context);
			return instance;
		}
	
	// getting context
		public Context getContext()
		{
			return context;
		}

	// setting context
		public void setContext(Context context)
		{
			this.context = context;
		}	
		
	// getting data base adapter
		public BaseAdapter getDataBaseAdapter()
		{
			return dataBaseAdapter;
		}

	// setting data base adapter
		public void setAdapter(BaseAdapter dataBaseAdapter)
		{
			this.dataBaseAdapter = dataBaseAdapter;
		}
		
	// initializing list from data base
		public void initTasksList()
		{
			this.tasksList = new ArrayList<TaskItem>();
			this.tasksList = this.dataBaseHandler.getAllTaskItems();
		}
		
	// initializing done list from data base
		public void initTasksDoneList()
		{
			this.tasksDoneList = new ArrayList<TaskItem>();
			this.tasksDoneList = this.dataBaseHandler.getAllTasksDoneItems();
		}
	
	// Adding new taskItem
		public long addTaskItem(TaskItem taskItem) 
		{
			taskItem.setID(this.dataBaseHandler.addTaskItem(taskItem));
			this.tasksList.add(0, taskItem);
			Log.d("inside add task item method: ", "after notify change");
			
			return taskItem.getID();
		}

	// Getting single taskItem
		public TaskItem getTaskItem(long id) 
		{
			for(int i=0; i<this.tasksList.size(); i++)
			{
				if(this.tasksList.get(i).getID() == id)
				{
					return this.tasksList.get(i);
				}
			}
			return null;
		}
	
	// Getting tasks list
		public ArrayList<TaskItem> getTasksList() 
		{
			this.tasksList = this.dataBaseHandler.getAllTaskItems();
			return this.tasksList;
		}

	// Updating single task item
		public void updateTaskItem(TaskItem taskItem) 
		{
			this.dataBaseHandler.updateTaskItem(taskItem);
			this.tasksList.remove(getTaskItem(taskItem.getID()));
			this.tasksList.add(taskItem);
			Collections.sort(tasksList);
		}

	// Deleting single taskItem
		public void deleteTaskItem(TaskItem taskItem) 
		{
			try
			{
				if(taskItem.getTimeReminderFlag())
				{ // cancel reminder by time
					ReminderHandler.cancelTimeReminder(context, taskItem.getID());
				}
				if(taskItem.getLocationReminderFlag())
				{ // cancel reminder by location
					ReminderHandler.cancelProximityAlert(context, taskItem.getID());
				}
				this.tasksList.remove(taskItem);
				this.dataBaseHandler.removeTask(taskItem);
			}
			catch(UnsupportedOperationException e)
			{
				e.printStackTrace();
			}
		}

	// Getting all done tasks list
		public ArrayList<TaskItem> getTasksDoneList() 
		{ 			
			this.tasksDoneList = this.dataBaseHandler.getAllTasksDoneItems();
			return this.tasksDoneList;
		}		
		
	// getting tasks list item count 
		public int getCount()
		{
			return tasksList.size();
		}
		
	// checks if the tasks list is empty	
		public boolean isEmpty()
		{
			return tasksList.isEmpty();
		}

	// move task item from list to done list
		public void moveTaskItemToDone(TaskItem taskItem) {
			this.tasksList.remove(taskItem);
			this.tasksDoneList.add(taskItem);
		}

	// move task item from done list to list
		public void moveTaskItemFromDone(TaskItem taskItem) {
			this.tasksDoneList.remove(taskItem);
			this.tasksList.add(taskItem);
			
		}	
}