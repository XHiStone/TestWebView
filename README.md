# TestWebView
H5交互Webview实现localStorage数据存储
先看看效果图吧
![image](https://github.com/18337129968/TestWebView/blob/master/photo/localStorage.gif)<br>
实现比较简单但是第一次用可能会遇到一些坑<br>
### 首先得有Webview控件：
有人问我是不是需要写布局文件，不写行不行，现在我就告诉你们，不写没问题，需要写就写不写直接创建New一个也行。
下面我就介绍一个，我new一个Webview实现localStorage。
```
 WebView mywebView = new WebView(this);
 mywebView.getSettings().setJavaScriptEnabled(true);
 mywebView.getSettings().setDomStorageEnabled(true);// 打开本地缓存提供JS调用,至关重要
 mywebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);// 实现8倍缓存
 mywebView.getSettings().setAllowFileAccess(true);
 mywebView.getSettings().setAppCacheEnabled(true);
 String appCachePath = getApplication().getCacheDir().getAbsolutePath();
 mywebView.getSettings().setAppCachePath(appCachePath);
 mywebView.getSettings().setDatabaseEnabled(true);
```
上面这些settings是实现localStorage需要的存储条件。
### 其次就是如何实现localStorage本地存储了：
其实我在网上搜索了很多比如这样
```
 String userAgent = "shixinzhang";
 String js = "window.localStorage.setItem('userAgent','" + userAgent + "');";
 String jsUrl = "javascript:(function({
    var localStorage = window.localStorage;
    localStorage.setItem('userAgent','" + userAgent + "')
 })()";
 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
    mWebView.evaluateJavascript(js, null);
 } else {
    mWebView.loadUrl(jsUrl);
    mWebView.reload();
 }
```
这里就会出现很多坑了，当然我也踩过比如你直接将该代码复制到settings下面，直接运行你会发现你存不进去反而出现报错，
你会发现提示Window找不到localStorage属性，怎么都存不进去。不是说上面代码写的是错误的，而是这样写的确有问题，
因为Webview浏览器并未打开找不到localStorage，所以要想解决这个问题就得先打开Android的Webview浏览器才能找到localStorage。
关于上面代码如果想用可以提供一种解决方案，那就是将上面代码写在onPageFinished里：
```
 mywebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String userAgent = "shixinzhang";
                String js = "window.localStorage.setItem('userAgent','" + userAgent + "');";
                String jsUrl = "javascript:(function({
                    var localStorage = window.localStorage;
                    localStorage.setItem('userAgent','" + userAgent + "')
                })()";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    view.evaluateJavascript(js, null);
                } else {
                    view.loadUrl(jsUrl);
                    view.reload();
                }
            }
        });
```
当然这种写法前提是最外层你得有：mywebView.loadUrl("**地址**");
我觉得这样写除非在你已经有路径可写的情况下这样可以，但是没有的话就乖乖的写一个吧，localStorage也写在Html里面。
### 在javaScript.html文件实现localStorage数据存储：
```
 <!DOCTYPE html>
 <html>
 <head>
    <meta charset="utf-8">
    <script>
       function saveData(param){
          <!--console.log("android调用此方法=====>saveData");-->
          localStorage.setItem("userAgent",param,true);
          console.log("android调用此方法=====>getData==="+localStorage.getItem("userAgent"));
          window.android.getUserKey(localStorage.getItem("userAgent"));
       }
    </script>
 </head>

 </html>
```
当然我在这里也写了Js回调，不熟悉的可以参考一下：window.android.getUserKey(localStorage.getItem("userAgent"));
### 剩下就是如何调用该Html了：
```
 mywebView.loadUrl("file:///android_asset/javascript.html");
 mywebView.setWebViewClient(new WebViewClient() {
         @Override
         public void onPageFinished(WebView view, String url) {
             super.onPageFinished(view, url);
             view.loadUrl("javascript:saveData('123')");
         }
   });
```
注意：view.loadUrl("javascript:saveData('123')");一定要在onPageFinished之后执行，因为浏览器加载完成这之后才能找到localStorage属性，因为这个是浏览器携带的属性。
### 当然JS回调我也给你们写出来：
```
 mywebView.addJavascriptInterface(new AppClass(getBaseContext()), "android");
```
```
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
```
