package com.jpaquery.core.facade;

public interface SelectPath<T> {

	/**
	 * 聚集函数类型
	 * 
	 * @author lujijiang
	 * 
	 */
	public enum SelectPathType {
		distinct, count, avg, sum, min, max
	}

	SelectPath<T> distinct();

	FunctionPath<Long> count();

	FunctionPath<T> avg();

	FunctionPath<T> sum();

	FunctionPath<T> min();

	FunctionPath<T> max();

}
