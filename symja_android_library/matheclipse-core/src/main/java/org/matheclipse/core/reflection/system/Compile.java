package org.matheclipse.core.reflection.system;

import java.util.Map;

import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.CompiledFunctionExpr;
import org.matheclipse.core.form.output.DoubleFormFactory;
import org.matheclipse.core.form.output.JavaDoubleFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.FEConfig;

import com.itranswarp.compiler.JavaStringCompiler;

/**
 * Compile an Symja expression to a java function
 *
 */
public class Compile extends AbstractCoreFunctionEvaluator {

	static final String JAVA_SOURCE_CODE = //
			"/* an in-memory compiled function */                                      \n"//
					+ "package org.matheclipse.core.compile;                                      \n"//
					+ "                                                                           \n"//
					+ "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;     \n"//
					+ "import org.matheclipse.core.interfaces.IExpr;                              \n"//
					+ "import org.matheclipse.core.interfaces.IAST;                               \n"//
					+ "import org.matheclipse.core.eval.EvalEngine;                               \n"//
					+ "import org.matheclipse.core.expression.F;                                  \n"//
					+ "                                                                           \n"//
					+ "public class CompiledFunction extends AbstractFunctionEvaluator {          \n"//
					+ "    public IExpr evaluate(final IAST ast, EvalEngine engine){              \n"//
					+ "        {$variables}                                                       \n"//
					+ "        return F.num({$expression});                                       \n"//
					+ "    }                                                                      \n"//
					+ "}                                                                          \n";

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (!ToggleFeature.COMPILE) {
			return F.NIL;
		}
		try {
			IAST variables = Validate.checkIsVariableOrVariableList(ast, 1, engine);
			JavaStringCompiler compiler = new JavaStringCompiler();
			Map<String, byte[]> results;

			// IAST variables = (IAST) ast.arg1();
			StringBuilder variablesBuf = new StringBuilder();
			for (int i = 1; i < variables.size(); i++) {
				variablesBuf.append("double " + variables.get(i) + " = engine.evalDouble(ast.get(" + i + "));\n");
			}
			IExpr expression = ast.arg2();
			DoubleFormFactory factory = JavaDoubleFormFactory.get(true, false);
			StringBuilder buf = new StringBuilder();
			factory.convert(buf, expression);
			String source = JAVA_SOURCE_CODE.replace("{$variables}", variablesBuf.toString());
			source = source.replace("{$expression}", buf.toString());
			// System.out.println(source);

			results = compiler.compile("CompiledFunction.java", source);

			Class<?> clazz = compiler.loadClass("org.matheclipse.core.compile.CompiledFunction", results);

			AbstractFunctionEvaluator fun = (AbstractFunctionEvaluator) clazz.newInstance();
			return CompiledFunctionExpr.newInstance(fun);
		} catch (ValidateException ve) {
			// org.matheclipse.core.eval.exception.Validate.checkIsVariableOrVariableList( )
			return engine.printMessage(ast.topHead(), ve);
		} catch (Exception rex) {
			rex.printStackTrace();
			return engine.printMessage("Compile: " + rex.getMessage());
		}
	}

	public int[] expectedArgSize() {
		return IOFunctions.ARGS_2_2;
	}
}