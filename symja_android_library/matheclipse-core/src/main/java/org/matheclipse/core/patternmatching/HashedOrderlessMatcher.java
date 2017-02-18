package org.matheclipse.core.patternmatching;

import java.util.List;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.OpenIntToList;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.BinaryFunctorImpl;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.visit.HashValueVisitor;

/**
 * Match two arguments of an <code>Orderless</code> AST into a new resulting
 * expression.
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
	 * <code>OP[lhs1Str, lhs2Str, ....] := OP[rhsStr, ...]</code>
	 * 
	 * @param lhs1Str
	 * @param lhs2Str
	 * @param rhsStr
	 */
	public void defineHashRule(final String lhs1Str, final String lhs2Str, final String rhsStr) {
		defineHashRule(lhs1Str, lhs2Str, rhsStr, null);
	}

	public void defineHashRule(final String lhs1Str, final String lhs2Str, final BinaryFunctorImpl<IExpr> function) {
		ExprParser parser = new ExprParser(EvalEngine.get());
		IExpr lhs1 = parser.parse(lhs1Str);
		// final Parser parser = new Parser();
		// ASTNode parsedAST = parser.parse(lhs1Str);
		// final IExpr lhs1 = AST2Expr.CONST.convert(parsedAST);
		IExpr lhs2 = parser.parse(lhs2Str);
		// parsedAST = parser.parse(lhs2Str);
		// final IExpr lhs2 = AST2Expr.CONST.convert(parsedAST);
		defineHashRule(lhs1, lhs2, function);
	}

	public void defineHashRule(final String lhs1Str, final String lhs2Str, final String rhsStr,
			final String conditionStr) {
		ExprParser parser = new ExprParser(EvalEngine.get());
		IExpr lhs1 = parser.parse(lhs1Str);
		// final Parser parser = new Parser();
		// ASTNode parsedAST = parser.parse(lhs1Str);
		// final IExpr lhs1 = AST2Expr.CONST.convert(parsedAST);
		IExpr lhs2 = parser.parse(lhs2Str);
		// parsedAST = parser.parse(lhs2Str);
		// final IExpr lhs2 = AST2Expr.CONST.convert(parsedAST);
		IExpr rhs = parser.parse(rhsStr);
		// parsedAST = parser.parse(rhsStr);
		// final IExpr rhs = AST2Expr.CONST.convert(parsedAST);
		IExpr condition = null;
		if (conditionStr != null) {
			condition = parser.parse(conditionStr);
			// parsedAST = parser.parse(conditionStr);
			// condition = AST2Expr.CONST.convert(parsedAST);
		}
		defineHashRule(lhs1, lhs2, rhs, condition);
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
	 * <code>OP[lhs1Str, rhsStr, ....] := OP[rhsStr, ...] /; condition</code>
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
	 * <code>OP[lhs1Str, rhsStr, ....] := OP[rhsStr, ...] /; condition</code>
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
	 * Evaluate an <code>Orderless</code> AST with the defined
	 * <code>HashedPatternRules</code> as long as the header of the given
	 * expression equals the evaluated expression.
	 * 
	 * @param orderlessAST
	 * @return
	 * @see HashedPatternRules
	 */
	public IAST evaluateRepeated(final IAST orderlessAST) {
		if (orderlessAST.isEvalFlagOn(IAST.IS_HASH_EVALED)) {
			return F.NIL;
		}
		IAST temp = orderlessAST;
		if (checkMinimmumASTs(temp)) {
			boolean evaled = false;
			int[] hashValues;
			if (!fPatternHashRuleMap.isEmpty()) {
				IExpr head = orderlessAST.head();
				IAST result = F.NIL;
				while (temp.isPresent()) {

					hashValues = new int[(temp.size() - 1)];
					HashValueVisitor v = new HashValueVisitor();
					for (int i = 0; i < hashValues.length; i++) {
						hashValues[i] = temp.get(i + 1).accept(v);
						v.setUp();
					}

					result = evaluateHashedValues(temp, fPatternHashRuleMap, hashValues);
					if (result.isPresent()) {
						temp = result;
						evaled = true;
						if (!temp.head().equals(head)) {
							temp.setEvalFlags(IAST.IS_HASH_EVALED);
							return temp;
						}
					} else {
						break;
					}
				}
			}
			if (!fHashRuleMap.isEmpty()) {
				hashValues = new int[(temp.size() - 1)];
				for (int i = 0; i < hashValues.length; i++) {
					hashValues[i] = temp.get(i + 1).head().hashCode();
				}
				IAST result = evaluateHashedValues(temp, fHashRuleMap, hashValues);
				if (result.isPresent()) {
					result.setEvalFlags(IAST.IS_HASH_EVALED);
					return result;
				}
			}
			if (evaled) {
				temp.setEvalFlags(IAST.IS_HASH_EVALED);
				return temp;
			}
		}
		orderlessAST.setEvalFlags(IAST.IS_HASH_EVALED);
		return F.NIL;
	}

	/**
	 * Check if there are at least two IAST arguments available.
	 * 
	 * @param ast
	 * @return
	 */
	private static boolean checkMinimmumASTs(IAST ast) {
		int counter = 0;
		int size = ast.size();
		for (int i = 1; i < size; i++) {
			if (ast.get(i).isAST() && ++counter == 2) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Evaluate an <code>Orderless</code> AST only once with the defined
	 * <code>HashedPatternRules</code>.
	 * 
	 * @param orderlessAST
	 * @return
	 * @see HashedPatternRules
	 */
	public IAST evaluate(final IAST orderlessAST) {
		// TODO Performance hotspot
		int[] hashValues = new int[(orderlessAST.size() - 1)];
		if (!fPatternHashRuleMap.isEmpty()) {
			HashValueVisitor v = new HashValueVisitor();
			for (int i = 0; i < hashValues.length; i++) {
				hashValues[i] = orderlessAST.get(i + 1).accept(v);
				v.setUp();
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
