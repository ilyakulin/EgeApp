package com.example.egeapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class WolframInput extends FragmentActivity
		implements
			OnClickListener,
			OnLongClickListener {

	EditText tw;	// историческое название. времен эмулирования.
	ViewPager pager;
	InputMethodManager imm;	
	OnTouchListener keyShow, keyHide;
	int cursorPos;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wolfram_input);
		tw = (EditText) findViewById(R.id.textView_wolfram_input);
		pager = (ViewPager) findViewById(R.id.pager_wolfram);

		// Click
		findViewById(R.id.button_wolfram_moveLeft).setOnClickListener(this);
		findViewById(R.id.button_wolfram_moveLeft).setOnLongClickListener(this);
		findViewById(R.id.button_wolfram_x).setOnClickListener(this);
		findViewById(R.id.button_wolfram_equals).setOnClickListener(this);
		findViewById(R.id.button_wolfram_delete).setOnClickListener(this);
		findViewById(R.id.button_wolfram_bracket_left).setOnClickListener(this);
		findViewById(R.id.button_wolfram_bracket_right)
				.setOnClickListener(this);
		findViewById(R.id.button_wolfram_sent_query).setOnClickListener(this);

		// Long Click
		findViewById(R.id.button_wolfram_moveRight).setOnClickListener(this);
		findViewById(R.id.button_wolfram_moveRight)
				.setOnLongClickListener(this);
		findViewById(R.id.button_wolfram_delete).setOnLongClickListener(this);
		findViewById(R.id.button_wolfram_bracket_left).setOnLongClickListener(
				this);
		// Go - long click only. Prevent accidental click
		findViewById(R.id.button_wolfram_sent_query).setOnLongClickListener(
				this);

		// Pager
		WolframAdapter adapter = new WolframAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);

		// Action bar
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// TextView
		tw.setMovementMethod(new ScrollingMovementMethod());
		tw.setSelected(false);
		
		// Set text if available		
		Bundle data = getIntent().getExtras();
		if(data!=null)
			tw.setText(data.getString("response"));

		// hide keyboard
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);		
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		keyHide = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		};
		keyShow = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		};

		tw.setOnTouchListener(keyHide);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.wolfram_input, menu);
		return true;
	}

	boolean iskeyShown = false;
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
					
		if(item.getTitle().toString().equals("keyboard")){
			if (!iskeyShown) {
				item.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
				tw.setOnTouchListener(keyShow);
				iskeyShown = !iskeyShown;
			} else {
				tw.requestFocus();
				item.setIcon(android.R.drawable.ic_menu_add);
				tw.setOnTouchListener(keyHide);			 
				imm.hideSoftInputFromWindow(tw.getWindowToken(), 0);
				iskeyShown = !iskeyShown;

			}
		}
		else{
			startActivity(new Intent(this, MainMenu.class));
		}

		return true;
	}

	private class WolframAdapter extends FragmentPagerAdapter {
		public WolframAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0)
				return new fragment_main(WolframInput.this);
			if (position == 1)
				return new fragment_additional(WolframInput.this);
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}

	}

	// 15 characters shift
	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
			case R.id.button_wolfram_moveLeft : {
				int cursorPos = tw.getSelectionStart();
				if (cursorPos != 0) {
					int shift = cursorPos >= 15 ? 15 : cursorPos;
					tw.setSelection(cursorPos-shift);
				}
				break;
			}
			case R.id.button_wolfram_moveRight : {
				int length = tw.getText().length();
				int cursorPos = tw.getSelectionStart();
				if (cursorPos != length) {										
					int shift = cursorPos + 15 <= length ? 15 : length - cursorPos;
					tw.setSelection(cursorPos+shift);					
				}
				break;
			}
			case R.id.button_wolfram_delete : {
				tw.setText("");
				tw.setSelection(0);				
				break;
			}
			case R.id.button_wolfram_bracket_left : {
				int cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "()", cursorPos));
				tw.setSelection(cursorPos+1);				
				break;
			}

			case R.id.button_wolfram_sent_query : {
				Intent intent = new Intent(this, WolframResponse.class);
				intent.putExtra("query", tw.getText().toString());
				startActivity(intent);
				break;
			}
		}

		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

			case R.id.button_wolfram_moveLeft : {								
				cursorPos = tw.getSelectionStart();				
				if(cursorPos!=0)
				tw.setSelection(cursorPos-1);								
				break;
			}
			case R.id.button_wolfram_moveRight : {
				int length = tw.getText().length();
				cursorPos = tw.getSelectionStart();
				if (cursorPos != length) 
					tw.setSelection(cursorPos+1);			
				break;
			}
			case R.id.button_wolfram_x : {
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "X", cursorPos));
				tw.setSelection(cursorPos+1);					
				break;
			}
			case R.id.button_wolfram_equals : {
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "=", cursorPos));
				tw.setSelection(cursorPos+1);
				
				break;
			}
			case R.id.button_wolfram_bracket_left : {
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "(", cursorPos));
				tw.setSelection(cursorPos+1);
				break;
			}
			case R.id.button_wolfram_bracket_right : {
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), ")", cursorPos));
				tw.setSelection(cursorPos+1);
				break;
			}
			case R.id.button_wolfram_delete : {
				cursorPos = tw.getSelectionStart();
				if (cursorPos != 0) {
					char[] st = tw.getText().toString().toCharArray();

					for (int i = cursorPos - 1; i < st.length - 1; i++)
						st[i] = st[i + 1];
					tw.setText(new String(st).substring(0, st.length - 1));
					tw.setSelection(cursorPos-1);
					
				}
				break;
			}
		}
	}

	String Insert(String baseString, String what, int pos) {
		if (pos > baseString.length())
			return null;
		if (pos == 0)
			return what + baseString;
		if (pos == baseString.length())
			return baseString + what;
		int baseLen = baseString.length();
		int whatLen = what.length();
		char[] outAr = new char[baseLen + whatLen];
		char[] baseAr = baseString.toCharArray();
		char[] whatAr = what.toCharArray();
		for (int i = 0; i < pos; i++)
			outAr[i] = baseAr[i];
		for (int i = pos, counter = 0; counter < whatLen; i++, counter++)
			outAr[i] = whatAr[counter];
		for (int i = pos + whatLen, counter = pos; i < baseLen + whatLen; i++, counter++)
			outAr[i] = baseAr[counter];
		return new String(outAr);
	}

}
