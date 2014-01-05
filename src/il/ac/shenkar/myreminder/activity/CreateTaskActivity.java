package il.ac.shenkar.myreminder.activity;

import il.ac.shenkar.myreminder.R;
import il.ac.shenkar.myreminder.dao.TasksDataBase;
import il.ac.shenkar.myreminder.entities.TaskItem;
import il.ac.shenkar.myreminder.fragment.AddressDialogFragment;
import il.ac.shenkar.myreminder.fragment.AlertDialogFragment;
import il.ac.shenkar.myreminder.handler.ReminderHandler;
import il.ac.shenkar.myreminder.utils.DateTimeDialog;
import il.ac.shenkar.myreminder.utils.UtilitiesFunctions;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

public class CreateTaskActivity extends FragmentActivity implements DateTimeDialog.INoticeDialogListener, OnCheckedChangeListener, OnClickListener,
AddressDialogFragment.IAddressDialogListener
{
	
	// define extra information
		public final static String EXTRA_NAME = "NAME";	
		public final static String EXTRA_DESCRIPTION = "DESCRIPTION";
		public final static String EXTRA_ID = "ID";
		public final static String EXTRA_ENTERING = "ENTERING";
		public final static String RANDOM_TASKS = "http://mobile1-tasks-dispatcher.herokuapp.com/task/random";
		public final static float GESTURE_THRESHOLD_DP = 140f;

	// define
		private TaskItem taskItem;
		private Context context;
		private Calendar calendarForReminder;
		private Switch switchTimeReminder;
		private Switch switchLocationReminder;
		private DateTimeDialog dateTimeDialog;
		private Button btnReminderDetails;
		private Button btnSelectedAddress;
		private EditText editTextName;
		private EditText editTextDescription;
		private String name;
		private RelativeLayout rlLocationReminder;
		private RelativeLayout rlTimeReminder;
		private CheckBox checkBoxArrive;
		private CheckBox checkBoxLeave;
		private double latitude;
		private double longitude;
		private boolean editTaskState; 
		private Tracker tracker;		
		private GoogleAnalytics gaInstance;
		
	// on create method
	    public void onCreate(Bundle savedInstanceState) 
	    {
			super.onCreate(savedInstanceState);
			final Typeface mFont = Typeface.createFromAsset(getAssets(),"fonts/AMC.ttf"); 
			final ViewGroup mContainer = (ViewGroup) findViewById(android.R.id.content).getRootView();
			UtilitiesFunctions.setAppFont(mContainer, mFont);
			setContentView(R.layout.create_task_activity);
			getActionBar().setDisplayHomeAsUpEnabled(true);
			context = this;
	
			gaInstance = GoogleAnalytics.getInstance(this);
			// Placeholder tracking ID from Strings XML
			tracker = gaInstance.getTracker(getString(R.string.googleAnalytics_ID)); 
	
			// Get the screen's density scale
			final float scale = getResources().getDisplayMetrics().density;
			// Convert the dp's to pixels, based on density scale
			final int destination = (int) (GESTURE_THRESHOLD_DP * scale + 0.5f);
			// get the intent sent from ListViewTasksAvtivity
			Intent intent = getIntent();
			taskItem = (TaskItem) intent.getSerializableExtra("taskObject");
			// check the reason for the launch of this activity ==>> EDIT || NEW
			if (taskItem.getName() != null)
			{ // the task that was sent via the intent is full
				editTextName = (EditText) findViewById(R.id.editText_name);
				editTextName.setText(taskItem.getName());
				setEditTaskState(true);
				
				if(taskItem.getDescription() != null)
				{ 
					editTextDescription = (EditText) findViewById(R.id.editText_description);
					editTextDescription.setText(taskItem.getDescription());
					setEditTaskState(true);					
				}
				
				// now if this task has reminders we will cancel them. they will be set again if necessary
				if (taskItem.getTimeReminderFlag())
				{ // cancel reminder by time
					ReminderHandler.cancelTimeReminder(context, taskItem.getID());
				}
				if (taskItem.getLocationReminderFlag())
				{ // cancel reminder by location
					ReminderHandler.cancelProximityAlert(context, taskItem.getID());
				}
	
			} else
			{ // the task that was sent via the intent is empty
				// show the soft keyboard
				final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				setEditTaskState(false);
			}
			calendarForReminder = taskItem.getReminderDate();
			btnReminderDetails = (Button) findViewById(R.id.button_reminderDetails);
			btnReminderDetails.setOnClickListener(this);

			// set listener for the reminder by location switch
			switchTimeReminder = (Switch) findViewById(R.id.switch_time_reminder);
			switchTimeReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
			// on checked changed method	
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					tracker.sendEvent("create new task activity", "time reminder switch", "", null);
					if (isChecked) // switch to ON
					{
						rlLocationReminder = (RelativeLayout) findViewById(R.id.RLlocationReminder);
						rlTimeReminder = (RelativeLayout) findViewById(R.id.RLtimeReminder);
						// handle the case that we are editing an existing task and it has a reminder by time
						if (isEditTaskState() && taskItem.getTimeReminderFlag())
						{ // set reminder details
							btnReminderDetails.setText(UtilitiesFunctions.fromCalendarToString(taskItem.getReminderDate()));							
						}
						// first time that we open the reminder by time switch.
						else
						{
							Calendar cal = Calendar.getInstance();
							cal.add(Calendar.MINUTE, 10);
							btnReminderDetails.setText(UtilitiesFunctions.fromCalendarToString(cal));
							calendarForReminder.setTimeInMillis(cal.getTimeInMillis());
						}
						Animation fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
						fadeIn.setStartOffset(500);
						fadeIn.setDuration(500);
						rlTimeReminder.startAnimation(fadeIn);
						rlTimeReminder.setVisibility(RelativeLayout.VISIBLE);
						ObjectAnimator objectAnimatorButton = ObjectAnimator.ofFloat(rlLocationReminder, "translationY", 0f, destination);
						objectAnimatorButton.setDuration(500);
						objectAnimatorButton.start();
					}
	
					else
					// switch to OFF
					{
						Animation fadeOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
						fadeOut.setDuration(500);
						rlTimeReminder.startAnimation(fadeOut);
						rlTimeReminder.setVisibility(RelativeLayout.GONE);
						ObjectAnimator objectAnimatorButton = ObjectAnimator.ofFloat(rlLocationReminder, "translationY", destination, 0f);
						objectAnimatorButton.setStartDelay(400);
						objectAnimatorButton.setDuration(500);
						objectAnimatorButton.start();
					}
				}
			});
			// set listener for the reminder by time switch
			switchLocationReminder = (Switch) findViewById(R.id.switch_location_reminder);
			switchLocationReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
			// on checked changed method	
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					tracker.sendEvent("create new task activity", "location reminder switch", "", null);
	
					RelativeLayout rlSubLocationReminder = (RelativeLayout) findViewById(R.id.RLsubLocationReminder);
					btnSelectedAddress = (Button) findViewById(R.id.button_select_address);
					btnSelectedAddress.setOnClickListener(CreateTaskActivity.this);
	
					checkBoxArrive = (CheckBox) findViewById(R.id.checkBox_arrive);
					checkBoxArrive.setOnCheckedChangeListener(CreateTaskActivity.this);
	
					checkBoxLeave = (CheckBox) findViewById(R.id.checkBox_leave);
					checkBoxLeave.setOnCheckedChangeListener(CreateTaskActivity.this);
	
					if (isChecked) // switch to ON
					{
						Animation fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
						fadeIn.setDuration(500);
						rlSubLocationReminder.startAnimation(fadeIn);
						rlSubLocationReminder.setVisibility(RelativeLayout.VISIBLE);
						// case that we are editing an existing task and it has a reminder by location 
						if (isEditTaskState() && taskItem.getLocationReminderFlag())
						{
							btnSelectedAddress.setText(taskItem.getLocation());
							checkBoxLeave.setChecked(taskItem.getLeaveFlag());
							checkBoxArrive.setChecked(taskItem.getArriveFlag());
							return;
						}
						// first time that we open the reminder by location switch.
						DialogFragment addressDialogFragment = new AddressDialogFragment(context, CreateTaskActivity.this);
						addressDialogFragment.show(getSupportFragmentManager(), "addressDialogFragment");
					} 
					else // switch to OFF				
					{
						Animation fadeOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
						fadeOut.setDuration(500);
						rlSubLocationReminder.startAnimation(fadeOut);
						rlSubLocationReminder.setVisibility(RelativeLayout.GONE);
					}
				}
			});
	
			// in case the reason we are in this activity is because the user
			switchTimeReminder.setChecked(taskItem.getTimeReminderFlag());
			switchLocationReminder.setChecked(taskItem.getLocationReminderFlag());
		}
	    
	// on create option menu method 
		@Override
		public boolean onCreateOptionsMenu(Menu menu)
		{
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.action_bar_details_activity, menu);
			return true;
		}
	
	// on option item selected method	
		@Override
		public boolean onOptionsItemSelected(MenuItem item)
		{
			switch (item.getItemId())
			{
			case android.R.id.home:
				tracker.sendEvent("create new task activity", "home button", "", null);
				NavUtils.navigateUpFromSameTask(this);
				return true;
			
			case R.id.menu_close:
				tracker.sendEvent("create new task activity", "close button", "", null);
				NavUtils.navigateUpFromSameTask(this);
				return true;
				
			case R.id.menu_add:
				tracker.sendEvent("create new task activity", "add button", "", null);
				if (isReadyToCreate())
				{
					createNewTask();
				}
				break;
			case R.id.menu_random:
				tracker.sendEvent("create new task activity", "random button", "", null);
				if (isOnline())
				{
					try
					{
						URL[] urls = { new URL(RANDOM_TASKS) };
						new GetTaskFromWeb().execute(urls);
					}
					catch (MalformedURLException e)
					{
						e.printStackTrace();
						tracker.sendException("MalformedURLException", false);
					}
				}
				break;
			}		
			return super.onOptionsItemSelected(item);
		}
	
	// is ready to create method
		private boolean isReadyToCreate()
		{
			// get the description from the EditText
			editTextName = (EditText) findViewById(R.id.editText_name);
			editTextDescription = (EditText) findViewById(R.id.editText_description);
			name = editTextName.getText().toString();
			// check if the description is not empty
			if (name.isEmpty())
			{
				AlertDialogFragment dialog = new AlertDialogFragment(getString(R.string.alert_empty_name));
				dialog.show(getSupportFragmentManager(), "emptyName");
				return false;
			}
			// variables in context of reminder by time
			if (switchTimeReminder.isChecked())
			{
				// check if the reminder time is valid - not past yet
				if (!isCalendarValid(calendarForReminder))
				{ // not valid - show alert dialog and dye the reminder details text
					// in red to point the user
					AlertDialogFragment dialog = new AlertDialogFragment(getString(R.string.alert_time_past));
					dialog.show(getSupportFragmentManager(), "timePast");
					btnReminderDetails.setTextColor(getResources().getColor(android.R.color.holo_red_light));
					return false;
				}
			} 
			// variables in context of reminder by location
			if (switchLocationReminder.isChecked())
			{ // in case the user opened the reminder by location switch but did not choose a location
				if (btnSelectedAddress.getText().equals(getString(R.string.select_address_button)))
				{
					AlertDialogFragment dialog = new AlertDialogFragment(getString(R.string.alert_no_location_chosen));
					dialog.show(getSupportFragmentManager(), "noLocationChosen");
					btnSelectedAddress.setTextColor(getResources().getColor(android.R.color.holo_red_light));
					return false;
				}
				// now we check that the user checked at least one of the [when I arrive] || [when I leave] check boxes
				if (!checkBoxArrive.isChecked() && !checkBoxLeave.isChecked())
				{
					AlertDialogFragment dialog = new AlertDialogFragment(getString(R.string.alert_no_checkBox_checked));
					dialog.show(getSupportFragmentManager(), "noCheckBoxChecked");
					return false;
				}
			} 
			return true;
		}
	
	// create new task method
		public void createNewTask()
		{
			TasksDataBase tasksDataBase = TasksDataBase.getInstance(this);
			taskItem.setName(name);
			taskItem.setDescription(editTextDescription.getText().toString());
			taskItem.setDoneFlag(false);
			if (!isEditTaskState())
			{ // set creation date only if the task is brand new
				tracker.sendEvent("create new task activity", "creating new task", "", null);
				taskItem.setCreationDate(Calendar.getInstance().getTimeInMillis());
			}
			tracker.sendEvent("create new task activity", "editing task", "", null);
			// reminder by time details
			if (switchTimeReminder.isChecked())
			{ // reminder by time is in use
				taskItem.setReminderDate(calendarForReminder.getTimeInMillis());
				taskItem.setTimeReminderFlag(true);
			} else
			{ // reminder by time is not in use
				taskItem.setReminderDate(0);
				taskItem.setTimeReminderFlag(false);
			}
			// reminder by location details
			if (switchLocationReminder.isChecked())
			{ // reminder by location is in use
				taskItem.setLocationReminderFlag(true);
				taskItem.setLocation(btnSelectedAddress.getText().toString());
				taskItem.setArriveFlag(checkBoxArrive.isChecked());
				taskItem.setLeaveFlag(checkBoxLeave.isChecked());
			} else
			{ // reminder by location is not in use
				taskItem.setLocation(null);
				taskItem.setArriveFlag(false);
				taskItem.setLeaveFlag(false);
				taskItem.setLocationReminderFlag(false);
			} // END OF DEALING WITH REMINDER BY LOCATION
	
			if (isEditTaskState())
			{ // in case we are editing an existing task it's already has an ID thus
				// we only need to update
				tasksDataBase.updateTaskItem(taskItem);
			}
	
			else
			{ // in case we created a new task we need to set it's ID
				taskItem.setID(tasksDataBase.addTaskItem(taskItem));
			}
			if (taskItem.getTimeReminderFlag())
			{ // set the ALARM only after we got the ID from the Data Base
				 // one time alarm
					ReminderHandler.setTimeReminder(context, taskItem);
			}
	
			if (taskItem.getLocationReminderFlag())
			{ // set proximity alert
				ReminderHandler.setProximityAlert(context, taskItem, latitude, longitude);
			}
	
			if (dateTimeDialog != null)
			{ // check if DateTimePickerDialog was created in the current session must be destroyed
				dateTimeDialog.destroyInstance();
			}
			finish();
		}
	
	// show date time picker dialog method
		public void showDateTimePickerDialog(int callerID)
		{
			dateTimeDialog = DateTimeDialog.getInstance(context, this, callerID);
			dateTimeDialog.show();
		}
	
	// dispatch touch event - hiding The soft keyboard
		@Override
		public boolean dispatchTouchEvent(MotionEvent event)
		{
	
			View v = getCurrentFocus();
			boolean ret = super.dispatchTouchEvent(event);
	
			if (v instanceof EditText)
			{
				View w = getCurrentFocus();
				int scrcoords[] = new int[2];
				w.getLocationOnScreen(scrcoords);
				float x = event.getRawX() + w.getLeft() - scrcoords[0];
				float y = event.getRawY() + w.getTop() - scrcoords[1];
	
				if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()))
				{
	
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
				}
			}
			return ret;
		}
	
	// on dialog ok click method - if the user clicked "OK" in dialog
		@Override
		public void onDialogOkClick(Calendar selectedDate)
		{
			int id = dateTimeDialog.getCallerID();
	
			switch (id)
			{
				case R.id.button_reminderDetails:
					tracker.sendEvent("create new task activity", "date was set", "for reminder", null);
					calendarForReminder = Calendar.getInstance();
					calendarForReminder = selectedDate;
					btnReminderDetails.setTextColor(getResources().getColor(android.R.color.black));
					btnReminderDetails.setText(UtilitiesFunctions.fromCalendarToString(calendarForReminder));
					break;
							
			}		
		}
	
	// cancel method - if the user clicked "Cancel" in dialog
		@Override
		public void onDialogCancelClick(int id)
		{
			switch (id)
			{
			case R.id.button_reminderDetails:
				switchTimeReminder.setChecked(false);
				break;
			}
	
		}
	
	// on click method	
		@Override
		public void onClick(View view)
		{
			switch (view.getId())
			{
			case R.id.button_reminderDetails:
				showDateTimePickerDialog(R.id.button_reminderDetails);
				break;
	
			case R.id.button_select_address:
				DialogFragment addressDialogFragment = new AddressDialogFragment(context, this);
				addressDialogFragment.show(getSupportFragmentManager(), "addressDialogFragment");
				break;
			}
		}
	
	// get task from the web method in AsyncTask 
		private class GetTaskFromWeb extends AsyncTask<URL, Integer, JSONObject>
		{
			private ProgressDialog progressDialog;
	
		// on pre execute method	
			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				progressDialog = ProgressDialog.show(CreateTaskActivity.this, "Fetching Task", "Please wait...");
			}
	
		// do in background method	
			@Override
			protected JSONObject doInBackground(URL... urls)
			{
				JSONObject resultJSON = UtilitiesFunctions.getFromWeb(urls[0]);
	
				return resultJSON;
			}
	
		// on post execute method	
			@Override
			protected void onPostExecute(JSONObject result)
			{
				super.onPostExecute(result);
				progressDialog.dismiss();
				editTextName = (EditText) findViewById(R.id.editText_name);
				try
				{
					if (result != null)
					{
					String tempName = result.getString("topic");
					editTextName.setText(tempName);
					}
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
		}
		
	// check if we are in edit mode or in new task mode
		public boolean isEditTaskState()
		{
			return editTaskState;
		}
		
	// set the edit task state
		public void setEditTaskState(boolean editTask)
		{
			this.editTaskState = editTask;
		}
	
	// on address selected method	
		@Override
		public void onAddressSelected(Address selectedAddress)
		{
			latitude = selectedAddress.getLatitude();
			longitude = selectedAddress.getLongitude();
			String location = UtilitiesFunctions.formatAddressToString(selectedAddress);
			taskItem.setLocation(location);
			btnSelectedAddress = (Button) findViewById(R.id.button_select_address);
			btnSelectedAddress.setText(location);
		}
		
	// check if the device has Internet connection
		public boolean isOnline()
		{
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnected())
			{
				return true;
			}
			// show alert dialog if not connected
			AlertDialogFragment dialog = new AlertDialogFragment("No Internet Connection Available");
			dialog.show(getSupportFragmentManager(), "noInternetCon");
			return false;
		}
	
	// check if the selected time for the reminder has not past 
		public boolean isCalendarValid(Calendar cal)
		{
			return cal.getTimeInMillis() > System.currentTimeMillis();
		}
	
	// on checked changed method	
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			switch (buttonView.getId())
			{
			case R.id.checkBox_arrive:
				if (isChecked && checkBoxLeave.isChecked())
				{
					checkBoxLeave.setChecked(false);
				}
				checkBoxArrive.setChecked(isChecked);
	
				break;
	
			case R.id.checkBox_leave:
				if (isChecked && checkBoxArrive.isChecked())
				{
					checkBoxArrive.setChecked(false);
				}
				checkBoxLeave.setChecked(isChecked);
	
				break;
			}
		}
	
	// on start method	
		@Override
		public void onStart()
		{
			super.onStart();
			EasyTracker.getInstance().activityStart(this);
		}
	
	// on stop method	
		@Override
		public void onStop()
		{
			super.onStop();
			EasyTracker.getInstance().activityStop(this);
		}
}
