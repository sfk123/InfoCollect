package com.example.infocollect.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtil {

	public static String getStringByKey(String key,Context context){
		SharedPreferences sharedata = context.getSharedPreferences("data", 0);    
		return sharedata.getString(key, null);  
	}
	public static boolean getBooleanByKey(String key,Context context){
		SharedPreferences sharedata = context.getSharedPreferences("data", 0);    
		return sharedata.getBoolean(key, false);
	}
	public static void save(String key,String value,Context context){
		Editor sharedata =context. getSharedPreferences("data", 0).edit();    
		sharedata.putString(key,value);    
		sharedata.commit();   
	}
	public static void save(String key,boolean value,Context context){
		Editor sharedata =context. getSharedPreferences("data", 0).edit();    
		sharedata.putBoolean(key,value);    
		sharedata.commit();   
	}
	public static void clearData(Context context) {
		Editor sharedata =context. getSharedPreferences("data", 0).edit();    
		sharedata.clear().commit();
	} 
}
