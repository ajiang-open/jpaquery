package com.jpaquery.core.facade;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jpaquery.core.vo.QueryContent;

/**
 * Finder查询器接口
 * 
 * @author lujijiang
 * 
 */
public interface JpaQuery extends QueryRender {
	/**
	 * 生成子查询
	 * 
	 * @return
	 */
	public abstract JpaQuery subJpaQuery();

	/**
	 * 指定from的表类型
	 * 
	 * @param type
	 * @return
	 */
	public abstract <T> T from(Class<T> type);

	/**
	 * 获得where子句对象
	 * 
	 * @return
	 */
	public abstract Where where();

	/**
	 * 直接产生查询字段路径对象
	 * 
	 * @param obj
	 * @return
	 */
	public <T> WherePath<T> where(T obj);

	/**
	 * 获得select子句对象
	 * 
	 * @return
	 */
	public abstract Select select();

	/**
	 * 直接指定select字段路径对象
	 * 
	 * @param obj
	 * @return
	 */
	public <T> SelectPath<T> select(T obj);

	/**
	 * 获得order子句对象
	 * 
	 * @return
	 */
	public abstract Order order();

	/**
	 * 直接指定相关字段的order路径对象
	 * 
	 * @param objs
	 * @return
	 */
	public OrderPath order(Object obj);

	/**
	 * 获得goup子句对象
	 * 
	 * @return
	 */
	public abstract Group group();

	/**
	 * 直接指定相关字段的group路径对象
	 * 
	 * @param objs
	 * @return
	 */
	public GroupPath group(Object obj);

	/**
	 * 获得having子句对象
	 * 
	 * @return
	 */
	public abstract Having having();

	/**
	 * 直接指定having字段路径对象
	 * 
	 * @param obj
	 * @return
	 */
	public <T> HavingPath<T> having(T obj);

	/**
	 * 获得join子句对象
	 * 
	 * @return
	 */
	public abstract Join join();

	/**
	 * 直接指定join字段路径对象
	 * 
	 * @param obj
	 * @return
	 */
	public <T> JoinPath<T> join(Collection<T> list);

	/**
	 * 直接指定join字段路径对象
	 * 
	 * @param obj
	 * @return
	 */
	public <T> JoinPath<T> join(T obj);

	/**
	 * 在join实例上获得on的where子句对象（此处类似于SQL标准join子句中的on）
	 * 
	 * @param join
	 * @return
	 */
	public abstract <T> Where on(T join);

	/**
	 * 获取查询语句
	 * 
	 * @return
	 */
	public abstract QueryContent toQueryContent();

	/**
	 * 生成any谓词
	 * 
	 * @return
	 */
	public SubJpaQuery any();

	/**
	 * 生成some谓词
	 * 
	 * @return
	 */
	public SubJpaQuery some();

	/**
	 * 生成all谓词
	 * 
	 * @return
	 */
	public SubJpaQuery all();

	/**
	 * 获取对象或者字段的别名
	 * 
	 * @param proxyInstance
	 *            模型对象或者模型对象属性
	 * @return
	 */
	public abstract String alias(Object proxyInstance);

	/**
	 * 获得统计类型的查询信息，和toQueryContent的区别是只有第一个select项有效并且order by子句消失
	 * 
	 * @return
	 */
	public QueryContent toCountQueryContent();

	/**
	 * 查询单个结果
	 * 
	 * @param em
	 * @return
	 */
	public Object one(EntityManager em);

	/**
	 * 查询单个结果
	 * 
	 * @param em
	 * @param cacheable
	 * @return
	 */
	public Object one(EntityManager em, boolean cacheable);

	/**
	 * 查询所有结果
	 * 
	 * @param em
	 * @return
	 */
	public List<?> list(EntityManager em);

	/**
	 * 查询所有结果
	 * 
	 * @param em
	 * @param cacheable
	 * @return
	 */
	public List<?> list(EntityManager em, boolean cacheable);

	/**
	 * 查询指定范围的结果
	 * 
	 * @param em
	 * @param start
	 * @param max
	 * @param cacheable
	 * @return
	 */
	public List<?> list(EntityManager em, int start, int max);

	/**
	 * 查询指定范围的结果
	 * 
	 * @param em
	 * @param start
	 * @param max
	 * @param cacheable
	 * @return
	 */
	public List<?> list(EntityManager em, int start, int max, boolean cacheable);

	/**
	 * 查询前top条结果
	 * 
	 * @param em
	 * @param top
	 * @param cacheable
	 * @return
	 */
	public List<?> top(EntityManager em, int top);

	/**
	 * 查询前top条结果
	 * 
	 * @param em
	 * @param top
	 * @param cacheable
	 * @return
	 */
	public List<?> top(EntityManager em, int top, boolean cacheable);

	/**
	 * 遍历所有结果
	 * 
	 * @param em
	 * @param each
	 * @return
	 */
	public <T> void each(EntityManager em, JpaQueryEach<T> each);

	/**
	 * 遍历所有结果
	 * 
	 * @param em
	 * @param each
	 * @param cacheable
	 * @return
	 */
	public <T> void each(EntityManager em, JpaQueryEach<T> each, boolean cacheable);

	/**
	 * 查询指定页面的结果
	 * 
	 * @param em
	 * @param pageable
	 * @param cacheable
	 * @return
	 */
	public Page<?> page(EntityManager em, Pageable pageable);

	/**
	 * 查询指定页面的结果
	 * 
	 * @param em
	 * @param pageable
	 * @param cacheable
	 * @return
	 */
	public Page<?> page(EntityManager em, Pageable pageable, boolean cacheable);

	/**
	 * 统计当前结果集
	 * 
	 * @param em
	 * @return
	 */
	public long count(EntityManager em);

	/**
	 * 克隆一个Finder，其中所有数据项都和原Finder一致，但两者后续操作不相互影响
	 * 
	 * @return
	 */
	public JpaQuery copy();

}
