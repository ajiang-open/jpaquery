package com.jpaquery.core.facade;

public interface Select extends QueryRender {

	<T> SelectPath<T> get(T obj);

	QueryAppender append(String queryString, Object... args);

}
