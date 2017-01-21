package com.milesseventh.vk.ssr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LoginActivity extends Activity {
	private WebView web;
	
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
		web = (WebView)findViewById(R.id.web);
		web.setWebViewClient(new CustomWebClient());
		web.loadUrl("https://oauth.vk.com/authorize?client_id=" + MainActivity.APP_ID + 
				"&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=status+offline&response_type=token&v=5.62");
	}
	
	public void complete(String _token){
		Intent _bukake = new Intent(this, MainActivity.class);
		_bukake.setAction(Intent.ACTION_VIEW);
		_bukake.putExtra(MainActivity.EXTRA_TOKEN, _token);
		
		if (getParent() == null)
			setResult(Activity.RESULT_OK, _bukake);
		else
			getParent().setResult(Activity.RESULT_OK, _bukake);

		finish();
	}
}
