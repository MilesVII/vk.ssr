package com.milesseventh.vk.ssr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class RotationManagerActivity extends Activity {
	public RotationData importing;
	
	private Button b_add;
	private EditText field_add;
	private LinearLayout list;
	private OnClickListener cl_add = new OnClickListener(){
		@Override
		public void onClick(View _stallionfucksstallion) {
			String _hellraiser = field_add.getText().toString().trim();
			field_add.setText("");
			if (!_hellraiser.isEmpty()){
				RotationData _t = new RotationData();
				_t.name = _hellraiser;
				Utils.data.add(_t);
				syncList();
			}
		}
	};
	
	@Override
	public void onBackPressed(){
		Utils.saveDataToFile(Utils.data);
		finish();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rotation_manager);
		
		field_add = (EditText)findViewById(R.id.field_add);
		list = (LinearLayout)findViewById(R.id.rotations_list);
		b_add = (Button)findViewById(R.id.b_add);
		b_add.setOnClickListener(cl_add);
		
		syncList();
	}

	public void syncList(){
		list.removeAllViews();
		for (RotationData _runner: Utils.data)
			list.addView(new RotationManagerEntry(this, _runner));
	}

	@Override
	public void onActivityResult(int _reqId, int _resCode, Intent _data){
		if(_resCode != Activity.RESULT_CANCELED)
			if (_reqId == Utils.IMPORT_REQUEST_CODE)
				if (importing != null){
					try {
						ArrayList<String> _collector = new ArrayList<String>();
						if (importing.rotation != null)
							for (String _runhorseyrun: importing.rotation)
								_collector.add(_runhorseyrun);
						
						String _runhorseyrun;
						BufferedReader _in = new BufferedReader(new FileReader(_data.getData().getPath()));
						while((_runhorseyrun = _in.readLine()) != null)
							if (!_runhorseyrun.trim().isEmpty())
								_collector.add(_runhorseyrun);
						_in.close();
						importing.rotation = new String[_collector.size()];
						importing.rotation = _collector.toArray(importing.rotation);
						Utils.shout("Imported successfully");
					} catch (Exception e) {
						e.printStackTrace();
						Utils.shout("An error occured");
					}
				}
	}
}
