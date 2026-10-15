package org.matheclipse.external.apfloat;

import java.util.ArrayList;
import java.util.List;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.InfiniteExpansionException;

/**
 * The Meijer G-function, as the sum of residues which Slater's theorem gives.<p>
 *
 * The residues at the poles of &Gamma;(<i>b</i><sub><i>k</i></sub> + <i>s</i>) give a sum of
 * functions <sub><i>p</i></sub><i>F</i><sub><i>q</i>-1</sub> in <i>z</i>, the residues at the
 * poles of &Gamma;(1 - <i>a</i><sub><i>k</i></sub> - <i>s</i>) a sum of functions
 * <sub><i>q</i></sub><i>F</i><sub><i>p</i>-1</sub> in 1 / <i>z</i>. The first is taken for
 * <i>p</i> &lt; <i>q</i>, the second for <i>p</i> &gt; <i>q</i>; for <i>p</i> = <i>q</i> the
 * first inside the unit circle and on it, the second outside of it.
 */

public class ApcomplexMeijerGMath
{
    private ApcomplexMeijerGMath()
    {
    }

    /**
     * The Meijer G-function
     * <i>G</i><sup><i>m</i>,<i>n</i></sup><sub><i>p</i>,<i>q</i></sub>(<i>z</i>, <i>r</i>),
     * the integral over &prod; &Gamma;(<i>b</i><sub><i>j</i></sub> + <i>s</i>) (<i>j</i> &le; <i>m</i>)
     * &prod; &Gamma;(1 - <i>a</i><sub><i>j</i></sub> - <i>s</i>) (<i>j</i> &le; <i>n</i>) /
     * (&prod; &Gamma;(<i>a</i><sub><i>j</i></sub> + <i>s</i>) (<i>j</i> &gt; <i>n</i>)
     * &prod; &Gamma;(1 - <i>b</i><sub><i>j</i></sub> - <i>s</i>) (<i>j</i> &gt; <i>m</i>))
     * <i>z</i><sup>-<i>s</i>/<i>r</i></sup> d<i>s</i> / (2 &pi; <i>i</i>).
     *
     * @param an The parameters <i>a</i><sub>1</sub>, &hellip;, <i>a</i><sub><i>n</i></sub>.
     * @param ap The parameters <i>a</i><sub><i>n</i>+1</sub>, &hellip;, <i>a</i><sub><i>p</i></sub>.
     * @param bm The parameters <i>b</i><sub>1</sub>, &hellip;, <i>b</i><sub><i>m</i></sub>.
     * @param bq The parameters <i>b</i><sub><i>m</i>+1</sub>, &hellip;, <i>b</i><sub><i>q</i></sub>.
     * @param z The argument.
     * @param r The scale of the exponent, 1 for the ordinary G-function.
     *
     * @return The value, to the smallest precision of the parameters and the argument.
     *
     * @throws ArithmeticException If the function isn't defined for the parameters, is infinite, or cannot be calculated at the argument.
     */

    public static Apcomplex meijerG(Apcomplex[] an, Apcomplex[] ap, Apcomplex[] bm, Apcomplex[] bq, Apcomplex z, Apcomplex r)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = Math.min(z.precision(), r.precision());
        for (Apcomplex[] list : new Apcomplex[][] { an, ap, bm, bq })
        {
            for (Apcomplex x : list)
            {
                precision = Math.min(precision, x.precision());
            }
        }
        return meijerG(an, ap, bm, bq, z, r, precision);
    }

    /**
     * The Meijer G-function to the given precision.
     *
     * @param an The parameters <i>a</i><sub>1</sub>, &hellip;, <i>a</i><sub><i>n</i></sub>.
     * @param ap The parameters <i>a</i><sub><i>n</i>+1</sub>, &hellip;, <i>a</i><sub><i>p</i></sub>.
     * @param bm The parameters <i>b</i><sub>1</sub>, &hellip;, <i>b</i><sub><i>m</i></sub>.
     * @param bq The parameters <i>b</i><sub><i>m</i>+1</sub>, &hellip;, <i>b</i><sub><i>q</i></sub>.
     * @param z The argument.
     * @param r The scale of the exponent, 1 for the ordinary G-function.
     * @param precision The precision of the result.
     *
     * @return The value.
     *
     * @throws ArithmeticException If the function isn't defined for the parameters, is infinite, or cannot be calculated at the argument.
     */

    public static Apcomplex meijerG(Apcomplex[] an, Apcomplex[] ap, Apcomplex[] bm, Apcomplex[] bq, Apcomplex z, Apcomplex r, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Meijer G-function to infinite precision", "meijerG.infinitePrecision");
        }
        final int n = an.length,
                  p = n + ap.length,
                  m = bm.length,
                  q = m + bq.length;
        Apcomplex[] parameters = new Apcomplex[p + q];
        System.arraycopy(an, 0, parameters, 0, n);
        System.arraycopy(ap, 0, parameters, n, ap.length);
        System.arraycopy(bm, 0, parameters, p, m);
        System.arraycopy(bq, 0, parameters, p + m, bq.length);
        if (r.real().signum() <= 0 || r.imag().signum() != 0)
        {
            throw new ArithmeticException("Meijer G-function needs a positive scale");
        }
        // The poles of the gamma functions on the left and on the right of the path coincide
        for (int j = 0; j < n; j++)
        {
            for (int k = 0; k < m; k++)
            {
                Apcomplex difference = an[j].subtract(bm[k]);
                if (difference.imag().signum() == 0 && difference.real().signum() > 0 && difference.real().isInteger())
                {
                    throw new ArithmeticException("Meijer G-function is not defined for these parameters");
                }
            }
        }
        boolean zero = ApcomplexHypercombMath.isZero(z);
        boolean inverse;
        if (p < q)
        {
            inverse = false;
        }
        else if (p > q)
        {
            inverse = true;
        }
        else
        {
            inverse = !zero && ApcomplexMath.abs(z).compareTo(new Apfloat(1, Apfloat.INFINITE, z.radix())) > 0;
        }
        if (zero && inverse)
        {
            throw new ArithmeticException("Meijer G-function is infinite at zero");
        }
        final boolean seriesInZ = !inverse;
        // At zero only the residues at b_k = 0 remain; those at Re(b_k) > 0 vanish
        final boolean[] keep = new boolean[m];
        for (int k = 0; k < m; k++)
        {
            keep[k] = !zero || ApcomplexHypercombMath.isZero(bm[k]);
            if (zero && !keep[k] && bm[k].real().signum() <= 0)
            {
                throw new ArithmeticException("Meijer G-function is infinite at zero");
            }
        }
        ApcomplexHypercombMath.Terms terms = (params, workingPrecision) ->
        {
            FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
            int radix = z.radix();
            Apfloat one = new Apfloat(1, workingPrecision, radix);
            Apcomplex[] a = new Apcomplex[p],
                        b = new Apcomplex[q];
            System.arraycopy(params, 0, a, 0, p);
            System.arraycopy(params, p, b, 0, q);
            Apcomplex scale = ApfloatHelperShim.ensurePrecision(r, workingPrecision),
                      x = ApfloatHelperShim.ensurePrecision(z, workingPrecision);
            List<ApcomplexHypercombMath.Term> list = new ArrayList<ApcomplexHypercombMath.Term>();
            if (seriesInZ)
            {
                // (-1)^(p-m-n) z^(1/r)
                Apcomplex argument = (zero ? Apcomplex.ZERO : power(h, x, h.divide(one, scale)));
                if ((p - m - n) % 2 != 0)
                {
                    argument = argument.negate();
                }
                for (int k = 0; k < m; k++)
                {
                    if (!keep[k])
                    {
                        continue;
                    }
                    Apcomplex exponent = h.divide(b[k], scale);
                    List<Apcomplex> gn = new ArrayList<Apcomplex>(),
                                    gd = new ArrayList<Apcomplex>(),
                                    hn = new ArrayList<Apcomplex>(),
                                    hd = new ArrayList<Apcomplex>();
                    for (int j = 0; j < m; j++)
                    {
                        if (j != k)
                        {
                            gn.add(h.subtract(b[j], b[k]));
                        }
                    }
                    for (int j = 0; j < n; j++)
                    {
                        gn.add(h.add(h.subtract(one, a[j]), b[k]));
                    }
                    for (int j = n; j < p; j++)
                    {
                        gd.add(h.subtract(a[j], b[k]));
                    }
                    for (int j = m; j < q; j++)
                    {
                        gd.add(h.add(h.subtract(one, b[j]), b[k]));
                    }
                    for (int j = 0; j < p; j++)
                    {
                        hn.add(h.add(h.subtract(one, a[j]), b[k]));
                    }
                    for (int j = 0; j < q; j++)
                    {
                        if (j != k)
                        {
                            hd.add(h.add(h.subtract(one, b[j]), b[k]));
                        }
                    }
                    list.add(term(zero ? null : x, exponent, gn, gd, hn, hd, argument));
                }
            }
            else
            {
                // (-1)^(q-m-n) / z^(1/r)
                Apcomplex argument = h.divide(one, power(h, x, h.divide(one, scale)));
                if ((q - m - n) % 2 != 0)
                {
                    argument = argument.negate();
                }
                for (int k = 0; k < n; k++)
                {
                    Apcomplex exponent = h.divide(h.subtract(a[k], one), scale);
                    List<Apcomplex> gn = new ArrayList<Apcomplex>(),
                                    gd = new ArrayList<Apcomplex>(),
                                    hn = new ArrayList<Apcomplex>(),
                                    hd = new ArrayList<Apcomplex>();
                    for (int j = 0; j < n; j++)
                    {
                        if (j != k)
                        {
                            gn.add(h.subtract(a[k], a[j]));
                        }
                    }
                    for (int j = 0; j < m; j++)
                    {
                        gn.add(h.add(h.subtract(one, a[k]), b[j]));
                    }
                    for (int j = m; j < q; j++)
                    {
                        gd.add(h.subtract(a[k], b[j]));
                    }
                    for (int j = n; j < p; j++)
                    {
                        gd.add(h.add(h.subtract(one, a[k]), a[j]));
                    }
                    for (int j = 0; j < q; j++)
                    {
                        hn.add(h.add(h.subtract(one, a[k]), b[j]));
                    }
                    for (int j = 0; j < p; j++)
                    {
                        if (j != k)
                        {
                            hd.add(h.subtract(h.add(one, a[j]), a[k]));
                        }
                    }
                    list.add(term(x, exponent, gn, gd, hn, hd, argument));
                }
            }
            return list;
        };
        return ApcomplexHypercombMath.hypercomb(terms, parameters, precision);
    }

    private static ApcomplexHypercombMath.Term term(Apcomplex base, Apcomplex exponent, List<Apcomplex> gn, List<Apcomplex> gd, List<Apcomplex> hn, List<Apcomplex> hd, Apcomplex argument)
    {
        Apcomplex[] none = new Apcomplex[0];
        return new ApcomplexHypercombMath.Term(base == null ? none : new Apcomplex[] { base },
                                              base == null ? none : new Apcomplex[] { exponent },
                                              gn.toArray(none), gd.toArray(none), hn.toArray(none), hd.toArray(none), argument);
    }

    // z^c on the principal branch
    private static Apcomplex power(FixedPrecisionApfloatHelper h, Apcomplex z, Apcomplex c)
    {
        if (c.real().signum() == 0 && c.imag().signum() == 0)
        {
            return new Apfloat(1, h.precision(), z.radix());
        }
        if (c.imag().signum() == 0 && c.real().compareTo(new Apfloat(1, Apfloat.INFINITE, z.radix())) == 0)
        {
            return z;
        }
        return h.pow(z, c);
    }
}
