package il.ac.shenkar.myreminder.entities;

import java.io.Serializable;
import java.util.Calendar;

public class TaskItem implements Serializable, Comparable<TaskItem>
{
	private static final long serialVersionUID = 1L;
	
	//private variables
		private long id;
		private String name;
		private String description;
		private Calendar creationDate;
		private Calendar reminderDate;
		private String location;
		
	//private flags
		private boolean	timeReminderFlag;
		private boolean locationReminderFlag;
		private boolean	leaveFlag;
		private boolean arriveFlag;
		private boolean	doneFlag;
		
	// Constructor
		public TaskItem()
		{
			this.setName(null);
			this.setDescription(null);
			this.setCreationDate(0);
			this.setReminderDate(0);
			this.setLocation(null);
			
			this.setTimeReminderFlag(false);
			this.setLocationReminderFlag(false);
			this.setLeaveFlag(false);
			this.setArriveFlag(false);
			this.setDoneFlag(false);
		}
		
	//Getters & Setters
				
	// getting ID
		public long getID()
		{
			return this.id;
		}
		
	// setting id
		public void setID(long taskId)
		{
			this.id = taskId;
		}
		
	// getting name
		public String getName()
		{
			return this.name;
		}
		
	// setting name
		public void setName(String name)
		{
			this.name = name;
		}
		
	// getting description
		public String getDescription()
		{
			return this.description;			
		}
		
	// setting description
		public void setDescription(String description)
		{
			this.description = description;
		}
		
	// getting creation date
		public Calendar getCreationDate()
		{
			return creationDate;
		}
		
	// setting creation date
		public void setCreationDate(long timeInMiliSec)
		{
			this.creationDate = Calendar.getInstance();
			this.creationDate.setTimeInMillis(timeInMiliSec);
		}
		
	// getting reminder date
		public Calendar getReminderDate()
		{
			return reminderDate;
		}
		
	// setting reminder date
		public void setReminderDate(long timeInMiliSec)
		{
			this.reminderDate = Calendar.getInstance();
			this.reminderDate.setTimeInMillis(timeInMiliSec);
		}
		
	// getting location
		public String getLocation()
		{
			return this.location;
		}
		
	// setting location
		public void setLocation(String location)
		{
			this.location = location;
		}
		
	// getting time reminder flag
		public boolean getTimeReminderFlag() {
			return timeReminderFlag;
		}

	// setting time reminder flag
		public void setTimeReminderFlag(boolean timeReminderFlag) {
			this.timeReminderFlag = timeReminderFlag;
		}

	// getting location reminder flag
		public boolean getLocationReminderFlag() {
			return locationReminderFlag;
		}

	// setting location reminder flag
		public void setLocationReminderFlag(boolean locationReminderFlag) {
			this.locationReminderFlag = locationReminderFlag;
		}

	// getting leave flag
		public boolean getLeaveFlag() {
			return leaveFlag;
		}

	// setting leave flag
		public void setLeaveFlag(boolean leaveFlag) {
			this.leaveFlag = leaveFlag;
		}

	// getting arrive flag
		public boolean getArriveFlag() {
			return arriveFlag;
		}

	// setting arrive flag
		public void setArriveFlag(boolean arriveFlag) {
			this.arriveFlag = arriveFlag;
		}
		
	// getting done flag
		public boolean getDoneFlag() {
			return doneFlag;
		}

	// setting done flag
		public void setDoneFlag(boolean doneFlag) {
			this.doneFlag = doneFlag;
		}
		
	// compare
		public int compareTo(TaskItem taskItem)
		{
			// sort by creation date
			if(this.creationDate.before(taskItem.getCreationDate()))
			{
				return 1;
			}
			return -1;
		}
		
	}