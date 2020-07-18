/* $Id: UserDefinedCommandOrEnvironment.java 561 2010-05-19 15:41:34Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.definitions;

/**
 * Partial base class for {@link UserDefinedCommand}s and {@link UserDefinedEnvironment}s.
 * 
 * @see UserDefinedEnvironment
 * 
 * @author  David McKain
 * @version $Revision: 561 $
 */
public abstract class UserDefinedCommandOrEnvironment implements CommandOrEnvironment {
 
    protected final String texName;
    
    /** Optional argument, which may be blank. null if not supported */
    protected final String optionalArgument;
    
    /** Number of arguments */
    protected final int argumentCount;
    
    public UserDefinedCommandOrEnvironment(final String texName, final String optionalArgument,
            final int argumentCount) {
        this.texName = texName;
        this.optionalArgument = optionalArgument;
        this.argumentCount = argumentCount;
    }

    public String getTeXName() {
        return texName;
    }

    public boolean isAllowingOptionalArgument() {
        return optionalArgument!=null;
    }
    
    public String getOptionalArgument() {
        return optionalArgument;
    }

    public int getArgumentCount() {
        return argumentCount;
    }

    /**
     * User-defined commands and environments stay in the same mode when parsing arguments.
     */
    public LaTeXMode getArgumentMode(int argumentIndex) {
        return null;
    }
    
    @Override
    public final String toString() {
        return getClass().getSimpleName() + "(" + texName + ")";
    }
}
