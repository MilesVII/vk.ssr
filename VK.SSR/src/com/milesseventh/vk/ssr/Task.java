package com.milesseventh.vk.ssr;

public class Task {
	private int horseystail = 0, target;
	private RotationData rd;
	
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
	
	public int getPeriod(){
		return rd.period;
	}
}
