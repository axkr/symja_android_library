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
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.tensor.qty.IQuantity;
import org.matheclipse.io.IOInit;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.math.MathException;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

/**
 * Stand-alone fuzz-testing harness for the Symja computer-algebra engine.
 *
 * <p>
 * See: <a href="https://en.wikipedia.org/wiki/Fuzzing">Wikipedia - Fuzzing</a>. Fuzz testing is an
 * automated software-testing technique that drives a program with invalid, unexpected, or random
 * inputs in order to surface crashes, hangs, assertion failures, leaks of internal sentinel values
 * (such as {@code NIL} or {@code null}), and other undefined behaviour in built-in functions and
 * the evaluation loop.
 *
 * <p>
 * <b>Nature of this class.</b> Despite its name and its use of {@code org.junit.jupiter.api}
 * assertions ({@link org.junit.jupiter.api.Assertions#assertEquals assertEquals},
 * {@link org.junit.jupiter.api.Assertions#fail fail}), this is a long-running fuzz harness launched
 * manually via {@link #main(String[])}, typically left running for hours to mine bugs out of the
 * {@code matheclipse-core} engine. The {@link #checkEvaluator(IAST, String)} helper is provided so
 * regression seeds for individual built-in functions can easily be co-located with the harness when
 * needed.
 *
 * <p>
 * <b>Fuzzing strategies.</b> Three complementary entry points are provided:
 * <ul>
 * <li>{@link #smartFuzz()} &mdash; replays expressions parsed from {@code ../data/harvest.sym}
 * (harvested from existing built-in JUnit tests) and randomly mutates one or two of their arguments
 * with values drawn from {@link #createSeedList()}. This exercises realistic call patterns with
 * hostile arguments.</li>
 * <li>{@link #builtinFunctionFuzz()} &mdash; iterates every symbol in
 * {@link AST2Expr#FUNCTION_STRINGS} whose evaluator is an {@link IFunctionEvaluator}, queries
 * {@link IFunctionEvaluator#expectedArgSize(IAST)} and synthesises ASTs of the appropriate arity
 * filled with random seeds in all four {@code (nestedChaos, headerExpr)} combinations.</li>
 * <li>{@link #nonBuiltinFunctionFuzz()} &mdash; mirror of the previous strategy targeting symbols
 * whose evaluator is <em>not</em> an {@code IFunctionEvaluator} (rule-based / pattern-defined
 * functions), using a smaller hand-rolled seed list.</li>
 * </ul>
 *
 * <p>
 * <b>Failure policy.</b> The harness distinguishes <em>expected</em> Symja exceptions from
 * <em>bugs</em>:
 * <ul>
 * <li>{@link FlowControlException}, {@link SyntaxError} and {@link ValidateException} are logged
 * (in non-quiet mode) and treated as acceptable outcomes of feeding nonsense to the evaluator.</li>
 * <li>{@link MathException}, any other {@link RuntimeException} and any {@link Error} other than
 * {@link StackOverflowError} are considered bugs and trigger
 * {@link org.junit.jupiter.api.Assertions#fail()}.</li>
 * <li>A {@link StackOverflowError} is reported but tolerated, because deep recursion is an
 * inevitable consequence of random pattern programs.</li>
 * <li>Any result containing a {@code null} or {@code NIL} leaf is flagged as a <em>corrupted
 * AST</em> and forced to fail.</li>
 * </ul>
 *
 * <p>
 * <b>Watchdog.</b> Individual evaluations are monitored by the nested {@link SlowComputationThread}
 * and additionally constrained via {@link EvalEngine#evalTimeConstrained(IExpr, long)} so a single
 * pathological input cannot stall the whole campaign.
 *
 * <p>
 * <b>Global configuration.</b> The {@code static} initializer tightens several
 * {@link org.matheclipse.core.basic.Config} switches before any fuzzing begins:
 * {@link Config#UNPROTECT_ALLOWED} is disabled so the harness cannot accidentally redefine
 * built-ins, {@link F#await()} waits for asynchronous initialization of the {@code Integrate()}
 * rules, {@link IOInit#init()} registers the IO-layer functions, and finally
 * {@link Config#FUZZ_TESTING} and friends are turned on. {@link #createSeedList()} additionally
 * marks {@link F#x} and {@link F#y} as {@link ISymbol#PROTECTED} so randomly generated assignments
 * cannot corrupt them.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Fuzzing">Fuzzing (Wikipedia)</a>
 * @see ExprEvaluator
 * @see IFunctionEvaluator
 */
public class ExprEvaluatorTests {
  /**
   * When {@code true}, {@link #generateASTs} bypasses {@link EvalEngine#evalTimeConstrained} and
   * invokes {@link IFunctionEvaluator#evaluate(IAST, EvalEngine)} directly on the random AST. This
   * yields a more targeted (and faster) test of a single evaluator, at the cost of skipping the
   * surrounding evaluation pipeline (attribute handling, threading over lists, listability, etc.)
   * and the per-call time limit. Off by default.
   */
  private final static boolean DIRECT_EVALUATOR_TESTS = false;

  /**
   * Read the harvested Symja expression corpus from {@code ../data/harvest.sym} and parse it as a
   * package of top-level expressions.
   *
   * <p>
   * Each input line is followed by two extra newlines so that {@link Parser#parsePackage(String)}
   * reliably treats consecutive lines as the start of a new rule. The file is parsed with
   * {@link ASTNodeFactory#RELAXED_STYLE_FACTORY relaxed syntax} so that both {@code f(x)} and
   * {@code f[x]} forms are accepted.
   *
   * <p>
   * On any I/O or parse failure the stack trace is printed and {@code null} is returned; the caller
   * (currently only {@link #smartFuzz()}) is expected to detect this and abort.
   *
   * @return the parsed top-level expressions, or {@code null} on failure
   */
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
   * Replay-style fuzzing: load real Symja expressions harvested from existing JUnit tests of
   * built-in functions and mutate a small, random subset of their arguments with adversarial seeds.
   *
   * <p>
   * The corpus is read once from {@code ../data/harvest.sym} via {@link #parseFileToList()} and
   * converted to {@link IExpr} through {@link AST2Expr}. For each top-level expression that is a
   * non-empty AST, the method makes a mutable copy and replaces two random argument positions with
   * values drawn from {@link #createSeedList()} (associations are mutated by writing a synthetic
   * {@code Rule(index, seed)} at the chosen slot). The mutant is then handed to
   * {@link EvalEngine#evaluate(IExpr)}.
   *
   * <p>
   * Heads with global side effects or that would defeat the test (assignments, compilation,
   * {@code Pause}, tracing toggles, test reports, deep polynomial GCDs, etc.) are skipped:
   * {@link S#Set}, {@link S#SetDelayed}, {@link S#UpSet}, {@link S#UpSetDelayed},
   * {@link S#Compile}, {@link S#CompiledFunction}, {@link S#Pause}, {@link S#On}, {@link S#Off},
   * {@link S#Share}, {@link S#OptimizeExpression}, {@link S#TestReport},
   * {@link S#VerificationTest}, {@link S#PolynomialGCD}, {@link S#FactorialPower} and
   * {@link S#InstanceOf}.
   *
   * <p>
   * Each evaluation is guarded by a fresh {@link SlowComputationThread} watchdog that interrupts
   * the calling thread if the call has not returned after roughly 30&nbsp;seconds. The outer loop
   * repeats the whole corpus up to 10000 times. Tolerated vs. failing exception categories follow
   * the policy documented on the class.
   *
   * <p>
   * Several {@link Config} limits are tightened on entry (AST size, output size, input leaves,
   * matrix dimension, apfloat precision, bit length, polynomial degree) and
   * {@link Config#FILESYSTEM_ENABLED} is disabled to keep the harness sand-boxed.
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

  /**
   * Build the canonical corpus of adversarial seed expressions used by all three fuzzing
   * strategies.
   *
   * <p>
   * The returned {@link IAST} is a flat {@code List(...)} containing edge-case values from many
   * categories, deliberately mixing types that built-in functions are most likely to mishandle:
   * <ul>
   * <li><b>Binary / numeric arrays:</b> empty and single-byte {@link ByteArrayExpr}, {@code Real64}
   * {@link NumericArrayExpr} with {@code 2&times;3} shape.</li>
   * <li><b>Symbolic constants:</b> {@link S#$Aborted}, {@link S#True}/{@link S#False}, {@link S#E},
   * {@link S#Pi}, {@link S#Indeterminate}, {@code Missing("test")}, {@code Null},
   * {@link F#CInfinity}, {@link F#CNInfinity}, {@link F#ComplexInfinity}.</li>
   * <li><b>Machine and arbitrary-precision numbers:</b> machine doubles, {@code 30}-digit apfloats
   * positive/negative, integers (including signed extremes {@code Integer.MIN_VALUE},
   * {@code Integer.MAX_VALUE} and the first negative/positive primes), rationals with
   * {@code Long.MIN_VALUE}/{@code Long.MAX_VALUE} numerators and denominators, complex numbers with
   * both finite and extreme components.</li>
   * <li><b>Intervals:</b> empty, symbolic, and {@code IntervalData} with mixed open/closed
   * bounds.</li>
   * <li><b>Patterns:</b> single ({@code x_}), {@code BlankSequence} ({@code x__}) and
   * {@code BlankNullSequence} ({@code x___}) patterns, plus {@link F#$OptionsPattern()} and
   * {@link F#OptionValue} variants.</li>
   * <li><b>Associations:</b> empty, simple, string-keyed and nested associations.</li>
   * <li><b>Sparse arrays:</b> vectors and matrices of zeros/identity built via
   * {@link SparseArrayExpr#newDenseList(IAST, IExpr)}.</li>
   * <li><b>Functions and graphs:</b> {@link F#Function} bodies and {@link S#Graph}s including a
   * weighted variant.</li>
   * <li><b>Lists:</b> matrices, ragged matrices, lists embedding {@link F#Sequence} (to test
   * splicing), tiny lists used to simulate level specifications, deeply nested lists.</li>
   * <li><b>Algebraic forms:</b> {@code GoldenRatio} as {@code (1+Sqrt(5))/2}, {@code 1/Sqrt(5)},
   * {@code Sqrt(2)} combinations, {@code Exp(I*Pi/3)}, unreduced {@link F#Plus()} and
   * {@link F#Times()}.</li>
   * <li><b>Parts and slots:</b> {@link F#Part} with valid and out-of-range indices,
   * {@link F#Slot2}, {@code Slot(Integer.MAX_VALUE)}.</li>
   * <li><b>Strings and regex:</b> empty, backslash, CR, LF, TAB, CRLF, padded, and the
   * non-character {@code \uffff}; plus a deliberately invalid {@link F#RegularExpression} pattern
   * that triggers {@link java.util.regex.PatternSyntaxException}.</li>
   * <li><b>Numerical pathologies:</b> {@code 1/0} ({@code Power(0,-1)}), {@code 1-1}.</li>
   * <li><b>Option-style rules:</b> {@code Modulus -> 2/10}, {@code Heads -> True/False}.</li>
   * <li><b>Quantity:</b> {@code 1.2 m} via {@link IQuantity#of(double, String)}.</li>
   * </ul>
   *
   * <p>
   * <b>Side effects.</b> The method marks {@link F#x} and {@link F#y} as {@link ISymbol#PROTECTED}
   * to prevent random {@code Set}/{@code SetDelayed} calls from corrupting these widely shared
   * symbols, and it tightens several {@link Config} limits (AST size, output size, input leaves,
   * matrix dimension, apfloat precision, bit length, polynomial degree) before constructing the
   * list. The list is rebuilt on every invocation.
   *
   * @return a {@code List(...)} of seed {@link IExpr} values, suitable for random selection via
   *         {@link ThreadLocalRandom#nextInt(int, int) random.nextInt(1, seedList.size())}
   */
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
        F.OptionValue(F.y), //
        F.Sequence());
    return seedList;
  }

  /*
   * One-time class initializer.
   *
   * Disables Unprotect of system symbols, blocks until F.await() reports that the asynchronously
   * loaded Integrate() rule base is ready, registers the matheclipse-io built-ins through
   * IOInit.init(), and finally turns on the fuzz-testing related Config switches (FUZZ_TESTING,
   * MAX_LOOP_COUNT, SHOW_STACKTRACE). Must run before any fuzz entry point is invoked.
   */
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

  /**
   * Launch the fuzz harness.
   *
   * <p>
   * The current configuration runs a short focused burst on {@link S#SequenceCases} (arity
   * 2&ndash;3) and then enters the long-running {@link #builtinFunctionFuzz()} campaign. The
   * alternative entry points {@link #smartFuzz()} and {@link #nonBuiltinFunctionFuzz()} are present
   * as commented-out lines and can be enabled by editing this method. Intended for manual
   * invocation; the JVM should be expected to run for hours.
   *
   * @param args ignored
   */
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
   * Exhaustive fuzzing of every built-in Symja symbol that exposes an {@link IFunctionEvaluator}.
   *
   * <p>
   * The driver iterates {@link AST2Expr#FUNCTION_STRINGS} 20000 times. For each symbol it queries
   * {@link IFunctionEvaluator#expectedArgSize(IAST)} and feeds {@link #generateASTs} a suitable
   * arity range:
   * <ul>
   * <li>If the declared upper bound is at most 10, the full range {@code [max(start,1), end]} is
   * swept.</li>
   * <li>Otherwise (or if {@code expectedArgSize} returns {@code null}, meaning the function is
   * variadic) a random starting arity is picked and a window of five consecutive arities is
   * sampled.</li>
   * </ul>
   * For every chosen range, {@link #generateASTs} is invoked in all four
   * {@code (nestedChaos, headerExpr)} combinations whenever the evaluator declares it accepts a
   * header expression ({@code argSize.length > 2}); otherwise only the two {@code headerExpr=false}
   * variants are explored.
   *
   * <p>
   * Heads that would be globally destructive or pointless to fuzz are skipped (same list as in
   * {@link #smartFuzz()}, with the addition of {@link S#Power} to avoid trivial {@code 0^0} /
   * overflow churn).
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

  /**
   * Fuzz every Symja symbol whose evaluator is <em>not</em> an {@link IFunctionEvaluator}, i.e.
   * rule-based / pattern-defined functions that the Java built-in dispatch does not handle
   * directly.
   *
   * <p>
   * Each candidate symbol is run through {@link #generateASTs} with fixed arity range {@code 1..5}
   * in all four {@code (nestedChaos, headerExpr)} combinations. The seed corpus used here is a
   * smaller, hand-curated subset of {@link #createSeedList()} sufficient to stress pattern matching
   * without exploding the search space.
   *
   * <p>
   * On entry the method tightens several {@link Config} limits and disables
   * {@link Config#FILESYSTEM_ENABLED}, and marks {@link F#x} / {@link F#y} as
   * {@link ISymbol#PROTECTED}.
   */
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

  /**
   * Watchdog timer that interrupts the parent thread if an evaluation exceeds the allotted budget.
   *
   * <p>
   * Once started, the thread joins on itself in 100&nbsp;ms slices for up to 300 iterations
   * (&asymp;&nbsp;30&nbsp;seconds). If {@link #terminate()} has not been called by then the
   * watchdog prints a {@code SLOW:} marker carrying the textual form of the offending input and
   * calls {@link Thread#interrupt()} on the parent so that
   * {@link EvalEngine#evalTimeConstrained(IExpr, long)} or the next interruption check can abort
   * the evaluation. A successful {@code terminate()} (called from the {@code finally} block of the
   * caller) clears the {@code running} flag and lets the watchdog exit silently.
   *
   * <p>
   * The {@code engine} field is kept for the (currently commented-out) alternative of calling
   * {@link EvalEngine#setStopRequested(boolean)} instead of interrupting the parent thread.
   */
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

    /**
     * Cancel the watchdog so that the parent thread will not be interrupted. Idempotent and safe to
     * call from any thread; invoked from the {@code finally} block of the evaluation site.
     */
    public void terminate() {
      running.set(false);
    }
  }

  /**
   * Core mutation engine: for every arity {@code j} in {@code [start, end]}, build a random AST
   * whose head is {@code sym} and whose {@code j} arguments are sampled from {@code seedList}, then
   * evaluate it and triage the outcome.
   *
   * <p>
   * Two structural variation knobs are exposed:
   * <ul>
   * <li><b>{@code headerExpr}</b> &mdash; when {@code true} the head of the constructed expression
   * is itself an AST {@code sym(seed)} rather than the bare symbol {@code sym}. This exercises the
   * curried-application path ({@code f(a)[b,c]}).</li>
   * <li><b>{@code nestedChaos}</b> &mdash; when {@code true} and a chosen seed is itself an AST
   * with at least one argument, one of its inner positions is replaced (via
   * {@link IAST#setAtCopy(int, IExpr)} or
   * {@link IAssociation#setAtCopy(int, org.matheclipse.core.interfaces.IExpr)} for associations)
   * with another random seed before being appended.</li>
   * </ul>
   *
   * <p>
   * The actual evaluation goes through either {@link EvalEngine#evalTimeConstrained(IExpr, long)
   * evalTimeConstrained(ast, 20)} (the default, 20-second budget) or, when
   * {@link #DIRECT_EVALUATOR_TESTS} is enabled and a non-{@code null} {@code evaluator} was
   * supplied, directly through {@link IFunctionEvaluator#evaluate(IAST, EvalEngine)}.
   *
   * <p>
   * The result is scanned for {@code null} / {@code NIL} leaves via
   * {@link IExpr#isFree(java.util.function.Predicate, boolean)}; any leak prints a
   * {@code "Corrupted AST"} report and throws a {@link NullPointerException}, escalating the
   * incident to the standard failure path.
   *
   * <p>
   * Exception triage matches the class-level failure policy: control-flow and validation exceptions
   * are tolerated; {@link MathException}, generic {@link RuntimeException} and most {@link Error}s
   * call {@link org.junit.jupiter.api.Assertions#fail()}; only {@link StackOverflowError} is logged
   * but ignored.
   *
   * @param sym the built-in symbol to fuzz (becomes the AST head)
   * @param start lower bound (inclusive) of the arity sweep, &ge;&nbsp;1 in practice
   * @param end upper bound (inclusive) of the arity sweep
   * @param seedList corpus produced by {@link #createSeedList()}
   * @param random a {@link ThreadLocalRandom} for index selection
   * @param counter single-element scratch array used to flush {@code stdout} / {@code stderr}
   *        periodically (after every 81 generated ASTs)
   * @param evaluator the symbol's {@link IFunctionEvaluator}; may be {@code null} (used by
   *        {@link #nonBuiltinFunctionFuzz()}) or only consulted when
   *        {@link #DIRECT_EVALUATOR_TESTS} is enabled
   * @param engine the shared {@link EvalEngine} (re-initialised on every iteration)
   * @param nestedChaos whether to mutate the interior of AST/association seeds before appending
   * @param headerExpr whether to wrap the head in {@code sym(seed)} instead of using the bare
   *        symbol
   */
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
                "Corrupted AST: " + IStringX.inputForm(ast) + "\n" + "       ===> "
                    + IStringX.inputForm(result));
            throw new NullPointerException();
          }
        }
      } catch (FlowControlException mex) {
        if (!quietMode) {
          System.err.println(IStringX.inputForm(ast));
          mex.printStackTrace();
          System.err.println();
        }
      } catch (SyntaxError se) {

        System.err.println(IStringX.inputForm(ast));
        se.printStackTrace();
        System.err.println();

        // fail();
      } catch (ValidateException ve) {
        System.err.println(ve.getMessage());
        System.err.println(IStringX.inputForm(ast));
        System.err.println();
        // fail();
      } catch (MathException mex) {
        System.err.println(IStringX.inputForm(ast));
        mex.printStackTrace();
        System.err.println();
        fail();
      } catch (RuntimeException rex) {
        System.err.println(IStringX.inputForm(ast));
        rex.printStackTrace();
        fail();
      } catch (Error rex) {
        System.err.println(ast.toString());
        if (rex instanceof StackOverflowError) {
          System.err.println("java.lang.StackOverflowError");
          rex.printStackTrace();
          // fail();
        } else {
          System.err.println(IStringX.inputForm(ast));
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

  /**
   * Regression helper for co-locating individual built-in expectations alongside the fuzz harness.
   *
   * <p>
   * Resolves the {@link IFunctionEvaluator} for {@code ast.topHead()}, evaluates the AST through it
   * and asserts that {@code result.toString()} equals {@code expected} using
   * {@link org.junit.jupiter.api.Assertions#assertEquals(Object, Object)}. Any
   * {@link RuntimeException} during resolution / evaluation is printed and the method falls through
   * to {@link org.junit.jupiter.api.Assertions#fail()}; the same fallback is taken when the head
   * has no {@code IFunctionEvaluator}.
   *
   * @param ast the input expression; its head must resolve to an {@link IBuiltInSymbol} with an
   *        {@link IFunctionEvaluator}
   * @param expected the expected {@link Object#toString() toString()} of the evaluated result
   */
  void checkEvaluator(IAST ast, String expected) {
    EvalEngine engine = EvalEngine.get();
    try {
      IFunctionEvaluator evaluator = ((IBuiltInSymbol) ast.topHead()).getEvaluator();
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
