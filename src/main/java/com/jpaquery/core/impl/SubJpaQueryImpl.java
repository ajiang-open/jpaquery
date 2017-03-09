package com.jpaquery.core.impl;

import com.jpaquery.core.facade.SubJpaQuery;

public class SubJpaQueryImpl implements SubJpaQuery {

	JpaQueryImpl jpaQueryImpl;

	SubJpaQueryType subJpaQueryType;

	public SubJpaQueryImpl(JpaQueryImpl jpaQueryImpl, SubJpaQueryType subJpaQueryType) {
		this.jpaQueryImpl = jpaQueryImpl;
		this.subJpaQueryType = subJpaQueryType;
	}

	public JpaQueryImpl getJpaQueryImpl() {
		return jpaQueryImpl;
	}

	public SubJpaQueryType getSubJpaQueryType() {
		return subJpaQueryType;
	}

}
