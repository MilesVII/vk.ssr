package com.milesseventh.vk.ssr;

import java.util.Timer;

public class Task {
	private int horseystail = 0, target;
	private RotationData rd;
	private Timer myTimer = null;
	
	public Task(RotationData _rd, int _target){
		rd = _rd;
		target = _target;
	}
	
	public String getCurrentLine(){
		return rd.rotation[horseystail];
	}
	
	public void switchToNext(){
		horseystail++;
		if (horseystail >= rd.rotation.length)
			horseystail = 0;
	}
	
	public int getTarget(){
		return target;
	}
	
	public String getName(){
		return rd.name;
	}
	
	public int getPeriod(){
		return rd.period;
	}
	
	public void linkTimer (Timer _in){
		myTimer = _in;
	}
	
	public Timer getTimer(){
		return myTimer;
	}
}
