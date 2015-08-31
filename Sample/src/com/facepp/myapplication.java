package com.facepp;


import android.app.Application;

import com.facerecognition.data.Configure;

public class myapplication extends Application {
	//也可以在此处设置api_key,api_secret
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Configure.updateProperties(Configure.API_KEY,
				"b7d79040d0e01b7764432604fc6f9bf4");
		Configure.updateProperties(Configure.API_SECRET,
				"lDK64dM11jEWlMEwONSquoLt6zcpEhbG");
	}
}
