package com.jpaquery.core.impl;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jpaquery.core.vo.PathInfo;
import com.jpaquery.util._Helper;
import com.jpaquery.util._Proxys;

/**
 * finder处理和工具类
 * 
 * @author lujijiang
 * 
 */
public class JpaQueryHandler {

	/**
	 * 日志器
	 */
	private static Logger log = LoggerFactory.getLogger(JpaQueryHandler.class);

	/**
	 * 路径构建线程保持对象
	 */
	private ThreadLocal<PathInfo> pathInfoLocal = new ThreadLocal<PathInfo>();

	/**
	 * 实习序号池
	 */
	private Map<Class<?>, Integer> entityIndexPool = new ConcurrentHashMap<Class<?>, Integer>();

	/**
	 * 代理类型
	 * 
	 * @param rootProxy
	 *            根代理对象
	 * @param type
	 *            代理类型
	 * @return
	 */
	<T> T proxy(final Object rootProxy, final Class<T> type) {
		if (_Helper.isBaseType(type)) {
			return null;
		}

		return _Proxys.newProxyInstance(new InvocationHandler() {
			/**
			 * 缓存代理对象
			 */
			Map<String, Object> cacheProxyMap = new ConcurrentHashMap<String, Object>();

			public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
				String propertyName = getterMethodPropertyName(method);

				if (propertyName == null) {
					return new UnsupportedOperationException(
							String.format("The method %s of object %s@%d is unsupported", method.getName(),
									proxy.getClass().getCanonicalName(), System.identityHashCode(proxy)));
				}

				setPathInfo(rootProxy, proxy, propertyName, method);

				Class<?> returnType = method.getReturnType();
				// Map类型不做代理
				if (Map.class.isAssignableFrom(returnType)) {
					return null;
				}

				// 是数组，则初始化一个长度为1的数组
				if (returnType.isArray()) {
					Class<?> componentType = returnType.getComponentType();
					Object array = Array.newInstance(componentType, 1);
					Object subProxy = subProxy(rootProxy, proxy, propertyName, componentType);
					Array.set(array, 0, subProxy);
					return array;
				}
				// 是List集合，则初始化指定类型的集合，并且调用集合的get方法返回的都将是固定的对象
				if (List.class.isAssignableFrom(returnType)) {
					Class<?> componentType = _Helper.getGenricReturnType(method, 0);
					final Object subProxy = subProxy(rootProxy, proxy, propertyName, componentType);
					return _Proxys.newProxyInstance(new InvocationHandler() {
						public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
							if ("get".equals(method.getName())) {
								if (method.getParameterTypes().length == 1
										&& int.class.equals(method.getParameterTypes()[0])) {
									return subProxy;
								}
							}
							if ("iterator".equals(method.getName())) {
								if (method.getParameterTypes().length == 0) {
									return new Iterator<Object>() {

										int size = 1;

										public boolean hasNext() {
											return size-- > 0;
										}

										public Object next() {
											return subProxy;
										}

										public void remove() {
											throw new UnsupportedOperationException();
										}
									};
								}
							}
							return new UnsupportedOperationException(
									String.format("The method %s of object %s@%d is unsupported", method.getName(),
											proxy.getClass().getCanonicalName(), System.identityHashCode(proxy)));
						}
					}, returnType);
				}

				// Set类型
				if (Set.class.isAssignableFrom(returnType)) {
					Class<?> componentType = _Helper.getGenricReturnType(method, 0);
					final Object subProxy = subProxy(rootProxy, proxy, propertyName, componentType);
					return _Proxys.newProxyInstance(new InvocationHandler() {
						public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
							if ("iterator".equals(method.getName())) {
								if (method.getParameterTypes().length == 0) {
									return new Iterator<Object>() {

										int size = 1;

										public boolean hasNext() {
											return size-- > 0;
										}

										public Object next() {
											return subProxy;
										}

										public void remove() {
											throw new UnsupportedOperationException();
										}
									};
								}
							}
							return new UnsupportedOperationException(
									String.format("The method %s of object %s@%d is unsupported", method.getName(),
											proxy.getClass().getCanonicalName(), System.identityHashCode(proxy)));
						}
					}, returnType);
				}

				return subProxy(rootProxy, proxy, propertyName, returnType);
			}

			private Object subProxy(final Object rootProxy, Object proxy, String propertyName, Class<?> componentType) {
				if (_Helper.isBaseType(componentType)) {
					return null;
				}
				Object subProxy = cacheProxyMap.get(propertyName);
				if (subProxy == null) {
					subProxy = proxy(rootProxy == null ? proxy : rootProxy, componentType);
					cacheProxyMap.put(propertyName, subProxy);
				}
				return subProxy;
			}
		}, type);
	}

	/**
	 * 注册路径信息
	 * 
	 * @param rootProxy
	 * @param proxy
	 * @param propertyName
	 */
	private void setPathInfo(Object rootProxy, Object proxy, String propertyName, Method getter) {

		PathInfo pathInfo = pathInfoLocal.get();
		if (pathInfo != null) {
			if (rootProxy == null) {
				// 如果没有根对象，说明调用了某个代理对象的第一层getter方法，重置pathInfo
				pathInfo = null;
			} else if (proxy == pathInfo.getRootProxy()) {
				// 如果根代理对象和当前代理对象一致，说明根对象的getter调用未被清除，重置pathInfo
				pathInfo = null;
			} else if (System.identityHashCode(rootProxy) != System.identityHashCode(pathInfo.getRootProxy())) {
				// 如果当前pathInfo中的根对象和传入的根对象不一致，说明调用了不同的代理对象的getter方法，重置pathInfo
				pathInfo = null;
			}
		}

		if (pathInfo == null) {
			pathInfo = new PathInfo(proxy, proxy, new StringBuilder().append(propertyName), getter);
		} else {
			pathInfo.setCurrentProxy(proxy);
			pathInfo.setGetter(getter);
			pathInfo.getPathBuilder().append('.');
			pathInfo.getPathBuilder().append(propertyName);
		}
		pathInfoLocal.set(pathInfo);
	}

	/**
	 * 根据getter方法获取属性名，如果这是一个属性，则返回属性名，否则返回false
	 * 
	 * @param method
	 * @return
	 */
	private String getterMethodPropertyName(Method method) {

		Class<?> returnType = method.getReturnType();

		if (returnType == null || Void.TYPE.equals(returnType) || Void.class.equals(returnType)) {
			return null;
		}

		if (method.getParameterTypes().length != 0) {
			return null;
		}

		String methodName = method.getName();

		if (Boolean.TYPE.equals(returnType) || Boolean.class.equals(returnType)) {
			if (methodName.startsWith("is") && methodName.length() > 2 && Character.isUpperCase(methodName.charAt(2))) {
				return StringUtils.uncapitalize(methodName.substring(2));
			}
		}

		if (methodName.startsWith("get") && methodName.length() > 3 && Character.isUpperCase(methodName.charAt(3))) {
			return StringUtils.uncapitalize(methodName.substring(3));
		}

		return null;
	}

	/**
	 * 获取路径信息
	 * 
	 * @return
	 */
	PathInfo getPathInfo() {
		PathInfo pathInfo = pathInfoLocal.get();
		if (pathInfo == null) {
			return null;
		}
		pathInfoLocal.remove();
		return pathInfo;
	}

	/**
	 * 获取实体序号，注意每生成一次序号自增
	 * 
	 * @param type
	 * @return
	 */
	public int generateEntityIndex(Class<?> type) {
		Integer index = entityIndexPool.get(type);
		if (index == null) {
			index = 0;
		}
		entityIndexPool.put(type, index + 1);
		return index;
	}

	private int paramIndex = 0;

	public void resetParamIndex() {
		paramIndex = 0;
	}

	public String generateParamName() {
		return "p_".concat(String.valueOf(++paramIndex));
	}
}
