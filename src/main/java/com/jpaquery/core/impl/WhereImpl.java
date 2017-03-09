package com.jpaquery.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jpaquery.core.facade.And;
import com.jpaquery.core.facade.JpaQuery;
import com.jpaquery.core.facade.Or;
import com.jpaquery.core.facade.QueryAppender;
import com.jpaquery.core.facade.Where;
import com.jpaquery.core.facade.WherePath;
import com.jpaquery.core.facade.SubFinder.SubFinderType;
import com.jpaquery.core.vo.EntityInfo;
import com.jpaquery.core.vo.QueryContent;

/**
 * Where实现
 * 
 * @author lujijiang
 * 
 */
public class WhereImpl implements Where, Or, And {

	static Logger log = LoggerFactory.getLogger(WhereImpl.class);

	/**
	 * 连接类型
	 */
	WhereType type;

	/**
	 * finder实现类
	 */
	JpaQueryImpl finderImpl;

	/**
	 * finder处理器
	 */
	JpaQueryHandler finderHandler;

	/**
	 * where路径
	 */
	List<Object> wherePaths = new ArrayList<>();

	/**
	 * 个性化映射
	 */
	Map<Long, EntityInfo<?>> entityInfoMap;

	public WhereType getType() {
		return type;
	}

	public JpaQueryImpl getFinderImpl() {
		return finderImpl;
	}

	public List<Object> getWherePaths() {
		return wherePaths;
	}

	public JpaQueryHandler getFinderHandler() {
		return finderHandler;
	}

	public Map<Long, EntityInfo<?>> getEntityInfoMap() {
		return entityInfoMap;
	}

	public WhereImpl(JpaQueryHandler finderHandler, JpaQueryImpl finderImpl, WhereType type,
			Map<Long, EntityInfo<?>> entityInfoMap) {
		this.finderImpl = finderImpl;
		this.finderHandler = finderHandler;
		this.type = type;
		this.entityInfoMap = entityInfoMap;
	}

	public QueryContent toQueryContent() {
		return finderImpl.finderRender.toWhere(finderImpl, this);
	}

	public <T> WherePath<T> get(T obj) {
		Object arg = finderHandler.getPathInfo();
		arg = arg == null ? obj : arg;
		WherePathImpl<T> wherePath = new WherePathImpl<T>(finderHandler, finderImpl, this, arg);
		wherePaths.add(wherePath);
		return wherePath;
	}

	public And and() {
		WhereImpl subWhereImpl = new WhereImpl(finderHandler, finderImpl, Where.WhereType.and, entityInfoMap);
		wherePaths.add(subWhereImpl);
		return subWhereImpl;
	}

	public Or or() {
		WhereImpl subWhereImpl = new WhereImpl(finderHandler, finderImpl, Where.WhereType.or, entityInfoMap);
		wherePaths.add(subWhereImpl);
		return subWhereImpl;
	}

	public Where exists(JpaQuery subFinder) {
		SubFinderImpl subFinderImpl = new SubFinderImpl((JpaQueryImpl) subFinder, SubFinderType.exists);
		wherePaths.add(subFinderImpl);
		return this;
	}

	public Where notExists(JpaQuery subFinder) {
		SubFinderImpl subFinderImpl = new SubFinderImpl((JpaQueryImpl) subFinder, SubFinderType.notExists);
		wherePaths.add(subFinderImpl);
		return this;
	}

	public QueryAppender append(String queryString, Object... args) {
		QueryAppenderImpl queryAppenderImpl = new QueryAppenderImpl(finderImpl, queryString, args);
		wherePaths.add(queryAppenderImpl);
		return queryAppenderImpl;
	}

}
