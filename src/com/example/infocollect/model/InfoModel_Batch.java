package com.example.infocollect.model;

import java.util.List;

import com.baidu.mapapi.model.LatLng;

public class InfoModel_Batch {
	private int startCode;//起始单号
	private int count;//快递单数量
	private String imgurl;//寄件人身份证地址
	private String address_1;//寄件人地址字符串
	private LatLng location_1;//寄件人坐标
	private String name;//寄件人姓名
	private String phone;//寄件人电话
	private String card_num;//寄件人身份证号码
	private String address_detail;//寄件人具体地址
	private List<Info_shoujian> list_shou;//收件人信息列表
	public int getStartCode() {
		return startCode;
	}
	public void setStartCode(int startCode) {
		this.startCode = startCode;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getImgurl() {
		return imgurl;
	}
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
	public String getAddress_1() {
		return address_1;
	}
	public void setAddress_1(String address_1) {
		this.address_1 = address_1;
	}
	public LatLng getLocation_1() {
		return location_1;
	}
	public void setLocation_1(LatLng location_1) {
		this.location_1 = location_1;
	}
	public List<Info_shoujian> getList_shou() {
		return list_shou;
	}
	public void setList_shou(List<Info_shoujian> list_shou) {
		this.list_shou = list_shou;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCard_num() {
		return card_num;
	}
	public void setCard_num(String card_num) {
		this.card_num = card_num;
	}
	public String getAddress_detail() {
		return address_detail;
	}
	public void setAddress_detail(String address_detail) {
		this.address_detail = address_detail;
	}
}
