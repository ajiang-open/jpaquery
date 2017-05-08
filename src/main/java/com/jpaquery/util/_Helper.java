package com.jpaquery.util;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class _Helper {

	/**
	 * 空类型数组
	 */
	public static final Class<?>[] EMPTY_TYPES = new Class<?>[] {};
	/**
	 * 空对象数组
	 */
	public static final Object[] EMPTY_ARRAY = new Object[0];

	/**
	 * 获取对象系统引用哈希值（不为负数）
	 * 
	 * @param x
	 * @return
	 */
	public static long identityHashCode(Object x) {
		return (long) System.identityHashCode(x) + (long) Integer.MAX_VALUE;
	}

	/**
	 * 判断一个对象是否是空对象
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		if (obj instanceof Map) {
			return ((Map) obj).isEmpty();
		}
		if (obj instanceof Collection) {
			return ((Collection) obj).isEmpty();
		}
		if (obj.getClass().isArray()) {
			return Array.getLength(obj) == 0;
		}
		if (obj instanceof CharSequence) {
			return obj.toString().trim().length() == 0;
		}
		return Object.class.equals(obj.getClass());
	}

	/**
	 * 判断一个类型是否是基本类型
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isBaseType(Class<?> type) {
		if (type.isPrimitive()) {
			return true;
		}
		if (type.isEnum()) {
			return true;
		}
		if (type.isArray()) {
			return true;
		}
		if (Modifier.isFinal(type.getModifiers())) {
			return true;
		}
		if (CharSequence.class.isAssignableFrom(type)) {
			return true;
		}
		if (Number.class.isAssignableFrom(type)) {
			return true;
		}
		if (Date.class.isAssignableFrom(type)) {
			return true;
		}
		if (Boolean.class.equals(type)) {
			return true;
		}
		if (Character.class.equals(type)) {
			return true;
		}
		if (Class.class.equals(type)) {
			return true;
		}
		if (StringBuilder.class.equals(type)) {
			return true;
		}
		if (StringBuffer.class.equals(type)) {
			return true;
		}
		if (Object.class.equals(type)) {
			return true;
		}
		if (Void.class.equals(type)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是数字类型
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isNumber(Class<?> type) {
		if (Number.class.isAssignableFrom(type)) {
			return true;
		}
		if (type.equals(int.class)) {
			return true;
		}
		if (type.equals(short.class)) {
			return true;
		}
		if (type.equals(long.class)) {
			return true;
		}
		if (type.equals(float.class)) {
			return true;
		}
		if (type.equals(double.class)) {
			return true;
		}
		if (type.equals(byte.class)) {
			return true;
		}
		return false;
	}

	/**
	 * 获得方法返回类型的参数泛型
	 * 
	 * @param method
	 * @param index
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Class getGenricReturnType(final Method method, final int index) {
		Type genericReturnType = method.getGenericReturnType();
		if (genericReturnType != null) {
			if (genericReturnType instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				if (actualTypeArguments.length > index) {
					return (Class) actualTypeArguments[index];
				}
			}
		}
		return Object.class;
	}

	/**
	 * 查找调用者
	 * 
	 * @param excludeBasePackage
	 *            过滤基础包名
	 * @return 返回基础包之外的首个调用者
	 */
	public static String findCaller(String excludeBasePackage) {
		String caller = "<UnkownCaller>";
		StackTraceElement[] stackTraceElements = new Exception().getStackTrace();
		for (StackTraceElement stackTraceElement : stackTraceElements) {
			if (!stackTraceElement.getClassName().startsWith(excludeBasePackage)) {
				caller = stackTraceElement.getClassName().concat(".").concat(stackTraceElement.getMethodName())
						.concat(":").concat(String.valueOf(stackTraceElement.getLineNumber()));
				break;
			}
		}
		return caller;
	}

	/**
	 * 查找调用者
	 * 
	 * @return 返回非JpaQuery本身的调用者
	 */
	public static String findCaller() {
		return findCaller("com.jpaquery");
	}
}
