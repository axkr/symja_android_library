package org.matheclipse.core.reflection.system;

import java.util.List;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

import edu.jas.arith.BigRational;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.root.ComplexRootsAbstract;
import edu.jas.root.ComplexRootsSturm;
import edu.jas.root.InvalidBoundaryException;
import edu.jas.root.Rectangle;
import edu.jas.ufd.Squarefree;
import edu.jas.ufd.SquarefreeFactory;

/**
 * Determine complex root intervals of a univariate polynomial
 * 
 */
public class RootIntervals extends AbstractFunctionEvaluator {

	public RootIntervals() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		return croots(ast.arg1(), false);
	}

	/**
	 * Complex numeric roots intervals.
	 * 
	 * @param ast
	 * @return
	 */
	public static IAST croots(final IExpr arg, boolean numeric) {

		try {
			VariablesSet eVar = new VariablesSet(arg);
			if (!eVar.isSize(1)) {
				// only possible for univariate polynomials
				return null;
			}
			IExpr expr = F.evalExpandAll(arg);
			ASTRange r = new ASTRange(eVar.getVarList(), 1);
			List<IExpr> varList = r.toList();

			ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(new BigRational(1));
			ComplexRootsAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);

			JASConvert<Complex<BigRational>> jas = new JASConvert<Complex<BigRational>>(varList, cfac);
			GenPolynomial<Complex<BigRational>> poly = jas.numericExpr2JAS(expr);

			Squarefree<Complex<BigRational>> engine = SquarefreeFactory.<Complex<BigRational>> getImplementation(cfac);
			poly = engine.squarefreePart(poly);

			List<Rectangle<BigRational>> roots = cr.complexRoots(poly);

			BigRational len = new BigRational(1, 100000L);

			IAST resultList = F.List();

			if (numeric) {
				for (Rectangle<BigRational> root : roots) {
					Rectangle<BigRational> refine = cr.complexRootRefinement(root, poly, len);
					resultList.add(JASConvert.jas2Numeric(refine.getCenter(), Config.DEFAULT_ROOTS_CHOP_DELTA));
				}
			} else {
				IAST rectangleList;
				for (Rectangle<BigRational> root : roots) {
					rectangleList = F.List();

					Rectangle<BigRational> refine = cr.complexRootRefinement(root, poly, len);
					rectangleList.add(JASConvert.jas2Complex(refine.getNW()));
					rectangleList.add(JASConvert.jas2Complex(refine.getSW()));
					rectangleList.add(JASConvert.jas2Complex(refine.getSE()));
					rectangleList.add(JASConvert.jas2Complex(refine.getNE()));
					resultList.add(rectangleList);
					// System.out.println("refine = " + refine);

				}
			}
			return resultList;
		} catch (InvalidBoundaryException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		} catch (JASConversionException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}
		return null;
	}

}