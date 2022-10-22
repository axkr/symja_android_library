package org.matheclipse.core.sympy.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DefaultDict;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Pair;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.sympy.exception.ValueError;

public class ExprTools {

  /**
   * Efficient representation of <code>f_1*f_2*...*f_n</code>.
   *
   */
  public final static class Factors {
    // https://github.com/sympy/sympy/blob/8f90e7f894b09a3edc54c44af601b838b15aa41b/sympy/core/exprtools.py#L284
    private HashMap<IExpr, IExpr> dict;

    public Factors(HashMap<IExpr, IExpr> dict) {
      this(dict, false);
    }

    public Factors() {
      this(S.None);
    }

    public Factors(HashMap<IExpr, IExpr> dictionary, boolean copy) {
      if (copy) {
        this.dict = (HashMap<IExpr, IExpr>) dictionary.clone();
      } else {
        this.dict = dictionary;
      }
      normalize(this.dict);
    }

    /**
     * Initialize Factors from {@link IExpr}
     * 
     * @param factors
     */
    public Factors(IExpr factors) {
      if (factors instanceof IAssociation) {
        // should be a dict-like association
        IAssociation assoc = (IAssociation) factors;
        HashMap<IExpr, IExpr> map = new HashMap<IExpr, IExpr>();
        for (int i = 1; i < assoc.size(); i++) {
          IAST rule = assoc.getRule(i);
          IExpr k = rule.first();
          IExpr v = rule.second();
          map.put(k, v);
        }
        normalize(map);
        return;
      }
      this.dict = new HashMap<IExpr, IExpr>();
      if (factors.isOne() || factors == S.None || factors.isEmptyList()) {
        //
      } else if (factors.isZero()) {
        dict.put(F.C0, F.C1);
      } else if (factors.isInfinity()) {
        dict.put(factors, F.C1);
      } else if (factors.isReal()) {
        INumber n = (INumber) factors;
        if (n.isNegative()) {
          dict.put(F.CN1, F.C1);
          n = n.negate();
        }
        if (!n.equals(F.C1)) {
          if (n.isFraction()) {
            IFraction frac = (IFraction) n;
            if (!frac.numerator().isOne()) {
              dict.put(frac.numerator(), F.C1);
            }
            dict.put(frac.denominator(), F.CN1);
          } else {
            dict.put(n, F.C1);
          }
          // throw new ValueError("Expected Float|Fraction|Integer, not: " + n);
        }
      } else if (factors instanceof IDataExpr) {
        dict.put(factors, F.C1);
      } else {
        Pair pair = Expr.argsCnc(factors);
        IASTAppendable c = (IASTAppendable) pair.first();
        IASTAppendable nc = (IASTAppendable) pair.second();

        int i = c.count(x -> x.equals(F.CI));
        for (int j = 0; j < i; j++) {
          c.remove(F.CI);
        }
        DefaultDict<IExpr> asPowersDict = c.apply(S.Times).asPowersDict();
        HashMap<IExpr, IExpr> powersDict = (HashMap<IExpr, IExpr>) asPowersDict.getMap();
        Iterator<Entry<IExpr, IExpr>> it = powersDict.entrySet().iterator();
        while (it.hasNext()) {
          Entry<IExpr, IExpr> entry = it.next();
          IExpr f = entry.getKey();
          IExpr fValue = entry.getValue();
          if (f.isFraction()) {
            IRational rat = (IRational) f;
            IInteger p = rat.numerator();
            IInteger q = rat.denominator();
            IExpr pValue = dict.get(p);
            IExpr qValue = dict.get(q);
            dict.put(p, (pValue == null ? F.C0 : pValue).plus(fValue));
            dict.put(q, (qValue == null ? F.C0 : qValue).subtract(fValue));
          } else {
            dict.put(f, fValue);
          }
        }
        if (i > 0) {
          IExpr fValue = dict.get(F.CI);
          dict.put(F.CI, (fValue == null ? F.C0 : fValue).plus(F.ZZ(i)));
        }
        if (nc.argSize() > 0) {
          dict.put(nc.apply(S.Times), F.C1);
        }
      }
    }

    private void normalize(HashMap<IExpr, IExpr> dictionary) {
      this.dict = dictionary;
      IExpr i1 = F.C1;
      Iterator<IExpr> it = dictionary.keySet().iterator();
      while (it.hasNext()) {
        IExpr k = it.next();
        if (k.equals(F.CI) || k.isOne() || k.isMinusOne()) {
          IExpr v = dict.get(k);
          if (v.isNumber()) {
            i1 = i1.times(k.pow(v));
            it.remove();
          }
        }
      }
      if (!i1.equals(F.C1)) {
        IAST args = i1.isTimes() ? (IAST) i1 : F.Times(i1);
        for (int i = 1; i < args.size(); i++) {
          IExpr a = args.get(i);
          if (a.isMinusOne() || a.isOne()) {
            dict.put(a, F.C1);
          } else if (a.isImaginaryUnit()) {
            if (a instanceof IComplexNum) {
              dict.put(F.CI, F.C1);
              dict.put(F.CD1, F.C1);
            } else {
              dict.put(F.CI, F.C1);
            }
          } else if (a.isNegativeImaginaryUnit()) {
            dict.put(F.CN1, F.C1);
            dict.put(F.CI, F.C1);
          } else if (a.isPower()) {
            IExpr v = dict.get(a.base());
            dict.put(a.base(), (v != null ? v : F.C0).plus(a.exponent()));
          } else {
            throw new ValueError("unexpected factor in i1: " + a);
          }
        }
      }
    }

    @Override
    public int hashCode() {
      return dict.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Factors other = (Factors) obj;
      return dict.equals(other.dict);
    }

    @Override
    public String toString() {
      return dict.toString();
    }

    /**
     * Return the underlying expression.
     * 
     * @return
     */
    public IExpr asExpr() {
      IASTAppendable args = F.TimesAlloc(dict.size());
      for (Map.Entry<IExpr, IExpr> entry : dict.entrySet()) {
        IExpr factor = entry.getKey();
        IExpr exp = entry.getValue();
        if (exp.isOne()) {
          args.append(factor);
        } else if (exp.isInteger()) {
          Pair pair = factor.asBaseExp();
          IExpr b = pair.first();
          IExpr e = pair.second();
          e = Mul.keepCoeff(exp, e);
          args.append(F.Power(b, e));
        } else {
          args.append(F.Power(factor, exp));
        }
      }
      return args.oneIdentity1();
    }

    public boolean isZero() {
      return dict.size() == 1 && dict.get(F.C0) != null;
    }

    public boolean isOne() {
      return dict.size() == 0;
    }

    public Factors mul(IExpr other) {
      return mul(new Factors(other));
    }

    public Factors mul(Factors other) {
      HashMap<IExpr, IExpr> factors;
      if (isZero() || other.isZero()) {
        return new Factors(F.C0);
      }
      factors = (HashMap<IExpr, IExpr>) dict.clone();

      for (Map.Entry<IExpr, IExpr> entry : other.dict.entrySet()) {
        IExpr factor = entry.getKey();
        IExpr exp = entry.getValue();
        if (factors.containsKey(factor)) {
          exp = factors.get(factor).plus(exp);
          if (exp.isZero()) {
            factors.remove(factor);
            continue;
          }
        }

        factors.put(factor, exp);
      }
      return new Factors(factors);
    }

    public Factors[] normal(IExpr other) {
      return normal(new Factors(other));
    }

    public Factors[] normal(Factors that) {
      if (that.isZero()) {
        return new Factors[] {new Factors(), new Factors(F.C0)};
      }
      if (isZero()) {
        return new Factors[] {new Factors(F.C0), new Factors()};
      }
      HashMap<IExpr, IExpr> self_factors = (HashMap<IExpr, IExpr>) dict.clone();
      HashMap<IExpr, IExpr> other_factors = (HashMap<IExpr, IExpr>) that.dict.clone();

      for (Map.Entry<IExpr, IExpr> entry : dict.entrySet()) {
        IExpr factor = entry.getKey();
        IExpr self_exp = entry.getValue();
        IExpr other_exp = that.dict.get(factor);
        if (other_exp == null) {
          continue;
        }
        IExpr exp = self_exp.subtract(other_exp);

        if (exp.isZero()) {
          self_factors.remove(factor);
          other_factors.remove(factor);
        } else if (exp.isNumber()) {
          if (exp.isPositive()) {
            self_factors.put(factor, exp);
            other_factors.remove(factor);
          } else {
            self_factors.remove(factor);
            other_factors.put(factor, exp.negate());
          }
        } else {
          // TODO self_exp.extract_additively(other_exp)
          IExpr r = Expr.extractAdditively(self_exp, other_exp);
          if (r.isPresent()) {
            if (!r.isZero()) {
              self_factors.put(factor, r);
              other_factors.remove(factor);
            } else {// should be handled already
              self_factors.remove(factor);
              other_factors.remove(factor);
            }
          } else {
            Pair sp = self_exp.asCoeffAdd();
            IExpr sc = sp.first();
            IExpr sa = F.eval(sp.second());
            if (!sc.isZero()) {
              Pair op = other_exp.asCoeffAdd();
              IExpr oc = op.first();
              IExpr oa = F.eval(op.second());
              IExpr diff = sc.subtract(oc);
              if (diff.isPositive()) {
                self_factors.put(factor, self_exp.subtract(oc));
                other_exp = oa;
              } else if (diff.isNegative()) {
                self_factors.put(factor, self_exp.subtract(sc));
                other_factors.put(factor, other_exp.subtract(sc));
                other_exp = oa.subtract(diff);
              } else {
                self_factors.put(factor, sa);
                other_exp = oa;
              }
            }
            if (!other_exp.isZero()) {
              other_factors.put(factor, other_exp);
            } else {
              other_factors.remove(factor);
            }
          }
        }

      }
      return new Factors[] {new Factors(self_factors), new Factors(other_factors)};
    }

    public Factors[] div(IExpr other) {
      return div(new Factors(other));
    }

    public Factors[] div(Factors other) {
      if (other.isZero()) {
        throw new ArithmeticException("Factors#div: division by zero");
      }
      if (isZero()) {
        return new Factors[] {new Factors(F.C0), new Factors()};
      }

      HashMap<IExpr, IExpr> quo = (HashMap<IExpr, IExpr>) dict.clone();
      HashMap<IExpr, IExpr> rem = new HashMap<IExpr, IExpr>();

      for (Map.Entry<IExpr, IExpr> entry : other.dict.entrySet()) {
        IExpr factor = entry.getKey();
        IExpr exp = entry.getValue();
        if (quo.containsKey(factor)) {
          IExpr quoFactor = quo.get(factor);
          IExpr d = quoFactor.subtract(exp);
          if (d.isNumber()) {
            if (d.isNegative() || d.isZero()) {
              quo.remove(factor);
            }
            if (d.isPositive() || d.isZero()) {
              if (!d.isZero()) {
                quo.put(factor, d);
              }
              continue;
            }
            exp = d.negate();
          } else {
            IExpr r = Expr.extractAdditively(quoFactor, exp);
            if (r.isPresent()) {
              if (r.isZero()) {
                quo.remove(factor);
              } else {
                quo.put(factor, r);
              }
            } else {
              IExpr otherExp = exp;
              Pair ps = quoFactor.asCoeffAdd();
              IExpr sc = ps.first();
              IExpr sa = F.eval(ps.second());
              if (!sc.isZero()) {
                Pair po = otherExp.asCoeffAdd();
                IExpr oc = po.first();
                IExpr oa = F.eval(po.second());
                IExpr diff = sc.subtract(oc);
                if (diff.isPositive()) {
                  quo.put(factor, quoFactor.subtract(oc));
                  otherExp = oa;
                } else if (diff.isNegative()) {
                  quo.put(factor, quoFactor.subtract(sc));
                  otherExp = oa.subtract(diff);
                } else {
                  quo.put(factor, sa);
                  otherExp = oa;
                }

              }
              if (!otherExp.isZero()) {
                rem.put(factor, otherExp);
              } else {
                assert rem.get(factor) == null;
              }
            }
            continue;
          }
        }

        rem.put(factor, exp);
      }
      return new Factors[] {new Factors(quo), new Factors(rem)};
    }

    public Factors quo(IExpr other) {
      return quo(new Factors(other));
    }

    public Factors quo(Factors other) {
      return div(other)[0];
    }

    public Factors rem(IExpr other) {
      return rem(new Factors(other));
    }

    public Factors rem(Factors other) {
      return div(other)[1];
    }

    /**
     * Return this raised to a non-negative integer power.
     * 
     * @param other
     * @return
     */
    public Factors pow(IExpr other) {
      int n = other.toIntDefault();
      if (n >= 0) {
        return pow(n);
      }
      throw new ValueError("expected non-negative integer, got " + other);
    }

    private Factors pow(int n) {
      if (n >= 0) {
        HashMap<IExpr, IExpr> factors = new HashMap<IExpr, IExpr>();
        IExpr other = F.ZZ(n);
        for (Map.Entry<IExpr, IExpr> entry : dict.entrySet()) {
          IExpr factor = entry.getKey();
          IExpr exp = entry.getValue();
          factors.put(factor, exp.times(other));
        }
        return new Factors(factors);
      }
      throw new ValueError("expected non-negative integer, got " + n);
    }

    public Factors gcd(IExpr other) {
      return gcd(new Factors(other));
    }

    public Factors gcd(Factors other) {
      if (other.isZero()) {
        return this;
      }
      HashMap<IExpr, IExpr> factors = new HashMap<IExpr, IExpr>();

      for (Map.Entry<IExpr, IExpr> entry : dict.entrySet()) {
        IExpr factor = entry.getKey();
        IExpr exp = entry.getValue();
        IExpr otherValue = other.dict.get(factor);
        if (otherValue != null) {
          IExpr lt = exp.subtract(otherValue);
          if (lt.isNegative() || lt.isZero()) {
            factors.put(factor, exp);
          } else if (lt.isPositive()) {
            factors.put(factor, otherValue);
          }
        }
      }
      return new Factors(factors);
    }

    public Factors lcm(IExpr other) {
      return lcm(new Factors(other));
    }

    public Factors lcm(Factors other) {
      if (isZero() || other.isZero()) {
        return new Factors(F.C0);
      }
      HashMap<IExpr, IExpr> factors = (HashMap<IExpr, IExpr>) dict.clone();

      for (Map.Entry<IExpr, IExpr> entry : other.dict.entrySet()) {
        IExpr factor = entry.getKey();
        IExpr exp = entry.getValue();
        IExpr value = factors.get(factor);
        if (value != null) {
          exp = F.Max.of(exp, value);
        }
        factors.put(factor, exp);
      }
      return new Factors(factors);
    }

    public HashMap<IExpr, IExpr> factorsMap() {
      return dict;
    }

  }

  /**
   * Compute the GCD of <code>terms</code> and put them together.
   * 
   * @param terms
   * @return
   */
  public static IExpr gcdTerms(IExpr terms) {
    return S.Cancel.of(EvalEngine.get(), terms);
  }

  public static IExpr normal(IExpr self, IExpr others) {
    if (!others.isAST(S.Factor)) {
      others = S.Factor.of(others);
      if (others.isZero()) {

      }
    }
    return F.NIL;
  }
}
