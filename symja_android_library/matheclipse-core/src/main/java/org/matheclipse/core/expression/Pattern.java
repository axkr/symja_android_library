package org.matheclipse.core.expression;

import java.io.ObjectStreamException;
import java.util.Map;
import com.duy.lambda.Predicate;

import javax.annotation.Nonnull;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMap;

/**
 * A pattern with assigned &quot;pattern name&quot; (i.e. <code>x_</code>)
 * 
 */
public class Pattern extends Blank {

	public static IPattern valueOf(@Nonnull final ISymbol symbol) {
		IPattern value = F.PREDEFINED_PATTERN_MAP.get(symbol.toString());
		if (value != null) {
			return value;
		}
		return new Pattern(symbol);
	}

	/**
	 * 
	 * @param numerator
	 * @return
	 */
	public static IPattern valueOf(@Nonnull final ISymbol symbol, final IExpr check) {
		return new Pattern(symbol, check);
	}

	/**
	 * 
	 */
	public static IPattern valueOf(@Nonnull final ISymbol symbol, final IExpr check, final boolean def) {
		return new Pattern(symbol, check, def);
	}

	public static IPattern valueOf(@Nonnull final ISymbol symbol, final IExpr check, final IExpr defaultValue) {
		return new Pattern(symbol, check, defaultValue);
	}

	private static final long serialVersionUID = 7617138748475243L;

	/**
	 * The associated symbol for this pattern
	 */
	final ISymbol fSymbol;

	/** package private */
	Pattern(@Nonnull final ISymbol symbol) {
		this(symbol, null, false);
	}

	/** package private */
	Pattern(@Nonnull final ISymbol symbol, IExpr condition) {
		this(symbol, condition, false);
	}

	/** package private */
	public Pattern(@Nonnull final ISymbol symbol, IExpr condition, boolean def) {
		super(condition, def);
		fSymbol = symbol;
	}

	/** package private */
	public Pattern(@Nonnull final ISymbol symbol, IExpr condition, IExpr defaultValue) {
		super(condition, defaultValue);
		fSymbol = symbol;
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

	/**
	 * Compares this expression with the specified expression for order. Returns a negative integer, zero, or a positive
	 * integer as this expression is canonical less than, equal to, or greater than the specified expression.
	 */
	@Override
	public int compareTo(final IExpr expr) {
		if (expr instanceof Pattern) {
			Pattern pat = ((Pattern) expr);
			int cp = fSymbol.compareTo(pat.fSymbol);
			if (cp != 0) {
				return cp;
			}
		}
		return super.compareTo(expr);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Pattern) {
			if (hashCode() != obj.hashCode()) {
				return false;
			}
			Pattern pattern = (Pattern) obj;
			if (fDefault != pattern.fDefault) {
				return false;
			}
			if (fSymbol.equals(pattern.fSymbol)) {
				return super.equals(obj);
			}
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
		if (patternObject instanceof Pattern) {
			// test if the pattern indices are equal
			final IPattern p2 = (IPattern) patternObject;
			if (getIndex(pm1) != p2.getIndex(pm2)) {
				return false;
			}
			// test if the "check" expressions are equal
			final IExpr o1 = getCondition();
			final IExpr o2 = p2.getCondition();
			if ((o1 == null) || (o2 == null)) {
				return o1 == o2;
			}
			return o1.equals(o2);
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean matchPattern(final IExpr expr, PatternMap patternMap) {
		if (!isConditionMatched(expr, patternMap)) {
			return false;
		}

		IExpr value = patternMap.getValue(this);
		if (value != null) {
			return expr.equals(value);
		}
		return patternMap.setValue(this, expr);
	}

	@Override
	public String fullFormString() {
		StringBuilder buf = new StringBuilder();
		buf.append("Pattern");
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			buf.append('(');
		} else {
			buf.append('[');
		}
		buf.append(fSymbol.toString());
		buf.append(", ");
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
			buf.append("))");
		} else {
			buf.append("]]");
		}

		return buf.toString();
	}

	@Override
	public int getIndex(PatternMap pm) {
		if (pm != null) {
			return pm.get(fSymbol);
		}
		return -1;
	}

	@Override
	public ISymbol getSymbol() {
		return fSymbol;
	}

	@Override
	public int hashCode() {
		return 19 + fSymbol.hashCode();
	}

	@Override
	public ISymbol head() {
		return F.PatternHead;
	}

	@Override
	public int hierarchy() {
		return PATTERNID;
	}

	@Override
	public boolean isConditionMatched(final IExpr expr, PatternMap patternMap) {
		if (fCondition == null || expr.head().equals(fCondition)) {
			return true;
		}
		EvalEngine engine = EvalEngine.get();
		boolean traceMode = false;
		try {
			traceMode = engine.isTraceMode();
			engine.setTraceMode(false);
			final Predicate<IExpr> matcher = Predicates.isTrue(engine, fCondition);
			if (matcher.test(expr)) {
				return true;
			}
		} finally {
			if (traceMode) {
				engine.setTraceMode(true);
			}
		}
		return false;
	}

	@Override
	public String internalJavaString(boolean symbolsAsFactoryMethod, int depth, boolean useOperaators, boolean usePrefix) {
		final StringBuilder buffer = new StringBuilder();
		String prefix = usePrefix ? "F." : "";
		buffer.append(prefix+"$p(");
		String symbolStr = fSymbol.toString();
		char ch = symbolStr.charAt(0);
		if (symbolStr.length() == 1 && fDefaultValue == null) {
			if (('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'G' && ch != 'D' && ch != 'E')) {
				if (!fDefault) {
					if (fCondition == null) {
						return prefix+symbolStr + "_";
					} else if (fCondition == F.SymbolHead) {
						return prefix+symbolStr + "_Symbol";
					}
				} else {
					if (fCondition == null) {
						return prefix+symbolStr + "_DEFAULT";
					}
				}
			}
		}
		if (Config.RUBI_CONVERT_SYMBOLS) {
			if (ch == '§' && symbolStr.length() == 2) {
				char ch2 = symbolStr.charAt(1);
				if ('a' <= ch2 && ch2 <= 'z') {
					if (!fDefault) {
						if (fCondition == null) {
							return prefix+"p" + ch2 + "_";
						}
					} else {
						if (fCondition == null) {
							return prefix+"p" + ch2 + "_DEFAULT";
						}
					}
				}
			}
		}

		if (symbolStr.length() == 1 && ('a' <= ch && ch <= 'z')) {
			buffer.append(prefix+symbolStr);
		} else {
			buffer.append("\"" + symbolStr + "\"");
		}
		if (fCondition != null) {
			if (fCondition == F.IntegerHead) {
				buffer.append(", IntegerHead");
			} else if (fCondition == F.SymbolHead) {
				buffer.append(", SymbolHead");
			} else {
				buffer.append("," + fCondition.internalJavaString(symbolsAsFactoryMethod, 0, useOperaators, false));
			}
		}
		if (fDefaultValue != null) {
			if (fCondition == null) {
				buffer.append(", null");
			}
			buffer.append("," + fDefaultValue.internalJavaString(symbolsAsFactoryMethod, 0, useOperaators, false));
		} else {
			if (fDefault) {
				buffer.append(",true");
			}
		}

		buffer.append(')');
		return buffer.toString();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isBlank() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	final public boolean isPattern() {
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		if (fCondition == null) {
			buffer.append(fSymbol.toString());
			buffer.append('_');
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
		} else {
			buffer.append(fSymbol.toString());
			buffer.append('_');
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
			buffer.append(fCondition.toString());
		}
		return buffer.toString();
	}

	private Object writeReplace() throws ObjectStreamException {
		return optional(F.GLOBAL_IDS_MAP.get(this));
	}
}