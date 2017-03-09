package com.jpaquery.core;

import com.jpaquery.core.facade.JpaQuery;
import com.jpaquery.core.impl.JpaQueryHandler;
import com.jpaquery.core.impl.JpaQueryImpl;
import com.jpaquery.core.render.impl.JpaQueryRenderImpl;

/**
 * Querys工具类
 * 
 * @author lujijiang
 * 
 */
public class Querys {

	/**
	 * 新建一个查询器
	 * 
	 * @return
	 */
	public static JpaQuery newJpaQuery() {
		return new JpaQueryImpl(new JpaQueryHandler(), new JpaQueryRenderImpl());
	}

}
