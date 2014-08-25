package ru.lazarev.database;

import android.widget.*;
import android.view.*;
import java.util.*;
import android.database.*;
import android.content.*;
import android.widget.RadioGroup.*;

public class ConversationAdapter extends BaseAdapter
{
    private Cursor cursor;
	private int viewFrom;
	private int viewTo;
	private int imageFrom;
	private int imageTo;
	private int [] resourcesFrom;
	private int [] resourcesTo;
	private String [] fields;
	private Context context;
	private final int TO = 2;
	private final int FROM = 1;

	public ConversationAdapter(Context context, Cursor cursor, int viewFrom, int imageFrom, int viewTo, int imageTo, String[] fields, int[] resourcesFrom, int [] resourcesTo)
	{
		this.cursor = cursor;
		this.viewFrom = viewFrom;
		this.viewTo = viewTo;
		this.imageFrom = imageFrom;
		this.imageTo = imageTo;
		this.resourcesFrom = resourcesFrom;
		this.resourcesTo = resourcesTo;
		this.fields = fields;
		this.context = context;
	}
	
	@Override
	public int getCount()
	{
		return cursor.getCount();
	}

	@Override
	public Object getItem(int position)
	{
		int i = getCount();
		cursor.moveToLast();
		//в обратном порядке, чтобы отображалось последнее сообщение без прокрутки
		while (i != getCount() - position)
		{
			cursor.moveToPrevious();
			i--;
		}		
		return (Object) cursor;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}
	
	public void changeCursor(Cursor cursor)
	{
		this.cursor = cursor;
	}

	@Override
	public View getView(int position, View view, ViewGroup vGroup)
	{
		
		int [] resources = {};
		int image = 0;
		
		LayoutInflater inflater = (LayoutInflater) 
		        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		

		Cursor cur = (Cursor) this.getItem(position);
		
		if(cur.getInt(cur.getColumnIndex(fields[fields.length-1])) == FROM)
		{
			view = inflater.inflate(viewFrom, null);
			resources = resourcesFrom;
			image = imageFrom;
		} 
		else if(cur.getInt(cur.getColumnIndex(fields[fields.length-1])) == TO)
		{
			view = inflater.inflate(viewTo, null);
			resources = resourcesTo;
			image = imageTo;
		}		
		
		for (int i = 0; i < resources.length; i ++)
		{
			if (view.findViewById(resources[i]) instanceof TextView)
			{		
				String text;
			    if (fields[i].contains("date"))
				{
					Date d = new Date(cur.getLong(cur.getColumnIndex(fields[i])));
					text = d.toString();
				}
				else
				{
					text = cur.getString(cur.getColumnIndex(fields[i]));
				}
	
				((TextView) view.findViewById(resources[i])).setText(text);
			} 
			else if (view.findViewById(resources[i]) instanceof ImageView)
			{
				//int res = cur.getInt(cur.getColumnIndex(fields[i]));
				//image = R.drawable.ic_action_person;
				((ImageView) view.findViewById(resources[i])).setImageResource(image);
			}			 
		}
		
		return view;
	}

}
