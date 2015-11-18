package com.example.infocollect;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.example.infocollect.adapter.Adapter_SelectLocation;
import com.example.infocollect.adapter.Adapter_SelectLocation.ViewHolder;
import com.example.infocollect.util.MyHttp.MyHttpCallBack;
import com.example.infocollect.util.MyUtil;

public class Activity_SelectLocation  extends Activity implements OnClickListener,OnItemClickListener,MyHttpCallBack{
	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();
	private MapView bmapView;
	private BaiduMap mBaiduMap;
	private boolean isFirstLoc=true;
	private RelativeLayout lable_input,lable_title;
	private LinearLayout lable_select;

	private ListView mylist,mylist_select_pop;
	private Adapter_SelectLocation adapter,adapter_pop;
	private GeoCoder coder ;//检索周边
	private String current_location;//当前选择的位置
	private String current_city;//当前城市名称
	private LatLng current_latlng;
	private PoiSearch mPoiSearch;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_location);
		current_location=getIntent().getStringExtra("current_location");
		TextView title=(TextView)findViewById(R.id.tv_title);
		ImageView btn_back=(ImageView)findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		mylist=(ListView)findViewById(R.id.mylist);
		mylist.setOnItemClickListener(this);
		

		coder = GeoCoder.newInstance();
        coder.setOnGetGeoCodeResultListener(GeoListener);
		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
		initLocation();
		mLocationClient.registerLocationListener( myListener );    //注册监听函数
		bmapView=(MapView)findViewById(R.id.bmapView);
		mBaiduMap = bmapView.getMap();
		
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15.0f));
		mBaiduMap.setOnMapClickListener(map_click_listener);
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
	   
	}
	private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }
	
	
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btn_back){
			finish();
		}
	}
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        bmapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        bmapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        bmapView.onPause();  
        }  
	class MyLocationListener implements BDLocationListener {
		 
        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
 
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
//            sb.append("\nlocationdescribe : ");
//                sb.append(location.getLocationDescribe());// 位置语义化信息
//                @SuppressWarnings("unchecked")
//				List<Poi> list = location.getPoiList();// POI数据
//                if (list != null) {
//                    sb.append("\npoilist size = : ");
//                    sb.append(list.size());
//                    for (Poi p : list) {
//                        sb.append("\npoi= : ");
//                        sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
//                        
//                    }
//                }
//            Log.i("BaiduLocationApiDem", sb.toString());
         // map view 销毁后不在处理新接收的位置
            if (location == null || bmapView == null) {
                return;
            }
            current_city=location.getCity();
            ReverseGeoCodeOption option = new ReverseGeoCodeOption();
            current_latlng=new LatLng(location.getLatitude(), location.getLongitude());
            option.location(current_latlng);
            coder.reverseGeoCode(option);
            popWin(new LatLng(location.getLatitude(), location.getLongitude()));
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
            mLocationClient.stop();
        }
	}
	OnGetGeoCoderResultListener GeoListener = new OnGetGeoCoderResultListener() {
		
		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			List<PoiInfo> list=result.getPoiList();
			if(list!=null){
				adapter=new Adapter_SelectLocation(Activity_SelectLocation.this, list,"");
				mylist.setAdapter(adapter);
			}else{
				System.out.println("没有获取到附近信息");
			}
		}
		
		@Override
		public void onGetGeoCodeResult(GeoCodeResult arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	OnMapClickListener map_click_listener = new OnMapClickListener() {
		/**
		 * 地图单击事件回调函数
		 * 
		 * @param point
		 *            点击的地理坐标
		 */
		public void onMapClick(LatLng point) {
			ReverseGeoCodeOption option = new ReverseGeoCodeOption();

            option.location(point);

            coder.reverseGeoCode(option);
			
			 popWin(point);
		}

		@Override
		public boolean onMapPoiClick(MapPoi arg0) {
			// TODO 自动生成的方法存根
			return false;
		}
	};
	// 自定义提示图标
	public void popWin(LatLng point) {
		// 构建Marker图标
		BitmapDescriptor bitmap= BitmapDescriptorFactory
				.fromResource(R.drawable.ico_location);
		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap);
		// 在地图上添加Marker，并显示
		mBaiduMap.clear();
		mBaiduMap.addOverlay(option);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(point);
        mBaiduMap.animateMapStatus(u);
}
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		ViewHolder holder=(ViewHolder)view.getTag();
		Adapter_SelectLocation adapter=(Adapter_SelectLocation)arg0.getAdapter();
		
		adapter.setSelect(holder);
		String address=adapter.getItem(position).address+" "+adapter.getItem(position).name;
		Bundle bundle=new Bundle();
		bundle.putString("address", address);
		bundle.putDouble("lat", adapter.getItem(position).location.latitude);
		bundle.putDouble("latlong", adapter.getItem(position).location.longitude);
		Intent intent=new Intent();
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
		finish();
		
	}
	private TextWatcher watcher = new TextWatcher() {
		   
	    @Override
	    public void onTextChanged(CharSequence s, int start, int before, int count) {
	        // TODO Auto-generated method stub
	       
	    }
	   
	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count,
	            int after) {
	        // TODO Auto-generated method stub
	       
	    }
	   
	    @Override
	    public void afterTextChanged(Editable s) {
//	    	Map<String, String> params=new HashMap<>();
//	    	params.put("query", s.toString());
//	    	params.put("page_size", "10");
//	    	params.put("page_num", "0");
//	    	params.put("region", current_city);
//	    	params.put("location", current_latlng.latitude+","+current_latlng.longitude);
//	    	params.put("radius", "2000");
//	    	params.put("key", "9f23b8e31818492b9e6da0ba5e748c67");
//	    	
//	    	http.Http_get(MyContext.GetPOIUrl("http://apis.haoservice.com/lifeservice/baiduPoi/search", params), Activity_SelectLocation.this);
	    	mPoiSearch.searchInCity((new PoiCitySearchOption())  
	    		    .city(current_city)  
	    		    .keyword(s.toString())  
	    		    .pageNum(0));
//	    	Parameters params = new Parameters();
//	    	params.add("city", current_city);
//	    	params.add("keyword", s.toString());
//	    	params.add("key", "42b3f6f4712d2bc22cc7fc2a824b5c34");
//	    	JuheData.executeWithAPI(Activity_SelectLocation.this,10, "http://apis.juhe.cn/baidu/getData", JuheData.GET, params, new DataCallBack() {
//	    		/*为请求返回数据.
//	    		 */
//	    		@Override
//	    		public void onSuccess(int statusCode, String responseString) {
//	    			// TODO Auto-generated method stub
//	    			System.out.println(responseString);
//	    		}
//
//	    		/**
//	    		 * 请求完成时调用的方法,无论成功或者失败都会调用.
//	    		 */
//	    		@Override
//	    		public void onFinish() {
//	    		    // TODO Auto-generated method stub
////	    			Toast.makeText(getApplicationContext(), "finish",Toast.LENGTH_SHORT).show();
//	    			}
//	    		/**
//	    		 * 请求失败时调用的方法,statusCode为http状态码,throwable为捕获到的异常
//	    		* statusCode:30002 没有检测到当前网络. 30003 没有进行初始化. 0
//	    		* 未明异常,具体查看Throwable信息. 其他异常请参照http状态码.
//	    		*/
//	    		@Override
//	    		public void onFailure(int statusCode,String responseString, Throwable throwable) {
//	    			// TODO Auto-generated method stub
////	    			tv.append(throwable.getMessage() + "\n");
//	    			}
//	    		
//	    	});
	    }
	};
	OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){  
	    public void onGetPoiResult(PoiResult result){  
	    //获取POI检索结果  
	    	if (result == null  
                    || result.error == PoiResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果  
                Toast.makeText(Activity_SelectLocation.this, "未找到结果",  
                        Toast.LENGTH_LONG).show();  
                return;  
            } 
	    	List<PoiInfo> list=result.getAllPoi();
//	    	for(PoiInfo info:list){
//	    		System.out.println(info.name+"<>"+info.address);
//	    	}
	    	adapter_pop=new Adapter_SelectLocation(Activity_SelectLocation.this, list,null);
	    	mylist_select_pop.setAdapter(adapter_pop);
	    }  
	    public void onGetPoiDetailResult(PoiDetailResult result){  
	    //获取Place详情页检索结果  
	    }  
	};
	@Override
	public void onResponse(JSONObject response) {
		System.out.println(response);
		try{
			if(response.getString("reason").equals("成功")){
				JSONArray json_array=response.getJSONArray("result");
				System.out.println(json_array);
			}else{
				MyUtil.alert(response.getString("reason"), this);
			}
		}catch(Exception e){
			e.printStackTrace();
			MyUtil.alert("http数据返回格式错误！", this);
		}
	}
	@Override
	public void onErrorResponse(VolleyError error) {
		error.printStackTrace();
		MyUtil.alert("出现错误，请检查网络后重拾", this);
	}

}
