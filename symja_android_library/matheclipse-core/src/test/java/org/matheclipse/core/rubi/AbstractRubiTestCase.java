package org.matheclipse.core.rubi;

import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalControlledCallable;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

import junit.framework.TestCase;

/**
 * Tests system.reflection classes
 */
public abstract class AbstractRubiTestCase extends TestCase {

	protected ExprEvaluator fEvaluator;
	/**
	 * Timeout limit in seconds as the default value for Symja expression evaluation.
	 */
	private long fSeconds = 10;
	private boolean isRelaxedSyntax;

	public AbstractRubiTestCase(String name, boolean isRelaxedSyntax) {
		super(name);
		this.isRelaxedSyntax = isRelaxedSyntax;
		Config.SERVER_MODE = false;
		Config.PARSER_USE_LOWERCASE_SYMBOLS = isRelaxedSyntax;
	}

	private String printResult(IExpr result, String expectedResult, String manuallyCheckedResult) throws IOException {
		// if (result.equals(F.Null)) {
		// return "";
		// }
		if (result.equals(F.$Aborted)) {
			return "TIMEOUT";
		}

		if (result.isFree(F.Integrate)) {
			if (manuallyCheckedResult != null) {
				manuallyCheckedResult = manuallyCheckedResult.trim();
				if (manuallyCheckedResult.length() > 0) {
					IExpr expected = fEvaluator.eval(manuallyCheckedResult);
					if (result.equals(expected)) {
						// the expressions are structurally equal
						return expectedResult;
					}
				}
			}

			expectedResult = expectedResult.trim();
			if (expectedResult.length() > 0) {
				IExpr expected = fEvaluator.eval(expectedResult);
				if (result.equals(expected)) {
					// the expressions are structurally equal
					return expectedResult;
				}

				expected = fEvaluator.eval(F.PossibleZeroQ(F.Subtract(result, expected)));
				if (expected.isTrue()) {
					// the expressions are structurally equal
					return expectedResult;
				}
				// IExpr resultTogether= F.Together.of(F.ExpandAll(result));
				// IExpr expectedTogether = F.Together.of(F.ExpandAll(expected));
				// if (resultTogether.equals(expectedTogether)) {
				// // the expressions are structurally equal
				// return expectedResult;
				// }
			}
		}
		final StringWriter buf = new StringWriter();
		OutputFormFactory.get(true).convert(buf, result);
		return buf.toString();
	}

	/**
	 * Evaluates the given string-expression and returns the result in <code>OutputForm</code>
	 */
	public String interpreter(final String inputExpression, final String expectedResult, String manuallyCheckedResult) {
		IExpr result;
		final StringWriter buf = new StringWriter();
		try {
			if (fSeconds <= 0) {
				result = fEvaluator.eval(inputExpression);
			} else {
				EvalEngine engine = fEvaluator.getEvalEngine();
				engine.setSeconds(fSeconds);
				result = fEvaluator.evaluateWithTimeout(inputExpression, fSeconds, TimeUnit.SECONDS, true,
						new EvalControlledCallable(fEvaluator.getEvalEngine()));
			}
			if (result != null) {
				return printResult(result, expectedResult, manuallyCheckedResult);
			}
		} catch (final AbortException re) {
			try {
				return printResult(F.$Aborted, expectedResult, manuallyCheckedResult);
			} catch (IOException e) {
				Validate.printException(buf, e);
				System.err.println(buf.toString());
				System.err.flush();
				return "";
			}
		} catch (final SyntaxError se) {
			String msg = se.getMessage();
			System.err.println(msg);
			System.err.println();
			System.err.flush();
			return "";
		} catch (final RuntimeException re) {
			Throwable me = re.getCause();
			if (me instanceof MathException) {
				Validate.printException(buf, me);
			} else {
				Validate.printException(buf, re);
			}
			System.err.println(buf.toString());
			System.err.flush();
			return "";
		} catch (final Exception e) {
			Validate.printException(buf, e);
			System.err.println(buf.toString());
			System.err.flush();
			return "";
		} catch (final OutOfMemoryError e) {
			Validate.printException(buf, e);
			System.err.println(buf.toString());
			System.err.flush();
			return "";
		} catch (final StackOverflowError e) {
			Validate.printException(buf, e);
			System.err.println(buf.toString());
			System.err.flush();
			return "";
		}
		return buf.toString();
	}

	public void check(String evalString, String expectedResult) {
		checkLength(evalString, expectedResult, null, -1);
	}

	public void checkLength(String evalString, String expectedResult, String manuallyCheckedResult, int resultLength) {
		try {
			if (evalString.length() == 0 && expectedResult.length() == 0) {
				return;
			}
			// scriptEngine.put("STEPWISE",Boolean.TRUE);
			String evaledResult = interpreter(evalString, expectedResult, manuallyCheckedResult);

			if (resultLength > 0 && evaledResult.length() > resultLength) {
				evaledResult = evaledResult.substring(0, resultLength) + "<<SHORT>>";
				assertEquals(expectedResult, evaledResult);
			} else {
				assertEquals(expectedResult, evaledResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", "1");
		}
	}

	/**
	 * Tests if in the evaluation of the integral the same rule is used as is used by Mathematica Rubi
	 * 
	 * @param evalString
	 *            String of the form "Integrate(expr, x)
	 * @param ruleNumberUsed
	 *            The internal Rubi rule that was used to solve this problem
	 * @param expectedResult
	 *            The expected antiderivative that Rubi calculates
	 */
	public void check(String evalString, String expectedResult, int... ruleNumberUsed) {
		check(evalString, expectedResult);
	}

	public void check(String evalString, String expectedResult, String manuallyCheckedResult, int... ruleNumberUsed) {
		checkLength(evalString, expectedResult, manuallyCheckedResult, -1);
	}

	/**
	 * The JUnit setup method
	 */
	@Override
	protected void setUp() {
		try {
			F.await();
			// start test with fresh instance
			EvalEngine engine = new EvalEngine(isRelaxedSyntax);
			fEvaluator = new ExprEvaluator(engine, true, 0);
			engine.setFileSystemEnabled(true);
			engine.setRecursionLimit(256);
			engine.setIterationLimit(500);
			EvalEngine.set(engine);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void tearDown() throws Exception {
		EvalEngine.remove();
		super.tearDown();
	}
}
