package com.milesseventh.vk.ssr;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TaskEntry extends ListEntry {
	public final int target;
	private Button picker;
	private MainActivity ctxt;
	public String rotationTitle;
	
	public TaskEntry(final MainActivity _ctxt, int _target, String _name) {
		super(_ctxt);
		target = _target;
		ctxt = _ctxt;
		
		TextView _tx = new TextView(_ctxt);
		_tx.setText(_name);
		_tx.setTextSize(textSize);
		
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
	
	public void setRotation(Rotation _new){
		if (_new != null){
			if (MainActivity.ctxt.msgAddTask(_new, target))
				rotationTitle = _new.name;
		}else{
			if (MainActivity.ctxt.msgRemoveTask(target));
				rotationTitle = null;
		}
		refreshCaption();
	}
	
	public boolean isActive(){
		return rotationTitle != null;
	}
	
	public void refreshCaption(){
		picker.setText((rotationTitle == null)?ctxt.getString(R.string.ui_off):rotationTitle);
	}
}
