package com.example.infocollect.popup;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONObject;

import com.example.infocollect.R;
import com.example.infocollect.model.CityModel;
import com.example.infocollect.model.DistrictModel;
import com.example.infocollect.model.ProvinceModel;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;

public class Select_City implements OnClickListener, OnWheelChangedListener{
	 private Context context;
	 /**
	     * 取消按钮
	     */
	private Button btnCancel;

	    /**
	     * 确定按钮
	     */
	private Button btnConfirm;

	private PopupWindow popupWindow;
	
	private WheelView mViewProvince;
	private WheelView mViewCity;
	private WheelView mViewDistrict;
	/**
	 * 所有省
	 */
	protected String[] mProvinceDatas;
	/**
	 * key - 省 value - 市
	 */
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	/**
	 * key - 市 values - 区
	 */
	protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
	
	/**
	 * key - 区 values - 邮编
	 */
	protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>(); 

	/**
	 * 当前省的名称
	 */
	protected String mCurrentProviceName;
	/**
	 * 当前市的名称
	 */
	protected String mCurrentCityName;
	/**
	 * 当前区的名称
	 */
	protected String mCurrentDistrictName ="";
	
	/**
	 * 当前区的邮政编码
	 */
	protected String mCurrentZipCode ="";
	private View view;
	private cityselectListener listener;
	public Select_City(Context activity){
	    	this.context = activity;

	}
	    @SuppressWarnings("deprecation")
		@SuppressLint("InflateParams")
		public Select_City builder(){

	        // PopView
	        view = LayoutInflater.from(context).inflate(
	                R.layout.popup_select_city, null);

	        initView(view);
	// android.view.View#getWindowToken()
	        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,false);
	        popupWindow.setFocusable(true);
	        popupWindow.setOutsideTouchable(true);
	        popupWindow.setBackgroundDrawable(new BitmapDrawable());
	        popupWindow.setOnDismissListener(new PopDismissListener());
	        popupWindow.setAnimationStyle(R.style.PopAnimationDownUp);
	        return this;
	    }
	    public  void initView(View contentView){
	    	mViewProvince = (WheelView) contentView.findViewById(R.id.id_province);
			mViewCity = (WheelView) contentView.findViewById(R.id.id_city);
			mViewDistrict = (WheelView)contentView. findViewById(R.id.id_district);
			btnConfirm = (Button)contentView. findViewById(R.id.btn_confirm);
			btnCancel=(Button)contentView. findViewById(R.id.btn_cancel);
			
			// 添加change事件
	    	mViewProvince.addChangingListener(this);
	    	// 添加change事件
	    	mViewCity.addChangingListener(this);
	    	// 添加change事件
	    	mViewDistrict.addChangingListener(this);
	    	// 添加onclick事件
	    	btnConfirm.setOnClickListener(this);
	    	btnCancel.setOnClickListener(this);
	    	
	    	initProvinceDatas();
			mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(context, mProvinceDatas));
			// 设置可见条目数量
			mViewProvince.setVisibleItems(7);
			mViewCity.setVisibleItems(7);
			mViewDistrict.setVisibleItems(7);
			updateCities();
			updateAreas();
	    }
	    /**
	     * 显示
	     */
	    public void show(){
	        if (null != popupWindow){
//	        	popupWindow.setBackgroundDrawable();
	            setBackgroundAlpha(1f);
		        popupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);
	        }
	    }

	    /**
	     * 撤销
	     */
	    public void dismiss(){
	        if (null != popupWindow){
	            popupWindow.dismiss();
	        }
	    }

	    /**
	     * 设置背景透明度
	     * @param alpha 背景的Alpha
	     */
	    private void setBackgroundAlpha(float alpha){
	        WindowManager.LayoutParams lp =  ((Activity)context).getWindow().getAttributes();
	        lp.alpha = alpha;
	        ((Activity)context).getWindow().setAttributes(lp);
	    }
	    private class PopDismissListener implements PopupWindow.OnDismissListener{

	        @Override
	        public void onDismiss() {
	            //更改背景透明度
	            setBackgroundAlpha(1.0f);
	        }
	    }
		
		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.btn_cancel){
				dismiss();
			}else if(v.getId()==R.id.btn_confirm){
				if(listener!=null){
					JSONObject json=new JSONObject();
					try{
					json.put("provence", mCurrentProviceName);
					json.put("city", mCurrentCityName);
					json.put("distirct", mCurrentDistrictName);
					json.put("distirctid", mCurrentZipCode);
					}catch(Exception e){
						e.printStackTrace();
					}
					listener.selectok(json);
					dismiss();
				}
			}
		}
		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (wheel == mViewProvince) {
				updateCities();
			} else if (wheel == mViewCity) {
				updateAreas();
			} else if (wheel == mViewDistrict) {
				mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
				mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
			}
		}
		/**
		 * 解析省市区的XML数据
		 */
		
	    protected void initProvinceDatas()
		{
			List<ProvinceModel> provinceList = null;
	    	AssetManager asset = context.getAssets();
	        try {
	            InputStream input = asset.open("province_data.xml");
	            // 创建一个解析xml的工厂对象
				SAXParserFactory spf = SAXParserFactory.newInstance();
				// 解析xml
				SAXParser parser = spf.newSAXParser();
				XmlParserHandler handler = new XmlParserHandler();
				parser.parse(input, handler);
				input.close();
				// 获取解析出来的数据
				provinceList = handler.getDataList();
				//*/ 初始化默认选中的省、市、区
				if (provinceList!= null && !provinceList.isEmpty()) {
					mCurrentProviceName = provinceList.get(0).getName();
					
					List<CityModel> cityList = provinceList.get(0).getCityList();
					if (cityList!= null && !cityList.isEmpty()) {
						mCurrentCityName = cityList.get(0).getName();
						List<DistrictModel> districtList = cityList.get(0).getDistrictList();
						mCurrentDistrictName = districtList.get(0).getName();
						mCurrentZipCode = districtList.get(0).getZipcode();
					}
				}
				//*/
				mProvinceDatas = new String[provinceList.size()];
	        	for (int i=0; i< provinceList.size(); i++) {
	        		// 遍历所有省的数据
	        		mProvinceDatas[i] = provinceList.get(i).getName();
	        		List<CityModel> cityList = provinceList.get(i).getCityList();
	        		String[] cityNames = new String[cityList.size()];
	        		for (int j=0; j< cityList.size(); j++) {
	        			// 遍历省下面的所有市的数据
	        			cityNames[j] = cityList.get(j).getName();
	        			List<DistrictModel> districtList = cityList.get(j).getDistrictList();
	        			String[] distrinctNameArray = new String[districtList.size()];
	        			DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
	        			for (int k=0; k<districtList.size(); k++) {
	        				// 遍历市下面所有区/县的数据
	        				DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
	        				// 区/县对于的邮编，保存到mZipcodeDatasMap
	        				mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
	        				distrinctArray[k] = districtModel;
	        				distrinctNameArray[k] = districtModel.getName();
	        			}
	        			// 市-区/县的数据，保存到mDistrictDatasMap
	        			mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
	        		}
	        		// 省-市的数据，保存到mCitisDatasMap
	        		mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
	        	}
	        } catch (Throwable e) {  
	            e.printStackTrace();  
	        } finally {
	        	
	        } 
		}
		/**
		 * 根据当前的省，更新市WheelView的信息
		 */
		private void updateCities() {
			int pCurrent = mViewProvince.getCurrentItem();
			mCurrentProviceName = mProvinceDatas[pCurrent];
			String[] cities = mCitisDatasMap.get(mCurrentProviceName);
			if (cities == null) {
				cities = new String[] { "" };
			}
			mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(context, cities));
			mViewCity.setCurrentItem(0);
			updateAreas();
		}
		/**
		 * 根据当前的市，更新区WheelView的信息
		 */
		private void updateAreas() {
			int pCurrent = mViewCity.getCurrentItem();
			mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
			String[] areas = mDistrictDatasMap.get(mCurrentCityName);

			if (areas == null) {
				areas = new String[] { "" };
			}
			mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(context, areas));
			mViewDistrict.setCurrentItem(0);
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
			mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
		}
		public interface cityselectListener{
			public void selectok(JSONObject json);
		}

		public cityselectListener getListener() {
			return listener;
		}
		public void setListener(cityselectListener listener) {
			this.listener = listener;
		}
}
