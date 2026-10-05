package org.matheclipse.core.reduce;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.integrate.IntegrateTimeBudget;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A cylindrical decomposition for a formula of polynomial equations and inequalities in two real
 * variables <code>x</code> and <code>y</code>, where every factor of a polynomial is of degree 1
 * at most in <code>y</code> or is free of <code>x</code>: <code>x/y &lt;= 0</code>,
 * <code>(x-1)/(y^2-1) &gt; 0</code>, <code>x*y &gt; 1 &amp;&amp; y &lt; x</code>.
 * <p>
 * For a fixed <code>x</code> the solutions in <code>y</code> are intervals between the points
 * <code>-b(x)/a(x)</code> of the polynomials <code>a(x)*y+b(x)</code> and the constant roots of the
 * polynomials in <code>y</code> alone. The order of these points, and which of them exist, changes
 * only at finitely many values of <code>x</code>: the roots of the <code>a(x)</code>, the places
 * where two of the points meet, and the roots of the polynomials in <code>x</code> alone. Between
 * two such values the formula is reduced at one sample value of <code>x</code>, and the numbers in
 * the result are read as the values of the points there; at the values themselves it is reduced
 * as it is.
 */
public final class BivariateReduce {

  /** The number of critical values of <code>x</code> a formula may have. */
  private static final int MAX_CRITICAL = 16;

  /** The size of a critical value: rational numbers and square roots of them. */
  private static final int MAX_ROOT_LEAVES = 16;

  /** The time of one decomposition. */
  private static final long MAX_MILLIS = 4000L;

  /** Two critical values which are closer are not told apart by their numerical value. */
  private static final double SEPARATION = 1e-9;

  /** The formula is not of the shape described above. */
  private static final class Declined extends RuntimeException {
    private static final long serialVersionUID = 1L;

    Declined() {
      super(null, null, false, false);
    }
  }

  /** One cell of the decomposition: a critical value, or the interval between two of them. */
  private static final class Cell {
    /** the critical value of a point cell, <code>null</code> for an interval */
    IExpr point;
    /** the end points of an interval; <code>null</code> for an infinite end */
    IExpr lower;
    IExpr upper;
    /** the condition on y: for an interval with the points as functions of x */
    IExpr condition;
  }

  /** Adjacent cells with one condition on y. */
  private static final class Run {
    IExpr lower;
    IExpr upper;
    boolean lowerClosed;
    boolean upperClosed;
    /** the condition of the intervals of the run, <code>null</code> for a single point */
    IExpr formula;
    /** the condition of the point, for a single point */
    IExpr pointCondition;
  }

  private final EvalEngine engine;
  private final IExpr x;
  private final IExpr y;
  /** the points <code>-b(x)/a(x)</code>, and the constant ones */
  private final List<IExpr> breakpoints = new ArrayList<IExpr>();

  private BivariateReduce(IExpr x, IExpr y, EvalEngine engine) {
    this.x = x;
    this.y = y;
    this.engine = engine;
  }

  /**
   * <code>Reduce(formula, {x, y}, Reals)</code>
   *
   * @return {@link F#NIL} if the formula is not of the shape which is decomposed
   */
  public static IExpr reduce(IExpr formula, IExpr x, IExpr y, EvalEngine engine) {
    if (!x.isSymbol() || !y.isSymbol() || x.equals(y) || formula.isFree(x) || formula.isFree(y)) {
      return F.NIL;
    }
    try {
      return IntegrateTimeBudget
          .runWithin(() -> new BivariateReduce(x, y, engine).reduce(formula), MAX_MILLIS);
    } catch (Declined declined) {
      return F.NIL;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  private IExpr reduce(IExpr formula) {
    final IExpr phi = QuadraticQE.polynomialForm(formula, engine);
    if (phi.isNIL() || phi.isTrue() || phi.isFalse()) {
      return phi;
    }
    if (!phi.isFree(t -> t.isSymbol() && !t.isBuiltInSymbol() && !t.equals(x) && !t.equals(y),
        false)) {
      // a parameter
      return F.NIL;
    }
    List<IExpr> critical = criticalValues(phi);
    List<Cell> cells = cells(phi, critical);
    return expression(runs(cells));
  }

  // --------------------------------------------------------------------------------------------
  // projection
  // --------------------------------------------------------------------------------------------

  /** The critical values of x in ascending order; the points of y are collected on the way. */
  private List<IExpr> criticalValues(IExpr phi) {
    final List<IExpr> polynomials = new ArrayList<IExpr>();
    final List<IExpr[]> linear = new ArrayList<IExpr[]>();
    final List<IExpr> constants = new ArrayList<IExpr>();
    final List<IExpr> atoms = new ArrayList<IExpr>();
    phi.isFree(t -> {
      if (t.isAST2() && (t.isAST(S.Less) || t.isAST(S.LessEqual) || t.isAST(S.Greater)
          || t.isAST(S.GreaterEqual) || t.isAST(S.Equal) || t.isAST(S.Unequal))) {
        atoms.add(t);
      }
      return false;
    }, true);
    // the sign of a polynomial changes where one of its factors vanishes
    final List<IExpr> factors = new ArrayList<IExpr>();
    for (IExpr atom : atoms) {
      IExpr product = engine.evaluate(F.Factor(F.Subtract(atom.first(), atom.second())));
      IAST list = product.isTimes() ? (IAST) product : F.Times(product);
      for (int i = 1; i <= list.argSize(); i++) {
        IExpr factor = list.get(i);
        if (factor.isPower() && factor.exponent().isInteger() && factor.exponent().isPositive()) {
          factor = factor.base();
        }
        if (!factor.isNumber() && !factors.contains(factor)) {
          factors.add(factor);
        }
      }
    }
    for (IExpr factor : factors) {
      IExpr p = engine.evaluate(F.Expand(factor));
      if (p.isFree(y)) {
        if (!p.isFree(x)) {
          polynomials.add(p);
        }
        continue;
      }
      int degree = engine.evaluate(F.Exponent(p, y)).toIntDefault();
      if (degree == 1) {
        linear.add(new IExpr[] {engine.evaluate(F.Coefficient(p, y, F.C1)),
            engine.evaluate(F.Coefficient(p, y, F.C0))});
      } else if (degree > 1 && p.isFree(x)) {
        for (IExpr root : realRoots(p, y)) {
          if (!constants.contains(root)) {
            constants.add(root);
          }
        }
      } else {
        throw new Declined();
      }
    }
    for (int i = 0; i < linear.size(); i++) {
      IExpr a = linear.get(i)[0];
      IExpr b = linear.get(i)[1];
      IExpr point = engine.evaluate(F.Together(F.Negate(F.Divide(b, a))));
      if (!breakpoints.contains(point)) {
        breakpoints.add(point);
      }
      polynomials.add(a);
      for (int j = 0; j < i; j++) {
        // where two of the points meet
        polynomials.add(engine.evaluate(F.Expand(
            F.Subtract(F.Times(a, linear.get(j)[1]), F.Times(linear.get(j)[0], b)))));
      }
      for (IExpr constant : constants) {
        polynomials.add(engine.evaluate(F.Expand(F.Plus(F.Times(a, constant), b))));
      }
    }
    for (IExpr constant : constants) {
      if (!breakpoints.contains(constant)) {
        breakpoints.add(constant);
      }
    }

    List<IExpr> values = new ArrayList<IExpr>();
    List<Double> numbers = new ArrayList<Double>();
    for (IExpr polynomial : polynomials) {
      if (polynomial.isFree(x)) {
        continue;
      }
      for (IExpr root : realRoots(polynomial, x)) {
        double number = engine.evalDouble(root);
        int position = 0;
        boolean known = false;
        while (position < numbers.size() && numbers.get(position) < number + SEPARATION) {
          if (Math.abs(numbers.get(position) - number) <= SEPARATION) {
            if (!engine.evalTrue(F.PossibleZeroQ(F.Subtract(values.get(position), root)))) {
              // too close to be ordered by their numerical values
              throw new Declined();
            }
            known = true;
            break;
          }
          position++;
        }
        if (!known) {
          values.add(position, root);
          numbers.add(position, number);
          if (values.size() > MAX_CRITICAL) {
            throw new Declined();
          }
        }
      }
    }
    return values;
  }

  /** The real roots of the polynomial as exact numbers. */
  private List<IExpr> realRoots(IExpr polynomial, IExpr variable) {
    List<IExpr> roots = new ArrayList<IExpr>();
    IExpr solutions = engine.evalQuiet(F.Solve(F.Equal(polynomial, F.C0), variable, S.Reals));
    if (!solutions.isList()) {
      throw new Declined();
    }
    for (int i = 1; i <= solutions.argSize(); i++) {
      IExpr solution = ((IAST) solutions).get(i);
      if (!solution.isList1() || !solution.first().isRule()
          || !solution.first().first().equals(variable)) {
        throw new Declined();
      }
      IExpr root = solution.first().second();
      IExpr number = engine.evalQuiet(F.N(root));
      if (!root.isFree(variable) || !root.isFree(S.ConditionalExpression) || !number.isReal()
          || root.leafCount() > MAX_ROOT_LEAVES) {
        // the roots of a cubic or a quartic in radicals: the reduction at such a value is slow
        throw new Declined();
      }
      roots.add(root);
    }
    return roots;
  }

  // --------------------------------------------------------------------------------------------
  // cells
  // --------------------------------------------------------------------------------------------

  private List<Cell> cells(IExpr phi, List<IExpr> critical) {
    final int n = critical.size();
    double[] numbers = new double[n];
    for (int i = 0; i < n; i++) {
      numbers[i] = engine.evalDouble(critical.get(i));
    }
    List<Cell> cells = new ArrayList<Cell>(2 * n + 1);
    for (int i = 0; i <= n; i++) {
      Cell interval = new Cell();
      interval.lower = i == 0 ? null : critical.get(i - 1);
      interval.upper = i == n ? null : critical.get(i);
      IExpr sample;
      if (n == 0) {
        sample = F.C0;
      } else if (i == 0) {
        sample = F.ZZ((long) Math.floor(numbers[0]) - 1);
      } else if (i == n) {
        sample = F.ZZ((long) Math.ceil(numbers[n - 1]) + 1);
      } else {
        double width = numbers[i] - numbers[i - 1];
        sample = engine.evaluate(F.Rationalize(F.num(numbers[i - 1] + width / 2), F.num(width / 4)));
        if (!sample.isRational()) {
          throw new Declined();
        }
      }
      interval.condition = functionsOfX(reduceAt(phi, sample), sample);
      cells.add(interval);
      if (i < n) {
        Cell point = new Cell();
        point.point = critical.get(i);
        point.condition = reduceAt(phi, point.point);
        cells.add(point);
      }
    }
    return cells;
  }

  /** The formula for the value of x, reduced for y. */
  private IExpr reduceAt(IExpr phi, IExpr value) {
    IExpr reduced = engine.evalQuiet(F.Reduce(F.subst(phi, x, value), y, S.Reals));
    if (!reduced.isFree(S.Reduce) || !reduced.isSpecialsFree()) {
      throw new Declined();
    }
    return reduced;
  }

  /**
   * The numbers in a condition on y, which was reduced at a sample value of x, as the points
   * <code>-b(x)/a(x)</code> with that value at the sample.
   */
  private IExpr functionsOfX(IExpr condition, IExpr sample) {
    if (condition.isTrue() || condition.isFalse()) {
      return condition;
    }
    if (condition.isAnd() || condition.isOr()) {
      IAST junction = (IAST) condition;
      IASTAppendable result = F.ast(junction.head(), junction.argSize());
      for (int i = 1; i <= junction.argSize(); i++) {
        result.append(functionsOfX(junction.get(i), sample));
      }
      return result;
    }
    if (condition.isAST2() && condition.first().equals(y) && condition.second().isFree(y)
        && (condition.isAST(S.Less) || condition.isAST(S.LessEqual) || condition.isAST(S.Greater)
            || condition.isAST(S.GreaterEqual) || condition.isAST(S.Equal)
            || condition.isAST(S.Unequal))) {
      IExpr number = condition.second();
      for (IExpr breakpoint : breakpoints) {
        IExpr value = engine.evalQuiet(F.subst(breakpoint, x, sample));
        if (value.isSpecialsFree()
            && engine.evalTrue(F.PossibleZeroQ(F.Subtract(value, number)))) {
          return F.binaryAST2(condition.head(), y, breakpoint);
        }
      }
    }
    throw new Declined();
  }

  // --------------------------------------------------------------------------------------------
  // result
  // --------------------------------------------------------------------------------------------

  /** Adjacent cells with one condition on y are one part of the result. */
  private List<Run> runs(List<Cell> cells) {
    List<Run> runs = new ArrayList<Run>();
    Run current = null;
    for (Cell cell : cells) {
      if (cell.condition.isFalse()) {
        current = null;
        continue;
      }
      if (current != null && cell.point != null && current.formula != null
          && holdsAt(current.formula, cell)) {
        current.upper = cell.point;
        current.upperClosed = true;
        continue;
      }
      if (current != null && cell.point == null && current.upperClosed) {
        if (current.formula != null && current.formula.equals(cell.condition)) {
          current.upper = cell.upper;
          current.upperClosed = false;
          continue;
        }
        if (current.formula == null && holdsAt(cell.condition, current)) {
          // the point at the lower end of the interval
          current.formula = cell.condition;
          current.upper = cell.upper;
          current.upperClosed = false;
          continue;
        }
      }
      current = new Run();
      if (cell.point != null) {
        current.lower = cell.point;
        current.upper = cell.point;
        current.lowerClosed = true;
        current.upperClosed = true;
        current.pointCondition = cell.condition;
      } else {
        current.lower = cell.lower;
        current.upper = cell.upper;
        current.formula = cell.condition;
      }
      runs.add(current);
    }
    return runs;
  }

  /** Whether the condition of an interval is the one of the point cell at its end. */
  private boolean holdsAt(IExpr formula, Cell point) {
    return sameAt(formula, point.point, point.condition);
  }

  private boolean holdsAt(IExpr formula, Run point) {
    return sameAt(formula, point.lower, point.pointCondition);
  }

  private boolean sameAt(IExpr formula, IExpr value, IExpr condition) {
    IExpr substituted = engine.evalQuiet(F.subst(formula, x, value));
    if (!substituted.isSpecialsFree()) {
      // a point -b(x)/a(x) which does not exist there
      return false;
    }
    IExpr reduced = engine.evalQuiet(F.Reduce(substituted, y, S.Reals));
    return reduced.equals(condition);
  }

  private IExpr expression(List<Run> runs) {
    IASTAppendable or = F.ast(S.Or, runs.size());
    for (Run run : runs) {
      IASTAppendable and = F.ast(S.And, 3);
      if (run.formula == null) {
        and.append(F.Equal(x, run.lower));
      } else {
        if (run.lower != null) {
          and.append(run.lowerClosed ? F.GreaterEqual(x, run.lower) : F.Greater(x, run.lower));
        }
        if (run.upper != null) {
          and.append(run.upperClosed ? F.LessEqual(x, run.upper) : F.Less(x, run.upper));
        }
      }
      IExpr condition = run.formula == null ? run.pointCondition : run.formula;
      if (!condition.isTrue()) {
        and.append(condition);
      }
      if (and.isAST0()) {
        return S.True;
      }
      or.append(and.oneIdentity1());
    }
    if (or.isAST0()) {
      return S.False;
    }
    return or.oneIdentity1();
  }
}
