package org.matheclipse.core.expression;

import com.duy.lambda.Predicate;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternImpl;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMap;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

import java.io.ObjectStreamException;
import java.util.Collection;
import java.util.Map;

/**
 * A &quot;blank pattern&quot; with no assigned &quot;pattern name&quot; (i.e. &quot;<code>_</code>&quot;)
 *
 */
public class Blank extends IPatternImpl implements IPattern {

	/**
	 *
	 */
	private static final long serialVersionUID = 1306007999071682207L;

	public static IPattern valueOf() {
		return new Blank(); // NULL_PATTERN;
	}

	public static IPattern valueOf(final IExpr condition) {
		return new Blank(condition);
	}

	/**
	 * The expression which should check this pattern
	 */
	final IExpr fCondition;

	/**
	 * Use default value, if no matching expression was found
	 */
	final boolean fDefault;

	/**
	 * Use default value, if no matching expression was found
	 */
	final IExpr fDefaultValue;

	public Blank() {
		this(null);
	}

	public Blank(final IExpr condition) {
		this(condition, false);
	}

	public Blank(final IExpr condition, boolean def) {
		super();
		this.fCondition = condition;
		this.fDefault = def;
		this.fDefaultValue = null;
	}

	public Blank(final IExpr condition, IExpr defaultValue) {
		super();
		this.fCondition = condition;
		this.fDefault = true;
		this.fDefaultValue = defaultValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T accept(IVisitor<T> visitor) {
		return visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean accept(IVisitorBoolean visitor) {
		return visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int accept(IVisitorInt visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	@Override
	public long accept(IVisitorLong visitor) {
		return visitor.visit(this);
	}

	@Override
	public int[] addPattern(PatternMap patternMap, Map<IExpr, Integer> patternIndexMap) {
		patternMap.addPattern(patternIndexMap, this);
		int[] result = new int[2];
		if (isPatternDefault()) {
			// the ast contains a pattern with default value (i.e. "x_." or
			// "x_:")
			result[0] = IAST.CONTAINS_DEFAULT_PATTERN;
			result[1] = 2;
		} else {
			// the ast contains a pattern without default value (i.e. "x_")
			result[0] = IAST.CONTAINS_PATTERN;
			result[1] = 5;
		}
		return result;
	}

	@Override
	public int compareTo(final IExpr expr) {
		if (expr instanceof Blank) {
			Blank blank = ((Blank) expr);
			if (fDefault != blank.fDefault) {
				return fDefault ? 1 : -1;
			}
			if (fCondition == null) {
				if (blank.fCondition != null) {
					return -1;
				}
			} else {
				if (blank.fCondition == null) {
					return 1;
				}
				int result = fCondition.compareTo(blank.fCondition);
				if (result != 0) {
					return result;
				}
			}
			if (fDefaultValue == null) {
				if (blank.fDefaultValue != null) {
					return -1;
				}
			} else {
				if (blank.fDefaultValue == null) {
					return 1;
				}
				int result = fDefaultValue.compareTo(blank.fDefaultValue);
				if (result != 0) {
					return result;
				}
			}
			return 0;
		}
		return IPattern.super.compareTo(expr);
	}

	@Override
	public IExpr copy() {
		try {
			return (IExpr) clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Blank) {
			if (hashCode() != obj.hashCode()) {
				return false;
			}
			Blank blank = (Blank) obj;
			if ((fCondition != null) && (blank.fCondition != null)) {
				return fCondition.equals(blank.fCondition);
			}
			return fCondition == blank.fCondition;
		}
		return false;
	}

	/**
	 * Check if the two left-hand-side pattern expressions are equivalent. (i.e. <code>f[x_,y_]</code> is equivalent to
	 * <code>f[a_,b_]</code> )
	 *
	 * @param patternObject
	 * @param pm1
	 * @param pm2
	 * @return
	 */
	@Override
	public boolean equivalent(final IPatternObject patternObject, final PatternMap pm1, PatternMap pm2) {
		if (this == patternObject) {
			return true;
		}
		if (patternObject instanceof Blank) {
			// test if the "check" expressions are equal
			final IExpr o1 = getCondition();
			final IExpr o2 = patternObject.getCondition();
			if ((o1 == null) || (o2 == null)) {
				return o1 == o2;
			}
			return o1.equals(o2);
		}
		return false;
	}

	@Override
	public String fullFormString() {
		StringBuilder buf = new StringBuilder();
		buf.append("Blank");
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			buf.append('(');
		} else {
			buf.append('[');
		}
		if (fCondition != null) {
			buf.append(fCondition.fullFormString());
		}
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			buf.append(')');
		} else {
			buf.append(']');
		}
		return buf.toString();
	}

	@Override
	public IExpr getCondition() {
		return fCondition;
	}

	@Override
	public IExpr getDefaultValue() {
		return fDefaultValue;
	}

	@Override
	public int getEvalFlags() {
		if (isPatternDefault()) {
			// the ast contains a pattern with default value (i.e. "x_." or
			// "x_:")
			return IAST.CONTAINS_DEFAULT_PATTERN;
		}
		// the ast contains a pattern without default value (i.e. "x_")
		return IAST.CONTAINS_PATTERN;
	}

	@Override
	public int getIndex(PatternMap pm) {
		if (pm != null) {
			return pm.get(this);
		}
		return -1;
	}

	@Override
	public ISymbol getSymbol() {
		return null;
	}

	@Override
	public int hashCode() {
		return (fCondition == null) ? 193 : 23 + fCondition.hashCode();
	}

	@Override
	public ISymbol head() {
		return F.BlankHead;
	}

	@Override
	public int hierarchy() {
		return BLANKID;
	}

	@Override
	public String internalJavaString(boolean symbolsAsFactoryMethod, int depth, boolean useOperators, boolean usePrefix) {
		String prefix = usePrefix ? "F." : "";
		final StringBuilder buffer = new StringBuilder();
		buffer.append(prefix+"$b(");
		if (fCondition != null) {
			buffer.append(fCondition.internalJavaString(symbolsAsFactoryMethod, 0, useOperators, false));
		}
		buffer.append(')');
		return buffer.toString();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isBlank() {
		return true;
	}

	/**
	 * Groovy operator overloading
	 *
	 * @param that
	 * @return
	 */
	public boolean isCase(IExpr that) {
		final IPatternMatcher matcher = new PatternMatcher(this);
		if (matcher.test(that)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isConditionMatched(final IExpr expr, PatternMap patternMap) {
		if (fCondition == null || expr.head().equals(fCondition)) {
			patternMap.setValue(this, expr);
			return true;
		}
		EvalEngine engine = EvalEngine.get();
		boolean traceMode = false;
		try {
			traceMode = engine.isTraceMode();
			engine.setTraceMode(false);
			final Predicate<IExpr> matcher = Predicates.isTrue(engine, fCondition);
			if (matcher.test(expr)) {
				patternMap.setValue(this, expr);
				return true;
			}
		} finally {
			if (traceMode) {
				engine.setTraceMode(true);
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isFreeOfPatterns() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPatternDefault() {
		return fDefault;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPatternExpr() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPatternOptional() {
		return fDefaultValue != null;
	}

	@Override
	public boolean matchPattern(final IExpr expr, PatternMap patternMap) {
		return isConditionMatched(expr, patternMap);
	}

	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append('_');
		if (fCondition != null) {
			buffer.append(fCondition.toString());
		} else {
			if (fDefaultValue != null) {
				buffer.append(':');
				if (!fDefaultValue.isAtom()) {
					buffer.append('(');
				}
				buffer.append(fDefaultValue.toString());
				if (!fDefaultValue.isAtom()) {
					buffer.append(')');
				}
			} else {
				if (fDefault) {
					buffer.append('.');
				}
			}
		}
		return buffer.toString();
	}

	/** {@inheritDoc} */
	@Override
	public IExpr variables2Slots(final Map<IExpr, IExpr> map, final Collection<IExpr> variableCollector) {
		return F.NIL;
	}

	private Object writeReplace() throws ObjectStreamException {
		return optional(F.GLOBAL_IDS_MAP.get(this));
	}

}