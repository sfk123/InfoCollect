package com.example.infocollect;

import com.example.infocollect.model.UserInfo;
import com.example.infocollect.util.MyUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class Activity_UserInfo extends Activity implements OnClickListener {

	private ImageView img_photo;
	private TextView tv_company,tv_wangdain,tv_address,tv_phone,tv_name,tv_IDcard;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		TextView tv_title=(TextView)findViewById(R.id.tv_title);
		tv_title.setText("个人信息");
		findViewById(R.id.btn_back).setOnClickListener(this);
		
		img_photo=(ImageView)findViewById(R.id.img_photo);
		tv_company=(TextView)findViewById(R.id.tv_company);
		tv_wangdain=(TextView)findViewById(R.id.tv_wangdain);
		tv_address=(TextView)findViewById(R.id.tv_address);
		tv_phone=(TextView)findViewById(R.id.tv_phone);
		tv_name=(TextView)findViewById(R.id.tv_name);
		tv_IDcard=(TextView)findViewById(R.id.tv_IDcard);
		UserInfo user=MyApplication.getInstence().getUser();
		int photoWidth=MyUtil.dip2px(this, 80);
		img_photo.setImageBitmap(MyUtil.resizeBitmap(user.getPhoto(), photoWidth, photoWidth));
		tv_company.setText(user.getCompany());
		tv_name.setText(user.getName());
		tv_IDcard.setText(user.getIdentityCardId());
		tv_wangdain.setText(user.getBranch());
		tv_address.setText(user.getAddress());
		tv_phone.setText(user.getPhone());
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btn_back){
			finish();
		}
	}
}
