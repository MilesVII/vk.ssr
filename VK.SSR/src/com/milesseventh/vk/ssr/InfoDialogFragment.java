package com.milesseventh.vk.ssr;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

public class InfoDialogFragment extends DialogFragment {
	private String title = "", text = "";
	private boolean linkify = false;
	
	public void setData (String _title, String _text){
		title = _title;
		text = _text;
	}
	
	public void linkifyOn(){
		linkify = true;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		Builder _builder = new Builder(getActivity());
		_builder.setTitle(title).setNeutralButton(R.string.ui_close, null);
		if (linkify){
			TextView _tv = new TextView(getActivity());
			_tv.setText(text);
			_tv.setPadding(5, 5, 5, 5);
			_tv.setTextSize(16);
			Linkify.addLinks(_tv, Linkify.WEB_URLS);
			_builder.setView(_tv);
		} else {
			_builder.setMessage(text);
		}
		return _builder.create();
	}
}
