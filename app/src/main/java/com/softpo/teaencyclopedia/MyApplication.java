package com.softpo.teaencyclopedia;

import android.app.Application;

import cn.sharesdk.framework.ShareSDK;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		ShareSDK.initSDK(getApplicationContext());
	}
}
