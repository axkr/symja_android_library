package org.matheclipse.core.system;

import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for compiler functions */
public class CompilerFunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testCompileSinReal001() {
    if (ToggleFeature.COMPILE) {

      check("cf = Compile({{x}}, x^2 + Sin(x^2));", //
          "");
      check("cf(Pi)", //
          "9.4393");
      check("Pi^2 + Sin(Pi^2) // N", //
          "9.4393");
    }
  }

  @Test
  public void testCompileSinReal002() {
    if (ToggleFeature.COMPILE) {

      check("cf = Compile({{x, _Real}}, Sin(x) + x^2 - 1/(1 + x));", //
          "");
      check("cf(Pi)", //
          "9.62815");
    }
  }

  @Test
  public void testCompileSinReal003() {
    if (ToggleFeature.COMPILE) {

      check("cf = Compile({{x, _Real}}, Sin(x)-x^2);", //
          "");
      check("cf(-1.3)", //
          "-2.65356");

      check("Sin(-1.3)-(-1.3)^2", //
          "-2.65356");
    }
  }

  // public void testCompileSurdReal001() {
  // if (ToggleFeature.COMPILE) {
  //
  // check("cf = Compile({{x, _Real}}, Surd(x,2));", //
  // "");
  // check("cf(7)", //
  // "-2.65356");
  //
  // check("Sin(-1.3)-(-1.3)^2", //
  // "-2.65356");
  // }
  // }

  @Test
  public void testCompileSinComplex() {
    if (ToggleFeature.COMPILE) {
      check("cf = Compile({{x, _Real}}, xr=Sin(x) + x^2 - 1/(1 + x);xr+1);", //
          "");
      check("cf(Pi)", //
          "10.62815");

      check("cf = Compile({{x, _Complex}}, Sin(x) + x^2 - 1/(1 + x));", //
          "");
      check("cf(Pi)", //
          "9.62815");
      check("cf(I*(-3.0))", //
          "-9.1+I*(-10.31787)");
    }
  }

  @Test
  public void testCompileModuleComplex() {
    if (ToggleFeature.COMPILE) {
      check(
          "CompilePrint({{z, _Complex}, {n, _Integer}}, Module({zn = z},\n"
              + "   Do(zn = (2*zn + 1/zn^2)/3, {n}); \n"
              + "   If(Re(zn) > 0, 1, If(Im(zn)> 0, 2, 3))));", //
          "");
      check(
          "newt = Compile({{z, _Complex}, {n, _Integer}}, Module({zn = z},\n"
              + "   Do(zn = (2*zn + 1/zn^2)/3, {n}); \n"
              + "   If(Re(zn) > 0, 1, If(Im(zn)> 0, 2, 3))))", //
          "CompiledFunction(Arg count: 2 Types: {Complex,Integer} Variables: {z,n})");
      check("newt(0.5+I*0.75,25)", //
          "3.0");

      check("newt = CompilePrint({{z, _Real}, {n, _Integer}}, Module({zn = z},\n"
          // + " Do(zn = (2*zn + 1/zn^2)/3, {n});Print(zn); \n"
          + "   If(Re(zn) > 0, 1, If(Im(zn)> 0, 2, 3))));", //
          "");
      check("newt = Compile({{z, _Real}, {n, _Integer}}, Module({zn = z},\n"
          // + " Do(zn = (2*zn + 1/zn^2)/3, {n});Print(zn); \n"
          + "   If(Re(zn) > 0, 1, If(Im(zn)> 0, 2, 3))));", //
          "");
      check("newt(-0.75,25)", //
          "3.0");
    }
  }

  @Test
  public void testCompileModuleOverflow() {
    if (ToggleFeature.COMPILE) {
      // check(
      // "CompilePrint({{n, _Integer}},\n" //
      // + " Module({s = 1}, Do(s = (2*s + i), {i, n}); s))", //
      // "3.80295*10^30 ");
      check("fi1 = Compile({{n, _Integer}},\n" //
          + "   Module({s = 1}, Do(s = (2*s + i), {i, n}); s));\n" //
          + "fi1(100)", //
          "3802951800684688204490109616026");
    }
  }

  @Test
  public void testCompileSqrtException() {
    if (ToggleFeature.COMPILE) {

      check("cf = Compile({x, y}, Sqrt(x*y));", //
          "");
      check("cf(1.0,2.0)", //
          "1.41421");
      check("cf(-1.0,2.0)", //
          "I*1.41421");
      check("cf2 = Compile({x, y}, x+y);", //
          "");
      check("cf2(1.0,2.0)", //
          "3.0");
      check("cf(1.0,2.0)", //
          "1.41421");
    }
  }

  @Test
  public void testCompile001() {
    if (ToggleFeature.COMPILE) {

      // test with random result

      // check(
      // "f = Compile({{n, _Integer}},\n"
      // + " Module({p = Range(n),i,x,t},\n"
      // + " Do(x = RandomInteger({1,i});\n"
      // + " t = p[[i]]; p[[i]] = p[[x]]; p[[x]] = t,\n"
      // + " {i,n,2,-1}\n"
      // + " );\n"
      // + " p\n"
      // + " )\n"
      // + " );", //
      // "");
      // check(
      // " f(4)", //
      // "{2,4,1,3}");

      check("f=Compile({{x, _Real}}, E^3-Cos(Pi^2/x));  ", //
          "");
      check("f(1.4567)", //
          "19.20421");

      // wrong input test
      check("f=Compile({x, _Real}, E^3-Cos(Pi^2/x));  ", //
          "");
      // error: CompiledFunction: CompiledFunction(Arg count: 2 Types: {Real,Real} Variables:
      // {x,_Real})[1.4567] called with 1 arguments; 2 arguments are expected.
      check("f(1.4567)", //
          "CompiledFunction(Arg count: 2 Types: {Real,Real} Variables: {x,_Real})[1.4567]");

      check("f=Compile({x}, x^3+Cos(x^2)); ", //
          "");
      check(" f(1.4567)", //
          "2.56739");

      check("f=Compile({x}, x^3+Gamma(x^2)); ", //
          "");
      check(" f(1.4567)", //
          "4.14894");
    }
  }

  @Test
  public void testCompilePrintModule001() {
    if (ToggleFeature.COMPILE) {
      check(
          "CompilePrint({{n, _Integer}}, Module({p = Range(n),i,x,t}, Do(x = RandomInteger({1,i}); t = p[[i]]; p[[i]] = p[[x]]; p[[x]] = t,{i,n,2,-1}); p))", //
          "/** Compile with <a href=\"https://github.com/janino-compiler/janino\">Janino compiler</a> */\n"
              + "package org.matheclipse.core.compile;\n" + "\n" + "import java.util.ArrayList;\n"
              + "import org.hipparchus.complex.Complex;\n"
              + "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;\n"
              + "import org.matheclipse.core.interfaces.*;\n"
              + "import org.matheclipse.core.eval.EvalEngine;\n"
              + "import org.matheclipse.core.expression.ExprTrie;\n"
              + "import org.matheclipse.core.expression.S;\n"
              + "import static org.matheclipse.core.expression.S.*;\n"
              + "import org.matheclipse.core.expression.F;\n"
              + "import static org.matheclipse.core.expression.F.*;\n" + "\n"
              + "public class CompiledFunction extends AbstractFunctionEvaluator {\n"
              + "  EvalEngine engine;\n" + "  IASTAppendable stack;\n" + "  ExprTrie vars;\n"
              + "  int top=1;\n" + "  public IExpr evaluate(final IAST ast, EvalEngine engine){\n"
              + "    if (ast.argSize()!=1) { return print(ast,1,engine); }\n"
              + "    this.engine = engine;\n" + "    stack  = F.ast(S.List, 100, true);\n"
              + "    vars = new ExprTrie();\n" + "    IExpr n = ast.get(1);\n"
              + "    int ni = engine.evalInt(n);\n" + "    stack.set(top++, F.ZZ(ni));\n" + "    \n"
              + "    return moduleExpression1();\n" + "    \n" + "  }\n"
              + "  public double evalDouble(IExpr expr)  {\n"
              + "    return engine.evalDouble(expr);\n" + "  }\n" + "  \n"
              + "  public Complex evalComplex(IExpr expr)  {\n"
              + "    return engine.evalComplex(expr);\n" + "  }\n" + "  \n"
              + "  public IExpr moduleExpression1() {\n"
              + "    ExprTrie oldVars = vars; int oldTop =  top;\n" + "    try {\n"
              + "      vars = vars.copy();\n" + "      ISymbol p = F.Dummy(\"p\");\n"
              + "      vars.put(\"p\",p);\n" + "      F.eval(F.Set(p,F.Range(stack.get(1))));\n"
              + "      ISymbol i = F.Dummy(\"i\");\n" + "      vars.put(\"i\",i);\n"
              + "      ISymbol x = F.Dummy(\"x\");\n" + "      vars.put(\"x\",x);\n"
              + "      ISymbol t = F.Dummy(\"t\");\n" + "      vars.put(\"t\",t);\n"
              + "      return compoundExpression2();\n"
              + "    } finally {top = oldTop; vars = oldVars;}\n" + "  }\n" + "  \n"
              + "  public IExpr compoundExpression2() {\n" + "    int oldTop =  top;\n"
              + "    try {\n"
              + "      F.eval(F.Do(F.CompoundExpression(F.Set(vars.get(\"x\"),F.RandomInteger(F.list(F.C1,vars.get(\"i\")))),F.Set(vars.get(\"t\"),F.Part(vars.get(\"p\"),vars.get(\"i\"))),F.Set(F.Part(vars.get(\"p\"),vars.get(\"i\")),F.Part(vars.get(\"p\"),vars.get(\"x\"))),F.Set(F.Part(vars.get(\"p\"),vars.get(\"x\")),vars.get(\"t\"))),F.List(\n"
              + "      vars.get(\"i\"),\n" + "      stack.get(1),\n" + "      F.C2,\n"
              + "      F.CN1\n" + "      )));\n" + "      return F.eval(vars.get(\"p\"));\n"
              + "    } finally {top = oldTop;}\n" + "  }\n" + "  \n" + "  \n" + "}\n" + "");
      // check(
      // "f = Compile({{n, _Integer}}, Module({p = Range(n),i,x,t}, Do(x =
      // RandomInteger({1,i}); t = p[[i]]; p[[i]] = p[[x]]; p[[x]] = t,{i,n,2,-1}); p));", //
      // "");
      // check(
      // " f(4)", //
      // "{3,1,2,4}");
    }
  }

  @Test
  public void testCompile0021() {
    if (ToggleFeature.COMPILE) {

      check("cf = Compile({{x, _Real}, {y, _Integer}}, Sin(x + y));", //
          "");
      check("cf(1,2)", //
          "0.14112");
      // message: ... called with 1 arguments; 2 arguments are expected.
      check("cf(x+y)", //
          "CompiledFunction(Arg count: 2 Types: {Real,Integer} Variables: {x,y})[x+y]");
    }
  }

  @Test
  public void testCompilePrint002() {
    if (ToggleFeature.COMPILE) {
      check("f=CompilePrint({x}, E^3-Cos(Pi^2/x))", //
          "/** Compile with <a href=\"https://github.com/janino-compiler/janino\">Janino compiler</a> */\n" //
              + "package org.matheclipse.core.compile;\n" //
              + "\n" //
              + "import java.util.ArrayList;\n" //
              + "import org.hipparchus.complex.Complex;\n" //
              + "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;\n" //
              + "import org.matheclipse.core.interfaces.*;\n" //
              + "import org.matheclipse.core.eval.EvalEngine;\n" //
              + "import org.matheclipse.core.expression.ExprTrie;\n" //
              + "import org.matheclipse.core.expression.S;\n" //
              + "import static org.matheclipse.core.expression.S.*;\n" //
              + "import org.matheclipse.core.expression.F;\n" //
              + "import static org.matheclipse.core.expression.F.*;\n" //
              + "\n" //
              + "public class CompiledFunction extends AbstractFunctionEvaluator {\n" //
              + "  EvalEngine engine;\n" //
              + "  IASTAppendable stack;\n" //
              + "  ExprTrie vars;\n" //
              + "  int top=1;\n" //
              + "  public IExpr evaluate(final IAST ast, EvalEngine engine){\n" //
              + "    if (ast.argSize()!=1) { return print(ast,1,engine); }\n" //
              + "    this.engine = engine;\n" //
              + "    stack  = F.ast(S.List, 100, true);\n" //
              + "    vars = new ExprTrie();\n" //
              + "    IExpr x = ast.get(1);\n" //
              + "    double xd = engine.evalDouble(x);\n" //
              + "    stack.set(top++, F.num(xd));\n" //
              + "    \n" //
              + "    return F.num(20.085536923187668-Math.cos(9.869604401089358/evalDouble(stack.get(1))));\n" //
              + "    \n" //
              + "  }\n" //
              + "  public double evalDouble(IExpr expr)  {\n" //
              + "    return engine.evalDouble(expr);\n" //
              + "  }\n" //
              + "  \n" //
              + "  public Complex evalComplex(IExpr expr)  {\n" //
              + "    return engine.evalComplex(expr);\n" //
              + "  }\n" //
              + "  \n" //
              + "  \n" //
              + "}\n" //
              + "");
    }
  }

  @Test
  public void testCompilePrint003() {
    if (ToggleFeature.COMPILE) {

      check("CompilePrint({x}, x^3+Cos(x^2))", //
          "/** Compile with <a href=\"https://github.com/janino-compiler/janino\">Janino compiler</a> */\n"
              + "package org.matheclipse.core.compile;\n" + "\n" + "import java.util.ArrayList;\n"
              + "import org.hipparchus.complex.Complex;\n"
              + "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;\n"
              + "import org.matheclipse.core.interfaces.*;\n"
              + "import org.matheclipse.core.eval.EvalEngine;\n"
              + "import org.matheclipse.core.expression.ExprTrie;\n"
              + "import org.matheclipse.core.expression.S;\n"
              + "import static org.matheclipse.core.expression.S.*;\n"
              + "import org.matheclipse.core.expression.F;\n"
              + "import static org.matheclipse.core.expression.F.*;\n" + "\n"
              + "public class CompiledFunction extends AbstractFunctionEvaluator {\n"
              + "  EvalEngine engine;\n" + "  IASTAppendable stack;\n" + "  ExprTrie vars;\n"
              + "  int top=1;\n" + "  public IExpr evaluate(final IAST ast, EvalEngine engine){\n"
              + "    if (ast.argSize()!=1) { return print(ast,1,engine); }\n"
              + "    this.engine = engine;\n" + "    stack  = F.ast(S.List, 100, true);\n"
              + "    vars = new ExprTrie();\n" + "    IExpr x = ast.get(1);\n"
              + "    double xd = engine.evalDouble(x);\n" + "    stack.set(top++, F.num(xd));\n"
              + "    \n"
              + "    return F.num(Math.pow(evalDouble(stack.get(1)),3)+Math.cos(Math.pow(evalDouble(stack.get(1)),2)));\n"
              + "    \n" + "  }\n" + "  public double evalDouble(IExpr expr)  {\n"
              + "    return engine.evalDouble(expr);\n" + "  }\n" + "  \n"
              + "  public Complex evalComplex(IExpr expr)  {\n"
              + "    return engine.evalComplex(expr);\n" + "  }\n" + "  \n" + "  \n" + "}\n" + "");
    }
  }

  @Test
  public void testCompilePrint004() {
    if (ToggleFeature.COMPILE) {

      check("CompilePrint({x}, x^3+Gamma(x^2)) ", //
          "/** Compile with <a href=\"https://github.com/janino-compiler/janino\">Janino compiler</a> */\n"
              + "package org.matheclipse.core.compile;\n" + "\n" + "import java.util.ArrayList;\n"
              + "import org.hipparchus.complex.Complex;\n"
              + "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;\n"
              + "import org.matheclipse.core.interfaces.*;\n"
              + "import org.matheclipse.core.eval.EvalEngine;\n"
              + "import org.matheclipse.core.expression.ExprTrie;\n"
              + "import org.matheclipse.core.expression.S;\n"
              + "import static org.matheclipse.core.expression.S.*;\n"
              + "import org.matheclipse.core.expression.F;\n"
              + "import static org.matheclipse.core.expression.F.*;\n" + "\n"
              + "public class CompiledFunction extends AbstractFunctionEvaluator {\n"
              + "  EvalEngine engine;\n" + "  IASTAppendable stack;\n" + "  ExprTrie vars;\n"
              + "  int top=1;\n" + "  public IExpr evaluate(final IAST ast, EvalEngine engine){\n"
              + "    if (ast.argSize()!=1) { return print(ast,1,engine); }\n"
              + "    this.engine = engine;\n" + "    stack  = F.ast(S.List, 100, true);\n"
              + "    vars = new ExprTrie();\n" + "    IExpr x = ast.get(1);\n"
              + "    double xd = engine.evalDouble(x);\n" + "    stack.set(top++, F.num(xd));\n"
              + "    \n"
              + "    return F.num(Math.pow(evalDouble(stack.get(1)),3)+F.Gamma.ofN(Math.pow(evalDouble(stack.get(1)),2)));\n"
              + "    \n" + "  }\n" + "  public double evalDouble(IExpr expr)  {\n"
              + "    return engine.evalDouble(expr);\n" + "  }\n" + "  \n"
              + "  public Complex evalComplex(IExpr expr)  {\n"
              + "    return engine.evalComplex(expr);\n" + "  }\n" + "  \n" + "  \n" + "}\n" + "");
    }
  }

  @Test
  public void testCompilePrint005() {
    if (ToggleFeature.COMPILE) {

      check("CompilePrint({x, y}, x + 2*y)", //
          "/** Compile with <a href=\"https://github.com/janino-compiler/janino\">Janino compiler</a> */\n"
              + "package org.matheclipse.core.compile;\n" + "\n" + "import java.util.ArrayList;\n"
              + "import org.hipparchus.complex.Complex;\n"
              + "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;\n"
              + "import org.matheclipse.core.interfaces.*;\n"
              + "import org.matheclipse.core.eval.EvalEngine;\n"
              + "import org.matheclipse.core.expression.ExprTrie;\n"
              + "import org.matheclipse.core.expression.S;\n"
              + "import static org.matheclipse.core.expression.S.*;\n"
              + "import org.matheclipse.core.expression.F;\n"
              + "import static org.matheclipse.core.expression.F.*;\n" + "\n"
              + "public class CompiledFunction extends AbstractFunctionEvaluator {\n"
              + "  EvalEngine engine;\n" + "  IASTAppendable stack;\n" + "  ExprTrie vars;\n"
              + "  int top=1;\n" + "  public IExpr evaluate(final IAST ast, EvalEngine engine){\n"
              + "    if (ast.argSize()!=2) { return print(ast,2,engine); }\n"
              + "    this.engine = engine;\n" + "    stack  = F.ast(S.List, 100, true);\n"
              + "    vars = new ExprTrie();\n" + "    IExpr x = ast.get(1);\n"
              + "    double xd = engine.evalDouble(x);\n" + "    stack.set(top++, F.num(xd));\n"
              + "    IExpr y = ast.get(2);\n" + "    double yd = engine.evalDouble(y);\n"
              + "    stack.set(top++, F.num(yd));\n" + "    \n"
              + "    return F.num(evalDouble(stack.get(1))+2*evalDouble(stack.get(2)));\n"
              + "    \n" + "  }\n" + "  public double evalDouble(IExpr expr)  {\n"
              + "    return engine.evalDouble(expr);\n" + "  }\n" + "  \n"
              + "  public Complex evalComplex(IExpr expr)  {\n"
              + "    return engine.evalComplex(expr);\n" + "  }\n" + "  \n" + "  \n" + "}\n" + "");
    }
  }

  @Test
  public void testCompilePrint006() {
    if (ToggleFeature.COMPILE) {

      check("CompilePrint({{x, _Real}}, Sin(x))", //
          "/** Compile with <a href=\"https://github.com/janino-compiler/janino\">Janino compiler</a> */\n"
              + "package org.matheclipse.core.compile;\n" + "\n" + "import java.util.ArrayList;\n"
              + "import org.hipparchus.complex.Complex;\n"
              + "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;\n"
              + "import org.matheclipse.core.interfaces.*;\n"
              + "import org.matheclipse.core.eval.EvalEngine;\n"
              + "import org.matheclipse.core.expression.ExprTrie;\n"
              + "import org.matheclipse.core.expression.S;\n"
              + "import static org.matheclipse.core.expression.S.*;\n"
              + "import org.matheclipse.core.expression.F;\n"
              + "import static org.matheclipse.core.expression.F.*;\n" + "\n"
              + "public class CompiledFunction extends AbstractFunctionEvaluator {\n"
              + "  EvalEngine engine;\n" + "  IASTAppendable stack;\n" + "  ExprTrie vars;\n"
              + "  int top=1;\n" + "  public IExpr evaluate(final IAST ast, EvalEngine engine){\n"
              + "    if (ast.argSize()!=1) { return print(ast,1,engine); }\n"
              + "    this.engine = engine;\n" + "    stack  = F.ast(S.List, 100, true);\n"
              + "    vars = new ExprTrie();\n" + "    IExpr x = ast.get(1);\n"
              + "    double xd = engine.evalDouble(x);\n" + "    stack.set(top++, F.num(xd));\n"
              + "    \n" + "    return F.num(Math.sin(evalDouble(stack.get(1))));\n" + "    \n"
              + "  }\n" + "  public double evalDouble(IExpr expr)  {\n"
              + "    return engine.evalDouble(expr);\n" + "  }\n" + "  \n"
              + "  public Complex evalComplex(IExpr expr)  {\n"
              + "    return engine.evalComplex(expr);\n" + "  }\n" + "  \n" + "  \n" + "}\n" + "");
    }
  }

  @Test
  public void testCompilePrint007() {
    if (ToggleFeature.COMPILE) {

      // message: CompilePrint: Duplicate parameter x found in {{x,_Real},{x,_Integer}}.
      check("CompilePrint({{x, _Real}, {x, _Integer}}, Sin(x + y))", //
          "CompilePrint({{x,_Real},{x,_Integer}},Sin(x+y))");
    }
  }

  @Test
  public void testCompilePrint008() {
    if (ToggleFeature.COMPILE) {

      check("CompilePrint({{x, _Real}, {y, _Integer}}, Sin(x + z))", //
          "/** Compile with <a href=\"https://github.com/janino-compiler/janino\">Janino compiler</a> */\n"
              + "package org.matheclipse.core.compile;\n" + "\n" + "import java.util.ArrayList;\n"
              + "import org.hipparchus.complex.Complex;\n"
              + "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;\n"
              + "import org.matheclipse.core.interfaces.*;\n"
              + "import org.matheclipse.core.eval.EvalEngine;\n"
              + "import org.matheclipse.core.expression.ExprTrie;\n"
              + "import org.matheclipse.core.expression.S;\n"
              + "import static org.matheclipse.core.expression.S.*;\n"
              + "import org.matheclipse.core.expression.F;\n"
              + "import static org.matheclipse.core.expression.F.*;\n" + "\n"
              + "public class CompiledFunction extends AbstractFunctionEvaluator {\n"
              + "  EvalEngine engine;\n" + "  IASTAppendable stack;\n" + "  ExprTrie vars;\n"
              + "  int top=1;\n" + "  public IExpr evaluate(final IAST ast, EvalEngine engine){\n"
              + "    if (ast.argSize()!=2) { return print(ast,2,engine); }\n"
              + "    this.engine = engine;\n" + "    stack  = F.ast(S.List, 100, true);\n"
              + "    vars = new ExprTrie();\n" + "    IExpr x = ast.get(1);\n"
              + "    double xd = engine.evalDouble(x);\n" + "    stack.set(top++, F.num(xd));\n"
              + "    IExpr y = ast.get(2);\n" + "    int yi = engine.evalInt(y);\n"
              + "    stack.set(top++, F.ZZ(yi));\n" + "    \n"
              + "    return F.eval(F.Sin(F.Plus(stack.get(1),F.z)));\n" + "    \n" + "  }\n"
              + "  public double evalDouble(IExpr expr)  {\n"
              + "    return engine.evalDouble(expr);\n" + "  }\n" + "  \n"
              + "  public Complex evalComplex(IExpr expr)  {\n"
              + "    return engine.evalComplex(expr);\n" + "  }\n" + "  \n" + "  \n" + "}\n" + "");
    }
  }

  @Test
  public void testCompilePrint009() {
    if (ToggleFeature.COMPILE) {

      check(
          "CompilePrint({{x, _Real}, {y, _Integer}}, If(x == 0.0 && y <= 0, 0.0,  Sin(x ^ y) + 1 / Min(x, 0.5)) + 0.5)", //
          "/** Compile with <a href=\"https://github.com/janino-compiler/janino\">Janino compiler</a> */\n"
              + "package org.matheclipse.core.compile;\n" + "\n" + "import java.util.ArrayList;\n"
              + "import org.hipparchus.complex.Complex;\n"
              + "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;\n"
              + "import org.matheclipse.core.interfaces.*;\n"
              + "import org.matheclipse.core.eval.EvalEngine;\n"
              + "import org.matheclipse.core.expression.ExprTrie;\n"
              + "import org.matheclipse.core.expression.S;\n"
              + "import static org.matheclipse.core.expression.S.*;\n"
              + "import org.matheclipse.core.expression.F;\n"
              + "import static org.matheclipse.core.expression.F.*;\n" + "\n"
              + "public class CompiledFunction extends AbstractFunctionEvaluator {\n"
              + "  EvalEngine engine;\n" + "  IASTAppendable stack;\n" + "  ExprTrie vars;\n"
              + "  int top=1;\n" + "  public IExpr evaluate(final IAST ast, EvalEngine engine){\n"
              + "    if (ast.argSize()!=2) { return print(ast,2,engine); }\n"
              + "    this.engine = engine;\n" + "    stack  = F.ast(S.List, 100, true);\n"
              + "    vars = new ExprTrie();\n" + "    IExpr x = ast.get(1);\n"
              + "    double xd = engine.evalDouble(x);\n" + "    stack.set(top++, F.num(xd));\n"
              + "    IExpr y = ast.get(2);\n" + "    int yi = engine.evalInt(y);\n"
              + "    stack.set(top++, F.ZZ(yi));\n" + "    \n"
              + "    return F.eval(F.Plus(F.If(F.And(F.Equal(stack.get(1),F.CD0),F.LessEqual(stack.get(2),F.C0)),F.CD0,F.Plus(F.Sin(F.Power(stack.get(1),stack.get(2))),F.Power(F.Min(stack.get(1),F.num(0.5)),F.CN1))),F.num(0.5)));\n"
              + "    \n" + "  }\n" + "  public double evalDouble(IExpr expr)  {\n"
              + "    return engine.evalDouble(expr);\n" + "  }\n" + "  \n"
              + "  public Complex evalComplex(IExpr expr)  {\n"
              + "    return engine.evalComplex(expr);\n" + "  }\n" + "  \n" + "  \n" + "}\n" + "");
    }
  }

  @Test
  public void testCompileDP1() {
    if (ToggleFeature.COMPILE) {
      // argument p is a matrix
      // check("DP1 = Compile({{p, _Real, 2}, {m, _Integer}},\n" //
      // + " Module({np = p, k, n = Length(p)},\n" //
      // + " Do(np = Table(If((np[[i, k]] == 1.0*m) || (np[[k, j]] == 1.0*m), \n" //
      // + " np[[i,j]], Min(np[[i,k]]+ np[[k,j]], np[[i,j]])\n" //
      // + " ), {i,n},{j,n}\r\n" //
      // + " ), {k, n});\n" //
      // + " np\n" //
      // + " )\n" //
      // + " )", //
      // "");
    }
  }

  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
