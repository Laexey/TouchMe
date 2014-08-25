package ru.lazarev.database;

import android.content.*;
import android.view.*;

abstract class MessageBox
{

    public static void show(Context context, String s, String s1, View view)
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        if(s.length() != 0)
            builder.setTitle(s);
        if(s1.length() != 0)
            builder.setMessage(s1);
        if(view != null)
        {
            s1 = "";
            builder.setView(view);
        }
        builder.setCancelable(false);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					// TODO: Implement this method
				}
			});
        builder.show();
    }
}
