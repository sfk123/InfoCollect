package com.example.infocollect;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.example.infocollect.util.MyContext;
import com.example.infocollect.util.MyHttp;
import com.example.infocollect.util.MyHttp.MyHttpCallBack;
import com.example.infocollect.util.SharedPreferencesUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Activity_Main extends Activity implements OnClickListener,MyHttpCallBack{
	
	private Button btn_fast,btn_batch,btn_info;
	private TextView tv_notice;
	private LinearLayout layout_notice;
	
	private MyHttp http;
	private String noticeUrl;
	private JSONObject notice;
	private Map<String, String> params;
	private Runnable runnable_getNotice;
	private Handler handler = new Handler();  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv_notice=(TextView)findViewById(R.id.tv_notice);
		btn_fast=(Button)findViewById(R.id.btn_fast);
		btn_batch=(Button)findViewById(R.id.btn_batch);
		btn_info=(Button)findViewById(R.id.btn_info);
		setClickListener(tv_notice,btn_fast,btn_batch,btn_info);
		layout_notice=(LinearLayout)findViewById(R.id.layout_notice);
		
		noticeUrl=MyContext.getUrl_Post(MyContext.httpInterface_notice);
		http=new MyHttp(this);
		params=new HashMap<>();
		params.put("token", MyApplication.getInstence().getUser().getToken());
		http.Http_post(noticeUrl, params, this);
		runnable_getNotice=new Runnable() {
			
			@Override
			public void run() {
				http.Http_post(noticeUrl, params, Activity_Main.this);
				handler.postDelayed(this, 60*1000);
			}
		};
		handler.postDelayed(runnable_getNotice, 60*1000);
	}
	private void setClickListener(View...views){
		for(View v:views){
			v.setOnClickListener(this);
		}
	}
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.tv_notice){
			try{
			Intent intent=new Intent(this,Activity_Notice.class);
			intent.putExtra("notice_title",notice.getString("title"));
			intent.putExtra("notice_time", notice.getString("time"));
			intent.putExtra("notice_content", notice.getString("content"));
			startActivity(intent);
			}catch(JSONException e){
				e.printStackTrace();
			}
		}else if(v.getId()==R.id.btn_fast){
			Intent intent=new Intent(this,Activity_Info_Confirm.class);
			startActivity(intent);
		}else if(v.getId()==R.id.btn_batch){
//			showDialog();
			Intent intent=new Intent(this,Activity_Batch_Start.class);
			startActivity(intent);
		}else if(v.getId()==R.id.btn_info){
			Intent intent=new Intent(this,Activity_UserInfo.class);
			startActivity(intent);
		}
	}
//	private void showDialog(){
//		dialog = new Dialog(this);
//		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		dialog.setContentView(R.layout.dialog_batch);
//		
//		setClickListener(dialog.findViewById(R.id.tv_btn_cancle),dialog.findViewById(R.id.btn_saomiao),img_jijian,dialog.findViewById(R.id.lable_location1)
//				,dialog.findViewById(R.id.lable_type),dialog.findViewById(R.id.tv_btn_sub));
//		dialog.show();
//	}
	@Override
	public void onResponse(JSONObject response) {
		System.out.println(response);
		try {
			if(response.getJSONObject("result").getBoolean("success")){
				if(response.has("messageInfo")){
					notice=response.getJSONObject("messageInfo");
					tv_notice.setText(notice.getString("title"));
					layout_notice.setVisibility(View.VISIBLE);
					SharedPreferencesUtil.save("notice_title", notice.getString("title"), Activity_Main.this);
					SharedPreferencesUtil.save("notice_content", notice.getString("content"), Activity_Main.this);
					SharedPreferencesUtil.save("notice_time", notice.getString("time"), Activity_Main.this);
				}else{
					String notice_title=SharedPreferencesUtil.getStringByKey("notice_title", Activity_Main.this);
					if(notice_title!=null){
						tv_notice.setText(notice_title);
						layout_notice.setVisibility(View.VISIBLE);
						notice=new JSONObject();
						notice.put("title", notice_title);
						notice.put("content", SharedPreferencesUtil.getStringByKey("notice_content", Activity_Main.this));
						notice.put("time", SharedPreferencesUtil.getStringByKey("notice_time", Activity_Main.this));
					}else{
						layout_notice.setVisibility(View.GONE);
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onErrorResponse(VolleyError error) {
		error.printStackTrace();
		
	}
	@Override
	protected  void onDestroy(){
		runnable_getNotice=null;
		super.onDestroy();
		
	}
	
}
