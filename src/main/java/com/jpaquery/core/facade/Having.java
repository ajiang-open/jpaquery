package com.jpaquery.core.facade;

public interface Having extends QueryRender {

	<T> HavingPath<T> get(T obj);

	QueryAppender append(String queryString, Object... args);

}
