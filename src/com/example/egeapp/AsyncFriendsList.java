package com.example.egeapp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class AsyncFriendsList extends AsyncTask<Void, Void, Void> {

	public static final int LOAD_FRIENDS_LIST = 0;
	public static final int UPDATE_FRIENDS_STATUS = 1;
	public static final int UPDATE_FRIENDS_ALL = 2;
	public static final int UPDATE_FRIENDS_ICONS = 3;

	VK_friends_list activity;
	VK_friends_list_cursor_adapter adapter;	
	int state;
	SQLiteDatabase db;
	DataBaseHelper dbh;
	Cursor c;
	public AsyncFriendsList(VK_friends_list activity, int whatToDo) {
		this.activity = activity;		
		state = whatToDo;		
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		activity.findViewById(R.id.progressBar_VK_friends).setVisibility(
				View.VISIBLE);
		activity.findViewById(R.id.listView_VK_friends)
				.setVisibility(View.GONE);
		adapter = new VK_friends_list_cursor_adapter(activity,
				R.layout.activity_vk_user_item, null, true);
		if (state > 0)// work with Network
			if (!Assistentnetwork.IsOnline(activity)) {
				Assistentnetwork.showNoConnectionDialog(activity, true);
				this.cancel(true);// выход в любом случае. никак не успеть сеть
									// включить.
			}

	}

	// фишка в том, что курсор формируется в background thread, а подставляется в UI thread
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);

		adapter.changeCursor(c);
		adapter.SetFieldsIndsFromCursor();
		((ListView) activity.findViewById(R.id.listView_VK_friends))
				.setAdapter(adapter);
		
		activity._dbh = dbh;
		activity._c = c;
		activity.findViewById(R.id.progressBar_VK_friends).setVisibility(
				View.GONE);
		activity.findViewById(R.id.listView_VK_friends).setVisibility(
				View.VISIBLE);

	}
	// 
	void ConnectToDB(){
		dbh = new DataBaseHelper(activity);
		db  = dbh.getWritableDatabase();
	}
	@Override
	protected Void doInBackground(Void... params) {
		ConnectToDB();
		switch (state) {

			case LOAD_FRIENDS_LIST : {
				updateCursor();
				if (!c.moveToFirst()) {
					loadFriendsVK();
					LoadPhotosVK();
					updateCursor();
				}
				break;

			}
			case UPDATE_FRIENDS_STATUS : {
				updateFriendsStatus();
				updateCursor();
				break;
			}
			case UPDATE_FRIENDS_ALL : {
				loadFriendsVK();
				LoadPhotosVK();
				updateCursor();
				break;
			}
			case UPDATE_FRIENDS_ICONS : {
				LoadPhotosVK();
				updateCursor();
				break;
			}
		}
		return null;
	}

	private void updateCursor() { // pidaraci blyat! goto <case:smth> netu!!11		
		c = db.query(SH.DB_TABLE_VK, new String[]{SH.DB_TABLE_VK_COLUMNS_ID,
				SH.DB_TABLE_VK_COLUMNS_NAME, SH.DB_TABLE_VK_COLUMNS_IMAGE_NAME,
				SH.DB_TABLE_VK_COLUMNS_STATUS}, null, null, null, null,
				"status desc");

	}

	void LoadPhotosVK() {
		try {

			Cursor c = db.query(SH.DB_TABLE_VK, new String[]{
					SH.DB_TABLE_VK_COLUMNS_IMAGE_HREF,
					SH.DB_TABLE_VK_COLUMNS_IMAGE_NAME}, null, null, null, null,
					null);

			if (c.moveToFirst()) {
				int columnImageName = c
						.getColumnIndex(SH.DB_TABLE_VK_COLUMNS_IMAGE_NAME);
				int columnImageHref = c
						.getColumnIndex(SH.DB_TABLE_VK_COLUMNS_IMAGE_HREF);
				URL url;
				URLConnection connection;
				FileOutputStream outputStream;
				InputStream inputStream;
				File AppDir = activity.getFilesDir();

				do {
					if (!c.getString(columnImageHref).equals("no_photo")) {
						File f = new File(AppDir, c.getString(columnImageName));
						// качаем только новые
						if (!f.exists()) {
							url = new URL(c.getString(columnImageHref));
							connection = url.openConnection();
							connection.connect();
							outputStream = new FileOutputStream(f);
							inputStream = null;
							{
								int count = 0;
								do {
									try {
										inputStream = new BufferedInputStream(
												url.openStream());
										break;
									} catch (Exception e) {
										count++;
										if (count == 3)
											throw new Exception(
													"Error while downloading friends' faces");
									}
								} while (true);
							}

							byte data[] = new byte[1024];

							int count;
							while ((count = inputStream.read(data)) != -1)
								outputStream.write(data, 0, count);

							outputStream.flush();
							outputStream.close();
							inputStream.close();

						}

					}
				} while (c.moveToNext());

				c.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void updateFriendsStatus() {
		try {

			HttpPost postQuery = new HttpPost(SH.VK_API_URL + "friends.get?");

			List<NameValuePair> data = new ArrayList<NameValuePair>();

			data.add(new BasicNameValuePair("uid", activity
					.getSharedPreferences(SH.VK_SETTINGS_SHARED,
							android.content.Context.MODE_PRIVATE).getString(
							SH.VK_SETTINGS_USER_ID, null)));

			data.add(new BasicNameValuePair("fields", "uid,online"));

			postQuery.setEntity(new UrlEncodedFormEntity(data));

			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse response = null;
			{
				int count = 0;
				do {
					try {
						response = client.execute(postQuery);
						break;
					} catch (Exception e) {
						// какие-то проблемы при загрузке. попробуем повторить
						// несколько
						// раз.
						count++;
						if (count == 3)
							throw new Exception(
									"Error while executing friends.get method");
					}
				} while (true);
			}

			HttpEntity recData = response.getEntity();

			String s = EntityUtils.toString(recData);
			JSONObject parser = new JSONObject(s);
			if (parser.opt("error") != null)
				throw new Exception("error received while getting friends list");
			JSONArray users = parser.optJSONArray("response");
			if (users != null) {

				ContentValues cv = new ContentValues();
				String id;
				db.beginTransaction();
				for (int i = 0; i < users.length(); i++) {
					JSONObject item = users.getJSONObject(i);

					id = item.getString("uid");
					cv.put("status", String.valueOf(item.getInt("online")));
					db.update(SH.DB_TABLE_VK, cv, "vk_id=" + id, null);
					cv.clear();
				}
				db.setTransactionSuccessful();
				db.endTransaction();
			} else {
				// пришел пустой response - нет друзей
			}

		} catch (Exception e) {

			e.printStackTrace();
			if (db.inTransaction())
				db.endTransaction();
		}
	}

	void loadFriendsVK() {
		try {

			HttpPost postQuery = new HttpPost(SH.VK_API_URL + "friends.get?");

			List<NameValuePair> data = new ArrayList<NameValuePair>();

			data.add(new BasicNameValuePair("uid", activity
					.getSharedPreferences(SH.VK_SETTINGS_SHARED,
							android.content.Context.MODE_PRIVATE).getString(
							SH.VK_SETTINGS_USER_ID, null)));
			data.add(new BasicNameValuePair("order", "name"));
			data.add(new BasicNameValuePair("fields",
					"uid,first_name,last_name,online,photo_100"));
			postQuery.setEntity(new UrlEncodedFormEntity(data));

			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse response = null;
			{
				int count = 0;
				do {
					try {
						response = client.execute(postQuery);
						break;
					} catch (Exception e) {
						count++;
						if (count == 3)
							throw new Exception(
									"Error while executing friends.get method");
					}
				} while (true);
			}

			HttpEntity recData = response.getEntity();

			String s = EntityUtils.toString(recData);
			JSONObject parser = new JSONObject(s);
			if (parser.opt("error") != null)
				throw new Exception("error received while getting friends list");
			JSONArray users = parser.optJSONArray("response");
			if (users != null) {

				ContentValues cv = new ContentValues();
				String tempPhotoHref;
				db.beginTransaction();
				for (int i = 0; i < users.length(); i++) {
					JSONObject item = users.getJSONObject(i);

					cv.put("vk_id", item.getString("uid"));
					cv.put("name",
							item.getString("first_name") + " "
									+ item.getString("last_name"));
					tempPhotoHref = item.getString("photo_100");
					if (tempPhotoHref.contains(SH.NO_PHOTO_HREF)) {
						cv.put("image_name", "no_photo");
						cv.put("image_href", "no_photo");
					} else {
						cv.put("image_name",
								tempPhotoHref
										.substring(
												tempPhotoHref.indexOf("vk.me") + 6)
										.replace('/', '_').toLowerCase());
						cv.put("image_href", tempPhotoHref);
					}
					cv.put("status", String.valueOf(item.getInt("online")));
					db.insert("vk_friends", null, cv);
					cv.clear();

				}
				db.setTransactionSuccessful();
				db.endTransaction();
			} else {
				// пришел пустой response - нет друзей
			}

		} catch (Exception e) {

			// тут может прийти error когда в SharedPreferences нет id. То есть
			// запуск загрузки друзей раньше авторизации.
			e.printStackTrace();
			Log.e(SH.LOGTAG, e.getMessage());
			if (db.inTransaction())
				db.endTransaction();
		}
	}

}
