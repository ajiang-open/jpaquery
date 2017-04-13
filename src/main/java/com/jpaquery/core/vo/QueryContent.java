package com.jpaquery.core.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jpaquery.builder.JPQL;

/**
 * 查询信息对象
 * 
 * @author lujijiang
 * 
 */
public class QueryContent {

	/**
	 * 查询语句
	 */
	StringBuilder queryBuilder;
	/**
	 * 查询参数
	 */
	Map<String, Object> arguments;

	public QueryContent(StringBuilder queryBuilder, Map<String, Object> arguments) {
		this.queryBuilder = queryBuilder;
		this.arguments = arguments;
	}

	public QueryContent(Object object, Map<String, Object> arguments) {
		this(new StringBuilder().append(object), arguments);
	}

	public QueryContent() {
		this(new StringBuilder(), new ConcurrentHashMap<String, Object>());
	}

	public StringBuilder getQueryBuilder() {
		return queryBuilder;
	}

	public String getQueryString() {
		return queryBuilder.toString();
	}

	public Map<String, Object> getArguments() {
		return arguments;
	}

	public QueryContent append(QueryContent otherQueryContent) {
		if (otherQueryContent != null) {
			this.getQueryBuilder().append(otherQueryContent.getQueryBuilder());
			this.getArguments().putAll(otherQueryContent.getArguments());
		}
		return this;
	}

	public QueryContent append(String queryString) {
		append(queryString, null);
		return this;
	}

	public QueryContent append(String queryString, Map<String, Object> args) {
		this.getQueryBuilder().append(queryString);
		if (args != null) {
			this.getArguments().putAll(args);
		}
		return this;
	}

	final static Pattern NAME__ARG_PATTERN = Pattern.compile(":\\w+");

	public String toString(String databaseType) {
		StringBuffer sb = new StringBuffer();
		List<Object> argList = new ArrayList<>();
		Matcher matcher = NAME__ARG_PATTERN.matcher(getQueryString());
		while (matcher.find()) {
			String group = matcher.group();
			Object arg = getArguments().get(group.substring(1));
			argList.add(arg);
			matcher.appendReplacement(sb, "?");
		}
		matcher.appendTail(sb);
		return JPQL.SqlFormatter.format(sb.toString(), databaseType, argList);
	}

	public String toString() {
		return toString(null);
	}

	public int length() {
		return getQueryBuilder().length();
	}

}
