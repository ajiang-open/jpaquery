package com.jpaquery.core.facade;

/**
 * 查询语句追加器
 * 
 * @author lujijiang
 * 
 */
public interface QueryAppender extends QueryRender {

	/**
	 * 用于设置名字参数，如where name = :name and password = :password
	 * 
	 * @param name
	 *            参数名
	 * @param value
	 *            参数值
	 * @return
	 */
	public QueryAppender arg(String name, Object value);

	/**
	 * 序号参数，JPA风格，如where name = ?1 and password = ?2
	 * 
	 * @param index
	 * @param value
	 * @return
	 */
	public QueryAppender arg(Integer index, Object value);

	/**
	 * 设置别名，查询语句片段中的别名用英文大括号包起来，如{somefield}=?
	 * 
	 * @param alias
	 *            别名
	 * @param proxy
	 *            别名所代表的模型对象或者模型字段
	 * @return
	 */
	public QueryAppender alias(String alias, Object proxy);
}
