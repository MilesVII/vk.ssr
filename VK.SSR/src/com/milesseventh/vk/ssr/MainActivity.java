package com.milesseventh.vk.ssr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	public static final String SECURE_KEY = "d82CNGqXXVXrEcfOk6o1",
			EXTRA_TOKEN = "com.milesseventh.vk.ssr.code",
			EXTRA_DATA = "com.milesseventh.vk.ssr.data";
	public static final int APP_ID = 5823245;
	private String token;
	public static Activity ctxt; 
	public TextView field;
	private Button b_load, b_save, b_start;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ctxt = this;
		Utils.setContext(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		reloadToken();
		
		field = (TextView)findViewById(R.id.main_field);
		field.setText(loadData());
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
			public void onClick(View arg0) {
				startService();
			}
		});
	}
	
	@Override
	public void onActivityResult(int _reqId, int _resCode, Intent _data){
		if(_resCode != RESULT_CANCELED)
			if (_reqId == 77){
				//Utils.showInfoDialog(this, "Result", _data.getExtras().getString(EXTRA_TOKEN));
				Utils.setPrefs(this, Utils.PREF_TOKEN, _data.getExtras().getString(EXTRA_TOKEN));
				reloadToken();
		    }
	}
	
	private void reloadToken(){
		token = Utils.getPrefs(this, Utils.PREF_TOKEN);
		if (token == "")
			this.startActivityForResult(new Intent(this, LoginActivity.class), 77);
	}

	public String loadData(){
		return Utils.getPrefs(this, Utils.PREF_DATA);
	}

	public void saveData(){
		Utils.setPrefs(this, Utils.PREF_DATA, field.getText().toString());
	}
	
	public void startService(){
		String _data = loadData();
		if (!_data.equalsIgnoreCase(field.getText().toString())){
			Utils.showInfoDialog(this, getString(R.string.ui_error), getString(R.string.message_unsaved));
			return;
		}
		if (!_data.contains("\n") || _data.trim().isEmpty()){
			Utils.showInfoDialog(this, getString(R.string.ui_error), getString(R.string.message_nolines));
			return;
		}
		
		Intent _hey = new Intent(this, RotationService.class);
		_hey.putExtra(EXTRA_DATA, _data);
		startService(_hey);
	}
}