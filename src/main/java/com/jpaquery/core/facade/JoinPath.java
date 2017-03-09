package com.jpaquery.core.facade;

public interface JoinPath<T> {
	enum JoinPathType {
		inner, left, right, full
	}

	T inner();

	T left();

	T right();

	T full();
}
