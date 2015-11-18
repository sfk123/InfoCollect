package com.example.infocollect;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Activity_Notice extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);
		TextView tv_title=(TextView)findViewById(R.id.tv_title);
		tv_title.setText("¹«¸æ");
		findViewById(R.id.btn_back).setOnClickListener(this);
		
		TextView tv_notice_title=(TextView)findViewById(R.id.tv_notice_title);
		tv_notice_title.setText(getIntent().getStringExtra("notice_title"));
		TextView tv_notice_content=(TextView)findViewById(R.id.tv_notice_content);
		tv_notice_content.setText(getIntent().getStringExtra("notice_content"));
		TextView tv_notice_time=(TextView)findViewById(R.id.tv_notice_time);
		tv_notice_time.setText(getIntent().getStringExtra("notice_time"));
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btn_back){
			finish();
		}
	}
}
