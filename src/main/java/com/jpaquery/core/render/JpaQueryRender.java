package com.jpaquery.core.render;

import com.jpaquery.core.impl.GroupImpl;
import com.jpaquery.core.impl.HavingImpl;
import com.jpaquery.core.impl.JpaQueryImpl;
import com.jpaquery.core.impl.OrderImpl;
import com.jpaquery.core.impl.SelectImpl;
import com.jpaquery.core.impl.WhereImpl;
import com.jpaquery.core.vo.QueryContent;

public interface JpaQueryRender {

	QueryContent toFrom(JpaQueryImpl finderImpl);

	QueryContent toWhere(JpaQueryImpl finderImpl, WhereImpl whereImpl);

	QueryContent toSelect(JpaQueryImpl finderImpl, SelectImpl selectImpl);

	QueryContent toOrder(JpaQueryImpl finderImpl, OrderImpl orderImpl);

	QueryContent toGroup(JpaQueryImpl finderImpl, GroupImpl groupImpl);

	QueryContent toHaving(JpaQueryImpl finderImpl, HavingImpl havingImpl);

	QueryContent toSelectCount(JpaQueryImpl finderImpl, SelectImpl selectImpl);

}
