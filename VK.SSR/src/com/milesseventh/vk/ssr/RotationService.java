package com.milesseventh.vk.ssr;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class RotationService extends Service {
	public static final String ID = "RotationService";
	private String token;
	//private Timer rotator;
	private int runningRotations = 0;
	
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
		token = Utils.getPrefs(this, Utils.PREF_TOKEN);
		
		NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		initShouting();
		shout(getString(R.string.ui_ison));
		
		startForeground(whatIsYourNameHorsey, smallHorsey.build());
		return Service.START_REDELIVER_INTENT;
	}
	
	public Timer addTask(final Task _t){
		runningRotations++;
		Timer _rotator = new Timer();
		_rotator.schedule(new TimerTask(){
			@Override
			public void run() {
				String _resp = Utils.isOK(Utils.setStatus(token, _t.getCurrentLine(), _t.getTarget()));
				if (_resp.isEmpty()){
					//All right
					_t.switchToNext();
					shout(runningRotations);
				} else {
					shout(getString(R.string.ui_E) + ": " + _resp);
					Log.e("MS.VK.SSR.MAYDAY", _resp);
				}
			}
		}, 0, 1000 * _t.getPeriod());
		return _rotator;
	}
	
	private void shout(int _ar){
		Calendar CURRENT_DATE = Calendar.getInstance();
		String _stamp = "" + CURRENT_DATE.get(Calendar.HOUR_OF_DAY) + ':' + CURRENT_DATE.get(Calendar.MINUTE);
		shout(getString(R.string.ui_ison) + ": AR:" + _ar + " LSU:" +_stamp);
		
	}
	
	public void shout(final String _voice){
		NM.notify(whatIsYourNameHorsey, smallHorsey.setOngoing(true).setContentText(_voice).build());
	}
	
	public void decreaseCounter(){
		runningRotations--;
		shout(runningRotations);
	}
	
	public void shutup(){
		NM.cancelAll();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}