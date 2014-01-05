package il.ac.shenkar.myreminder.adapter;

import il.ac.shenkar.myreminder.R;
import il.ac.shenkar.myreminder.activity.CreateTaskActivity;
import il.ac.shenkar.myreminder.dao.TasksDataBase;
import il.ac.shenkar.myreminder.entities.TaskItem;
import il.ac.shenkar.myreminder.utils.UtilitiesFunctions;

import java.util.ArrayList;

import com.google.analytics.tracking.android.Tracker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class TasksListBaseAdapter extends BaseAdapter {
	
	// define
		private static ArrayList<TaskItem> tasksArrayList;
		private LayoutInflater l_Inflater;
		private Context context;
		TaskItem selectedTaskItem;
		private TasksDataBase tasksDataBase;
		private Tracker tracker;
	
	// constructor
		public TasksListBaseAdapter(Context context, Tracker mainTracker, TasksDoneListBaseAdapter tasksDoneListBaseAdapter)
		{
			this.tracker = mainTracker;
			this.context = context;
			this.tasksDataBase = TasksDataBase.getInstance(context);
			tasksArrayList = this.tasksDataBase.getTasksList();
			l_Inflater = LayoutInflater.from(context);
			this.selectedTaskItem = new TaskItem();
		}

	// get list count
		public int getCount()
		{
			return tasksArrayList.size();
		}

	// get task item
		public Object getItem(int position)
		{
			return tasksArrayList.get(position);
		}

	// get task id
		public long getItemId(int position)
		{
			return position;
		}

	// get view
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder;
			if (convertView == null)
			{
				convertView = l_Inflater.inflate(R.layout.task_details_view, null);
				holder = new ViewHolder();
				holder.taskName = (TextView) convertView.findViewById(R.id.task_name);
				holder.taskDescription = (TextView) convertView.findViewById(R.id.task_description);
				holder.taskCheckBoxDone = (CheckBox) convertView.findViewById(R.id.done_checkbox);
				holder.taskDeleteButton = (ImageButton) convertView.findViewById(R.id.delete_button);
				holder.taskReminderTime = (TextView) convertView.findViewById(R.id.textView_timeReminder_listView);
				holder.taskReminderLocation = (TextView) convertView.findViewById(R.id.textView_locationReminder_listView);
				convertView.setClickable(true);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			TaskItem taskItem = tasksArrayList.get(position);
			holder.taskName.setText(taskItem.getName());
			holder.taskDescription.setText(taskItem.getDescription());
			holder.taskCheckBoxDone.setChecked(taskItem.getDoneFlag());
		
			// set reminder by time
			if(taskItem.getTimeReminderFlag())
			{
				holder.taskReminderTime.setText(UtilitiesFunctions.fromCalendarToString(taskItem.getReminderDate()));
				holder.taskReminderTime.setVisibility(View.VISIBLE);
			}
			else
			{
				holder.taskReminderTime.setText("");
				holder.taskReminderTime.setVisibility(View.GONE);
			}
			// set reminder by location
			if(taskItem.getLocationReminderFlag())
			{
				holder.taskReminderLocation.setText(taskItem.getLocation());
				holder.taskReminderLocation.setVisibility(View.VISIBLE);
			}
			else 
			{ 
				holder.taskReminderLocation.setText("");
				holder.taskReminderLocation.setVisibility(View.GONE);
			}
			holder.taskName.setTag(taskItem.getID());
			holder.taskDescription.setTag(taskItem.getID());
			holder.taskReminderTime.setTag(taskItem.getID());
			holder.taskReminderLocation.setTag(taskItem.getID());
			holder.taskCheckBoxDone.setTag(taskItem.getID());
			
			convertView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					// fill the Task with the clicked task's details
					selectedTaskItem = (TaskItem) tasksArrayList.get(position);
					// create an intent to launch the "CreateTaskActivity"
					Intent intent = new Intent(context, CreateTaskActivity.class);
					intent.putExtra("taskObject", selectedTaskItem);
					context.startActivity(intent);
				}
			});	
			
			holder.taskCheckBoxDone.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
				
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
	        		if (tasksArrayList.get(position) != null)
	        		{				
	            		TaskItem taskItem = tasksArrayList.get(position);
	            		tracker.sendEvent("MainActivity", "change check box state", "task list", null);

            			taskItem.setDoneFlag(!taskItem.getDoneFlag());
            			TasksDataBase.getInstance(context).updateTaskItem(taskItem);
            			if (taskItem.getDoneFlag()==true)
            			{
            				tasksDataBase.moveTaskItemToDone(taskItem);
            				tasksArrayList = tasksDataBase.getTasksList();
            			}
            			notifyDataSetChanged();
            		}
                }
			
			});
			
			holder.taskDeleteButton.setOnClickListener(new OnClickListener() {
			
			// on click method	
				@Override
				public void onClick(View v) 
				{
					if (tasksArrayList.get(position) != null)
            		{
					TaskItem taskItem = tasksArrayList.get(position);
            		tracker.sendEvent("MainActivity", "delete task", "task list", null);
            		
            			taskItem.setDoneFlag(true);
            			tasksArrayList.remove(position);
            			TasksDataBase.getInstance(context).deleteTaskItem(taskItem);
           				notifyDataSetChanged();
            		}
				}
 			});
			return convertView;
		}
	
	// define view holder
		static class ViewHolder
		{
			public TextView taskName;
			TextView taskDescription;
			TextView taskReminderTime;
			TextView taskReminderLocation;
			CheckBox taskCheckBoxDone;
			ImageButton taskDeleteButton;
		}
}