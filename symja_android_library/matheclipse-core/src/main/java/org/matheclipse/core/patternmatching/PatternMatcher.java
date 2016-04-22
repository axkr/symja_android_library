package org.matheclipse.core.patternmatching;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
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
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class PatternMatcher extends IPatternMatcher implements Externalizable {

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
			if (patternExpr.isPatternExpr()) {
				if (patternExpr.isAST()) {
					// insert for delayed evaluation in matchRest() method
					fStack.add(new Entry(patternExpr, evalExpr));
					return true;
				}
				if (patternExpr instanceof IPatternObject) {
					return ((IPatternObject) patternExpr).matchPattern(evalExpr, fPatternMap);
				}
				throw new UnsupportedOperationException("Object doesn't support pattern-matching");
			}
			return patternExpr.equals(evalExpr);
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
	 * priority of this matcher
	 * 
	 */
	protected transient int fPriority;

	/**
	 * Additional condition for pattern-matching maybe <code>null</code>
	 * 
	 */
	protected IExpr fPatternCondition;

	/**
	 * A map from a pattern to a possibly found value during pattern-matching.
	 * Will be set to <code>null</code> if the left-hand-side pattern expression
	 * contains no pattern.
	 */
	protected transient PatternMap fPatternMap;

	/**
	 * Needed for serialization
	 * 
	 * @param patternExpr
	 */
	public PatternMatcher() {
		super(null);
		this.fPriority = PatternMap.DEFAULT_RULE_PRIORITY;
		this.fLhsPatternExpr = null;
		this.fPatternCondition = null;
		this.fPatternMap = new PatternMap();
	}

	public PatternMatcher(final IExpr patternExpr) {
		super(patternExpr);
		this.fPriority = PatternMap.DEFAULT_RULE_PRIORITY;
		this.fPatternCondition = null;
		if (patternExpr.isCondition()) {
			this.fLhsPatternExpr = ((IAST) patternExpr).arg1();
			this.fPatternCondition = ((IAST) patternExpr).arg2();
		}
		this.fPatternMap = new PatternMap();
		init(fLhsPatternExpr);
	}

	protected final void init(IExpr patternExpr) {
		fPriority = fPatternMap.determinePatterns(patternExpr);
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
				final IExpr substConditon = fPatternMap.substituteSymbols(fPatternCondition);
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
	public static boolean equivalent(final IExpr patternExpr1, final IExpr patternExpr2, final PatternMap pm1,
			PatternMap pm2) {
		if (patternExpr1 == patternExpr2) {
			return true;
		}
		if (!patternExpr1.isPatternExpr()) {
			if (!patternExpr2.isPatternExpr()) {
				return patternExpr1.equals(patternExpr2);
			}
			return false;
		}
		if (patternExpr1.isAST()) {
			if (patternExpr2.isAST()) {
				final IAST l1 = (IAST) patternExpr1;
				final IAST l2 = (IAST) patternExpr2;
				if (l1.size() != l2.size()) {
					return false;
				}
				IExpr temp1, temp2;
				for (int i = 0; i < l1.size(); i++) {
					temp1 = l1.get(i);
					temp2 = l2.get(i);
					if (temp1 == temp2) {
						continue;
					}
					if (temp1.hashCode() != temp2.hashCode()) {
						if (temp1.isPatternExpr() && temp2.isPatternExpr()) {
							if (!equivalent(temp1, temp2, pm1, pm2)) {
								return false;
							} else {
								continue;
							}
						}
						return false;
					}
					if (!temp1.isPatternExpr() || !temp2.isPatternExpr()) {
						if (!temp1.equals(temp2)) {
							return false;
						}
					}
					if (!equivalent(temp1, temp2, pm1, pm2)) {
						return false;
					}
				}
				return true;
			}
			return false;
		}
		if (patternExpr1 instanceof IPatternObject) {
			if (patternExpr2 instanceof IPatternObject) {
				return ((IPatternObject) patternExpr1).equivalent((IPatternObject) patternExpr2, pm1, pm2);
			}
			return false;
		}
		return patternExpr1.equals(patternExpr2);
	}

	/**
	 * Returns the matched pattern in the order they appear in the pExpr
	 * 
	 * @param resultList
	 * @param pExpr
	 */
	@Override
	public void getPatterns(final List<IExpr> resultList, final IExpr pExpr) {
		if (pExpr.isAST()) {
			final IAST list = (IAST) pExpr;
			getPatterns(resultList, list.head());
			for (int i = 1; i < list.size(); i++) {
				getPatterns(resultList, list.get(i));
			}
		} else if (pExpr.isPattern()) {
			resultList.add(fPatternMap.getValue((IPattern) pExpr));
		}
	}

	/**
	 * Return the matched value for index 0 if possisble.
	 * 
	 * @return <code>null</code> if no matched expression exists
	 */
	protected IExpr getPatternValue0() {
		return fPatternMap.getValue(0);
	}

	/**
	 * Get the priority of this pattern-matcher. Lower values have higher
	 * priorities.
	 * 
	 * @return the priority
	 */
	public int getPriority() {
		return fPriority;
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
	public boolean test(final IExpr leftHandSide) {

		if (isRuleWithoutPatterns()) {
			// no patterns found match equally:
			return fLhsPatternExpr.equals(leftHandSide);
		}

		fPatternMap.initPattern();
		return matchExpr(fLhsPatternExpr, leftHandSide);
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
		if (lhsPatternExpr.isAST()) {
			if (lhsPatternExpr.isCondition()) {
				// expression /; test
				lhsPatternExpr = fPatternMap.substituteSymbols(lhsPatternExpr);
				if (lhsPatternExpr.isAST()) {
					lhsPatternExpr = PatternMatcher.evalLeftHandSide((IAST) lhsPatternExpr);
				}
				final PatternMatcher matcher = new PatternMatcher(lhsPatternExpr);
				if (matcher.test(lhsEvalExpr)) {
					matched = true;
					fPatternMap.copyPatternValuesFromPatternMatcher(matcher.fPatternMap);
				}
			} else {
				IAST lhsPatternAST = (IAST) lhsPatternExpr;
				IExpr[] patternValues = fPatternMap.copyPattern();
				try {
					matched = matchAST(lhsPatternAST, lhsEvalExpr, stackMatcher);
					if ((lhsPatternAST.getEvalFlags()
							& IAST.CONTAINS_DEFAULT_PATTERN) == IAST.CONTAINS_DEFAULT_PATTERN) {
						if (!matched) {
							IExpr temp = null;
							fPatternMap.resetPattern(patternValues);
							temp = matchDefaultAST(lhsPatternAST.topHead(), lhsPatternAST);
							if (temp.isPresent()) {
								matched = matchExpr(temp, lhsEvalExpr, stackMatcher);
							}
						}
					}
				} finally {
					if (!matched) {
						fPatternMap.resetPattern(patternValues);
					}
				}
			}
		} else if (lhsPatternExpr instanceof IPatternObject) {
			matched = ((IPatternObject) lhsPatternExpr).matchPattern(lhsEvalExpr, fPatternMap);
		} else {
			matched = lhsPatternExpr.equals(lhsEvalExpr);
		}
		if (matched) {
			return stackMatcher.matchRest();
		}
		return false;

	}

	/**
	 * Match the <code>lhsPatternAST</code> with its <code>Default[]</code>
	 * values.
	 * 
	 * @param symbolWithDefaultValue
	 *            the symbol for getting the associated default values from
	 * @param lhsPatternAST
	 *            left-hand-side which may contain patterns with default values
	 * @return <code>F.NIL</code> if the given <code>lhsPatternAST</code> could
	 *         not be matched or contains no pattern with default value.
	 */
	private IExpr matchDefaultAST(ISymbol symbolWithDefaultValue, IAST lhsPatternAST) {
		IAST cloned = F.ast(lhsPatternAST.head(), lhsPatternAST.size(), false);
		boolean defaultValueMatched = false;
		for (int i = 1; i < lhsPatternAST.size(); i++) {
			if (lhsPatternAST.get(i).isPatternDefault()) {
				IExpr positionDefaultValue = symbolWithDefaultValue.getDefaultValue(i);
				if (positionDefaultValue != null) {
					if (!((IPatternObject) lhsPatternAST.get(i)).matchPattern(positionDefaultValue, fPatternMap)) {
						return F.NIL;
					}
					defaultValueMatched = true;
					continue;
				} else {
					IExpr commonDefaultValue = symbolWithDefaultValue.getDefaultValue();
					if (commonDefaultValue != null) {
						if (!((IPatternObject) lhsPatternAST.get(i)).matchPattern(commonDefaultValue, fPatternMap)) {
							return F.NIL;
						}
						defaultValueMatched = true;
						continue;
					}
				}

			}
			cloned.add(lhsPatternAST.get(i));
		}
		if (defaultValueMatched) {
			// if (cloned.size() == 1) {
			// return null;
			// }
			if (cloned.size() == 2) {
				return cloned.arg1();
			}
			return cloned;
		}
		return F.NIL;
	}

	private boolean matchFlatAndFlatOrderlessAST(final ISymbol sym, final IAST lhsPatternAST, final IAST lhsEvalAST,
			StackMatcher stackMatcher) {
		if ((sym.getAttributes() & ISymbol.ORDERLESS) == ISymbol.ORDERLESS) {
			// System.out.println(lhsPatternAST.toString() + " << >> " +
			// lhsEvalAST.toString());

			if (lhsPatternAST.size() == 2) {
				// TODO check for OneIdentity?
				return matchExpr(lhsPatternAST.arg1(), lhsEvalAST, stackMatcher);
			}
			for (int i = 1; i < lhsPatternAST.size(); i++) {
				if (!(lhsPatternAST.get(i) instanceof IPatternObject)) {
					// try to find a matchin sub-expression
					for (int j = 1; j < lhsEvalAST.size(); j++) {
						if (matchExpr(lhsPatternAST.get(i), lhsEvalAST.get(j))) {
							if (matchFlatAndFlatOrderlessAST(sym, lhsPatternAST.removeAtClone(i),
									lhsEvalAST.removeAtClone(j), stackMatcher)) {
								return true;
							}
						}
					}
					return false;
				}
			}

			FlatOrderlessStepVisitor visitor = new FlatOrderlessStepVisitor(sym, lhsPatternAST, lhsEvalAST,
					stackMatcher, fPatternMap);
			MultisetPartitionsIterator iter = new MultisetPartitionsIterator(visitor, lhsPatternAST.size() - 1);
			return !iter.execute();
		} else {
			if (lhsPatternAST.size() == 2) {
				if (lhsPatternAST.arg1().isPatternSequence()) {
					// TODO only the special case, where the last element is
					// a pattern sequence, is handled here
					IAST seq = F.Sequence();
					seq.addAll(lhsEvalAST, 1, lhsEvalAST.size());
					if (((IPatternSequence) lhsPatternAST.arg1()).matchPatternSequence(seq, fPatternMap)) {
//						if (matchAST(lhsPatternAST.copyUntil(1), lhsEvalAST.copyUntil(1),
//								stackMatcher)) {
//							return fPatternMap.isPatternTest(lhsPatternAST.arg1(), lhsPatternAST.arg2());
//						}
						return true;
					}
				}
				return false;
			}
			FlatStepVisitor visitor = new FlatStepVisitor(sym, lhsPatternAST, lhsEvalAST, stackMatcher, fPatternMap);
			NumberPartitionsIterator iter = new NumberPartitionsIterator(visitor, lhsEvalAST.size() - 1,
					lhsPatternAST.size() - 1);
			return !iter.execute();
		}
	}

	protected boolean matchAST(final IAST lhsPatternAST, final IExpr lhsEvalExpr, StackMatcher stackMatcher) {
		// System.out.println(lhsPatternAST.toString()+" -
		// "+lhsEvalExpr.toString());
		if (lhsPatternAST.isAST(F.PatternTest, 3)) {
			if (matchExpr(lhsPatternAST.arg1(), lhsEvalExpr, stackMatcher)) {
				return fPatternMap.isPatternTest(lhsPatternAST.arg1(), lhsPatternAST.arg2());
			}
			return false;
		}
		if (lhsPatternAST.isAST(F.Except, 2, 3)) {
			if (lhsPatternAST.size() == 3) {
				return !matchExpr(lhsPatternAST.arg1(), lhsEvalExpr, stackMatcher)
						&& matchExpr(lhsPatternAST.arg2(), lhsEvalExpr, stackMatcher);
			} else {
				return !matchExpr(lhsPatternAST.arg1(), lhsEvalExpr, stackMatcher);
			}
		}
		if (lhsEvalExpr instanceof IAST) {
			if (!lhsPatternAST.isPatternExpr() && lhsPatternAST.equals(lhsEvalExpr)) {
				return stackMatcher.matchRest();
			}

			final IAST lhsEvalAST = (IAST) lhsEvalExpr;
			final ISymbol sym = lhsPatternAST.topHead();
			if (lhsPatternAST.size() <= lhsEvalAST.size()) {
				if ((lhsPatternAST.isFlatAST()) && sym.equals(lhsEvalAST.topHead())
						&& !(lhsPatternAST.isOrderlessAST() && lhsPatternAST.size() == lhsEvalAST.size())) {
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
						if (lhsPatternAST.get(lastPosition).isAST(F.PatternTest, 3)) {
							IAST patternTest = (IAST) lhsPatternAST.get(lastPosition);
							if (patternTest.arg1().isPatternSequence()) {
								// TODO only the special case, where the last
								// element is
								// a pattern sequence, is handled here
								IAST seq = F.Sequence();
								seq.addAll(lhsEvalAST, lastPosition, lhsEvalAST.size());
								if (((IPatternSequence) patternTest.arg1()).matchPatternSequence(seq, fPatternMap)) {
									if (matchAST(lhsPatternAST.copyUntil(lastPosition),
											lhsEvalAST.copyUntil(lastPosition), stackMatcher)) {
										return fPatternMap.isPatternTest(patternTest.arg1(), patternTest.arg2());
									}
									return false;
								}
							}

						}
						if (lhsPatternAST.get(lastPosition).isPatternSequence()) {
							// TODO only the special case, where the last
							// element is
							// a pattern sequence, is handled here
							IAST seq = F.Sequence();
							seq.addAll(lhsEvalAST, lastPosition, lhsEvalAST.size());
							if (((IPatternSequence) lhsPatternAST.get(lastPosition)).matchPatternSequence(seq,
									fPatternMap)) {
								return matchAST(lhsPatternAST.copyUntil(lastPosition),
										lhsEvalAST.copyUntil(lastPosition), stackMatcher);
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
				int attr = sym.getAttributes();
				// only "pure Orderless" and "FlatOrderless with same size()"
				// will be handled here:
				OrderlessStepVisitor visitor = new OrderlessStepVisitor(sym, lhsPatternAST, lhsEvalAST, stackMatcher,
						fPatternMap,
						((attr & ISymbol.ONEIDENTITY) == ISymbol.ONEIDENTITY)
								// if FLAT isn't and the Orderless ASTs have
								// same size ==> use OneIdentity in pattern
								// matching
								|| (lhsPatternAST.size() == lhsEvalAST.size()
										&& (attr & ISymbol.FLAT) == ISymbol.NOATTRIBUTE));
				MultisetPartitionsIterator iter = new MultisetPartitionsIterator(visitor, lhsPatternAST.size() - 1);
				return !iter.execute();
			}

			return matchASTSequence(lhsPatternAST, lhsEvalAST, 0, stackMatcher);
		}
		if (lhsPatternAST.isAST(F.Rational, 3) && lhsEvalExpr.isRational()) {
			IRational numer = ((IRational) lhsEvalExpr).getNumerator();
			IRational denom = ((IRational) lhsEvalExpr).getDenominator();
			if (matchExpr(lhsPatternAST.arg1(), numer) && matchExpr(lhsPatternAST.arg2(), denom)) {
				return true;
			}
		} else if (lhsPatternAST.isAST(F.Complex, 3) && lhsEvalExpr.isNumber()) {
			ISignedNumber re = ((INumber) lhsEvalExpr).getRe();
			ISignedNumber im = ((INumber) lhsEvalExpr).getIm();
			if (matchExpr(lhsPatternAST.arg1(), re) && matchExpr(lhsPatternAST.arg2(), im)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Match all sub-expresions which contain no pattern objects if possible
	 * (i.e. no FLAT or Orderless expressions,...)
	 * 
	 * Distinguishes between "equally" matched list-expressions and list
	 * expressions with <code>expr.isPatternExpr()==true</code>.
	 * 
	 * @param lhsPatternAST
	 * @param lhsEvalAST
	 * @param lhsEvalOffset
	 * @param stackMatcher
	 * @return
	 */
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

	/**
	 * 
	 * @param lhsPatternAST
	 * @param lhsEvalAST
	 * @param rhsExpr
	 * @param stackMatcher
	 * @return <code>F.NIL</code> if no match was found.
	 */
	protected IExpr evalAST(final IAST lhsPatternAST, final IAST lhsEvalAST, final IExpr rhsExpr,
			StackMatcher stackMatcher) {
		if (lhsPatternAST.size() < lhsEvalAST.size()) {
			if (lhsPatternAST.isOrderlessAST() && lhsPatternAST.isFlatAST()) {
				if (!matchExpr(lhsPatternAST.head(), lhsEvalAST.head(), new StackMatcher())) {
					return F.NIL;
				}
				final OrderlessMatcher foMatcher = new OrderlessMatcher(lhsPatternAST, lhsEvalAST);
				boolean matched = foMatcher.matchOrderlessAST(1, stackMatcher);
				if (matched) {
					IAST lhsResultAST = (lhsEvalAST).clone();
					foMatcher.filterResult(lhsResultAST);
					IExpr result = fPatternMap.substituteSymbols(rhsExpr);
					try {
						result = F.eval(result);
						lhsResultAST.add(result);
						return lhsResultAST;
					} catch (final ConditionException e) {
						logConditionFalse(lhsEvalAST, lhsPatternAST, rhsExpr);
						// fall through
					} catch (final ReturnException e) {
						lhsResultAST.add(e.getValue());
						return lhsResultAST;
					}
				}
				return F.NIL;
			}
			if (lhsPatternAST.isFlatAST()) {
				if (!matchExpr(lhsPatternAST.head(), lhsEvalAST.head(), new StackMatcher())) {
					return F.NIL;
				}
				int len = lhsEvalAST.size() - lhsPatternAST.size();
				for (int i = 0; i < len; i++) {
					if (matchASTSequence(lhsPatternAST, lhsEvalAST, i, stackMatcher)) {
						IAST lhsResultAST = (lhsEvalAST).clone();
						for (int j = 1; j < lhsPatternAST.size(); j++) {
							lhsResultAST.remove(i + 1);
						}
						try {
							IExpr result = fPatternMap.substituteSymbols(rhsExpr);
							result = F.eval(result);
							lhsResultAST.add(i + 1, result);
							return lhsResultAST;
						} catch (final ConditionException e) {
							logConditionFalse(lhsEvalAST, lhsPatternAST, rhsExpr);
						} catch (final ReturnException e) {
							lhsResultAST.add(i + 1, e.getValue());
							return lhsResultAST;
						}
						return F.NIL;
					}
				}
			}
		}
		return F.NIL;
	}

	protected void logConditionFalse(final IExpr lhsEvalAST, final IExpr lhsPatternAST, IExpr rhsAST) {
		// System.out.println("\nCONDITION false: " + lhsEvalAST.toString() +
		// "\n >>> " + lhsPatternAST.toString() + " := "
		// + rhsAST.toString());
	}

	/** {@inheritDoc} */
	@Override
	public IExpr eval(final IExpr leftHandSide) {
		return F.NIL;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof IPatternMatcher) {
			return equivalent((IPatternMatcher) obj) == 0;
		}
		return false;
		// if (this == obj) {
		// return true;
		// }
		// if (obj instanceof PatternMatcher) {
		// final PatternMatcher pm = (PatternMatcher) obj;
		// if (fPatternMap.size() != pm.fPatternMap.size()) {
		// return false;
		// }
		// if (isRuleWithoutPatterns()) {
		// return fLhsPatternExpr.equals(pm.fLhsPatternExpr);
		// }
		// if (equivalent(fLhsPatternExpr, pm.fLhsPatternExpr, fPatternMap,
		// pm.fPatternMap)) {
		// if ((fPatternCondition != null) && (pm.fPatternCondition != null)) {
		// return fPatternCondition.equals(pm.fPatternCondition);
		// }
		// if ((fPatternCondition != null) || (pm.fPatternCondition != null)) {
		// return false;
		// }
		// return true;
		// }
		// }
		// return false;
	}

	@Override
	public int hashCode() {
		return fLhsPatternExpr.hashCode();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		PatternMatcher v = (PatternMatcher) super.clone();
		v.fPatternCondition = fPatternCondition;
		v.fPatternMap = fPatternMap.clone();
		v.fPriority = fPriority;
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

	/**
	 * Transform the ast recursively, according to the attributes Flat, HoldAll,
	 * HoldFirst, HoldRest, Orderless for the left-hand-side of a Set[] or
	 * SetDelayed[] expression. Delegates to
	 * <code>EvalEngine#evalSetAttributes()</code>method
	 * 
	 * @param ast
	 * @return <code>F.NIL</code> if evaluation is not possible
	 * @see org.matheclipse.core.eval.EvalEngine#evalSetAttributes()
	 */
	public static IExpr evalLeftHandSide(final IAST leftHandSide, final EvalEngine engine) {
		return engine.evalSetAttributes((IAST) leftHandSide);
	}

	public static IExpr evalLeftHandSide(IAST leftHandSide) {
		return evalLeftHandSide(leftHandSide, EvalEngine.get());
	}

	@Override
	public int compareTo(IPatternMatcher o) {
		if (fPriority < o.getPriority()) {
			return -1;
		}
		if (fPriority > o.getPriority()) {
			return 1;
		}
		return equivalent(o);
	}

	public int equivalent(final IPatternMatcher obj) {
		if (this == obj) {
			return 0;
		}
		if (obj instanceof PatternMatcher) {
			final PatternMatcher pm = (PatternMatcher) obj;
			if (fPatternMap.size() != pm.fPatternMap.size()) {
				return (fPatternMap.size() < pm.fPatternMap.size()) ? -1 : 1;
			}
			if (isRuleWithoutPatterns()) {
				return fLhsPatternExpr.compareTo(pm.fLhsPatternExpr);
			}
			if (equivalent(fLhsPatternExpr, pm.fLhsPatternExpr, fPatternMap, pm.fPatternMap)) {
				if (fPatternCondition != null) {
					if (pm.fPatternCondition != null) {
						return fPatternCondition.compareTo(pm.fPatternCondition);
					}
					return 1;
				}
				return (pm.fPatternCondition != null) ? -1 : 0;
			}
		}
		return fLhsPatternExpr.compareTo(obj.fLhsPatternExpr);
	}

	// @SuppressWarnings("unchecked")
	// private void readObject(ObjectInputStream stream) throws IOException,
	// ClassNotFoundException {
	// ObjectInputStream.GetField fields = stream.readFields();
	// this.fPatternMap = new PatternMap();
	// if (fLhsPatternExpr != null) {
	// init(fLhsPatternExpr);
	// }
	// }

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeObject(fLhsPatternExpr);
		objectOutput.writeObject(fPatternCondition);
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		fLhsPatternExpr = (IExpr) objectInput.readObject();
		fPatternCondition = (IExpr) objectInput.readObject();
		this.fPatternMap = new PatternMap();
		if (fLhsPatternExpr != null) {
			fPriority = fPatternMap.determinePatterns(fLhsPatternExpr);
		}
	}
}