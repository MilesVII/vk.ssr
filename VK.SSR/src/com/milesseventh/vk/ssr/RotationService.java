package com.milesseventh.vk.ssr;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class RotationService extends Service {
	public static final String ID = "RotationService";
	int currentLine = 0;
	String[] rotation;
	String token;
	Timer rotator;

	private NotificationManager NM;
	private NotificationCompat.Builder smallHorsey;
	private int whatIsYourNameHorsey;
	
	public void initShouting(){
		smallHorsey = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)//(R.drawable.not_icon)
				.setContentTitle(getString(R.string.app_name))
				.setContentIntent(PendingIntent.getActivity(this, 0, 
															new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP),
															PendingIntent.FLAG_UPDATE_CURRENT));
	}
	
	public RotationService() {
		super();
		MainActivity.rotator = this;
	}
	
	@Override
	public int onStartCommand(Intent _int, int _flags, int _startId){
		Bundle _instructions = _int.getExtras();
		token = Utils.getPrefs(this, Utils.PREF_TOKEN);
		rotation = _instructions.getString(MainActivity.EXTRA_DATA).split("\n");
		rotator = new Timer();
		rotator.schedule(new TimerTask(){
			@Override
			public void run() {
				while (rotation[currentLine].trim().isEmpty())
					currentLine = cycledInc(currentLine, rotation.length);
				String _resp = Utils.isOK(Utils.setStatus(rotation[currentLine], token));
				
				Date CURRENT_DATE = GregorianCalendar.getInstance().getTime();
				String _stamp = "" + CURRENT_DATE.getHours() + ':' + CURRENT_DATE.getMinutes();
				if (_resp.isEmpty()){
					//All right
					currentLine = cycledInc(currentLine, rotation.length);
					shout(getString(R.string.ui_OK) + " " + currentLine + "/" + rotation.length + " (" +_stamp + ')');
				} else {
					shout(getString(R.string.ui_E) + " " + _resp + '(' +_stamp + ')');
					Log.e("MS.VK.SSR.MAYDAY", _resp);
				}
			}
		}, 0, 1000 * _instructions.getInt(MainActivity.EXTRA_PERIOD));
		NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		initShouting();
		shout(getString(R.string.ui_starting));
		return Service.START_REDELIVER_INTENT;
	}
	
	public int cycledInc(int _in, int _max){
		_in++;
		if (_in >= _max)
			_in = 0;
		return _in;
	}
	
	public void shout(final String _voice){
		NM.notify(whatIsYourNameHorsey, smallHorsey.setOngoing(true).setContentText(_voice).build());
	}
	
	public void shutup(){
		NM.cancelAll();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}