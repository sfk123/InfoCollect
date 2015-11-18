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
	private GeoCoder coder ;//�����ܱ�
	private String current_location;//��ǰѡ���λ��
	private String current_city;//��ǰ��������
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
		mLocationClient = new LocationClient(getApplicationContext());     //����LocationClient��
		initLocation();
		mLocationClient.registerLocationListener( myListener );    //ע���������
		bmapView=(MapView)findViewById(R.id.bmapView);
		mBaiduMap = bmapView.getMap();
		
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15.0f));
		mBaiduMap.setOnMapClickListener(map_click_listener);
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
	   
	}
	private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);//��ѡ��Ĭ�ϸ߾��ȣ����ö�λģʽ���߾��ȣ��͹��ģ����豸
        option.setCoorType("bd09ll");//��ѡ��Ĭ��gcj02�����÷��صĶ�λ�������ϵ
        int span=1000;
        option.setScanSpan(span);//��ѡ��Ĭ��0��������λһ�Σ����÷���λ����ļ����Ҫ���ڵ���1000ms������Ч��
        option.setIsNeedAddress(true);//��ѡ�������Ƿ���Ҫ��ַ��Ϣ��Ĭ�ϲ���Ҫ
        option.setOpenGps(true);//��ѡ��Ĭ��false,�����Ƿ�ʹ��gps
        option.setLocationNotify(true);//��ѡ��Ĭ��false�������Ƿ�gps��Чʱ����1S1��Ƶ�����GPS���
        option.setIsNeedLocationDescribe(true);//��ѡ��Ĭ��false�������Ƿ���Ҫλ�����廯�����������BDLocation.getLocationDescribe��õ�����������ڡ��ڱ����찲�Ÿ�����
        option.setIsNeedLocationPoiList(true);//��ѡ��Ĭ��false�������Ƿ���ҪPOI�����������BDLocation.getPoiList��õ�
        option.setIgnoreKillProcess(false);//��ѡ��Ĭ��false����λSDK�ڲ���һ��SERVICE�����ŵ��˶������̣������Ƿ���stop��ʱ��ɱ��������̣�Ĭ��ɱ��
        option.SetIgnoreCacheException(false);//��ѡ��Ĭ��false�������Ƿ��ռ�CRASH��Ϣ��Ĭ���ռ�
        option.setEnableSimulateGps(false);//��ѡ��Ĭ��false�������Ƿ���Ҫ����gps��������Ĭ����Ҫ
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
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        bmapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        bmapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
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
            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS��λ���
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// ��λ������ÿСʱ
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// ��λ����
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// ��λ��
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps��λ�ɹ�");
 
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// ���綨λ���
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //��Ӫ����Ϣ
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("���綨λ�ɹ�");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// ���߶�λ���
                sb.append("\ndescribe : ");
                sb.append("���߶�λ�ɹ������߶�λ���Ҳ����Ч��");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("��������綨λʧ�ܣ����Է���IMEI�źʹ��嶨λʱ�䵽loc-bugs@baidu.com��������׷��ԭ��");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("���粻ͬ���¶�λʧ�ܣ����������Ƿ�ͨ��");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("�޷���ȡ��Ч��λ���ݵ��¶�λʧ�ܣ�һ���������ֻ���ԭ�򣬴��ڷ���ģʽ��һ���������ֽ�����������������ֻ�");
            }
//            sb.append("\nlocationdescribe : ");
//                sb.append(location.getLocationDescribe());// λ�����廯��Ϣ
//                @SuppressWarnings("unchecked")
//				List<Poi> list = location.getPoiList();// POI����
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
         // map view ���ٺ��ڴ����½��յ�λ��
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
				System.out.println("û�л�ȡ��������Ϣ");
			}
		}
		
		@Override
		public void onGetGeoCodeResult(GeoCodeResult arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	OnMapClickListener map_click_listener = new OnMapClickListener() {
		/**
		 * ��ͼ�����¼��ص�����
		 * 
		 * @param point
		 *            ����ĵ�������
		 */
		public void onMapClick(LatLng point) {
			ReverseGeoCodeOption option = new ReverseGeoCodeOption();

            option.location(point);

            coder.reverseGeoCode(option);
			
			 popWin(point);
		}

		@Override
		public boolean onMapPoiClick(MapPoi arg0) {
			// TODO �Զ����ɵķ������
			return false;
		}
	};
	// �Զ�����ʾͼ��
	public void popWin(LatLng point) {
		// ����Markerͼ��
		BitmapDescriptor bitmap= BitmapDescriptorFactory
				.fromResource(R.drawable.ico_location);
		// ����MarkerOption�������ڵ�ͼ�����Marker
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap);
		// �ڵ�ͼ�����Marker������ʾ
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
		setResult(RESULT_OK, intent); //intentΪA�����Ĵ���Bundle��intent����ȻҲ�����Լ������µ�Bundle
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
//	    		/*Ϊ���󷵻�����.
//	    		 */
//	    		@Override
//	    		public void onSuccess(int statusCode, String responseString) {
//	    			// TODO Auto-generated method stub
//	    			System.out.println(responseString);
//	    		}
//
//	    		/**
//	    		 * �������ʱ���õķ���,���۳ɹ�����ʧ�ܶ������.
//	    		 */
//	    		@Override
//	    		public void onFinish() {
//	    		    // TODO Auto-generated method stub
////	    			Toast.makeText(getApplicationContext(), "finish",Toast.LENGTH_SHORT).show();
//	    			}
//	    		/**
//	    		 * ����ʧ��ʱ���õķ���,statusCodeΪhttp״̬��,throwableΪ���񵽵��쳣
//	    		* statusCode:30002 û�м�⵽��ǰ����. 30003 û�н��г�ʼ��. 0
//	    		* δ���쳣,����鿴Throwable��Ϣ. �����쳣�����http״̬��.
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
	    //��ȡPOI�������  
	    	if (result == null  
                    || result.error == PoiResult.ERRORNO.RESULT_NOT_FOUND) {// û���ҵ��������  
                Toast.makeText(Activity_SelectLocation.this, "δ�ҵ����",  
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
	    //��ȡPlace����ҳ�������  
	    }  
	};
	@Override
	public void onResponse(JSONObject response) {
		System.out.println(response);
		try{
			if(response.getString("reason").equals("�ɹ�")){
				JSONArray json_array=response.getJSONArray("result");
				System.out.println(json_array);
			}else{
				MyUtil.alert(response.getString("reason"), this);
			}
		}catch(Exception e){
			e.printStackTrace();
			MyUtil.alert("http���ݷ��ظ�ʽ����", this);
		}
	}
	@Override
	public void onErrorResponse(VolleyError error) {
		error.printStackTrace();
		MyUtil.alert("���ִ��������������ʰ", this);
	}

}
