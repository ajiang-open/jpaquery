package com.jpaquery.example;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.jpaquery.core.facade.JpaQuery;
import com.jpaquery.core.impl.JpaQueryImpl;
import com.jpaquery.core.vo.FromInfo;
import com.jpaquery.util._Helper;

public class Example {

	Example() {

	}

	/**
	 * 包含字段
	 */
	private Set<String> includes = new HashSet<String>();
	/**
	 * 排除字段
	 */
	private Set<String> excludes = new HashSet<String>();

	/**
	 * 是否包含null字段
	 */
	private boolean includeNull;

	/**
	 * 是否包含0，仅针对基本数字类型字段有效
	 */
	private boolean includePrimitiveZero = false;

	/**
	 * 是否包含false，仅针对基本boolean类型字段有效
	 */
	private boolean includePrimitiveFalse = false;

	/**
	 * 是否包含空字段
	 */
	private boolean includeEmpty;

	/**
	 * 需要包括的字段名列表
	 * 
	 * @param names
	 * @return
	 */
	public Example includes(String... names) {
		if (names == null) {
			throw new IllegalArgumentException("The names argument should not be null");
		}
		for (String name : names) {
			if (name == null) {
				throw new IllegalArgumentException("The names argument should not contain null");
			}
			name = name.trim();
			includes.add(name);
		}
		return this;
	}

	/**
	 * 需要排除的字段名列表
	 * 
	 * @param names
	 * @return
	 */
	public Example excludes(String... names) {
		if (names == null) {
			throw new IllegalArgumentException("The names argument should not be null");
		}
		for (String name : names) {
			if (name == null) {
				throw new IllegalArgumentException("The names argument should not contain null");
			}
			name = name.trim();
			excludes.add(name);
		}
		return this;
	}

	/**
	 * 是否包含null字段
	 * 
	 * @return
	 */
	public Example includeNull() {
		includeNull = true;
		return this;
	}

	/**
	 * 是否排除null字段
	 * 
	 * @return
	 */
	public Example excludeNull() {
		includeNull = false;
		return this;
	}

	/**
	 * 是否包含空字段
	 * 
	 * @return
	 */
	public Example includeEmpty() {
		includeEmpty = true;
		return this;
	}

	/**
	 * 是否排除空字段
	 * 
	 * @return
	 */
	public Example excludeEmpty() {
		includeEmpty = false;
		return this;
	}

	/**
	 * 是否包含值为0的基本类型字段
	 * 
	 * @return
	 */
	public Example includePrimitiveZero() {
		includePrimitiveZero = true;
		return this;
	}

	/**
	 * 是否排除值为0的基本类型字段
	 * 
	 * @return
	 */
	public Example excludePrimitiveZero() {
		includePrimitiveZero = false;
		return this;
	}

	/**
	 * 是否包含值为false的基本类型字段
	 * 
	 * @return
	 */
	public Example includePrimitiveFalse() {
		includePrimitiveFalse = true;
		return this;
	}

	/**
	 * 是否排除值为false的基本类型字段
	 * 
	 * @return
	 */
	public Example excludePrimitiveFalse() {
		includePrimitiveFalse = false;
		return this;
	}

	private Class<? extends Object> excludeProxy(Class<?> type) {
		while (type.getSimpleName().contains("$")) {
			type = type.getSuperclass();
		}
		return type;
	}

	/**
	 * @param jpaQuery
	 *            查找器
	 * @param example
	 *            样例对象
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> void toJpaQuery(JpaQuery jpaQuery, Object example) {
		JpaQueryImpl jpaQueryImpl = (JpaQueryImpl) jpaQuery;
		Iterator<FromInfo> iterator = jpaQueryImpl.froms().iterator();
		if (!iterator.hasNext()) {
			throw new IllegalStateException("The jpaQuery should from some entity class first");
		}
		T entityModel = (T) iterator.next().getEntityInfo().getProxy();
		toJpaQuery(jpaQuery, example, entityModel);
	}

	/**
	 * 以样例填充Finder，除了Exampe的规则之外，仅对字符串、数字和日期类型有效
	 * 
	 * @param jpaQuery
	 *            查找器
	 * 
	 * @param entityClass
	 *            实体类型
	 * 
	 * @param example
	 *            样例对象
	 * @return
	 */
	public <T> void toJpaQuery(JpaQuery jpaQuery, Object example, T entityModel) {
		if (jpaQuery == null) {
			throw new IllegalArgumentException("The jpaQuery should not be null");
		}
		if (entityModel == null) {
			throw new IllegalArgumentException("The entityModel should not be null");
		}
		if (example == null) {
			throw new IllegalArgumentException("The example should not be null");
		}
		String alias = jpaQuery.alias(entityModel);
		AtomicInteger paramIndex = new AtomicInteger(0);
		try {
			toJpaQuery(new HashSet<Integer>(), null, example, jpaQuery, alias, paramIndex);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 以样例填充Finder，除了Exampe的规则之外，仅对字符串、数字和日期类型有效
	 * 
	 * @param hashs
	 *            防止循环引用
	 * @param prefixName
	 *            路径前缀
	 * @param example
	 *            样例对象
	 * @param jpaQuery
	 *            查找器
	 * @param alias
	 *            模型别名
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IntrospectionException
	 */
	private void toJpaQuery(Set<Integer> hashs, String prefixPath, Object example, JpaQuery jpaQuery, String alias,
			AtomicInteger paramIndex)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException {
		Integer hash = System.identityHashCode(example);
		if (hashs.contains(hash)) {
			return;
		}
		hashs.add(hash);

		PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(excludeProxy(example.getClass()))
				.getPropertyDescriptors();

		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {

			Class<?> propertyType = propertyDescriptor.getPropertyType();

			String path = propertyDescriptor.getName();

			if (prefixPath != null) {
				path = prefixPath.concat(".").concat(path);
			}

			if (excludes.contains(path)) {
				continue;
			}
			if (!includes.isEmpty() && !includes.contains(path)) {
				continue;
			}

			Method readMethod = propertyDescriptor.getReadMethod();

			if (readMethod == null) {
				continue;
			}

			String name = "e_".concat(Integer.toString(paramIndex.getAndIncrement()));
			Object value = readMethod.invoke(example);

			if (value == null) {
				// 过滤null
				if (includeNull) {
					jpaQuery.where().append(alias.concat(".").concat(path).concat(" is null"));
				}
				continue;
			} else if (CharSequence.class.isAssignableFrom(propertyType)) {
				// 过滤字符串
				if (value.toString().trim().length() == 0) {
					if (!includeEmpty) {
						continue;
					}
				}
				jpaQuery.where().append(alias.concat(".").concat(path).concat(" like :").concat(name)).arg(name,
						String.valueOf(value));
			} else if (propertyType.isPrimitive()) {
				// 过滤基本类型
				if (value != null && value.equals(0)) {
					if (!includePrimitiveZero) {
						continue;
					}
				}
				if (value != null && value.equals(false)) {
					if (!includePrimitiveFalse) {
						continue;
					}
				}
				if (value instanceof Boolean) {
					value = ((Boolean) value).booleanValue();
				}
				jpaQuery.where().append(alias.concat(".").concat(path).concat(" = :").concat(name)).arg(name, value);
			} else {
				// 过滤数字和日期类型等其它类型
				if (Number.class.isAssignableFrom(propertyType) || Boolean.class.equals(propertyType)
						|| Character.class.equals(propertyType) || Date.class.isAssignableFrom(propertyType)) {
					jpaQuery.where().append(alias.concat(".").concat(path).concat(" = :").concat(name)).arg(name,
							value);
				} else {
					if (_Helper.isBaseType(propertyType)) {
						continue;
					}
					if (propertyType.isArray()) {
						continue;
					}
					if (Collection.class.isAssignableFrom(propertyType)) {
						continue;
					}
					if (Map.class.isAssignableFrom(propertyType)) {
						continue;
					}
					toJpaQuery(hashs, path, value, jpaQuery, alias, paramIndex);
				}
			}
		}
	}
}
