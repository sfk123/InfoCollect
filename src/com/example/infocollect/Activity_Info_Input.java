package com.example.infocollect;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/*
 * 填写收件方信息
 * 
 */
public class Activity_Info_Input extends Activity implements OnClickListener{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TextView tv_title=(TextView)findViewById(R.id.tv_title);
		tv_title.setText("收件方信息");
		findViewById(R.id.btn_back).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btn_back){
			finish();
		}
	}
}
