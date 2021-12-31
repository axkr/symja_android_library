package org.matheclipse.core.builtin;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.FieldVector;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractCorePredicateEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IExpr.COMPARE_TERNARY;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPredicate;
import org.matheclipse.core.interfaces.ISparseArray;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.VisitorBooleanLevelSpecification;

public class PredicateQ {
  private static final Logger LOGGER = LogManager.getLogger();

  /** Constructor for the unary predicate */
  // public final static AtomQ ATOMQ = new AtomQ();

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.AntisymmetricMatrixQ.setEvaluator(new AntisymmetricMatrixQ());
      S.AntihermitianMatrixQ.setEvaluator(new AntihermitianMatrixQ());
      S.ArrayQ.setEvaluator(new ArrayQ());
      S.AssociationQ.setPredicateQ(x -> x.isAssociation());
      S.AtomQ.setPredicateQ(x -> x.isAtom());
      S.BooleanQ.setPredicateQ(x -> x.isTrue() || x.isFalse());
      S.ByteArrayQ.setPredicateQ(WXFFunctions::isByteArray);
      S.DigitQ.setEvaluator(new DigitQ());
      S.EvenQ.setEvaluator(new EvenQ());
      S.ExactNumberQ.setPredicateQ(x -> x.isExactNumber());
      S.FreeQ.setEvaluator(new FreeQ());
      S.HermitianMatrixQ.setEvaluator(new HermitianMatrixQ());
      S.InexactNumberQ.setPredicateQ(x -> x.isInexactNumber());
      S.IntegerQ.setPredicateQ(x -> x.isInteger());
      S.ListQ.setPredicateQ(x -> x.isList());
      S.MachineNumberQ.setPredicateQ(x -> x.isMachineNumber());
      S.MatchQ.setEvaluator(new MatchQ());
      S.MatrixQ.setEvaluator(new MatrixQ());
      S.MemberQ.setEvaluator(new MemberQ());
      S.MissingQ.setPredicateQ(x -> x.isAST(S.Missing, 2));
      S.NotListQ.setPredicateQ(x -> !x.isList());
      S.NumberQ.setPredicateQ(x -> x.isNumber());
      S.NumericQ.setPredicateQ(x -> x.isNumericFunction());
      S.OddQ.setEvaluator(new OddQ());
      S.OrthogonalMatrixQ.setEvaluator(new OrthogonalMatrixQ());
      S.PossibleZeroQ.setEvaluator(new PossibleZeroQ());
      S.PrimeQ.setEvaluator(new PrimeQ());
      S.QuantityQ.setEvaluator(new QuantityQ());
      S.RealNumberQ.setEvaluator(new RealNumberQ());
      S.SquareMatrixQ.setEvaluator(new SquareMatrixQ());
      S.StringQ.setPredicateQ(x -> x.isString());
      S.SymbolQ.setPredicateQ(x -> x.isSymbol());
      S.SymmetricMatrixQ.setEvaluator(new SymmetricMatrixQ());
      S.SyntaxQ.setEvaluator(new SyntaxQ());
      S.ValueQ.setEvaluator(new ValueQ());
      S.VectorQ.setEvaluator(new VectorQ());
    }
  }

  /**
   *
   *
   * <pre>
   * AntihermitianMatrixQ(m)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>m</code> is a anti hermitian matrix.
   *
   * </blockquote>
   *
   * <p>See:<br>
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Skew-Hermitian_matrix">Wikipedia - Skew-Hermitian
   *       matrix</a>
   * </ul>
   */
  private static final class AntihermitianMatrixQ extends SymmetricMatrixQ {

    @Override
    protected boolean compareElements(IExpr expr1, IExpr expr2, EvalEngine engine) {
      if (expr1.isNumber() && expr2.isNumber()) {
        if (expr1.conjugate().negate().equals(expr2)) {
          return true;
        }
        return false;
      }
      return S.Equal.ofQ(engine, F.Times(F.CN1, F.Conjugate(expr1)), expr2);
    }
  }

  /**
   *
   *
   * <pre>
   * AntisymmetricMatrixQ(m)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>m</code> is a anti symmetric matrix.
   *
   * </blockquote>
   *
   * <p>See:<br>
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Skew-symmetric_matrix">Wikipedia - Skew-symmetric
   *       matrix</a>
   * </ul>
   */
  private static final class AntisymmetricMatrixQ extends SymmetricMatrixQ {

    @Override
    protected boolean compareElements(IExpr expr1, IExpr expr2, EvalEngine engine) {
      if (expr1.isNumber() && expr2.isNumber()) {
        if (expr1.negate().equals(expr2)) {
          return true;
        }
        return false;
      }
      return S.Equal.ofQ(engine, F.Times(F.CN1, expr1), expr2);
    }
  }

  /**
   *
   *
   * <pre>
   * 'ArrayQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>tests whether expr is a full array.
   *
   * </blockquote>
   *
   * <pre>
   * 'ArrayQ(expr, pattern)
   * </pre>
   *
   * <blockquote>
   *
   * <p>also tests whether the array depth of expr matches pattern.
   *
   * </blockquote>
   *
   * <pre>
   * 'ArrayQ(expr, pattern, test)
   * </pre>
   *
   * <blockquote>
   *
   * <p>furthermore tests whether <code>test</code> yields <code>True</code> for all elements of
   * expr.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ArrayQ(a)
   * False
   * &gt;&gt; ArrayQ({a})
   * True
   * &gt;&gt; ArrayQ({{{a}},{{b,c}}})
   * False
   * &gt;&gt; ArrayQ({{a, b}, {c, d}}, 2, SymbolQ)
   * True
   * </pre>
   */
  private static final class ArrayQ extends AbstractCoreFunctionEvaluator implements IPredicate {

    /**
     * Determine the depth of the given expression <code>expr</code> which should be a full array of
     * (possibly nested) lists. Return <code>-1</code> if the expression isn't a full array.
     *
     * @param expr
     * @param depth start depth of the full array
     * @param predicate an optional <code>Predicate</code> which would be applied to all elements
     *     which aren't lists.
     * @return <code>-1</code> if the expression isn't a full array.
     */
    private static int determineDepth(final IExpr expr, int depth, Predicate<IExpr> predicate) {
      int resultDepth = depth;
      if (expr.isSparseArray()) {
        int[] dims = ((ISparseArray) expr).getDimension();
        if (dims == null) {
          return -1;
        }
        return depth + dims.length;
      } else if (expr.isList()) {
        IAST ast = (IAST) expr;
        int size = ast.size();
        if (size == 1) {
          return depth;
        }
        IExpr arg1AST = ast.arg1();
        boolean isList = arg1AST.isList();
        int arg1Size = 0;
        if (isList) {
          arg1Size = ((IAST) ast.arg1()).size();
        }
        resultDepth = determineDepth(arg1AST, depth + 1, predicate);
        if (resultDepth < 0) {
          return -1;
        }
        int tempDepth;
        for (int i = 2; i < size; i++) {
          if (isList) {
            if (!ast.get(i).isList()) {
              return -1;
            }
            if (arg1Size != ((IAST) ast.get(i)).size()) {
              return -1;
            }
            tempDepth = determineDepth(ast.get(i), depth + 1, predicate);
            if (tempDepth < 0 || tempDepth != resultDepth) {
              return -1;
            }
          } else {
            if (ast.get(i).isList()) {
              return -1;
            }
            if (predicate != null) {
              if (!predicate.test(ast.get(i))) {
                return -1;
              }
            }
          }
        }
        return resultDepth;
      }
      if (predicate != null) {
        if (!predicate.test(expr)) {
          return -1;
        }
      }
      return resultDepth;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr arg1 = engine.evaluate(ast.arg1());
      Predicate<IExpr> test = null;
      if ((ast.size() >= 4)) {
        final IExpr testArg3 = engine.evaluate(ast.arg3());
        test = x -> engine.evalTrue(testArg3, x);
      }
      int depth = determineDepth(arg1, 0, test);
      if (depth >= 0) {
        if ((ast.size() >= 3)) {
          // Match the depth with the second argument
          final IPatternMatcher matcher = engine.evalPatternMatcher(ast.arg2());
          if (!matcher.test(F.ZZ(depth), engine)) {
            return S.False;
          }
        }
        return S.True;
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /**
   *
   *
   * <pre>
   * DigitQ(str)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>str</code> is a string which contains only digits.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; DigitQ("1234")
   * True
   * </pre>
   */
  private static final class DigitQ extends AbstractCorePredicateEvaluator
      implements Predicate<IExpr>, IPredicate {

    @Override
    public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
      if (arg1 instanceof IStringX) {
        return test(arg1);
      }
      return false;
    }

    @Override
    public boolean test(final IExpr obj) {
      if (obj instanceof IStringX) {
        final String str = obj.toString();
        char ch;
        for (int i = 0; i < str.length(); i++) {
          ch = str.charAt(i);
          if (!((ch >= '0') && (ch <= '9'))) {
            return false;
          }
        }
        return true;
      }
      return false;
    }
  }

  /**
   *
   *
   * <pre>
   * EvenQ(x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>x</code> is even, and <code>False</code> otherwise.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; EvenQ(4)
   * True
   * &gt;&gt; EvenQ(-3)
   * False
   * &gt;&gt; EvenQ(n)
   * False
   * </pre>
   */
  private static final class EvenQ extends AbstractCorePredicateEvaluator
      implements Predicate<IExpr>, IPredicate {

    @Override
    public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
      return arg1.isEvenResult();
    }

    @Override
    public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine, OptionArgs options) {
      IExpr option = options.getOption(S.GaussianIntegers);
      if (!option.isTrue()) {
        return evalArg1Boole(arg1, engine);
      }
      IInteger[] reImParts = arg1.gaussianIntegers();
      if (reImParts == null) {
        return false;
      }
      if (reImParts[1].isZero()) {
        return reImParts[0].isEven();
      }
      if (reImParts[0].isZero()) {
        return reImParts[1].isEven();
      }
      return reImParts[0].isEven() && reImParts[1].isEven();
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }

    @Override
    public boolean test(final IExpr expr) {
      return (expr.isInteger()) && ((IInteger) expr).isEven();
    }
  }

  /**
   *
   *
   * <pre>
   * FreeQ(`expr`, `x`)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns 'True' if <code>expr</code> does not contain the expression <code>x</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; FreeQ(y, x)
   * True
   * &gt;&gt; FreeQ(a+b+c, a+b)
   * False
   * &gt;&gt; FreeQ({1, 2, a^(a+b)}, Plus)
   * False
   * &gt;&gt; FreeQ(a+b, x_+y_+z_)
   * True
   * &gt;&gt; FreeQ(a+b+c, x_+y_+z_)
   * False
   * &gt;&gt; FreeQ(x_+y_+z_)(a+b)
   * True
   * </pre>
   */
  private static final class FreeQ extends AbstractCoreFunctionEvaluator implements IPredicate {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.size() == 3) {
        final IExpr arg1 = engine.evaluate(ast.arg1());
        final IExpr arg2 = engine.evalPattern(ast.arg2());
        return F.bool(arg1.isFree(arg2, true));
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
   * HermitianMatrixQ(m)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>m</code> is a hermitian matrix.
   *
   * </blockquote>
   *
   * <p>See:<br>
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Hermitian_matrix">Wikipedia - Hermitian matrix</a>
   * </ul>
   */
  private static final class HermitianMatrixQ extends SymmetricMatrixQ {
    @Override
    protected boolean compareElements(IExpr expr1, IExpr expr2, EvalEngine engine) {
      if (expr1.isReal() && expr2.isReal()) {
        if (expr1.equals(expr2)) {
          return true;
        }
        return false;
      }
      if (expr1.isNumber() && expr2.isNumber()) {
        if (expr1.conjugate().equals(expr2)) {
          return true;
        }
        return false;
      }
      return S.Equal.ofQ(engine, F.Conjugate(expr1), expr2);
    }
  }

  /**
   *
   *
   * <pre>
   * MatchQ(expr, form)
   * </pre>
   *
   * <blockquote>
   *
   * <p>tests whether <code>expr</code> matches <code>form</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; MatchQ(123, _Integer)
   * True
   *
   * &gt;&gt; MatchQ(123, _Real)
   * False
   *
   * &gt;&gt; MatchQ(_Integer)[123]
   * True
   * </pre>
   */
  private static final class MatchQ extends AbstractCoreFunctionEvaluator implements IPredicate {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      //      if (ast.isAST1()) {
      //        ast = F.operatorForm1Append(ast);
      //        if (!ast.isPresent()) {
      //          return F.NIL;
      //        }
      //      }
      if ((ast.isAST2())) {
        IExpr arg1 = ast.arg1();
        IPatternMatcher matcher = engine.evalPatternMatcher(ast.arg2());
        IExpr arg1Evaled = engine.evaluate(arg1);
        if (matcher.test(arg1Evaled, engine)) {
          return S.True;
        }
        if (arg1Evaled.isAST()) {
          return F.bool(matcher.test(arg1, engine));
        }
        // if (!arg2.isCondition()) {
        // try {
        // arg2 = engine.evaluate(arg2);
        // } catch (RuntimeException rte) {
        //
        // }
        // }
        // return F.bool(engine.evalPatternMatcher(arg2).test(arg1, engine));

      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2_1;
    }
  }

  /**
   *
   *
   * <pre>
   * MatrixQ(m)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>m</code> is a list of equal-length lists.
   *
   * </blockquote>
   *
   * <pre>
   * MatrixQ[m, f]
   * </pre>
   *
   * <blockquote>
   *
   * <p>only returns <code>True</code> if <code>f(x)</code> returns <code>True</code> for each
   * element <code>x</code> of the matrix <code>m</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; MatrixQ({{1, 3}, {4.0, 3/2}}, NumberQ)
   * True
   * </pre>
   */
  private static final class MatrixQ extends AbstractCoreFunctionEvaluator implements IPredicate {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr arg1 = engine.evaluate(ast.arg1());
      int[] dims = arg1.isMatrix();
      if (dims == null) {
        return S.False;
      }

      if (ast.isAST1()) {
        return S.True;
      }
      if (ast.isAST2()) {
        final IExpr arg2 = engine.evaluate(ast.arg2());
        if (arg2.isSparseArray()) {
          // TODO SparseArray
          return F.NIL;
        }

        IASTAppendable temp = F.ast(arg2);
        temp.append(F.Slot1);
        if (arg1.isAST()) {
          IAST matrix = (IAST) arg1;
          for (int i = 1; i < dims[0]; i++) {
            if (!((IAST) matrix.get(i))
                .forAll(
                    x -> {
                      temp.set(1, x);
                      return engine.evalTrue(temp);
                    })) {
              return S.False;
            }
          }
          return S.True;
        } else {
          FieldMatrix<IExpr> matrix = Convert.list2Matrix(arg1);
          if (matrix != null) {
            for (int i = 0; i < dims[0]; i++) {
              for (int j = 1; j < dims[1]; j++) {
                IExpr expr = matrix.getEntry(i, j);
                temp.set(1, expr);
                if (!engine.evalTrue(temp)) {
                  return S.False;
                }
              }
            }
            return S.True;
          }
        }
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * MemberQ(list, pattern)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if pattern matches any element of <code>list</code>, or <code>
   * False</code> otherwise.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; MemberQ({a, b, c}, b)
   * True
   * &gt;&gt; MemberQ({a, b, c}, d)
   * False
   * &gt;&gt; MemberQ({"a", b, f(x)}, _?NumericQ)
   * False
   * &gt;&gt; MemberQ(_List)({{}})
   * True
   * </pre>
   */
  private static final class MemberQ extends AbstractCoreFunctionEvaluator implements IPredicate {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      boolean heads = false;
      int size = ast.size();
      if (ast.size() > 3) {
        final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, size, engine);
        if (options.isTrue(S.Heads)) {
          heads = true;
        }
        int pos = options.getLastPosition();
        if (pos != -1) {
          size = pos;
        }
      }

      if (size >= 3) {
        final IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isAST()) {
          final IExpr arg2 = engine.evaluate(ast.arg2());
          if (size == 3) {
            return F.bool(arg1.isMember(arg2, heads, null));
          }

          Predicate<IExpr> predicate = Predicates.toMemberQ(arg2);
          IVisitorBoolean level =
              new VisitorBooleanLevelSpecification(predicate, ast.arg3(), heads, engine);

          return F.bool(arg1.accept(level));
        }

        return S.False;
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY_1;
    }
  }

  /**
   *
   *
   * <pre>
   * OddQ(x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>x</code> is odd, and <code>False</code> otherwise.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; OddQ(-3)
   * True
   *
   * &gt;&gt; OddQ(0)
   * False
   * </pre>
   */
  private static final class OddQ extends AbstractCorePredicateEvaluator
      implements Predicate<IExpr>, IPredicate {

    @Override
    public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
      return arg1.isInteger() && ((IInteger) arg1).isOdd();
    }

    @Override
    public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine, OptionArgs options) {
      IExpr option = options.getOption(S.GaussianIntegers);
      if (!option.isTrue()) {
        return evalArg1Boole(arg1, engine);
      }
      IInteger[] reImParts = arg1.gaussianIntegers();
      if (reImParts == null) {
        return false;
      }
      if (reImParts[1].isZero()) {
        return reImParts[0].isOdd();
      }
      if (reImParts[0].isZero()) {
        return reImParts[1].isOdd();
      }
      if (reImParts[0].isOdd() && reImParts[1].isOdd()) {
        return false;
      }
      return reImParts[0].isOdd() || reImParts[1].isOdd();
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }

    @Override
    public boolean test(final IExpr expr) {
      return expr.isInteger() && ((IInteger) expr).isOdd();
    }
  }

  private static final class QuantityQ extends AbstractCorePredicateEvaluator
      implements Predicate<IExpr>, IPredicate {

    @Override
    public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
      return arg1.isQuantity();
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public boolean test(final IExpr expr) {
      return expr.isQuantity();
    }
  }

  private static final class OrthogonalMatrixQ extends AbstractCoreFunctionEvaluator
      implements IPredicate {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      final IExpr arg1 = engine.evaluate(ast.arg1());
      int[] dims = arg1.isMatrix();
      if (dims == null) {
        // no square matrix
        return S.False;
      }
      IExpr identityMatrix = F.NIL;
      int[] identityMatrixDims = null;
      if (dims[0] >= dims[1]) {
        identityMatrix = S.Dot.of(engine, F.Transpose(arg1), arg1);
        identityMatrixDims = identityMatrix.isMatrix();
        if (identityMatrixDims == null
            || identityMatrixDims[0] != dims[1]
            || identityMatrixDims[1] != dims[1]) {
          return S.False;
        }
      } else {
        identityMatrix = S.Dot.of(engine, arg1, F.Transpose(arg1));
        identityMatrixDims = identityMatrix.isMatrix();
        if (identityMatrixDims == null
            || identityMatrixDims[0] != dims[0]
            || identityMatrixDims[1] != dims[0]) {
          return S.False;
        }
      }
      if (identityMatrix.isAST()) {
        IAST matrix = (IAST) identityMatrix;
        for (int i = 1; i <= identityMatrixDims[0]; i++) {
          IAST row = (IAST) matrix.get(i);
          for (int j = 1; j <= identityMatrixDims[1]; j++) {
            final IExpr expr = row.get(j);
            if (i == j) {
              if (!S.PossibleZeroQ.ofQ(engine, F.Plus(F.CN1, expr))) {
                return S.False;
              }
            } else {
              if (!S.PossibleZeroQ.ofQ(engine, expr)) {
                return S.False;
              }
            }
          }
        }
      } else {
        FieldMatrix<IExpr> matrix = Convert.list2Matrix(identityMatrix);
        if (matrix != null) {
          for (int i = 0; i < dims[0]; i++) {
            for (int j = 1; j < dims[1]; j++) {
              final IExpr expr = matrix.getEntry(i, j);
              if (i == j) {
                if (!S.PossibleZeroQ.ofQ(engine, F.Plus(F.CN1, expr))) {
                  return S.False;
                }
              } else {
                if (!S.PossibleZeroQ.ofQ(engine, expr)) {
                  return S.False;
                }
              }
            }
          }
          return S.True;
        }
      }
      return S.True;
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
   * PossibleZeroQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>maps a (possible) zero <code>expr</code> to <code>True</code> and returns <code>False</code>
   * otherwise.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; PossibleZeroQ((E + Pi)^2 - E^2 - Pi^2 - 2*E*Pi)
   * True
   *
   * &gt;&gt; PossibleZeroQ(Sqrt(x^2) - x)
   * False
   * </pre>
   */
  private static final class PossibleZeroQ extends AbstractCorePredicateEvaluator
      implements IPredicate {

    @Override
    public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
      IExpr expr = arg1;
      if (expr.isNumber()) {
        return expr.isZero();
      }
      if (expr.isAST()) {
        return isPossibleZeroQ((IAST) expr, false, engine);
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * PrimeQ(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>n</code> is a integer prime number.<br>
   *
   * </blockquote>
   *
   * <pre>
   * PrimeQ(n, GaussianIntegers -&gt; True)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>n</code> is a Gaussian prime number.<br>
   *
   * </blockquote>
   *
   * <p>For very large numbers, <code>PrimeQ</code> uses <a
   * href="https://en.wikipedia.org/wiki/Prime_number#Primality_testing_versus_primality_proving">probabilistic
   * prime testing</a>, so it might be wrong sometimes<br>
   * (a number might be composite even though <code>PrimeQ</code> says it is prime).
   *
   * <p>See:
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Prime_number">Wikipedia - Prime number</a>
   *   <li><a href="https://en.wikipedia.org/wiki/Gaussian_integer#Gaussian_primes">Wikipedia -
   *       Gaussian primes</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; PrimeQ(2)
   * True
   * &gt;&gt; PrimeQ(-3)
   * True
   * &gt;&gt; PrimeQ(137)
   * True
   * &gt;&gt; PrimeQ(2 ^ 127 - 1)
   * True
   * &gt;&gt; PrimeQ(1)
   * False
   * &gt;&gt; PrimeQ(2 ^ 255 - 1)
   * False
   * </pre>
   *
   * <p>All prime numbers between 1 and 100:
   *
   * <pre>
   * &gt;&gt; Select(Range(100), PrimeQ)
   *  = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97}
   * </pre>
   *
   * <p>'PrimeQ' has attribute 'Listable':
   *
   * <pre>
   * &gt;&gt; PrimeQ(Range(20))
   *  = {False, True, True, False, True, False, True, False, False, False, True, False, True, False, False, False, True, False, True, False}
   * </pre>
   *
   * <p>The Gaussian integer <code>2 == (1 + i)*(1 − i)</code> isn't a Gaussian prime number:
   *
   * <pre>
   * &gt;&gt; PrimeQ(2, GaussianIntegers-&gt;True)
   * False
   *
   * &gt;&gt; PrimeQ(5+2*I, GaussianIntegers-&gt;True)
   * True
   * </pre>
   */
  private static final class PrimeQ extends AbstractCorePredicateEvaluator
      implements Predicate<IInteger>, IPredicate {

    @Override
    public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
      if (!arg1.isInteger()) {
        return false;
      }
      return ((IInteger) arg1).isProbablePrime();
    }

    /**
     * Eval <a href="https://en.wikipedia.org/wiki/Gaussian_integer#Gaussian_primes">Gaussian
     * primes</a> if option <code>GaussianIntegers->True</code> is set.
     */
    @Override
    public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine, OptionArgs options) {
      IExpr option = options.getOption(S.GaussianIntegers);
      if (!option.isTrue()) {
        return evalArg1Boole(arg1, engine);
      }
      IInteger[] reImParts = arg1.gaussianIntegers();
      if (reImParts == null) {
        return false;
      }
      if (reImParts[1].isZero()) {
        if (reImParts[0].isProbablePrime()) {
          return reImParts[0].abs().mod(F.C4).equals(F.C3);
        }
        return false;
      }
      if (reImParts[0].isZero()) {
        if (reImParts[1].isProbablePrime()) {
          return reImParts[1].abs().mod(F.C4).equals(F.C3);
        }
        return false;
      }
      // re^2 + im^2 is probable prime?
      return reImParts[0].powerRational(2L).add(reImParts[1].powerRational(2L)).isProbablePrime();
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }

    @Override
    public boolean test(final IInteger obj) {
      return obj.isProbablePrime();
    }
  }

  /**
   *
   *
   * <pre>
   * RealNumberQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>expr</code> is an explicit number with no imaginary
   * component.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; RealNumberQ[10]
   *  = True
   *
   * &gt;&gt; RealNumberQ[4.0]
   *  = True
   *
   * &gt;&gt; RealNumberQ[1+I]
   *  = False
   *
   * &gt;&gt; RealNumberQ[0 * I]
   *  = True
   *
   * &gt;&gt; RealNumberQ[0.0 * I]
   *  = False
   * </pre>
   */
  private static final class RealNumberQ extends AbstractCoreFunctionEvaluator
      implements IPredicate {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr arg1 = engine.evaluate(ast.arg1());
      if (arg1.isNumber()) {
        if (arg1.isComplex() || arg1.isComplexNumeric()) {
          return S.False;
        }
        return F.bool(arg1.isReal());
      }

      // CAUTION: the following can not be used because Rubi uses another definition
      // IExpr temp = engine.evaluate(arg1);
      // if (temp.isReal()) {
      // return S.True;
      // }
      // if (temp.isNumericFunction()) {
      // temp = engine.evalN(arg1);
      // if (temp.isReal()) {
      // return S.True;
      // }
      // }
      return S.False;
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
   * SquareMatrixQ(m)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>m</code> is a square matrix.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; SquareMatrixQ({{1, 3 + 4*I}, {3 - 4*I, 2}})
   * True
   *
   * &gt;&gt; SquareMatrixQ({{}})
   * False
   * </pre>
   */
  private static final class SquareMatrixQ extends AbstractCoreFunctionEvaluator
      implements IPredicate {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      final IExpr arg1 = engine.evaluate(ast.arg1());
      int[] dims = arg1.isMatrix();
      if (dims == null || dims[0] != dims[1]) {
        // no square matrix
        return S.False;
      }

      return S.True;
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
   * SymmetricMatrixQ(m)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>m</code> is a symmetric matrix.
   *
   * </blockquote>
   *
   * <p>See:<br>
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Symmetric_matrix">Wikipedia - Symmetric matrix</a>
   * </ul>
   */
  private static class SymmetricMatrixQ extends AbstractCoreFunctionEvaluator
      implements IPredicate {

    protected boolean compareElements(IExpr expr1, IExpr expr2, EvalEngine engine) {
      if (expr1.isNumber() && expr2.isNumber()) {
        if (expr1.equals(expr2)) {
          return true;
        }
        return false;
      }
      return S.Equal.ofQ(engine, expr1, expr2);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr arg1 = engine.evaluate(ast.arg1());
      int[] dims = arg1.isMatrix();
      if (dims == null || dims[0] != dims[1]) {
        // no square matrix
        return S.False;
      }

      if (arg1.isAST()) {
        final IAST matrix = (IAST) arg1;
        for (int i = 1; i <= dims[0]; i++) {
          IAST row = matrix.getAST(i);
          for (int j = i + 1; j <= dims[1]; j++) {
            IExpr expr = row.get(j);
            IExpr symmetricExpr = matrix.getPart(j, i);
            if (!compareElements(expr, symmetricExpr, engine)) {
              return S.False;
            }
          }
        }
        return S.True;
      } else {
        FieldMatrix<IExpr> matrix = Convert.list2Matrix(arg1);
        if (matrix != null) {
          for (int i = 0; i < dims[0]; i++) {
            for (int j = i + 1; j < dims[1]; j++) {
              IExpr expr = matrix.getEntry(i, j);
              IExpr symmetricExpr = matrix.getEntry(j, i);
              if (!compareElements(expr, symmetricExpr, engine)) {
                return S.False;
              }
            }
          }
          return S.True;
        }
      }
      return S.False;
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
   * SyntaxQ(str)
   * </pre>
   *
   * <blockquote>
   *
   * <p>is <code>True</code> if the given <code>str</code> is a string which has the correct syntax.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; SyntaxQ("Integrate(f(x),{x,0,10})")
   * True
   * </pre>
   */
  private static final class SyntaxQ extends AbstractCorePredicateEvaluator implements IPredicate {

    @Override
    public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
      return arg1.isString() ? ExprParser.test(arg1.toString(), engine) : false;
    }
  }

  /**
   *
   *
   * <pre>
   * ValueQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if and only if <code>expr</code> is defined.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ValueQ(x)
   * False
   *
   * &gt;&gt; x=1;
   * &gt;&gt; ValueQ(x)
   * True
   *
   * &gt;&gt; ValueQ(True)
   * False
   * </pre>
   */
  private static final class ValueQ extends AbstractCoreFunctionEvaluator
      implements Predicate<IExpr>, IPredicate {

    /**
     * Returns <code>True</code> if the 1st argument is an atomic object; <code>False</code>
     * otherwise
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // don't eval first argument
      return F.bool(ast.arg1().isValue());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public boolean test(final IExpr expr) {
      return expr.isValue();
    }
  }

  /**
   *
   *
   * <pre>
   * VectorQ(v)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>v</code> is a list of elements which are not themselves
   * lists.
   *
   * </blockquote>
   *
   * <pre>
   * VectorQ(v, f)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>v</code> is a vector and <code>f(x)</code> returns <code>
   * True</code> for each element <code>x</code> of <code>v</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; VectorQ({a, b, c})
   * True
   * </pre>
   */
  private static final class VectorQ extends AbstractCoreFunctionEvaluator implements IPredicate {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      final IExpr arg1 = engine.evaluate(ast.arg1());
      int dim = arg1.isVector();
      if (dim == (-1)) {
        return S.False;
      }
      if (ast.isAST1()) {
        return S.True;
      }
      if (ast.isAST2()) {
        final IExpr arg2 = engine.evaluate(ast.arg2());
        if (arg2.isSparseArray()) {
          // TODO SparseArray
          return F.NIL;
        }
        IASTAppendable temp = F.ast(arg2);
        temp.append(F.Slot1);

        if (arg1.isAST()) {
          IAST vector = (IAST) arg1;
          if (!vector.forAll(
              x -> {
                temp.set(1, x);
                return engine.evalTrue(temp);
              })) {
            return S.False;
          }
          return S.True;
        } else {
          FieldVector<IExpr> vector = Convert.list2Vector(arg1);
          if (vector != null) {
            for (int i = 0; i < dim; i++) {
              IExpr expr = vector.getEntry(i);
              temp.set(1, expr);
              if (!engine.evalTrue(temp)) {
                return S.False;
              }
            }
            return S.True;
          }
        }
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static boolean isZeroTogether(IExpr expr, EvalEngine engine) {
    //    expr = F.expandAll(expr, true, true);
    //    expr = engine.evaluate(expr);
    //    if (expr.isZero()) {
    //      return true;
    //    }
    long leafCount = expr.leafCount();
    if (leafCount > Config.MAX_POSSIBLE_ZERO_LEAFCOUNT) {
      return false;
    }
    if (expr.isPlusTimesPower()) {
      if (leafCount > (Config.MAX_POSSIBLE_ZERO_LEAFCOUNT / 3)) {
        return false;
      }
      expr = engine.evaluate(F.Together(expr));
      if (expr.isNumber()) {
        return expr.isZero();
      }
      if (expr.isTimes()) {
        IExpr denominator = engine.evaluate(F.Denominator(expr));
        if (!denominator.isOne()) {
          IExpr numerator = engine.evaluate(F.Numerator(expr));
          if (numerator.isAST()) {
            return isPossibleZeroQ((IAST) numerator, false, engine);
          }
        }
      }
    }
    return false;
  }

  public static boolean isPossibleZeroQ(IAST function, boolean fastTest, EvalEngine engine) {
    try {
      VariablesSet varSet = new VariablesSet(function);
      IAST variables = varSet.getVarList();

      if (function.leafCount() < Config.MAX_POSSIBLE_ZERO_LEAFCOUNT / 5) {
        IExpr expr = F.TrigExpand.of(engine, function);
        expr = F.expandAll(expr, true, true);
        expr = engine.evaluate(expr);
        if (!expr.isAST()) {
          return expr.isZero();
        }
        function = (IAST) expr;
      }

      if (variables.isEmpty()) {
        INumber num = function.isNumericFunction(true) ? function.evalNumber() : null;
        if (num == null
            || !(F.isZero(num.reDoubleValue(), Config.SPECIAL_FUNCTIONS_TOLERANCE)
                && F.isZero(num.imDoubleValue(), Config.SPECIAL_FUNCTIONS_TOLERANCE))) {
          return false;
        }
        return true;
      } else {
        if (function.isNumericFunction(varSet)) {

          if (function.isFreeAST(h -> isSpecialNumericFunction(h))) {
            int trueCounter = 0;

            // 1. step test some special complex numeric values
            COMPARE_TERNARY possibeZero =
                isPossibeZeroFixedValues(F.C0, function, variables, engine);
            if (possibeZero == IExpr.COMPARE_TERNARY.FALSE) {
              return false;
            }
            if (possibeZero == IExpr.COMPARE_TERNARY.TRUE) {
              trueCounter++;
            }
            possibeZero = isPossibeZeroFixedValues(F.C1, function, variables, engine);
            if (possibeZero == IExpr.COMPARE_TERNARY.FALSE) {
              return false;
            }
            if (possibeZero == IExpr.COMPARE_TERNARY.TRUE) {
              trueCounter++;
            }
            possibeZero = isPossibeZeroFixedValues(F.CN1, function, variables, engine);
            if (possibeZero == IExpr.COMPARE_TERNARY.FALSE) {
              return false;
            }
            if (possibeZero == IExpr.COMPARE_TERNARY.TRUE) {
              trueCounter++;
            }
            possibeZero = isPossibeZeroFixedValues(F.CI, function, variables, engine);
            if (possibeZero == IExpr.COMPARE_TERNARY.FALSE) {
              return false;
            }
            if (possibeZero == IExpr.COMPARE_TERNARY.TRUE) {
              trueCounter++;
            }
            possibeZero = isPossibeZeroFixedValues(F.CNI, function, variables, engine);
            if (possibeZero == IExpr.COMPARE_TERNARY.FALSE) {
              return false;
            }
            if (possibeZero == IExpr.COMPARE_TERNARY.TRUE) {
              trueCounter++;
            }

            if (trueCounter == 5) {
              // 2. step test some random complex numeric values
              for (int i = 0; i < 36; i++) {
                possibeZero = isPossibeZero(function, variables, engine);
                if (possibeZero == IExpr.COMPARE_TERNARY.FALSE) {
                  return false;
                }
                if (possibeZero == IExpr.COMPARE_TERNARY.TRUE) {
                  trueCounter++;
                }
              }
              if (trueCounter > 28) {
                return true;
              }
            }
            if (fastTest) {
              return false;
            }
          }
        }
      }

      IExpr temp =
          function.replace( //
              x -> x.isNumericFunction(true), //
              x -> {
                IExpr t = x.evalNumber();
                return t != null ? t : F.NIL;
              });
      if (temp.isPresent()) {
        temp = engine.evaluate(temp);
        if (temp.isZero()) {
          return true;
        }
      }

      //      if (function.isPlus()) {
      //        IExpr[] commonFactors = InternalFindCommonFactorPlus.findCommonFactors(function,
      // true);
      //        if (commonFactors != null) {
      //          temp = S.Simplify.of(engine, F.Times(commonFactors[0], commonFactors[1]));
      //          if (temp.isNumber()) {
      //            return temp.isZero();
      //          }
      //          temp = temp.evalNumber();
      //          if (temp != null) {
      //            if (temp.isZero()) {
      //              return true;
      //            }
      //          }
      //        }
      //      }

      return isZeroTogether(function, engine);
    } catch (ValidateException ve) {
      LOGGER.debug("PredicateQ.isPossibleZeroQ() failed", ve);
    }
    return false;
  }

  private static boolean isSpecialNumericFunction(IExpr head) {
    if (head.isPower()) {
      if (!head.exponent().isNumber()) {
        return false;
      }
      return true;
    }
    int h = head.headID();

    return h == ID.AppellF1
        || h == ID.Clip
        //        || h == ID.Cosh
        || h == ID.Csch
        || h == ID.Cot
        || h == ID.Csc
        || h == ID.Gamma
        || h == ID.HankelH1
        || h == ID.HankelH2
        || h == ID.Hypergeometric0F1
        || h == ID.Hypergeometric1F1
        || h == ID.Hypergeometric2F1
        || h == ID.Hypergeometric1F1Regularized
        || h == ID.HypergeometricPFQ
        || h == ID.HypergeometricPFQRegularized
        || h == ID.HypergeometricU
        || h == ID.JacobiAmplitude
        || h == ID.JacobiCD
        || h == ID.JacobiCN
        || h == ID.JacobiDC
        || h == ID.JacobiDN
        || h == ID.JacobiNC
        || h == ID.JacobiND
        || h == ID.JacobiSC
        || h == ID.JacobiSD
        || h == ID.JacobiSN
        || h == ID.JacobiZeta
        || h == ID.KleinInvariantJ
        || h == ID.Log
        || h == ID.Piecewise
        // || h == ID.Power
        || h == ID.ProductLog
        //        || h == ID.Sinh
        || h == ID.StruveH
        || h == ID.StruveL
        || h == ID.Tan
        || h == ID.WeierstrassHalfPeriods
        || h == ID.WeierstrassInvariants
        || h == ID.WeierstrassP
        || h == ID.WeierstrassPPrime
        || h == ID.InverseWeierstrassP;
  }

  /**
   * Test if <code>Complex(re, im)</code> inserted into the arguments of the function and evaluated
   * approximates <code>0</code>.
   *
   * <ul>
   *   <li><code>IExpr.COMPARE_TERNARY.TRUE</code> if the result approximates <code>0</code>
   *   <li><code>IExpr.COMPARE_TERNARY.FALSE</code> if the result is a number and doesn't
   *       approximate <code>0</code>
   *   <li><code>IExpr.COMPARE_TERNARY.UNDECIDABLE</code> if the result isn't a number
   * </ul>
   *
   * @param function the function which should be evaluate for the <code>variables</code>
   * @param variables variables the symbols which will be replaced by <code>Complex(re, im)</code>
   *     to evaluate <code>function</code>
   * @param engine
   * @return
   */
  private static IExpr.COMPARE_TERNARY isPossibeZero(
      IAST function, IAST variables, EvalEngine engine) {
    IASTAppendable listOfRules = F.ListAlloc(variables.size());
    ThreadLocalRandom tlr = ThreadLocalRandom.current();
    for (int i = 1; i < variables.size(); i++) {
      double re = tlr.nextDouble(-100, 100);
      double im = tlr.nextDouble(-100, 100);
      listOfRules.append(F.Rule(variables.get(i), F.complexNum(re, im)));
    }
    IExpr temp = function.replaceAll(listOfRules);
    return isPossibleZeroApproximate(temp, engine);
  }

  private static IExpr.COMPARE_TERNARY isPossibeZeroFixedValues(
      INumber number, IAST function, IAST variables, EvalEngine engine) {
    IASTAppendable listOfRules = F.ListAlloc(variables.size());
    for (int i = 1; i < variables.size(); i++) {
      listOfRules.append(F.Rule(variables.get(i), number));
    }
    IExpr temp = function.replaceAll(listOfRules);
    return isPossibleZeroExact(temp, engine);
  }

  private static IExpr.COMPARE_TERNARY isPossibleZeroExact(IExpr temp, EvalEngine engine) {
    try {
      if (temp.isPresent()) {
        IExpr result = engine.evalQuiet(temp);
        if (result.isNumber()) {
          return result.isZero() ? IExpr.COMPARE_TERNARY.TRUE : IExpr.COMPARE_TERNARY.FALSE;
        }
        if (result.isDirectedInfinity()) {
          return IExpr.COMPARE_TERNARY.FALSE;
        }

        //        if (isZeroTogether(result, engine)) {
        //          return IExpr.COMPARE_TERNARY.TRUE;
        //        }
      }
    } catch (RuntimeException rex) {
      //
    }
    return IExpr.COMPARE_TERNARY.UNDECIDABLE;
  }

  private static IExpr.COMPARE_TERNARY isPossibleZeroApproximate(IExpr temp, EvalEngine engine) {
    try {
      if (temp.isPresent()) {
        IExpr result = engine.evalQuiet(F.N(temp));
        if (result.isZero()) {
          return IExpr.COMPARE_TERNARY.TRUE;
        }
        if (result.isNumber() && !result.isZero()) {
          double realPart = ((INumber) result).reDoubleValue();
          double imaginaryPart = ((INumber) result).imDoubleValue();
          if (!(F.isZero(realPart, Config.SPECIAL_FUNCTIONS_TOLERANCE)
              && F.isZero(imaginaryPart, Config.SPECIAL_FUNCTIONS_TOLERANCE))) {
            if (Double.isNaN(realPart)
                || Double.isNaN(imaginaryPart)
                || Double.isInfinite(realPart)
                || Double.isInfinite(imaginaryPart)) {
              return IExpr.COMPARE_TERNARY.UNDECIDABLE;
            }
            return IExpr.COMPARE_TERNARY.FALSE;
          }
          return IExpr.COMPARE_TERNARY.TRUE;
        }
        if (result.isDirectedInfinity()) {
          return IExpr.COMPARE_TERNARY.FALSE;
        }
      }
    } catch (RuntimeException rex) {
      //
    }
    return IExpr.COMPARE_TERNARY.UNDECIDABLE;
  }

  public static void initialize() {
    Initializer.init();
  }

  private PredicateQ() {}
}
