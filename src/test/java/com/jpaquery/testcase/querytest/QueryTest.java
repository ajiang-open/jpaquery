package com.jpaquery.testcase.querytest;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jpaquery.core.Querys;
import com.jpaquery.core.facade.JpaQuery;
import com.jpaquery.testcase.schema.Clazz;
import com.jpaquery.testcase.schema.Student;

public class QueryTest {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void test1() {
		JpaQuery jpaQuery = Querys.newJpaQuery();
		Student modelStudent = jpaQuery.from(Student.class);
		jpaQuery.select(modelStudent.getClazz());
		Clazz modelClazz = jpaQuery.join(modelStudent.getTeachers().get(0).getClazzs()).left();
		jpaQuery.on(modelClazz).get(modelClazz.getName()).equal("aaa");
		jpaQuery.where(modelStudent.getName()).equal("张三");
		logger.info(jpaQuery.toQueryContent().toString());
	}
}
