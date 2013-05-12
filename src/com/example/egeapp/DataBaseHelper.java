package com.example.egeapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DataBaseHelper extends SQLiteOpenHelper {

	public DataBaseHelper(Context context) {
		super(context, "EgeAppDB", null, 1);
		
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		 Log.d("TestTest", "--- onCreate database ---");
		 
	      db.execSQL("create table vk_friends ("
		          + "_id integer primary key autoincrement,"
	    		  + "vk_id text not null unique,"
		          + "name text,"
		          + "image_name text,"
		          + "image_href text,"
		          +	"status text"
		          + ");");
	      
		 
	    return;   

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

}
