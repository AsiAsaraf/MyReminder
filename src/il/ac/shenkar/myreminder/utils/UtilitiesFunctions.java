package il.ac.shenkar.myreminder.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.location.Address;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UtilitiesFunctions 
{

	// Convert string to boolean
		public static boolean stringToBoolean(String toConvert)
		{
			return toConvert.equals("true");
		}

	// Convert address format to string
		public static String formatAddressToString(Address address)
		{
			String addressText = String.format(Locale.ENGLISH, "%s, %s, %s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getLocality(), address.getCountryName());
			return addressText;
		}
	
	// Retrieve a JSON from a URL
		public static JSONObject getFromWeb(URL url)
		{
			try
			{
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	
				/* From InputStream to String */
				InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
				InputStreamReader inReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inReader);
				StringBuilder responseBuilder = new StringBuilder();
	
				for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine())
				{
					responseBuilder.append(line);
				}
				String response = responseBuilder.toString();
	
				JSONObject taskJSON = new JSONObject(response);
	
				return taskJSON;
			}
	
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
	// Convert Calendar to informative string
		public static String fromCalendarToString(Calendar calendar)
		{
			DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
			String s = df.format(calendar.getTime());
			df = DateFormat.getTimeInstance(DateFormat.SHORT);
			s += " " + df.format(calendar.getTime());
			return s;
		}
		
	// Set app font
		public static final void setAppFont(ViewGroup mContainer, Typeface mFont)
		{
		    if (mContainer == null || mFont == null) return;

		    final int mCount = mContainer.getChildCount();

		    // Loop through all of the children.
		    for (int i = 0; i < mCount; ++i)
		    {
		        final View mChild = mContainer.getChildAt(i);
		        if (mChild instanceof TextView)
		        {
		            // Set the font if it is a TextView.
		            ((TextView) mChild).setTypeface(mFont);
		        }
		        else if (mChild instanceof ViewGroup)
		        {
		            // Recursively attempt another ViewGroup.
		            setAppFont((ViewGroup) mChild, mFont);
		        }
		    }
		}
	
}