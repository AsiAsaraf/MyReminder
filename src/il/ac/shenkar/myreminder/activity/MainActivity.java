package il.ac.shenkar.myreminder.activity;

import il.ac.shenkar.myreminder.R;
import il.ac.shenkar.myreminder.adapter.TasksDoneListBaseAdapter;
import il.ac.shenkar.myreminder.adapter.TasksListBaseAdapter;
import il.ac.shenkar.myreminder.dao.TasksDataBase;
import il.ac.shenkar.myreminder.entities.TaskItem;
import il.ac.shenkar.myreminder.utils.UtilitiesFunctions;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
 
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends Activity  implements TabHost.OnTabChangeListener{
	// define	
		private ListView lv1, lv2;
		TasksDataBase tasksDataBase;
		private Tracker tracker;
		private GoogleAnalytics gaInstance;
		
	// on create method	
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			
			gaInstance = GoogleAnalytics.getInstance(this);
		    tracker = gaInstance.getTracker(getString(R.string.googleAnalytics_ID));	 
	
			
			final Typeface font = Typeface.createFromAsset(getAssets(),"fonts/AMC.ttf"); 
			final ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content).getRootView();
			UtilitiesFunctions.setAppFont(rootView, font);
			
			TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
			tabHost.setup();
	
			TabSpec spec1 = tabHost.newTabSpec("Tasks");
			spec1.setContent(R.id.tab1);
			spec1.setIndicator("Tasks");
	
			TabSpec spec2 = tabHost.newTabSpec("Done");
			spec2.setIndicator("Done");
			spec2.setContent(R.id.tab2);
	
			tasksDataBase = TasksDataBase.getInstance(getApplicationContext());
			TasksDoneListBaseAdapter tasksDoneListBaseAdapter = new TasksDoneListBaseAdapter(this, this.tracker);
	
			lv1 = (ListView) findViewById(R.id.tasks_list_view);
			lv1.setAdapter(new TasksListBaseAdapter(this, this.tracker, tasksDoneListBaseAdapter));
			lv2 = (ListView) findViewById(R.id.done_list_view);
			lv2.setAdapter(tasksDoneListBaseAdapter);
	
			tabHost.addTab(spec1);
			tabHost.addTab(spec2);
			
			this.onTabChanged(spec1.getTag());
			this.onTabChanged(spec2.getTag());
			}
			
	
	// on create option menu method	
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu
			getMenuInflater().inflate(R.menu.action_bar_list_activity, menu);
			return true;
		}
		
	// on option item selected method
		@Override
		public boolean onOptionsItemSelected(MenuItem item)
		{
			switch (item.getItemId())
			{
			case R.id.menu_add:
				tracker.sendEvent("MainActivity", "click", "add button", null);
				Intent intent = new Intent(getApplicationContext(), CreateTaskActivity.class);
				TaskItem taskItem = new TaskItem();
				intent.putExtra("taskObject", taskItem);
				startActivity(intent);
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
			}
		}
	
	// on resume method	
		@Override
		protected void onResume() {
			Log.i("MainActivity", "Activity Resumed");
			((BaseAdapter) lv1.getAdapter()).notifyDataSetChanged();
			((BaseAdapter) lv2.getAdapter()).notifyDataSetChanged();
			super.onResume();
		}
		
	// on start method		
		@Override
		protected void onStart() {
			((BaseAdapter) lv1.getAdapter()).notifyDataSetChanged();
			((BaseAdapter) lv2.getAdapter()).notifyDataSetChanged();
			EasyTracker.getInstance().setContext(this);
			EasyTracker.getInstance().activityStart(this);
			super.onStart();			
		}
		
	// on stop method		
		@Override
		protected void onStop() {
			((BaseAdapter) lv1.getAdapter()).notifyDataSetChanged();
			((BaseAdapter) lv2.getAdapter()).notifyDataSetChanged();
			EasyTracker.getInstance().setContext(this);
			EasyTracker.getInstance().activityStop(this);
			super.onStop();
		}
	
	// // on tab changed method		
		@Override
		public void onTabChanged(String tabId) {
			((BaseAdapter) lv1.getAdapter()).notifyDataSetChanged();
			((BaseAdapter) lv2.getAdapter()).notifyDataSetChanged();
		}

}
