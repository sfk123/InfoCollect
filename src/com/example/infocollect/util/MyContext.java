package com.example.infocollect.util;

import java.util.Map;

public class MyContext {
	private final static String host="http://61.164.44.165:39175/appRequest/";//��Ŀ��̨��ַ
	public final static String httpInterface_Login="login";//��¼�ӿ�����
	public final static String httpInterface_upload="uploadExpressInfo";//	�ϴ������Ϣ
	public final static String httpInterface_upload_batch="uploadExpressInfos";//�����ϴ�
	public final static String httpInterface_notice="getUnreadMessage";//��ѯ�����ӿ�����
	public final static String httpInterface_getBillDetail="getBillDetail";//��������ӿ�����
	
	public static String GetUrl(String interface_type,Map<String ,String> params){//������ʵ�ַ��ƴ��
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
	public static String GetUrl(String host,String interface_type,Map<String ,String> params){//������ʵ�ַ��ƴ��
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
