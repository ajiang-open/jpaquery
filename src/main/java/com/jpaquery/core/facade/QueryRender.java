package com.jpaquery.core.facade;

import com.jpaquery.core.vo.QueryContent;

/**
 * 查询信息渲染接口
 * 
 * @author lujijiang
 * 
 */
public interface QueryRender {
	/**
	 * 获得查询信息
	 * 
	 * @return
	 */
	public QueryContent toQueryContent();
}
