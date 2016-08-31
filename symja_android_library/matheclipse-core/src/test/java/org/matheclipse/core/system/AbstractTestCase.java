package org.matheclipse.core.system;

import java.io.StringWriter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TimeConstrainedEvaluator;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.script.engine.MathScriptEngine;

import junit.framework.TestCase;

/**
 * Tests system.reflection classes
 */
public abstract class AbstractTestCase extends TestCase {
	protected ScriptEngine fScriptEngine;
	protected static ScriptEngineManager fScriptManager = new ScriptEngineManager();

	public AbstractTestCase(String name) {
		super(name);
		Config.SERVER_MODE = true;
	}

	public void check(String evalString, String expectedResult) {
		check(fScriptEngine, evalString, expectedResult, -1);
	}

	public void check(String evalString, String expectedResult, int resultLength) {
		check(fScriptEngine, evalString, expectedResult, resultLength);
	}

	public void check(ScriptEngine scriptEngine, String evalString, String expectedResult, int resultLength) {
		try {
			if (evalString.length() == 0 && expectedResult.length() == 0) {
				return;
			}
			// scriptEngine.put("STEPWISE",Boolean.TRUE);
			String evaledResult = (String) scriptEngine.eval(evalString);

			if (resultLength > 0 && evaledResult.length() > resultLength) {
				evaledResult = evaledResult.substring(0, resultLength) + "<<SHORT>>";
				assertEquals(evaledResult, expectedResult);
			} else {
				assertEquals(evaledResult, expectedResult);
			}
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
					off.convert(buf, ast);
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
				fScriptEngine = new MathScriptEngine();//fScriptManager.getEngineByExtension("m");
				fScriptEngine.put("RELAXED_SYNTAX", Boolean.TRUE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
