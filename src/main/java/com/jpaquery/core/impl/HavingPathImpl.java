package com.jpaquery.core.impl;

import com.jpaquery.core.facade.JpaQuery;
import com.jpaquery.core.facade.Having;
import com.jpaquery.core.facade.HavingFunctionPath;
import com.jpaquery.core.facade.HavingPath;
import com.jpaquery.core.facade.SelectPath.SelectPathType;
import com.jpaquery.core.facade.WherePath.WherePathType;

public class HavingPathImpl<T> implements HavingFunctionPath<T> {

	JpaQueryHandler finderHandler;
	JpaQueryImpl finderImpl;
	HavingImpl havingImpl;
	/**
	 * 左参数
	 */
	Object left;
	/**
	 * select选择子句
	 */
	SelectPathType selectPathType;
	/**
	 * 父select
	 */
	HavingPathImpl<?> parentHavingPathImpl;

	/**
	 * 是否允许空参数
	 */
	boolean ifExist;
	/**
	 * 路径类型
	 */
	WherePathType wherePathType;

	/**
	 * 路径参数
	 */
	Object[] args;

	public JpaQueryHandler getFinderHandler() {
		return finderHandler;
	}

	public JpaQueryImpl getFinderImpl() {
		return finderImpl;
	}

	public HavingImpl getHavingImpl() {
		return havingImpl;
	}

	public Object getLeft() {
		return left;
	}

	public SelectPathType getSelectPathType() {
		return selectPathType;
	}

	public HavingPathImpl<?> getParentHavingPathImpl() {
		return parentHavingPathImpl;
	}

	public boolean isIfExist() {
		return ifExist;
	}

	public WherePathType getWherePathType() {
		return wherePathType;
	}

	public Object[] getArgs() {
		return args;
	}

	public HavingPathImpl(JpaQueryHandler finderHandler, JpaQueryImpl finderImpl,
			HavingImpl havingImpl, Object left) {
		super();
		this.finderHandler = finderHandler;
		this.finderImpl = finderImpl;
		this.havingImpl = havingImpl;
		this.left = left;
	}

	public HavingPath<T> distinct() {
		return toHavingPath(SelectPathType.distinct);
	}

	public HavingFunctionPath<Long> count() {
		HavingPathImpl<Long> parentHavingPathImpl = new HavingPathImpl<Long>(
				finderHandler, finderImpl, havingImpl, this);
		this.parentHavingPathImpl = parentHavingPathImpl;
		parentHavingPathImpl.selectPathType = SelectPathType.count;
		return parentHavingPathImpl;
	}

	public HavingFunctionPath<T> avg() {
		return toHavingPath(SelectPathType.avg);
	}

	public HavingFunctionPath<T> sum() {
		return toHavingPath(SelectPathType.sum);
	}

	public HavingFunctionPath<T> min() {
		return toHavingPath(SelectPathType.min);
	}

	public HavingFunctionPath<T> max() {
		return toHavingPath(SelectPathType.max);
	}

	private HavingFunctionPath<T> toHavingPath(SelectPathType selectPathType) {
		HavingPathImpl<T> parentHavingPathImpl = new HavingPathImpl<T>(
				finderHandler, finderImpl, havingImpl, this);
		this.parentHavingPathImpl = parentHavingPathImpl;
		parentHavingPathImpl.selectPathType = selectPathType;
		return parentHavingPathImpl;
	}

	Having fillPath(WherePathType wherePathType, boolean ifExist,
			Object... args) {
		this.args = args;
		this.wherePathType = wherePathType;
		this.ifExist = ifExist;
		return havingImpl;
	}

	public Having equal(T obj) {
		Object arg = finderHandler.getPathInfo();
		arg = arg == null ? obj : arg;
		return fillPath(WherePathType.equal, false, arg);
	}

	public Having notEqual(T obj) {
		Object arg = finderHandler.getPathInfo();
		arg = arg == null ? obj : arg;
		return fillPath(WherePathType.notEqual, false, arg);
	}

	public Having greatThan(T obj) {
		Object arg = finderHandler.getPathInfo();
		arg = arg == null ? obj : arg;
		return fillPath(WherePathType.greatThan, false, arg);
	}

	public Having greatEqual(T obj) {
		Object arg = finderHandler.getPathInfo();
		arg = arg == null ? obj : arg;
		return fillPath(WherePathType.greatEqual, false, arg);
	}

	public Having lessThan(T obj) {
		Object arg = finderHandler.getPathInfo();
		arg = arg == null ? obj : arg;
		return fillPath(WherePathType.lessThan, false, arg);
	}

	public Having lessEqual(T obj) {
		Object arg = finderHandler.getPathInfo();
		arg = arg == null ? obj : arg;
		return fillPath(WherePathType.lessEqual, false, arg);
	}

	public Having equal(JpaQuery subFinder) {
		return fillPath(WherePathType.equal, false, subFinder);
	}

	public Having notEqual(JpaQuery subFinder) {
		return fillPath(WherePathType.notEqual, false, subFinder);
	}

	public Having greatThan(JpaQuery subFinder) {
		return fillPath(WherePathType.greatThan, false, subFinder);
	}

	public Having greatEqual(JpaQuery subFinder) {
		return fillPath(WherePathType.greatEqual, false, subFinder);
	}

	public Having lessThan(JpaQuery subFinder) {
		return fillPath(WherePathType.lessThan, false, subFinder);
	}

	public Having lessEqual(JpaQuery subFinder) {
		return fillPath(WherePathType.lessEqual, false, subFinder);
	}

	public Having equalIfExist(T obj) {
		return fillPath(WherePathType.equal, true, obj);
	}

	public Having notEqualIfExist(T obj) {
		return fillPath(WherePathType.notEqual, true, obj);
	}

	public Having greatThanIfExist(T obj) {
		return fillPath(WherePathType.greatThan, true, obj);
	}

	public Having greatEqualIfExist(T obj) {
		return fillPath(WherePathType.greatEqual, true, obj);
	}

	public Having lessThanIfExist(T obj) {
		return fillPath(WherePathType.lessThan, true, obj);
	}

	public Having lessEqualIfExist(T obj) {
		return fillPath(WherePathType.lessEqual, true, obj);
	}

	public boolean equals(Object obj) {
		throw new UnsupportedOperationException(
				"The equals method is not supported, maybe you should use equal method");
	}

}
