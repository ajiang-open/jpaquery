package com.jpaquery.testcase.querytest;

import com.jpaquery.testcase.schema.Clazz;
import com.jpaquery.testcase.schema.Student;
import com.jpaquery.testcase.schema.Teacher;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jpaquery.core.Querys;

public class QueryTest {

	Logger logger = LoggerFactory.getLogger(getClass());

	// @Test
	public void test1() {
		Querys.query(query -> {
			Student modelStudent = query.from(Student.class);
			query.select(modelStudent.getClazz());
			Clazz modelClazz = query.join(modelStudent.getTeachers().get(0).getClazzs()).left();
			query.on(modelClazz).get(modelClazz.getName()).equal("aaa");
			query.where(modelStudent.getName()).equal("张三");
			logger.info(query.toQueryContent().toString());
			return null;
		});
	}

	// @Test
	public void test2() {
		Querys.query(query -> {
			Clazz modelClazz = query.from(Clazz.class);
			query.subQuery(subQuery->{
				Student modelStudent = subQuery.from(Student.class);
				subQuery.where(modelStudent.getClazz()).equal(modelClazz);
				subQuery.select(modelStudent.getId());
				query.where().exists(subQuery);
			});
			logger.info(query.toQueryContent().toString());
			return null;
		});
	}

	/**
	 * 多级Join展示
	 */
	// @Test
	public void test3() {
		Querys.query(query -> {
			Student modelStudent = query.from(Student.class);
			{
				Clazz modelClazz = query.join(modelStudent.getClazz()).left();
				query.on(modelClazz).get(modelClazz.getName()).equal("aaa");
				{
					Teacher modelTeacher = query.join(modelClazz.getTeachers()).inner();
					query.on(modelTeacher).get(modelTeacher.getName()).equal("Miss Wang");
				}
			}
			query.where(modelStudent.getName()).equal("张三");
			logger.info(query.toQueryContent().toString());
			return null;
		});
	}

	@Test
	public void test5() {
		Querys.query(query -> {
			Student modelStudent = query.from(Student.class);
			query.select(modelStudent.getClazz());
			Clazz modelClazz = query.join(modelStudent.getTeachers().get(0).getClazzs()).left();
			query.on(modelClazz).get(modelClazz.getName()).equal("aaa");
			query.where(modelStudent.getName()).likeAllIfExist("张三");
			query.where(modelStudent.getName()).likeAll(modelStudent.getClazz().getName());
			logger.info(query.toQueryContent().toString());
			return null;
		});
	}
}
