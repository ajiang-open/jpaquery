package com.jpaquery.core.impl;

import com.jpaquery.core.facade.JoinHandler;
import com.jpaquery.core.facade.JoinPath;
import com.jpaquery.core.facade.Where;
import com.jpaquery.core.facade.Where.WhereType;
import com.jpaquery.core.vo.EntityInfo;
import com.jpaquery.core.vo.FromInfo;
import com.jpaquery.core.vo.PathInfo;
import com.jpaquery.util._Helper;
import com.jpaquery.util._MergeMap;

import java.util.LinkedList;

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
		this.whereImpl = new WhereImpl(finderHandler, finderImpl, WhereType.and, new _MergeMap<Long, EntityInfo<?>>());
	}

	private void handleJoin(JoinHandler<T> joinHandler) {
		T model = entityInfo.getProxy();
		JoinPathImpl<?> joinPathImpl = joinImpl.getJoinPathMap().get(_Helper.identityHashCode(model));
		LinkedList<Where> ons = finderImpl.joinOnHolder.get();
		if (ons == null) {
			ons = new LinkedList<Where>();
			finderImpl.joinOnHolder.set(ons);
		}
		try {
			ons.addFirst(joinPathImpl.getWhereImpl());
			joinHandler.handle(model);
		} finally {
			ons.removeFirst();
		}
	}

	public void inner(JoinHandler<T> joinHandler) {
		this.joinPathType = JoinPathType.inner;
		handleJoin((JoinHandler<T>) joinHandler);
	}

	public void left(JoinHandler<T> joinHandler) {
		this.joinPathType = JoinPathType.left;
		handleJoin(joinHandler);
	}

	public void right(JoinHandler<T> joinHandler) {
		this.joinPathType = JoinPathType.right;
		handleJoin(joinHandler);
	}

	public void full(JoinHandler<T> joinHandler) {
		this.joinPathType = JoinPathType.full;
		handleJoin(joinHandler);
	}

}
