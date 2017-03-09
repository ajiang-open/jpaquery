package com.jpaquery.core.facade;

public interface HavingFunctionPath<T> extends HavingPath<T> {
	Having equal(T obj);

	Having notEqual(T obj);

	Having greatThan(T obj);

	Having greatEqual(T obj);

	Having lessThan(T obj);

	Having lessEqual(T obj);

	Having equal(JpaQuery subFinder);

	Having notEqual(JpaQuery subFinder);

	Having greatThan(JpaQuery subFinder);

	Having greatEqual(JpaQuery subFinder);

	Having lessThan(JpaQuery subFinder);

	Having lessEqual(JpaQuery subFinder);

	Having equalIfExist(T obj);

	Having notEqualIfExist(T obj);

	Having greatThanIfExist(T obj);

	Having greatEqualIfExist(T obj);

	Having lessThanIfExist(T obj);

	Having lessEqualIfExist(T obj);
}
