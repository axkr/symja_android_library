package org.matheclipse.core.patternmatching;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
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
public class PatternMap implements ISymbol2IntMap, Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5384429232269800438L;

	/**
	 * Priority of this PatternMap. Lower values have higher priorities
	 */
	protected int fPriority;

	/**
	 * Count the number of patterns with associated symbols in the pattern map.
	 */
	private int fPatternCounter;

	/**
	 * If <code>true</code> the rule contains no pattern.
	 */
	private boolean fRuleWithoutPattern;

	/**
	 * Contains the symbols of the patterns. The corresponding value (or <code>null</code>) is stored in
	 * <code>fPatternValuesArray</code>.
	 */
	private ISymbol[] fSymbolsArray;

	/**
	 * Contains the current values of the pattern symbols. The corresponding symbol is stored in <code>fSymbolsArray</code>.
	 */
	private IExpr[] fPatternValuesArray;

	/**
	 * The default priority when associating a new rule to a symbol. Lower values have higher priorities.
	 */
	public final static int DEFAULT_RULE_PRIORITY = Integer.MAX_VALUE;

	protected PatternMap() {
		this(new IExpr[0]);
	}

	private PatternMap(IExpr[] exprArray) {
		this.fPriority = 0;
		this.fPatternCounter = 0;
		this.fRuleWithoutPattern = true;
		this.fPatternValuesArray = exprArray;
	}

	/**
	 * Set the index of <code>fPatternSymbolsArray</code> where the <code>pattern</code> stores it's assigned value during pattern
	 * matching.
	 * 
	 * @param pattern
	 * @param patternIndexMap
	 */
	public void addPattern(Map<ISymbol, Integer> patternIndexMap, IPatternObject pattern) {
		fRuleWithoutPattern = false;
		ISymbol sym = pattern.getSymbol();
		if (sym != null) {
			Integer i = patternIndexMap.get(sym);
			if (i != null) {
				return;
			}
			patternIndexMap.put(sym, Integer.valueOf(fPatternCounter++));
		}
	}

	protected void addSinglePattern(IPatternObject pattern) {
		fRuleWithoutPattern = false;
		ISymbol sym = pattern.getSymbol();
		if (sym != null) {
			this.fSymbolsArray = new ISymbol[1];
			this.fPatternValuesArray = new IExpr[1];
			fSymbolsArray[0] = sym;
		}
	}

	@Override
	protected PatternMap clone() {
		PatternMap result = new PatternMap(null);
		// avoid Arrays.copyOf because of Android version
		result.fPatternValuesArray = new IExpr[fPatternValuesArray.length];
		System.arraycopy(fPatternValuesArray, 0, result.fPatternValuesArray, 0, fPatternValuesArray.length);
		// don't clone the fSymbolsArray which is final after the
		// #determinepatterns() method
		result.fPriority = fPriority;
		result.fSymbolsArray = fSymbolsArray;
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
	protected IExpr[] copyPattern() {
		IExpr[] patternValuesArray = new IExpr[fPatternValuesArray.length];
		System.arraycopy(fPatternValuesArray, 0, patternValuesArray, 0, fPatternValuesArray.length);
		return patternValuesArray;
	}

	/**
	 * Copy the found pattern matches from the given <code>patternMap</code> back to this maps pattern values.
	 * 
	 * @param patternMap
	 */
	protected void copyPatternValuesFromPatternMatcher(final PatternMap patternMap) {
		ISymbol[] symbolsArray = patternMap.fSymbolsArray;
		for (int i = 0; i < symbolsArray.length; i++) {
			for (int j = 0; j < fSymbolsArray.length; j++) {
				if (fSymbolsArray[j] == symbolsArray[i]) {
					fPatternValuesArray[j] = patternMap.fPatternValuesArray[i];
				}
			}
		}
	}

	/**
	 * Determine all patterns (i.e. all objects of instance IPattern) in the given expression
	 * 
	 * Increments this classes pattern counter.
	 * 
	 * @param lhsPatternExpr
	 *            the (left-hand-side) expression which could contain pattern objects.
	 * @return the piority of this pattern-matcher
	 */
	protected int determinePatterns(final IExpr lhsPatternExpr) {
		fPriority = DEFAULT_RULE_PRIORITY;
		if (lhsPatternExpr instanceof IAST) {
			Map<ISymbol, Integer> patternIndexMap = new IdentityHashMap<ISymbol, Integer>();
			determinePatternsRecursive(patternIndexMap, (IAST) lhsPatternExpr, 1);
			this.fSymbolsArray = new ISymbol[fPatternCounter];
			this.fPatternValuesArray = new IExpr[fPatternCounter];
			for (ISymbol sym : patternIndexMap.keySet()) {
				Integer indx = patternIndexMap.get(sym);
				fSymbolsArray[indx.intValue()] = sym;
			}
		} else if (lhsPatternExpr instanceof IPatternObject) {
			addSinglePattern((IPatternObject) lhsPatternExpr);
		}
		return fPriority;
	}

	/**
	 * Determine all patterns (i.e. all objects of instance IPattern) in the given expression
	 * 
	 * Increments this classes pattern counter.
	 * 
	 * @param patternIndexMap
	 * @param lhsPatternExpr
	 *            the (left-hand-side) expression which could contain pattern objects.
	 * @param treeLevel
	 *            TODO
	 */
	private int determinePatternsRecursive(Map<ISymbol, Integer> patternIndexMap, final IAST lhsPatternExpr, int treeLevel) {
		final IAST ast = lhsPatternExpr;
		int listEvalFlags = IAST.NO_FLAG;
		for (int i = 0; i < ast.size(); i++) {
			IExpr temp = ast.get(i);
			if (temp.isAST()) {
				listEvalFlags |= determinePatternsRecursive(patternIndexMap, (IAST) temp, treeLevel + 1);
				fPriority -= 11;
			} else if (temp instanceof IPatternObject) {
				int[] result = ((IPatternObject) temp).addPattern(this, patternIndexMap);
				listEvalFlags |= result[0];
				fPriority -= result[1];
			} else {
				fPriority -= (50 - treeLevel);
			}
		}
		ast.setEvalFlags(listEvalFlags);
		// disable flag "pattern with default value"
		listEvalFlags &= IAST.CONTAINS_NO_DEFAULT_PATTERN_MASK;
		return listEvalFlags;
	}

	/** {@inheritDoc} */
	@Override
	public int get(ISymbol key) {
		if (key != null) {
			for (int i = 0; i < fSymbolsArray.length; i++) {
				if (fSymbolsArray[i].equals((ISymbol) key)) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Get the priority of this PatternMap
	 * 
	 * @return
	 */
	public int getPriority() {
		return fPriority;
	}

	private Map<ISymbol, IExpr> getRulesMap() {
		final Map<ISymbol, IExpr> rulesMap = new IdentityHashMap<ISymbol, IExpr>(fSymbolsArray.length * 2);
		for (int i = 0; i < fSymbolsArray.length; i++) {
			if (fPatternValuesArray[i] != null) {
				rulesMap.put(fSymbolsArray[i], fPatternValuesArray[i]);
			}
		}
		return rulesMap;
	}

	/**
	 * Return the matched value for the given <code>index</code> if possisble.
	 * 
	 * @return <code>null</code> if no matched expression exists
	 */
	protected IExpr getValue(int index) {
		if (index < fPatternValuesArray.length) {
			return fPatternValuesArray[index];
		}
		return null;
	}

	/**
	 * Return the matched value for the given pattern object
	 * 
	 * @param pExpr
	 * @return <code>null</code> if no matched expression exists
	 */
	public IExpr getValue(IPatternObject pattern) {
		int indx = get(pattern.getSymbol());
		return indx >= 0 ? fPatternValuesArray[indx] : null;
	}

	protected List<IExpr> getValuesAsList() {
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
	 * Check if all symbols in the symbols array have corresponding values assigned.
	 * 
	 * @return
	 */
	protected boolean isAllPatternsAssigned() {
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

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return fSymbolsArray.length > 0;
	}

	/**
	 * Returns true if the given expression contains no patterns
	 * 
	 * @return
	 */
	protected boolean isRuleWithoutPatterns() {
		return fRuleWithoutPattern;
	}

	/**
	 * Reset the values to the values in the given array
	 * 
	 * @param patternValuesArray
	 * @see PatternMap#copyPattern()
	 */
	protected void resetPattern(IExpr[] patternValuesArray) {
		System.arraycopy(patternValuesArray, 0, fPatternValuesArray, 0, fPatternValuesArray.length);
	}

	public void setValue(IPatternObject pattern, IExpr expr) {
		int indx = get(pattern.getSymbol());
		if (indx >= 0) {
			fPatternValuesArray[indx] = expr;
		}
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return fSymbolsArray.length;
	}

	/**
	 * Substitute all symbols in the given expression with the current value of the given arrays
	 * 
	 * @param expression
	 * 
	 * @return
	 */
	protected IExpr substitutePatternSymbols(final IExpr expression) {
		if (fPatternValuesArray != null) {
			return F.subst(expression, Functors.rules(getRulesMap()));
		}
		return expression;
	}

}