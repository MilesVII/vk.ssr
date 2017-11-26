package com.milesseventh.vk.ssr;

import java.io.Serializable;

public class Task implements Serializable{
	private static final long serialVersionUID = 7566142107405940792L;
	public int currentLine = 0, target;
	public Rotation rotation;

	public Task(Rotation _rd, int _target){
		rotation = _rd;
		target = _target;
	}
	
	public void switchToNext(){
		currentLine++;
		currentLine %= rotation.text.length;
	}
}
