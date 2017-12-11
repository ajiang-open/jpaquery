package com.jpaquery.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedList;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import com.jpaquery.core.facade.JpaQuery;
import com.jpaquery.core.impl.JpaQueryHandler;
import com.jpaquery.core.impl.JpaQueryImpl;
import com.jpaquery.core.render.impl.JpaQueryRenderImpl;
import com.jpaquery.util._Proxys;

/**
 * Querys工具类
 *
 * @author lujijiang
 *
 */
public class Querys {

	private static ThreadLocal<LinkedList<Boolean>> READONLY_MARK = new ThreadLocal<>();

	/**
	 * 新建一个查询器
	 *
	 * @return
	 */
	public static JpaQuery newJpaQuery() {
		return new JpaQueryImpl(new JpaQueryHandler(), new JpaQueryRenderImpl());
	}

	/**
	 * 代理只读事务，开启缓存
	 *
	 * @param platformTransactionManager
	 * @return
	 */
	public static AbstractPlatformTransactionManager proxyTransactionManager(
			final AbstractPlatformTransactionManager platformTransactionManager) {
		return _Proxys.newProxyInstance(new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if ("doBegin".equals(method.getName())) {
					preDoBegin((TransactionDefinition) args[1]);
				}
				if ("cleanupAfterCompletion".equals(method.getName())) {
					preDoCleanupAfterCompletion();
				}
				if (!method.isAccessible()) {
					method.setAccessible(true);
				}
				Object value = method.invoke(platformTransactionManager, args);
				return value;
			}
		}, AbstractPlatformTransactionManager.class);
	}

	protected static void preDoCleanupAfterCompletion() {
		LinkedList<Boolean> stack = READONLY_MARK.get();
		if (stack == null) {
			stack = new LinkedList();
			READONLY_MARK.set(stack);
		}
		if (!stack.isEmpty()) {
			stack.removeFirst();
		}
	}

	protected static void preDoBegin(TransactionDefinition transactionDefinition) {
		LinkedList<Boolean> stack = READONLY_MARK.get();
		if (stack == null) {
			stack = new LinkedList();
			READONLY_MARK.set(stack);
		}
		if (transactionDefinition.isReadOnly()) {
			stack.addFirst(true);
		} else {
			stack.addFirst(false);
		}
	}

	/**
	 * 判断是否只读事务方法
	 *
	 * @return
	 */
	public static boolean isReadonly() {
		LinkedList<Boolean> stack = READONLY_MARK.get();
		if (stack == null) {
			stack = new LinkedList();
			READONLY_MARK.set(stack);
		}
		if (stack.isEmpty()) {
			return false;
		}
		return stack.getFirst();
	}

}
