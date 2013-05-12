package com.example.egeapp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AsyncPhotoDownload extends AsyncTask<Void, Integer, Void> {

	
	File image;
	Context context;
	SharedPreferences settings;
	boolean authRequired;

	public AsyncPhotoDownload(Context context, File image) {
		this.context = context;
		this.image = image;
		settings = context.getSharedPreferences(SH.VK_SETTINGS_SHARED,
				Context.MODE_PRIVATE);

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();

		if (!Assistentnetwork.IsOnline((Activity) context)) {
			Assistentnetwork.showNoConnectionDialog((Activity) context, false);
			this.cancel(true);
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			publishProgress(0);
			Random ran = new Random();
			int ranInt = ran.nextInt(100);

			AndroidHttpClient client = AndroidHttpClient
					.newInstance("httpClient_" + ranInt);
			// Stage 1. Receive server to upload
			String url = SH.VK_API_URL + "photos.getMessagesUploadServer?"
					+ "save_big=1\u0026access_token=";
			String token = settings.getString(SH.VK_SETTINGS_TOKEN, null);
			HttpGet get = new HttpGet(url + token);
			HttpResponse response = null;
			{
				int count = 0;
				do {
					try {
						response = client.execute(get);
						break;
					} catch (Exception e) {
						count++;
						if (count == 3)
							throw new Exception(
									"Error while executing photos.getMessagesUploadServer method");
					}
				} while (true);
			}
			publishProgress(1);
			HttpEntity entityHttp = response.getEntity();
			String s = EntityUtils.toString(entityHttp);
			if(s.contains("error")){
				publishProgress(555);
				authRequired=true;
				return null;
			}
			JSONObject parser = new JSONObject(s);
			String uploadUrl = parser.optJSONObject("response").getString(
					"upload_url");

			HttpPost post = new HttpPost(uploadUrl);

			// File f =File.createTempFile("photo"+ranInt,
			// ".jpg",context.getFilesDir());
			// ByteArrayOutputStream bos = new ByteArrayOutputStream();
			// boolean t1 = img.compress(CompressFormat.JPEG,100, bos);
			// byte[] bitmapdata = bos.toByteArray();
			// bos.close();
			// FileOutputStream fos = new FileOutputStream(f);
			// fos.write(bitmapdata);
			// fos.flush();

			MultipartEntity entityMulti = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			// File f = new File(imagePath.getPath());
			entityMulti.addPart("photo", new FileBody(image, "image/jpeg"));
			// entityMulti.addPart("photo",new FileBody(f, "image/jpeg"));
			post.setEntity(entityMulti);
			publishProgress(2);
			{
				int count = 0;
				do {
					try {
						response = client.execute(post, new BasicHttpContext());
						break;
					} catch (Exception e) {
						count++;
						e.printStackTrace();
						if (count == 3)
							throw new Exception("Error while uploading photo");
					}
				} while (true);
			}
			publishProgress(3);
			image.delete();// картинка больше не нужна. удаляем.
			s = EntityUtils.toString(response.getEntity());
			parser = new JSONObject(s);

			post = new HttpPost(SH.VK_API_URL + "photos.saveMessagesPhoto?");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);

			String photoSave = parser.getString("photo");
			String serverSave = String.valueOf(parser.getLong("server"));
			String hashSave = parser.getString("hash");

			nameValuePairs.add(new BasicNameValuePair("photo", photoSave));
			nameValuePairs.add(new BasicNameValuePair("server", serverSave));
			nameValuePairs.add(new BasicNameValuePair("hash", hashSave));
			nameValuePairs.add(new BasicNameValuePair("access_token", token));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			{
				int count = 0;
				do {
					try {
						response = client.execute(post);
						break;
					} catch (Exception e) {
						count++;
						if (count == 3)
							throw new Exception(
									"Error while executing photos.saveMessagesPhoto method");
					}
				} while (true);
			}

			s = EntityUtils.toString(response.getEntity());

			parser = new JSONObject(s);
			parser = parser.getJSONArray("response").getJSONObject(0);
			String attachement = parser.getString("id");

			SQLiteDatabase db = new DataBaseHelper(context)
					.getWritableDatabase();
			String temp = settings.getString(SH.VK_SETTINGS_FRIENDS_TO_SEND, null);
			String[] receivers = temp.split(",");

			
			Cursor c;
			for (int i = 0; i < receivers.length; i++) {
				c = db.query(SH.DB_TABLE_VK, new String[]{SH.DB_TABLE_VK_COLUMNS_VK_ID, SH.DB_TABLE_VK_COLUMNS_NAME},
						"name=\'" + receivers[i] + "\'", null, null, null, null);
				if (c.moveToFirst()) {
					int id_ind = c.getColumnIndex("vk_id");
					//resultIds += c.getString(id_ind).toString() + ",";
					nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("uid", c.getString(id_ind).toString()));
					nameValuePairs.add(new BasicNameValuePair("message",
							"Sent from EGEApp"));
					nameValuePairs.add(new BasicNameValuePair("title", "Photos"));
					nameValuePairs
							.add(new BasicNameValuePair("attachment", attachement));
					nameValuePairs.add(new BasicNameValuePair("access_token", token));
					post = new HttpPost(SH.VK_API_URL + "messages.send?");
					post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					{
						int count = 0;
						do {
							try {
								response = client.execute(post);
								Log.i(SH.LOGTAG,EntityUtils.toString(response.getEntity()));
								break;
							} catch (Exception e) {
								count++;
								if (count == 3)
									throw new Exception(
											"Error while executing messages.send method");
							}
						} while (true);
					}

				}
				c.close();
			}
			
			db.close();
			client.close();
			
			publishProgress(4);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		String s = null;
		switch (values[0]) {
			case 0 :
				s = "Соединение установлено";
				break;
			case 1 :
				s = "Сервисные данные отправлены";
				break;
			case 2 :
				s = "Загрузка фото начата";
				break;
			case 3 :
				s = "Загрузка фото закончена";
				break;
			case 4:
				s="Сообщение(я) отправлены";
				break;
			case 555:
				s="Ошибка! Повторите загрузку фото после авторизации!";
				break;
		}
		Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
	}
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(authRequired){
			Intent intent = new Intent(context,AuthVK.class);
			intent.putExtra("mode", "auth");
			context.startActivity(intent);
			
		}

	}

}
