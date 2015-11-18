package com.example.infocollect.model;

import android.graphics.Bitmap;

public class UserInfo {
	private String id;
	private String identityCardId;//身份证号
	private String name;//姓名
	private String sex;//性别
	private String token;//令牌
	private String company;//公司名称
	private Bitmap photo;//头像
	private String branch;//网点
	private String phone;//电话
	private String address;//地址
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIdentityCardId() {
		return identityCardId;
	}
	public void setIdentityCardId(String identityCardId) {
		this.identityCardId = identityCardId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public Bitmap getPhoto() {
		return photo;
	}
	public void setPhoto(Bitmap photo) {
		this.photo = photo;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
}
