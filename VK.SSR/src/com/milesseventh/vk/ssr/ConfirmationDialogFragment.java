package com.milesseventh.vk.ssr;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ConfirmationDialogFragment extends DialogFragment {
	private String text = "";
	private DialogInterface.OnClickListener act;
	
	public void setData (String _text, DialogInterface.OnClickListener _act){
		text = _text;
		act = _act;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		Builder _builder = new Builder(getActivity());
		_builder.setTitle(R.string.conf_title).setMessage(text).setNegativeButton(R.string.conf_cancel, null).setPositiveButton(R.string.conf_proceed, act);
		return _builder.create();
	}
}
