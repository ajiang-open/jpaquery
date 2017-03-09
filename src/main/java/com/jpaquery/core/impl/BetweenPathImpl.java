package com.jpaquery.core.impl;

import com.jpaquery.core.facade.BetweenPath;
import com.jpaquery.core.facade.JpaQuery;
import com.jpaquery.core.facade.Where;
import com.jpaquery.core.facade.WherePath.WherePathType;

public class BetweenPathImpl<T> implements BetweenPath<T> {

	JpaQueryHandler finderHandler;
	JpaQueryImpl finderImpl;
	WhereImpl whereImpl;
	WherePathImpl<T> wherePathImpl;
	WherePathType wherePathType;
	boolean ifExist;
	Object arg;

	public BetweenPathImpl(JpaQueryHandler finderHandler, JpaQueryImpl finderImpl,
			WhereImpl whereImpl, WherePathImpl<T> wherePathImpl,
			WherePathType wherePathType, boolean ifExist, Object arg) {
		this.finderHandler = finderHandler;
		this.finderImpl = finderImpl;
		this.whereImpl = whereImpl;
		this.wherePathImpl = wherePathImpl;
		this.wherePathType = wherePathType;
		this.ifExist = ifExist;
		this.arg = arg;
	}

	public Where and(T obj) {
		Object secondArg = finderHandler.getPathInfo();
		secondArg = secondArg == null ? obj : secondArg;
		wherePathImpl.fillPath(wherePathType, ifExist, arg, secondArg);
		return whereImpl;
	}

	public Where and(JpaQuery subFinder) {
		wherePathImpl.fillPath(wherePathType, ifExist, arg, subFinder);
		return whereImpl;
	}

}
