package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorExpr;
import org.matheclipse.parser.client.SyntaxError;

/**
 * 
 */
public class Together extends AbstractFunctionEvaluator {
	/**
	 * 
	 */
	static class TogetherVisitor extends VisitorExpr {
		/**
		 * 
		 */
		public TogetherVisitor() {
			super();
		}

		@Override
		public IExpr visit(IAST ast) {
			IExpr temp = visitAST(ast);
			IAST astTemp = ast;
			if (temp != null) {
				if (temp.isAST()) {
					astTemp = (IAST) temp;
				} else {
					return temp;
				}
			}
			if (astTemp.isPlus()) {
				return visitPlus(astTemp);
			} else if (astTemp.isTimes() || astTemp.isPower()) {
				try {
					return Cancel.cancelPowerTimes(astTemp);
				} catch (JASConversionException jce) {
					if (Config.DEBUG) {
						jce.printStackTrace();
					}
				}
			}

			return astTemp;
		}

		private IExpr visitPlus(IAST plusAST) {
			if (plusAST.size() <= 1) {
				return plusAST;
			}
			IAST ni;
			IExpr temp;
			IAST numer = F.ast(F.Plus, plusAST.size(), false);
			IAST denom = F.ast(F.Times, plusAST.size(), false);
			for (int i = 1; i < plusAST.size(); i++) {
				numer.add(i, F.eval(F.Numerator(plusAST.get(i))));
				denom.add(i, F.eval(F.Denominator(plusAST.get(i))));
			}

			for (int i = 1; i < plusAST.size(); i++) {
				ni = F.Times(numer.get(i));
				for (int j = 1; j < plusAST.size(); j++) {
					if (i == j) {
						continue;
					}
					temp = denom.get(j);
					if (!temp.equals(F.C1)) {
						ni.add(temp);
					}
				}
				numer.set(i, ni);
			}
			int i = 1;
			while (denom.size() > i) {
				if (denom.get(i).equals(F.C1)) {
					denom.remove(i);
					continue;
				}
				i++;
			}
			// System.out.println(numer.toString());
			IExpr exprNumerator = F.evalExpandAll(numer);
			if (denom.size() == 1) {
				return exprNumerator;
			}
			IExpr exprDenominator = F.evalExpandAll(denom);
			if (!exprDenominator.equals(F.C1)) {
				try {
					IExpr[] result = Cancel.cancelGCD(exprNumerator, exprDenominator);
					if (result != null) {
						return F.Times(result[0], result[1], F.Power(result[2], F.CN1));
					}
				} catch (JASConversionException jce) {
					if (Config.DEBUG) {
						jce.printStackTrace();
					}
				}
			}
			return F.Times(exprNumerator, F.Power(exprDenominator, F.CN1));
		}
	}

	private static final TogetherVisitor VISITOR = new TogetherVisitor();

	public Together() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		if (ast.get(1).isAST()) {
			IExpr expr = together((IAST) ast.get(1));
			if (expr != null) {
				return expr;
			}
		}
		return ast.get(1);
	}

	public static IExpr together(IAST ast) {
		return F.eval(ast.accept(VISITOR));
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}
}