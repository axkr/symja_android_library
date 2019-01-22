/* $Id:CommandOrEnvironment.java 179 2008-08-01 13:41:24Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.definitions;

/**
 * Base interface for LaTeX commands and environments.
 *
 * @author  David McKain
 * @version $Revision:179 $
 */
public interface CommandOrEnvironment {
    
    /**
     * Returns the TeX name for this command or environment. (In the case of commands, this does
     * not include the leading backslash.)
     */
    String getTeXName();
    
    /**
     * Returns whether this command/environment takes an optional argument.
     * As per LaTeX convention, this will only return true if the command/environment takes
     * at least one mandatory argument as well.
     */
    boolean isAllowingOptionalArgument();
    
    /**
     * Returns the number of mandatory arguments this command expects, which will be zero
     * or greater.
     */
    int getArgumentCount();
    
    /**
     * Returns the LaTeX mode that each argument should be parsed in, starting with the
     * optional argument (if used) and then each mandatory argument in order. Null entries
     * indicate that the current LaTeX mode should be used.
     */
    LaTeXMode getArgumentMode(int argumentIndex);

}
