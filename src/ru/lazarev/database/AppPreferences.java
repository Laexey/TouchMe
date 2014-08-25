package ru.lazarev.database;
import android.content.*;
import java.util.*;
import java.security.*;

public class AppPreferences
{
	public static final String B_CONVERSATION_CALLED = "conversation_called";
	public static final String APP_PREFS_NAME = "AppPrefs";
	public static final String S_SAVED_ADDRESS = "saved_address";
	public static final String CONV_ACTIVITY_ON_FRONT = "conv_avtivity_on_front";
	public static final String THREADS_ACTIVITY_ON_FRONT = "threads_activity_on_front";
	
	public static void setPreference(Context context, String key, Object value)
	{
	    SharedPreferences prefs = context.getSharedPreferences(APP_PREFS_NAME, context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
        if (value instanceof Boolean)
		{
			editor.putBoolean(key, (Boolean) value);
		} else if (value instanceof String)
		{
			editor.putString(key, (String) value);
		}
		editor.commit();
	}
	
	public static Boolean getBoolean(Context context, String key)
	{
		SharedPreferences prefs = context.getSharedPreferences(APP_PREFS_NAME, context.MODE_PRIVATE);	
		return prefs.getBoolean(key, false);
	}
	
	public static String getString(Context context, String key)
	{
		SharedPreferences prefs = context.getSharedPreferences(APP_PREFS_NAME, context.MODE_PRIVATE);	
		return prefs.getString(key, "");
	}
}
