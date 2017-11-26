package com.milesseventh.vk.ssr;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	public String D = "DebugPoint:Main";
	public static final String EXTRA_TOKEN = "com.milesseventh.vk.ssr.token";
	private String token;
	public static MainActivity ctxt; 
	private LinearLayout taskList;
	private Button b_manage;
	public boolean connected = false;
	public Intent serviceIntent;
	private DialogInterface.OnClickListener cl_logout = new DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			Utils.setPrefs(ctxt, Utils.PREF_TOKEN, "");
			finish();
		}
	};
	private DialogInterface.OnClickListener cl_close = new DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			msgDie();
			finish();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.ctxt = ctxt = this;
		serviceIntent = new Intent(this, RotationService.class);
		Utils.initDataContainer();
		token = Utils.getPrefs(this, Utils.PREF_TOKEN);
		
		setContentView(R.layout.activity_main);
		b_manage = (Button)findViewById(R.id.b_manage);
		b_manage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ctxt, RotationManagerActivity.class));
			}
		});
		taskList = (LinearLayout) findViewById(R.id.task_list);
		
			Log.d(D, "started, connected: " + connected);
		if (!Utils.isServiceRunning(this)){
			startService(serviceIntent);
		}
		loadTargets();
		//if (!connected)
	}
	
	@Override
	public void onResume(){
		Log.d(D, "onResume()");
		if (connected)
			msgCheckUp();
		else
			bindService(serviceIntent, sc, Service.BIND_ABOVE_CLIENT);
		super.onResume();
	}
	
	@Override
	public void onStop(){
		Log.d(D, "onStop()");
		if (connected){
			unbindService(sc);
			connected = false;
		}
		super.onStop();
	}
	
	@Override
	public void onDestroy(){
		Log.d(D, "onDestroy()");
		if (getParent() != null)
			getParent().finish();
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem _i){
		switch(_i.getItemId()){
		case R.id.m_about:
			Utils.showAboutDialog(this);
			return true;
		case R.id.m_logout:
			Utils.requestConfirmation(this, getString(R.string.conf_stop), cl_logout);
			return true;
		case R.id.m_close:
			Utils.requestConfirmation(this, getString(R.string.conf_close), cl_close);
			return true;
		}
		return super.onOptionsItemSelected(_i);
	}
	
	public Object targetloadingMonitor = new Object();
	public Runnable groupLoader = new Runnable(){
		@Override
		public void run() {
			for (VKGroup _lickingcroups: Utils.groups)
				taskList.addView(new TaskEntry(ctxt, _lickingcroups.gid, _lickingcroups.name));
		}
	};
	private void loadTargets(){
		taskList.removeAllViews();
		taskList.addView(new TaskEntry(this, Utils.TARGET_USER, getString(R.string.ui_user)));
		Thread _loader = new Thread(new Runnable(){
			@Override
			public void run() {
				if (Utils.getGroupsList(token).isEmpty())//Means groups list loaded without errors
					runOnUiThread(groupLoader);
				synchronized(targetloadingMonitor){
					MainActivity.ctxt.targetloadingMonitor.notify();
				}
			}
		});
		_loader.start();
		synchronized(targetloadingMonitor){
			try {
				targetloadingMonitor.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (connected)
			msgCheckUp();
	}
	
	///////////////////////////////////////////////////////////
	//IPC SECTOR
	public Messenger sendMan;
	public ServiceConnection sc = new ServiceConnection(){
			@Override
			public void onServiceConnected(ComponentName arg0, IBinder binder) {
				connected = true;
				sendMan = new Messenger(binder);
				msgCheckUp();
			}
			
			@Override
			public void onServiceDisconnected(ComponentName arg0) {
				connected = false;
				sendMan = null;
			}
		};
	
	Messenger receiver = new Messenger(new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case(RotationService.MSG_CHECK_UP):
				@SuppressWarnings("unchecked") ArrayList<Task> tasksInCloud = (ArrayList<Task>) msg.getData().getSerializable(RotationService.KEY_TASKS);
				if (!tasksInCloud.isEmpty()){
					for (int i = 0; i < taskList.getChildCount(); i++){
						TaskEntry te = ((TaskEntry)taskList.getChildAt(i));
						for (Task trapped: tasksInCloud)
							if (trapped.target == te.target){
								te.rotationTitle = trapped.rotation.name;
								te.refreshCaption();
							}
						//ohfuck
					}
				}
			}
		}
	});
	private boolean sand(Message m){
		try {
			if (connected)
				sendMan.send(m);
			else 
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean msgAddTask(Rotation r, int target){
		Message m = Message.obtain(null, RotationService.MSG_ADD_TASK, target, 0);
		Bundle box = new Bundle();
		box.putSerializable(RotationService.KEY_ROTATION, r);
		m.setData(box);
		return sand(m);
	}
	public boolean msgRemoveTask(int t){
		Message m = Message.obtain();
		m.arg1 = t;
		m.what = RotationService.MSG_REMOVE_TASK;
		return sand(m);
	}
	public void msgCheckUp(){
		Message m = Message.obtain(null, RotationService.MSG_CHECK_UP);
		Bundle box = new Bundle();
		box.putString(RotationService.KEY_TOKEN, token);
		m.setData(box);
		m.replyTo = receiver;
		sand(m);
	}
	public void msgKillTheLights(){
		Message m = Message.obtain(null, RotationService.MSG_KILL_THE_LIGHTS);
		sand(m);
	}
	public void msgDie(){
		Message m = Message.obtain(null, RotationService.MSG_DIE);
		sand(m);
	}
}