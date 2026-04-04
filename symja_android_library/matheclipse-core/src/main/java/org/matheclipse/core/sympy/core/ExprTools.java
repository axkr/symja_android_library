package org.matheclipse.core.sympy.core;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
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
    private TreeMap<IExpr, IExpr> dict;

    public Factors() {
      this(S.None);
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
        TreeMap<IExpr, IExpr> map = new TreeMap<IExpr, IExpr>();
        for (int i = 1; i < assoc.size(); i++) {
          IAST rule = assoc.getRule(i);
          IExpr k = rule.first();
          IExpr v = rule.second();
          map.put(k, v);
        }
        normalize(map);
        return;
      }
      this.dict = new TreeMap<IExpr, IExpr>();
      if (factors.isOne() || factors.isNone() || factors.isEmptyList()) {
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
        Map<IExpr, IExpr> powersDict = asPowersDict.getMap();
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

    public Factors(TreeMap<IExpr, IExpr> dict) {
      this(dict, false);
    }

    public Factors(TreeMap<IExpr, IExpr> dictionary, boolean copy) {
      if (copy) {
        this.dict = (TreeMap<IExpr, IExpr>) dictionary.clone();
      } else {
        this.dict = dictionary;
      }
      normalize(this.dict);
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
      // args.sortInplace();
      return args.oneIdentity1();
    }

    public Factors[] div(Factors other) {
      if (other.isZero()) {
        throw new ArithmeticException("Factors#div: division by zero");
      }
      if (isZero()) {
        return new Factors[] {new Factors(F.C0), new Factors()};
      }

      TreeMap<IExpr, IExpr> quo = (TreeMap<IExpr, IExpr>) dict.clone();
      TreeMap<IExpr, IExpr> rem = new TreeMap<IExpr, IExpr>();

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

    public Factors[] div(IExpr other) {
      return div(new Factors(other));
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

    public TreeMap<IExpr, IExpr> factorsMap() {
      return dict;
    }

    public Factors gcd(Factors other) {
      if (other.isZero()) {
        return this;
      }
      TreeMap<IExpr, IExpr> factors = new TreeMap<IExpr, IExpr>();

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

    public Factors gcd(IExpr other) {
      return gcd(new Factors(other));
    }

    @Override
    public int hashCode() {
      return dict.hashCode();
    }

    public boolean isOne() {
      return dict.size() == 0;
    }

    public boolean isZero() {
      return dict.size() == 1 && dict.get(F.C0) != null;
    }

    public Factors lcm(Factors other) {
      if (isZero() || other.isZero()) {
        return new Factors(F.C0);
      }
      TreeMap<IExpr, IExpr> factors = (TreeMap<IExpr, IExpr>) dict.clone();

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

    public Factors lcm(IExpr other) {
      return lcm(new Factors(other));
    }

    public Factors mul(Factors other) {
      TreeMap<IExpr, IExpr> factors;
      if (isZero() || other.isZero()) {
        return new Factors(F.C0);
      }
      factors = (TreeMap<IExpr, IExpr>) dict.clone();

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

    public Factors mul(IExpr other) {
      return mul(new Factors(other));
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
      TreeMap<IExpr, IExpr> self_factors = (TreeMap<IExpr, IExpr>) dict.clone();
      TreeMap<IExpr, IExpr> other_factors = (TreeMap<IExpr, IExpr>) that.dict.clone();

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

    private void normalize(TreeMap<IExpr, IExpr> dictionary) {
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
        TreeMap<IExpr, IExpr> factors = new TreeMap<IExpr, IExpr>();
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

    public Factors quo(Factors other) {
      return div(other)[0];
    }

    public Factors quo(IExpr other) {
      return quo(new Factors(other));
    }

    public Factors rem(Factors other) {
      return div(other)[1];
    }

    public Factors rem(IExpr other) {
      return rem(new Factors(other));
    }

    @Override
    public String toString() {
      return dict.toString();
    }

  }

  /**
   * Efficient representation of {@code coeff*(numer/denom)}.
   */
  public final static class Term {
    public final IExpr coeff;
    public final Factors numer;
    public final Factors denom;

    public Term(IExpr term) {
      IExpr c = F.C1;
      TreeMap<IExpr, IExpr> nMap = new TreeMap<>();
      TreeMap<IExpr, IExpr> dMap = new TreeMap<>();

      if (term.isNumber()) {
        c = term;
      } else {
        IAST mul;
        if (term.isTimes()) {
          mul = (IAST) term;
        } else {
          mul = F.Times(term);
        }

        IASTAppendable rest = F.TimesAlloc(mul.argSize());
        for (int i = 1; i <= mul.argSize(); i++) {
          IExpr arg = mul.get(i);
          if (arg.isNumber()) {
            c = F.eval(F.Times(c, arg));
          } else {
            rest.append(arg);
          }
        }

        Factors factors = new Factors(rest.oneIdentity1());
        for (Map.Entry<IExpr, IExpr> entry : factors.factorsMap().entrySet()) {
          IExpr base = entry.getKey();
          IExpr exp = entry.getValue();

          boolean isNegative = false;
          if (exp.isNumber() && exp.isNegative()) {
            isNegative = true;
          } else if (exp.isTimes() && exp.first().isNumber() && exp.first().isNegative()) {
            isNegative = true;
          }

          if (isNegative) {
            dMap.put(base, F.eval(exp.negate()));
          } else {
            nMap.put(base, exp);
          }
        }
      }

      this.coeff = c;
      this.numer = new Factors(nMap);
      this.denom = new Factors(dMap);
    }

    public Term(IExpr coeff, Factors numer, Factors denom) {
      this.coeff = coeff;
      this.numer = numer;
      this.denom = denom;
    }

    public IExpr asExpr() {
      IExpr n = this.numer.asExpr();
      IExpr d = this.denom.asExpr();
      return F.eval(F.Times(this.coeff, n, F.Power(d, F.CN1)));
    }

    public Term gcd(Term other) {
      IExpr newCoeff = fractionalGCD(this.coeff, other.coeff);
      Factors newNumer = this.numer.gcd(other.numer);
      Factors newDenom = this.denom.gcd(other.denom);
      return new Term(newCoeff, newNumer, newDenom);
    }

    public Term lcm(Term other) {
      IExpr newCoeff = fractionalLCM(this.coeff, other.coeff);
      Factors newNumer = this.numer.lcm(other.numer);
      Factors newDenom = this.denom.lcm(other.denom);
      return new Term(newCoeff, newNumer, newDenom);
    }

    public Term quo(Term other) {
      IExpr newCoeff = F.eval(F.Divide(this.coeff, other.coeff));
      Factors[] nDiv = this.numer.div(other.numer);
      Factors[] dDiv = this.denom.div(other.denom);
      Factors newNumer = nDiv[0].mul(dDiv[1]);
      Factors newDenom = dDiv[0].mul(nDiv[1]);
      return new Term(newCoeff, newNumer, newDenom);
    }
  }

  /**
   * Helper function for {@link #gcdTerms(IExpr, boolean, boolean, boolean)}.
   */
  public static IExpr[] _gcdTerms(IAST terms, boolean isprimitive, boolean fraction) {
    int size = terms.argSize();
    if (size == 0) {
      return new IExpr[] {F.C0, F.C0, F.C1};
    }

    Term[] termArr = new Term[size];
    for (int i = 0; i < size; i++) {
      termArr[i] = new Term(terms.get(i + 1));
    }

    if (size == 1) {
      return new IExpr[] {termArr[0].coeff, termArr[0].numer.asExpr(), termArr[0].denom.asExpr()};
    }

    Term cont = termArr[0];
    for (int i = 1; i < size; i++) {
      cont = cont.gcd(termArr[i]);
    }

    for (int i = 0; i < size; i++) {
      termArr[i] = termArr[i].quo(cont);
    }

    IExpr denomExpr;
    IExpr numerExpr;
    if (fraction) {
      Factors denom = termArr[0].denom;
      for (int i = 1; i < size; i++) {
        denom = denom.lcm(termArr[i].denom);
      }
      denomExpr = denom.asExpr();

      IASTAppendable numers = F.PlusAlloc(size);
      for (int i = 0; i < size; i++) {
        Factors n = termArr[i].numer.mul(denom.quo(termArr[i].denom));
        numers.append(F.eval(F.Times(termArr[i].coeff, n.asExpr())));
      }
      numerExpr = numers;
    } else {
      IASTAppendable numers = F.PlusAlloc(size);
      for (int i = 0; i < size; i++) {
        numers.append(termArr[i].asExpr());
      }
      numerExpr = numers;
      denomExpr = F.C1;
    }

    IExpr contExpr = cont.asExpr();
    numerExpr = F.eval(numerExpr);

    return new IExpr[] {contExpr, numerExpr, denomExpr};
  }

  private static IExpr fractionalGCD(IExpr a, IExpr b) {
    if (a.isRational() && b.isRational()) {
      IInteger numA = a.isFraction() ? ((IFraction) a).numerator() : (IInteger) a;
      IInteger denA = a.isFraction() ? ((IFraction) a).denominator() : F.C1;
      IInteger numB = b.isFraction() ? ((IFraction) b).numerator() : (IInteger) b;
      IInteger denB = b.isFraction() ? ((IFraction) b).denominator() : F.C1;

      IExpr numGCD = F.eval(F.GCD(numA, numB));
      IExpr denLCM = F.eval(F.LCM(denA, denB));
      return F.eval(F.Divide(numGCD, denLCM));
    }
    return F.eval(F.GCD(a, b));
  }

  private static IExpr fractionalLCM(IExpr a, IExpr b) {
    if (a.isRational() && b.isRational()) {
      IInteger numA = a.isFraction() ? ((IFraction) a).numerator() : (IInteger) a;
      IInteger denA = a.isFraction() ? ((IFraction) a).denominator() : F.C1;
      IInteger numB = b.isFraction() ? ((IFraction) b).numerator() : (IInteger) b;
      IInteger denB = b.isFraction() ? ((IFraction) b).denominator() : F.C1;

      IExpr numLCM = F.eval(F.LCM(numA, numB));
      IExpr denGCD = F.eval(F.GCD(denA, denB));
      return F.eval(F.Divide(numLCM, denGCD));
    }
    return F.eval(F.LCM(a, b));
  }

  /**
   * Compute the GCD of <code>terms</code> and put them together.
   *
   * @param terms the expression or sequence to process
   * @return the combined expression
   */
  public static IExpr gcdTerms(IExpr terms) {
    return gcdTerms(terms, false, true, true);
  }

  /**
   * Compute the GCD of <code>terms</code> and put them together.
   *
   * @param terms the expression or sequence to process
   * @param fraction whether to put the expression over a common denominator
   * @return the combined expression
   */
  public static IExpr gcdTerms(IExpr terms, boolean fraction) {
    return gcdTerms(terms, false, true, fraction);
  }

  /**
   * Compute the GCD of <code>terms</code> and put them together.
   *
   * @param terms the expression or sequence to process
   * @param isprimitive if true, bypass primitive extraction
   * @param clear whether to remove integers from the denominator of Add
   * @param fraction whether to put the expression over a common denominator
   * @return the combined expression
   */
  public static IExpr gcdTerms(IExpr terms, boolean isprimitive, boolean clear, boolean fraction) {
    if (!terms.isPlus() && !terms.isList()) {
      if (terms.isAtom()) {
        return terms;
      }
      if (terms.isTimes()) {
        IAST mul = (IAST) terms;
        IASTAppendable newMul = F.TimesAlloc(mul.argSize());
        for (int i = 1; i <= mul.argSize(); i++) {
          newMul.append(gcdTerms(mul.get(i), isprimitive, clear, fraction));
        }
        return F.eval(newMul);
      }
      return terms;
    }

    IAST ast = (IAST) terms;
    IExpr[] parts = _gcdTerms(ast, isprimitive, fraction);
    IExpr cont = parts[0];
    IExpr numer = parts[1];
    IExpr denom = parts[2];

    return F.eval(F.Times(cont, numer, F.Power(denom, F.CN1)));
  }

  // /**
  // * Compute the GCD of <code>terms</code> and put them together.
  // *
  // * @param terms
  // * @return
  // */
  // public static IExpr gcdTerms(IExpr terms) {
  // return S.Cancel.of(EvalEngine.get(), terms);
  // }

  public static IExpr normal(IExpr self, IExpr others) {
    if (!others.isAST(S.Factor)) {
      others = S.Factor.of(others);
      if (others.isZero()) {

      }
    }
    return F.NIL;
  }
}
