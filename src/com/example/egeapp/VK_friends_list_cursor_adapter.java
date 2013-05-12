package com.example.egeapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class VK_friends_list_cursor_adapter extends ResourceCursorAdapter {

	
	
	
	String[] FriendsToSend;
	Context _context;
	Cursor _c;
	int blackColor;
	int greenColor;
	int imageNameInd;
	int userNameInd;
	int statusInd;
	String path;


	public VK_friends_list_cursor_adapter(Context context, int layout,
			Cursor c, boolean autoRequery) {
		super(context, layout, c, autoRequery);
		_context = context;		
		greenColor = context.getResources().getColor(R.color.caption);
		blackColor = context.getResources().getColor(android.R.color.black);
		UpdateFriendsToSend(true);
		path =context.getFilesDir().getAbsolutePath();
		
		
	}
	
	// выполняются отдельно, когда будет присвоен непустой курсор
	public void SetFieldsIndsFromCursor()
	{
		_c = getCursor();
		imageNameInd = _c.getColumnIndex("image_name");
		userNameInd = _c.getColumnIndex("name");		
		statusInd = _c.getColumnIndex("status");
	}
	
	
	@SuppressWarnings("deprecation")
	public void UpdateFriendsToSend(boolean missRequery)
	{
		FriendsToSend = _context.getSharedPreferences(SH.VK_SETTINGS_SHARED, Context.MODE_PRIVATE)
				.getString(SH.VK_SETTINGS_FRIENDS_TO_SEND, "").split(",");
		if(!missRequery)
		this.getCursor().requery();
	}



	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
		// TODO Auto-generated method stub
		Cursor c = arg2;
		
		
		String imageNameString = c.getString(imageNameInd);
		
		if (imageNameString.equals("no_photo"))
			((ImageView)arg0.findViewById(R.id.image_vk_user_avatarka)).setImageResource(R.drawable.camera_b);
		else
		((ImageView)arg0.findViewById(R.id.image_vk_user_avatarka)).setImageURI(Uri.parse(path+"/"+c.getString(imageNameInd)));
		//setImageBitmap(BitmapFactory.decodeFile(path+"/"+c.getString(imageName)));
		

		String name = c.getString(userNameInd);
		
		TextView tw =((TextView)arg0.findViewById(R.id.text_vk_user_name));
		tw.setTextColor(blackColor);
		tw.setText(name);	
		// bold names, if selected		
		// количество итераций равно количеству выбранных друзей. То есть, пара,тройка проходов обычно.
		for (int i = 0; i < FriendsToSend.length; i++) {
			if(name.equals(FriendsToSend[i])){
				tw.setTextColor(greenColor);
				break;
			}
		}					
		if (c.getString(statusInd).equals("1"))
			((ImageView)arg0.findViewById(R.id.image_vk_user_status)).setImageResource(R.drawable.vk_online);
		else
			((ImageView)arg0.findViewById(R.id.image_vk_user_status)).setImageResource(R.drawable.vk_ofline);
	}

}
