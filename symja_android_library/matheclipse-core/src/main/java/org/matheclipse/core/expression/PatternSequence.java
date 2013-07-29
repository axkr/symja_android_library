package org.matheclipse.core.expression;

import java.util.List;
import java.util.Map;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;

import com.google.common.base.Predicate;

/**
 * A concrete pattern sequence implementation
 * 
 */
public class PatternSequence extends ExprImpl implements IPatternSequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2773651826316158627L;

	/**
	 * 
	 */
	public static PatternSequence valueOf(final ISymbol symbol, final IExpr check, final boolean def) {
		PatternSequence p = new PatternSequence();
		p.fSymbol = symbol;
		p.fCondition = check;
		p.fDefault = def;
		return p;
	}

	/**
	 * 
	 * @param numerator
	 * @return
	 */
	public static PatternSequence valueOf(final ISymbol symbol, final IExpr check) {
		PatternSequence p = new PatternSequence();
		p.fSymbol = symbol;
		p.fCondition = check;
		return p;
	}

	public static PatternSequence valueOf(final ISymbol symbol) {
		return valueOf(symbol, null);
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

	/**
	 * Index for the pattern-matcher
	 * 
	 * @see org.matheclipse.core.patternmatching.PatternMatcher
	 */
	int fIndex = -1;

	private PatternSequence() {
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof PatternSequence) {
			PatternSequence pattern = (PatternSequence) obj;
			if (fSymbol == pattern.fSymbol) {
				if ((fCondition != null) && (pattern.fCondition != null)) {
					return fCondition.equals(pattern.fCondition);
				}
				return fCondition == pattern.fCondition;
			}
		}
		return false;
	}

	public IExpr getCondition() {
		return fCondition;
	}

	/**
	 * @return
	 */
	public int getIndex() {
		return fIndex;
	}

	/**
	 * @return
	 */
	public ISymbol getSymbol() {
		return fSymbol;
	}

	@Override
	public int hashCode() {
		if (fSymbol == null) {
			return 203;
		}
		return 17 + fSymbol.hashCode();
	}

	/** {@inheritDoc} */
	public int hierarchy() {
		return PATTERNID;
	}

	@Override
	public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		if (symbolsAsFactoryMethod) {
			final StringBuffer buffer = new StringBuffer();
			buffer.append("$ps(");
			if (fSymbol == null) {
				buffer.append("(ISymbol)null");
				if (fCondition != null) {
					buffer.append("," + fCondition.internalFormString(symbolsAsFactoryMethod, 0));
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
					buffer.append("," + fCondition.internalFormString(symbolsAsFactoryMethod, 0));
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
	
	@Override
	public void setIndex(final int i) {
		fIndex = i;
	}
	
	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		if (fSymbol == null) {
			buffer.append("__");
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
				if (fDefault) {
					buffer.append('.');
				}
			} else {
				buffer.append(fSymbol.toString());
				buffer.append("__");
				if (fDefault) {
					buffer.append('.');
				}
				buffer.append(fCondition.toString());
			}
		}
		return buffer.toString();
	}

	public String fullFormString() {
		StringBuffer buf = new StringBuffer();
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
	 * Compares this expression with the specified expression for order. Returns
	 * a negative integer, zero, or a positive integer as this expression is
	 * canonical less than, equal to, or greater than the specified expression.
	 */
	public int compareTo(final IExpr obj) {
		if (obj instanceof PatternSequence) {
			int cp;
			if (fSymbol == null) {
				if (((PatternSequence) obj).fSymbol == null) {
					cp = -1;
				} else {
					cp = 0;
				}
			} else if (((PatternSequence) obj).fSymbol == null) {
				cp = 1;
			} else {
				cp = fSymbol.compareTo(((PatternSequence) obj).fSymbol);
			}
			if (cp != 0) {
				return cp;
			}
			if (fCondition == null) {
				if (((PatternSequence) obj).fCondition != null) {
					return -1;
				}
				return 0;
			} else {
				if (((PatternSequence) obj).fCondition == null) {
					return 1;
				} else {
					return fCondition.compareTo(((PatternSequence) obj).fCondition);
				}
			}
		}
		return (hierarchy() - (obj).hierarchy());
	}

	public ISymbol head() {
		return F.PatternHead;
	}

	public boolean isBlank() {
		return (fSymbol == null);
	}

	public boolean isConditionMatchedSequence(final IAST sequence) {
		if (fCondition == null) {
			return true;
		}
		EvalEngine engine = EvalEngine.get();
		boolean traceMode = false;
		traceMode = engine.isTraceMode();
		final Predicate<IExpr> matcher = Predicates.isTrue(engine, fCondition);
		try {
			for (int i = 1; i < sequence.size(); i++) {
				if (sequence.get(i).head().equals(fCondition)) {
					continue;
				}
				engine.setTraceMode(false);

				if (matcher.apply(sequence.get(i))) {
					continue;
				}
				return false;

			}
			return true;
		} finally {
			if (traceMode) {
				engine.setTraceMode(true);
			}
		}
	}

	@Override
	public IExpr variables2Slots(final Map<IExpr, IExpr> map, final List<IExpr> variableList) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T accept(IVisitor<T> visitor) {
		return visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean accept(IVisitorBoolean visitor) {
		return visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public int accept(IVisitorInt visitor) {
		return visitor.visit(this);
	}

	/**
	 * Use default value, if no matching was found.
	 * 
	 * @return
	 */
	public boolean isDefault() {
		return fDefault;
	}

	/** {@inheritDoc} */
	public final boolean isPatternExpr() {
		return true;
	}

	/** {@inheritDoc} */
	final public boolean isPatternSequence() {
		return true;
	}

	/**
	 * Groovy operator overloading
	 */
	public boolean isCase(IExpr that) {
		final PatternMatcher matcher = new PatternMatcher(this);
		if (matcher.apply(that)) {
			return true;
		}
		return false;
	}

}