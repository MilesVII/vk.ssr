package com.milesseventh.vk.ssr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class RotationManagerActivity extends Activity {
	public class RMEActionDialogFragment extends DialogFragment {
		private LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		private Rotation heart;
		private RotationManagerActivity host;
		
		private OnClickListener cl_delete = new OnClickListener(){
			@Override
			public void onClick(View _host) {
				Utils.data.remove(heart);
				host.syncList();
				getDialog().dismiss();
			}
		};
		private OnClickListener cl_edit = new OnClickListener(){
			@Override
			public void onClick(View _host) {
				EditorDialogFragment _x = new EditorDialogFragment();
				_x.setData(heart, host);
				_x.show(host.getFragmentManager(), "...");
				getDialog().dismiss();
			}
		};
		private OnClickListener cl_import = new OnClickListener(){
			@Override
			public void onClick(View _host) {
				Intent _i = new Intent(Intent.ACTION_GET_CONTENT);
				_i.setType("file/*");
				host.startActivityForResult(_i, Utils.IMPORT_REQUEST_CODE);
				host.importing = heart;
				getDialog().dismiss();
			}
		};
		
		public void setData(Rotation _heart, RotationManagerActivity _host){
			heart = _heart;
			host = _host;
		}
		
		public View generateButton(int title, OnClickListener ocl){
			Button b = new Button(getActivity());
			b.setBackgroundResource(R.drawable.button_custom);
			b.setLayoutParams(lp);
			b.setText(title);
			b.setOnClickListener(ocl);
			return b;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState){
			Builder builder = new Builder(getActivity());

			LinearLayout li = new LinearLayout(getActivity());
			li.setOrientation(LinearLayout.VERTICAL);

			li.addView(generateButton(R.string.ui_edit, cl_edit));
			li.addView(generateButton(R.string.ui_import, cl_import));
			li.addView(generateButton(R.string.ui_delete, cl_delete));
			
			builder.setView(li).setTitle(heart.name).setNeutralButton(getString(R.string.ui_close), null);

			return builder.create();
		}
	}
	public class RotationManagerEntry extends Button {
		public RotationManagerEntry(final RotationManagerActivity _ctxt, final Rotation _heart) {
			super(_ctxt);	
			setText(_heart.name);
			setBackgroundResource(R.drawable.button_custom);
			setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					RMEActionDialogFragment _bigHorsey = new RMEActionDialogFragment();
					_bigHorsey.setData(_heart, _ctxt);
					_bigHorsey.show(_ctxt.getFragmentManager(), "...");
				}
			});
		}
	}
	public class EditorDialogFragment extends DialogFragment {
		private Rotation in;
		private RotationManagerActivity ctxt;
		
		public void setData (Rotation _in, RotationManagerActivity _ctxt){
			in = _in;
			ctxt = _ctxt;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState){
			Builder _builder = new Builder(ctxt);

			LinearLayout _li = new LinearLayout(ctxt);
			_li.setOrientation(LinearLayout.VERTICAL);
			final EditText _name = new EditText(ctxt);
			_name.setText(in.name);
			final EditText _period = new EditText(ctxt);
			_period.setText("" + in.period);
			_period.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
			final EditText _rotation = new EditText(ctxt);
			if (in.text != null)
				_rotation.setText(merge(in.text));
			_rotation.setInputType(EditorInfo.TYPE_CLASS_TEXT + EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
			
			_li.addView(_name);
			_li.addView(_period);
			_li.addView(_rotation);
			
			_builder.setTitle(R.string.ui_rotation_editor).setView(_li).setNegativeButton(R.string.conf_cancel, null).setPositiveButton(R.string.ui_save, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					boolean _horror = true;
					//Name safety check
					if (!_name.getText().toString().isEmpty())
						in.name = _name.getText().toString();
					else 
						_horror = false;
					//Period safety check
					if (!_period.getText().toString().isEmpty()){
						int _hatred = Integer.parseInt(_period.getText().toString());
						if (_hatred < Utils.MIN_PERIOD){
							Utils.showInfoDialog(ctxt, getString(R.string.ui_error), getString(R.string.message_period));
							_hatred = Utils.MIN_PERIOD;
						}
						in.period = _hatred;
					} else 
						_horror = false;
					//Rotation data safety check
					if (!_rotation.getText().toString().isEmpty()){
						ArrayList<String> _wrath = new ArrayList<String>();
						for (String _cummy: _rotation.getText().toString().split("\n"))
							if (!_cummy.trim().isEmpty())
								_wrath.add(_cummy);
						in.text = new String[_wrath.size()];
						in.text = _wrath.toArray(in.text);
						if (in.text.length == 0)
							_horror = false;
					} else 
						_horror = false;
					
					in.isvalid = _horror;
					ctxt.syncList();
				}
			});
			return _builder.create();
		}
		
		private String merge(String[] _in){
			String _pleasure = "";
			for (String _undertail: _in)
				_pleasure += _undertail + "\n";
			return _pleasure.substring(0, _pleasure.length() - 1);
		}
	}

	public Rotation importing;
	
	private Button b_add;
	private EditText field_add;
	private LinearLayout list;
	private OnClickListener cl_add = new OnClickListener(){
		@Override
		public void onClick(View _stallionfucksstallion) {
			String _hellraiser = field_add.getText().toString().trim();
			field_add.setText("");
			if (!_hellraiser.isEmpty()){
				Rotation _t = new Rotation();
				_t.name = _hellraiser;
				Utils.data.add(_t);
				syncList();
			}
		}
	};
	
	@Override
	public void onBackPressed(){
		Utils.saveDataToFile(Utils.data);
		super.onBackPressed();
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
		for (Rotation _runner: Utils.data)
			list.addView(new RotationManagerEntry(this, _runner));
	}

	@Override
	public void onActivityResult(int _reqId, int _resCode, Intent _data){
		if(_resCode != Activity.RESULT_CANCELED)
			if (_reqId == Utils.IMPORT_REQUEST_CODE)
				if (importing != null){
					try {
						ArrayList<String> _collector = new ArrayList<String>();
						if (importing.text != null)
							for (String _runhorseyrun: importing.text)
								_collector.add(_runhorseyrun);
						
						String _runhorseyrun;
						BufferedReader _in = new BufferedReader(new FileReader(_data.getData().getPath()));
						while((_runhorseyrun = _in.readLine()) != null)
							if (!_runhorseyrun.trim().isEmpty())
								_collector.add(_runhorseyrun);
						_in.close();
						importing.text = new String[_collector.size()];
						importing.text = _collector.toArray(importing.text);
						Utils.shout("Imported successfully");
					} catch (Exception e) {
						e.printStackTrace();
						Utils.shout("An error occured");
					}
				}
	}
}
