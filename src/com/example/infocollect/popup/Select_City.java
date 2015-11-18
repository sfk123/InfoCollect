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
	     * ȡ����ť
	     */
	private Button btnCancel;

	    /**
	     * ȷ����ť
	     */
	private Button btnConfirm;

	private PopupWindow popupWindow;
	
	private WheelView mViewProvince;
	private WheelView mViewCity;
	private WheelView mViewDistrict;
	/**
	 * ����ʡ
	 */
	protected String[] mProvinceDatas;
	/**
	 * key - ʡ value - ��
	 */
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	/**
	 * key - �� values - ��
	 */
	protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
	
	/**
	 * key - �� values - �ʱ�
	 */
	protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>(); 

	/**
	 * ��ǰʡ������
	 */
	protected String mCurrentProviceName;
	/**
	 * ��ǰ�е�����
	 */
	protected String mCurrentCityName;
	/**
	 * ��ǰ��������
	 */
	protected String mCurrentDistrictName ="";
	
	/**
	 * ��ǰ������������
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
			
			// ���change�¼�
	    	mViewProvince.addChangingListener(this);
	    	// ���change�¼�
	    	mViewCity.addChangingListener(this);
	    	// ���change�¼�
	    	mViewDistrict.addChangingListener(this);
	    	// ���onclick�¼�
	    	btnConfirm.setOnClickListener(this);
	    	btnCancel.setOnClickListener(this);
	    	
	    	initProvinceDatas();
			mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(context, mProvinceDatas));
			// ���ÿɼ���Ŀ����
			mViewProvince.setVisibleItems(7);
			mViewCity.setVisibleItems(7);
			mViewDistrict.setVisibleItems(7);
			updateCities();
			updateAreas();
	    }
	    /**
	     * ��ʾ
	     */
	    public void show(){
	        if (null != popupWindow){
//	        	popupWindow.setBackgroundDrawable();
	            setBackgroundAlpha(1f);
		        popupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);
	        }
	    }

	    /**
	     * ����
	     */
	    public void dismiss(){
	        if (null != popupWindow){
	            popupWindow.dismiss();
	        }
	    }

	    /**
	     * ���ñ���͸����
	     * @param alpha ������Alpha
	     */
	    private void setBackgroundAlpha(float alpha){
	        WindowManager.LayoutParams lp =  ((Activity)context).getWindow().getAttributes();
	        lp.alpha = alpha;
	        ((Activity)context).getWindow().setAttributes(lp);
	    }
	    private class PopDismissListener implements PopupWindow.OnDismissListener{

	        @Override
	        public void onDismiss() {
	            //���ı���͸����
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
		 * ����ʡ������XML����
		 */
		
	    protected void initProvinceDatas()
		{
			List<ProvinceModel> provinceList = null;
	    	AssetManager asset = context.getAssets();
	        try {
	            InputStream input = asset.open("province_data.xml");
	            // ����һ������xml�Ĺ�������
				SAXParserFactory spf = SAXParserFactory.newInstance();
				// ����xml
				SAXParser parser = spf.newSAXParser();
				XmlParserHandler handler = new XmlParserHandler();
				parser.parse(input, handler);
				input.close();
				// ��ȡ��������������
				provinceList = handler.getDataList();
				//*/ ��ʼ��Ĭ��ѡ�е�ʡ���С���
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
	        		// ��������ʡ������
	        		mProvinceDatas[i] = provinceList.get(i).getName();
	        		List<CityModel> cityList = provinceList.get(i).getCityList();
	        		String[] cityNames = new String[cityList.size()];
	        		for (int j=0; j< cityList.size(); j++) {
	        			// ����ʡ����������е�����
	        			cityNames[j] = cityList.get(j).getName();
	        			List<DistrictModel> districtList = cityList.get(j).getDistrictList();
	        			String[] distrinctNameArray = new String[districtList.size()];
	        			DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
	        			for (int k=0; k<districtList.size(); k++) {
	        				// ����������������/�ص�����
	        				DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
	        				// ��/�ض��ڵ��ʱ࣬���浽mZipcodeDatasMap
	        				mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
	        				distrinctArray[k] = districtModel;
	        				distrinctNameArray[k] = districtModel.getName();
	        			}
	        			// ��-��/�ص����ݣ����浽mDistrictDatasMap
	        			mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
	        		}
	        		// ʡ-�е����ݣ����浽mCitisDatasMap
	        		mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
	        	}
	        } catch (Throwable e) {  
	            e.printStackTrace();  
	        } finally {
	        	
	        } 
		}
		/**
		 * ���ݵ�ǰ��ʡ��������WheelView����Ϣ
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
		 * ���ݵ�ǰ���У�������WheelView����Ϣ
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
