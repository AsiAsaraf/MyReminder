package il.ac.shenkar.myreminder.fragment;

import il.ac.shenkar.myreminder.R;
import il.ac.shenkar.myreminder.adapter.AddressListBaseAdapter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class AddressDialogFragment extends DialogFragment
{
	// address dialog listener
		public interface IAddressDialogListener
		{
			public void onAddressSelected(Address selectedAddress);
		}
		
	// number of results to appear in the address list view
		private static final int MAX_RESULTS = 15;
		private Context callerContext;
		private List<Address> addresses;
		private IAddressDialogListener mListener;

	// constructor
		public AddressDialogFragment(Context context, IAddressDialogListener listener)
		{
			super();
			callerContext = context;
			addresses = null;
			mListener = listener; 
		}

	// on create view method
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View view = inflater.inflate(R.layout.address_dialog, container, false);
			getDialog().requestWindowFeature(STYLE_NO_TITLE);
			
			//show the soft keyboard
		    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		    
		    ImageButton searchButton = (ImageButton) view.findViewById(R.id.image_button_search_icon);
			searchButton.setOnClickListener(new OnClickListener()
			{
			// on click method
				public void onClick(View view)
				{
					// search image button was clicked
					EditText searchField = (EditText) getView().findViewById(R.id.editText_address_search_field);
					String inputAddress = searchField.getText().toString();
					if (!inputAddress.isEmpty())
					{ 
						//hide the soft keyboard
					    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
					    // search for addresses in AsynchTask method
					    new ReverseGeocodingTask(callerContext, inputAddress, MAX_RESULTS).execute();
					} 
					else
					{ // no input
						Toast.makeText(callerContext, "Please enter an address", Toast.LENGTH_SHORT).show();
					}
				}
			});
			
			final ListView addressesListView = (ListView) view.findViewById(R.id.list_view_addresses);
			addressesListView.setAdapter(new AddressListBaseAdapter(callerContext));
			// set itemClick for the list of addresses
			addressesListView.setOnItemClickListener(new OnItemClickListener()
			{
			// on item click method	
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					Object ob = addressesListView.getItemAtPosition(position);
					Address selectedAddress = (Address) ob;
					mListener.onAddressSelected(selectedAddress);
					dismiss();
				}
			});
			return view;
		}
		
	// 	reverse geocoding task method
		public class ReverseGeocodingTask extends AsyncTask<Void, Void, List<Address>>
		{
		// define
			Context mContext;
			ProgressBar progressBar;
			String mInputAddress;
			int mMaxResult;
			
		// constructor
			public ReverseGeocodingTask(Context context, String inputAddress, int maxResult)
			{
				super();
				mContext = context;
				mInputAddress = inputAddress;
				mMaxResult = maxResult;
			}
	
		// on pre execute method	
			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				progressBar = (ProgressBar) getView().findViewById(R.id.progress_bar);
				progressBar.setVisibility(View.VISIBLE);
			}
	
		// do in background method	
			@Override
			protected List<Address> doInBackground(Void... params)
			{			
				Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
				// get the location service
				LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
	
				@SuppressWarnings("unused")
				String provider;
	
				// Define the criteria how to select the location provider -> use default
				Criteria criteria = new Criteria();
				provider = locationManager.getBestProvider(criteria, false);
				try
				{
					addresses = geocoder.getFromLocationName(mInputAddress, mMaxResult);
				}
				catch (IOException e)
				{ 	//if the network is unavailable or any other I/O problem occurs
					e.printStackTrace();
					return null;
				}
					
				if (addresses != null)
				{
					return addresses;
				}
				return null;		
			}
	
		// on post execute method		
			@Override
			protected void onPostExecute(List<Address> addresses)
			{
				super.onPostExecute(addresses);
				progressBar.setVisibility(View.GONE);
				
				if(addresses == null)
				{ // no connection
					AlertDialogFragment dialog = new AlertDialogFragment(getString(R.string.alert_no_connection));
					dialog.show(getFragmentManager(), "noConnection");
					return;
				}
				
				if(addresses.isEmpty())
				{ // no addresses found to match the input address
					AlertDialogFragment dialog = new AlertDialogFragment(getString(R.string.alert_no_match_addresses));
					dialog.show(getFragmentManager(), "noMatchAddresses");
					return;
				}
							
				final ListView addressesListView = (ListView) getView().findViewById(R.id.list_view_addresses);		
				addressesListView.setAdapter(new AddressListBaseAdapter(mContext));
				// populate the list view of addresses
				((AddressListBaseAdapter) addressesListView.getAdapter()).setAddressesList(addresses);
				((BaseAdapter) addressesListView.getAdapter()).notifyDataSetChanged();
			}
		}
}