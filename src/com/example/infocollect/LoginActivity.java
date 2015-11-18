package com.example.infocollect;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.example.infocollect.model.UserInfo;
import com.example.infocollect.util.MyContext;
import com.example.infocollect.util.MyHttp;
import com.example.infocollect.util.MyHttp.MyHttpCallBack;
import com.example.infocollect.util.MyUtil;
import com.example.infocollect.view.ClearEditText;
import com.example.infocollect.view.LoadingDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener,MyHttpCallBack{
	private ClearEditText tv_name,tv_password;
	private Button btn_login;
	private MyHttp http;
	private ImageView checkbox;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		tv_name=(ClearEditText)findViewById(R.id.username);
		tv_password=(ClearEditText)findViewById(R.id.password);
		btn_login=(Button)findViewById(R.id.login);
		btn_login.setOnClickListener(this);
		checkbox=(ImageView)findViewById(R.id.checkbox);
		checkbox.setOnClickListener(this);
		http=new MyHttp(this);
	}
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.login){
			String username=tv_name.getText().toString();
			String password=tv_password.getText().toString();
			if(username.equals("")){
				Toast.makeText(this, "请输入用户名！", Toast.LENGTH_LONG).show();
				return;
			}else if(password.equals("")){
				Toast.makeText(this, "请输入密码！", Toast.LENGTH_LONG).show();
				return;
			}else{
				LoadingDialog.showQuitWindow(this);
				Map<String, String> params=new HashMap<String, String>();
				params.put("identityCardId", username);
				params.put("pwd", password);
				String loginUrl=MyContext.getUrl_Post(MyContext.httpInterface_Login);
				System.out.println(loginUrl);
				http.Http_post(loginUrl, params, this);

			}
		}else if(v.getId()==R.id.checkbox){
			if(v.getTag().toString().equals("true")){
				v.setTag("false");
				checkbox.setImageResource(R.drawable.check_bg);
			}else{
				v.setTag("true");
				checkbox.setImageResource(R.drawable.check_bg_checked);
			}
		}
	}
	@Override
	public void onResponse(JSONObject response) {
		if(LoadingDialog.isShowing()){
			LoadingDialog.dismiss();
		}
		System.out.println(response);
		try {
			if(response.getJSONObject("result").getBoolean("success")){
				UserInfo user=new UserInfo();
				JSONObject loginedCourierInfo=response.getJSONObject("loginedCourierInfo");
				user.setToken(loginedCourierInfo.getString("token"));
				JSONObject courier=loginedCourierInfo.getJSONObject("courier");
				user.setId(courier.getString("id"));
				user.setIdentityCardId(courier.getString("identityCardId"));
				user.setName(courier.getString("name"));
				user.setSex(courier.getString("sex"));
				user.setBranch(courier.getString("branch"));
				user.setPhone(courier.getString("phone"));
				user.setAddress(courier.getString("address"));
				user.setCompany(courier.getJSONObject("company").getString("name"));
				String photo_base64=courier.getString("photo");
				user.setPhoto(MyUtil.base64ToBitmap(photo_base64));
				MyApplication.getInstence().setUser(user);
				Intent intent=new Intent(this,Activity_Main.class);
				startActivity(intent);
				finish();
			}else{
				MyUtil.alert("身份证号或密码错误！", this);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void onErrorResponse(VolleyError error) {
		if(LoadingDialog.isShowing()){
			LoadingDialog.dismiss();
		}
		MyUtil.alert("请检查网络后重试！", this);
	}

	
}
