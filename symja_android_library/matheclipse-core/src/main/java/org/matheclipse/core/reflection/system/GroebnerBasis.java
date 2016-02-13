package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.gbufd.GroebnerBasePartial;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.OptimizedPolynomialList;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderByName;

public class GroebnerBasis extends AbstractFunctionEvaluator {

	public GroebnerBasis() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() >= 3) {
			try {
				if (ast.arg1().isVector() < 0) {
					return F.NIL;
				}
				if (ast.arg2().isVector() < 0) {
					return F.NIL;
				}
				TermOrder termOrder = TermOrderByName.Lexicographic;
				if (ast.size() > 3) {
					final Options options = new Options(ast.topHead(), ast, ast.size() - 1, engine);
					termOrder = options.getMonomialOrder(ast, termOrder);
				}
				if (ast.size() >= 3) {
					IAST vars = (IAST) ast.arg2();
					if (vars.size() <= 1) {
						return F.NIL;
					}
					List<ISymbol> varList = new ArrayList<ISymbol>(vars.size() - 1);
					String[] pvars = new String[vars.size() - 1];
				 
					for (int i = 1; i < vars.size(); i++) {
						if (!vars.get(i).isSymbol()) {
							return F.NIL;
						}
						varList.add((ISymbol) vars.get(i));
						pvars[i - 1] = ((ISymbol) vars.get(i)).toString();
					}
					 
					GroebnerBasePartial<BigRational> gbp = new GroebnerBasePartial<BigRational>();
					IAST polys = (IAST) ast.arg1();
					List<GenPolynomial<BigRational>> polyList = new ArrayList<GenPolynomial<BigRational>>(
							polys.size() - 1);
					JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO, termOrder);
					for (int i = 1; i < polys.size(); i++) {
						IExpr expr = F.evalExpandAll(polys.get(i));
						GenPolynomial<BigRational> poly = jas.expr2JAS(expr, false);
						polyList.add(poly);
					}

					OptimizedPolynomialList<BigRational> opl = gbp.partialGB(polyList, pvars);
					// System.out.println(opl);

					IAST resultList = F.List();
					for (GenPolynomial<BigRational> p : opl.list) {
						// convert rational to integer coefficients and add
						// polynomial to result list
						resultList.add(jas.integerPoly2Expr((GenPolynomial<BigInteger>) jas.factorTerms(p)[2]));
					}
					return resultList;
				}
			} catch (JASConversionException e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}
		}
		return F.NIL;
	}

}