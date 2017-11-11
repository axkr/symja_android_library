package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Graphics;
import static org.matheclipse.core.expression.F.Line;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.Show;

import java.util.Arrays;

import org.hipparchus.stat.descriptive.moment.Mean;
import org.hipparchus.stat.descriptive.moment.StandardDeviation;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.graphics.Dimensions2D;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
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

	@Override
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
					final IASTAppendable graphics = Graphics();
					IASTAppendable line = Line();
					IExpr temp;
					Dimensions2D dim = new Dimensions2D();
					if (ast.get(1).isList()) {
						final IAST list = (IAST) ast.get(1);
						int size = list.size();
						final IASTAppendable primitives = F.ListAlloc(size);
						for (int i = 1; i < size; i++) {
							temp = plotLine(xMinD, xMaxd, yMinD, yMaxD, list.get(i), x, dim, engine);

							if (temp.isPresent()) {
								line.append(temp);
								primitives.append(line);
							}
							if (i < size - 1) {
								line = Line();
							}
						}
						graphics.append(primitives);

					} else {
						temp = plotLine(xMinD, xMaxd, yMinD, yMaxD, ast.get(1), x, dim, engine);
						if (temp.isPresent()) {
							line.append(temp);
							graphics.append(line);
						}
					}
					IAST plotRange;
					if (dim.isValidRange()) {
						plotRange = Rule(F.PlotRange, F.List(F.List(dim.xMin, dim.xMax), F.List(dim.yMin, dim.yMax)));
					} else {
						plotRange = Rule(F.PlotRange, F.Automatic);
					}
					final IExpr options[] = { plotRange, Rule(F.AxesStyle, F.Automatic),
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
	 * Calculates mean and standard deviation, throwing away all points which are
	 * more than 'thresh' number of standard deviations away from the mean. These
	 * are then used to find good vmin and vmax values. These values can then be
	 * used to find Automatic Plotrange.
	 * 
	 * @param values
	 *            of the y-axe
	 * @return vmin and vmax value of the range
	 */
	private double[] automaticPlotRange(final double values[]) {

		double thresh = 2.0;
		double[] yValues = new double[values.length];
		System.arraycopy(values, 0, yValues, 0, values.length);
		Arrays.sort(yValues);
		double valavg = new Mean().evaluate(yValues);
		double valdev = new StandardDeviation().evaluate(yValues, valavg);

		int n1 = 0;
		int n2 = values.length - 1;
		if (valdev != 0) {
			for (double v : yValues) {
				if (Math.abs(v - valavg) / valdev < thresh) {
					break;
				}
				n1 += 1;
			}
			for (int i = yValues.length - 1; i >= 0; i--) {
				double v = yValues[i];
				if (Math.abs(v - valavg) / valdev < thresh) {
					break;
				}
				n2 -= 1;
			}
		}

		double vrange = yValues[n2] - yValues[n1];
		double vmin = yValues[n1] - 0.05 * vrange; // 5% extra looks nice
		double vmax = yValues[n2] + 0.05 * vrange;
		return new double[] { vmin, vmax };
	}

	/**
	 * 
	 * @param xMin
	 *            the minimum x-range value
	 * @param xMax
	 *            the maximum x-range value
	 * @param yMin
	 *            if <code>yMin != 0 && yMax != 0</code> filter only results which
	 *            are in the y-range and set yMin or yMax as plot result-range.
	 * @param yMax
	 *            if <code>yMin != 0 && yMax != 0</code> filter only results which
	 *            are in the y-range and set yMin or yMax as plot result-range.
	 * @param function
	 *            the function which should be plotted
	 * @param xVar
	 *            the variable name
	 * @param engine
	 *            the evaluation engine
	 * @return <code>F.NIL</code> is no conversion of the data into an
	 *         <code>IExpr</code> was possible
	 */
	public IExpr plotLine(final double xMin, final double xMax, final double yMin, final double yMax,
			final IExpr function, final ISymbol xVar, Dimensions2D autoPlotRange, final EvalEngine engine) {
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
		double[] vMinMax = automaticPlotRange(data[1]);
		autoPlotRange.minMax(xMin, x, vMinMax[0], vMinMax[1]);
		// autoPlotRange.append(F.List(xMin, vMinMax[0]));
		// autoPlotRange.append(F.List(x, vMinMax[1]));
		return Convert.toExprTransposed(data);
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}
}
