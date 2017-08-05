package org.matheclipse.core.graphics;

import java.io.IOException;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISignedNumber;

public class Show2SVG {

	private static void graphics3dToSVG(IAST ast, Appendable buf) throws IOException {
		double width = 400;
		double height = 200;
		buf.append("<graphics3d data=\"");
		try {
			for (int i = 1; i < ast.size(); i++) {
				if (ast.get(i).isASTSizeGE(F.Line, 2)) {
					lineToSVG(ast.getAST(i), buf, width, height);
				}
			}
		} finally {
			buf.append("\" />");
		}
	}

	private static void graphicsToSVG(IAST ast, Appendable buf) throws IOException {
		int width = 350;
		int height = 350;
		buf.append("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" "
				+ "viewBox=\"-1.666667 -1.666667 353.333333 353.333333\"" + " width=\"" + width + "px\" height=\""
				+ height + "px\">\n");

		try {
			for (int i = 1; i < ast.size(); i++) {
				if (ast.get(i).isASTSizeGE(F.Line, 2)) {
					lineToSVG(ast.getAST(i), buf, width, height);
				}
			}
		} finally {
			buf.append("\n</svg>");
		}
	}

	private static void lineToSVG(IAST ast, Appendable buf, double width, double height) throws IOException {
		try {
			if (ast.arg1().isList()) {
				buf.append("<polyline points=\"");
				IAST pointList = (IAST) ast.arg1();
				double x[], y[];
				int numberOfPoints = pointList.size() - 1;

				double xMin = Double.MAX_VALUE;
				double xMax = Double.MIN_VALUE;
				double yMin = Double.MAX_VALUE;
				double yMax = Double.MIN_VALUE;
				x = new double[numberOfPoints];
				y = new double[numberOfPoints];
				IExpr point;
				for (int i = 0; i < numberOfPoints; i++) {
					point = pointList.get(i + 1);
					if (point.isList() && ((IAST) point).isAST2()) {
						x[i] = ((ISignedNumber) ((IAST) point).arg1()).doubleValue();
						if (x[i] < xMin) {
							xMin = x[i];
						}
						if (x[i] > xMax) {
							xMax = x[i];
						}
						y[i] = ((ISignedNumber) ((IAST) point).arg2()).doubleValue();
						if (y[i] < yMin) {
							yMin = y[i];
						}
						if (y[i] > yMax) {
							yMax = y[i];
						}
					}
				}
				double xAxisScalingFactor = width / (xMax - xMin); // (double) numberOfPoints;
				double yAxisScalingFactor = height / (yMax - yMin);
				for (int i = 0; i < numberOfPoints; i++) {
					// buf.append(Double.toString((i) * xAxisScalingFactor));
					buf.append(Double.toString(((x[i] - xMin) * xAxisScalingFactor)));
					buf.append(",");
					buf.append(Double.toString(height - ((y[i] - yMin) * yAxisScalingFactor)));
					if (i < numberOfPoints-1) {
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

	public static void toSVG(IAST ast, Appendable buf) throws IOException {
		// buf.append("<math><mtable><mtr><mtd>");
		if (ast.size() > 1 && ast.get(1).isASTSizeGE(F.Graphics, 2)) {
			graphicsToSVG(ast.getAST(1), buf);
		} else if (ast.size() > 1 && ast.get(1).isASTSizeGE(F.Graphics3D, 2)) {
			graphics3dToSVG(ast.getAST(1), buf);
		}
		// buf.append("</mtd></mtr></mtable></math>");
	}
}
