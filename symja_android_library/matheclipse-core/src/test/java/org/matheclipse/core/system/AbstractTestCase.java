package org.matheclipse.core.system;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import junit.framework.TestCase;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TimeConstrainedEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.output.StringBufferWriter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Tests system.reflection classes
 */
public abstract class AbstractTestCase extends TestCase {
	private ScriptEngine fScriptEngine;
	protected static ScriptEngineManager fScriptManager = new ScriptEngineManager();

	public AbstractTestCase(String name) {
		super(name);
		F.initSymbols(null, null, false);
		Config.SERVER_MODE = true;
	}

	// public void check(String evalString, String expectedResult) {
	// check(true, evalString, expectedResult);
	// }
	public void check(String evalString, String expectedResult) {
		check(fScriptEngine, evalString, expectedResult);
	}

	public void check(ScriptEngine scriptEngine, String evalString, String expectedResult) {
		try {
			if (evalString.length() == 0 && expectedResult.length() == 0) {
				return;
			}
			// scriptEngine.put("STEPWISE",Boolean.TRUE);
			String evaledResult = (String) scriptEngine.eval(evalString);

			assertEquals(evaledResult, expectedResult);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(e, "");
		}
	}

	public void check(IAST ast, String strResult) {
		check(EvalEngine.get(), true, ast, strResult);
	}

	public void check(EvalEngine engine, boolean configMode, IAST ast, String strResult) {
		boolean mode = Config.SERVER_MODE;
		try {
			StringBufferWriter buf = new StringBufferWriter();
			buf.setIgnoreNewLine(true);
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
		} finally {
			Config.SERVER_MODE = mode;
		}
	}

	/**
	 * The JUnit setup method
	 */
	protected void setUp() {
		try {
			synchronized (fScriptManager) {
				fScriptEngine = fScriptManager.getEngineByExtension("m");
			}

		} catch (Exception e) {
			e.printStackTrace();
			// assertEquals("", "ParserError");
		}
		// setup the expression factory (and bind to current thread)
		// fParser.setFactory(ExpressionFactory.get());

	}

}
