package org.matheclipse.core.numerics.integral;

import java.util.function.DoubleUnaryOperator;
import org.matheclipse.core.numerics.utils.Constants;

/**
 * An adaptive integrator obtained by replacing the Gauss-Kronrod rules in QUADPACK with recursive
 * monotone stable (RMS) formulas [1]. A translation of a corresponding Fortran subroutine by Alan
 * Miller.
 * 
 * <p>
 * References:
 * <ul>
 * <li>[1] Favati, Paola, Grazia Lotti, and Francesco Romani. "Algorithm 691: Improving QUADPACK
 * automatic integration routines." ACM Transactions on Mathematical Software (TOMS) 17.2 (1991):
 * 218-232.</li>
 * </ul>
 * </p>
 */
public final class RmsRule extends Quadrature {

  private static final int[] istart = {0, 7, 17, 31};
  private static final int[] length = {7, 10, 14, 21};

  private static final double[] xx = {0.0, .25000000000000000000E+00, .50000000000000000000E+00,
      .75000000000000000000E+00, .87500000000000000000E+00, .93750000000000000000E+00,
      .10000000000000000000E+01, .37500000000000000000E+00, .62500000000000000000E+00,
      .96875000000000000000E+00, .12500000000000000000E+00, .68750000000000000000E+00,
      .81250000000000000000E+00, .98437500000000000000E+00, .18750000000000000000E+00,
      .31250000000000000000E+00, .43750000000000000000E+00, .56250000000000000000E+00,
      .84375000000000000000E+00, .90625000000000000000E+00, .99218750000000000000E+00};

  private static final double[] ww = {1.303262173284849021810473057638590518409112513421E-1,
      2.390632866847646220320329836544615917290026806242E-1,
      2.630626354774670227333506083741355715758124943143E-1,
      2.186819313830574175167853094864355208948886875898E-1,
      2.757897646642836865859601197607471574336674206700E-2,
      1.055750100538458443365034879086669791305550493830E-1,
      1.571194260595182254168429283636656908546309467968E-2,
      1.298751627936015783241173611320651866834051160074E-1,
      2.249996826462523640447834514709508786970828213187E-1,
      1.680415725925575286319046726692683040162290325505E-1,
      1.415567675701225879892811622832845252125600939627E-1,
      1.006482260551160175038684459742336605269707889822E-1,
      2.510604860724282479058338820428989444699235030871E-2,
      9.402964360009747110031098328922608224934320397592E-3,
      5.542699233295875168406783695143646338274805359780E-2,
      9.986735247403367525720377847755415293097913496236E-2,
      4.507523056810492466415880450799432587809828791196E-2,
      6.300942249647773931746170540321811473310938661469E-2,
      1.261383225537664703012999637242003647020326905948E-1,
      1.273864433581028272878709981850307363453523117880E-1,
      8.576500414311820514214087864326799153427368592787E-2,
      7.102884842310253397447305465997026228407227220665E-2,
      5.026383572857942403759829860675892897279675661654E-2,
      4.683670010609093810432609684738393586390722052124E-3,
      1.235837891364555000245004813294817451524633100256E-1,
      1.148933497158144016800199601785309838604146040215E-1,
      1.252575774226122633391477702593585307254527198070E-2,
      1.239572396231834242194189674243818619042280816640E-1,
      2.501306413750310579525950767549691151739047969345E-2,
      4.915957918146130094258849161350510503556792927578E-2,
      2.259167374956474713302030584548274729936249753832E-2,
      6.362762978782724559269342300509058175967124446839E-2,
      9.950065827346794643193261975720606296171462239514E-2,
      7.048220002718565366098742295389607994441704889441E-2,
      6.512297339398335645872697307762912795346716454337E-2,
      3.998229150313659724790527138690215186863915308702E-2,
      3.456512257080287509832054272964315588028252136044E-2,
      2.212167975884114432760321569298651047876071264944E-3,
      8.140326425945938045967829319725797511040878579808E-2,
      6.583213447600552906273539578430361199084485578379E-2,
      2.592913726450792546064232192976262988065252032902E-2,
      1.187141856692283347609436153545356484256869129472E-1,
      5.999947605385971985589674757013565610751028128731E-2,
      5.500937980198041736910257988346101839062581489820E-2,
      5.264422421764655969760271538981443718440340270116E-3,
      1.533126874056586959338368742803997744815413565014E-2,
      3.527159369750123100455704702965541866345781113903E-2,
      5.000556431653955124212795201196389006184693561679E-2,
      5.744164831179720106340717579281831675999717767532E-2,
      1.598823797283813438301248206397233634639162043386E-2,
      2.635660410220884993472478832884065450876913559421E-2,
      1.196003937945541091670106760660561117114584656319E-2};

  private final double myRelTol;

  /**
   * Creates a new instance of the RMS quadrature integrator.
   * 
   * @param relativeTolerance the smallest acceptable relative change in integral estimates in
   *        consecutive iterations that indicates the algorithm has converged
   * @param tolerance the smallest acceptable absolute change in integral estimates in consecutive
   *        iterations that indicates the algorithm has converged
   * @param maxEvaluations the maximum number of evaluations of each function permitted
   */
  public RmsRule(final double tolerance, final double relativeTolerance, final int maxEvaluations) {
    super(tolerance, maxEvaluations);
    myRelTol = relativeTolerance;
  }

  public RmsRule(final double tolerance, final int maxEvaluations) {
    this(tolerance, 50.0 * Constants.EPSILON, maxEvaluations);
  }

  @Override
  final QuadratureResult properIntegral(final DoubleUnaryOperator f, final double a,
      final double b) {
    return qxgs(f, a, b, myTol, myRelTol, myMaxEvals);
  }

  @Override
  public final String getName() {
    return "RMS Rule";
  }

  private final QuadratureResult qxgs(final DoubleUnaryOperator f, final double a, final double b,
      final double epsabs, final double epsrel, final int limit) {

    // prepare variables
    final int[] ier = new int[1];
    final double[] result = new double[1];
    final double[] abserr = new double[1];
    final int[] last = new int[1];
    final int[] fev = new int[1];

    // call main subroutine
    qxgs(f, a, b, epsabs, epsrel, result, abserr, ier, limit, last, fev);
    return new QuadratureResult(result[0], abserr[0], fev[0], ier[0] == 0);
  }

  private static void qxgs(final DoubleUnaryOperator f, final double a, final double b,
      final double epsabs, final double epsrel, final double[] result, final double[] abserr,
      final int[] ier, final int limit, final int[] last, final int[] fev) {
    ier[0] = 6;
    last[0] = 0;
    result[0] = abserr[0] = 0.0;
    fev[0] = 0;
    if (limit < 1) {
      return;
    }

    // PREPARE CALL FOR QXGSE.
    qxgse1(f, a, b, epsabs, epsrel, limit, result, abserr, ier, last, fev);
  }

  private static void qxgse1(final DoubleUnaryOperator f, final double a, final double b,
      final double epsabs, final double epsrel, final int limit, final double[] result,
      final double[] abserr, final int[] ier, final int[] last, final int[] fev) {

    // THE ROUTINE CALCULATES AN APPROXIMATION RESULT TO A
    // DEFINITE INTEGRAL I = INTEGRAL OF F OVER (A,B),
    // HOPEFULLY SATISFYING FOLLOWING CLAIM FOR ACCURACY
    // ABS(I-RESULT).LE.MAX(EPSABS,EPSREL*ABS(I)).
    boolean extrap, noext;
    int id, ierro, iroff1, iroff2, iroff3, jupbnd, k, ksgn, ktmin;
    double area, area12, a1, a2, b1, b2, correc = 0.0, dres, epmach, erlarg = 0.0, erlast, errbnd,
        erro12, errsum, ertest = 0.0, oflow, rerr, small = 0.0, t, uflow;
    final int[] ln1 = new int[1], lp1 = new int[1], ln2 = new int[1], lp2 = new int[1],
        maxerr = new int[1], nrmax = new int[1], numrl2 = new int[1], nres = new int[1];
    final double[] resabs = new double[1], defabs = new double[1], area1 = new double[1],
        error1 = new double[1], defab1 = new double[1], defab2 = new double[1],
        area2 = new double[1], error2 = new double[1], errmax = new double[1],
        reseps = new double[1], abseps = new double[1];
    final double[] alist = new double[limit];
    final double[] blist = new double[limit];
    final double[] elist = new double[limit];
    final double[] res3la = new double[3];
    final double[] rlist = new double[limit];
    final double[] rlist2 = new double[52];
    final double[][] valp = new double[limit][21];
    final double[][] valn = new double[limit][21];
    final double[] vp1 = new double[21];
    final double[] vp2 = new double[21];
    final double[] vn1 = new double[21];
    final double[] vn2 = new double[21];
    final int[] iord = new int[limit];
    final int[] lp = new int[limit];
    final int[] ln = new int[limit];

    // MACHINE DEPENDENT CONSTANTS
    epmach = Constants.EPSILON;
    uflow = Double.MIN_VALUE;
    oflow = Double.MAX_VALUE;

    // TEST ON VALIDITY OF parameterS
    last[0] = 0;
    result[0] = abserr[0] = 0.0;
    alist[1 - 1] = a;
    blist[1 - 1] = b;
    rlist[1 - 1] = elist[1 - 1] = 0.0;
    ier[0] = 6;
    if (epsabs < 0.0 || epsrel < 0.0) {
      return;
    }
    ier[0] = 0;
    rerr = Math.max(epsrel, 50.0 * epmach);
    fev[0] = 0;

    // FIRST APPROXIMATION TO THE INTEGRAL
    ierro = 0;
    lp[1 - 1] = ln[1 - 1] = 1;
    valp[1 - 1][1 - 1] = f.applyAsDouble((a + b) * 0.5);
    ++fev[0];
    valn[1 - 1][1 - 1] = valp[1 - 1][1 - 1];
    qxlqm(f, a, b, result, abserr, resabs, defabs, valp[1 - 1], valn[1 - 1], lp, ln, 2, epmach,
        uflow, oflow, fev);

    // TEST ON ACCURACY.
    dres = Math.abs(result[0]);
    errbnd = Math.max(epsabs, rerr * dres);
    last[0] = 1;
    rlist[1 - 1] = result[0];
    elist[1 - 1] = abserr[0];
    iord[1 - 1] = 1;
    if (abserr[0] <= 100.0 * epmach * defabs[0] && abserr[0] > errbnd) {
      ier[0] = 2;
    }
    if (limit == 1) {
      ier[0] = 1;
    }
    if (ier[0] != 0 || (abserr[0] <= errbnd && abserr[0] != resabs[0]) || (abserr[0] == 0.0)) {
      return;
    }

    // INITIALIZATION
    rlist2[1 - 1] = result[0];
    errmax[0] = abserr[0];
    maxerr[0] = 1;
    area = result[0];
    errsum = abserr[0];
    abserr[0] = oflow;
    nrmax[0] = 1;
    nres[0] = 0;
    numrl2[0] = 2;
    ktmin = 0;
    extrap = noext = false;
    iroff1 = iroff2 = iroff3 = 0;
    ksgn = -1;
    if (dres >= (1.0 - 50.0 * epmach) * defabs[0]) {
      ksgn = 1;
    }
    t = 1.0 + 100.0 * epmach;

    // MAIN DO-LOOP
    int exit = 100;
    for (last[0] = 2; last[0] <= limit; ++last[0]) {

      // BISECT THE SUBINTERVAL WITH THE NRMAX-TH LARGEST ERROR ESTIMATE.
      a1 = alist[maxerr[0] - 1];
      b1 = 0.5 * (alist[maxerr[0] - 1] + blist[maxerr[0] - 1]);
      a2 = b1;
      b2 = blist[maxerr[0] - 1];
      erlast = errmax[0];
      qxrrd(f, valn[maxerr[0] - 1], ln[maxerr[0] - 1], b1, a1, vn1, vp1, ln1, lp1, fev);
      qxlqm(f, a1, b1, area1, error1, resabs, defab1, vp1, vn1, lp1, ln1, 2, epmach, uflow, oflow,
          fev);
      qxrrd(f, valp[maxerr[0] - 1], lp[maxerr[0] - 1], a2, b2, vp2, vn2, lp2, ln2, fev);
      qxlqm(f, a2, b2, area2, error2, resabs, defab2, vp2, vn2, lp2, ln2, 2, epmach, uflow, oflow,
          fev);

      // IMPROVE PREVIOUS APPROXIMATIONS TO INTEGRAL
      // AND ERROR AND TEST FOR ACCURACY.
      area12 = area1[0] + area2[0];
      erro12 = error1[0] + error2[0];
      errsum = errsum + erro12 - errmax[0];
      area = area + area12 - rlist[maxerr[0] - 1];
      if (defab1[0] == error1[0] || defab2[0] == error2[0]) {
      } else {
        if (Math.abs(rlist[maxerr[0] - 1] - area12) > 0.1e-4 * Math.abs(area12)
            || erro12 < 0.99 * errmax[0]) {
        } else {
          if (extrap) {
            ++iroff2;
          }
          if (!extrap) {
            ++iroff1;
          }
        }
        if (last[0] > 10 && erro12 > errmax[0]) {
          ++iroff3;
        }
      }
      rlist[maxerr[0] - 1] = area1[0];
      rlist[last[0] - 1] = area2[0];
      errbnd = Math.max(epsabs, rerr * Math.abs(area));

      // TEST FOR ROUNDOFF ERROR AND EVENTUALLY SET ERROR FLAG.
      if (iroff1 + iroff2 >= 10 || iroff3 >= 20) {
        ier[0] = 2;
      }
      if (iroff2 >= 5) {
        ierro = 3;
      }

      // SET ERROR FLAG IN THE CASE THAT THE NUMBER OF SUBINTERVALS
      // EQUALS LIMIT.
      if (last[0] == limit) {
        ier[0] = 1;
      }

      // SET ERROR FLAG IN THE CASE OF BAD INTEGRAND BEHAVIOUR
      // AT A POINT OF THE INTEGRATION RANGE.
      if (Math.max(Math.abs(a1), Math.abs(b2)) <= t * (Math.abs(a2) + 1.0e3 * uflow)) {
        ier[0] = 4;
      }

      // APPEND THE NEWLY-CREATED INTERVALS TO THE LIST.
      if (error2[0] > error1[0]) {
        alist[maxerr[0] - 1] = a2;
        alist[last[0] - 1] = a1;
        blist[last[0] - 1] = b1;
        rlist[maxerr[0] - 1] = area2[0];
        rlist[last[0] - 1] = area1[0];
        elist[maxerr[0] - 1] = error2[0];
        elist[last[0] - 1] = error1[0];
        qxcpy(valp[maxerr[0] - 1], vp2, lp2[0]);
        lp[maxerr[0] - 1] = lp2[0];
        qxcpy(valn[maxerr[0] - 1], vn2, ln2[0]);
        ln[maxerr[0] - 1] = ln2[0];
        qxcpy(valp[last[0] - 1], vp1, lp1[0]);
        lp[last[0] - 1] = lp1[0];
        qxcpy(valn[last[0] - 1], vn1, ln1[0]);
        ln[last[0] - 1] = ln1[0];
      } else {
        alist[last[0] - 1] = a2;
        blist[maxerr[0] - 1] = b1;
        blist[last[0] - 1] = b2;
        elist[maxerr[0] - 1] = error1[0];
        elist[last[0] - 1] = error2[0];
        qxcpy(valp[maxerr[0] - 1], vp1, lp1[0]);
        lp[maxerr[0] - 1] = lp1[0];
        qxcpy(valn[maxerr[0] - 1], vn1, ln1[0]);
        ln[maxerr[0] - 1] = ln1[0];
        qxcpy(valp[last[0] - 1], vp2, lp2[0]);
        lp[last[0] - 1] = lp2[0];
        qxcpy(valn[last[0] - 1], vn2, ln2[0]);
        ln[last[0] - 1] = ln2[0];
      }

      // 30: CALL subroutine QPSRT TO MAINTAIN THE DESCENDING ORDERING
      // IN THE LIST OF ERROR ESTIMATES AND SELECT THE SUBINTERVAL
      // WITH NRMAX-TH LARGEST ERROR ESTIMATE (TO BE BISECTED NEXT).
      qpsrt(limit, last, maxerr, errmax, elist, iord, nrmax);
      if (errsum <= errbnd) {
        exit = 115;
        break;
      }
      if (ier[0] != 0) {
        break;
      }
      if (last[0] == 2) {
        small = Math.abs(b - a) * 0.375;
        erlarg = errsum;
        ertest = errbnd;
        rlist2[2 - 1] = area;
        continue;
      }
      if (noext) {
        continue;
      }
      erlarg -= erlast;
      if (Math.abs(b1 - a1) > small) {
        erlarg += erro12;
      }
      if (extrap) {
      } else {

        // TEST WHETHER THE INTERVAL TO BE BISECTED NEXT
        // IS THE SMALLEST INTERVAL.
        if (Math.abs(blist[maxerr[0] - 1] - alist[maxerr[0] - 1]) > small) {
          continue;
        }
        extrap = true;
        nrmax[0] = 2;
      }

      // 40: THE BOUND 0.3*ERTEST HAS BEEN INTRODUCED TO PERFORM A
      // MORE CAUTIOUS EXTRAPOLATION THAN IN THE ORIGINAL DQAGSE R(out)INE
      if (ierro == 3 || erlarg <= 0.3 * ertest) {
      } else {

        // THE SMALLEST INTERVAL HAS THE LARGEST ERROR.
        // BEFORE BISECTING DECREASE THE SUM OF THE ERRORS OVER THE
        // LARGER INTERVALS (ERLARG) AND PERFORM EXTRAPOLATION.
        id = nrmax[0];
        jupbnd = last[0];
        if (last[0] > (2 + (limit >> 1))) {
          jupbnd = limit + 3 - last[0];
        }
        for (k = id; k <= jupbnd; ++k) {
          maxerr[0] = iord[nrmax[0] - 1];
          errmax[0] = elist[maxerr[0] - 1];
          if (Math.abs(blist[maxerr[0] - 1] - alist[maxerr[0] - 1]) > small) {
            continue;
          }
          ++nrmax[0];
        }
      }

      // 60: PERFORM EXTRAPOLATION.
      ++numrl2[0];
      rlist2[numrl2[0] - 1] = area;
      qelg(numrl2, rlist2, reseps, abseps, res3la, nres, epmach, oflow);
      ++ktmin;
      if (ktmin > 5 && abserr[0] < 0.1e-2 * errsum) {
        ier[0] = 5;
      }
      if (abseps[0] >= abserr[0]) {
      } else {
        ktmin = 0;
        abserr[0] = abseps[0];
        result[0] = reseps[0];
        correc = erlarg;
        ertest = Math.max(epsabs, rerr * Math.abs(reseps[0]));
        if (abserr[0] <= ertest) {
          break;
        }
      }

      // 70: PREPARE BISECTION OF THE SMALLEST INTERVAL.
      if (numrl2[0] == 1) {
        noext = true;
      }
      if (ier[0] == 5) {
        break;
      }
      maxerr[0] = iord[1 - 1];
      errmax[0] = elist[maxerr[0] - 1];
      nrmax[0] = 1;
      extrap = false;
      small *= 0.5;
      erlarg = errsum;
    }

    if (exit == 100) {

      // 100: SET FINAL RESULT AND ERROR ESTIMATE.
      if (abserr[0] == oflow) {
        exit = 115;
      } else if (ier[0] + ierro == 0) {
        exit = 110;
      } else {
        if (ierro == 3) {
          abserr[0] += correc;
        }
        if (ier[0] == 0) {
          ier[0] = 3;
        }
        if (result[0] != 0.0 && area != 0.0) {
          if (abserr[0] / Math.abs(result[0]) > errsum / Math.abs(area)) {
            exit = 115;
          } else {
            exit = 110;
          }
        } else if (abserr[0] > errsum) {
          exit = 115;
        } else {
          if (area == 0.0) {
            if (ier[0] > 2) {
              --ier[0];
            }
            return;
          }
          exit = 110;
        }
      }
    }

    if (exit == 110) {

      // 110: TEST ON DIVERGENCE.
      if (ksgn == -1 && Math.max(Math.abs(result[0]), Math.abs(area)) <= defabs[0] * 0.1e-1) {
      } else if (0.1e-1 > (result[0] / area) || (result[0] / area) > 0.1e3
          || errsum > Math.abs(area)) {
        ier[0] = 6;
      }
      if (ier[0] > 2) {
        --ier[0];
      }
    } else {

      // 115: COMPUTE GLOBAL INTEGRAL SUM.
      result[0] = 0.0;
      for (int ii = 1; ii <= last[0]; ++ii) {
        result[0] += rlist[ii - 1];
      }
      abserr[0] = errsum;
      if (ier[0] > 2) {
        --ier[0];
      }
    }
  }

  private static void qxlqm(final DoubleUnaryOperator f, final double a, final double b,
      final double[] result, final double[] abserr, final double[] resabs, final double[] resasc,
      final double[] vr, final double[] vs, final int[] lr, final int[] ls, final int key,
      final double epmach, final double uflow, final double oflow, final int[] fev) {

    // TO COMPUTE I = INTEGRAL OF F OVER (A, B), WITH ERROR ESTIMATE
    // J = INTEGRAL OF ABS(F) OVER (A,B)
    final double[] resg = new double[1];
    final double[] resk = new double[1];
    double t, errold;
    int k, k0, k1, k2, key1;

    key1 = Math.max(key, 0);
    key1 = Math.min(key1, 4);
    k0 = Math.max(key1 - 2, 0);
    k1 = k0 + 1;
    k2 = Math.min(key1 + 1, 3);
    qxrul(f, a, b, resg, resabs, resasc, k0, k1, vr, vs, lr, ls, fev);
    errold = oflow;
    t = 10.0 * epmach;
    for (k = k1; k <= k2; ++k) {
      qxrul(f, a, b, resk, resabs, resasc, k, k1, vr, vs, lr, ls, fev);
      result[0] = resk[0];
      abserr[0] = Math.abs(resk[0] - resg[0]);
      if (resasc[0] != 0.0 && abserr[0] != 0.0) {
        abserr[0] = resasc[0] * Math.min(1.0, Math.pow(200.0 * abserr[0] / resasc[0], 1.5));
      }
      if (resabs[0] > uflow / t) {
        abserr[0] = Math.max(t * resabs[0], abserr[0]);
      }
      resg[0] = resk[0];
      if (abserr[0] > errold * 0.16) {
        break;
      }
      if (abserr[0] < 1000.0 * epmach * resabs[0]) {
        continue;
      }
      errold = abserr[0];
    }
  }

  private static void qxrul(final DoubleUnaryOperator f, final double xl, final double xu,
      final double[] y, final double[] ya, final double[] ym, final int ke, final int k1,
      final double[] fv1, final double[] fv2, final int[] l1, final int[] l2, final int[] fev) {

    // TO COMPUTE I = INTEGRAL OF F OVER (A,B), WITH ERROR ESTIMATE
    // AND CONDITIONALLY COMPUTE J = INTEGRAL OF ABS(F) OVER (A,B)
    // BY USING AN RMS RULE
    double ldl, y2, aa, bb, c;
    int j, i, is, k, ks;

    k = ke + 1;
    is = istart[k - 1];
    ks = length[k - 1];
    ldl = xu - xl;
    bb = ldl * 0.5;
    aa = xl + bb;
    y[0] = 0.0;
    for (i = 1; i <= ks; ++i) {
      c = bb * xx[i - 1];
      if (i > l1[0]) {
        fv1[i - 1] = f.applyAsDouble(aa + c);
        ++fev[0];
        if (aa + c >= xu && !Double.isFinite(fv1[i - 1])) {
          fv1[i - 1] = 0.0;
        }
      }
      if (i > l2[0]) {
        fv2[i - 1] = f.applyAsDouble(aa - c);
        ++fev[0];
        if (aa - c <= xl && !Double.isFinite(fv2[i - 1])) {
          fv2[i - 1] = 0.0;
        }
      }
      j = is + i;
      y[0] += (fv1[i - 1] + fv2[i - 1]) * ww[j - 1];
    }
    y2 = y[0];
    y[0] *= bb;
    if (l1[0] < ks) {
      l1[0] = ks;
    }
    if (l2[0] < ks) {
      l2[0] = ks;
    }
    if (ke != k1) {
      return;
    }
    ya[0] = 0.0;
    for (i = 1; i <= ks; ++i) {
      j = is + i;
      ya[0] += (Math.abs(fv1[i - 1]) + Math.abs(fv2[i - 1])) * ww[j - 1];
    }
    ya[0] *= Math.abs(bb);
    y2 *= 0.5;
    ym[0] = 0.0;
    for (i = 1; i <= ks; ++i) {
      j = is + i;
      ym[0] += (Math.abs(fv1[i - 1] - y2) + Math.abs(fv2[i - 1] - y2)) * ww[j - 1];
    }
    ym[0] *= Math.abs(bb);
  }

  private static void qxrrd(final DoubleUnaryOperator f, final double[] z, final int lz,
      final double xl, final double xu, final double[] r, final double[] s, final int[] lr,
      final int[] ls, final int[] fev) {

    // TO REORDER THE COMPUTED FUNCTIONAL VALUES BEFORE THE BISECTION
    // OF AN INTERVAL
    final double dlen = 0.5 * (xu - xl);
    final double centr = xl + dlen;
    r[1 - 1] = z[3 - 1];
    r[2 - 1] = z[9 - 1];
    r[3 - 1] = z[4 - 1];
    r[4 - 1] = z[5 - 1];
    r[5 - 1] = z[6 - 1];
    r[6 - 1] = z[10 - 1];
    r[7 - 1] = z[7 - 1];
    s[1 - 1] = z[3 - 1];
    s[2 - 1] = z[8 - 1];
    s[3 - 1] = z[2 - 1];
    s[7 - 1] = z[1 - 1];
    if (lz <= 11) {
      r[8 - 1] = f.applyAsDouble(centr + 0.375 * dlen);
      r[9 - 1] = f.applyAsDouble(centr + 0.625 * dlen);
      r[10 - 1] = f.applyAsDouble(centr + 0.96875 * dlen);
      fev[0] += 3;
      lr[0] = 10;
      if (lz != 11) {
        s[4 - 1] = f.applyAsDouble(centr - dlen * 0.75);
        ++fev[0];
      }
      if (lz == 11) {
        s[4 - 1] = z[11 - 1];
      }
      s[5 - 1] = f.applyAsDouble(centr - dlen * 0.875);
      s[6 - 1] = f.applyAsDouble(centr - dlen * 0.9375);
      s[8 - 1] = f.applyAsDouble(centr - dlen * 0.375);
      s[9 - 1] = f.applyAsDouble(centr - dlen * 0.625);
      s[10 - 1] = f.applyAsDouble(centr - dlen * 0.96875);
      fev[0] += 5;
      ls[0] = 10;
      return;
    }
    r[8 - 1] = z[12 - 1];
    r[9 - 1] = z[13 - 1];
    r[10 - 1] = z[14 - 1];
    lr[0] = 10;
    s[4 - 1] = z[11 - 1];
    s[5 - 1] = f.applyAsDouble(centr - dlen * 0.875);
    s[6 - 1] = f.applyAsDouble(centr - dlen * 0.9375);
    fev[0] += 2;
    if (lz <= 14) {
      s[8 - 1] = f.applyAsDouble(centr - dlen * 0.375);
      s[9 - 1] = f.applyAsDouble(centr - dlen * 0.625);
      s[10 - 1] = f.applyAsDouble(centr - dlen * 0.96875);
      fev[0] += 3;
      ls[0] = 10;
      return;
    }
    r[11 - 1] = z[18 - 1];
    r[12 - 1] = z[19 - 1];
    r[13 - 1] = z[20 - 1];
    r[14 - 1] = z[21 - 1];
    lr[0] = 14;
    s[8 - 1] = z[16 - 1];
    s[9 - 1] = z[15 - 1];
    s[10 - 1] = f.applyAsDouble(centr - dlen * 0.96875);
    ++fev[0];
    s[11 - 1] = z[17 - 1];
    ls[0] = 11;
  }

  private static void qxcpy(final double[] a, final double[] b, final int l) {

    // TO COPY THE REAL VECTOR B OF LENGTH L INTO THE REAL VECTOR A OF LENGTH L
    System.arraycopy(b, 0, a, 0, l);
  }

  private static void qpsrt(final int limit, final int[] last, final int[] maxerr,
      final double[] ermax, final double[] elist, final int[] iord, final int[] nrmax) {
    double errmax, errmin;
    int i, ibeg, ido, isucc, j, jbnd, jupbn, k;

    // CHECK WHETHER THE LIST CONTAINS MORE THAN TWO ERROR ESTIMATES.
    if (last[0] <= 2) {
      iord[1 - 1] = 1;
      iord[2 - 1] = 2;
      maxerr[0] = iord[nrmax[0] - 1];
      ermax[0] = elist[maxerr[0] - 1];
      return;
    }

    // 10: THIS PART OF THE ROUTINE IS ONLY EXECUTED IF,
    // DUE TO A DIFFICULT INTEGRAND, SUBDIVISION INCREASED
    // THE ERROR ESTIMATE. IN THE NORMAL CASE THE INSERT PROCEDURE
    // SHOULD START AFTER THE NRMAX-TH LARGEST ERROR ESTIMATE.
    errmax = elist[maxerr[0] - 1];
    if (nrmax[0] != 1) {
      ido = nrmax[0] - 1;
      for (i = 1; i <= ido; ++i) {
        isucc = iord[nrmax[0] - 1 - 1];
        if (errmax <= elist[isucc - 1]) {
          break;
        }
        iord[nrmax[0] - 1] = isucc;
        --nrmax[0];
      }
    }

    // 30: COMPUTE THE NUMBER OF ELEMENTS IN THE LIST TO BE MAINTAINED
    // IN DESCENDING ORDER. THIS NUMBER DEPENDS ON THE NUMBER OF
    // SUBDIVISIONS STILL ALLOWED.
    jupbn = last[0];
    if (last[0] > ((limit >> 1) + 2)) {
      jupbn = limit + 3 - last[0];
    }
    errmin = elist[last[0] - 1];

    // INSERT ERRMAX BY TRAVERSING THE LIST TOP-DOWN,
    // STARTING COMPARISON FROM THE ELEMENT ELIST(IORD(NRMAX+1)).
    jbnd = jupbn - 1;
    ibeg = nrmax[0] + 1;
    for (i = ibeg; i <= jbnd; ++i) {
      isucc = iord[i - 1];
      if (errmax >= elist[isucc - 1]) {

        // 60: INSERT ERRMIN BY TRAVERSING THE LIST BOTTOM-UP.
        iord[i - 1 - 1] = maxerr[0];
        k = jbnd;
        for (j = 1; j <= jbnd; ++j) {
          isucc = iord[k - 1];
          if (errmin < elist[isucc - 1]) {
            iord[k + 1 - 1] = last[0];
            maxerr[0] = iord[nrmax[0] - 1];
            ermax[0] = elist[maxerr[0] - 1];
            return;
          }
          iord[k + 1 - 1] = isucc;
          --k;
        }
        iord[i - 1] = last[0];
        maxerr[0] = iord[nrmax[0] - 1];
        ermax[0] = elist[maxerr[0] - 1];
        return;
      }
      iord[i - 1 - 1] = isucc;
    }
    iord[jbnd - 1] = maxerr[0];
    iord[jupbn - 1] = last[0];
    maxerr[0] = iord[nrmax[0] - 1];
    ermax[0] = elist[maxerr[0] - 1];
  }

  private static void qelg(final int[] n, final double[] epstab, final double[] result,
      final double[] abserr, final double[] res3la, final int[] nres, final double epmach,
      final double oflow) {
    double delta1, delta2, delta3, epsinf, error, err1, err2, err3, e0, e1, e1abs, e2, e3, res, ss,
        tol1, tol2, tol3;
    int i, ib, ib2, ie, indx, k1, k2, k3, limexp, newelm, num;

    ++nres[0];
    abserr[0] = oflow;
    result[0] = epstab[n[0] - 1];
    if (n[0] < 3) {
      abserr[0] = Math.max(abserr[0], 5.0 * epmach * Math.abs(result[0]));
      return;
    }
    limexp = 50;
    epstab[n[0] + 2 - 1] = epstab[n[0] - 1];
    newelm = (n[0] - 1) >> 1;
    epstab[n[0] - 1] = oflow;
    num = k1 = n[0];
    for (i = 1; i <= newelm; ++i) {
      k2 = k1 - 1;
      k3 = k1 - 2;
      res = epstab[k1 + 2 - 1];
      e0 = epstab[k3 - 1];
      e1 = epstab[k2 - 1];
      e2 = res;
      e1abs = Math.abs(e1);
      delta2 = e2 - e1;
      err2 = Math.abs(delta2);
      tol2 = Math.max(Math.abs(e2), e1abs) * epmach;
      delta3 = e1 - e0;
      err3 = Math.abs(delta3);
      tol3 = Math.max(e1abs, Math.abs(e0)) * epmach;
      if (err2 <= tol2 && err3 <= tol3) {

        // IF E0, E1 AND E2 ARE EQUAL TO WITHIN MACHINE
        // ACCURACY, CONVERGENCE IS ASSUMED.
        // RESULT = E2
        // ABSERR = ABS(E1-E0) + ABS(E2-E1)
        result[0] = res;
        abserr[0] = err2 + err3;
        abserr[0] = Math.max(abserr[0], 5.0 * epmach * Math.abs(result[0]));
        return;
      }
      e3 = epstab[k1 - 1];
      epstab[k1 - 1] = e1;
      delta1 = e1 - e3;
      err1 = Math.abs(delta1);
      tol1 = Math.max(e1abs, Math.abs(e3)) * epmach;

      // IF TWO ELEMENTS ARE VERY CLOSE TO EACH OTHER, OMIT
      // A PART OF THE TABLE BY ADJUSTING THE VALUE OF N
      if (err1 <= tol1 || err2 <= tol2 || err3 <= tol3) {
        n[0] = i + i - 1;
        break;
      }
      ss = 1.0 / delta1 + 1.0 / delta2 - 1.0 / delta3;
      epsinf = Math.abs(ss * e1);

      // TEST TO DETECT IRREGULAR BEHAVIOUR IN THE TABLE, AND EVENTUALLY
      // OMIT A PART OF THE TABLE ADJUSTING THE VALUE OF N.
      if (epsinf <= 0.1e-3) {
        n[0] = i + i - 1;
        break;
      }

      // 30: COMPUTE A NEW ELEMENT AND EVENTUALLY ADJUST THE VALUE
      // OF RESULT.
      res = e1 + 1.0 / ss;
      epstab[k1 - 1] = res;
      k1 -= 2;
      error = err2 + Math.abs(res - e2) + err3;
      if (error > abserr[0]) {
        continue;
      }
      abserr[0] = error;
      result[0] = res;
    }

    // 50: SHIFT THE TABLE.
    if (n[0] == limexp) {
      n[0] = ((limexp >> 1) << 1) - 1;
    }
    ib = 1;
    if (((num >> 1) << 1) == num) {
      ib = 2;
    }
    ie = newelm + 1;
    for (i = 1; i <= ie; ++i) {
      ib2 = ib + 2;
      epstab[ib - 1] = epstab[ib2 - 1];
      ib = ib2;
    }
    if (num != n[0]) {
      indx = num - n[0] + 1;
      for (i = 1; i <= n[0]; ++i) {
        epstab[i - 1] = epstab[indx - 1];
        ++indx;
      }
    }
    if (nres[0] < 4) {
      res3la[nres[0] - 1] = result[0];
      abserr[0] = oflow;
      abserr[0] = Math.max(abserr[0], 5.0 * epmach * Math.abs(result[0]));
      return;
    }

    // 90: COMPUTE ERROR ESTIMATE
    abserr[0] = Math.abs(result[0] - res3la[3 - 1]) + Math.abs(result[0] - res3la[2 - 1])
        + Math.abs(result[0] - res3la[1 - 1]);
    res3la[1 - 1] = res3la[2 - 1];
    res3la[2 - 1] = res3la[3 - 1];
    res3la[3 - 1] = result[0];
    abserr[0] = Math.max(abserr[0], 5.0 * epmach * Math.abs(result[0]));
  }
}
