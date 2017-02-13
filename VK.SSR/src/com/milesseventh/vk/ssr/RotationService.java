package com.milesseventh.vk.ssr;

import java.util.ArrayList;
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
	private static final String SPECIAL_NDATE = "\\[special:nonadate\\]";
	private static final String SPECIAL_DATE = "\\[special:date\\]";
	private static final String SPECIAL_YEARPERCENTAGE = "\\[special:yp\\]";
	private String token;
	private ArrayList<Task> tasks = new ArrayList<Task>();
	
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
	
	/*public RotationService() {
		super();
	}*/
	
	public Task isItIn(int _target){
		for (Task _hooves: tasks)
			if (_hooves.getTarget() == _target)
				return _hooves;
		return null;
	}
	
	@Override
	public int onStartCommand(Intent _int, int _flags, int _startId){
		MainActivity.ctxt.onServiceStart(this);
		token = Utils.getPrefs(MainActivity.ctxt, Utils.PREF_TOKEN);
		NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		initShouting();
		startForeground(whatIsYourNameHorsey, smallHorsey.build());
		shout(getString(R.string.ui_ison));
		return Service.START_REDELIVER_INTENT;
	}
	
	private String preprocessSpecials(String _in, Calendar _c){
		String _out;
		_out = _in.replaceAll(SPECIAL_DATE, "" + _c.get(Calendar.DAY_OF_MONTH) + "." + (_c.get(Calendar.MONTH) + 1) + "." + _c.get(Calendar.YEAR));
		_out = _out.replaceAll(SPECIAL_NDATE, "" + _c.get(Calendar.DAY_OF_YEAR));
		_out = _out.replaceAll(SPECIAL_YEARPERCENTAGE, "" + (_c.get(Calendar.DAY_OF_YEAR) * 100 / (float)_c.getActualMaximum(Calendar.DAY_OF_YEAR)) + '%');
		return _out;
	}
	
	public Task addTask(RotationData _rd, int _target){
		final Task _t = new Task(_rd, _target);
		Timer _rotator = new Timer();
		_rotator.schedule(new TimerTask(){
			@Override
			public void run() {
				String _resp = Utils.isOK(
							Utils.setStatus(token, preprocessSpecials(_t.getCurrentLine(), Calendar.getInstance()), _t.getTarget())
						);
				if (_resp.isEmpty()){
					//All right
					_t.switchToNext();
					shout();
				} else {
					shout(getString(R.string.ui_E) + ": " + _resp);
					Log.e("MS.VK.SSR.MAYDAY", _resp);
				}
			}
		}, 0, 1000 * _t.getPeriod());
		_t.linkTimer(_rotator);
		tasks.add(_t);
		return _t;
	}
	
	public void removeTask(Task _victim){
		_victim.getTimer().cancel();
		tasks.remove(_victim);
		shout();
	}
	
	private void shout(){
		Calendar CURRENT_DATE = Calendar.getInstance();
		String _stamp = "" + CURRENT_DATE.get(Calendar.HOUR_OF_DAY) + ':' + CURRENT_DATE.get(Calendar.MINUTE);
		shout(getString(R.string.ui_ison) + ": AR:" + tasks.size() + " LSU:" +_stamp);
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