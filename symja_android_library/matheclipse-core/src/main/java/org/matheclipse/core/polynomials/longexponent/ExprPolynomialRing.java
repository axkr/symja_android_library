package org.matheclipse.core.polynomials.longexponent;

import java.io.Reader;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
// import
// org.matheclipse.core.polynomials.longexponent.GenPolynomialIterator.GenPolynomialMonomialIterator;
import edu.jas.kern.PrettyPrint;
import edu.jas.kern.Scripting;
import edu.jas.structure.RingFactory;
import edu.jas.util.CartesianProduct;
import edu.jas.util.CartesianProductInfinite;
import edu.jas.util.LongIterable;

/**
 * GenPolynomialRing generic polynomial factory implementing ExprRingFactory; Factory for n-variate
 * ordered polynomials over C. Almost immutable object, except variable names.
 */
public class ExprPolynomialRing implements RingFactory<ExprPolynomial> {
  /** Polynomial monomial iterator. */
  private static class GenPolynomialMonomialIterator implements Iterator<ExprPolynomial> {

    /** data structure. */
    final ExprPolynomialRing ring;

    final Iterator<List<IExpr>> iter;

    ExprPolynomial current;

    /** Polynomial iterator constructor. */
    @SuppressWarnings("unchecked")
    public GenPolynomialMonomialIterator(ExprPolynomialRing fac) {
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
      List<Long> ecl = (List<Long>) ec.get(0);
      IExpr c = ec.get(1); // zero
      ExpVectorLong e = ExpVectorLong.create(ecl);
      // System.out.println("exp = " + e);
      // System.out.println("coeffs = " + c);
      current = new ExprPolynomial(ring, c, e);
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
    public synchronized ExprPolynomial next() {
      ExprPolynomial res = current;

      List<IExpr> ec = iter.next();
      IExpr c = ec.get(1);
      while (c.isZERO()) { // zero already done in first next
        ec = iter.next();
        c = ec.get(1);
      }
      List<Long> ecl = (List<Long>) ec.get(0);
      ExpVectorLong e = ExpVectorLong.create(ecl);
      // System.out.println("exp = " + e);
      // System.out.println("coeffs = " + c);
      current = new ExprPolynomial(ring, c, e);

      return res;
    }

    /** Remove an element if allowed. */
    @Override
    public void remove() {
      throw new UnsupportedOperationException("cannnot remove elements");
    }
  }

  /** Comparator for polynomials. */
  private static class ExprPolynomialComparator
      implements Serializable, Comparator<ExprPolynomial> {

    /** */
    private static final long serialVersionUID = -2427163728878196089L;

    public final ExprTermOrder tord;

    public final boolean reverse;

    /**
     * Constructor.
     *
     * @param t TermOrder.
     * @param reverse flag if reverse ordering is requested.
     */
    public ExprPolynomialComparator(ExprTermOrder t, boolean reverse) {
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
    public int compare(ExprPolynomial p1, ExprPolynomial p2) {
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
      if (o instanceof ExprPolynomialComparator) {
        ExprPolynomialComparator pc = (ExprPolynomialComparator) o;
        return tord.equals(pc.tord);
      }
      return false;
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

  /** */
  private static final long serialVersionUID = -6136386786501333693L;

  /** The factory for the coefficients. */
  public final ExprRingFactory coFac;

  /** The number of variables. */
  public final int nvar;

  /** The term order. */
  public final ExprTermOrder tord;

  /** True for partially reversed variables. */
  protected boolean partial;

  /** The names of the variables. This value can be modified. */
  protected IAST vars;

  /** The names of all known variables. */
  private static Set<IExpr> knownVars = new HashSet<IExpr>();

  /** The constant polynomial 0 for this ring. */
  public final ExprPolynomial ZERO;

  /** The constant polynomial 1 for this ring. */
  public final ExprPolynomial ONE;

  /** The constant exponent vector 0 for this ring. */
  public final ExpVectorLong evzero;

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
  public ExprPolynomialRing(IAST listOfVariables) {
    this(
        ExprRingFactory.CONST,
        listOfVariables,
        listOfVariables.argSize(),
        ExprTermOrderByName.Lexicographic);
  }

  /**
   * The constructor creates a polynomial factory object.
   *
   * @param listOfVariables names for the variables.
   * @param t a term order.
   */
  public ExprPolynomialRing(IAST listOfVariables, ExprTermOrder t) {
    this(ExprRingFactory.CONST, listOfVariables, listOfVariables.argSize(), t);
  }

  /**
   * The constructor creates a polynomial factory object.
   *
   * @param symbol name of a variable.
   */
  public ExprPolynomialRing(ISymbol symbol) {
    this(ExprRingFactory.CONST, F.List(symbol), 1, ExprTermOrderByName.Lexicographic);
  }

  /**
   * The constructor creates a polynomial factory object.
   *
   * @param symbol name of a variable.
   * @param t a term order.
   */
  public ExprPolynomialRing(ISymbol symbol, ExprTermOrder t) {
    this(ExprRingFactory.CONST, F.List(symbol), 1, t);
  }

  /**
   * The constructor creates a polynomial factory object.
   *
   * @param cf factory for coefficients of type C.
   * @param listOfVariables names for the variables.
   */
  public ExprPolynomialRing(ExprRingFactory cf, IAST listOfVariables) {
    this(cf, listOfVariables, listOfVariables.argSize(), ExprTermOrderByName.Lexicographic);
  }

  /**
   * The constructor creates a polynomial factory object.
   *
   * @param cf factory for coefficients of type C.
   * @param listOfVariables names for the variables.
   * @param t a term order.
   */
  public ExprPolynomialRing(ExprRingFactory cf, IAST listOfVariables, ExprTermOrder t) {
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
  private ExprPolynomialRing(ExprRingFactory cf, IAST listOfVariables, int n, ExprTermOrder t) {
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
  public ExprPolynomialRing(
      ExprRingFactory cf, IAST listOfVariables, int n, ExprTermOrder t, boolean numericFunction) {
    coFac = cf;
    nvar = n;
    tord = t;
    partial = false;
    // if (v == null) {
    // vars = null;
    // } else {
    vars = listOfVariables.copyAppendable(); // Arrays.copyOf(v, v.length); // >
    // Java-5
    // }
    ZERO = new ExprPolynomial(this);
    IExpr coeff = coFac.getONE();
    evzero = new ExpVectorLong(nvar);
    this.numericFunction = numericFunction;
    ONE = new ExprPolynomial(this, coeff, evzero);
    // if (vars == null) {
    // if (PrettyPrint.isTrue()) {
    // vars = newVars("x", nvar);
    // }
    // } else {
    if (vars.argSize() != nvar) {
      throw new IllegalArgumentException("incompatible variable size " + vars.size() + ", " + nvar);
    }
    addVars(vars);
    // }
  }

  /**
   * The constructor creates a polynomial factory object with the the same term order, number of
   * variables and variable names as the given polynomial factory, only the coefficient factories
   * differ.
   *
   * @param cf factory for coefficients of type C.
   * @param o other polynomial ring.
   */
  public ExprPolynomialRing(ExprRingFactory cf, ExprPolynomialRing o) {
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
  public ExprPolynomialRing(ExprPolynomialRing o, ExprTermOrder to) {
    this(o.coFac, o.vars, o.nvar, to);
  }

  /**
   * Copy this factory.
   *
   * @return a clone of this.
   */
  public ExprPolynomialRing copy() {
    return new ExprPolynomialRing(coFac, this);
  }

  /**
   * Create a <code>Polynomial</code> from the given <code>exprPoly</code>.
   *
   * @param exprPoly the polynomial expression
   * @return
   */
  public ExprPolynomial create(final IExpr exprPoly)
      throws ArithmeticException, JASConversionException {
    return create(exprPoly, false, true, false);
  }

  /**
   * Create a <code>Polynomial</code> from the given <code>exprPoly</code>.
   *
   * @param exprPoly the polynomial expression
   * @param coefficient set to <code>true</code> if called by the <code>Coefficient()</code>
   *     function
   * @param checkNegativeExponents if <code>true</code> don't allow negative exponents and throw an
   *     ArithmeticException
   * @param coefficientListMode if in coefficient list mode don't collect negative <code>Power()
   *     </code> exponents
   * @return
   */
  public ExprPolynomial create(
      final IExpr exprPoly,
      boolean coefficient,
      boolean checkNegativeExponents,
      boolean coefficientListMode)
      throws ArithmeticException, JASConversionException {
    int ix = ExpVectorLong.indexVar(exprPoly, getVars());
    if (ix >= 0) {
      ExpVectorLong e = new ExpVectorLong(vars.argSize(), ix, 1L);
      return getOne().multiply(e);
    }
    if (exprPoly instanceof IAST) {
      final IAST ast = (IAST) exprPoly;
      if (ast.isDirectedInfinity()) {
        throw new JASConversionException();
      }
      ExprPolynomial result = getZero();
      ExprPolynomial p = getZero();
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
        ix = ExpVectorLong.indexVar(base, getVars());
        if (ix >= 0) {
          int exponent = ast.exponent().toIntDefault();
          if (checkNegativeExponents && exponent < 0) {
            throw new ArithmeticException(
                "JASConvert:expr2Poly - invalid exponent: " + ast.arg2().toString());
          }
          if (exponent < 0 && coefficientListMode) {
            return new ExprPolynomial(this, ast);
          }
          if (exponent == Integer.MIN_VALUE) {
            return new ExprPolynomial(this, ast);
          }
          ExpVectorLong e = new ExpVectorLong(vars.argSize(), ix, exponent);
          return getOne().multiply(e);
        }
      }
      if (coefficient) {
        return new ExprPolynomial(this, ast);
      }
      if (numericFunction) {
        if (ast.isNumericFunction(true)) {
          return new ExprPolynomial(this, ast);
        }
      }
    } else if (exprPoly instanceof ISymbol) {
      if (exprPoly.isIndeterminate()) {
        throw new JASConversionException();
      }
      if (coefficient) {
        return new ExprPolynomial(this, exprPoly);
      }
      if (numericFunction) {
        if (exprPoly.isNumericFunction(true)) {
          return new ExprPolynomial(this, exprPoly);
        }
        throw new JASConversionException();
      } else {
        return new ExprPolynomial(this, exprPoly);
      }
    } else if (exprPoly.isNumber()) {
      return new ExprPolynomial(this, exprPoly);
    }
    if (exprPoly.isFree(Predicates.in(vars), true) && !(exprPoly instanceof IDataExpr)) {
      return new ExprPolynomial(this, exprPoly);
    }
    throw new JASConversionException();
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
  public static Map<IExpr, IExpr> create(
      final IExpr exprPoly, IExpr x, Map<IExpr, IExpr> coefficientMap, IASTAppendable restList)
      throws ArithmeticException {
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

  private static Map<IExpr, IExpr> createTimesSub(
      final IAST ast, IExpr x, Map<IExpr, IExpr> coefficientMap, IASTAppendable restList) {
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
        if (!mainExponent.isPresent()) {
          mainExponent = exponent;
          continue;
        }
      } else if (expr.isPower()) {
        final IExpr exponent = expr.exponent();
        if (exponent.isFree(x)) {
          if (expr.base().equals(x)) {
            if (!mainExponent.isPresent()) {
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

  public static Map<IExpr, IExpr> createTimes(
      final IAST ast, IExpr x, Map<IExpr, IExpr> coefficientMap, IASTAppendable restList) {
    IExpr mainExponent = F.NIL;
    IExpr expr;
    IASTAppendable times = F.TimesAlloc(ast.size());
    for (int i = 1; i < ast.size(); i++) {
      expr = ast.get(i);

      if (expr.isFree(x, true)) {
        times.append(expr);
        continue;
      } else if (expr.equals(x)) {
        if (!mainExponent.isPresent()) {
          mainExponent = F.C1;
          continue;
        }
      } else if (expr.isPower()) {
        final IExpr base = expr.base();
        final IExpr exponent = expr.exponent();
        if (exponent.isFree(x)) {
          if (base.equals(x)) {
            if (exponent.isInteger() && !mainExponent.isPresent()) {
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
  private static Map<IExpr, IExpr> addCoefficient(
      Map<IExpr, IExpr> coefficientMap, final IExpr exponent, IExpr coefficient) {
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
      throws ArithmeticException, JASConversionException {
    return isPolynomial(expression, false);
  }

  /**
   * Create a <code>Polynomial</code> from the given <code>exprPoly</code>.
   *
   * @param expression the expression which should be checked if it's a polynomial
   * @param coefficient set to <code>true</code> if called by the <code>Coefficient()</code>
   *     function
   * @return <code>true</code> if the given expression is a polynomial
   */
  public boolean isPolynomial(final IExpr expression, boolean coefficient)
      throws ArithmeticException, JASConversionException {
    for (int i = 1; i < vars.size(); i++) {
      IExpr variable = vars.get(i);

      if (variable.equals(expression)) {
        return true;
      }
      if (variable.isPower()
          && variable.base().equals(expression)
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
          } else if (variable.isPower()
              && variable.base().equals(ast.base())
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
      if (res == null) {
        res = coFac.toString();
        if (res.matches("[0-9].*")) {
          res = scf;
        }
      }
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
  public String toScript(ExpVectorLong e) {
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
    if (!(other instanceof ExprPolynomialRing)) {
      return false;
    }
    ExprPolynomialRing oring = (ExprPolynomialRing) other;
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
  public ExprPolynomial getZero() {
    return ZERO;
  }

  /**
   * Get the one element.
   *
   * @return 1 as GenPolynomial.
   */
  public ExprPolynomial getOne() {
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
  public ExprPolynomial valueOf(IExpr a) {
    return new ExprPolynomial(this, a);
  }

  /**
   * Get a GenPolynomial&lt;C&gt; element from an exponent vector.
   *
   * @param e exponent vector.
   * @return a GenPolynomial&lt;C&gt;.
   */
  public ExprPolynomial valueOf(ExpVectorLong e) {
    return new ExprPolynomial(this, coFac.getONE(), e);
  }

  /**
   * Get a GenPolynomial&lt;C&gt; element from a coeffcient and an exponent vector.
   *
   * @param a coefficient.
   * @param e exponent vector.
   * @return a GenPolynomial&lt;C&gt;.
   */
  public ExprPolynomial valueOf(IExpr a, ExpVectorLong e) {
    return new ExprPolynomial(this, a, e);
  }

  /**
   * Get a (constant) GenPolynomial&lt;C&gt; element from a long value.
   *
   * @param a long.
   * @return a GenPolynomial&lt;C&gt;.
   */
  @Override
  public ExprPolynomial fromInteger(long a) {
    return new ExprPolynomial(this, coFac.fromInteger(a), evzero);
  }

  /**
   * Get a (constant) GenPolynomial&lt;C&gt; element from a BigInteger value.
   *
   * @param a BigInteger.
   * @return a GenPolynomial&lt;C&gt;.
   */
  @Override
  public ExprPolynomial fromInteger(BigInteger a) {
    return new ExprPolynomial(this, coFac.fromInteger(a), evzero);
  }

  /**
   * Copy polynomial c.
   *
   * @param c
   * @return a copy of c.
   */
  @Override
  public ExprPolynomial copy(ExprPolynomial c) {
    // System.out.println("GP copy = " + this);
    return new ExprPolynomial(this, c.val);
  }

  /**
   * Generate univariate polynomial in a given variable.
   *
   * @param i the index of the variable.
   * @return X_i as univariate polynomial.
   */
  public ExprPolynomial univariate(int i) {
    return univariate(0, i, 1L);
  }

  /**
   * Generate univariate polynomial in a given variable with given exponent.
   *
   * @param i the index of the variable.
   * @param e the exponent of the variable.
   * @return X_i^e as univariate polynomial.
   */
  public ExprPolynomial univariate(int i, long e) {
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
  public ExprPolynomial univariate(int modv, int i, long e) {
    ExprPolynomial p = getZero();
    int r = nvar - modv;
    if (0 <= i && i < r) {
      IExpr one = coFac.getONE();
      ExpVectorLong f = new ExpVectorLong(r, i, e);
      if (modv > 0) {
        f = f.extend(modv, 0, 0l);
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
  public List<ExprPolynomial> getGenerators() {
    List<? extends ExprPolynomial> univs = univariateList();
    List<ExprPolynomial> gens = new ArrayList<ExprPolynomial>(univs.size() + 1);
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
  public List<ExprPolynomial> generators() {
    List<? extends IExpr> cogens = coFac.generators();
    List<? extends ExprPolynomial> univs = univariateList();
    List<ExprPolynomial> gens = new ArrayList<ExprPolynomial>(univs.size() + cogens.size());
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
  public List<ExprPolynomial> generators(int modv) {
    List<? extends IExpr> cogens = coFac.generators();
    List<? extends ExprPolynomial> univs = univariateList(modv);
    List<ExprPolynomial> gens = new ArrayList<ExprPolynomial>(univs.size() + cogens.size());
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
  public List<? extends ExprPolynomial> univariateList() {
    return univariateList(0, 1L);
  }

  /**
   * Generate list of univariate polynomials in all variables.
   *
   * @param modv number of module variables.
   * @return List(X_1,...,X_n) a list of univariate polynomials.
   */
  public List<? extends ExprPolynomial> univariateList(int modv) {
    return univariateList(modv, 1L);
  }

  /**
   * Generate list of univariate polynomials in all variables with given exponent.
   *
   * @param modv number of module variables.
   * @param e the exponent of the variables.
   * @return List(X_1^e,...,X_n^e) a list of univariate polynomials.
   */
  public List<? extends ExprPolynomial> univariateList(int modv, long e) {
    List<ExprPolynomial> pols = new ArrayList<ExprPolynomial>(nvar);
    int nm = nvar - modv;
    for (int i = 0; i < nm; i++) {
      ExprPolynomial p = univariate(modv, nm - 1 - i, e);
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
  public ExprPolynomialRing extend(IAST vn) {
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
    ExprTermOrder to = tord.extend(nvar, i);
    ExprPolynomialRing pfac = new ExprPolynomialRing(coFac, v, nvar + i, to);
    return pfac;
  }

  /**
   * Distributive representation as polynomial with all main variables.
   *
   * @return distributive polynomial ring factory.
   */
  @SuppressWarnings("cast")
  public ExprPolynomialRing distribute() {
    // if (!(coFac instanceof GenPolynomialRing)) {
    return this;
    // }
    // ExprRingFactory cf = coFac;
    // ExprRingFactory<GenPolynomial> cfp = (ExprRingFactory<GenPolynomial>)
    // cf;
    // GenPolynomialRing cr = (GenPolynomialRing) cfp;
    // GenPolynomialRing pfac;
    // if (cr.vars != null) {
    // pfac = extend(cr.vars);
    // } else {
    // pfac = extend(cr.nvar);
    // }
    // return pfac;
  }

  /**
   * Get PolynomialComparator.
   *
   * @return polynomial comparator.
   */
  public ExprPolynomialComparator getComparator() {
    return new ExprPolynomialComparator(tord, false);
  }

  /**
   * Get PolynomialComparator.
   *
   * @param rev for reverse comparator.
   * @return polynomial comparator.
   */
  public ExprPolynomialComparator getComparator(boolean rev) {
    return new ExprPolynomialComparator(tord, rev);
  }

  /**
   * Add variable names.
   *
   * @param vars variable names to be recorded.
   */
  public static void addVars(IAST vars) {
    if (vars == null) {
      return;
    }
    synchronized (knownVars) {
      for (int i = 1; i < vars.size(); i++) {
        knownVars.add(vars.get(i)); // eventualy names 'overwritten'
      }
    }
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
  public Iterator<ExprPolynomial> iterator() {
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
  public ExprPolynomial getZERO() {
    return getZero();
  }

  @Override
  public ExprPolynomial random(int n) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ExprPolynomial random(int n, Random random) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ExprPolynomial parse(String s) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ExprPolynomial parse(Reader r) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ExprPolynomial getONE() {
    return getOne();
  }

  /** Polynomial iterator. */
  private static class GenPolynomialIterator implements Iterator<ExprPolynomial> {

    /** data structure. */
    final ExprPolynomialRing ring;

    final Iterator<List<Long>> eviter;

    final List<ExpVectorLong> powers;

    final List<Iterable<IExpr>> coeffiter;

    Iterator<List<IExpr>> itercoeff;

    ExprPolynomial current;

    /** Polynomial iterator constructor. */
    @SuppressWarnings("unchecked")
    public GenPolynomialIterator(ExprPolynomialRing fac) {
      ring = fac;
      LongIterable li = new LongIterable();
      li.setNonNegativeIterator();
      List<Iterable<Long>> tlist = new ArrayList<Iterable<Long>>(ring.nvar);
      for (int i = 0; i < ring.nvar; i++) {
        tlist.add(li);
      }
      CartesianProductInfinite<Long> ei = new CartesianProductInfinite<Long>(tlist);
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
      powers = new ArrayList<ExpVectorLong>();
      ExpVectorLong e = ExpVectorLong.create(eviter.next());
      powers.add(e);
      // System.out.println("new e = " + e);
      // System.out.println("powers = " + powers);
      List<IExpr> c = itercoeff.next();
      // System.out.println("coeffs = " + c);
      current = new ExprPolynomial(ring, c.get(0), e);
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
    public synchronized ExprPolynomial next() {
      ExprPolynomial res = current;
      if (!itercoeff.hasNext()) {
        ExpVectorLong e = ExpVectorLong.create(eviter.next());
        powers.add(0, e); // add new ev at beginning
        // System.out.println("new e = " + e);
        // System.out.println("powers = " + powers);
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
      // System.out.println(" skip zero ");
      // coeffs = itercoeff.next(); // skip tuples with zero in first
      // component
      // }
      // System.out.println("coeffs = " + coeffs);
      ExprPolynomial pol = ring.getZero().copy();
      int i = 0;
      for (ExpVectorLong f : powers) {
        IExpr c = coeffs.get(i++);
        if (c.isZERO()) {
          continue;
        }
        if (pol.val.get(f) != null) {
          // System.out.println("error f in pol = " + f + ", " + pol.getMap().get(f));
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
}
