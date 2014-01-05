package il.ac.shenkar.myreminder.adapter;

import il.ac.shenkar.myreminder.R;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AddressListBaseAdapter extends BaseAdapter
{
	// define 
		private LayoutInflater l_inflater;
		private List<Address> addresses;
		private Context contextAdapter;

	// constructor
		public AddressListBaseAdapter(Context context)
		{
			contextAdapter = context;
			l_inflater = LayoutInflater.from(contextAdapter);
			addresses = new ArrayList<Address>();
		}
	
	// setting address list	
		public void setAddressesList(List<Address> list)
		{
			addresses = list;
		}
		
	// get address count
		public int getCount()
		{
			return addresses.size();
		}
	
	// getting item
		public Object getItem(int position)
		{
			return addresses.get(position);
		}
	
	// getting item id
		public long getItemId(int position)
		{
			return position;
		}
	
	// getting view
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder;
			if(convertView == null)
			{
				convertView = l_inflater.inflate(R.layout.address_details_view, null);
				holder = new ViewHolder();
				holder.txt_address = (TextView) convertView.findViewById(R.id.textView_address_details);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			
			String addressLine = addresses.get(position).getAddressLine(0) 
						+ ", " + addresses.get(position).getAddressLine(1) 
						+ ", " + addresses.get(position).getAddressLine(2);
			
			holder.txt_address.setText(addressLine);
			return convertView;
		}
		
	// define static class view holder	
		static class ViewHolder
		{
			TextView txt_address;
		}
	
}