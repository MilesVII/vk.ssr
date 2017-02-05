package com.milesseventh.vk.ssr;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.Toast;
import net.sf.corn.httpclient.HttpForm;

public class Utils extends android.app.Application {
	/*
	 * This class contains some commonly-used methods
	 */	
	public static final String PREF_TOKEN = "token";//, PREF_DATA = "data", PREF_PERIOD = "period";
	private static final String DATA_CONTAINER = "rotations.dat"; 
	public static final int TARGET_USER = -1, MIN_PERIOD = 120, LOGIN_REQUEST_CODE = 777;
	public static ArrayList<RotationData> data;
	public static ArrayList<VKGroup> groups = new ArrayList<VKGroup>();
	public static MainActivity ctxt;
	private static JsonParser jp = new JsonParser();
	
	public static void setContext(MainActivity _ctxt){
		ctxt = _ctxt;
	}
	
	public static String getPrefs(Context _ctxt, String _key){
		String _t = PreferenceManager.getDefaultSharedPreferences(_ctxt).getString(_key, "");
		if (_t == null) _t = "";
		return _t;
	}
	
	public static void setPrefs(Context _ctxt, String _key, String _val){
		Editor _ed = PreferenceManager.getDefaultSharedPreferences(_ctxt).edit(); 
		_ed.putString(_key, _val);
		_ed.commit();
	}

	public static void showInfoDialog(Activity _ctxt, String _title, String _text){
		InfoDialogFragment _t = new InfoDialogFragment();
		_t.setData(_title, _text);
		_t.show(_ctxt.getFragmentManager(), "...");
	}
	
	public static void showAboutDialog(Activity _ctxt){
		InfoDialogFragment _t = new InfoDialogFragment();
		_t.setData(_ctxt.getString(R.string.ui_about), _ctxt.getString(R.string.ui_about_data));
		_t.linkifyOn();
		_t.show(_ctxt.getFragmentManager(), "...");
	}
	
	public static void requestConfirmation(Activity _ctxt, String _text, DialogInterface.OnClickListener _act){
		ConfirmationDialogFragment _t = new ConfirmationDialogFragment();
		_t.setData(_text, _act);
		_t.show(_ctxt.getFragmentManager(), "...");
	}
	
	public static String setStatus(String _token, String _text, int _target){
		try{
			HttpForm _form = new HttpForm(new URI("https://api.vk.com/method/status.set"));
			_form.putFieldValue("text", _text);
			_form.putFieldValue("access_token", _token);
			if (_target != TARGET_USER)
				_form.putFieldValue("group_id", "" + _target);
			return _form.doGet().getData();
		} catch(Exception _ex){
			_ex.printStackTrace();
		}
		return ctxt.getString(R.string.ui_conerror);
	}

	public static String getGroupsList(String _token){
		try{
			HttpForm _form = new HttpForm(new URI("https://api.vk.com/method/groups.get"));
			_form.putFieldValue("access_token", _token);
			_form.putFieldValue("filter", "editor");
			_form.putFieldValue("extended", "1");
			_form.putFieldValue("count", "700");
			
			groups.clear();
			String _response = _form.doGet().getData();
			JsonArray _jresponse = jp.parse(_response).getAsJsonObject().get("response").getAsJsonArray();
			for (JsonElement _orgyMember: _jresponse)
				if (_orgyMember.isJsonObject())
					groups.add(new VKGroup(_orgyMember.getAsJsonObject().get("gid").getAsInt(), _orgyMember.getAsJsonObject().get("name").getAsString()));
			return "";
		} catch(Exception _ex){
			_ex.printStackTrace();
		}
		return ctxt.getString(R.string.ui_conerror);
	}
	
	public static void shout(final String _text){
		ctxt.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				Toast.makeText(ctxt, _text, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	public static String isOK(String _response){
		return (_response.trim().equals("{\"response\":1}"))?"":_response;
	}
	
	public static void initDataContainer(){
		data = loadDataFromFile();
		if (data == null)
			data = new ArrayList<RotationData>();
	}
	
	public static void saveDataToFile(ArrayList<RotationData> _data){
		try {
			FileOutputStream _fos = ctxt.openFileOutput(DATA_CONTAINER, Context.MODE_PRIVATE);
			ObjectOutputStream _oos = new ObjectOutputStream(_fos);
			_oos.writeObject(_data);
			_oos.close();
			_fos.close();
		} catch (Exception _ex) {
			_ex.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<RotationData> loadDataFromFile(){
		ArrayList<RotationData> _r;
		try{
			FileInputStream _fos = ctxt.openFileInput(DATA_CONTAINER);
			ObjectInputStream _oos = new ObjectInputStream(_fos);
			_r = (ArrayList<RotationData>)_oos.readObject();
			_oos.close();
			_fos.close();
			return _r;
		} catch (Exception _ex) {
			_ex.printStackTrace();
		}
		return null;
	}
}