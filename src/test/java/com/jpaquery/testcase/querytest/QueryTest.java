package com.jpaquery.testcase.querytest;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jpaquery.core.Querys;
import com.jpaquery.core.facade.JpaQuery;
import com.jpaquery.testcase.schema.Student;
import com.jpaquery.testcase.schema.Teacher;

public class QueryTest {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void test1() {
		JpaQuery jpaQuery = Querys.newJpaQuery();
		Student modelStudent = jpaQuery.from(Student.class);
		jpaQuery.select(modelStudent.getClazz());
		Teacher modelTeacher = jpaQuery.join(modelStudent.getTeachers()).left();
		jpaQuery.on(modelTeacher).get(modelTeacher.getClazzs().get(100).getName()).equal("aaa");
		jpaQuery.where(modelStudent.getName()).equal("张三");
		logger.info(jpaQuery.toQueryContent().toString());
	}
}
