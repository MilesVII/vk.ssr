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

public class RDPickerDialogFragment extends DialogFragment {
	private TaskEntry host;
	//private final Activity _ctxt = MainActivity.getInstance();
	private LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	
	public void setHost (TaskEntry _te){
		host = _te;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		Builder _builder = new Builder(getActivity());

		LinearLayout _li = new LinearLayout(getActivity());
		_li.setOrientation(LinearLayout.VERTICAL);

		_li.addView(generateEntry(null));
		for (RotationData _runhorseyrun: Utils.data)
			if (_runhorseyrun.isvalid)
				_li.addView(generateEntry(_runhorseyrun));
		
		_builder.setView(_li).setTitle(getString(R.string.ui_rotation_menu_title)).setNeutralButton(getString(R.string.ui_close), null);

		return _builder.create();
	}
	
	private Button generateEntry(final RotationData _in){
		Button _b = new Button(getActivity());
		_b.setBackgroundResource(R.drawable.button_custom);
		_b.setLayoutParams(lp);
		_b.setText((_in == null?getString(R.string.ui_off):_in.name));
		_b.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View no){
				host.setState(_in);
				getDialog().dismiss();
			}
		});
		return _b;
	}
}
