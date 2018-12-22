/* $Id:TokenType.java 179 2008-08-01 13:41:24Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.tokens;

/**
 * Enumerates the various types of LaTeX tokens.
 * 
 * <h2>Developer Note</h2>
 * 
 * The less complex types of tokens are described as {@link SimpleToken}s here.
 * 
 * @author  David McKain
 * @version $Revision:179 $
 */
public enum TokenType {
    
    /** Main types, correspond to a subclass of {@link Token} */
    ARGUMENT_CONTAINER,
    BRACE_CONTAINER,
    ENVIRONMENT,
    COMMAND,
    ERROR,

    /* Simple tokens */
    NEW_PARAGRAPH, /* (This is stripped out during fixing) */
    TAB_CHARACTER, /* (a.k.a. &, as used in tables and arrays) */
    TEXT_MODE_TEXT,
    LR_MODE_NEW_PARAGRAPH, /* (This is substituted in during fixing - not entered directly) */
    VERBATIM_MODE_TEXT,
    MATH_NUMBER,
    SINGLE_CHARACTER_MATH_IDENTIFIER,
    SINGLE_CHARACTER_MATH_SPECIAL,
    
    ;

}
