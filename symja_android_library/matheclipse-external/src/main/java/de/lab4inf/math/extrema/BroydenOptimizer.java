/*
 * Lab4Inf
 *
 * University of applied sciences, Muenster, Germany
 * Lab for computer science.
 *
 * Project Mathmatics
 */

package de.lab4inf.math.extrema;

import de.lab4inf.math.Function;
import de.lab4inf.math.differentiation.Gradient;
import de.lab4inf.math.differentiation.Hessian;
import de.lab4inf.math.util.Aitken;

import static de.lab4inf.math.lapack.LinearAlgebra.add;
import static de.lab4inf.math.lapack.LinearAlgebra.copy;
import static de.lab4inf.math.lapack.LinearAlgebra.diff;
import static de.lab4inf.math.lapack.LinearAlgebra.identity;
import static de.lab4inf.math.lapack.LinearAlgebra.mult;
import static de.lab4inf.math.lapack.LinearAlgebra.multTrans;
import static de.lab4inf.math.lapack.LinearAlgebra.multTransposeB;
import static de.lab4inf.math.lapack.LinearAlgebra.norm;
import static de.lab4inf.math.lapack.LinearAlgebra.sub;
import static de.lab4inf.math.util.Accuracy.hasReachedAccuracy;
import static de.lab4inf.math.util.Accuracy.round;
import static java.lang.String.format;

/**
 * Optimizer using the Broyden method to find the optima without
 * using the hesse matrix. It uses an approximation of the hesse
 * matrix with the Sherman-Morrison formula without calculating
 * the inverse matrix.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: BroydenOptimizer.java,v 1.20 2013/02/09 16:21:22 nwulff Exp $
 * @since 13.10.2007
 */

public class BroydenOptimizer extends GenericOptimizer {
    private boolean debug = false;

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.extrema.GenericOptimizer#runMinimisation(de.lab4inf.math.Function, double[])
     */
    @Override
    protected boolean runMinimisation(final Function fct, final double... guess) {
        return runMaximisation(new MinimizerFct(fct), guess);
    }

    /**
     * Calculate the inverse matrix A using the Sherman-Morrison formula.
     */
    private double[][] shermanmorrison(final double[][] a, final double[] y,
                                       final double[] s) {
        double[] sAy, ay = mult(a, y);
        double scale = 1.0 / mult(s, ay);
        double[][] ai, sAys;
        sAy = sub(s, ay);
        sAys = multTrans(sAy, s);
        // a is a symmetric matrix => multiplication with transpose is quicker...
        ai = add(a, mult(multTransposeB(sAys, a), scale));
        return ai;
    }

    /*
     *     (non-Javadoc)
     * @see de.lab4inf.math.extrema.GenericOptimizer#runMaximisation(de.lab4inf.math.Function, double[])
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected boolean runMaximisation(final Function fct, final double... guess) {
        String th = Thread.currentThread().getName();
        int iteration = 0, n = guess.length;
        double delta;
        double[] s, y, yo, yn = copy(guess), fp, fpp, pp, p = copy(guess);
        double[][] a, aold;
        Gradient g = getGradient(fct);
        Hessian h = getHessian(fct);
        setTarget(fct);
        Aitken[] aitken = new Aitken[n];
        for (int i = 0; i < n; aitken[i] = new Aitken(), i++) ;
        if (debug) {
            getLogger().info(format("%15s %3d %s r:%5.4f", th, iteration,
                    display(p), norm(p)));
        }
        informIterationIsFinished(iteration, p);
        // calculate the inverse hessian matrix as starting point
        try {
            a = laSolver.inverse(h.hessian(p));
            aold = a;
        } catch (IllegalArgumentException e) {
            // determinant zero choose random ...
            // getLogger().warn("determinant is zero, using random matrix");
            // A = rndMatrix(n);
            getLogger().warn("determinant is zero, using id matrix");
            a = identity(n);
            aold = a;
        }
        fp = g.gradient(p);
        pp = sub(p, mult(a, fp));
        fpp = g.gradient(pp);
        do {
            yo = yn;
            // calculate dx = 1/A *grad(F)
            // using Sherman-Morrison to approximate the inverse Hessian
            y = sub(fpp, fp);
            s = sub(pp, p);
            try {
                a = shermanmorrison(aold, y, s);
                aold = a;
            } catch (Exception e) {
                getLogger().warn("Sherman-Morrison failed");
                a = aold;
            }
            fp = fpp;
            p = pp;
            s = mult(a, fp);
            pp = sub(p, s);
            fpp = g.gradient(pp);
            // using Aitkens delta method...
            yn = aitkenAccelerate(pp, aitken);
            if (debug) {
                delta = diff(pp, p);
                getLogger().info(format("%15s %3d %s r:%5.4f diff:%6.5f", th,
                        iteration, display(yn), norm(yn), delta));
            }
            informIterationIsFinished(iteration, yn);
            // } while(!hasConverged(yn, yo, getPrecision(), ++iteration,
            // getMaxIterations()));
        } while (!hasReachedAccuracy(yn, yo, getPrecision())
                && ++iteration <= getMaxIterations());
        if (iteration > getMaxIterations()) {
            getLogger().warn(format("no convergence, limit: %s", display(yn)));
        }
        for (int i = 0; i < n; i++) {
            guess[i] = round(yn[i], getPrecision());
        }

        informOptimizationIsFinished(iteration, guess);
        return iteration < getMaxIterations();
    }
}
 