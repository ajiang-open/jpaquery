package com.jpaquery.testcase.querytest;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jpaquery.core.Querys;
import com.jpaquery.core.facade.JpaQuery;
import com.jpaquery.example.Examples;
import com.jpaquery.testcase.Gender;
import com.jpaquery.testcase.schema.Student;
import com.jpaquery.testcase.vo.VClazz;
import com.jpaquery.testcase.vo.VStudent;

public class ExampleTest {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void test1() {
		VStudent vStudent = new VStudent();
		vStudent.setName("小明");
		vStudent.setNickName("aaaaa");
		vStudent.setGender(Gender.female);
		vStudent.setClazz(new VClazz());
		vStudent.getClazz().setName("A班");
		vStudent.getClazz().setOrder(1);
		JpaQuery jpaQuery = Querys.newJpaQuery();
		Student modelStudent = jpaQuery.from(Student.class);
		Examples.create().toJpaQuery(jpaQuery, vStudent, modelStudent);
		logger.info(jpaQuery.toQueryContent().toString());
	}
}
