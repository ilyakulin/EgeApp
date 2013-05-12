package com.example.egeapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class WolframResponse extends Activity
		implements
			ActionBar.OnNavigationListener {

	String query;
	TextView tw;
	ArrayAdapter<String> adapter;
	ActionBar bar;
	String[] podNames, podPlaintext;
	boolean firstTimeExecute;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wolfram_response);
		setTitle("");
		query = getIntent().getExtras().getString("query");
		tw = (TextView) findViewById(R.id.textView_wolfram_response);
		new AsyncWolfram(this, query).execute();
		bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);		
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		bar.hide();

	}

	@Override
	protected void onStart() {
		super.onStart();
		firstTimeExecute = true;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.wolfram_response, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		if(item!=null&&item.getItemId()==android.R.id.home){
			Intent intent = new Intent(this,WolframInput.class);
			intent.putExtra("response", "");
			startActivity(new Intent(this,WolframInput.class));
		}
		
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (podPlaintext != null
				&& podPlaintext[podPlaintext.length - 1] != null&&bar!=null) {
		bar.show();
			
		}	
//			menu.setGroupEnabled(R.id.grup_wolfram_copy_answer, true);
//			menu.setGroupVisible(R.id.grup_wolfram_copy_answer, true);

//		} else {
//			menu.setGroupEnabled(R.id.grup_wolfram_copy_answer, false);
//			menu.setGroupVisible(R.id.grup_wolfram_copy_answer, false);
//		}
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		
		if (podPlaintext != null && podPlaintext.length >= itemPosition) {
			if (!firstTimeExecute) {
				Intent intent = new Intent(this, WolframInput.class);
				intent.putExtra("response", podPlaintext[itemPosition-1]);
				startActivity(intent);
			} else
				firstTimeExecute = false;
		}

		return false;
	}


}
