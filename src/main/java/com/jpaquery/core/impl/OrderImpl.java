package com.jpaquery.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jpaquery.core.facade.Order;
import com.jpaquery.core.facade.OrderPath;
import com.jpaquery.core.facade.QueryAppender;
import com.jpaquery.core.vo.QueryContent;

public class OrderImpl implements Order {

	static Logger log = LoggerFactory.getLogger(OrderImpl.class);

	JpaQueryHandler finderHandler;
	JpaQueryImpl finderImpl;

	/**
	 * 路径
	 */
	List<Object> paths = new ArrayList<>();

	public JpaQueryHandler getFinderHandler() {
		return finderHandler;
	}

	public JpaQueryImpl getFinderImpl() {
		return finderImpl;
	}

	public List<Object> getPaths() {
		return paths;
	}

	public OrderImpl(JpaQueryHandler finderHandler, JpaQueryImpl finderImpl) {
		super();
		this.finderHandler = finderHandler;
		this.finderImpl = finderImpl;
	}

	public QueryContent toQueryContent() {
		return finderImpl.finderRender.toOrder(finderImpl, this);
	}

	public OrderPath get(Object obj) {
		Object arg = finderHandler.getPathInfo();
		arg = arg == null ? obj : arg;
		OrderPath path = new OrderPathImpl(finderHandler, finderImpl, this, arg);
		paths.add(path);
		return path;
	}

	public QueryAppender append(String queryString, Object... args) {
		QueryAppenderImpl queryAppenderImpl = new QueryAppenderImpl(finderImpl, queryString, args);
		paths.add(queryAppenderImpl);
		return queryAppenderImpl;
	}

}
