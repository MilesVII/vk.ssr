package com.milesseventh.vk.ssr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class LoginActivity extends Activity {
	private WebView web;
	private final int APP_ID = 5823245;
	
	public class CustomWebClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView _host, String _url, Bitmap _fi){
			if (_url.contains("blank.html#"))
				complete(_url.substring(_url.indexOf("access_token=") + 13, _url.indexOf('&', _url.indexOf("access_token="))));
			else super.onPageStarted(_host, _url, _fi);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		try{CookieManager.getInstance().removeAllCookie();}catch(Exception _ex){}
		final Activity me = this;
		web = (WebView)findViewById(R.id.web);
		web.setWebViewClient(new CustomWebClient());
		web.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				me.setProgress(progress * 100);
			}
		});
		((Button)findViewById(R.id.reload)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				web.reload();
			}
		});
		//this.setProgress(progress);
		web.loadUrl("https://oauth.vk.com/authorize?client_id=" + APP_ID + 
				"&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=status+offline&response_type=token&v=5.62");
	}
	
	public void complete(String _token){
		Intent _bukake = new Intent();
		_bukake.putExtra(MainActivity.EXTRA_TOKEN, _token);
		setResult(RESULT_OK, _bukake);
		finish();
	}
}