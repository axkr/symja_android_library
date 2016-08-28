package org.matheclipse.core.examples;

import java.io.FileReader;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.function.Get;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class LoadPackageVectorAnalysis {

	public static void main(String[] args) {
		try {

			// distinguish between lower- and uppercase identifiers
			Config.PARSER_USE_LOWERCASE_SYMBOLS = true;
			EvalEngine engine = new EvalEngine(true);

			// Windows c:\temp\...
			FileReader reader = new FileReader(
					"..\\symja_android_library\\tools\\src\\main\\java\\org\\matheclipse\\core\\examples\\VectorAnalysis_lc.m");
			Get.loadPackage(engine, reader);
			reader.close();

			ExprEvaluator util = new ExprEvaluator(engine, false, 100);
			// System.out.println();
			// IExpr result = util.evaluate("Definition(DotProduct)");
			// System.out.println(result.toString());

			System.out.println();
			IExpr result = util.evaluate("CoordinatesToCartesian({a,b,c},Cartesian)");
			System.out.println(result.toString());

			System.out.println();
			result = util.evaluate("DotProduct({a,b,c},{d,e,f},Spherical)");
			System.out.println(result.toString());

			System.out.println();
			result = util.evaluate("Parameters(Spherical)");
			System.out.println(result.toString());

		} catch (SyntaxError e) {
			// catch Symja parser errors here
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (MathException me) {
			// catch Symja math errors here
			System.out.println(me.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
