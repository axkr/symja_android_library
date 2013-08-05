/*
 * $Id: UnivPowerSeriesMap.java 3315 2010-09-05 18:26:34Z kredel $
 */

package edu.jas.ps;


import edu.jas.structure.RingElem;


/**
 * Univariate power series map interface. Defines method for mapping of power
 * series.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public interface UnivPowerSeriesMap<C extends RingElem<C>> {


    /**
     * Map.
     * @return new power series resulting from mapping elements of ps.
     */
    public UnivPowerSeries<C> map(UnivPowerSeries<C> ps);

}
