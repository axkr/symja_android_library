package org.matheclipse.core.patternmatching;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayDeque;
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

	private static class Entry {
		IExpr fPatternExpr;
		IExpr fEvalExpr;

		public Entry(IExpr patternExpr, IExpr evalExpr) {
			this.fPatternExpr = patternExpr;
			this.fEvalExpr = evalExpr;
		}
	}

	/**
	 * Matches an <code>IAST</code> with header attribute <code>Orderless</code> .
	 * 
	 * @see ISymbol#ORDERLESS
	 */
	private class OrderlessMatcher {

		private IAST fLHSPatternAST;

		private IAST fLHSEvalAST;

		/**
		 * The used (i.e. matched) expression indexes in the LHS evaluation expression;
		 * <code>-1</code> indicates an unused index.
		 */
		private int[] fUsedIndex;

		/**
		 * Match a pattern expression against an evaluation expression, there the
		 * arguments are commutative (i.e. the head of the AST expression has attribute
		 * <code>Orderless</code>)
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

		/**
		 * 
		 * @param lhsPosition
		 *            the position in the LHS expression which should actually be
		 *            matched.
		 * @param stackMatcher
		 *            TODO
		 * @return
		 */
		public boolean matchOrderlessAST(int lhsPosition, StackMatcher stackMatcher) {
			if (lhsPosition >= fLHSPatternAST.size()) {
				return stackMatcher == null || stackMatcher.matchRest();
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
					if (stackMatcher == null) {
						stackMatcher = new StackMatcher();
					}
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
	}

	/**
	 * Manage a stack of pairs of expressions, which have to match each other
	 * 
	 */
	@SuppressWarnings("serial")
	public class StackMatcher extends ArrayDeque<Entry> {

		/**
		 * Match the entries of the stack recursively starting from the top entry.
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
					push(entry);
				}
			}
		}

		public boolean push(IExpr patternExpr, IExpr evalExpr) {
			if (patternExpr.isPatternExpr()) {
				if (patternExpr.isAST()) {
					// insert for delayed evaluation in matchRest() method
					push(new Entry(patternExpr, evalExpr));
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
		 * Remove all elements starting at the given <code>fromPosition</code>.
		 * 
		 * @param fromPosition
		 */
		public void removeFrom(int fromPosition) {
			int len = size();
			while (len > fromPosition) {
				pop();
				len--;
			}
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6708462090303928690L;

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

	public static IExpr evalLeftHandSide(IAST leftHandSide) {
		return evalLeftHandSide(leftHandSide, EvalEngine.get());
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

	/**
	 * priority of the left-hand-side of this matcher
	 * 
	 */
	protected transient int fLHSPriority;

	/**
	 * Additional condition for pattern-matching maybe <code>null</code>
	 * 
	 */
	protected IExpr fPatternCondition;

	/**
	 * A map from a pattern to a possibly found value during pattern-matching. Will
	 * be set to <code>null</code> if the left-hand-side pattern expression contains
	 * no pattern.
	 */
	protected transient PatternMap fPatternMap;

	public PatternMap getPatternMap() {
		return fPatternMap;
	}

	/**
	 * Needed for serialization
	 * 
	 * @param patternExpr
	 */
	public PatternMatcher() {
		super(null);
		this.fLHSPriority = PatternMap.DEFAULT_RULE_PRIORITY;
		this.fLhsPatternExpr = null;
		this.fPatternCondition = null;
		this.fPatternMap = new PatternMap();
	}

	public PatternMatcher(final IExpr patternExpr) {
		super(patternExpr);
		this.fLHSPriority = PatternMap.DEFAULT_RULE_PRIORITY;
		this.fPatternCondition = null;
		if (patternExpr.isCondition()) {
			this.fLhsPatternExpr = ((IAST) patternExpr).arg1();
			this.fPatternCondition = ((IAST) patternExpr).arg2();
		}
		this.fPatternMap = new PatternMap();
		init(fLhsPatternExpr);
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

	@Override
	public Object clone() throws CloneNotSupportedException {
		PatternMatcher v = (PatternMatcher) super.clone();
		v.fPatternCondition = fPatternCondition;
		v.fPatternMap = fPatternMap.clone();
		v.fLHSPriority = fLHSPriority;
		return v;
	}

	// @Override
	// public int compareTo(IPatternMatcher pm) {
	// if (fPriority < pm.getPriority()) {
	// return -1;
	// }
	// if (fPriority > pm.getPriority()) {
	// return 1;
	// }
	// return equivalent(pm);
	// }

	@Override
	public int equivalentTo(IPatternMatcher patternMatcher) {
		// if (fLHSPriority+getRHSPriority() <
		// patternMatcher.getLHSPriority()+patternMatcher.getRHSPriority()) {
		// return -1;
		// }
		// if (fLHSPriority+getRHSPriority() >
		// patternMatcher.getLHSPriority()+patternMatcher.getRHSPriority()) {
		// return 1;
		// }

		if (fLHSPriority < patternMatcher.getLHSPriority()) {
			return -1;
		}
		if (fLHSPriority > patternMatcher.getLHSPriority()) {
			return 1;
		}

//		if (getRHSPriority() < patternMatcher.getRHSPriority()) {
//			return -1;
//		}
//		if (getRHSPriority() > patternMatcher.getRHSPriority()) {
//			return 1;
//		}

		return equivalent(patternMatcher);

	}

	// package private
	int equivalent(final IPatternMatcher obj) {
		if (this == obj) {
			return 0;
		}
		if (obj instanceof PatternMatcher) {
			return equivalentLHS(obj);
		}
		return fLhsPatternExpr.compareTo(obj.fLhsPatternExpr);
	}

	public int equivalentLHS(final IPatternMatcher obj) {
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
		return fLhsPatternExpr.compareTo(obj.fLhsPatternExpr);
	}

	/** {@inheritDoc} */
	@Override
	public IExpr eval(final IExpr leftHandSide) {
		return F.NIL;
	}

	/**
	 * 
	 * @param lhsPatternAST
	 * @param lhsEvalAST
	 * @param rhsExpr
	 * @param stackMatcher
	 * @return <code>F.NIL</code> if no match was found.
	 */
	protected IExpr evalAST(final IAST lhsPatternAST, final IAST lhsEvalAST, final IExpr rhsExpr) {
		if (lhsPatternAST.size() < lhsEvalAST.size()) {
			if (lhsPatternAST.isOrderlessAST() && lhsPatternAST.isFlatAST()) {
				if (!matchExpr(lhsPatternAST.head(), lhsEvalAST.head(), null)) {
					return F.NIL;
				}
				final OrderlessMatcher foMatcher = new OrderlessMatcher(lhsPatternAST, lhsEvalAST);
				boolean matched = foMatcher.matchOrderlessAST(1, null);
				if (matched) {
					IAST lhsResultAST = (lhsEvalAST).clone();
					foMatcher.filterResult(lhsResultAST);
					IExpr result = fPatternMap.substituteSymbols(rhsExpr);
					try {
						result = F.eval(result);
						lhsResultAST.append(result);
						return lhsResultAST;
					} catch (final ConditionException e) {
						logConditionFalse(lhsEvalAST, lhsPatternAST, rhsExpr);
						// fall through
					} catch (final ReturnException e) {
						lhsResultAST.append(e.getValue());
						return lhsResultAST;
					}
				}
				return F.NIL;
			}
			if (lhsPatternAST.isFlatAST()) {
				if (!matchExpr(lhsPatternAST.head(), lhsEvalAST.head(), null)) {
					return F.NIL;
				}
				int len = lhsEvalAST.size() - lhsPatternAST.size();
				for (int i = 0; i < len; i++) {
					if (matchASTSequence(lhsPatternAST, lhsEvalAST, i, null)) {
						IAST lhsResultAST = (lhsEvalAST).clone();
						for (int j = 1; j < lhsPatternAST.size(); j++) {
							lhsResultAST.remove(i + 1);
						}
						try {
							IExpr result = fPatternMap.substituteSymbols(rhsExpr);
							result = F.eval(result);
							lhsResultAST.append(i + 1, result);
							return lhsResultAST;
						} catch (final ConditionException e) {
							logConditionFalse(lhsEvalAST, lhsPatternAST, rhsExpr);
						} catch (final ReturnException e) {
							lhsResultAST.append(i + 1, e.getValue());
							return lhsResultAST;
						}
						return F.NIL;
					}
				}
			}
		}
		return F.NIL;
	}

	/**
	 * Get the additional condition for pattern-matching
	 * 
	 */
	public IExpr getCondition() {
		return fPatternCondition;
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
	public int getLHSPriority() {
		return fLHSPriority;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((fPatternCondition == null) ? 0 : fPatternCondition.hashCode());
		// result = prime * result + ((fPatternMap == null) ? 0 :
		// fPatternMap.hashCode());
		// result = prime * result + fPriority;
		return result;
	}

	protected final void init(IExpr patternExpr) {
		fLHSPriority = fPatternMap.determinePatterns(patternExpr);
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

	protected void logConditionFalse(final IExpr lhsEvalAST, final IExpr lhsPatternAST, IExpr rhsAST) {
		// System.out.println("\nCONDITION false: " + lhsEvalAST.toString() +
		// "\n >>> " + lhsPatternAST.toString() + " := "
		// + rhsAST.toString());
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
		if (lhsPatternAST.isExcept()) {
			if (lhsPatternAST.isAST2()) {
				return !matchExpr(lhsPatternAST.arg1(), lhsEvalExpr, stackMatcher)
						&& matchExpr(lhsPatternAST.arg2(), lhsEvalExpr, stackMatcher);
			} else {
				return !matchExpr(lhsPatternAST.arg1(), lhsEvalExpr, stackMatcher);
			}
		}
		if (lhsEvalExpr instanceof IAST) {
			if (!lhsPatternAST.isPatternExpr() && lhsPatternAST.equals(lhsEvalExpr)) {
				return stackMatcher == null || stackMatcher.matchRest();
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
								seq.appendAll(lhsEvalAST, lastPosition, lhsEvalAST.size());
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
							seq.appendAll(lhsEvalAST.range(), lastPosition, lhsEvalAST.size());
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
				// int size = lhsPatternAST.size();
				// if (lhsPatternAST.hasDefaultArgument()) {
				// IAST lhsAST = lhsPatternAST.removeAtClone(size - 1);
				// return matchAST(lhsAST, lhsEvalExpr, stackMatcher);
				// }
				return false;
			}

			final IExpr lhsPatternHead = lhsPatternAST.head();
			final IExpr lhsEvalHead = lhsEvalAST.head();
			if (lhsPatternHead.isSymbol() && lhsEvalHead.isSymbol()) {
				if (!lhsPatternHead.equals(lhsEvalHead)) {
					return false;
				}
			} else {
				// TODO create correct stack-matcher for the following call:
				if (!matchExpr(lhsPatternHead, lhsEvalHead, null)) {
					return false;
				}
			}

			if (lhsPatternAST.isOrderlessAST()) {
				// only "pure Orderless" and "FlatOrderless with same size()"
				// will be handled here:
				OrderlessStepVisitor visitor = new OrderlessStepVisitor(sym, lhsPatternAST, lhsEvalAST, stackMatcher,
						fPatternMap, (sym.hasOneIdentityAttribute())
								// if FLAT isn't set and the Orderless ASTs have
								// same size ==> use OneIdentity in pattern
								// matching
								|| (lhsPatternAST.size() == lhsEvalAST.size() && !sym.hasFlatAttribute()));
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
			ISignedNumber re = ((INumber) lhsEvalExpr).re();
			ISignedNumber im = ((INumber) lhsEvalExpr).im();
			if (matchExpr(lhsPatternAST.arg1(), re) && matchExpr(lhsPatternAST.arg2(), im)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Match all sub-expressions which contain no pattern objects if possible (i.e.
	 * no Flat or Orderless expressions,...)
	 * 
	 * Distinguishes between "equally" matched list-expressions and list expressions
	 * with <code>expr.isPatternExpr()==true</code>.
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
		if (stackMatcher == null) {
			stackMatcher = new StackMatcher();
		}
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
			if (stackMatcher != null && !stackMatcher.matchRest()) {
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
	 * Match the <code>lhsPatternAST</code> with its <code>Default[]</code> values.
	 * 
	 * @param symbolWithDefaultValue
	 *            the symbol for getting the associated default values from
	 * @param lhsPatternAST
	 *            left-hand-side which may contain patterns with default values
	 * @return <code>F.NIL</code> if the given <code>lhsPatternAST</code> could not
	 *         be matched or contains no pattern with default value.
	 */
	private IExpr matchDefaultArgumentsAST(ISymbol symbolWithDefaultValue, IAST lhsPatternAST) {
		IAST cloned = F.ast(lhsPatternAST.head(), lhsPatternAST.size(), false);
		boolean defaultValueMatched = false;
		for (int i = 1; i < lhsPatternAST.size(); i++) {
			if (lhsPatternAST.get(i).isPatternDefault()) {
				IPattern pattern = (IPattern) lhsPatternAST.get(i);
				IExpr positionDefaultValue = pattern.getDefaultValue();
				if (positionDefaultValue == null) {
					positionDefaultValue = symbolWithDefaultValue.getDefaultValue(i);
				}
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
			cloned.append(lhsPatternAST.get(i));
		}
		if (defaultValueMatched) {
			if (cloned.isOneIdentityAST1()) {
				return cloned.arg1();
			}
			return cloned;
		}
		return F.NIL;
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
			} else if (lhsPatternExpr.isAlternatives()) {
				IAST alternatives = (IAST) lhsPatternExpr;
				for (int i = 1; i < alternatives.size(); i++) {
					if (matchExpr(alternatives.get(i), lhsEvalExpr)) {
						matched = true;
						break;
					}
				}
				if (!matched) {
					return false;
				}
			} else if (lhsPatternExpr.isExcept()) {
				IAST except = (IAST) lhsPatternExpr;
				if (except.isAST2()) {
					matched = !matchExpr(except.arg1(), lhsEvalExpr, stackMatcher)
							&& matchExpr(except.arg2(), lhsEvalExpr, stackMatcher);
				} else {
					matched = !matchExpr(except.arg1(), lhsEvalExpr, stackMatcher);
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
							if (lhsEvalExpr.isAST() && lhsPatternAST.hasOptionalArgument()
									&& !lhsPatternAST.isOrderlessAST()) {
								temp = matchOptionalArgumentsAST(lhsPatternAST.topHead(), lhsPatternAST,
										(IAST) lhsEvalExpr);
								if (temp.isPresent()) {
									matched = matchExpr(temp, lhsEvalExpr, stackMatcher);
								}
							} else {
								temp = matchDefaultArgumentsAST(lhsPatternAST.topHead(), lhsPatternAST);
								if (temp.isPresent()) {
									matched = matchExpr(temp, lhsEvalExpr, stackMatcher);
								}
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
			return stackMatcher == null || stackMatcher.matchRest();
		}
		return false;

	}

	private boolean matchFlatAndFlatOrderlessAST(final ISymbol sym, final IAST lhsPatternAST, final IAST lhsEvalAST,
			StackMatcher stackMatcher) {
		if ((sym.getAttributes() & ISymbol.ORDERLESS) == ISymbol.ORDERLESS) {
			// System.out.println(lhsPatternAST.toString() + " << >> " +
			// lhsEvalAST.toString());

			if (lhsPatternAST.isAST1()) {
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
			if (lhsPatternAST.isAST1()) {
				if (lhsPatternAST.arg1().isPatternSequence()) {
					// TODO only the special case, where the last element is
					// a pattern sequence, is handled here
					IAST seq = F.Sequence();
					seq.appendAll(lhsEvalAST, 1, lhsEvalAST.size());
					if (((IPatternSequence) lhsPatternAST.arg1()).matchPatternSequence(seq, fPatternMap)) {
						// if (matchAST(lhsPatternAST.copyUntil(1),
						// lhsEvalAST.copyUntil(1),
						// stackMatcher)) {
						// return
						// fPatternMap.isPatternTest(lhsPatternAST.arg1(),
						// lhsPatternAST.arg2());
						// }
						return true;
					}
				}
				if (lhsPatternAST.size() == lhsEvalAST.size()) {
					return matchASTSequence(lhsPatternAST, lhsEvalAST, 0, stackMatcher);
				}
				return false;
			}
			FlatStepVisitor visitor = new FlatStepVisitor(sym, lhsPatternAST, lhsEvalAST, stackMatcher, fPatternMap);
			NumberPartitionsIterator iter = new NumberPartitionsIterator(visitor, lhsEvalAST.size() - 1,
					lhsPatternAST.size() - 1);
			return !iter.execute();
		}
	}

	/**
	 * Match the <code>lhsPatternAST</code> with its <code>Default[]</code> values.
	 * 
	 * @param symbolWithDefaultValue
	 *            the symbol for getting the associated default values from
	 * @param lhsPatternAST
	 *            left-hand-side which may contain patterns with default values
	 * @return <code>F.NIL</code> if the given <code>lhsPatternAST</code> could not
	 *         be matched or contains no pattern with default value.
	 */
	private IExpr matchOptionalArgumentsAST(ISymbol symbolWithDefaultValue, IAST lhsPatternAST, IAST lhsEvalAST) {
		int lhsSize = lhsEvalAST.size();
		IAST cloned = F.ast(lhsPatternAST.head(), lhsPatternAST.size(), false);
		boolean defaultValueMatched = false;
		for (int i = 1; i < lhsPatternAST.size(); i++) {
			if (lhsPatternAST.get(i).isPatternDefault()) {
				IPattern pattern = (IPattern) lhsPatternAST.get(i);
				if (i < lhsSize) {
					cloned.append(pattern);
					continue;
				}
				IExpr positionDefaultValue = pattern.getDefaultValue();
				if (positionDefaultValue == null) {
					positionDefaultValue = symbolWithDefaultValue.getDefaultValue(i);
				}
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
			cloned.append(lhsPatternAST.get(i));
		}
		if (defaultValueMatched) {
			if (cloned.isOneIdentityAST1()) {
				return cloned.arg1();
			}
			return cloned;
		}
		return F.NIL;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		fLhsPatternExpr = (IExpr) objectInput.readObject();
		fPatternCondition = (IExpr) objectInput.readObject();
		this.fPatternMap = new PatternMap();
		if (fLhsPatternExpr != null) {
			fLHSPriority = fPatternMap.determinePatterns(fLhsPatternExpr);
		}
	}

	/**
	 * Sets an additional evaluation-condition for pattern-matching
	 * 
	 */
	public void setCondition(final IExpr condition) {
		fPatternCondition = condition;
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
	public boolean test(final IExpr leftHandSide) {

		if (isRuleWithoutPatterns()) {
			// no patterns found match equally:
			return fLhsPatternExpr.equals(leftHandSide);
		}

		fPatternMap.initPattern();
		return matchExpr(fLhsPatternExpr, leftHandSide);
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeObject(fLhsPatternExpr);
		objectOutput.writeObject(fPatternCondition);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PatternMatcher other = (PatternMatcher) obj;
		if (fPatternCondition == null) {
			if (other.fPatternCondition != null)
				return false;
		} else if (!fPatternCondition.equals(other.fPatternCondition))
			return false;
		// if (fPatternMap == null) {
		// if (other.fPatternMap != null)
		// return false;
		// } else if (!fPatternMap.equals(other.fPatternMap))
		// return false;
		// if (fPriority != other.fPriority)
		// return false;
		return true;
	}
}