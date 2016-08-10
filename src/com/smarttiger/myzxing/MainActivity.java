package com.smarttiger.myzxing;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class MainActivity extends Activity {


	private ClipboardManager myClipboard;//剪贴板
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Intent intent;
		myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
		if(myClipboard.hasPrimaryClip() && !TextUtils.isEmpty(myClipboard.getText()))
			intent = new Intent(this, BuildView.class);
		else
			intent = new Intent(this, ScanningView.class);
		startActivity(intent);
		
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		finish();
	}

}
