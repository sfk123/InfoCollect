package com.example.infocollect.util;

import java.util.Map;

public class MyContext {
	private final static String host="http://61.164.44.165:39175/appRequest/";//项目后台地址
	public final static String httpInterface_Login="login";//登录接口名称
	public final static String httpInterface_upload="uploadExpressInfo";//	上传快件信息
	public final static String httpInterface_upload_batch="uploadExpressInfos";//批量上传
	public final static String httpInterface_notice="getUnreadMessage";//查询订单接口名称
	public final static String httpInterface_getBillDetail="getBillDetail";//付款详情接口名称
	
	public static String GetUrl(String interface_type,Map<String ,String> params){//具体访问地址的拼接
		String url_temp=host+interface_type;
		if(params!=null){
		 url_temp=url_temp+"?";
		int i=0;
		for (String key : params.keySet()) {
			if(i==0)
			url_temp=url_temp+key +"="+params.get(key);
			else
				url_temp=url_temp+"&"+key +"="+params.get(key);
			i++;
		}
		}
		return url_temp;
	}
	public static String GetUrl(String host,String interface_type,Map<String ,String> params){//具体访问地址的拼接
		String url_temp=host+interface_type;
		if(params!=null){
		 url_temp=url_temp+"?";
		int i=0;
		for (String key : params.keySet()) {
			if(i==0)
			url_temp=url_temp+key +"="+params.get(key);
			else
				url_temp=url_temp+"&"+key +"="+params.get(key);
			i++;
		}
		}
		return url_temp;
	}
	public static String getUrl_Post(String interface_str){
		return  host+interface_str;
	}
}
