package com.jpaquery.builder;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.jpaquery.util._Helper;

/**
 * JPQL通用组装器
 * 
 * @author lujijiang
 *
 */
public class JPQL {

	/**
	 * 用于格式化SQL和注入参数
	 * 
	 * @author lujijiang
	 */
	public static class SqlFormatter {
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

	private static final Logger logger = LoggerFactory.getLogger(JPQL.class);

	static final Pattern QUERY_ARG_PATTERN = Pattern.compile("\\?");

	private JPQL() {
	}

	private StringBuilder jpqlBuilder = new StringBuilder();

	private List<Object> argList = new ArrayList<>();

	public static JPQL create() {
		return new JPQL();
	}

	public JPQL append(String jpqlFragment, Object... args) {
		if (jpqlFragment == null) {
			throw new IllegalArgumentException(String.format("The append jpql fragment should not be null"));
		}
		if (jpqlFragment.contains("'")) {
			throw new IllegalArgumentException(String.format("The append jpql fragment should not contains \"'\""));
		}
		if (args != null) {
			int count = StringUtils.countMatches(jpqlFragment, "?");
			if (count != args.length) {
				throw new IllegalArgumentException(
						String.format("The number of question marks:%d and the number of arguments:%d is not equal",
								count, args.length));
			}
			for (Object arg : args) {
				this.argList.add(arg);
			}
		}
		if (this.jpqlBuilder.length() > 1
				&& !this.jpqlBuilder.substring(this.jpqlBuilder.length() - 1, this.jpqlBuilder.length()).equals(" ")) {
			this.jpqlBuilder.append(" ");
		}
		this.jpqlBuilder.append(jpqlFragment);
		return this;
	}

	private Query createQuery(EntityManager em, String jpql, List<Object> argList, boolean cacheable) {
		if (logger.isDebugEnabled()) {
			logger.debug("JPQL:{}", this);
		}
		Query query = em.createQuery(jpql);
		for (int i = 0; i < argList.size(); i++) {
			Object arg = argList.get(i);
			if (arg != null && arg instanceof Date) {
				arg = new Timestamp(((Date) arg).getTime());
			}
			query.setParameter(i + 1, arg);
		}
		query.setHint("org.hibernate.cacheable", cacheable);
		return query;
	}

	private Query createQuery(EntityManager em, boolean cacheable) {
		return createQuery(em, this.jpqlBuilder.toString(), argList, cacheable);
	}

	/**
	 * 获取结果列表
	 * 
	 * @param em
	 *            实体管理器
	 * @param cacheable
	 *            查询缓存开关
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> list(EntityManager em, boolean cacheable) {
		Query query = createQuery(em, cacheable);
		return query.getResultList();
	}

	/**
	 * 获取结果列表
	 * 
	 * @param em
	 *            实体管理器
	 * @return
	 */
	public <T> List<T> list(EntityManager em) {
		return list(em, false);
	}

	public Object one(EntityManager em) {
		return one(em, false);
	}

	public Object one(EntityManager em, boolean cacheable) {
		Query query = createQuery(em, cacheable);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<?> list(EntityManager em, int start, int max, boolean cacheable) {
		Query query = createQuery(em, cacheable);
		query.setFirstResult(start);
		query.setMaxResults(max);
		return query.getResultList();
	}

	public List<?> list(EntityManager em, int start, int max) {
		return list(em, start, max, false);
	}

	public List<?> top(EntityManager em, int top, boolean cacheable) {
		return list(em, 0, top, cacheable);
	}

	public List<?> top(EntityManager em, int top) {
		return top(em, top, false);
	}

	public Long count(EntityManager em) {
		Session session = em.unwrap(Session.class);
		SessionFactory sessionFactory = session.getSessionFactory();
		String originalHql = this.jpqlBuilder.toString();
		QueryTranslatorImpl queryTranslator = new QueryTranslatorImpl(originalHql, originalHql, Collections.EMPTY_MAP,
				(SessionFactoryImplementor) sessionFactory);
		queryTranslator.compile(Collections.EMPTY_MAP, false);
		String sql = "select count(*) from (" + queryTranslator.getSQLString() + ") tmp";
		SQLQuery query = session.createSQLQuery(sql);
		for (int i = 0; i < argList.size(); i++) {
			query.setParameter(i, argList.get(i));
		}
		return ((Number) query.uniqueResult()).longValue();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> Page<T> page(EntityManager em, Pageable pageable, boolean cacheable) {
		Long total = count(em);
		List<?> content = total > pageable.getOffset()
				? list(em, pageable.getOffset(), pageable.getPageSize(), cacheable) : Collections.emptyList();
		return new PageImpl(content, pageable, total);
	}

	public <T> Page<T> page(EntityManager em, Pageable pageable) {
		return page(em, pageable, false);
	}

	public String toString() {
		return SqlFormatter.format(jpqlBuilder.toString(), "mysql", argList);
	}

}
