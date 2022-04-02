package org.matheclipse.core.reflection.system;

import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Stores (expr, dummy) pairs, and how to rewrite expressions.
 * 
 * Explanation
 * ===========
 * 
 * The gruntz algorithm needs to rewrite certain expressions in term of a new
 * variable w.
 * 
 * Consider the expression::
 * 
 * exp(x - exp(-x)) + exp(x) + x.
 * 
 * The MRV set is { Exp(x), Exp(-x), Exp(x - Exp(-x)) }.
 * We introduce corresponding dummy variables d1, d2, d3 and rewrite:
 * 
 * d3 + d1 + x.
 * 
 * This class first of all keeps track of the mapping expression->variable, i.e.
 * will at this stage be a dictionary:
 * 
 * { Exp(x) -> d1, Exp(-x) -> d2, Exp(x - Exp(-x)) -> d3}.
 *
 * [It turns out to be more convenient this way round.]
 * But sometimes expressions in the MRV set have other expressions from the
 * MRV set as subexpressions, and we need to keep track of that as well. In
 * this case, d3 is really exp(x - d2), so rewrites at this stage is:
 * 
 * { d3 -> Exp(x - d2) }.
 * 
 * The function rewrite uses all this information to correctly rewrite our
 * expression in terms of w. In this case w can be chosen to be Exp(-x),
 * i.e. d2. The correct rewriting then is:
 * 
 * Exp(-w)/w + 1/w + x
 */
class SubsSet {
  private Map<IExpr, IExpr> rules;

  public SubsSet() {
    this.rules = new IdentityHashMap<IExpr, IExpr>();
  }

  public IExpr getRule(IExpr key) {
    IExpr value = this.rules.get(key);
    if (value == null) {
      value = F.Dummy();
      this.rules.put(key, value);
    }
    return value;
  }

  /**
   * Substitute the variables with expressions
   */
  public IExpr do_subs(IExpr expression) {
    Iterator<IdentityHashMap.Entry<IExpr, IExpr>> iterator
      = this.rules.entrySet().iterator();

    while (itr.hasNext()) {
      IdentityHashMap.Entry<Integer, String> entry = iterator.next();
      e.replaceAll(F.Rule(entry.getKey(), entry.getValue()));
    }

    return e;
  }

  /**
   * Tell whether or not this and s have non-empty intersection
   */
  public boolean meets(SubsSet s) {
    Iterator<IdentityHashMap.Entry<IExpr, IExpr>> iterator
      = this.rules.entrySet().iterator();

    while(iterator.hasNext()){
      IdentityHashMap.Entry<Integer,String> entry = iterator.next();
      if (s.rule.get(entry.getKey())) return true;
    }

    return false;
  }

  /**
   * Compute the union of this and s
   */
  public SubsSet union(SubsSet s) {
    Map<IExpr, IExpr> result = IdentityHashMap<IExpr, IExpr>(this.rules);

    Iterator<IdentityHashMap.Entry<IExpr, IExpr>> iterator
      = s.rules.entrySet().iterator();

    while (iterator.hasNext()) {
      IdentityHashMap.Entry<Integer, String> entry = iterator.next();
      if (result.rules.get(entry.getKey()) == null) {
        result.rules.put(entry.getKey(), entry.getValue())
      }
    }

    return result;
  }
}

class Pair<T1, T2> {
  public T1 first;
  public T2 second;

  public Pair(T1 first, T2 second) {
    this.first = first;
    this.second = second;
  }
}

/**
 * See issue
 * <a href="https://github.com/axkr/symja_android_library/issues/455">#455
 * Implement the
 * Gruntz algorithm for limit calculation</a>
 *
 * This implements the Gruntz algorithm for calculating limit<sub>x->p</sub>
 * f(x)
 */
public class NewLimit extends AbstractEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  /** Direction of limit computation */
  private static enum Direction {
    /** Compute the limit approaching from larger real values. */
    FROM_ABOVE(-1),

    /**
     * Compute the limit approaching from larger or smaller real values
     * automatically.
     * FIXME: this isn't supported by the Gruntz algorithm
     */
    TWO_SIDED(0),

    /** Compute the limit approaching from smaller real values. */
    FROM_BELOW(1);

    private int direction;

    /**
     * Convert the direction <code>FROM_ABOVE, TWO_SIDED, FROM_BELOW</code> to the
     * corresponding
     * value <code>-1, 0, 1</code>
     *
     * For infinite p the direction doesn't matter.
     * 
     * @return
     */
    int toInt() {
      return direction;
    }

    private Direction(int direction) {
      this.direction = direction;
    }
  }

  public NewLimit() {
  }

  /**
   * @return "<" if a < b, "=" for a == b, ">" for a > b
   */
  private String compare(IExpr a, IExpr b, IExpr rule, EvalEngine engine) {
    IExpr logA;
    if(a.head() == S.Power && a.first() == F.E){
      logA = a.rest();
    } else {
      logA = F.Log(a);
    }

    IExpr logB;
    if (b.head() == S.Power && b.first() == F.E) {
      logB = b.rest();
    } else {
      logB = F.Log(b);
    }

    IExpr comparsion = limitInf(logA.divide(logB), rule, engine);
    if (comparsion.isZero()) {
      return "<";
    } else if (comparsion.isInfinite()) {
      return ">";
    } else {
      return "=";
    }
  }

  /**
   * Computes the maximum of two sets of expressions f and g, which
   * are in the same comparability class, i.e. max() compares (two elements of)
   * f and g.
   * 
   * @return (f, expsf) if f > g, (g, expsg) if g > f, (union, expsboth)
   *         otherwise.
   */
  private Pair<SubsSet, IExpr> MRVMax3(
      SubsSet f,
      IExpr expsf,
      SubsSet g,
      IExpr expsg,
      SubsSet union,
      IExpr expsboth,
      IExpr rule,
      EvalEngine engine) {
    if (f == new SubsSet()) {
      return new Pair(g, expsg);
    } else if (g == new SubsSet()) {
      return new Pair(f, expsf);
    } else if (f.meets(g)) {
      return new Pair(union, expsboth);
    }

    String c = compare(list(f.keys())[0], list(g.keys())[0], rule.first(), engine);
    if (c == ">") {
      return new Pair(f, expsf);
    } else if (c == "<") {
      return new Pair(g, expsg);
    } else {
      return new Pair(union, expsboth);
    }
  }

  /**
   * Differently from SymPy MRV, this function makes a lot of assumptions
   * about Limit, e.g. "there is a rule for Limit(k_Number, k->p)"
   *
   * @return a subset of most rapidly varying (MRV) subexpressions of f
   */
  private Pair<SubsSet, IExpr> MRV(IExpr f, IExpr rule, EvalEngine engine) {
    if(f.head() == S.Plus || f.head() == S.Times) {
      i, d = f.as_independent(rule.first()); // throw away x-independent terms
      Pair<> pair = MRV(d, rule, engine);
	    return new Pair(pair.first, f.func(i, pair.first));
    } else if (f.head() == S.Power && f.first() != S.E) {
      IExpr e1 = F.C1;
      IExpr b1;
      while(f.head() == S.Power) {
        b1 = f.first();
        e1 = e1.multiply(f.rest());
        f = b1;
      }

      if (b1 == F.C1) {
        return new Pair<>(new SubsSet(), b1);
      }

      if(e1.has(rule.second())) {
        IExpr baseLimit = limitInf(b1, rule, engine);

        if(baseLimit == F.C1 ){
          return MRV(F.Exp(e1.multiply(b1.subtract(1))), rule, engine);
        }

        return MRV(F.Exp(e1.multiply(F.Log(b1))), rule, engine);
      } else {
        Pair<> pair = MRV(b1, rule, engine);
        return new Pair(pair.first, pair.second.pow(e1));
      }
    } else if(f.head() == S.Log) {
      Pair<> pair = MRV(f.first(), rule, engine);
      return new Pair(pair.first, F.Log(pair.second));
    } else if(f.head() == S.Exp) {
      // if a product has an infinite factor the result will be
      // infinite if there is no zero, otherwise NaN; here, we
      // consider the result infinite if any factor is infinite
      IExpr limit = limitInf(f.rest(), rule, engine);
      if(limit.isInfinite()) {
        SubsSet s1 = new SubsSet();
        IExpr e1 = s1.getRule(f);
        Pair<> pair = MRV(f.rest(), rule, engine);
        IExpr su = s1.union(pair.first)[0];
        return MRVMax3(
          s1,
          e1,
          pair.first,
          F.Exp(pair.second),
          su,
          e1,
          rule,
          engine
        );
      } else {
        Pair<> pair = MRV(f.rest(), rule, engine);
        return new Pair(pair.first, F.Exp(pair.second));
      }
    } else if (f.isFunction()) {
      l = [MRV(a, x, engine) for a in f.args]
      l2 = [s for (s, _) in l if s != SubsSet()]
      if len(l2)!=1:
        // e.g. something like BesselJ(x, x)
        // raise NotImplementedError("MRV set computation for functions in"
        // " several variables not implemented.")
        s,ss=l2[0],SubsSet()args=[ss.do_subs(x[1])for x in l]
		return s, f.func(*args)}
      else if(f.head() == S.D) {
        // raise NotImplementedError("MRV set computation for derviatives"
        // " not implemented yet.")
      }
      // raise NotImplementedError(
      // "Don't know how to calculate the mrv of '%s'" % e)
  }

  /**
   * @return (c0, e0) for f
   */
  private IExpr[] MRVLeadTerm(IAST f, IAST rule) {
    Pair<> pair = MRV(f, x);
    if (x in pair.first) {
      // move the whole omega up (exponentiate each term):
      Omega_up = moveup2(pair.first, x);
      exps_up = moveup([pair.second], x)[0];
      // NOTE: there is no need to move this down!
      pair.first = Omega_up;
      pair.second = exps_up;
    }
    //
    // The positive dummy, w, is used here so log(w*2) etc. will expand;
    // a unique dummy is needed in this algorithm
    //
    // For limits of complex functions, the algorithm would have to be
    // improved, or just find limits of Re and Im components separately.
    //
    ISymbol w = F.Dummy("w", positive=True);
    f, logw = rewrite(exps, Omega, x, w)
    series = calculate_series(f, w, logx=logw)
    try {
      lt = series.leadterm(w, logx=logw);
    } catch (ValueError, PoleError) {
      lt = f.as_coeff_exponent(w)
      // as_coeff_exponent won't always split in required form. It may simply
      // return (f, 0) when a better form may be obtained. Example (-x)**(-pi)
      // can be written as (-1**(-pi), -pi) which as_coeff_exponent does not
      // return 
      if (lt[0].has(w)) {
        base = f.as_base_exp()[0].as_coeff_exponent(w);
        ex = f.as_base_exp()[1];
        lt = (base[0] ** ex, base[1] * ex);
        return (lt[0].subs(log(w), logw), lt[1]);
      }
    }
  }

  /**
   * Evaluate limit f(x) as x->oo
   */
  private IExpr limitInf(IExpr f, IExpr rule, EvalEngine engine) {
    // TODO: simplify Sqrt(rule.first())^2 to rule.first()
    // as if rule.first() was positive.

    c0, e0 = MRVLeadTerm(e, x);
    sig = sign(e0, x);
    if (sig == 1) {
      return S.Zero; // e0>0: lim f = 0
    } else if (sig == -1) {
      // e0<0: lim f = +-oo (the sign depends on the sign of c0)

      if (c0.match(I*Wild("a", exclude=[I]))) {
        return c0*oo;
      }

      s = sign(c0, x);
      // the leading term shouldn't be 0:
      if (s == 0) {
        raise ValueError("Leading term should not be 0");
        return s*oo;
      } else if (sig == 0) {
        return limitinf(c0, x); // e0=0: lim f = lim c0
      } else {
        raise ValueError("{} could not be evaluated".format(sig));
      }
    }
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IOFunctions.printExperimental(S.NewLimit);
    if (ast.argSize() > 1) {
      // Convert all limits to limit x->oo.
      // The sign of x is handled in limitinf.
      IExpr f = ast.arg1();
      IExpr p = ast.arg2();

      if (p.isRule()) {
        if (p.isInfinity()) {
        } else if (p.isNegativeInfinity()) {
          f.replaceAll(F.Rule(p.first(), p.first().negate()));
        } else {
          Direction direction = Direction.TWO_SIDED; // no direction as default
          if (ast.isAST3()) {
            final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
            IExpr option = options.getOption(S.Direction);
            if (option.isPresent()) {
              if (option.isOne() || option.equals(S.Automatic)) {
                direction = Direction.FROM_BELOW;
              } else if (option.isMinusOne()) {
                direction = Direction.FROM_ABOVE;
              } else {
                LOGGER.log(engine.getLogLevel(), "{}: direction option expected at position 2!",
                    ast.topHead());
                return F.NIL;
              }
            } else {
              LOGGER.log(engine.getLogLevel(), "{}: direction option expected at position 2!", ast.topHead());
              return F.NIL;
            }

          }

          if (direction == Direction.FROM_BELOW) {
            f.replaceAll(F.Rule(p.first(), ((IExpr) p.second()).add(((IExpr) p.first()).reciprocal())));
          } else {
            f.replaceAll(F.Rule(p.first(), ((IExpr) p.second()).subtract(((IExpr) p.first()).reciprocal())));
          }
        }

        return limitInf(f, p, engine);
      }
    }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
  }
}
