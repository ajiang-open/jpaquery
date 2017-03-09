package com.jpaquery.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jpaquery.core.facade.QueryAppender;
import com.jpaquery.core.facade.Select;
import com.jpaquery.core.facade.SelectPath;
import com.jpaquery.core.vo.EntityInfo;
import com.jpaquery.core.vo.QueryContent;

/**
 * Select实现
 * 
 * @author lujijiang
 * 
 */
public class SelectImpl implements Select {

	static Logger log = LoggerFactory.getLogger(SelectImpl.class);

	/**
	 * finder实现类
	 */
	JpaQueryImpl finderImpl;

	/**
	 * finder处理器
	 */
	JpaQueryHandler finderHandler;

	/**
	 * select路径
	 */
	List<Object> selectPaths = new ArrayList<>();
	/**
	 * 个性化映射
	 */
	Map<Long, EntityInfo<?>> entityInfoMap;

	public JpaQueryImpl getFinderImpl() {
		return finderImpl;
	}

	public JpaQueryHandler getFinderHandler() {
		return finderHandler;
	}

	public List<Object> getSelectPaths() {
		return selectPaths;
	}

	public Map<Long, EntityInfo<?>> getEntityInfoMap() {
		return entityInfoMap;
	}

	public SelectImpl(JpaQueryHandler finderHandler, JpaQueryImpl finderImpl, Map<Long, EntityInfo<?>> entityInfoMap) {
		super();
		this.finderImpl = finderImpl;
		this.finderHandler = finderHandler;
		this.entityInfoMap = entityInfoMap;
	}

	public QueryContent toQueryContent() {
		return finderImpl.finderRender.toSelect(finderImpl, this);
	}

	public <T> SelectPath<T> get(T obj) {
		Object arg = finderHandler.getPathInfo();
		arg = arg == null ? obj : arg;
		SelectPath<T> selectPath = new SelectPathImpl<T>(finderHandler, finderImpl, this, arg);
		selectPaths.add(selectPath);
		return selectPath;
	}

	public QueryAppender append(String queryString, Object... args) {
		QueryAppenderImpl queryAppenderImpl = new QueryAppenderImpl(finderImpl, queryString, args);
		selectPaths.add(queryAppenderImpl);
		return queryAppenderImpl;
	}

}
