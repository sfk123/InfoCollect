package com.example.infocollect;

import java.io.File;
import java.util.Date;

import cn.hugo.android.scanner.CaptureActivity;

import com.baidu.mapapi.model.LatLng;
import com.example.infocollect.util.MyUtil;
import com.example.infocollect.view.ClearEditText;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class Activity_Batch_Start extends Activity implements OnClickListener{
	private ClearEditText edt_code,edt_num,
							edt_phone_ji,//寄件人电话
							edt_name_ji,//寄件人姓名
							edt_cardnum_ji,//寄件人身份证号
							edt_address_detail_ji;//寄件人具体位置
	private ImageView img_jijian;
	private TextView tv_notice,tv_location,tv_type;
	public final static int request_pushCode=1;
	public final static int request_caramaCode=2;
	public final static int request_selectLocation=3;
	private int[] windowSize;
	private LatLng location;
	private String cacheUr;
	private boolean photoOk=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_batch);
		TextView tv_title=(TextView)findViewById(R.id.tv_title);
		tv_title.setText("批量采集");
		findViewById(R.id.btn_back).setOnClickListener(this);
		windowSize=MyUtil.getWindowSize(this);
		
		edt_code=(ClearEditText)findViewById(R.id.edt_code);
		img_jijian=(ImageView)findViewById(R.id.img_jijian);
		tv_location=(TextView)findViewById(R.id.tv_location);
		tv_type=(TextView)findViewById(R.id.tv_type);
		edt_num=(ClearEditText)findViewById(R.id.edt_num);
		edt_phone_ji=(ClearEditText)findViewById(R.id.edt_phone_ji);
		edt_name_ji=(ClearEditText)findViewById(R.id.edt_name_ji);
		edt_cardnum_ji=(ClearEditText)findViewById(R.id.edt_cardnum_ji);
		edt_address_detail_ji=(ClearEditText)findViewById(R.id.edt_address_detail_ji);
		
		setClickListener(findViewById(R.id.btn_saomiao),img_jijian,findViewById(R.id.lable_location1)
				,findViewById(R.id.lable_type),findViewById(R.id.tv_btn_sub));
	}
	private void setClickListener(View...views){
		for(View v:views){
			v.setOnClickListener(this);
		}
	}
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btn_back){
			choseBack();
		}else if(v.getId()==R.id.img_jijian){
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
			Date date=new Date();
			cacheUr=date.getTime()+"";
			Uri imageUri = Uri.fromFile(new File(MyUtil.getSDPath(this)+"/imageloader/Cache",cacheUr+".png"));
			cacheUr=MyUtil.getSDPath(this)+"/imageloader/Cache/"+cacheUr+".png";
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
	        startActivityForResult(intent, request_caramaCode);  
		}else if(v.getId()==R.id.lable_location1){
			Intent intent=new Intent(this,Activity_SelectLocation.class);
			   startActivityForResult(intent, request_selectLocation);
		}else if(v.getId()==R.id.lable_type){
			 final String[] items = getResources().getStringArray(  
                     R.array.types);  
             new AlertDialog.Builder(this)  
                     .setTitle("请点击选择")  
                     .setItems(items, new DialogInterface.OnClickListener() {  

                         public void onClick(DialogInterface dialog,  
                                 int which) {  
                        	 tv_type.setText(items[which]); 
                         }  
                     }).show(); 
		}else if(v.getId()==R.id.tv_btn_sub){
			if(edt_code.getText().toString().trim().equals("")){
				MyUtil.alert("请输入或扫描起始单号", this);
			}else if(edt_num.getText().toString().trim().equals("")){
				MyUtil.alert("请输入快件数量", this);
			}else if(edt_phone_ji.getText().toString().trim().equals("")){
				MyUtil.alert("请输入寄件人电话", this);
			}else if(!photoOk){
				MyUtil.alert("请拍取寄件人证件", this);
			}else if(edt_name_ji.getText().toString().trim().equals("")){
				MyUtil.alert("请输入寄件人姓名", this);
			}else if(edt_cardnum_ji.getText().toString().trim().equals("")){
				MyUtil.alert("请输入寄件人身份证号", this);
			}else if(location==null){
				MyUtil.alert("请选择寄件人位置", this);
			}else if(edt_address_detail_ji.getText().toString().trim().equals("")){
				MyUtil.alert("请输入寄件人具体位置", this);
			}else if(tv_type.getText().toString().trim().equals("")){
				MyUtil.alert("请选择物品类别", this);
			}else{
				int start=0;
				int count=0;
				try{
					start=Integer.parseInt(edt_code.getText().toString().trim());
				}catch(Exception e){
					MyUtil.alert("起始单号输入错误！", this);
					return;
				}
				try{
					count=Integer.parseInt(edt_num.getText().toString().trim());
				}catch(Exception e){
					MyUtil.alert("快件数量输入错误！", this);
					return;
				}
				Intent intent=new Intent(this,Activity_Batch.class);
				intent.putExtra("address", tv_location.getText().toString());
				intent.putExtra("count", count);
				intent.putExtra("phone", edt_phone_ji.getText().toString().trim());
				intent.putExtra("name", edt_name_ji.getText().toString().trim());
				intent.putExtra("card_num", edt_cardnum_ji.getText().toString().trim());
				intent.putExtra("address_detail", edt_address_detail_ji.getText().toString().trim());
				intent.putExtra("startcode", start);
				intent.putExtra("image", cacheUr);
				intent.putExtra("lat", location.latitude);
				intent.putExtra("lng", location.longitude);
				intent.putExtra("type", tv_type.getText().toString().trim());
				startActivity(intent);
			}
		}else if(v.getId()==R.id.btn_saomiao){
			Intent intent=new Intent(this,CaptureActivity.class);
			startActivityForResult(intent, request_pushCode);
		}
	}
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	   if(resultCode==RESULT_OK){
		   if(requestCode==request_pushCode){
			   edt_code.setText(data.getStringExtra("scanStr"));
		   }else if(requestCode==request_caramaCode){
			   
//			   Matrix matrix = new Matrix();  
//	            matrix.reset();  
//	            matrix.setRotate(270);  
//	           Bitmap tmpBitmap=MyUtil.convertToBitmap(cacheUr, windowSize[0]-MyUtil.dip2px(this, 20));  
//	           tmpBitmap = Bitmap.createBitmap(tmpBitmap,0,0, tmpBitmap.getWidth(), tmpBitmap.getHeight(),matrix, true); 
			   img_jijian.setImageBitmap(MyUtil.convertToBitmap(cacheUr, windowSize[0]-MyUtil.dip2px(this, 20)));
			  
			   photoOk=true;
			   System.gc();
		   }else if(requestCode==request_selectLocation){
			   tv_location.setText(data.getStringExtra("address"));
			   location=new LatLng(data.getDoubleExtra("lat", 0), data.getDoubleExtra("latlong", 0));
		   }
	   }
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
	private void choseBack(){
		 Dialog alertDialog = new AlertDialog.Builder(this). 
	                setTitle("系统提示"). 
	                setMessage("您确定要退出批量采集吗？"). 
	                setIcon(R.drawable.ic_launcher). 
	                setPositiveButton("确定", new DialogInterface.OnClickListener() { 
	                     
	                    @Override 
	                    public void onClick(DialogInterface dialog, int which) { 
	                        finish(); 
	                    } 
	                }). 
	                setNegativeButton("取消", new DialogInterface.OnClickListener() { 
	                     
	                    @Override 
	                    public void onClick(DialogInterface dialog, int which) { 
	                        // TODO Auto-generated method stub  
	                    } 
	                }). 
	                create(); 
	        alertDialog.show(); 
	}
	@Override 
    public void onBackPressed() { 
//        super.onBackPressed(); 
		choseBack();        
    }  
}
