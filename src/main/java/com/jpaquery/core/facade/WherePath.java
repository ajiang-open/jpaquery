package com.jpaquery.core.facade;

public interface WherePath<T> {
	public enum WherePathType {
		equal, notEqual, like, notLike, ilike, notIlike, greatThan, greatEqual, lessThan, lessEqual, between, notBetween, isNull, isNotNull, in, notIn
	}

	Where equal(T obj);

	Where notEqual(T obj);

	Where like(T obj);

	Where notLike(T obj);

	Where likeLeft(T obj);

	Where notLikeLeft(T obj);

	Where likeRight(T obj);

	Where notLikeRight(T obj);

	Where ilike(T obj);

	Where notIlike(T obj);

	Where ilikeLeft(T obj);

	Where notILikeLeft(T obj);

	Where ilikeRight(T obj);

	Where notILikeRight(T obj);

	Where greatThan(T obj);

	Where greatEqual(T obj);

	Where lessThan(T obj);

	Where lessEqual(T obj);

	BetweenPath<T> between(T obj);

	BetweenPath<T> notBetween(T obj);

	Where in(T... objs);

	Where notIn(T... objs);

	Where equal(JpaQuery subFinder);

	Where notEqual(JpaQuery subFinder);

	Where like(JpaQuery subFinder);

	Where notLike(JpaQuery subFinder);

	Where ilike(JpaQuery subFinder);

	Where notIlike(JpaQuery subFinder);

	Where greatThan(JpaQuery subFinder);

	Where greatEqual(JpaQuery subFinder);

	Where lessThan(JpaQuery subFinder);

	Where lessEqual(JpaQuery subFinder);

	BetweenPath<T> between(JpaQuery subFinder);

	BetweenPath<T> notBetween(JpaQuery subFinder);

	Where in(JpaQuery subFinder);

	Where notIn(JpaQuery subFinder);

	Where equalIfExist(T obj);

	Where notEqualIfExist(T obj);

	Where likeIfExist(T obj);

	Where notLikeIfExist(T obj);

	Where likeLeftIfExist(T obj);

	Where notLikeLeftIfExist(T obj);

	Where likeRightIfExist(T obj);

	Where notLikeRightIfExist(T obj);

	Where ilikeIfExist(T obj);

	Where notIlikeIfExist(T obj);

	Where ilikeLeftIfExist(T obj);

	Where notILikeLeftIfExist(T obj);

	Where ilikeRightIfExist(T obj);

	Where notILikeRightIfExist(T obj);

	Where greatThanIfExist(T obj);

	Where greatEqualIfExist(T obj);

	Where lessThanIfExist(T obj);

	Where lessEqualIfExist(T obj);

	BetweenPath<T> betweenIfExist(T obj);

	BetweenPath<T> notBetweenIfExist(T obj);

	Where isNull();

	Where isNotNull();

}