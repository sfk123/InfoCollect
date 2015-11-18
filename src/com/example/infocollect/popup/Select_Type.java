package com.example.infocollect.popup;

import java.util.ArrayList;
import java.util.List;

import com.example.infocollect.R;
import com.example.infocollect.view.WheelView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

public class Select_Type {
	 private Context context;
	    /**
	     * ȡ����ť
	     */
	    private Button btnCancel;

	    /**
	     * ȷ����ť
	     */
	    private Button btnConfirm;

	    /**
	     *  ��Ļ�Ŀ�ȣ����ڶ�̬�趨������Ŀ��
	     * */
	    private WheelView wvTransportation;
	    private List<String> Transportations;//��ͨ�����б�
	    private PopupWindow popupWindow;
	    private SelectListener selectListtener;
	    public void setSelectListener(SelectListener selectListtener){
	    	this.selectListtener=selectListtener;
	    }
	    public Select_Type(Activity activity){
	    	this.context = activity;

	    }
	    @SuppressWarnings("deprecation")
		@SuppressLint("InflateParams")
		public Select_Type builder(){

	        // PopView
	        View view = LayoutInflater.from(context).inflate(
	                R.layout.popup_select_type, null);

	        loadCity(view);
	// android.view.View#getWindowToken()
	        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,false);
	        popupWindow.setFocusable(true);
	        popupWindow.setOutsideTouchable(true);
	        popupWindow.setBackgroundDrawable(new BitmapDrawable());
	        popupWindow.setOnDismissListener(new PopDismissListener());
	        popupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);
	        popupWindow.setAnimationStyle(R.style.PopAnimationDownUp);
	        return this;
	    }
	    public  void loadCity(View view){
	    	btnCancel = (Button) view.findViewById(R.id.btn_cancel);
	        btnCancel.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                dismiss();
	            }
	        });
	        
	        btnConfirm = (Button) view.findViewById(R.id.btn_confirm);
	        btnConfirm.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	if(selectListtener!=null)
	            	selectListtener.select_ok(wvTransportation.getSeletedItem());
	            	dismiss();
	            }
	        });
	        wvTransportation=(WheelView)view.findViewById(R.id.wheel_view1);
	        Transportations=new ArrayList<>();
	        Transportations.add("�·�");
	        Transportations.add("����1");
	        Transportations.add("����12");
	        Transportations.add("����3");
	        wvTransportation.setItems(Transportations);
	    }
	    /**
	     * ��ʾ
	     */
	    public void show(){
	        if (null != popupWindow){
//	        	popupWindow.setBackgroundDrawable();
	            setBackgroundAlpha(0.5f);
	            popupWindow.update();
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
	    public interface SelectListener{
	    	public void select_ok(String s);
	    }
}
