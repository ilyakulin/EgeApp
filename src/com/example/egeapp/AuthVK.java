package com.example.egeapp;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class AuthVK extends Activity {

	WebView browser;
	SharedPreferences settings;
	Activity currentActivity = this;
	boolean navigateAgain;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth_vk);

		browser = (WebView) findViewById(R.id.web_vk_auth);

		settings = getSharedPreferences("VK_settings", Context.MODE_PRIVATE);

		// TODO check network availability

		// get token
		browser.setWebViewClient(new WebViewClient() {

			int logout_count = 0;
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {

				if (!url.contains("oauth.vk.com/authorize")
						&& url.contains(SH.AUTH_CALLBACK_URL)) {

					if (!url.contains("error")) {
						String[] dataAuth = url.substring(
								url.indexOf("\u0023") + 1).split("\u0026");
						if (dataAuth.length == 3) {
							Editor editor = settings.edit();
							editor.putString("vk_token", dataAuth[0]
									.substring(dataAuth[0].indexOf("=") + 1));
							editor.putLong("vk_expires_in", Long
									.valueOf(dataAuth[1].substring(dataAuth[1]
											.indexOf("=") + 1)));
							editor.putLong("vk_request_time",
									System.currentTimeMillis());
							editor.putString("vk_user_id", dataAuth[2]
									.substring(dataAuth[2].indexOf("=") + 1));
							editor.commit();
							setResult(RESULT_OK);
							AuthVK.this.tokenDone(true);

						}
					} else {

						Editor editor = settings.edit();
						editor.putString("vk_token", "");
						editor.putLong("vk_expires_in", 0L);
						editor.putLong("vk_request_time", 0L);
						editor.putString("vk_user_id", "");
						editor.commit();
						setResult(RESULT_CANCELED);
						AuthVK.this.tokenDone(false);
					}

				} else if (url.contains("?act=logout")) {
					logout_count++;
					if (logout_count == 2) {
						Editor editor = settings.edit();
						editor.putString("vk_token", "");
						editor.putLong("vk_expires_in", 0L);
						editor.putLong("vk_request_time", 0L);
						editor.putString("vk_user_id", "");
						editor.putString(SH.VK_SETTINGS_FRIENDS_TO_SEND,"");
						editor.commit();
						setResult(RESULT_OK);

						// Два потока. Один удаляет фотки предыдущего
						// пользователя. Второй чистит БД.
						// Соединение к БД создается в этом активти, потому что
						// когда удалятся фотки
						// это активити уже скорей всего будет мертво, и второй
						// поток не сможет воспользоваться
						// его контекстом чтобы создать подключение к БД.
						final SQLiteDatabase db = new DataBaseHelper(
								currentActivity).getWritableDatabase();
						final String path = getFilesDir().getAbsolutePath()
								+ "/";

						final Thread t1 = new Thread(new Runnable() {

							@Override
							public void run() {
								try {
									db.beginTransaction();
									db.delete(SH.DB_TABLE_VK, null, null);
									ContentValues cv = new ContentValues();
									cv.put("_id", 0);
									cv.put("vk_id", "3214");
									db.insert(SH.DB_TABLE_VK, null, cv);																		
									db.delete(SH.DB_TABLE_VK, null, null);
									db.setTransactionSuccessful();
								} finally {
									db.endTransaction();
								}

							}
						});

						Thread t = new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try{
									db.beginTransaction();
									Cursor c = db
											.query(SH.DB_TABLE_VK,
													new String[]{SH.DB_TABLE_VK_COLUMNS_IMAGE_NAME},
													SH.DB_TABLE_VK_COLUMNS_IMAGE_NAME
															+ "!=\'no_photo\'",
													null, null, null, null);
									File f;
									if (c.moveToFirst()) {
										int indColumn = c
												.getColumnIndex(SH.DB_TABLE_VK_COLUMNS_IMAGE_NAME);
										do {
											f = new File(path
													+ c.getString(indColumn));
											f.delete();											

										} while (c.moveToNext());
									}
									db.setTransactionSuccessful();
								}
								finally
								{
									db.endTransaction();
								}
								
								t1.run();
							}
						});

						t.run();
						currentActivity.finish();
					}
				}

				super.onPageStarted(view, url, favicon);
			}
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {

				if (!Assistentnetwork.IsOnline(currentActivity))
					Assistentnetwork.showNoConnectionDialog(currentActivity,
							true);

				super.onReceivedError(view, errorCode, description, failingUrl);
			}
		});

		browser.getSettings().setJavaScriptEnabled(true);

		Intent outerIntent = getIntent();
		String mode = outerIntent.getExtras().getString("mode");
		if (mode.equals("auth"))
			browser.loadUrl(getResources().getString(R.string.auth_adr));
		if (mode.equals("switch_user")) {
			browser.loadUrl("http:\\\\m.vk.com");
			Toast.makeText(this, "Выйдите вручную", Toast.LENGTH_LONG).show();
		}

	}

	void tokenDone(boolean success) {
		if (success)
			Toast.makeText(this, "Авторизация успешно пройдена!",
					Toast.LENGTH_LONG).show();
		else
			Toast.makeText(this, "ПечальБеда в авторизации отказано :(",
					Toast.LENGTH_LONG).show();
		finish();
	}

}
