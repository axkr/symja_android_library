package org.matheclipse.parser.test.eval;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Assert;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.CoreCallbackFunction;
import org.matheclipse.parser.client.eval.BooleanVariable;
import org.matheclipse.parser.client.eval.DoubleEvaluator;
import org.matheclipse.parser.client.eval.DoubleVariable;
import org.matheclipse.parser.client.eval.IDoubleValue;
import org.matheclipse.parser.client.math.MathException;

import junit.framework.TestCase;

/**
 * Tests evaluation in <code>double</code> expression mode
 */
public class EvalDoubleTestCase extends TestCase {

	public EvalDoubleTestCase(String name) {
		super(name);
	}

	public void check(String in, String compareWith) {
		try {
			DoubleEvaluator engine = new DoubleEvaluator();
			double d = engine.evaluate(in);
			assertEquals(Double.valueOf(d).toString(), compareWith);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(e.getMessage(), compareWith);
		}
	}

	public void testEval001() {
		check("42", "42.0");
		if (!Config.EXPLICIT_TIMES_OPERATOR) {
			check("1.0E10", "EvalDouble#evaluate(ASTNode) not possible for: E10");
			check("1.0E-10", "-7.281718171540955");
			check("1.0E+10", "12.718281828459045");
		} else {
			check("1.0E10", "1.0E10");
			check("1.0E-10", "1.0E-10");
			check("1.0E+10", "1.0E10");
		}
		// throws NumberFormatException
		// check("1.0E", "");
		check("1.5", "1.5");
		check("-42", "-42.0");
		check("+42", "42.0");
		check("-42.1", "-42.1");
		check("+42.2", "42.2");
		check("-3/4", "-0.75");
		check("+3/4", "0.75");
		check("3^3", "27.0");
		check("2+2*2", "6.0");
		check("2^9", "512.0");
		check("2^3^2", "512.0");
		check("(2^3)^2", "64.0");
		check("3+4*7", "31.0");
		check("3+4*7*3", "87.0");
		check("1+2+3+4*7*3", "90.0");
		// calculate in Radians
		check("Sin[Cos[3.2]]", "-0.8405484252742996");
		// Pi / 2
		check("90.0*Degree", "1.5707963267948966");
		check("Pi/2", "1.5707963267948966");
		check("Sin[Pi/2*Cos[Pi]]", "-1.0");

		check("Max[0,-42,Pi,12]", "12.0");
		check("Min[0,-42,Pi,12]", "-42.0");

		// check("Random[]", "-1.0");
	}

	public void testEval002() {
		check("If[3/4<0.51, 1.1, -1.2]", "-1.2");
		check("If[True, 1.1, -1.2]", "1.1");
		check("If[3/4>0.51 && 3/8>0.1, 1.1, -1.2]", "1.1");
		check("If[3/4>0.51 || 3/8>0.1, 1.1, -1.2]", "1.1");
		check("If[!(3/4>0.51 || 3/8>0.1), 1.1, -1.2]", "-1.2");
	}

	public void testEval003() {
		try {
			DoubleEvaluator engine = new DoubleEvaluator();
			double d = engine.evaluate("Sin[Pi/2*Cos[Pi]]");
			assertEquals(Double.toString(d), "-1.0");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testEval004() {
		try {
			IDoubleValue vd = new DoubleVariable(3.0);
			DoubleEvaluator engine = new DoubleEvaluator();
			engine.defineVariable("x", vd);
			double d = engine.evaluate("x^2+3");
			assertEquals(Double.valueOf(d).toString(), "12.0");
			vd.setValue(4);
			d = engine.evaluate();
			assertEquals(Double.valueOf(d).toString(), "19.0");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testEval005() {
		try {
			IDoubleValue vd = new DoubleVariable(3.0);
			DoubleEvaluator engine = new DoubleEvaluator();
			engine.defineVariable("x", vd);
			double d = engine.evaluate("x^2*x^2-1");
			assertEquals(Double.valueOf(d).toString(), "80.0");
			vd.setValue(4);
			d = engine.evaluate();
			assertEquals(Double.valueOf(d).toString(), "255.0");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testEval006() {
		try {
			DoubleEvaluator engine = new DoubleEvaluator();

			BooleanVariable vb = new BooleanVariable(true);
			engine.defineVariable("$1", vb);
			BooleanVariable vb2 = new BooleanVariable(true);
			engine.defineVariable("$2", vb2);
			double d = engine.evaluate("If[$1 && $2, 1, 0]");
			Assert.assertEquals(d, 1d, DoubleEvaluator.EPSILON);
			vb.setValue(false);
			d = engine.evaluate();
			Assert.assertEquals(d, 0d, DoubleEvaluator.EPSILON);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals("", e.getMessage());
		}
	}

	public void testEval007() {
		try {
			DoubleEvaluator engine = new DoubleEvaluator();
			// IDoubleValue vdi = new DoubleVariable(1.0);
			// engine.defineVariable("$i", vdi);
			IDoubleValue vd = new DoubleVariable(3.0);
			engine.defineVariable("$1", vd);
			IDoubleValue vd2 = new DoubleVariable(-4.0);
			engine.defineVariable("$2", vd2);
			double d = engine.evaluate("$i = $1+$2; If[$i>0, 1, -1]");
			Assert.assertEquals(d, -1d, DoubleEvaluator.EPSILON);
			vd2.setValue(4.0);
			d = engine.evaluate();
			Assert.assertEquals(d, 1d, DoubleEvaluator.EPSILON);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals("", e.getMessage());
		}
	}

	//

	public void testEval008() {
		try {
			HashSet<String> result = new HashSet<String>();
			DoubleEvaluator.getVariables("a+2*b+$c", result);
			ArrayList<String> list = new ArrayList<String>();
			for (String string : result) {
				list.add(string);
			}
			Assert.assertEquals(list.toString(), "[a, b, $c]");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals("", e.getMessage());
		}
	}

	public void testEval009() {
		try {
			DoubleEvaluator engine = new DoubleEvaluator();

			BooleanVariable vb = new BooleanVariable(true);
			engine.defineVariable("$1", vb);
			double d = engine.evaluate("If[$1, 1, 0]");
			Assert.assertEquals(d, 1d, DoubleEvaluator.EPSILON);
			vb.setValue(false);
			d = engine.evaluate();
			Assert.assertEquals(d, 0d, DoubleEvaluator.EPSILON);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals("", e.getMessage());
		}
	}

	public void testMissingFunction009() {
		try {
			DoubleEvaluator engine = new DoubleEvaluator();
			double d = engine.evaluate("aTest[1.0]");
			assertEquals(Double.toString(d), "");
		} catch (MathException e) {
			assertEquals("EvalDouble#evaluateFunction(FunctionNode) not possible for: aTest(1.0)", e.getMessage());
		}
	}

	public void testInterpolatingFunction() {
		try {
			DoubleEvaluator engine = new DoubleEvaluator();
			engine.setCallbackFunction(CoreCallbackFunction.CONST);
			engine.defineVariable("x", new DoubleVariable(3.0));
			double d = engine.evaluate("InterpolatingFunction[{{0, 0}, {1, 1}, {2, 3}, {3, 4}, {4, 3}, {5, 0}}][x]");

			assertEquals(Double.toString(d), "4.0");
		} catch (MathException e) {
			e.printStackTrace();
			assertEquals("EvalDouble#evaluateFunction(FunctionNode) not possible for: aTest(1.0)", e.getMessage());
		}
	}
}