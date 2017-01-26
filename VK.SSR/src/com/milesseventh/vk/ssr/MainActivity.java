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
import android.widget.TextView;

public class MainActivity extends Activity {
	public static final String SECURE_KEY = "",
			EXTRA_TOKEN = "com.milesseventh.vk.ssr.token",
			EXTRA_DATA = "com.milesseventh.vk.ssr.data",
			EXTRA_PERIOD = "com.milesseventh.vk.ssr.period";
	public static final int APP_ID = 5823245, MIN_PERIOD = 120, LOGIN_REQUEST_CODE = 777;
	private String token;
	public static Activity ctxt; 
	public TextView field, period_field;
	private Button b_load, b_save, b_start;
	public static RotationService rotator = null;
	private Intent rotInt; 
	private DialogInterface.OnClickListener cl_logout = new DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			if (rotator != null)
				stopService();
			//
			Utils.setPrefs(ctxt, Utils.PREF_TOKEN, "");
			reloadToken();
		}
	};
	private DialogInterface.OnClickListener cl_close = new DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			if (rotator != null)
				stopService();
			finish();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ctxt = this;
		Utils.setContext(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		reloadToken();
		
		field = (TextView)findViewById(R.id.main_field);
		field.setText(loadData());
		period_field = (TextView)findViewById(R.id.period_field);
		period_field.setText(loadPeriod());
		b_load = (Button)findViewById(R.id.b_load);
		b_save = (Button)findViewById(R.id.b_submit);
		b_start = (Button)findViewById(R.id.b_onoff);

		b_load.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				field.setText(loadData());
			}
		});
		b_save.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				saveData();
			}
		});
		b_start.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View _me) {
				if (rotator == null){
					if (startService())
						((Button)_me).setText(R.string.ui_stop);
				} else {
					stopService();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem _i){
		switch(_i.getItemId()){
		case R.id.m_logout:
			Utils.requestConfirmation(this, getString(R.string.conf_logout), cl_logout);
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
			if (_reqId == LOGIN_REQUEST_CODE){
				Utils.setPrefs(this, Utils.PREF_TOKEN, _data.getExtras().getString(EXTRA_TOKEN));
				reloadToken();
			}
	}
	
	private void reloadToken(){
		token = Utils.getPrefs(this, Utils.PREF_TOKEN);
		if (token == "")
			startActivityForResult(new Intent(this, LoginActivity.class), LOGIN_REQUEST_CODE);
	}

	public String loadData(){
		return Utils.getPrefs(this, Utils.PREF_DATA);
	}

	public String loadPeriod(){
		return Utils.getPrefs(this, Utils.PREF_PERIOD);
	}

	public void saveData(){
		Utils.setPrefs(this, Utils.PREF_DATA, field.getText().toString());
		Utils.setPrefs(this, Utils.PREF_PERIOD, period_field.getText().toString());
	}
	
	public boolean startService(){
		String _data = loadData();
		int _period;
		try {
			_period = Integer.parseInt(loadPeriod());
		} catch(Exception _ex) {
			Utils.showInfoDialog(this, getString(R.string.ui_error), getString(R.string.message_parse));
			return false;
		}
		if (!_data.equalsIgnoreCase(field.getText().toString()) || 
			!Integer.toString(_period).equalsIgnoreCase(period_field.getText().toString())){
			Utils.showInfoDialog(this, getString(R.string.ui_error), getString(R.string.message_unsaved));
			return false;
		}
		if (!_data.contains("\n") || _data.trim().isEmpty()){
			Utils.showInfoDialog(this, getString(R.string.ui_error), getString(R.string.message_nolines));
			return false;
		}
		if (_period < MIN_PERIOD){
			Utils.showInfoDialog(this, getString(R.string.ui_error), getString(R.string.message_period));
			return false;
		}
		
		rotInt = new Intent(this, RotationService.class);
		rotInt.putExtra(EXTRA_DATA, _data);
		rotInt.putExtra(EXTRA_PERIOD, _period);
		startService(rotInt);
		return true;
	}
	
	public void stopService(){
		b_start.setText(R.string.ui_start);
		ctxt.stopService(rotInt);
		
		rotator.shutup();
		rotator = null;
	}
}