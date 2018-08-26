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
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
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
		 * The used (i.e. matched) expression indexes in the LHS evaluation expression; <code>-1</code> indicates an
		 * unused index.
		 */
		private int[] fUsedIndex;

		/**
		 * Match a pattern expression against an evaluation expression, there the arguments are commutative (i.e. the
		 * head of the AST expression has attribute <code>Orderless</code>)
		 * 
		 * @param lhsPatternAST
		 *            the pattern AST
		 * @param lhsEvalAST
		 *            the evaluation AST
		 */
		public OrderlessMatcher(final IAST lhsPatternAST, final IAST lhsEvalAST) {
			this.fLHSPatternAST = lhsPatternAST;
			this.fLHSEvalAST = lhsEvalAST;
			this.fUsedIndex = new int[fLHSPatternAST.argSize()];
			for (int l = 0; l < fUsedIndex.length; l++) {
				fUsedIndex[l] = -1;
			}
		}

		public void filterResult(IASTAppendable result) {
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
		 *            the position in the LHS expression which should actually be matched.
		 * @param stackMatcher
		 * @return
		 */
		public boolean matchOrderlessAST(int lhsPosition, StackMatcher stackMatcher, EvalEngine engine) {
			if (lhsPosition >= fLHSPatternAST.size()) {
				return stackMatcher == null || stackMatcher.matchRest();
			}
			final IExpr subPattern = fLHSPatternAST.get(lhsPosition);
			final IExpr[] patternValues = fPatternMap.copyPattern();
			return fLHSEvalAST.exists((temp, j) -> {
				if (fLHSPatternAST.forAll((x, i) -> fUsedIndex[i - 1] != j)) {
					boolean matched = false;
					final StackMatcher localStackMatcher = stackMatcher == null ? new StackMatcher(engine)
							: stackMatcher;
					int lastStackSize = localStackMatcher.size();
					try {
						if (localStackMatcher.push(subPattern, temp)) {
							fUsedIndex[lhsPosition - 1] = j;
							if (matchOrderlessAST(lhsPosition + 1, localStackMatcher, engine)) {
								matched = true;
								return true;
							}
						}
					} finally {
						if (!matched) {
							fPatternMap.resetPattern(patternValues);
							fUsedIndex[lhsPosition - 1] = -1;
						}
						localStackMatcher.removeFrom(lastStackSize);
					}
				}
				return false;
			});
		}
	}

	/**
	 * Manage a stack of pairs of expressions, which have to match each other
	 * 
	 */
	@SuppressWarnings("serial")
	public class StackMatcher extends ArrayDeque<Entry> {
		final EvalEngine fEngine;

		public StackMatcher(EvalEngine engine) {
			fEngine = engine;
		}

		/**
		 * Match the entries of the stack recursively starting from the top entry.
		 * 
		 * @return <code>true</code> if all expressions could be matched.
		 */
		public boolean matchRest() {
			if (isEmpty()) {
				return checkCondition(fEngine);
			}
			boolean matched = true;
			Entry entry = pop();
			try {
				// if (SUBSTITUTE_MATCHED_PATTERNS) {
				// IExpr temp = fPatternMap.substitutePatternOrSymbols(entry.fPatternExpr);
				// if (!temp.isPresent()) {
				// temp = entry.fPatternExpr;
				// }
				// matched = matchExpr(temp, entry.fEvalExpr, fEngine, this);
				// } else {
				matched = matchExpr(entry.fPatternExpr, entry.fEvalExpr, fEngine, this);
				// }
				return matched;
			} finally {
				if (!matched) {
					push(entry);
				}
			}
		}

		public boolean push(IExpr patternExpr, IExpr evalExpr) {
			if (!patternExpr.isFreeOfPatterns()) {
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
	 * Check if the two left-hand-side pattern expressions are equivalent. (i.e. <code>f[x_,y_]</code> is equivalent to
	 * <code>f[a_,b_]</code> )
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
				return l1.forAll((temp1, i) -> {
					IExpr temp2 = l2.get(i);
					if (temp1 == temp2) {
						return true;
					}
					if (temp1.hashCode() != temp2.hashCode()) {
						if (temp1.isPatternExpr() && temp2.isPatternExpr()) {
							return equivalent(temp1, temp2, pm1, pm2);
						}
						return false;
					}
					if (!temp1.isPatternExpr() || !temp2.isPatternExpr()) {
						if (!temp1.equals(temp2)) {
							return false;
						}
					}
					return equivalent(temp1, temp2, pm1, pm2);
				}, 0);
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
	 * Transform the ast recursively, according to the attributes Flat, HoldAll, HoldFirst, HoldRest, Orderless for the
	 * left-hand-side of a Set[] or SetDelayed[] expression. Delegates to
	 * <code>EvalEngine#evalSetAttributes()</code>method
	 * 
	 * @param ast
	 * @return <code>F.NIL</code> if evaluation is not possible
	 * @deprecated use EvalEngine#evalHoldPattern(leftHandSide)
	 */
	public static IExpr evalLeftHandSide(final IAST leftHandSide, final EvalEngine engine) {
		return engine.evalHoldPattern(leftHandSide);
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
	 * A map from a pattern to a possibly found value during pattern-matching. Will be set to <code>null</code> if the
	 * left-hand-side pattern expression contains no pattern.
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
			this.fLhsPatternExpr = patternExpr.first();
			this.fPatternCondition = patternExpr.second();
		}
		this.fPatternMap = new PatternMap();
		init(fLhsPatternExpr);
	}

	/**
	 * Check if the condition for this pattern matcher evaluates to <code>true</code>.
	 */
	public boolean checkCondition(EvalEngine engine) {

		if (fPatternCondition != null) {
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
		} else {
			boolean traceMode = false;
			try {
				traceMode = engine.isTraceMode();
				engine.setTraceMode(false);
				return checkRHSCondition(engine);
			} finally {
				engine.setTraceMode(traceMode);
			}
		}
	}

	/**
	 * Check if the condition for the right-hand-sides <code>Module[] or Condition[]</code> expressions evaluates to
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

		// if (getRHSPriority() < patternMatcher.getRHSPriority()) {
		// return -1;
		// }
		// if (getRHSPriority() > patternMatcher.getRHSPriority()) {
		// return 1;
		// }

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

	@Override
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
	public IExpr eval(final IExpr leftHandSide, EvalEngine engine) {
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
	protected IExpr evalAST(final IAST lhsPatternAST, final IAST lhsEvalAST, final IExpr rhsExpr, EvalEngine engine) {
		if (lhsPatternAST.size() < lhsEvalAST.size()) {
			if (lhsPatternAST.isOrderlessAST() && lhsPatternAST.isFlatAST()) {
				if (!matchExpr(lhsPatternAST.head(), lhsEvalAST.head(), engine, null)) {
					return F.NIL;
				}
				final OrderlessMatcher foMatcher = new OrderlessMatcher(lhsPatternAST, lhsEvalAST);
				boolean matched = foMatcher.matchOrderlessAST(1, null, engine);
				if (matched) {
					IASTAppendable lhsResultAST = (lhsEvalAST).copyAppendable();
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
				if (!matchExpr(lhsPatternAST.head(), lhsEvalAST.head(), engine, (StackMatcher) null)) {
					return F.NIL;
				}
				int len = lhsEvalAST.size() - lhsPatternAST.size();
				for (int i = 0; i < len; i++) {
					if (matchASTSequence(lhsPatternAST, lhsEvalAST, i, engine, null)) {
						IASTAppendable lhsResultAST = lhsEvalAST.copyAppendable();
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
			((IAST) pExpr).forEach(x -> getPatterns(resultList, x), 0);
		} else if (pExpr.isPattern()) {
			resultList.add(fPatternMap.getValue((IPattern) pExpr));
		}
	}

	/**
	 * Return the matched value for index 0 if possisble.
	 * 
	 * @return <code>null</code> if no matched expression exists
	 */
	public IExpr getPatternValue0() {
		return fPatternMap.getValue(0);
	}

	/**
	 * Get the priority of this pattern-matcher. Lower values have higher priorities.
	 * 
	 * @return the priority
	 */
	@Override
	public int getLHSPriority() {
		return fLHSPriority;
	}

	public void setLHSPriority(final int priority) {
		fLHSPriority = priority;
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

	protected boolean matchAST(final IAST lhsPatternAST, final IExpr lhsEvalExpr, EvalEngine engine,
			StackMatcher stackMatcher) {
		// System.out.println(lhsPatternAST.toString()+" -
		// "+lhsEvalExpr.toString());
		int functionID = lhsPatternAST.headID();
		if (functionID > ID.UNKNOWN) {
			switch (functionID) {
			case ID.Except:
				if (lhsPatternAST.isExcept()) {
					if (lhsPatternAST.isAST2()) {
						return !matchExpr(lhsPatternAST.arg1(), lhsEvalExpr, engine, stackMatcher)
								&& matchExpr(lhsPatternAST.arg2(), lhsEvalExpr, engine, stackMatcher);
					} else {
						return !matchExpr(lhsPatternAST.arg1(), lhsEvalExpr, engine, stackMatcher);
					}
				}
				break;
			case ID.PatternTest:
				if (lhsPatternAST.isPatternTest()) {
					if (matchExpr(lhsPatternAST.arg1(), lhsEvalExpr, engine, stackMatcher)) {
						return fPatternMap.isPatternTest(lhsPatternAST.arg1(), lhsPatternAST.arg2(), engine);
					}
					return false;
				}
				break;
			default:
				break;
			}
		}
		if (lhsEvalExpr instanceof IAST) {
			if (lhsPatternAST.isFreeOfPatterns() && lhsPatternAST.equals(lhsEvalExpr)) {
				return stackMatcher == null || stackMatcher.matchRest();
			}

			final IAST lhsEvalAST = (IAST) lhsEvalExpr;
			final ISymbol sym = lhsPatternAST.topHead();
			if (lhsPatternAST.size() <= lhsEvalAST.size()) {
				if ((lhsPatternAST.isFlatAST()) && sym.equals(lhsEvalAST.topHead())
						&& !(lhsPatternAST.isOrderlessAST() && lhsPatternAST.size() == lhsEvalAST.size())) {
					if (!matchExpr(lhsPatternAST.head(), lhsEvalAST.head(), engine)) {
						return false;
					}
					return matchFlatAndFlatOrderlessAST(sym, lhsPatternAST, lhsEvalAST, engine, stackMatcher);
				}

				if (lhsPatternAST.size() < lhsEvalAST.size()) {
					if (lhsPatternAST.isEvalFlagOn(IAST.CONTAINS_PATTERN_SEQUENCE)) {
						if (!matchExpr(lhsPatternAST.head(), lhsEvalAST.head(), engine)) {
							return false;
						}
						int lastPosition = lhsPatternAST.argSize();
						if (lhsPatternAST.get(lastPosition).isAST(F.PatternTest, 3)) {
							IAST patternTest = (IAST) lhsPatternAST.get(lastPosition);
							if (patternTest.arg1().isPatternSequence(false)) {
								// TODO only the special case, where the last
								// element is
								// a pattern sequence, is handled here
								IASTAppendable seq = F.Sequence();
								seq.appendAll(lhsEvalAST, lastPosition, lhsEvalAST.size());
								if (((IPatternSequence) patternTest.arg1()).matchPatternSequence(seq, fPatternMap)) {
									if (matchAST(lhsPatternAST.removeFromEnd(lastPosition),
											lhsEvalAST.copyUntil(lastPosition), engine, stackMatcher)) {
										return fPatternMap.isPatternTest(patternTest.arg1(), patternTest.arg2(),
												engine);
									}
									return false;
								}
							}

						}
						if (lhsPatternAST.get(lastPosition).isPatternSequence(false)) {
							// TODO only the special case, where the last
							// element is
							// a pattern sequence, is handled here
							IASTAppendable seq = F.Sequence();
							seq.appendAll(lhsEvalAST, lastPosition, lhsEvalAST.size());
							if (((IPatternSequence) lhsPatternAST.get(lastPosition)).matchPatternSequence(seq,
									fPatternMap)) {
								return matchAST(lhsPatternAST.copyUntil(lastPosition),
										lhsEvalAST.copyUntil(lastPosition), engine, stackMatcher);
							}
						}
					}

					return false;
				}
			}

			final IExpr lhsPatternHead = lhsPatternAST.head();
			final IExpr lhsEvalHead = lhsEvalAST.head();
			if (lhsPatternAST.size() != lhsEvalAST.size()) {
				if (lhsPatternAST.isEvalFlagOn(IAST.CONTAINS_PATTERN_SEQUENCE) && lhsPatternHead.equals(lhsEvalHead)
						&& lhsPatternAST.size() > lhsEvalAST.size()) {
					return matchASTWithBlankNullSequence(lhsPatternAST, lhsEvalAST, engine, stackMatcher);
				}
				// int size = lhsPatternAST.size();
				// if (lhsPatternAST.hasDefaultArgument()) {
				// IAST lhsAST = lhsPatternAST.removeAtClone(size - 1);
				// return matchAST(lhsAST, lhsEvalExpr, stackMatcher);
				// }
				return false;
			}
			if (lhsPatternHead.isSymbol() && lhsEvalHead.isSymbol()) {
				if (!lhsPatternHead.equals(lhsEvalHead)) {
					return false;
				}
			} else {
				// TODO create correct stack-matcher for the following call:
				if (!matchExpr(lhsPatternHead, lhsEvalHead, engine, null)) {
					return false;
				}
			}

			if (lhsPatternAST.isOrderlessAST() && lhsPatternAST.size() > 2) {
				// only "pure Orderless" and "FlatOrderless with same size()"
				// will be handled here:
				OrderlessStepVisitor visitor = new OrderlessStepVisitor(sym, lhsPatternAST, lhsEvalAST, stackMatcher,
						fPatternMap, (sym.hasOneIdentityAttribute())
								// if FLAT isn't set and the Orderless ASTs have
								// same size ==> use OneIdentity in pattern
								// matching
								|| (lhsPatternAST.size() == lhsEvalAST.size() && !sym.hasFlatAttribute()));
				MultisetPartitionsIterator iter = new MultisetPartitionsIterator(visitor, lhsPatternAST.argSize());
				return !iter.execute();
			}

			return matchASTSequence(lhsPatternAST, lhsEvalAST, 0, engine, stackMatcher);
		}
		if (lhsPatternAST.isAST(F.Rational, 3) && lhsEvalExpr.isRational()) {
			IRational numer = ((IRational) lhsEvalExpr).numerator();
			IRational denom = ((IRational) lhsEvalExpr).denominator();
			if (matchExpr(lhsPatternAST.arg1(), numer, engine) && matchExpr(lhsPatternAST.arg2(), denom, engine)) {
				return true;
			}
		} else if (lhsPatternAST.isAST(F.Complex, 3) && lhsEvalExpr.isNumber()) {
			ISignedNumber re = ((INumber) lhsEvalExpr).re();
			ISignedNumber im = ((INumber) lhsEvalExpr).im();
			if (matchExpr(lhsPatternAST.arg1(), re, engine) && matchExpr(lhsPatternAST.arg2(), im, engine)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Precondition <code>lhsPatternAST.size() > lhsEvalAST.size()</code>.
	 * 
	 * @param lhsPatternAST
	 *            an AST which contains a BlankNullSequence
	 * @param lhsEvalAST
	 * @param engine
	 * @param stackMatcher
	 * @return
	 */
	private boolean matchASTWithBlankNullSequence(final IAST lhsPatternAST, final IAST lhsEvalAST, EvalEngine engine,
			StackMatcher stackMatcher) {
		int size = lhsPatternAST.size();
		if (lhsPatternAST.isOrderlessAST()) {
			IASTAppendable result = F.NIL;
			for (int i = 1; i < lhsPatternAST.size(); i++) {
				if (!lhsPatternAST.get(i).isPatternSequence(true)) {
					if (result.isPresent()) {
						result.append(lhsPatternAST.get(i));
					}
				} else {
					size--;
					if (!result.isPresent()) {
						result = lhsPatternAST.copyUntil(i);
					}
					if (size == lhsEvalAST.size()) {
						if (result.exists(x -> x.isPatternSequence(true))) {
							result.addEvalFlags(IAST.CONTAINS_PATTERN_SEQUENCE);
						}
						return matchAST(result, lhsEvalAST, engine, stackMatcher);
					}
				}
			}
		} else {
			boolean evaled = false;
			IAST result = lhsPatternAST;
			// search for null sequences from the start
			for (int i = 1; i < lhsPatternAST.size(); i++) {
				if (!lhsPatternAST.get(i).isPatternSequence(true)) {
					if (evaled) {
						result = lhsPatternAST.copyFrom(i);
					}
					break;
				} else {
					evaled = true;
					size--;
					if (size == lhsEvalAST.size()) {
						result = lhsPatternAST.copyFrom(i + 1);
						if (result.exists(x -> x.isPatternSequence(true))) {
							result.addEvalFlags(IAST.CONTAINS_PATTERN_SEQUENCE);
						}
						return matchAST(result, lhsEvalAST, engine, stackMatcher);
					}
				}
			}
			// search for null sequences from the end
			for (int i = result.size() - 1; i >= 1; i--) {
				if (!result.get(i).isPatternSequence(true)) {
					break;
				} else {
					size--;
					if (size == lhsEvalAST.size()) {
						result = result.copyUntil(i);
						if (result.exists(x -> x.isPatternSequence(true))) {
							result.addEvalFlags(IAST.CONTAINS_PATTERN_SEQUENCE);
						}
						return matchAST(result, lhsEvalAST, engine, stackMatcher);
					}
				}
			}
		}
		return false;
	}

	/**
	 * Match all sub-expressions which contain no pattern objects if possible (i.e. no Flat or Orderless
	 * expressions,...)
	 * 
	 * Distinguishes between "equally" matched list-expressions and list expressions with
	 * <code>expr.isPatternExpr()==true</code>.
	 * 
	 * @param lhsPatternAST
	 * @param lhsEvalAST
	 * @param lhsEvalOffset
	 * @param stackMatcher
	 * @return
	 */
	private boolean matchASTSequence(final IAST lhsPatternAST, final IAST lhsEvalAST, final int lhsEvalOffset,
			EvalEngine engine, StackMatcher stackMatcher) {
		// distinguish between "equally" matched list-expressions and AST expressions with "CONTAINS_PATTERN" flag
		IExpr[] patternValues = fPatternMap.copyPattern();
		if (stackMatcher == null) {
			stackMatcher = new StackMatcher(engine);
		}
		int lastStackSize = stackMatcher.size();
		boolean matched = true;
		try {
			// loop from the end
			// for (int i = lhsPatternAST.argSize(); i > 0; i--) {
			// if (!stackMatcher.push(lhsPatternAST.get(i), lhsEvalAST.get(lhsEvalOffset + i))) {
			// matched = false;
			// return false;
			// }
			// }
			for (int i = 1; i < lhsPatternAST.size(); i++) {
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
	 * @return <code>F.NIL</code> if the given <code>lhsPatternAST</code> could not be matched or contains no pattern
	 *         with default value.
	 */
	private IExpr matchDefaultArgumentsAST(ISymbol symbolWithDefaultValue, IAST lhsPatternAST) {
		IASTAppendable cloned = F.ast(lhsPatternAST.head(), lhsPatternAST.size(), false);
		boolean[] defaultValueMatched = new boolean[] { false };
		if (lhsPatternAST.exists((temp, i) -> {
			if (temp.isPatternDefault()) {
				IPattern pattern = (IPattern) temp;
				IExpr positionDefaultValue = pattern.getDefaultValue();
				if (positionDefaultValue == null) {
					positionDefaultValue = symbolWithDefaultValue.getDefaultValue(i);
				}
				if (positionDefaultValue != null) {
					if (!((IPatternObject) temp).matchPattern(positionDefaultValue, fPatternMap)) {
						return true;
					}
					defaultValueMatched[0] = true;
					return false;
				} else {
					IExpr commonDefaultValue = symbolWithDefaultValue.getDefaultValue();
					if (commonDefaultValue != null) {
						if (!((IPatternObject) temp).matchPattern(commonDefaultValue, fPatternMap)) {
							return true;
						}
						defaultValueMatched[0] = true;
						return false;
					}
				}
			}
			cloned.append(temp);
			return false;
		})) {
			return F.NIL;
		}
		if (defaultValueMatched[0]) {
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
	protected boolean matchExpr(IExpr lhsPatternExpr, final IExpr lhsEvalExpr, EvalEngine engine) {
		return matchExpr(lhsPatternExpr, lhsEvalExpr, engine, new StackMatcher(engine));
	}

	/**
	 * Checks if the two expressions match each other
	 * 
	 * @return
	 */
	protected boolean matchExpr(IExpr lhsPatternExpr, final IExpr lhsEvalExpr, EvalEngine engine,
			StackMatcher stackMatcher) {
		boolean matched = false;
		if (lhsPatternExpr.isAST()) {
			IAST lhsPatternAST = (IAST) lhsPatternExpr;
			int functionID = lhsPatternAST.headID();
			if (functionID > ID.UNKNOWN) {
				switch (functionID) {
				case ID.HoldPattern:
					if (lhsPatternAST.isHoldPattern()) {
						return matchExpr(lhsPatternAST.arg1(), lhsEvalExpr, engine, stackMatcher);
					}
					break;
				case ID.Condition:
					if (lhsPatternAST.isCondition()) {
						// expression /; test
						IExpr lhsTempPatternExpr = fPatternMap.substituteSymbols(lhsPatternAST);
						if (lhsTempPatternExpr.isAST()) {
							lhsTempPatternExpr = engine.evalHoldPattern((IAST) lhsTempPatternExpr);
						}
						final PatternMatcher matcher = new PatternMatcherEvalEngine(lhsTempPatternExpr, engine);
						if (matcher.test(lhsEvalExpr, engine)) {
							matched = true;
							fPatternMap.copyPatternValuesFromPatternMatcher(matcher.fPatternMap);
						}
					}
					break;
				case ID.Alternatives:
					if (lhsPatternAST.isAlternatives()) {
						IAST alternatives = (IAST) lhsPatternAST;
						matched = alternatives.exists(x -> matchExpr(x, lhsEvalExpr, engine));
						if (!matched) {
							return false;
						}
					}
					break;
				case ID.Except:
					if (lhsPatternAST.isExcept()) {
						if (lhsPatternAST.isAST2()) {
							matched = !matchExpr(lhsPatternAST.arg1(), lhsEvalExpr, engine, stackMatcher)
									&& matchExpr(lhsPatternAST.arg2(), lhsEvalExpr, engine, stackMatcher);
						} else {
							matched = !matchExpr(lhsPatternAST.arg1(), lhsEvalExpr, engine, stackMatcher);
						}
						if (!matched) {
							return false;
						}
					}
					break;
				case ID.Complex:
					if (lhsPatternAST.isAST(F.Complex, 3)) {
						if (lhsEvalExpr.isNumber()) {
							INumber number = (INumber) lhsEvalExpr;
							matched = matchExpr(lhsPatternAST.arg1(), number.re(), engine, stackMatcher)
									&& matchExpr(lhsPatternAST.arg2(), number.im(), engine, stackMatcher);
						} else {
							matched = matchASTExpr(lhsPatternAST, lhsEvalExpr, engine, stackMatcher);
						}
						if (!matched) {
							return false;
						}
					}
					break;
				case ID.Rational:
					if (lhsPatternAST.isAST(F.Rational, 3)) {
						if (lhsEvalExpr.isRational()) {
							IRational rational = (IRational) lhsEvalExpr;
							matched = matchExpr(lhsPatternAST.arg1(), rational.numerator(), engine, stackMatcher)
									&& matchExpr(lhsPatternAST.arg2(), rational.denominator(), engine, stackMatcher);
						} else {
							matched = matchASTExpr(lhsPatternAST, lhsEvalExpr, engine, stackMatcher);
						}
						if (!matched) {
							return false;
						}
					}
					break;
				default:
					matched = matchASTExpr(lhsPatternAST, lhsEvalExpr, engine, stackMatcher);
					break;
				}
			} else {
				matched = matchASTExpr(lhsPatternAST, lhsEvalExpr, engine, stackMatcher);
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

	private boolean matchASTExpr(IAST lhsPatternExpr, final IExpr lhsEvalExpr, EvalEngine engine,
			StackMatcher stackMatcher) {
		boolean matched = false;
		IAST lhsPatternAST = (IAST) lhsPatternExpr;
		IExpr[] patternValues = fPatternMap.copyPattern();
		try {
			matched = matchAST(lhsPatternAST, lhsEvalExpr, engine, stackMatcher);
			if (!matched) {
				if (!lhsPatternAST.isFreeOfDefaultPatterns()) {
					// if ((lhsPatternAST.getEvalFlags() & IAST.CONTAINS_DEFAULT_PATTERN) ==
					// IAST.CONTAINS_DEFAULT_PATTERN) {

					IExpr temp = null;
					fPatternMap.resetPattern(patternValues);
					if (lhsEvalExpr.isAST() && lhsPatternAST.hasOptionalArgument() && !lhsPatternAST.isOrderlessAST()) {
						// TODO for Power[x_, y_.] matching Power[a,b] test both cases Power[a,b] && Power[Power[a,b],1]
						temp = matchOptionalArgumentsAST(lhsPatternAST.topHead(), lhsPatternAST, (IAST) lhsEvalExpr);
						if (temp.isPresent()) {
							matched = matchExpr(temp, lhsEvalExpr, engine, stackMatcher);
						}
					} else {
						temp = matchDefaultArgumentsAST(lhsPatternAST.topHead(), lhsPatternAST);
						if (temp.isPresent()) {
							matched = matchExpr(temp, lhsEvalExpr, engine, stackMatcher);
						}
					}
				}
			}
		} finally {
			if (!matched) {
				fPatternMap.resetPattern(patternValues);
			}
		}
		return matched;
	}

	private boolean matchFlatAndFlatOrderlessAST(final ISymbol sym, IAST lhsPattern, IAST lhsEval, EvalEngine engine,
			StackMatcher stackMatcher) {
		int lhsPatternSize = lhsPattern.size();
		if ((sym.getAttributes() & ISymbol.ORDERLESS) == ISymbol.ORDERLESS) {
			// System.out.println(lhsPatternAST.toString() + " << >> " +
			// lhsEvalAST.toString());

			if (lhsPattern.isAST1()) {
				return matchExpr(lhsPattern.arg1(), lhsEval, engine, stackMatcher);
			}
			IExpr[] patternValues = fPatternMap.copyPattern();

			IExpr temp = fPatternMap.substitutePatternOrSymbols(lhsPattern);
			if (!temp.isPresent()) {
				temp = lhsPattern;
			}
			IASTAppendable lhsPatternAST = (IASTAppendable) ((IAST) temp).copyAppendable();
			IASTAppendable lhsEvalAST = (IASTAppendable) lhsEval.copyAppendable();
			int iIndex = 1;
			while (iIndex < lhsPatternAST.size()) {
				// for (int i = 1; i < lhsPatternSize; i++) {
				temp = lhsPatternAST.get(iIndex);

				if (temp.isFreeOfPatterns()) {
					boolean evaled = false;
					int jIndex = 1;
					while (jIndex < lhsEvalAST.size()) {
						IExpr x = lhsEvalAST.get(jIndex);
						if (x.equals(temp)) {
							lhsPatternAST.remove(iIndex);
							lhsEvalAST.remove(jIndex);
							evaled = true;
							break;
						}
						jIndex++;
					}
					if (!evaled) {
						return false;
					}
					continue;
				}
				iIndex++;
			}
			if (lhsPatternAST.size() <= 2) {
				if (lhsPatternAST.isOneIdentityAST1()) {
					return matchExpr(lhsPatternAST.arg1(), lhsEvalAST, engine, stackMatcher);
				}
				if (lhsPatternAST.size() == 2) {
					return matchExpr(lhsPatternAST.arg1(), lhsEvalAST, engine, stackMatcher);
				}
				if (lhsPatternAST.size() == 1 && lhsEvalAST.size() > 1) {
					return false;
				}
				return stackMatcher == null || stackMatcher.matchRest();
			}
			lhsPattern = lhsPatternAST;
			lhsEval = lhsEvalAST;

			final IAST lhsPatternFinal = lhsPattern;
			final IAST lhsEvalFinal = lhsEval;
			for (int i = 1; i < lhsPatternFinal.size(); i++) {
				temp = lhsPatternFinal.get(i);
				if (!(temp instanceof IPatternObject)) {
					final int index = i;
					return lhsEval.exists((x, j) -> {
						StackMatcher myStackMatcher = stackMatcher;
						if (myStackMatcher == null) {
							myStackMatcher = new StackMatcher(engine);
						}
						int lastStackSize = myStackMatcher.size();

						if (myStackMatcher.push(lhsPatternFinal.removeAtClone(index), lhsEvalFinal.removeAtClone(j))) {
							boolean matched = false;
							try {
								if (matchExpr(lhsPatternFinal.get(index), x, engine, myStackMatcher)) {
									// if (matchFlatAndFlatOrderlessAST(sym, lhsPatternAST.removeAtClone(index),
									// lhsEvalAST.removeAtClone(j), engine, stackMatcher)) {
									matched = true;
									return true;
									// }
								}
							} finally {
								if (!matched) {
									stackMatcher.removeFrom(lastStackSize);
								}
							}
						}
						fPatternMap.resetPattern(patternValues);
						return false;
					}, 1);
				}
			}

			FlatOrderlessStepVisitor visitor = new FlatOrderlessStepVisitor(sym, lhsPatternFinal, lhsEvalFinal,
					stackMatcher, fPatternMap);
			MultisetPartitionsIterator iter = new MultisetPartitionsIterator(visitor, lhsPattern.argSize());
			return !iter.execute();
		} else {
			int lhsEvalSize = lhsEval.size();
			if (lhsPattern.isAST1()) {
				if (lhsPattern.arg1().isPatternSequence(false)) {
					// TODO only the special case, where the last element is
					// a pattern sequence, is handled here
					IASTAppendable seq = F.Sequence();
					seq.appendAll(lhsEval, 1, lhsEvalSize);
					if (((IPatternSequence) lhsPattern.arg1()).matchPatternSequence(seq, fPatternMap)) {
						return true;
					}
				}
				if (lhsPatternSize == lhsEvalSize) {
					return matchASTSequence(lhsPattern, lhsEval, 0, engine, stackMatcher);
				}
				return false;
			}
			FlatStepVisitor visitor = new FlatStepVisitor(sym, lhsPattern, lhsEval, stackMatcher, fPatternMap);
			NumberPartitionsIterator iter = new NumberPartitionsIterator(visitor, lhsEval.argSize(),
					lhsPattern.argSize());
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
	 * @return <code>F.NIL</code> if the given <code>lhsPatternAST</code> could not be matched or contains no pattern
	 *         with default value.
	 */
	private IExpr matchOptionalArgumentsAST(ISymbol symbolWithDefaultValue, IAST lhsPatternAST, IAST lhsEvalAST) {
		int lhsSize = lhsEvalAST.size();
		IExpr head = lhsEvalAST.head();
		IASTAppendable cloned = F.ast(lhsPatternAST.head(), lhsPatternAST.size(), false);
		boolean defaultValueMatched = false;
		for (int i = 1; i < lhsPatternAST.size(); i++) {
			if (lhsPatternAST.get(i).isPatternDefault()) {
				IPattern pattern = (IPattern) lhsPatternAST.get(i);
				IExpr positionDefaultValue = symbolWithDefaultValue.getDefaultValue(i);
				if (positionDefaultValue == null) {
					if (i < lhsSize && symbolWithDefaultValue.equals(head)) {
						cloned.append(pattern);
						continue;
					}
				}
				if (positionDefaultValue == null) {
					positionDefaultValue = pattern.getDefaultValue();
				}
				if (positionDefaultValue != null) {
					if (!((IPatternObject) lhsPatternAST.get(i)).matchPattern(positionDefaultValue, fPatternMap)) {
						return F.NIL;
					}
					defaultValueMatched = true;
					continue;
				} else {
					if (i < lhsSize) {
						cloned.append(pattern);
						continue;
					}
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
		return test(leftHandSide, EvalEngine.get());
	}

	@Override
	public boolean test(final IExpr leftHandSide, EvalEngine engine) {
		if (isRuleWithoutPatterns()) {
			// no patterns found match equally:
			return fLhsPatternExpr.equals(leftHandSide);
		}

		fPatternMap.initPattern();
		return matchExpr(fLhsPatternExpr, leftHandSide, engine);
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