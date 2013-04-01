package org.matheclipse.core.system;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.eval.TimeConstrainedEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.output.StringBufferWriter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

/**
 * Tests system.reflection classes
 */
public class OutOfMemoryTest extends TestCase {

	private Parser fParser;

	protected EvalUtilities util;

	protected static boolean DEBUG = true;

	public OutOfMemoryTest(String name) {
		super(name);
		F.initSymbols(null, null, false);
	}

	public void check(String strEval, String strResult) {
		check(EvalEngine.get(), false, strEval, strResult, false);
	}

	public void check(String strEval, String strResult, boolean relaxedOutput) {
		check(EvalEngine.get(), false, strEval, strResult, relaxedOutput);
	}

	public void check(IAST ast, String strResult) {
		check(EvalEngine.get(), false, ast, strResult);
	}

	public void check(EvalEngine engine, boolean configMode, String strEval, String strResult) {
		check(engine, configMode, strEval, strResult, false);
	}

	public void check(EvalEngine engine, boolean configMode, String strEval, String strResult, boolean relaxedSyntax) {
		try {
			if (strEval.length() == 0 && strResult.length() == 0) {
				return;
			}
			IExpr result;
			StringBufferWriter buf = new StringBufferWriter();
			buf.setIgnoreNewLine(true);
			// F.initSymbols();
			Config.SERVER_MODE = configMode;
			if (Config.SERVER_MODE) {
				Parser parser = new Parser(relaxedSyntax);
				ASTNode node = parser.parse(strEval);
				IExpr inExpr = AST2Expr.CONST.convert(node);
				TimeConstrainedEvaluator utility = new TimeConstrainedEvaluator(engine, false, Config.FOREVER, relaxedSyntax);
				result = utility.constrainedEval(buf, inExpr);
			} else {
				result = util.evaluate(strEval);
				if ((result != null) && !result.equals(F.Null)) {
					OutputFormFactory.get(relaxedSyntax).convert(buf, result);
				}
			}

			assertEquals(buf.toString(), strResult);
		} catch (OutOfMemoryError oome) {
			assertEquals("OutOfMemoryError", "");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(e, "");
		}
	}

	public void check(EvalEngine engine, boolean configMode, IAST ast, String strResult) {
		try {

			StringBufferWriter buf = new StringBufferWriter();
			buf.setIgnoreNewLine(true);
			// F.initSymbols();
			Config.SERVER_MODE = configMode;
			if (Config.SERVER_MODE) {
				// Parser parser = new Parser();
				// ASTNode node = parser.parse(strEval);
				IAST inExpr = ast;
				TimeConstrainedEvaluator utility = new TimeConstrainedEvaluator(engine, false, Config.FOREVER);
				utility.constrainedEval(buf, inExpr);
			} else {
				if (ast != null) {
					OutputFormFactory.get().convert(buf, ast);
				}
			}

			assertEquals(buf.toString(), strResult);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(e, "");
		}
	}

	public void checkPattern(String patternString, String evalString, String resultString) {
		try {
			ASTNode node = fParser.parse(patternString);
			IExpr pat = AST2Expr.CONST.convert(node);

			node = fParser.parse(evalString);
			IExpr eval = AST2Expr.CONST.convert(node);
			// IExpr condition;
			// if (conditionString == null) {
			// condition = null;
			// } else {
			// node = fParser.parse(conditionString);
			// condition = AST2Expr.CONST.convert(node);
			// }
			PatternMatcher matcher = new PatternMatcher(pat);
			// matcher.setCondition(condition);
			if (matcher.apply(eval)) {
				ArrayList<IExpr> resultList = new ArrayList<IExpr>();
				matcher.getPatterns(resultList, pat);
				assertEquals(resultList.toString(), resultString);
				return;
			}
			assertEquals("", resultString);
		} catch (Exception e) {
			assertEquals("", resultString);
		}
	}

	/**
	 * The JUnit setup method
	 */
	protected void setUp() {
		try {
			// setup the evaluation engine (and bind to current thread)
			EvalEngine engine = new EvalEngine(); // EvalEngine.get();
			EvalEngine.set(engine);
			engine.setSessionID("AbstractTestCase");
			engine.setRecursionLimit(256);
			engine.setIterationLimit(1024 * 1024);
			util = new EvalUtilities(engine, false);
			// setup a parser for the math expressions

			fParser = new Parser();
		} catch (Exception e) {
			e.printStackTrace();
			// assertEquals("", "ParserError");
		}
		// setup the expression factory (and bind to current thread)
		// fParser.setFactory(ExpressionFactory.get());

	}

	/**
	 * Test system functions
	 */
	public void testSystem000() {
		check("NumberPartitions[100]", "");

	}

}
