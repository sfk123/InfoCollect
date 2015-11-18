package com.example.infocollect;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.infocollect.model.Info_shoujian;
import com.example.infocollect.popup.Select_City;
import com.example.infocollect.popup.Select_City.cityselectListener;
import com.example.infocollect.popup.Select_Type.SelectListener;
import com.example.infocollect.popup.Select_Type;
import com.example.infocollect.view.ClearEditText;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Activity_Input_Shou extends Activity implements OnClickListener,cityselectListener{

	private JSONObject address;
	private ClearEditText edt_name_shou,edt_phone_shou,
						edt_address_detail_shou;//收件人具体位置
	private TextView tv_type;
	private TextView tv_location_shou;
	private Select_City selectCity;
	private Select_Type selectTpye;
	private int code;//快件号
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_shou);
		TextView tv_title=(TextView)findViewById(R.id.tv_title);
		code=getIntent().getIntExtra("code",0);
		tv_title.setText(code+"");
		findViewById(R.id.btn_back).setOnClickListener(this);
		
		tv_location_shou=(TextView)findViewById(R.id.tv_location_shou);
		edt_address_detail_shou=(ClearEditText)findViewById(R.id.edt_address_detail_shou);
		String temp=getIntent().getStringExtra("address");
		try{
			if(temp!=null&&!temp.equals("")){
				address=new JSONObject(temp);
				tv_location_shou.setText(address.getString("provence")+" "+address.getString("city")+" "+address.getString("distirct"));
			}else
				address=new JSONObject();
		}catch(JSONException e){
			e.printStackTrace();
		}
		edt_name_shou=(ClearEditText)findViewById(R.id.edt_name_shou);
		edt_name_shou.setText(getIntent().getStringExtra("name"));
		edt_phone_shou=(ClearEditText)findViewById(R.id.edt_phone_shou);
		edt_phone_shou.setText(getIntent().getStringExtra("phone"));
		
		tv_type=(TextView)findViewById(R.id.tv_type);
		String type=getIntent().getStringExtra("type");
		if(type!=null)
			tv_type.setText(type);
		
		selectCity=new Select_City(this);
		selectCity.setListener(this);
		selectTpye=new Select_Type(this);
		selectTpye.setSelectListener(selectlistener);
		findViewById(R.id.lable_location2).setOnClickListener(this);
		findViewById(R.id.lable_type).setOnClickListener(this);
		findViewById(R.id.tv_btn_sub).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btn_back){
			finish();
		}else if(v.getId()==R.id.lable_location2){
			selectCity.builder().show();
		}else if(v.getId()==R.id.lable_type){
			selectTpye.builder().show();
		}else if(v.getId()==R.id.tv_btn_sub){
			Info_shoujian shoujian=new Info_shoujian();
			shoujian.setCode(code);
			shoujian.setAddress(address.toString());
			shoujian.setName(edt_name_shou.getText().toString());
			shoujian.setPhone(edt_phone_shou.getText().toString());
			shoujian.setType(tv_type.getText().toString());
			shoujian.setAddress_detail(edt_address_detail_shou.getText().toString());
			Activity_Batch.getInstence().setShou(shoujian);
			finish();
		}
	}
	private SelectListener selectlistener=new SelectListener() {
		
		@Override
		public void select_ok(String s) {
			tv_type.setText(s);
		}
	};
	@Override
	public void selectok(JSONObject json) {
		address=json;
		try {
			tv_location_shou.setText(address.getString("provence")+" "+address.getString("city")+" "+address.getString("distirct"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
