package org.matheclipse.core.polynomials.symbolicexponent;

import java.io.Reader;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.longexponent.ExpVectorLong;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;
import org.matheclipse.core.polynomials.longexponent.ExprTermOrder;
import edu.jas.kern.PrettyPrint;
import edu.jas.kern.Scripting;
import edu.jas.structure.RingFactory;
import edu.jas.util.CartesianProduct;
import edu.jas.util.CartesianProductInfinite;
import edu.jas.util.LongIterable;

/**
 * GenPolynomialRing generic polynomial factory implementing ExprRingFactory; Factory for n-variate
 * ordered polynomials over C with Java {@link IExpr} exponents. Almost immutable object, except
 * variable names.
 */
public class SymbolicPolynomialRing implements RingFactory<SymbolicPolynomial> {

  /** Comparator for polynomials. */
  private static class SymbolicPolynomialComparator
      implements Serializable, Comparator<SymbolicPolynomial> {

    /** */
    private static final long serialVersionUID = -2427163728878196089L;

    public final SymbolicTermOrder tord;

    public final boolean reverse;

    /**
     * Constructor.
     *
     * @param t TermOrder.
     * @param reverse flag if reverse ordering is requested.
     */
    public SymbolicPolynomialComparator(SymbolicTermOrder t, boolean reverse) {
      tord = t;
      this.reverse = reverse;
    }

    /**
     * Compare polynomials.
     *
     * @param p1 first polynomial.
     * @param p2 second polynomial.
     * @return 0 if ( p1 == p2 ), -1 if ( p1 < p2 ) and +1 if ( p1 > p2 ).
     */
    @Override
    public int compare(SymbolicPolynomial p1, SymbolicPolynomial p2) {
      // check if p1.tord = p2.tord = tord ?
      int s = p1.compareTo(p2);
      if (reverse) {
        return -s;
      }
      return s;
    }

    /**
     * Equals test of comparator.
     *
     * @param o other object.
     * @return true if this = o, else false.
     */
    @Override
    public boolean equals(Object o) {
      SymbolicPolynomialComparator pc = null;
      try {
        pc = (SymbolicPolynomialComparator) o;
      } catch (ClassCastException ignored) {
        return false;
      }
      if (pc == null) {
        return false;
      }
      return tord.equals(pc.tord);
    }

    /**
     * Hash code for this PolynomialComparator.
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      return tord.hashCode();
    }

    /** toString. */
    @Override
    public String toString() {
      return "PolynomialComparator(" + tord + ")";
    }
  }

  /** Iterable for IExpr. */
  private static class IExprIterable implements Iterable<IExpr> {

    private boolean nonNegative = true;

    private IExpr upperBound = F.CInfinity;

    /** Constructor. */
    public IExprIterable() {}

    /** Constructor. */
    public IExprIterable(IExpr ub) {
      upperBound = ub;
    }

    /**
     * Set the upper bound for the iterator.
     *
     * @param ub an upper bound for the iterator elements.
     */
    public void setUpperBound(IExpr ub) {
      upperBound = ub;
    }

    /**
     * Get the upper bound for the iterator.
     *
     * @return the upper bound for the iterator elements.
     */
    public IExpr getUpperBound() {
      return upperBound;
    }

    /** Set the iteration algorithm to all elements. */
    public void setAllIterator() {
      nonNegative = false;
    }

    /** Set the iteration algorithm to non-negative elements. */
    public void setNonNegativeIterator() {
      nonNegative = true;
    }

    /**
     * Get an iterator over Long.
     *
     * @return an iterator.
     */
    @Override
    public Iterator<IExpr> iterator() {
      return new IExprIterator(nonNegative, upperBound);
    }

    /** IExpr iterator. */
    class IExprIterator implements Iterator<IExpr> {

      /** data structure. */
      IExpr current;

      boolean empty;

      final boolean nonNegative;

      protected IExpr upperBound;

      /**
       * Set the upper bound for the iterator.
       *
       * @param ub an upper bound for the iterator elements.
       */
      public void setUpperBound(IExpr ub) {
        upperBound = ub;
      }

      /**
       * Get the upper bound for the iterator.
       *
       * @return the upper bound for the iterator elements.
       */
      public IExpr getUpperBound() {
        return upperBound;
      }

      /** Long iterator constructor. */
      public IExprIterator() {
        this(false, F.CInfinity);
      }

      /**
       * Long iterator constructor.
       *
       * @param nn true for an iterator over non-negative longs, false for all elements iterator.
       * @param ub an upper bound for the entries.
       */
      public IExprIterator(boolean nn, IExpr ub) {
        current = F.C0;
        empty = false;
        nonNegative = nn;
        upperBound = ub;
      }

      /**
       * Test for availability of a next long.
       *
       * @return true if the iteration has more Longs, else false.
       */
      @Override
      public synchronized boolean hasNext() {
        return !empty;
      }

      /**
       * Get next Long.
       *
       * @return next Long.
       */
      @Override
      public synchronized IExpr next() {
        if (empty) {
          throw new NoSuchElementException("invalid call of next()");
        }
        IExpr res = current; // Long.valueOf(current);
        if (nonNegative) {
          current = current.inc();
        } else if (current.isPositiveResult()) {
          current = current.negate();
        } else {
          current = current.negate();
          current = current.inc();
        }
        if (S.Greater.ofQ(current, upperBound)) {
          empty = true;
        }
        return res;
      }

      /** Remove a tuple if allowed. */
      @Override
      public void remove() {
        throw new UnsupportedOperationException("cannnot remove elements");
      }
    }
  }

  /** */
  private static final long serialVersionUID = -6136386786501333693L;

  /** The factory for the coefficients. */
  public final ExprRingFactory coFac;

  /** The number of variables. */
  public final int nvar;

  /** The term order. */
  public final SymbolicTermOrder tord;

  /** True for partially reversed variables. */
  protected boolean partial;

  /** The names of the variables. This value can be modified. */
  protected IAST vars;

  /** The constant polyl 0 for this ring. */
  public final SymbolicPolynomial ZERO;

  /** The constant polynomial 1 for this ring. */
  public final SymbolicPolynomial ONE;

  /** The constant exponent vector 0 for this ring. */
  public final ExpVectorSymbolic evzero;

  /** A default random sequence generator. */
  protected static final ThreadLocalRandom random = ThreadLocalRandom.current();

  /** Indicator if this ring is a field. */
  protected int isField = -1; // initially unknown

  final boolean numericFunction;

  /**
   * The constructor creates a polynomial factory object.
   *
   * @param listOfVariables names for the variables.
   */
  public SymbolicPolynomialRing(IAST listOfVariables) {
    this(ExprRingFactory.CONST, listOfVariables, listOfVariables.argSize(),
        SymbolicTermOrderByName.Lexicographic);
  }

  /**
   * The constructor creates a polynomial factory object.
   *
   * @param listOfVariables names for the variables.
   * @param t a term order.
   */
  public SymbolicPolynomialRing(IAST listOfVariables, SymbolicTermOrder t) {
    this(ExprRingFactory.CONST, listOfVariables, listOfVariables.argSize(), t);
  }

  /**
   * The constructor creates a polynomial factory object.
   *
   * @param symbol name of a variable.
   */
  public SymbolicPolynomialRing(ISymbol symbol) {
    this(ExprRingFactory.CONST, F.list(symbol), 1, SymbolicTermOrderByName.Lexicographic);
  }

  /**
   * The constructor creates a polynomial factory object.
   *
   * @param symbol name of a variable.
   * @param t a term order.
   */
  public SymbolicPolynomialRing(ISymbol symbol, SymbolicTermOrder t) {
    this(ExprRingFactory.CONST, F.list(symbol), 1, t);
  }

  /**
   * The constructor creates a polynomial factory object.
   *
   * @param cf factory for coefficients of type C.
   * @param listOfVariables names for the variables.
   */
  public SymbolicPolynomialRing(ExprRingFactory cf, IAST listOfVariables) {
    this(cf, listOfVariables, listOfVariables.argSize(), SymbolicTermOrderByName.Lexicographic);
  }

  /**
   * The constructor creates a polynomial factory object.
   *
   * @param cf factory for coefficients of type C.
   * @param listOfVariables names for the variables.
   * @param t a term order.
   */
  public SymbolicPolynomialRing(ExprRingFactory cf, IAST listOfVariables, SymbolicTermOrder t) {
    this(cf, listOfVariables, listOfVariables.argSize(), t);
  }

  /**
   * The constructor creates a polynomial factory object.
   *
   * @param cf factory for coefficients of type C.
   * @param listOfVariables names for the variables.
   * @param n number of variables.
   * @param t a term order.
   */
  private SymbolicPolynomialRing(ExprRingFactory cf, IAST listOfVariables, int n,
      SymbolicTermOrder t) {
    this(cf, listOfVariables, n, t, false);
  }

  /**
   * The constructor creates a polynomial factory object.
   *
   * @param cf factory for coefficients of type C.
   * @param listOfVariables names for the variables.
   * @param n number of variables.
   * @param t a term order.
   * @param numericFunction
   */
  public SymbolicPolynomialRing(ExprRingFactory cf, IAST listOfVariables, int n,
      SymbolicTermOrder t, boolean numericFunction) {
    coFac = cf;
    nvar = n;
    tord = t;
    partial = false;
    vars = listOfVariables.copyAppendable();
    ZERO = new SymbolicPolynomial(this);
    IExpr coeff = coFac.getONE();
    evzero = new ExpVectorSymbolic(nvar);
    this.numericFunction = numericFunction;
    ONE = new SymbolicPolynomial(this, coeff, evzero);
    if (vars.argSize() != nvar) {
      throw new IllegalArgumentException("incompatible variable size " + vars.size() + ", " + nvar);
    }
  }

  /**
   * The constructor creates a polynomial factory object with the the same term order, number of
   * variables and variable names as the given polynomial factory, only the coefficient factories
   * differ.
   *
   * @param cf factory for coefficients of type C.
   * @param o other polynomial ring.
   */
  public SymbolicPolynomialRing(ExprRingFactory cf, SymbolicPolynomialRing o) {
    this(cf, o.vars, o.nvar, o.tord);
  }

  /**
   * The constructor creates a polynomial factory object with the the same coefficient factory,
   * number of variables and variable names as the given polynomial factory, only the term order
   * differs.
   *
   * @param to term order.
   * @param o other polynomial ring.
   */
  public SymbolicPolynomialRing(SymbolicPolynomialRing o, SymbolicTermOrder to) {
    this(o.coFac, o.vars, o.nvar, to);
  }

  /**
   * Copy this factory.
   *
   * @return a clone of this.
   */
  public SymbolicPolynomialRing copy() {
    return new SymbolicPolynomialRing(coFac, this);
  }

  /**
   * Create a <code>Polynomial</code> from the given <code>exprPoly</code>.
   *
   * @param exprPoly the polynomial expression
   * @return
   */
  public SymbolicPolynomial create(final IExpr exprPoly)
      throws ArithmeticException, ClassCastException {
    return create(exprPoly, false, true, false);
  }

  /**
   * Create a <code>Polynomial</code> from the given <code>exprPoly</code>.
   *
   * @param exprPoly the polynomial expression
   * @param coefficient set to <code>true</code> if called by the <code>Coefficient()</code>
   *        function
   * @param checkNegativeExponents if <code>true</code> don't allow negative exponents and throw an
   *        ArithmeticException
   * @param coefficientListMode if in coefficient list mode don't collect negative <code>Power()
   *     </code> exponents
   * @return
   */
  public SymbolicPolynomial create(final IExpr exprPoly, boolean coefficient,
      boolean checkNegativeExponents, boolean coefficientListMode)
      throws ArithmeticException, ClassCastException {
    int ix = ExpVectorSymbolic.indexVar(exprPoly, getVars());
    if (ix >= 0) {
      ExpVectorSymbolic e = new ExpVectorSymbolic(vars.argSize(), ix, F.C1);
      return getOne().multiply(e);
    }
    if (exprPoly instanceof IAST) {
      final IAST ast = (IAST) exprPoly;
      SymbolicPolynomial result = getZero();
      SymbolicPolynomial p = getZero();
      if (ast.isPlus()) {
        IExpr expr = ast.arg1();
        result = create(expr, coefficient, checkNegativeExponents, coefficientListMode);
        for (int i = 2; i < ast.size(); i++) {
          expr = ast.get(i);
          p = create(expr, coefficient, checkNegativeExponents, coefficientListMode);
          result = result.sum(p);
        }
        return result;
      } else if (ast.isTimes()) {
        IExpr expr = ast.arg1();
        result = create(expr, coefficient, checkNegativeExponents, coefficientListMode);
        for (int i = 2; i < ast.size(); i++) {
          expr = ast.get(i);
          p = create(expr, coefficient, checkNegativeExponents, coefficientListMode);
          result = result.multiply(p);
        }
        return result;
      } else if (ast.isPower()) {
        final IExpr base = ast.base();
        ix = ExpVectorSymbolic.indexVar(base, getVars());
        if (ix >= 0) {
          IExpr exponent = ast.exponent();
          if (checkNegativeExponents && //
              (!exponent.isInteger() || exponent.isNegative())) {
            throw new ArithmeticException(
                "SymbolicPolynomialRing - invalid exponent: " + ast.arg2().toString());
          }
          if (exponent.isNegative() && coefficientListMode) {
            return new SymbolicPolynomial(this, ast);
          }
          ExpVectorSymbolic e = new ExpVectorSymbolic(vars.argSize(), ix, exponent);
          return getOne().multiply(e);
        }
      }
      if (coefficient) {
        return new SymbolicPolynomial(this, ast);
      }
      if (numericFunction) {
        if (ast.isNumericFunction(true)) {
          return new SymbolicPolynomial(this, ast);
        }
      }
    } else if (exprPoly instanceof ISymbol) {
      if (coefficient) {
        return new SymbolicPolynomial(this, exprPoly);
      }
      if (numericFunction) {
        if (exprPoly.isNumericFunction(true)) {
          return new SymbolicPolynomial(this, exprPoly);
        }
        throw new ClassCastException(exprPoly.toString());
      } else {
        return new SymbolicPolynomial(this, exprPoly);
      }
    } else if (exprPoly.isNumber()) {
      return new SymbolicPolynomial(this, exprPoly);
    }
    if (exprPoly.isFree(Predicates.in(vars), true)) {
      return new SymbolicPolynomial(this, exprPoly);
    }
    throw new ClassCastException(exprPoly.toString());
  }

  /**
   * Create the coefficients of the (univariate) polynomial in <code>coefficientMap</code> and
   * append non-polynomial terms to <code>restList</code>
   *
   * @param exprPoly the polynomial expression
   * @param x the variable x
   * @param coefficientMap the map of exponents to the associated coefficients
   * @param restList the terms which are non-polynomial
   * @return
   * @throws ArithmeticException
   */
  public static Map<IExpr, IExpr> create(final IExpr exprPoly, IExpr x,
      Map<IExpr, IExpr> coefficientMap, IASTAppendable restList) throws ArithmeticException {
    if (exprPoly instanceof IAST) {
      final IAST ast = (IAST) exprPoly;
      if (ast.isPlus()) {
        IExpr expr;
        for (int i = 1; i < ast.size(); i++) {
          expr = ast.get(i);
          coefficientMap = create(expr, x, coefficientMap, restList);
        }
        return coefficientMap;
      } else if (ast.isTimes()) {
        return createTimesSub(ast, x, coefficientMap, restList);
      } else if (ast.isPower()) {
        final IExpr base = ast.base();
        final IExpr exponent = ast.exponent();
        if (exponent.isFree(x)) {
          if (base.equals(x)) {
            return addCoefficient(coefficientMap, exponent, F.C1);
          }
          if (base.isFree(x)) {
            return addCoefficient(coefficientMap, F.C0, ast);
          }
        }
        restList.append(ast);
        return coefficientMap;
      }

    } else if (exprPoly.equals(x)) {
      return addCoefficient(coefficientMap, F.C1, F.C1);
    } else if (exprPoly.isNumber()) {
      return addCoefficient(coefficientMap, F.C0, exprPoly);
    }
    if (exprPoly.isFree(x, true)) {
      return addCoefficient(coefficientMap, F.C0, exprPoly);
    }
    restList.append(exprPoly);
    return coefficientMap;
  }

  private static Map<IExpr, IExpr> createTimesSub(final IAST ast, IExpr x,
      Map<IExpr, IExpr> coefficientMap, IASTAppendable restList) {
    IExpr mainExponent = F.NIL;
    IExpr expr;
    IASTAppendable times = F.TimesAlloc(ast.size());
    for (int i = 1; i < ast.size(); i++) {
      expr = ast.get(i);

      if (expr.isFree(x, true)) {
        times.append(expr);
        continue;
      } else if (expr.equals(x)) {
        final IExpr exponent = F.C1;
        if (mainExponent.isNIL()) {
          mainExponent = exponent;
          continue;
        }
      } else if (expr.isPower()) {
        final IExpr exponent = expr.exponent();
        if (exponent.isFree(x)) {
          if (expr.base().equals(x)) {
            if (mainExponent.isNIL()) {
              mainExponent = exponent;
              continue;
            }
          }
        }
      }
      restList.append(ast);
      return coefficientMap;
    }
    return addCoefficient(coefficientMap, mainExponent, times.oneIdentity1());
  }

  public static Map<IExpr, IExpr> createTimes(final IAST ast, IExpr x,
      Map<IExpr, IExpr> coefficientMap, IASTAppendable restList) {
    IExpr mainExponent = F.NIL;
    IExpr expr;
    IASTAppendable times = F.TimesAlloc(ast.size());
    for (int i = 1; i < ast.size(); i++) {
      expr = ast.get(i);

      if (expr.isFree(x, true)) {
        times.append(expr);
        continue;
      } else if (expr.equals(x)) {
        if (mainExponent.isNIL()) {
          mainExponent = F.C1;
          continue;
        }
      } else if (expr.isPower()) {
        final IExpr base = expr.base();
        final IExpr exponent = expr.exponent();
        if (exponent.isFree(x)) {
          if (base.equals(x)) {
            if (exponent.isInteger() && mainExponent.isNIL()) {
              mainExponent = exponent;
              continue;
            }
          }
        }
      }
      restList.append(expr);
    }
    return addCoefficient(coefficientMap, mainExponent, times.oneIdentity1());
  }

  /**
   * Add a coefficient to the coefficient map.
   *
   * @param coefficientMap
   * @param exponent
   * @param coefficient the coefficient
   * @return
   */
  private static Map<IExpr, IExpr> addCoefficient(Map<IExpr, IExpr> coefficientMap,
      final IExpr exponent, IExpr coefficient) {
    if (exponent.isPresent()) {
      IExpr oldCoefficient = coefficientMap.get(exponent);
      if (oldCoefficient != null) {
        if (oldCoefficient.isTimes()) {
          ((IASTAppendable) oldCoefficient).append(coefficient);
        } else {
          IASTAppendable times = F.TimesAlloc(4);
          times.append(oldCoefficient);
          times.append(coefficient);
          coefficientMap.put(exponent, times);
        }
      } else {
        coefficientMap.put(exponent, coefficient);
      }
    }
    return coefficientMap;
  }

  /**
   * Create a <code>Polynomial</code> from the given <code>exprPoly</code>.
   *
   * @param expression the expression which should be checked if it's a polynomial
   * @return <code>true</code> if the given expression is a polynomial
   */
  public boolean isPolynomial(final IExpr expression)
      throws ArithmeticException, ClassCastException {
    return isPolynomial(expression, false);
  }

  /**
   * Create a <code>Polynomial</code> from the given <code>exprPoly</code>.
   *
   * @param expression the expression which should be checked if it's a polynomial
   * @param coefficient set to <code>true</code> if called by the <code>Coefficient()</code>
   *        function
   * @return <code>true</code> if the given expression is a polynomial
   */
  public boolean isPolynomial(final IExpr expression, boolean coefficient)
      throws ArithmeticException, ClassCastException {
    for (int i = 1; i < vars.size(); i++) {
      IExpr variable = vars.get(i);

      if (variable.equals(expression)) {
        return true;
      }
      if (variable.isPower() && variable.base().equals(expression)
          && variable.exponent().isRational()) {
        IExpr expr = variable.exponent().reciprocal();
        if (!expr.isZero()) {
          if (expr.isInteger()) {
            return true;
          }
          return false;
        }
      }
    }
    if (expression instanceof IAST) {
      final IAST ast = (IAST) expression;
      if (ast.isPlus()) {
        for (int i = 1; i < ast.size(); i++) {
          if (!isPolynomial(ast.get(i), coefficient)) {
            return false;
          }
        }
        return true;
      } else if (ast.isTimes()) {
        for (int i = 1; i < ast.size(); i++) {
          if (!isPolynomial(ast.get(i), coefficient)) {
            return false;
          }
        }
        return true;
      } else if (ast.isPower()) {
        IExpr base = ast.base();
        for (int i = 1; i < vars.size(); i++) {
          IExpr variable = vars.get(i);
          if (variable.equals(base)) {
            int exponent = ast.exponent().toIntDefault();
            if (exponent < 0) {
              return false;
            }
            return true;
          } else if (variable.isPower() && variable.base().equals(ast.base())
              && variable.exponent().isRational()) {
            IExpr expr = variable.exponent().reciprocal().times(ast.exponent());
            if (!expr.isZero()) {
              if (expr.isInteger()) {
                return true;
              }
              return false;
            }
          }
        }
      }
      if (coefficient) {
        return true;
      }
      if (numericFunction) {
        if (ast.isNumericFunction(true)) {
          return true;
        }
      }
    } else if (expression instanceof ISymbol) {
      if (coefficient) {
        return true;
      }
      if (numericFunction) {
        if (expression.isNumericFunction(true)) {
          return true;
        }
        return false;
      } else {
        return true;
      }
    } else if (expression.isNumber()) {
      return true;
    }
    if (expression.isFree(Predicates.in(vars), true)) {
      return true;
    }
    return false;
  }

  /**
   * Get the String representation.
   *
   * @see java.lang.Object#toString()
   */
  // @SuppressWarnings("cast")
  @Override
  public String toString() {
    String res = null;
    if (PrettyPrint.isTrue()) { // wrong: && coFac != null
      String scf = coFac.getClass().getSimpleName();
      // if (coFac instanceof AlgebraicNumberRing) {
      // AlgebraicNumberRing an = (AlgebraicNumberRing) coFac;
      // // String[] v = an.ring.vars;
      // res = "AN[ (" + an.ring.varsToString() + ") (" + an.toString() +
      // ") ]";
      // }
      // if (coFac instanceof GenPolynomialRing) {
      // GenPolynomialRing rf = (GenPolynomialRing) coFac;
      // // String[] v = rf.vars;
      // // ExprRingFactory cf = rf.coFac;
      // // String cs;
      // // if (cf instanceof ModIntegerRing) {
      // // cs = cf.toString();
      // // } else {
      // // cs = " " + cf.getClass().getSimpleName();
      // // }
      // // res = "IntFunc" + "{" + cs + "( " + rf.varsToString() + " )" +
      // " } ";
      // res = "IntFunc" + "( " + rf.toString() + " )";
      // }
      // if (((Object) coFac) instanceof ModIntegerRing) {
      // ModIntegerRing mn = (ModIntegerRing) ((Object) coFac);
      // res = "Mod " + mn.getModul() + " ";
      // }
      // if (res == null) {
      res = coFac.toString();
      if (res.matches("[0-9].*")) {
        res = scf;
      }
      // }
      res += "( " + varsToString() + " ) " + tord.toString() + " ";
    } else {
      res = this.getClass().getSimpleName() + "[ " + coFac.toString() + " ";
      // + coFac.getClass().getSimpleName();
      // if (coFac instanceof AlgebraicNumberRing) {
      // AlgebraicNumberRing an = (AlgebraicNumberRing) coFac;
      // res = "AN[ (" + an.ring.varsToString() + ") (" + an.modul + ")
      // ]";
      // }
      // if (coFac instanceof GenPolynomialRing) {
      // GenPolynomialRing rf = (GenPolynomialRing) coFac;
      // // String[] v = rf.vars;
      // // ExprRingFactory cf = rf.coFac;
      // // String cs;
      // // if (cf instanceof ModIntegerRing) {
      // // cs = cf.toString();
      // // } else {
      // // cs = " " + cf.getClass().getSimpleName();
      // // }
      // // res = "IntFunc{ " + cs + "( " + rf.varsToString() + " )" + " }
      // ";
      // res = "IntFunc" + "( " + rf.toString() + " )";
      // }
      // if (((Object) coFac) instanceof ModIntegerRing) {
      // ModIntegerRing mn = (ModIntegerRing) ((Object) coFac);
      // res = "Mod " + mn.getModul() + " ";
      // }
      // res += ", " + nvar + ", " + tord.toString() + ", " +
      // varsToString() + ", " + partial + " ]";
      res += "( " + varsToString() + " ) " + tord.toString() + " ]";
    }
    return res;
  }

  /**
   * Get a scripting compatible string representation.
   *
   * @return script compatible representation for this Element.
   * @see edu.jas.structure.Element#toScript()
   */
  @Override
  public String toScript() {
    StringBuilder s = new StringBuilder();
    switch (Scripting.getLang()) {
      case Ruby:
        s.append("PolyRing.new(");
        break;
      case Python:
      default:
        s.append("PolyRing(");
    }
    // if (coFac instanceof RingElem) {
    // s.append(coFac.toScriptFactory());
    // } else {
    s.append(coFac.toScript().trim());
    // }
    s.append(",\"" + varsToString() + "\"");
    String to = tord.toString();
    if (tord.getEvord() == ExprTermOrder.INVLEX) {
      to = ",PolyRing.lex";
    }
    if (tord.getEvord() == ExprTermOrder.IGRLEX) {
      to = ",PolyRing.grad";
    }
    s.append(to);
    s.append(")");
    return s.toString();
  }

  /**
   * Get a scripting compatible string representation of an ExpVectorLong of this ring.
   *
   * @param e exponent vector
   * @return script compatible representation for the ExpVectorLong.
   */
  public String toScript(ExpVectorSymbolic e) {
    if (vars != null) {
      return e.toScript(vars);
    }
    return e.toScript();
  }

  /**
   * Comparison with any other object.
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }
    if (!(other instanceof SymbolicPolynomialRing)) {
      return false;
    }
    SymbolicPolynomialRing oring = (SymbolicPolynomialRing) other;
    if (nvar != oring.nvar) {
      return false;
    }
    if (!coFac.equals(oring.coFac)) {
      return false;
    }
    if (!tord.equals(oring.tord)) {
      return false;
    }
    // same variables required ?
    if (!vars.equals(oring.vars)) {
      return false;
    }
    return true;
  }

  /**
   * Hash code for this polynomial ring.
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    int h;
    h = (nvar << 27);
    h += (coFac.hashCode() << 11);
    h += tord.hashCode();
    return h;
  }

  /**
   * Get the number of polynomial creations.
   *
   * @return creations.
   */
  // public int getCreations() {
  // return creations;
  // }

  /**
   * Get the variable names.
   *
   * @return vars.
   */
  public IAST getVars() {
    return vars; // .copyAppendable();
  }

  /**
   * Set the variable names.
   *
   * @return old vars.
   */
  public IAST setVars(IAST v) {
    if (v.argSize() != nvar) {
      throw new IllegalArgumentException(
          "v not matching number of variables: " + v.toString() + ", nvar " + nvar);
    }
    IAST t = vars;
    vars = v.copyAppendable(); // Arrays.copyOf(v, v.length); // > Java-5
    return t;
  }

  /**
   * Get a String representation of the variable names.
   *
   * @return names seperated by commas.
   */
  public String varsToString() {
    if (vars == null) {
      return "#" + nvar;
    }
    // return Arrays.toString(vars);
    return ExpVectorLong.varsToString(vars);
  }

  /**
   * Get the zero element from the coefficients.
   *
   * @return 0 as C.
   */
  public IExpr getZEROCoefficient() {
    return coFac.getZERO();
  }

  /**
   * Get the one element from the coefficients.
   *
   * @return 1 as C.
   */
  public IExpr getONECoefficient() {
    return coFac.getONE();
  }

  /**
   * Get the zero element.
   *
   * @return 0 as GenPolynomial.
   */
  public SymbolicPolynomial getZero() {
    return ZERO;
  }

  /**
   * Get the one element.
   *
   * @return 1 as GenPolynomial.
   */
  public SymbolicPolynomial getOne() {
    return ONE;
  }

  /**
   * Query if this ring is commutative.
   *
   * @return true if this ring is commutative, else false.
   */
  @Override
  public boolean isCommutative() {
    return coFac.isCommutative();
  }

  /**
   * Query if this ring is associative.
   *
   * @return true if this ring is associative, else false.
   */
  @Override
  public boolean isAssociative() {
    return coFac.isAssociative();
  }

  /**
   * Query if this ring is a field.
   *
   * @return false.
   */
  @Override
  public boolean isField() {
    if (isField > 0) {
      return true;
    }
    if (isField == 0) {
      return false;
    }
    if (coFac.isField() && nvar == 0) {
      isField = 1;
      return true;
    }
    isField = 0;
    return false;
  }

  /**
   * Characteristic of this ring.
   *
   * @return characteristic of this ring.
   */
  @Override
  public java.math.BigInteger characteristic() {
    return coFac.characteristic();
  }

  /**
   * Get a (constant) GenPolynomial&lt;C&gt; element from a coefficient value.
   *
   * @param a coefficient.
   * @return a GenPolynomial&lt;C&gt;.
   */
  public SymbolicPolynomial valueOf(IExpr a) {
    return new SymbolicPolynomial(this, a);
  }

  /**
   * Get a GenPolynomial&lt;C&gt; element from an exponent vector.
   *
   * @param e exponent vector.
   * @return a GenPolynomial&lt;C&gt;.
   */
  public SymbolicPolynomial valueOf(ExpVectorSymbolic e) {
    return new SymbolicPolynomial(this, coFac.getONE(), e);
  }

  /**
   * Get a GenPolynomial&lt;C&gt; element from a coeffcient and an exponent vector.
   *
   * @param a coefficient.
   * @param e exponent vector.
   * @return a GenPolynomial&lt;C&gt;.
   */
  public SymbolicPolynomial valueOf(IExpr a, ExpVectorSymbolic e) {
    return new SymbolicPolynomial(this, a, e);
  }

  /**
   * Get a (constant) GenPolynomial&lt;C&gt; element from a long value.
   *
   * @param a long.
   * @return a GenPolynomial&lt;C&gt;.
   */
  @Override
  public SymbolicPolynomial fromInteger(long a) {
    return new SymbolicPolynomial(this, coFac.fromInteger(a), evzero);
  }

  /**
   * Get a (constant) GenPolynomial&lt;C&gt; element from a BigInteger value.
   *
   * @param a BigInteger.
   * @return a GenPolynomial&lt;C&gt;.
   */
  @Override
  public SymbolicPolynomial fromInteger(BigInteger a) {
    return new SymbolicPolynomial(this, coFac.fromInteger(a), evzero);
  }

  /**
   * Copy polynomial c.
   *
   * @param c
   * @return a copy of c.
   */
  @Override
  public SymbolicPolynomial copy(SymbolicPolynomial c) {
    return new SymbolicPolynomial(this, c.val);
  }

  /**
   * Generate univariate polynomial in a given variable.
   *
   * @param i the index of the variable.
   * @return X_i as univariate polynomial.
   */
  public SymbolicPolynomial univariate(int i) {
    return univariate(0, i, F.C1);
  }

  /**
   * Generate univariate polynomial in a given variable with given exponent.
   *
   * @param i the index of the variable.
   * @param e the exponent of the variable.
   * @return X_i^e as univariate polynomial.
   */
  public SymbolicPolynomial univariate(int i, IExpr e) {
    return univariate(0, i, e);
  }

  /**
   * Generate univariate polynomial in a given variable with given exponent.
   *
   * @param modv number of module variables.
   * @param i the index of the variable.
   * @param e the exponent of the variable.
   * @return X_i^e as univariate polynomial.
   */
  public SymbolicPolynomial univariate(int modv, int i, IExpr e) {
    SymbolicPolynomial p = getZero();
    int r = nvar - modv;
    if (0 <= i && i < r) {
      IExpr one = coFac.getONE();
      ExpVectorSymbolic f = new ExpVectorSymbolic(r, i, e);
      if (modv > 0) {
        f = f.extend(modv, 0, F.C0);
      }
      p = p.sum(one, f);
    }
    return p;
  }

  /**
   * Get the generating elements excluding the generators for the coefficient ring.
   *
   * @return a list of generating elements for this ring.
   */
  public List<SymbolicPolynomial> getGenerators() {
    List<? extends SymbolicPolynomial> univs = univariateList();
    List<SymbolicPolynomial> gens = new ArrayList<SymbolicPolynomial>(univs.size() + 1);
    gens.add(getOne());
    gens.addAll(univs);
    return gens;
  }

  /**
   * Get a list of the generating elements.
   *
   * @return list of generators for the algebraic structure.
   * @see edu.jas.structure.ElemFactory#generators()
   */
  @Override
  public List<SymbolicPolynomial> generators() {
    List<? extends IExpr> cogens = coFac.generators();
    List<? extends SymbolicPolynomial> univs = univariateList();
    List<SymbolicPolynomial> gens = new ArrayList<SymbolicPolynomial>(univs.size() + cogens.size());
    for (IExpr c : cogens) {
      gens.add(getOne().multiply(c));
    }
    gens.addAll(univs);
    return gens;
  }

  /**
   * Get a list of the generating elements excluding the module variables.
   *
   * @param modv number of module variables
   * @return list of generators for the polynomial ring.
   */
  public List<SymbolicPolynomial> generators(int modv) {
    List<? extends IExpr> cogens = coFac.generators();
    List<? extends SymbolicPolynomial> univs = univariateList(modv);
    List<SymbolicPolynomial> gens = new ArrayList<SymbolicPolynomial>(univs.size() + cogens.size());
    for (IExpr c : cogens) {
      gens.add(getOne().multiply(c));
    }
    gens.addAll(univs);
    return gens;
  }

  /**
   * Is this structure finite or infinite.
   *
   * @return true if this structure is finite, else false.
   * @see edu.jas.structure.ElemFactory#isFinite()
   */
  @Override
  public boolean isFinite() {
    return (nvar == 0) && coFac.isFinite();
  }

  /**
   * Generate list of univariate polynomials in all variables.
   *
   * @return List(X_1,...,X_n) a list of univariate polynomials.
   */
  public List<? extends SymbolicPolynomial> univariateList() {
    return univariateList(0, F.C1);
  }

  /**
   * Generate list of univariate polynomials in all variables.
   *
   * @param modv number of module variables.
   * @return List(X_1,...,X_n) a list of univariate polynomials.
   */
  public List<? extends SymbolicPolynomial> univariateList(int modv) {
    return univariateList(modv, F.C1);
  }

  /**
   * Generate list of univariate polynomials in all variables with given exponent.
   *
   * @param modv number of module variables.
   * @param e the exponent of the variables.
   * @return List(X_1^e,...,X_n^e) a list of univariate polynomials.
   */
  public List<? extends SymbolicPolynomial> univariateList(int modv, IExpr e) {
    List<SymbolicPolynomial> pols = new ArrayList<SymbolicPolynomial>(nvar);
    int nm = nvar - modv;
    for (int i = 0; i < nm; i++) {
      SymbolicPolynomial p = univariate(modv, nm - 1 - i, e);
      pols.add(p);
    }
    return pols;
  }

  /**
   * Extend variables. Used e.g. in module embedding. Extend number of variables by length(vn).
   *
   * @param vn names for extended variables.
   * @return extended polynomial ring factory.
   */
  public SymbolicPolynomialRing extend(IAST vn) {
    if (vn == null || vars == null) {
      throw new IllegalArgumentException("vn and vars may not be null");
    }
    int i = vn.argSize();
    IASTAppendable v = vars.copyAppendable();
    v.appendArgs(vn);
    // for (int k = 0; k < vars.length; k++) {
    // v[k] = vars[k];
    // }
    // for (int k = 0; k < vn.length; k++) {
    // v[vars.length + k] = vn[k];
    // }
    SymbolicTermOrder to = tord.extend(nvar, i);
    SymbolicPolynomialRing pfac = new SymbolicPolynomialRing(coFac, v, nvar + i, to);
    return pfac;
  }

  /**
   * Get PolynomialComparator.
   *
   * @return polynomial comparator.
   */
  public SymbolicPolynomialComparator getComparator() {
    return new SymbolicPolynomialComparator(tord, false);
  }

  /**
   * Get PolynomialComparator.
   *
   * @param rev for reverse comparator.
   * @return polynomial comparator.
   */
  public SymbolicPolynomialComparator getComparator(boolean rev) {
    return new SymbolicPolynomialComparator(tord, rev);
  }

  /**
   * Permute variable names.
   *
   * @param vars variable names.
   * @param P permutation.
   * @return P(vars).
   */
  public static String[] permuteVars(List<Integer> P, String[] vars) {
    if (vars == null || vars.length <= 1) {
      return vars;
    }
    String[] b = new String[vars.length];
    int j = 0;
    for (Integer i : P) {
      b[j++] = vars[i];
    }
    return b;
  }

  /**
   * Get a GenPolynomial iterator.
   *
   * @return an iterator over all polynomials.
   */
  public Iterator<SymbolicPolynomial> iterator() {
    if (coFac.isFinite()) {
      return new GenPolynomialIterator(this);
    }
    // logger.warn("ring of coefficients " + coFac + " is infinite, constructing iterator only over
    // monomials");
    return new GenPolynomialMonomialIterator(this);
    // throw new IllegalArgumentException("only for finite iterable
    // coefficients implemented");
  }

  @Override
  public SymbolicPolynomial getZERO() {
    return getZero();
  }

  @Override
  public SymbolicPolynomial random(int n) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SymbolicPolynomial random(int n, Random random) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SymbolicPolynomial parse(String s) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SymbolicPolynomial parse(Reader r) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SymbolicPolynomial getONE() {
    return getOne();
  }

  /** Polynomial iterator. */
  private static class GenPolynomialIterator implements Iterator<SymbolicPolynomial> {

    /** data structure. */
    final SymbolicPolynomialRing ring;

    final Iterator<List<IExpr>> eviter;

    final List<ExpVectorSymbolic> powers;

    final List<Iterable<IExpr>> coeffiter;

    Iterator<List<IExpr>> itercoeff;

    SymbolicPolynomial current;

    /** Polynomial iterator constructor. */
    @SuppressWarnings("unchecked")
    public GenPolynomialIterator(SymbolicPolynomialRing fac) {
      ring = fac;
      IExprIterable li = new IExprIterable();
      li.setNonNegativeIterator();
      List<Iterable<IExpr>> tlist = new ArrayList<Iterable<IExpr>>(ring.nvar);
      for (int i = 0; i < ring.nvar; i++) {
        tlist.add(li);
      }
      CartesianProductInfinite<IExpr> ei = new CartesianProductInfinite<IExpr>(tlist);
      eviter = ei.iterator();
      ExprRingFactory cf = ring.coFac;
      coeffiter = new ArrayList<Iterable<IExpr>>();
      if (cf instanceof Iterable && cf.isFinite()) {
        Iterable<IExpr> cfi = (Iterable<IExpr>) cf;
        coeffiter.add(cfi);
      } else {
        throw new IllegalArgumentException("only for finite iterable coefficients implemented");
      }
      CartesianProduct<IExpr> tuples = new CartesianProduct<IExpr>(coeffiter);
      itercoeff = tuples.iterator();
      powers = new ArrayList<ExpVectorSymbolic>();
      ExpVectorSymbolic e = ExpVectorSymbolic.create(eviter.next());
      powers.add(e);
      List<IExpr> c = itercoeff.next();
      current = new SymbolicPolynomial(ring, c.get(0), e);
    }

    /**
     * Test for availability of a next element.
     *
     * @return true if the iteration has more elements, else false.
     */
    @Override
    public boolean hasNext() {
      return true;
    }

    /**
     * Get next polynomial.
     *
     * @return next polynomial.
     */
    @Override
    public synchronized SymbolicPolynomial next() {
      SymbolicPolynomial res = current;
      if (!itercoeff.hasNext()) {
        ExpVectorSymbolic e = ExpVectorSymbolic.create(eviter.next());
        powers.add(0, e); // add new ev at beginning
        if (coeffiter.size() == 1) { // shorten frist iterator by one
          // element
          coeffiter.add(coeffiter.get(0));
          Iterable<IExpr> it = coeffiter.get(0);
          List<IExpr> elms = new ArrayList<IExpr>();
          for (IExpr elm : it) {
            elms.add(elm);
          }
          elms.remove(0);
          coeffiter.set(0, elms);
        } else {
          coeffiter.add(coeffiter.get(1));
        }
        CartesianProduct<IExpr> tuples = new CartesianProduct<IExpr>(coeffiter);
        itercoeff = tuples.iterator();
      }
      List<IExpr> coeffs = itercoeff.next();
      // while ( coeffs.get(0).isZERO() ) {
      // coeffs = itercoeff.next(); // skip tuples with zero in first
      // component
      // }
      SymbolicPolynomial pol = ring.getZero().copy();
      int i = 0;
      for (ExpVectorSymbolic f : powers) {
        IExpr c = coeffs.get(i++);
        if (c.isZERO()) {
          continue;
        }
        if (pol.val.get(f) != null) {
          throw new RuntimeException("error in iterator");
        }
        pol.doPutToMap(f, c);
      }
      current = pol;
      return res;
    }

    /** Remove an element if allowed. */
    @Override
    public void remove() {
      throw new UnsupportedOperationException("cannnot remove elements");
    }
  }

  /** Polynomial monomial iterator. */
  private static class GenPolynomialMonomialIterator implements Iterator<SymbolicPolynomial> {

    /** data structure. */
    final SymbolicPolynomialRing ring;

    final Iterator<List<IExpr>> iter;

    SymbolicPolynomial current;

    /** Polynomial iterator constructor. */
    @SuppressWarnings("unchecked")
    public GenPolynomialMonomialIterator(SymbolicPolynomialRing fac) {
      ring = fac;
      LongIterable li = new LongIterable();
      li.setNonNegativeIterator();
      List<Iterable<Long>> tlist = new ArrayList<Iterable<Long>>(ring.nvar);
      for (int i = 0; i < ring.nvar; i++) {
        tlist.add(li);
      }
      CartesianProductInfinite<Long> ei = new CartesianProductInfinite<Long>(tlist);
      // Iterator<List<Long>> eviter = ei.iterator();

      ExprRingFactory cf = ring.coFac;
      Iterable<IExpr> coeffiter;
      if (cf instanceof Iterable && !cf.isFinite()) {
        Iterable<IExpr> cfi = (Iterable<IExpr>) cf;
        coeffiter = cfi;
      } else {
        throw new IllegalArgumentException("only for infinite iterable coefficients implemented");
      }

      // Cantor iterator for exponents and coeffcients
      List<Iterable> eci = new ArrayList<Iterable>(2); // no type parameter
      eci.add(ei);
      eci.add(coeffiter);
      CartesianProductInfinite ecp = new CartesianProductInfinite(eci);
      iter = ecp.iterator();

      List<IExpr> ec = iter.next();
      List<IExpr> ecl = (List<IExpr>) ec.get(0);
      IExpr c = ec.get(1); // zero
      ExpVectorSymbolic e = ExpVectorSymbolic.create(ecl);
      current = new SymbolicPolynomial(ring, c, e);
    }

    /**
     * Test for availability of a next element.
     *
     * @return true if the iteration has more elements, else false.
     */
    @Override
    public boolean hasNext() {
      return true;
    }

    /**
     * Get next polynomial.
     *
     * @return next polynomial.
     */
    @Override
    @SuppressWarnings("unchecked")
    public synchronized SymbolicPolynomial next() {
      SymbolicPolynomial res = current;

      List<IExpr> ec = iter.next();
      IExpr c = ec.get(1);
      while (c.isZERO()) { // zero already done in first next
        ec = iter.next();
        c = ec.get(1);
      }
      List<IExpr> ecl = (List<IExpr>) ec.get(0);
      ExpVectorSymbolic e = ExpVectorSymbolic.create(ecl);
      current = new SymbolicPolynomial(ring, c, e);

      return res;
    }

    /** Remove an element if allowed. */
    @Override
    public void remove() {
      throw new UnsupportedOperationException("cannnot remove elements");
    }
  }
}
