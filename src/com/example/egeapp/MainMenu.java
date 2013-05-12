package com.example.egeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainMenu extends Activity implements OnClickListener {
	



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// deleteDatabase("EgeAppDB");
		// finish();

		setContentView(R.layout.activity_main_menu);
		findViewById(R.id.button_send_photo_vk).setOnClickListener(this);
		findViewById(R.id.button_wolfram_input).setOnClickListener(this);						

	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {

			case R.id.button_send_photo_vk : {

				startActivity(new Intent(this, VKSendPhotos.class));								
				break;
			}
			case R.id.button_wolfram_input : {
				startActivity(new Intent(this, WolframInput.class));
				break;

			}

		}
	}

}
