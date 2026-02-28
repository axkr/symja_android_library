package org.matheclipse.core.system;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for compiler functions */
public class CompilerFunctionsTest extends ExprEvaluatorTestCase {

  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    ToggleFeature.COMPILE = true;
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @AfterEach
  public void tearDown() throws Exception {
    // super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
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
          "3");
    }
  }

  @Test
  public void testCompileModuleOverflow() {
    if (ToggleFeature.COMPILE) {
      check("fi1 = Compile({{n, _Integer}},\n" //
          + "   Module({s = 1}, Do(s = (2*s + i), {i, n}); s));\n" //
          + "fi1(100)", //
          "3.80295*10^30");
    }
  }

  @Test
  public void testCompile002() {
    if (ToggleFeature.COMPILE) {
      check("f=Compile({x}, E^3-Cos(Pi^2/x));", //
          "");
      check("f(10.0)", //
          "19.53431");
    }
  }

  @Test
  public void testCompile003() {
    if (ToggleFeature.COMPILE) {
      check("f=Compile({x}, x^3+Cos(x^2));", //
          "");
      check("f(0.5)", //
          "1.09391");
    }
  }

  @Test
  public void testCompile004() {
    if (ToggleFeature.COMPILE) {
      check("f=Compile({x}, x^3+Gamma(x^2));", //
          "");
      check("f(1.1)", //
          "2.24658");
    }
  }

  @Test
  public void testCompile005() {
    if (ToggleFeature.COMPILE) {

      check("f=Compile({x, y}, x + 2*y);", //
          "");
      check("f(2,3)", //
          "8.0");
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
  public void testCompile008() {
    if (ToggleFeature.COMPILE) {

      check("f=Compile({{x, _Real}, {y, _Integer}}, Sin(x + z));", //
          "");
      check("f(Pi/2, 42)", //
          "Sin(1.5708+z)");

      check("f=Compile({{x, _Real}, {y, _Integer}}, Sin(x + y));", //
          "");
      check("f(Pi/2, 42)", //
          "-0.399985");
    }
  }

  @Test
  public void testCompile009() {
    if (ToggleFeature.COMPILE) {

      check(
          "f=Compile({{x, _Real}, {y, _Integer}}, If(x == 0.0 && y <= 0, 0.0,  Sin(x ^ y) + 1 / Min(x, 0.5)) + 0.5);", //
          "");
      check("f(0.0, -1)", //
          "0.5");
      check("f(0.0, 2)", //
          "ComplexInfinity");
      check("f(1.0, -1)", //
          "3.34147");
    }
  }

  @Test
  public void testCompilePrintModule001() {
    if (ToggleFeature.COMPILE) {
      check(
          "f=CompilePrint({{n, _Integer}}, Module({p = Range(n),i,x,t}, Do(x = RandomInteger({1,i}); t = p[[i]]; p[[i]] = p[[x]]; p[[x]] = t,{i,n,2,-1}); p));", //
          "");
      // check("f(10)", //
      // "{3,8,1,2,9,7,4,5,10,6}");
    }
  }

  @Test
  public void testCompileReal001() {
    if (ToggleFeature.COMPILE) {

      check("f=Compile({{x, _Real}}, Sin(x));", //
          "");
      check("f(Pi)", //
          "1.22465*10^-16");
    }
  }

  @Test
  public void testCompileReal002() {
    if (ToggleFeature.COMPILE) {
      check("cp=Compile({{x, _Real}}, AiryAi(x)+BesselJ(x,0.5));", //
          "");
      check("cp(3.5)", //
          "0.00324648");
    }
  }

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
  public void testCompileLogGammaComplex() {
    if (ToggleFeature.COMPILE) {
      check("cf = Compile({{x, _Real}}, xr=LogGamma(x) + x^2 - 1/(1 + x);xr+1);", //
          "");
      check("cf(Pi)", //
          "11.45585");

      check("cf = Compile({{x, _Complex}}, LogGamma(x) + x^2 - 1/(1 + x));", //
          "");
      check("cf(Pi)", //
          "10.45585");
      check("cf(I*(-3.0))", //
          "-13.44276+I*0.217446");
    }
  }

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
  public void testCompileWhile() {
    if (ToggleFeature.COMPILE) {
      // Compile( (n, _Integer), Module( (i = 0, sum = 0), While(i < n, i = i + 1; sum = sum + i);
      // sum) )
      check("cf = Compile({{n, _Integer}},\n" //
          + "  Module({i = 0, sum = 0},\n" //
          + "    While(i < n,\n" //
          + "      i = i + 1;\n" //
          + "      sum = sum + i\n" //
          + "    );\n" //
          + "    sum\n" //
          + "  )\n" //
          + ");", //
          "");

      // Test the compiled while loop logic
      check("cf(5)", // 1 + 2 + 3 + 4 + 5 = 15
          "15.0");
      check("cf(10)", // 1 + 2 + ... + 10 = 55
          "55.0");
    }
  }
}
