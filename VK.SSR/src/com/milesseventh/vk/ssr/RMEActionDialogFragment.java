package com.milesseventh.vk.ssr;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class RMEActionDialogFragment extends DialogFragment {
	private LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	private RotationData heart;
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
	
	public void setData(RotationData _heart, RotationManagerActivity _host){
		heart = _heart;
		host = _host;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		Builder _builder = new Builder(getActivity());

		LinearLayout _li = new LinearLayout(getActivity());
		_li.setOrientation(LinearLayout.VERTICAL);

		Button _edit = new Button(getActivity());
		_edit.setBackgroundResource(R.drawable.button_custom);
		_edit.setLayoutParams(lp);
		_edit.setText(R.string.ui_edit);
		_edit.setOnClickListener(cl_edit);
		_li.addView(_edit);
		
		Button _delete = new Button(getActivity());
		_delete.setBackgroundResource(R.drawable.button_custom);
		_delete.setLayoutParams(lp);
		_delete.setText(R.string.ui_delete);
		_delete.setOnClickListener(cl_delete);
		_li.addView(_delete);
		
		_builder.setView(_li).setTitle(heart.name).setNeutralButton(getString(R.string.ui_close), null);

		return _builder.create();
	}
}
