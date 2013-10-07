package org.matheclipse.core.patternmatching;

import java.util.List;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.visit.HashValueVisitor;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;

import com.google.common.collect.ArrayListMultimap;

/**
 * Match two arguments of an <code>Orderless</code> AST into a new resulting expression.
 * 
 * @see HashedPatternRules
 */
public class HashedOrderlessMatcher {
	ArrayListMultimap<Integer, HashedPatternRules> hashRuleMap;
	private final boolean fDefaultHashCode;

	public HashedOrderlessMatcher(boolean defaultHashCode) {
		this.fDefaultHashCode = defaultHashCode;
		this.hashRuleMap = ArrayListMultimap.create();
	}

	/**
	 * Define the rule for the <code>Orderless</code> operator <b>OP</b>. <code>OP[lhs1Str, rhsStr, ....] := OP[rhsStr, ...]</code>
	 * 
	 * @param lhs1Str
	 * @param lhs2Str
	 * @param rhsStr
	 * @throws SyntaxError
	 */
	public void setUpHashRule(final String lhs1Str, final String lhs2Str, final String rhsStr) throws SyntaxError {
		setUpHashRule(lhs1Str, lhs2Str, rhsStr, null);
	}

	public void setUpHashRule(final String lhs1Str, final String lhs2Str, final String rhsStr, final String conditionStr)
			throws SyntaxError {
		final Parser parser = new Parser();
		ASTNode parsedAST = parser.parse(lhs1Str);
		final IExpr lhs1 = AST2Expr.CONST.convert(parsedAST);
		parsedAST = parser.parse(lhs2Str);
		final IExpr lhs2 = AST2Expr.CONST.convert(parsedAST);
		parsedAST = parser.parse(rhsStr);
		final IExpr rhs = AST2Expr.CONST.convert(parsedAST);
		IExpr condition = null;
		if (conditionStr != null) {
			parsedAST = parser.parse(conditionStr);
			condition = AST2Expr.CONST.convert(parsedAST);
		}
		setUpHashRule(lhs1, lhs2, rhs, condition);
	}

	/**
	 * Define the rule for the <code>Orderless</code> operator <b>OP</b>.
	 * <code>OP[lhs1Str, rhsStr, ....] := OP[rhsStr, ...] /; condition</code>
	 * 
	 * @param lhs1
	 * @param lhs2
	 * @param rhs
	 */
	public void setUpHashRule(final IExpr lhs1, final IExpr lhs2, final IExpr rhs) {
		setUpHashRule(lhs1, lhs2, rhs, null);
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
	public void setUpHashRule(final IExpr lhs1, final IExpr lhs2, final IExpr rhs, final IExpr condition) {
		HashedPatternRules hashRule = new HashedPatternRules(lhs1, lhs2, rhs, condition, fDefaultHashCode);
		hashRuleMap.put(hashRule.getHash1(), hashRule);
	}

	/**
	 * Evaluate an <code>Orderless</code> AST with the defined <code>HashedPatternRules</code>.
	 * 
	 * @param orderlessAST
	 * @return
	 * @see HashedPatternRules
	 */
	public IAST evaluate(final IAST orderlessAST) {
		int[] hashValues = new int[(orderlessAST.size() - 1)];
		if (fDefaultHashCode) {
			for (int i = 0; i < hashValues.length; i++) {
				hashValues[i] = orderlessAST.get(i + 1).head().hashCode();
			}
		} else {
			HashValueVisitor v = new HashValueVisitor();
			for (int i = 0; i < hashValues.length; i++) {
				hashValues[i] = orderlessAST.get(i + 1).accept(v);
				v.setUp();
			}
		}
		return evaluateHashedValues(orderlessAST, hashValues);
	}

	private IAST evaluateHashedValues(final IAST orderlessAST, int[] hashValues) {
		boolean evaled = false;
		IAST result = null;
		IExpr temp;
		for (int i = 0; i < hashValues.length; i++) {
			if (hashValues[i] == 0) {
				// already used entry
				continue;
			}
			final List<HashedPatternRules> hashRuleList = hashRuleMap.get(hashValues[i]);
			if (hashRuleList != null) {
				evaled: for (HashedPatternRules hashRule : hashRuleList) {

					for (int j = 0; j < hashValues.length; j++) {
						if (!hashRule.isPattern2()) {
							if (hashValues[j] != hashRule.getHash2() || j == i) {
								// hash code of both entries aren't matching
								continue;
							}
						}
						DownRulesData rulesData = hashRule.getRulesData();
						if ((temp = rulesData.evalDownRule(F.List(orderlessAST.get(i + 1), orderlessAST.get(j + 1)))) != null) {
							hashValues[i] = 0;
							hashValues[j] = 0;
							if (!evaled) {
								result = orderlessAST.copyHead();
								evaled = true;
							}
							result.add(temp);
							break evaled;
						}
					}
				}
			}
		}

		if (evaled) {
			// append rest of unevaluated arguments
			for (int i = 0; i < hashValues.length; i++) {
				if (hashValues[i] != 0) {
					result.add(orderlessAST.get(i + 1));
				}
			}
		}
		return result;
	}
}
