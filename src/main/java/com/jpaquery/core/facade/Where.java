package com.jpaquery.core.facade;

/**
 * where字句
 * 
 * @author lujijiang
 * 
 */
public interface Where extends QueryRender {
	/**
	 * 逻辑类型
	 * 
	 * @author lujijiang
	 * 
	 */
	public enum WhereType {
		and, or
	}

	/**
	 * 获取属性
	 * 
	 * @param obj
	 * @return
	 */
	<T> WherePath<T> get(T obj);

	And and();

	Or or();

	/**
	 * 存在子查询
	 * 
	 * @param subFinder
	 */
	Where exists(JpaQuery subFinder);

	/**
	 * 不存在子查询
	 * 
	 * @param subFinder
	 */
	Where notExists(JpaQuery subFinder);

	/**
	 * 追加查询语句
	 * 
	 * @param queryString
	 * @param args
	 * @return
	 */
	public QueryAppender append(String queryString, Object... args);

}
