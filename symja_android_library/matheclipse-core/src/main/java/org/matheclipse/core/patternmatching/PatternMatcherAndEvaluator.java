package org.matheclipse.core.patternmatching;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.annotation.Nonnull;

import org.matheclipse.core.builtin.Programming;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.ExprUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.ISymbol.RuleType;

public class PatternMatcherAndEvaluator extends PatternMatcher implements Externalizable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2241135467123931061L;

	private IExpr fRightHandSide;

	/**
	 * Leaf count of the right-hand-side of this matcher if it's a <code>Condition()</code> or
	 * <code>Module(...,Condition()) or With(...,Condition())</code> expression.
	 * 
	 */
	protected transient long fRHSleafCountSimplify;

	private ISymbol.RuleType fSetSymbol;

	/**
	 * Public constructor for serialization.
	 */
	public PatternMatcherAndEvaluator() {

	}

	/**
	 * Define a pattern-matching rule.
	 * 
	 * @param leftHandSide
	 *            could contain pattern expressions for "pattern-matching"
	 * @param rightHandSide
	 *            the result which should be evaluated if the "pattern-matching" succeeds
	 */
	public PatternMatcherAndEvaluator(final IExpr leftHandSide, final IExpr rightHandSide) {
		this(ISymbol.RuleType.SET_DELAYED, leftHandSide, rightHandSide);
	}

	/**
	 * ine a pattern-matching rule.
	 * 
	 * @param setSymbol
	 *            the symbol which defines this pattern-matching rule (i.e. Set, SetDelayed,...)
	 * @param leftHandSide
	 *            could contain pattern expressions for "pattern-matching"
	 * @param rightHandSide
	 *            the result which should be evaluated if the "pattern-matching" succeeds
	 */
	public PatternMatcherAndEvaluator(final ISymbol.RuleType setSymbol, final IExpr leftHandSide,
			final IExpr rightHandSide) {
		this(setSymbol, leftHandSide, rightHandSide, true);
	}

	public PatternMatcherAndEvaluator(final ISymbol.RuleType setSymbol, final IExpr leftHandSide,
			final IExpr rightHandSide, boolean initAll) {
		super(leftHandSide, initAll);
		fSetSymbol = setSymbol;
		fRightHandSide = rightHandSide;
		if (initAll) {
			initRHSleafCountSimplify();
		}
	}

	/**
	 * Calculate the leaf count of the right-hand-side of this matcher if it's a <code>Condition()</code> or
	 * <code>Module(...,Condition()) or With(...,Condition())</code> expression.
	 * 
	 */
	private void initRHSleafCountSimplify() {
		fRHSleafCountSimplify = Long.MIN_VALUE;
		if (fRightHandSide != null) {
			if (fRightHandSide.isCondition()) {
				fRHSleafCountSimplify = fRightHandSide.second().leafCountSimplify();
			} else if (fRightHandSide.isModuleOrWithCondition()) {
				IAST condition = (IAST) fRightHandSide.second();
				fRHSleafCountSimplify = condition.second().leafCountSimplify();
			}
		}
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		PatternMatcherAndEvaluator v = (PatternMatcherAndEvaluator) super.clone();
		v.fRightHandSide = fRightHandSide;
		v.fSetSymbol = fSetSymbol;
		return v;
	}

	/**
	 * Check if the two expressions are equivalent. (i.e. <code>f[x_,y_]</code> is equivalent to <code>f[a_,b_]</code> )
	 * 
	 * @param patternExpr1
	 * @param patternExpr2
	 * @param pm1
	 * @param pm2
	 * @return
	 */
	private static int equivalentRHS(final IExpr patternExpr1, final IExpr patternExpr2, final PatternMap pm1,
			final PatternMap pm2) {
		IExpr p1, p2;
		if (patternExpr1.isCondition()) {
			p1 = patternExpr1.second();
			if (patternExpr2.isCondition()) {
				p2 = patternExpr2.second();
				if (equivalent(p1, p2, pm1, pm2)) {
					return 0;
				}
				return p1.compareTo(p2);
			} else if (patternExpr2.isModuleOrWithCondition()) {
				p2 = patternExpr2.second().second();
				if (equivalent(p1, p2, pm1, pm2)) {
					return 0;
				}
				return p1.compareTo(p2);
			}
		} else if (patternExpr1.isModuleOrWithCondition()) {
			p1 = patternExpr1.second().second();
			if (patternExpr2.isCondition()) {
				p2 = patternExpr2.second();
				if (equivalent(p1, p2, pm1, pm2)) {
					return 0;
				}
				return p1.compareTo(p2);
			} else if (patternExpr2.isModuleOrWithCondition()) {
				p2 = patternExpr2.second().second();
				if (equivalent(p1, p2, pm1, pm2)) {
					return 0;
				}
				return p1.compareTo(p2);
			}
		}
		return 0;
	}

	/**
	 * Check if the condition for the right-hand-sides <code>Module[], With[] or Condition[]</code> expressions
	 * evaluates to <code>true</code>.
	 * 
	 * @return <code>true</code> if the right-hand-sides condition is fulfilled.
	 */
	@Override
	public boolean checkRHSCondition(EvalEngine engine) {
		PatternMap patternMap = getPatternMap();
		if (patternMap.getRHSEvaluated()) {
			return true;
		}
		boolean matched = false;
		if (!(fRightHandSide.isModuleOrWith() || fRightHandSide.isCondition())) {
			matched = true;
		} else {
			if (!patternMap.isAllPatternsAssigned()) {
				matched = true;
			} else {
				IExpr substConditon = patternMap.substituteSymbols(fRightHandSide);
				if (substConditon.isCondition()) {
					matched = Programming.checkCondition(substConditon.first(), substConditon.second(), engine);
				} else if (substConditon.isModuleOrWith()) {
					matched = Programming.checkModuleOrWithCondition(substConditon.first(), substConditon.second(),
							engine);
				}
			}
		}
		patternMap.setRHSEvaluated(matched);
		return matched;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr eval(final IExpr leftHandSide, @Nonnull EvalEngine engine) {
		return replace(leftHandSide, engine, true);
	}

	final public IExpr replace(final IExpr leftHandSide, @Nonnull EvalEngine engine, boolean evaluate) {
		PatternMap patternMap = null;
		if (isRuleWithoutPatterns()) {
			// no patterns found match equally:
			if (fLhsPatternExpr.equals(leftHandSide)) {
				IExpr result = fRightHandSide;
				try {
					if (evaluate) {
						return F.eval(result);
					}
					return result;
				} catch (final ConditionException e) {
					logConditionFalse(leftHandSide, fLhsPatternExpr, fRightHandSide);
					return F.NIL;
				} catch (final ReturnException e) {
					return e.getValue();
				}
			}
			if (!(fLhsPatternExpr.isOrderlessAST() && leftHandSide.isOrderlessAST())) {
				if (!(fLhsPatternExpr.isFlatAST() && leftHandSide.isFlatAST())) {
					return F.NIL;
				}
				// replaceSubExpressionOrderlessFlat() below implements equals matching for
				// special cases, if the AST is Orderless or Flat
			}
			if (fLhsPatternExpr.size() == leftHandSide.size()) {
				return F.NIL;
			}
		} else {
			patternMap = getPatternMap();
			patternMap.initPattern();
			if (matchExpr(fLhsPatternExpr, leftHandSide, engine, new StackMatcher(engine), true)) {

				if (RulesData.showSteps) {
					if (fLhsPatternExpr.head().equals(F.Integrate)) {
						IExpr rhs = fRightHandSide;
						if (!rhs.isPresent()) {
							rhs = F.Null;
						}
						System.out.println("\nCOMPLEX: " + fLhsPatternExpr.toString() + " := " + rhs.toString());
						System.out.println("\n>>>>> " + toString());
					}
				}

				IExpr result = patternMap.substituteSymbols(fRightHandSide);
				try {
					// System.out.println(result.toString());
					if (evaluate) {
						result = F.eval(result);
					}
					return result;
				} catch (final ConditionException e) {
					logConditionFalse(leftHandSide, fLhsPatternExpr, fRightHandSide);
					return F.NIL;
				} catch (final ReturnException e) {
					return e.getValue();
				}

			}
		}

		if (fLhsPatternExpr.isAST() && leftHandSide.isAST()) {
			patternMap = getPatternMap();
			patternMap.initPattern();
			return replaceSubExpressionOrderlessFlat((IAST) fLhsPatternExpr, (IAST) leftHandSide, fRightHandSide,
					engine);
		}
		return F.NIL;
	}

	@Override
	public IExpr getRHS() {
		return ExprUtil.ofNullable(fRightHandSide);
	}

	/**
	 * Get the leaf count of the right-hand-side of this matcher if it's a <code>Condition()</code> or
	 * <code>Module(...,Condition()) or With(...,Condition())</code> expression.
	 * 
	 * @return the leaf count
	 */
	@Override
	public long getRHSleafCountSimplify() {
		return fRHSleafCountSimplify;
	}

	public IAST getAsAST() {
		ISymbol setSymbol = getSetSymbol();
		IExpr condition = getCondition();
		if (condition != null) {
			return F.binaryAST2(setSymbol, getLHS(), F.Condition(getRHS(), condition));
		}
		return F.binaryAST2(setSymbol, getLHS(), getRHS());
	}

	/**
	 * Return <code>Set</code> or <code>SetDelayed</code> symbol.
	 * 
	 * @return <code>null</code> if no symbol was defined
	 */
	public ISymbol getSetSymbol() {
		if (fSetSymbol == ISymbol.RuleType.SET_DELAYED) {
			return F.SetDelayed;
		}
		if (fSetSymbol == ISymbol.RuleType.SET) {
			return F.Set;
		}
		if (fSetSymbol == ISymbol.RuleType.UPSET_DELAYED) {
			return F.UpSetDelayed;
		}
		if (fSetSymbol == ISymbol.RuleType.UPSET) {
			return F.UpSet;
		}
		return null;
	}

	@Override
	int equivalent(final IPatternMatcher obj) {
		// don't compare fSetSymbol here
		int comp = super.equivalent(obj);
		if (comp == 0) {
			if (obj instanceof PatternMatcherAndEvaluator) {
				// if (fLhsPatternExpr.equals(obj.fLhsPatternExpr)) {
				// return 0;
				// }
				PatternMatcherAndEvaluator pm = (PatternMatcherAndEvaluator) obj;
				if (fRightHandSide != null && pm.fRightHandSide != null) {
					if (fRightHandSide.isCondition() || fRightHandSide.isModuleOrWithCondition()) {
						if (pm.fRightHandSide.isCondition() || pm.fRightHandSide.isModuleOrWithCondition()) {
							if (getRHSleafCountSimplify() < pm.getRHSleafCountSimplify()) {
								return -1;
							}
							if (getRHSleafCountSimplify() > pm.getRHSleafCountSimplify()) {
								return 1;
							}
							return equivalentRHS(fRightHandSide, pm.fRightHandSide, getPatternMap(),
									pm.getPatternMap());
						}
						return 1;
					} else if (pm.fRightHandSide.isModuleOrWithCondition() || pm.fRightHandSide.isCondition()) {
						return -1;
					}
				}
			}
		}
		return comp;
	}

	@Override
	public String toString() {
		return getAsAST().toString();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		short ordinal = (short) fSetSymbol.ordinal();
		if (fPatternCondition == null) {
			ordinal |= 0x8000;
		}
		objectOutput.writeShort(ordinal);
		objectOutput.writeObject(fLhsPatternExpr);
		objectOutput.writeObject(fRightHandSide);
		if (fPatternCondition != null) {
			objectOutput.writeObject(fPatternCondition);
		}
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		short ordinal = objectInput.readShort();
		fSetSymbol = RuleType.values()[ordinal & 0x7FFF];
		fLhsPatternExpr = (IExpr) objectInput.readObject();
		fRightHandSide = (IExpr) objectInput.readObject();
		if ((ordinal & 0x8000) == 0x0000) {
			fPatternCondition = (IExpr) objectInput.readObject();
		}
		this.fPatternMap = new PatternMap();
		if (fLhsPatternExpr != null) {
			fLHSPriority = fPatternMap.determinePatterns(fLhsPatternExpr);
		}
		initRHSleafCountSimplify();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((fRightHandSide == null) ? 0 : fRightHandSide.hashCode());
		result = prime * result + ((fSetSymbol == null) ? 0 : fSetSymbol.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PatternMatcherAndEvaluator other = (PatternMatcherAndEvaluator) obj;
		if (fRightHandSide == null) {
			if (other.fRightHandSide != null) {
				return false;
			}
		} else if (!fRightHandSide.equals(other.fRightHandSide)) {
			return false;
		}
		return fSetSymbol == other.fSetSymbol;
	}
}