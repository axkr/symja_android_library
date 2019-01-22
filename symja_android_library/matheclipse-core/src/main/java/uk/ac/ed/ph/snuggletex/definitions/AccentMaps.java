/* $Id:AccentMaps.java 179 2008-08-01 13:41:24Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.definitions;

/**
 * Defines all of the {@link AccentMap}s for the various types of accents we support.
 * 
 * <h2>Developer Note</h2>
 * 
 * Add more entries to here as required, subject to the resulting accented characters
 * having adequate font support across the targetted browsers.
 *
 * @author  David McKain
 * @version $Revision:179 $
 */
public interface AccentMaps {
    
    public static final AccentMap ACCENT = new AccentMap(new char[] {
            'A', '\u00c1',
            'E', '\u00c9',
            'I', '\u00cd',
            'O', '\u00d3',
            'U', '\u00da',
            'a', '\u00e1',
            'e', '\u00e9',
            'i', '\u00ed',
            'o', '\u00f3',
            'u', '\u00fa',
            'y', '\u00fd'
        }, "");
    
    public static final AccentMap GRAVE = new AccentMap(new char[] {
            'A', '\u00c0',
            'E', '\u00c9',
            'I', '\u00cc',
            'O', '\u00d2',
            'U', '\u00d9',
            'a', '\u00e0',
            'e', '\u00e8',
            'i', '\u00ec',
            'o', '\u00f2',
            'u', '\u00f9'
        }, "");
    
    public static final AccentMap CIRCUMFLEX = new AccentMap(new char[] {
            'A', '\u00c2',
            'E', '\u00ca',
            'I', '\u00ce',
            'O', '\u00d4',
            'U', '\u00db',
            'a', '\u00e2',
            'e', '\u00ea',
            'i', '\u00ee',
            'o', '\u00f4',
            'u', '\u00fb'
        }, "");
    
    public static final AccentMap TILDE = new AccentMap(new char[] {
            'A', '\u00c3',
            'O', '\u00d5',
            'a', '\u00e3',
            'n', '\u00f1',
            'o', '\u00f5',
        }, "");
    
    public static final AccentMap UMLAUT = new AccentMap(new char[] {
            'A', '\u00c4',
            'E', '\u00cb',
            'I', '\u00cf',
            'O', '\u00d6',
            'U', '\u00dc',
            'a', '\u00e4',
            'e', '\u00eb',
            'i', '\u00ef',
            'o', '\u00f6',
            'u', '\u00fc'
        }, "");
}
