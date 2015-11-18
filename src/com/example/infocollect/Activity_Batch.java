package com.example.infocollect;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.baidu.mapapi.model.LatLng;
import com.example.infocollect.adapter.Adapter_Batch;
import com.example.infocollect.model.InfoModel_Batch;
import com.example.infocollect.model.Info_shoujian;
import com.example.infocollect.util.MyContext;
import com.example.infocollect.util.MyHttp;
import com.example.infocollect.util.MyUtil;
import com.example.infocollect.util.MyHttp.MyHttpCallBack;
import com.example.infocollect.view.LoadingDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class Activity_Batch extends Activity implements OnClickListener,OnItemClickListener,MyHttpCallBack{

	private ListView mylist;
	private InfoModel_Batch infodata;
	private Adapter_Batch adapter;
	private List<Info_shoujian> shou;
	private static Activity_Batch instence;
	public static Activity_Batch getInstence(){
		return instence;
	}
	@SuppressLint("HandlerLeak")
	private Handler handler=new Handler(){
		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg){
			switch (msg.what) {   
            case 1:   
            	PostHttp(msg.obj.toString());
                 break;  
            case 2:
            	MyUtil.alert(msg.obj.toString(), Activity_Batch.this);
            	break;
       }   
       super.handleMessage(msg);  
		}
	};
	private void PostHttp(String bitmap_str){
		Map<String, String> params=new HashMap<>();
		params.put("token", MyApplication.getInstence().getUser().getToken());
		params.put("courierIdentityCardId", MyApplication.getInstence().getUser().getIdentityCardId());
		params.put("postPersonName", infodata.getName());
		params.put("postPersonPhone", infodata.getPhone());
		params.put("postPersonIdentityCardId", infodata.getCard_num());
		params.put("postPersonIdentityCard", bitmap_str);
		params.put("takeAddr", infodata.getAddress_1()+" "+infodata.getAddress_detail());
		params.put("takeTime", new Date().getTime()+"");
		params.put("postPersonIdentityCard", bitmap_str);
		JSONArray array=new JSONArray();
		try{
		for(Info_shoujian inf_s:shou){
			JSONObject json=new JSONObject();
			json.put("expressNo", inf_s.getCode());
			json.put("materialType", inf_s.getType());
			if(inf_s.getAddress()!=null){
				JSONObject address=new JSONObject(inf_s.getAddress());
				json.put("receiveDistrictid", address.getString("distirctid"));
				json.put("receiveAddr",address.getString("provence")+" "+address.getString("city")+" "+address.getString("distirct")+" "+
						inf_s.getAddress_detail());
			}
			json.put("receivePersonName", inf_s.getName());
			json.put("receivePersonPhone", inf_s.getPhone());
			array.put(json);
		}
		params.put("receiveInfos", array.toString());
		MyHttp http=new MyHttp(this);
		String url=MyContext.getUrl_Post(MyContext.httpInterface_upload_batch);
		System.out.println(url);
		http.Http_post(url, params, this);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_batch);
		TextView tv_title=(TextView)findViewById(R.id.tv_title);
		tv_title.setText("批量采集");
		findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.tv_btn).setOnClickListener(this);
		mylist=(ListView)findViewById(R.id.mylist);
		infodata=new InfoModel_Batch();
		infodata.setAddress_1(getIntent().getStringExtra("address"));
		infodata.setCount(getIntent().getIntExtra("count", 0));
		infodata.setStartCode(getIntent().getIntExtra("startcode", 0));
		infodata.setImgurl(getIntent().getStringExtra("image"));
		LatLng latlng=new LatLng(getIntent().getDoubleExtra("lat", 0), getIntent().getDoubleExtra("lng", 0));
		infodata.setLocation_1(latlng);
		infodata.setPhone(getIntent().getStringExtra("phone"));
		infodata.setName(getIntent().getStringExtra("name"));
		infodata.setCard_num(getIntent().getStringExtra("card_num"));
		infodata.setAddress_detail(getIntent().getStringExtra("address_detail"));
		String type=getIntent().getStringExtra("type");
		shou=new ArrayList<Info_shoujian>();
		for(int i=0;i<infodata.getCount();i++){
			Info_shoujian info=new Info_shoujian();
			info.setType(type);
			info.setCode(infodata.getStartCode()+i);
			shou.add(info);
		}
		infodata.setList_shou(shou);
		adapter=new Adapter_Batch(this, infodata);
		mylist.setAdapter(adapter);
		mylist.setOnItemClickListener(this);
		instence=this;
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btn_back){
			confirmExit();
		}else if(v.getId()==R.id.tv_btn){
			LoadingDialog.showWindow(this);
		    Thread thread=new Thread(new Runnable() {
				
				@Override
				public void run() {
					Bitmap bitmap = null;  
				    try  
				    {  
				        File file = new File(infodata.getImgurl());  
				        if(file.exists())  
				        {  
				            bitmap = BitmapFactory.decodeFile(infodata.getImgurl());  
				            bitmap=MyUtil.compressImage(bitmap);
				            System.gc();
							String bitmap_str=MyUtil.bitmapToBase64(bitmap);
							bitmap.recycle();
//							System.out.println("压缩完成："+bitmap_str.length());
							Message msg=new Message();
							msg.what=1;
							msg.obj=bitmap_str;
							handler.sendMessage(msg);
				        }  
				    } catch (Exception e)  
				    {  
				    	e.printStackTrace();
				    	Message msg=new Message();
						msg.what=2;
						msg.obj="照片不存在，请重新拍取寄件人身份证";
						handler.sendMessage(msg);
				    	
				    }  
				}
			});
		    thread.start();
			
		}
	}
	private void confirmExit(){
		new AlertDialog.Builder(this)  
                .setTitle("系统提示")  
                .setMessage("确认要退出批量采集？")  
                .setPositiveButton(  
                        "确定",  
                        new DialogInterface.OnClickListener() {  

                            public void onClick(  
                                    DialogInterface dialog,  
                                    int which) {  
                              finish();
                            }  
                        })  
                .setNegativeButton(  
                        "取消",  
                        new DialogInterface.OnClickListener() {  

                            public void onClick(  
                                    DialogInterface dialog,  
                                    int which) {  
                                // 这里点击取消之后可以进行的操作   
                            }  
                        }).show();  
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 
        if (keyCode == KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0) {
        	confirmExit();
              return true;
          }
          return super.onKeyDown(keyCode, event);
      }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Info_shoujian shoujian=shou.get(position);
		Intent intent=new Intent(this,Activity_Input_Shou.class);
		intent.putExtra("code", shoujian.getCode());
		intent.putExtra("address", shoujian.getAddress());
		intent.putExtra("name", shoujian.getName());
		intent.putExtra("phone", shoujian.getPhone());
		intent.putExtra("type", shoujian.getType());
		intent.putExtra("address_detail", shoujian.getAddress_detail());
		startActivity(intent);
	}
	public void setShou(Info_shoujian shoujian){
		int i=0;
		for(Info_shoujian s:shou){
			if(s.getCode()==shoujian.getCode()){
				shou.set(i, shoujian);
				break;
			}
			i++;
		}
	}
	@Override
	public void onResponse(JSONObject response) {
		if(LoadingDialog.isShowing()){
			LoadingDialog.dismiss();
		}
		try{
			if(response.getBoolean("success")){
				MyUtil.alert("上传成功！", this);
				 try  
				    {  
				        File file = new File(infodata.getImgurl());  
				        if(file.exists())  
				        {  
				           if(file.delete())
				           Log.i("cmd", "删除照片");
				           else
				        	   Log.e("cmd", "删除照片失败"); 
				        }  
				        finish();
				    } catch (Exception e)  
				    {  
				    	e.printStackTrace();
				    	 Log.e("cmd", "照片文件不存在"); 
				    	return;
				    }  
			}else{
				MyUtil.alert(response.getString("resultMsg"), this);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void onErrorResponse(VolleyError error) {
		if(LoadingDialog.isShowing()){
			LoadingDialog.dismiss();
		}
		error.printStackTrace();
		MyUtil.alert("请检查网络后重试！", this);
	}
}
