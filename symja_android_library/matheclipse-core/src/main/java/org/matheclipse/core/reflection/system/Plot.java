package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Graphics;
import static org.matheclipse.core.expression.F.Line;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.Show;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

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
		if ((ast.size() >= 3) && (ast.size() <= 4) && ast.arg2().isList()) {
			try {
				final IAST rangeList = (IAST) ast.arg2();
				if (rangeList.isAST3()) {
					final ISymbol x = (ISymbol) rangeList.arg1();
					final IExpr xMin = engine.evalN(rangeList.arg2());
					final IExpr xMax = engine.evalN(rangeList.arg3());
					if ((!(xMin instanceof INum)) || (!(xMax instanceof INum))) {
						return F.NIL;
					}
					final double xMinD = ((INum) xMin).getRealPart();
					final double xMaxd = ((INum) xMax).getRealPart();
					if (xMaxd <= xMinD) {
						return F.NIL;
					}
					double yMinD = 0.0f;
					double yMaxD = 0.0f;

					if ((ast.isAST3()) && ast.get(3).isList()) {
						final IAST lsty = (IAST) ast.arg3();
						if (lsty.isAST2()) {
							final IExpr y0 = engine.evalN(lsty.arg1());
							final IExpr y1 = engine.evalN(lsty.arg2());
							if ((y0 instanceof INum) && (y1 instanceof INum)) {
								yMinD = ((INum) y0).getRealPart();
								yMaxD = ((INum) y1).getRealPart();
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
							temp = plotLine(xMinD, xMaxd, yMinD, yMaxD, list.get(i), x, engine);

							if (temp.isPresent()) {
								line.append(temp);
								primitives.append(line);
							}
							if (i < list.size() - 1) {
								line = Line();
							}
						}
						graphics.append(primitives);

					} else {
						temp = plotLine(xMinD, xMaxd, yMinD, yMaxD, ast.get(1), x, engine);
						if (temp.isPresent()) {
							line.append(temp);
							graphics.append(line);
						}
					}
					final IExpr options[] = { Rule(F.PlotRange, F.Automatic), Rule(F.AxesStyle, F.Automatic),
							Rule(F.AxesOrigin, List(F.C0, F.C0)), Rule(F.Axes, F.True), Rule(F.Background, F.White) };
					graphics.appendAll(F.ast(options, F.List), 1, options.length);
					return Show(graphics);
				}
			} catch (RuntimeException rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}
		}
		return F.Null;
	}

	/**
	 * 
	 * @param xMin
	 *            the minimum x-range value
	 * @param xMax
	 *            the maximum x-range value
	 * @param yMin
	 *            if <code>yMin != 0 && yMax != 0</code> filter only results which are in the y-range and set yMin or
	 *            yMax as plot result-range.
	 * @param yMax
	 *            if <code>yMin != 0 && yMax != 0</code> filter only results which are in the y-range and set yMin or
	 *            yMax as plot result-range.
	 * @param function
	 *            the function which should be plotted
	 * @param xVar
	 *            the variable name
	 * @param engine
	 *            the evaluation engine
	 * @return <code>F.NIL</code> is no conversion of the data into an <code>IExpr</code> was possible
	 */
	public IExpr plotLine(final double xMin, final double xMax, final double yMin, final double yMax,
			final IExpr function, final ISymbol xVar, final EvalEngine engine) {
		final double step = (xMax - xMin) / N;
		double y;

		final UnaryNumerical hun = new UnaryNumerical(function, xVar, engine);
		final double data[][] = new double[2][N + 1];
		double x = xMin;

		for (int i = 0; i < N + 1; i++) {
			y = hun.value(x);
			if ((yMin != 0.0) || (yMax != 0.0)) {
				if ((y >= yMin) && (y <= yMax)) {
					data[0][i] = x;
					data[1][i] = y;
				} else {
					if (y < yMin) {
						data[0][i] = x;
						data[1][i] = yMin;
					} else {
						data[0][i] = x;
						data[1][i] = yMax;
					}
				}
			} else {
				data[0][i] = x;
				data[1][i] = y;
			}
			x += step;
		}
		return Convert.toExprTransposed(data);
	}

	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}
}
