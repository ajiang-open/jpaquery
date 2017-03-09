package com.jpaquery.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.MethodUtils;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 代理工具类
 * 
 * @author lujijiang
 * 
 */
public class _Proxys {

	private _Proxys() {
	}

	/**
	 * 创建代理对象（只能最多有一个非接口类型）
	 * 
	 * @param invocationHandler
	 *            代理处理器
	 * @param mainType
	 *            主类型
	 * @param otherTypes
	 *            其它类型
	 * @param arguments
	 *            父类需要的构造参数
	 * @return 代理对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newProxyInstance(final InvocationHandler invocationHandler, Class<T> mainType,
			Class<?>[] otherTypes, Object... arguments) {
		List<Class<?>> interfaceTypes = new ArrayList<Class<?>>();
		Class<?> superClass = null;

		if (!mainType.isInterface()) {
			superClass = mainType;
		} else {
			interfaceTypes.add(mainType);
		}
		if (otherTypes != null) {
			for (Class<?> otherType : otherTypes) {
				if (!otherType.isInterface()) {
					if (superClass != null) {
						throw new IllegalArgumentException(String
								.format("Should not be more than one super class:%s and %s", superClass, otherType));
					}
					superClass = otherType;
				} else {
					interfaceTypes.add(otherType);
				}
			}
		}
		if (superClass == null) {
			// 当全部是接口类型时，使用JDK原生代理
			return (T) Proxy.newProxyInstance(getClassLoader(), interfaceTypes.toArray(new Class[0]),
					new InvocationHandler() {
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							return invokeMethod(invocationHandler, proxy, method, args);
						}
					});
		}
		Enhancer en = new Enhancer();
		en.setUseCache(true);
		en.setUseFactory(false);
		en.setSuperclass(superClass);
		en.setInterfaces(interfaceTypes.toArray(_Helper.EMPTY_TYPES));
		en.setCallback(new MethodInterceptor() {
			public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
				return invokeMethod(invocationHandler, obj, method, args);
			}
		});
		if (arguments == null || arguments.length == 0) {
			return (T) en.create();
		} else {
			int argumentLength = arguments.length;
			Class<?> parameterTypes[] = new Class<?>[argumentLength];
			for (int i = 0; i < argumentLength; i++) {
				parameterTypes[i] = arguments[i] == null ? null : arguments[i].getClass();
			}
			try {
				return (T) en.create(parameterTypes, arguments);
			} catch (Exception e) {
				Constructor<?> constructor = getMatchingAccessibleConstructor(superClass, parameterTypes);
				if (constructor == null) {
					throw new IllegalArgumentException(
							String.format("Could not found a constructor of type:%s for arguments types:%s",
									superClass.getCanonicalName(), argumentTypesToString(parameterTypes)));
				}
				return (T) en.create(constructor.getParameterTypes(), arguments);
			}
		}
	}

	/**
	 * 获得系统类加载器
	 * 
	 * @return
	 */
	private static ClassLoader getClassLoader() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		return classLoader == null ? _Proxys.class.getClassLoader() : classLoader;
	}

	/**
	 * 尝试寻找匹配的构造器
	 * 
	 * @param clazz
	 * @param parameterTypes
	 * @return
	 */
	private static <T> Constructor<T> getMatchingAccessibleConstructor(Class<T> clazz, Class<?>[] parameterTypes) {
		try {
			Constructor<T> ctor = clazz.getConstructor(parameterTypes);
			try {
				ctor.setAccessible(true);
			} catch (SecurityException se) {
			}
			return ctor;

		} catch (NoSuchMethodException e) {
		}

		int paramSize = parameterTypes.length;
		Constructor<?>[] ctors = clazz.getConstructors();
		for (int i = 0, size = ctors.length; i < size; i++) {
			Class<?>[] ctorParams = ctors[i].getParameterTypes();
			int ctorParamSize = ctorParams.length;
			if (ctorParamSize == paramSize) {
				boolean match = true;
				for (int n = 0; n < ctorParamSize; n++) {
					if (!MethodUtils.isAssignmentCompatible(ctorParams[n], parameterTypes[n])) {
						match = false;
						break;
					}
				}

				if (match) {
					Constructor<?> ctor = ConstructorUtils.getAccessibleConstructor(ctors[i]);
					if (ctor != null) {
						try {
							ctor.setAccessible(true);
						} catch (SecurityException se) {
						}
						@SuppressWarnings("unchecked")
						Constructor<T> typedCtor = (Constructor<T>) ctor;
						return typedCtor;
					}
				}
			}
		}

		return null;
	}

	/**
	 * 输出类型信息
	 * 
	 * @param argTypes
	 * @return
	 */
	private static String argumentTypesToString(Class<?>[] argTypes) {
		StringBuilder buf = new StringBuilder();
		buf.append("(");
		if (argTypes != null) {
			for (int i = 0; i < argTypes.length; i++) {
				if (i > 0) {
					buf.append(", ");
				}
				Class<?> c = argTypes[i];
				buf.append((c == null) ? "null" : c.getName());
			}
		}
		buf.append(")");
		return buf.toString();
	}

	/**
	 * 创建代理对象（只能最多有一个非接口类型）
	 * 
	 * @param invocationHandler
	 *            代理处理器
	 * @param mainType
	 *            主类型
	 * @param otherTypes
	 *            其它类型
	 * @return 代理对象
	 */
	public static <T> T newProxyInstance(final InvocationHandler invocationHandler, Class<T> mainType,
			Class<?>... otherTypes) {
		return newProxyInstance(invocationHandler, mainType, otherTypes, _Helper.EMPTY_ARRAY);
	}

	/**
	 * 创建代理对象（只能最多有一个非接口类型）
	 * 
	 * @param invocationHandler
	 *            代理处理器
	 * @param mainType
	 *            主类型
	 * @param arguments
	 *            构造参数
	 * @return 代理对象
	 */
	public static <T> T newProxyInstance(final InvocationHandler invocationHandler, Class<T> mainType,
			Object... arguments) {
		return newProxyInstance(invocationHandler, mainType, _Helper.EMPTY_TYPES, arguments);
	}

	/**
	 * 创建代理对象
	 * 
	 * @param invocationHandler
	 *            代理处理器
	 * @param mainType
	 *            主类型
	 * @return 代理对象
	 */
	public static <T> T newProxyInstance(final InvocationHandler invocationHandler, Class<T> mainType) {
		return newProxyInstance(invocationHandler, mainType, _Helper.EMPTY_TYPES);
	}

	/**
	 * 实际执行方法
	 * 
	 * @param invocationHandler
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 * @throws Exception
	 */
	private static Object invokeMethod(final InvocationHandler invocationHandler, Object proxy, Method method,
			Object[] args) throws Throwable {
		return invocationHandler.invoke(proxy, method, args);
	}
}