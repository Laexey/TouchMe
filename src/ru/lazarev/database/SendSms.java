package ru.lazarev.database;
import android.telephony.*;
import java.util.*;


public class SendSms
{
    SmsManager manager;
	
	public SendSms()
	{
		manager = SmsManager.getDefault();
	}
	
	public void send(String dest, String message)
	{
		if(PhoneNumberUtils.isWellFormedSmsAddress(dest))
		{
			
		    ArrayList<String> messageArray = manager.divideMessage(message);
	    	manager.sendMultipartTextMessage(dest, null, messageArray, null, null);
				
		} else
		{
			
		}		
	}
} 
