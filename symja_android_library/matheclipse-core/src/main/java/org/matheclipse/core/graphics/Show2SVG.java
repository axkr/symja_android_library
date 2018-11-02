package org.matheclipse.core.graphics;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;

public class Show2SVG {
	//
	// private static class Dimensions2D {
	//
	//
	// }

	private static final DecimalFormatSymbols US_SYNBOLS = new DecimalFormatSymbols(Locale.US);
	protected static final DecimalFormat FORMATTER = new DecimalFormat("0.0####", US_SYNBOLS);

	private static void elementDimension(IAST ast, Dimensions2D dim) {
		for (int i = 1; i < ast.size(); i++) {
			if (ast.get(i).isSymbol()) {
				//
			} else if (ast.get(i).isASTSizeGE(F.Line, 2)) {
				lineDimension(ast.getAST(i), dim);
			} else if (ast.get(i).isASTSizeGE(F.Point, 2)) {
				pointDimension(ast.getAST(i), dim);
			} else if (ast.get(i).isASTSizeGE(F.Rectangle, 1)) {
				rectangleDimension(ast.getAST(i), dim);
			}
		}
	}

	private static void elementToSVG(IAST ast, StringBuilder buf, Dimensions2D dim) {
		for (int i = 1; i < ast.size(); i++) {
			if (ast.get(i).isSymbol()) {
				dim.setColorRGB(ast.get(i).toString());
			} else if (ast.get(i).isASTSizeGE(F.Line, 2)) {
				lineToSVG(ast.getAST(i), buf, dim);
			} else if (ast.get(i).isASTSizeGE(F.Point, 2)) {
				pointToSVG(ast.getAST(i), buf, dim);
			} else if (ast.get(i).isASTSizeGE(F.Rectangle, 2)) {
				rectangleToSVG(ast.getAST(i), buf, dim);
			}
		}
	}

	public static void graphicsToSVG(IAST ast, StringBuilder buf) {
		EvalEngine engine = EvalEngine.get();
		IAST numericAST = (IAST) engine.evalN(ast);
		Dimensions2D dim = new Dimensions2D(350, 350);
		if (numericAST.size() > 2) {
			final Options options = new Options(numericAST.topHead(), numericAST, 2, engine);
			IExpr option = options.getOption("PlotRange");
			if (option.isListOfLists() && option.size() == 3) {
				IAST list = (IAST) option;
				dim.setPlotRange(list.getAST(1), list.getAST(2));
			}
			option = options.getOption("Axes");
			if (option.isTrue()) {
				dim.setAxes(true);
			}
		}

		int width = dim.width;
		int height = dim.height;
		IExpr arg1 = numericAST.arg1();
		if (arg1.isList()) {
			IAST list = (IAST) arg1;
			elementDimension(list, dim);
		} else {
			elementDimension(numericAST, dim);
		}
		buf.append("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" "
				+ "viewBox=\"-1.666667 -1.666667 353.333333 353.333333\"" + " width=\"" + width + "px\" height=\""
				+ height + "px\">\n");

		try {
			if (arg1.isList()) {
				IAST list = (IAST) arg1;
				elementToSVG(list, buf, dim);
			} else {
				elementToSVG(numericAST, buf, dim);
			}
			if (dim.isAxes()) {
				double xScale = width / (dim.xMax - dim.xMin);
				double yScale = height / (dim.yMax - dim.yMin);
				double x1 = 0;

				// vertical axe
				// + "0.000000,233.333333 6.666667,233.333333");
				buf.append("<polyline points=\"");

				buf.append(FORMATTER.format((x1 - dim.xMin) * xScale));
				buf.append(",");
				buf.append(FORMATTER.format(0.0));
				buf.append(" ");
				buf.append(FORMATTER.format((x1 - dim.xMin) * xScale));
				buf.append(",");
				buf.append(FORMATTER.format(height));

				buf.append(
						"\" style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\"/>\n");

				// horizontals axe
				double y1 = (-dim.yMin) * yScale;
				buf.append("<polyline points=\"");

				buf.append(FORMATTER.format(0));
				buf.append(",");
				buf.append(FORMATTER.format(y1));
				buf.append(" ");
				buf.append(FORMATTER.format(width));
				buf.append(",");
				buf.append(FORMATTER.format(y1));

				buf.append(
						"\" style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\"/>\n");
			}
		} finally {
			buf.append("\n</svg>");
		}
	}

	private static void lineDimension(IAST ast, Dimensions2D dim) {
		if (ast.arg1().isList()) {
			IAST pointList = (IAST) ast.arg1();
			double x[], y[];
			int numberOfPoints = pointList.argSize();

			double xMin = Double.MAX_VALUE;
			double xMax = Double.MIN_VALUE;
			double yMin = Double.MAX_VALUE;
			double yMax = Double.MIN_VALUE;
			x = new double[numberOfPoints];
			y = new double[numberOfPoints];
			IExpr point;
			for (int i = 0; i < numberOfPoints; i++) {
				point = pointList.get(i + 1);
				if (point.isList() && point.isAST2()) {
					x[i] = ((ISignedNumber) point.first()).doubleValue();
					if (x[i] < xMin) {
						xMin = x[i];
					}
					if (x[i] > xMax) {
						xMax = x[i];
					}
					y[i] = ((ISignedNumber) point.second()).doubleValue();
					if (y[i] < yMin) {
						yMin = y[i];
					}
					if (y[i] > yMax) {
						yMax = y[i];
					}
				}
			}
			dim.minMax(xMin, xMax, yMin, yMax);
		}
	}

	private static void lineToSVG(IAST ast, StringBuilder buf, Dimensions2D dim) {
		try {
			if (ast.arg1().isList()) {
				buf.append("<polyline points=\"");
				IAST pointList = (IAST) ast.arg1();
				double x[], y[];
				int numberOfPoints = pointList.argSize();

				int width = dim.width;
				int height = dim.height;
				double xMin = dim.xMin;
				double xMax = dim.xMax;
				double yMin = dim.yMin;
				double yMax = dim.yMax;
				x = new double[numberOfPoints];
				y = new double[numberOfPoints];
				IExpr point;
				for (int i = 0; i < numberOfPoints; i++) {
					point = pointList.get(i + 1);
					if (point.isList() && ((IAST) point).isAST2()) {
						x[i] = ((ISignedNumber) point.first()).doubleValue();
						y[i] = ((ISignedNumber) point.second()).doubleValue();
					}
				}
				double xAxisScalingFactor = width / (xMax - xMin);
				double yAxisScalingFactor = height / (yMax - yMin);
				for (int i = 0; i < numberOfPoints; i++) {
					buf.append(FORMATTER.format(((x[i] - xMin) * xAxisScalingFactor)));
					buf.append(",");
					buf.append(FORMATTER.format(height - ((y[i] - yMin) * yAxisScalingFactor)));
					if (i < numberOfPoints - 1) {
						buf.append(" ");
					}
				}

			}
		} catch (RuntimeException ex) {
			// catch cast exceptions for example
			ex.printStackTrace();
		} finally {
			buf.append(
					"\" \n          style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\" />");
		}
	}

	private static void pointDimension(IAST ast, Dimensions2D dim) {
		if (ast.size() == 2) {
			IExpr arg1 = ast.arg1();
			if (arg1.isListOfLists()) {
				IAST list = (IAST) arg1;
				for (int i = 1; i < list.size(); i++) {
					if (list.get(i).isAST(F.List, 3)) {
						IAST point = (IAST) list.get(i);
						singlePointDimensions(point, dim);
					}
				}
			} else if (arg1.isAST(F.List, 3)) {
				IAST point = (IAST) ast.arg1();

				singlePointDimensions(point, dim);
			}
		}
	}

	private static void singlePointDimensions(IAST point, Dimensions2D dim) {
		double x1 = ((ISignedNumber) point.arg1()).doubleValue();
		double y1 = ((ISignedNumber) point.arg2()).doubleValue();

		dim.minMax(x1 - Config.DOUBLE_EPSILON, x1 + Config.DOUBLE_EPSILON, y1 - Config.DOUBLE_EPSILON,
				y1 + Config.DOUBLE_EPSILON);
	}

	private static void pointToSVG(IAST ast, StringBuilder buf, Dimensions2D dim) {

		if (ast.size() == 2) {
			IExpr arg1 = ast.arg1();
			if (arg1.isListOfLists()) {
				IAST list = (IAST) arg1;
				for (int i = 1; i < list.size(); i++) {
					if (list.get(i).isAST(F.List, 3)) {
						IAST point = (IAST) list.get(i);
						singlePointToSVG(point, buf, dim);
					}
				}
			} else if (arg1.isAST(F.List, 3)) {
				IAST point = (IAST) arg1;
				singlePointToSVG(point, buf, dim);
			}
		}

	}

	private static void singlePointToSVG(IAST point, StringBuilder buf, Dimensions2D dim) {
		try {
			double xMin = dim.xMin;
			double yMax = dim.yMax;
			buf.append("<circle ");
			double xAxisScalingFactor = dim.getXScale();
			double yAxisScalingFactor = dim.getYScale();

			double x1 = ((ISignedNumber) point.arg1()).doubleValue();
			double y1 = ((ISignedNumber) point.arg2()).doubleValue();
			double r = 1.0;
			// x="0.000000" y="0.000000" width="350.000000" height="350.000000"
			double cx = (x1 - xMin) * xAxisScalingFactor;
			double cy = (yMax - y1) * yAxisScalingFactor;
			if (F.isZero(cx)) {
				cx = dim.width / 2.0;
			}
			if (F.isZero(cy)) {
				cy = dim.height / 2.0;
			}
			buf.append("cx=\"");
			buf.append(FORMATTER.format(cx));
			buf.append("\" cy=\"");
			buf.append(FORMATTER.format(cy));
			buf.append("\" r=\"");
			buf.append(FORMATTER.format(r));
		} catch (RuntimeException ex) {
			// catch cast exceptions for example
			ex.printStackTrace();
		} finally {
			buf.append("\" \n      style=\"stroke: none; stroke-width: 0.000000px; ");
			buf.append("fill: rgb(");
			dim.getColorRGB(buf);
			buf.append("); ");
			buf.append("fill-opacity: 1\" />\n");
		}
	}

	private static void rectangleDimension(IAST ast, Dimensions2D dim) {
		if (ast.size() == 2) {
			if (ast.arg1().isAST(F.List, 3)) {
				IAST list1 = (IAST) ast.arg1();

				double x1 = ((ISignedNumber) list1.arg1()).doubleValue();
				double y1 = ((ISignedNumber) list1.arg2()).doubleValue();
				double x2 = x1 + 1.0;
				double y2 = y1 + 1.0;

				dim.minMax(x1, x2, y1, y2);
			}
		} else if (ast.size() == 3 && ast.arg1().isAST(F.List, 3) && ast.arg2().isAST(F.List, 3)) {
			IAST list1 = (IAST) ast.arg1();
			IAST list2 = (IAST) ast.arg2();

			double x1 = ((ISignedNumber) list1.arg1()).doubleValue();
			double y1 = ((ISignedNumber) list1.arg2()).doubleValue();
			double x2 = ((ISignedNumber) list2.arg1()).doubleValue();
			double y2 = ((ISignedNumber) list2.arg2()).doubleValue();

			dim.minMax(x1, x2, y1, y2);
		}
	}

	private static void rectangleToSVG(IAST ast, StringBuilder buf, Dimensions2D dim) {
		try {
			int width = dim.width;
			int height = dim.height;
			double xMin = dim.xMin;
			double xMax = dim.xMax;
			double yMin = dim.yMin;
			double yMax = dim.yMax;
			if (ast.size() == 2) {
				if (ast.arg1().isAST(F.List, 3)) {
					IAST list1 = (IAST) ast.arg1();
					buf.append("<rect ");
					double xAxisScalingFactor = width / (xMax - xMin);
					double yAxisScalingFactor = height / (yMax - yMin);

					double x1 = ((ISignedNumber) list1.arg1()).doubleValue();
					double y1 = ((ISignedNumber) list1.arg2()).doubleValue();
					double w = 1.0;
					double h = 1.0;
					// x="0.000000" y="0.000000" width="350.000000" height="350.000000"
					buf.append("x=\"");
					buf.append(FORMATTER.format((x1 - xMin) * xAxisScalingFactor));
					buf.append("\" y=\"");
					buf.append(FORMATTER.format((yMax - y1 - 1) * yAxisScalingFactor));
					buf.append("\" width=\"");
					buf.append(FORMATTER.format(w * xAxisScalingFactor));
					buf.append("\" height=\"");
					buf.append(FORMATTER.format(h * yAxisScalingFactor));
				}
			} else if (ast.size() == 3 && ast.arg1().isAST(F.List, 3) && ast.arg2().isAST(F.List, 3)) {
				IAST list1 = (IAST) ast.arg1();
				IAST list2 = (IAST) ast.arg2();
				buf.append("<rect ");
				double xAxisScalingFactor = width / (xMax - xMin);
				double yAxisScalingFactor = height / (yMax - yMin);

				double x1 = ((ISignedNumber) list1.arg1()).doubleValue();
				double y1 = ((ISignedNumber) list1.arg2()).doubleValue();
				double x2 = ((ISignedNumber) list2.arg1()).doubleValue();
				double y2 = ((ISignedNumber) list2.arg2()).doubleValue();
				double w = x2 - x1;
				double h = y2 - y1;
				buf.append("x=\"");
				buf.append(FORMATTER.format((x1 - xMin) * xAxisScalingFactor));
				buf.append("\" y=\"");
				buf.append(FORMATTER.format((yMax - y1 - h) * yAxisScalingFactor));
				buf.append("\" width=\"");
				buf.append(FORMATTER.format(w * xAxisScalingFactor));
				buf.append("\" height=\"");
				buf.append(FORMATTER.format(h * yAxisScalingFactor));
			}

		} catch (RuntimeException ex) {
			// catch cast exceptions for example
			ex.printStackTrace();
		} finally {
			buf.append("\" \n      style=\"stroke: none; stroke-width: 0.000000px; ");
			buf.append("fill: rgb(");
			dim.getColorRGB(buf);
			buf.append("); ");
			buf.append("stroke-opacity: 1; stroke-width: 0.666667px; fill-opacity: 1\" />\n");
		}
	}

	public static void toSVG(IAST ast, StringBuilder buf) {
		if (ast.size() > 1 && ast.get(1).isASTSizeGE(F.Graphics, 2)) {
			graphicsToSVG(ast.getAST(1), buf);
		} else if (ast.size() > 1 && ast.get(1).isASTSizeGE(F.Graphics3D, 2)) {
			Show3D2ThreeJS.graphics3dToSVG(ast.getAST(1), buf);
		}
	}
}
