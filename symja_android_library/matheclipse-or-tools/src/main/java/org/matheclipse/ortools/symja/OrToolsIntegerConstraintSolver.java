package org.matheclipse.ortools.symja;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.matheclipse.core.convert.ChocoConvert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.TimeoutException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.optimization.IIntegerConstraintSolver;
import org.matheclipse.ortools.sat.BoolVar;
import org.matheclipse.ortools.sat.Constraint;
import org.matheclipse.ortools.sat.CpModel;
import org.matheclipse.ortools.sat.CpSolver;
import org.matheclipse.ortools.sat.CpSolverSolutionCallback;
import org.matheclipse.ortools.sat.CpSolverStatus;
import org.matheclipse.ortools.sat.IntVar;
import org.matheclipse.ortools.sat.LinearArgument;
import org.matheclipse.ortools.sat.LinearExpr;
import org.matheclipse.ortools.sat.LinearExprBuilder;
import org.matheclipse.ortools.sat.Literal;
import org.matheclipse.ortools.util.Domain;

/**
 * {@link IIntegerConstraintSolver} with the CP-SAT solver of the OR-Tools port: the system is
 * translated into a <code>CpModel</code> and all its solutions (up to the requested number) are
 * enumerated.
 *
 * <p>
 * The translation knows sums, products and non-negative integer powers of the variables (with
 * auxiliary product variables), <code>Abs</code>, <code>Max</code>, <code>Min</code> and
 * <code>Mod</code>, <code>Quotient</code> by a constant (the floor convention of Symja), the
 * relations <code>Equal, Unequal, Less, LessEqual, Greater, GreaterEqual</code> and their
 * combinations with <code>And, Or, Not, Xor, Implies</code>. Anything else makes
 * {@link #solve} return {@link F#NIL}, and <code>Solve</code> continues with choco-solver.
 */
public class OrToolsIntegerConstraintSolver implements IIntegerConstraintSolver {

  /** The bounds of an intermediate value are cut to this magnitude. */
  private static final long MAX_BOUND = 1L << 50;

  /** The largest prime of the {@link S#Primes} domain, as in the choco-solver translation. */
  private static final int MAX_PRIME = 32749;

  /** The domain of the primes up to {@link #MAX_PRIME}, built on first use. */
  private static final class PrimeDomain {
    static final Domain PRIMES = sieve(MAX_PRIME);

    private static Domain sieve(int max) {
      boolean[] composite = new boolean[max + 1];
      int count = 0;
      for (int i = 2; i <= max; i++) {
        if (!composite[i]) {
          count++;
          for (long j = (long) i * i; j <= max; j += i) {
            composite[(int) j] = true;
          }
        }
      }
      long[] primes = new long[count];
      int k = 0;
      for (int i = 2; i <= max; i++) {
        if (!composite[i]) {
          primes[k++] = i;
        }
      }
      return Domain.fromValues(primes);
    }
  }

  /** Thrown for a term the translation doesn't know. */
  private static final class Unsupported extends RuntimeException {
    private static final long serialVersionUID = 1L;

    Unsupported(String message) {
      super(message, null, false, false);
    }
  }

  /** A linear expression of the model together with bounds of its value. */
  private static final class Term {
    final LinearExpr expr;
    final long lo;
    final long hi;

    Term(LinearExpr expr, long lo, long hi) {
      this.expr = expr;
      this.lo = lo;
      this.hi = hi;
    }
  }

  /** The translation of one system. */
  private static final class Translation {
    final CpModel model = new CpModel();
    final Map<ISymbol, IntVar> variables = new TreeMap<ISymbol, IntVar>();
    /** whether a bound was cut to {@link #MAX_BOUND}, so that solutions may have been lost */
    boolean restricted = false;
    int auxiliaries = 0;

    long clamp(long value) {
      if (value > MAX_BOUND) {
        restricted = true;
        return MAX_BOUND;
      }
      if (value < -MAX_BOUND) {
        restricted = true;
        return -MAX_BOUND;
      }
      return value;
    }

    long clamp(double value) {
      if (value > MAX_BOUND) {
        restricted = true;
        return MAX_BOUND;
      }
      if (value < -MAX_BOUND) {
        restricted = true;
        return -MAX_BOUND;
      }
      return (long) value;
    }

    IntVar auxiliary(long lo, long hi) {
      return model.newIntVar(clamp(lo), clamp(hi), "aux" + (auxiliaries++));
    }

    Term term(IExpr expr) {
      if (expr.isInteger()) {
        long value = ((IInteger) expr).toLongDefault();
        if (value == Long.MIN_VALUE || Math.abs(value) > MAX_BOUND) {
          throw new Unsupported("integer out of range: " + expr);
        }
        return new Term(LinearExpr.constant(value), value, value);
      }
      if (expr.isSymbol()) {
        IntVar v = variables.get(expr);
        if (v == null) {
          throw new Unsupported("unknown variable: " + expr);
        }
        Domain d = v.getDomain();
        return new Term(LinearExpr.term(v, 1), d.min(), d.max());
      }
      if (!expr.isAST()) {
        throw new Unsupported("unsupported term: " + expr);
      }
      IAST ast = (IAST) expr;
      if (ast.isPlus()) {
        LinearExprBuilder builder = LinearExpr.newBuilder();
        double lo = 0;
        double hi = 0;
        for (IExpr arg : ast) {
          Term t = term(arg);
          builder.add(t.expr);
          lo += t.lo;
          hi += t.hi;
        }
        return new Term(builder.build(), clamp(lo), clamp(hi));
      }
      if (ast.isTimes()) {
        // the constant factor scales the product of the other factors
        long constant = 1;
        List<Term> factors = new ArrayList<Term>();
        for (IExpr arg : ast) {
          if (arg.isInteger()) {
            long c = ((IInteger) arg).toLongDefault();
            if (c == Long.MIN_VALUE) {
              throw new Unsupported("integer out of range: " + arg);
            }
            constant = Math.multiplyExact(constant, c);
          } else {
            factors.add(term(arg));
          }
        }
        Term product = product(factors);
        return scale(product, constant);
      }
      if (ast.isPower() && ast.exponent().isInteger()) {
        int n = ast.exponent().toIntDefault();
        if (n < 0 || n > 64) {
          throw new Unsupported("unsupported exponent: " + expr);
        }
        if (n == 0) {
          return new Term(LinearExpr.constant(1), 1, 1);
        }
        Term base = term(ast.base());
        List<Term> factors = new ArrayList<Term>(n);
        for (int i = 0; i < n; i++) {
          factors.add(base);
        }
        return product(factors);
      }
      if (ast.isAbs()) {
        Term a = term(ast.arg1());
        long hi = Math.max(Math.abs(a.lo), Math.abs(a.hi));
        long lo = a.lo <= 0 && a.hi >= 0 ? 0 : Math.min(Math.abs(a.lo), Math.abs(a.hi));
        IntVar target = auxiliary(lo, hi);
        model.addAbsEquality(target, a.expr);
        return new Term(LinearExpr.term(target, 1), lo, hi);
      }
      if (ast.isAST(S.Max) || ast.isAST(S.Min)) {
        if (ast.argSize() == 0) {
          throw new Unsupported("empty " + ast.head());
        }
        boolean isMax = ast.isAST(S.Max);
        LinearArgument[] args = new LinearArgument[ast.argSize()];
        long lo = isMax ? Long.MIN_VALUE : Long.MAX_VALUE;
        long hi = isMax ? Long.MIN_VALUE : Long.MAX_VALUE;
        for (int i = 1; i < ast.size(); i++) {
          Term t = term(ast.get(i));
          args[i - 1] = t.expr;
          lo = isMax ? Math.max(lo, t.lo) : Math.min(lo, t.lo);
          hi = isMax ? Math.max(hi, t.hi) : Math.min(hi, t.hi);
        }
        IntVar target = auxiliary(lo, hi);
        if (isMax) {
          model.addMaxEquality(target, args);
        } else {
          model.addMinEquality(target, args);
        }
        return new Term(LinearExpr.term(target, 1), lo, hi);
      }
      if ((ast.isAST(S.Mod, 3) || ast.isAST(S.Quotient, 3)) && ast.arg2().isInteger()) {
        // a == n*q + r with 0 <= r < n (n > 0) or n < r <= 0 (n < 0): the floor convention
        long n = ((IInteger) ast.arg2()).toLongDefault();
        if (n == 0 || n == Long.MIN_VALUE || Math.abs(n) > MAX_BOUND) {
          throw new Unsupported("unsupported modulus: " + expr);
        }
        Term a = term(ast.arg1());
        long rLo = n > 0 ? 0 : n + 1;
        long rHi = n > 0 ? n - 1 : 0;
        double q1 = Math.floor((double) a.lo / n);
        double q2 = Math.floor((double) a.hi / n);
        IntVar q = auxiliary(clamp(Math.min(q1, q2)), clamp(Math.max(q1, q2)));
        IntVar r = auxiliary(rLo, rHi);
        model.addEquality(a.expr,
            LinearExpr.newBuilder().addTerm(q, n).addTerm(r, 1).build());
        if (ast.isAST(S.Mod, 3)) {
          return new Term(LinearExpr.term(r, 1), rLo, rHi);
        }
        Domain dq = q.getDomain();
        return new Term(LinearExpr.term(q, 1), dq.min(), dq.max());
      }
      throw new Unsupported("unsupported term: " + expr);
    }

    Term scale(Term t, long c) {
      if (c == 1) {
        return t;
      }
      double a = (double) t.lo * c;
      double b = (double) t.hi * c;
      return new Term(LinearExpr.term(t.expr, c), clamp(Math.min(a, b)), clamp(Math.max(a, b)));
    }

    /** The product of the factors, with one auxiliary variable per multiplication. */
    Term product(List<Term> factors) {
      if (factors.isEmpty()) {
        return new Term(LinearExpr.constant(1), 1, 1);
      }
      Term result = factors.get(0);
      for (int i = 1; i < factors.size(); i++) {
        Term f = factors.get(i);
        double[] p = new double[] {(double) result.lo * f.lo, (double) result.lo * f.hi,
            (double) result.hi * f.lo, (double) result.hi * f.hi};
        double lo = p[0];
        double hi = p[0];
        for (double x : p) {
          lo = Math.min(lo, x);
          hi = Math.max(hi, x);
        }
        IntVar target = auxiliary(clamp(lo), clamp(hi));
        model.addMultiplicationEquality(target, new LinearArgument[] {result.expr, f.expr});
        Domain d = target.getDomain();
        result = new Term(LinearExpr.term(target, 1), d.min(), d.max());
      }
      return result;
    }

    /** Post a relation of the conjunction directly. */
    void post(IExpr relation) {
      if (relation.isTrue()) {
        return;
      }
      if (relation.isFalse()) {
        model.addBoolOr(new Literal[0]);
        return;
      }
      if (relation.isAnd()) {
        for (IExpr arg : (IAST) relation) {
          post(arg);
        }
        return;
      }
      if (isRelation(relation)) {
        relation((IAST) integral(relation), false);
        return;
      }
      model.addBoolOr(new Literal[] {literal(relation)});
    }

    /**
     * Post the relation; with <code>negated</code> the negation of it.
     *
     * @return the posted constraint, which a caller can make conditional
     */
    Constraint relation(IAST relation, boolean negated) {
      ISymbol head = relation.topHead();
      if (negated) {
        head = negate(head);
      }
      Term lhs = term(relation.arg1());
      Term rhs = term(relation.arg2());
      if (head == S.Equal) {
        return model.addEquality(lhs.expr, rhs.expr);
      } else if (head == S.Unequal) {
        return model.addDifferent(lhs.expr, rhs.expr);
      } else if (head == S.Less) {
        return model.addLessThan(lhs.expr, rhs.expr);
      } else if (head == S.LessEqual) {
        return model.addLessOrEqual(lhs.expr, rhs.expr);
      } else if (head == S.Greater) {
        return model.addGreaterThan(lhs.expr, rhs.expr);
      } else if (head == S.GreaterEqual) {
        return model.addGreaterOrEqual(lhs.expr, rhs.expr);
      }
      throw new Unsupported("unsupported relation: " + relation);
    }

    /** A literal which is true exactly when the condition holds. */
    Literal literal(IExpr condition) {
      if (condition.isTrue() || condition.isFalse()) {
        BoolVar b = model.newBoolVar("const" + (auxiliaries++));
        model.addBoolOr(new Literal[] {condition.isTrue() ? b : b.not()});
        return b;
      }
      if (condition.isNot()) {
        return literal(condition.first()).not();
      }
      if (condition.isAnd() || condition.isOr() || condition.isAST(S.Xor)) {
        IAST ast = (IAST) condition;
        if (ast.argSize() == 0) {
          throw new Unsupported("empty " + ast.head());
        }
        Literal[] args = new Literal[ast.argSize()];
        for (int i = 1; i < ast.size(); i++) {
          args[i - 1] = literal(ast.get(i));
        }
        BoolVar b = model.newBoolVar("bool" + (auxiliaries++));
        if (condition.isAnd()) {
          // b == And(args)
          model.addBoolAnd(args).onlyEnforceIf(b);
          Literal[] clause = new Literal[args.length + 1];
          for (int i = 0; i < args.length; i++) {
            clause[i] = args[i].not();
          }
          clause[args.length] = b;
          model.addBoolOr(clause);
        } else if (condition.isOr()) {
          // b == Or(args)
          model.addBoolOr(args).onlyEnforceIf(b);
          for (Literal a : args) {
            model.addImplication(a, b);
          }
        } else {
          // Xor(args, Not(b)) holds exactly when b == Xor(args)
          Literal[] all = new Literal[args.length + 1];
          System.arraycopy(args, 0, all, 0, args.length);
          all[args.length] = b.not();
          model.addBoolXor(all);
        }
        return b;
      }
      if (condition.isAST(S.Implies, 3)) {
        return literal(F.Or(F.Not(condition.first()), condition.second()));
      }
      if (isRelation(condition)) {
        IExpr relation = integral(condition);
        if (relation.isTrue() || relation.isFalse()) {
          return literal(relation);
        }
        BoolVar b = model.newBoolVar("rel" + (auxiliaries++));
        relation((IAST) relation, false).onlyEnforceIf(b);
        relation((IAST) relation, true).onlyEnforceIf(b.not());
        return b;
      }
      throw new Unsupported("unsupported condition: " + condition);
    }
  }

  private static ISymbol negate(ISymbol head) {
    if (head == S.Equal) {
      return S.Unequal;
    } else if (head == S.Unequal) {
      return S.Equal;
    } else if (head == S.Less) {
      return S.GreaterEqual;
    } else if (head == S.LessEqual) {
      return S.Greater;
    } else if (head == S.Greater) {
      return S.LessEqual;
    } else if (head == S.GreaterEqual) {
      return S.Less;
    }
    throw new Unsupported("unsupported relation: " + head);
  }

  private static boolean isRelation(IExpr expr) {
    return expr.isAST2() && (expr.isEqual() || expr.isAST(S.Unequal) || expr.isAST(S.Greater)
        || expr.isAST(S.GreaterEqual) || expr.isAST(S.Less) || expr.isAST(S.LessEqual));
  }

  /**
   * A relation with integer coefficients only: both sides are multiplied by the common denominator
   * of the rational numbers in it, and a single rational bound of an integer valued side is
   * rounded.
   */
  private static IExpr integral(IExpr relation) {
    if (relation.isFree(x -> x.isFraction(), false)) {
      return relation;
    }
    IAST rel = (IAST) relation;
    IInteger[] denominator = new IInteger[] {F.C1};
    rel.forAllLeaves(x -> {
      if (x.isFraction()) {
        denominator[0] = denominator[0].lcm(((IFraction) x).denominator());
      }
      return true;
    }, 1);
    EvalEngine engine = EvalEngine.get();
    IExpr lhs = engine.evaluate(F.Expand(F.Times(denominator[0], rel.arg1())));
    IExpr rhs = engine.evaluate(F.Expand(F.Times(denominator[0], rel.arg2())));
    return F.binaryAST2(rel.head(), lhs, rhs);
  }

  @Override
  public IAST solve(IAST equationsAndInequations, IAST variables, IAST userDefinedVariables,
      int maximumNumberOfResults, ISymbol domain, long lowerBound, long upperBound,
      EvalEngine engine) {
    Translation t = new Translation();
    try {
      Domain primes = null;
      if (domain == S.Primes) {
        primes = PrimeDomain.PRIMES;
      } else if (domain != S.Integers) {
        return F.NIL;
      }
      for (int i = 1; i < variables.size(); i++) {
        IExpr v = variables.get(i);
        if (v.isSymbol()) {
          IntVar var = primes != null ? t.model.newIntVarFromDomain(primes, v.toString())
              : t.model.newIntVar(lowerBound, upperBound, v.toString());
          t.variables.put((ISymbol) v, var);
        }
      }
      if (t.variables.isEmpty()) {
        return F.NIL;
      }
      boolean constrained = false;
      for (IExpr c : equationsAndInequations) {
        if (c.isTrue()) {
          continue;
        }
        if (c.isFalse()) {
          return F.CEmptyList;
        }
        t.post(c);
        constrained = true;
      }
      if (!constrained) {
        // without a constraint every point of the search box is a solution, which is no answer
        return F.NIL;
      }
    } catch (Unsupported | ArithmeticException e) {
      return F.NIL;
    }

    final List<ISymbol> symbols = new ArrayList<ISymbol>(t.variables.keySet());
    final IntVar[] vars = new IntVar[symbols.size()];
    for (int i = 0; i < vars.length; i++) {
      vars[i] = t.variables.get(symbols.get(i));
    }
    final int limit = maximumNumberOfResults < 0 ? Short.MAX_VALUE : maximumNumberOfResults;
    final List<long[]> solutions = new ArrayList<long[]>();
    final boolean[] interrupted = new boolean[] {false};
    CpSolverSolutionCallback callback = new CpSolverSolutionCallback() {
      @Override
      public void onSolutionCallback() {
        if (Thread.currentThread().isInterrupted() || engine.isStopRequested()) {
          interrupted[0] = true;
          stopSearch();
          return;
        }
        long[] values = new long[vars.length];
        for (int i = 0; i < vars.length; i++) {
          values[i] = value(vars[i]);
        }
        solutions.add(values);
        if (solutions.size() >= limit) {
          stopSearch();
        }
      }
    };
    CpSolver solver = new CpSolver();
    solver.getParameters().setEnumerateAllSolutions(true);
    solver.getParameters().setMaxTimeInSeconds(60.0);
    CpSolverStatus status = solver.solve(t.model, callback);
    if (interrupted[0]) {
      if (Thread.currentThread().isInterrupted()) {
        throw TimeoutException.TIMED_OUT;
      }
      return F.NIL;
    }
    if (status == CpSolverStatus.MODEL_INVALID) {
      return F.NIL;
    }
    if (solutions.size() < limit && status != CpSolverStatus.OPTIMAL
        && status != CpSolverStatus.INFEASIBLE) {
      // the search stopped before it enumerated every solution
      return F.NIL;
    }
    if (solutions.isEmpty()) {
      // a solution may lie outside a cut intermediate range
      return t.restricted ? F.NIL : F.CEmptyList;
    }
    // the same rules as the choco-solver translation gives
    List<Map<ISymbol, Long>> byName = new ArrayList<Map<ISymbol, Long>>(solutions.size());
    for (long[] values : solutions) {
      Map<ISymbol, Long> map = new TreeMap<ISymbol, Long>();
      for (int i = 0; i < symbols.size(); i++) {
        map.put(symbols.get(i), values[i]);
      }
      byName.add(map);
    }
    return ChocoConvert.solutionRules(byName, variables, userDefinedVariables, engine);
  }
}
