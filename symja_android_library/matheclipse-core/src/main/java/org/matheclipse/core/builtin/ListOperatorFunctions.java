package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.IPatternMatcher;

/**
 * Functions which apply functions or patterns to the elements of a list: <code>Construct</code>,
 * <code>Comap</code>, <code>AllMatch</code>, <code>AnyMatch</code>, <code>DeleteElements</code>,
 * <code>UniqueElements</code>, <code>OrderingBy</code>, <code>BlockMap</code>,
 * <code>Downsample</code> and <code>MovingMap</code>.
 */
public final class ListOperatorFunctions {

  private static class Initializer {

    private static void init() {
      S.Construct.setEvaluator(new Construct());
      S.Comap.setEvaluator(new Comap());
      S.AllMatch.setEvaluator(new MatchElements(S.AllMatch, true));
      S.AnyMatch.setEvaluator(new MatchElements(S.AnyMatch, false));
      S.DeleteElements.setEvaluator(new DeleteElements());
      S.UniqueElements.setEvaluator(new UniqueElements());
      S.OrderingBy.setEvaluator(new OrderingBy());
      S.BlockMap.setEvaluator(new BlockMap());
      S.Downsample.setEvaluator(new Downsample());
      S.MovingMap.setEvaluator(new MovingMap());
    }
  }

  /** <code>Construct(f, a1, a2, ...)</code> - <code>f(a1, a2, ...)</code>. */
  private static final class Construct extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IASTAppendable result = F.ast(ast.arg1(), ast.argSize() - 1);
      result.appendArgs(2, ast.size(), i -> ast.get(i));
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  /**
   * <code>Comap({f1, f2, ...}, x)</code> - <code>{f1(x), f2(x), ...}</code>; any other head than
   * <code>List</code> and an association keep their structure. An atom <code>f</code> gives
   * <code>f</code>. <code>Comap(fs, x, levelspec)</code> applies the parts of <code>fs</code> at
   * the levels to <code>x</code>. The operator form <code>Comap(fs)(x)</code> takes <code>x</code>
   * later.
   */
  private static final class Comap extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head() != S.Comap) {
        if (ast.head().isAST(S.Comap, 2) && ast.isAST1()) {
          return F.binaryAST2(S.Comap, ast.head().first(), ast.arg1());
        }
        return F.NIL;
      }
      if (ast.isAST1()) {
        return F.NIL;
      }
      IExpr functions = ast.arg1();
      IExpr x = ast.arg2();
      if (ast.isAST3() && !ast.arg3().isOne() && !ast.arg3().equals(F.list(F.C1))) {
        // every part at the levels becomes the part applied to x, the deepest parts first
        return F.ternaryAST3(S.Map, F.Function(F.unaryAST1(F.Slot1, x)), functions, ast.arg3());
      }
      if (functions.isAssociation()) {
        IAssociation assoc = (IAssociation) functions;
        IASTAppendable rules = F.ListAlloc(assoc.argSize());
        for (int i = 1; i < assoc.size(); i++) {
          IAST rule = assoc.getRule(i);
          rules.append(rule.setAtCopy(2, F.unaryAST1(rule.second(), x)));
        }
        return F.assoc(rules);
      }
      if (!functions.isAST()) {
        return functions;
      }
      return ((IAST) functions).map(f -> F.unaryAST1(f, x), 1);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ast.head() == S.Comap ? ARGS_1_3 : null;
    }
  }

  /**
   * <code>AllMatch(expr, form)</code> / <code>AnyMatch(expr, form)</code> - do all / any of the
   * elements of <code>expr</code> match <code>form</code>? <code>AllMatch(expr, form, levelspec)</code>
   * tests the parts at <code>levelspec</code>, the operator form <code>AllMatch(form)(expr)</code>
   * takes <code>expr</code> later. The values of an association are its elements.
   */
  /**
   * The operator form <code>head(arg)(list)</code> of <code>head(list, arg)</code>; NIL for any
   * other call.
   */
  @AllowNIL
  private static IExpr operatorFormWithList(IAST ast, IBuiltInSymbol head) {
    if (ast.head().isAST(head, 2) && ast.isAST1()) {
      return F.binaryAST2(head, ast.arg1(), ast.head().first());
    }
    return F.NIL;
  }

  /** <code>true</code> if the first of at least two arguments is a non-association AST. */
  private static boolean hasListArgument(IAST ast) {
    return !ast.isAST1() && ast.arg1().isAST() && !ast.arg1().isAssociation();
  }

  private static final class MatchElements extends AbstractFunctionEvaluator {
    private final IBuiltInSymbol head;
    private final boolean all;

    MatchElements(IBuiltInSymbol head, boolean all) {
      this.head = head;
      this.all = all;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head() != head) {
        return operatorFormWithList(ast, head);
      }
      if (ast.isAST1()) {
        return F.NIL;
      }
      IExpr expr = ast.arg1();
      IAST elements;
      if (ast.isAST3()) {
        IExpr level = engine.evaluate(F.binaryAST2(S.Level, expr, ast.arg3()));
        if (!level.isList()) {
          return F.NIL;
        }
        elements = (IAST) level;
      } else if (expr.isAssociation()) {
        elements = ((IAssociation) expr).values();
      } else if (expr.isAST()) {
        elements = (IAST) expr;
      } else {
        elements = F.CEmptyList;
      }
      final IPatternMatcher matcher = engine.evalPatternMatcher(ast.arg2());
      for (int i = 1; i < elements.size(); i++) {
        if (matcher.test(elements.get(i), engine) != all) {
          return F.booleSymbol(!all);
        }
      }
      return F.booleSymbol(all);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ast.head() == head ? ARGS_1_3 : null;
    }
  }

  /**
   * <code>DeleteElements(list, {e1, e2, ...})</code> - delete every element of <code>list</code>
   * which is one of the <code>ei</code> (by <code>SameQ</code>).
   * <code>DeleteElements(list, n -> {e1, ...})</code> deletes at most <code>n</code> occurrences of
   * each <code>ei</code>, the first ones; <code>{n1, n2, ...} -> {e1, e2, ...}</code> gives each
   * its own count. The operator form <code>DeleteElements(elems)(list)</code> takes the list
   * later.
   */
  private static final class DeleteElements extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head() != S.DeleteElements) {
        return operatorFormWithList(ast, S.DeleteElements);
      }
      if (!hasListArgument(ast)) {
        return F.NIL;
      }
      IAST list = (IAST) ast.arg1();
      IExpr spec = ast.arg2();
      IExpr counts = F.CInfinity;
      if (spec.isRuleAST()) {
        counts = spec.first();
        spec = spec.second();
      }
      if (!spec.isList()) {
        if (!ast.arg2().isRuleAST()) {
          // The argument `1` is not a list.
          Errors.printMessage(S.DeleteElements, "invl", F.list(spec), engine);
          return F.binaryAST2(S.DeleteElements, list, F.Rule(F.CInfinity, spec));
        }
        return F.NIL;
      }
      IAST elements = (IAST) spec;
      Map<IExpr, Integer> remaining = new HashMap<IExpr, Integer>();
      for (int i = 1; i < elements.size(); i++) {
        IExpr count = counts.isList() ? (i < counts.size() ? counts.get(i) : F.NIL) : counts;
        int n;
        if (count.isInfinity()) {
          n = Integer.MAX_VALUE;
        } else {
          n = count.toIntDefault();
          if (n < 0) {
            return F.NIL;
          }
        }
        remaining.put(elements.get(i), n);
      }
      IASTAppendable result = F.ast(list.head(), list.argSize());
      for (int i = 1; i < list.size(); i++) {
        IExpr element = list.get(i);
        Integer n = remaining.get(element);
        if (n != null && n > 0) {
          remaining.put(element, n - 1);
          continue;
        }
        result.append(element);
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ast.head() == S.DeleteElements ? ARGS_1_2 : null;
    }
  }

  /**
   * <code>UniqueElements({list1, list2, ...})</code> - for each <code>listi</code> the elements
   * which occur in no other list, without duplicates. <code>UniqueElements(lists, test)</code>
   * compares the elements with <code>test</code>.
   */
  private static final class UniqueElements extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList()) {
        return F.NIL;
      }
      IAST lists = (IAST) ast.arg1();
      for (int i = 1; i < lists.size(); i++) {
        if (!lists.get(i).isAST() || lists.get(i).isAssociation()) {
          return F.NIL;
        }
      }
      IASTAppendable result = F.ListAlloc(lists.argSize());
      if (ast.isAST1()) {
        Map<IExpr, Integer> owner = new HashMap<IExpr, Integer>();
        for (int i = 1; i < lists.size(); i++) {
          IAST list = (IAST) lists.get(i);
          for (int j = 1; j < list.size(); j++) {
            Integer previous = owner.get(list.get(j));
            if (previous == null) {
              owner.put(list.get(j), i);
            } else if (previous != i) {
              owner.put(list.get(j), -1);
            }
          }
        }
        for (int i = 1; i < lists.size(); i++) {
          IAST list = (IAST) lists.get(i);
          IASTAppendable unique = F.ast(list.head(), list.argSize());
          Set<IExpr> seen = new HashSet<IExpr>();
          for (int j = 1; j < list.size(); j++) {
            IExpr element = list.get(j);
            if (owner.get(element) == i && seen.add(element)) {
              unique.append(element);
            }
          }
          result.append(unique);
        }
        return result;
      }
      IExpr test = ast.arg2();
      for (int i = 1; i < lists.size(); i++) {
        IAST list = (IAST) lists.get(i);
        IASTAppendable unique = F.ast(list.head(), list.argSize());
        List<IExpr> kept = new ArrayList<IExpr>();
        for (int j = 1; j < list.size(); j++) {
          engine.checkTimeLimit();
          IExpr element = list.get(j);
          boolean shared = false;
          for (int k = 1; k < lists.size() && !shared; k++) {
            if (k == i) {
              continue;
            }
            IAST other = (IAST) lists.get(k);
            for (int m = 1; m < other.size() && !shared; m++) {
              shared = engine.evalTrue(F.binaryAST2(test, element, other.get(m)));
            }
          }
          if (shared) {
            continue;
          }
          boolean duplicate = false;
          for (IExpr previous : kept) {
            if (engine.evalTrue(F.binaryAST2(test, previous, element))) {
              duplicate = true;
              break;
            }
          }
          if (!duplicate) {
            kept.add(element);
            unique.append(element);
          }
        }
        result.append(unique);
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>OrderingBy(list, f)</code> - the positions which sort <code>list</code> by the values of
   * <code>f</code> on its elements, <code>OrderingBy(list, f, n)</code> the first <code>n</code>
   * of them, <code>OrderingBy(list, f, n, p)</code> sorting with the ordering function
   * <code>p</code>. The operator form <code>OrderingBy(f)(list)</code> takes the list later.
   */
  private static final class OrderingBy extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head() != S.OrderingBy) {
        return operatorFormWithList(ast, S.OrderingBy);
      }
      if (!hasListArgument(ast)) {
        return F.NIL;
      }
      IAST list = (IAST) ast.arg1();
      IExpr f = ast.arg2();
      IASTAppendable keys = F.ListAlloc(list.argSize());
      for (int i = 1; i < list.size(); i++) {
        keys.append(engine.evaluate(F.unaryAST1(f, list.get(i))));
      }
      IASTAppendable ordering = F.ast(S.Ordering, ast.argSize() - 1);
      ordering.append(keys);
      ordering.appendArgs(3, ast.size(), i -> ast.get(i));
      IExpr result = engine.evaluate(ordering);
      return result.isList() ? result : F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ast.head() == S.OrderingBy ? ARGS_1_4 : null;
    }
  }

  /**
   * <code>BlockMap(f, list, n)</code> - <code>f</code> applied to the blocks of <code>n</code>
   * elements of <code>list</code> (an incomplete last block is dropped);
   * <code>BlockMap(f, list, n, d)</code> uses blocks with offset <code>d</code>. Same as
   * <code>Map(f, Partition(list, n, d))</code>.
   */
  private static final class BlockMap extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IASTAppendable partition = F.ast(S.Partition, ast.argSize() - 1);
      partition.appendArgs(2, ast.size(), i -> ast.get(i));
      IExpr blocks = engine.evaluate(partition);
      if (!blocks.isAST() || blocks.isAST(S.Partition)) {
        return F.NIL;
      }
      return F.Map(ast.arg1(), blocks);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_4;
    }
  }

  /**
   * <code>Downsample(array, n)</code> - every <code>n</code>-th element of <code>array</code>,
   * starting with the first one; <code>Downsample(array, n, offset)</code> starts with the element
   * at <code>offset</code>. <code>n</code> and <code>offset</code> may be lists with one entry per
   * dimension.
   */
  private static final class Downsample extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList()) {
        return F.NIL;
      }
      // a single step or offset applies to every axis of the array
      final int depth = Math.max(1,
          org.matheclipse.core.eval.LinearAlgebraUtil.dimensions((IAST) ast.arg1()).size());
      IAST steps = ast.arg2().isList() ? (IAST) ast.arg2() : repeated(ast.arg2(), depth);
      IAST offsets;
      if (ast.isAST3()) {
        offsets = ast.arg3().isList() ? (IAST) ast.arg3() : repeated(ast.arg3(), steps.argSize());
      } else {
        offsets = repeated(F.C1, steps.argSize());
      }
      if (offsets.argSize() != steps.argSize()) {
        return F.NIL;
      }
      IASTAppendable part = F.ast(S.Part, steps.argSize() + 1);
      part.append(ast.arg1());
      for (int i = 1; i < steps.size(); i++) {
        int n = steps.get(i).toIntDefault();
        int offset = offsets.get(i).toIntDefault();
        if (n < 1 || offset < 1) {
          return F.NIL;
        }
        part.append(F.Span(F.ZZ(offset), S.All, F.ZZ(n)));
      }
      IExpr result = engine.evaluateNIL(part);
      return result.isList() ? result : F.NIL;
    }

    /** The list of <code>n</code> copies of <code>value</code>. */
    private static IAST repeated(IExpr value, int n) {
      IASTAppendable list = F.ListAlloc(n);
      for (int i = 0; i < n; i++) {
        list.append(value);
      }
      return list;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  /**
   * <code>MovingMap(f, list, w)</code> - <code>f</code> applied to the windows of <code>w+1</code>
   * consecutive elements of <code>list</code>. <code>MovingMap(f, list, w, padding)</code> gives
   * one result per element: the window ending at the element, padded on the left by
   * <code>"Fixed"</code> (repeating the first element), <code>"Periodic"</code>,
   * <code>"Reflected"</code>, a constant value, or <code>None</code> for shorter windows at the
   * start.
   */
  private static final class MovingMap extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg2().isList()) {
        return F.NIL;
      }
      IAST list = (IAST) ast.arg2();
      IExpr spec = ast.arg3();
      if (spec.isList() && spec.argSize() == 1) {
        spec = spec.first();
      }
      final int w = spec.toIntDefault();
      final int n = list.argSize();
      if (w < 0 || n == 0) {
        return F.NIL;
      }
      IExpr f = ast.arg1();
      if (ast.isAST3()) {
        IASTAppendable result = F.ListAlloc(Math.max(n - w, 0));
        for (int end = w + 1; end <= n; end++) {
          result.append(F.unaryAST1(f, list.subList(end - w, end + 1)));
        }
        return result;
      }
      IExpr padding = ast.arg4();
      String mode = padding.isString() || padding.isSymbol() ? padding.toString() : "";
      IASTAppendable result = F.ListAlloc(n);
      for (int end = 1; end <= n; end++) {
        engine.checkTimeLimit();
        IASTAppendable window = F.ListAlloc(w + 1);
        for (int k = end - w; k <= end; k++) {
          if (k >= 1) {
            window.append(list.get(k));
            continue;
          }
          switch (mode) {
            case "None":
              continue;
            case "Fixed":
              window.append(list.get(1));
              break;
            case "Periodic":
              window.append(list.get(Math.floorMod(k - 1, n) + 1));
              break;
            case "Reflected": {
              if (n == 1) {
                window.append(list.get(1));
                break;
              }
              int period = 2 * (n - 1);
              int r = Math.floorMod(k - 1, period);
              window.append(list.get((r < n ? r : period - r) + 1));
              break;
            }
            default:
              window.append(padding);
          }
        }
        result.append(F.unaryAST1(f, window));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_4;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ListOperatorFunctions() {}
}
