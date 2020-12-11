package org.matheclipse.core.preprocessor;

import java.util.Map;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;

import com.itranswarp.compiler.JavaStringCompiler;

/**
 * Example for <a href="https://github.com/axkr/symja_android_library/issues/132">github #132</a>
 */
public class CompileFunction {

  public static void main(String[] args) {
    JavaStringCompiler compiler = new JavaStringCompiler();
    Map<String, byte[]> results;
    try {
      ExprParser p = new ExprParser(EvalEngine.get(), true);
      IExpr expr = p.parse("x^3+Cos(x^2)");
      String source =
          JAVA_SOURCE_CODE.replace(
              "{$expression}", expr.internalJavaString(false, 0, false, true, true, x -> null));

      results = compiler.compile("CompiledFunction.java", source);

      // System.out.println(JAVA_SOURCE_CODE);
      Class<?> clazz = compiler.loadClass("org.matheclipse.core.compile.CompiledFunction", results);
      // try instance:
      AbstractFunctionEvaluator fun = (AbstractFunctionEvaluator) clazz.newInstance();

      // CompiledFunction(1.4567);
      IExpr result = fun.evaluate(F.List(F.num(1.4567)), EvalEngine.get());
      // 2.5673892025409693
      System.out.println(result.toString());

      double x = 1.4567;
      double value = Math.pow(x, 3) + Math.cos(Math.pow(x, 2));
      System.out.println(value);

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  static final String JAVA_SOURCE_CODE = //
      "/* an in-memory compiled function */                                      \n" //
          + "package org.matheclipse.core.compile;                                      \n" //
          + "                                                                           \n" //
          + "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;     \n" //
          + "import org.matheclipse.core.interfaces.IExpr;                              \n" //
          + "import org.matheclipse.core.interfaces.IAST;                               \n" //
          + "import org.matheclipse.core.eval.EvalEngine;                               \n" //
          + "import org.matheclipse.core.expression.F;                                  \n" //
          + "                                                                           \n" //
          + "public class CompiledFunction extends AbstractFunctionEvaluator {          \n" //
          + "    public IExpr evaluate(final IAST ast, EvalEngine engine){              \n" //
          + "        IExpr x = ast.arg1();                                              \n" //
          + "        return engine.evaluate({$expression});                             \n" //
          + "    }                                                                      \n" //
          + "}                                                                          \n";
}
