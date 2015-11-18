package com.example.infocollect;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.baidu.mapapi.model.LatLng;
import com.example.infocollect.popup.Select_City;
import com.example.infocollect.popup.Select_City.cityselectListener;
import com.example.infocollect.popup.Select_Type;
import com.example.infocollect.popup.Select_Type.SelectListener;
import com.example.infocollect.util.MyContext;
import com.example.infocollect.util.MyHttp;
import com.example.infocollect.util.MyUtil;
import com.example.infocollect.util.MyHttp.MyHttpCallBack;
import com.example.infocollect.view.ClearEditText;
import com.example.infocollect.view.LoadingDialog;

import cn.hugo.android.scanner.CaptureActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * 信息确认页面
 * 
 */
public class Activity_Info_Confirm extends Activity implements OnClickListener,cityselectListener,MyHttpCallBack{
	
	public final static int request_pushCode=1;//快件单号扫描
	public final static int request_caramaCode=2;
	public final static int request_selectLocation=3;
	private boolean saomiao_default=true;
	private boolean paizhao_default=true;
	private String cacheUr;//拍照缓存路径
	private int[] windowSize;
	
	private ClearEditText edt_code;//快件单号
	private ClearEditText edt_phone_ji;//寄件人电话
	private ClearEditText edt_name_ji;//寄件人姓名
	private ClearEditText edt_cardnum_ji;//寄件人身份证号
	private ImageView img_jijian;//寄件人身份证
	private TextView tv_location;//寄件人位置
	private LatLng location;//寄件人坐标
	private ClearEditText edt_address_detail_ji;//寄件人具体位置
	private ClearEditText edt_address_detail_shou;//收件人具体位置
	private ClearEditText edt_name_shou,edt_phone_shou;
	private TextView tv_location_shou,btn_saomiao,tv_type;
	private Select_City selectCity;
	private Select_Type selectTpye;
	
	private String receiveDistrictid;//收件人地区ID；
	private MyHttp http;
	private Handler handler=new Handler(){
		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg){
			switch (msg.what) {   
            case 1:   
            	PostHttp(msg.obj.toString());
                 break;  
            case 2:
            	MyUtil.alert(msg.obj.toString(), Activity_Info_Confirm.this);
            	break;
       }   
       super.handleMessage(msg);  
		}
	};
	private void PostHttp(String bitmap_str){
		Map<String, String> params=new HashMap<>();
		params.put("token", MyApplication.getInstence().getUser().getToken());
		params.put("courierIdentityCardId", MyApplication.getInstence().getUser().getIdentityCardId());
		params.put("expressNo", edt_code.getText().toString());
		params.put("materialType", tv_type.getText().toString());
		params.put("postPersonName", edt_name_ji.getText().toString());
		params.put("postPersonPhone", edt_phone_ji.getText().toString());
		params.put("postPersonIdentityCardId", edt_cardnum_ji.getText().toString());
		params.put("postPersonIdentityCard", bitmap_str);
		params.put("receiveDistrictid", receiveDistrictid);
		params.put("receiveAddr", tv_location_shou.getText().toString()+" "+edt_address_detail_shou.getText().toString());
		params.put("receivePersonName", edt_name_shou.getText().toString());
		params.put("receivePersonPhone", edt_phone_shou.getText().toString());
		params.put("takeAddr", tv_location.getText().toString()+" "+edt_address_detail_ji.getText().toString());
		params.put("takeTime",new Date().getTime()+"");
		String loginUrl=MyContext.getUrl_Post(MyContext.httpInterface_upload);
		System.out.println(loginUrl);
		http.Http_post(loginUrl, params, this);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_confirm);
		TextView tv_title=(TextView)findViewById(R.id.tv_title);
		tv_title.setText("信息采集");
		findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.lable_location1).setOnClickListener(this);
		findViewById(R.id.lable_location2).setOnClickListener(this);
		findViewById(R.id.lable_type).setOnClickListener(this);
		
		btn_saomiao=(TextView)findViewById(R.id.btn_saomiao);
		btn_saomiao.setOnClickListener(this);
		edt_code=(ClearEditText)findViewById(R.id.edt_code);
		img_jijian=(ImageView)findViewById(R.id.img_jijian);
		img_jijian.setOnClickListener(this);
		tv_location=(TextView)findViewById(R.id.tv_location);
		tv_location_shou=(TextView)findViewById(R.id.tv_location_shou);
		tv_type=(TextView)findViewById(R.id.tv_type);
		edt_phone_ji=(ClearEditText)findViewById(R.id.edt_phone_ji);
		edt_name_ji=(ClearEditText)findViewById(R.id.edt_name_ji);
		edt_cardnum_ji=(ClearEditText)findViewById(R.id.edt_cardnum_ji);
		edt_address_detail_ji=(ClearEditText)findViewById(R.id.edt_address_detail_ji);
		edt_address_detail_shou=(ClearEditText)findViewById(R.id.edt_address_detail_shou);
		edt_name_shou=(ClearEditText)findViewById(R.id.edt_name_shou);
		edt_phone_shou=(ClearEditText)findViewById(R.id.edt_phone_shou);
		
		windowSize=MyUtil.getWindowSize(this);
		selectCity=new Select_City(this);
		selectCity.setListener(this);
		selectTpye=new Select_Type(this);
		selectTpye.setSelectListener(selectlistener);
		findViewById(R.id.tv_btn_sub).setOnClickListener(this);
		http=new MyHttp(this);
		
		Intent intent=new Intent(this,CaptureActivity.class);
		startActivityForResult(intent, request_pushCode);
	}
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	   if(resultCode==RESULT_OK){
		   if(requestCode==request_pushCode){
			   edt_code.setText(data.getStringExtra("scanStr"));
			   if(saomiao_default)
			   startPaiZhao();  
		   }else if(requestCode==request_caramaCode){
//			   BitmapFactory.decodeFile(cacheUr)
			   img_jijian.setImageBitmap(MyUtil.convertToBitmap(cacheUr, windowSize[0]-MyUtil.dip2px(this, 20)));
			   if(paizhao_default){
			   Intent intent=new Intent(this,Activity_SelectLocation.class);
			   startActivityForResult(intent, request_selectLocation);
			   }
		   }else if(requestCode==request_selectLocation){
			   tv_location.setText(data.getStringExtra("address"));
			   location=new LatLng(data.getDoubleExtra("lat", 0), data.getDoubleExtra("latlong", 0));
		   }
	   }
	} 
	private void startPaiZhao(){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
		Date date=new Date();
		cacheUr=date.getTime()+"";
		Uri imageUri = Uri.fromFile(new File(MyUtil.getSDPath(this)+"/imageloader/Cache",cacheUr+".png"));
		cacheUr=MyUtil.getSDPath(this)+"/imageloader/Cache/"+cacheUr+".png";
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, request_caramaCode);  
	}
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btn_back){
			finish();
		}else if(v.getId()==R.id.lable_location2){
			selectCity.builder().show();
		}else if(v.getId()==R.id.lable_location1){
			Intent intent=new Intent(this,Activity_SelectLocation.class);
			startActivityForResult(intent, request_selectLocation);
		}else if(v.getId()==R.id.img_jijian){
			paizhao_default=false;
			startPaiZhao();
		}else if(v.getId()==R.id.btn_saomiao){
			saomiao_default=false;
			Intent intent=new Intent(this,CaptureActivity.class);
			startActivityForResult(intent, request_pushCode);
		}else if(v.getId()==R.id.lable_type){
			selectTpye.builder().show();
		}else if(v.getId()==R.id.tv_btn_sub){
			if(edt_code.getText().toString().trim().equals("")){
				MyUtil.alert("请扫描或输入快递单号", this);
			}else if(edt_phone_ji.getText().toString().trim().equals("")){
				MyUtil.alert("请输入寄件人电话", this);
			}else if(cacheUr==null){
				MyUtil.alert("请拍取寄件人证件", this);
			}else if(tv_location_shou.getText().toString().trim().equals("")){
				MyUtil.alert("请选择收件人地址", this);
			}else if(tv_type.getText().toString().equals("")){
				MyUtil.alert("请选择物品类别", this);
			}else{
		    LoadingDialog.showWindow(this);
		    Thread thread=new Thread(new Runnable() {
				
				@Override
				public void run() {
					Bitmap bitmap = null;  
				    try  
				    {  
				        File file = new File(cacheUr);  
				        if(file.exists())  
				        {  
				            bitmap = BitmapFactory.decodeFile(cacheUr);  
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
	}
	@Override
	public void selectok(JSONObject json) {
//		System.out.println(json);
		try {
			tv_location_shou.setText(json.getString("provence")+"-"+json.getString("city")+"-"+json.getString("distirct"));
			receiveDistrictid=json.getString("distirctid");
			System.out.println("城市ID："+receiveDistrictid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private SelectListener selectlistener=new SelectListener() {
		
		@Override
		public void select_ok(String s) {
			tv_type.setText(s);
		}
	};
	@Override
	public void onResponse(JSONObject response) {
		if(LoadingDialog.isShowing()){
			LoadingDialog.dismiss();
		}
		System.out.println(response);
		try {
			if(response.getBoolean("success")){
				
				 try  
				    {  
				        File file = new File(cacheUr);  
				        if(file.exists())  
				        {  
				           if(file.delete())
				           Log.i("cmd", "删除照片");
				           else
				        	   Log.e("cmd", "删除照片失败"); 
				        }
				    } catch (Exception e)  
				    {  
				    	e.printStackTrace();
				    	 Log.e("cmd", "照片文件不存在"); 
				    	return;
				    }  
				 new AlertDialog.Builder(this)  
	                .setTitle("系统提示")  
	                .setMessage("信息上传成功！")  
	                .setPositiveButton(  
	                        "确定",  
	                        new DialogInterface.OnClickListener() {  

	                            public void onClick(  
	                                    DialogInterface dialog,  
	                                    int which) {  
	                              finish();
	                            }  
	                        }) .show();  
			}else{
				MyUtil.alert(response.getString("resultMsg"), this);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onErrorResponse(VolleyError error) {
		error.printStackTrace();
		if(LoadingDialog.isShowing()){
			LoadingDialog.dismiss();
		}
		MyUtil.alert("请检查网络后重试！", this);
	}
	@Override
	protected  void onDestroy(){
		super.onDestroy();
		if(cacheUr!=null){
		File file = new File(cacheUr);  
        if(file.exists())  
        {  
           if(file.delete())
           Log.i("cmd", "删除照片");
           else
        	   Log.e("cmd", "删除照片失败"); 
        }  
		}
	}
}
