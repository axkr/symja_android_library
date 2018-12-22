/* $Id:AccentMap.java 179 2008-08-01 13:41:24Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.definitions;

/**
 * Map defines how unaccented alphabetic characters should map to corresponding
 * Unicode characters when a particular type of accent is applied (e.g. circumflex).
 *
 * @author  David McKain
 * @version $Revision:179 $
 */
public final class AccentMap {
    
    private static final int MIN_CODEPOINT = 32;
    private static final int MAX_CODEPOINT = 128;
    
    private final char[] textAccentByCodePoint;
    private final char[] mathSafeAccentByCodePoint;
    
    public AccentMap(char[] accentData, String mathUnsafeChars) {
        textAccentByCodePoint = new char[MAX_CODEPOINT - MIN_CODEPOINT];
        mathSafeAccentByCodePoint = new char[MAX_CODEPOINT - MIN_CODEPOINT];
        
        char ascii, accented;
        int index;
        for (int i=0; i<accentData.length; ) {
            ascii = accentData[i++];
            accented = accentData[i++];
            index = charToIndex(ascii);
            if (index!=-1) {
                textAccentByCodePoint[index] = accented;
                if (mathUnsafeChars.indexOf(ascii)==-1) {
                    mathSafeAccentByCodePoint[index] = accented;
                }
            }
        }
    }
    
    public char getAccentedTextChar(char c) {
        int index = charToIndex(c);
        return index!=-1 ? textAccentByCodePoint[index] : 0;
    }
    
    public char getAccentedMathChar(char c) {
        int index = charToIndex(c);
        return index!=-1 ? mathSafeAccentByCodePoint[index] : 0;
    }
    
    private int charToIndex(char c) {
        int index = c - MIN_CODEPOINT;
        return (index>=0 && index<MAX_CODEPOINT) ? index : -1;
    }

}
