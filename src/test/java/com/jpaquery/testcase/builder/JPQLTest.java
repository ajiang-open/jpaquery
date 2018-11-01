package com.jpaquery.testcase.builder;

import java.io.IOException;

import org.junit.Test;

import com.jpaquery.builder.JPQL;

public class JPQLTest {
	@Test
	public void testJPQL() {
		JPQL jpql = JPQL.create();
		jpql.append("select name,?", "aaaaa").append("from User where name=?", "tester");
		System.out.println(jpql);
	}

	@Test
	public void testCommand() {
		try {
			Runtime.getRuntime().exec(
					"/bin/sh -c 'gpg' '--use-agent' '--batch' '--passphrase-fd' '0' '--armor' '--detach-sign' '--no-tty' '--output' '/Users/lujijiang/git/jpaquery/target/jpaquery-0.1.jar.asc' '/Users/lujijiang/git/jpaquery/target/jpaquery-0.1.jar'");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
