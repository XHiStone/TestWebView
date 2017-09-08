package com.isoftstone.testwebview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onclick(View v) {
        Toast.makeText(getBaseContext(), "saveData", Toast.LENGTH_SHORT).show();
        WebView mywebView = new WebView(this);
        mywebView.getSettings().setJavaScriptEnabled(true);
        mywebView.getSettings().setDomStorageEnabled(true);// 打开本地缓存提供JS调用,至关重要
        mywebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);// 实现8倍缓存
        mywebView.getSettings().setAllowFileAccess(true);
        mywebView.getSettings().setAppCacheEnabled(true);
        String appCachePath = getApplication().getCacheDir().getAbsolutePath();
        mywebView.getSettings().setAppCachePath(appCachePath);
        mywebView.getSettings().setDatabaseEnabled(true);
        mywebView.addJavascriptInterface(new AppClass(getBaseContext()), "android");
        mywebView.loadUrl("file:///android_asset/javascript.html");
        mywebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:saveData('123')");
            }
        });
    }

    private class AppClass {
        private Context c;

        public AppClass(Context baseContext) {
            this.c = baseContext;
        }

        @JavascriptInterface
        public void getUserKey(String userKey) {
            Toast.makeText(c, userKey + "", Toast.LENGTH_SHORT).show();
            Log.e("Tag", "读取到userKey : " + userKey);
        }
    }
}
