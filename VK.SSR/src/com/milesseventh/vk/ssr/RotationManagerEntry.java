package com.milesseventh.vk.ssr;

import android.view.View;
import android.widget.Button;

public class RotationManagerEntry extends Button {
	public RotationManagerEntry(final RotationManagerActivity _ctxt, final RotationData _heart) {
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
		/*
		//Entry caption
		TextView _tx = new TextView(_ctxt);
		_tx.setText(heart.name);
		_tx.setTextSize(textSize);
		
		Button _be = new Button(_ctxt);
		_be.setText(R.string.ui_edit);
		_be.setOnClickListener(cl_edit);
		_be.setBackgroundResource(R.drawable.button_custom);
		_be.setMinimumWidth(MIN_EX_HEIGHT);
		
		Button _bx = new Button(_ctxt);
		_bx.setText(R.string.ui_delete);
		_bx.setOnClickListener(cl_delete);
		_bx.setBackgroundResource(R.drawable.button_custom);
		_bx.setMinimumWidth(MIN_EX_HEIGHT);*/

		//addView(_be);
		//addView(_bx);
		//addView(_tx);
	}

}
