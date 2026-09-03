package org.matheclipse.compile.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for compiler functions */
public class CompilerFunctionsTest extends AbstractTestCase {

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
      // message "cfct": the number of arguments (1) does not match the argument template length (2)
      check("f(1.4567)", //
          "CompiledFunction({x,_Real})");

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
      // message "cfct": the number of arguments (1) does not match the argument template length (2)
      check("cf(x+y)", //
          "CompiledFunction({x,y})");
    }
  }

  @Test
  public void testCompileIntegerVector() {
    if (ToggleFeature.COMPILE) {
      // argument v is a 1D integer vector
      check("iv1 = Compile({{v, _Integer, 1}, {c, _Integer}},\n" //
          + " Module({res = v, n = Length(v)},\n" //
          + " Do(res = Table(res[[i]] * c + 2, {i, n}), {k, 1});\n" //
          + " res\n" //
          + " )\n" //
          + " );", //
          "");

      // Test the compiled integer vector algorithm
      // Vector v = {1, 2, 3}
      // Scalar c = 3
      // res[[1]] = 1 * 3 + 2 = 5
      // res[[2]] = 2 * 3 + 2 = 8
      // res[[3]] = 3 * 3 + 2 = 11
      check("iv1({1, 2, 3}, 3)", //
          "{5,8,11}");
    }
  }

  @Test
  public void testCompileIntegerMatrix() {
    if (ToggleFeature.COMPILE) {
      // argument m is a 2D integer matrix
      check("im1 = Compile({{m, _Integer, 2}, {c, _Integer}},\n" //
          + " Module({res = m, n = Length(m)},\n" //
          + " Do(res = Table(res[[i, j]] * c - 1, {i, n}, {j, n}), {k, 1});\n" //
          + " res\n" //
          + " )\n" //
          + " );", //
          "");

      // Test the compiled integer matrix algorithm
      // Matrix m = {{1, 2}, {3, 4}}
      // Scalar c = 2
      // res[[1,1]] = 1 * 2 - 1 = 1
      // res[[1,2]] = 2 * 2 - 1 = 3
      // res[[2,1]] = 3 * 2 - 1 = 5
      // res[[2,2]] = 4 * 2 - 1 = 7
      check("im1({{1, 2}, {3, 4}}, 2)", //
          "{{1,3},{5,7}}");
    }
  }

  @Test
  public void testCompileDP1() {
    if (ToggleFeature.COMPILE) {
      // argument p is a matrix
      check("dp1 = Compile({{p, _Real, 2}, {m, _Integer}},\n" //
          + " Module({np = p, k, n = Length(p)},\n" //
          + " Do(np = Table(If((np[[i, k]] == 1.0*m) || (np[[k, j]] == 1.0*m), \n" //
          + " np[[i,j]], Min(np[[i,k]]+ np[[k,j]], np[[i,j]])\n" //
          + " ), {i,n},{j,n}\r\n" //
          + " ), {k, n});\n" //
          + " np\n" //
          + " )\n" //
          + " );", //
          "");
      // Test the compiled shortest path (Floyd-Warshall) algorithm
      // Node 1 -> Node 2: 5.0
      // Node 1 -> Node 4: 10.0 (but Node 1 -> Node 2 -> Node 3 -> Node 4 = 5 + 3 + 1 = 9.0)
      // Disconnected paths are represented by the "infinity" weight: m = 99
      check("dp1({{0.0, 5.0, 99.0, 10.0}, " //
          + "     {99.0, 0.0, 3.0, 99.0}, " //
          + "     {99.0, 99.0, 0.0, 1.0}, " //
          + "     {99.0, 99.0, 99.0, 0.0}}, 99)", //
          // The compiled function should successfully substitute the 10.0 with the shorter path 9.0
          // and establish the 1->3 route as 8.0
          "{{0.0,5.0,8.0,9.0},{99.0,0.0,3.0,4.0},{99.0,99.0,0.0,1.0},{99.0,99.0,99.0,0.0}}");
    }
  }

  @Test
  public void testCompileComplexVector() {
    if (ToggleFeature.COMPILE) {
      // argument v is a 1D complex vector
      check("cv1 = Compile({{v, _Complex, 1}, {c, _Complex}},\n" //
          + " Module({res = v, n = Length(v)},\n" //
          + " Do(res = Table(res[[i]] * c + 1.0, {i, n}), {k, 1});\n" //
          + " res\n" //
          + " )\n" //
          + " );", //
          "");

      // Test the compiled complex vector algorithm
      // Vector v = {1.0+I, 2.0-I}
      // Scalar c = 1.0+I
      // res[[1]] = (1.0+I)*(1.0+I) + 1.0 = (1.0 + 2.0*I - 1.0) + 1.0 = 1.0 + 2.0*I
      // res[[2]] = (2.0-I)*(1.0+I) + 1.0 = (2.0 + 2.0*I - I + 1.0) + 1.0 = 4.0 + I
      check("cv1({1.0+I, 2.0-I}, 1.0+I)", //
          "{1.0+I*2.0,4.0+I*1.0}");
    }
  }

  @Test
  public void testCompileComplexMatrix() {
    if (ToggleFeature.COMPILE) {
      // argument m is a 2D complex matrix
      check("cm1 = Compile({{m, _Complex, 2}, {c, _Complex}},\n" //
          + " Module({res = m, n = Length(m)},\n" //
          + " Do(res = Table(res[[i, j]] * c, {i, n}, {j, n}), {k, 1});\n" //
          + " res\n" //
          + " )\n" //
          + " );", //
          "");

      // Test the compiled complex matrix algorithm
      // Matrix m = {{1.0+I, 2.0*I}, {-I, 3.0}}
      // Scalar c = -I
      // res[[1,1]] = (1.0+I)*(-I) = -I - I^2 = 1.0 - I
      // res[[1,2]] = (2.0*I)*(-I) = -2.0*I^2 = 2.0
      // res[[2,1]] = (-I)*(-I) = I^2 = -1.0
      // res[[2,2]] = (3.0)*(-I) = -3.0*I
      check("cm1({{1.0+I, 2.0*I}, {-I, 3.0}}, 0.0-I)", //
          "{{1.0+I*(-1.0),2.0},{-1.0,I*(-3.0)}}");
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
          "CompiledFunction(Arg count: 2 Types: {Complex,Integer} Variables: {z,n} Attributes: {})");
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

  /**
   * A local variable and loop variable the analyzer proves integer-valued are compiled as an
   * exact <code>long</code>, with overflow-checked arithmetic ({@code Math.addExact} and
   * friends). <code>s</code> here roughly doubles every iteration, so by iteration 100 it is far
   * past even a <code>long</code>'s range: the checked arithmetic throws, which is caught the
   * same way any other numerical failure is and falls back to the uncompiled evaluation - which
   * computes the exact, arbitrary-precision answer. Before <code>long</code> locals existed, the
   * accumulation ran in <code>double</code> throughout and came back as the lossy
   * <code>3.80295*10^30</code>, wrong from partway through the loop onward.
   */
  @Test
  public void testCompileModuleOverflow() {
    if (ToggleFeature.COMPILE) {
      check("fi1 = Compile({{n, _Integer}},\n" //
          + "   Module({s = 1}, Do(s = (2*s + i), {i, n}); s));\n" //
          + "fi1(100)", //
          "3802951800684688204490109616026");
      // "Speed" turns the checked arithmetic off, so it wraps around at the long boundary
      // instead of overflowing into the uncompiled fallback
      check("fi1s = Compile({{n, _Integer}},\n" //
          + "   Module({s = 1}, Do(s = (2*s + i), {i, n}); s), RuntimeOptions -> \"Speed\");\n" //
          + "fi1s(100)", //
          "-102");
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
          "f=Compile({{n, _Integer}}, Module({p = Range(n),i,x,t}, Do(x = RandomInteger({1,i}); t = p[[i]]; p[[i]] = p[[x]]; p[[x]] = t,{i,n,2,-1}); p));", //
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

      // Test the compiled while loop logic - i and sum are both proven integer-valued (both
      // initialized to 0, an Integer literal), so the result is an exact Integer, not the
      // 15.0/55.0 a double accumulator used to give
      check("cf(5)", // 1 + 2 + 3 + 4 + 5 = 15
          "15");
      check("cf(10)", // 1 + 2 + ... + 10 = 55
          "55");
    }
  }


  @Test
  public void testCompileRuntimeAttributes() {
    if (ToggleFeature.COMPILE) {
      check("cf = Compile({x}, x^2, RuntimeAttributes -> {Listable});", //
          "");
      check("cf(10.1)", //
          "102.01");

      check("cf({-5,0,5})", //
          "{25.0,0.0,25.0}");
    }
  }

  @Test
  public void testCompile() {
    check(
        "cf=Compile({{x, _Real}}, x^2, RuntimeAttributes -> {Listable}, RuntimeOptions -> \"Speed\"); cf[3.]",
        "9.0");
  }

  /**
   * Every option is recognized as one: an option name <code>Compile</code> does not know is counted
   * as an ordinary argument and reported as a wrong argument count instead. What the options then
   * do - which for <code>CompilationTarget</code> and <code>Parallelization</code> is still nothing
   * - is checked elsewhere.
   */
  @Test
  public void testCompileIgnoredOptions() {
    if (ToggleFeature.COMPILE) {
      check("Compile({{x, _Real}}, x^2, RuntimeOptions -> \"Speed\")[3.]", //
          "9.0");
      check("Compile({{x, _Real}}, x^2, CompilationTarget -> \"C\")[3.]", //
          "9.0");
      check("Compile({{x, _Real}}, x^2, CompilationOptions -> Automatic)[3.]", //
          "9.0");
      check("Compile({{x, _Real}}, x^2, Parallelization -> True)[3.]", //
          "9.0");

      // all five at once, in declaration order
      check("Compile({{x, _Real}}, x^2, RuntimeAttributes -> {Listable}, RuntimeOptions -> \"Speed\","
          + " CompilationOptions -> Automatic, CompilationTarget -> \"C\", Parallelization -> True)"
          + "[{1., 2., 3.}]", //
          "{1.0,4.0,9.0}");

      // Options(...) reports the options sorted by name, not in declaration order
      check("Options(Compile)", //
          "{CompilationOptions->Automatic,CompilationTarget->WVM,Parallelization->Automatic,"
              + "RuntimeAttributes->{},RuntimeOptions->Automatic}");
    }
  }

  /**
   * <code>RuntimeOptions</code> is normalized to one flat set of settings, whichever of the
   * accepted forms it was written in. Two compiled functions compare equal exactly when their
   * settings agree, which is what these tests read the normalization off.
   *
   * <p>
   * The settings do not change how a compiled function runs yet, so there is nothing else to
   * observe here.
   */
  @Test
  public void testCompileRuntimeOptions() {
    if (ToggleFeature.COMPILE) {
      // Automatic is the default, so naming it changes nothing
      check("Compile({{x, _Real}}, x^2, RuntimeOptions -> Automatic) === Compile({{x, _Real}}, x^2)", //
          "True");

      // "Quality" turns both overflow checks on, "Speed" turns both off
      check("Compile({{x, _Real}}, x^2, RuntimeOptions -> \"Quality\") === "
          + "Compile({{x, _Real}}, x^2, RuntimeOptions -> "
          + "{\"CatchMachineIntegerOverflow\" -> True, \"CatchMachineOverflow\" -> True})", //
          "True");
      check("Compile({{x, _Real}}, x^2, RuntimeOptions -> \"Speed\") === "
          + "Compile({{x, _Real}}, x^2, RuntimeOptions -> \"Quality\")", //
          "False");

      // a rule after a name overrides that part of it: "Speed" turns CatchMachineIntegerOverflow
      // off and the rule turns it back on, which leaves the default settings
      check("Compile({{x, _Real}}, x^2, "
          + "RuntimeOptions -> {\"Speed\", \"CatchMachineIntegerOverflow\" -> True}) === "
          + "Compile({{x, _Real}}, x^2)", //
          "True");

      // RuleDelayed is accepted wherever Rule is
      check("Compile({{x, _Real}}, x^2, RuntimeOptions :> \"Speed\") === "
          + "Compile({{x, _Real}}, x^2, RuntimeOptions -> \"Speed\")", //
          "True");
      check("Compile({{x, _Real}}, x^2, RuntimeOptions -> {\"WarningMessages\" :> False}) === "
          + "Compile({{x, _Real}}, x^2)", //
          "False");

      // "RuntimeErrorHandler" takes an arbitrary expression rather than a boolean
      check("Compile({{x, _Real}}, x^2, RuntimeOptions -> {\"RuntimeErrorHandler\" -> Function(0)})"
          + " === Compile({{x, _Real}}, x^2)", //
          "False");
    }
  }

  /**
   * A <code>RuntimeOptions</code> value which cannot be read is reported and then skipped, so that
   * the function still compiles and the settings written beside the bad one still count.
   */
  @Test
  public void testCompileRuntimeOptionsInvalid() {
    if (ToggleFeature.COMPILE) {
      // prints message "cfro" - Value of option RuntimeOptions -> "Fast" should be Automatic,
      // "Speed", "Quality", a rule or a list of rules
      check("Compile({{x, _Real}}, x^2, RuntimeOptions -> \"Fast\")[3.]", //
          "9.0");
      check("Compile({{x, _Real}}, x^2, RuntimeOptions -> \"Fast\") === Compile({{x, _Real}}, x^2)", //
          "True");

      // prints message "optnf" - Option name "Bogus" not found in defaults for RuntimeOptions
      check("Compile({{x, _Real}}, x^2, RuntimeOptions -> {\"Bogus\" -> True}) === "
          + "Compile({{x, _Real}}, x^2)", //
          "True");

      // prints message "opttf" - Value of option "WarningMessages" -> 7 should be True or False
      check("Compile({{x, _Real}}, x^2, RuntimeOptions -> {\"WarningMessages\" -> 7}) === "
          + "Compile({{x, _Real}}, x^2)", //
          "True");

      // the bad entry is skipped, the good one beside it is kept
      check("Compile({{x, _Real}}, x^2, RuntimeOptions -> {\"Bogus\" -> True, \"Speed\"}) === "
          + "Compile({{x, _Real}}, x^2, RuntimeOptions -> \"Speed\")", //
          "True");

      // CompilePrint reports the same way, and still returns its source
      check("Head(CompilePrint({{x, _Real}}, x^2, RuntimeOptions -> \"Fast\"))", //
          "String");
    }
  }

  /**
   * <code>"CatchMachineIntegerOverflow"</code> decides whether an integer-typed result is tested
   * for leaving the range an exact integer can represent. <code>"Speed"</code> turns the test off,
   * which is visible both in the generated source and in the result of a computation which does
   * leave that range.
   */
  @Test
  public void testCompileCatchMachineIntegerOverflow() {
    if (ToggleFeature.COMPILE) {
      // 100000000^2 + 1 is above 2^53, so the checked wrapper gives up on an exact result
      check("Compile({{n, _Integer}}, n^2 + 1)[100000000]", //
          "1.*10^16");
      check("Compile({{n, _Integer}}, n^2 + 1, RuntimeOptions -> \"Speed\")[100000000]", //
          "10000000000000000");

      // a result which stays in range is the same either way
      check("Compile({{n, _Integer}}, n^2 + 1, RuntimeOptions -> \"Speed\")[5]", //
          "26");
      check("Compile({{n, _Integer}}, n^2 + 1, RuntimeOptions -> \"Quality\")[5]", //
          "26");

      // the setting reaches the generated source
      check("StringContainsQ(CompilePrint({{n, _Integer}}, n^2 + 1), \"symjifyIntegerUnchecked\")", //
          "False");
      check("StringContainsQ(CompilePrint({{n, _Integer}}, n^2 + 1, RuntimeOptions -> \"Speed\"),"
          + " \"symjifyIntegerUnchecked\")", //
          "True");
    }
  }

  /**
   * <code>"EvaluateSymbolically" -> False</code> stops a call the compiled code cannot take from
   * falling back to the uncompiled expression; the call stays unevaluated instead.
   */
  @Test
  public void testCompileEvaluateSymbolically() {
    if (ToggleFeature.COMPILE) {
      check("Compile({{x, _Real}}, x^2 + 1)[a]", //
          "1+a^2");
      // settings other than the default ones are printed with the compiled function
      check("Compile({{x, _Real}}, x^2 + 1, RuntimeOptions -> {\"EvaluateSymbolically\" -> False})[a]", //
          "CompiledFunction(Arg count: 1 Types: {Real} Variables: {x} Attributes: {}"
              + " Options: {CatchMachineIntegerOverflow->True,CatchMachineOverflow->False,"
              + "CompareWithTolerance->True,EvaluateSymbolically->False,"
              + "RuntimeErrorHandler->Evaluate,WarningMessages->True})[a]");
    }
  }

  /**
   * <code>"RuntimeErrorHandler"</code> is applied to a call which ends in an exception. The
   * default <code>Evaluate</code> leaves the call unevaluated, which is what it did before the
   * option existed.
   */
  @Test
  public void testCompileRuntimeErrorHandler() {
    if (ToggleFeature.COMPILE) {
      // reading past the end of the vector throws out of the compiled code
      check("Compile({{v, _Real, 1}}, v[[5]])[{1., 2.}]", //
          "CompiledFunction(Arg count: 1 Types: {Real} Variables: {v} Attributes: {})[{1.0,2.0}]");
      check("Compile({{v, _Real, 1}}, v[[5]],"
          + " RuntimeOptions -> {\"RuntimeErrorHandler\" -> Function(0)})[{1., 2.}]", //
          "0");

      // a call which does not fail never reaches the handler
      check("Compile({{v, _Real, 1}}, v[[1]],"
          + " RuntimeOptions -> {\"RuntimeErrorHandler\" -> Function(0)})[{1., 2.}]", //
          "1.0");
    }
  }

  /**
   * <code>"WarningMessages" -> False</code> silences the messages a compiled function prints. Only
   * the messages change - which this harness does not read - so what is pinned here is that the
   * results do not change with them.
   */
  @Test
  public void testCompileWarningMessages() {
    if (ToggleFeature.COMPILE) {
      // prints "cfct" unless the messages are turned off
      check("Compile({{x, _Real}}, x, RuntimeOptions -> {\"WarningMessages\" -> False})[1.0, 2.0]", //
          "CompiledFunction({x})");

      // prints "cfn" unless the messages are turned off
      check("Compile({{x, _Real}}, x^2 + 1, RuntimeOptions -> {\"WarningMessages\" -> False})[a]", //
          "1+a^2");

      // prints the exception unless the messages are turned off
      check("Compile({{v, _Real, 1}}, v[[5]], RuntimeOptions ->"
          + " {\"RuntimeErrorHandler\" -> Function(0), \"WarningMessages\" -> False})[{1., 2.}]", //
          "0");
    }
  }

  /**
   * <code>x += y</code> and its relatives compile to the same code as the <code>Set</code> they
   * stand for. Before this they reached the code generator's symbolic fallback, which wrote
   * <code>F.AddTo(...)</code> into the generated source - a factory method which does not exist, so
   * the generated class failed to compile.
   */
  @Test
  public void testCompileCompoundAssignment() {
    if (ToggleFeature.COMPILE) {
      check("Compile({{n, _Integer}}, Module({a = 0.0}, Do(a += 1.5, {n}); a))[3]", //
          "4.5");
      check("Compile({{n, _Integer}}, Module({a = 0.0}, Do(a -= 1.5, {n}); a))[3]", //
          "-4.5");
      check("Compile({{n, _Integer}}, Module({a = 1.0}, Do(a *= 2.0, {n}); a))[3]", //
          "8.0");
      check("Compile({{n, _Integer}}, Module({a = 8.0}, Do(a /= 2.0, {n}); a))[3]", //
          "1.0");

      // the same computation written out with Set
      check("Compile({{n, _Integer}}, Module({a = 0.0}, Do(a = a + 1.5, {n}); a))[3]", //
          "4.5");

      // the accumulator shape a fractal noise loop is written in
      check("Compile({{n, _Integer}, {amp, _Real}, {gain, _Real}},"
          + " Module({v = 0.0, a = amp}, Do(v += 2.0*a; a *= gain, {n}); v))[3, 1.0, 0.5]", //
          "3.5");
    }
  }

  /**
   * The smallest form of the defect the tests around this one guard against: a single straight
   * line assignment to a <code>Module</code> variable which was given an initial value.
   *
   * <p>
   * Every other test of it writes the assignment inside <code>Do</code>, <code>While</code> or a
   * compound operator, which makes it look like a defect of those. It is not - no loop and no
   * operator is needed. A scalar initial value is what makes the compiler give the variable a
   * numeric field, and an assignment which only updated the <code>ExprTrie</code> entry left the
   * two disagreeing, so the initial value was what came back.
   *
   * <p>
   * The form without an initial value is here as the contrast: it has no field, went through the
   * symbolic path throughout, and was correct even while this one was not.
   */
  @Test
  public void testCompileModuleInitializerIsNotOverwritten() {
    if (ToggleFeature.COMPILE) {
      check("Compile({{i, _Integer}}, Module({a = 0}, a = i + 1; 1.0*a))[2]", //
          "3.0");
      check("Compile({{i, _Integer}}, Module({a = 0.0}, a = 1.0*i + 1.0; a))[2]", //
          "3.0");

      // the same function without the initial value, which never had the problem
      check("Compile({{i, _Integer}}, Module({a}, a = i + 1; 1.0*a))[2]", //
          "3.0");
    }
  }

  /**
   * <code>++x</code> returns the new value of the variable and <code>x++</code> the value it held
   * before, and both write the numeric field the compiled code reads. <code>x++</code> used to
   * compile - <code>F.Increment</code> exists, unlike <code>F.AddTo</code> - and then quietly
   * return the wrong answer, because the symbolic form it generated assigned to the
   * <code>ExprTrie</code> entry of the variable rather than to that field.
   */
  @Test
  public void testCompileIncrement() {
    if (ToggleFeature.COMPILE) {
      check("Compile({{n, _Integer}}, Module({a = 0.0}, Do(++a, {n}); a))[3]", //
          "3.0");
      check("Compile({{n, _Integer}}, Module({a = 0.0}, Do(--a, {n}); a))[3]", //
          "-3.0");
      check("Compile({{n, _Integer}}, Module({a = 0.0}, Do(a++, {n}); a))[3]", //
          "3.0");
      check("Compile({{n, _Integer}}, Module({a = 0.0}, Do(a--, {n}); a))[3]", //
          "-3.0");

      // x++ is the value before the assignment, ++x the value after it
      check("Compile({{x, _Real}}, Module({a = 5.0, b}, b = a++; b*100.0 + a))[0.]", //
          "506.0");

      check("Compile({{x, _Real}}, Module({a = 5.0, b}, a += 1.0; b = a; b*100.0 + a))[0.]", //
          "606.0");

      // an assignment read as an expression rather than written as a statement of its own
      check("Compile({{x, _Real}}, Module({a = 5.0, b}, b = (a += 1.0); b*100.0 + a))[0.]", //
          "606.0");
    }
  }

  /**
   * A call to another compiled function is expanded into the body being compiled. Generated code
   * has no way to reach a compiled function otherwise - the symbol it is assigned to is written
   * into the source as a bare Java identifier which nothing declares - so this is what makes
   * compiled functions composable at all.
   */
  @Test
  public void testCompileInlineCompiledFunctions() {
    if (ToggleFeature.COMPILE) {
      check("cfDouble = Compile({{x, _Real}}, x*2.0);", //
          "");
      check("Compile({{x, _Real}}, cfDouble(x) + 1.0)[3.]", //
          "7.0");

      // a function which calls a function which calls a third one comes out flat
      check("cfTwice = Compile({{x, _Real}}, cfDouble(x) + cfDouble(x));", //
          "");
      check("Compile({{x, _Real}}, cfTwice(x) + 1.0)[1.]", //
          "5.0");

      // the parameters of the called function have the same names as the caller's variables
      check("cfLerp = Compile({{x, _Real}, {y, _Real}, {t, _Real}}, (1.0 - t)*x + t*y);", //
          "");
      check("Compile({{a, _Real}}, Module({x = 10.0, y = 20.0, u = 0.25}, cfLerp(x, y, u)))[0.]", //
          "12.5");
    }
  }

  /**
   * The body of the called function is alpha-renamed before its arguments are substituted in.
   * Without that, the <code>Module</code> in <code>cfShadow</code> would capture the caller's
   * <code>x</code> in the argument <code>x*2.0</code> and the call would compute something else.
   */
  @Test
  public void testCompileInlineHygiene() {
    if (ToggleFeature.COMPILE) {
      check("cfShadow = Compile({{x0, _Real}}, Module({x}, x = x0*10.0; x + 1.0));", //
          "");
      check("Compile({{q, _Real}}, Module({x = 3.0}, cfShadow(x*2.0)))[0.]", //
          "61.0");
    }
  }

  /**
   * <code>"InlineCompiledFunctions" -> False</code> leaves the call in place, and the generated
   * source then does not compile - there is nothing else it could do. The other two settings of
   * <code>CompilationOptions</code> are accepted and have no effect yet.
   */
  @Test
  public void testCompileCompilationOptions() {
    if (ToggleFeature.COMPILE) {
      check("Options(Compile)", //
          "{CompilationOptions->Automatic,CompilationTarget->WVM,Parallelization->Automatic,"
              + "RuntimeAttributes->{},RuntimeOptions->Automatic}");

      check("cfDouble = Compile({{x, _Real}}, x*2.0);", //
          "");
      check("Compile({{x, _Real}}, cfDouble(x),"
          + " CompilationOptions -> {\"InlineCompiledFunctions\" -> True})[3.]", //
          "6.0");

      // prints message "cfco" - Value of option CompilationOptions -> "Fast" should be ...
      check("Compile({{x, _Real}}, x^2, CompilationOptions -> \"Fast\")[3.]", //
          "9.0");
      // prints message "optnf" - Option name "Bogus" not found in defaults for CompilationOptions
      check("Compile({{x, _Real}}, x^2, CompilationOptions -> {\"Bogus\" -> True})[3.]", //
          "9.0");
      // prints message "opttfa" - should be True, False or Automatic
      check("Compile({{x, _Real}}, x^2,"
          + " CompilationOptions -> {\"InlineCompiledFunctions\" -> 7})[3.]", //
          "9.0");

      // the settings which are read and stored but not acted on
      check("Compile({{x, _Real}}, x^2, CompilationOptions ->"
          + " {\"ExpressionOptimization\" -> True, \"InlineExternalDefinitions\" -> True})[3.]", //
          "9.0");
    }
  }

  /**
   * An option name <code>Compile</code> does not know is reported and skipped, rather than stopping
   * the scan for options.
   *
   * <p>
   * The scan runs backwards from the last argument, so stopping at the first name it does not
   * recognize left every option written in front of that one unread, and the call was then reported
   * as having too many arguments. One misspelled option name - or an option name written as a
   * string, which is easy to do when the sub-options of <code>CompilationOptions</code> and
   * <code>RuntimeOptions</code> are strings - was enough to lose all of them.
   */
  @Test
  public void testCompileUnknownOption() {
    if (ToggleFeature.COMPILE) {
      // prints message "optx" - Unknown option Foo->1 in Compile
      check("Compile({{x, _Real}}, x^2, Foo -> 1)[3.]", //
          "9.0");

      // an option name written as a string is not an option name
      check("Compile({{x, _Real}}, x^2, \"CompilationTarget\" -> \"WVM\")[3.]", //
          "9.0");

      // the options in front of the unknown one are still read: RuntimeAttributes takes effect
      check("Compile({{x, _Real}}, x^2, RuntimeAttributes -> {Listable},"
          + " \"CompilationTarget\" -> \"WVM\")[{1., 2., 3.}]", //
          "{1.0,4.0,9.0}");

      // several unknown ones in a row
      check("Compile({{x, _Real}}, x^2, RuntimeAttributes -> {Listable}, Foo -> 1, Bar -> 2)"
          + "[{1., 2.}]", //
          "{1.0,4.0}");

      // too few arguments is still an error, and too many non-option arguments still is too
      check("Compile({{x, _Real}})", //
          "Compile({{x,_Real}})");
      check("Compile({{x, _Real}}, x^2, x^3)", //
          "Compile({{x,_Real}},x^2,x^3)");
    }
  }

  /**
   * A large constant list is written into the generated source once, as a field, instead of being
   * rebuilt in place at every use.
   *
   * <p>
   * The code generator's symbolic fallback writes a list out wherever it is mentioned, so a lookup
   * table indexed from a handful of places appeared in the generated method dozens of times over
   * and ran the method into the 64 KB limit the class file format puts on it.
   */
  @Test
  public void testCompileConstantHoisting() {
    if (ToggleFeature.COMPILE) {
      check("With({t = Range(10, 73)}, Compile({{i, _Integer}}, t[[i]] + t[[i+1]]))[1]", //
          "21");

      // the largest entry of the table is written once, however often the table is read
      check("With({t = Range(0, 63)},"
          + " StringCount(CompilePrint({{i, _Integer}}, t[[i]] + t[[i+1]] + t[[i+2]]),"
          + " \"F.ZZ(63L)\"))", //
          "1");
      check("With({t = Range(0, 63)},"
          + " StringContainsQ(CompilePrint({{i, _Integer}}, t[[i]]), \"const_1\"))", //
          "True");

      // a short list is left where it is - a field would be more source rather than less
      check("With({t = {10, 20, 30}},"
          + " StringContainsQ(CompilePrint({{i, _Integer}}, t[[i]]), \"const_1\"))", //
          "False");
      check("With({t = {10, 20, 30}}, Compile({{i, _Integer}}, t[[i]]))[2]", //
          "20");
    }
  }

  /**
   * An assignment whose right hand side is not a number still writes the numeric field of the
   * variable, and a scope which stands where a value is expected is generated rather than written
   * into the symbolic form of the expression around it.
   *
   * <p>
   * A variable which was assigned a number once is read back out of a numeric field, so an
   * assignment which only updated the <code>ExprTrie</code> entry left every later read seeing the
   * value from before it. And the symbolic form of a <code>Module</code> writes its local variables
   * as Java identifiers which nothing in the generated class declares, so a scope in the middle of
   * an expression did not compile at all.
   */
  @Test
  public void testCompileScopeInExpression() {
    if (ToggleFeature.COMPILE) {
      // a Module which is not numeric, standing where a value is expected
      check("With({p = {10., 20., 30.}},"
          + " Compile({{i, _Integer}}, 1.0 + Module({a}, a = p[[i]]; a*2.0)))[2]", //
          "41.0");

      // ... and on the right hand side of an assignment, which has to write the field of `s`
      check("With({p = {10., 20., 30.}},"
          + " Compile({{i, _Integer}}, Module({s = 0.0}, s += Module({a}, a = p[[i]]; a*2.0); s)))[2]", //
          "40.0");

      // an assignment read as an expression updates the field too
      check("Compile({{x, _Real}}, Module({a = 5.0, b}, b = (a = a + 1.0); b*100.0 + a))[0.]", //
          "606.0");
      check("Compile({{x, _Real}}, Module({a = 1.0, b = 2.0}, a = a + (b = b + 3.0); a*100.0 + b))[0.]", //
          "605.0");

      // the shape a fractal noise loop has: a compiled function whose body is a Module, inlined
      // into the middle of an accumulation
      check("cfBody = Compile({{x0, _Real}}, Module({t}, t = x0*2.0; t + 1.0));", //
          "");
      check("Compile({{x, _Real}}, Module({s = 0.0}, Do(s += cfBody(x)*2.0, {3}); s))[1.]", //
          "18.0");
    }
  }

  /**
   * Evaluate <code>input</code> with the messages of the engine captured, and return them.
   *
   * <p>
   * {@link AbstractTestCase#check} compares results, and a message is not one - but a message which
   * names what went wrong is the whole point of some of this behaviour, so it has to be read
   * somewhere.
   */
  private String messagesOf(String input) {
    EvalEngine engine = EvalEngine.get();
    PrintStream original = engine.getErrorPrintStream();
    ByteArrayOutputStream captured = new ByteArrayOutputStream();
    try {
      engine.setErrorPrintStream(new PrintStream(captured, true, StandardCharsets.UTF_8));
      evaluator.eval(input);
    } catch (RuntimeException rex) {
      // the message is what this reads, not the result
    } finally {
      engine.setErrorPrintStream(original);
    }
    return new String(captured.toByteArray(), StandardCharsets.UTF_8);
  }

  /**
   * An argument the compiled code cannot read is reported by name and position.
   *
   * <p>
   * The call still falls back to evaluating the uncompiled expression, which is easy to miss: it
   * gives the right answer, only far more slowly. Before this the message said no more than that
   * something had gone numerically wrong, which left a compiled function called with an undefined
   * symbol - a typo, or a parameter someone forgot to set - looking like a performance problem.
   */
  @Test
  public void testCompileArgumentMessage() {
    if (ToggleFeature.COMPILE) {
      check("cfReal = Compile({{a, _Real}, {b, _Real}}, a*b + 1.0);", //
          "");
      assertTrue(messagesOf("cfReal(2.0, unsetarg)").contains(
          "Argument unsetarg at position 2 should be a machine-size real number."));
      assertTrue(messagesOf("cfReal(unsetarg2, 3.0)").contains(
          "Argument unsetarg2 at position 1 should be a machine-size real number."));

      // the fallback still gives the right answer
      check("cfReal(2.0, unsetarg)", //
          "1.0+2.0*unsetarg");

      // an argument which is not a literal but does evaluate to a number is not reported
      check("cfReal(2.0, Pi)", //
          "7.28319");
      assertEquals("", messagesOf("cfReal(2.0, Pi)"));

      check("cfInt = Compile({{n, _Integer}}, n^2);", //
          "");
      assertTrue(messagesOf("cfInt(1.5)")
          .contains("Argument 1.5 at position 1 should be a machine-size integer."));

      // a list may be the vector the argument template asks for, so it is never reported
      check("cfVector = Compile({{v, _Real, 1}}, v[[1]]);", //
          "");
      assertEquals("", messagesOf("cfVector({1.0, 2.0})"));

      // and silence is still silence
      check("cfQuiet = Compile({{x, _Real}}, x,"
          + " RuntimeOptions -> {\"WarningMessages\" -> False});", //
          "");
      assertEquals("", messagesOf("cfQuiet(unsetarg)"));
    }
  }

  /**
   * A lifted table of numbers is also emitted as a primitive array, and a read of it compiles to an
   * array access rather than to an evaluation of <code>Part</code>.
   *
   * <p>
   * Evaluating <code>Part</code> costs a few microseconds; an array access costs nothing worth
   * measuring. A function which reads a lookup table a few dozen times - which is what a noise
   * function is - spends nearly all of its time there.
   */
  @Test
  public void testCompileConstantArrayAccess() {
    if (ToggleFeature.COMPILE) {
      // the array is emitted beside the expression form, and read through
      check("With({t = Range(0, 63)},"
          + " StringContainsQ(CompilePrint({{i, _Integer}}, t[[i]]), \"const_1_a\"))", //
          "True");
      check("With({t = Range(0, 63)},"
          + " StringContainsQ(CompilePrint({{i, _Integer}}, t[[i]]), \"const_1_a[(int)(\"))", //
          "True");

      // a table of integers still gives an integer, a table of reals a real
      check("With({t = Range(10, 73)}, Compile({{i, _Integer}}, t[[i]] + t[[i+1]]))[1]", //
          "21");
      check("With({t = N(Range(10, 73))}, Compile({{i, _Integer}}, t[[i]] + t[[i+1]]))[1]", //
          "21.0");

      // a table of tables, read both ways
      check("With({m = Table(10*r + c, {r, 1, 20}, {c, 1, 20})},"
          + " Compile({{i, _Integer}, {j, _Integer}}, m[[i]][[j]]))[2, 3]", //
          "23");
      check("With({m = Table(10*r + c, {r, 1, 20}, {c, 1, 20})},"
          + " Compile({{i, _Integer}, {j, _Integer}}, m[[i, j]]))[2, 3]", //
          "23");

      // a read which does not produce a number - one index short of the rank - is left alone
      check("With({m = Table(10*r + c, {r, 1, 20}, {c, 1, 20})},"
          + " Compile({{i, _Integer}}, Total(m[[i]])))[2]", //
          "610");

      // a list which was never lifted, and one which is not numbers at all
      check("With({t = {10, 20, 30}}, Compile({{i, _Integer}}, t[[i]]))[2]", //
          "20");
      check("With({t = {a, b, c}}, Compile({{i, _Integer}}, t[[i]]))[2]", //
          "b");
    }
  }

  @Test
  public void testCfInteger() {
    check("Compile({{n, _Integer}}, n^2 + 1)[5]", "26");
    check("Compile({{a, _Integer}, {b, _Integer}}, a*b - b)[6, 7]", "35");
  }

  @Test
  public void testCfSymbolicFallback() {
    check("Compile({{x, _Real}}, x^2 + 1)[a]", "1+a^2");
  }

  @Test
  public void testCfObjectWrongArity() {
    // prints message "cfct" - The number of arguments 2 does not match the length 1 of the
    // argument template
    check("Compile({{x, _Real}}, x)[1.0, 2.0]", "CompiledFunction({x})");
  }

  /**
   * A <code>Return(...)</code> anywhere in the body returns from the compiled function, whether
   * it is generated directly or reached through a method one of the other native heads generates
   * for it. Before this the generated <code>evaluate</code> never caught the exception a
   * <code>Return</code> throws, so it printed as a runtime error and the call stayed unevaluated.
   */
  @Test
  public void testCompileReturn() {
    if (ToggleFeature.COMPILE) {
      check("Compile({{x, _Real}}, Return(x + 1.))[3.]", "4.0");
      check("Compile({{x, _Real}}, If(x > 0, Return(1.)); 2.)[3.]", "1.0");
      check("Compile({{x, _Real}}, Module({s = 0.}, "
          + "Do(If(i > 2, Return(s)); s += 1., {i, 10}); -1.))[3.]", "2.0");
    }
  }

  /**
   * <code>Do</code> with more than one iterator is rewritten into nested single-iterator
   * <code>Do</code>s before compilation: <code>F.Do</code> only ever has a two-argument factory
   * method (body plus one iterator), so the symbolic fallback wrote source Janino could not
   * compile for every other native head this used to reach through it.
   */
  @Test
  public void testCompileMultiIteratorDo() {
    if (ToggleFeature.COMPILE) {
      check("Compile({{n, _Integer}}, Module({s = 0.}, Do(s += i*j, {i, n}, {j, n}); s))[3]",
          "36.0");
      check("Compile({{n, _Integer}}, Module({s = 0.}, "
          + "Do(s += 1., {i, 1, n}, {j, i, n}); s))[3]", "6.0");
    }
  }

  /**
   * A native head with no matching <code>F</code> factory method - <code>NestList</code>,
   * <code>FixedPointList</code>, a three-argument <code>Array</code> - or a call to a
   * user-defined function used to fail to compile, because the symbolic fallback wrote every
   * head as <code>F.&lt;Name&gt;(args)</code> regardless of whether such a factory method
   * actually existed. It now falls back to the fully general <code>F.function(head, args...)</code>
   * constructor, which always compiles.
   */
  @Test
  public void testCompileSymbolicFactoryFallback() {
    if (ToggleFeature.COMPILE) {
      check("Compile({{n, _Integer}}, NestList(# + 1 &, 0, n))[3]", "{0,1,2,3}");
      check("cf = Compile({{x, _Real}}, FixedPointList((#/2) &, x)); cf(3)[[1]]", "3.0");
      check("cf = Compile({{r, _Real}}, Array(Sin, 3, r)); cf(0)[[1]]", "0");
      check("Compile({{x, _Real}}, foo(x))[2.]", "foo(2.0)");
    }
  }

  /**
   * A list literal reaching the numeric emitters produced a working but noisy result:
   * <code>isNumericFunction</code> counts any <code>List</code> as numeric regardless of its
   * elements, so <code>{x, x^2}</code> or <code>Clip(x, {-4, 4})</code> were handed to
   * <code>convertNumeric</code>, which wrote <code>F.List.ofN(...)</code>/<code>F.Clip.ofN(...)</code>
   * - calls that throw at every invocation, are caught, and fall back to an uncompiled
   * evaluation. The value was always right; only the silent detour and the message were not.
   */
  @Test
  public void testCompileListLiteralNoNumericalError() {
    if (ToggleFeature.COMPILE) {
      check("Compile({{x, _Real}}, {x, x^2})[2.]", "{2.0,4.0}");
      assertEquals("", messagesOf("Compile({{x, _Real, 0}}, Clip(x, {-4, 4}))[-5.]"));
      check("Compile({{x, _Real, 0}}, Clip(x, {-4, 4}))[-5.]", "-4");
    }
  }

  /**
   * The imaginary unit reaching the double emitter of a real-valued function was written as a
   * bare, unresolvable Java identifier - the double emitter has no notion of a complex number at
   * all - so an expression which is complex-valued despite every argument being real failed to
   * compile. It is now kept out of the numeric emitters the same way a list literal is.
   */
  @Test
  public void testCompileImaginaryUnitInRealFunction() {
    if (ToggleFeature.COMPILE) {
      check("Compile({{x, _Real}}, x + I)[1.]", "1.0+I*1.0");
      check("Compile({{x, _Real}}, Sqrt(x) + I)[4.]", "2.0+I*1.0");
      check("Compile({{x, _Real}, {y, _Real}}, Module({z = x + I*y}, Abs(z)))[3., 4.]", "5.0");
    }
  }

  /**
   * <code>Re</code>, <code>Im</code> and <code>Arg</code> read a <code>double</code> out of a
   * <code>Complex</code>, which Hipparchus has no method returning directly as a
   * <code>Complex</code>; wrapping the <code>double</code> back up is what lets it compose with
   * the rest of the complex-domain codegen, which chains Hipparchus instance methods throughout.
   * <code>Exp(z)</code> is exactly <code>E^z</code> internally, and Hipparchus's
   * <code>Complex</code> has no two-argument <code>pow</code> taking a real base, so the general
   * <code>Power</code> codegen wrote an instance method call on the primitive <code>double</code>
   * <code>Math.E</code> is written as; <code>E</code> as a <code>Power</code> base is now written
   * as <code>.exp()</code> of the exponent instead.
   */
  @Test
  public void testCompileComplexAccessors() {
    if (ToggleFeature.COMPILE) {
      check("Compile({{z, _Complex}}, Re(z))[1. + 2.*I]", "1.0");
      check("Compile({{z, _Complex}}, Im(z) + Arg(z))[1. + 2.*I]", "3.10715");
      check("Compile({{z, _Complex}}, Conjugate(z))[1. + 2.*I]", "1.0+I*(-2.0)");
      check("Compile({{z, _Complex}}, Exp(z))[1. + 2.*I]", "-1.1312+I*2.47173");
    }
  }

  /**
   * <code>Max</code>/<code>Min</code> of more than two arguments used to fail to compile in the
   * real domain: <code>Math.max</code>/<code>Math.min</code> only ever take two arguments, and
   * the generated call passed every argument to one call. More than two now nest.
   */
  @Test
  public void testCompileMaxMinNary() {
    if (ToggleFeature.COMPILE) {
      check("Compile({{x, _Real}}, Max(x, 1., 2.))[3.]", "3.0");
      check("Compile({{x, _Real}}, Max(x, 1., 2.))[0.]", "2.0");
      check("Compile({{x, _Real}}, Min(x, 1., 2.))[3.]", "1.0");
    }
  }

  /**
   * An untyped parameter used only as a repetition count infers <code>_Integer</code>, matching
   * real <code>Compile</code>'s usage-based inference: the 3rd argument of
   * <code>Nest</code>/<code>NestList</code>/<code>FixedPointList</code>, the 2nd of
   * <code>Array</code>, or a bare <code>{name}</code> iterator of a
   * <code>Do</code>/<code>Table</code>/<code>Sum</code>/<code>Product</code>. Before this such a
   * parameter always defaulted to <code>_Real</code>, so <code>NestList</code> - which insists on
   * an exact integer count - failed rather than compute anything, and every other consumer of the
   * count received e.g. <code>100.</code> instead of <code>100</code>.
   */
  @Test
  public void testCompileIntegerCountInference() {
    if (ToggleFeature.COMPILE) {
      check("Compile({n}, NestList(# + 1 &, 0, n))[3]", "{0,1,2,3}");
      check("Compile({n}, Nest(# + 1 &, 0, n))[5]", "5");
      // v is proven integer-valued (initialized to 0, incremented by 1), so this is an exact
      // Integer, not the 4.0 a double accumulator used to give
      check("cf = Compile({count}, Module({v = 0}, Do(v += 1, {count}); v)); cf(4)", "4");
      // an argument only ever used arithmetically (not as a count) still defaults to _Real
      check("Compile({x}, x + 1)[3]", "4.0");
      // a bare {count} iterator spec also infers _Integer
      check("walk = Compile({n, trials}, "
          + "Module({v = 0}, Do(v += Total(NestList(# + 1 &, 0, n)), {trials}); v)); "
          + "walk(10, 5)", "275.0");
    }
  }

  /**
   * A <code>Module</code>/<code>Block</code>/<code>With</code> local, and a native
   * <code>Do</code> loop variable, the analyzer proves is always integer-valued is compiled as an
   * exact <code>long</code> field, with checked arithmetic ({@code Math.addExact} and friends,
   * under the default <code>"CatchMachineIntegerOverflow" -> True</code>) rather than always
   * <code>double</code>. Before this:
   * <ul>
   * <li>the accumulator here came back as <code>55.0</code>, not the correct <code>55</code>,
   * because it was a <code>double</code> throughout;</li>
   * <li><code>EvenQ</code> of the loop variable was always <code>False</code>, because
   * <code>F.symjify</code> of a <code>double</code> gives a Real, and {@code EvenQ} only answers
   * for an actual Integer - a <code>long</code> loop variable symjifies to an exact Integer
   * instead;</li>
   * <li>two scalar <code>_Integer</code> arguments multiplied past the 32-bit <code>int</code>
   * range (they stay <code>int</code>-typed; only the arithmetic widens to <code>long</code>)
   * silently wrapped to the wrong value instead of computing the exact product.</li>
   * </ul>
   */
  @Test
  public void testCompileExactIntegerArithmetic() {
    if (ToggleFeature.COMPILE) {
      check("Compile({{n, _Integer}}, Module({s = 0}, Do(s += i, {i, n}); s))[10]", "55");
      check("Compile({{n, _Integer}}, Module({s = 0}, "
          + "Do(s += If(EvenQ(i), 1, 0), {i, n}); s))[5]", "2");
      check("Compile({{a, _Integer}, {b, _Integer}}, a*b)[100000, 100000]", "10000000000");
      check("Compile({{n, _Integer}}, Mod(n, 3))[7]", "1");
      check("Compile({{n, _Integer}}, Quotient(n, 2))[7]", "3");
    }
  }

  /**
   * A <code>Divide</code> - and a <code>Power</code> whose exponent is not a literal
   * non-negative integer - never has an exact-integer codegen, even between two integer
   * arguments: real <code>Compile</code>'s own <code>Divide</code> always computes a machine
   * real, so the type this compiler infers for it has to be Real too, or an evenly-divisible
   * input would come back an Integer while an input that is not stays Real - an inconsistent
   * return type depending on the argument's value, not just its declared type. Before the
   * analyzer knew this, <code>n/2</code> reached the double emitter with an <code>int</code>
   * field substituted by name into it, and Java's own operator typing did integer division
   * regardless of what the analyzer had inferred.
   */
  @Test
  public void testCompileDivideAlwaysReal() {
    if (ToggleFeature.COMPILE) {
      check("Compile({{n, _Integer}}, n/2)[7]", "3.5");
      check("Compile({{n, _Integer}}, n/2)[6]", "3.0");
    }
  }

  /**
   * An integer computation which genuinely leaves even a <code>long</code>'s range throws out of
   * the checked arithmetic, is caught the same way any other numerical failure is, and falls back
   * to the uncompiled evaluation - which computes the exact, arbitrary-precision answer, matching
   * real <code>Compile</code>'s own overflow behavior for a machine integer. <code>"Speed"</code>
   * turns the checked arithmetic off instead, so it silently wraps at the <code>long</code>
   * boundary - the exact analogue of what <code>"Speed"</code> already did for the double-based
   * integer result.
   */
  @Test
  public void testCompileIntegerOverflowFallsBack() {
    if (ToggleFeature.COMPILE) {
      check("Compile({{n, _Integer}}, n^4)[100000]", "100000000000000000000");
      assertTrue(messagesOf("Compile({{n, _Integer}}, n^4)[100000]")
          .contains("Numerical error encountered"));
      check("Compile({{n, _Integer}}, n^4, RuntimeOptions -> \"Speed\")[100000]", //
          Long.toString((long) (100000L * 100000L * 100000L * 100000L)));
    }
  }

  /**
   * An assignment nested inside a <i>held</i> argument - one a builtin does not evaluate when it
   * is itself evaluated, so its own evaluator decides how many times, and in which order, it
   * actually runs - is left as part of the symbolic expression instead of being hoisted into a
   * once-called Java method. <code>Table</code>, <code>Sum</code>, <code>Map</code>'s function
   * argument and <code>Function</code>'s body are all held this way (this is read off the head's
   * actual <code>HoldFirst</code>/<code>HoldRest</code> attributes, not a fixed list of names).
   *
   * <p>
   * Before this, hoisting ran the assignment eagerly, exactly once, at the point the enclosing
   * call's Java arguments were being built - not once per iteration, and not only in the branch
   * an <code>If</code>'s condition actually picked. <code>Table(s += 1., {i, n})</code> updated
   * the hoisted method's own field once and returned, so the accumulator came back
   * <code>1.0</code> for any <code>n</code> rather than counting all <code>n</code> iterations;
   * <code>1.0 + If(x > 0, s = 1., s = 2.)</code> ran <i>both</i> of the <code>If</code>'s branches
   * (each hoisted to its own once-called method) instead of exactly the one the condition picked.
   */
  @Test
  public void testCompileHeldAssignmentNotHoisted() {
    if (ToggleFeature.COMPILE) {
      check("Compile({{n, _Integer}}, Module({s = 0.}, Table(s += 1., {i, n}); s))[3]", "3.0");
      check("Compile({{x, _Real}}, Module({s = 0.}, 1.0 + If(x > 0, s = 1., s = 2.)))[3.]", "2.0");
      check("Compile({{x, _Real}}, Module({s = 0.}, 1.0 + If(x > 0, s = 1., s = 2.)))[-3.]", "3.0");
      // Sum's summand is held exactly like Table's body
      check("Compile({{n, _Integer}}, Module({s = 0.}, Sum(s += i, {i, n}); s))[3]", "6.0");
      // Map itself does not hold its function argument, but the Function it is given does hold
      // its own body - which is where the actual per-element assignment lives
      check("Compile({{x, _Real}}, Module({s = 0.}, Map((s += #) &, {1., 2., 3.}); s))[3.]",
          "6.0");
      // a Table nested inside a Module that is itself inside another Table - held-ness is sticky
      // through however many levels of nesting a held position contains
      check("Compile({{x, _Real}}, "
          + "Module({s = 0.}, Table(If(i > 1, s += 1.), {i, 3}); s))[3.]", "2.0");
    }
  }
}
