package il.ac.shenkar.myreminder.utils;

import il.ac.shenkar.myreminder.R;
import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.ViewSwitcher;

public class DateTimeDialog extends Dialog implements OnDateChangedListener, OnTimeChangedListener, View.OnClickListener 
{	
	
	// 
		public interface INoticeDialogListener
		{
			public void onDialogOkClick(Calendar calendar);
			public void onDialogCancelClick(int id);			
		}
		
	// instance of this class
		private static DateTimeDialog instance = null;
		
	// the interface to handle callback
		private INoticeDialogListener mListener;
		private Calendar calendar;
		private DatePicker datePicker;
		private TimePicker timePicker;
		private ViewSwitcher viewSwitcher;
		private Button btnSwitchToTime;
		private Button btnSwitchToDate;
		private Button btnOk;
		private Button btnReset;
		private Button btnCancel;
		private boolean reminderFlag;
		private boolean is24HourFormat;
		private static int mCallerID;
	
	// 	instantiate method to DateTimeDialog class
		public static DateTimeDialog getInstance(Context context, INoticeDialogListener noticeListener, int callerID)
		{
			if(instance == null)
			{
				instance = new DateTimeDialog(context, noticeListener, callerID);
			}
			// set the callerID
			setCallerID(callerID);
			return instance;
		}
	
	// constructor	
		private DateTimeDialog(Context context, INoticeDialogListener noticeDialogListener, int callerID)
		{	
			super(context);
			mListener = noticeDialogListener;
			setContentView(R.layout.date_time_dialog);
			setTitle(R.string.date_time_dialog_title);
			
			// set the calendar with the current date
			calendar = Calendar.getInstance();
			calendar.getTime();
			
			// set the reminder flag. this flag is for setting the ToggleButton in the parent activity
			reminderFlag = true;
			
			// define if the system is using 24 hour format
			is24HourFormat = DateFormat.is24HourFormat(context);
			
			// initialize the date picker and the time picker with current time and date
			datePicker = (DatePicker) findViewById(R.id.picker_date);
			timePicker = (TimePicker) findViewById(R.id.picker_time);
			timePicker.setIs24HourView(is24HourFormat);
			resetDateTime();
			
			// set listener for the time picker
			timePicker.setOnTimeChangedListener(this);
			
			// populate the view switcher
			viewSwitcher = (ViewSwitcher) findViewById(R.id.view_switcher_date_time);
			
			/* populate and set listeners for all the buttons */
			btnSwitchToTime = (Button) findViewById(R.id.button_switch_time);
			btnSwitchToTime.setOnClickListener(this);
			
			btnSwitchToDate = (Button) findViewById(R.id.button_switch_date);
			btnSwitchToDate.setOnClickListener(this);
			
			btnOk = (Button) findViewById(R.id.button_ok);
			btnOk.setOnClickListener(this);
			
			btnReset = (Button) findViewById(R.id.button_reset);
			btnReset.setOnClickListener(this);
			
			btnCancel = (Button) findViewById(R.id.button_cancel);
			btnCancel.setOnClickListener(this);
		}

	// onClick method
		public void onClick(View view)
		{
			switch(view.getId())
			{
				case R.id.button_switch_time:
					btnSwitchToTime.setEnabled(false);
					findViewById(R.id.button_switch_date).setEnabled(true);
					viewSwitcher.showNext();
					break;
					
				case R.id.button_switch_date:
					btnSwitchToDate.setEnabled(false);
					findViewById(R.id.button_switch_time).setEnabled(true);
					viewSwitcher.showPrevious();
					break;
					
				case R.id.button_cancel:
					setReminderFlag(false);
					mListener.onDialogCancelClick(mCallerID);
					cancel();
					break;
					
				case R.id.button_ok:
					// no need to set "reminderFlag", his default is "true"
					mListener.onDialogOkClick(calendar);
					dismiss();
					break;
					
				case R.id.button_reset:
					resetDateTime();	
					break;
			}
			
		}

	// method called when time changed
		public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
		{
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);	
		}

	// method called when date changed
		public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)
		{
			calendar.set(year, monthOfYear, dayOfMonth, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));	
		}

	// reset date time dialog
		public void resetDateTime()
		{
			calendar = Calendar.getInstance();
			calendar.getTime();
			datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), this);
			if(is24HourFormat)
			{ // in case system is set to 24 hours view
				timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
			}
			else
			{ // in case the system is set to 12 hours view
				timePicker.setCurrentHour(calendar.get(Calendar.HOUR));
			}
			timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		}
	
	// destroy instance, called from the parent activity
		public void destroyInstance()
		{
			instance = null;
		}	
	
	// getting reminder time
		public Calendar getReminderDateTime()
		{
			return calendar;
		}

	// get reminder flag
		public boolean getReminderFlag()
		{
			return reminderFlag;
		}
	
	// set reminder flag	
		public void setReminderFlag(boolean reminderFlag)
		{
			this.reminderFlag = reminderFlag;
		}
	
	// get caller id	
		public int getCallerID()
		{
			return mCallerID;
		}
	
	// set caller id	
		public static void setCallerID(int callerID)
		{
			mCallerID = callerID;
		}	
}