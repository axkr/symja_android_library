package org.matheclipse.core.eval.util;

import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Options;
import static org.matheclipse.core.expression.F.ReplaceAll;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderByName;

/**
 * Managing <i>Options</i> used in evaluation of function symbols (i.e. <code>Modulus-&gt;n</code> is an option which
 * could be used for an integer <code>n</code> in a function like <code>Factor(polynomial, Modulus-&gt;2)</code>.
 * 
 */
public class OptionArgs {
	private IAST fDefaultOptionsList;

	private IASTAppendable fCurrentOptionsList;

	private final EvalEngine fEngine;

	private int fLastPosition = -1;

	/**
	 * Construct special <i>Options</i> used in evaluation of function symbols (i.e. <code>Modulus-&gt;n</code> is an
	 * option which could be used for an integer <code>n</code> in a function like
	 * <code>Factor(polynomial, Modulus-&gt;2)</code>.
	 * 
	 * @param symbol
	 *            the options symbol for determining &quot;default option values&quot;
	 * @param currentOptionsList
	 *            the AST where the option could be defined starting at position <code>startIndex</code>
	 * @param startIndex
	 *            the index from which to look for options defined in <code>currentOptionsList</code>
	 */
	public OptionArgs(final ISymbol symbol, final IAST currentOptionsList, final int startIndex,
			final EvalEngine engine) {
		fEngine = engine;
		// get the List of pre-defined options:
		final IExpr temp = fEngine.evaluate(Options(symbol));
		if ((temp != null) && (temp instanceof IAST) && temp.isList()) {
			fDefaultOptionsList = (IAST) temp;
		} else {
			fDefaultOptionsList = null;
		}
		this.fCurrentOptionsList = null;

		if (currentOptionsList != null && startIndex < currentOptionsList.size()) {
			int size = currentOptionsList.size();
			this.fCurrentOptionsList = F.ListAlloc(size);
			for (int i = startIndex; i < size; i++) {
				this.fCurrentOptionsList.append(1, currentOptionsList.get(i));
			}
		}
	}

	/**
	 * Construct special <i>Options</i> used in evaluation of function symbols (i.e. <code>Modulus-&gt;n</code> is an
	 * option which could be used for an integer <code>n</code> in a function like
	 * <code>Factor(polynomial, Modulus-&gt;2)</code>.
	 * 
	 * @param symbol
	 *            the options symbol for determining &quot;default option values&quot;
	 * @param currentOptionsList
	 *            the AST where the option could be defined starting at position <code>startIndex</code>
	 * @param startIndex
	 *            the index from which to look for options defined in <code>currentOptionsList</code>
	 * @param endIndex
	 *            the index from which to look for options defined in <code>currentOptionsList</code>
	 */
	public OptionArgs(final ISymbol symbol, final IAST currentOptionsList, final int startIndex, final int endIndex,
			final EvalEngine engine) {
		fEngine = engine;
		// get the List of pre-defined options:
		final IExpr temp = fEngine.evaluate(Options(symbol));
		if ((temp != null) && (temp instanceof IAST) && temp.isList()) {
			fDefaultOptionsList = (IAST) temp;
		} else {
			fDefaultOptionsList = null;
		}
		this.fCurrentOptionsList = null;

		if (currentOptionsList != null && startIndex < currentOptionsList.size()) {
			int size = currentOptionsList.size();
			this.fCurrentOptionsList = F.ListAlloc(size);
			for (int i = endIndex - 1; i >= startIndex; i--) {
				IExpr opt = currentOptionsList.get(i);
				if (opt.isRule()) {
					fLastPosition = i;
					this.fCurrentOptionsList.append(1, opt);
				} else {
					break;
				}
			}
		}
	}

	/**
	 * Construct <i>Options</i> used in evaluation of function symbols (i.e. <code>Modulus-&gt;n</code> is an option
	 * which could be used for an integer <code>n</code> in a function like
	 * <code>Factor(polynomial, Modulus-&gt;2)</code>.
	 * 
	 * @param symbol
	 *            the options symbol for determining &quot;default option values&quot;
	 * @param optionExpr
	 *            the value which should be defined for the option
	 */
	public OptionArgs(final ISymbol symbol, final IExpr optionExpr, final EvalEngine engine) {
		fEngine = engine;
		// get the List of pre-defined options:
		final IExpr temp = fEngine.evaluate(Options(symbol));
		if ((temp != null) && (temp instanceof IAST) && temp.isList()) {
			fDefaultOptionsList = (IAST) temp;
		} else {
			fDefaultOptionsList = null;
		}
		this.fCurrentOptionsList = F.ListAlloc();
		this.fCurrentOptionsList.append(optionExpr);
	}

	/**
	 * Get the option from the internal options list and check if it's set to <code>F.True</code>.
	 * 
	 * @param option
	 *            the option
	 * @return <code>true</code> if the option is set to <code>True</code> or <code>false</code> otherwise.
	 */
	public boolean isTrue(final ISymbol option) {
		return getOption(option).isTrue();
	}

	/**
	 * Get the option from the internal options list and check if it's set to <code>F.False</code>.
	 * 
	 * @param option
	 *            the option
	 * @return <code>true</code> if the option is set to <code>False</code> or <code>false</code> otherwise.
	 */
	public boolean isFalse(final ISymbol option) {
		return getOption(option).isFalse();
	}

	/**
	 * If option 'Automatic' is set, return 'F.NIL'.
	 * 
	 * @param option
	 * @return
	 */
	public IExpr getOptionAutomatic(final ISymbol option) {
		IExpr temp = getOption(option);
		if (temp == F.Automatic) {
			return F.NIL;
		}
		return temp;
	}

	/**
	 * Get the option which ist set in the options argument <code>option -&gt; ...</code>.
	 * 
	 * @param option
	 * @return the found option value or <code>F.NIL</code> if the option is not available.
	 */
	public IExpr getOption(final ISymbol option) {
		IAST[] rule = new IAST[1];
		if (fCurrentOptionsList != null) {
			try {
				if (fCurrentOptionsList.exists(x -> {
					if (x.isAST()) {
						IAST temp = (IAST) x;
						if (temp.isRuleAST() && temp.arg1().equals(option)) {
							rule[0] = temp;
							return true;
						}
					}
					return false;
				})) {
					return rule[0].arg2();
				}
			} catch (Exception e) {

			}
		}
		if (fDefaultOptionsList != null) {
			try {
				if (fDefaultOptionsList.exists(x -> {
					if (x.isAST()) {
						IAST temp = (IAST) x;
						if (temp.isRuleAST() && temp.arg1().equals(option)) {
							rule[0] = temp;
							return true;
						}
					}
					return false;
				}, 1)) {
					return rule[0].arg2();
				}
			} catch (Exception e) {

			}
		}
		return F.NIL;
	}

	/**
	 * Get the last position which is not an option rule.
	 * 
	 * @return <code>-1</code> if no options is found
	 */
	public int getLastPosition() {
		return fLastPosition;
	}

	public IAST replaceAll(final IAST options) {
		if (fCurrentOptionsList != null) {
			return (IAST) fEngine.evaluate(ReplaceAll(options, fCurrentOptionsList));
		}
		if (fDefaultOptionsList != null) {
			return (IAST) fEngine.evaluate(ReplaceAll(options, fDefaultOptionsList));
		}
		return options;
	}

	/**
	 * Map the <code>MonomialOrder-&gt;...</code> option to JAS TermOrder.
	 * 
	 * @param defaultTermOrder
	 *            the term order which should be used as default if no MonomialOrder option is set.
	 * @return
	 */
	public TermOrder monomialOrder(final TermOrder defaultTermOrder) {
		TermOrder termOrder = defaultTermOrder;
		IExpr option = getOption(F.MonomialOrder);
		if (option.isSymbol()) {
			// String orderStr = option.toString();
			termOrder = monomialOrder((ISymbol) option, termOrder);
		}
		return termOrder;
	}

	/**
	 * Map the polynomial order option symbol to JAS TermOrder.
	 * 
	 * @param orderOption
	 * @param defaultTermOrder
	 * @return
	 */
	public static TermOrder monomialOrder(ISymbol orderOption, TermOrder defaultTermOrder) {
		TermOrder termOrder = defaultTermOrder;
		if (orderOption == F.Lexicographic) {
			termOrder = TermOrderByName.Lexicographic;
		} else if (orderOption == F.NegativeLexicographic) {
			termOrder = TermOrderByName.NegativeLexicographic;
		} else if (orderOption == F.DegreeLexicographic) {
			termOrder = TermOrderByName.DegreeLexicographic;
		} else if (orderOption == F.DegreeReverseLexicographic) {
			termOrder = TermOrderByName.DegreeReverseLexicographic;
		} else if (orderOption == F.NegativeDegreeLexicographic) {
			termOrder = TermOrderByName.NegativeDegreeLexicographic;
		} else if (orderOption == F.NegativeDegreeReverseLexicographic) {
			termOrder = TermOrderByName.NegativeDegreeReverseLexicographic;
		}
		return termOrder;
	}

}
