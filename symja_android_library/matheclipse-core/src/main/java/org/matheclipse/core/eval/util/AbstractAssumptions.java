package org.matheclipse.core.eval.util;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.ISignedNumberConstant;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public abstract class AbstractAssumptions implements IAssumptions {

  @Override
  public IAssumptions addAssumption(IExpr expr) {
    return null;
  }

  @Override
  public IAST distribution(IExpr expr) {
    return F.NIL;
  }

  public Map<IExpr, IAST> getTensorsMap() {
    return new HashMap<IExpr, IAST>();
  }

  @Override
  public IAST tensors(IExpr expr) {
    return F.NIL;
  }
  /**
   * TODO implement algebraic number conditions.
   *
   * @param expr
   * @return
   */
  public static ISymbol assumeAlgebraic(final IExpr expr) {
    if (expr.isRational()) {
      return S.True;
    }
    if (expr.isNumber()) {
      return S.True;
    }
    if (expr.isDirectedInfinity()) {
      return S.False;
    }
    if (expr == S.Undefined) {
      return S.Undefined;
    }
    if (expr.isSymbol()) {
      if (expr.equals(S.Degree)) {
        return S.False;
      }
      if (expr.equals(S.Pi)) {
        return S.False;
      }
      if (expr.equals(S.E)) {
        return S.False;
      }

      // if (((ISymbol) expr).getEvaluator() instanceof
      // ISignedNumberConstant) {
      // return S.True;
      // }
    }
    IAssumptions assumptions = EvalEngine.get().getAssumptions();
    if (assumptions != null) {
      if (assumptions.isAlgebraic(expr)) {
        return S.True;
      }
      if (assumptions.isRational(expr)) {
        return S.True;
      }
      if (assumptions.isInteger(expr)) {
        return S.True;
      }
      if (assumptions.isPrime(expr)) {
        return S.True;
      }
    }
    return null;
  }

  /**
   * Test if <code>expr</code> is assumed to be an <code>Arrays(list, domain, symmetry)</code>
   * expression.
   *
   * @param expr
   * @return
   */
  public static ISymbol assumeArray(final IExpr expr) {
    IAssumptions assumptions = EvalEngine.get().getAssumptions();
    if (assumptions != null) {
      IAST temp = assumptions.tensors(expr);
      if (temp.isAST(S.Arrays, 2, 4)) {
        return S.True;
      }
    }
    return null;
  }

  public static ISymbol assumeMatrices(final IExpr expr) {
    IAssumptions assumptions = EvalEngine.get().getAssumptions();
    if (assumptions != null) {
      IAST temp = assumptions.tensors(expr);
      if (temp.isAST(S.Matrices, 3, 4)) {
        return S.True;
      }
    }
    return null;
  }

  public static ISymbol assumeVectors(final IExpr expr) {
    IAssumptions assumptions = EvalEngine.get().getAssumptions();
    if (assumptions != null) {
      IAST temp = assumptions.tensors(expr);
      if (temp.isAST(S.Vectors, 3, 4)) {
        return S.True;
      }
    }
    return null;
  }
  /**
   * Test if <code>expr</code> is assumed to be an boolean value.
   *
   * @param expr
   * @return <code>S.True</code> or <code>S.False</code> if <code>expr</code> is assumed to be a
   *     boolean value or no boolean value. In all other cases return <code>null</code>.
   */
  public static ISymbol assumeBoolean(final IExpr expr) {
    if (expr.isTrue() || expr.isFalse()) {
      return S.True;
    }
    if (expr.isNumber()) {
      return S.False;
    }
    if (expr.isDirectedInfinity()) {
      return S.False;
    }
    if (expr == S.Undefined) {
      return S.Undefined;
    }
    IAssumptions assumptions = EvalEngine.get().getAssumptions();
    if (assumptions != null) {
      if (assumptions.isBoolean(expr)) {
        return S.True;
      }
    }
    return null;
  }

  /**
   * Test if <code>expr</code> is assumed to be a complex number.
   *
   * @param expr
   * @return <code>S.True</code> or <code>S.False</code> if <code>expr</code> is assumed to be a
   *     complex number or no complex number. In all other cases return <code>null</code>.
   */
  public static ISymbol assumeComplex(final IExpr expr) {
    if (expr.isNumber()) {
      return S.True;
    }
    if (expr.isRealConstant()) {
      return S.True;
    }
    if (expr.isDirectedInfinity()) {
      return S.False;
    }
    if (expr == S.Undefined) {
      return S.Undefined;
    }
    IAssumptions assumptions = EvalEngine.get().getAssumptions();
    if (assumptions != null) {
      if (assumptions.isComplex(expr)) {
        return S.True;
      }
      if (assumptions.isRational(expr)) {
        return S.True;
      }
      if (assumptions.isInteger(expr)) {
        return S.True;
      }
      if (assumptions.isPrime(expr)) {
        return S.True;
      }
      if (assumptions.isReal(expr)) {
        return S.True;
      }
      if (assumptions.isAlgebraic(expr)) {
        return S.True;
      }
    }
    return null;
  }

  /**
   * Test if <code>expr</code> is assumed to be an integer.
   *
   * @param expr
   * @return <code>S.True</code> or <code>S.False</code> if <code>expr</code> is assumed to be an
   *     integer or no integer. In all other cases return <code>null</code>.
   */
  public static ISymbol assumeInteger(final IExpr expr) {
    if (expr.isInteger()) {
      return S.True;
    }
    if (expr.isNumber()) {
      return S.False;
    }
    if (expr.isDirectedInfinity()) {
      return S.False;
    }
    if (expr == S.Undefined) {
      return S.Undefined;
    }
    IAssumptions assumptions = EvalEngine.get().getAssumptions();
    if (assumptions != null) {
      if (assumptions.isInteger(expr)) {
        return S.True;
      }
      if (assumptions.isPrime(expr)) {
        return S.True;
      }
    }
    return null;
  }

  /**
   * Test if <code>expr</code> is assumed to be an negative number.
   *
   * @param expr
   * @return <code>true</code> if <code>expr</code> is assumed to be a negative number. Return
   *     <code>false</code> in all other cases.
   */
  public static boolean assumeNegative(final IExpr expr) {
    if (expr.isReal()) {
      return expr.isNegative();
    }
    if (expr.isNumber()) {
      return false;
    }
    if (expr.isRealConstant()) {
      return ((ISignedNumberConstant) ((IBuiltInSymbol) expr).getEvaluator()).evalReal() < 0.0;
    }
    IAssumptions assumptions = EvalEngine.get().getAssumptions();
    if (assumptions != null) {
      if (assumptions.isNegative(expr)) {
        return true;
      }
    }
    return false;
  }

  public static boolean assumeLessEqual(final IExpr expr, final ISignedNumber number) {
    if (expr.isReal()) {
      return ((ISignedNumber) expr).isLE(number);
    }
    if (expr.isNumber()) {
      return false;
    }
    if (expr.isRealConstant()) {
      return ((ISignedNumberConstant) ((IBuiltInSymbol) expr).getEvaluator()).evalReal()
          <= number.doubleValue();
    }
    IAssumptions assumptions = EvalEngine.get().getAssumptions();
    if (assumptions != null) {
      if (assumptions.isLessEqual(expr, number)) {
        return true;
      }
    }
    return false;
  }

  public static boolean assumeLessThan(final IExpr expr, ISignedNumber number) {
    if (expr.isReal()) {
      return ((ISignedNumber) expr).isLT(number);
    }
    if (expr.isNumber()) {
      return false;
    }
    if (expr.isRealConstant()) {
      return ((ISignedNumberConstant) ((IBuiltInSymbol) expr).getEvaluator()).evalReal()
          < number.doubleValue();
    }
    IAssumptions assumptions = EvalEngine.get().getAssumptions();
    if (assumptions != null) {
      if (assumptions.isLessThan(expr, number)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Test if <code>expr</code> is assumed to be an non negative number.
   *
   * @param expr
   * @return <code>true</code> if <code>expr</code> is assumed to be a non negative number. Return
   *     <code>false</code> in all other cases.
   */
  public static boolean assumeNonNegative(final IExpr expr) {
    if (expr.isReal()) {
      return ((ISignedNumber) expr).isNonNegativeResult();
    }
    if (expr.isNumber()) {
      return false;
    }
    if (expr.isRealConstant()) {
      return ((ISignedNumberConstant) ((IBuiltInSymbol) expr).getEvaluator()).evalReal() >= 0.0;
    }
    IAssumptions assumptions = EvalEngine.get().getAssumptions();
    if (assumptions != null) {
      if (assumptions.isNonNegative(expr)) {
        return true;
      }
    }
    return false;
  }

  public static boolean assumeGreaterEqual(final IExpr expr, final ISignedNumber number) {
    if (expr.isReal()) {
      return ((ISignedNumber) expr).isGT(number);
    }
    if (expr.isNumber()) {
      return false;
    }
    if (expr.isRealConstant()) {
      return ((ISignedNumberConstant) ((IBuiltInSymbol) expr).getEvaluator()).evalReal()
          >= number.doubleValue();
    }
    IAssumptions assumptions = EvalEngine.get().getAssumptions();
    if (assumptions != null) {
      if (assumptions.isGreaterEqual(expr, number)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Test if <code>expr</code> is assumed to be an positive number.
   *
   * @param expr
   * @return <code>true</code> if <code>expr</code> is assumed to be a positive number. Return
   *     <code>false</code> in all other cases.
   */
  public static boolean assumePositive(final IExpr expr) {
    if (expr.isReal()) {
      return ((ISignedNumber) expr).isPositive();
    }
    if (expr.isNumber()) {
      return false;
    }
    if (expr.isRealConstant()) {
      return ((ISignedNumberConstant) ((IBuiltInSymbol) expr).getEvaluator()).evalReal() > 0.0;
    }
    IAssumptions assumptions = EvalEngine.get().getAssumptions();
    if (assumptions != null) {
      if (assumptions.isPositive(expr)) {
        return true;
      }
    }
    return false;
  }

  public static boolean assumeGreaterThan(final IExpr expr, ISignedNumber number) {
    if (expr.isReal()) {
      return ((ISignedNumber) expr).isGT(number);
    }
    if (expr.isNumber()) {
      return false;
    }
    if (expr.isRealConstant()) {
      return ((ISignedNumberConstant) ((IBuiltInSymbol) expr).getEvaluator()).evalReal()
          > number.doubleValue();
    }
    IAssumptions assumptions = EvalEngine.get().getAssumptions();
    if (assumptions != null) {
      if (assumptions.isGreaterThan(expr, number)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Test if <code>expr</code> is assumed to be an prime number.
   *
   * @param expr
   * @return <code>S.True</code> or <code>S.False</code> if <code>expr</code> is assumed to be a
   *     prime number or no prime number. In all other cases return <code>F.NIL</code>.
   */
  public static IExpr assumePrime(final IExpr expr) {
    if (expr.isInteger() && ((IInteger) expr).isProbablePrime()) {
      return S.True;
    }
    if (expr.isNumber()) {
      return S.False;
    }
    if (expr.isDirectedInfinity()) {
      return S.False;
    }
    if (expr == S.Undefined) {
      return S.Undefined;
    }
    IAssumptions assumptions = EvalEngine.get().getAssumptions();
    if (assumptions != null) {
      if (assumptions.isPrime(expr)) {
        return S.True;
      }
    }
    return F.NIL;
  }

  /**
   * Test if <code>expr</code> is assumed to be a rational number.
   *
   * @param expr
   * @return <code>S.True</code> or <code>S.False</code> if <code>expr</code> is assumed to be a
   *     rational number or no rational number. In all other cases return <code>null</code>.
   */
  public static ISymbol assumeRational(final IExpr expr) {
    if (expr.isRational()) {
      return S.True;
    }
    if (expr.isNumber()) {
      return S.False;
    }
    if (expr.isDirectedInfinity()) {
      return S.False;
    }
    if (expr == S.Undefined) {
      return S.Undefined;
    }
    IAssumptions assumptions = EvalEngine.get().getAssumptions();
    if (assumptions != null) {
      if (assumptions.isRational(expr)) {
        return S.True;
      }
      if (assumptions.isInteger(expr)) {
        return S.True;
      }
      if (assumptions.isPrime(expr)) {
        return S.True;
      }
    }
    return null;
  }

  /**
   * Test if <code>expr</code> is assumed to be a real number.
   *
   * @param expr
   * @return <code>S.True</code> or <code>S.False</code> if <code>expr</code> is assumed to be a
   *     real number or no real number. In all other cases return <code>null</code>.
   */
  public static ISymbol assumeReal(final IExpr expr) {
    if (expr.isReal()) {
      return S.True;
    }
    if (expr.isNumber()) {
      return S.False;
    }
    if (expr.isRealConstant()) {
      return S.True;
    }
    if (expr.isDirectedInfinity()) {
      return S.False;
    }
    if (expr == S.Undefined) {
      return S.Undefined;
    }
    IAssumptions assumptions = EvalEngine.get().getAssumptions();
    if (assumptions != null) {
      if (assumptions.isReal(expr)) {
        return S.True;
      }
      if (assumptions.isInteger(expr)) {
        return S.True;
      }
      if (assumptions.isPrime(expr)) {
        return S.True;
      }
      if (assumptions.isRational(expr)) {
        return S.True;
      }
    }
    return null;
  }

  public static boolean isNegativeResult(IAST ast) {
    ISignedNumber e = ast.evalReal();
    if (e != null) {
      return e.isNegative();
    }
    IExpr head = ast.head();
    if (head.isSymbol()) {
      ISymbol symbol = (ISymbol) head;
      int size = ast.size();
      if (size == 2) {
        IExpr arg1 = ast.arg1();
        if (symbol.equals(S.Log) && assumePositive(arg1) && assumeLessThan(arg1, F.C1)) {
          return true;
        }
      }
      if (ast.isPlus()) {
        for (int i = 1; i < size; i++) {
          IExpr x = ast.get(i);
          if (x.isNegativeResult() || assumeNegative(x)) {
            continue;
          }
          return false;
        }
        return true;
      }
      if (ast.isTimes()) {
        boolean flag = false;
        for (int i = 1; i < size; i++) {
          IExpr x = ast.get(i);
          if (x.isNonNegativeResult() || assumeNonNegative(x)) {
          } else {
            if (x.isNegativeResult()) {
            } else if (assumeNegative(x)) {
            } else {
              return false;
            }
            flag = !flag;
          }
        }
        return flag;
      }
      if (ast.isPower()) {
        IExpr base = ast.base();
        IExpr exponent = ast.exponent();
        if (base.isNegativeResult() && exponent.isInteger()) {
          IInteger iArg2 = (IInteger) exponent;
          if (iArg2.isOdd()) {
            return true;
          }
        }
        return false;
      }
    }
    return false;
  }

  public static boolean isNonNegativeResult(IAST ast) {
    ISignedNumber e = ast.evalReal();
    if (e != null) {
      return !e.isNegative();
    }
    IExpr head = ast.head();
    if (head.isSymbol()) {
      ISymbol symbol = (ISymbol) head;
      int size = ast.size();
      if (size == 2) {
        IExpr arg1 = ast.arg1();
        if (symbol.equals(S.Abs)) {
          return true;
        }
        if (symbol.equals(S.Log) && assumeGreaterEqual(arg1, F.C1)) {
          return true;
        }
      }
      if (ast.isPlus()) {
        for (int i = 1; i < size; i++) {
          IExpr x = ast.get(i);
          if (x.isNonNegativeResult() || assumeNonNegative(x)) {
            continue;
          }
          return false;
        }
        return true;
      }
      if (ast.isTimes()) {
        boolean flag = true;
        for (int i = 1; i < size; i++) {
          IExpr x = ast.get(i);
          if (x.isNonNegativeResult() || assumeNonNegative(x)) {
          } else {
            if (x.isNegativeResult()) {
            } else if (assumeNegative(x)) {
            } else {
              return false;
            }
            flag = !flag;
          }
        }
        return flag;
      }
      if (ast.isPower()) {
        IExpr base = ast.base();
        IExpr exponent = ast.exponent();
        if (base.isRealResult() && exponent.isInteger()) {
          IInteger iArg2 = (IInteger) exponent;
          if (iArg2.isEven()) {
            return true;
          }
        }
        if (base.isE()) {
          if (exponent.isRealResult()) {
            return true;
          }
        }
        return false;
      }
    }
    return false;
  }

  public static boolean isPositiveResult(IAST ast) {
    ISignedNumber e = ast.evalReal();
    if (e != null) {
      return e.isPositive();
    }
    IExpr head = ast.head();
    if (head.isSymbol()) {

      ISymbol symbol = (ISymbol) head;
      int size = ast.size();
      if (size == 2) {
        IExpr arg1 = ast.arg1();
        if (symbol.equals(S.Abs) && (assumeNegative(arg1) || assumePositive(arg1))) {
          return true;
        }
        if (symbol.equals(S.Log) && assumeGreaterThan(arg1, F.C1)) {
          return true;
        }
      }
      if (ast.isPlus()) {
        for (int i = 1; i < size; i++) {
          IExpr x = ast.get(i);
          if (x.isPositiveResult() || assumePositive(x)) {
            continue;
          }
          return false;
        }
        return true;
      }
      if (ast.isTimes()) {
        boolean flag = true;
        for (int i = 1; i < size; i++) {
          IExpr x = ast.get(i);
          if (x.isPositiveResult() || assumePositive(x)) {
          } else {
            if (x.isNegativeResult()) {
            } else if (assumeNegative(x)) {
            } else {
              return false;
            }
            flag = !flag;
          }
        }
        return flag;
      }
      if (ast.isPower()) {
        // base ^ exponent
        IExpr base = ast.base();
        IExpr exponent = ast.exponent();
        if (exponent.isInteger()) {
          IInteger iArg2 = (IInteger) exponent;
          if (base.isNegativeResult()) {
            if (iArg2.isEven()) {
              return true;
            }
          }
          if (base.isPositiveResult() && exponent.isInteger()) {
            if (iArg2.isEven()) {
              return true;
            }
          }
        }
        if (base.isE()) {
          if (exponent.isRealResult()) {
            return true;
          }
        }
        return false;
      }
    }
    return false;
  }

  @Override
  public boolean isAlgebraic(IExpr expr) {
    return false;
  }

  @Override
  public boolean isBoolean(IExpr expr) {
    return false;
  }

  @Override
  public boolean isComplex(IExpr expr) {
    return false;
  }

  @Override
  public boolean isGreaterEqual(IExpr expr, ISignedNumber number) {
    return false;
  }

  @Override
  public boolean isGreaterThan(IExpr expr, ISignedNumber number) {
    return false;
  }

  @Override
  public boolean isInteger(IExpr expr) {
    return false;
  }

  @Override
  public boolean isLessEqual(IExpr expr, ISignedNumber number) {
    return false;
  }

  @Override
  public boolean isLessThan(IExpr expr, ISignedNumber number) {
    return false;
  }

  @Override
  public boolean isNegative(IExpr expr) {
    return false;
  }

  @Override
  public boolean isNonNegative(IExpr expr) {
    return false;
  }

  @Override
  public boolean isPositive(IExpr expr) {
    return false;
  }

  @Override
  public boolean isPrime(IExpr expr) {
    return false;
  }

  @Override
  public boolean isRational(IExpr expr) {
    return false;
  }

  @Override
  public boolean isReal(IExpr expr) {
    return false;
  }
}
