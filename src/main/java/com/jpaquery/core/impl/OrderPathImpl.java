package com.jpaquery.core.impl;

import com.jpaquery.core.facade.Order;
import com.jpaquery.core.facade.OrderPath;

public class OrderPathImpl implements OrderPath {

	JpaQueryHandler finderHandler;
	JpaQueryImpl finderImpl;
	OrderImpl orderImpl;
	Object arg;
	OrderPathType orderPathType = OrderPathType.asc;

	public JpaQueryHandler getFinderHandler() {
		return finderHandler;
	}

	public JpaQueryImpl getFinderImpl() {
		return finderImpl;
	}

	public OrderImpl getOrderImpl() {
		return orderImpl;
	}

	public Object getArg() {
		return arg;
	}

	public OrderPathType getOrderPathType() {
		return orderPathType;
	}

	public OrderPathImpl(JpaQueryHandler finderHandler, JpaQueryImpl finderImpl,
			OrderImpl orderImpl, Object arg) {
		super();
		this.finderHandler = finderHandler;
		this.finderImpl = finderImpl;
		this.orderImpl = orderImpl;
		this.arg = arg;
	}

	public Order asc() {
		orderPathType = OrderPathType.asc;
		return orderImpl;
	}

	public Order desc() {
		orderPathType = OrderPathType.desc;
		return orderImpl;
	}

}
