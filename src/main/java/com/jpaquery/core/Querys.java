package com.jpaquery.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedList;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import com.jpaquery.core.facade.JpaQuery;
import com.jpaquery.core.impl.JpaQueryHandler;
import com.jpaquery.core.impl.JpaQueryImpl;
import com.jpaquery.core.render.impl.JpaQueryRenderImpl;
import com.jpaquery.util._Proxys;

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
	private static JpaQuery newJpaQuery() {
		return new JpaQueryImpl(new JpaQueryHandler(), new JpaQueryRenderImpl());
	}

	/**
	 * 匿名查询器
	 * @param queryHandler
	 * @param <T>
	 * @return
	 */
	public static <T> T query(QueryHandler<T> queryHandler){
		return queryHandler.handle(newJpaQuery());
	}
}
