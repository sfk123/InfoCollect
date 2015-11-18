package com.example.infocollect.adapter;

import com.example.infocollect.R;
import com.example.infocollect.model.InfoModel_Batch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Adapter_Batch extends BaseAdapter{
	private LayoutInflater inflater;
	private InfoModel_Batch infodata;
	public Adapter_Batch(Context context,InfoModel_Batch infodata){
		this.infodata=infodata;
		inflater=LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return infodata.getList_shou().size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=inflater.inflate(R.layout.item_batch, null);
			holder.tv_code=(TextView)convertView.findViewById(R.id.tv_code);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		holder.tv_code.setText((infodata.getStartCode()+position)+"");
		return convertView;
	}
	class ViewHolder{
		TextView tv_code;
	}
}
