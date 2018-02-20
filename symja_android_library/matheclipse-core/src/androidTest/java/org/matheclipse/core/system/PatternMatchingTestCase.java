package org.matheclipse.core.system;

import java.io.StringWriter;
import java.util.ArrayList;

import javax.script.ScriptEngine;

import junit.framework.TestCase;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.eval.TimeConstrainedEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

public class PatternMatchingTestCase extends TestCase {

	private Parser fParser;

	protected EvalUtilities util;

	protected static boolean DEBUG = true;

	public PatternMatchingTestCase(String name) {
		super(name);
	}

	public void check(ScriptEngine scriptEngine, String evalString, String expectedResult) {
		try {
			if (evalString.length() == 0 && expectedResult.length() == 0) {
				return;
			}

			String evaledResult = (String) scriptEngine.eval(evalString);

			assertEquals(evaledResult, expectedResult);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", "1");
		}
	}

	public void check(String strEval, String strResult) {
		check(EvalEngine.get(), false, strEval, strResult, false);
	}

	public void check(IAST ast, String strResult) {
		check(EvalEngine.get(), true, ast, strResult);
	}

	//
	public void check(EvalEngine engine, boolean configMode, String strEval, String strResult) {
		check(engine, configMode, strEval, strResult, false);
	}

	public void check(EvalEngine engine, boolean configMode, String strEval, String strResult, boolean relaxedSyntax) {
		try {
			if (strEval.length() == 0 && strResult.length() == 0) {
				return;
			}
			IExpr result;
			StringWriter buf = new StringWriter();

			Config.SERVER_MODE = configMode;// configMode;
			if (Config.SERVER_MODE) {
				Parser parser = new Parser(relaxedSyntax);
				ASTNode node = parser.parse(strEval);
				IExpr inExpr = new AST2Expr(false, engine).convert(node);
				TimeConstrainedEvaluator utility = new TimeConstrainedEvaluator(engine, false, Config.FOREVER,
						relaxedSyntax);
				result = utility.constrainedEval(buf, inExpr);
			} else {
				Parser parser = new Parser(relaxedSyntax);
				ASTNode node = parser.parse(strEval);
				IExpr inExpr = new AST2Expr(false, engine).convert(node);
				result = util.evaluate(inExpr);
				if ((result != null) && !result.equals(F.Null)) {
					OutputFormFactory off = OutputFormFactory.get(relaxedSyntax);
					off.setIgnoreNewLine(true);
					off.convert(buf, result);
				}
			}

			assertEquals(buf.toString(), strResult);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", "1");
		}
	}

	public void check(EvalEngine engine, boolean configMode, IAST ast, String strResult) {
		try {

			StringWriter buf = new StringWriter();
			Config.SERVER_MODE = configMode;
			if (Config.SERVER_MODE) {
				IAST inExpr = ast;
				TimeConstrainedEvaluator utility = new TimeConstrainedEvaluator(engine, false, Config.FOREVER);
				utility.constrainedEval(buf, inExpr);
			} else {
				if (ast != null) {
					OutputFormFactory off = OutputFormFactory.get();
					off.setIgnoreNewLine(true);
					OutputFormFactory.get().convert(buf, ast);
				}
			}

			assertEquals(buf.toString(), strResult);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", "1");
		}
	}

	public void checkPattern(String patternString, String evalString, String resultString) {
		try {
			ASTNode node = fParser.parse(patternString);
			EvalEngine engine = EvalEngine.get();
			IExpr pat = new AST2Expr(false, engine).convert(node);

			node = fParser.parse(evalString);
			IExpr eval = new AST2Expr(false, engine).convert(node);
			PatternMatcher matcher = new PatternMatcher(pat);
			if (matcher.test(eval)) {
				ArrayList<IExpr> resultList = new ArrayList<IExpr>();
				matcher.getPatterns(resultList, pat);
				assertEquals(resultList.toString(), resultString);
				return;
			}
			assertEquals("", resultString);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", resultString);
		}
	}

	public void checkPriority(String patternString, int priority) {
		try {
			EvalEngine engine = EvalEngine.get();
			ASTNode node = fParser.parse(patternString);
			IExpr pat = new AST2Expr(false, engine).convert(node);

			PatternMatcher matcher = new PatternMatcher(pat);
			assertEquals(matcher.getLHSPriority(), priority);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(0, priority);
		}
	}

	public void comparePriority(String patternString1, String patternString2, int result) {
		try {
			EvalEngine engine = EvalEngine.get();
			ASTNode node = fParser.parse(patternString1);
			IExpr pat1 = new AST2Expr(false, engine).convert(node);
			node = fParser.parse(patternString1);
			IExpr pat2 = new AST2Expr(false, engine).convert(node);

			PatternMatcher matcher1 = new PatternMatcher(pat1);
			PatternMatcher matcher2 = new PatternMatcher(pat2);
			assertEquals(matcher1.equivalentTo(matcher2), result);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(Integer.MAX_VALUE, result);
		}
	}

	/**
	 * Test system functions
	 */
	public void testSimplePatternMatching() {
		// the space between "x_" and "." operator is needed:
		checkPattern("test[F_[a_.*x_^m_.]]", "test[g[h*y^2]]", "[g, h, y, 2]");
		checkPattern("x_ . y_", "a.b.c", "[a, b.c]");// "[a.b, c]");
		checkPattern("x_+y_", "a+b+c", "[a, b+c]");
		checkPattern("f[x_]", "f[a]", "[a]");
		checkPattern("f[x_,y_]", "f[a,b]", "[a, b]");
		checkPattern("g[x_,y_]", "f[a,b]", "");
		checkPattern("g[x_,42, y_]", "g[a,42,b]", "[a, b]");
	}

	// public void testSlotPatternMatching() {
	// checkPattern("b_.* #+c_.*#^2", "#-1*#^2", "");
	// checkPattern("b_.* #+c_.*#^2", "#+#^2", "[1, 1]");
	// checkPattern("a_. + b_.* #+c_.*#^2", "-1+#+#^2", "[-1, 1, 1]");
	// }

	/**
	 * The JUnit setup method
	 */
	@Override
	protected void setUp() {
		try {
			// setup the evaluation engine (and bind to current thread)
			// F.initSymbols();
			EvalEngine engine = new EvalEngine(); // EvalEngine.get();
			EvalEngine.set(engine);
			engine.setSessionID("SpecialTestCase");
			engine.setRecursionLimit(256);
			engine.setIterationLimit(1024 * 1024);
			util = new EvalUtilities(engine, false, false);
			// setup a parser for the math expressions
			fParser = new Parser();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
