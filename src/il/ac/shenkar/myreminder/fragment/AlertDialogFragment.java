package il.ac.shenkar.myreminder.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

@SuppressLint("ValidFragment")
public class AlertDialogFragment extends DialogFragment
{
		private String alertMessage;
	
	// constructor	
		public AlertDialogFragment(String message)
		{
			alertMessage = message;
		}
		
	// on create dialog method	
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(alertMessage).setTitle("Alert").setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
				}
			});
	
			// Create the AlertDialog object and return it
			return builder.create();
		}
}
