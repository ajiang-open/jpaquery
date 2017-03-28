package com.jpaquery.core.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jpaquery.core.facade.Join;
import com.jpaquery.core.facade.JoinPath;
import com.jpaquery.core.vo.EntityInfo;
import com.jpaquery.core.vo.FromInfo;
import com.jpaquery.core.vo.PathInfo;
import com.jpaquery.util._Helper;

public class JoinImpl implements Join {

	static Logger log = LoggerFactory.getLogger(JoinImpl.class);

	JpaQueryHandler finderHandler;
	JpaQueryImpl finderImpl;

	Map<Long, JoinPathImpl<?>> joinPathMap = new HashMap<>();

	public JpaQueryHandler getFinderHandler() {
		return finderHandler;
	}

	public JpaQueryImpl getFinderImpl() {
		return finderImpl;
	}

	public Map<Long, JoinPathImpl<?>> getJoinPathMap() {
		return joinPathMap;
	}

	public JoinImpl(JpaQueryHandler finderHandler, JpaQueryImpl finderImpl) {
		super();
		this.finderHandler = finderHandler;
		this.finderImpl = finderImpl;
	}

	@SuppressWarnings("unchecked")
	public <T> JoinPath<T> get(T[] array) {
		T proxy = (T) Array.get(array, 0);
		return getJoinPath(proxy);
	}

	public <T> JoinPath<T> get(Collection<T> list) {
		T proxy = list.iterator().next();
		return getJoinPath(proxy);
	}

	public <T> JoinPath<T> get(T obj) {
		return getJoinPath(obj);
	}

	private <T> JoinPath<T> getJoinPath(T proxy) {
		PathInfo pathInfo = finderHandler.getPathInfo();
		Class<T> componentType = getComponentTypeByGetter(pathInfo.getGetter());
		if (!joinPathMap.containsKey(_Helper.identityHashCode(proxy))) {
			// 为了性能最大化，尽量使用原有的代理对象，如果原代理对象已经被使用，则重新生成代理对象
			proxy = finderHandler.proxy(null, componentType);
		}

		EntityInfo<T> entityInfo = new EntityInfo<T>(finderHandler, componentType, proxy);
		JoinPathImpl<T> joinPath = new JoinPathImpl<T>(finderHandler, finderImpl, this, entityInfo, pathInfo);
		FromInfo fromInfo = finderImpl.getCurrentFromInfos().get(pathInfo.getRootKey());
		fromInfo.getJoinPaths().add(joinPath);
		// 特设带JoinPath的ID的别名以示和正常实体类型的区别
		// entityInfo.setAlias(entityInfo
		// .getAlias()
		// .concat("_")
		// .concat(String.valueOf(finderHandler
		// .generateEntityIndex(JoinPathImpl.class))));
		joinPathMap.put(entityInfo.getKey(), joinPath);
		joinPath.getWhereImpl().getEntityInfoMap().put(entityInfo.getKey(), entityInfo);
		finderImpl.getSelectImpl().getEntityInfoMap().put(entityInfo.getKey(), entityInfo);
		return joinPath;
	}

	@SuppressWarnings("unchecked")
	private <T> Class<T> getComponentTypeByGetter(Method getter) {
		if (getter.getReturnType().isArray()) {
			return (Class<T>) getter.getReturnType().getComponentType();
		}
		if (Collection.class.isAssignableFrom(getter.getReturnType())) {
			return (Class<T>) _Helper.getGenricReturnType(getter, 0);
		}
		return (Class<T>) getter.getReturnType();
	}

}
