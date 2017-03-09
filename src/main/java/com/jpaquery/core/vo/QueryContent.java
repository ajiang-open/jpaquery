package com.jpaquery.core.vo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.jpaquery.util._Helper;

/**
 * 查询信息对象
 * 
 * @author lujijiang
 * 
 */
public class QueryContent {
	/**
	 * 用于格式化SQL和注入参数
	 * 
	 * @author lujijiang
	 */
	static class SqlFormatter {
		private static final String WHITESPACE = " \n\r\f\t";

		private static final Set<String> BEGIN_CLAUSES = new HashSet<String>();
		private static final Set<String> END_CLAUSES = new HashSet<String>();
		private static final Set<String> LOGICAL = new HashSet<String>();
		private static final Set<String> QUANTIFIERS = new HashSet<String>();
		private static final Set<String> DML = new HashSet<String>();
		private static final Set<String> MISC = new HashSet<String>();

		static {
			BEGIN_CLAUSES.add("left");
			BEGIN_CLAUSES.add("right");
			BEGIN_CLAUSES.add("inner");
			BEGIN_CLAUSES.add("outer");
			BEGIN_CLAUSES.add("group");
			BEGIN_CLAUSES.add("order");
			BEGIN_CLAUSES.add("limit");// 适应MYSQL

			END_CLAUSES.add("where");
			END_CLAUSES.add("set");
			END_CLAUSES.add("having");
			END_CLAUSES.add("join");
			END_CLAUSES.add("from");
			END_CLAUSES.add("by");
			END_CLAUSES.add("join");
			END_CLAUSES.add("into");
			END_CLAUSES.add("union");

			LOGICAL.add("and");
			LOGICAL.add("or");
			LOGICAL.add("when");
			LOGICAL.add("else");
			LOGICAL.add("end");

			QUANTIFIERS.add("in");
			QUANTIFIERS.add("all");
			QUANTIFIERS.add("exists");
			QUANTIFIERS.add("some");
			QUANTIFIERS.add("any");

			DML.add("insert");
			DML.add("update");
			DML.add("delete");

			MISC.add("select");
			MISC.add("on");
			MISC.add("with");
		}

		static final String indentString = "    ";
		static final String initial = "\n    ";

		public static String format(String source, String databaseType, List<Object> args) {
			return new FormatProcess(source, databaseType, args).perform();
		}

		private static class FormatProcess {
			String databaseType;
			List<Object> args;
			boolean beginLine = true;
			boolean afterBeginBeforeEnd = false;
			boolean afterByOrSetOrFromOrSelect = false;
			boolean afterValues = false;
			boolean afterOn = false;
			boolean afterBetween = false;
			boolean afterInsert = false;
			int inFunction = 0;
			int parensSinceSelect = 0;
			private LinkedList<Integer> parenCounts = new LinkedList<Integer>();
			private LinkedList<Boolean> afterByOrFromOrSelects = new LinkedList<Boolean>();

			int indent = 1;

			StringBuilder result = new StringBuilder();
			StringTokenizer tokens;
			String lastToken;
			String token;
			String lcToken;
			private int i;

			public FormatProcess(String sql, String databaseType, List<Object> args) {
				this.databaseType = databaseType;
				this.args = args;
				tokens = new StringTokenizer(sql, "()+*/-=<>'`\"[],?" + WHITESPACE, true);
			}

			private String formatArg(Object arg) {
				if (arg == null) {
					return "null";
				}
				if (_Helper.isNumber(arg.getClass())) {
					return arg.toString();
				}
				if (arg instanceof Date) {
					if ("oracle".equalsIgnoreCase(databaseType)) {
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						return "to_date('" + dateFormat.format((Date) arg) + "','yyyy-mm-dd hh24:mi:ss')";
					}
					if ("mysql".equalsIgnoreCase(databaseType)) {
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						return "str_to_date('" + dateFormat.format((Date) arg) + "','%Y-%m-%d %T')";
					}
					if ("informix".equalsIgnoreCase(databaseType)) {
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						return "to_date('" + dateFormat.format((Date) arg) + "','%Y-%m-%d %H:%M:%S')";
					}
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					return "'" + dateFormat.format((Date) arg) + "'";
				}
				return "'" + StringUtils.replace(StringUtils.replace(arg.toString(), "'", "''"), "\\", "\\\\") + "'";
			}

			public String perform() {
				i = 0;

				result.append(initial);

				while (tokens.hasMoreTokens()) {
					token = tokens.nextToken();
					lcToken = token.toLowerCase();

					if ("'".equals(token)) {
						String t;
						do {
							t = tokens.nextToken();
							token += t;
						} while (!"'".equals(t) && tokens.hasMoreTokens()); // cannot
																			// handle
																			// single
																			// quotes
					} else if ("\"".equals(token)) {
						String t;
						do {
							t = tokens.nextToken();
							token += t;
						} while (!"\"".equals(t));
					}
					if ("?".equals(token)) {// 注入参数
						if (args.size() > i) {
							Object arg = args.get(i);
							result.append(formatArg(arg));
						} else {
							result.append("?");
						}
						i++;
					} else if (afterByOrSetOrFromOrSelect && ",".equals(token)) {
						commaAfterByOrFromOrSelect();
					} else if (afterOn && ",".equals(token)) {
						commaAfterOn();
					}

					else if ("(".equals(token)) {
						openParen();
					} else if (")".equals(token)) {
						closeParen();
					}

					else if (BEGIN_CLAUSES.contains(lcToken)) {
						beginNewClause();
					}

					else if (END_CLAUSES.contains(lcToken)) {
						endNewClause();
					}

					else if ("select".equals(lcToken)) {
						select();
					}

					else if (DML.contains(lcToken)) {
						updateOrInsertOrDelete();
					}

					else if ("values".equals(lcToken)) {
						values();
					}

					else if ("on".equals(lcToken)) {
						on();
					}

					else if (afterBetween && lcToken.equals("and")) {
						misc();
						afterBetween = false;
					}

					else if (LOGICAL.contains(lcToken)) {
						logical();
					}

					else if (isWhitespace(token)) {
						white();
					}

					else {
						misc();
					}

					if (!isWhitespace(token)) {
						lastToken = lcToken;
					}

				}
				return result.toString();
			}

			private void commaAfterOn() {
				out();
				indent--;
				newline();
				afterOn = false;
				afterByOrSetOrFromOrSelect = true;
			}

			private void commaAfterByOrFromOrSelect() {
				out();
				newline();
			}

			private void logical() {
				if ("end".equals(lcToken)) {
					indent--;
				}
				newline();
				out();
				beginLine = false;
			}

			private void on() {
				indent++;
				afterOn = true;
				newline();
				out();
				beginLine = false;
			}

			private void misc() {
				out();
				if ("between".equals(lcToken)) {
					afterBetween = true;
				}
				if (afterInsert) {
					newline();
					afterInsert = false;
				} else {
					beginLine = false;
					if ("case".equals(lcToken)) {
						indent++;
					}
				}
			}

			private void white() {
				if (!beginLine) {
					result.append(" ");
				}
			}

			private void updateOrInsertOrDelete() {
				out();
				indent++;
				beginLine = false;
				if ("update".equals(lcToken)) {
					newline();
				}
				if ("insert".equals(lcToken)) {
					afterInsert = true;
				}
			}

			private void select() {
				out();
				indent++;
				newline();
				parenCounts.addLast(Integer.valueOf(parensSinceSelect));
				afterByOrFromOrSelects.addLast(Boolean.valueOf(afterByOrSetOrFromOrSelect));
				parensSinceSelect = 0;
				afterByOrSetOrFromOrSelect = true;
			}

			private void out() {
				result.append(token);
			}

			private void endNewClause() {
				if (!afterBeginBeforeEnd) {
					indent--;
					if (afterOn) {
						indent--;
						afterOn = false;
					}
					newline();
				}
				out();
				if (!"union".equals(lcToken)) {
					indent++;
				}
				newline();
				afterBeginBeforeEnd = false;
				afterByOrSetOrFromOrSelect = "by".equals(lcToken) || "set".equals(lcToken) || "from".equals(lcToken);
			}

			private void beginNewClause() {
				if (!afterBeginBeforeEnd) {
					if (afterOn) {
						indent--;
						afterOn = false;
					}
					indent--;
					newline();
				}
				out();
				beginLine = false;
				afterBeginBeforeEnd = true;
			}

			private void values() {
				indent--;
				newline();
				out();
				indent++;
				newline();
				afterValues = true;
			}

			private void closeParen() {
				parensSinceSelect--;
				if (parensSinceSelect < 0) {
					indent--;
					parensSinceSelect = parenCounts.removeLast().intValue();
					afterByOrSetOrFromOrSelect = afterByOrFromOrSelects.removeLast().booleanValue();
				}
				if (inFunction > 0) {
					inFunction--;
					out();
				} else {
					if (!afterByOrSetOrFromOrSelect) {
						indent--;
						newline();
					}
					out();
				}
				beginLine = false;
			}

			private void openParen() {
				if (isFunctionName(lastToken) || inFunction > 0) {
					inFunction++;
				}
				beginLine = false;
				if (inFunction > 0) {
					out();
				} else {
					out();
					if (!afterByOrSetOrFromOrSelect) {
						indent++;
						newline();
						beginLine = true;
					}
				}
				parensSinceSelect++;
			}

			private static boolean isFunctionName(String tok) {
				final char begin = tok.charAt(0);
				final boolean isIdentifier = Character.isJavaIdentifierStart(begin) || '"' == begin;
				return isIdentifier && !LOGICAL.contains(tok) && !END_CLAUSES.contains(tok)
						&& !QUANTIFIERS.contains(tok) && !DML.contains(tok) && !MISC.contains(tok);
			}

			private static boolean isWhitespace(String token) {
				return WHITESPACE.indexOf(token) >= 0;
			}

			private void newline() {
				result.append("\n");
				for (int i = 0; i < indent; i++) {
					result.append(indentString);
				}
				beginLine = true;
			}
		}

	}

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
		return SqlFormatter.format(sb.toString(), databaseType, argList);
	}

	public String toString() {
		return toString(null);
	}

	public int length() {
		return getQueryBuilder().length();
	}

}
