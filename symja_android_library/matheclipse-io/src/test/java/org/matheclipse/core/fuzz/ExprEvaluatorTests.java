package org.matheclipse.core.fuzz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.FlowControlException;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ByteArrayExpr;
import org.matheclipse.core.expression.data.NumericArrayExpr;
import org.matheclipse.core.expression.data.SparseArrayExpr;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.tensor.qty.IQuantity;
import org.matheclipse.io.IOInit;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.math.MathException;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

/**
 * See: <a href="https://en.wikipedia.org/wiki/Fuzzing">Wikipedia - Fuzzing</a>: Fuzz testing is an
 * automated software testing technique that involves providing invalid, unexpected, or random data
 * as inputs to a computer program.
 */
public class ExprEvaluatorTests {
  private final static boolean DIRECT_EVALUATOR_TESTS = false;

  private static List<ASTNode> parseFileToList() {
    try {
      File file = new File("../data/harvest.sym");
      final BufferedReader f = new BufferedReader(new FileReader(file));
      final StringBuffer buff = new StringBuffer(1024);
      String line;
      while ((line = f.readLine()) != null) {
        buff.append(line);
        buff.append('\n');
        // Insert newlines to let the parser see that a new rule starts
        buff.append('\n');
        buff.append('\n');
      }
      f.close();
      String inputString = buff.toString();
      Parser p = new Parser(ASTNodeFactory.RELAXED_STYLE_FACTORY, true, true);
      return p.parsePackage(inputString);
      // return p.parsePackage(inputString);

      // assertEquals(obj.toString(),
      // "Plus[Plus[Times[-1, a], Times[-1, Times[b, Factorial2[c]]]], d]");
    } catch (Exception e) {
      e.printStackTrace();
      // assertEquals("", e.getMessage());
    }
    return null;
  }

  /**
   * Fuzz testing - automated software testing that involves providing random arguments as inputs
   * for the input expressions in file <code>./data/harvest.sym</code> harvested from existing JUnit
   * tests of built-in functions.
   */
  public static void smartFuzz() {
    Config.MAX_AST_SIZE = 10000;
    Config.MAX_OUTPUT_SIZE = 10000;
    Config.MAX_INPUT_LEAVES = 100L;
    Config.MAX_MATRIX_DIMENSION_SIZE = 100;
    Config.MAX_PRECISION_APFLOAT = 100;
    Config.MAX_BIT_LENGTH = 200000;
    Config.MAX_POLYNOMIAL_DEGREE = 100;
    Config.FILESYSTEM_ENABLED = false;
    boolean quietMode = true;

    EvalEngine engine = new EvalEngine(true);
    engine.setRecursionLimit(256);
    engine.setIterationLimit(1000);
    ExprEvaluator eval = new ExprEvaluator(engine, true, (short) 20);

    List<ASTNode> node = parseFileToList();
    IExpr temp;

    OutputFormFactory fInputFactory = OutputFormFactory.get(true, false, 5, 7);
    fInputFactory.setInputForm(true);
    AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
    IAST seedList = createSeedList();
    ThreadLocalRandom random = ThreadLocalRandom.current();
    SlowComputationThread thread = null;
    for (int j = 1; j < 10000; j++) {
      int i = 0;
      while (i < node.size()) {
        temp = ast2Expr.convert(node.get(i++));
        if (temp.isAST() && temp.size() > 1) {
          int seedIndex = random.nextInt(1, seedList.size());
          IExpr seed = seedList.get(seedIndex);
          String mutantStr = "initial";
          IASTMutable mutant = ((IAST) temp).copy();
          try {
            ISymbol sym = mutant.topHead();
            if (sym == S.InstanceOf || sym == S.PolynomialGCD || sym == S.TestReport
                || sym == S.VerificationTest || sym == S.On || sym == S.Off || sym == S.Compile
                || sym == S.CompiledFunction || sym == S.FactorialPower || sym == S.Pause
                || sym == S.OptimizeExpression || sym == S.Share || sym == S.Set
                || sym == S.SetDelayed || sym == S.UpSet || sym == S.UpSetDelayed) {
              continue;
            }
            int randomIndex = random.nextInt(1, mutant.size());
            if (mutant.isAssociation()) {
              mutant.set(randomIndex, F.Rule(F.ZZ(randomIndex), seed));
            } else {
              mutant.set(randomIndex, seed);
            }
            for (int k = 0; k < 1; k++) {
              seedIndex = random.nextInt(1, seedList.size());
              seed = seedList.get(seedIndex);
              randomIndex = random.nextInt(1, mutant.size());
              if (mutant.isAssociation()) {
                mutant.set(randomIndex, F.Rule(F.ZZ(randomIndex), seed));
              } else {
                mutant.set(randomIndex, seed);
              }
            }

            engine.init();
            engine.setQuietMode(quietMode);
            engine.setRecursionLimit(256);
            engine.setIterationLimit(1000);
            // mutantStr = fInputFactory.toString(mutant);

            // System.out.println(">> " + mutantStr);
            // if (counter++ > 80) {
            // System.out.println("");
            // counter = 0;
            // System.out.flush();
            // System.err.flush();
            // }

            thread = new SlowComputationThread(Thread.currentThread(), ">> " + mutant.toString(),
                engine);
            thread.start();
            engine.evaluate(mutant);

          } catch (FlowControlException mex) {
            if (!quietMode) {
              System.err.println(mutant.toString());
              mex.printStackTrace();
              System.err.println();
            }
          } catch (SyntaxError se) {

            System.err.println(mutant.toString());
            se.printStackTrace();
            System.err.println();

            // fail();
          } catch (ValidateException ve) {
            System.err.println(mutant.toString());
            ve.printStackTrace();
            System.err.println();
            // fail();
          } catch (MathException mex) {
            System.err.println(mutant.toString());
            mex.printStackTrace();
            System.err.println();
            fail();
          } catch (RuntimeException rex) {
            System.err.println(mutant.toString());
            rex.printStackTrace();
            fail();
          } catch (Error rex) {
            System.err.println(mutant.toString());
            if (rex instanceof StackOverflowError) {
              System.err.println("java.lang.StackOverflowError");
              rex.printStackTrace();
              fail();
            } else {
              System.err.println(mutantStr);
              rex.printStackTrace();
              fail();
            }
          } finally {
            thread.terminate();
            thread.interrupt();
          }
        }
      }
    }
    // return result;
  }

  protected static IAST createSeedList() {

    byte[] bArray = new byte[0];
    ByteArrayExpr ba = ByteArrayExpr.newInstance(bArray);
    byte[] b0Array = new byte[] {0};
    ByteArrayExpr b0a = ByteArrayExpr.newInstance(b0Array);
    F.x.setAttributes(ISymbol.PROTECTED);
    F.y.setAttributes(ISymbol.PROTECTED);
    double[] doubleArr = new double[] {1.0, -1.0, 0.0, 2.0, 100.0, 200.0};
    int[] dims = new int[] {2, 3};
    NumericArrayExpr nae = new NumericArrayExpr(doubleArr, dims, NumericArrayExpr.Real64);

    Config.MAX_AST_SIZE = 10000;
    Config.MAX_OUTPUT_SIZE = 10000;
    Config.MAX_INPUT_LEAVES = 100L;
    Config.MAX_MATRIX_DIMENSION_SIZE = 100;
    Config.MAX_PRECISION_APFLOAT = 100;
    Config.MAX_BIT_LENGTH = 20000;
    Config.MAX_POLYNOMIAL_DEGREE = 100;

    IAST seedList = F.List( //
        ba, //
        b0a, //
        nae, //
        // F.NIL, //
        S.$Aborted, //
        S.False, //
        S.True, //
        S.E, //
        S.Pi, //
        S.Indeterminate, //
        F.Missing("test"), //
        F.complex(-0.5, 0.5), //
        F.complex(0.0, 0.5), //
        F.CDI, //
        F.CDNI, //
        F.complex(2.0, -1.0), //
        F.complex(2.0, 1.0), //
        F.complex(-2.0, -2.0), //
        F.complex(-2.0, 2.0), //
        F.complexNum("-0.8", "1.2", 30), //
        // F.complexNum(new Apfloat(Long.MIN_VALUE, 30), new Apfloat(Long.MAX_VALUE,
        // 30)), //
        F.CD0, //
        F.num(0.5), //
        F.num(-0.5), //
        F.num(Math.PI * (-0.5)), //
        F.num(Math.PI * 0.5), //
        F.num(-Math.PI), //
        F.num(Math.PI), //
        F.num(-Math.E), //
        F.num(Math.E), //
        F.num("-0.8", 30), //
        // F.num(new Apfloat(Long.MAX_VALUE, 30)), //
        // F.num(new Apfloat(Long.MIN_VALUE, 30)), //
        F.C0, //
        F.C1, //
        F.CN1, //
        F.CN1D2, //
        F.C1D2, //
        F.CNI, //
        F.CI, //
        F.ZZ(42), F.CC(Long.MAX_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, Long.MAX_VALUE), //
        F.QQ(Long.MAX_VALUE, Long.MIN_VALUE), F.QQ(Long.MIN_VALUE, Long.MAX_VALUE), //
        F.Slot2, //
        // some primes
        F.C2, F.C3, F.C5, F.C7, F.ZZ(11), F.ZZ(13), F.ZZ(17), F.ZZ(19), F.ZZ(101), F.ZZ(1009),
        F.ZZ(10007), //
        F.CN2, //
        F.CN3, //
        F.CN5, //
        F.CN7, //
        F.ZZ(-11), //
        F.ZZ(-13), //
        F.ZZ(-17), //
        F.ZZ(-19), //
        F.ZZ(-101), //
        F.ZZ(-1009), //
        F.ZZ(-10007), //
        F.ZZ(Integer.MIN_VALUE), //
        F.ZZ(Integer.MAX_VALUE), //
        F.ZZ(Byte.MIN_VALUE), //
        F.ZZ(Byte.MAX_VALUE), //
        F.CInfinity, //
        F.CNInfinity, //
        F.Null, //
        F.Power(F.C0, F.CN1), //
        F.Power(F.x, F.C2), //
        F.Indeterminate, //
        F.ComplexInfinity, //
        F.CEmptyInterval, //
        F.CEmptyIntervalData, //
        F.Interval(F.a), //
        F.IntervalData(F.List(F.CNInfinity, S.Less, F.Less, F.CNInfinity)), //
        F.IntervalData(//
            F.List(F.ZZ(0), F.LessEqual, F.LessEqual, F.ZZ(2)), //
            F.List(F.ZZ(3), F.LessEqual, F.LessEqual, F.ZZ(3))), //
        F.x_, //
        F.y_, //
        F.x__, // any sequence of one or more expressions
        F.y__, // any sequence of one or more expressions
        F.x___, // any sequence of zero or more expressions
        F.y___, // any sequence of zero or more expressions
        F.CEmptyList, //
        F.assoc(F.List(F.Rule(F.a, F.C0), F.RuleDelayed(F.b, F.C1))), //
        F.assoc(F.List()), //
        F.assoc(F.List(F.Rule(F.stringx("s1"), F.C0), F.RuleDelayed(F.stringx("s2"), F.C1))), //
        F.assoc(F.List(
            F.Rule(F.stringx("s1"), F.assoc(F.List(F.Rule(F.a, F.C0), F.RuleDelayed(F.b, F.C1)))),
            F.RuleDelayed(F.stringx("s2"),
                F.assoc(F.List(F.Rule(F.a, F.C0), F.RuleDelayed(F.b, F.C1)))))), //
        SparseArrayExpr.newDenseList(F.List(F.C0, F.C0), F.C0), //
        SparseArrayExpr.newDenseList(F.List(F.C0, F.C1, F.C0, F.C2), F.C0), //
        SparseArrayExpr.newDenseList(F.List(F.List(F.C0, F.C0), F.List(F.C0, F.C0)), F.C0), //
        SparseArrayExpr.newDenseList(F.List(F.List(F.C1, F.C0), F.List(F.C0, F.C1)), F.C0), //
        // F.sparseArray(F.Sin(F.x)), //
        F.Function(F.EvenQ(F.Slot1)), //
        F.Function(F.Expand(F.Power(F.Plus(F.C2, F.Slot1), F.C3))), //
        S.Graph.of(F.List(F.Rule(F.C1, F.C2), F.Rule(F.C2, F.C3), F.Rule(F.C3, F.C1))), //
        S.Graph.of(F.List()), //
        S.Graph.of(F.List(F.Rule(F.C1, F.C2), F.Rule(F.C2, F.C3), F.Rule(F.C3, F.C1)),
            F.List(F.Rule(S.EdgeWeight, F.List(F.CD0, F.CD1, F.CD1)))), //
        F.CEmptySequence, //
        F.CEmptyList, //
        F.List(F.List()), //
        F.List(F.List(F.List())), //
        F.List(F.List(F.C0)), //
        F.List(F.List(F.C1)), //
        F.List(F.List(F.CN1)), //
        F.List(F.List(F.C1, F.C0), F.List(F.C0, F.C1)), //
        F.List(F.List(F.C0, F.C0), F.List(F.C0, F.C0)), //
        F.List(F.List(F.C1, F.C0), F.List(F.C0, F.C1), F.C0), //
        F.List(F.List(F.C0, F.C0), F.List(F.C0, F.C0), F.C0), //
        F.List(F.num("-3.1415", 30), F.num("2.987", 30), F.num("-1", 30), F.num("0.0", 30),
            F.num("1", 30)), //
        F.List(F.CN1, F.CN2, F.C3), //
        F.List(F.CN1D2, F.CN2, F.C3), //
        F.List(F.x, F.CN2, F.C3), //
        F.List(F.x, F.C5, F.CN3), //
        F.List(F.x, F.CN3, F.CN1D2), //
        F.List(F.x, F.CN1D2, F.C1D2, F.C1D4), //
        F.List(F.C0, F.C0), //
        F.List(F.C0, F.C0, F.C0), //
        F.List(F.C1, F.C2, F.C3), //
        F.List(F.C1, F.C1, F.C1), //
        F.List(F.C1, F.C2, F.C3, F.a), //
        F.List(F.C0, F.C0, F.C0, F.C0), //
        F.List(F.C1, F.C1, F.C1, F.C1), //
        F.List(F.x, F.CN1, F.C1, F.C1), //
        F.List(F.x, F.C0, F.C0, F.C0), //
        F.List(F.x, F.C1, F.CN1, F.CN1), //
        F.List(F.Sequence(), F.C1, F.C2), //
        F.List(F.C0, F.Sequence(), F.C1, F.C2), //
        F.List(F.C0, F.C1, F.Sequence(), F.C1, F.C2), //
        F.List(F.C0, F.Sequence(), F.C2), //
        F.List(F.C0, F.C1, F.Sequence()), //
        F.List(F.CN1), //
        F.List(F.C0), //
        F.List(F.C1), //
        F.List(F.CN5), // simulate level spec
        F.List(F.C7), // simulate level spec
        F.List(F.complex(0.0, -1.0)), //
        F.List(F.complex(0.0, 1.0)), //
        F.List(F.x), //
        F.List(F.CN3D2), //
        F.List(F.C3D2), //
        F.List(F.C3D4), //
        F.List(F.List(F.List(F.C1, F.C0), F.List(F.C2, F.C3)),
            F.List(F.List(F.C4, F.a), F.List(F.C0, F.C1))),
        F.List(F.List(F.List(F.C1, F.C0), F.List(F.C2, F.C3)),
            F.List(F.List(F.C4, F.a), F.List(F.C1))),
        F.Part(F.x, F.C1), //
        F.Part(F.x, F.C2), //
        F.Part(F.x, F.ZZ(Integer.MAX_VALUE)), //
        F.Part(F.x, F.CN1, F.C1, F.C1), //
        F.Part(F.x, F.C1, F.C1, F.C1, F.C1), //
        F.C1DSqrt5, //
        F.Divide(F.Plus(F.C1, F.Sqrt(5)), F.C2), // GoldenRatio
        F.Divide(F.C2, F.Plus(F.C1, F.Sqrt(5))), // 1/GoldenRatio
        F.Negate(F.Sqrt(2)), //
        F.Divide(F.Sqrt(2), F.C2), //
        F.Negate(F.Divide(F.Sqrt(2), F.C2)), //
        F.Plus(F.Sqrt(2), F.C1), //
        F.Plus(F.Sqrt(2), F.CN1), //
        F.Exp(F.Times(F.Pi, F.CI, F.C1D3)), //
        F.Plus(), // unreduced form
        F.Plus(F.CN1), // unreduced form
        F.Plus(F.C1, F.CI), //
        F.Plus(F.CN1, F.CI), //
        F.Times(), // unreduced form
        F.Times(F.CN1), // unreduced form
        F.Times(F.Sqrt(2), F.C7), //
        F.Times(F.Sqrt(2), F.Sqrt(5)), //
        F.CSqrt2, //
        F.C2Pi, //
        F.CN3D2, //
        F.C3D2, //
        F.C3D4, //
        F.QQ(Long.MAX_VALUE, 7L), //
        F.QQ(Long.MIN_VALUE, 11L), //
        F.QQ(7, Long.MAX_VALUE), //
        F.QQ(11, Long.MAX_VALUE), //
        F.QQ(Long.MAX_VALUE, Long.MAX_VALUE), //
        F.QQ(Long.MIN_VALUE, Long.MAX_VALUE), //
        F.Slot2, //
        F.Slot(Integer.MAX_VALUE), //
        IQuantity.of(1.2, "m"), //
        // throws PatternSyntaxException
        F.RegularExpression("?i)"), //
        F.CEmptyString, //
        F.stringx("\\"), //
        F.stringx("\r"), //
        F.stringx("\t"), //
        F.stringx("\n"), //
        F.stringx("\r\n"), //
        F.stringx("\n   "), //
        F.stringx("\uffff"), //
        F.Power(F.C0, F.CN1), // division by zero problem
        F.Subtract(F.C1, F.C1), //
        F.Rule(S.Modulus, F.C2), //
        F.Rule(S.Modulus, F.C10), //
        F.Rule(S.Heads, S.True), //
        F.Rule(S.Heads, S.False), //
        F.$OptionsPattern(), //
        F.OptionValue(F.a), //
        F.OptionValue(F.b), //
        F.OptionValue(F.x), //
        F.OptionValue(F.y),//
        F.Sequence());
    return seedList;
  }

  static {
    Config.UNPROTECT_ALLOWED = false;

    // wait for initializing of Integrate() rules:
    try {
      F.await();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    IOInit.init();
    // define after initialization
    Config.FUZZ_TESTING = true;
    Config.MAX_LOOP_COUNT = Short.MAX_VALUE;
    Config.SHOW_STACKTRACE = true;
  }

  public static void main(String[] args) {
    IBuiltInSymbol symbol = S.SequenceCases;
    IEvaluator evaluator = symbol.getEvaluator();
    if (evaluator instanceof IFunctionEvaluator) {
      ThreadLocalRandom random = ThreadLocalRandom.current();
      IAST seedList = createSeedList();
      int[] counter = new int[] {0};

      generateASTs(symbol, 2, 3, seedList, random, counter, (IFunctionEvaluator) evaluator,
          EvalEngine.get(), false, false);
    }

    builtinFunctionFuzz();

    // smartFuzz();
    // nonBuiltinFunctionFuzz();
  }

  /**
   * Fuzz testing - automated software testing that involves providing random arguments as inputs
   * for the Symja built-in functions.
   */
  public static void builtinFunctionFuzz() {
    Config.FILESYSTEM_ENABLED = false;

    IAST seedList = createSeedList();
    EvalEngine engine = new EvalEngine(true);
    engine.setRecursionLimit(256);
    engine.setIterationLimit(1000);
    ExprEvaluator eval = new ExprEvaluator(engine, true, (short) 20);
    ThreadLocalRandom random = ThreadLocalRandom.current();
    String[] functionStrs = AST2Expr.FUNCTION_STRINGS;
    int[] counter = new int[] {0};
    for (int loop = 0; loop < 20000; loop++) {
      for (int i = 0; i < functionStrs.length; i++) {
        IBuiltInSymbol sym = (IBuiltInSymbol) F.symbol(functionStrs[i]);
        if (sym == S.PolynomialGCD || sym == S.TestReport || sym == S.VerificationTest
            || sym == S.On || sym == S.Off || sym == S.Compile || sym == S.CompiledFunction
            || sym == S.FactorialPower || sym == S.Pause || sym == S.Power
            || sym == S.OptimizeExpression || sym == S.Share || sym == S.Set || sym == S.SetDelayed
            || sym == S.UpSet || sym == S.UpSetDelayed) {
          continue;
        }
        IEvaluator evaluator = sym.getEvaluator();
        if (evaluator instanceof IFunctionEvaluator) {
          int[] argSize = ((IFunctionEvaluator) evaluator).expectedArgSize(null);
          if (argSize != null) {
            int end = argSize[1];
            if (end <= 10) {
              int start = argSize[0];
              if (start == 0) {
                start = 1;
              }
              generateASTs(sym, start, end, seedList, random, counter,
                  (IFunctionEvaluator) evaluator, engine, false, false);
              generateASTs(sym, start, end, seedList, random, counter,
                  (IFunctionEvaluator) evaluator, engine, true, false);
              if (argSize.length > 2) {
                generateASTs(sym, start, end, seedList, random, counter,
                    (IFunctionEvaluator) evaluator, engine, false, true);
                generateASTs(sym, start, end, seedList, random, counter,
                    (IFunctionEvaluator) evaluator, engine, true, true);
              }
              continue;
            } else {
              int start = random.nextInt(argSize[0], 10);
              generateASTs(sym, start, start + 4, seedList, random, counter,
                  (IFunctionEvaluator) evaluator, engine, false, false);
              generateASTs(sym, start, start + 4, seedList, random, counter,
                  (IFunctionEvaluator) evaluator, engine, true, false);
              if (argSize.length > 2) {
                generateASTs(sym, start, start + 4, seedList, random, counter,
                    (IFunctionEvaluator) evaluator, engine, false, true);
                generateASTs(sym, start, start + 4, seedList, random, counter,
                    (IFunctionEvaluator) evaluator, engine, true, true);
              }
            }
          } else {
            int start = random.nextInt(1, 7);
            generateASTs(sym, start, start + 4, seedList, random, counter,
                (IFunctionEvaluator) evaluator, engine, false, false);
            generateASTs(sym, start, start + 4, seedList, random, counter,
                (IFunctionEvaluator) evaluator, engine, false, true);
            generateASTs(sym, start, start + 4, seedList, random, counter,
                (IFunctionEvaluator) evaluator, engine, true, false);
            generateASTs(sym, start, start + 4, seedList, random, counter,
                (IFunctionEvaluator) evaluator, engine, true, true);
          }
        }
      }
    }
  }

  public static void nonBuiltinFunctionFuzz() {
    Config.MAX_AST_SIZE = 10000;
    Config.MAX_OUTPUT_SIZE = 10000;
    Config.MAX_INPUT_LEAVES = 100L;
    Config.MAX_MATRIX_DIMENSION_SIZE = 100;
    Config.MAX_PRECISION_APFLOAT = 100;
    Config.MAX_BIT_LENGTH = 200000;
    Config.MAX_POLYNOMIAL_DEGREE = 100;
    Config.FILESYSTEM_ENABLED = false;

    EvalEngine engine = new EvalEngine(true);
    engine.setRecursionLimit(256);
    engine.setIterationLimit(1000);
    ExprEvaluator eval = new ExprEvaluator(engine, true, (short) 20);
    byte[] bArray = new byte[0];
    ByteArrayExpr ba = ByteArrayExpr.newInstance(bArray);
    byte[] b0Array = new byte[] {0};
    ByteArrayExpr b0a = ByteArrayExpr.newInstance(b0Array);
    F.x.setAttributes(ISymbol.PROTECTED);
    F.y.setAttributes(ISymbol.PROTECTED);
    IAST seedList = F.List( //
        ba, //
        b0a, //
        // F.NIL, //
        F.complex(-0.5, 0.5), //
        F.complex(0.0, 0.5), //
        F.complex(0.0, -1.0), //
        F.complex(0.0, 1.0), //
        F.num(0.5), //
        F.num(-0.5), //
        F.num(Math.PI * (-0.5)), //
        F.num(Math.PI * 0.5), //
        F.num(-Math.PI), //
        F.num(Math.PI), //
        F.num(-Math.E), //
        F.num(Math.E), //
        F.C0, //
        F.C1, //
        F.CN1, //
        F.CN1D2, //
        F.C1D2, //
        F.CNI, //
        F.CI, //
        // F.ZZ(Integer.MIN_VALUE), //
        F.CInfinity, //
        F.CNInfinity, //
        F.Null, //
        F.Power(F.x, F.C2), //
        F.Indeterminate, //
        F.ComplexInfinity, //
        F.x_, //
        F.y_, //
        F.CEmptyList, //
        F.assoc(F.List(F.Rule(F.a, F.C0), F.RuleDelayed(F.b, F.C1))), F.assoc(F.List()), //
        F.assoc(F.List(F.Rule(F.stringx("s1"), F.C0), F.RuleDelayed(F.stringx("s2"), F.C1))), //
        SparseArrayExpr.newDenseList(F.List(F.C0, F.C0), F.C0), //
        SparseArrayExpr.newDenseList(F.List(F.C0, F.C1, F.C0, F.C2), F.C0), //
        SparseArrayExpr.newDenseList(F.List(F.List(F.C0, F.C0), F.List(F.C0, F.C0)), F.C0), //
        SparseArrayExpr.newDenseList(F.List(F.List(F.C1, F.C0), F.List(F.C0, F.C1)), F.C0), //
        F.List(F.List(F.C0)), //
        F.List(F.List(F.C1)), //
        F.List(F.List(F.CN1)), //
        F.List(F.List(F.C1, F.C0), F.List(F.C0, F.C1)), //
        F.List(F.List(F.C0, F.C0), F.List(F.C0, F.C0)), //
        F.List(F.List(F.C1, F.C0), F.List(F.C0, F.C1), F.C0), //
        F.List(F.List(F.C0, F.C0), F.List(F.C0, F.C0), F.C0), //
        F.List(F.CN1, F.CN2, F.C3), //
        F.List(F.CN1D2, F.CN2, F.C3), //
        F.List(F.x, F.CN2, F.C3), //
        F.List(F.x, F.C5, F.CN3), //
        F.List(F.x, F.CN3, F.CN1D2), //
        F.List(F.CN5), // simulate level spec
        F.List(F.C7), // simulate level spec
        F.C1DSqrt5, //
        F.C2Pi, //
        F.CN3D2, //
        F.C3D2, //
        F.C3D4, //
        F.Slot2, //
        F.stringx(""), //
        F.stringx("\\"), //
        F.stringx("\r"), //
        F.stringx("\t"), //
        F.stringx("\n"), //
        F.stringx("\r\n"), //
        F.stringx("\n   "), //
        F.stringx("\uffff"), //
        F.Subtract(F.C1, F.C1));

    String[] functionStrs = AST2Expr.FUNCTION_STRINGS;
    ThreadLocalRandom random = ThreadLocalRandom.current();
    int[] counter = new int[] {0};
    for (int i = 0; i < functionStrs.length; i++) {
      IBuiltInSymbol sym = (IBuiltInSymbol) F.symbol(functionStrs[i]);
      IEvaluator evaluator = sym.getEvaluator();
      if (evaluator instanceof IFunctionEvaluator) {
        continue;
      }
      generateASTs(sym, 1, 5, seedList, random, counter, null, engine, false, false);
      generateASTs(sym, 1, 5, seedList, random, counter, null, engine, false, true);
      generateASTs(sym, 1, 5, seedList, random, counter, null, engine, true, false);
      generateASTs(sym, 1, 5, seedList, random, counter, null, engine, true, true);
    }
  }

  private static class SlowComputationThread extends Thread {
    private String str;
    private AtomicBoolean running;
    private EvalEngine engine;
    private Thread parent;

    SlowComputationThread(Thread parent, String str, EvalEngine engine) {
      this.parent = parent;
      this.str = str;
      this.running = new AtomicBoolean(true);
      this.engine = engine;
    }

    @Override
    public void run() {
      if (running.get()) {
        try {
          for (int i = 0; i < 300; i++) {
            join(100);
            if (!running.get()) {
              break;
            }
          }
        } catch (InterruptedException e) {
          //
          running.set(false);
        }
        if (running.get()) {
          System.err.println("SLOW: " + str);
          // engine.setStopRequested(true);
          parent.interrupt();
        }
      }
    }

    public void terminate() {
      running.set(false);
    }
  }

  private static void generateASTs(IBuiltInSymbol sym, int start, int end, IAST seedList,
      ThreadLocalRandom random, int[] counter, IFunctionEvaluator evaluator, EvalEngine engine,
      boolean nestedChaos, boolean headerExpr) {
    boolean quietMode = true;
    ExprEvaluator eval;
    System.out.flush();

    for (int j = start; j <= end; j++) {

      eval = new ExprEvaluator(engine, true, (short) 20);
      engine.init();
      engine.setQuietMode(quietMode);
      final IASTAppendable ast;
      if (headerExpr) {
        int seedIndex = random.nextInt(1, seedList.size());
        IExpr seed = seedList.get(seedIndex);
        ast = F.ast(F.unaryAST1(sym, seed));
      } else {
        ast = F.ast(sym);
      }
      // SlowComputationThread thread = null;
      try {
        for (int k = 0; k < j; k++) {
          int seedIndex = random.nextInt(1, seedList.size());
          IExpr seed = seedList.get(seedIndex);
          if (nestedChaos && seed.isAST() && seed.size() > 1) {
            int seedIndex2 = random.nextInt(1, seed.size());
            seedIndex = random.nextInt(1, seedList.size());
            IExpr seed2 = seedList.get(seedIndex);
            if (seed != seed2) {
              if (seed.isAssociation()) {
                seed = ((IAssociation) seed).setAtCopy(seedIndex2, F.Rule(F.ZZ(seedIndex2), seed2));
              } else {
                seed = ((IAST) seed).setAtCopy(seedIndex2, seed2);
              }
            }
          }
          ast.append(seed);
        }

        if (counter[0]++ > 80) {
          // System.out.println("");
          counter[0] = 0;
          System.out.flush();
          System.err.flush();
        }
        // System.out.println(">> " + ast.toString());
        // System.out.print(".");

        // thread = new SlowComputationThread(Thread.currentThread(), ">> " + ast.toString(),
        // engine);
        // thread.start();

        // IAST debugTest = (IAST) engine.parse(
        // "Integrate(2/(1+Sqrt(5)))[x^(Heads->True), {{0}}+I,OptionValue(
        // {{1}}),{x,5,Quantity(1.2,\"m\")}]");
        // if (evaluator != null) {
        // evaluator.evaluate(debugTest, engine);
        // } else {
        // eval.eval(debugTest);
        // }
        // engine.evaluate(ast);
        final IExpr result;
        engine.init();
        if (evaluator != null && DIRECT_EVALUATOR_TESTS) {
          result = evaluator.evaluate(ast, engine);
        } else {
          result = eval.getEvalEngine().evalTimeConstrained(ast, 20);
        }
        if (result.isAST()) {
          if (!result.isFree(x -> x == null || x.isNIL(), true)) {
            System.out.println(
                "Corrupted AST: " + ast.toString() + "\n" + "       ===> " + result.toString());
            throw new NullPointerException();
          }
        }
      } catch (FlowControlException mex) {
        if (!quietMode) {
          System.err.println(ast.toString());
          mex.printStackTrace();
          System.err.println();
        }
      } catch (SyntaxError se) {

        System.err.println(ast.toString());
        se.printStackTrace();
        System.err.println();

        // fail();
      } catch (ValidateException ve) {
        System.err.println(ve.getMessage());
        System.err.println(ast.toString());
        System.err.println();
        // fail();
      } catch (MathException mex) {
        System.err.println(ast.toString());
        mex.printStackTrace();
        System.err.println();
        fail();
      } catch (RuntimeException rex) {
        System.err.println(ast.toString());
        rex.printStackTrace();
        fail();
      } catch (Error rex) {
        System.err.println(ast.toString());
        if (rex instanceof StackOverflowError) {
          System.err.println("java.lang.StackOverflowError");
          rex.printStackTrace();
          fail();
        } else {
          System.err.println(ast.toString());
          rex.printStackTrace();
          fail();
        }
      } finally {
        // if (thread != null) {
        // thread.terminate();
        // thread.interrupt();
        // }
      }
    }
  }

  public void testLucasL() {
    // LucasL(19,{x,{1,2,3,a},-1/2})
    IAST expr = F.LucasL(F.ZZ(19), F.List(F.x, F.List(F.C1, F.C2, F.C3, F.a), F.CN1D2));
    checkEvaluator(expr, //
        "{19*x+285*x^3+1254*x^5+2508*x^7+2717*x^9+1729*x^11+665*x^13+152*x^15+19*x^17+x^\n"
            + "19,{9349,18738638,7222746567,19*a+285*a^3+1254*a^5+2508*a^7+2717*a^9+1729*a^11+\n"
            + "665*a^13+152*a^15+19*a^17+a^19},-57746701/524288}");
  }

  public void testTogether() {
    // Together(1+SparseArray(Number of elements: 0 Dimensions: {2,2} Default value: 0))
    IExpr sa = SparseArrayExpr.newDenseList(F.List(F.List(F.C1, F.C0), F.List(F.C0, F.C1)), F.C0);
    checkEvaluator(F.Together(F.Plus(F.C1, sa)), //
        "1+SparseArray(Number of elements: 2 Dimensions: {2,2} Default value: 0)");
  }

  void checkEvaluator(IAST ast, String expected) {
    EvalEngine engine = EvalEngine.get();
    try {
      IFunctionEvaluator evaluator =
          (IFunctionEvaluator) ((IBuiltInSymbol) ast.topHead()).getEvaluator();
      if (evaluator instanceof IFunctionEvaluator) {// evaluator may be null
        IExpr result = evaluator.evaluate(ast, engine);
        assertEquals(expected, result.toString());
        return;
      }
    } catch (RuntimeException rex) {
      rex.printStackTrace();
    }
    fail();
  }
}
