package com.jpaquery.core.facade;

public interface JpaQueryEach<T> {
	/**
	 * 处理各个实体
	 * 
	 * @param entity
	 */
	public void handle(T entity);
}
