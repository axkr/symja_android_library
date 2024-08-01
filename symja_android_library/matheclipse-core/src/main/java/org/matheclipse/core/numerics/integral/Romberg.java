package org.matheclipse.core.numerics.integral;

import java.util.function.DoubleUnaryOperator;
import org.matheclipse.core.numerics.utils.Constants;
import org.matheclipse.core.numerics.utils.SimpleMath;

/**
 * Implements three versions of the Romberg numerical integrator:
 * <ol>
 * <li>Richardson is the simplest and is based on Richardson extrapolation [4]</li>
 * <li>CADRE is a translation of the Cautious Adaptive Romberg Extrapolator (CADRE) [1, 2]</li>
 * <li>Havie is a translation of the Havie integrator introduced in [3]</li>
 * </ol>
 * All three versions are translated from the INTLIB package by John Burkardt.
 * 
 * <p>
 * References:
 * <ul>
 * <li>[1] Philip Davis, Philip Rabinowitz. Methods of Numerical Integration, Second Edition, Dover,
 * 2007.</li>
 * <li>[2] de Boor, Carl. "CADRE: An algorithm for numerical quadrature." Mathematical software.
 * Academic Press, 1971. 417-449.</li>
 * <li>[3] Robert N. Kubik. 1965. Algorithm 257: Havie integrator. Commun. ACM 8, 6 (June 1965),
 * 381. DOI:https://doi.org/10.1145/364955.364978</li>
 * <li>[4] CF Dunkl, Romberg quadrature to prescribed accuracy, SHARE file number 7090-1481
 * TYQUAD</li>
 * </ul>
 * </p>
 */
public final class Romberg extends Quadrature {

  private final double myRelTol;
  private final RombergExtrapolationMethod myMethod;

  /**
   * The extrapolation method to use for Romberg integration.
   */
  public static enum RombergExtrapolationMethod {
    RICHARDSON, CADRE, HAVIE
  }

  /**
   * Creates a new instance of the Romberg integrator.
   * 
   * @param relativeTolerance the smallest acceptable relative change in integral estimates in
   *        consecutive iterations that indicates the algorithm has converged
   * @param absoluteTolerance the smallest acceptable absolute change in integral estimates in
   *        consecutive iterations that indicates the algorithm has converged
   * @param maxEvaluations the maximum number of function evaluations
   * @param method the extrapolation method to use
   */
  public Romberg(final double absoluteTolerance, final double relativeTolerance,
      final int maxEvaluations, final RombergExtrapolationMethod method) {
    super(absoluteTolerance, maxEvaluations);
    myRelTol = relativeTolerance;
    myMethod = method;
  }

  /**
   * Creates a new instance of the Romberg integrator.
   * 
   * @param absoluteTolerance the smallest acceptable absolute change in integral estimates in
   *        consecutive iterations that indicates the algorithm has converged
   * @param maxEvaluations the maximum number of function evaluations
   * @param method the extrapolation method to use
   * 
   */
  public Romberg(final double absoluteTolerance, final int maxEvaluations,
      final RombergExtrapolationMethod method) {
    this(absoluteTolerance, 100.0 * Constants.EPSILON, maxEvaluations, method);
  }

  public Romberg(final double absoluteTolerance, final int maxEvaluations) {
    this(absoluteTolerance, maxEvaluations, RombergExtrapolationMethod.CADRE);
  }

  @Override
  final QuadratureResult properIntegral(final DoubleUnaryOperator f, final double a,
      final double b) {
    switch (myMethod) {
      case RICHARDSON:
        return romberg(f, a, b);
      case CADRE:
        return cadre(f, a, b);
      case HAVIE:
        return havie(f, a, b, 2);
      default:
        return new QuadratureResult(Double.NaN, Double.NaN, 0, false);
    }
  }

  @Override
  public final String getName() {
    return "Romberg-" + myMethod.toString();
  }

  private final QuadratureResult romberg(final DoubleUnaryOperator func, final double a,
      final double b) {

    // prepare variables
    final double[] work = new double[myMaxEvals + 1];
    double rnderr = Constants.EPSILON;
    double qx1 = 0.0;
    double qx2 = 0.0;
    double h = b - a;
    double fcna = func.applyAsDouble(a);
    double fcnb = func.applyAsDouble(b);
    double tabs = Math.abs(h) * (Math.abs(fcna) + Math.abs(fcnb)) * 0.5;
    double t = h * (fcna + fcnb) * 0.5;
    int nx = 1;
    int nfev = 2;
    final int nleast = 6;

    // main iterations
    for (int i = 1; nfev < myMaxEvals; ++i) {
      h *= 0.5;
      double sum1 = 0.0;
      double sumabs = 0.0;
      for (int j = 1; j <= nx; ++j) {
        final double fcnxi = func.applyAsDouble(a + h * ((j << 1) - 1));
        sumabs += Math.abs(fcnxi);
        sum1 += fcnxi;
        ++nfev;
        if (nfev >= myMaxEvals) {
          return new QuadratureResult(work[0], Double.NaN, nfev, false);
        }
      }
      t = 0.5 * t + h * sum1;
      tabs = tabs * 0.5 + Math.abs(h) * sumabs;
      work[i - 1] = 2.0 * (t + h * sum1) / 3.0;
      if (1 < i) {

        // Construct difference table for Richardson extrapolation.
        double f = 4.0;
        for (int j = 2; j <= i; ++j) {
          final int k = i + 1 - j;
          f *= 4.0;
          work[k - 1] = work[k] + (work[k] - work[k - 1]) / (f - 1.0);
        }

        // Perform acceptance check.
        if (nleast <= i) {
          final double x = Math.abs(work[0] - qx2) + Math.abs(qx2 - qx1);
          if (x <= 3.0 * tabs * (Math.abs(myRelTol) + rnderr) || x <= 3.0 * Math.abs(myTol)) {
            return new QuadratureResult(work[0], x, nfev, true);
          }
        }

        // Save old result, perform bisection, repeat.
        qx1 = qx2;
      }
      qx2 = work[0];
      nx <<= 1;
    }
    return new QuadratureResult(work[0], Double.NaN, nfev, false);
  }

  private final QuadratureResult cadre(final DoubleUnaryOperator func, final double a,
      final double b) {
    final int mxstge = 30, maxtbl = 10, maxts = 2049;
    final double aitlow = 1.1, aittol = 0.1, h2tol = 0.15, tljump = 0.01;
    final boolean[] reglsv = new boolean[mxstge];
    final int[] ibegs = new int[mxstge];
    final double[] ait = new double[maxtbl], begin = new double[mxstge], dif = new double[maxtbl],
        est = new double[mxstge], finis = new double[mxstge], r = new double[maxtbl],
        rn = new double[4], ts = new double[maxts];
    final double[][] t = new double[maxtbl][maxtbl];
    boolean aitken, h2conv, reglar, right;
    int i = 0, ibeg, iend, ii, iii, istage, istep, istep2, it = 0, l, lm1 = 0, n, n2, nnleft,
        fev = 0, ind;
    double astep, beg, bma, curest, diff = 0.0, end, ergoal = 0.0, erra, errer = 0.0, errr, fbeg,
        fbeg2 = 0.0, fend, fextm1, fextrp = 0.0, fn, fnsize, h2next = 0.0, h2tfex, hovn, prever,
        rnderr, sing, singnx = 0.0, slope = 0.0, stage, step, stepmn, sum1, sumabs, tabs,
        tabtlm = 0.0, vint, result = 0.0, error = 0.0;

    vint = 0.0;
    rn[0] = 0.7142005;
    rn[1] = 0.3466282;
    rn[2] = 0.8437510;
    rn[3] = 0.1263305;
    rnderr = Constants.EPSILON;
    ind = 1;
    bma = Math.abs(b - a);
    errr = Math.min(0.1, Math.max(Math.abs(myRelTol), 10.0 * rnderr));
    erra = Math.abs(myTol);
    final double absmax = Math.max(Math.abs(a), Math.abs(b));
    stepmn = Math.max(bma / SimpleMath.pow(2.0, mxstge), Math.max(bma, absmax) * rnderr);
    stage = 0.5;
    istage = 1;
    curest = fnsize = prever = 0.0;
    reglar = false;
    beg = a;
    fbeg = func.applyAsDouble(beg) / 2.0;
    fev = 1;
    ts[1 - 1] = fbeg;
    ibeg = 1;
    end = b;
    fend = func.applyAsDouble(end) / 2.0;
    ++fev;
    ts[2 - 1] = fend;
    iend = 2;

    right = false;
    step = end - beg;
    astep = Math.abs(step);
    if (astep < stepmn) {
      ind = 5;
      return new QuadratureResult(curest + vint, error, fev, true);
    }
    t[1 - 1][1 - 1] = fbeg + fend;
    tabs = Math.abs(fbeg) + Math.abs(fend);
    l = n = 1;
    h2conv = aitken = false;

    int flag = 40;
    while (true) {

      if (flag == 40) {
        lm1 = l;
        ++l;
        n2 = n << 1;
        fn = n2;
        istep = (iend - ibeg) / n;
        if (1 >= istep) {
          ii = iend;
          iend += n;
          if (maxts < iend) {
            ind = 4;
            return new QuadratureResult(curest + vint, error, fev, true);
          }
          hovn = step / fn;
          iii = iend;
          for (i = 1; i <= n2; i += 2) {
            ts[iii - 1] = ts[ii - 1];
            ts[iii - 1 - 1] = func.applyAsDouble(end - i * hovn);
            ++fev;
            iii -= 2;
            --ii;
            if (fev >= myMaxEvals) {
              return new QuadratureResult(Double.NaN, error, fev, false);
            }
          }
          istep = 2;
        }

        istep2 = ibeg + (istep >> 1);
        sum1 = sumabs = 0.0;
        for (i = istep2; i <= iend; i += istep) {
          sum1 += ts[i - 1];
          sumabs += Math.abs(ts[i - 1]);
        }

        t[l - 1][1 - 1] = t[l - 1 - 1][1 - 1] / 2.0 + sum1 / fn;
        tabs = tabs / 2.0 + sumabs / fn;
        n = n2;
        it = 1;
        vint = step * t[l - 1][1 - 1];
        tabtlm = tabs * rnderr;
        fnsize = Math.max(fnsize, Math.abs(t[l - 1][1 - 1]));
        ergoal = Math.max(astep * rnderr * fnsize,
            stage * Math.max(erra, errr * Math.abs(curest + vint)));
        fextrp = 1.0;
        for (i = 1; i <= lm1; ++i) {
          fextrp *= 4.0;
          t[i - 1][l - 1] = t[l - 1][i - 1] - t[l - 1 - 1][i - 1];
          t[l - 1][i + 1 - 1] = t[l - 1][i - 1] + t[i - 1][l - 1] / (fextrp - 1.0);
        }

        errer = astep * Math.abs(t[1 - 1][l - 1]);
        if (2 >= l) {
          if (Math.abs(t[1 - 1][2 - 1]) <= tabtlm) {
            slope = (fend - fbeg) * 2.0;
            fbeg2 = fbeg * 2.0;
            int next = 340;
            for (i = 1; i <= 4; ++i) {
              diff =
                  Math.abs(func.applyAsDouble(beg + rn[i - 1] * step) - fbeg2 - rn[i - 1] * slope);
              ++fev;
              if (fev >= myMaxEvals) {
                return new QuadratureResult(Double.NaN, error, fev, false);
              }
              if (tabtlm < diff) {
                next = 330;
                break;
              }
            }
            if (next == 330) {
              flag = 330;
            } else {
              flag = 340;
            }
          }
          continue;
        }

        for (i = 2; i <= lm1; ++i) {
          if (tabtlm < Math.abs(t[i - 1 - 1][l - 1])) {
            diff = t[i - 1 - 1][lm1 - 1] / t[i - 1 - 1][l - 1];
          } else {
            diff = 0.0;
          }
          t[i - 1 - 1][lm1 - 1] = diff;
        }

        if (Math.abs(4.0 - t[1 - 1][lm1 - 1]) > h2tol) {
          if (t[1 - 1][lm1 - 1] == 0.0) {
            if (errer <= ergoal) {
              slope = (fend - fbeg) * 2.0;
              fbeg2 = fbeg * 2.0;
              i = 1;
              diff =
                  Math.abs(func.applyAsDouble(beg + rn[i - 1] * step) - fbeg2 - rn[i - 1] * slope);
              ++fev;
              flag = 330;
              if (fev >= myMaxEvals) {
                return new QuadratureResult(Double.NaN, error, fev, false);
              }
            } else {
              flag = 380;
            }
          } else if (Math.abs(2.0 - Math.abs(t[1 - 1][lm1 - 1])) < tljump) {
            if (ergoal < errer) {
              reglar = true;
              flag = 380;
            } else {
              diff = Math.abs(t[1 - 1][l - 1]) * 2.0 * fn;
              flag = 340;
            }
          } else if (l == 3) {
            flag = 40;
          } else {
            h2conv = false;
            if (Math.abs((t[1 - 1][lm1 - 1] - t[1 - 1][l - 2 - 1]) / t[1 - 1][lm1 - 1]) <= aittol) {
              flag = 160;
            } else if (!reglar && l == 4) {
              flag = 40;
            } else if (errer <= ergoal) {
              slope = (fend - fbeg) * 2.0;
              fbeg2 = fbeg * 2.0;
              i = 1;
              diff =
                  Math.abs(func.applyAsDouble(beg + rn[i - 1] * step) - fbeg2 - rn[i - 1] * slope);
              ++fev;
              flag = 330;
              if (fev >= myMaxEvals) {
                return new QuadratureResult(Double.NaN, error, fev, false);
              }
            } else {
              flag = 380;
            }
          }
          continue;
        } else {
          if (!h2conv) {
            aitken = false;
            h2conv = true;
          }
          fextrp = 4.0;
          flag = 150;
        }
      }

      if (flag == 150) {
        while (true) {
          ++it;
          vint = step * t[l - 1][it - 1];
          errer = Math.abs(step / (fextrp - 1.0) * t[it - 1 - 1][l - 1]);
          if (errer <= ergoal) {
            flag = 340;
            break;
          } else if (it == lm1) {
            flag = 270;
            break;
          } else if (t[it - 1][lm1 - 1] == 0.0) {
          } else if (t[it - 1][lm1 - 1] <= fextrp) {
            flag = 270;
            break;
          } else if (Math.abs(t[it - 1][lm1 - 1] / 4.0 - fextrp) / fextrp < aittol) {
            fextrp *= 4.0;
          }
        }
      }

      if (flag == 160) {
        if (t[1 - 1][lm1 - 1] < aitlow) {
          flag = 380;
        } else {
          if (!aitken) {
            h2conv = false;
            aitken = true;
          }
          fextrp = t[l - 2 - 1][lm1 - 1];
          if (4.5 < fextrp) {
            fextrp = 4.0;
            flag = 150;
            continue;
          } else if (fextrp < aitlow
              || h2tol < Math.abs(fextrp - t[l - 3 - 1][lm1 - 1]) / t[1 - 1][lm1 - 1]) {
            flag = 380;
          } else {
            sing = fextrp;
            fextm1 = fextrp - 1.0;
            ait[1 - 1] = 0.0;
            for (i = 2; i <= l; ++i) {
              ait[i - 1] = t[i - 1][1 - 1] + (t[i - 1][1 - 1] - t[i - 1 - 1][1 - 1]) / fextm1;
              r[i - 1] = t[1 - 1][i - 1 - 1];
              dif[i - 1] = ait[i - 1] - ait[i - 1 - 1];
            }
            it = 2;

            while (true) {
              vint = step * ait[l - 1];
              errer /= fextm1;
              if (errer <= ergoal) {
                ind = Math.max(ind, 2);
                flag = 340;
                break;
              }
              ++it;
              if (it == lm1) {
                flag = 270;
                break;
              }
              if (it <= 3) {
                h2next = 4.0;
                singnx = 2.0 * sing;
              }
              if (h2next < singnx) {
                fextrp = h2next;
                h2next *= 4.0;
              } else {
                fextrp = singnx;
                singnx *= 2.0;
              }

              for (i = it; i <= lm1; ++i) {
                if (tabtlm < Math.abs(dif[i + 1 - 1])) {
                  r[i + 1 - 1] = dif[i - 1] / dif[i + 1 - 1];
                } else {
                  r[i + 1 - 1] = 0.0;
                }
              }
              h2tfex = -h2tol * fextrp;
              if (r[l - 1] - fextrp >= h2tfex && r[l - 1 - 1] - fextrp >= h2tfex) {
                errer = astep * Math.abs(dif[l - 1]);
                fextm1 = fextrp - 1.0;
                for (i = it; i <= l; ++i) {
                  ait[i - 1] += (dif[i - 1] / fextm1);
                  dif[i - 1] = ait[i - 1] - ait[i - 1 - 1];
                }
              } else {
                flag = 270;
                break;
              }
            }
          }
        }
      }

      if (flag == 270) {
        fextrp = Math.max(prever / errer, aitlow);
        prever = errer;
        if (l < 5) {
          flag = 40;
          continue;
        } else if (2.0 < l - it && istage < mxstge) {
          reglar = true;
          flag = 380;
        } else if (errer / SimpleMath.pow(fextrp, maxtbl - l) < ergoal) {
          flag = 40;
          continue;
        } else {
          reglar = true;
          flag = 380;
        }
      }

      if (flag == 330) {
        while (true) {
          errer = Math.max(errer, astep * diff);
          if (ergoal < errer) {
            flag = 380;
            break;
          }
          ++i;
          if (i <= 4) {
            diff = Math.abs(func.applyAsDouble(beg + rn[i - 1] * step) - fbeg2 - rn[i - 1] * slope);
            ++fev;
            if (fev >= myMaxEvals) {
              return new QuadratureResult(Double.NaN, error, fev, false);
            }
          } else {
            ind = 3;
            flag = 340;
            break;
          }
        }
      }

      if (flag == 340) {
        result += vint;
        error += errer;
        if (right) {
          curest += vint;
          stage *= 2.0;
          iend = ibeg;
          ibeg = ibegs[istage - 1];
          end = beg;
          beg = begin[istage - 1];
          fend = fbeg;
          fbeg = ts[ibeg - 1];
          right = false;
        } else {
          --istage;
          if (istage == 0) {
            return new QuadratureResult(result, error, fev, true);
          }
          reglar = reglsv[istage - 1];
          beg = begin[istage - 1];
          end = finis[istage - 1];
          curest = curest - est[istage + 1 - 1] + vint;
          iend = ibeg - 1;
          fend = ts[iend - 1];
          ibeg = ibegs[istage - 1];
          right = true;
          beg = (beg + end) / 2.0;
          ibeg = (ibeg + iend) >> 1;
          ts[ibeg - 1] /= 2.0;
          fbeg = ts[ibeg - 1];
        }
        step = end - beg;
        astep = Math.abs(step);
        if (astep < stepmn) {
          ind = 5;
          return new QuadratureResult(curest + vint, error, fev, true);
        }
        t[1 - 1][1 - 1] = fbeg + fend;
        tabs = Math.abs(fbeg) + Math.abs(fend);
        l = n = 1;
        h2conv = aitken = false;
        flag = 40;
        continue;
      }

      if (flag == 380) {
        if (istage == mxstge) {
          ind = 5;
          return new QuadratureResult(curest + vint, error, fev, true);
        }
        if (!right) {
          reglsv[istage + 1 - 1] = reglar;
          begin[istage - 1] = beg;
          ibegs[istage - 1] = ibeg;
          stage /= 2.0;
          right = true;
          beg = (beg + end) / 2.0;
          ibeg = (ibeg + iend) >> 1;
          ts[ibeg - 1] /= 2.0;
          fbeg = ts[ibeg - 1];
        } else {

          nnleft = ibeg - ibegs[istage - 1];
          if (maxts <= end + nnleft) {
            ind = 4;
            return new QuadratureResult(curest + vint, error, fev, true);
          }
          iii = ibegs[istage - 1];
          ii = iend;
          for (i = iii; i <= ibeg; ++i) {
            ++ii;
            ts[ii - 1] = ts[i - 1];
          }
          for (i = ibeg; i <= ii; ++i) {
            ts[iii - 1] = ts[i - 1];
            ++iii;
          }

          ++iend;
          ibeg = iend - nnleft;
          fend = fbeg;
          fbeg = ts[ibeg - 1];
          finis[istage - 1] = end;
          end = beg;
          beg = begin[istage - 1];
          begin[istage - 1] = end;
          reglsv[istage - 1] = reglar;
          ++istage;
          reglar = reglsv[istage - 1];
          est[istage - 1] = vint;
          curest += est[istage - 1];
          right = false;
        }
        step = end - beg;
        astep = Math.abs(step);
        if (astep < stepmn) {
          ind = 5;
          return new QuadratureResult(curest + vint, error, fev, true);
        }
        t[1 - 1][1 - 1] = fbeg + fend;
        tabs = Math.abs(fbeg) + Math.abs(fend);
        l = n = 1;
        h2conv = aitken = false;
        flag = 40;
      }
    }
  }

  private final QuadratureResult havie(final DoubleUnaryOperator func, final double a,
      final double b, final int iop) {
    double alf, alfnj, alfno = 0.0, ar = 0.0, bet, betnj, betno, const1, const2, deltan,
        endpts = 0.0, error, fac1 = 0.411233516712057, fac2 = 0.822467033441132, factor, gamman,
        hnstep, pi = Math.PI, r1, r2, rn, rnderr, rounde, tend = 0.0, triarg = 0.0, umid, xmin,
        xplus, epsout = Double.NaN;
    int i, index = 0, iout = 0, j, n, nhalf, nupper = 9, fev = 0;
    final double[] acof = new double[11], bcof = new double[nupper + 1];

    // Set coefficients in formula for accumulated roundoff error,
    // rounde = rnderr*(r1+r2*n), where r1, r2 are two empirical constants
    // and n is the current number of function values used.
    rnderr = Constants.EPSILON;
    if (iop == 2) {
      r1 = 50.0;
    } else {
      r1 = 1.0;
    }
    if (iop == 1) {
      r2 = 0.02;
    } else {
      r2 = 2.0;
    }
    error = myTol;

    // Initial calculations.
    alf = 0.5 * (b - a);
    bet = 0.5 * (a + b);
    acof[1 - 1] = func.applyAsDouble(a) + func.applyAsDouble(b);
    fev += 2;
    bcof[1 - 1] = func.applyAsDouble(bet);
    ++fev;

    // Modified Romberg algorithm, ordinary case.
    if (iop != 2) {
      hnstep = 2.0;
      bcof[1 - 1] *= hnstep;
      factor = 1.0;
    } else {

      // Modified Romberg, cosine transformed case.
      hnstep = pi;
      ar = fac1;
      endpts = acof[1 - 1];
      acof[1 - 1] *= fac2;
      bcof[1 - 1] = hnstep * bcof[1 - 1] - ar * endpts;
      factor = 4.0;
      ar /= 4.0;
      triarg = pi / 4.0;
      alfno = -1.0;
    }
    hnstep *= 0.5;
    nhalf = 1;
    n = 2;
    rn = 2.0;
    acof[1 - 1] = 0.5 * (acof[1 - 1] + bcof[1 - 1]);
    acof[2 - 1] = acof[1 - 1] - (acof[1 - 1] - bcof[1 - 1]) / (4.0 * factor - 1.0);

    // End of initial calculation. Start actual calculations.
    for (i = 1; i <= nupper; ++i) {
      umid = 0.0;

      if (iop == 1) {

        // Modified Romberg algorithm, ordinary case.
        // compute first element in mid-point formula for ordinary case
        alfnj = 0.5 * hnstep;
        for (j = 1; j <= nhalf; ++j) {
          xplus = alf * alfnj + bet;
          xmin = -alf * alfnj + bet;
          umid += (func.applyAsDouble(xplus) + func.applyAsDouble(xmin));
          fev += 2;
          alfnj += hnstep;
          if (fev >= myMaxEvals) {
            return new QuadratureResult(Double.NaN, epsout, fev, false);
          }
        }
        umid *= hnstep;
      } else if (iop == 2) {

        // Modified Romberg algorithm, cosine transformed case
        // compute first element in mid-point formula for cosine
        // transformed Romberg algorithm.
        const1 = -Math.sin(triarg);
        const2 = 0.5 * alfno / const1;
        alfno = const1;
        betno = const2;
        gamman = 1.0 - 2.0 * alfno * alfno;
        deltan = -2.0 * alfno * betno;
        for (j = 1; j <= nhalf; ++j) {
          alfnj = gamman * const1 + deltan * const2;
          betnj = gamman * const2 - deltan * const1;
          xplus = alf * alfnj + bet;
          xmin = -alf * alfnj + bet;
          umid += betnj * (func.applyAsDouble(xplus) + func.applyAsDouble(xmin));
          fev += 2;
          const1 = alfnj;
          const2 = betnj;
          if (fev >= myMaxEvals) {
            return new QuadratureResult(Double.NaN, epsout, fev, false);
          }
        }
        umid = hnstep * umid - ar * endpts;
        ar /= 4.0;
      }

      // Modified Romberg algorithm, calculate (i+1)-th row in the U table.
      const1 = 4.0 * factor;
      index = i + 1;
      for (j = 2; j <= i + 1; ++j) {
        tend = umid + (umid - bcof[j - 1 - 1]) / (const1 - 1.0);
        bcof[j - 1 - 1] = umid;
        umid = tend;
        const1 *= 4.0;
      }
      bcof[i + 1 - 1] = tend;
      xplus = const1;

      // Calculation of (i+1)-th row in the U table is finished
      // Test to see if the required accuracy has been obtained.
      epsout = 1.0;
      iout = 1;
      for (j = 1; j <= index; ++j) {
        const1 = 0.5 * (acof[j - 1] + bcof[j - 1]);
        const2 = 0.5 * Math.abs((acof[j - 1] - bcof[j - 1]) / const1);
        if (const2 <= epsout) {
          epsout = const2;
          iout = j;
        }
        acof[j - 1] = const1;
      }

      // Testing on accuracy finished
      if (iout == index) {
        ++iout;
      }
      acof[index + 1 - 1] = acof[index - 1] - (acof[index - 1] - bcof[index - 1]) / (xplus - 1.0);
      rounde = rnderr * (r1 + r2 * rn);
      epsout = Math.max(epsout, rounde);
      error = Math.max(error, rounde);
      if (epsout <= error) {

        // Calculation for modified Romberg algorithm finished.
        n <<= 1;
        --index;
        ++n;
        return new QuadratureResult(alf * acof[iout - 1], epsout, fev, true);
      }
      nhalf = n;
      n <<= 1;
      rn *= 2.0;
      hnstep *= 0.5;
      if (1 < iop) {
        triarg *= 0.5;
      }
    }

    // Accuracy not reached with maximum number of subdivisions.
    n = nhalf;

    // Calculation for modified Romberg algorithm finished.
    n <<= 1;
    --index;
    ++n;
    return new QuadratureResult(alf * acof[iout - 1], epsout, fev, true);
  }
}
