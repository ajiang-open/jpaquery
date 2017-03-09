package com.jpaquery.core.impl;

import com.jpaquery.core.facade.SubFinder;

public class SubFinderImpl implements SubFinder {

	JpaQueryImpl finderImpl;

	SubFinderType subFinderType;

	public SubFinderImpl(JpaQueryImpl finderImpl, SubFinderType subFinderType) {
		this.finderImpl = finderImpl;
		this.subFinderType = subFinderType;
	}

	public JpaQueryImpl getFinderImpl() {
		return finderImpl;
	}

	public SubFinderType getSubFinderType() {
		return subFinderType;
	}

}
