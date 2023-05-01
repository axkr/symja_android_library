package org.matheclipse.core.builtin;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalHistory;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Lambda;
import org.matheclipse.core.eval.util.OpenFixedSizeMap;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISparseArray;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.core.patternmatching.PatternMatcherAndEvaluator;
import org.matheclipse.core.visit.AbstractVisitorLong;
import org.matheclipse.core.visit.IndexedLevel;
import org.matheclipse.core.visit.ModuleReplaceAll;
import org.matheclipse.core.visit.VisitorLevelSpecification;
import it.unimi.dsi.fastutil.ints.IntList;

public class StructureFunctions {
  private static final Logger LOGGER = LogManager.getLogger();

  private static final Set<ISymbol> LOGIC_EQUATION_HEADS =
      Collections.newSetFromMap(new IdentityHashMap<ISymbol, Boolean>(29));
  private static final Set<ISymbol> PLUS_LOGIC_EQUATION_HEADS =
      Collections.newSetFromMap(new IdentityHashMap<ISymbol, Boolean>(29));
  private static final Set<ISymbol> LIST_LOGIC_EQUATION_HEADS =
      Collections.newSetFromMap(new IdentityHashMap<ISymbol, Boolean>(29));

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.Apply.setEvaluator(new Apply());
      S.ByteCount.setEvaluator(new ByteCount());
      S.Depth.setEvaluator(new Depth());
      S.Exit.setEvaluator(new QuitExit());
      S.Flatten.setEvaluator(new Flatten());
      S.FlattenAt.setEvaluator(new FlattenAt());
      S.Function.setEvaluator(new Function());
      S.Head.setEvaluator(new Head());
      S.LeafCount.setEvaluator(new LeafCount());
      S.Map.setEvaluator(new Map());
      S.MapAll.setEvaluator(new MapAll());
      S.MapAt.setEvaluator(new MapAt());
      S.MapIndexed.setEvaluator(new MapIndexed());
      S.MapThread.setEvaluator(new MapThread());
      S.NumericalOrder.setEvaluator(new NumericalOrder());
      S.Order.setEvaluator(new Order());
      S.OrderedQ.setEvaluator(new OrderedQ());
      S.Operate.setEvaluator(new Operate());
      S.ParallelMap.setEvaluator(new ParallelMap());
      S.PatternOrder.setEvaluator(new PatternOrder());
      S.Quit.setEvaluator(new QuitExit());
      S.Scan.setEvaluator(new Scan());
      S.Sort.setEvaluator(new Sort());
      S.SortBy.setEvaluator(new SortBy());
      S.Symbol.setEvaluator(new Symbol());
      S.SymbolName.setEvaluator(new SymbolName());
      S.Thread.setEvaluator(new Thread());
      S.Through.setEvaluator(new Through());
      ISymbol[] logicEquationHeads = {S.And, S.Or, S.Xor, S.Nand, S.Nor, S.Not, S.Implies,
          S.Equivalent, S.Equal, S.Unequal, S.Less, S.Greater, S.LessEqual, S.GreaterEqual};
      for (int i = 0; i < logicEquationHeads.length; i++) {
        LOGIC_EQUATION_HEADS.add(logicEquationHeads[i]);
      }
      PLUS_LOGIC_EQUATION_HEADS.addAll(LOGIC_EQUATION_HEADS);
      PLUS_LOGIC_EQUATION_HEADS.add(S.Plus);
      LIST_LOGIC_EQUATION_HEADS.addAll(LOGIC_EQUATION_HEADS);
      LIST_LOGIC_EQUATION_HEADS.add(S.List);
    }
  }

  /**
   *
   *
   * <pre>
   * Apply(f, expr)
   *
   * f @@ expr
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * replaces the head of <code>expr</code> with <code>f</code>.
   *
   * <pre>
   * Apply(f, expr, levelspec)
   * </pre>
   *
   * <p>
   * applies <code>f</code> on the parts specified by <code>levelspec</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; f @@ {1, 2, 3}
   * f(1, 2, 3)
   * &gt;&gt; Plus @@ {1, 2, 3}
   * 6
   * </pre>
   *
   * <p>
   * The head of $expr$ need not be 'List':
   *
   * <pre>
   * &gt;&gt; f @@ (a + b + c)
   * f(a, b, c)
   * </pre>
   *
   * <p>
   * Apply on level 1:
   *
   * <pre>
   * &gt;&gt; Apply(f, {a + b, g(c, d, e * f), 3}, {1})
   * {f(a, b), f(c, d, e*f), 3}
   * </pre>
   *
   * <p>
   * The default level is 0:
   *
   * <pre>
   * &gt;&gt; Apply(f, {a, b, c}, {0})
   * f(a, b, c)
   * </pre>
   *
   * <p>
   * Range of levels, including negative level (counting from bottom):
   *
   * <pre>
   * &gt;&gt; Apply(f, {{{{{a}}}}}, {2, -3})
   * {{f(f({a}))}}
   * </pre>
   *
   * <p>
   * Convert all operations to lists:
   *
   * <pre>
   * &gt;&gt; Apply(List, a + b * c ^ e * f(g), {0, Infinity})
   * {a,{b,{c,e},{g}}}
   * </pre>
   *
   * <p>
   * Level specification x + y is not of the form n, {n}, or {m, n}.
   *
   * <pre>
   * &gt;&gt; Apply(f, {a, b, c}, x+y)
   * Apply(f, {a, b, c}, x + y)
   * </pre>
   */
  private static final class Apply extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() < 2 || ast.argSize() > 4) {
        return IOFunctions.printArgMessage(ast, ARGS_2_4, engine);
      }
      IASTAppendable evaledAST = ast.copyAppendable();
      evaledAST.setArgs(evaledAST.size(), (int i) -> engine.evaluate(evaledAST.get(i)));

      int lastIndex = evaledAST.argSize();
      boolean heads = false;
      final OptionArgs options = new OptionArgs(evaledAST.topHead(), evaledAST, lastIndex, engine);
      IExpr option = options.getOption(S.Heads);
      if (option.isPresent()) {
        lastIndex--;
        if (option.isTrue()) {
          heads = true;
        }
      } else {
        if (ast.argSize() == 4) {
          return IOFunctions.printArgMessage(ast, ARGS_2_3, engine);
        }
      }

      IExpr arg1 = evaledAST.arg1();
      IExpr arg2 = evaledAST.arg2();
      if (arg1.isQuantity() || arg2.isQuantity()) {
        return F.NIL;
      }
      return evalApply(arg1, arg2, evaledAST, lastIndex, heads, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4_2;
    }

    public static IExpr evalApply(IExpr f, IExpr expr, IAST evaledAST, int lastIndex, boolean heads,
        EvalEngine engine) {

      java.util.function.Function<IExpr, IExpr> af =
          x -> x.isAST() ? ((IAST) x).setAtCopy(0, f) : F.NIL;
      try {
        VisitorLevelSpecification level = null;
        if (lastIndex == 3) {
          level = new VisitorLevelSpecification(af, evaledAST.get(lastIndex), heads, engine);
        } else {
          level = new VisitorLevelSpecification(af, 0);
        }

        if (expr.isAST()) {
          return ((IAST) expr).acceptChecked(level).orElse(expr);
        } else {
          // arg2 is an Atom to which the head f couldn't be applied
          if (evaledAST.size() >= 3) {
            if (f.isFunction()) {
              return F.unaryAST1(f, expr);
            }
            return expr;
          }
        }
      } catch (final ValidateException ve) {
        // see level specification
        return IOFunctions.printMessage(S.Apply, ve, engine);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static class ByteCount extends AbstractCoreFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * Depth(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the depth of <code>expr</code>.
   *
   * </blockquote>
   *
   * <p>
   * The depth of an expression is defined as one plus the maximum number of <code>Part</code>
   * indices required to reach any part of <code>expr</code>, except for heads.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Depth(x)
   * 1
   *
   * &gt;&gt; Depth(x + y)
   * 2
   *
   * &gt;&gt; Depth({{{{x}}}})
   * 5
   * </pre>
   *
   * <p>
   * Complex numbers are atomic, and hence have depth 1:
   *
   * <pre>
   * &gt;&gt; Depth(1 + 2*I)
   * 1
   * </pre>
   *
   * <p>
   * <code>Depth</code> ignores heads:
   *
   * <pre>
   * &gt;&gt; Depth(f(a, b)[c])
   * 2
   * </pre>
   */
  private static final class Depth extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr arg1 = engine.evaluate(ast.arg1());
      // if (!(arg1.isASTOrAssociation())) {
      // return F.C1;
      // }
      return F.ZZ(arg1.depth());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * Flatten(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * flattens out nested lists in <code>expr</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Flatten(expr, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * stops flattening at level <code>n</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Flatten(expr, n, h)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * flattens expressions with head <code>h</code> instead of 'List'.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Flatten({{a, b}, {c, {d}, e}, {f, {g, h}}})
   * {a, b, c, d, e, f, g, h}
   * &gt;&gt; Flatten({{a, b}, {c, {e}, e}, {f, {g, h}}}, 1)
   * {a, b, c, {e}, e, f, {g, h}}
   * &gt;&gt; Flatten(f(a, f(b, f(c, d)), e), Infinity, f)
   * f(a, b, c, d, e)
   * &gt;&gt; Flatten({{a, b}, {c, d}}, {{2}, {1}})
   * {{a, c}, {b, d}}
   * &gt;&gt; Flatten({{a, b}, {c, d}}, {{1, 2}})
   * {a, b, c, d}
   * </pre>
   *
   * <p>
   * Flatten also works in irregularly shaped arrays
   *
   * <pre>
   * &gt;&gt; Flatten({{1, 2, 3}, {4}, {6, 7}, {8, 9, 10}}, {{2}, {1}})
   * {{1, 4, 6, 8}, {2, 7, 9}, {3, 10}}
   *
   * &gt;&gt; Flatten({{{111, 112, 113}, {121, 122}}, {{211, 212}, {221, 222, 223}}}, {{3}, {1}, {2}})
   * {{{111, 121}, {211, 221}}, {{112, 122}, {212, 222}}, {{113}, {223}}}
   *
   * &gt;&gt; Flatten({{{1, 2, 3}, {4, 5}}, {{6, 7}, {8, 9,  10}}}, {{3}, {1}, {2}})
   * {{{1, 4}, {6, 8}}, {{2, 5}, {7, 9}}, {{3}, {10}}}
   *
   * &gt;&gt; Flatten({{{1, 2, 3}, {4, 5}}, {{6, 7}, {8, 9, 10}}}, {{2}, {1, 3}})
   * {{1, 2, 3, 6, 7}, {4, 5, 8, 9, 10}}
   *
   * &gt;&gt; Flatten({{1, 2}, {3,4}}, {1, 2})
   * {1, 2, 3, 4}
   * </pre>
   *
   * <p>
   * Levels to be flattened together in {{-1, 2}} should be lists of positive integers.
   *
   * <pre>
   * &gt;&gt; Flatten({{1, 2}, {3, 4}}, {{-1, 2}})
   * Flatten({{1, 2}, {3, 4}}, {{-1, 2}}, List)
   * </pre>
   *
   * <p>
   * Level 2 specified in {{1}, {2}} exceeds the levels, 1, which can be flattened together in {a,
   * b}.
   *
   * <pre>
   * &gt;&gt; Flatten({a, b}, {{1}, {2}})
   * Flatten({a, b}, {{1}, {2}}, List)
   * </pre>
   *
   * <p>
   * Check <code>n</code> completion
   *
   * <pre>
   * &gt;&gt; m = {{{1, 2}, {3}}, {{4}, {5, 6}}}
   * &gt;&gt; Flatten(m, {2})
   * {{{1, 2}, {4}}, {{3}, {5, 6}}}
   * &gt;&gt; Flatten(m, {{2}})
   * {{{1, 2}, {4}}, {{3}, {5, 6}}}
   * &gt;&gt; Flatten(m, {{2}, {1}})
   * {{{1, 2}, {4}}, {{3}, {5, 6}}}
   * &gt;&gt; Flatten(m, {{2}, {1}, {3}})
   * {{{1, 2}, {4}}, {{3}, {5, 6}}}
   * </pre>
   *
   * <p>
   * Level 4 specified in {{2}, {1}, {3}, {4}} exceeds the levels, 3, which can be flattened
   * together in {{{1, 2}, {3}}, {{4}, {5, 6}}}.
   *
   * <pre>
   * &gt;&gt; Flatten(m, {{2}, {1}, {3}, {4}})
   * Flatten({{{1, 2}, {3}}, {{4}, {5, 6}}}, {{2}, {1}, {3}, {4}}, List)
   * </pre>
   *
   * <pre>
   * &gt;&gt; m{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
   * &gt;&gt; Flatten(m, {1})
   * {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}
   * &gt;&gt; Flatten(m, {2})
   * {{1, 4, 7}, {2, 5, 8}, {3, 6, 9}}
   * &gt;&gt; Flatten(m, {3})
   *  : Level 3 specified in {3} exceeds the levels, 2, which can be flattened together in {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}.
   * Flatten({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {3}, List)
   * &gt;&gt; Flatten(m, {2, 1})
   * {1, 4, 7, 2, 5, 8, 3, 6, 9}
   * Reproduce strange head behaviour
   * &gt;&gt; Flatten({{1}, 2}, {1, 2})
   *  : Level 2 specified in {1, 2} exceeds the levels, 1, which can be flattened together in {{1}, 2}.
   * Flatten({{1}, 2}, {1, 2}, List)
   * &gt;&gt; Flatten(a(b(1, 2), b(3)), {1, 2}, b)     (* MMA BUG: {{1, 2}} not {1, 2}  *)
   *  : Level 1 specified in {1, 2} exceeds the levels, 0, which can be flattened together in a(b(1, 2), b(3)).
   * Flatten(a(b(1, 2), b(3)), {1, 2}, b)
   * &gt;&gt; Flatten({{1, 2}, {3, {4}}}, {{1, 2}})
   * {1, 2, 3, {4}}
   * &gt;&gt; Flatten({{1, 2}, {3, {4}}}, {{1, 2, 3}})
   *  : Level 3 specified in {{1, 2, 3}} exceeds the levels, 2, which can be flattened together in {{1, 2}, {3, {4}}}.
   * Flatten({{1, 2}, {3, {4}}}, {{1, 2, 3}}, List)
   * &gt;&gt; Flatten(p(1, p(2), p(3)))
   * p(1, 2, 3)
   * &gt;&gt; Flatten(p(1, p(2), p(3)), 2)
   * p(1, 2, 3)
   * </pre>
   */
  private static final class Flatten extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr arg1 = engine.evaluate(ast.arg1());
      if (ast.isAST1()) {
        if (arg1.isSparseArray()) {
          ISparseArray sparseArray = (ISparseArray) arg1;
          return sparseArray.flatten();
        }
      }
      if (arg1.isSparseArray()) {
        arg1 = arg1.normal(false);
      }
      if (arg1.isAST()) {
        IAST arg1AST = (IAST) arg1;

        if (ast.isAST1()) {
          IAST resultList = EvalAttributes.flattenDeep(arg1AST.topHead(), (IAST) arg1);
          if (resultList.isPresent()) {
            return resultList;
          }
          return arg1AST;
        } else if (ast.isAST2()) {
          IExpr arg2 = engine.evaluate(ast.arg2());

          int level = Validate.checkIntLevelType(arg2);
          if (level > 0) {
            IASTAppendable resultList = F.ast(arg1AST.topHead(), arg1AST.size());
            if (EvalAttributes.flatten(arg1AST.topHead(), (IAST) arg1, resultList, 0, level)) {
              return resultList;
            }
          }
          return arg1;
        } else if (ast.isAST3() && ast.arg3().isSymbol()) {
          IExpr arg2 = engine.evaluate(ast.arg2());

          int level = Validate.checkIntLevelType(arg2);
          if (level > 0) {
            IASTAppendable resultList = F.ast(arg1AST.topHead());
            if (EvalAttributes.flatten((ISymbol) ast.arg3(), (IAST) arg1, resultList, 0, level)) {
              return resultList;
            }
          }
          return arg1;
        }
        return F.NIL;
      }
      // Nonatomic expression expected at position `1` in `2`.
      return IOFunctions.printMessage(ast.topHead(), "normal", F.list(F.C1, ast), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * FlattenAt(expr, position)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * flattens out nested lists at the given <code>position</code> in <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; FlattenAt(f(a, g(b,c), {d, e}, {f}), -2)
   * f(a,g(b,c),d,e,{f})
   *
   * &gt;&gt; FlattenAt(f(a, g(b,c), {d, e}, {f}), 4)
   * f(a,g(b,c),{d,e},f)
   * </pre>
   */
  private static final class FlattenAt extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr arg1 = engine.evaluate(ast.arg1());
      IExpr arg2 = engine.evaluate(ast.arg2());
      if (arg1.isAST()) {
        IAST arg1AST = (IAST) arg1;
        int[] positions = null;
        if (arg2.isInteger()) {
          positions = new int[1];
          positions[0] = ((IInteger) arg2).toIntDefault();
          if (positions[0] == Integer.MIN_VALUE) {
            return F.NIL;
          }
        }
        if (positions != null) {
          int size = arg1AST.size();
          for (int i = 0; i < positions.length; i++) {
            if (positions[i] < 0) {
              positions[i] = size + positions[i];
            }
          }
          IAST resultList = EvalAttributes.flattenAt(arg1AST.topHead(), arg1AST, positions);
          if (resultList.isPresent()) {
            return resultList;
          }
          return arg1AST;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static final class Function extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head().equals(S.Function)) {
        if (!validateArgs(engine, ast)) {
          return F.NIL;
        }
        IExpr temp = engine.evalHoldPattern(ast, true, false);
        if (temp.isPresent() && !temp.equals(ast)) {
          return temp;
        }
        return F.NIL;
      }

      if (ast.head().isAST()) {
        final IAST function = (IAST) ast.head();
        if (!validateArgs(engine, function)) {
          return F.NIL;
        }
        int attributes = ISymbol.NOATTRIBUTE;
        if (function.argSize() == 3) {
          final IExpr arg3 = function.arg3();
          attributes = AttributeFunctions.getSymbolsAsAttributes(arg3.makeList(), engine);
        }
        IAST astEvaled = engine.evalArgs(ast, attributes).orElse(ast);

        IExpr arg1 = function.arg1();
        if (function.isAST1()) {
          return Lambda.replaceSlotsOrElse(arg1, astEvaled, arg1);
        } else if (function.isAST2() || function.isAST3()) {
          IExpr arg2 = function.arg2();
          IAST symbolSlots = arg1.makeList();
          if (symbolSlots.size() > astEvaled.size()) {
            // To many parameters in `1` to be filled from `2`.
            return IOFunctions.printMessage(S.Function, "fpct", F.list(symbolSlots, function),
                engine);
          }
          java.util.IdentityHashMap<ISymbol, IExpr> moduleVariables =
              new IdentityHashMap<ISymbol, IExpr>();
          // final long moduleCounter = engine.incModuleCounter();
          IExpr subst = arg2
              .accept(new ModuleReplaceAll(moduleVariables, engine, EvalEngine.uniqueName("$")));
          if (subst.isPresent()) {
            arg2 = subst;
          }

          return arg2.replaceAll(x -> {
            IExpr temp = getRulesMap(symbolSlots, astEvaled).get(x);
            return temp != null ? temp : F.NIL;
          }).orElse(arg2);
        }

      }
      return F.NIL;
    }

    private static boolean validateArgs(EvalEngine engine, final IAST function) {
      if (function.argSize() > 3 || function.argSize() <= 0) {
        // `1` called with `2` arguments; between `3` and `4` arguments are expected.
        IOFunctions.printMessage(S.Function, "argb",
            F.List(S.Function, F.ZZ(function.argSize()), F.C1, F.C3), engine);
        return false;
      }
      if (function.isAST2()) {
        IExpr arg1 = function.arg1();
        if (arg1.isList()) {
          IAST listOfSymbols = (IAST) arg1;
          for (int i = 1; i < listOfSymbols.size(); i++) {
            if (!listOfSymbols.get(i).isSymbol()) {
              // Parameter specification `1` in `2` should be a symbol or a list of symbols.
              IOFunctions.printMessage(S.Function, "flpar", F.List(arg1, function), engine);
              return false;
            }
          }
        } else if (!arg1.isSymbol()) {
          // Parameter specification `1` in `2` should be a symbol or a list of symbols.
          IOFunctions.printMessage(S.Function, "flpar", F.List(arg1, function), engine);
        }
      }
      return true;
    }

    private static java.util.Map<IExpr, IExpr> getRulesMap(final IAST symbolSlots, final IAST ast) {
      int size = symbolSlots.argSize();
      final java.util.Map<IExpr, IExpr> rulesMap;
      if (size <= 5) {
        rulesMap = new OpenFixedSizeMap<IExpr, IExpr>(size * 3 - 1);
      } else {
        rulesMap = new HashMap<IExpr, IExpr>();
      }
      for (int i = 1; i <= size; i++) {
        rulesMap.put(symbolSlots.get(i), ast.get(i));
      }
      return rulesMap;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // don't set HOLDALL - the arguments are evaluated before applying the 'function
      // head'
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Head(expr)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the head of the expression or atom <code>expr</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>Head(expr, newHead)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>newHead(Head(expr))</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Head(a * b)
   * Times
   *
   * &gt;&gt; Head(6)
   * Integer
   *
   * &gt;&gt; Head(6+I)
   * Complex
   *
   * &gt;&gt; Head(6.0)
   * Real
   *
   * &gt;&gt; Head(6.0+I)
   * Complex
   *
   * &gt;&gt; Head(3/4)
   * Rational
   *
   * &gt;&gt; Head(x)
   * Symbol
   * </code>
   * </pre>
   */
  private static class Head extends AbstractCoreFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        return F.unaryAST1(engine.evaluate(ast.arg2()), engine.evaluate(ast.arg1()).head());
      }
      return ast.arg1().eval(engine).head();
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** Count the number of leaves of an expression. */
  public static class LeafCount extends AbstractCoreFunctionEvaluator {

    /** Calculate the number of leaves in an AST */
    private static class LeafCountVisitor extends AbstractVisitorLong {
      int fHeadOffset;

      public LeafCountVisitor() {
        this(1);
      }

      public LeafCountVisitor(int hOffset) {
        fHeadOffset = hOffset;
      }

      @Override
      public long visit(IAST list) {
        long sum = 0;
        for (int i = fHeadOffset; i < list.size(); i++) {
          sum += list.get(i).accept(this);
        }
        return sum;
      }

      @Override
      public long visit(IComplex element) {
        return element.leafCount();
      }

      @Override
      public long visit(IComplexNum element) {
        return element.leafCount();
      }

      @Override
      public long visit(IFraction element) {
        return element.leafCount();
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.ZZ(engine.evaluate(ast.arg1()).leafCount());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * Map(f, expr)  or  f /@ expr
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * applies <code>f</code> to each part on the first level of <code>expr</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Map(f, expr, levelspec)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * applies f to each level specified by <code>levelspec</code> of <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; f /@ {1, 2, 3}
   * {f(1),f(2),f(3)}
   * &gt;&gt; #^2&amp; /@ {1, 2, 3, 4}
   * {1,4,9,16}
   * </pre>
   *
   * <p>
   * Map <code>f</code> on the second level:
   *
   * <pre>
   * &gt;&gt; Map(f, {{a, b}, {c, d, e}}, {2})
   * {{f(a),f(b)},{f(c),f(d),f(e)}}
   * </pre>
   *
   * <p>
   * Include heads:
   *
   * <pre>
   * &gt;&gt; Map(f, a + b + c, Heads-&gt;True)
   * f(Plus)[f(a),f(b),f(c)]
   * </pre>
   *
   * <p>
   * Level specification a + b is not of the form n, {n}, or {m, n}.
   *
   * <pre>
   * &gt;&gt; Map(f, expr, a+b, Heads-&gt;True)
   * Map(f, expr, a + b, Heads -&gt; True)
   * </pre>
   */
  private static class Map extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      int lastIndex = ast.argSize();
      boolean heads = false;
      final OptionArgs options = new OptionArgs(ast.topHead(), ast, lastIndex, engine);
      if (options.isInvalidPosition(3)) {
        return options.printNonopt(ast, 3, engine);
      }
      IExpr option = options.getOption(S.Heads);
      if (option.isPresent()) {
        lastIndex--;
        heads = option.isTrue();
      }

      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (ast.isAST2()) {
        if (arg2.isSparseArray()) {
          return ((ISparseArray) arg2).map(x -> F.unaryAST1(arg1, x));
        }
      }
      VisitorLevelSpecification level;
      if (lastIndex == 3) {
        level = new VisitorLevelSpecification(x -> F.unaryAST1(arg1, x), ast.get(lastIndex), heads,
            engine);
      } else {
        level = new VisitorLevelSpecification(x -> F.unaryAST1(arg1, x), 1, heads);
      }
      return arg2.accept(level).orElse(arg2);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_4_2;
    }
  }

  private static class MapAll extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr arg1 = ast.arg1();
      final VisitorLevelSpecification level =
          new VisitorLevelSpecification(x -> F.unaryAST1(arg1, x), 0, Integer.MAX_VALUE, false);

      final IExpr result = ast.arg2().accept(level);
      return result.isPresent() ? result : ast.arg2();
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class MapAt extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        if (ast.head().isAST2() && ast.isAST1()) {
          IAST headAST = (IAST) ast.head();
          ast = F.ternaryAST3(headAST.topHead(), headAST.arg1(), ast.arg1(), headAST.arg2());
        } else {
          return F.NIL;
        }
      }

      if (ast.isAST3()) {
        final IExpr arg2 = ast.arg2();
        if (arg2.isASTOrAssociation()) {
          try {
            final IExpr arg1 = ast.arg1();
            IExpr arg3 = ast.arg3();
            if (arg3.isInteger() || arg3.isString() || arg3.isAST(S.Key, 2) || arg3.equals(S.All)) {
              arg3 = F.list(arg3);
            }
            if (arg3.isListOfLists()) {
              IAST listOfLists = ((IAST) arg3);
              IAST result = ((IAST) arg2);
              for (int i = 1; i < listOfLists.size(); i++) {
                IExpr temp =
                    mapAtRecursive(x -> F.unaryAST1(arg1, x), result, listOfLists.getAST(i), 1);
                if (temp.isPresent()) {
                  if (temp.isAST()) {
                    result = (IAST) temp;
                  }
                }
              }
              return result;

            } else if (arg3.isList()) {
              IExpr temp = mapAtRecursive(x -> F.unaryAST1(arg1, x), ((IAST) arg2), (IAST) arg3, 1);
              if (temp.isPresent()) {
                return temp;
              }
              return arg2;
            }
          } catch (final ValidateException ve) {
            return IOFunctions.printMessage(ast.topHead(), ve, engine);
          } catch (RuntimeException ae) {
            LOGGER.debug("MapAt.evaluate() failed", ae);
          }
        }
      }
      return F.NIL;
    }

    private static IExpr mapAtRecursive(java.util.function.Function<IExpr, IExpr> f, IAST result,
        IAST positions, int index) {
      IExpr pos = positions.get(index);
      if (pos.equals(S.All)) {
        IASTMutable subResult;
        if (index == positions.size() - 1) {
          subResult = result.copy();
          for (int i = 1; i < result.size(); i++) {
            IExpr temp = f.apply(result.get(i));
            if (temp.isPresent()) {
              subResult.set(i, temp);
            }
          }
        } else {
          subResult = result.copy();
          for (int i = 1; i < result.size(); i++) {
            IExpr temp = mapAtRecursive(f, subResult.getAST(i), positions, index + 1);
            if (temp.isPresent()) {
              subResult.set(i, temp);
            }
          }
        }
        return subResult;
      }
      if (pos.isString() || pos.isAST(S.Key, 2)) {
        if (result.isAssociation()) {
          IExpr key = pos.isString() ? pos : pos.first();
          IAST rule = ((IAssociation) result).getRule(key);
          if (rule.isPresent()) {
            if (index == positions.size() - 1) {
              IExpr temp = f.apply(rule.second());
              if (temp.isPresent()) {
                rule = rule.setAtCopy(2, temp);
                IASTAppendable association = result.copyAppendable();
                association.appendRule(rule);
                return association;
              }

            } else {
              IExpr arg = rule.second();
              if (arg.isASTOrAssociation()) {
                IExpr temp = mapAtRecursive(f, ((IAST) arg), positions, index + 1);
                if (temp.isPresent()) {
                  rule = rule.setAtCopy(2, temp);
                  IASTAppendable association = result.copyAppendable();
                  association.appendRule(rule);
                  return association;
                }
              }
            }
          }
          // Part `1` of `2` does not exist.
          throw new ArgumentTypeException("partw", F.list(F.list(pos), result));
        }
      }

      int p = pos.toIntDefault();
      if (p == Integer.MIN_VALUE) {
        // Part `1` of `2` does not exist.
        throw new ArgumentTypeException("partw", F.list(F.list(pos), result));
      }
      if (p < 0) {
        p = result.size() + p;
      }

      if (p >= 0 && p < result.size()) {
        if (index == positions.size() - 1) {
          IExpr temp = f.apply(result.get(p));
          if (temp.isPresent()) {
            if (result.isAssociation()) {
              IExpr rule = ((IAST) result.getRule(p)).setAtCopy(2, temp);
              return result.setAtCopy(p, rule);
            }
            return result.setAtCopy(p, temp);
          }
        } else {

          IExpr arg = result.get(p);
          if (arg.isASTOrAssociation()) {
            IExpr temp = mapAtRecursive(f, ((IAST) arg), positions, index + 1);
            if (temp.isPresent()) {
              return result.setAtCopy(p, temp);
            }
          }
        }
      }
      // Part `1` of `2` does not exist.
      throw new ArgumentTypeException("partw", F.list(F.list(pos), result));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3_0;
    }
  }

  /**
   *
   *
   * <pre>
   * MapIndexed(f, expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * applies <code>f</code> to each part on the first level of <code>expr</code> and appending the
   * elements position as a list in the second argument.
   *
   * </blockquote>
   *
   * <pre>
   * MapIndexed(f, expr, levelspec)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * applies <code>f</code> to each level specified by <code>levelspec</code> of <code>expr
   * </code> and appending the elements position as a list in the second argument.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; MapIndexed(f, {{{{a, b}, {c, d}}}, {{{u, v}, {s, t}}}}, 2)
   * {f({f({{a,b},{c,d}},{1,1})},{1}),f({f({{u,v},{s,t}},{2,1})},{2})}
   * </pre>
   */
  private static final class MapIndexed extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      int lastIndex = ast.argSize();
      boolean heads = false;
      final OptionArgs options = new OptionArgs(ast.topHead(), ast, lastIndex, engine);
      IExpr option = options.getOption(S.Heads);
      if (option.isPresent()) {
        lastIndex--;
        if (option.isTrue()) {
          heads = true;
        }
      }

      IExpr arg1 = ast.arg1();
      IndexedLevel level;
      if (lastIndex == 3) {
        level =
            new IndexedLevel((x, y) -> F.binaryAST2(arg1, x, y), ast.get(lastIndex), heads, engine);
      } else {
        level = new IndexedLevel((x, y) -> F.binaryAST2(arg1, x, y), 1, heads);
      }

      IExpr arg2 = ast.arg2();
      if (arg2.isSparseArray()) {
        arg2 = arg2.normal(false);
      }
      if (arg2.isAssociation()) {
        // `1` currently not supported in `2`.
        return IOFunctions.printMessage(ast.topHead(), "unsupported",
            F.List(S.Association, S.MapIndexed), engine);
      }
      if (arg2.isAST()) {
        return level.visitAST(((IAST) arg2), new int[0]).orElse(arg2);
      }
      return arg2;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3_2;
    }
  }

  /**
   *
   *
   * <pre>
   * MapThread(`f`, {{`a1`, `a2`, ...}, {`b1`, `b2`, ...}, ...})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns '{<code>f</code>(<code>a1</code>, <code>b1</code>, &hellip;), <code>f</code>(<code>
   * a2</code>, <code>b2</code>, &hellip;), &hellip;}'.<br>
   *
   * </blockquote>
   *
   * <pre>
   * MapThread(`f`, {`expr1`, `expr2`, ...}, `n`)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * applies <code>f</code> at level <code>n</code>.<br>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; MapThread(f, {{a, b, c}, {1, 2, 3}})
   * {f(a,1),f(b,2),f(c,3)}
   *
   * &gt;&gt; MapThread(f, {{{a, b}, {c, d}}, {{e, f}, {g, h}}}, 2)
   * {{f(a, e), f(b, f)}, {f(c, g), f(d, h)}}
   * </pre>
   *
   * <p>
   * Non-negative machine-sized integer expected at position 3 in MapThread(f, {{a, b}, {c, d}},
   * {1}).<br>
   *
   * <pre>
   * &gt;&gt; MapThread(f, {{a, b}, {c, d}}, {1})
   * MapThread(f, {{a, b}, {c, d}}, {1})
   * </pre>
   *
   * <p>
   * Object {a, b} at position {2, 1} in MapThread(f, {{a, b}, {c, d}}, 2) has only 1 of required 2
   * dimensions.<br>
   *
   * <pre>
   * &gt;&gt; MapThread(f, {{a, b}, {c, d}}, 2)
   * MapThread(f, {{a, b}, {c, d}}, 2)
   * </pre>
   *
   * <p>
   * Incompatible dimensions of objects at positions {2, 1} and {2, 2} of MapThread(f, {{a}, {b,
   * c}}); dimensions are 1 and 2.<br>
   *
   * <pre>
   * &gt;&gt; MapThread(f, {{a}, {b, c}})
   * MapThread(f, {{a}, {b, c}})
   *
   * &gt;&gt; MapThread(f, {})
   * {}
   *
   * &gt;&gt; MapThread(f, {a, b}, 0)
   * f(a, b)
   * </pre>
   *
   * <p>
   * Object a at position {2, 1} in MapThread(f, {a, b}, 1) has only 0 of required 1 dimensions.
   *
   * <pre>
   * &gt;&gt; MapThread(f, {a, b}, 1)
   * MapThread(f, {a, b}, 1)
   * </pre>
   */
  private static final class MapThread extends AbstractFunctionEvaluator {

    private static class MapThreadLevel {
      /** The permutation of the result tensor */
      final int level;

      final IExpr constant;

      private MapThreadLevel(IExpr constant, int level) {
        this.constant = constant;
        this.level = level;
      }

      private IAST mapThreadRecursive(final int recursionLevel, IAST lst,
          IASTAppendable resultList) {
        if (recursionLevel >= level) {
          return lst;
        }
        int size = lst.first().size() - 1;
        IAST list;
        if (level == recursionLevel + 1) {
          list = EvalAttributes.threadList(lst, S.List, constant, size);
          if (resultList != null) {
            resultList.append(list);
          }
        } else {
          list = EvalAttributes.threadList(lst, S.List, S.List, size);
          IASTAppendable result = F.ListAlloc(size);
          list.forEach(x -> mapThreadRecursive(recursionLevel + 1, (IAST) x, result));
          if (resultList != null) {
            resultList.append(result);
          }
          return result;
        }
        return list;
      }
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.arg2().isAST()) {
        int level = 1;
        if (ast.isAST3()) {
          level = ast.arg3().toIntDefault(-1);
          if (level < 0) {
            return F.NIL;
          }
        }

        IAST tensor = (IAST) ast.arg2();
        IntList dims = LinearAlgebra.dimensions(tensor, tensor.head());
        if (dims.size() > level) {
          if (level == 0) {
            return tensor.apply(ast.arg1());
          }
          // if (level == 1) {
          // return EvalAttributes.threadList(tensor, S.List, ast.arg1(), dims.get(level));
          // }
          return new MapThreadLevel(ast.arg1(), level).mapThreadRecursive(0, tensor, null);
        }
        if (tensor.isEmptyList()) {
          return tensor;
        }
        LOGGER.log(engine.getLogLevel(), "MapThread: argument 2 dimensions less than level.");
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3_2;
    }
  }

  /**
   *
   *
   * <pre>
   * Order(a, b)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * is <code>0</code> if <code>a</code> equals <code>b</code>. Is <code>-1</code> or <code>1
   * </code> according to canonical order of <code>a</code> and <code>b</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Order(3,4)
   * 1
   *
   * &gt;&gt; Order(4,3)
   * -1
   * </pre>
   */
  private static class Order extends AbstractFunctionEvaluator {

    /**
     * Compares the first expression with the second expression for order. Returns 1, 0, -1 as this
     * expression is canonical less than, equal to, or greater than the specified expression. <br>
     * <br>
     * (<b>Implementation note</b>: see the different results in the <code>IExpr#compareTo(IExpr)
     * </code> method)
     *
     * @see org.matheclipse.core.interfaces.IExpr#compareTo(IExpr)
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.ZZ(compareTo(ast.arg1(), ast.arg2()));
    }

    public int compareTo(final IExpr arg1, final IExpr arg2) {
      final int cp = arg1.compareTo(arg2);
      if (cp < 0) {
        return 1;
      } else if (cp > 0) {
        return -1;
      }
      return 0;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static final class NumericalOrder extends Order {

    @Override
    public int compareTo(final IExpr arg1, final IExpr arg2) {
      // canonical order
      final int cp = arg1.compareToNumerical(arg2);
      if (cp < 0) {
        return 1;
      } else if (cp > 0) {
        return -1;
      }
      return 0;
    }

  }

  /**
   *
   *
   * <pre>
   * OrderedQ({a, b})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * is <code>True</code> if <code>a</code> sorts before <code>b</code> according to canonical
   * ordering.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; OrderedQ({a, b})
   * True
   *
   * &gt;&gt; OrderedQ({b, a})
   * False
   * </pre>
   */
  private static final class OrderedQ extends AbstractFunctionEvaluator implements Predicate<IAST> {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAST arg1AST = Validate.checkASTOrAssociationType(ast, ast.arg1(), 1, engine);
      if (arg1AST.isPresent()) {
        if (ast.isAST2()) {
          return F.booleSymbol(test(arg1AST, ast.arg2(), engine));
        }
        return F.booleSymbol(test(arg1AST));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public boolean test(IAST ast) {
      return ast.compareAdjacent((x, y) -> x.isLEOrdered(y));
    }

    /**
     * Test the ordering of elements with the <code>comparatorHead</code> as head of a binary
     * predicate: <code>comparatorHead(x,y)</code>.
     * 
     * @param ast
     * @param comparatorHead
     * @param engine
     * @return
     */
    private static boolean test(final IAST ast, final IExpr comparatorHead,
        final EvalEngine engine) {
      return ast.compareAdjacent((x, y) -> comparatorBiPredicate(comparatorHead, x, y, engine));
    }

    private static boolean comparatorBiPredicate(final IExpr comparatorHead, final IExpr x,
        final IExpr y, final EvalEngine engine) {
      IExpr predicateResult = engine.evaluate(F.binaryAST2(comparatorHead, x, y));
      if (predicateResult.isFalse() || predicateResult.isMinusOne()) {
        return false;
      }
      // all other values are interpreted as "x and y are in order"
      return true;
    }
  }

  /**
   *
   *
   * <pre>
   * Operate(p, expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * applies <code>p</code> to the head of <code>expr</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Operate(p, expr, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * applies <code>p</code> to the <code>n</code>th head of <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Operate(p, f(a, b))
   * p(f)[a,b]
   * </pre>
   *
   * <p>
   * The default value of <code>n</code> is <code>1</code>:
   *
   * <pre>
   * &gt;&gt; Operate(p, f(a, b), 1)
   * p(f)[a,b]
   * </pre>
   *
   * <p>
   * With <code>n = 0</code>, <code>Operate</code> acts like <code>Apply</code>:
   *
   * <pre>
   * &gt;&gt; Operate(p, f(a)[b][c], 0)
   * p(f(a)[b][c])
   *
   * &gt;&gt; Operate(p, f(a)[b][c])
   * p(f(a)[b])[c]
   *
   * &gt;&gt; Operate(p, f(a)[b][c], 1)
   * p(f(a)[b])[c]
   *
   * &gt;&gt; Operate(p, f(a)[b][c], 2)
   * p(f(a))[b][c]
   *
   * &gt;&gt; Operate(p, f(a)[b][c], 3)
   * p(f)[a][b][c]
   *
   * &gt;&gt; Operate(p, f(a)[b][c], 4)
   * f(a)[b][c]
   *
   * &gt;&gt; Operate(p, f)
   * f
   *
   * &gt;&gt; Operate(p, f, 0)
   * p(f)
   * </pre>
   *
   * <p>
   * Non-negative integer expected at position <code>3</code> in <code>Operate(p, f, -1)</code>.
   *
   * <pre>
   * &gt;&gt; Operate(p, f, -1)
   * Operate(p, f, -1)
   * </pre>
   */
  private static final class Operate extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int headDepth = 1;
      if (ast.isAST3()) {
        if (!ast.arg3().isInteger()) {
          return F.NIL;
        }
        IInteger depth = (IInteger) ast.arg3();
        if (depth.isNegative()) {
          // Non-negative integer expected.
          return IOFunctions.printMessage(ast.topHead(), "intnn", F.CEmptyList, engine);
          // LOGGER.log(
          // engine.getLogLevel(), "Non-negative integer expected at position 3 in
          // Operate()");
          // return F.NIL;
        }

        headDepth = depth.toIntDefault();
        if (headDepth == Integer.MIN_VALUE) {
          return F.NIL;
        }
      }

      IExpr p = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (headDepth == 0) {
        // act like Apply()
        return F.unaryAST1(p, arg2);
      }

      if (!arg2.isAST()) {
        return arg2;
      }

      IExpr expr = arg2;
      for (int i = 1; i < headDepth; i++) {
        expr = expr.head();
        if (!expr.isAST()) {
          // headDepth is higher than the depth of heads in arg2
          // return arg2 unmodified.
          return arg2;
        }
      }

      IASTAppendable result = ((IAST) arg2).copyAppendable();
      IASTAppendable last = result;
      IASTAppendable head = result;

      for (int i = 1; i < headDepth; i++) {
        head = ((IAST) head.head()).copyAppendable();
        last.set(0, head);
        last = head;
      }

      head.set(0, F.unaryAST1(p, head.head()));
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  private static class ParallelMap extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      int lastIndex = ast.argSize();
      boolean heads = false;
      final OptionArgs options = new OptionArgs(ast.topHead(), ast, lastIndex, engine);
      if (options.isInvalidPosition(3)) {
        return options.printNonopt(ast, 3, engine);
      }
      IExpr option = options.getOption(S.Heads);
      if (option.isPresent()) {
        lastIndex--;
        heads = option.isTrue();
      }

      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (ast.isAST2()) {
        if (arg2.isSparseArray()) {
          return ((ISparseArray) arg2).map(x -> F.unaryAST1(arg1, x));
        }
      }
      VisitorLevelSpecification level;
      if (lastIndex == 3) {
        level = new VisitorLevelSpecification(x -> F.unaryAST1(arg1, x), ast.get(lastIndex), heads,
            engine);
      } else {
        level = new VisitorLevelSpecification(x -> F.unaryAST1(arg1, x), 1, heads);
      }
      return arg2.accept(level).orElse(arg2);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_4_2;
    }
  }

  private static final class PatternOrder extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() == 3) {
        IExpr arg1 = ast.arg1();
        IExpr arg2 = ast.arg2();
        final PatternMatcherAndEvaluator pmEvaluator1 =
            new PatternMatcherAndEvaluator(arg1, S.Null);
        final PatternMatcherAndEvaluator pmEvaluator2 =
            new PatternMatcherAndEvaluator(arg2, S.Null);
        // IPatternMap patternMap1 = pmEvaluator1.getPatternMap();
        // IPatternMap patternMap2 = pmEvaluator2.getPatternMap();

        int[] priority1 = new int[] {IPatternMap.DEFAULT_RULE_PRIORITY};
        IPatternMap.determinePatterns(arg1, priority1, null);
        // patternMap1.determinePatterns(arg1, priority1);
        int[] priority2 = new int[] {IPatternMap.DEFAULT_RULE_PRIORITY};
        IPatternMap.determinePatterns(arg2, priority2, null);
        // patternMap2.determinePatterns(arg2, priority2);
        if (pmEvaluator1.isRuleWithoutPatterns()) {
          if (pmEvaluator2.isRuleWithoutPatterns()) {
            return F.ZZ(-1 * arg1.compareTo(arg2));
          }
          return F.C1;
        }
        if (pmEvaluator2.isRuleWithoutPatterns()) {
          return F.CN1;
        }
        if (priority1[0] > priority2[0]) {
          return F.C1;
        } else if (priority1[0] < priority2[0]) {
          return F.CN1;
        }

        return F.ZZ(pmEvaluator1.equivalentLHS(pmEvaluator2));
      }
      return F.NIL;
    }
  }

  /** Implements both the <code>Exit()</code> and <code>Quit()</code> function. */
  private static final class QuitExit extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return quitEngine(ast, engine);
    }

    private static IExpr quitEngine(final IAST ast, EvalEngine engine) {
      EvalEngine newEngine = new EvalEngine("", engine.getRecursionLimit(),
          engine.getIterationLimit(), null, null, engine.isRelaxedSyntax());
      engine.setPrintStreamsOf(engine);

      EvalHistory lch = engine.getEvalHistory();
      if (lch != null) {
        newEngine.setOutListDisabled(false, lch.getHistoryLength());
      }
      EvalEngine.setReset(newEngine);
      if (ast.isAST1()) {
        int value = ast.arg1().toIntDefault();
        if (value < 0) {
          return IOFunctions.printMessage(ast.topHead(), "intnn", F.CEmptyList, newEngine);
        }
        if (value > 0) {
          return F.ZZ(value);
        }
      }
      return S.Null;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * Scan(f, expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * applies <code>f</code> to each element of <code>expr</code> and returns 'Null'.
   *
   * </blockquote>
   *
   * <pre>
   * Scan(f, expr, levelspec)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * applies <code>f</code> to each level specified by <code>levelspec</code> of <code>expr
   * </code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Scan(Print, {1, 2, 3})
   *  1
   *  2
   *  3
   * &gt;&gt; Scan(Print, f(g(h(x))), 2)
   *  h(x)
   *  g(h(x))
   * &gt;&gt; Scan(Print)({1, 2})
   *  1
   *  2
   * &gt;&gt; Scan(Return, {1, 2})
   * 1
   * </pre>
   */
  private static final class Scan extends Map {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      final int argSize = ast.argSize();
      if (argSize >= 2) {
        int lastIndex = argSize;
        boolean heads = false;
        if (argSize > 2) {
          final OptionArgs options = new OptionArgs(ast.topHead(), ast, lastIndex, engine);
          IExpr option = options.getOption(S.Heads);
          if (option.isPresent()) {
            lastIndex--;
            if (option.isTrue()) {
              heads = true;
            }
          }
        }

        try {
          IExpr arg1 = ast.arg1();
          IExpr arg2 = ast.arg2();
          if (lastIndex == 3) {
            IASTAppendable result = F.ListAlloc(10);
            java.util.function.Function<IExpr, IExpr> sf = x -> {
              IAST a = F.unaryAST1(arg1, x);
              result.append(a);
              return F.NIL;
            };
            VisitorLevelSpecification level =
                new VisitorLevelSpecification(sf, ast.get(lastIndex), heads, engine);
            arg2.accept(level);
            result.forEach(result.size(), x -> engine.evaluate(x));
          } else {
            if (arg2.isAST()) {
              ((IAST) arg2).forEach(x -> engine.evaluate(F.unaryAST1(arg1, x)), heads ? 0 : 1);
            }
          }
          return S.Null;
        } catch (final ReturnException e) {
          return e.getValue();
          // don't catch Throw[] here !
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4_2;
    }
  }

  /**
   *
   *
   * <pre>
   * Sort(list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * sorts $list$ (or the leaves of any other expression) according to canonical ordering.
   *
   * </blockquote>
   *
   * <pre>
   * Sort(list, p)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * sorts using <code>p</code> to determine the order of two elements.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Sort({4, 1.0, a, 3+I})
   * {1.0,4,3+I,a}
   * </pre>
   */
  private static class Sort extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isASTOrAssociation()) {
        IAST arg1 = (IAST) ast.arg1();
        if (ast.isAST1()) {
          if (arg1.isEvalFlagOn(IAST.IS_SORTED)) {
            return arg1;
          }
          if (arg1.isAssociation()) {
            return ((IAssociation) arg1).sort();
          }
        } else {
          if (arg1.isAssociation()) {
            return ((IAssociation) arg1).sort(new Predicates.IsBinaryFalse(ast.arg2()));
          }
        }
        final IASTMutable shallowCopy = ((IAST) ast.arg1()).copy();
        if (shallowCopy.size() <= 2) {
          return shallowCopy;
        }
        try {
          if (ast.isAST1()) {
            EvalAttributes.sort(shallowCopy);
          } else {
            // use the 2nd argument as a head for the comparator operation:
            EvalAttributes.sort(shallowCopy, new Predicates.IsBinaryFalse(ast.arg2()));
          }
          return shallowCopy;
        } catch (RuntimeException rex) {
          LOGGER.error("Sort.evaluate() failed", rex);
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Sort(list, f)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * sorts <code>list</code> (or the leaves of any other expression) according to canonical ordering
   * of the keys that are extracted from the <code>list</code>'s elements using <code>f
   * </code>. Chunks of leaves that appear the same under <code>f</code> are sorted according to
   * their natural order (without applying <code>f</code>).
   *
   * </blockquote>
   *
   * <pre>
   * <code>Sort(f)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * creates an operator function that, when applied, sorts by <code>f</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; SortBy({{5, 1}, {10, -1}}, Last)
   * {{10,-1},{5,1}}
   *
   * &gt;&gt; SortBy(Total)[{{5, 1}, {10, -9}}]
   * {{10,-9},{5,1}}
   * </code>
   * </pre>
   */
  private static class SortBy extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        try {
          if (ast.arg1().isDataset()) {
            List<String> listOfStrings = Convert.toStringList(ast.arg2());
            if (listOfStrings != null) {
              return ((IASTDataset) ast.arg1()).groupBy(listOfStrings);
            }
            return F.NIL;
          }
          if (ast.arg1().isASTOrAssociation()) {
            final IAST arg1 = (IAST) ast.arg1();
            final IExpr arg2 = ast.arg2();

            // sort a list of indices. after sorting, we reorder the leaves.
            final IASTAppendable sortAST = F.mapRange(1, arg1.size(), i -> {
              IExpr unary = engine.evaluate(F.unaryAST1(arg2, arg1.get(i)));
              return F.binaryAST2(S.List, unary, F.ZZ(i));
            });
            EvalAttributes.sort(sortAST);

            return F.mapFunction(arg1.head(), sortAST, t -> {
              int sortedIndex = t.second().toIntDefault(-1);
              if (sortedIndex < 0) {
                // stop iterating and return fFalse
                return null;
              }
              return arg1.get(sortedIndex);
            });
          }
        } catch (ValidateException ve) {
          return IOFunctions.printMessage(ast.topHead(), ve, engine);
        } catch (RuntimeException rex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2_1;
    }
  }

  /**
   *
   *
   * <pre>
   * Symbol
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * is the head of symbols.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Head(x)
   * Symbol
   * </pre>
   *
   * <p>
   * You can use <code>Symbol</code> to create symbols from strings:
   *
   * <pre>
   * &gt;&gt; Symbol("x") + Symbol("x")
   * 2*x
   * &gt;&gt; {\[Eta], \[CapitalGamma]\[Beta], Z\[Infinity], \[Angle]XYZ, \[FilledSquare]r, i\[Ellipsis]j}
   * {\u03b7, \u0393\u03b2, Z\u221e, \u2220XYZ, \u25a0r, i\u2026j}
   * </pre>
   */
  private static class Symbol extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isString()) {
        return F.symbol(ast.arg1().toString(), engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * SymbolName(s)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the name of the symbol <code>s</code> (without any leading context name).
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; SymbolName(x)  // InputForm
   * "x"
   * &gt;&gt; SymbolName(a`b`x)  // InputForm
   * "x"
   * </pre>
   */
  private static class SymbolName extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isSymbol()) {
        return F.stringx(ast.arg1().toString());
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * Thread(f(args)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * threads <code>f</code> over any lists that appear in <code>args</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Thread(f(args), h)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * threads over any parts with head <code>h</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Thread(f({a, b, c}))
   * {f(a),f(b),f(c)}
   *
   * &gt;&gt; Thread(f({a, b, c}, t))
   * {f(a,t),f(b,t),f(c,t)}
   *
   * &gt;&gt; Thread(f(a + b + c), Plus)
   * f(a)+f(b)+f(c)
   * </pre>
   *
   * <p>
   * Functions with attribute <code>Listable</code> are automatically threaded over lists:
   *
   * <pre>
   * &gt;&gt; {a, b, c} + {d, e, f} + g
   * {a+d+g,b+e+g,c+f+g}
   * </pre>
   */
  private static final class Thread extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1().isAST())) {
        return F.NIL;
      }
      // LevelSpec level = null;
      // if (functionList.isAST3()) {
      // level = new LevelSpecification(functionList.arg3());
      // } else {
      // level = new LevelSpec(1);
      // }
      IExpr head = S.List;
      if (ast.isAST2()) {
        head = ast.arg2();
      }
      final IAST list = (IAST) ast.arg1();
      if (list.size() > 1) {
        return threadList(list, head, list.head()).orElse(list);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    /**
     * Thread through all lists in the arguments of the IAST [i.e. the list header has the attribute
     * ISymbol.LISTABLE] example: Sin[{2,x,Pi}] ==> {Sin[2],Sin[x],Sin[Pi]}
     *
     * @param list
     * @param head the head over which
     * @param mapHead the arguments head (typically <code>ast.head()</code>)
     * @return
     */
    public static IAST threadList(final IAST list, IExpr head, IExpr mapHead) {

      int listLength = -1;

      for (int i = 1; i < list.size(); i++) {
        if ((list.get(i).isAST()) && (((IAST) list.get(i)).head().equals(head))) {
          if (listLength == -1) {
            listLength = ((IAST) list.get(i)).argSize();
          } else {
            if (listLength != ((IAST) list.get(i)).argSize()) {
              // Objects of unequal length in `1` cannot be combined.
              IOFunctions.printMessage(S.Thread, "tdlen", F.list(list), EvalEngine.get());
              listLength = -1;
              return F.NIL;
              // for loop
            }
          }
        }
      }
      if (listLength == -1) {
        return list;
      }
      return EvalAttributes.threadList(list, head, mapHead, listLength);
    }
  }

  /**
   *
   *
   * <pre>
   * Through(p(f)[x])
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives <code>p(f(x))</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Through(f(g)[x])
   * f(g(x))
   *
   * &gt;&gt; Through(p(f, g)[x])
   * p(f(x), g(x))
   *
   * &gt;&gt; Through(p(f, g)[x, y])
   * p(f(x, y), g(x, y))
   *
   * &gt;&gt; Through(p(f, g)[])
   * p(f(), g())
   *
   * &gt;&gt; Through(p(f, g))
   * Through(p(f, g))
   *
   * &gt;&gt; Through(f()[x])
   * f()
   * </pre>
   */
  private static class Through extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isAST()) {
        IAST arg1AST = (IAST) ast.arg1();
        IExpr arg1Head = arg1AST.head();
        if (arg1Head.isAST()) {

          final IAST arg1HeadAST = (IAST) arg1Head;
          final IExpr head = arg1HeadAST.head();
          if (ast.isAST2() && !head.equals(ast.arg2())) {
            return arg1AST;
          }

          return F.mapFunction(head, arg1HeadAST, t -> arg1AST.apply(t));
        }
        return arg1AST;
      }
      return ast.arg1();
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * Maps the elements of the <code>expr</code> with the cloned <code>replacement</code>. <code>
   * replacement</code> is an IAST where the argument at the given position will be replaced by the
   * currently mapped element. Thread over the following headers: <code>
   * S.And, S.Or, S.Xor, S.Nand, S.Nor, S.Not, S.Implies, S.Equivalent, S.Equal,S.Unequal, S.Less, S.Greater, S.LessEqual, S.GreaterEqual
   * </code>
   *
   * @param expr typically the first element of <code>replacement</code> ast.
   * @param replacement an IAST there the argument at the given position is replaced by the
   *        currently mapped argument of this IAST.
   * @param position
   * @return
   */
  public static IAST threadLogicEquationOperators(IExpr expr, IAST replacement, int position) {
    if (expr.size() > 1 && expr.isAST()) {
      IAST ast = (IAST) expr;
      if (LOGIC_EQUATION_HEADS.contains(ast.head())) {
        // IASTMutable copy = replacement.setAtCopy(position, null);
        return ast.mapThread(replacement, position);
      }
    }
    return F.NIL;
  }

  /**
   * Maps the elements of the <code>expr</code> with the cloned <code>replacement</code>. <code>
   * replacement</code> is an IAST where the argument at the given position will be replaced by the
   * currently mapped element. Thread over the following headers: <code>
   * S.Plus, S.And, S.Or, S.Xor, S.Nand, S.Nor, S.Not, S.Implies, S.Equivalent, S.Equal,S.Unequal, S.Less, S.Greater, S.LessEqual, S.GreaterEqual
   * </code>
   *
   * @param expr typically the first element of <code>replacement</code> ast.
   * @param replacement an IAST there the argument at the given position is replaced by the
   *        currently mapped argument of this IAST.
   * @param position
   * @return
   */
  public static IAST threadPlusLogicEquationOperators(IExpr expr, IAST replacement, int position) {
    if (expr.size() > 1 && expr.isAST()) {
      IAST ast = (IAST) expr;
      if (PLUS_LOGIC_EQUATION_HEADS.contains(ast.head())) {
        // IASTMutable copy = replacement.setAtCopy(position, null);
        return ast.mapThread(replacement, position);
      }
    }
    return F.NIL;
  }

  /**
   * Maps the elements of the <code>expr</code> with the cloned <code>replacement</code>. <code>
   * replacement</code> is an IAST where the argument at the given position will be replaced by the
   * currently mapped element. Thread over the following headers: <code>
   * S.List S.And, S.Or, S.Xor, S.Nand, S.Nor, S.Not, S.Implies, S.Equivalent, S.Equal,S.Unequal, S.Less, S.Greater, S.LessEqual, S.GreaterEqual
   * </code>
   *
   * @param expr typically the first element of <code>replacement</code> ast.
   * @param replacement an IAST there the argument at the given position is replaced by the
   *        currently mapped argument of this IAST.
   * @param position
   * @return
   */
  public static IAST threadListLogicEquationOperators(IExpr expr, IAST replacement, int position) {
    if (expr.size() > 1 && expr.isAST()) {
      IAST ast = (IAST) expr;
      if (LIST_LOGIC_EQUATION_HEADS.contains(ast.head())) {
        // IASTMutable copy = replacement.setAtCopy(position, null);
        return ast.mapThread(replacement, position);
      }
    }
    return F.NIL;
  }

  public static AbstractVisitorLong leafCountVisitor() {
    return new LeafCount.LeafCountVisitor(0);
  }

  public static void initialize() {
    Initializer.init();
  }

  private StructureFunctions() {}
}
