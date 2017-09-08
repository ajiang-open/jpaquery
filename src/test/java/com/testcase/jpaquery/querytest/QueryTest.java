package com.testcase.jpaquery.querytest;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jpaquery.core.Querys;
import com.jpaquery.core.facade.JpaQuery;
import com.testcase.jpaquery.schema.Clazz;
import com.testcase.jpaquery.schema.Student;
import com.testcase.jpaquery.schema.Teacher;

public class QueryTest {

	Logger logger = LoggerFactory.getLogger(getClass());

	// @Test
	public void test1() {
		JpaQuery jpaQuery = Querys.newJpaQuery();
		Student modelStudent = jpaQuery.from(Student.class);
		jpaQuery.select(modelStudent.getClazz());
		Clazz modelClazz = jpaQuery.join(modelStudent.getTeachers().get(0).getClazzs()).left();
		jpaQuery.on(modelClazz).get(modelClazz.getName()).equal("aaa");
		jpaQuery.where(modelStudent.getName()).equal("张三");
		logger.info(jpaQuery.toQueryContent().toString());
	}

	// @Test
	public void test2() {
		JpaQuery jpaQuery = Querys.newJpaQuery();
		Clazz modelClazz = jpaQuery.from(Clazz.class);
		{
			JpaQuery subJpaQuery = jpaQuery.subJpaQuery();
			Student modelStudent = subJpaQuery.from(Student.class);
			subJpaQuery.where(modelStudent.getClazz()).equal(modelClazz);
			subJpaQuery.select(modelStudent.getId());
			jpaQuery.where().exists(subJpaQuery);
		}
		logger.info(jpaQuery.toQueryContent().toString());
	}

	/**
	 * 多级Join展示
	 */
	// @Test
	public void test3() {
		JpaQuery jpaQuery = Querys.newJpaQuery();
		Student modelStudent = jpaQuery.from(Student.class);
		{
			Clazz modelClazz = jpaQuery.join(modelStudent.getClazz()).left();
			jpaQuery.on(modelClazz).get(modelClazz.getName()).equal("aaa");
			{
				Teacher modelTeacher = jpaQuery.join(modelClazz.getTeachers()).inner();
				jpaQuery.on(modelTeacher).get(modelTeacher.getName()).equal("Miss Wang");
			}
		}
		jpaQuery.where(modelStudent.getName()).equal("张三");
		logger.info(jpaQuery.toQueryContent().toString());
	}

	@Test
	public void test4() {
		JpaQuery jpaQuery = Querys.newJpaQuery();
		Student modelStudent = jpaQuery.from(Student.class);
		jpaQuery.select(modelStudent.getClazz());
		Clazz modelClazz = jpaQuery.join(modelStudent.getTeachers().get(0).getClazzs()).left();
		jpaQuery.on(modelClazz).get(modelClazz.getName()).equal("aaa");
		jpaQuery.where(modelStudent.getName()).notIlikeAll("张三");
		jpaQuery.where(modelStudent.getName()).likeAll(modelStudent.getClazz().getName());
		logger.info(jpaQuery.toQueryContent().toString());
	}
}
