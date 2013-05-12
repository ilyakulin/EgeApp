package com.example.egeapp;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Formuls extends Activity {

	final String ATTRIBUTE_ITEM_CAPTION = "Item_CAPTION";
	final String ATTRIBUTE_BUTTON_CAPTION = "Button_CAPTION";
	
	
	ListView listview_formuls;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_formuls);
		listview_formuls = (ListView)findViewById(R.id.listView_formuls);
		
		ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>(10);		
		HashMap<String, String> item = new HashMap<String, String>();
		
		for (int i =0; i <100; i++)
		{
			item.put(ATTRIBUTE_ITEM_CAPTION,"Ёлемент номер " + i);			
			data.add(item);
			item = new HashMap<String, String>();
		}
		
		String[] from = new String[] {ATTRIBUTE_ITEM_CAPTION};
		int[] to = new int[] {R.id.listview_textview_item_caption};
		
		SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.listview_formuls_item_layout, from, to);
		
		listview_formuls.setAdapter(adapter);
		
	}



}
