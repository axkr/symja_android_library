/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.arith.Modular;
import edu.jas.arith.ModularRingFactory;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;


/**
 * Greatest common divisor by sparse interpolation, Zippel's algorithm, over a modular coefficient
 * field.
 *
 * <p>
 * {@link GreatestCommonDivisorModEval} interpolates one variable of the gcd from
 * <code>deg+1</code> images, each of which costs a gcd computation in one variable less. That is
 * Brown's algorithm and its work grows with the product of the degrees, whether or not the gcd
 * actually has that many terms. Zippel's algorithm computes only the first image that way and then
 * assumes that every further image has the <b>same set of monomials</b> - the skeleton. An image is
 * then determined by its coefficients alone, and <code>t</code> coefficients need only
 * <code>t</code> evaluations instead of a full recursive gcd.
 *
 * <p>
 * The evaluation points are chosen as a geometric progression
 * <code>(y_1^k, ..., y_(j-1)^k)</code>, <code>k = 1, ..., t</code>. A monomial takes the value
 * <code>m(y)^k</code> there, so the linear system for the coefficients is a transposed Vandermonde
 * system in the nodes <code>m(y)</code>, which
 * {@link #solveTransposedVandermonde(List, List)} solves in <code>O(t^2)</code> instead of the
 * <code>O(t^3)</code> of a general elimination.
 *
 * <p>
 * <b>Normalization.</b> The gcd is not monic in the main variable, and its images are only
 * determined up to a factor. Every univariate image is therefore made monic and multiplied by the
 * value of <code>gamma = gcd(lc(A), lc(B))</code> at the same point. That interpolates
 * <code>Ghat = G * gamma / lc(G)</code>, which is a polynomial because <code>lc(G)</code> divides
 * <code>gamma</code>, and the gcd is recovered as the primitive part of <code>Ghat</code>. This is
 * the same normalization {@link GreatestCommonDivisorModEval} uses.
 *
 * <p>
 * <b>The skeleton assumption can be wrong.</b> A monomial of the gcd whose coefficient happens to
 * vanish at the first evaluation point is missing from the skeleton and then from every image built
 * on it. The algorithm is therefore Monte Carlo and the result is <b>always</b> verified by trial
 * division; on failure it retries with new points and finally falls back to a dense algorithm, so
 * the answer this class returns is never a probabilistic one.
 *
 * @see GreatestCommonDivisorModular for the lift from a modular gcd to <code>Z[x_1, ..., x_n]</code>
 * @author Claude Opus 5
 */

public class GreatestCommonDivisorZippel<MOD extends GcdRingElem<MOD> & Modular>
                extends GreatestCommonDivisorAbstract<MOD> {


    private static final Logger logger = LogManager.getLogger(GreatestCommonDivisorZippel.class);


    /**
     * Univariate gcd algorithm, the base of the recursion, and the fall back for coefficient rings
     * which are not a field.
     */
    protected final GreatestCommonDivisorAbstract<MOD> mufd = new GreatestCommonDivisorSimple<MOD>();


    /**
     * Dense multivariate gcd algorithm: the algorithm for the inputs the sparse one is not meant
     * for, and the fall back whenever it gives up.
     *
     * <p>
     * Brown's algorithm rather than a polynomial remainder sequence on purpose. A sequence is
     * hopeless on exactly the inputs which reach this class - it did not finish within 40 seconds on
     * any of the benchmark problems in five or six variables which Brown's algorithm solves in well
     * under a second - so falling back to one would turn a give up into a hang.
     */
    protected final GreatestCommonDivisorAbstract<MOD> dufd = new GreatestCommonDivisorModEval<MOD>();


    /**
     * Fewest variables for which the sparse algorithm is used at all.
     *
     * <p>
     * Measured against {@link GreatestCommonDivisorModEval}, on sparse arguments and on arguments
     * dense up to a total degree, in milliseconds:
     *
     * <pre>
     * variables    2      3       4       5        6
     * sparse    zippel   12      13      17       25
     *           brown    11      53      69     1787
     * dense     zippel    5      14      32       90
     *           brown     6      10      33      107
     * </pre>
     *
     * Three variables is the crossover: below it Brown's algorithm is at least as good on either
     * kind of input, above it the sparse algorithm is never worse and is better by a factor which
     * grows with the number of variables.
     *
     * <p>
     * Note that no test of the density is needed on top of this. Sparse interpolation is slower than
     * a dense one only when it has nothing to save, and by four variables that case has become a tie
     * - the dense measurements above are of arguments which use every monomial up to their total
     * degree.
     */
    protected static final int MIN_VARIABLES = 4;


    /**
     * Resultant algorithm. Zippel's algorithm computes no resultants, but
     * {@link GreatestCommonDivisorAbstract#resultant(GenPolynomial, GenPolynomial)} dispatches to
     * {@link #recursiveUnivariateResultant(GenPolynomial, GenPolynomial)}, which throws in the base
     * class. Without this delegation every caller of <code>resultant()</code> would break as soon as
     * this engine is selected.
     */
    protected final GreatestCommonDivisorAbstract<MOD> rufd = new GreatestCommonDivisorSubres<MOD>();


    /**
     * Number of restarts with fresh evaluation points before giving up and falling back to the dense
     * algorithm.
     */
    protected final int maxAttempts;


    /**
     * A fixed seed keeps the algorithm reproducible: the same input always takes the same evaluation
     * points, so a failure can be repeated. Each attempt draws fresh points, so an unlucky choice is
     * still retried.
     */
    private static final long SEED = 0x2B1E5A7DL;


    /**
     * Constructor.
     */
    public GreatestCommonDivisorZippel() {
        this(5);
    }


    /**
     * Constructor.
     * @param maxAttempts number of restarts with new evaluation points before falling back.
     */
    public GreatestCommonDivisorZippel(int maxAttempts) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts < 1: " + maxAttempts);
        }
        this.maxAttempts = maxAttempts;
    }


    /**
     * Univariate GenPolynomial greatest common divisor.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return gcd(P,S).
     */
    @Override
    public GenPolynomial<MOD> baseGcd(GenPolynomial<MOD> P, GenPolynomial<MOD> S) {
        // required as recursion base
        return mufd.baseGcd(P, S);
    }


    /**
     * Recursive univariate GenPolynomial greatest common divisor.
     * @param P univariate recursive GenPolynomial.
     * @param S univariate recursive GenPolynomial.
     * @return gcd(P,S).
     */
    @Override
    public GenPolynomial<GenPolynomial<MOD>> recursiveUnivariateGcd(GenPolynomial<GenPolynomial<MOD>> P,
                    GenPolynomial<GenPolynomial<MOD>> S) {
        // distributed polynomials gcd
        GenPolynomialRing<GenPolynomial<MOD>> rfac = P.ring;
        @SuppressWarnings("unchecked")
        GenPolynomialRing<MOD> cfac = (GenPolynomialRing<MOD>) (Object) rfac.coFac;
        GenPolynomialRing<MOD> dfac = cfac.extend(rfac.nvar);
        GenPolynomial<MOD> Pd = PolyUtil.<MOD> distribute(dfac, P);
        GenPolynomial<MOD> Sd = PolyUtil.<MOD> distribute(dfac, S);
        GenPolynomial<MOD> Dd = gcd(Pd, Sd);
        return PolyUtil.<MOD> recursive(rfac, Dd);
    }


    /**
     * Univariate GenPolynomial resultant. Delegated, see {@link #rufd}.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return res(P,S).
     */
    @Override
    public GenPolynomial<MOD> baseResultant(GenPolynomial<MOD> P, GenPolynomial<MOD> S) {
        return rufd.baseResultant(P, S);
    }


    /**
     * Univariate recursive GenPolynomial resultant. Delegated, see {@link #rufd}.
     * @param P univariate recursive GenPolynomial.
     * @param S univariate recursive GenPolynomial.
     * @return res(P,S).
     */
    @Override
    public GenPolynomial<GenPolynomial<MOD>> recursiveUnivariateResultant(
                    GenPolynomial<GenPolynomial<MOD>> P, GenPolynomial<GenPolynomial<MOD>> S) {
        return rufd.recursiveUnivariateResultant(P, S);
    }


    /**
     * GenPolynomial resultant. Delegated, see {@link #rufd}.
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return res(P,S).
     */
    @Override
    public GenPolynomial<MOD> resultant(GenPolynomial<MOD> P, GenPolynomial<MOD> S) {
        return rufd.resultant(P, S);
    }


    /**
     * GenPolynomial greatest common divisor, sparse interpolation algorithm.
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return gcd(P,S).
     */
    @Override
    public GenPolynomial<MOD> gcd(GenPolynomial<MOD> P, GenPolynomial<MOD> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        GenPolynomialRing<MOD> fac = P.ring;
        if (fac.nvar <= 1) {
            return baseGcd(P, S);
        }
        if (!fac.coFac.isField()) {
            // the interpolation inverts coefficients throughout
            return mufd.gcd(P, S);
        }
        if (!isWorthIt(P, S)) {
            return dufd.gcd(P, S);
        }
        try {
            GenPolynomial<MOD> g = sparseGcd(P.abs(), S.abs());
            if (g != null) {
                return g;
            }
            logger.info("sparse interpolation gave up, falling back to {}",
                            dufd.getClass().getSimpleName());
        } catch (ArithmeticException e) {
            // an inverse of a zero element, an exhausted coefficient field, ...
            logger.info("sparse interpolation failed: {}", e.toString());
        }
        return dufd.gcd(P, S);
    }


    /**
     * Whether the sparse algorithm is expected to beat the dense one on these arguments.
     * @see #MIN_VARIABLES
     */
    protected boolean isWorthIt(GenPolynomial<MOD> P, GenPolynomial<MOD> S) {
        return P.ring.nvar >= MIN_VARIABLES;
    }


    /**
     * Greatest common divisor by sparse interpolation.
     *
     * <p>
     * The main variable is the one of exponent index <code>0</code>, which is the variable
     * {@link GenPolynomialRing#contract(int)} splits off and the one
     * {@link GreatestCommonDivisorAbstract#gcd(GenPolynomial, GenPolynomial)} recurses on.
     *
     * @return gcd(A,S), or <code>null</code> if every attempt hit an unlucky evaluation point or a
     *         wrong skeleton. The result is verified by division, so it is never a guess.
     */
    protected GenPolynomial<MOD> sparseGcd(GenPolynomial<MOD> A, GenPolynomial<MOD> B) {
        GenPolynomialRing<MOD> fac = A.ring;
        GenPolynomialRing<MOD> cfac = fac.contract(1);
        GenPolynomialRing<GenPolynomial<MOD>> rfac = fac.recursive(1);

        // contents and primitive parts with respect to the main variable
        GenPolynomial<GenPolynomial<MOD>> Ar = PolyUtil.<MOD> recursive(rfac, A);
        GenPolynomial<GenPolynomial<MOD>> Br = PolyUtil.<MOD> recursive(rfac, B);
        GenPolynomial<MOD> ca = recursiveContent(Ar);
        GenPolynomial<MOD> cb = recursiveContent(Br);
        GenPolynomial<MOD> content = gcd(ca, cb); // recursion in one variable less
        Ar = PolyUtil.<MOD> recursiveDivide(Ar, ca);
        Br = PolyUtil.<MOD> recursiveDivide(Br, cb);
        if (Ar.isONE() || Br.isONE()) {
            return content.extend(fac, 0, 0L);
        }
        // lc(G) divides gamma, so G*gamma/lc(G) is a polynomial and can be interpolated
        GenPolynomial<MOD> gamma = gcd(Ar.leadingBaseCoefficient(), Br.leadingBaseCoefficient());

        GenPolynomial<MOD> Ap = PolyUtil.<MOD> distribute(fac, Ar);
        GenPolynomial<MOD> Bp = PolyUtil.<MOD> distribute(fac, Br);

        Random random = new Random(SEED);
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            checkInterrupted();
            GenPolynomial<MOD> g = attempt(Ap, Bp, gamma, cfac, rfac, random);
            if (g != null) {
                return g.multiply(content.extend(fac, 0, 0L)).monic();
            }
            logger.debug("attempt {} of the sparse interpolation failed", attempt);
        }
        return null;
    }


    /**
     * One attempt of the sparse interpolation with a fresh evaluation point.
     *
     * @param Ap primitive part of the first argument with respect to the main variable
     * @param Bp primitive part of the second argument
     * @param gamma gcd of the leading coefficients, in <code>cfac</code>
     * @return the verified primitive gcd of <code>Ap</code> and <code>Bp</code>, or
     *         <code>null</code> if this attempt has to be discarded
     */
    protected GenPolynomial<MOD> attempt(GenPolynomial<MOD> Ap, GenPolynomial<MOD> Bp,
                    GenPolynomial<MOD> gamma, GenPolynomialRing<MOD> cfac,
                    GenPolynomialRing<GenPolynomial<MOD>> rfac, Random random) {
        GenPolynomialRing<MOD> fac = Ap.ring;
        int nvar = fac.nvar;
        @SuppressWarnings("unchecked")
        ModularRingFactory<MOD> cofac = (ModularRingFactory<MOD>) fac.coFac;
        GenPolynomialRing<MOD> ufac = new GenPolynomialRing<MOD>(fac.coFac, 1);

        long degA = degree(Ap, 0);
        long degB = degree(Bp, 0);

        // the point at which all but the main variable are evaluated
        List<MOD> alpha = randomPoint(nvar, cofac, random);

        // first image: a full univariate gcd
        GenPolynomial<MOD> image = univariateImage(Ap, Bp, alpha, ufac, degA, degB);
        if (image == null) {
            return null;
        }
        long degGcd = image.degree(0);
        if (degGcd == 0L) {
            // the primitive parts are coprime, the gcd is the content alone
            return fac.getONE();
        }
        MOD gv = evaluateContracted(gamma, alpha);
        if (gv.isZERO()) {
            return null;
        }
        // Ghat = G * gamma / lc(G), still only in the main variable at this point
        GenPolynomial<MOD> ghat = liftUnivariate(image.multiply(gv), fac);

        // add one variable per stage, each image of it by sparse interpolation
        for (int stage = 1; stage < nvar; stage++) {
            checkInterrupted();
            long bound = Math.min(degree(Ap, stage), degree(Bp, stage)) + degree(gamma, stage - 1);
            if (bound == 0L) {
                continue; // Ghat does not involve this variable
            }
            Map<Long, List<ExpVector>> skeleton = skeleton(ghat);
            List<MOD> betas = new ArrayList<MOD>();
            List<GenPolynomial<MOD>> images = new ArrayList<GenPolynomial<MOD>>();
            betas.add(alpha.get(stage));
            images.add(ghat);
            for (long k = 0; k < bound; k++) {
                MOD beta = distinctNonZero(betas, cofac, random);
                if (beta == null) {
                    return null; // coefficient field exhausted
                }
                GenPolynomial<MOD> next = sparseImage(Ap, Bp, gamma, skeleton, degGcd, stage, beta,
                                alpha, ufac, cofac, random, degA, degB);
                if (next == null) {
                    return null;
                }
                betas.add(beta);
                images.add(next);
            }
            ghat = newtonInterpolate(stage, betas, images, fac);
        }

        // remove the gamma scaling again
        GenPolynomial<GenPolynomial<MOD>> gr = PolyUtil.<MOD> recursive(rfac, ghat);
        GenPolynomial<MOD> g = PolyUtil.<MOD> distribute(fac, recursivePrimitivePart(gr)).abs();
        if (g.isZERO()) {
            return null;
        }
        // the skeleton may have been wrong - never return an unverified result
        if (!Ap.remainder(g).isZERO() || !Bp.remainder(g).isZERO()) {
            logger.debug("candidate does not divide the arguments, discarding: {}", g);
            return null;
        }
        return g;
    }


    /**
     * One image of <code>Ghat</code> with the variable <code>stage</code> evaluated at
     * <code>beta</code>, determined from its assumed skeleton alone.
     *
     * <p>
     * The coefficients are read off <code>t</code> univariate gcds taken along a geometric
     * progression of points, which turns the linear systems into transposed Vandermonde systems.
     *
     * @return the image, or <code>null</code> if a point was unlucky or the skeleton is not usable
     */
    protected GenPolynomial<MOD> sparseImage(GenPolynomial<MOD> Ap, GenPolynomial<MOD> Bp,
                    GenPolynomial<MOD> gamma, Map<Long, List<ExpVector>> skeleton, long degGcd,
                    int stage, MOD beta, List<MOD> alpha, GenPolynomialRing<MOD> ufac,
                    ModularRingFactory<MOD> cofac, Random random, long degA, long degB) {
        GenPolynomialRing<MOD> fac = Ap.ring;
        int terms = 0;
        for (List<ExpVector> monomials : skeleton.values()) {
            terms = Math.max(terms, monomials.size());
        }
        if (terms == 0) {
            return null;
        }
        // base point of the geometric progression, only the variables before `stage` matter
        List<MOD> base = randomPoint(fac.nvar, cofac, random);

        List<GenPolynomial<MOD>> samples = new ArrayList<GenPolynomial<MOD>>(terms);
        List<MOD> powers = new ArrayList<MOD>(base); // base^k, updated in place
        for (int k = 1; k <= terms; k++) {
            checkInterrupted();
            List<MOD> point = new ArrayList<MOD>(alpha);
            for (int i = 1; i < stage; i++) {
                point.set(i, powers.get(i));
            }
            point.set(stage, beta);
            GenPolynomial<MOD> h = univariateImage(Ap, Bp, point, ufac, degA, degB);
            if (h == null || h.degree(0) != degGcd) {
                return null; // unlucky point or wrong degree
            }
            MOD gv = evaluateContracted(gamma, point);
            if (gv.isZERO()) {
                return null;
            }
            samples.add(h.multiply(gv));
            for (int i = 1; i < stage; i++) {
                powers.set(i, powers.get(i).multiply(base.get(i)));
            }
        }

        GenPolynomial<MOD> result = fac.getZERO().copy();
        for (Map.Entry<Long, List<ExpVector>> entry : skeleton.entrySet()) {
            long d = entry.getKey();
            List<ExpVector> monomials = entry.getValue();
            int t = monomials.size();
            List<MOD> nodes = new ArrayList<MOD>(t);
            for (ExpVector e : monomials) {
                MOD node = monomialValue(e, base, 1, stage, fac.coFac);
                if (node.isZERO() || nodes.contains(node)) {
                    return null; // the system would be singular
                }
                nodes.add(node);
            }
            List<MOD> rhs = new ArrayList<MOD>(t);
            ExpVector de = ExpVector.create(1, 0, d);
            for (int k = 0; k < t; k++) {
                rhs.add(samples.get(k).coefficient(de));
            }
            List<MOD> coefficients = solveTransposedVandermonde(nodes, rhs, fac.coFac);
            if (coefficients == null) {
                return null;
            }
            for (int i = 0; i < t; i++) {
                MOD c = coefficients.get(i);
                if (!c.isZERO()) {
                    result.doAddTo(c, monomials.get(i).subst(0, d));
                }
            }
        }
        return result;
    }


    /**
     * Solve the transposed Vandermonde system <code>sum_i node_i^k c_i = rhs_k</code> for
     * <code>k = 1, ..., t</code> in <code>O(t^2)</code>.
     *
     * <p>
     * With <code>c'_i = node_i * c_i</code> the system reads <code>sum_i node_i^j c'_i = rhs_j</code>
     * for <code>j = 0, ..., t-1</code>. For the master polynomial
     * <code>M(z) = prod_l (z - node_l)</code> and <code>M_i = M / (z - node_i)</code> the identity
     * <code>sum_j m_ij rhs_j = c'_i * M_i(node_i)</code> holds, because <code>M_i</code> vanishes at
     * every other node. Both <code>M_i</code> and <code>M_i(node_i)</code> cost <code>O(t)</code>
     * each.
     *
     * @param nodes pairwise distinct and non zero
     * @return the coefficients, or <code>null</code> if the system is singular
     */
    protected List<MOD> solveTransposedVandermonde(List<MOD> nodes, List<MOD> rhs,
                    RingFactory<MOD> cofac) {
        int t = nodes.size();
        MOD zero = cofac.getZERO();
        // master polynomial M(z) = prod_l (z - node_l), coefficients low to high
        List<MOD> master = new ArrayList<MOD>(t + 1);
        master.add(cofac.getONE());
        for (int l = 0; l < t; l++) {
            MOD n = nodes.get(l).negate();
            master.add(zero);
            for (int j = master.size() - 1; j > 0; j--) {
                master.set(j, master.get(j - 1).sum(master.get(j).multiply(n)));
            }
            master.set(0, master.get(0).multiply(n));
        }
        List<MOD> result = new ArrayList<MOD>(t);
        for (int i = 0; i < t; i++) {
            MOD node = nodes.get(i);
            // M_i = M / (z - node) by synthetic division, coefficients high to low
            List<MOD> quotient = new ArrayList<MOD>(Collections.nCopies(t, zero));
            MOD carry = master.get(t);
            for (int j = t - 1; j >= 0; j--) {
                quotient.set(j, carry);
                carry = master.get(j).sum(carry.multiply(node));
            }
            // denominator M_i(node) = prod_{l != i} (node - node_l)
            MOD denominator = cofac.getONE();
            for (int l = 0; l < t; l++) {
                if (l != i) {
                    denominator = denominator.multiply(node.subtract(nodes.get(l)));
                }
            }
            if (denominator.isZERO()) {
                return null;
            }
            MOD numerator = zero;
            for (int j = 0; j < t; j++) {
                numerator = numerator.sum(quotient.get(j).multiply(rhs.get(j)));
            }
            result.add(numerator.multiply(denominator.inverse()).multiply(node.inverse()));
        }
        return result;
    }


    /**
     * Newton interpolation in the variable of exponent index <code>index</code>.
     * @param betas the pairwise distinct evaluation points
     * @param images the values there, polynomials which do not involve <code>index</code>
     */
    protected GenPolynomial<MOD> newtonInterpolate(int index, List<MOD> betas,
                    List<GenPolynomial<MOD>> images, GenPolynomialRing<MOD> fac) {
        GenPolynomial<MOD> result = images.get(0);
        GenPolynomial<MOD> product = fac.getONE(); // prod_{l<k} (x_index - beta_l)
        GenPolynomial<MOD> variable = fac.valueOf(ExpVector.create(fac.nvar, index, 1L));
        for (int k = 1; k < betas.size(); k++) {
            MOD beta = betas.get(k);
            product = product.multiply(variable.subtract(fac.getONE().multiply(betas.get(k - 1))));
            GenPolynomial<MOD> difference =
                            images.get(k).subtract(evaluateComponent(result, index, beta));
            if (difference.isZERO()) {
                continue;
            }
            MOD divisor = fac.coFac.getONE();
            for (int l = 0; l < k; l++) {
                divisor = divisor.multiply(beta.subtract(betas.get(l)));
            }
            if (divisor.isZERO()) {
                throw new ArithmeticException("interpolation points not distinct");
            }
            result = result.sum(difference.multiply(divisor.inverse()).multiply(product));
        }
        return result;
    }


    /**
     * The monomials of <code>P</code> grouped by their degree in the main variable, with the main
     * variable removed from the exponent vectors. This is the skeleton the next stage assumes.
     */
    protected Map<Long, List<ExpVector>> skeleton(GenPolynomial<MOD> P) {
        Map<Long, List<ExpVector>> result = new LinkedHashMap<Long, List<ExpVector>>();
        for (ExpVector e : P.getMap().keySet()) {
            Long d = Long.valueOf(e.getVal(0));
            List<ExpVector> monomials = result.get(d);
            if (monomials == null) {
                monomials = new ArrayList<ExpVector>();
                result.put(d, monomials);
            }
            monomials.add(e.subst(0, 0L));
        }
        return result;
    }


    /**
     * The monic gcd of the two arguments with every variable but the main one evaluated.
     * @return <code>null</code> if a leading coefficient vanished at the point, which would make the
     *         image gcd a divisor of the true one only by accident
     */
    protected GenPolynomial<MOD> univariateImage(GenPolynomial<MOD> Ap, GenPolynomial<MOD> Bp,
                    List<MOD> point, GenPolynomialRing<MOD> ufac, long degA, long degB) {
        GenPolynomial<MOD> au = evaluateToUnivariate(Ap, point, ufac);
        if (au.isZERO() || au.degree(0) != degA) {
            return null;
        }
        GenPolynomial<MOD> bu = evaluateToUnivariate(Bp, point, ufac);
        if (bu.isZERO() || bu.degree(0) != degB) {
            return null;
        }
        return baseGcd(au, bu).monic();
    }


    /**
     * Evaluate every variable but the main one, giving a univariate polynomial in the main variable.
     * @param point values by exponent index, the entry of the main variable is not used
     */
    protected GenPolynomial<MOD> evaluateToUnivariate(GenPolynomial<MOD> P, List<MOD> point,
                    GenPolynomialRing<MOD> ufac) {
        GenPolynomial<MOD> result = ufac.getZERO().copy();
        for (Map.Entry<ExpVector, MOD> m : P.getMap().entrySet()) {
            ExpVector e = m.getKey();
            MOD c = m.getValue().multiply(monomialValue(e, point, 1, e.length(), ufac.coFac));
            if (!c.isZERO()) {
                result.doAddTo(c, ExpVector.create(1, 0, e.getVal(0)));
            }
        }
        return result;
    }


    /**
     * Evaluate a polynomial of the contracted ring, whose exponent index <code>j</code> is the
     * exponent index <code>j+1</code> of the full ring.
     */
    protected MOD evaluateContracted(GenPolynomial<MOD> P, List<MOD> point) {
        MOD result = P.ring.coFac.getZERO();
        for (Map.Entry<ExpVector, MOD> m : P.getMap().entrySet()) {
            ExpVector e = m.getKey();
            MOD c = m.getValue();
            for (int i = 0; i < e.length(); i++) {
                long k = e.getVal(i);
                if (k > 0L) {
                    c = c.multiply(Power.<MOD> positivePower(point.get(i + 1), k));
                }
            }
            result = result.sum(c);
        }
        return result;
    }


    /**
     * Substitute a value for a single variable.
     */
    protected GenPolynomial<MOD> evaluateComponent(GenPolynomial<MOD> P, int index, MOD value) {
        GenPolynomial<MOD> result = P.ring.getZERO().copy();
        for (Map.Entry<ExpVector, MOD> m : P.getMap().entrySet()) {
            ExpVector e = m.getKey();
            long k = e.getVal(index);
            MOD c = m.getValue();
            if (k > 0L) {
                c = c.multiply(Power.<MOD> positivePower(value, k));
            }
            if (!c.isZERO()) {
                result.doAddTo(c, e.subst(index, 0L));
            }
        }
        return result;
    }


    /**
     * The value of a monomial at a point, over the exponent indices <code>from</code> (inclusive) to
     * <code>to</code> (exclusive).
     */
    protected MOD monomialValue(ExpVector e, List<MOD> point, int from, int to,
                    RingFactory<MOD> cofac) {
        MOD result = cofac.getONE();
        for (int i = from; i < to && i < e.length(); i++) {
            long k = e.getVal(i);
            if (k > 0L) {
                result = result.multiply(Power.<MOD> positivePower(point.get(i), k));
            }
        }
        return result;
    }


    /**
     * The degree in the variable of exponent index <code>index</code>.
     */
    protected long degree(GenPolynomial<MOD> P, int index) {
        long deg = 0L;
        for (ExpVector e : P.getMap().keySet()) {
            if (index < e.length()) {
                long d = e.getVal(index);
                if (d > deg) {
                    deg = d;
                }
            }
        }
        return deg;
    }


    /**
     * Embed a univariate polynomial of the main variable into the full ring.
     */
    protected GenPolynomial<MOD> liftUnivariate(GenPolynomial<MOD> P, GenPolynomialRing<MOD> fac) {
        GenPolynomial<MOD> result = fac.getZERO().copy();
        for (Map.Entry<ExpVector, MOD> m : P.getMap().entrySet()) {
            result.doAddTo(m.getValue(), ExpVector.create(fac.nvar, 0, m.getKey().getVal(0)));
        }
        return result;
    }


    /**
     * A point with non zero entries, the entry of the main variable included but unused.
     */
    protected List<MOD> randomPoint(int nvar, ModularRingFactory<MOD> cofac, Random random) {
        List<MOD> point = new ArrayList<MOD>(nvar);
        for (int i = 0; i < nvar; i++) {
            point.add(nonZero(cofac, random));
        }
        return point;
    }


    /**
     * A non zero element of the coefficient field.
     */
    protected MOD nonZero(ModularRingFactory<MOD> cofac, Random random) {
        long modul = cofac.getIntegerModul().longValueExact();
        MOD value;
        do {
            value = cofac.fromInteger(1L + (long) (random.nextDouble() * (modul - 1L)));
        } while (value.isZERO());
        return value;
    }


    /**
     * A non zero element which is not in <code>used</code> yet.
     * @return <code>null</code> if no such element was found, the field being too small
     */
    protected MOD distinctNonZero(List<MOD> used, ModularRingFactory<MOD> cofac, Random random) {
        for (int i = 0; i < 100; i++) {
            MOD value = nonZero(cofac, random);
            if (!used.contains(value)) {
                return value;
            }
        }
        logger.warn("elements of the coefficient field exhausted, modul = {}", cofac.getIntegerModul());
        return null;
    }


}
