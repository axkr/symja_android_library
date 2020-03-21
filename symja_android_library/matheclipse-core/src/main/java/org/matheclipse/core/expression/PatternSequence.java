package org.matheclipse.core.expression;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

/**
 * A concrete pattern sequence implementation (i.e. x__)
 * 
 */
public class PatternSequence implements IPatternSequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2773651826316158627L;

	/**
	 * @param nullAllowed
	 *            TODO
	 * 
	 */
	public static PatternSequence valueOf(final ISymbol symbol, final IExpr check, final boolean def,
			boolean zeroArgsAllowed) {
		PatternSequence p = new PatternSequence();
		p.fSymbol = symbol;
		p.fCondition = check;
		p.fDefault = def;
		p.fZeroArgsAllowed = zeroArgsAllowed;
		return p;
	}

	/**
	 * 
	 * @param numerator
	 * @return
	 */
	public static PatternSequence valueOf(final ISymbol symbol, final IExpr check, boolean zeroArgsAllowed) {
		PatternSequence p = new PatternSequence();
		p.fSymbol = symbol;
		p.fCondition = check;
		p.fZeroArgsAllowed = zeroArgsAllowed;
		return p;
	}

	public static PatternSequence valueOf(final ISymbol symbol, boolean zeroArgsAllowed) {
		return valueOf(symbol, null, zeroArgsAllowed);
	}

	/**
	 * The expression which should check this pattern sequence
	 */
	IExpr fCondition;

	/**
	 * The associated symbol for this pattern sequence
	 */
	ISymbol fSymbol;

	/**
	 * Use default value, if no matching was found
	 */
	boolean fDefault = false;

	boolean fZeroArgsAllowed = false;

	private PatternSequence() {
	}

	@Override
	public int[] addPattern(List<IExpr> patternIndexMap) {
		IPatternMap.addPattern(patternIndexMap, this);
		// the ast contains a pattern sequence (i.e. "x__")
		int[] result = new int[2];
		result[0] = IAST.CONTAINS_PATTERN_SEQUENCE;
		result[1] = 1;
		if (fCondition != null) {
			result[1] += 2;
		}
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof PatternSequence) {
			PatternSequence pattern = (PatternSequence) obj;
			if (fSymbol == null) {
				if (pattern.fSymbol == null) {
					if (fDefault == pattern.fDefault && fZeroArgsAllowed == pattern.fZeroArgsAllowed) {
						if ((fCondition != null) && (pattern.fCondition != null)) {
							return fCondition.equals(pattern.fCondition);
						}
						return fCondition == pattern.fCondition;
					}
				}
				return false;
			}
			if (fSymbol.equals(pattern.fSymbol) && fDefault == pattern.fDefault
					&& fZeroArgsAllowed == pattern.fZeroArgsAllowed) {
				if ((fCondition != null) && (pattern.fCondition != null)) {
					return fCondition.equals(pattern.fCondition);
				}
				return fCondition == pattern.fCondition;
			}
		}
		return false;
	}

	/**
	 * Check if the two left-hand-side pattern expressions are equivalent. (i.e. <code>f[x_,y_]</code> is equivalent to
	 * <code>f[a_,b_]</code> )
	 * 
	 * @param patternExpr2
	 * @param pm1
	 * @param pm2
	 * @return
	 */
	@Override
	public boolean equivalent(final IPatternObject patternExpr2, final IPatternMap pm1, IPatternMap pm2) {
		if (this == patternExpr2) {
			return true;
		}
		if (patternExpr2 instanceof PatternSequence) {
			// test if the pattern indices are equal
			final IPatternSequence p2 = (IPatternSequence) patternExpr2;
			if (getIndex(pm1) != p2.getIndex(pm2)) {
				return false;
			}
			// test if the "check" expressions are equal
			final Object o1 = getHeadTest();
			final Object o2 = p2.getHeadTest();
			if ((o1 == null) || (o2 == null)) {
				return o1 == o2;
			}
			return o1.equals(o2);
		}
		return false;
	}

	@Override
	public boolean matchPattern(final IExpr expr, IPatternMap patternMap) {
		IAST sequence = F.Sequence(expr);
		return matchPatternSequence(sequence, patternMap);
	}

	@Override
	public boolean matchPatternSequence(final IAST sequence, IPatternMap patternMap) {
		if (!isConditionMatchedSequence(sequence, patternMap)) {
			return false;
		}
		if (sequence.size() == 1 && !isNullSequence()) {
			return false;
		}

		IExpr value = patternMap.getValue(this);
		if (value != null) {
			return sequence.equals(value);
		}
		patternMap.setValue(this, sequence);
		return true;
	}

	@Override
	public IExpr getHeadTest() {
		return fCondition;
	}

	@Override
	public int getEvalFlags() {
		// the ast contains a pattern sequence (i.e. "x__")
		return IAST.CONTAINS_PATTERN_SEQUENCE;
	}

	/**
	 * @return
	 */
	@Override
	public int getIndex(IPatternMap pm) {
		if (pm != null) {
			return pm.get(fSymbol);
		}
		return -1;
	}

	/**
	 * @return
	 */
	@Override
	public ISymbol getSymbol() {
		return fSymbol;
	}

	@Override
	public int hashCode() {
		return (fSymbol == null) ? 203 : 17 + fSymbol.hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public int hierarchy() {
		return PATTERNID;
	}

	@Override
	public String internalJavaString(boolean symbolsAsFactoryMethod, int depth, boolean useOperators, boolean usePrefix,
			boolean noSymbolPrefix) {
		if (symbolsAsFactoryMethod) {
			String prefix = usePrefix ? "F." : "";
			final StringBuilder buffer = new StringBuilder();
			buffer.append(prefix + "$ps(");
			if (fSymbol == null) {
				buffer.append("(ISymbol)null");
				if (fCondition != null) {
					buffer.append("," + fCondition.internalJavaString(symbolsAsFactoryMethod, 0, useOperators,
							usePrefix, noSymbolPrefix));
				}
				if (fDefault) {
					if (fCondition == null) {
						buffer.append(",null");
					}
					buffer.append(",true");
				}
			} else {
				buffer.append("\"" + fSymbol.toString() + "\"");
				if (fCondition != null) {
					buffer.append("," + fCondition.internalJavaString(symbolsAsFactoryMethod, 0, useOperators,
							usePrefix, noSymbolPrefix));
				}
				if (fDefault) {
					buffer.append(",true");
				}
			}
			buffer.append(')');
			return buffer.toString();
		}
		return toString();
	}

	// @Override
	// public void setIndex(final int i) {
	// fIndex = i;
	// }

	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		if (fSymbol == null) {
			buffer.append("__");
			if (fZeroArgsAllowed) {
				buffer.append('_');
			}
			if (fDefault) {
				buffer.append('.');
			}
			if (fCondition != null) {
				buffer.append(fCondition.toString());
			}
		} else {
			if (fCondition == null) {
				buffer.append(fSymbol.toString());
				buffer.append("__");
				if (fZeroArgsAllowed) {
					buffer.append('_');
				}
				if (fDefault) {
					buffer.append('.');
				}
			} else {
				buffer.append(fSymbol.toString());
				buffer.append("__");
				if (fZeroArgsAllowed) {
					buffer.append('_');
				}
				if (fDefault) {
					buffer.append('.');
				}
				buffer.append(fCondition.toString());
			}
		}
		return buffer.toString();
	}

	@Override
	public String fullFormString() {
		StringBuilder buf = new StringBuilder();
		if (fSymbol == null) {
			buf.append("BlankSequence[");
			if (fCondition != null) {
				buf.append(fCondition.fullFormString());
			}
			buf.append(']');
		} else {
			buf.append("PatternSequence[");
			buf.append(fSymbol.toString());
			buf.append(", ");
			buf.append("BlankSequence[");
			if (fCondition != null) {
				buf.append(fCondition.fullFormString());
			}
			buf.append("]]");
		}

		return buf.toString();
	}

	/**
	 * Compares this expression with the specified expression for order. Returns a negative integer, zero, or a positive
	 * integer as this expression is canonical less than, equal to, or greater than the specified expression.
	 */
	@Override
	public int compareTo(final IExpr expr) {
		if (expr instanceof PatternSequence) {
			if (fSymbol == null) {
				if (((PatternSequence) expr).fSymbol != null) {
					return -1;
				}
			} else if (((PatternSequence) expr).fSymbol == null) {
				return 1;
			} else {
				int cp = fSymbol.compareTo(((PatternSequence) expr).fSymbol);
				if (cp != 0) {
					return cp;
				}
			}

			if (fCondition == null) {
				if (((PatternSequence) expr).fCondition != null) {
					return -1;
				}
			} else {
				if (((PatternSequence) expr).fCondition == null) {
					return 1;
				} else {
					return fCondition.compareTo(((PatternSequence) expr).fCondition);
				}
			}
			return 0;
		}
		return IPatternSequence.super.compareTo(expr);
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
	public ISymbol head() {
		return F.Pattern;
	}

	@Override
	public boolean isBlank() {
		return false;
	}

	@Override
	public boolean isConditionMatchedSequence(final IAST sequence, IPatternMap patternMap) {
		if (fCondition == null) {
			patternMap.setValue(this, sequence);
			return true;
		}
		// EvalEngine engine = EvalEngine.get();
		// boolean traceMode = false;
		// traceMode = engine.isTraceMode();
		// final Predicate<IExpr> matcher = Predicates.isTrue(engine, fCondition);
		// try {
		for (int i = 1; i < sequence.size(); i++) {
			if (!sequence.get(i).head().equals(fCondition)) {
				return false;
			}
			// engine.setTraceMode(false);
			//
			// if (matcher.test(sequence.get(i))) {
			// continue;
			// }
			// return false;
		}
		patternMap.setValue(this, sequence);
		return true;
		// } finally {
		// if (traceMode) {
		// engine.setTraceMode(true);
		// }
		// }
	}

	/** {@inheritDoc} */
	@Override
	public IExpr variables2Slots(final Map<IExpr, IExpr> map, final Collection<IExpr> variableCollector) {
		return F.NIL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IExpr accept(IVisitor visitor) {
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

	/**
	 * Use default value, if no matching was found.
	 * 
	 * @return
	 */
	@Override
	public boolean isDefault() {
		return fDefault;
	}

	public boolean isNullSequence() {
		return fZeroArgsAllowed;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isFreeOfPatterns() {
		return false;
	}

	/** {@inheritDoc} */
	// public boolean isFreeOfDefaultPatterns() {
	// return true;
	// }

	/** {@inheritDoc} */
	@Override
	public final boolean isPatternExpr() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	final public boolean isPatternSequence(boolean testNullSequence) {
		if (testNullSequence) {
			return fZeroArgsAllowed;
		}
		return true;
	}

	/**
	 * SymjaMMA operator overloading
	 */
	public boolean isCase(IExpr that) {
		final IPatternMatcher matcher = new PatternMatcher(this);
		if (matcher.test(that)) {
			return true;
		}
		return false;
	}

}