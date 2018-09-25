package org.matheclipse.core.patternmatching;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;

import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * A map from a pattern to a possibly found value during pattern-matching.
 * 
 */
public final class PatternMap implements ISymbol2IntMap, Cloneable, Serializable {

	private final static IExpr[] EMPTY_ARRAY = {};

	/**
	 * 
	 */
	private static final long serialVersionUID = -5384429232269800438L;

	/**
	 * Priority of this PatternMap. Lower values have higher priorities
	 */
	protected int fPriority;

	/**
	 * If <code>true</code> the rule contains no pattern.
	 */
	private boolean fRuleWithoutPattern;

	/**
	 * Contains the symbols of the patterns or the pattern objects itself. The corresponding value (or <code>null</code>) is stored in
	 * <code>fSymbolsOrPatternValues</code>.
	 */
	private IExpr[] fSymbolsOrPattern;

	/**
	 * Contains the current values (or <code>null</code>) of the symbols of the patterns or the pattern objects itself. The
	 * corresponding symbol or pattern is stored in <code>fSymbolsOrPattern</code>.
	 */
	private IExpr[] fSymbolsOrPatternValues;

	/**
	 * The default priority when associating a new rule to a symbol. Lower values have higher priorities.
	 */
	public final static int DEFAULT_RULE_PRIORITY = Integer.MAX_VALUE;

	private transient boolean evaluatedRHS = false;

	public PatternMap() {
		this(EMPTY_ARRAY);
	}

	private PatternMap(IExpr[] exprArray) {
		this.fPriority = 0;
		this.fRuleWithoutPattern = true;
		this.fSymbolsOrPatternValues = exprArray;
	}

	/**
	 * Set the index of <code>fPatternSymbolsArray</code> where the <code>pattern</code> stores it's assigned value during pattern
	 * matching.
	 * 
	 * @param pattern
	 * @param patternIndexMap
	 */
	public void addPattern(Set<IExpr> patternIndexMap, IPatternObject pattern) {
		fRuleWithoutPattern = false;
		ISymbol sym = pattern.getSymbol();
		if (sym != null) {
			patternIndexMap.add(sym);
			return;
		}
		patternIndexMap.add(pattern);
	}

	protected void addSinglePattern(IPatternObject pattern) {
		fRuleWithoutPattern = false;
		this.fSymbolsOrPattern = new IExpr[1];
		this.fSymbolsOrPatternValues = new IExpr[1];
		final ISymbol sym = pattern.getSymbol();
		fSymbolsOrPattern[0] = (sym != null) ? sym : pattern;
	}

	@Override
	protected PatternMap clone() {
		PatternMap result = new PatternMap(null);
		// avoid Arrays.copyOf because of Android version
		final int length = fSymbolsOrPatternValues.length;
		result.fSymbolsOrPatternValues = new IExpr[length];
		// System.arraycopy(fPatternValuesArray, 0, result.fPatternValuesArray, 0, length);

		// don't clone the fSymbolsArray which is final after the #determinePatterns() method
		result.fPriority = fPriority;
		result.fSymbolsOrPattern = fSymbolsOrPattern;
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
		final int length = fSymbolsOrPatternValues.length;
		IExpr[] patternValuesArray = new IExpr[length];
		System.arraycopy(fSymbolsOrPatternValues, 0, patternValuesArray, 0, length);
		return patternValuesArray;
	}

	/**
	 * Copy the found pattern matches from the given <code>patternMap</code> back to this maps pattern values.
	 * 
	 * @param patternMap
	 */
	protected void copyPatternValuesFromPatternMatcher(final PatternMap patternMap) {
		IExpr[] symbolsArray = patternMap.fSymbolsOrPattern;
		for (int i = 0; i < symbolsArray.length; i++) {
			for (int j = 0; j < fSymbolsOrPattern.length; j++) {
				// compare object references with operator '==' here !
				if (fSymbolsOrPattern[j] == symbolsArray[i]) {
					fSymbolsOrPatternValues[j] = patternMap.fSymbolsOrPatternValues[i];
				}
			}
		}
	}

	/**
	 * Determine all patterns (i.e. all objects of instance IPattern) in the given expression
	 * 
	 * Increments this classes pattern counter.
	 * 
	 * @param lhsPatternExpr the (left-hand-side) expression which could contain pattern objects.
	 * @return the priority of this pattern-matcher
	 */
	public int determinePatterns(final IExpr lhsPatternExpr) {
		fPriority = DEFAULT_RULE_PRIORITY;
		if (lhsPatternExpr instanceof IAST) {
			Set<IExpr> patternIndexMap = Collections.newSetFromMap(new IdentityHashMap<IExpr, Boolean>(16));
			determinePatternsRecursive(patternIndexMap, (IAST) lhsPatternExpr, 1);
			final int size = patternIndexMap.size();
			this.fSymbolsOrPattern = new IExpr[size];
			this.fSymbolsOrPatternValues = new IExpr[size];
			int i = 0;
			for (IExpr entry : patternIndexMap) {
				fSymbolsOrPattern[i++] = entry;
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
	 * @param lhsPatternExpr  the (left-hand-side) expression which could contain pattern objects.
	 * @param treeLevel       the level of the tree where the patterns are determined
	 */
	private int determinePatternsRecursive(Set<IExpr> patternIndexMap, final IAST lhsPatternExpr, int treeLevel) {
		if (lhsPatternExpr.isAlternatives() || lhsPatternExpr.isExcept()) {
			fRuleWithoutPattern = false;
		}
		int[] listEvalFlags = new int[] { IAST.NO_FLAG };
		lhsPatternExpr.forEach(x -> {
			if (x.isAST()) {
				listEvalFlags[0] |= determinePatternsRecursive(patternIndexMap, (IAST) x, treeLevel + 1);
				fPriority -= 11;
			} else if (x instanceof IPatternObject) {
				int[] result = ((IPatternObject) x).addPattern(this, patternIndexMap);
				listEvalFlags[0] |= result[0];
				fPriority -= result[1];
			} else {
				fPriority -= (50 - treeLevel);
			}
		}, 0);
		lhsPatternExpr.setEvalFlags(listEvalFlags[0]);
		// disable flag "pattern with default value"
		// listEvalFlags &= IAST.CONTAINS_NO_DEFAULT_PATTERN_MASK;
		return listEvalFlags[0];
	}

	/** {@inheritDoc} */
	@Override
	public int get(IExpr patternOrSymbol) {
		final int length = fSymbolsOrPattern.length;
		for (int i = 0; i < length; i++) {
			// compare object references with operator '==' here !
			if (patternOrSymbol == fSymbolsOrPattern[i]) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Get the priority of this PatternMap
	 * 
	 * @return
	 */
	public final int getPriority() {
		return fPriority;
	}

	public final boolean getRHSEvaluated() {
		return evaluatedRHS;
	}

	/**
	 * Return the matched value for the given <code>index</code> if possisble.
	 * 
	 * @return <code>null</code> if no matched expression exists
	 */
	protected IExpr getValue(int index) {
		if (index < fSymbolsOrPatternValues.length) {
			return fSymbolsOrPatternValues[index];
		}
		return null;
	}

	/**
	 * Return the matched value for the given pattern object
	 * 
	 * @param pExpr
	 * @return <code>null</code> if no matched expression exists
	 */
	public IExpr getValue(@Nonnull IPatternObject pattern) {
		ISymbol sym = pattern.getSymbol();
		if (sym != null) {
			return val(sym);
		}
		IExpr temp = pattern;

		int indx = get(temp);
		return indx >= 0 ? fSymbolsOrPatternValues[indx] : null;
	}

	/**
	 * Return the matched value for the given symbol
	 * 
	 * @param symbol the symbol
	 * @return <code>null</code> if no matched expression exists
	 */
	public final IExpr val(@Nonnull ISymbol symbol) {
		int indx = get(symbol);
		return indx >= 0 ? fSymbolsOrPatternValues[indx] : null;
	}

	protected List<IExpr> getValuesAsList() {
		final int length = fSymbolsOrPatternValues.length;
		List<IExpr> args = new ArrayList<IExpr>(length);
		for (int i = 0; i < length; i++) {
			IExpr arg = fSymbolsOrPatternValues[i];
			if (arg == null) {
				return null;
			}
			args.add(arg);
		}
		return args;
	}

	/**
	 * Set all pattern values to <code>null</code>;
	 */
	protected final void initPattern() {
		evaluatedRHS = false;
		Arrays.fill(fSymbolsOrPatternValues, null);
	}

	/**
	 * Check if all symbols in the symbols array have corresponding values assigned.
	 * 
	 * @return
	 */
	protected boolean isAllPatternsAssigned() {
		if (fSymbolsOrPatternValues != null) {
			// all patterns have values assigned?
			final int length = fSymbolsOrPatternValues.length;
			for (int i = 0; i < length; i++) {
				if (fSymbolsOrPatternValues[i] == null) {
					return false;
				}
			}
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return fSymbolsOrPattern.length > 0;
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
	 * Check if the substituted expression still contains a symbol of a pattern expression.
	 * 
	 * @param substitutedExpr
	 * @return
	 */
	protected boolean isFreeOfPatternSymbols(IExpr substitutedExpr) {
		if (isAllPatternsAssigned()) {
			return true;
		}
		if (fSymbolsOrPattern != null) {
			return substitutedExpr.isFree(x -> {
				final int length = fSymbolsOrPattern.length;
				for (int i = 0; i < length; i++) {
					// compare object references with operator '==' here !
					if (fSymbolsOrPattern[i] == x) {
						return false;
					}
				}
				return true;
			}, true);

		}
		return true;
	}

	/**
	 * Reset the values to the values in the given array
	 * 
	 * @param patternValuesArray
	 * @see PatternMap#copyPattern()
	 */
	protected final void resetPattern(final IExpr[] patternValuesArray) {
		evaluatedRHS = false;
		System.arraycopy(patternValuesArray, 0, fSymbolsOrPatternValues, 0, fSymbolsOrPatternValues.length);
	}

	public boolean isPatternTest(IExpr expr, IExpr patternTest, EvalEngine engine) {
		final IExpr temp = substitutePatternOrSymbols(expr).orElse(expr);
		final IASTMutable test = (IASTMutable) F.unaryAST1(patternTest, null);
		if (temp.isSequence()) {
			return ((IAST) temp).forAll((x, i) -> {
				test.set(1, x);
				return engine.evalTrue(test);
			}, 1);
		}
		test.set(1, temp);
		if (!engine.evalTrue(test)) {
			return false;
		}
		return true;
	}

	public final void setRHSEvaluated(boolean evaluated) {
		evaluatedRHS = evaluated;
	}

	public void setValue(IPatternObject pattern, IExpr expr) {
		ISymbol sym = pattern.getSymbol();
		IExpr temp = pattern;
		if (sym != null) {
			temp = sym;
		}
		int indx = get(temp);
		if (indx >= 0) {
			fSymbolsOrPatternValues[indx] = expr;
			return;
		}
		throw new IllegalStateException("Pattern:" + pattern + " is not available");
	}

	public void setValue(IPatternSequence pattern, IAST sequence) {
		ISymbol sym = pattern.getSymbol();
		IExpr temp = pattern;
		if (sym != null) {
			temp = sym;
		}
		int indx = get(temp);
		if (indx >= 0) {
			fSymbolsOrPatternValues[indx] = sequence;
			return;
		}
		throw new IllegalStateException("Patternsequence:" + pattern + " is not available");
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return fSymbolsOrPattern.length;
	}

	/**
	 * Substitute all patterns and symbols in the given expression with the current value of the corresponding internal pattern values
	 * arrays
	 * 
	 * @param lhsPatternExpr left-hand-side expression which may contain pattern objects
	 * 
	 * @return <code>F.NIL</code> if substitutions isn't possible
	 */
	protected IExpr substitutePatternOrSymbols(final IExpr lhsPatternExpr) {
		if (fSymbolsOrPatternValues != null) {
			IExpr result = lhsPatternExpr.replaceAll(input -> {
				if (input instanceof IPatternObject) {
					IExpr symbolOrPatternObject = ((IPatternObject) input).getSymbol();
					if (symbolOrPatternObject == null) {
						symbolOrPatternObject = input;
					}
					final int length = fSymbolsOrPattern.length;
					for (int i = 0; i < length; i++) {
						// compare object references with operator '==' here !
						if (symbolOrPatternObject == fSymbolsOrPattern[i]) {
							return fSymbolsOrPatternValues[i] != null ? fSymbolsOrPatternValues[i] : F.NIL;
						}
					}
				}
				return F.NIL;
			});

			if (result.isPresent()) {
				if (result.isAST()) {
					if (result.isFlatAST()) {
						IExpr temp = EvalAttributes.flatten((IAST) result);
						if (temp.isPresent()) {
							result = temp;
						}
					}
					if (result.isOrderlessAST()) {
						EvalAttributes.sort((IASTMutable) result);
					}
				}
				return result;
			}
		}
		return lhsPatternExpr;
	}

	/**
	 * Substitute all symbols in the given expression with the current value of the corresponding internal pattern values arrays
	 * 
	 * @param rhsExpr right-hand-side expression, substitute all symbols from the pattern-matching values
	 * 
	 * @return
	 */
	protected IExpr substituteSymbols(final IExpr rhsExpr) {
		if (fSymbolsOrPatternValues != null) {
			return rhsExpr.replaceAll((IExpr input) -> {
				if (input.isSymbol()) {
					final ISymbol symbol = (ISymbol) input;
					final int length = fSymbolsOrPattern.length;
					for (int i = 0; i < length; i++) {
						// compare object references with operator '==' here !
						if (symbol == fSymbolsOrPattern[i]) {
							return fSymbolsOrPatternValues[i] != null ? fSymbolsOrPatternValues[i] : F.NIL;
						}
					}
				}
				return F.NIL;
			}

			).orElse(rhsExpr);
		}
		return rhsExpr;
	}

	@Override
	public String toString() {
		if (fSymbolsOrPattern != null) {
			StringBuilder buf = new StringBuilder();
			buf.append("Patterns[");
			int length = fSymbolsOrPattern.length;
			for (int i = 0; i < length; i++) {
				buf.append(fSymbolsOrPattern[i].toString());
				buf.append(" => ");
				if (fSymbolsOrPatternValues[i] != null) {
					buf.append(fSymbolsOrPatternValues[i].toString());
				} else {
					buf.append("?");
				}
				if (i < length - 1) {
					buf.append(", ");
				}
			}
			buf.append("]");
			return buf.toString();
		}
		return "PatternMap[]";
	}
}