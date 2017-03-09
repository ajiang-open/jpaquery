package com.jpaquery.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jpaquery.core.facade.Having;
import com.jpaquery.core.facade.HavingPath;
import com.jpaquery.core.facade.QueryAppender;
import com.jpaquery.core.vo.QueryContent;

public class HavingImpl implements Having {

	static Logger log = LoggerFactory.getLogger(HavingImpl.class);

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

	public HavingImpl(JpaQueryHandler finderHandler, JpaQueryImpl finderImpl) {
		super();
		this.finderHandler = finderHandler;
		this.finderImpl = finderImpl;
	}

	public QueryContent toQueryContent() {
		return finderImpl.finderRender.toHaving(finderImpl, this);
	}

	public <T> HavingPath<T> get(T obj) {
		Object arg = finderHandler.getPathInfo();
		arg = arg == null ? obj : arg;
		HavingPath<T> havingPath = new HavingPathImpl<T>(finderHandler, finderImpl, this, arg);
		paths.add(havingPath);
		return havingPath;
	}

	public QueryAppender append(String queryString, Object... args) {
		QueryAppenderImpl queryAppenderImpl = new QueryAppenderImpl(finderImpl, queryString, args);
		paths.add(queryAppenderImpl);
		return queryAppenderImpl;
	}

}
