package il.ac.shenkar.myreminder.adapter;

import il.ac.shenkar.myreminder.R;
import il.ac.shenkar.myreminder.dao.TasksDataBase;
import il.ac.shenkar.myreminder.entities.TaskItem;
import il.ac.shenkar.myreminder.utils.UtilitiesFunctions;

import java.util.ArrayList;

import com.google.analytics.tracking.android.Tracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TasksDoneListBaseAdapter extends BaseAdapter
{
	// define	
		private static ArrayList<TaskItem> tasksDoneArrayList;
		private LayoutInflater l_Inflater;
		protected Context context;
		TaskItem selectedTaskItem;
		private TasksDataBase tasksDataBase;
		private Tracker tracker;
	

	// constructor
		public TasksDoneListBaseAdapter(Context context, Tracker mainTracker)
		{
			this.tracker = mainTracker;
			this.context = context;
			this.tasksDataBase = TasksDataBase.getInstance(context);
			if(this.tasksDataBase.getTasksDoneList() != null)
			{
				tasksDoneArrayList = this.tasksDataBase.getTasksDoneList();
			}
			else
			{
				tasksDoneArrayList = new ArrayList<TaskItem>();
			}
			l_Inflater = LayoutInflater.from(context);
			this.selectedTaskItem = new TaskItem();
		}

	// get list count
		public int getCount()
		{
			return tasksDoneArrayList.size();
		}

	// get task item
		public Object getItem(int position)
		{
			return tasksDoneArrayList.get(position);
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
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			TaskItem taskItem = tasksDoneArrayList.get(position);
			// set the done check box
			holder.taskCheckBoxDone.setChecked(taskItem.getDoneFlag());
			// set the name text
			holder.taskName.setText(taskItem.getName());
			// set the description text
			holder.taskDescription.setText(taskItem.getDescription());
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
			holder.taskCheckBoxDone.setTag(taskItem.getID());
			holder.taskDescription.setTag(taskItem.getID());
			holder.taskReminderTime.setTag(taskItem.getID());
			holder.taskReminderLocation.setTag(taskItem.getID());
			
			
			holder.taskCheckBoxDone.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
				
			// on checked changed method	
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	                {
						if (tasksDoneArrayList.get(position) != null)
	            		{
		            		TaskItem taskItem = tasksDoneArrayList.get(position);
		            		tracker.sendEvent("MainActivity", "change check box state", "done task list", null);
	            		
	            			taskItem.setDoneFlag(!taskItem.getDoneFlag());
	            			TasksDataBase.getInstance(context).updateTaskItem(taskItem);
	            			if (taskItem.getDoneFlag()==false)
	            			{
	            				tasksDataBase.moveTaskItemFromDone(taskItem);
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
            		if (tasksDoneArrayList.get(position) != null)
            		{
	            		TaskItem taskItem = tasksDoneArrayList.get(position);
	            		tracker.sendEvent("MainActivity", "delete task", "done task list", null);

            			taskItem.setDoneFlag(true);
            			tasksDoneArrayList.remove(position);
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
			protected boolean flag;
			public TextView taskName;
			TextView taskDescription;
			TextView taskReminderTime;
			TextView taskReminderLocation;
			CheckBox taskCheckBoxDone;
			ImageButton taskDeleteButton;
		}
}
