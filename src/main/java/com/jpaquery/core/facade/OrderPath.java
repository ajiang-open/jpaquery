package com.jpaquery.core.facade;

public interface OrderPath {

	enum OrderPathType {
		asc, desc
	}

	Order asc();

	Order desc();

}
