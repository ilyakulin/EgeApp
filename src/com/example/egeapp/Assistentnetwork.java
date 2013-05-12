package com.example.egeapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

public class Assistentnetwork {

	public static void showNoConnectionDialog(Context ctx1, boolean closeActivity) {
		final Context ctx = ctx1;
		final boolean _exit = closeActivity;
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setCancelable(false);
		builder.setMessage("Нет соединения с интернетом");
		builder.setTitle("Ошибка");
		builder.setPositiveButton("Настройки сети",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						ctx.startActivity(new Intent(
								Settings.ACTION_WIRELESS_SETTINGS));
						if (_exit && ctx instanceof Activity) 
							((Activity) ctx).finish();
					}
				});
			builder.setNegativeButton("Отстань!",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (_exit && ctx instanceof Activity) 
								((Activity) ctx).finish();							
							return;
						}
					});

		
		builder.show();

	}
	public static boolean IsOnline(Activity activity) {

		ConnectivityManager cm = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();

		if (netInfo == null)
			return false;
		else
			return netInfo.isConnected();
	}

}
