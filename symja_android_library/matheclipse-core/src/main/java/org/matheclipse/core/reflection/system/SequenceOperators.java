package org.matheclipse.core.reflection.system;

import java.util.Arrays;
import java.util.Comparator;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Folds with a state, operators which rearrange the arguments of a function and functions which
 * rearrange the elements of a list.
 */
public class SequenceOperators {

  private SequenceOperators() {}

  /**
   * <code>FoldPair(f, y0, {a1, a2, ...})</code> - <code>f(y, a)</code> returns a pair
   * <code>{output, newState}</code>; the result is the output of the last step.
   * <code>FoldPair(f, y0, list, g)</code> takes the output <code>g({output, newState})</code>
   * instead, <code>FoldPair(f, {y0, a1, ...})</code> starts with the first element.
   */
  public static final class FoldPair extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr f = ast.arg1();
      IExpr state;
      IAST list;
      if (ast.isAST2()) {
        if (!ast.arg2().isAST() || ast.arg2().argSize() < 1) {
          return F.NIL;
        }
        state = ast.arg2().first();
        list = ((IAST) ast.arg2()).rest();
      } else {
        if (!ast.arg3().isAST()) {
          return F.NIL;
        }
        state = ast.arg2();
        list = (IAST) ast.arg3();
      }
      IExpr output = F.NIL;
      for (int i = 1; i < list.size(); i++) {
        engine.checkTimeLimit();
        IExpr pair = engine.evaluate(F.binaryAST2(f, state, list.get(i)));
        if (!pair.isList() || pair.argSize() < 2) {
          return F.NIL;
        }
        output = ast.argSize() == 4 ? engine.evaluate(F.unaryAST1(ast.arg4(), pair)) : pair.first();
        state = pair.second();
      }
      if (output.isNIL()) {
        // no step: the result of a step with no element
        return F.NIL;
      }
      return output;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_4;
    }
  }

  /**
   * The operator forms <code>head(f, test)(list)</code> and <code>head(f, test)(x, list)</code> of
   * <code>FoldWhile</code> and <code>FoldWhileList</code>: <code>head(f, list, test)</code> and
   * <code>head(f, x, list, test)</code>.
   */
  @AllowNIL
  private static IExpr foldWhileOperatorForm(IAST ast, IBuiltInSymbol symbol) {
    IExpr head = ast.head();
    if (head.isAST(symbol, 3)) {
      if (ast.isAST1()) {
        return F.ternaryAST3(symbol, head.first(), ast.arg1(), head.second());
      }
      if (ast.isAST2()) {
        return F.quaternary(symbol, head.first(), ast.arg1(), ast.arg2(), head.second());
      }
    }
    return F.NIL;
  }

  /**
   * The start value, the list and the test of <code>head(f, {x, a1, ...}, test)</code> or
   * <code>head(f, x, {a1, ...}, test, ...)</code>.
   *
   * @return <code>{value, list, test}</code>, or <code>null</code> if there is no list to fold over
   */
  private static IExpr[] foldWhileArguments(IAST ast) {
    if (ast.isAST3()) {
      if (!ast.arg2().isAST() || ast.arg2().argSize() < 1) {
        return null;
      }
      return new IExpr[] {ast.arg2().first(), ((IAST) ast.arg2()).rest(), ast.arg3()};
    }
    if (!ast.arg3().isAST()) {
      return null;
    }
    return new IExpr[] {ast.arg2(), ast.arg3(), ast.arg4()};
  }

  /**
   * <code>FoldWhile(f, x, {a1, a2, ...}, test)</code> - fold <code>f</code> while
   * <code>test</code> gives <code>True</code> on the latest result; the result is the first value
   * on which the test fails or the last value. <code>FoldWhile(f, {x, a1, ...}, test)</code> starts
   * with the first element, the operator form <code>FoldWhile(f, test)(x, list)</code> or
   * <code>FoldWhile(f, test)(list)</code> takes the arguments later.
   */
  public static final class FoldWhile extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head() != S.FoldWhile) {
        return foldWhileOperatorForm(ast, S.FoldWhile);
      }
      if (ast.isAST2()) {
        return F.NIL;
      }
      IExpr f = ast.arg1();
      IExpr[] arguments = foldWhileArguments(ast);
      if (arguments == null) {
        return F.NIL;
      }
      IExpr value = arguments[0];
      IAST list = (IAST) arguments[1];
      IExpr test = arguments[2];
      if (!engine.evalTrue(F.unaryAST1(test, value))) {
        return value;
      }
      for (int i = 1; i < list.size(); i++) {
        engine.checkTimeLimit();
        value = engine.evaluate(F.binaryAST2(f, value, list.get(i)));
        if (!engine.evalTrue(F.unaryAST1(test, value))) {
          return value;
        }
      }
      return value;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ast.head() == S.FoldWhile ? ARGS_2_4 : null;
    }
  }

  /**
   * <code>FoldWhileList(f, x, {a1, a2, ...}, test)</code> - the list of the values of
   * <code>FoldWhile</code>: <code>x</code>, <code>f(x, a1)</code>, ... up to and including the
   * first value on which <code>test</code> doesn't give <code>True</code>.
   * <code>FoldWhileList(f, {x, a1, ...}, test)</code> starts with the first element,
   * <code>FoldWhileList(f, x, list, test, 1, n)</code> makes <code>n</code> more steps after the
   * test failed, or drops the last <code>-n</code> values for a negative <code>n</code>. The
   * operator form <code>FoldWhileList(f, test)(x, list)</code> or
   * <code>FoldWhileList(f, test)(list)</code> takes the arguments later.
   */
  public static final class FoldWhileList extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head() != S.FoldWhileList) {
        return foldWhileOperatorForm(ast, S.FoldWhileList);
      }
      final int argSize = ast.argSize();
      if (argSize == 2 || argSize == 5) {
        return F.NIL;
      }
      IExpr f = ast.arg1();
      IExpr[] arguments = foldWhileArguments(ast);
      if (arguments == null) {
        return F.NIL;
      }
      IExpr value = arguments[0];
      IAST list = (IAST) arguments[1];
      IExpr test = arguments[2];
      int extra = 0;
      if (argSize == 6) {
        if (!ast.arg5().isOne()) {
          // only the latest value is given to the test
          return F.NIL;
        }
        extra = ast.get(6).toIntDefault();
        if (F.isNotPresent(extra)) {
          return F.NIL;
        }
      }
      IASTAppendable result = F.ListAlloc(list.size() + 1);
      result.append(value);
      int i = 1;
      boolean failed = !engine.evalTrue(F.unaryAST1(test, value));
      while (!failed && i < list.size()) {
        engine.checkTimeLimit();
        value = engine.evaluate(F.binaryAST2(f, value, list.get(i++)));
        result.append(value);
        failed = !engine.evalTrue(F.unaryAST1(test, value));
      }
      if (extra > 0) {
        for (int k = 0; k < extra && i < list.size(); k++) {
          value = engine.evaluate(F.binaryAST2(f, value, list.get(i++)));
          result.append(value);
        }
      } else if (extra < 0) {
        int keep = Math.max(result.argSize() + extra, 0);
        return result.copyUntil(keep + 1);
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ast.head() == S.FoldWhileList ? new int[] {2, 6} : null;
    }
  }

  /**
   * <code>SequenceFoldList(f, {x1, ..., xn}, {a1, a2, ...})</code> - <code>{x1, ..., xn, y1, y2,
   * ...}</code> where each new value is <code>f</code> of the <code>n</code> values before it
   * followed by the next element: <code>y1 = f(x1, ..., xn, a1)</code>. <code>SequenceFold</code>
   * gives the last value.
   */
  public static final class SequenceFold extends AbstractFunctionEvaluator {
    private final boolean list;

    public SequenceFold(boolean list) {
      this.list = list;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg2().isList() || ast.arg2().argSize() < 1 || !ast.arg3().isList()) {
        return F.NIL;
      }
      IAST start = (IAST) ast.arg2();
      IAST elements = (IAST) ast.arg3();
      final int n = start.argSize();
      IASTAppendable values = F.ListAlloc(n + elements.argSize());
      values.appendArgs(start);
      for (int i = 1; i < elements.size(); i++) {
        engine.checkTimeLimit();
        IASTAppendable call = F.ast(ast.arg1(), n + 1);
        for (int j = values.argSize() - n + 1; j <= values.argSize(); j++) {
          call.append(values.get(j));
        }
        call.append(elements.get(i));
        values.append(engine.evaluate(call));
      }
      return list ? values : values.last();
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }
  }

  /**
   * <code>LexicographicOrder(list1, list2)</code> - <code>1</code> if <code>list1</code> comes
   * before <code>list2</code> when their elements are compared one after the other,
   * <code>-1</code> if it comes after, <code>0</code> if they are the same; a prefix comes first.
   * <code>LexicographicOrder(list1, list2, p)</code> compares the elements with the ordering
   * function <code>p</code>. Arguments which aren't lists compare as one element.
   */
  public static final class LexicographicOrder extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAST x = elements(ast.arg1());
      IAST y = elements(ast.arg2());
      final IExpr order = ast.isAST3() ? ast.arg3() : S.Order;
      int n = Math.min(x.argSize(), y.argSize());
      for (int i = 1; i <= n; i++) {
        int c = LexicographicSort.compare(order, x.get(i), y.get(i), engine);
        if (c != 0) {
          return c < 0 ? F.C1 : F.CN1;
        }
      }
      int c = Integer.compare(x.argSize(), y.argSize());
      return c == 0 ? F.C0 : (c < 0 ? F.C1 : F.CN1);
    }

    private static IAST elements(IExpr arg) {
      if (arg.isString()) {
        String s = arg.toString();
        IASTAppendable chars = F.ListAlloc(s.length());
        s.codePoints().forEach(cp -> chars.append(F.stringx(new String(Character.toChars(cp)))));
        return chars;
      }
      return arg.isList() ? (IAST) arg : F.list(arg);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  /**
   * <code>ReverseApplied(f)(x1, ..., xn)</code> gives <code>f(xn, ..., x1)</code>,
   * <code>ReverseApplied(f, n)</code> reverses only the first <code>n</code> arguments or for a
   * negative <code>n</code> the last <code>-n</code> arguments.
   */
  public static final class ReverseApplied extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr head = ast.head();
      if (head == S.ReverseApplied || !head.isAST(S.ReverseApplied) || head.argSize() > 2) {
        return F.NIL;
      }
      final int size = ast.argSize();
      int from = 0;
      int to = size;
      if (head.argSize() == 2) {
        int n = head.second().toIntDefault();
        if (F.isNotPresent(n)) {
          return F.NIL;
        }
        if (n >= 0) {
          to = Math.min(n, size);
        } else {
          from = size - Math.min(-n, size);
        }
      }
      IExpr[] arguments = new IExpr[size];
      for (int i = 0; i < size; i++) {
        arguments[i] = ast.get(i + 1);
      }
      for (int i = from, j = to - 1; i < j; i++, j--) {
        IExpr temp = arguments[i];
        arguments[i] = arguments[j];
        arguments[j] = temp;
      }
      return F.ast(arguments, head.first());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ast.head() == S.ReverseApplied ? ARGS_1_2 : null;
    }
  }

  /**
   * <code>LexicographicSort(list)</code> sorts the elements of <code>list</code> by comparing their
   * parts one after the other; a sequence which is a prefix of another comes first.
   * <code>LexicographicSort(list, p)</code> compares the parts with the ordering function
   * <code>p</code>, which gives <code>1</code>, <code>0</code> or <code>-1</code> like
   * {@link S#Order}.
   */
  public static final class LexicographicSort extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isAST()) {
        return F.NIL;
      }
      IAST list = (IAST) ast.arg1();
      final IExpr order = ast.isAST2() ? ast.arg2() : S.Order;
      for (int i = 1; i < list.size(); i++) {
        if (!list.get(i).isAST()) {
          return F.NIL;
        }
      }
      Comparator<IExpr> comparator = (a, b) -> {
        IAST x = (IAST) a;
        IAST y = (IAST) b;
        int n = Math.min(x.argSize(), y.argSize());
        for (int i = 1; i <= n; i++) {
          int c = compare(order, x.get(i), y.get(i), engine);
          if (c != 0) {
            return c;
          }
        }
        return Integer.compare(x.argSize(), y.argSize());
      };
      IExpr[] elements = new IExpr[list.argSize()];
      for (int i = 0; i < elements.length; i++) {
        elements[i] = list.get(i + 1);
      }
      Arrays.sort(elements, comparator);
      return F.ast(elements, list.head());
    }

    /** The order of <code>a</code> and <code>b</code> as a {@link Comparator} result. */
    static int compare(IExpr order, IExpr a, IExpr b, EvalEngine engine) {
      if (order == S.Order) {
        return a.compareTo(b);
      }
      IExpr result = engine.evaluate(F.binaryAST2(order, a, b));
      if (result.isOne() || result.isTrue()) {
        return -1;
      }
      if (result.isMinusOne() || result.isFalse()) {
        return 1;
      }
      return 0;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>DeleteAdjacentDuplicates(list)</code> deletes the elements which are equal to the element
   * before them, <code>DeleteAdjacentDuplicates(list, test)</code> the elements for which
   * <code>test(previous, element)</code> gives <code>True</code>.
   */
  public static final class DeleteAdjacentDuplicates extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isAST() || ast.arg1().isAssociation()) {
        return F.NIL;
      }
      IAST list = (IAST) ast.arg1();
      IASTAppendable result = F.ast(list.head(), list.argSize());
      for (int i = 1; i < list.size(); i++) {
        IExpr element = list.get(i);
        if (i > 1) {
          IExpr previous = list.get(i - 1);
          boolean same = ast.isAST2()
              ? engine.evalTrue(F.binaryAST2(ast.arg2(), previous, element))
              : previous.equals(element);
          if (same) {
            continue;
          }
        }
        result.append(element);
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>CenterArray(a, n, pad)</code> places the array <code>a</code> in the middle of an array
   * with the dimensions <code>n</code> filled with <code>pad</code> (default <code>0</code>); an
   * odd difference puts the extra element on the right. <code>CenterArray(n)</code> is
   * <code>CenterArray(1, n)</code> and <code>CenterArray(a, n, pad, SparseArray)</code> gives a
   * {@link S#SparseArray}.
   */
  public static final class CenterArray extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr a = ast.isAST1() ? F.C1 : ast.arg1();
      IExpr spec = ast.isAST1() ? ast.arg1() : ast.arg2();
      IExpr pad = ast.argSize() >= 3 ? ast.arg3() : F.C0;
      if (ast.argSize() == 4) {
        if (ast.arg4() != S.SparseArray) {
          return F.NIL;
        }
        IExpr dense = engine.evaluate(F.ternaryAST3(S.CenterArray, a, spec, pad));
        if (!dense.isList()) {
          return F.NIL;
        }
        return F.ternaryAST3(S.SparseArray, dense, S.Dimensions.of(engine, dense), pad);
      }
      int[] dims;
      if (spec.isList()) {
        dims = new int[spec.argSize()];
        for (int i = 0; i < dims.length; i++) {
          dims[i] = spec.getAt(i + 1).toIntDefault();
        }
      } else {
        dims = new int[] {spec.toIntDefault()};
      }
      long total = 1;
      for (int d : dims) {
        if (d < 0) {
          return F.NIL;
        }
        total *= Math.max(d, 1);
        if (total > 1_000_000L) {
          return F.NIL;
        }
      }
      // a block of lower rank sits in the middle of the higher dimensions
      int rank = 0;
      IExpr block = a;
      while (block.isList() && block.argSize() > 0) {
        rank++;
        block = block.first();
      }
      if (rank > dims.length) {
        return F.NIL;
      }
      while (rank < dims.length) {
        a = F.list(a);
        rank++;
      }
      return center(a, dims, 0, pad);
    }

    private static IExpr center(IExpr a, int[] dims, int level, IExpr pad) {
      if (level == dims.length) {
        return a;
      }
      IAST block = a.isList() ? (IAST) a : F.list(a);
      int n = dims[level];
      int k = block.argSize();
      int left = n >= k ? (n - k) / 2 : 0;
      int skip = n >= k ? 0 : (k - n) / 2;
      IASTAppendable result = F.ListAlloc(n);
      for (int i = 0; i < n; i++) {
        int j = i - left + skip;
        if (j >= 0 && j < k && (n >= k || i < n)) {
          result.append(center(block.get(j + 1), dims, level + 1, pad));
        } else {
          result.append(filled(dims, level + 1, pad));
        }
      }
      return result;
    }

    private static IExpr filled(int[] dims, int level, IExpr pad) {
      if (level == dims.length) {
        return pad;
      }
      IASTAppendable result = F.ListAlloc(dims[level]);
      for (int i = 0; i < dims[level]; i++) {
        result.append(filled(dims, level + 1, pad));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      super.setUp(newSymbol);
    }
  }
}
