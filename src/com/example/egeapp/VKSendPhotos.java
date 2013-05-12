package com.example.egeapp;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class VKSendPhotos extends Activity implements OnClickListener {

	public static final int CODE_AUTH_friendslist = 1;
	public static final int CODE_AUTH_takephoto = 2;
	public static final int CODE_SWITCH_USER = 3;
	final static int REQUEST_CODE_CAMERA = 4;
	int photoCount = 0;
	String pathPhoto;

	SharedPreferences settings;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vksend_photos);
		findViewById(R.id.button_send_photo).setOnClickListener(this);
		findViewById(R.id.button_select_friends).setOnClickListener(this);
		findViewById(R.id.button_change_user).setOnClickListener(this);
		settings = getSharedPreferences(SH.VK_SETTINGS_SHARED, MODE_PRIVATE);
	}

	boolean isUserLoggedInAlready() {
		String vk_id = settings.getString(SH.VK_SETTINGS_USER_ID, null);
		return !(vk_id == null || vk_id.equals(""));
	}

	boolean isTokenOK() {
		Long expires_in = settings.getLong(SH.VK_SETTINGS_EXPIRES_IN, 86400) * 1000;
		Long request_time = settings.getLong(SH.VK_SETTINGS_REQUEST_TIME, 0L);
		Long currentTime = System.currentTimeMillis();
		Long result = currentTime - request_time - expires_in;
		boolean b = result < 0;
		return b;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK)
			switch (requestCode) {
				case CODE_AUTH_friendslist : {
					startActivity(new Intent(this, VK_friends_list.class));
					break;
				}
				case CODE_SWITCH_USER : {
					String user_id = settings.getString(SH.VK_SETTINGS_USER_ID,
							null);
					if (user_id == null || user_id.equals(""))
						Toast.makeText(this, "Текущий пользователь не выбран",
								Toast.LENGTH_LONG).show();
					else
						Toast.makeText(this,
								"ID текущего пользователя: " + user_id,
								Toast.LENGTH_LONG).show();
					break;
				}

				case REQUEST_CODE_CAMERA : {
					String[] projection = {MediaStore.Images.Media._ID};
					String sort = MediaStore.Images.Media._ID + " DESC";
					// запрос крошечный. по количеству фоток с их айди.
					// точно на быстрой памяти устройства. ничего не повиснет.
					@SuppressWarnings("deprecation")
					Cursor myCursor = getContentResolver().query(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							projection, null, null, sort);

					long imageId = 0l;
					try {
						myCursor.moveToFirst();
						imageId = myCursor
								.getLong(myCursor
										.getColumnIndexOrThrow(MediaStore.Images.Media._ID));

					} finally {
						myCursor.close();
					}

					Uri uriLargeImage = Uri.withAppendedPath(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							String.valueOf(imageId));

					File f = new File(getRealPathFromURI(uriLargeImage));
					if (f.exists()) {
						AsyncPhotoDownload async = new AsyncPhotoDownload(this,
								f);
						async.execute();
					}

				}

					break;
			}
	}



	private String getRealPathFromURI(Uri contentURI) {
		Cursor cursor = getContentResolver().query(contentURI, null, null,
				null, null);
		cursor.moveToFirst();
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
		return cursor.getString(idx);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_send_photo : {

				//if (isUserLoggedInAlready())
				boolean b1 =  isUserLoggedInAlready();
				boolean b2 =  isTokenOK();
				if(!b1||!b2)
					{
						Intent intent = new Intent(this, AuthVK.class);
						intent.putExtra("mode", "auth");
						startActivity(intent);
						return;
					}
				String friends2send = settings.getString(
						SH.VK_SETTINGS_FRIENDS_TO_SEND, "");
				if (friends2send == null || friends2send.equals("")) {
					Toast.makeText(this,
							"Не выбрано пользователей для отправки фото",
							Toast.LENGTH_LONG).show();
					return;
				}

				Intent takePictureIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);

				startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
				break;
			}
			case R.id.button_select_friends : {
				if (isUserLoggedInAlready())
					startActivity(new Intent(this, VK_friends_list.class));
				else {
					Intent intent = new Intent(this, AuthVK.class);
					intent.putExtra("mode", "auth");
					startActivityForResult(intent, CODE_AUTH_friendslist);
				}

				break;
			}
			case R.id.button_change_user : {
				if (isUserLoggedInAlready()) {
					Intent intent = new Intent(this, AuthVK.class);
					intent.putExtra("mode", "switch_user");
					startActivityForResult(intent, CODE_SWITCH_USER);
				} else {
					Intent intent = new Intent(this, AuthVK.class);
					intent.putExtra("mode", "auth");
					startActivityForResult(intent, CODE_SWITCH_USER);
				}
				break;
			}

		}
	}

}
