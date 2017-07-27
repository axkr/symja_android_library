package org.matheclipse.core.examples;

import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

/**
 * See <a href="http://stackoverflow.com/questions/43739728">Stackoverflow 43739728</a>
 */
public class SymbolicSO43739728 {

	public static void main(String[] args) {
		try {
			ExprEvaluator util = new ExprEvaluator();
			// (3/4)*(4/3)*Sqrt(2)*I
			IExpr formula = F.Times(F.QQ(3, 4), F.QQ(4, 3), F.Sqrt(F.ZZ(2)), F.CI);

			// symbolic evaluation
			IExpr result = util.eval(formula);
			// print: I*Sqrt(2)
			System.out.println(result.toString());

			// numerical evaluations
			result = util.eval(F.N(formula));
			// I*1.4142135623730951
			System.out.println(result.toString());

			// print result in decimal format
			final StringWriter buf = new StringWriter();
			DecimalFormatSymbols usSymbols = new DecimalFormatSymbols(Locale.US);
			DecimalFormat decimalFormat = new DecimalFormat("0.0####", usSymbols);
			OutputFormFactory.get(true, false, decimalFormat).convert(buf, result);
			// I*1.41421
			System.out.println(buf.toString());

		} catch (SyntaxError e) {
			// catch Symja parser errors here
			System.out.println(e.getMessage());
		} catch (MathException me) {
			// catch Symja math errors here
			System.out.println(me.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} catch (final StackOverflowError soe) {
			System.out.println(soe.getMessage());
		} catch (final OutOfMemoryError oome) {
			System.out.println(oome.getMessage());
		}
	}
}
