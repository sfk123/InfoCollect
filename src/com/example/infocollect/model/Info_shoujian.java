package com.example.infocollect.model;

public class Info_shoujian {
	private int code;//�������
	private String address;//�ռ��˵�ַ
	private String address_detail;//�ռ��˾���λ��
	private String name="";//�ռ�������
	private String phone="";//�ռ��˵绰
	private String type;//��Ʒ���
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getAddress_detail() {
		return address_detail;
	}
	public void setAddress_detail(String address_detail) {
		this.address_detail = address_detail;
	}
}
