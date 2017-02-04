package com.milesseventh.vk.ssr;

import android.content.Context;
import android.widget.LinearLayout;

public abstract class ListEntry extends LinearLayout {
	protected final float textSize = 16;
	private LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

	public ListEntry(Context _ctxt) {
		super(_ctxt);
		
		this.setLayoutParams(lp);
		setOrientation(LinearLayout.HORIZONTAL);
	}

}
