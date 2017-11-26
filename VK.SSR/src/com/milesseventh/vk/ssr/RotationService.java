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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.SparseArray;

public class RotationService extends Service	{
	public static final String TAG = "DebugPoint";
	
	public static final int MSG_ADD_TASK = 1,
	                        MSG_REMOVE_TASK = 2,
	                        MSG_CHECK_UP = 4,
	                        MSG_KILL_THE_LIGHTS = 5,
	                        MSG_DIE = 6;
	public static final String KEY_TOKEN = "com.milesseventh.vk.ssr.keys.token",
	                           KEY_ROTATION = "com.milesseventh.vk.ssr.keys.rot",
	                           KEY_TASKS = "com.milesseventh.vk.ssr.keys.tasks";
	
	private static final String SPECIAL_NDATE = "\\[special:nonadate\\]";
	private static final String SPECIAL_DATE = "\\[special:date\\]";
	private static final String SPECIAL_YEARPERCENTAGE = "\\[special:yp\\]";
	private String token;
	private ArrayList<Task> tasks = new ArrayList<Task>();
	private SparseArray<Timer> timerMap = new SparseArray<Timer>();//It's a long story
	/* Firstly, class Task was containing "timer" field. 
	 * When Task was generated with addTask, "timer" was containing link to created Timer object
	 * So, when Task someone decide to stop task
	 * One could just do timer.cancel();
	 * But when we started implementing IPC, wanting our services to live some more time
	 * We faced the problem
	 * Activities wanted to control, what tasks were running as they birth
	 * So we decided to show them ArrayList<Task> tasks by passing it via Message
	 * But turns out it isn't possible to pass not Parcelable objects between processes
	 * We tried to implement Parcelable interface for ArraList, and almost done it, but we failed as we tried to serialize timer object
	 * So, timerMap was born
	 * We should replace it with array, maybe
	 
	 */
	
	Messenger messenger = new Messenger(new Handler(){
		@Override
		public void handleMessage(Message in) {
			switch(in.what){
			case(MSG_ADD_TASK):
				addTask((Rotation) in.getData().getSerializable(KEY_ROTATION), in.arg1);
				break;
			case(MSG_REMOVE_TASK):
				removeTask(in.arg1);
				break;
			case(MSG_CHECK_UP):
				setToken(in.getData().getString(KEY_TOKEN));
				Message m = Message.obtain(null, MSG_CHECK_UP);
				Bundle box = new Bundle();
				box.putSerializable(KEY_TASKS, tasks);
				m.setData(box);
				try {
					in.replyTo.send(m);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case(MSG_KILL_THE_LIGHTS):
				killTheLights();
				break;
			case(MSG_DIE):
				killTheLights();
				gnight();
			}
		}
	});
	private void setToken(String t){
		if (token == null || !token.equals(t)){
			token = t;
			killTheLights();
		}
	}
	private void killTheLights(){
		@SuppressWarnings("unchecked")
		ArrayList<Task> belowTheWaterfallOfRocks = (ArrayList<Task>) tasks.clone();
		for (Task rock: belowTheWaterfallOfRocks)
			removeTask(rock);
	}
	
	private NotificationManager NM;
	private NotificationCompat.Builder smallHorsey;
	private int whatIsYourNameHorsey;
	
	public void initShouting(){
		smallHorsey = new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle(getString(R.string.app_name))
			.setContentIntent(PendingIntent.getActivity(this, 0, 
			                                            new Intent(this, MainActivity.class),//.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP),
			                                            PendingIntent.FLAG_UPDATE_CURRENT));
	}
	
	@Override
	public int onStartCommand(Intent _int, int _flags, int _startId){
		return Service.START_NOT_STICKY;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		initShouting();
		startForeground(whatIsYourNameHorsey, smallHorsey.build());
		shout(getString(R.string.ui_ison));
	}
	
	@Override
	public void onDestroy(){
		shutup();
		super.onDestroy();
	}
	
	private String preprocessSpecials(String _in){
		Calendar _c = Calendar.getInstance();
		String _out;
		_out = _in.replaceAll(SPECIAL_DATE, "" + _c.get(Calendar.DAY_OF_MONTH) + "." + (_c.get(Calendar.MONTH) + 1) + "." + _c.get(Calendar.YEAR));
		_out = _out.replaceAll(SPECIAL_NDATE, "" + _c.get(Calendar.DAY_OF_YEAR));
		_out = _out.replaceAll(SPECIAL_YEARPERCENTAGE, "" + (_c.get(Calendar.DAY_OF_YEAR) * 100 / (float)_c.getActualMaximum(Calendar.DAY_OF_YEAR)) + '%');
		return _out;
	}
	
	public Task addTask(Rotation _rd, int _target){
		removeTask(_target);
		final Task task = new Task(_rd, _target);
		Timer rotor = new Timer();
		rotor.schedule(new TimerTask(){
			@Override
			public void run() {
				String responce = Utils.isOK(Utils.setStatus(token, preprocessSpecials(task.rotation.text[task.currentLine]), task.target));
				if (responce.isEmpty()){
					//All right
					task.switchToNext();
					shout();
				} else {
					shout(getString(R.string.ui_E) + ": " + responce);
					Log.e("MS.VK.SSR.MAYDAY", responce);
				}
			}
		}, 0, 1000 * task.rotation.period);
		tasks.add(task);
		timerMap.put(task.target, rotor);
		return task;
	}
	
	@SuppressWarnings("unchecked")
	public void removeTask(int target){//By target
		for (Task machineHead: (ArrayList<Task>)tasks.clone())
			if (machineHead.target == target){
				Log.d(TAG, "Removed task with: " + machineHead.rotation.name);
				removeTask(machineHead);
			}
	}
	public void removeTask(Task victim){
		timerMap.get(victim.target).cancel();
		timerMap.remove(victim.target);
		tasks.remove(victim);
		shout();
	}
	
	private void shout(){
		Calendar CURRENT_DATE = Calendar.getInstance();
		String _stamp = "" + CURRENT_DATE.get(Calendar.HOUR_OF_DAY) + ':' + CURRENT_DATE.get(Calendar.MINUTE);
		shout(getString(R.string.ui_ison) + ": AR:" + tasks.size() + " LSU:" + _stamp);
	}
	
	public void shout(final String _voice){
		NM.notify(whatIsYourNameHorsey, smallHorsey.setOngoing(true).setContentText(_voice).build());
	}
	
	public void shutup(){
		NM.cancelAll();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return messenger.getBinder();
	}
	
	public void gnight(){
		shutup();
		stopSelf();
	}
}