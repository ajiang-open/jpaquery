package com.jpaquery.core.impl;

import java.sql.Timestamp;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.hql.spi.QueryTranslatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.jpaquery.core.facade.And;
import com.jpaquery.core.facade.Group;
import com.jpaquery.core.facade.GroupPath;
import com.jpaquery.core.facade.Having;
import com.jpaquery.core.facade.HavingPath;
import com.jpaquery.core.facade.Join;
import com.jpaquery.core.facade.JoinPath;
import com.jpaquery.core.facade.JpaQuery;
import com.jpaquery.core.facade.JpaQueryEach;
import com.jpaquery.core.facade.Or;
import com.jpaquery.core.facade.Order;
import com.jpaquery.core.facade.OrderPath;
import com.jpaquery.core.facade.Select;
import com.jpaquery.core.facade.SelectPath;
import com.jpaquery.core.facade.SubJpaQuery;
import com.jpaquery.core.facade.SubJpaQuery.SubJpaQueryType;
import com.jpaquery.core.facade.Where;
import com.jpaquery.core.facade.WherePath;
import com.jpaquery.core.render.JpaQueryRender;
import com.jpaquery.core.vo.EntityInfo;
import com.jpaquery.core.vo.FromInfo;
import com.jpaquery.core.vo.PathInfo;
import com.jpaquery.core.vo.QueryContent;
import com.jpaquery.util._Helper;
import com.jpaquery.util._MergeMap;

/**
 * Finder实现类
 *
 * @author lujijiang
 *
 */
public class JpaQueryImpl implements JpaQuery {

	private static final Logger logger = LoggerFactory.getLogger(JpaQueryImpl.class);
	/**
	 * each遍历时每次查询的数量
	 */
	private static final int EACH_SIZE = 100;
	/**
	 * 子查询
	 */
	List<JpaQueryImpl> subFinderImpls = new ArrayList<>();
	/**
	 * finder处理器
	 */
	JpaQueryHandler finderHandler;
	/**
	 * 父finder实体信息
	 */
	Map<Long, FromInfo> parentFromInfos = new _MergeMap<>();
	/**
	 * finder实体信息
	 */
	Map<Long, FromInfo> currentFromInfos = new _MergeMap<>();
	/**
	 * 默认finder渲染器
	 */
	JpaQueryRender finderRender;

	/**
	 * select子句
	 */
	SelectImpl selectImpl;

	/**
	 * finder主where字句
	 */
	WhereImpl whereImpl;

	/**
	 * order子句
	 */
	OrderImpl orderImpl;
	/**
	 * group子句
	 */
	GroupImpl groupImpl;
	/**
	 * having子句
	 */
	HavingImpl havingImpl;
	/**
	 * join子句
	 */
	JoinImpl joinImpl;
	/**
	 * 父finder对象
	 */
	private JpaQueryImpl parentFinder;

	public List<JpaQueryImpl> getSubFinderImpls() {
		return subFinderImpls;
	}

	public JpaQueryHandler getFinderHandler() {
		return finderHandler;
	}

	public Map<Long, FromInfo> getParentFromInfos() {
		return parentFromInfos;
	}

	public Map<Long, FromInfo> getCurrentFromInfos() {
		return currentFromInfos;
	}

	public JpaQueryRender getFinderRender() {
		return finderRender;
	}

	public SelectImpl getSelectImpl() {
		return selectImpl;
	}

	public WhereImpl getWhereImpl() {
		return whereImpl;
	}

	public OrderImpl getOrderImpl() {
		return orderImpl;
	}

	public GroupImpl getGroupImpl() {
		return groupImpl;
	}

	public HavingImpl getHavingImpl() {
		return havingImpl;
	}

	public JoinImpl getJoinImpl() {
		return joinImpl;
	}

	public JpaQueryImpl(JpaQueryHandler finderHandler, JpaQueryRender finderRender) {
		this.finderHandler = finderHandler;
		this.finderRender = finderRender;
		// 初始化子句
		selectImpl = new SelectImpl(finderHandler, this, new _MergeMap<Long, EntityInfo<?>>());
		whereImpl = new WhereImpl(finderHandler, this, Where.WhereType.and, new _MergeMap<Long, EntityInfo<?>>());
		orderImpl = new OrderImpl(finderHandler, this);
		groupImpl = new GroupImpl(finderHandler, this);
		havingImpl = new HavingImpl(finderHandler, this);
		joinImpl = new JoinImpl(finderHandler, this);
	}

	public JpaQuery subJpaQuery() {
		JpaQueryImpl subFinderImpl = new JpaQueryImpl(finderHandler, finderRender);
		subFinderImpl.parentFinder = this;
		subFinderImpl.getParentFromInfos().putAll(getCurrentFromInfos());
		subFinderImpl.getParentFromInfos().putAll(getParentFromInfos());
		subFinderImpls.add(subFinderImpl);
		return subFinderImpl;
	}

	public <T> T from(Class<T> type) {
		T proxy = finderHandler.proxy(null, type);
		EntityInfo<T> entityInfo = new EntityInfo<T>(finderHandler, type, proxy);
		FromInfo fromInfo = new FromInfo(entityInfo);
		getCurrentFromInfos().put(entityInfo.getKey(), fromInfo);
		return proxy;
	}

	public Where where() {
		return whereImpl;
	}

	public <T> WherePath<T> where(T obj) {
		return where().get(obj);
	}

	public Select select() {
		return selectImpl;
	}

	public <T> SelectPath<T> select(T obj) {
		return select().get(obj);
	}

	public Order order() {
		return orderImpl;
	}

	public OrderPath order(Object obj) {
		return order().get(obj);
	}

	public Group group() {
		return groupImpl;
	}

	public GroupPath group(Object obj) {
		return group().get(obj);
	}

	public Having having() {
		return havingImpl;
	}

	public <T> HavingPath<T> having(T obj) {
		return having().get(obj);
	}

	public Join join() {
		return joinImpl;
	}

	public <T> JoinPath<T> join(Collection<T> list) {
		return join().get(list);
	}

	public <T> JoinPath<T> join(T obj) {
		return join().get(obj);
	}

	public <T> Where on(T join) {
		JoinPathImpl<?> joinPathImpl = joinImpl.getJoinPathMap().get(_Helper.identityHashCode(join));
		if (joinPathImpl == null) {
			throw new IllegalStateException(String.format("对象：%s 不是Finder：%s的Join子句代理对象", this, join));
		}
		return joinPathImpl.getWhereImpl();
	}

	/**
	 * 生成QueryContent
	 *
	 * @param countSwich
	 *            是否是统计查询
	 * @return
	 */
	private QueryContent toQueryContent(boolean countSwich) {
		// 重置参数序号
		if (parentFinder == null) {
			finderHandler.resetParamIndex();
		}
		QueryContent queryContent = new QueryContent();
		// select
		QueryContent selectQueryContent = selectImpl.toQueryContent();
		if (selectQueryContent != null) {
			queryContent.append("select ");
			queryContent.append(selectQueryContent);
		}

		// from
		QueryContent fromQueryContent = finderRender.toFrom(this);
		if (fromQueryContent != null) {
			if (queryContent.length() > 0) {
				queryContent.append(" ");
			}
			queryContent.append("from ");
			queryContent.append(fromQueryContent);
		} else {
			throw new IllegalStateException("Must be exist from statement query");
		}

		// where
		QueryContent whereQueryContent = whereImpl.toQueryContent();
		if (whereQueryContent != null) {
			queryContent.append(" where ");
			queryContent.append(whereQueryContent);
		}

		// group
		QueryContent groupQueryContent = groupImpl.toQueryContent();
		if (groupQueryContent != null) {
			queryContent.append(" group by ");
			queryContent.append(groupQueryContent);
		}

		// having
		QueryContent havingQueryContent = havingImpl.toQueryContent();
		if (havingQueryContent != null) {
			queryContent.append(" having ");
			queryContent.append(havingQueryContent);
		}

		// order
		if (!countSwich) {
			QueryContent orderQueryContent = orderImpl.toQueryContent();
			if (orderQueryContent != null) {
				queryContent.append(" order by ");
				queryContent.append(orderQueryContent);
			}
		}

		return queryContent;
	}

	public QueryContent toQueryContent() {
		return toQueryContent(false);
	}

	public QueryContent toCountQueryContent() {
		return toQueryContent(true);
	}

	public SubJpaQuery any() {
		return new SubJpaQueryImpl(this, SubJpaQueryType.any);
	}

	public SubJpaQuery some() {
		return new SubJpaQueryImpl(this, SubJpaQueryType.some);
	}

	public SubJpaQuery all() {
		return new SubJpaQueryImpl(this, SubJpaQueryType.all);
	}

	public String alias(Object proxyInstance) {
		PathInfo pathInfo = finderHandler.getPathInfo();
		if (pathInfo != null) {
			FromInfo fromInfo = getCurrentFromInfos().get(pathInfo.getRootKey());
			if (fromInfo == null) {
				fromInfo = getParentFromInfos().get(pathInfo.getRootKey());
			}
			if (fromInfo == null) {
				throw new IllegalArgumentException(
						String.format("The info path %s root proxy instance is not valid", pathInfo));
			}
			return fromInfo.getEntityInfo().getAlias().concat(".").concat(pathInfo.getPathBuilder().toString());
		}
		if (proxyInstance == null) {
			throw new IllegalArgumentException(String.format("The proxy instance should't be null"));
		}
		if (proxyInstance instanceof JpaQuery) {
			return ((JpaQuery) proxyInstance).toQueryContent().getQueryString();
		}

		long key = _Helper.identityHashCode(proxyInstance);

		FromInfo fromInfo = getCurrentFromInfos().get(key);
		if (fromInfo == null) {
			fromInfo = getParentFromInfos().get(key);
		}

		if (fromInfo != null) {
			return fromInfo.getEntityInfo().getAlias();
		}
		throw new IllegalStateException(String.format(
				"Should be call a model getter method or argument is model object in this finder or sub finder object"));
	}

	// 特设附加方法
	/**
	 * 获取所有From对象
	 *
	 * @return
	 */
	public Collection<FromInfo> froms() {
		return getCurrentFromInfos().values();
	}

	/**
	 * 根据查询内容创建查询对象
	 *
	 * @param em
	 * @param queryContent
	 * @return
	 */
	private Query createQuery(EntityManager em, QueryContent queryContent) {
		if (logger.isDebugEnabled()) {
			String caller = _Helper.findCaller();
			logger.debug("JPQL({}):{}", caller, queryContent);
		}
		Query query = em.createQuery(queryContent.getQueryString());
		for (String name : queryContent.getArguments().keySet()) {
			Object arg = queryContent.getArguments().get(name);
			if (arg != null && arg instanceof Date) {
				Timestamp value = new Timestamp(((Date) arg).getTime());
				query.setParameter(name, value);
				continue;
			}
			query.setParameter(name, arg);
		}
		return query;
	}

	private Query createQuery(EntityManager em, JpaQuery finder, boolean cacheable) {
		QueryContent queryContent = finder.toQueryContent();

		Query query = createQuery(em, queryContent);
		cacheable(query, cacheable);
		return query;
	}

	private Query createQuery(EntityManager em, boolean cacheable) {
		return createQuery(em, this, cacheable);
	}

	/**
	 * 生成统计专用的查询对象
	 *
	 * @param em
	 * @return
	 */
	private Query createCountQuery(EntityManager em) {
		QueryContent queryContent = toCountQueryContent();
		String hql = queryContent.getQueryString();
		List<Object> argList = new ArrayList<>();
		Pattern pattern = Pattern.compile(":[a-zA-Z0-9_]+");
		Matcher matcher = pattern.matcher(hql);
		while (matcher.find()) {
			String group = matcher.group();
			String name = group.substring(1);
			argList.add(queryContent.getArguments().get(name));
		}
		Session session = em.unwrap(Session.class);
		QueryTranslatorFactory translatorFactory = new ASTQueryTranslatorFactory();
		SessionFactoryImplementor factory = (SessionFactoryImplementor) session.getSessionFactory();
		QueryTranslator translator = translatorFactory.createQueryTranslator(hql, hql, Collections.EMPTY_MAP, factory,
				null);
		translator.compile(Collections.EMPTY_MAP, false);
		String sql = translator.getSQLString();
		sql = "SELECT COUNT(1) FROM (" + sql + ") TMP";
		Query query = em.createNativeQuery(sql);
		for (int i = 0; i < argList.size(); i++) {
			Object arg = argList.get(i);
			if (arg != null) {
				if (arg.getClass().isEnum()) {
					arg = ((Enum) arg).name();
				}
			}
			query.setParameter(i + 1, arg);
		}
		return query;
	}

	@Override
	public Object one(EntityManager em) {
		return one(em, false);
	}

	@Override
	public Object one(EntityManager em, boolean cacheable) {
		Query query = createQuery(em, cacheable);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<?> list(EntityManager em) {
		return list(em, false);
	}

	@Override
	public List<?> list(EntityManager em, boolean cacheable) {
		return new AbstractList<Object>() {
			int count = -1;
			List<?> data;
			int page = -1;

			@Override
			public Object get(int index) {
				synchronized (this) {
					if (index < 0 || (count >= 0 && index >= count)) {
						throw new IndexOutOfBoundsException("Index: " + index + ",Size:" + size());
					}
					if (page != index / EACH_SIZE) {
						page = index / EACH_SIZE;
						data = list(em, page * EACH_SIZE, EACH_SIZE, cacheable);
					}
					try {
						return data.get(index - page * EACH_SIZE);
					} catch (IndexOutOfBoundsException e) {
						throw new IndexOutOfBoundsException("Index: " + index + ",Size:" + size());
					}
				}
			}

			@Override
			public int size() {
				synchronized (this) {
					if (count < 0) {
						if (page == 0 && data != null && data.size() < EACH_SIZE) {
							count = data.size();
						} else {
							count = (int) count(em);
						}
					}
					return count;
				}
			}
		};
	}

	private void cacheable(Query query, boolean cacheable) {
		query.setHint("org.hibernate.cacheable", cacheable);
	}

	@Override
	public List<?> list(EntityManager em, int start, int max) {
		return list(em, start, max, false);
	}

	@Override
	public List<?> list(EntityManager em, int start, int max, boolean cacheable) {
		Query query = createQuery(em, cacheable);
		query.setFirstResult(start);
		query.setMaxResults(max);
		return query.getResultList();
	}

	@Override
	public List<?> top(EntityManager em, int top) {
		return top(em, top, false);
	}

	@Override
	public List<?> top(EntityManager em, int top, boolean cacheable) {
		return list(em, 0, top, cacheable);
	}

	@Override
	public Page<?> page(EntityManager em, Pageable pageable) {
		return page(em, pageable, false);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Page<?> page(EntityManager em, Pageable pageable, boolean cacheable) {
		JpaQuery finder = this.copy();
		Boolean countSwitch = null;
		Map<String, Object> searchMap = null;
		Map<String, Object> globalSearchMap = null;

		// if (pageable instanceof TablePageRequest) {
		// countSwitch = ((TablePageRequest) pageable).getCountSwitch();
		// searchMap = ((TablePageRequest) pageable).getSearchMap();
		// globalSearchMap = ((TablePageRequest) pageable).getGlobalSearchMap();
		// }

		// if (searchMap != null) {
		// appendSearchMapToFinder(finder, searchMap);
		// }
		//
		// if (globalSearchMap != null) {
		// appendGlobalSearchMapToFinder(finder, globalSearchMap);
		// }

		long total = countSwitch == null || countSwitch ? ((Number) createCountQuery(em).getSingleResult()).longValue()
				: -1;

		List<?> content;

		if (countSwitch == null) {
			content = total > pageable.getOffset()
					? createQuery(em, appendSortToFinder(finder, pageable.getSort()), cacheable)
							.setFirstResult(pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList()
					: Collections.emptyList();
		} else if (!countSwitch) {
			content = createQuery(em, appendSortToFinder(finder, pageable.getSort()), cacheable)
					.setFirstResult(pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();
		} else {
			content = Collections.emptyList();
		}
		return new PageImpl(content, pageable, total);
	}

	/**
	 * 追加search信息给Finder
	 *
	 * @param searchMap
	 */
	private void appendSearchMapToFinder(JpaQuery finder, Map<String, Object> searchMap) {
		JpaQueryImpl finderImpl = (JpaQueryImpl) finder;
		if (finderImpl.getSelectImpl().getSelectPaths().size() == 1) {
			Object selectPath = finderImpl.getSelectImpl().getSelectPaths().get(0);
			if (selectPath != null && selectPath instanceof SelectPathImpl<?>) {
				SelectPathImpl<?> selectPathImpl = (SelectPathImpl<?>) selectPath;
				Object arg = selectPathImpl.getArg();
				if (arg != null && !(arg instanceof JpaQuery)) {
					try {
						String alias = finderImpl.alias(arg);
						And and = finder.where().and();
						for (String name : searchMap.keySet()) {
							Object value = searchMap.get(name);
							if (value != null) {
								if (value instanceof String) {
									and.append(alias + "." + name + " like ?", value);
								} else {
									and.append(alias + "." + name + " = ?", value);
								}
							}
						}
						return;
					} catch (IllegalStateException e) {

					}
				}
			}
		}
		And and = finder.where().and();
		for (String name : searchMap.keySet()) {
			Object value = searchMap.get(name);
			if (value != null) {
				if (value instanceof String) {
					and.append(name + " like ?", value);
				} else {
					and.append(name + " = ?", value);
				}
			}
		}
	}

	/**
	 * 将排序信息追加到Finder中，注意Finder将会被改变
	 *
	 * @param finder
	 * @param sort
	 */
	private JpaQuery appendSortToFinder(JpaQuery finder, Sort sort) {
		if (sort == null) {
			return finder;
		}
		JpaQueryImpl finderImpl = (JpaQueryImpl) finder;
		if (finderImpl.getSelectImpl().getSelectPaths().size() == 1) {
			Object selectPath = finderImpl.getSelectImpl().getSelectPaths().get(0);
			if (selectPath != null && selectPath instanceof SelectPathImpl<?>) {
				SelectPathImpl<?> selectPathImpl = (SelectPathImpl<?>) selectPath;
				Object arg = selectPathImpl.getArg();
				if (arg != null && !(arg instanceof JpaQuery)) {
					try {
						String alias = finderImpl.alias(arg);
						for (Sort.Order order : sort) {
							finder.order().append(alias.concat(".").concat(order.getProperty()).concat(" ")
									.concat(order.getDirection().name().toLowerCase()));
						}
						return finder;
					} catch (IllegalStateException e) {

					}
				}
			}
		}
		for (Sort.Order order : sort) {
			finder.order().append(order.getProperty().concat(" ").concat(order.getDirection().name().toLowerCase()));
		}
		return finder;
	}

	/**
	 * 追加globalSearch信息给Finder
	 *
	 * @param globalSearchMap
	 */
	private void appendGlobalSearchMapToFinder(JpaQuery finder, Map<String, Object> globalSearchMap) {
		JpaQueryImpl finderImpl = (JpaQueryImpl) finder;
		if (finderImpl.getSelectImpl().getSelectPaths().size() == 1) {
			Object selectPath = finderImpl.getSelectImpl().getSelectPaths().get(0);
			if (selectPath != null && selectPath instanceof SelectPathImpl<?>) {
				SelectPathImpl<?> selectPathImpl = (SelectPathImpl<?>) selectPath;
				Object arg = selectPathImpl.getArg();
				if (arg != null && !(arg instanceof JpaQuery)) {
					try {
						String alias = finderImpl.alias(arg);
						Or or = finder.where().or();
						for (String name : globalSearchMap.keySet()) {
							Object value = globalSearchMap.get(name);
							if (value != null) {
								if (value instanceof String) {
									or.append(alias + "." + name + " like ?", value);
								} else {
									or.append(alias + "." + name + " = ?", value);
								}
							}
						}
						return;
					} catch (IllegalStateException e) {

					}
				}
			}
		}
		Or or = finder.where().or();
		for (String name : globalSearchMap.keySet()) {
			Object value = globalSearchMap.get(name);
			if (value != null) {
				if (value instanceof String) {
					or.append(name + " like ?", value);
				} else {
					or.append(name + " = ?", value);
				}
			}
		}
	}

	public long count(EntityManager em) {
		Query query = createCountQuery(em);
		return ((Number) query.getSingleResult()).longValue();
	}

	public JpaQuery copy() {
		JpaQueryImpl finder = new JpaQueryImpl(this.finderHandler, this.finderRender);

		finder.parentFinder = parentFinder;
		finder.parentFromInfos = parentFromInfos;

		finder.currentFromInfos = new HashMap<Long, FromInfo>(currentFromInfos);
		finder.subFinderImpls = new ArrayList<JpaQueryImpl>(subFinderImpls);

		finder.groupImpl = new GroupImpl(finderHandler, finder);
		finder.groupImpl.paths = new ArrayList<Object>(groupImpl.paths);

		finder.havingImpl = new HavingImpl(finderHandler, finder);
		finder.havingImpl.paths = new ArrayList<Object>(havingImpl.paths);

		finder.joinImpl = new JoinImpl(finderHandler, finder);
		finder.joinImpl.joinPathMap = new HashMap<Long, JoinPathImpl<?>>(joinImpl.joinPathMap);

		finder.orderImpl = new OrderImpl(finderHandler, finder);
		finder.orderImpl.paths = new ArrayList<Object>(orderImpl.paths);

		finder.selectImpl = new SelectImpl(finderHandler, finder, selectImpl.entityInfoMap);
		finder.selectImpl.selectPaths = new ArrayList<Object>(selectImpl.selectPaths);

		finder.whereImpl = new WhereImpl(finderHandler, finder, whereImpl.type, whereImpl.entityInfoMap);
		finder.whereImpl.wherePaths = new ArrayList<Object>(whereImpl.wherePaths);

		return finder;
	}

	public String toString() {
		return "JpaQuery[" + hashCode() + "]";
	}

	@Override
	public <T> void each(EntityManager em, JpaQueryEach<T> each) {
		each(em, each, false);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> void each(EntityManager em, JpaQueryEach<T> each, boolean cacheable) {
		for (int i = 0;; i += EACH_SIZE) {
			List<T> list = (List<T>) list(em, i, EACH_SIZE, cacheable);
			if (list.isEmpty()) {
				break;
			}
			for (T entity : list) {
				each.handle(entity);
			}
		}
	}

}
