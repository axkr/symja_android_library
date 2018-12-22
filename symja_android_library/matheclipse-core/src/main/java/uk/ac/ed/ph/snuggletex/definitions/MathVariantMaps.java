/* $Id: MathVariantMaps.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.definitions;

/**
 * Defines the (safe) mappings of characters for the different types of "mathvariants"
 * we support.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public interface MathVariantMaps {

    /**
     * These are defined from the <a href="http://www.w3.org/TR/MathML2/script.html">MathML Script</a>
     * characters page, omitting those in Unicode plane 1.
     * 
     * TODO: Check font support for these!
     */
    public static final MathVariantMap SCRIPT = new MathVariantMap("script", new char[] {
            'B', '\u212c',
            'E', '\u2130',
            'F', '\u2131',
            'H', '\u210b',
            'I', '\u2110',
            'L', '\u2112',
            'M', '\u2133',
            'R', '\u211b',
            'e', '\u212f',
            'g', '\u210a',
            'o', '\u2134',
        });
    
    /**
     * These are defined from the <a href="http://www.w3.org/TR/MathML2/double-struck.html">MathML Double Struck</a>
     * characters page, omitting those in Unicode plane 1.
     * 
     * TODO: Check font support for these!
     */
    public static final MathVariantMap DOUBLE_STRUCK = new MathVariantMap("double-struck", new char[] {
            'C', '\u2102',
            'H', '\u210d',
            'N', '\u2115',
            'P', '\u2119',
            'Q', '\u211a',
            'R', '\u211d',
            'Z', '\u2124'
        });
    
    /**
     * These are defined from the <a href="http://www.w3.org/TR/MathML2/fracktur.html">Fraktur</a>
     * characters page, omitting those in Unicode plane 1.
     * 
     * TODO: Check font support for these!
     */
    public static final MathVariantMap FRAKTUR = new MathVariantMap("fraktur", new char[] {
            'C', '\u212d',
            'H', '\u210c',
            'I', '\u2111',
            'R', '\u211c',
            'Z', '\u2128',
            'R', '\u211b',
            'e', '\u212f',
            'g', '\u210a',
            'o', '\u2134',
        });
}
