package com.milesseventh.vk.ssr;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TaskEntry extends ListEntry {
	private int target;
	private Task mytask;
	private Button picker;
	private MainActivity ctxt;
	
	@SuppressWarnings("static-access")
	public TaskEntry(final MainActivity _ctxt, int _target, String _name) {
		super(_ctxt);
		target = _target;
		ctxt = _ctxt;
		
		TextView _tx = new TextView(_ctxt);
		_tx.setText(_name);
		_tx.setTextSize(textSize);

		mytask = _ctxt.rotator.isItIn(_target);
		
		picker = new Button(_ctxt);
		refreshCaption();
		picker.setBackgroundResource(R.drawable.button_custom);
		final TaskEntry _me = this;
		picker.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				RDPickerDialogFragment _picker = new RDPickerDialogFragment();
				_picker.setHost(_me);
				_picker.show(_ctxt.getFragmentManager(), "...");
			}
		});

		addView(picker);
		addView(_tx);
	}
	
	@SuppressWarnings("static-access")
	public void setState(RotationData _new){
		//Kill previous timer;
		if (mytask != null)
			ctxt.rotator.removeTask(mytask);
		
		//And schedule a new one
		if (_new != null)
			mytask = ctxt.rotator.addTask(_new, target);
		else
			mytask = null;
		refreshCaption();
	}
	
	private void refreshCaption(){
		picker.setText((mytask == null)?ctxt.getString(R.string.ui_off):mytask.getName());
	}
}
