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
	}
}
