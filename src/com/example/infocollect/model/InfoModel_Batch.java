package com.example.infocollect.model;

import java.util.List;

import com.baidu.mapapi.model.LatLng;

public class InfoModel_Batch {
	private int startCode;//��ʼ����
	private int count;//��ݵ�����
	private String imgurl;//�ļ������֤��ַ
	private String address_1;//�ļ��˵�ַ�ַ���
	private LatLng location_1;//�ļ�������
	private String name;//�ļ�������
	private String phone;//�ļ��˵绰
	private String card_num;//�ļ������֤����
	private String address_detail;//�ļ��˾����ַ
	private List<Info_shoujian> list_shou;//�ռ�����Ϣ�б�
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
