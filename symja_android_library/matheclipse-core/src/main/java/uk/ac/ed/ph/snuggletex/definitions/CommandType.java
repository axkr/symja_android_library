/* $Id:CommandType.java 179 2008-08-01 13:41:24Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.definitions;

/**
 * Enumerates different types of LaTeX commands.
 * 
 * @author  David McKain
 * @version $Revision:179 $
 */
public enum CommandType {
    
    /** Takes no arguments */
    SIMPLE,
    
    /** Takes no arguments, absorbs the following token */
    COMBINER,
    
    /** Takes arguments */
    COMPLEX,
    
    ;
}