package com.milesseventh.vk.ssr;

import java.io.Serializable;

public class RotationData implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public String name = "";
	public int period = 0;
	public String[] rotation = null;
	public boolean isvalid = false;
	public RotationData() {
		// TODO Auto-generated constructor stub
	}

}
