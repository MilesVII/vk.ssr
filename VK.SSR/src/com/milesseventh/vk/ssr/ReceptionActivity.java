package com.milesseventh.vk.ssr;

import android.app.Activity;
import android.content.Intent;

public class ReceptionActivity extends Activity {
	public Intent loginIntent, mainIntent;
	@Override
	protected void onResume() {
		super.onResume();
		loginIntent = new Intent(this, LoginActivity.class);
		mainIntent = new Intent(this, MainActivity.class);
		
		String token = Utils.getPrefs(this, Utils.PREF_TOKEN);
		if (token.isEmpty())
			startActivityForResult(loginIntent, Utils.LOGIN_REQUEST_CODE);
		else
			startActivity(mainIntent);
	}
	
	@Override
	public void onActivityResult(int reqId, int resCode, Intent data){
		if(resCode != RESULT_CANCELED && reqId == Utils.LOGIN_REQUEST_CODE){
			Utils.setPrefs(this, Utils.PREF_TOKEN, data.getExtras().getString(MainActivity.EXTRA_TOKEN));
			startActivity(mainIntent);
		}
	}
}
