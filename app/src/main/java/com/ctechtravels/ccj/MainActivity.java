package com.ctechtravels.ccj;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MainActivity extends Activity {
    ProgressDialog progressDialog;


    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();

        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    //private Button button;
    private WebView webView;
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //This will not show title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        startActivity(new Intent(MainActivity.this,SignUp.class));

        //Get webview
        webView = (WebView) findViewById(R.id.webView1);
        if(haveNetworkConnection()){
            startWebView("http://ctechtravels.com");
        } else {
            webView.loadData("<!doctype html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "  <meta charset=\"utf-8\">\n" +
                    "  <meta name=\"apple-mobile-web-app-capable\" content=\"yes\">\n" +
                    "  <meta name=\"mobile-web-app-capable\" content=\"yes\">\n" +
                    "  <title>No Connection</title>  \n" +
                    "\n" +
                    "<!-- Stylesheets--> \n" +
                    " <style type=\"text/css\">\n" +
                    " body{\n" +
                    "  background: #E1e1e1;\n" +
                    "}\n" +
                    "#cloud{\n" +
                    "  width: 300px;\n" +
                    "  height: 120px;\n" +
                    "  background: #676767;\n" +
                    "  background: -webkit-linear-gradient(-90deg,#676767 5%, #676767 100%);\n" +
                    "  -webkit-border-radius: 100px;\n" +
                    "  -moz-border-radius: 100px;\n" +
                    "  border-radius: 100px;\n" +
                    "  position: relative;\n" +
                    "  margin: 150px auto 0;\n" +
                    "  opacity: .5;\n" +
                    "}\n" +
                    "#cloud:before, #cloud:after{\n" +
                    "  content: '';\n" +
                    "  position:absolute;\n" +
                    "  background: #676767;\n" +
                    "  z-index: -1;\n" +
                    "}\n" +
                    "#cloud:after{\n" +
                    "  width: 100px;\n" +
                    "  height: 100px;\n" +
                    "  top: -50px;\n" +
                    "  left:50px;\n" +
                    "  -webkit-border-radius: 100px;\n" +
                    "  -moz-border-radius: 100px;\n" +
                    "  border-radius: 100px;\n" +
                    "}\n" +
                    "#cloud:before{\n" +
                    "  width: 120px;\n" +
                    "  height: 120px;\n" +
                    "  top: -70px;\n" +
                    "  right: 50px;\n" +
                    "  -webkit-border-radius: 200px;\n" +
                    "  -moz-border-radius: 200px;\n" +
                    "  border-radius: 200px;\n" +
                    "}\n" +
                    ".shadow {\n" +
                    "  width: 300px;\n" +
                    "  position: absolute;\n" +
                    "  bottom: -10px;\n" +
                    "  background: black;\n" +
                    "  z-index: -1;\n" +
                    "  -webkit-box-shadow: 0 0 25px 8px rgba(0,0,0,0.4);\n" +
                    "  -moz-box-shadow: 0 0 25px 8px rgba(0,0,0,0.4);\n" +
                    "  box-shadow: 0 0 25px 8px rgba(0,0,0,0.4);\n" +
                    "  -webkit-border-radius: 50%;\n" +
                    "  -moz-border-radius: 50%;\n" +
                    "  border-radius: 50%;\n" +
                    "}\n" +
                    "h2 {\n" +
                    "  color: #fff;\n" +
                    "  font-size: 20px;\n" +
                    "  padding-top: 15px;\n" +
                    "  text-align: center;\n" +
                    "  margin: 5px auto;\n" +
                    "}\n" +
                    "h4 {\n" +
                    "  color: #fff;\n" +
                    "  font-size: 12px;\n" +
                    "  margin: 0 auto;\n" +
                    "  padding: 0;\n" +
                    "  text-align: center;\n" +
                    "}\n" +
                    " </style>\n" +
                    "\n" +
                    "<body>    \n" +
                    "<div id=\"cloud\"> <h2>No Connection :(</h2>\n" +
                    "<h4>Check your WiFi or Mobile Internet!</h4>\n" +
                    "<span class=\"shadow\"></span></div>\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>","text/html",null);
        }
    }

    private void startWebView(String url) {

        //Create new webview Client to show progress dialog
        //When opening a url or click on link
        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress==100)
                {
                    if (progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {

            //If you will not use this method url links are opeen in new brower not in webview
        /*    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
          */  //If url has "tel:245678" , on clicking the number it will directly call to inbuilt calling feature of phone
            public boolean shouldOverrideUrlLoading(WebView view ,String url){

                if(url.startsWith("tel:")){
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                    return false;
                } else {

                    view.loadUrl(url);
                    return true;
                }
            }

            //Show loader on url load
            public void onLoadResource (WebView view, String url) {
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }
            }
            public void onPageFinished(WebView view, String url) {
                try{
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }

        });

        // Javascript inabled on webview
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setLayoutAlgorithm(webView.getSettings().getLayoutAlgorithm().NORMAL);
        webView.setInitialScale(1);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);



        // Other webview options
        /*
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setBuiltInZoomControls(true);
        		//Additional Webview Properties
        	        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		        webView.getSettings().setDatabaseEnabled(true);
		        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
		        webView.getSettings().setAppCacheEnabled(true);
		        webView.getSettings().setLayoutAlgorithm(webView.getSettings().getLayoutAlgorithm().NORMAL);
		        webView.getSettings().setLoadWithOverviewMode(true);
		        webView.getSettings().setUseWideViewPort(false);
		        webView.setSoundEffectsEnabled(true);
		        webView.setHorizontalFadingEdgeEnabled(false);
		        webView.setKeepScreenOn(true);
		        webView.setScrollbarFadingEnabled(true);
		        webView.setVerticalFadingEdgeEnabled(false);






        */

        /*
         String summary = "<html><body>You scored <b>192</b> points.</body></html>";
         webview.loadData(summary, "text/html", null);
         */

        //Load url in webview
        webView.loadUrl(url);


    }



    // Open previous opened link from history on webview when back button pressed

    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }

}