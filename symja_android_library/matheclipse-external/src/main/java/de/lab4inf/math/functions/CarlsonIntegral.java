/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2015,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer Sciences (Lab4Inf).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package de.lab4inf.math.functions;

import de.lab4inf.math.L4MObject;
import de.lab4inf.math.util.Accuracy;

import static de.lab4inf.math.util.MinMax.absmax;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * Calculation of Carlson's elliptic integrals, which are related to Legendre's
 * first, second and third complete or incomplete elliptic integrals.
 * The algorithms are due to
 * <pre>
 * B.C. Carlson: Numerical Computation of real or complex elliptic integrals.
 * Numerical Algorithms 10(1995), pp 13-26.
 * </pre>
 * <hr/>
 * <pre>
 *                 &infin;
 *                 &#8992;
 * R<sub>F</sub>(x,y,z) = 1/2 &#9134; dt [(t+x)(t+y)(t+z)]<sup>-1/2</sup>
 *                 &#8993;
 *                 0
 *
 *                   &infin;
 *                   &#8992;
 * R<sub>J</sub>(x,y,z,p) = 3/2 &#9134; dt [(t+x)(t+y)(t+z)]<sup>-1/2</sup> (t+p)<sup>-1</sup>
 *                   &#8993;
 *                   0
 * </pre>
 * <p>
 * with some further special degenerate integrals of the former:
 * <p>
 * <pre>
 *
 * R<sub>C</sub>(x,y)   = R<sub>F</sub>(x,y,y)
 * R<sub>D</sub>(x,y,z) = R<sub>J</sub>(x,y,z,z)
 * 2 R<sub>G</sub>(x,y,z) = zR<sub>F</sub>(x,y,z)-1/3(x-z)(y-z)R<sub>D</sub>(x,y,z)+sqrt(xy/z)
 *
 * </pre>
 * <p>
 * which shorten to:
 * <p>
 * <pre>
 *               &infin;
 *               &#8992;
 * R<sub>C</sub>(x,y) = 1/2 &#9134; dt (t+x)<sup>-1/2</sup>(t+y)<sup>-1</sup>
 *               &#8993;
 *               0
 *
 *                &infin;
 *                &#8992;
 * R<sub>D</sub>(x,y,z) = 3/2&#9134; dt [(t+x)(t+y)]<sup>-1/2</sup>(t+z)<sup>-3/2</sup>
 *                &#8993;
 *                0
 *
 * </pre>
 * <hr/>
 * These integrals can express complete Legendre's elliptic integrals in a
 * systematic common fashion:
 * <pre>
 *
 *    K(k) = R<sub>F</sub>(0,1-k<sup>2</sup>,1)
 *    E(k) = 2 R<sub>G</sub>(0,1-k<sup>2</sup>,1)
 *
 *    K(k)-E(k) = k<sup>2</sup>/3 R<sub>D</sub>(0,1-k<sup>2</sup>,1)
 *
 * </pre>
 * and also the three incomplete ones, with the abbreviation
 * c = csc<sup>2</sup>&phi; = 1/sin<sup>2</sup>&phi; , k = sin<sup>2</sup>&alpha;
 * <pre>
 *
 *    F(&phi;,k) = R<sub>F</sub>(c-1,c-k<sup>2</sup>,c)
 *    E(&phi;,k) = R<sub>F</sub>(c-1,c-k<sup>2</sup>,c) - k<sup>2</sup>/3 R<sub>D</sub>(c-1,c-k<sup>2</sup>,c)
 *    &Pi;(n;&phi;,k) = R<sub>F</sub>(c-1,c-k<sup>2</sup>,c) - n/3 R<sub>J</sub>(c-1,c-k<sup>2</sup>,c,c+n)
 * </pre>
 *
 * @author nwulff
 * @version $Id: CarlsonIntegral.java,v 1.23 2015/01/29 14:51:48 nwulff Exp $
 * @see <a href="CompleteFirstEllipticIntegral.html">CompleteFirstEllipticIntegral</a>
 * @see <a href="CompleteSecondEllipticIntegral.html">CompleteSecondEllipticIntegral</a>
 * @see <a href="IncompleteFirstEllipticIntegral.html">IncompleteFirstEllipticIntegral</a>
 * @see <a href="IncompleteSecondEllipticIntegral.html">IncompleteSecondEllipticIntegral</a>
 * <p>
 * <hr/>
 * @since 27.01.2015
 */
public final class CarlsonIntegral extends L4MObject {
    private static final int NMAX = 16;
    private static final double DEPS = 3 * Accuracy.DEPS;
    private static final double RF = pow(DEPS, -1. / 6.);
    private static final double RC = pow(DEPS, -1. / 8.);
    private static final double RJ = pow(DEPS / 2, -1. / 6.);
    private static final double RD = RJ;

    /**
     * No public constructor for an utility class.
     */
    private CarlsonIntegral() {
    }

    /**
     * Calculate  R<sub>D</sub>(x,y,z).
     *
     * @param x 1.st argument
     * @param y 2.nd argument
     * @param z 3.rd argument
     * @return R<sub>D</sub>(x,y,z)
     */
    public static double rd(final double x, final double y, final double z) {
        if (z == 0)
            throw new IllegalArgumentException(String.format("rd(%f,%f,%f) zero argument", x, y, z));

        int n = 0;
        double a0 = (x + y + 3 * z) / 5;
        double s = 0;
        double q = RD * absmax(x - a0, y - a0, z - a0);
        double fn = 1, am = a0, lm, xm = x, ym = y, zm = z;
        double e2, e3, e4, e5, rd;
        double sqx, sqy, sqz;
        do {
            n++;
            sqx = sqrt(xm);
            sqy = sqrt(ym);
            sqz = sqrt(zm);
            lm = sqx * sqy + sqx * sqz + sqy * sqz;
            s += 1 / (fn * sqz * (zm + lm));

            fn *= 4;
            am = (am + lm) / 4;
            xm = (xm + lm) / 4;
            ym = (ym + lm) / 4;
            zm = (zm + lm) / 4;

        } while (q > fn * abs(am) && n < NMAX);
        if (n >= NMAX) {
            throw new IllegalArgumentException(String.format("rd(%f,%f,%f) no convergence", x, y, z));
        }
        rd = 1 / (fn * am * sqrt(am));
        am *= fn;
        xm = (a0 - x) / am;
        ym = (a0 - y) / am;
        zm = -(xm + ym) / 3;

        e2 = xm * ym - 6 * zm * zm;
        e3 = (3 * xm * ym - 8 * zm * zm) * zm;
        e4 = 3 * (xm * ym - zm * zm) * zm * zm;
        e5 = xm * ym * zm * zm * zm;

        rd *= (1 - 3. / 14. * e2 + e3 / 6 + 9 * e2 * e2 / 88 - 3 * e4 / 22 - 9 * e2 * e3 / 52 + 3 * e5 / 26);
        rd += 3 * s;
        return rd;

    }

    /**
     * Calculate  R<sub>C</sub>(x,y)=R<sub>F</sub>(x,y,y).
     *
     * @param x 1.st argument
     * @param y 2.nd argument
     * @return R<sub>C</sub>(x,y)
     */
    public static double rc(final double x, final double y) {
        if (y < 0) {
            return sqrt(x / (x - y)) * rc(x - y, -y);
        }

        int n = 0;
        double a0 = (x + 2 * y) / 3;
        double q = RC * abs(x - a0);
        double fn = 1, am = a0, lm, xm = x, ym = y;
        double s, rc;
        do {
            n++;
            fn *= 4;
            lm = 2 * sqrt(xm * ym) + ym;

            am = (am + lm) / 4;
            xm = (xm + lm) / 4;
            ym = (ym + lm) / 4;

        } while (q > fn * abs(am) && n < NMAX);
        if (n >= NMAX) {
            throw new IllegalArgumentException(String.format("rc(%f,%f) no convergence", x, y));
        }
        rc = 1 / sqrt(am);
        am *= fn;
        s = (y - a0) / am;

        rc *= (1 + s * (s * (3. / 10. + s * (1. / 7. + s * (3. / 8. + s * (9. / 22. + s * (159. / 208. + s * 9. / 8)))))));
        return rc;
    }

    /**
     * Calculate  R<sub>G</sub>(x,y,z).
     *
     * @param x 1.st argument
     * @param y 2.nd argument
     * @param z 3.rd argument
     * @return R<sub>G</sub>(x,y,z)
     */
    public static double rg(final double x, final double y, final double z) {
        if (x == 0)
            return rg(y, z, 0);
        if (z == 0)
            return rg(x, y);
        return (z * rf(x, y, z) - (x - z) * (y - z) * rd(x, y, z) / 3 + sqrt(x * y / z)) / 2;
    }

    /**
     * Calculate  R<sub>J</sub>(x,y,z,p).
     *
     * @param x 1.st argument
     * @param y 2.nd argument
     * @param z 3.rd argument
     * @param p 4.th argument
     * @return R<sub>J</sub>(x,y,z,p)
     */
    public static double rj(final double x, final double y, final double z, final double p) {
        if (p < 0) {
            double q = (z - y) * (y - x);
            if (q < 0)
                return rj(y, x, z, p);
            q = y + q / (y - p);
            return ((q - y) * rj(x, y, z, q) - 3 * rf(x, y, z) + 3 * sqrt(x * y * z / (x * z - p * q)) * rc(x * z - p * q, -p * q)) / (y - p);
        }
        if (x == 0)
            return rj(y, z, x, p);
        int n = 0;
        double a0 = (x + y + z + 2 * p) / 5;
        double em, srcm = 0, rcm, dm, d0 = (p - x) * (p - y) * (p - z);
        double q = RJ * absmax(x - a0, y - a0, z - a0, p - a0);
        double fn = 1, am = a0, lm, xm = x, ym = y, zm = z, pm = p;
        double e2, e3, e4, e5, rj;
        double sqx, sqy, sqz, sqp;
        do {
            n++;
            sqx = sqrt(xm);
            sqy = sqrt(ym);
            sqz = sqrt(zm);
            sqp = sqrt(pm);
            lm = sqx * sqy + sqx * sqz + sqy * sqz;
            dm = (sqp + sqx) * (sqp + sqy) * (sqp + sqz);
            em = d0 / (dm * dm * fn * fn * fn);
            rcm = rc(1, 1 + em);
            srcm += rcm / (fn * dm);

            fn *= 4;
            am = (am + lm) / 4;
            xm = (xm + lm) / 4;
            ym = (ym + lm) / 4;
            zm = (zm + lm) / 4;
            pm = (pm + lm) / 4;

        } while (q > fn * abs(am) && n < NMAX);
        if (n >= NMAX) {
            throw new IllegalArgumentException(String.format("rj(%f,%f,%f,%f) no convergence", x, y, z, p));
        }
        rj = 1 / (fn * am * sqrt(am));
        am *= fn;
        xm = (a0 - x) / am;
        ym = (a0 - y) / am;
        zm = (a0 - z) / am;
        pm = -(xm + ym + zm) / 2;
        e2 = xm * ym + xm * zm + ym * zm - 3 * pm * pm;
        e3 = xm * ym * zm + 2 * e2 * pm + 4 * pm * pm * pm;
        e4 = (2 * xm * ym * zm + e2 * pm + 3 * pm * pm * pm) * pm;
        e5 = xm * ym * zm * pm * pm;

        rj *= (1 - 3. / 14. * e2 + e3 / 6 + 9 * e2 * e2 / 88 - 3 * e4 / 22 - 9 * e2 * e3 / 52 + 3 * e5 / 26);
        rj += 6 * srcm;
        return rj;
    }

    /**
     * Calculate  R<sub>F</sub>(x,y,z).
     *
     * @param x 1.st argument
     * @param y 2.nd argument
     * @param z 3.rd argument
     * @return R<sub>F</sub>(x,y,z)
     */
    public static double rf(final double x, final double y, final double z) {
        if (z == 0)
            return rf(x, y);
        if (x <= 0) {
            if (y <= 0) {
                throw new IllegalArgumentException(String.format("rf(0,0,%f)", z));
            }
            return rf(y, z, x);
        }
        int n = 0;
        double a0 = (x + y + z) / 3;
        double q = RF * absmax(x - a0, y - a0, z - a0);
        double fn = 1, am = a0, lm, xm = x, ym = y, zm = z;
        double e2, e3, rf;
        double sqx, sqy, sqz;
        do {
            n++;
            fn *= 4;
            sqx = sqrt(xm);
            sqy = sqrt(ym);
            sqz = sqrt(zm);
            lm = sqx * sqy + sqx * sqz + sqy * sqz;

            am = (am + lm) / 4;
            xm = (xm + lm) / 4;
            ym = (ym + lm) / 4;
            zm = (zm + lm) / 4;

        } while (q > fn * abs(am) && n < NMAX);
        if (n >= NMAX) {
            throw new IllegalArgumentException(String.format("rf(%f,%f,%f) no convergence", x, y, z));
        }
        rf = 1 / sqrt(am);
        am *= fn;
        xm = (a0 - x) / am;
        ym = (a0 - y) / am;
        zm = -(xm + ym);
        e2 = xm * ym - zm * zm;
        e3 = xm * ym * zm;
        rf *= (1 - e2 / 10 + e3 / 14 + e2 * e2 / 24 - 3 * e2 * e3 / 44);
        return rf;
    }

    /**
     * Short term for R<sub>F</sub>(x,y,0)=R<sub>F</sub>(x,y).
     *
     * @param x 1.st argument
     * @param y 2.nd argument
     * @return R<sub>F</sub>(x,y,0)
     */
    public static double rf(final double x, final double y) {
        if (x < 0) {
            return rf(0, y + x, x) + rf(y - x, 2 * y, y);
        }

        if (x <= 0 || y <= 0) {
            throw new IllegalArgumentException(String.format("rf(%f,%f) negativ argument", x, y));
        }
        int n = 0;
        double rf, xm, ym, xn = sqrt(x), yn = sqrt(y), zm;
        do {
            n++;
            xm = (xn + yn) / 2;
            ym = sqrt(xn * yn);
            zm = abs(xm - ym);
            xn = xm;
            yn = ym;
        } while (zm > xm * DEPS && n < NMAX);
        if (n >= NMAX) {
            throw new IllegalArgumentException(String.format("rf(%f,%f) no convergence", x, y));
        }
        rf = Math.PI / (xm + ym);
        return rf;
    }

    /**
     * Short term for R<sub>G</sub>(x,y,0) = R<sub>G</sub>(x,y).
     *
     * @param x 1.st argument
     * @param y 2.nd argument
     * @return R<sub>G</sub>(x,y,0)
     */
    public static double rg(final double x, final double y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException(String.format("rg(%f,%f) negativ argument", x, y));
        }
        int n = 0;
        double rf, rg, tn = 1, xm, ym, xn = sqrt(x), yn = sqrt(y), zm, s0, sm = 0;
        s0 = xn + yn;
        s0 = s0 * s0;
        do {
            n++;
            tn *= 2;
            xm = (xn + yn) / 2;
            ym = sqrt(xn * yn);
            zm = (xm - ym);
            sm += tn * zm * zm;
            xn = xm;
            yn = ym;
        } while (abs(zm) > xm * DEPS && n < NMAX);
        if (n >= NMAX) {
            throw new IllegalArgumentException(String.format("rg(%f,%f) no convergence", x, y));
        }
        rf = Math.PI / (2 * xm);
        rg = (s0 - sm) * rf / 8;
        return rg;
    }

    /**
     * The complete first elliptic integral K(m),K(k<sup>2</sup>) or K(&alpha;)
     * for different arguments depending on the mod character.
     *
     * @param mod the argument out of {m,k,a}.
     * @param arg the argument standing for {m,k<sup>2</sup>,&alpha;}.
     * @return K(m)
     */
    public static double K(final char mod, final double arg) {
        double k, m;
        switch (mod) {
            case 'k':
                k = arg;
                m = k * k;
                // a = asin(k);
                break;
            case 'm':
                // k = sqrt(x);
                m = arg;
                // a = asin(k);
                break;
            case 'a':
                k = sin(arg);
                m = k * k;
                // a = x;
                break;
            default:
                throw new IllegalArgumentException("unkown mod: " + mod);
        }
        ;
        return rf(1 - m, 1);
    }

    /**
     * The complete second elliptic integral E(m),E(k<sup>2</sup>) or E(&alpha;)
     * for different arguments depending on the mod character.
     *
     * @param mod the argument out of {m,k,a}.
     * @param arg the argument standing for {m,k<sup>2</sup>,&alpha;}.
     * @return E(m)
     */
    public static double E(final char mod, final double arg) {
        double k, m;
        switch (mod) {
            case 'k':
                k = arg;
                m = k * k;
                // a = asin(k);
                break;
            case 'm':
                // k = sqrt(x);
                m = arg;
                // a = asin(k);
                break;
            case 'a':
                k = sin(arg);
                m = k * k;
                // a = x;
                break;
            default:
                throw new IllegalArgumentException("unkown mod: " + mod);
        }
        ;
        return 2 * rg(1 - m, 1);
    }

    /**
     * The complete first elliptic integral K(m)=F(1|m).
     *
     * @param m the argument
     * @return K(m)
     */
    public static double K(final double m) {
        return K('m', m);
    }

    /**
     * The complete second elliptic integral E(m)=E(1|m).
     *
     * @param m the argument
     * @return E(m)
     */
    public static double E(final double m) {
        return E('m', m);
    }

    /**
     * Calculate K(m) and E(m) using the AGM method for both functions in one shot.
     *
     * @param m the argument with m = k<sub>2</sub> = sin<sub>2</sub>&alpha;
     * @return array with [K,E](m)
     */
    public static double[] KandE(final double m) {
        if (m < 0 || 1 < m) {
            throw new IllegalArgumentException(String.format("K/E(%f) wrong argument", m));
        }
        double x = 1 - m;
        int n = 0;
        double k, e, tn = 1, xm, ym, xn = sqrt(x), yn = 1, zm, s0, sm = 0;
        s0 = xn + yn;
        s0 = s0 * s0;
        do {
            n++;
            tn *= 2;
            xm = (xn + yn) / 2;
            ym = sqrt(xn * yn);
            zm = (xm - ym);
            sm += tn * zm * zm;
            xn = xm;
            yn = ym;
        } while (abs(zm) > xm * DEPS && n < 2 * NMAX);
        if (n >= 2 * NMAX) {
            String msg = String.format("K/E(%f) no convergence", m);
            getLogger().error(msg);
            // throw new IllegalArgumentException();
        }
        k = Math.PI / (2 * xm);
        e = (s0 - sm) * k / 4;
        return new double[]{k, e};
    }

}
 