package com.jpaquery.core.impl;

import java.util.concurrent.ConcurrentHashMap;

import com.jpaquery.core.facade.JoinPath;
import com.jpaquery.core.facade.Where.WhereType;
import com.jpaquery.core.vo.EntityInfo;
import com.jpaquery.core.vo.FromInfo;
import com.jpaquery.core.vo.PathInfo;

public class JoinPathImpl<T> implements JoinPath<T> {

	JpaQueryHandler finderHandler;
	JpaQueryImpl finderImpl;
	FromInfo fromInfo;
	JoinImpl joinImpl;
	EntityInfo<T> entityInfo;
	PathInfo pathInfo;
	JoinPathType joinPathType = JoinPathType.inner;
	WhereImpl whereImpl;

	public JpaQueryHandler getFinderHandler() {
		return finderHandler;
	}

	public JpaQueryImpl getFinderImpl() {
		return finderImpl;
	}

	public FromInfo getFromInfo() {
		return fromInfo;
	}

	public void setFromInfo(FromInfo fromInfo) {
		this.fromInfo = fromInfo;
	}

	public JoinImpl getJoinImpl() {
		return joinImpl;
	}

	public EntityInfo<T> getEntityInfo() {
		return entityInfo;
	}

	public PathInfo getPathInfo() {
		return pathInfo;
	}

	public JoinPathType getJoinPathType() {
		return joinPathType;
	}

	public WhereImpl getWhereImpl() {
		return whereImpl;
	}

	public JoinPathImpl(JpaQueryHandler finderHandler, JpaQueryImpl finderImpl, JoinImpl joinImpl,
			EntityInfo<T> entityInfo, PathInfo pathInfo) {
		super();
		this.finderHandler = finderHandler;
		this.finderImpl = finderImpl;
		this.joinImpl = joinImpl;
		this.entityInfo = entityInfo;
		this.pathInfo = pathInfo;
		this.whereImpl = new WhereImpl(finderHandler, finderImpl, WhereType.and,
				new ConcurrentHashMap<Long, EntityInfo<?>>());
	}

	public T inner() {
		this.joinPathType = JoinPathType.inner;
		return entityInfo.getProxy();
	}

	public T left() {
		this.joinPathType = JoinPathType.left;
		return entityInfo.getProxy();
	}

	public T right() {
		this.joinPathType = JoinPathType.right;
		return entityInfo.getProxy();
	}

	public T full() {
		this.joinPathType = JoinPathType.full;
		return entityInfo.getProxy();
	}

}
