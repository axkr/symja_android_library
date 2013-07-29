package org.matheclipse.core.patternmatching;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * A map from a pattern to a possibly found value during pattern-matching.
 * 
 */
public class PatternMap implements Cloneable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5384429232269800438L;

	/**
	 * Count the number of patterns with associated symbols in the pattern map.
	 */
	private int fPatternCounter;

	/**
	 * If <code>true</code> the rule contains no pattern.
	 */
	private boolean fRuleWithoutPattern;

	/**
	 * Map a pattern-object to an index in the <code>fPatternValuesArray</code>.
	 */
	private TreeMap<ISymbol, Integer> fPatternIndexMap;

	/**
	 * Contains the current values of the patterns index stored in
	 * <code>fPatternIndexMap</code>.
	 */
	private IExpr[] fPatternValuesArray;

	public PatternMap() {
		this(new TreeMap<ISymbol, Integer>(), new IExpr[0]);
	}

	private PatternMap(TreeMap<ISymbol, Integer> map, IExpr[] exprArray) {
		this.fPatternIndexMap = map;
		this.fPatternCounter = 0;
		this.fRuleWithoutPattern = true;
		this.fPatternValuesArray = exprArray;
	}

	/**
	 * Set the index of <code>fPatternSymbolsArray</code> where the
	 * <code>pattern</code> stores it's assigned value during pattern matching.
	 * 
	 * @param pattern
	 * @param patternIndexMap
	 */
	protected void addPattern(IPatternObject pattern) {
		fRuleWithoutPattern = false;
		ISymbol sym = pattern.getSymbol();
		if (sym != null) {
			Integer i = fPatternIndexMap.get(sym);
			if (i != null) {
				// for "named" patterns (i.e. "x_" or "x_IntegerQ")
				pattern.setIndex(i.intValue());
				return;
			}
			pattern.setIndex(fPatternCounter);
			fPatternIndexMap.put(sym, Integer.valueOf(fPatternCounter++));
		}
	}

	/**
	 * Determine all patterns (i.e. all objects of instance IPattern) in the
	 * given expression
	 * 
	 * Increments this classes pattern counter.
	 * 
	 * @param lhsPatternExpr
	 * @param patternIndexMap
	 */
	protected int determinePatterns(final IExpr lhsPatternExpr) {
		if (lhsPatternExpr instanceof IAST) {
			final IAST ast = (IAST) lhsPatternExpr;
			int listEvalFlags = IAST.NO_FLAG;
			listEvalFlags |= determinePatterns(ast.head());
			for (int i = 1; i < ast.size(); i++) {
				if (ast.get(i).isPattern()) {
					IPattern pat = (IPattern) ast.get(i);
					addPattern(pat);
					if (pat.isDefault()) {
						// the ast contains a pattern with default value (i.e.
						// "x_.")
						listEvalFlags |= IAST.CONTAINS_DEFAULT_PATTERN;
					} else {
						// the ast contains a pattern without value (i.e. "x_")
						listEvalFlags |= IAST.CONTAINS_PATTERN;
					}
				} else if (ast.get(i).isPatternSequence()) {
					IPatternSequence pat = (IPatternSequence) ast.get(i);
					addPattern(pat);
					// the ast contains a pattern sequence (i.e. "x__")
					listEvalFlags |= IAST.CONTAINS_PATTERN_SEQUENCE;
				} else {
					listEvalFlags |= determinePatterns(ast.get(i));
				}
			}
			ast.setEvalFlags(listEvalFlags);
			// disable flag "pattern with default value"
			listEvalFlags &= IAST.CONTAINS_NO_DEFAULT_PATTERN_MASK;
			this.fPatternValuesArray = new IExpr[fPatternCounter];
			return listEvalFlags;
		} else {
			if (lhsPatternExpr.isPattern()) {
				addPattern((IPattern) lhsPatternExpr);
				this.fPatternValuesArray = new IExpr[fPatternCounter];
				return IAST.CONTAINS_PATTERN;
			} else if (lhsPatternExpr.isPatternSequence()) {
				addPattern((IPatternSequence) lhsPatternExpr);
				this.fPatternValuesArray = new IExpr[fPatternCounter];
				return IAST.CONTAINS_PATTERN_SEQUENCE;
			}
		}
		this.fPatternValuesArray = new IExpr[fPatternCounter];
		return IAST.NO_FLAG;
	}

	@Override
	protected PatternMap clone() {
		PatternMap result = new PatternMap(null, null);
		// avoid Arrays.copyOf because of Android version
		result.fPatternValuesArray = new IExpr[fPatternValuesArray.length];
		System.arraycopy(fPatternValuesArray, 0, result.fPatternValuesArray, 0, fPatternValuesArray.length);
		// don't clone the map which is final after the #determinepatterns()
		// mehtod
		result.fPatternIndexMap = fPatternIndexMap;
		result.fPatternCounter = fPatternCounter;
		result.fRuleWithoutPattern = fRuleWithoutPattern;
		return result;
	}

	/**
	 * Copy the current values into a new array.
	 * 
	 * @return
	 * @see PatternMap#resetPattern(IExpr[])
	 */
	public IExpr[] copyPattern() {
		IExpr[] patternValuesArray = new IExpr[fPatternValuesArray.length];
		System.arraycopy(fPatternValuesArray, 0, patternValuesArray, 0, fPatternValuesArray.length);
		return patternValuesArray;
	}

	/**
	 * Copy the found pattern matches from the given <code>patternMap</code>
	 * back to this maps pattern values.
	 * 
	 * @param patternMap
	 */
	public void copyPatternValuesFromPatternMatcher(final PatternMap patternMap) {
		for (ISymbol pattern : patternMap.fPatternIndexMap.keySet()) {
			if (pattern != null) {
				int indx = getIndex(pattern);
				fPatternValuesArray[indx] = patternMap.getValue(pattern);
			}
		}
	}

	protected int getIndex(ISymbol pattern) {
		Integer indx = fPatternIndexMap.get(pattern);
		if (indx == null) {
			return -1;
		}
		return indx;
	}

	// protected int getIndex(IPatternObject pattern) {
	// ISymbol sym = pattern.getSymbol();
	// if (sym != null) {
	// return fPatternIndexMap.get(sym);
	// }
	// return -1;
	// }

	private Map<ISymbol, IExpr> getRulesMap() {
		final Map<ISymbol, IExpr> rulesMap = new HashMap<ISymbol, IExpr>();
		for (ISymbol sym : fPatternIndexMap.keySet()) {
			if (sym != null) {
				Integer indx = fPatternIndexMap.get(sym);
				if (fPatternValuesArray[indx.intValue()] != null) {
					rulesMap.put(sym, fPatternValuesArray[indx.intValue()]);
				}
			}
		}
		return rulesMap;
	}

	public IExpr getValue(ISymbol pattern) {
		Integer indx = getIndex(pattern);
		if (indx == null) {
			return null;
		}

		return fPatternValuesArray[indx];
	}

	public IExpr getValue(IPatternObject pattern) {
		int indx = pattern.getIndex();
		if (indx >= 0) {
			return fPatternValuesArray[indx];
		}
		return null;
		// ISymbol sym = pattern.getSymbol();
		// if (sym != null) {
		// Integer indx = getIndex(pattern);
		// if (indx == null) {
		// return null;
		// }
		//
		// return fPatternValuesArray[indx];
		// }
		// return null;
	}

	public List<IExpr> getValuesAsList() {
		List<IExpr> args = new ArrayList<IExpr>(fPatternValuesArray.length);
		IExpr arg;
		for (int i = 0; i < fPatternValuesArray.length; i++) {
			arg = fPatternValuesArray[i];
			if (arg == null)
				return null;
			args.add(arg);
		}
		return args;
	}

	/**
	 * Set all pattern values to <code>null</code>;
	 */
	protected void initPattern() {
		Arrays.fill(fPatternValuesArray, null);
	}

	/**
	 * Check if all symbols in the symbols array have corresponding values
	 * assigned.
	 * 
	 * @return
	 */
	public boolean isAllPatternsAssigned() {
		if (fPatternValuesArray != null) {
			// all patterns have values assigned?
			for (int i = 0; i < fPatternValuesArray.length; i++) {
				if (fPatternValuesArray[i] == null) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns true if the given expression contains no patterns
	 * 
	 * @return
	 */
	public boolean isRuleWithoutPatterns() {
		return fRuleWithoutPattern;
	}

	/**
	 * Reset the values to the values in the given array
	 * 
	 * @param patternValuesArray
	 * @see PatternMap#copyPattern()
	 */
	public void resetPattern(IExpr[] patternValuesArray) {
		System.arraycopy(patternValuesArray, 0, fPatternValuesArray, 0, fPatternValuesArray.length);
	}

	public void setValue(IPatternObject pattern, IExpr expr) {
		int indx = pattern.getIndex();
		if (indx >= 0) {
			fPatternValuesArray[indx] = expr;
		}
	}

	public void setValue(ISymbol pattern, IExpr expr) {
		Integer indx = getIndex(pattern);
		if (indx != null) {
			fPatternValuesArray[indx] = expr;
		}
	}

	public int size() {
		return fPatternValuesArray.length;
	}

	/**
	 * Substitute all symbols in the given expression with the current value of
	 * the given arrays
	 * 
	 * @param expression
	 * 
	 * @return
	 */
	public IExpr substitutePatternSymbols(final IExpr expression) {
		if (fPatternValuesArray != null) {
			final Map<ISymbol, IExpr> rulesMap = getRulesMap();
			return F.subst(expression, Functors.rules(rulesMap));
		}
		return expression;
	}

}