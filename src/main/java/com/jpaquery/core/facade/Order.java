package com.jpaquery.core.facade;

public interface Order extends QueryRender {

	OrderPath get(Object obj);

	QueryAppender append(String queryString, Object... args);

}
