package ru.lazarev.database;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.provider.*;
import android.database.*;
import android.net.*;
import java.util.*;
import android.content.*;
import android.widget.AdapterView.*;

public class ContactsActivity extends ListActivity
{
	SimpleCursorAdapter adapt;
	final String DISPLAY_NAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
	final int DISPLAY_NAME_INT = 1;
	final String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
	final int NUMBER_INT = 2;
	final static String TRANSFER_NAME = "Name";
	final static String TRANSFER_NUMBER = "address";
	
	@Override
	public void onListItemClick(ListView lstView,View view, int pos, long posLong)
	{
		super.onListItemClick(lstView,view,pos,posLong);	
		Cursor cur = (Cursor) adapt.getItem(pos);
		
		Intent conversation = new Intent(this, ConversationActivity.class);
		//conversation.putExtra(TRANSFER_NAME, cur.getString(DISPLAY_NAME_INT));
		conversation.putExtra(TRANSFER_NUMBER, cur.getString(cur.getColumnIndex(NUMBER)));
		startActivityForResult(conversation, 101);
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		
		Uri cont = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] filter = {"_id", DISPLAY_NAME, NUMBER};
		
		Cursor cursor = getContentResolver().query(cont, filter, null, null,null);
		
		adapt = new SimpleCursorAdapter(this, 
		        R.layout.thread_form,
		        cursor,
				new String[] {DISPLAY_NAME, NUMBER},
		        new int[]{R.id.thread_form_address, R.id.thread_form_snippet});
		
		//adapt.setViewBinder(new MyCursorViewBinder());
		setListAdapter(adapt);

		ActionBar actBar = getActionBar();
		actBar.setTitle(R.string.contacts);		
    }

	private final int MENU_SETTING = 102;
	private final int MENU_FIND = 101;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(Menu.NONE, MENU_FIND, Menu.NONE, "Find");
		menu.add(Menu.NONE, MENU_SETTING, Menu.NONE, "Settings");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case MENU_FIND:
				Toast.makeText(this, "Menu Find", Toast.LENGTH_SHORT).show();
				break;
			case MENU_SETTING:
				Toast.makeText(this, "Menu Setting", Toast.LENGTH_SHORT).show();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}

class MyCursorViewBinder implements SimpleCursorAdapter.ViewBinder
{
	final int DISPLAY_NAME_INT = 1;
	final int NUMBER_INT = 2;
	
	@Override
	public boolean setViewValue(View view, Cursor curs, int pos)
	{
		switch(view.getId())
		{
			case R.id.txt_name:
				((TextView)view).setText(curs.getString(pos));break;
			case R.id.txt_phone:
				((TextView)view).setText(curs.getString(pos));break;
		}
		return false;
	}

	
}
