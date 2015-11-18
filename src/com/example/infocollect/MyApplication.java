package com.example.infocollect;

import com.baidu.mapapi.SDKInitializer;
import com.example.infocollect.model.UserInfo;

import android.app.Application;

public class MyApplication extends Application{
	

	private UserInfo user;//用户登录后的信息
	private static MyApplication application;
	public static MyApplication getInstence(){
		return application;
	}
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate()
    {
        super.onCreate();
        SDKInitializer.initialize(this);  
    	application=this;
    }
	public UserInfo getUser() {
		return user;
	}
	public void setUser(UserInfo user) {
		this.user = user;
	}
	
}
