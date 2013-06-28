package org.matheclipse.core.patternmatching;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.matheclipse.combinatoric.MultisetPartitionsIterator;
import org.matheclipse.combinatoric.NumberPartitionsIterator;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternMatcher;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.ISymbol;

public class PatternMatcher extends IPatternMatcher<IExpr> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6708462090303928690L;

	private static class Entry {
		IExpr fPatternExpr;
		IExpr fEvalExpr;

		public Entry(IExpr patternExpr, IExpr evalExpr) {
			this.fPatternExpr = patternExpr;
			this.fEvalExpr = evalExpr;
		}
	}

	/**
	 * Manage a stack of pairs of expressions, which have to match each other
	 * 
	 */
	protected class StackMatcher {

		private ArrayList<Entry> fStack = new ArrayList<Entry>();

		public StackMatcher() {

		}

		/**
		 * @return
		 * @see java.util.Stack#peek()
		 */
		public Entry peek() {
			return fStack.get(fStack.size() - 1);
		}

		/**
		 * @return
		 * @see java.util.Stack#pop()
		 */
		public Entry pop() {
			return fStack.remove(fStack.size() - 1);
		}

		/**
		 * Remove all elements starting at the given <code>fromPosition</code>.
		 * 
		 * @param fromPosition
		 */
		public void removeFrom(int fromPosition) {
			int len = fStack.size();
			while (len > fromPosition) {
				fStack.remove(len - 1);
				len--;
			}
		}

		public boolean push(IExpr patternExpr, IExpr evalExpr) {
			if (patternExpr.isAST()
					&& (((IAST) patternExpr).isEvalFlagOn(IAST.CONTAINS_PATTERN) || ((IAST) patternExpr)
							.isEvalFlagOn(IAST.CONTAINS_PATTERN_SEQUENCE))) {
				// insert for delayed evaluation in matchRest() method
				fStack.add(new Entry(patternExpr, evalExpr));
				return true;
			}
			if (patternExpr.isPattern()) {
				return matchPattern((IPattern) patternExpr, evalExpr);
			} else if (patternExpr.isPatternSequence()) {
				return matchPatternSequence((IPatternSequence) patternExpr, F.Sequence(evalExpr));
			} else {
				return patternExpr.equals(evalExpr);
			}
		}

		/**
		 * Match the entries of the stack recursively starting from the top
		 * entry.
		 * 
		 * @return <code>true</code> if all expressions could be matched.
		 */
		public boolean matchRest() {
			if (isEmpty()) {
				return checkCondition();
			}
			boolean matched = true;
			Entry entry = pop();
			try {
				matched = matchExpr(entry.fPatternExpr, entry.fEvalExpr, this);
				return matched;
			} finally {
				if (!matched) {
					fStack.add(entry);
				}
			}
		}

		/**
		 * Check if this stack is empty.
		 * 
		 * @return
		 * @see java.util.ArrayList#isEmpty()
		 */
		public boolean isEmpty() {
			return fStack.isEmpty();
		}

		/**
		 * The size of the stack.
		 * 
		 * @return
		 * @see java.util.ArrayList#size()
		 */
		public int size() {
			return fStack.size();
		}
	}

	/**
	 * Matches an <code>IAST</code> with header attribute <code>Orderless</code>
	 * .
	 * 
	 * @see ISymbol#ORDERLESS
	 */
	public class OrderlessMatcher {

		private IAST fLHSPatternAST;

		private IAST fLHSEvalAST;

		/**
		 * The used (i.e. matched) expression indexes in the LHS evaluation
		 * expression; <code>-1</code> indicates an unused index.
		 */
		private int[] fUsedIndex;

		/**
		 * Match a pattern expression against an evaluation expression, there
		 * the arguments are commutative (i.e. the head of the AST expression
		 * has attribute <code>Orderless</code>)
		 * 
		 * @param lhsPatternAST
		 *            the pattern AST
		 * @param lhsEvalAST
		 *            the evaluation AST
		 */
		public OrderlessMatcher(final IAST lhsPatternAST, final IAST lhsEvalAST) {
			this.fLHSPatternAST = lhsPatternAST;
			this.fLHSEvalAST = lhsEvalAST;
			this.fUsedIndex = new int[fLHSPatternAST.size() - 1];
			for (int l = 0; l < fUsedIndex.length; l++) {
				fUsedIndex[l] = -1;
			}
		}

		/**
		 * 
		 * @param lhsPosition
		 *            the position in the LHS expression which should actually
		 *            be matched.
		 * @param stackMatcher
		 *            TODO
		 * @return
		 */
		public boolean matchOrderlessAST(int lhsPosition, StackMatcher stackMatcher) {
			if (lhsPosition >= fLHSPatternAST.size()) {
				return stackMatcher.matchRest();
			}
			boolean isNotInUse;
			IExpr subPattern = fLHSPatternAST.get(lhsPosition);
			IExpr[] patternValues = fPatternMap.copyPattern();
			for (int j = 1; j < fLHSEvalAST.size(); j++) {
				isNotInUse = true;
				for (int k = 0; k < fLHSPatternAST.size() - 1; k++) {
					if (fUsedIndex[k] == j) {
						isNotInUse = false;
						break;
					}
				}
				if (isNotInUse) {
					boolean matched = false;
					int lastStackSize = stackMatcher.size();
					try {
						if (stackMatcher.push(subPattern, fLHSEvalAST.get(j))) {
							// if (matchExpr(subPattern, fLHSEvalAST.get(j))) {
							fUsedIndex[lhsPosition - 1] = j;
							if (matchOrderlessAST(lhsPosition + 1, stackMatcher)) {
								matched = true;
								return true;
							}
						}
					} finally {
						if (!matched) {
							fPatternMap.resetPattern(patternValues);
							stackMatcher.removeFrom(lastStackSize);
							fUsedIndex[lhsPosition - 1] = -1;
						}
					}
				}
			}
			return false;
		}

		public void filterResult(IAST result) {
			for (int i = 0; i < fUsedIndex.length; i++) {
				result.set(fUsedIndex[i], null);
			}
			int indx = 1;
			while (indx < result.size()) {
				if (result.get(indx) == null) {
					result.remove(indx);
				} else {
					indx++;
				}
			}
		}
	}

	/**
	 * Contains the "pattern-matching" expression
	 * 
	 */
	protected IExpr fLhsPatternExpr;

	/**
	 * Additional condition for pattern-matching maybe <code>null</code>
	 * 
	 */
	protected IExpr fPatternCondition;

	protected PatternMap fPatternMap;

	/**
	 * Needed for serialization
	 * 
	 * @param patternExpr
	 */
	public PatternMatcher() {
		this.fLhsPatternExpr = null;
		this.fPatternCondition = null;
		this.fPatternMap = new PatternMap();
	}

	public PatternMatcher(final IExpr patternExpr) {
		this.fLhsPatternExpr = patternExpr;
		this.fPatternCondition = null;
		if (patternExpr.isCondition()) {
			this.fLhsPatternExpr = ((IAST) patternExpr).get(1);
			this.fPatternCondition = ((IAST) patternExpr).get(2);
		}
		this.fPatternMap = new PatternMap();
		init(fLhsPatternExpr);
	}

	protected final void init(IExpr patternExpr) {
		fPatternMap.determinePatterns(patternExpr);
	}

	/**
	 * Check if the condition for the right-hand-sides
	 * <code>Module[] or Condition[]</code> expressions evaluates to
	 * <code>true</code>. Override it in subclasses.
	 * 
	 * @return <code>true</code>
	 * @see PatternMatcherAndEvaluator#checkRHSCondition(EvalEngine)
	 */
	public boolean checkRHSCondition(EvalEngine engine) {
		return true;
	}

	/**
	 * Check if the condition for this pattern matcher evaluates to
	 * <code>true</code>.
	 */
	public boolean checkCondition() {

		if (fPatternCondition != null) {
			final EvalEngine engine = EvalEngine.get();
			boolean traceMode = false;
			try {
				traceMode = engine.isTraceMode();
				engine.setTraceMode(false);
				final IExpr substConditon = fPatternMap.substitutePatternSymbols(fPatternCondition);
				if (engine.evalTrue(substConditon)) {
					return checkRHSCondition(engine);
				}
				return false;
			} finally {
				engine.setTraceMode(traceMode);
			}
		}
		return true;
	}

	/**
	 * Check if the two left-hand-side pattern expressions are equivalent. (i.e.
	 * <code>f[x_,y_]</code> is equivalent to <code>f[a_,b_]</code> )
	 * 
	 * @param patternExpr1
	 * @param patternExpr2
	 * @param pm1
	 * @param pm2
	 * @return
	 */
	public static boolean equivalent(final IExpr patternExpr1, final IExpr patternExpr2, final PatternMap pm1, final PatternMap pm2) {
		if (patternExpr1 == patternExpr2) {
			return true;
		}
		if ((patternExpr1.isAST()) && (patternExpr2.isAST())) {
			final IAST l1 = (IAST) patternExpr1;
			final IAST l2 = (IAST) patternExpr2;
			if (l1.size() != l2.size()) {
				return false;
			}
			if (!equivalent(l1.head(), l2.head(), pm1, pm2)) {
				return false;
			}
			for (int i = 1; i < l1.size(); i++) {

				if (!equivalent(l1.get(i), l2.get(i), pm1, pm2)) {
					return false;
				}
			}
			return true;
		}
		if (patternExpr1.isPattern() && patternExpr2.isPattern()) {
			// test if the pattern indices are equal
			final IPattern p1 = (IPattern) patternExpr1;
			final IPattern p2 = (IPattern) patternExpr2;
			if (pm1.getIndex(p1) != pm2.getIndex(p2)) {
				return false;
			}
			// test if the "check" expressions are equal
			final Object o1 = p1.getCondition();
			final Object o2 = p2.getCondition();
			if ((o1 == null) || (o2 == null)) {
				return o1 == o2;
			}
			return o1.equals(o2);
		}
		return patternExpr1.equals(patternExpr2);
	}

	/**
	 * Get the right-hand-side expression. Override in derived classes. The
	 * default implementation returns <code>null</code>.
	 * 
	 * @return <code>null</code>
	 */
	public IExpr getRightHandside() {
		return null;
	}

	/**
	 * Returns the matched pattern in the order they appear in the pExpr
	 * 
	 * @param resultList
	 * @param pExpr
	 */
	@Override
	public void getPatterns(final List<IExpr> resultList, final IExpr pExpr) {
		if (pExpr instanceof IAST) {
			final IAST list = (IAST) pExpr;
			getPatterns(resultList, list.head());
			for (int i = 1; i < list.size(); i++) {
				getPatterns(resultList, list.get(i));
			}
		} else {
			if (pExpr.isPattern()) {
				resultList.add(fPatternMap.getValue((IPattern) pExpr));
			}
		}
	}

	/**
	 * Set the symbol values for the matched patterns.
	 * 
	 * @param resultList
	 * @param pExpr
	 */
	public void setPatternValue2Local(final IExpr pExpr) {
		if (pExpr instanceof IAST) {
			final IAST list = (IAST) pExpr;
			setPatternValue2Local(list.head());
			for (int i = 0; i < list.size(); i++) {
				setPatternValue2Local(list.get(i));
			}
		} else {
			if (pExpr.isPattern()) {
				ISymbol sym = ((IPattern) pExpr).getSymbol();
				if (!sym.hasLocalVariableStack()) {
					throw new UnsupportedOperationException("Pattern symbol has to be defined with local stack");
				}
				sym.set(fPatternMap.getValue((IPattern) pExpr));
			}
		}
	}

	/**
	 * Returns true if the given expression contains no patterns
	 * 
	 * @return
	 */
	@Override
	final public boolean isRuleWithoutPatterns() {
		return fPatternMap.isRuleWithoutPatterns();
	}

	@Override
	public boolean apply(final IExpr evalExpr) {

		if (isRuleWithoutPatterns()) {
			// no patterns found match equally:
			return fLhsPatternExpr.equals(evalExpr);
		}

		fPatternMap.initPattern();
		return matchExpr(fLhsPatternExpr, evalExpr);
	}

	/**
	 * Checks if the two expressions match each other
	 * 
	 * 
	 * @return
	 */
	protected boolean matchExpr(IExpr lhsPatternExpr, final IExpr lhsEvalExpr) {
		return matchExpr(lhsPatternExpr, lhsEvalExpr, new StackMatcher());
	}

	/**
	 * Checks if the two expressions match each other
	 * 
	 * @return
	 */
	protected boolean matchExpr(IExpr lhsPatternExpr, final IExpr lhsEvalExpr, StackMatcher stackMatcher) {
		boolean matched = false;
		if (lhsPatternExpr.isCondition()) {
			// expression /; test
			lhsPatternExpr = fPatternMap.substitutePatternSymbols(lhsPatternExpr);
			if (lhsPatternExpr.isAST()) {
				lhsPatternExpr = PatternMatcher.evalLeftHandSide((IAST) lhsPatternExpr);
			}
			final PatternMatcher matcher = new PatternMatcher(lhsPatternExpr);
			if (matcher.apply(lhsEvalExpr)) {
				matched = true;
				fPatternMap.copyPatternValuesFromPatternMatcher(matcher.fPatternMap);
			}
		} else if (lhsPatternExpr instanceof IAST) {
			IAST lhsPatternAST = (IAST) lhsPatternExpr;
			IExpr[] patternValues = fPatternMap.copyPattern();
			try {
				matched = matchAST(lhsPatternAST, lhsEvalExpr, stackMatcher);
				if ((lhsPatternAST.getEvalFlags() & IAST.CONTAINS_DEFAULT_PATTERN) == IAST.CONTAINS_DEFAULT_PATTERN) {
					if (!matched) {
						IExpr temp = null;
						ISymbol symbol = lhsPatternAST.topHead();
						int attr = symbol.getAttributes();
						fPatternMap.resetPattern(patternValues);
						temp = matchDefaultAST(symbol, attr, lhsPatternAST);
						if (temp != null) {
							matched = matchExpr(temp, lhsEvalExpr, stackMatcher);
						}
					}
				}
			} finally {
				if (!matched) {
					fPatternMap.resetPattern(patternValues);
				}
			}

		} else if (lhsPatternExpr.isPattern()) {
			matched = matchPattern((IPattern) lhsPatternExpr, lhsEvalExpr);
		} else if (lhsPatternExpr.isPatternSequence()) {
			matched = matchPatternSequence((IPatternSequence) lhsPatternExpr, F.Sequence(lhsEvalExpr));
		} else {
			matched = lhsPatternExpr.equals(lhsEvalExpr);
		}
		if (matched) {
			return stackMatcher.matchRest();
		}
		return false;

	}

	/**
	 * Match the <code>ast</code> with its <code>Default[]</code> values.
	 * 
	 * @param symbol
	 * @param attr
	 * @param ast
	 * @return
	 */
	private IExpr matchDefaultAST(ISymbol symbol, int attr, IAST ast) {
		IAST cloned = F.ast(ast.head(), ast.size(), false);
		for (int i = 1; i < ast.size(); i++) {
			if (ast.get(i).isPattern() && ((IPattern) ast.get(i)).isDefault()) {
				IExpr positionDefaultValue = symbol.getDefaultValue(i);
				if (positionDefaultValue != null) {
					if (!matchPattern((IPattern) ast.get(i), positionDefaultValue)) {
						return null;
					}
					continue;
				} else {
					IExpr commonDefaultValue = symbol.getDefaultValue();
					if (commonDefaultValue != null) {
						if (!matchPattern((IPattern) ast.get(i), commonDefaultValue)) {
							return null;
						}
						continue;
					}
				}

			}
			cloned.add(ast.get(i));
		}
		if (cloned.size() == 2) {
			return cloned.get(1);
		}
		return null;
	}

	private boolean matchFlatAndFlatOrderlessAST(final ISymbol sym, final IAST lhsPatternAST, final IAST lhsEvalAST,
			StackMatcher stackMatcher) {
		if ((sym.getAttributes() & ISymbol.ORDERLESS) == ISymbol.ORDERLESS) {
			FlatOrderlessStepVisitor visitor = new FlatOrderlessStepVisitor(sym, lhsPatternAST, lhsEvalAST, stackMatcher,
					fPatternMap);
			MultisetPartitionsIterator iter = new MultisetPartitionsIterator(visitor, lhsPatternAST.size() - 1);
			return !iter.execute();
		} else {
			FlatStepVisitor visitor = new FlatStepVisitor(sym, lhsPatternAST, lhsEvalAST, stackMatcher, fPatternMap);
			NumberPartitionsIterator iter = new NumberPartitionsIterator(visitor, lhsEvalAST.size() - 1, lhsPatternAST.size() - 1);
			return !iter.execute();
			// final FlatMatcher fMatcher = new FlatMatcher(sym, lhsPatternAST,
			// lhsEvalAST);
			// return fMatcher.matchFlatAST(stackMatcher);
		}
	}

	protected boolean matchAST(final IAST lhsPatternAST, final IExpr lhsEvalExpr, StackMatcher stackMatcher) {
		if (lhsEvalExpr instanceof IAST) {
			if ((!lhsPatternAST.isEvalFlagOn(IAST.CONTAINS_PATTERN))
					&& (!lhsPatternAST.isEvalFlagOn(IAST.CONTAINS_PATTERN_SEQUENCE)) && lhsPatternAST.equals(lhsEvalExpr)) {
				return stackMatcher.matchRest();
			}

			final IAST lhsEvalAST = (IAST) lhsEvalExpr;
			final ISymbol sym = lhsPatternAST.topHead();
			if (lhsPatternAST.size() <= lhsEvalAST.size()) {
				if (((sym.getAttributes() & ISymbol.FLAT) == ISymbol.FLAT)
						&& sym.equals(lhsEvalAST.topHead())
						&& !(((sym.getAttributes() & ISymbol.ORDERLESS) == ISymbol.ORDERLESS) && lhsPatternAST.size() == lhsEvalAST
								.size())) {
					if (!matchExpr(lhsPatternAST.head(), lhsEvalAST.head())) {
						return false;
					}
					return matchFlatAndFlatOrderlessAST(sym, lhsPatternAST, lhsEvalAST, stackMatcher);
				}

				if (lhsPatternAST.size() < lhsEvalAST.size()) {
					if (lhsPatternAST.isEvalFlagOn(IAST.CONTAINS_PATTERN_SEQUENCE)) {
						if (!matchExpr(lhsPatternAST.head(), lhsEvalAST.head())) {
							return false;
						}
						int lastPosition = lhsPatternAST.size() - 1;
						if (lhsPatternAST.get(lastPosition).isPatternSequence()) {
							// TODO only the special case, where the last
							// element is
							// a pattern sequence, is handled here
							IAST seq = F.Sequence();
							seq.addAll(lhsEvalAST, lastPosition, lhsEvalAST.size());
							if (matchPatternSequence((IPatternSequence) lhsPatternAST.get(lastPosition), seq)) {
								return matchAST(lhsPatternAST.copyUntil(lastPosition), lhsEvalAST.copyUntil(lastPosition),
										stackMatcher);
							}
						}
					}

					return false;
				}
			}

			if (lhsPatternAST.size() != lhsEvalAST.size()) {
				return false;
			}

			IExpr e1 = lhsPatternAST.head();
			IExpr e2 = lhsEvalAST.head();
			if (e1.isSymbol() && e2.isSymbol()) {
				if (!e1.equals(e2)) {
					return false;
				}
			} else {
				// TODO create correct stack-matcher for the following call:
				if (!matchExpr(lhsPatternAST.head(), lhsEvalAST.head())) {
					return false;
				}
			}

			if (lhsPatternAST.isOrderlessAST()) {
				// only pure Orderless things (without Flat) will be handled
				// here:
				// final OrderlessMatcher foMatcher = new
				// OrderlessMatcher(lhsPatternAST, lhsEvalAST);
				// return foMatcher.matchOrderlessAST(1, stackMatcher);
				OrderlessStepVisitor visitor = new OrderlessStepVisitor(sym, lhsPatternAST, lhsEvalAST, stackMatcher, fPatternMap);
				MultisetPartitionsIterator iter = new MultisetPartitionsIterator(visitor, lhsPatternAST.size() - 1);
				return !iter.execute();
			}

			return matchASTSequence(lhsPatternAST, lhsEvalAST, 0, stackMatcher);
		}

		return false;
	}

	private boolean matchASTSequence(final IAST lhsPatternAST, final IAST lhsEvalAST, final int lhsEvalOffset,
			StackMatcher stackMatcher) {
		// distinguish between "equally" matched list-expressions and
		// AST expressions with "CONTAINS_PATTERN" flag
		IExpr[] patternValues = fPatternMap.copyPattern();
		int lastStackSize = stackMatcher.size();
		boolean matched = true;
		try {
			// loop from the end
			for (int i = lhsPatternAST.size() - 1; i > 0; i--) {
				if (!stackMatcher.push(lhsPatternAST.get(i), lhsEvalAST.get(lhsEvalOffset + i))) {
					matched = false;
					return false;
				}
			}
			if (!stackMatcher.matchRest()) {
				matched = false;
				return false;
			}
			return true;
		} finally {
			if (!matched) {
				stackMatcher.removeFrom(lastStackSize);
				fPatternMap.resetPattern(patternValues);
			}
		}
	}

	protected IExpr evalAST(final IAST lhsPatternAST, final IAST lhsEvalAST, final IExpr rhsExpr, StackMatcher stackMatcher) {
		if (lhsPatternAST.size() < lhsEvalAST.size()) {
			if (lhsPatternAST.isOrderlessAST()) {
				if (!matchExpr(lhsPatternAST.head(), lhsEvalAST.head(), new StackMatcher())) {
					return null;
				}
				final OrderlessMatcher foMatcher = new OrderlessMatcher(lhsPatternAST, lhsEvalAST);
				boolean matched = foMatcher.matchOrderlessAST(1, stackMatcher);
				if (matched) {
					IAST lhsResultAST = (lhsEvalAST).clone();
					foMatcher.filterResult(lhsResultAST);
					try {
						IExpr result = fPatternMap.substitutePatternSymbols(rhsExpr);
						result = F.eval(result);
						lhsResultAST.add(result);
						return lhsResultAST;
					} catch (final ConditionException e) {
						// fall through
					} catch (final ReturnException e) {
						lhsResultAST.add(e.getValue());
						return lhsResultAST;
					}
					return null;
				}

			}
			if (lhsPatternAST.isFlatAST()) {
				if (!matchExpr(lhsPatternAST.head(), lhsEvalAST.head(), new StackMatcher())) {
					return null;
				}
				int len = lhsEvalAST.size() - lhsPatternAST.size();
				for (int i = 0; i < len; i++) {
					if (matchASTSequence(lhsPatternAST, lhsEvalAST, i, stackMatcher)) {
						IAST lhsResultAST = (lhsEvalAST).clone();
						for (int j = 1; j < lhsPatternAST.size(); j++) {
							lhsResultAST.remove(i + 1);
						}
						try {
							IExpr result = fPatternMap.substitutePatternSymbols(rhsExpr);
							result = F.eval(result);
							lhsResultAST.add(i + 1, result);
							return lhsResultAST;
						} catch (final ConditionException e) {
							// fall through
						} catch (final ReturnException e) {
							lhsResultAST.add(i + 1, e.getValue());
							return lhsResultAST;
						}
						return null;
					}
				}
			}
		}
		return null;
	}

	private boolean matchPattern(final IPattern pattern, final IExpr expr) {
		if (!pattern.isConditionMatched(expr)) {
			return false;
		}

		ISymbol sym = pattern.getSymbol();

		if (sym != null) {
			final IExpr value = fPatternMap.getValue(pattern);
			if (value != null) {
				return expr.equals(value);
			}

			fPatternMap.setValue(pattern, expr);
		}
		return true;
	}

	private boolean matchPatternSequence(final IPatternSequence pattern, final IAST sequence) {
		if (!pattern.isConditionMatchedSequence(sequence)) {
			return false;
		}

		ISymbol sym = pattern.getSymbol();

		if (sym != null) {
			final IExpr value = fPatternMap.getValue(pattern);
			if (value != null) {
				return sequence.equals(value);
			}

			fPatternMap.setValue(pattern, sequence);
		}
		return true;
	}

	public IExpr getLHS() {
		return fLhsPatternExpr;
	}

	@Override
	public IExpr eval(final IExpr leftHandSide) {
		return null;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof PatternMatcher) {
			final PatternMatcher pm = (PatternMatcher) obj;
			if (fPatternMap.size() != pm.fPatternMap.size()) {
				return false;
			}
			if (isRuleWithoutPatterns()) {
				return fLhsPatternExpr.equals(pm.fLhsPatternExpr);
			}
			if (equivalent(fLhsPatternExpr, pm.fLhsPatternExpr, fPatternMap, pm.fPatternMap)) {
				if ((fPatternCondition != null) && (pm.fPatternCondition != null)) {
					return fPatternCondition.equals(pm.fPatternCondition);
				}
				if ((fPatternCondition != null) || (pm.fPatternCondition != null)) {
					return false;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return fLhsPatternExpr.hashCode();
	}

	@Override
	public Object clone() {
		PatternMatcher v = (PatternMatcher) super.clone();
		v.fLhsPatternExpr = fLhsPatternExpr;
		v.fPatternCondition = fPatternCondition;
		v.fPatternMap = fPatternMap.clone();
		return v;
	}

	/**
	 * Get the additional condition for pattern-matching
	 * 
	 */
	public IExpr getCondition() {
		return fPatternCondition;
	}

	/**
	 * Sets an additional evaluation-condition for pattern-matching
	 * 
	 */
	public void setCondition(final IExpr condition) {
		fPatternCondition = condition;
	}

	public static IExpr evalLeftHandSide(final IAST leftHandSide, final EvalEngine engine) {
		final IExpr temp = engine.evalSetAttributes((IAST) leftHandSide);
		if (temp != null) {
			return temp;
		}
		return leftHandSide;
	}

	public static IExpr evalLeftHandSide(IAST leftHandSide) {
		return evalLeftHandSide(leftHandSide, EvalEngine.get());
	}

}