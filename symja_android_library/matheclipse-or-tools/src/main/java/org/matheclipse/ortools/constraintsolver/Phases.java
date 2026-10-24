package org.matheclipse.ortools.constraintsolver;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import org.matheclipse.ortools.constraintsolver.cp.CpVar;

/**
 * The built-in decisions and decision builders returned by <code>Solver.makePhase</code>,
 * <code>makeDecision</code> and <code>makeFailDecision</code>. They reimplement the OR-Tools
 * variable and value selection strategies on the simplified engine of this module.
 */
final class Phases {

  private Phases() {}

  /** <code>x == v</code> on the left branch, <code>x != v</code> on the right branch. */
  static final class AssignValue extends Decision {
    private final IntVar var;

    private final long value;

    AssignValue(IntVar var, long value) {
      this.var = var;
      this.value = value;
    }

    @Override
    public void apply(Solver s) {
      var.setValue(value);
    }

    @Override
    public void refute(Solver s) {
      var.removeValue(value);
    }

    @Override
    public String toString() {
      return "[" + var.name() + " == " + value + "]";
    }
  }

  /** <code>x &lt;= v</code> (or <code>x &gt; v</code> when <code>upper</code>) on the left. */
  static final class Split extends Decision {
    private final IntVar var;

    private final long value;

    private final boolean upper;

    Split(IntVar var, long value, boolean upper) {
      this.var = var;
      this.value = value;
      this.upper = upper;
    }

    @Override
    public void apply(Solver s) {
      if (upper) {
        var.setMin(value + 1);
      } else {
        var.setMax(value);
      }
    }

    @Override
    public void refute(Solver s) {
      if (upper) {
        var.setMax(value);
      } else {
        var.setMin(value + 1);
      }
    }

    @Override
    public String toString() {
      return "[" + var.name() + (upper ? " > " : " <= ") + value + "]";
    }
  }

  /** A decision whose branches are Java closures. */
  static final class Closure extends Decision {
    private final Consumer<Solver> apply;

    private final Consumer<Solver> refute;

    Closure(Consumer<Solver> apply, Consumer<Solver> refute) {
      this.apply = apply;
      this.refute = refute;
    }

    @Override
    public void apply(Solver s) {
      apply.accept(s);
    }

    @Override
    public void refute(Solver s) {
      refute.accept(s);
    }

    @Override
    public String toString() {
      return "ClosureDecision";
    }
  }

  /** A decision which fails in both branches. */
  static final class Fail extends Decision {
    @Override
    public void apply(Solver s) {
      s.fail();
    }

    @Override
    public void refute(Solver s) {
      s.fail();
    }

    @Override
    public String toString() {
      return "FailDecision";
    }
  }

  /** Selects an unbound variable and a value by one of the OR-Tools strategies. */
  static final class Phase extends DecisionBuilder {
    private final IntVar[] vars;

    private final int varStrategy;

    private final int valueStrategy;

    private final LongUnaryOperator varEvaluator;

    private final LongBinaryOperator valueEvaluator;

    private final Random random;

    Phase(IntVar[] vars, int varStrategy, int valueStrategy, LongUnaryOperator varEvaluator,
        LongBinaryOperator valueEvaluator, Random random) {
      this.vars = vars.clone();
      this.varStrategy = varStrategy;
      this.valueStrategy = valueStrategy;
      this.varEvaluator = varEvaluator;
      this.valueEvaluator = valueEvaluator;
      this.random = random;
    }

    @Override
    public Decision nextWrap(Solver s) {
      int index = selectVariable();
      if (index < 0) {
        return null;
      }
      IntVar var = vars[index];
      if (valueEvaluator != null) {
        long best = var.min();
        long bestEval = Long.MAX_VALUE;
        for (long v : var.cpVar.values()) {
          long e = valueEvaluator.applyAsLong(index, v);
          if (e < bestEval) {
            bestEval = e;
            best = v;
          }
        }
        return new AssignValue(var, best);
      }
      CpVar x = var.cpVar;
      switch (valueStrategy) {
        case Solver.ASSIGN_MAX_VALUE:
          return new AssignValue(var, x.max());
        case Solver.ASSIGN_RANDOM_VALUE: {
          long[] values = x.size() <= 1_000_000 ? x.values() : null;
          if (values == null) {
            return new AssignValue(var, x.min());
          }
          return new AssignValue(var, values[random.nextInt(values.length)]);
        }
        case Solver.ASSIGN_CENTER_VALUE: {
          long mid = x.min() + (x.max() - x.min()) / 2;
          long up = x.nextValue(mid);
          long down = x.previousValue(mid);
          long v = up == Long.MAX_VALUE ? down
              : (down == Long.MIN_VALUE ? up : (up - mid <= mid - down ? up : down));
          return new AssignValue(var, v);
        }
        case Solver.SPLIT_LOWER_HALF:
          return new Split(var, x.min() + (x.max() - x.min()) / 2, false);
        case Solver.SPLIT_UPPER_HALF:
          return new Split(var, x.min() + (x.max() - x.min()) / 2, true);
        default:
          return new AssignValue(var, x.min());
      }
    }

    private int selectVariable() {
      int best = -1;
      if (varEvaluator != null) {
        long bestEval = Long.MAX_VALUE;
        for (int i = 0; i < vars.length; i++) {
          if (!vars[i].bound()) {
            long e = varEvaluator.applyAsLong(i);
            if (best < 0 || e < bestEval) {
              best = i;
              bestEval = e;
            }
          }
        }
        return best;
      }
      switch (varStrategy) {
        case Solver.CHOOSE_RANDOM: {
          int count = 0;
          for (int i = 0; i < vars.length; i++) {
            if (!vars[i].bound()) {
              count++;
              if (random.nextInt(count) == 0) {
                best = i;
              }
            }
          }
          return best;
        }
        case Solver.CHOOSE_MIN_SIZE_LOWEST_MIN:
        case Solver.CHOOSE_MIN_SIZE_HIGHEST_MIN:
        case Solver.CHOOSE_MIN_SIZE_LOWEST_MAX:
        case Solver.CHOOSE_MIN_SIZE_HIGHEST_MAX:
        case Solver.CHOOSE_MIN_SIZE:
        case Solver.CHOOSE_MAX_SIZE: {
          long bestSize = 0;
          long bestTie = 0;
          for (int i = 0; i < vars.length; i++) {
            CpVar x = vars[i].cpVar;
            if (x.isBound()) {
              continue;
            }
            long size = x.size();
            long tie;
            switch (varStrategy) {
              case Solver.CHOOSE_MIN_SIZE_LOWEST_MIN:
                tie = x.min();
                break;
              case Solver.CHOOSE_MIN_SIZE_HIGHEST_MIN:
                tie = -x.min();
                break;
              case Solver.CHOOSE_MIN_SIZE_LOWEST_MAX:
                tie = x.max();
                break;
              case Solver.CHOOSE_MIN_SIZE_HIGHEST_MAX:
                tie = -x.max();
                break;
              default:
                tie = 0;
                break;
            }
            if (varStrategy == Solver.CHOOSE_MAX_SIZE) {
              size = -size;
            }
            if (best < 0 || size < bestSize || (size == bestSize && tie < bestTie)) {
              best = i;
              bestSize = size;
              bestTie = tie;
            }
          }
          return best;
        }
        case Solver.CHOOSE_LOWEST_MIN:
        case Solver.CHOOSE_HIGHEST_MAX: {
          long bestKey = 0;
          for (int i = 0; i < vars.length; i++) {
            CpVar x = vars[i].cpVar;
            if (x.isBound()) {
              continue;
            }
            long key = varStrategy == Solver.CHOOSE_LOWEST_MIN ? x.min() : -x.max();
            if (best < 0 || key < bestKey) {
              best = i;
              bestKey = key;
            }
          }
          return best;
        }
        case Solver.CHOOSE_MAX_REGRET_ON_MIN: {
          long bestRegret = 0;
          for (int i = 0; i < vars.length; i++) {
            CpVar x = vars[i].cpVar;
            if (x.isBound()) {
              continue;
            }
            long regret = x.nextValue(x.min() + 1) - x.min();
            if (best < 0 || regret > bestRegret) {
              best = i;
              bestRegret = regret;
            }
          }
          return best;
        }
        default:
          for (int i = 0; i < vars.length; i++) {
            if (!vars[i].bound()) {
              return i;
            }
          }
          return -1;
      }
    }

    @Override
    public String toString() {
      return "Phase(" + vars.length + " variables)";
    }
  }

  /** Selects the (variable, value) pair with the smallest evaluation. */
  static final class GlobalBestPhase extends DecisionBuilder {
    private final IntVar[] vars;

    private final LongBinaryOperator eval;

    private final LongUnaryOperator tieBreaker;

    GlobalBestPhase(IntVar[] vars, LongBinaryOperator eval, LongUnaryOperator tieBreaker) {
      this.vars = vars.clone();
      this.eval = eval;
      this.tieBreaker = tieBreaker;
    }

    @Override
    public Decision nextWrap(Solver s) {
      int bestVar = -1;
      long bestValue = 0;
      long bestEval = Long.MAX_VALUE;
      long bestTie = Long.MAX_VALUE;
      for (int i = 0; i < vars.length; i++) {
        if (vars[i].bound()) {
          continue;
        }
        for (long v : vars[i].cpVar.values()) {
          long e = eval.applyAsLong(i, v);
          long t = tieBreaker == null ? 0 : tieBreaker.applyAsLong(i);
          if (bestVar < 0 || e < bestEval || (e == bestEval && t < bestTie)) {
            bestVar = i;
            bestValue = v;
            bestEval = e;
            bestTie = t;
          }
        }
      }
      return bestVar < 0 ? null : new AssignValue(vars[bestVar], bestValue);
    }
  }
}
