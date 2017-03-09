package com.jpaquery.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jpaquery.core.facade.Group;
import com.jpaquery.core.facade.GroupPath;
import com.jpaquery.core.facade.QueryAppender;
import com.jpaquery.core.vo.QueryContent;

public class GroupImpl implements Group {

	static Logger log = LoggerFactory.getLogger(GroupImpl.class);

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

	public GroupImpl(JpaQueryHandler finderHandler, JpaQueryImpl finderImpl) {
		super();
		this.finderHandler = finderHandler;
		this.finderImpl = finderImpl;
	}

	public QueryContent toQueryContent() {
		return finderImpl.finderRender.toGroup(finderImpl, this);
	}

	public GroupPath get(Object obj) {
		Object arg = finderHandler.getPathInfo();
		arg = arg == null ? obj : arg;
		GroupPath path = new GroupPathImpl(finderHandler, finderImpl, this, arg);
		paths.add(path);
		return path;
	}

	public QueryAppender append(String queryString, Object... args) {
		QueryAppenderImpl queryAppenderImpl = new QueryAppenderImpl(finderImpl, queryString, args);
		paths.add(queryAppenderImpl);
		return queryAppenderImpl;
	}

}
