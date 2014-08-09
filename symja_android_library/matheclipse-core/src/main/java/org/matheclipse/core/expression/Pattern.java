package org.matheclipse.core.expression;

import java.util.List;
import java.util.Map;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMap;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

import com.google.common.base.Predicate;

/**
 * A concrete pattern implementation (i.e. x_)
 * 
 */
public class Pattern extends ExprImpl implements IPattern {

	private static final long serialVersionUID = 7617138748475243L;

	private static Pattern NULL_PATTERN = new Pattern(null);

	/**
	 * 
	 */
	public static IPattern valueOf(final ISymbol symbol, final IExpr check, final boolean def) {
		return new Pattern(symbol, check, def);
		// p.fSymbol = symbol;
		// p.fCondition = check;
		// p.fDefault = def;
		// return p;
	}

	/**
	 * 
	 * @param numerator
	 * @return
	 */
	public static IPattern valueOf(final ISymbol symbol, final IExpr check) {
		return new Pattern(symbol, check);
		// p.fSymbol = symbol;
		// p.fCondition = check;
		// return p;
	}

	public static IPattern valueOf(final ISymbol symbol) {
		if (symbol == null) {
			return NULL_PATTERN;
		}
		IPattern value = F.PREDEFINED_PATTERN_MAP.get(symbol.toString());
		if (value != null) {
			return value;
		}
		return new Pattern(symbol);
	}

	/**
	 * The expression which should check this pattern
	 */
	final IExpr fCondition;

	/**
	 * Index for the pattern-matcher
	 * 
	 * @see org.matheclipse.core.patternmatching.PatternMatcher
	 */
	// int fIndex = -1;

	/**
	 * The hash value of this object computed in the constructor.
	 * 
	 */
	final int fHashValue;

	/**
	 * The associated symbol for this pattern
	 */
	final ISymbol fSymbol;

	/**
	 * Use default value, if no matching expression was found
	 */
	final boolean fDefault;

	private Pattern() {
		this(null, null, false);
	}

	/** package private */
	Pattern(final ISymbol symbol) {
		this(symbol, null, false);
	}

	/** package private */
	Pattern(final ISymbol symbol, IExpr condition) {
		this(symbol, condition, false);
	}

	/** package private */
	public Pattern(final ISymbol symbol, IExpr condition, boolean def) {
		fHashValue = (symbol == null) ? 199 : 19 + symbol.hashCode();
		fSymbol = symbol;
		fCondition = condition;
		fDefault = def;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Pattern) {
			if (hashCode() != obj.hashCode()) {
				return false;
			}
			if (this == obj) {
				return true;
			}
			Pattern pattern = (Pattern) obj;
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
	public int getIndex(PatternMap pm) {
		if (pm != null) {
			return pm.get(fSymbol);
		}
		return -1;
	}

	/**
	 * @return
	 */
	public ISymbol getSymbol() {
		return fSymbol;
	}

	@Override
	public int hashCode() {
		return fHashValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.matheclipse.parser.interfaces.IExpr#hierarchy()
	 */
	public int hierarchy() {
		return PATTERNID;
	}

	// public boolean isString(final String str) {
	// if (fSymbol == null) {
	// return str == null;
	// }
	// return fSymbol.toString().equals(str);
	// }

	// public void setIndex(final int i) {
	// fIndex = i;
	// }

	// public String toString() {
	// if (fCheck == null) {
	// return fSymbol.toString() + "_";
	// }
	// return fSymbol.toString() + "_" + fCheck.toString();
	// }

	@Override
	public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		// if (symbolsAsFactoryMethod) {
		final StringBuffer buffer = new StringBuffer();
		buffer.append("$p(");
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
			String symbolStr = fSymbol.toString();
			char ch = symbolStr.charAt(0);
			if (symbolStr.length() == 1) {
				if ('a' <= ch && ch <= 'z') {
					if (!fDefault) {
						if (fCondition == null) {
							if (ch == 'd' || ch == 'e' || ch == 'i') {
								return "$p(" + symbolStr + ")";
							} else {
								return symbolStr + "_";
							}
						} else if (fCondition == F.SymbolHead) {
							if (ch == 'x') {
								return "x_Symbol";
							} else if (ch == 'y') {
								return "y_Symbol";
							} else if (ch == 'z') {
								return "z_Symbol";
							}
						}
					} else {
						if (fCondition == null) {
							if (ch == 'd' || ch == 'e' || ch == 'i') {
							} else {
								return symbolStr + "_DEFAULT";
							}
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
								return "p" + ch2 + "_";
							}
						} else {
							if (fCondition == null) {
								return "p" + ch2 + "_DEFAULT";
							}
						}
					}
				}
			}

			if (symbolStr.length() == 1 && ('a' <= ch && ch <= 'z')) {
				buffer.append(symbolStr);
			} else {
				buffer.append("\"" + symbolStr + "\"");
			}
			if (fCondition != null) {
				if (fCondition == F.SymbolHead) {
					buffer.append(", SymbolHead");
				} else {
					buffer.append("," + fCondition.internalFormString(symbolsAsFactoryMethod, 0));
				}
			}
			if (fDefault) {
				// if (fCondition == null) {
				// buffer.append(",null");
				// }
				buffer.append(",true");
			}
		}
		buffer.append(')');
		return buffer.toString();
		// }
		// return toString();
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		if (fSymbol == null) {
			buffer.append('_');
			if (fDefault) {
				buffer.append('.');
			}
			if (fCondition != null) {
				buffer.append(fCondition.toString());
			}
		} else {
			if (fCondition == null) {
				buffer.append(fSymbol.toString());
				buffer.append('_');
				if (fDefault) {
					buffer.append('.');
				}
			} else {
				buffer.append(fSymbol.toString());
				buffer.append('_');
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
		} else {
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
		}

		return buf.toString();
	}

	/**
	 * Compares this expression with the specified expression for order. Returns a negative integer, zero, or a positive integer as
	 * this expression is canonical less than, equal to, or greater than the specified expression.
	 */
	public int compareTo(final IExpr obj) {
		if (obj instanceof Pattern) {
			int cp;
			if (fSymbol == null) {
				if (((Pattern) obj).fSymbol == null) {
					cp = -1;
				} else {
					cp = 0;
				}
			} else if (((Pattern) obj).fSymbol == null) {
				cp = 1;
			} else {
				cp = fSymbol.compareTo(((Pattern) obj).fSymbol);
			}
			if (cp != 0) {
				return cp;
			}
			if (fCondition == null) {
				if (((Pattern) obj).fCondition != null) {
					return -1;
				}
				return 0;
			} else {
				if (((Pattern) obj).fCondition == null) {
					return 1;
				} else {
					return fCondition.compareTo(((Pattern) obj).fCondition);
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

	public boolean isConditionMatched(final IExpr expr) {
		if (fCondition == null) {
			return true;
		}
		if (expr.head().equals(fCondition)) {
			return true;
		}
		EvalEngine engine = EvalEngine.get();
		boolean traceMode = false;
		try {
			traceMode = engine.isTraceMode();
			engine.setTraceMode(false);
			final Predicate<IExpr> matcher = Predicates.isTrue(engine, fCondition);
			return matcher.apply(expr);
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

	/** {@inheritDoc} */
	@Override
	public long accept(IVisitorLong visitor) {
		return visitor.visit(this);
	}

	/**
	 * Use default value, if not matching was found.
	 * 
	 * @return
	 */
	public boolean isPatternDefault() {
		return fDefault;
	}

	/** {@inheritDoc} */
	final public boolean isPattern() {
		return true;
	}

	/** {@inheritDoc} */
	public final boolean isPatternExpr() {
		return true;
	}

	/**
	 * Groovy operator overloading
	 */
	public boolean isCase(IExpr that) {
		final IPatternMatcher matcher = new PatternMatcher(this);
		if (matcher.apply(that)) {
			return true;
		}
		return false;
	}

}