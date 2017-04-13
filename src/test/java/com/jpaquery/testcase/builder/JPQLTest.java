package com.jpaquery.testcase.builder;

import org.junit.Test;

import com.jpaquery.builder.JPQL;

public class JPQLTest {
	@Test
	public void testJPQL() {
		JPQL jpql = JPQL.create();
		jpql.append("select name,?", "aaaaa").append("from User where name=?", "tester");
		System.out.println(jpql);
	}
}
