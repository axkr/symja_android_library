package org.matheclipse.core.patternmatching;

import java.util.List;

import org.matheclipse.core.eval.util.OpenIntToList;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.BinaryFunctorImpl;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.visit.HashValueVisitor;

/**
 * Match two arguments of an <code>Orderless</code> AST into a new resulting expression.
 * 
 * @see HashedPatternRules
 */
public class HashedOrderlessMatcher {
	private OpenIntToList<AbstractHashedPatternRules> fHashRuleMap;
	private OpenIntToList<AbstractHashedPatternRules> fPatternHashRuleMap;

	public HashedOrderlessMatcher() {
		this.fHashRuleMap = new OpenIntToList<AbstractHashedPatternRules>();
		this.fPatternHashRuleMap = new OpenIntToList<AbstractHashedPatternRules>();
	}

	/**
	 * Define the rule for the <code>Orderless</code> operator <b>OP</b>.
	 * <code>OP[lhs1, lhs2, ....] := OP[rhs, ...]</code>
	 * 
	 * @param lhs1
	 * @param lhs2
	 * @param rhs
	 */
	public void defineHashRule(final IExpr lhs1, final IExpr lhs2, final IExpr rhs) {
		defineHashRule(lhs1, lhs2, rhs, null);
	}

	/**
	 * Define the rule for the <code>Orderless</code> operator <b>OP</b>.
	 * <code>OP[lhs1, lhs2, ....] := OP[rhs, ...] /; condition</code>
	 * 
	 * @param lhs1
	 * @param lhs2
	 * @param rhs
	 * @param condition
	 */
	public void defineHashRule(final IExpr lhs1, final IExpr lhs2, final IExpr rhs, final IExpr condition) {
		AbstractHashedPatternRules hashRule = new HashedPatternRules(lhs1, lhs2, rhs, condition, true);
		fHashRuleMap.put(hashRule.hashCode(), hashRule);
	}

	public void defineHashRule(final IExpr lhs1, final IExpr lhs2, final BinaryFunctorImpl<IExpr> function) {
		AbstractHashedPatternRules hashRule = new HashedPatternFunction(lhs1, lhs2, function, true);
		fHashRuleMap.put(hashRule.hashCode(), hashRule);
	}

	/**
	 * Define the rule for the <code>Orderless</code> operator <b>OP</b>.
	 * <code>OP[lhs1, lhs2, ....] := OP[rhs, ...] /; condition</code>
	 * 
	 * @param lhs1
	 * @param lhs2
	 * @param rhs
	 */
	public void definePatternHashRule(final IExpr lhs1, final IExpr lhs2, final IExpr rhs) {
		definePatternHashRule(lhs1, lhs2, rhs, null);
	}

	/**
	 * Define the rule for the <code>Orderless</code> operator <b>OP</b>.
	 * <code>OP[lhs1, lhs2, ....] := OP[rhs, ...] /; condition</code>
	 * 
	 * @param lhs1
	 * @param lhs2
	 * @param rhs
	 * @param condition
	 */
	public void definePatternHashRule(final IExpr lhs1, final IExpr lhs2, final IExpr rhs, final IExpr condition) {
		AbstractHashedPatternRules hashRule = new HashedPatternRules(lhs1, lhs2, rhs, condition, false);
		fPatternHashRuleMap.put(hashRule.hashCode(), hashRule);
	}

	/**
	 * Evaluate an <code>Orderless</code> AST with the defined <code>HashedPatternRules</code> as long as the header of
	 * the given expression equals the evaluated expression.
	 * 
	 * @param orderlessAST
	 * @return
	 * @see HashedPatternRules
	 */
	public IAST evaluateRepeated(final IAST orderlessAST) {
		if (orderlessAST.isEvalFlagOn(IAST.IS_HASH_EVALED)) {
			return F.NIL;
		}

		if (exists2ASTArguments(orderlessAST)) {
			IAST temp = orderlessAST;
			boolean evaled = false;
			int[] hashValues;
			if (!fPatternHashRuleMap.isEmpty()) {
				IExpr head = orderlessAST.head();
				IAST result = F.NIL;
				while (temp.isPresent()) {
					int size = temp.size() - 1;
					hashValues = new int[size];
					for (int i = 0; i < size; i++) {
						hashValues[i] = temp.get(i + 1).accept(HashValueVisitor.HASH_VALUE_VISITOR);
					}

					result = evaluateHashedValues(temp, fPatternHashRuleMap, hashValues);
					if (result.isPresent()) {
						temp = result;
						evaled = true;
						if (!temp.head().equals(head)) {
							return setIsHashEvaledFlag(temp);
						}
					} else {
						break;
					}
				}
			}
			if (!fHashRuleMap.isEmpty()) {
				int size = temp.size() - 1;
				hashValues = new int[size];
				for (int i = 0; i < size; i++) {
					hashValues[i] = temp.get(i + 1).head().hashCode();
				}
				IAST result = evaluateHashedValues(temp, fHashRuleMap, hashValues);
				if (result.isPresent()) {
					return setIsHashEvaledFlag(result);
				}
			}
			if (evaled) {
				return setIsHashEvaledFlag(temp);
			}
		}
		orderlessAST.setEvalFlags(IAST.IS_HASH_EVALED);
		return F.NIL;
	}

	/**
	 * Set the <code>IAST.IS_HASH_EVALED</code> for the given <code>ast</code>. And return the <code>ast</code>
	 * 
	 * @param ast
	 * @return
	 */
	private static IAST setIsHashEvaledFlag(IAST ast) {
		ast.setEvalFlags(IAST.IS_HASH_EVALED);
		return ast;
	}

	/**
	 * Check if there are at least two IAST arguments available in <code>ast</code>.
	 * 
	 * @param ast
	 * @return
	 */
	private static boolean exists2ASTArguments(IAST ast) {
		final int[] counter = { 0 };
		return ast.exists(x -> x.isAST() && ++counter[0] == 2, 1);
	}

	/**
	 * Evaluate an <code>Orderless</code> AST only once with the defined <code>HashedPatternRules</code>.
	 * 
	 * @param orderlessAST
	 * @return
	 * @see HashedPatternRules
	 */
	public IAST evaluate(final IAST orderlessAST) {
		// TODO Performance hotspot

		// Compute hash values for each argument of the orderlessAST
		// if hashValues[i]==0 then the corresponding argument is evaluated and not available anymore
		int[] hashValues = new int[(orderlessAST.size() - 1)];
		if (!fPatternHashRuleMap.isEmpty()) {
			for (int i = 0; i < hashValues.length; i++) {
				hashValues[i] = orderlessAST.get(i + 1).accept(HashValueVisitor.HASH_VALUE_VISITOR);
			}
			IAST result = evaluateHashedValues(orderlessAST, fPatternHashRuleMap, hashValues);
			if (result.isPresent()) {
				return result;
			}
		}
		if (!fHashRuleMap.isEmpty()) {
			for (int i = 0; i < hashValues.length; i++) {
				hashValues[i] = orderlessAST.get(i + 1).head().hashCode();
			}
			return evaluateHashedValues(orderlessAST, fHashRuleMap, hashValues);
		}
		return F.NIL;
	}

	private static IAST evaluateHashedValues(final IAST orderlessAST,
			OpenIntToList<AbstractHashedPatternRules> hashRuleMap, int[] hashValues) {
		boolean evaled = false;
		IAST result = orderlessAST.copyHead();
		for (int i = 0; i < hashValues.length - 1; i++) {
			if (hashValues[i] == 0) {
				// already used entry
				continue;
			}
			evaled: for (int j = i + 1; j < hashValues.length; j++) {
				if (hashValues[j] == 0) {
					// already used entry
					continue;
				}
				final List<AbstractHashedPatternRules> hashRuleList = hashRuleMap
						.get(AbstractHashedPatternRules.calculateHashcode(hashValues[i], hashValues[j]));
				if (hashRuleList != null) {
					for (AbstractHashedPatternRules hashRule : hashRuleList) {

						if (!hashRule.isPattern1() && !hashRule.isPattern2()) {
							if (hashValues[i] != hashRule.getHash1() || hashValues[j] != hashRule.getHash2()) {
								if (hashValues[i] != hashRule.getHash2() || hashValues[j] != hashRule.getHash1()) {
									// hash code of both entries aren't matching
									continue;
								}

								if (updateHashValues(result, orderlessAST, hashRule, hashValues, j, i)) {
									evaled = true;
									break evaled;
								}
								continue;
							}

							if (updateHashValues(result, orderlessAST, hashRule, hashValues, i, j)) {
								evaled = true;
								break evaled;
							}

							if (hashValues[i] != hashRule.getHash2() || hashValues[j] != hashRule.getHash1()) {
								// hash code of both entries aren't matching
								continue;
							}

							if (updateHashValues(result, orderlessAST, hashRule, hashValues, j, i)) {
								evaled = true;
								break evaled;
							}
							continue;

						}

						if (updateHashValues(result, orderlessAST, hashRule, hashValues, i, j)) {
							evaled = true;
							break evaled;
						}

						if (updateHashValues(result, orderlessAST, hashRule, hashValues, j, i)) {
							evaled = true;
							break evaled;
						}
					}
				}
			}
		}

		if (evaled) {
			// append the rest of the unevaluated arguments
			for (int i = 0; i < hashValues.length; i++) {
				if (hashValues[i] != 0) {
					result.append(orderlessAST.get(i + 1));
				}
			}
			return result;
		}
		return F.NIL;
	}

	private static boolean updateHashValues(IAST result, final IAST orderlessAST, AbstractHashedPatternRules hashRule,
			int[] hashValues, int i, int j) {
		IExpr temp;
		if ((temp = hashRule.evalDownRule(orderlessAST.get(i + 1), orderlessAST.get(j + 1))).isPresent()) {
			hashValues[i] = 0;
			hashValues[j] = 0;
			result.append(temp);
			return true;
		}
		return false;
	}
}
