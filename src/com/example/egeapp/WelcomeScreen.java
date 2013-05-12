package com.example.egeapp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

public class WelcomeScreen extends Activity implements OnClickListener {

	HashMap<String, String> groupAtr;
	HashMap<String, String> elemAtr;

	LinkedList<HashMap<String, String>> groupList;
	LinkedList<HashMap<String, String>> elemList;

	LinkedList<List<HashMap<String, String>>> generalList;

	String[] subjects = new String[]{"Общие сведения", "Руссикй язык",
			"Математика"};

	String[] itemDescription;

	SharedPreferences sharedSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getSharedPreferences("egeapp", MODE_PRIVATE).getBoolean(
				"dont_showWelcome", false))
			startActivity(new Intent(this, MainMenu.class));

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome_screen);
		findViewById(R.id.btnwelscr).setOnClickListener(this);

		itemDescription = getResources().getStringArray(R.array.description);

		// Список групп. В таком списке хранятся атрибуты
		groupList = new LinkedList<HashMap<String, String>>();

		for (int i = 0; i < subjects.length; i++) {
			groupAtr = new HashMap<String, String>();
			groupAtr.put("nameOfSubject", subjects[i]);
			groupList.add(groupAtr);
		}
		// Список атрибутов групп и id text view для этих атрибутов
		String[] groupAtrNames = new String[]{"nameOfSubject"};
		int[] groupIds = new int[]{android.R.id.text1};

		// Список всех элементов
		generalList = new LinkedList<List<HashMap<String, String>>>();

		{
			int ind = 0;
			for (@SuppressWarnings("unused") HashMap<String, String> i : groupList) {
				elemList = new LinkedList<HashMap<String, String>>();
				elemAtr = new HashMap<String, String>();
				elemAtr.put("itemDescription", itemDescription[ind++]);
				elemList.add(elemAtr);
				generalList.add(elemList);
			}
		}

		String[] itemAtrNames = new String[]{"itemDescription"};
		int[] itemIds = new int[]{android.R.id.text1};

		SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
				this, groupList,
				android.R.layout.simple_expandable_list_item_2, groupAtrNames,
				groupIds, generalList, android.R.layout.simple_list_item_1,
				itemAtrNames, itemIds);
		((ExpandableListView) findViewById(com.example.egeapp.R.id.expandableListView1))
				.setAdapter(adapter);

	}
		

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btnwelscr : {
				sharedSettings = getSharedPreferences("egeapp", MODE_PRIVATE);
				Editor editor = sharedSettings.edit();
				editor.putBoolean("dont_showWelcome", true);
				editor.apply();
				startActivity(new Intent(this, MainMenu.class));
				break;
			}
		}
	}

}
