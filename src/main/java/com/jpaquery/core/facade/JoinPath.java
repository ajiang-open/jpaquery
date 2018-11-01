package com.jpaquery.core.facade;

public interface JoinPath<T> {
	enum JoinPathType {
		inner, left, right, full
	}

	void inner(JoinHandler<T> joinHandler);

	void left(JoinHandler<T> joinHandler);

	void right(JoinHandler<T> joinHandler);

	void full(JoinHandler<T> joinHandler);
}
