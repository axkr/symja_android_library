package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.LossOfPrecisionException;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numerics.functions.ZetaJS;
import org.matheclipse.external.apfloat.ApfloatRiemannSiegelMath;

public class ZetaZero extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    final IExpr arg1 = ast.arg1();
    final int index = arg1.isInteger() ? arg1.toIntDefault() : 0;
    if (index == 0 || index == Integer.MIN_VALUE) {
      if (arg1.isNumber()) {
        // ZetaZero(0) and ZetaZero(3.0) - Nonzero integer expected at position `1` in `2`.
        return Errors.printMessage(S.ZetaZero, "intnz", F.List(F.C1, S.ZetaZero), engine);
      }
      return F.NIL;
    }
    // ZetaZero(-k) is the conjugate of the k-th zero
    final boolean conjugate = index < 0;
    final int k = Math.abs(index);
    if (conjugate && ast.isAST2()) {
      return F.NIL;
    }

    final boolean arbitrary = engine.isArbitraryMode();
    final boolean doubleMode = engine.isDoubleMode();
    if (!arbitrary && !doubleMode) {
      // keep symbolic if not evaluated numerically (e.g. via N(...))
      return F.NIL;
    }

    try {
      final FixedPrecisionApfloatHelper h =
          arbitrary ? EvalEngine.getApfloat() : EvalEngine.getApfloatDouble();
      final long precision = h.precision();

      Apfloat tMin = null;
      if (ast.isAST2()) {
        double tMinDouble = ast.arg2().evalfNaN();
        if (Double.isNaN(tMinDouble) || Double.isInfinite(tMinDouble)) {
          return F.NIL;
        }
        tMin = new Apfloat(tMinDouble, precision);
      }

      Apfloat imaginaryPart = ZetaZero.zetaZeroImaginaryPart(h, k, tMin);
      if (conjugate) {
        imaginaryPart = imaginaryPart.negate();
      }
      if (arbitrary) {
        final Apfloat half = new Apfloat("0.5", precision);
        return F.complexNum(new Apcomplex(half, imaginaryPart.precision(precision)));
      }
      return F.complexNum(0.5, imaginaryPart.doubleValue());
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(S.ZetaZero, rex, engine);
    }
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NHOLDFIRST, Attribute.NUMERICFUNCTION);
  }

  /** Riemann-Siegel Z function at {@code t} (real-valued). */
  public static Apfloat zzZ(FixedPrecisionApfloatHelper h, Apfloat t, Apfloat half, Apfloat quarter,
      Apfloat two, Apfloat logPi) {
    Apfloat theta = RiemannSiegelTheta.zzTheta(h, t, quarter, two, logPi);
    Apcomplex s = new Apcomplex(half, t);
    Apcomplex zeta = h.zeta(s);
    Apcomplex factor = h.exp(new Apcomplex(Apfloat.ZERO, theta));
    return h.multiply(factor, zeta).real();
  }

  /** Refine a sign-change bracket {@code [a,b]} of Z(t) by bisection. */
  private static Apfloat zzBisect(FixedPrecisionApfloatHelper h, Apfloat a, Apfloat b, Apfloat half,
      Apfloat quarter, Apfloat two, Apfloat logPi, Apfloat tol, long precision) {
    Apfloat fa = ZetaZero.zzZ(h, a, half, quarter, two, logPi);
    long maxIter = Math.min(100000L, precision * 4 + 80);
    Apfloat m = a.add(b).divide(two);
    for (long i = 0; i < maxIter; i++) {
      m = a.add(b).divide(two);
      Apfloat fm = ZetaZero.zzZ(h, m, half, quarter, two, logPi);
      if (fm.signum() == 0 || ApfloatMath.abs(b.subtract(a)).compareTo(tol) < 0) {
        return m;
      }
      if (fm.signum() == fa.signum()) {
        a = m;
        fa = fm;
      } else {
        b = m;
      }
    }
    return m;
  }

  /**
   * Find the imaginary part of the {@code index}-th nontrivial zeta zero. A cheap machine-precision
   * Riemann-Siegel locate phase brackets the zero and seeds a high-precision root polish; if that
   * fast path fails to produce a valid bracket the method falls back to the arbitrary-precision
   * scan-and-bisect in {@link #zzFindZeroApfloat}.
   */
  private static Apfloat zzFindZero(FixedPrecisionApfloatHelper h, int index, Apfloat half,
      Apfloat quarter, Apfloat two, Apfloat logPi, Apfloat tol, long precision) {
    double[] bracket = ZetaZero.locateBracketDouble(index);
    if (bracket != null) {
      Apfloat root =
          ZetaZero.zzPolish(h, bracket[0], bracket[1], half, quarter, two, logPi, tol, precision);
      if (root != null) {
        return root;
      }
    }
    return ZetaZero.zzFindZeroApfloat(h, index, half, quarter, two, logPi, tol, precision);
  }

  /** Above this height the O(t) double-precision Z gives way to the O(sqrt(t)) Riemann-Siegel Z. */
  private static final double EM_DOUBLE_LIMIT = 1.0e5;

  /** Digits of the Riemann-Siegel Z which stands in where the double one cannot tell the sign. */
  private static final long SIGN_PRECISION = 30;

  /**
   * Machine-precision {@code Z(t)}. Up to {@link #EM_DOUBLE_LIMIT} it is computed from zeta(1/2 +
   * I*t) by Euler-Maclaurin summation, accurate to about {@code 1e-9} at {@code t ~ 1e5} - enough to
   * see the tiny extremum between the two zeros of a Lehmer pair, which the leading Riemann-Siegel
   * term alone cannot; above it the Riemann-Siegel formula with its correction terms takes over.
   * Its phases lose the digits of <code>t*Log(t)</code>, so a value below that error is calculated
   * again in arbitrary precision: the sign of the result is right.
   */
  static double zDouble(double t) {
    if (t > EM_DOUBLE_LIMIT) {
      double z = ApfloatRiemannSiegelMath.riemannSiegelZ(t);
      if (Math.abs(z) <= ApfloatRiemannSiegelMath.doubleError(t)) {
        z = ApfloatRiemannSiegelMath
            .riemannSiegelZ(new Apfloat(new java.math.BigDecimal(t), SIGN_PRECISION)).doubleValue();
      }
      return z;
    }
    // N direct terms with t/(2 Pi N) = 1/Pi, so the Bernoulli tail shrinks by 1/Pi^2 per term
    int n = (int) Math.ceil(0.5 * t) + 30;
    double re = 0.0;
    double im = 0.0;
    for (int k = 1; k < n; k++) {
      double lnk = Math.log(k);
      double mag = 1.0 / Math.sqrt(k);
      double phase = t * lnk;
      re += mag * Math.cos(phase);
      im -= mag * Math.sin(phase);
    }
    double lnN = Math.log(n);
    double magN = 1.0 / Math.sqrt(n);
    // N^(-s)
    double nsRe = magN * Math.cos(t * lnN);
    double nsIm = -magN * Math.sin(t * lnN);
    // N^(1-s)/(s-1) with s-1 = -1/2 + I*t
    double aRe = n * nsRe;
    double aIm = n * nsIm;
    double den = 0.25 + t * t;
    re += (aRe * -0.5 + aIm * t) / den;
    im += (aIm * -0.5 - aRe * t) / den;
    re += 0.5 * nsRe;
    im += 0.5 * nsIm;
    // Bernoulli tail: B_2j/(2j)! * s(s+1)...(s+2j-2) * N^(-s-2j+1)
    double riseRe = 0.5;
    double riseIm = t;
    double powRe = nsRe / n;
    double powIm = nsIm / n;
    for (int j = 1; j <= 10; j++) {
      double c = BERNOULLI_OVER_FACTORIAL[j - 1];
      re += c * (riseRe * powRe - riseIm * powIm);
      im += c * (riseRe * powIm + riseIm * powRe);
      // next rising factorial: times (s+2j-1)(s+2j)
      for (int m = 2 * j - 1; m <= 2 * j; m++) {
        double fRe = 0.5 + m;
        double nRe = riseRe * fRe - riseIm * t;
        double nIm = riseRe * t + riseIm * fRe;
        riseRe = nRe;
        riseIm = nIm;
      }
      powRe /= (double) n * n;
      powIm /= (double) n * n;
    }
    double theta = ZetaJS.riemannSiegelThetaDouble(t);
    return Math.cos(theta) * re - Math.sin(theta) * im;
  }

  /** B_2j / (2j)! for j = 1..10. */
  private static final double[] BERNOULLI_OVER_FACTORIAL = {1.0 / 12.0, -1.0 / 720.0,
      1.0 / 30240.0, -1.0 / 1209600.0, 1.0 / 47900160.0, -691.0 / 1307674368000.0,
      7.0 / 523069747200.0, -3617.0 / 10670622842880000.0, 43867.0 / 5109094217170944000.0,
      -174611.0 / 802857662698291200000.0};

  /** The Gram point g_n, where theta(g_n) = n*Pi, for {@code n >= -1}. */
  static double gramPoint(int n) {
    double t = Math.max(9.0, ZetaJS.zetaZeroEstimate(n + 1));
    for (int i = 0; i < 60; i++) {
      double f = ZetaJS.riemannSiegelThetaDouble(t) - n * Math.PI;
      double dt = f / (0.5 * Math.log(t / (2.0 * Math.PI)));
      t -= dt;
      if (Math.abs(dt) <= 1e-13 * t) {
        break;
      }
    }
    return t;
  }

  /** Whether g_n is a good Gram point, (-1)^n Z(g_n) > 0; then N(g_n) = n + 1 (Rosser's rule). */
  private static boolean isGoodGramPoint(int n, double g) {
    double z = zDouble(g);
    return (n % 2 == 0) ? z > 0.0 : z < 0.0;
  }

  /**
   * The exceptions to Rosser's rule: pairs of indices <code>a, b</code> of Gram points, between
   * which there are <code>b - a</code> zeros although a Gram block in between does not hold as
   * many zeros as it has Gram intervals. The list is complete for indices up to 400,000,000; the
   * entries above that are the ones noted in the literature, and the zeros up there are counted
   * with Turing's method. It is the list of mpmath, which is
   * taken from R. P. Brent, J. van de Lune, H. J. J. te Riele, D. T. Winter, On the zeros of the
   * Riemann zeta function in the critical strip II, Mathematics of Computation 39 (1982), 681-688,
   * and J. van de Lune, H. J. J. te Riele, On the zeros of the Riemann zeta function in the
   * critical strip III, Mathematics of Computation 41 (1983), 759-767.
   */
  private static final int[] ROSSER_EXCEPTIONS = {
      13999525, 13999528, 30783329, 30783332, 30930926, 30930929, 37592215, 37592218,
      40870156, 40870159, 43628107, 43628110, 46082042, 46082045, 46875667, 46875670,
      49624540, 49624543, 50799238, 50799241, 55221453, 55221456, 56948779, 56948782,
      60515663, 60515666, 61331766, 61331770, 69784843, 69784846, 75052114, 75052117,
      79545240, 79545243, 79652247, 79652250, 83088043, 83088046, 83689522, 83689525,
      85348958, 85348961, 86513820, 86513823, 87947596, 87947599, 88600095, 88600098,
      93681183, 93681186, 100316551, 100316554, 100788444, 100788447, 106236172, 106236175,
      106941327, 106941330, 107287955, 107287958, 107532016, 107532019, 110571044, 110571047,
      111885253, 111885256, 113239783, 113239786, 120159903, 120159906, 121424391, 121424394,
      121692931, 121692934, 121934170, 121934173, 122612848, 122612851, 126116567, 126116570,
      127936513, 127936516, 128710277, 128710280, 129398902, 129398905, 130461096, 130461099,
      131331947, 131331950, 137334071, 137334074, 137832603, 137832606, 138799471, 138799474,
      139027791, 139027794, 141617806, 141617809, 144454931, 144454934, 145402379, 145402382,
      146130245, 146130248, 147059770, 147059773, 147896099, 147896102, 151097113, 151097116,
      152539438, 152539441, 152863168, 152863171, 153522726, 153522729, 155171524, 155171527,
      155366607, 155366610, 157260686, 157260689, 157269224, 157269227, 157755123, 157755126,
      158298484, 158298487, 160369050, 160369053, 162962787, 162962790, 163724709, 163724712,
      164198113, 164198116, 164689301, 164689305, 164880228, 164880231, 166201932, 166201935,
      168573836, 168573839, 169750763, 169750766, 170375507, 170375510, 170704879, 170704882,
      172000992, 172000995, 173289941, 173289944, 173737613, 173737616, 174102513, 174102516,
      174284990, 174284993, 174500513, 174500516, 175710609, 175710612, 176870843, 176870846,
      177332732, 177332735, 177902861, 177902864, 179979095, 179979098, 181233726, 181233729,
      181625435, 181625438, 182105255, 182105259, 182223559, 182223562, 191116404, 191116407,
      191165599, 191165602, 191297535, 191297539, 192485616, 192485619, 193264634, 193264638,
      194696968, 194696971, 195876805, 195876808, 195916548, 195916551, 196395160, 196395163,
      196676303, 196676306, 197889882, 197889885, 198014122, 198014125, 199235289, 199235292,
      201007375, 201007378, 201030605, 201030608, 201184290, 201184293, 201685414, 201685418,
      202762875, 202762878, 202860957, 202860960, 203832577, 203832580, 205880544, 205880547,
      206357111, 206357114, 207159767, 207159770, 207167343, 207167346, 207482539, 207482543,
      207669540, 207669543, 208053426, 208053429, 208110027, 208110030, 209513826, 209513829,
      212623522, 212623525, 213841715, 213841718, 214012333, 214012336, 214073567, 214073570,
      215170600, 215170603, 215881039, 215881042, 216274604, 216274607, 216957120, 216957123,
      217323208, 217323211, 218799264, 218799267, 218803557, 218803560, 219735146, 219735149,
      219830062, 219830065, 219897904, 219897907, 221205545, 221205548, 223601929, 223601932,
      223907076, 223907079, 223970397, 223970400, 224874044, 224874048, 225291157, 225291160,
      227481734, 227481737, 228006442, 228006445, 228357900, 228357903, 228386399, 228386402,
      228907446, 228907449, 228984552, 228984555, 229140285, 229140288, 231810024, 231810027,
      232838062, 232838065, 234389088, 234389091, 235588194, 235588197, 236645695, 236645698,
      236962876, 236962879, 237516723, 237516727, 240004911, 240004914, 240221306, 240221309,
      241389213, 241389217, 241549003, 241549006, 241729717, 241729720, 241743684, 241743687,
      243780200, 243780203, 243801317, 243801320, 244122072, 244122075, 244691224, 244691227,
      244841577, 244841580, 245813461, 245813464, 246299475, 246299478, 246450176, 246450179,
      249069349, 249069352, 250076378, 250076381, 252442157, 252442160, 252904231, 252904234,
      255145220, 255145223, 255285971, 255285974, 256713230, 256713233, 257992082, 257992085,
      258447955, 258447959, 259298045, 259298048, 262141503, 262141506, 263681743, 263681746,
      266527881, 266527885, 266617122, 266617125, 266628044, 266628047, 267305763, 267305766,
      267388404, 267388407, 267441672, 267441675, 267464886, 267464889, 267554907, 267554910,
      269787480, 269787483, 270881434, 270881437, 270997583, 270997586, 272096378, 272096381,
      272583009, 272583012, 274190881, 274190884, 274268747, 274268750, 275297429, 275297432,
      275545476, 275545479, 275898479, 275898482, 275953000, 275953003, 277117197, 277117201,
      277447310, 277447313, 279059657, 279059660, 279259144, 279259147, 279513636, 279513639,
      279849069, 279849072, 280291419, 280291422, 281449425, 281449428, 281507953, 281507956,
      281825600, 281825603, 282547093, 282547096, 283120963, 283120966, 283323493, 283323496,
      284764535, 284764538, 286172639, 286172642, 286688824, 286688827, 287222172, 287222175,
      287235534, 287235537, 287304861, 287304864, 287433571, 287433574, 287823551, 287823554,
      287872422, 287872425, 288766615, 288766618, 290122963, 290122966, 290450849, 290450853,
      291426141, 291426144, 292810353, 292810356, 293109861, 293109864, 293398054, 293398057,
      294134426, 294134429, 294216438, 294216441, 295367141, 295367144, 297834111, 297834114,
      299099969, 299099972, 300746958, 300746961, 301097423, 301097426, 301834209, 301834212,
      302554791, 302554794, 303497445, 303497448, 304165344, 304165347, 304790218, 304790222,
      305302352, 305302355, 306785996, 306785999, 307051443, 307051446, 307481539, 307481542,
      308605569, 308605572, 309237610, 309237613, 310509287, 310509290, 310554057, 310554060,
      310646345, 310646348, 311274896, 311274899, 311894272, 311894275, 312269470, 312269473,
      312306601, 312306605, 312683193, 312683196, 314499804, 314499807, 314636802, 314636805,
      314689897, 314689900, 314721319, 314721322, 316132890, 316132893, 316217470, 316217474,
      316465705, 316465708, 316542790, 316542793, 320822347, 320822350, 321733242, 321733245,
      324413970, 324413973, 325890638, 325890641, 325950140, 325950143, 326675884, 326675887,
      326704208, 326704211, 327596247, 327596250, 328123172, 328123175, 328182212, 328182215,
      328257498, 328257501, 328315836, 328315839, 328800974, 328800977, 328998509, 328998512,
      329725370, 329725373, 332080601, 332080604, 332221246, 332221249, 332299899, 332299902,
      332532822, 332532825, 333334544, 333334548, 333881266, 333881269, 334703267, 334703270,
      334875138, 334875141, 336531451, 336531454, 336825907, 336825910, 336993167, 336993170,
      337493998, 337494001, 337861034, 337861037, 337899191, 337899194, 337958123, 337958126,
      342331982, 342331985, 342676068, 342676071, 347063781, 347063784, 347697348, 347697351,
      347954319, 347954322, 348162775, 348162778, 349210702, 349210705, 349212913, 349212916,
      349248650, 349248653, 349913500, 349913503, 350891529, 350891532, 351089323, 351089326,
      351826158, 351826161, 352228580, 352228583, 352376244, 352376247, 352853758, 352853761,
      355110439, 355110442, 355808090, 355808094, 355941556, 355941559, 356360231, 356360234,
      356586657, 356586660, 356892926, 356892929, 356908232, 356908235, 357738762, 357738765,
      357912730, 357912733, 358120344, 358120347, 359044096, 359044099, 360819357, 360819360,
      361399662, 361399666, 362361315, 362361318, 363610112, 363610115, 363964804, 363964807,
      364527375, 364527378, 365090327, 365090330, 365414539, 365414542, 366738474, 366738477,
      368714778, 368714783, 368714778, 368714783, 368831545, 368831548, 368902387, 368902390,
      370109769, 370109772, 370963333, 370963336, 372541136, 372541140, 372681562, 372681565,
      373009410, 373009413, 373458970, 373458973, 375648658, 375648661, 376834728, 376834731,
      377119945, 377119948, 377335703, 377335706, 378091745, 378091748, 379139522, 379139525,
      380279160, 380279163, 380619442, 380619445, 381244231, 381244234, 382327446, 382327450,
      382357073, 382357076, 383545479, 383545482, 384363766, 384363769, 384401786, 384401790,
      385198212, 385198215, 385824476, 385824479, 385908194, 385908197, 386946806, 386946809,
      387592175, 387592179, 388329293, 388329296, 388679566, 388679569, 388832142, 388832145,
      390087103, 390087106, 390190926, 390190930, 390331207, 390331210, 391674495, 391674498,
      391937831, 391937834, 391951632, 391951636, 392963986, 392963989, 393007921, 393007924,
      393373210, 393373213, 393759572, 393759575, 394036662, 394036665, 395813866, 395813869,
      395956690, 395956693, 396031670, 396031673, 397076433, 397076436, 397470601, 397470604,
      398289458, 398289461, 437953499, 437953504, 526196233, 526196238, 744719566, 744719571,
      750375857, 750375862, 958241932, 958241937, 983377342, 983377347, 1003780080, 1003780085,
      1070232754, 1070232759, 1209834865, 1209834870, 1257209100, 1257209105,
      1368002233, 1368002238
  };

  /**
   * The position in {@link #ROSSER_EXCEPTIONS} of the block which the <code>index</code>-th zero
   * lies in, or <code>-1</code>.
   */
  private static int rosserException(int index) {
    for (int i = 0; i < ROSSER_EXCEPTIONS.length; i += 2) {
      if (ROSSER_EXCEPTIONS[i] <= index - 2 && index - 1 <= ROSSER_EXCEPTIONS[i + 1]) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Machine-precision locate phase: bracket the {@code index}-th zeta zero by counting.
   *
   * <p>
   * The zero is looked for between two good Gram points g_a < g_b around it, where the number of
   * zeros below each is known: {@code N(g_n) = n + 1} (Rosser's rule). The sign changes of Z in
   * between are counted, and when there are fewer than {@code b - a} a pair of zeros is hiding in
   * a step - found where Z comes close to the axis and turns back. The {@code index}-th zero is
   * then picked by its position in the count, not by its distance to an estimate, which gets a
   * close pair (the Lehmer pair #6709/#6710, 0.038 apart) and a zero off its Gram interval right.
   *
   * @return the bracket, or {@code null} to fall back to the arbitrary-precision search
   */
  private static double[] locateBracketDouble(int index) {
    if (index > Integer.MAX_VALUE - 200) {
      // the Gram points have int indices
      throw new ArgumentTypeException("ZetaZero: unable to count the zeros at index " + index);
    }
    int b = index - 1;
    int a = index - 2;
    double gb;
    double ga;
    int exception = rosserException(index);
    if (exception >= 0) {
      // the good Gram points next to the zero don't count the zeros here: the block of Gram
      // intervals around the exception does
      a = ROSSER_EXCEPTIONS[exception];
      b = ROSSER_EXCEPTIONS[exception + 1];
      ga = gramPoint(a);
      gb = gramPoint(b);
    } else {
      gb = gramPoint(b);
      for (int i = 0; i < 100 && !isGoodGramPoint(b, gb); i++) {
        gb = gramPoint(++b);
      }
      ga = a >= -1 ? gramPoint(a) : 1.0;
      for (int i = 0; i < 100 && a >= 0 && !isGoodGramPoint(a, ga); i++) {
        ga = gramPoint(--a);
      }
      if (index > ROSSER_EXCEPTIONS_COMPLETE) {
        // no list of the exceptions up here: Turing's method. The number of zeros below a good
        // Gram point is the expected one if enough Gram blocks on both sides of it hold as many
        // zeros as they have Gram intervals; a block which doesn't joins the block in the middle.
        int guard = 2 * sureNumberOfBlocks(gb);
        int end = b;
        double gEnd = gb;
        for (int good = 0, blocks = 0; good < guard; blocks++) {
          if (blocks > MAX_TURING_BLOCKS || end > Integer.MAX_VALUE - 200) {
            // the Gram points have int indices
            throw new ArgumentTypeException("ZetaZero: unable to count the zeros at index " + index);
          }
          int next = end + 1;
          double gNext = gramPoint(next);
          for (int i = 0; i < 100 && !isGoodGramPoint(next, gNext); i++) {
            gNext = gramPoint(++next);
          }
          if (blockBrackets(end, gEnd, next, gNext) != null) {
            good++;
          } else {
            good = 0;
            b = next;
            gb = gNext;
          }
          end = next;
          gEnd = gNext;
        }
        end = a;
        gEnd = ga;
        for (int good = 0, blocks = 0; good < guard; blocks++) {
          if (blocks > MAX_TURING_BLOCKS) {
            throw new ArgumentTypeException("ZetaZero: unable to count the zeros at index " + index);
          }
          int next = end - 1;
          double gNext = gramPoint(next);
          for (int i = 0; i < 100 && !isGoodGramPoint(next, gNext); i++) {
            gNext = gramPoint(--next);
          }
          if (blockBrackets(next, gNext, end, gEnd) != null) {
            good++;
          } else {
            good = 0;
            a = next;
            ga = gNext;
          }
          end = next;
          gEnd = gNext;
        }
      }
    }
    List<double[]> brackets = blockBrackets(a, ga, b, gb);
    // below g_{-1} ~ 9.67 there is no zero at all
    int position = index - Math.max(a + 1, 0) - 1;
    if (brackets == null || position < 0 || position >= brackets.size()) {
      return null;
    }
    double[] bracket = brackets.get(position);
    return ZetaZero.refineBracketDouble(bracket[0], bracket[1]);
  }

  /** Up to this index {@link #ROSSER_EXCEPTIONS} has every exception. */
  private static final int ROSSER_EXCEPTIONS_COMPLETE = 400000000;

  private static final int MAX_TURING_BLOCKS = 2000;

  /**
   * The sign-change brackets of Z between the Gram points <code>g_a</code> and <code>g_b</code> if
   * there are as many of them as zeros are expected there, <code>b - a</code>; <code>null</code>
   * otherwise.
   */
  private static List<double[]> blockBrackets(int a, double ga, int b, double gb) {
    int expected = (b + 1) - Math.max(a + 1, 0);
    double gap = 2.0 * Math.PI / Math.max(Math.log(gb / (2.0 * Math.PI)), 0.3);
    double step = gap / 8.0;
    List<double[]> brackets = null;
    for (int refine = 0; refine < 8; refine++) {
      brackets = signChanges(ga, gb, step, expected);
      if (brackets.size() >= expected) {
        break;
      }
      step *= 0.5;
    }
    return brackets.size() == expected ? brackets : null;
  }

  /**
   * The number of consecutive Gram blocks obeying Rosser's rule, on each side of a Gram point at
   * the height <code>g</code>, which make the number of zeros below it the expected one: the
   * smaller of the bounds of R. P. Brent, On the zeros of the Riemann zeta function in the critical
   * strip, Mathematics of Computation 33 (1979), 1361-1372, and T. Trudgian, Improvements to
   * Turing's method, Mathematics of Computation.
   */
  private static int sureNumberOfBlocks(double g) {
    double lg = Math.log(g);
    double brent = 0.0061 * lg * lg + 0.08 * lg;
    double trudgian = 0.0031 * lg * lg + 0.11 * lg;
    return (int) Math.ceil(Math.min(brent, trudgian));
  }

  /**
   * The sign-change brackets of Z in {@code [lo, hi]}, sampled with {@code step}; when fewer than
   * {@code expected} are seen, each place where Z approaches the axis and turns back is searched
   * for a hidden pair.
   */
  private static List<double[]> signChanges(double lo, double hi, double step, int expected) {
    int n = Math.max(2, (int) Math.ceil((hi - lo) / step));
    double[] x = new double[n + 1];
    double[] z = new double[n + 1];
    for (int i = 0; i <= n; i++) {
      x[i] = i == n ? hi : lo + (hi - lo) * i / n;
      z[i] = zDouble(x[i]);
    }
    List<double[]> brackets = new ArrayList<double[]>();
    for (int i = 1; i <= n; i++) {
      if (z[i - 1] != 0.0 && z[i] != 0.0 && Math.signum(z[i - 1]) != Math.signum(z[i])) {
        brackets.add(new double[] {x[i - 1], x[i]});
      }
    }
    if (brackets.size() >= expected) {
      return brackets;
    }
    // candidates for a hidden pair: a sample closer to the axis than both its neighbours
    List<Integer> candidates = new ArrayList<Integer>();
    for (int i = 1; i < n; i++) {
      double s = Math.signum(z[i]);
      if (s != 0 && Math.signum(z[i - 1]) == s && Math.signum(z[i + 1]) == s
          && Math.abs(z[i]) <= Math.abs(z[i - 1]) && Math.abs(z[i]) <= Math.abs(z[i + 1])) {
        candidates.add(i);
      }
    }
    candidates.sort((p, q) -> Double.compare(Math.abs(z[p]), Math.abs(z[q])));
    for (int i : candidates) {
      if (brackets.size() >= expected) {
        break;
      }
      double sign = Math.signum(z[i]);
      double m = closestToAxis(x[i - 1], x[i + 1], sign);
      double zm = zDouble(m);
      if (zm != 0.0 && Math.signum(zm) != sign) {
        brackets.add(new double[] {x[i - 1], m});
        brackets.add(new double[] {m, x[i + 1]});
      }
    }
    brackets.sort((p, q) -> Double.compare(p[0], q[0]));
    return brackets;
  }

  /** Golden-section search for the point of {@code [a, b]} where {@code sign * Z} is least. */
  private static double closestToAxis(double a, double b, double sign) {
    final double r = 0.5 * (Math.sqrt(5.0) - 1.0);
    double c = b - r * (b - a);
    double d = a + r * (b - a);
    double fc = sign * zDouble(c);
    double fd = sign * zDouble(d);
    for (int i = 0; i < 60 && (b - a) > 1e-12 * Math.max(1.0, Math.abs(b)); i++) {
      if (fc < 0.0) {
        return c;
      }
      if (fd < 0.0) {
        return d;
      }
      if (fc < fd) {
        b = d;
        d = c;
        fd = fc;
        c = b - r * (b - a);
        fc = sign * zDouble(c);
      } else {
        a = c;
        c = d;
        fc = fd;
        d = a + r * (b - a);
        fd = sign * zDouble(d);
      }
    }
    return fc < fd ? c : d;
  }

  /**
   * Tighten a double-precision sign-change bracket by bisection, down to a width of about
   * {@code 1e-12 t} - well above the error of {@link #zDouble(double)}, so the endpoints keep
   * reliable opposite signs at full precision, and narrow enough that the full precision polish
   * needs only a few evaluations. Above {@link #EM_DOUBLE_LIMIT} the error of the double
   * Riemann-Siegel Z sets the width instead: closer to the zero every evaluation is an arbitrary
   * precision one.
   */
  private static double[] refineBracketDouble(double a, double b) {
    double fa = zDouble(a);
    double t = Math.max(0.5 * (a + b), 1.0);
    double target =
        t > EM_DOUBLE_LIMIT ? Math.max(4.0 * ApfloatRiemannSiegelMath.doubleError(t), 1e-12 * t)
            : 1e-12 * Math.max(t, 100.0);
    for (int i = 0; i < 80 && (b - a) > target; i++) {
      double m = 0.5 * (a + b);
      double fm = zDouble(m);
      if (fm == 0.0) {
        return new double[] {m - target, m + target};
      }
      if (Math.signum(fm) == Math.signum(fa)) {
        a = m;
        fa = fm;
      } else {
        b = m;
      }
    }
    return new double[] {a, b};
  }

  /**
   * Machine-precision estimate of the imaginary part of the {@code index}-th zeta zero, used to
   * calibrate indices against a lower bound without paying for an arbitrary-precision root polish.
   */
  private static double zeroImagDouble(int index) {
    double[] bracket = ZetaZero.locateBracketDouble(index);
    if (bracket == null) {
      return ZetaJS.zetaZeroEstimate(index);
    }
    return 0.5 * (bracket[0] + bracket[1]);
  }

  /**
   * Polish a zero to full precision from a double-precision bracket using the secant method with a
   * bisection safeguard on the arbitrary-precision Riemann-Siegel {@code Z(t)}. The secant iterates
   * converge superlinearly (order ~1.6), so only a handful of arbitrary-precision zeta evaluations
   * are needed instead of the ~{@code 3.3 * precision} of a bisection. The bracket {@code [a, b]}
   * is kept straddling the root and a bisection step is taken whenever a secant step would leave
   * it. Returns {@code null} if the bracket does not straddle a sign change at full precision, so
   * the caller can fall back to the arbitrary-precision search.
   */
  private static Apfloat zzPolish(FixedPrecisionApfloatHelper h, double aDouble, double bDouble,
      Apfloat half, Apfloat quarter, Apfloat two, Apfloat logPi, Apfloat tol, long precision) {
    Apfloat a = new Apfloat(aDouble, precision);
    Apfloat b = new Apfloat(bDouble, precision);
    java.util.function.UnaryOperator<Apfloat> z;
    if (riemannSiegelReaches(a)) {
      // a sum of Sqrt(t/(2*Pi)) terms
      z = ApfloatRiemannSiegelMath::riemannSiegelZ;
    } else if (bDouble >= CRITICAL_LINE_LIMIT) {
      // the zeta values come from the summation of CriticalLineZ, which is much faster at large t
      // than the general arbitrary precision zeta
      z = new CriticalLineZ(precision, bDouble)::z;
    } else {
      z = x -> ZetaZero.zzZ(h, x, half, quarter, two, logPi);
    }
    // converged at a couple of units in the last place of t: a secant step that small cannot be
    // resolved at this precision, and a looser test leaves the last digits unfinished
    tol = ApfloatMath.pow(new Apfloat(10, precision), -precision)
        .multiply(new Apfloat(2.0 * Math.max(1.0, bDouble), precision));
    Apfloat fa;
    Apfloat fb;
    try {
      fa = z.apply(a);
      if (fa.signum() == 0) {
        return a;
      }
      fb = z.apply(b);
      if (fb.signum() == 0) {
        return b;
      }
    } catch (LossOfPrecisionException lop) {
      // an endpoint sits on the zero: zeta(1/2 + I*t) underflowed to zero at working precision
      return b;
    }
    if (fa.signum() == fb.signum()) {
      // the double-precision bracket did not straddle a sign change at full precision
      return null;
    }
    // secant memory: the two most recent iterates (x0, x1); the bracket [a, b] is the safeguard
    Apfloat x0 = a;
    Apfloat fx0 = fa;
    Apfloat x1 = b;
    Apfloat fx1 = fb;
    long maxIter = Math.min(100000L, precision * 4 + 80);
    for (long i = 0; i < maxIter; i++) {
      Apfloat denom = fx1.subtract(fx0);
      Apfloat x2;
      if (denom.signum() != 0) {
        x2 = x1.subtract(fx1.multiply(x1.subtract(x0)).divide(denom));
        if (ApfloatMath.abs(x2.subtract(x1)).compareTo(tol) < 0) {
          // converged - possibly onto the endpoint the last step became, which the bracket test
          // below would take for leaving the bracket and answer with a slow bisection
          return x2;
        }
        if (x2.compareTo(a) <= 0 || x2.compareTo(b) >= 0) {
          x2 = a.add(b).divide(two); // secant would leave the bracket: bisect instead
        }
      } else {
        x2 = a.add(b).divide(two);
      }
      Apfloat fx2;
      try {
        fx2 = z.apply(x2);
      } catch (LossOfPrecisionException lop) {
        // zeta(1/2 + I*x2) underflowed to zero at working precision: x2 is the zero
        return x2;
      }
      if (fx2.signum() == 0 || ApfloatMath.abs(x2.subtract(x1)).compareTo(tol) < 0) {
        return x2;
      }
      if (ApfloatMath.abs(fx2).compareTo(ApfloatMath.abs(fx1)) >= 0
          && ApfloatMath.abs(x2.subtract(x1)).compareTo(tol.multiply(new Apfloat(1000))) < 0) {
        // Z no longer shrinks: the iterates are at the noise floor of the working precision
        return x1;
      }
      // keep [a, b] straddling the root by replacing the like-signed endpoint
      if (fx2.signum() == fa.signum()) {
        a = x2;
        fa = fx2;
      } else {
        b = x2;
        fb = fx2;
      }
      x0 = x1;
      fx0 = fx1;
      x1 = x2;
      fx1 = fx2;
    }
    return x1;
  }

  /**
   * Whether the Riemann-Siegel formula reaches the precision of <code>t</code> at <code>t</code>:
   * the precision it can reach grows with the height.
   */
  private static boolean riemannSiegelReaches(Apfloat t) {
    if (t.doubleValue() < RIEMANN_SIEGEL_LIMIT) {
      return false;
    }
    try {
      ApfloatRiemannSiegelMath.riemannSiegelZ(t);
      return true;
    } catch (ArithmeticException ae) {
      return false;
    }
  }

  /** Below this height the Riemann-Siegel formula is not asked. */
  private static final double RIEMANN_SIEGEL_LIMIT = 5000.0;

  /**
   * Arbitrary-precision fallback: find the imaginary part of the {@code index}-th nontrivial zeta
   * zero by scanning a bracket around the asymptotic estimate for a sign change of Z(t) and
   * refining it by bisection. The bracket is widened if no sign change is found. Used only when the
   * machine-precision locate phase fails to bracket the zero.
   */
  private static Apfloat zzFindZeroApfloat(FixedPrecisionApfloatHelper h, int index, Apfloat half,
      Apfloat quarter, Apfloat two, Apfloat logPi, Apfloat tol, long precision) {
    double t0d = ZetaJS.zetaZeroEstimate(index);
    double lnArg = Math.log(t0d / (2.0 * Math.PI));
    double gapd = 2.0 * Math.PI / Math.max(lnArg, 0.3);
    Apfloat t0 = new Apfloat(t0d, precision);
    Apfloat gap = new Apfloat(gapd, precision);
    Apfloat segments = new Apfloat(ZetaJS.ZZ_SCAN_SEGMENTS, precision);

    for (int widen = 0; widen < ZetaJS.ZZ_MAX_WIDEN; widen++) {
      Apfloat lo = t0.subtract(gap);
      if (lo.signum() <= 0) {
        lo = new Apfloat("0.1", precision);
      }
      Apfloat hi = t0.add(gap);
      Apfloat step = hi.subtract(lo).divide(segments);

      Apfloat prev = lo;
      Apfloat fprev = ZetaZero.zzZ(h, prev, half, quarter, two, logPi);
      Apfloat bestA = null;
      Apfloat bestB = null;
      double bestDist = Double.MAX_VALUE;
      for (int i = 1; i <= ZetaJS.ZZ_SCAN_SEGMENTS; i++) {
        Apfloat cur = lo.add(step.multiply(new Apfloat(i, precision)));
        Apfloat fcur = ZetaZero.zzZ(h, cur, half, quarter, two, logPi);
        if (fprev.signum() != 0 && fcur.signum() != 0 && fprev.signum() != fcur.signum()) {
          double mid = prev.add(cur).divide(two).doubleValue();
          double dist = Math.abs(mid - t0d);
          if (dist < bestDist) {
            bestDist = dist;
            bestA = prev;
            bestB = cur;
          }
        }
        prev = cur;
        fprev = fcur;
      }
      if (bestA != null) {
        return ZetaZero.zzBisect(h, bestA, bestB, half, quarter, two, logPi, tol, precision);
      }
      gap = gap.multiply(two);
    }
    throw new ArgumentTypeException("ZetaZero: unable to bracket zero for index " + index);
  }

  /**
   * Compute the imaginary part of a nontrivial zeta zero on the critical line.
   *
   * @param h a fixed precision helper configured to the requested numeric precision
   * @param k the (1-based) zero index; for {@code tMin == null} this selects the k-th zero with
   *        smallest positive imaginary part
   * @param tMin if non-null, the result is the k-th zero whose imaginary part is greater than
   *        {@code tMin}
   * @return the imaginary part {@code t_k} such that {@code zeta(1/2 + I*t_k) == 0}, with three
   *         guard digits beyond the helper's precision for the caller to round
   */
  private static Apfloat zetaZeroImaginaryPart(FixedPrecisionApfloatHelper h, int k, Apfloat tMin) {
    // polished with guard digits: evaluated at exactly the requested precision the noise of Z
    // leaves the last digit or two of the zero undecided
    return zetaZeroImaginaryPartAt(new FixedPrecisionApfloatHelper(h.precision() + 3), k, tMin);
  }

  private static Apfloat zetaZeroImaginaryPartAt(FixedPrecisionApfloatHelper h, int k,
      Apfloat tMin) {
    long precision = h.precision();
    Apfloat two = new Apfloat(2, precision);
    Apfloat half = new Apfloat("0.5", precision);
    Apfloat quarter = new Apfloat("0.25", precision);
    Apfloat pi = ApfloatMath.pi(precision);
    Apfloat logPi = ApfloatMath.log(pi);
    Apfloat tol = ApfloatMath.pow(new Apfloat(10, precision), -(precision - 2));

    if (tMin == null) {
      return ZetaZero.zzFindZero(h, k, half, quarter, two, logPi, tol, precision);
    }

    // estimate the number of zeros with imaginary part <= tMin via N(t) ~ theta(t)/Pi + 1, then
    // calibrate the index entirely in machine precision (tMin is a double bound). Only the final
    // selected zero is polished to full precision, so the calibration is essentially free.
    double tMinDouble = tMin.doubleValue();
    double nApprox = ZetaJS.riemannSiegelThetaDouble(tMinDouble) / Math.PI + 1.0;
    long m = Math.max(1, Math.round(nApprox) + 1);

    // correct the index so that zero m is the first zero strictly greater than tMin
    while (m > 1 && ZetaZero.zeroImagDouble((int) (m - 1)) > tMinDouble) {
      m--;
    }
    while (ZetaZero.zeroImagDouble((int) m) <= tMinDouble) {
      m++;
    }
    return ZetaZero.zzFindZero(h, (int) (m + k - 1), half, quarter, two, logPi, tol, precision);
  }

  /** From this height on the zero is polished with {@link CriticalLineZ}. */
  private static final double CRITICAL_LINE_LIMIT = 200.0;

  /**
   * The Riemann-Siegel {@code Z(t)} at arbitrary precision for large {@code t}, from zeta(1/2 +
   * I*t) by Euler-Maclaurin summation.
   *
   * <p>
   * The direct sum runs to {@code N ~ t/2}, so that {@code t/(2 Pi N) = 1/Pi} and every Bernoulli
   * term of the tail is about {@code 1/Pi^2} of the one before. Its terms {@code k^(-1/2 - I*t)}
   * are completely multiplicative, so only the primes need a complex exponential; every other term
   * is the product of two earlier ones. The logarithms and square roots of the primes do not depend
   * on {@code t} and are kept for the few evaluations a root polish makes. The work precision has
   * guard digits for the phase {@code t*log(k)} of every term.
   */
  static final class CriticalLineZ {
    private final long precision;
    private final int n;
    private final FixedPrecisionApfloatHelper hw;
    private final int[] smallestFactor;
    private final Apfloat[] logPrime;
    private final Apfloat[] invSqrtPrime;
    private final Apfloat half;
    private final Apfloat quarter;
    private final Apfloat two;
    private final Apfloat logPi;
    private final Apfloat epsilon;
    private final List<Apfloat> bernoulliOverFactorial = new ArrayList<Apfloat>();

    CriticalLineZ(long precision, double tMax) {
      this.precision = precision;
      this.n = (int) Math.ceil(0.5 * tMax) + 30;
      long work =
          precision + (long) Math.ceil(Math.log10(tMax * Math.log(n) + 10.0)) + 6;
      this.hw = new FixedPrecisionApfloatHelper(work);
      this.half = new Apfloat("0.5", work);
      this.quarter = new Apfloat("0.25", work);
      this.two = new Apfloat(2, work);
      this.logPi = ApfloatMath.log(ApfloatMath.pi(work));
      this.epsilon = ApfloatMath.pow(new Apfloat(10, work), -work);
      smallestFactor = new int[n + 1];
      logPrime = new Apfloat[n + 1];
      invSqrtPrime = new Apfloat[n + 1];
      for (int k = 2; k <= n; k++) {
        if (smallestFactor[k] == 0) {
          for (long m = k; m <= n; m += k) {
            if (smallestFactor[(int) m] == 0) {
              smallestFactor[(int) m] = k;
            }
          }
          Apfloat pk = new Apfloat(k, work);
          logPrime[k] = hw.log(pk);
          invSqrtPrime[k] = hw.inverseRoot(pk, 2);
        }
      }
    }

    /** The coefficient B_2j / (2j)! of the Euler-Maclaurin tail. */
    private Apfloat bernoulliOverFactorial(int j) {
      while (bernoulliOverFactorial.size() < j) {
        int m = 2 * (bernoulliOverFactorial.size() + 1);
        org.matheclipse.core.interfaces.IRational b =
            org.matheclipse.core.expression.AbstractFractionSym.bernoulliNumber(m);
        java.math.BigInteger factorial = java.math.BigInteger.ONE;
        for (int i = 2; i <= m; i++) {
          factorial = factorial.multiply(java.math.BigInteger.valueOf(i));
        }
        Apfloat numerator = new Apfloat(b.toBigNumerator(), hw.precision());
        Apfloat denominator =
            new Apfloat(b.toBigDenominator().multiply(factorial), hw.precision());
        bernoulliOverFactorial.add(hw.divide(numerator, denominator));
      }
      return bernoulliOverFactorial.get(j - 1);
    }

    /** Z(t) at the precision this object was made for. */
    Apfloat z(Apfloat tIn) {
      Apfloat t = tIn.precision(hw.precision());
      Apcomplex[] power = new Apcomplex[n + 1];
      power[1] = Apcomplex.ONE;
      Apcomplex sum = Apcomplex.ONE;
      for (int k = 2; k <= n; k++) {
        int p = smallestFactor[k];
        if (p == k) {
          Apfloat phase = hw.multiply(t, logPrime[k]);
          power[k] = new Apcomplex(hw.multiply(invSqrtPrime[k], hw.cos(phase)),
              hw.multiply(invSqrtPrime[k], hw.sin(phase)).negate());
        } else {
          power[k] = hw.multiply(power[p], power[k / p]);
        }
        if (k < n) {
          sum = hw.add(sum, power[k]);
        }
      }
      Apcomplex s = new Apcomplex(half, t);
      Apfloat bigN = new Apfloat(n, hw.precision());
      Apcomplex powerN = power[n];
      // N^(1-s)/(s-1) + N^(-s)/2
      sum = hw.add(sum,
          hw.divide(hw.multiply(powerN, bigN), hw.subtract(s, Apcomplex.ONE)));
      sum = hw.add(sum, hw.divide(powerN, two));
      // Bernoulli tail: B_2j/(2j)! * s(s+1)...(s+2j-2) * N^(-s-2j+1)
      Apcomplex rising = s;
      Apcomplex tailPower = hw.divide(powerN, bigN);
      Apfloat nSquared = hw.multiply(bigN, bigN);
      Apfloat previous = null;
      for (int j = 1; j <= 400; j++) {
        Apcomplex term = hw.multiply(hw.multiply(rising, tailPower), bernoulliOverFactorial(j));
        Apfloat size = hw.abs(term);
        sum = hw.add(sum, term);
        if (size.compareTo(epsilon) < 0 || (previous != null && size.compareTo(previous) > 0)) {
          break;
        }
        previous = size;
        rising = hw.multiply(hw.multiply(rising, hw.add(s, new Apfloat(2 * j - 1))),
            hw.add(s, new Apfloat(2 * j)));
        tailPower = hw.divide(tailPower, nSquared);
      }
      Apfloat theta = RiemannSiegelTheta.zzTheta(hw, t, quarter, two, logPi);
      Apfloat value = hw.subtract(hw.multiply(hw.cos(theta), sum.real()),
          hw.multiply(hw.sin(theta), sum.imag()));
      return value.precision(precision);
    }
  }

}
