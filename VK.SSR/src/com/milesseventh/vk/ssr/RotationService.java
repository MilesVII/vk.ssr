package com.milesseventh.vk.ssr;

import java.util.Timer;
import java.util.TimerTask;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

public class RotationService extends IntentService {
	public RotationService() {
		super("RotationService");
	}
	int currentLine = 0;
	String[] rotation;
	String token;
	Timer rotator;
	
	@Override
    protected void onHandleIntent(Intent _int) {
        Bundle _instructions = _int.getExtras();
        token = Utils.getPrefs(this, Utils.PREF_TOKEN);
        rotation = _instructions.getString(MainActivity.EXTRA_DATA).split("\n");
		rotator = new Timer();
		rotator.schedule(new TimerTask(){
			@Override
			public void run() {
				while (rotation[currentLine].trim().isEmpty())
					currentLine = cycledInc(currentLine, rotation.length);
				Utils.shout(Utils.setStatus(rotation[currentLine], token));
				//Utils.ctxt.field.setText(Utils.setStatus(rotation[currentLine], token));;
				currentLine = cycledInc(currentLine, rotation.length);
			}
		}, 0, 1000 * 100);
    }
	
	public int cycledInc(int _in, int _max){
		_in++;
		if (_in>=_max)
			_in = 0;
		return _in;
	}
}