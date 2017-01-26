package com.milesseventh.vk.ssr;

import java.net.URI;

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
	public static final String PREF_TOKEN = "token", PREF_DATA = "data", PREF_PERIOD = "period";
	public static MainActivity ctxt;
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
	
	public static void requestConfirmation(Activity _ctxt, String _text, DialogInterface.OnClickListener _act){
		ConfirmationDialogFragment _t = new ConfirmationDialogFragment();
		_t.setData(_text, _act);
		_t.show(_ctxt.getFragmentManager(), "...");
	}
	
	/*public static void showError(Activity _ctxt, Exception _ex){
		showInfoDialog(_ctxt, _ctxt.getString(R.string.ui_e), _ex.getMessage() + "\n" + _ex.getLocalizedMessage());
	}*/
	
	public static boolean isCustomParserUsed(Context _ctxt){
		return PreferenceManager.getDefaultSharedPreferences(_ctxt).getBoolean("use_php_parser", false);
	}
	
	public static String getCustomParserURL(Context _ctxt){
		return PreferenceManager.getDefaultSharedPreferences(_ctxt).getString("php_parser_url", "http://no");
	}
	
	public static String setStatus(String _text, String _token){
		try{
			HttpForm _form = new HttpForm(new URI("https://api.vk.com/method/status.set"));
			_form.putFieldValue("text", _text);
			_form.putFieldValue("access_token", _token);
			return _form.doGet().getData();
			//final String _x = _form.getURI().toString()+"\n"+_form.doGet().getData();
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
}