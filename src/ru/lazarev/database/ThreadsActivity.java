package ru.lazarev.database;
import android.app.*;
import android.os.*;
import android.net.*;
import android.widget.*;
import android.database.*;
import java.util.*;
import android.view.*;
import android.util.*;
import android.content.*;
import android.database.sqlite.*;
import android.provider.*;

public class ThreadsActivity extends ListActivity
{
	private SimpleAdapter adapt;
	private ArrayList<Map<String, Object>> list;
	private Cursor cursor = null;
	private Cursor threads_cur = null;
	private String savedAddress;
	private SharedPreferences pref;
	private final String ADDRESS = "address";
	private final String BODY = "body";
	private final String THREAD = "thread_id";
	private final String DATE = "date";
	private final String TYPE = "type";
	private final String MSG_COUNT = "msg_count";
	private final String SNIPPET = "snippet";
	private final String PERSON_IMAGE = "image";	
	private Uri smsBox = Uri.parse("content://sms/");
	private Uri threads = Uri.parse("content://sms/conversations");
	final static String SELECT_ADDRESS="select_address";
    final static int RESULT_OK = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Toast.makeText(this, "Threads onCreate", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		// TODO: Implement this method
		super.onNewIntent(intent);
	}

	private final int MENU_NEW_MESSAGE = 101;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(Menu.NONE, MENU_NEW_MESSAGE, Menu.NONE, R.string.menu_item_new_message);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case MENU_NEW_MESSAGE:
			    //Intent intent = new Intent(this, ConversationFragment.class);	
			    //Intent intent = new Intent(this, ContactsActivity.class);
				//startActivity(intent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onRestoreInstanceState(Bundle state)
	{
		super.onRestoreInstanceState(state);
		Toast.makeText(this, "Threads onRestoreInstanceState", Toast.LENGTH_SHORT).show();
	}	

	@Override
	protected void onStart()
	{
		super.onStart();
		Toast.makeText(this, "Threads onStart", Toast.LENGTH_SHORT).show();
	}		

	@Override
	protected void onResume()
	{
		super.onResume();
		readSmsDb();
		if (AppPreferences.getBoolean(this, AppPreferences.B_CONVERSATION_CALLED))
		{
			Intent conversation = new Intent(this, ConversationActivity.class);
			savedAddress = AppPreferences.getString(this, AppPreferences.S_SAVED_ADDRESS);
			conversation.putExtra(SELECT_ADDRESS, savedAddress);
			startActivityForResult(conversation, 101);
		}
		Toast.makeText(this, "Threads onResume", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		Toast.makeText(this, "Threads onPause", Toast.LENGTH_SHORT).show();
		cursor.close();
		threads_cur.close();
	}

	@Override
	protected void onStop()
	{
		Toast.makeText(this, "Threads onStop", Toast.LENGTH_SHORT).show();
		super.onStop();
	}

	@Override
	protected void onDestroy()
	{
		Toast.makeText(this, "Threads onDestroy", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Toast.makeText(this, "Threads onActivityResult", Toast.LENGTH_SHORT).show();
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		Toast.makeText(this, "Threads onSaveInstanceState", Toast.LENGTH_SHORT).show();
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		
		//получаем из позиции адаптера Map обьект
	    HashMap map = (HashMap) adapt.getItem(position);
		//извлекаю по ключу значение
		savedAddress = (String) map.get("address");
		//вызываем активность беседы
		Intent conversation = new Intent(this, ConversationActivity.class);
		conversation.putExtra(SELECT_ADDRESS, savedAddress);
		AppPreferences.setPreference(this, AppPreferences.S_SAVED_ADDRESS, savedAddress);
		AppPreferences.setPreference(this, AppPreferences.B_CONVERSATION_CALLED, true);	
		startActivityForResult(conversation, 101);
		
	}
	
	private void readSmsDb()
	{	
		try
		{	   			    
			//запрос базы данных смс
            makeCursor();
		}
		catch(Exception e)
		{
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	
		if(cursor.getCount() != 0 && threads_cur.getCount() != 0)
		{			
			list = new ArrayList<Map<String, Object>>();
			ArrayList<Integer> threads_list = new ArrayList<>();
			Map<String, Object> map;
			int count = 0;
			
			cursor.moveToFirst();			
			do
			{
				threads_cur.moveToFirst();
				int item = cursor.getInt(cursor.getColumnIndex(THREAD));
				do
				{
					if(item == threads_cur.getInt(threads_cur.getColumnIndex(THREAD)) &&
					   !threads_list.contains(item))
					{
						map = new HashMap<String, Object>();
						//добавить преобразование даты в нормальный вид
						map.put(ADDRESS, cursor.getString(cursor.getColumnIndex(ADDRESS)));
						map.put(SNIPPET, threads_cur.getString(threads_cur.getColumnIndex(SNIPPET)));
						map.put(DATE, cursor.getString(cursor.getColumnIndex(DATE)));
						map.put(PERSON_IMAGE, R.drawable.ic_action_person);
						list.add(map);
						threads_list.add(item);
						count++;
						break;
					}	
				} while (threads_cur.moveToNext()); 
				
			    if (count == threads_cur.getCount()) break;
			} while (cursor.moveToNext());
			
		    adapt = new SimpleAdapter(this, list,
						R.layout.thread_form,
						new String[] {ADDRESS, SNIPPET, PERSON_IMAGE},
						new int[]{R.id.thread_form_address,
						R.id.thread_form_snippet,
						R.id.thread_form_image});
			setListAdapter(adapt); 
        }
	}
	
	private void makeCursor()
	{
	    String[] filter = new String [] {"_id", ADDRESS, BODY, THREAD, DATE, TYPE, "person"};//поля
	    String order = DATE + " DESC";//сортировка по убыванию потоков	
		//база данных диалогов
		threads_cur = getContentResolver().query(threads, 
				new String [] {THREAD, MSG_COUNT, SNIPPET},
				null, null, THREAD + " ASC");
		//запрашиваем базу данных смс
        cursor = getContentResolver().query(smsBox, filter, null, null, order);
	}
		
}
