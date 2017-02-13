package com.milesseventh.vk.ssr;

import java.util.ArrayList;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

public class EditorDialogFragment extends DialogFragment {
	private RotationData in;
	private RotationManagerActivity ctxt;
	
	public void setData (RotationData _in, RotationManagerActivity _ctxt){
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
		if (in.rotation != null)
			_rotation.setText(merge(in.rotation));
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
					in.rotation = new String[_wrath.size()];
					in.rotation = _wrath.toArray(in.rotation);
					if (in.rotation.length == 0)
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
