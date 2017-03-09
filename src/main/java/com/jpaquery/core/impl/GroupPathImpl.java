package com.jpaquery.core.impl;

import com.jpaquery.core.facade.GroupPath;

public class GroupPathImpl implements GroupPath {

	JpaQueryHandler finderHandler;
	JpaQueryImpl finderImpl;
	GroupImpl groupImpl;
	Object arg;

	public JpaQueryHandler getFinderHandler() {
		return finderHandler;
	}

	public JpaQueryImpl getFinderImpl() {
		return finderImpl;
	}

	public GroupImpl getGroupImpl() {
		return groupImpl;
	}

	public Object getArg() {
		return arg;
	}

	public GroupPathImpl(JpaQueryHandler finderHandler, JpaQueryImpl finderImpl,
			GroupImpl groupImpl, Object arg) {
		super();
		this.finderHandler = finderHandler;
		this.finderImpl = finderImpl;
		this.groupImpl = groupImpl;
		this.arg = arg;
	}

}
