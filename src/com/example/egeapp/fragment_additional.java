package com.example.egeapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

public class fragment_additional extends Fragment implements OnClickListener {

	WolframInput activity;
	EditText tw;

	public fragment_additional(WolframInput activity) {
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
		View v = inflater.inflate(R.layout.fragment_additional, null);
		v.findViewById(R.id.button_wolfragment_additional_abs).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_additional_cos).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_additional_ctg).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_additional_derivate).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_additional_derivate_second).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_additional_e).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_additional_limit).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_additional_ln).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_additional_log).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_additional_percentt).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_additional_pi).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_additional_power).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_additional_sin).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_additional_squareroot).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_additional_tg).setOnClickListener(this);
		v.findViewById(R.id.button_wolfragment_additional_y).setOnClickListener(this);
		
		return v;
	}
	@Override
	public void onClick(View v) {
		int cursorPos=0;
		switch(v.getId())
		{
			case R.id.button_wolfragment_additional_abs:
			{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "abs()", cursorPos));
				tw.setSelection(cursorPos+4);			
				break;
			}
			case R.id.button_wolfragment_additional_cos:
			{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "cos()", cursorPos));
				tw.setSelection(cursorPos+4);				
				break;
			}
			case R.id.button_wolfragment_additional_ctg:
			{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "ctg()", cursorPos));
				tw.setSelection(cursorPos+4);				
				break;
			}
			case R.id.button_wolfragment_additional_derivate:
			{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "d/dx()", cursorPos));
				tw.setSelection(cursorPos+5);				
				break;
			}
			case R.id.button_wolfragment_additional_derivate_second:
			{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "d^2/dx^2()", cursorPos));
				tw.setSelection(cursorPos+9);						
				break;
			}
			case R.id.button_wolfragment_additional_e:
			{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "e", cursorPos));
				tw.setSelection(cursorPos+1);						
				break;
			}
			case R.id.button_wolfragment_additional_limit:
			{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "lim() as x -> inf", cursorPos));
				tw.setSelection(cursorPos+4);
				break;
			}
			case R.id.button_wolfragment_additional_ln:
			{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "ln()", cursorPos));
				tw.setSelection(cursorPos+3);				
				break;
			}
			case R.id.button_wolfragment_additional_log:
			{		
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "log(,)", cursorPos));
				tw.setSelection(cursorPos+4);
				break;
			}
			case R.id.button_wolfragment_additional_percentt:
			{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "%", cursorPos));
				tw.setSelection(cursorPos+1);				
				break;
			}
			case R.id.button_wolfragment_additional_pi:
			{				
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "pi", cursorPos));
				tw.setSelection(cursorPos+2);
				break;
			}
			case R.id.button_wolfragment_additional_power:
			{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "()^()", cursorPos));
				tw.setSelection(cursorPos+1);
				break;
			}
			case R.id.button_wolfragment_additional_sin:{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "sin()", cursorPos));
				tw.setSelection(cursorPos+4);
				break;	
			}
			
			case R.id.button_wolfragment_additional_squareroot:{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "()^(1/2)", cursorPos));
				tw.setSelection(cursorPos+1);
				
				break;	
			}
			case R.id.button_wolfragment_additional_tg:{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "tg()", cursorPos));
				tw.setSelection(cursorPos+3);
				break;	
			}
			case R.id.button_wolfragment_additional_y:{
				cursorPos = tw.getSelectionStart();
				tw.setText(Insert(tw.getText().toString(), "Y", cursorPos));
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
