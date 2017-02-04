package com.milesseventh.vk.ssr;

import java.util.Timer;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TaskEntry extends ListEntry {
	private int target;
	private RotationData state = null;
	private Timer mytask = null;
	private Button picker;
	private Context ctxt;
	
	public TaskEntry(final Activity _ctxt, int _target, String _name) {
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
	
	public void setState(RotationData _new){
		state = _new;
		//Kill previous timer;
		if (mytask != null){
			mytask.cancel();
			MainActivity.rotator.decreaseCounter();
		}
		//And schedule a new one
		if (state != null)
			mytask = MainActivity.rotator.addTask(new Task(state, target));
		refreshCaption();
	}
	
	private void refreshCaption(){
		picker.setText((state == null)?ctxt.getString(R.string.ui_off):state.name);
	}
}
