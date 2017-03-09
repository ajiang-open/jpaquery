package com.jpaquery.core.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jpaquery.core.facade.QueryAppender;
import com.jpaquery.core.vo.QueryContent;
import com.jpaquery.util._Helper;

/**
 * SQL追加工具类
 * 
 * @author lujijiang
 * 
 */
public class QueryAppenderImpl implements QueryAppender {

	static final Pattern QUERY_PATTERN = Pattern
			.compile(
					"(')" + "|((^\\s*and\\s+)" + "|(\\s+and\\s*$))" + "|((^\\s*or\\s+)" + "|(\\s+or\\s*$))"
							+ "|((^\\s*,)|(,\\s*$))" + "|(^\\s*where\\s+)" + "|(^\\s*select\\s+)" + "|(^\\s*having\\s+)"
							+ "|(^\\s*order\\s+by\\s+)" + "|(^\\s*group\\s+by\\s+)" + "|(\\?(\\d)*)",
					Pattern.CASE_INSENSITIVE);
	static final Pattern QUERY_ARG_PATTERN = Pattern.compile("(:\\s*\\w+)" + "|(\\{\\s*\\w+\\s*\\})" + "|(\\?\\d*)",
			Pattern.CASE_INSENSITIVE);

	JpaQueryImpl finderImpl;
	/**
	 * 查询语句
	 */
	String queryString;

	/**
	 * 占位参数
	 */
	Object[] args;

	/**
	 * 序号参数，JPA风格
	 */
	Map<Integer, Object> argIndexMap = new ConcurrentHashMap<Integer, Object>();

	/**
	 * 名字参数
	 */
	Map<String, Object> argNameMap = new ConcurrentHashMap<String, Object>();
	/**
	 * 别名参数
	 */
	Map<String, String> aliasNameMap = new ConcurrentHashMap<String, String>();

	public QueryAppenderImpl(JpaQueryImpl finderImpl, String queryString, Object[] args) {
		args = args == null ? _Helper.EMPTY_ARRAY : args;
		this.finderImpl = finderImpl;
		this.args = args;
		this.queryString = queryString;
		checkAndFix();
	}

	/**
	 * 检查查询语句是否合法以及提出非必要的关键字
	 * 
	 * @param queryString
	 * @param argCount
	 * @return
	 */
	private void checkAndFix() {
		if (queryString == null) {
			throw new IllegalArgumentException(String.format("The append query string should not be null"));
		}
		int argCount = args.length;
		StringBuffer sb = new StringBuffer();
		Matcher matcher = QUERY_PATTERN.matcher(queryString);
		int count = 0;
		while (matcher.find()) {
			String group = matcher.group();
			if (group.contains("'")) {
				throw new IllegalArgumentException(String.format("The append query string should not contains \"'\""));
			} else if (group.equals("?")) {
				if (count >= argCount) {
					throw new IllegalArgumentException(String.format(
							"The append query string and argument's count is not equal,query string contains %d and argument's count is %d",
							count + 1, argCount));
				}
				// String id = "arg"
				// + UUID.randomUUID().toString().replace("-", "");
				// matcher.appendReplacement(sb, ":".concat(id));
				// argNameMap.put(id, args[count]);
				count++;
			} else if (group.matches("\\?\\d+")) {

			} else {
				matcher.appendReplacement(sb, "");
			}
		}
		if (count != argCount) {
			throw new IllegalArgumentException(String.format(
					"The append query string and argument's count is not equal,query string contains %d and argument's count is %d",
					count, argCount));
		}
		matcher.appendTail(sb);
		this.queryString = sb.toString();
	}

	public QueryContent toQueryContent() {
		Map<String, Object> argMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		Matcher matcher = QUERY_ARG_PATTERN.matcher(queryString);
		int count = 0;
		while (matcher.find()) {
			String group = matcher.group();
			if (group.startsWith("{")) {
				String alias = aliasNameMap.get(group.subSequence(1, group.length() - 1).toString().trim());
				if (alias == null) {
					throw new IllegalStateException(String.format("The alias which mapped %s is not exists", group));
				}
				matcher.appendReplacement(sb, alias);
			} else {
				Object arg;
				if (group.startsWith(":")) {
					String name = group.substring(1).trim();
					arg = argNameMap.get(name);
					argMap.put(name, arg);
				} else if (group.equals("?")) {
					String name = finderImpl.finderHandler.generateParamName();
					arg = args[count];
					argMap.put(name, arg);
					matcher.appendReplacement(sb, ":".concat(name));
					count++;
				} else {
					String name = finderImpl.finderHandler.generateParamName();
					arg = argIndexMap.get(Integer.valueOf(group.substring(1).trim()));
					argMap.put(name, arg);
					matcher.appendReplacement(sb, ":".concat(name));
				}
				if (arg == null) {
					throw new IllegalStateException(String.format("The arg which mapped %s is not exists", group));
				}
			}
		}
		matcher.appendTail(sb);
		QueryContent queryContent = new QueryContent(sb, argMap);
		return queryContent;
	}

	public QueryAppender arg(String name, Object value) {
		this.argNameMap.put(name.trim(), value);
		return this;
	}

	public QueryAppender arg(Integer index, Object value) {
		this.argIndexMap.put(index, value);
		return this;
	}

	public QueryAppender alias(String alias, Object proxy) {
		this.aliasNameMap.put(alias.trim(), finderImpl.alias(proxy));
		return this;
	}

}
