package com.example.egeapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VK_friends_list extends Activity {


	SharedPreferences settings;	
	DataBaseHelper _dbh;
	Cursor _c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vk_friends_list);

		
		new AsyncFriendsList(this, AsyncFriendsList.LOAD_FRIENDS_LIST)
				.execute();

		settings = getSharedPreferences(SH.VK_SETTINGS_SHARED, MODE_PRIVATE);
		
		final ListView lw = (ListView) findViewById(R.id.listView_VK_friends);

		// Обработка выбора списка друзей для отправки им фотографий
		// Список друзей хранится в Sharedpreferences. Это строка
		// разделенная запятыми
		lw.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				String Friends = settings.getString(SH.VK_SETTINGS_FRIENDS_TO_SEND, null);
				String selectedFriend = ((TextView) arg1
						.findViewById(R.id.text_vk_user_name)).getText()
						.toString();
				Editor editor = settings.edit();
				if (Friends==null || Friends.equals("")) {
					editor.putString(SH.VK_SETTINGS_FRIENDS_TO_SEND, selectedFriend);
					editor.apply();
				} else {

					if(Friends.contains(selectedFriend)){
						
						Friends = Friends.replace(selectedFriend, "");						
						Friends = Friends.replace(",,", ",");
						if (Friends.startsWith(","))
							Friends = Friends.substring(1);
						if (Friends.endsWith(","))
							Friends = Friends.substring(0, Friends.length()-1);
						
						editor.putString(SH.VK_SETTINGS_FRIENDS_TO_SEND, Friends);
						editor.apply();
						Toast.makeText(
								VK_friends_list.this,
								selectedFriend + " removed!",
								Toast.LENGTH_SHORT).show();
					}
					else
					{
						editor.putString(SH.VK_SETTINGS_FRIENDS_TO_SEND, Friends + ","
							+ selectedFriend);												
						editor.apply();
						
						Toast.makeText(
								VK_friends_list.this,
								selectedFriend + " added!",
								Toast.LENGTH_SHORT).show();
					}
					
					
				}
				
				((VK_friends_list_cursor_adapter) lw.getAdapter())
				.UpdateFriendsToSend(false);
				String result_friends = settings.getString(SH.VK_SETTINGS_FRIENDS_TO_SEND, null);
				if (result_friends.equals(""))
				Toast.makeText(VK_friends_list.this,"Никто не выбран" , Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(VK_friends_list.this,result_friends, Toast.LENGTH_SHORT).show();
				

				

				return false;
			}
		});
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(!_c.isClosed()){
		_c.close();
		_dbh.close();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(1, 2, 2, "Обновить список");
		menu.add(0, 3, 3, "Показать выбранных");
		menu.add(0, 1, 1, "Обновить статус");

		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {	
		switch (item.getItemId()) {
			case 1 :
				(new AsyncFriendsList(this,
						AsyncFriendsList.UPDATE_FRIENDS_STATUS)).execute();
				break;
			case 2 :
				(new AsyncFriendsList(this, AsyncFriendsList.UPDATE_FRIENDS_ALL))
						.execute();
				break;
			case 3 :
				String result_friends = settings.getString(SH.VK_SETTINGS_FRIENDS_TO_SEND, null);
				if (result_friends.equals(""))
				Toast.makeText(VK_friends_list.this,"Никто не выбран" , Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(VK_friends_list.this,result_friends, Toast.LENGTH_SHORT).show();
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

}
