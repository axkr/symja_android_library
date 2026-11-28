package org.matheclipse.core.builtin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.Random;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalHistory;
import org.matheclipse.core.eval.exception.GotoException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.eval.interfaces.ISetEvaluator;
import org.matheclipse.core.eval.interfaces.ISetValueEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;

/**
 * Evaluation control: <code>Assert</code>, <code>Goto</code> and <code>Label</code>,
 * <code>BlockRandom</code>, <code>MemoryConstrained</code>, <code>PreemptProtect</code>,
 * <code>TracePrint</code> and <code>TraceScan</code>, <code>MessageList</code>, the sequential
 * <code>ParallelDo</code>, <code>ParallelArray</code> and <code>ParallelCases</code>, and a few
 * inert or legacy heads.
 */
public final class EvaluationControlFunctions {

  /**
   * The <code>CompoundExpression</code>s with a <code>Label</code> which are evaluating on this
   * thread, innermost first.
   */
  private static final ThreadLocal<ArrayDeque<IAST>> LABEL_SCOPES =
      ThreadLocal.withInitial(ArrayDeque::new);

  /** The options of the parallel functions which have no meaning in a sequential evaluation. */
  private static final IBuiltInSymbol[] PARALLEL_OPTIONS =
      {S.Method, S.DistributedContexts, S.ProgressReporting};

  private static class Initializer {

    private static void init() {
      S.Assert.setEvaluator(new Assert());
      S.Goto.setEvaluator(new Goto());
      S.Label.setEvaluator(new Inert(Attribute.HOLDFIRST));
      S.BlockRandom.setEvaluator(new BlockRandom());
      S.MemoryConstrained.setEvaluator(new MemoryConstrained());
      S.PreemptProtect.setEvaluator(new PreemptProtect());
      S.Release.setEvaluator(new Release());
      S.HoldCompleteForm.setEvaluator(new Inert(Attribute.HOLDALLCOMPLETE));
      S.Interpretation.setEvaluator(new Inert(Attribute.HOLDALLCOMPLETE));
      S.ClearSystemCache.setEvaluator(new ClearSystemCache());
      S.MessageList.setEvaluator(new MessageList());
      S.TracePrint.setEvaluator(new TraceWalk(false));
      S.TraceScan.setEvaluator(new TraceWalk(true));
      S.TraceDepth.setEvaluator(new Inert());
      S.PrintTemporary.setEvaluator(new PrintTemporary());
      S.ParallelDo.setEvaluator(new Sequential(S.Do, Attribute.HOLDALL));
      S.ParallelArray.setEvaluator(new Sequential(S.Array));
      S.ParallelCases.setEvaluator(new Sequential(S.Cases));
      S.CompressedData.setEvaluator(new CompressedData());
      S.RawArray.setEvaluator(new RawArray());
      S.Update.setEvaluator(new Update());
      S.SyntaxInformation.setEvaluator(new SyntaxInformation());
      S.$DisplayFunction.setEvaluator(new $DisplayFunction());
    }
  }

  // ---------------------------------------------------------------------------------------------
  // Assert

  /**
   * Switch the assertions on or off for <code>On(Assert)</code> and <code>Off(Assert)</code>.
   *
   * @return <code>true</code> if <code>ast</code> is <code>On(Assert)</code> or
   *         <code>Off(Assert)</code>
   */
  static boolean switchAssertions(IAST ast, boolean on, EvalEngine engine) {
    if (ast.isAST1() && ast.arg1() == S.Assert) {
      engine.setDollarValue(S.Assert, on ? S.True : S.False);
      return true;
    }
    return false;
  }

  /**
   * <code>Assert(test)</code>, <code>Assert(test, tag)</code> - with assertions switched on by
   * <code>On(Assert)</code>, print <code>Assert::asrtf</code> if <code>test</code> isn't
   * <code>True</code>. With assertions off - the default - the assertion stays unevaluated.
   */
  private static final class Assert extends AbstractCoreFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() < 1 || ast.argSize() > 2) {
        return engine.checkBuiltinArgsSize(ast, this);
      }
      if (engine.getDollarValue(S.Assert) != S.True) {
        // with assertions switched off the assertion stays as it is
        return F.NIL;
      }
      if (!engine.evaluate(ast.arg1()).isTrue()) {
        // Assertion `1` failed.
        Errors.printMessage(S.Assert, "asrtf", F.list(F.HoldForm(ast.arg1())), engine);
      }
      return S.Null;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALLCOMPLETE);
    }
  }

  // ---------------------------------------------------------------------------------------------
  // Goto and Label

  /** @return <code>true</code> if an element of the compound expression is a <code>Label</code> */
  static boolean hasLabel(IAST compoundExpression) {
    for (int i = compoundExpression.argSize(); i > 0; i--) {
      if (compoundExpression.get(i).isAST(S.Label, 2)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Evaluate a <code>CompoundExpression</code> which contains a <code>Label</code>. A
   * <code>Goto(tag)</code> inside it continues with the element after <code>Label(tag)</code>.
   */
  static IExpr evaluateWithLabels(final IAST ast, EvalEngine engine) {
    final int argSize = ast.argSize();
    final ArrayDeque<IAST> scopes = LABEL_SCOPES.get();
    scopes.push(ast);
    try {
      IExpr result = S.Null;
      int i = 1;
      while (i <= argSize) {
        try {
          for (; i <= argSize; i++) {
            if (i == argSize && i > 1 && ast.get(i) == S.Null) {
              // "expr;" - the input history keeps the value of expr
              engine.setSuppressedOutput(result);
              return S.Null;
            }
            result = engine.evaluate(ast.get(i));
          }
        } catch (GotoException ge) {
          if (ge.getCompoundExpression() != ast) {
            throw ge;
          }
          engine.checkTimeLimit();
          i = ge.getPosition() + 1;
          result = S.Null;
        }
      }
      return result;
    } finally {
      scopes.pop();
    }
  }

  /**
   * <code>Goto(tag)</code> - continue with the element after <code>Label(tag)</code> in an
   * enclosing <code>CompoundExpression</code>.
   */
  private static final class Goto extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr tag = ast.arg1();
      for (IAST compoundExpression : LABEL_SCOPES.get()) {
        for (int i = 1; i < compoundExpression.size(); i++) {
          IExpr element = compoundExpression.get(i);
          if (element.isAST(S.Label, 2)) {
            IExpr label = element.first();
            if (label.equals(tag) || engine.evaluate(label).equals(tag)) {
              throw new GotoException(compoundExpression, i);
            }
          }
        }
      }
      // Label `1` not found.
      Errors.printMessage(S.Goto, "nolabel", F.list(tag), engine);
      return F.Hold(F.unaryAST1(S.Goto, tag));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // BlockRandom, MemoryConstrained, PreemptProtect, Release

  /**
   * <code>BlockRandom(expr)</code> - evaluate <code>expr</code> with a copy of the random generator,
   * so that the random numbers drawn after it are the same as without it.
   */
  private static final class BlockRandom extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.isAST1()) {
        return engine.checkBuiltinArgsSize(ast, this);
      }
      final Random outer = engine.getRandom();
      engine.setRandom(copy(outer));
      try {
        return engine.evaluate(ast.arg1());
      } finally {
        engine.setRandom(outer);
      }
    }

    /** A copy of the generator in its current state, or a new generator if it can't be copied. */
    private static Random copy(Random random) {
      try {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
          out.writeObject(random);
        }
        try (ObjectInputStream in =
            new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
          return (Random) in.readObject();
        }
      } catch (Exception e) {
        return new Random();
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALL);
    }
  }

  /**
   * <code>MemoryConstrained(expr, b)</code>, <code>MemoryConstrained(expr, b, failexpr)</code> -
   * evaluate <code>expr</code>; if the memory runs out, return <code>failexpr</code> or
   * <code>$Aborted</code>.
   */
  private static final class MemoryConstrained extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() < 2 || ast.argSize() > 3) {
        return engine.checkBuiltinArgsSize(ast, this);
      }
      try {
        return engine.evaluate(ast.arg1());
      } catch (OutOfMemoryError oome) {
        return ast.isAST3() ? engine.evaluate(ast.arg3()) : S.$Aborted;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDFIRST);
    }
  }

  /** <code>PreemptProtect(expr)</code> - evaluate <code>expr</code>. */
  private static final class PreemptProtect extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.isAST1()) {
        return engine.checkBuiltinArgsSize(ast, this);
      }
      return engine.evaluate(ast.arg1());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALL);
    }
  }

  /** <code>Release(expr)</code> - the older name of <code>ReleaseHold(expr)</code>. */
  private static final class Release extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return engine.evaluate(F.unaryAST1(S.ReleaseHold, ast.arg1()));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** A head without evaluation of its own, with the given attributes. */
  private static final class Inert extends AbstractCoreFunctionEvaluator {

    private final Attribute[] attributes;

    Inert(Attribute... attributes) {
      this.attributes = attributes;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      if (attributes.length > 0) {
        newSymbol.setAttributes(attributes);
      }
    }
  }

  // ---------------------------------------------------------------------------------------------
  // ClearSystemCache, MessageList, Update, SyntaxInformation, $DisplayFunction

  /** <code>ClearSystemCache()</code> - empty the caches of results the engine keeps. */
  private static final class ClearSystemCache extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      engine.clearSystemCaches();
      return S.Null;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  /**
   * <code>MessageList(n)</code> - the messages generated while the input line <code>n</code> was
   * evaluated, as a list of <code>HoldForm(symbol::tag)</code>.
   */
  private static final class MessageList extends AbstractEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      final int line = ast.arg1().toIntDefault();
      if (line == Integer.MIN_VALUE) {
        return F.NIL;
      }
      EvalHistory history = engine.getEvalHistory();
      if (history == null || line == history.getLineCounter()) {
        // the line which is evaluated now
        return engine.lineMessages();
      }
      return history.getMessages(line);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NHOLDFIRST);
    }
  }

  /** <code>Update()</code>, <code>Update(symbol)</code> - nothing to update; gives Null. */
  private static final class Update extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return S.Null;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  /**
   * <code>SyntaxInformation(f)</code> - the syntax information assigned with
   * <code>SyntaxInformation(f) = {...}</code>, otherwise <code>{}</code>.
   */
  private static final class SyntaxInformation extends AbstractEvaluator implements ISetEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isSymbol()) {
        IExpr assigned = ((ISymbol) ast.arg1()).evalDownRule(engine, ast);
        if (assigned.isPresent()) {
          return assigned;
        }
      }
      return F.CEmptyList;
    }

    @Override
    public @AllowNIL IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide,
        IBuiltInSymbol builtinSymbol, EvalEngine engine) {
      if (leftHandSide.isAST(S.SyntaxInformation, 2) && leftHandSide.first().isSymbol()) {
        ISymbol symbol = (ISymbol) leftHandSide.first();
        if (builtinSymbol != S.SetDelayed) {
          rightHandSide = engine.evaluate(rightHandSide);
        }
        symbol.putDownRule(IPatternMatcher.SET, true, (IAST) leftHandSide, rightHandSide,
            engine.isPackageMode());
        return builtinSymbol == S.Set ? rightHandSide : S.Null;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>$DisplayFunction</code> - the function the graphics functions apply to a picture which
   * is shown; <code>Identity</code> unless something else was assigned.
   */
  private static final class $DisplayFunction extends AbstractSymbolEvaluator
      implements ISetValueEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      IExpr value = engine.getDollarValue(S.$DisplayFunction);
      return value.isPresent() ? value : S.Identity;
    }

    @Override
    public IExpr evaluateSet(IExpr rightHandSide, boolean setDelayed, final EvalEngine engine) {
      EvalEngine.incEpoch();
      engine.setDollarValue(S.$DisplayFunction, rightHandSide);
      return rightHandSide;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // Trace

  /**
   * The number of arguments of a <code>Trace</code>, <code>TracePrint</code> or
   * <code>TraceScan</code> call without its trailing options.
   */
  static int traceArgSize(IAST ast) {
    int argSize = ast.argSize();
    while (argSize > 1 && ast.get(argSize).isRuleAST()) {
      argSize--;
    }
    return argSize;
  }

  /**
   * The value of the <code>TraceDepth</code> option given after the first <code>argSize</code>
   * arguments: <code>Integer.MAX_VALUE</code> for <code>Infinity</code> or no option, or
   * <code>-1</code> (after a message) for an option which isn't understood.
   */
  static int traceDepth(IAST ast, int argSize, EvalEngine engine) {
    int depth = Integer.MAX_VALUE;
    for (int i = argSize + 1; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      if (option.first() != S.TraceDepth) {
        // Unknown option `1` in `2`.
        Errors.printMessage(ast.topHead(), "optx", F.list(option, ast), engine);
        return -1;
      }
      IExpr value = engine.evaluate(option.second());
      if (value.isInfinity()) {
        depth = Integer.MAX_VALUE;
      } else {
        depth = value.toIntDefault();
        if (depth < 0) {
          // Non-negative machine-sized integer expected at position `2` in `1`.
          Errors.printMessage(ast.topHead(), "intnm", F.list(option, F.C2), engine);
          return -1;
        }
      }
    }
    return depth;
  }

  /**
   * Remove the nested lists of a trace below <code>maxDepth</code> levels. The top-level list is
   * level <code>1</code>.
   */
  static IAST pruneTrace(IAST trace, int maxDepth) {
    if (maxDepth == Integer.MAX_VALUE) {
      return trace;
    }
    return pruneTrace(trace, 1, maxDepth);
  }

  private static IAST pruneTrace(IAST trace, int level, int maxDepth) {
    IASTAppendable result = F.ListAlloc(trace.argSize());
    for (IExpr step : trace) {
      if (step.isList()) {
        if (level < maxDepth) {
          result.append(pruneTrace((IAST) step, level + 1, maxDepth));
        }
      } else {
        result.append(step);
      }
    }
    return result;
  }

  /**
   * <code>TracePrint(expr)</code>, <code>TracePrint(expr, form)</code> - print the steps of the
   * evaluation of <code>expr</code>, indented by one space per level, and return the value of
   * <code>expr</code>.
   *
   * <p>
   * <code>TraceScan(f, expr)</code>, <code>TraceScan(f, expr, form)</code> - apply <code>f</code>
   * to every step, wrapped in <code>HoldForm</code>, and return the value of <code>expr</code>.
   *
   * <p>
   * Both take the option <code>TraceDepth -&gt; n</code>.
   */
  private static final class TraceWalk extends AbstractCoreFunctionEvaluator {

    /** <code>true</code> for <code>TraceScan</code>, <code>false</code> for TracePrint. */
    private final boolean scan;

    TraceWalk(boolean scan) {
      this.scan = scan;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      final int argSize = traceArgSize(ast);
      final int first = scan ? 2 : 1;
      if (argSize < first || argSize > first + 1) {
        return engine.checkBuiltinArgsSize(ast, this);
      }
      final int maxDepth = traceDepth(ast, argSize, engine);
      if (maxDepth < 0) {
        return F.NIL;
      }
      final IExpr function = scan ? engine.evaluate(ast.arg1()) : S.Null;
      IPatternMatcher matcher = null;
      if (argSize == first + 1) {
        matcher = engine.evalPatternMatcher(ast.get(first + 1));
      }
      IExpr[] result = new IExpr[] {S.Null};
      IAST trace = pruneTrace(engine.evalTrace(ast.get(first), matcher, result), maxDepth);
      walk(trace, 1, function, engine);
      return result[0];
    }

    private void walk(IAST trace, int level, IExpr function, EvalEngine engine) {
      for (IExpr step : trace) {
        if (step.isList()) {
          walk((IAST) step, level + 1, function, engine);
        } else if (scan) {
          engine.evaluate(F.unaryAST1(function, F.HoldForm(step)));
        } else {
          engine.evaluate(F.binaryAST2(S.Print, F.stringx(" ".repeat(level)), F.HoldForm(step)));
        }
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return scan ? ARGS_2_INFINITY : ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALL);
    }
  }

  // ---------------------------------------------------------------------------------------------
  // PrintTemporary, parallel functions, CompressedData, RawArray

  /** <code>PrintTemporary(expr, ...)</code> - print like <code>Print</code>. */
  private static final class PrintTemporary extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return engine.evaluate(ast.setAtCopy(0, S.Print));
    }
  }

  /**
   * A parallel function which is evaluated by its sequential counterpart; the options of the
   * parallel evaluation are dropped.
   */
  private static final class Sequential extends AbstractCoreFunctionEvaluator {

    private final IBuiltInSymbol sequential;

    private final Attribute[] attributes;

    Sequential(IBuiltInSymbol sequential, Attribute... attributes) {
      this.sequential = sequential;
      this.attributes = attributes;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IASTAppendable call = F.ast(sequential, ast.argSize());
      for (int i = 1; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isRuleAST() && isParallelOption(arg.first())) {
          continue;
        }
        call.append(arg);
      }
      return engine.evaluate(call);
    }

    private static boolean isParallelOption(IExpr name) {
      for (IBuiltInSymbol option : PARALLEL_OPTIONS) {
        if (option == name) {
          return true;
        }
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      if (attributes.length > 0) {
        newSymbol.setAttributes(attributes);
      }
    }
  }

  /** <code>CompressedData("...")</code> - the expression the compressed string stands for. */
  private static final class CompressedData extends AbstractEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isString()) {
        return F.NIL;
      }
      return engine.evaluate(F.unaryAST1(S.Uncompress, ast.arg1()));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** <code>RawArray(type, data)</code> - the older form of <code>NumericArray(data, type)</code>. */
  private static final class RawArray extends AbstractEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isString()) {
        return F.NIL;
      }
      return engine.evaluate(F.binaryAST2(S.NumericArray, ast.arg2(), ast.arg1()));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private EvaluationControlFunctions() {}
}
