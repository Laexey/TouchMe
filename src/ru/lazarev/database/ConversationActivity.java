package ru.lazarev.database;

import android.app.*;
import android.os.*;
import android.net.*;
import android.database.*;
import android.widget.*;
import android.telephony.*;
import android.support.v4.app.*;
import android.view.*;
import android.content.*;
import android.view.View.*;
import android.graphics.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.*;
import android.provider.*;
import android.util.*;
import android.text.*;
import org.apache.http.impl.conn.tsccm.*;

public class ConversationActivity extends Activity
implements OnClickListener, OnTouchListener
{
    private ListView convListView;
	private EditText editSendText;
	private ImageButton btnSendText;
	private ConversationAdapter adapt;
	private ActionBar actBar;
    private SendSms sms;
	private String transferredAddress;
	private final int FROM = 1;
	private final int TO = 2;
	private final String ADDRESS = "address";
	private final String BODY = "body";
	private final String DATE = "date";
	private final String TYPE = "type";
	private final String THREAD = "thread_id";
	private Uri smsBox = Uri.parse("content://sms/");
	private String[] filter = new String [] {"_id", ADDRESS, BODY, DATE, TYPE, THREAD};//поля
	private Cursor cursor = null;
	final static String SMS_BODY = "sms_body";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversation);
		
		sms = new SendSms();
		
		//инициализируем View элементы
		convListView = (ListView) findViewById(R.id.convListView);
	    
		
		editSendText = (EditText) findViewById(R.id.editSendText);
		editSendText.setHint(R.string.type_text_hint);
	
		btnSendText = (ImageButton) findViewById(R.id.btnSendText);
		btnSendText.setOnClickListener(this);
		btnSendText.setOnTouchListener(this);
		
		//извлекаем данные, переданные из ThreadsActivity
		//transferredAddress = getIntent().getExtras().getString(ThreadsActivity.SELECT_ADDRESS);	
		
		//readSmsDb();
		Toast.makeText(this, "Conversation onCreate", Toast.LENGTH_SHORT).show();	
		
		//инициализируем ActionBar
		actBar = getActionBar();
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayUseLogoEnabled(false);
		actBar.setTitle(transferredAddress);
	}
    
	@Override
	protected void onNewIntent(Intent intent)
	{
		//здесь разместить код для обновления Activity, если он
		//на переднем плане
		//super.onNewIntent(intent);
		//makeCursor();
		Bundle bndl = intent.getExtras();

		ContentValues addValues = new ContentValues();

		addValues.put(ADDRESS, bndl.getString(ThreadsActivity.SELECT_ADDRESS));
		//addValues.put(THREAD, cursor.getInt(cursor.getColumnIndex(THREAD)));
		addValues.put(BODY, bndl.getString(ConversationActivity.SMS_BODY));
		addValues.put(TYPE, FROM);

		ContentResolver content = getContentResolver();
		Uri uri = content.insert(smsBox, addValues);
		//Toast.makeText(this, uri.toString(), Toast.LENGTH_LONG).show();
		//Toast.makeText(this, "Сообщение", Toast.LENGTH_LONG).show();
		makeCursor();
		adapt.changeCursor(cursor);
		adapt.notifyDataSetChanged();
		
	}
	
	private final int REFRESH = 101;
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(Menu.NONE, REFRESH, Menu.NONE, "Refresh");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onBackPressed()
	{
		AppPreferences.setPreference(this, AppPreferences.B_CONVERSATION_CALLED, false);
		super.onBackPressed();
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		Toast.makeText(this, "Conversation onRestoreInstanceState", Toast.LENGTH_SHORT).show();
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		//извлекаем данные, переданные при вызове через intent
		transferredAddress = getIntent().getExtras().getString(ThreadsActivity.SELECT_ADDRESS);	
		readSmsDb();
		
		//записываем в настройках, что Activity на виду
		AppPreferences.setPreference(this, AppPreferences.CONV_ACTIVITY_ON_FRONT, true);
		Toast.makeText(this, "Conversation onStart", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		if(cursor.isClosed())
		{
			makeCursor();
			adapt.changeCursor(cursor);
		}
		Toast.makeText(this, "Conversation onResume", Toast.LENGTH_SHORT).show();	
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		Toast.makeText(this, "Conversation onSaveInstanceState", Toast.LENGTH_SHORT).show();
	    super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onPause()
	{
		Toast.makeText(this, "Conversation onPause", Toast.LENGTH_SHORT).show();
		cursor.close();
		super.onPause();		
	}

	@Override
	protected void onStop()
	{
		Toast.makeText(this, "Conversation onStop", Toast.LENGTH_SHORT).show();
		
		//Activity не видно
		AppPreferences.setPreference(this, AppPreferences.CONV_ACTIVITY_ON_FRONT, false);
		finish();
		super.onStop();		
	}

	@Override
	protected void onDestroy()
	{
		Toast.makeText(this, "Conversation onDestroy", Toast.LENGTH_SHORT).show();		
		super.onDestroy();
	}

	/*Обработка нажатий на Меню*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case (android.R.id.home):
				//Intent intent = new Intent(this, ThreadsActivity.class);
				//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				//startActivity(intent);
				this.onBackPressed();
				return true;
			case (REFRESH):
				makeCursor();
				adapt.changeCursor(cursor);
				adapt.notifyDataSetChanged();
				return true;
		    default:
				return super.onOptionsItemSelected(item);
		}
	}

	/*Обработка нажатий View элементов*/
	@Override
	public void onClick(View item)
	{
		switch(item.getId())
		{
		    case R.id.btnSendText:
				if(editSendText.getText().length()>0)
				{
					//Размещаем сообщение в базу данных
					ContentValues addValues = new ContentValues();
					
					addValues.put(ADDRESS, transferredAddress);
					addValues.put(THREAD, cursor.getInt(cursor.getColumnIndex(THREAD)));
					addValues.put(BODY, editSendText.getText().toString());
					addValues.put(TYPE, TO);
					
					ContentResolver content = getContentResolver();
					Uri uri = content.insert(smsBox, addValues);
				    Toast.makeText(this, uri.toString(), Toast.LENGTH_LONG).show();
					
					//Отправка сообщения
					sms.send(transferredAddress, editSendText.getText().toString());
					
					makeCursor();
					adapt.changeCursor(cursor);
					adapt.notifyDataSetChanged();
					editSendText.setText("");
					
					Toast.makeText(this, "Сообщение отправлено", Toast.LENGTH_SHORT).show();
	            }
			break;
		}
	}
	
	/*Для отображения реакции при нажатии на элемент*/
	@Override
	public boolean onTouch(View item, MotionEvent event)
	{
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
			switch (item.getId())
		    {   		
			case R.id.btnSendText:
				if(editSendText.getText().length()>0)
				{
				    btnSendText.setBackgroundColor(Color.LTGRAY);
				    btnSendText.bringToFront();
				}
				break;		
		    }
			break;
			
		    case MotionEvent.ACTION_UP:	
			switch (item.getId()){
			case R.id.btnSendText:
				btnSendText.setBackgroundColor(Color.WHITE);
				btnSendText.bringToFront();
				break;
		    }
		    break;

		}
		return false;
	}
	
	/*Для чтения содержимого базы данных смс по address*/
	private void readSmsDb()
	{	
		try
		{
		    makeCursor();
		}
        catch (Exception e)
        {
	        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			cursor = null;
        }
		
		if(cursor != null)
		{
			adapt = new ConversationAdapter(this, cursor,
				R.layout.conv_row_from,
				R.drawable.ic_action_person,
				R.layout.conv_row_to,
				R.drawable.ic_action_person,
		    	new String [] {BODY, DATE, TYPE},
	    		new int []{R.id.conv_from_text,
		        	R.id.conv_from_service_text,
					R.id.conv_from_image},
				new int []{R.id.conv_to_text,
					R.id.conv_to_service_text,
					R.id.conv_to_image});
					
			convListView.setAdapter(adapt);

        }
	}
	
	//выделил в метод, чтобы обновлять курсор по запросу
	private void makeCursor()
	{
		String like =  "'%" + transferredAddress.substring(2) + "'";
		String where = ADDRESS + " LIKE " + like;
		String order = DATE + " ASC";//сортировка по возрастанию даты
		cursor = this.getContentResolver().query(smsBox, filter, where, null, order);
	}
}
