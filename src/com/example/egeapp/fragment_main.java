package com.example.egeapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

public class fragment_main extends Fragment implements OnClickListener {
	
	
	WolframInput activity;
	EditText tw;
	
	public fragment_main(WolframInput activity) {
		// TODO Auto-generated constructor stub
		this.activity = activity;
		tw = activity.tw;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main,null);
		v.findViewById(R.id.button_wolfragment_main_0).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_main_1).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_main_2).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_main_3).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_main_4).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_main_5).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_main_6).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_main_7).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_main_8).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_main_9).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_main_add).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_main_comma).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_main_divide).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_main_dot).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_main_multiply).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_main_substract).setOnClickListener(this);
		
		return v;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int cursorPos=0;
		switch (v.getId())
		{
			case R.id.button_wolfragment_main_0 :{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "0", cursorPos));
				tw.setSelection(cursorPos+1);
				break;
			}
			case R.id.button_wolfragment_main_1 :{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "1", cursorPos));
				tw.setSelection(cursorPos+1);
				break;
			}
			case R.id.button_wolfragment_main_2 :{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "2", cursorPos));
				tw.setSelection(cursorPos+1);
				break;
			}
			case R.id.button_wolfragment_main_3 :{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "3", cursorPos));
				tw.setSelection(cursorPos+1);
				break;
			}
			case R.id.button_wolfragment_main_4 :{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "4", cursorPos));
				tw.setSelection(cursorPos+1);
				break;
			}
			case R.id.button_wolfragment_main_5 :{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "5", cursorPos));
				tw.setSelection(cursorPos+1);
				break;
			}
			case R.id.button_wolfragment_main_6 :{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "6", cursorPos));
				tw.setSelection(cursorPos+1);
				break;
			}
			case R.id.button_wolfragment_main_7 :{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "7", cursorPos));
				tw.setSelection(cursorPos+1);
				break;
			}
			case R.id.button_wolfragment_main_8 :{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "8", cursorPos));
				tw.setSelection(cursorPos+1);
				break;
			}
			case R.id.button_wolfragment_main_9 :{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "9", cursorPos));
				tw.setSelection(cursorPos+1);
				break;
			}
			case R.id.button_wolfragment_main_add :{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "+", cursorPos));
				tw.setSelection(cursorPos+1);
				break;
			}
			case R.id.button_wolfragment_main_comma :{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), ",", cursorPos));
				tw.setSelection(cursorPos+1);
				break;
			}
			case R.id.button_wolfragment_main_divide :{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "/", cursorPos));
				tw.setSelection(cursorPos+1);
				break;
			}
			case R.id.button_wolfragment_main_dot :{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), ".", cursorPos));
				tw.setSelection(cursorPos+1);
				break;
			}
			case R.id.button_wolfragment_main_multiply :{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "\u00B7", cursorPos));
				tw.setSelection(cursorPos+1);
				break;
			}
			case R.id.button_wolfragment_main_substract :{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "-", cursorPos));
				tw.setSelection(cursorPos+1);
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
