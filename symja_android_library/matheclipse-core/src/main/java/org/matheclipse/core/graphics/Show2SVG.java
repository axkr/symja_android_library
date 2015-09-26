package org.matheclipse.core.graphics;

import java.io.IOException;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;

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
		double width = 400;
		double height = 200;
		buf.append("<svg xmlns:svg=\"http://www.w3.org/2000/svg\" xmlns=\"http://www.w3.org/2000/svg\"\nversion=\"1.0\" "
		// +
		// "width=\"400.000000\" height=\"247.213595\" viewBox=\"-17.666667 -14.610939 435.333333 276.435473\">");
				+ "width=\"400\" height=\"200\">");

		try {
			for (int i = 1; i < ast.size(); i++) {
				if (ast.get(i).isASTSizeGE(F.Line, 2)) {
					lineToSVG(ast.getAST(i), buf, width, height);
				}
			}
		} finally {
			buf.append("</svg>");
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
					if (point.isList() && ((IAST) point).size() == 3) {
						x[i] = ((INum) ((IAST) point).arg1()).doubleValue();
						if (x[i] < xMin) {
							xMin = x[i];
						}
						if (x[i] > xMax) {
							xMax = x[i];
						}
						y[i] = ((INum) ((IAST) point).arg2()).doubleValue();
						if (y[i] < yMin) {
							yMin = y[i];
						}
						if (y[i] > yMax) {
							yMax = y[i];
						}
					}
				}
				double xAxisScalingFactor = width / (double) numberOfPoints;
				double yAxisScalingFactor = height / (yMax - yMin);
				for (int i = 0; i < numberOfPoints; i++) {
					buf.append(Double.toString((i) * xAxisScalingFactor));
					buf.append(" ");
					buf.append(Double.toString(height - ((y[i] - yMin) * yAxisScalingFactor)));
					if (i < numberOfPoints) {
						buf.append(", ");
					}
				}

			}
		} catch (Exception ex) {
			// catch cast exceptions for example
		} finally {
			buf.append("\" style=\"stroke: rgb(24.720000%, 24.000000%, 60.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\" />");
		}
	}

	public static void toSVG(IAST ast, Appendable buf) throws IOException {
		buf.append("<math><mtable><mtr><mtd>");
		if (ast.size() > 1 && ast.get(1).isASTSizeGE(F.Graphics, 2)) {
			graphicsToSVG(ast.getAST(1), buf);
		} else if (ast.size() > 1 && ast.get(1).isASTSizeGE(F.Graphics3D, 2)) {
			graphics3dToSVG(ast.getAST(1), buf);
		}
		buf.append("</mtd></mtr></mtable></math>");
	}
}
