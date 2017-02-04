package com.milesseventh.vk.ssr;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	public static final String 
			EXTRA_TOKEN = "com.milesseventh.vk.ssr.token";
	private String token;
	public static Activity ctxt; 
	//public TextView field, period_field;
	private LinearLayout taskList;
	private Button b_manage;//b_load, b_save, b_start;
	public static RotationService rotator = null;
	//private Intent rotInt; 
	private DialogInterface.OnClickListener cl_reload = new DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			//Shutdown every rotation
			stopRotations();
			//Clear list
			taskList.removeAllViews();
			//And reload
			loadTargets();
		}
	};
	private DialogInterface.OnClickListener cl_logout = new DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			stopRotations();
			Utils.setPrefs(ctxt, Utils.PREF_TOKEN, "");
			reloadToken();
		}
	};
	private DialogInterface.OnClickListener cl_close = new DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			stopRotations();
			finish();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ctxt = this;
		Utils.setContext(this);
		Utils.initDataContainer();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		reloadToken();

		b_manage = (Button)findViewById(R.id.b_manage);
		b_manage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ctxt, RotationManagerActivity.class));
			}
		});
		
		taskList = (LinearLayout) findViewById(R.id.task_list);
		loadTargets();
		startService(new Intent(this, RotationService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem _i){
		switch(_i.getItemId()){
		case R.id.m_reload:
			Utils.requestConfirmation(this, getString(R.string.conf_stop), cl_reload);
			return true;
		case R.id.m_logout:
			Utils.requestConfirmation(this, getString(R.string.conf_stop), cl_logout);
			return true;
		case R.id.m_close:
			Utils.requestConfirmation(this, getString(R.string.conf_close), cl_close);
			return true;
		}
		return super.onOptionsItemSelected(_i);
	}
	
	@Override
	public void onActivityResult(int _reqId, int _resCode, Intent _data){
		if(_resCode != RESULT_CANCELED)
			if (_reqId == Utils.LOGIN_REQUEST_CODE){
				Utils.setPrefs(this, Utils.PREF_TOKEN, _data.getExtras().getString(EXTRA_TOKEN));
				reloadToken();
			}
	}
	
	private void loadTargets(){
		taskList.addView(new TaskEntry(this, Utils.TARGET_USER, getString(R.string.ui_user)));
		Thread _loader = new Thread(new Runnable(){
			@Override
			public void run() {
				if (Utils.getGroupsList(token).isEmpty()){
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							for (VKGroup _lickingcroups: Utils.groups)
								taskList.addView(new TaskEntry(ctxt, _lickingcroups.gid, _lickingcroups.name));
						}
					});
				}
			}
		});
		_loader.start();
	}
	
	private void reloadToken(){
		token = Utils.getPrefs(this, Utils.PREF_TOKEN);
		if (token == "")
			startActivityForResult(new Intent(this, LoginActivity.class), Utils.LOGIN_REQUEST_CODE);
	}
	
	private void stopRotations(){
		for (int _fur = 0; _fur < taskList.getChildCount(); _fur++)
			((TaskEntry)taskList.getChildAt(_fur)).setState(null);
	}
}