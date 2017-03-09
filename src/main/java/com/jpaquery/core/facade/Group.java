package com.jpaquery.core.facade;

/**
 * 分组对象
 * 
 * @author lujijiang
 * 
 */
public interface Group extends QueryRender {
	/**
	 * 获得分组路径
	 * 
	 * @param obj
	 * @return
	 */
	GroupPath get(Object obj);

	QueryAppender append(String queryString, Object... args);

}
