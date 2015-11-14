/*
 * $Id$
 */

package edu.jas.ps;


import java.util.List;

import edu.jas.structure.RingElem;
import edu.jas.structure.UnaryFunctor;
import edu.jas.util.ListUtil;


/**
 * Power series utilities. For example monic power series.
 * @author Heinz Kredel
 */

public class PSUtil {


    /**
     * Power series list monic.
     * @param <C> coefficient type.
     * @param L list of power series with field coefficients.
     * @return list of power series with leading coefficient 1.
     */
    public static <C extends RingElem<C>> List<MultiVarPowerSeries<C>> monic(List<MultiVarPowerSeries<C>> L) {
        return ListUtil.<MultiVarPowerSeries<C>, MultiVarPowerSeries<C>> map(L,
                        new UnaryFunctor<MultiVarPowerSeries<C>, MultiVarPowerSeries<C>>() {


                            public MultiVarPowerSeries<C> eval(MultiVarPowerSeries<C> c) {
                                if (c == null) {
                                    return null;
                                }
                                return c.monic();
                            }
                        });
    }


    /**
     * Univariate power series list monic.
     * @param <C> coefficient type.
     * @param L list of univariate power series with field coefficients.
     * @return list of univariate power series with leading coefficient 1.
     */
    public static <C extends RingElem<C>> List<UnivPowerSeries<C>> monicUniv(List<UnivPowerSeries<C>> L) {
        return ListUtil.<UnivPowerSeries<C>, UnivPowerSeries<C>> map(L,
                        new UnaryFunctor<UnivPowerSeries<C>, UnivPowerSeries<C>>() {


                            public UnivPowerSeries<C> eval(UnivPowerSeries<C> c) {
                                if (c == null) {
                                    return null;
                                }
                                return c.monic();
                            }
                        });
    }

}
