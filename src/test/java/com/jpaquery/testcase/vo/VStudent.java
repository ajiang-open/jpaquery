package com.jpaquery.testcase.vo;

import com.jpaquery.testcase.Gender;

public class VStudent {

	String name;

	String nickName;

	VClazz clazz;

	Gender gender;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public VClazz getClazz() {
		return clazz;
	}

	public void setClazz(VClazz clazz) {
		this.clazz = clazz;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

}
