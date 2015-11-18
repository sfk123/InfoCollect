package com.example.infocollect.adapter;

import java.util.List;

import com.baidu.mapapi.search.core.PoiInfo;
import com.example.infocollect.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Adapter_SelectLocation extends BaseAdapter {
	private LayoutInflater mInflater = null; 
	private List<PoiInfo> data;
	private ImageView currentSelect;
	private String current_location;
	public Adapter_SelectLocation(Context context,List<PoiInfo> data,String current_location){
		this.data=data;
		mInflater=LayoutInflater.from(context);
		this.current_location=current_location;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public PoiInfo getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;    
		if (convertView == null) {    
            holder = new ViewHolder();    
            convertView = mInflater.inflate(R.layout.item_select_location, null);  
            holder.img_select=(ImageView)convertView.findViewById(R.id.img_select);
            holder.tv_title=(TextView)convertView.findViewById(R.id.tv_title);
            holder.tv_slogan=(TextView)convertView.findViewById(R.id.tv_slogan);
            convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		if(current_location!=null){
			if(current_location.equals("")&&position==0){
				holder.img_select.setVisibility(View.VISIBLE);
				currentSelect=holder.img_select;
			}else if(current_location.equals(data.get(position).address)){
				holder.img_select.setVisibility(View.VISIBLE);
				currentSelect=holder.img_select;
			}
		}
		holder.tv_title.setText((position+1)+"."+data.get(position).name);
		holder.tv_slogan.setText(data.get(position).address);
		return convertView;
	}
	public class ViewHolder{
		ImageView img_select;
		TextView tv_title,tv_slogan;
	}
	public void setSelect(ViewHolder holder){
		holder.img_select.setVisibility(View.VISIBLE);
		if(currentSelect!=null){
		currentSelect.setVisibility(View.GONE);
		currentSelect=holder.img_select;
		}
	}
}
