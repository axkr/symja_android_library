/* $Id: MathVariantMap.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.definitions;

/**
 * This map encapsulates how to apply certain types of Mathematical character variants to
 * "safe and plain" Unicode characters by mapping them to other characters.
 * 
 * @see MathVariantMaps
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class MathVariantMap {
    
    private static final int MIN_CODEPOINT = 32;
    private static final int MAX_CODEPOINT = 128;

    private final String mathVariantName;
    private final char[] accentByCodePoint;
    
    public MathVariantMap(final String mathVariantName, final char[] accentData) {
        this.mathVariantName = mathVariantName;
        this.accentByCodePoint = new char[MAX_CODEPOINT - MIN_CODEPOINT];
        char ascii, accented;
        int index;
        for (int i=0; i<accentData.length; ) {
            ascii = accentData[i++];
            accented = accentData[i++];
            index = charToIndex(ascii);
            if (index!=-1) {
                accentByCodePoint[index] = accented;
            }
        }
    }
    
    public String getMathVariantName() {
        return mathVariantName;
    }

    public char getAccentedChar(char c) {
        int index = charToIndex(c);
        return index!=-1 ? accentByCodePoint[index] : 0;
    }
    
    private int charToIndex(char c) {
        int index = c - MIN_CODEPOINT;
        return (index>=0 && index<MAX_CODEPOINT) ? index : -1;
    }

}
