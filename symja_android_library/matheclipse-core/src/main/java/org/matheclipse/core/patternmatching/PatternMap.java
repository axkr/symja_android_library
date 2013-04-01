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
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * A map from a pattern to a possibly found value during pattern-matching.
 * 
 */
public class PatternMap implements Cloneable, Serializable {
	/**
	 * Count the number of patterns in the pattern map.
	 */
	private int fPatternCounter;

	/**
	 * Map a pattern-object to an index in the <code>fPatternValuesArray</code>.
	 */
	private TreeMap<IPatternObject, Integer> fPatternIndexMap;

	/**
	 * Contains the current values of the patterns index stored in
	 * <code>fPatternIndexMap</code>.
	 */
	private IExpr[] fPatternValuesArray;

	public PatternMap() {
		this.fPatternIndexMap = new TreeMap<IPatternObject, Integer>(PatternComparator.CONST);
		this.fPatternCounter = 0;
		this.fPatternValuesArray = new IExpr[0];
	}

	/**
	 * Set the index of <code>fPatternSymbolsArray</code> where the
	 * <code>pattern</code> stores it's assigned value during pattern matching.
	 * 
	 * @param pattern
	 * @param patternIndexMap
	 */
	public void addPattern(IPatternObject pattern) {
		if (pattern.getSymbol() != null && fPatternIndexMap.get(pattern) != null) {
			// for "named" patterns (i.e. "x_" or "x_IntegerQ")
			return;
		}
		fPatternIndexMap.put(pattern, Integer.valueOf(fPatternCounter++));
	}

	/**
	 * Allocate an array of <code>IExpr</code> with the length of the symbols
	 * list.
	 */
	public void allocValuesArray() {
		this.fPatternValuesArray = new IExpr[fPatternCounter];
	}

	@Override
	protected PatternMap clone() {
		PatternMap result = null;
		try {
			result = (PatternMap) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		// avoid Arrays.copyOf because of Android version
		result.fPatternValuesArray = new IExpr[fPatternValuesArray.length];
		System.arraycopy(fPatternValuesArray, 0, result.fPatternValuesArray, 0, fPatternValuesArray.length);
		result.fPatternIndexMap = (TreeMap<IPatternObject, Integer>) fPatternIndexMap.clone();
		result.fPatternCounter = fPatternCounter;
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
	 * Copy the found pattern matches from the given <code>patternMap</code> back
	 * to this maps pattern values.
	 * 
	 * @param patternMap
	 */
	public void copyPatternValuesFromPatternMatcher(final PatternMap patternMap) {
		for (IPatternObject pattern : patternMap.fPatternIndexMap.keySet()) {
			if (pattern.getSymbol() != null) {
				Integer indx = getIndex(pattern);
				if (indx != null) {
					fPatternValuesArray[indx.intValue()] = patternMap.getValue(pattern);
				}
			}
		}
	}

	public int getIndex(IPatternObject pattern) {
		return fPatternIndexMap.get(pattern);
	}

	private Map<ISymbol, IExpr> getRulesMap() {
		final Map<ISymbol, IExpr> rulesMap = new HashMap<ISymbol, IExpr>();
		for (IPatternObject pattern : fPatternIndexMap.keySet()) {
			ISymbol sym = pattern.getSymbol();
			if (sym != null) {
				Integer indx = fPatternIndexMap.get(pattern);
				if (fPatternValuesArray[indx.intValue()] != null) {
					rulesMap.put(sym, fPatternValuesArray[indx.intValue()]);
				}
			}
		}
		return rulesMap;
	}

	public IExpr getValue(IPatternObject pattern) {
		return fPatternValuesArray[getIndex(pattern)];
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
	 * Set all values to <code>null</code>;
	 */
	public void initPattern() {
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
		return fPatternCounter == 0;
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
		fPatternValuesArray[getIndex(pattern)] = expr;
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