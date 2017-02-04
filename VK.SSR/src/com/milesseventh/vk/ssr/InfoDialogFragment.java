package com.milesseventh.vk.ssr;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class InfoDialogFragment extends DialogFragment {
	private String title = "", text = "";
	
	public void setData (String _title, String _text){
		title = _title;
		text = _text;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		Builder _builder = new Builder(getActivity());
		_builder.setTitle(title).setMessage(text).setNeutralButton(R.string.ui_close, null);
		return _builder.create();
	}
}
