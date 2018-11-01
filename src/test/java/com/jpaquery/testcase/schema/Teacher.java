package com.jpaquery.testcase.schema;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

@Entity
@Table(indexes = {})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, include = "non-lazy")
@SelectBeforeUpdate(true)
@DynamicUpdate
@DynamicInsert
public class Teacher extends IdEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2674345024644705051L;
	@Column(length = 64, unique = true)
	String name;

	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, include = "non-lazy")
	@ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinTable(joinColumns = @JoinColumn(name = "teacher_id_"), inverseJoinColumns = @JoinColumn(name = "clazz_id_"))
	List<Clazz> clazzs;

	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, include = "non-lazy")
	@ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinTable(joinColumns = @JoinColumn(name = "teacher_id_"), inverseJoinColumns = @JoinColumn(name = "student_id_"))
	List<Student> students;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Clazz> getClazzs() {
		return clazzs;
	}

	public void setClazzs(List<Clazz> clazzs) {
		this.clazzs = clazzs;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

	@Override
	public String toString() {
		return "Teacher [name=" + name + "]";
	}

}
