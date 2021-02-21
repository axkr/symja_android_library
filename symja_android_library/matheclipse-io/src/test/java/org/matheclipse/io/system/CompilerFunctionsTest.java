package org.matheclipse.io.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.script.engine.MathScriptEngine;

/** Tests for compiler functions */
public class CompilerFunctionsTest extends AbstractTestCase {

  public CompilerFunctionsTest(String name) {
    super(name);
  }

  public void testCompileSqrtException() {
    if (ToggleFeature.COMPILE) {
      // A JDK is needed here

      check(
          "cf = Compile({x, y}, Sqrt(x*y));", //
          "");
      check(
          "cf(1.0,2.0)", //
          "1.41421");
      check(
          "cf(-1.0,2.0)", //
          "I*1.41421");
      check(
          "cf2 = Compile({x, y}, x+y);", //
          "");
      check(
          "cf2(1.0,2.0)", //
          "3.0");
      check(
          "cf(1.0,2.0)", //
          "1.41421");
    }
  }

  public void testCompile001() {
    if (ToggleFeature.COMPILE) {
      // A JDK is needed here
    	
      // test with random result
    	
      //      check(
      //          "f = Compile({{n, _Integer}},\n"
      //              + "    	             Module({p = Range(n),i,x,t},\n"
      //              + "    		            Do(x = RandomInteger({1,i});\n"
      //              + "    			        t = p[[i]]; p[[i]] = p[[x]]; p[[x]] = t,\n"
      //              + "    			        {i,n,2,-1}\n"
      //              + "    			    );\n"
      //              + "    		            p\n"
      //              + "    		     )\n"
      //              + "    	        );", //
      //          "");
      //      check(
      //          " f(4)", //
      //          "{2,4,1,3}");

      check(
          "f=Compile({{x, _Real}}, E^3-Cos(Pi^2/x));  ", //
          "");
      check(
          " f(1.4567)", //
          "19.20421");

      check(
          "f=Compile({x}, x^3+Cos(x^2)); ", //
          "");
      check(
          " f(1.4567)", //
          "2.56739");

      check(
          "f=Compile({x}, x^3+Gamma(x^2)); ", //
          "");
      check(
          " f(1.4567)", //
          "4.14894");
    }
  }

  public void testCompile0021() {
    if (ToggleFeature.COMPILE) {

      check(
          "cf = Compile({{x, _Real}, {y, _Integer}}, Sin(x + y));", //
          "");
      check(
          "cf(1,2)", //
          "0.14112");
      // message: ... called with 1 arguments; 2 arguments are expected.
      check(
          "cf(x+y)", //
          "CompiledFunction(Arg count: 2 Types: {Real,Integer} Variables: {x,y})[x+y]");
    }
  }

  public void testCompilePrint001() {
    if (ToggleFeature.COMPILE) {
      // A JDK is needed here
      check(
          "f = CompilePrint({{n, _Integer}},\n"
              + "    	             Module({p = Range(n),i,x,t},\n"
              + "    		            Do(x = RandomInteger({1,i});\n"
              + "    			        t = p[[i]]; p[[i]] = p[[x]]; p[[x]] = t,\n"
              + "    			        {i,n,2,-1}\n"
              + "    			    );\n"
              + "    		            p\n"
              + "    		     )\n"
              + "    	        )", //
          "/* an in-memory compiled function */                                      \n"
              + "package org.matheclipse.core.compile;                                      \n"
              + "                                                                           \n"
              + "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;     \n"
              + "import org.matheclipse.core.interfaces.IExpr;                              \n"
              + "import org.matheclipse.core.interfaces.IAST;                               \n"
              + "import org.matheclipse.core.eval.EvalEngine;                               \n"
              + "import org.matheclipse.core.expression.F;                                  \n"
              + "import static org.matheclipse.core.expression.F.*;                         \n"
              + "                                                                           \n"
              + "public class CompiledFunction extends AbstractFunctionEvaluator {          \n"
              + "    public IExpr evaluate(final IAST ast, EvalEngine engine){              \n"
              + "        if (ast.argSize()!=1) { return print(ast,1,engine); }     \n"
              + "        IExpr n = ast.get(1);\n"
              + "int ni = engine.evalInt(n);\n"
              + "                                                       \n"
              + "        return \n"
              + "F.Module(\n"
              + "F.List(F.Set(F.p,F.Range(n)),F.i,F.x,F.t),\n"
              + "F.CompoundExpression(\n"
              + "F.Do(\n"
              + "F.CompoundExpression(\n"
              + "F.Set(F.x,F.RandomInteger(F.List(F.C1,F.i))),\n"
              + "F.Set(F.t,F.Part(F.p,F.i)),\n"
              + "F.Set(F.Part(F.p,F.i),F.Part(F.p,F.x)),\n"
              + "F.Set(F.Part(F.p,F.x),F.t)\n"
              + ")\n"
              + ",\n"
              + "F.List(F.i,n,F.C2,F.CN1)\n"
              + ")\n"
              + ",\n"
              + "F.p\n"
              + ")\n"
              + "\n"
              + ")\n"
              + ";\n"
              + "\n"
              + "    }                                                                      \n"
              + "}                                                                          \n"
              + "");
    }
  }

  public void testCompilePrint002() {
    if (ToggleFeature.COMPILE) {
      // A JDK is needed here
      check(
          "f=CompilePrint({x}, E^3-Cos(Pi^2/x))", //
          "/* an in-memory compiled function */                                      \n"
              + "package org.matheclipse.core.compile;                                      \n"
              + "                                                                           \n"
              + "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;     \n"
              + "import org.matheclipse.core.interfaces.IExpr;                              \n"
              + "import org.matheclipse.core.interfaces.IAST;                               \n"
              + "import org.matheclipse.core.eval.EvalEngine;                               \n"
              + "import org.matheclipse.core.expression.F;                                  \n"
              + "import static org.matheclipse.core.expression.F.*;                         \n"
              + "                                                                           \n"
              + "public class CompiledFunction extends AbstractFunctionEvaluator {          \n"
              + "    public IExpr evaluate(final IAST ast, EvalEngine engine){              \n"
              + "        if (ast.argSize()!=1) { return print(ast,1,engine); }     \n"
              + "        IExpr x = ast.get(1);\n"
              + "double xd = engine.evalDouble(x);\n"
              + "                                                       \n"
              + "        return \n"
              + "F.num((20.085536923187664)-Math.cos((9.869604401089358)/xd));\n"
              + "\n"
              + "    }                                                                      \n"
              + "}                                                                          \n"
              + "");
    }
  }

  public void testCompilePrint003() {
    if (ToggleFeature.COMPILE) {
      // A JDK is needed here

      check(
          "CompilePrint({x}, x^3+Cos(x^2))", //
          "/* an in-memory compiled function */                                      \n"
              + "package org.matheclipse.core.compile;                                      \n"
              + "                                                                           \n"
              + "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;     \n"
              + "import org.matheclipse.core.interfaces.IExpr;                              \n"
              + "import org.matheclipse.core.interfaces.IAST;                               \n"
              + "import org.matheclipse.core.eval.EvalEngine;                               \n"
              + "import org.matheclipse.core.expression.F;                                  \n"
              + "import static org.matheclipse.core.expression.F.*;                         \n"
              + "                                                                           \n"
              + "public class CompiledFunction extends AbstractFunctionEvaluator {          \n"
              + "    public IExpr evaluate(final IAST ast, EvalEngine engine){              \n"
              + "        if (ast.argSize()!=1) { return print(ast,1,engine); }     \n"
              + "        IExpr x = ast.get(1);\n"
              + "double xd = engine.evalDouble(x);\n"
              + "                                                       \n"
              + "        return \n"
              + "F.num(Math.pow(xd,3)+Math.cos(Math.pow(xd,2)));\n"
              + "\n"
              + "    }                                                                      \n"
              + "}                                                                          \n"
              + "");
    }
  }

  public void testCompilePrint004() {
    if (ToggleFeature.COMPILE) {
      // A JDK is needed here

      check(
          "CompilePrint({x}, x^3+Gamma(x^2)) ", //
          "/* an in-memory compiled function */                                      \n"
              + "package org.matheclipse.core.compile;                                      \n"
              + "                                                                           \n"
              + "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;     \n"
              + "import org.matheclipse.core.interfaces.IExpr;                              \n"
              + "import org.matheclipse.core.interfaces.IAST;                               \n"
              + "import org.matheclipse.core.eval.EvalEngine;                               \n"
              + "import org.matheclipse.core.expression.F;                                  \n"
              + "import static org.matheclipse.core.expression.F.*;                         \n"
              + "                                                                           \n"
              + "public class CompiledFunction extends AbstractFunctionEvaluator {          \n"
              + "    public IExpr evaluate(final IAST ast, EvalEngine engine){              \n"
              + "        if (ast.argSize()!=1) { return print(ast,1,engine); }     \n"
              + "        IExpr x = ast.get(1);\n"
              + "double xd = engine.evalDouble(x);\n"
              + "                                                       \n"
              + "        return \n"
              + "F.num(Math.pow(xd,3)+F.Gamma.ofN((Math.pow(xd,2))));\n"
              + "\n"
              + "    }                                                                      \n"
              + "}                                                                          \n"
              + "");
    }
  }

  public void testCompilePrint005() {
    if (ToggleFeature.COMPILE) {
      // A JDK is needed here

      check(
          "CompilePrint({x, y}, x + 2*y)", //
          "/* an in-memory compiled function */                                      \n"
              + "package org.matheclipse.core.compile;                                      \n"
              + "                                                                           \n"
              + "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;     \n"
              + "import org.matheclipse.core.interfaces.IExpr;                              \n"
              + "import org.matheclipse.core.interfaces.IAST;                               \n"
              + "import org.matheclipse.core.eval.EvalEngine;                               \n"
              + "import org.matheclipse.core.expression.F;                                  \n"
              + "import static org.matheclipse.core.expression.F.*;                         \n"
              + "                                                                           \n"
              + "public class CompiledFunction extends AbstractFunctionEvaluator {          \n"
              + "    public IExpr evaluate(final IAST ast, EvalEngine engine){              \n"
              + "        if (ast.argSize()!=2) { return print(ast,2,engine); }     \n"
              + "        IExpr x = ast.get(1);\n"
              + "double xd = engine.evalDouble(x);\n"
              + "IExpr y = ast.get(2);\n"
              + "double yd = engine.evalDouble(y);\n"
              + "                                                       \n"
              + "        return \n"
              + "F.num(xd+2*yd);\n"
              + "\n"
              + "    }                                                                      \n"
              + "}                                                                          \n"
              + "");
    }
  }

  public void testCompilePrint006() {
    if (ToggleFeature.COMPILE) {
      // A JDK is needed here

      check(
          "CompilePrint({{x, _Real}}, Sin(x))", //
          "/* an in-memory compiled function */                                      \n"
              + "package org.matheclipse.core.compile;                                      \n"
              + "                                                                           \n"
              + "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;     \n"
              + "import org.matheclipse.core.interfaces.IExpr;                              \n"
              + "import org.matheclipse.core.interfaces.IAST;                               \n"
              + "import org.matheclipse.core.eval.EvalEngine;                               \n"
              + "import org.matheclipse.core.expression.F;                                  \n"
              + "import static org.matheclipse.core.expression.F.*;                         \n"
              + "                                                                           \n"
              + "public class CompiledFunction extends AbstractFunctionEvaluator {          \n"
              + "    public IExpr evaluate(final IAST ast, EvalEngine engine){              \n"
              + "        if (ast.argSize()!=1) { return print(ast,1,engine); }     \n"
              + "        IExpr x = ast.get(1);\n"
              + "double xd = engine.evalDouble(x);\n"
              + "                                                       \n"
              + "        return \n"
              + "F.num(Math.sin(xd));\n"
              + "\n"
              + "    }                                                                      \n"
              + "}                                                                          \n"
              + "");
    }
  }

  public void testCompilePrint007() {
    if (ToggleFeature.COMPILE) {
      // A JDK is needed here

      // message: CompilePrint: Duplicate parameter x found in {{x,_Real},{x,_Integer}}.
      check(
          "CompilePrint({{x, _Real}, {x, _Integer}}, Sin(x + y))", //
          "CompilePrint({{x,_Real},{x,_Integer}},Sin(x+y))");
    }
  }

  public void testCompilePrint008() {
    if (ToggleFeature.COMPILE) {
      // A JDK is needed here

      check(
          "CompilePrint({{x, _Real}, {y, _Integer}}, Sin(x + z))", //
          "/* an in-memory compiled function */                                      \n"
              + "package org.matheclipse.core.compile;                                      \n"
              + "                                                                           \n"
              + "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;     \n"
              + "import org.matheclipse.core.interfaces.IExpr;                              \n"
              + "import org.matheclipse.core.interfaces.IAST;                               \n"
              + "import org.matheclipse.core.eval.EvalEngine;                               \n"
              + "import org.matheclipse.core.expression.F;                                  \n"
              + "import static org.matheclipse.core.expression.F.*;                         \n"
              + "                                                                           \n"
              + "public class CompiledFunction extends AbstractFunctionEvaluator {          \n"
              + "    public IExpr evaluate(final IAST ast, EvalEngine engine){              \n"
              + "        if (ast.argSize()!=2) { return print(ast,2,engine); }     \n"
              + "        IExpr x = ast.get(1);\n"
              + "double xd = engine.evalDouble(x);\n"
              + "IExpr y = ast.get(2);\n"
              + "int yi = engine.evalInt(y);\n"
              + "                                                       \n"
              + "        return \n"
              + "F.Sin(F.Plus(x,F.z));\n"
              + "\n"
              + "    }                                                                      \n"
              + "}                                                                          \n"
              + "");
    }
  }

  public void testCompilePrint009() {
    if (ToggleFeature.COMPILE) {
      // A JDK is needed here

      check(
          "CompilePrint({{x, _Real}, {y, _Integer}}, If(x == 0.0 && y <= 0, 0.0,  Sin(x ^ y) + 1 / Min(x, 0.5)) + 0.5)", //
          "/* an in-memory compiled function */                                      \n"
              + "package org.matheclipse.core.compile;                                      \n"
              + "                                                                           \n"
              + "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;     \n"
              + "import org.matheclipse.core.interfaces.IExpr;                              \n"
              + "import org.matheclipse.core.interfaces.IAST;                               \n"
              + "import org.matheclipse.core.eval.EvalEngine;                               \n"
              + "import org.matheclipse.core.expression.F;                                  \n"
              + "import static org.matheclipse.core.expression.F.*;                         \n"
              + "                                                                           \n"
              + "public class CompiledFunction extends AbstractFunctionEvaluator {          \n"
              + "    public IExpr evaluate(final IAST ast, EvalEngine engine){              \n"
              + "        if (ast.argSize()!=2) { return print(ast,2,engine); }     \n"
              + "        IExpr x = ast.get(1);\n"
              + "double xd = engine.evalDouble(x);\n"
              + "IExpr y = ast.get(2);\n"
              + "int yi = engine.evalInt(y);\n"
              + "                                                       \n"
              + "        return \n"
              + "F.Plus(F.If(F.And(F.Equal(x,F.CD0),F.LessEqual(y,F.C0)),F.CD0,F.Plus(F.Sin(F.Power(x,y)),F.Power(F.Min(x,F.num(0.5)),F.CN1))),F.num(0.5));\n"
              + "\n"
              + "    }                                                                      \n"
              + "}                                                                          \n"
              + "");
    }
  }

  /** The JUnit setup method */
  @Override
  protected void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
