package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Graphics;
import static org.matheclipse.core.expression.F.Line;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.N;
import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.Show;

import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Plots x/y functions
 *
 */
public class Plot extends AbstractEvaluator {
	/**
	 * Constructor for the singleton
	 */
	public final static Plot CONST = new Plot();

	private final static int N = 100;

	public Plot() {
	}

	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if ((ast.size() >= 3) && (ast.size() <= 4) && ast.get(2).isList()) {
			final IAST lst = (IAST) ast.get(2);
			if (lst.size() == 4) {
				final IExpr a = engine.evaluate(N(lst.get(2)));
				final IExpr b = engine.evaluate(N(lst.get(3)));
				if ((!(a instanceof INum)) || (!(b instanceof INum))) {
					return F.UNEVALED;
				}
				final double ad = ((INum) a).getRealPart();
				final double bd = ((INum) b).getRealPart();
				if (bd <= ad) {
					return F.UNEVALED;
				}
				double y0d = 0.0f;
				double y1d = 0.0f;

				if ((ast.size() == 4) && ast.get(3).isList()) {
					final IAST lsty = (IAST) ast.get(3);
					if (lsty.size() == 3) {
						final IExpr y0 = engine.evaluate(N(lsty.get(1)));
						final IExpr y1 = engine.evaluate(N(lsty.get(2)));
						if ((y0 instanceof INum) && (y1 instanceof INum)) {
							y0d = ((INum) y0).getRealPart();
							y1d = ((INum) y1).getRealPart();
						}
					}
				}
				final IAST graphics = Graphics();
				IAST line = Line();
				IExpr temp;

				if (ast.get(1).isList()) {
					final IAST list = (IAST) ast.get(1);
					final IAST primitives = List();
					for (int i = 1; i < list.size(); i++) {
						temp = plotLine(ad, bd, y0d, y1d, list.get(2), (ISymbol) lst.get(1), engine);

						if (temp != null) {
							line.add(temp);
							primitives.add(line);
						}
						if (i < list.size() - 1) {
							line = Line();
						}
					}
					graphics.add(primitives);

				} else {
					temp = plotLine(ad, bd, y0d, y1d, ast.get(1), (ISymbol) lst.get(1), engine);
					if (temp != null) {
						line.add(temp);
						graphics.add(line);
					}
				}
				final IExpr options[] = { Rule(F.PlotRange, F.Automatic), Rule(F.AxesStyle, F.Automatic),
						Rule(F.AxesOrigin, List(F.C0, F.C0)), Rule(F.Axes, F.True), Rule(F.Background, F.White) };
				graphics.add(F.ast(options, F.List));
				return Show(graphics);
			}
		}
		return F.Null;
	}

	public IExpr plotLine(final double ad, final double bd, final double y0d, final double y1d, final IExpr function,
			final ISymbol xVar, final EvalEngine engine) {
		final double step = (bd - ad) / N;
		double y;

		final UnaryNumerical hun = new UnaryNumerical(function, xVar, engine);
		final double data[][] = new double[2][N+1]; 
		double x = ad;

		for (int i = 0; i < N + 1; i++) {
			y = hun.value(x);
			if ((y0d != 0.0) || (y1d != 0.0)) {
				if ((y >= y0d) && (y <= y1d)) {
					data[0][i] = x;
					data[1][i] = y;
				} else {
					if (y < y0d) {
						data[0][i] = x;
						data[1][i] = y0d;
					} else {
						data[0][i] = x;
						data[1][i] = y1d;
					}
				}
			} else {
				data[0][i] = x;
				data[1][i] = y;
			}
			x += step;
		}
		return Convert.doubleToExprTranspose(data);
	} 

	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
