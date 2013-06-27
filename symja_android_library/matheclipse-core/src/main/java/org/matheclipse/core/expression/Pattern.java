package org.matheclipse.core.expression;

import java.util.List;
import java.util.Map;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;

import com.google.common.base.Predicate;

/**
 * A concrete pattern implementation.
 * 
 */
public class Pattern extends ExprImpl implements IPattern {

	private static final long serialVersionUID = 7617138748475243L;

	private static Pattern NULL_PATTERN = new Pattern(null);

	//
	// private static final ObjectFactory<Pattern> FACTORY = new
	// ObjectFactory<Pattern>() {
	// @Override
	// protected Pattern create() {
	// if (Config.SERVER_MODE && currentQueue().getSize() >=
	// Config.PATTERN_MAX_POOL_SIZE) {
	// throw new PoolMemoryExceededException("PatternImpl",
	// currentQueue().getSize());
	// }
	// return new Pattern();
	// }
	// };

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
	// int fIndex = 0;

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
	Pattern(final ISymbol symbol, IExpr condition, boolean def) {
		fHashValue = (symbol == null) ? 199 : 19 + symbol.hashCode();
		fSymbol = symbol;
		fCondition = condition;
		fDefault = def;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Pattern) {
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
	// public int getIndex() {
	// return fIndex;
	// }

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

	// @Override
	// public boolean move(final ObjectSpace os) {
	// if (super.move(os)) {
	// if (fSymbol != null) {
	// fSymbol.move(os);
	// }
	// if (fCondition != null) {
	// fCondition.move(os);
	// }
	// }
	// return false;
	// }
	// public Pattern copy() {
	// // Pattern p;
	// // if (Config.SERVER_MODE) {
	// // p = FACTORY.object();
	// // } else {
	// // p = new Pattern();
	// // }
	// Pattern p= new Pattern();
	// p.fSymbol = fSymbol;
	// if (fCondition != null) {
	// p.fCondition = fCondition.copy();
	// } else {
	// p.fCondition = null;
	// }
	// return p;
	// }
	//
	// public Pattern copyNew() {
	// Pattern r = new Pattern();
	// r.fSymbol = fSymbol;
	// if (fCondition != null) {
	// r.fCondition = fCondition.copyNew();
	// } else {
	// r.fCondition = null;
	// }
	// return r;
	// }

	// public void recycle() {
	// if (fCondition != null) {
	// fCondition.recycle();
	// }
	// FACTORY.recycle(this);
	// }

	// public Text toText() {
	// final TextBuilder tb = TextBuilder.newInstance();
	// if (fSymbol == null) {
	// if (fCondition == null) {
	// tb.append("_");
	// } else {
	// tb.append("_" + fCondition.toString());
	// }
	// } else {
	// if (fCondition == null) {
	// tb.append(fSymbol.toString() + "_");
	// } else {
	// tb.append(fSymbol.toString() + "_" + fCondition.toString());
	// }
	// }
	// return tb.toText();
	// }
	@Override
	public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		if (symbolsAsFactoryMethod) {
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
				if (symbolStr.length() == 1 && fCondition == null && !fDefault) {
					if ('a' <= ch && ch <= 'z') {
						return symbolStr + "_";
					}
				}
				if (symbolStr.length() == 1 && ('a' <= ch && ch <= 'z')) {
					buffer.append(symbolStr);
				} else {
					buffer.append("\"" + symbolStr + "\"");
				}
				if (fCondition != null) {
					buffer.append("," + fCondition.internalFormString(symbolsAsFactoryMethod, 0));
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
		}
		return toString();
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
	 * Compares this expression with the specified expression for order. Returns
	 * a negative integer, zero, or a positive integer as this expression is
	 * canonical less than, equal to, or greater than the specified expression.
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

	/**
	 * Use default value, if not matching was found.
	 * 
	 * @return
	 */
	public boolean isDefault() {
		return fDefault;
	}

	/**
	 * {@inheritDoc}
	 */
	final public boolean isPattern() {
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