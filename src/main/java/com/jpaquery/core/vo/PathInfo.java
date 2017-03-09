package com.jpaquery.core.vo;

import java.lang.reflect.Method;

import com.jpaquery.util._Helper;

/**
 * 路径消息
 * 
 * @author lujijiang
 * 
 */
public class PathInfo {
	/**
	 * 根代理对象
	 */
	Object rootProxy;

	/**
	 * 根代理对象的系统引用
	 */
	long rootKey;
	/**
	 * 当前代理对象
	 */
	Object currentProxy;
	/**
	 * 当前代理对象的系统引用
	 */
	long currentKey;
	/**
	 * 路径构建
	 */
	StringBuilder pathBuilder;

	/**
	 * getter方法
	 */
	Method getter;

	public PathInfo(Object rootProxy, Object currentProxy, StringBuilder pathBuilder, Method getter) {
		super();
		this.rootProxy = rootProxy;
		this.rootKey = _Helper.identityHashCode(rootProxy);
		this.setCurrentProxy(currentProxy);
		this.pathBuilder = pathBuilder;
		this.getter = getter;
	}

	public Object getCurrentProxy() {
		return currentProxy;
	}

	public void setCurrentProxy(Object currentProxy) {
		this.currentProxy = currentProxy;
		this.currentKey = _Helper.identityHashCode(currentProxy);
	}

	public Object getRootProxy() {
		return rootProxy;
	}

	public long getRootKey() {
		return rootKey;
	}

	public long getCurrentKey() {
		return currentKey;
	}

	public StringBuilder getPathBuilder() {
		return pathBuilder;
	}

	public Method getGetter() {
		return getter;
	}

	public String toString() {
		return "PathInfo [path=" + pathBuilder + ",rootProxy=" + rootProxy.getClass().getCanonicalName().concat("@")
				+ System.identityHashCode(rootProxy) + ", currentProxy="
				+ currentProxy.getClass().getCanonicalName().concat("@") + System.identityHashCode(currentProxy) + "]";
	}

}
