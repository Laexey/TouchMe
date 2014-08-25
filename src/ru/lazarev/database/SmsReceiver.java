package ru.lazarev.database;

import android.content.*;
import android.widget.*;
import android.os.*;
import android.telephony.*;
import android.app.*;
import android.media.*;
import java.text.*;

public class SmsReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		//извлекаем pdu, объект сообщения
		Bundle bundle = intent.getExtras();
		Object[] pdus = (Object []) bundle.get("pdus");
		//блокировка дальнейшей обработки сообщения
		abortBroadcast();
		
		//преобразование pdu в объект SmsMessage
		SmsMessage [] messages = new SmsMessage[pdus.length];				
		for (int i = 0; i < pdus.length; i++)
		{
			messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
		}
		
		String message = "";
		String address = "";
		
		//вывод сообщения
		for(SmsMessage msg: messages)
		{
			message += msg.getMessageBody();
		}
		address = messages[0].getOriginatingAddress();
		
		//Нотификация о сообщении
		NotificationManager notifyManager = (NotificationManager)
		        context.getSystemService(context.NOTIFICATION_SERVICE);
		Notification.Builder builder = new Notification.Builder(context);
	    String str = context.getResources().getString(R.string.menu_item_new_message);
	    
		//Отложенное намерение для запуска ConversationActivity из ящика уведомлений
		Intent intentNew = new Intent(context.getApplicationContext(), ConversationActivity.class);
		intentNew.putExtra(ThreadsActivity.SELECT_ADDRESS, address);
		intentNew.putExtra(ConversationActivity.SMS_BODY, message);
		intentNew.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
		                   Intent.FLAG_ACTIVITY_SINGLE_TOP|
						   Intent.FLAG_ACTIVITY_NO_ANIMATION);
		
		if(AppPreferences.getBoolean(context, AppPreferences.CONV_ACTIVITY_ON_FRONT))
		{
			context.getApplicationContext().startActivity(intentNew);
		} else if (AppPreferences.getBoolean(context, AppPreferences.THREADS_ACTIVITY_ON_FRONT))
		{
		    
			
		}
		
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentNew, 0);
		builder.setSmallIcon(R.drawable.ic_launcher)
			.setTicker(str)
			.setAutoCancel(true)
			.setContentTitle(str)
			.setContentText(message)
			.setContentIntent(pendingIntent)
			.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		Notification notification = builder.getNotification();
		notifyManager.notify(1, notification);
	}

}
