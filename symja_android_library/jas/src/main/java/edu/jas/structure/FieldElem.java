/*
 * $Id: FieldElem.java 4056 2012-07-26 17:44:13Z kredel $
 */

package edu.jas.structure;


/**
 * Field element interface. Empty interface since inverse is already in
 * RingElem.
 * @param <C> field element type
 * @author Heinz Kredel
 */

public interface FieldElem<C extends FieldElem<C>> extends RingElem<C> {

}
