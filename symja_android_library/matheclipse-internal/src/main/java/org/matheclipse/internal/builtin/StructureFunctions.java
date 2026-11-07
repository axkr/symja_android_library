package org.matheclipse.internal.builtin;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.internal.InternalFunction;

/** List and expression structure functions of the <code>Internal`</code> context. */
public class StructureFunctions {

  /**
   * <code>Internal`PartitionRagged(list, {n1, n2, ...})</code> - split <code>list</code> into
   * consecutive sublists of the lengths <code>n1, n2, ...</code>, which have to add up to the
   * length of <code>list</code>.
   */
  private static final class PartitionRagged extends InternalFunction {
    PartitionRagged() {
      super(2, 2);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      if (!ast.arg1().isAST() || !ast.arg2().isList()) {
        return F.NIL;
      }
      IAST list = (IAST) ast.arg1();
      IAST lengths = (IAST) ast.arg2();
      int[] sizes = new int[lengths.argSize()];
      long total = 0;
      for (int i = 0; i < sizes.length; i++) {
        sizes[i] = lengths.get(i + 1).toIntDefault();
        if (sizes[i] < 0) {
          return F.NIL;
        }
        total += sizes[i];
      }
      if (total != list.argSize()) {
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc(sizes.length);
      int start = 1;
      for (int size : sizes) {
        result.append(list.copyFrom(start, start + size));
        start += size;
      }
      return result;
    }
  }

  /**
   * <code>Internal`Deflatten(list, {d1, d2, ...})</code> - the array of dimensions
   * <code>{d1, d2, ...}</code> whose flattened elements are <code>list</code>.
   */
  private static final class Deflatten extends InternalFunction {
    Deflatten() {
      super(2, 2);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList() || !ast.arg2().isList()) {
        return F.NIL;
      }
      long count = 1;
      for (IExpr d : (IAST) ast.arg2()) {
        int dimension = d.toIntDefault();
        if (dimension < 0) {
          return F.NIL;
        }
        count *= dimension;
      }
      if (count != ast.arg1().argSize()) {
        return F.NIL;
      }
      return engine.evaluate(F.binaryAST2(S.ArrayReshape, ast.arg1(), ast.arg2()));
    }
  }

  /**
   * <code>Internal`JoinOrFail(list1, list2)</code> - <code>list2</code> appended to
   * <code>list1</code>, or <code>$Failed</code> if either isn't a list.
   */
  private static final class JoinOrFail extends InternalFunction {
    JoinOrFail() {
      super(2, 2);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      if (ast.arg1().isList() && ast.arg2().isList()) {
        IASTAppendable result = ((IAST) ast.arg1()).copyAppendable(ast.arg2().argSize());
        result.appendArgs((IAST) ast.arg2());
        return result;
      }
      return S.$Failed;
    }
  }

  /**
   * <code>Internal`ListMin({row1, row2, ...})</code> - the rows of real numbers which no other
   * row is less than or equal to in every component; of equal rows the first is kept.
   */
  private static final class ListMin extends InternalFunction {
    ListMin() {
      super(1, 1);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      int[] dimensions = ast.arg1().isMatrix(false);
      if (dimensions == null) {
        return F.NIL;
      }
      IAST matrix = (IAST) ast.arg1();
      double[][] rows = new double[matrix.argSize()][];
      for (int i = 0; i < rows.length; i++) {
        IAST row = (IAST) matrix.get(i + 1);
        if (!row.forAll(x -> x.isReal())) {
          return F.NIL;
        }
        rows[i] = row.toDoubleVector();
      }
      IASTAppendable result = F.ListAlloc(rows.length);
      for (int i = 0; i < rows.length; i++) {
        if (!isDominated(rows, i)) {
          result.append(matrix.get(i + 1));
        }
      }
      return result;
    }

    /** Is row <code>i</code> dominated by another row, or equal to an earlier one? */
    private static boolean isDominated(double[][] rows, int i) {
      for (int j = 0; j < rows.length; j++) {
        if (j == i) {
          continue;
        }
        boolean lessEqual = true;
        boolean equal = true;
        for (int k = 0; k < rows[i].length; k++) {
          if (rows[j][k] > rows[i][k]) {
            lessEqual = false;
            break;
          }
          if (rows[j][k] != rows[i][k]) {
            equal = false;
          }
        }
        if (lessEqual && (!equal || j < i)) {
          return true;
        }
      }
      return false;
    }
  }

  /** <code>Internal`DeleteTrailingZeros(list)</code> - <code>list</code> without its trailing zeros. */
  private static final class DeleteTrailingZeros extends InternalFunction {
    DeleteTrailingZeros() {
      super(1, 1);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList()) {
        return F.NIL;
      }
      IAST list = (IAST) ast.arg1();
      int end = list.size();
      while (end > 1 && list.get(end - 1).isZero()) {
        end--;
      }
      return list.copyFrom(1, end);
    }
  }

  /**
   * <code>Internal`GetIteratorLength({i, imin, imax, di})</code> - the number of iterations an
   * iterator of <code>Table</code> runs: <code>{n}</code>, <code>{i, imax}</code>,
   * <code>{i, imin, imax}</code>, <code>{i, imin, imax, di}</code> or
   * <code>{i, {e1, e2, ...}}</code>. The variable <code>i</code> is not evaluated.
   */
  private static final class GetIteratorLength extends InternalFunction {
    GetIteratorLength() {
      super(1, 1);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList() || ast.arg1().argSize() < 1 || ast.arg1().argSize() > 4) {
        return F.NIL;
      }
      IAST iterator = (IAST) ast.arg1();
      if (iterator.argSize() == 1) {
        return count(F.C1, engine.evaluate(iterator.arg1()), F.C1, engine);
      }
      if (iterator.argSize() == 2) {
        IExpr range = engine.evaluate(iterator.arg2());
        if (range.isList()) {
          return F.ZZ(range.argSize());
        }
        return count(F.C1, range, F.C1, engine);
      }
      IExpr min = engine.evaluate(iterator.arg2());
      IExpr max = engine.evaluate(iterator.arg3());
      IExpr step = iterator.argSize() == 4 ? engine.evaluate(iterator.arg4()) : F.C1;
      return count(min, max, step, engine);
    }

    private static IExpr count(IExpr min, IExpr max, IExpr step, EvalEngine engine) {
      if (!min.isReal() || !max.isReal() || !step.isReal() || step.isZero()) {
        return F.NIL;
      }
      IExpr n = engine.evaluate(F.Plus(F.Floor(F.Divide(F.Subtract(max, min), step)), F.C1));
      if (!n.isInteger()) {
        return F.NIL;
      }
      return n.isNegative() ? F.C0 : n;
    }
  }

  /**
   * <code>Internal`ToEquivalenceClass(expr, tol)</code> - <code>expr</code> with every inexact
   * real number <code>x</code> replaced by the representative <code>tol*Round(x/tol)</code> of
   * its class.
   */
  private static final class ToEquivalenceClass extends InternalFunction {
    ToEquivalenceClass() {
      super(2, 2);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      IExpr tolerance = ast.arg2();
      if (!tolerance.isReal() || !tolerance.isPositive()) {
        return F.NIL;
      }
      return ast.arg1().replaceAll(x -> x.isReal() && x.isInexactNumber()
          ? engine.evaluate(F.N(F.Times(tolerance, F.Round(F.Divide(x, tolerance)))))
          : F.NIL).orElse(ast.arg1());
    }
  }

  /**
   * <code>Internal`MakePolynomial(expr)</code> - <code>{poly, {v1 -&gt; k1, v2 -&gt; k2, ...}}
   * </code>: <code>expr</code> as a polynomial <code>poly</code> in new variables
   * <code>v1, v2, ...</code> which stand for its non-polynomial subexpressions
   * <code>k1, k2, ...</code>.
   */
  private static final class MakePolynomial extends InternalFunction {
    MakePolynomial() {
      super(1, 1);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      Map<IExpr, IExpr> kernels = new java.util.LinkedHashMap<>();
      IExpr polynomial = replaceKernels(ast.arg1(), kernels);
      IASTAppendable rules = F.ListAlloc(kernels.size());
      kernels.forEach((kernel, variable) -> rules.append(F.Rule(variable, kernel)));
      return F.list(polynomial, rules);
    }

    private static IExpr replaceKernels(IExpr expr, Map<IExpr, IExpr> kernels) {
      if (expr.isNumber() || expr.isSymbol()) {
        return expr;
      }
      if (expr.isPlus() || expr.isTimes()) {
        return ((IAST) expr).map(x -> replaceKernels(x, kernels));
      }
      if (expr.isPower() && expr.exponent().isInteger() && !expr.exponent().isNegative()) {
        return F.Power(replaceKernels(expr.base(), kernels), expr.exponent());
      }
      return kernels.computeIfAbsent(expr, k -> F.Dummy("x" + EvalEngine.uniqueName("$")));
    }
  }

  /**
   * <code>Internal`DirectoryByteCount(dir)</code> - the total size in bytes of the files in
   * <code>dir</code> and its subdirectories.
   */
  private static final class DirectoryByteCount extends InternalFunction {
    DirectoryByteCount() {
      super(1, 1);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      if (!ast.arg1().isString()) {
        return F.NIL;
      }
      if (!Config.isFileSystemEnabled(engine)) {
        return F.NIL;
      }
      Path directory = Paths.get(ast.arg1().toString());
      if (!Files.isDirectory(directory)) {
        // Directory `1` not found.
        return Errors.printMessage(S.General, "nodir", F.list(ast.arg1()), engine);
      }
      long[] bytes = new long[1];
      try {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
            if (attributes.isRegularFile()) {
              bytes[0] += attributes.size();
            }
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult visitFileFailed(Path file, IOException e) {
            // a file or directory which may not be read doesn't count
            return FileVisitResult.CONTINUE;
          }
        });
        return F.ZZ(bytes[0]);
      } catch (IOException | RuntimeException e) {
        return Errors.printMessage(S.General, e, engine);
      }
    }
  }

  static void init() {
    InternalFunctions.define("PartitionRagged", ISymbol.NOATTRIBUTE, new PartitionRagged());
    InternalFunctions.define("Deflatten", ISymbol.NOATTRIBUTE, new Deflatten());
    InternalFunctions.define("JoinOrFail", ISymbol.NOATTRIBUTE, new JoinOrFail());
    InternalFunctions.define("ListMin", ISymbol.NOATTRIBUTE, new ListMin());
    InternalFunctions.define("DeleteTrailingZeros", ISymbol.NOATTRIBUTE,
        new DeleteTrailingZeros());
    InternalFunctions.define("GetIteratorLength", ISymbol.HOLDALL, new GetIteratorLength());
    InternalFunctions.define("ToEquivalenceClass", ISymbol.NOATTRIBUTE,
        new ToEquivalenceClass());
    InternalFunctions.define("MakePolynomial", ISymbol.NOATTRIBUTE, new MakePolynomial());
    InternalFunctions.define("DirectoryByteCount", ISymbol.NOATTRIBUTE,
        new DirectoryByteCount());
  }

  private StructureFunctions() {}
}
