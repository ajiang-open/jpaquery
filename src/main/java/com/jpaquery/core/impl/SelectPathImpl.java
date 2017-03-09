package com.jpaquery.core.impl;

import com.jpaquery.core.facade.FunctionPath;
import com.jpaquery.core.facade.SelectPath;

/**
 * select路径实现
 * 
 * @author lujijiang
 * 
 * @param <T>
 */
public class SelectPathImpl<T> implements FunctionPath<T> {

	/**
	 * 
	 */
	SelectPathType selectPathType;

	/**
	 * 
	 */
	JpaQueryHandler finderHandler;
	/**
	 * 
	 */
	JpaQueryImpl finderImpl;
	/**
	 * 
	 */
	SelectImpl selectImpl;
	/**
	 * 
	 */
	Object arg;

	/**
	 * 父select
	 */
	SelectPathImpl<?> parentSelectPathImpl;

	public SelectPathType getSelectPathType() {
		return selectPathType;
	}

	public JpaQueryHandler getFinderHandler() {
		return finderHandler;
	}

	public JpaQueryImpl getFinderImpl() {
		return finderImpl;
	}

	public SelectImpl getSelectImpl() {
		return selectImpl;
	}

	public Object getArg() {
		return arg;
	}

	public SelectPathImpl<?> getParentSelectPathImpl() {
		return parentSelectPathImpl;
	}

	public SelectPathImpl(JpaQueryHandler finderHandler, JpaQueryImpl finderImpl,
			SelectImpl selectImpl, Object arg) {
		this.finderHandler = finderHandler;
		this.finderImpl = finderImpl;
		this.selectImpl = selectImpl;
		this.arg = arg;
	}

	public SelectPath<T> distinct() {
		return toSelectPath(SelectPathType.distinct);
	}

	public FunctionPath<Long> count() {
		SelectPathImpl<Long> parentSelectPathImpl = new SelectPathImpl<Long>(
				finderHandler, finderImpl, selectImpl, this);
		this.parentSelectPathImpl = parentSelectPathImpl;
		parentSelectPathImpl.selectPathType = SelectPathType.count;
		return parentSelectPathImpl;
	}

	public FunctionPath<T> avg() {
		return toSelectPath(SelectPathType.avg);
	}

	public FunctionPath<T> sum() {
		return toSelectPath(SelectPathType.sum);
	}

	public FunctionPath<T> min() {
		return toSelectPath(SelectPathType.min);
	}

	public FunctionPath<T> max() {
		return toSelectPath(SelectPathType.max);
	}

	private FunctionPath<T> toSelectPath(SelectPathType selectPathType) {
		SelectPathImpl<T> parentSelectPathImpl = new SelectPathImpl<T>(
				finderHandler, finderImpl, selectImpl, this);
		this.parentSelectPathImpl = parentSelectPathImpl;
		parentSelectPathImpl.selectPathType = selectPathType;
		return parentSelectPathImpl;
	}

}
