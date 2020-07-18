/* $Id: EnvironmentToken.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.tokens;

import uk.ac.ed.ph.snuggletex.internal.util.DumpMode;
import uk.ac.ed.ph.snuggletex.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.snuggletex.definitions.BuiltinEnvironment;
import uk.ac.ed.ph.snuggletex.definitions.LaTeXMode;
import uk.ac.ed.ph.snuggletex.internal.FrozenSlice;

/**
 * Represents a {@link BuiltinEnvironment}.
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class EnvironmentToken extends FlowToken {
    
    private final BuiltinEnvironment environment;
    private final ArgumentContainerToken optionalArgument;
    private final ArgumentContainerToken[] arguments;
    private final ArgumentContainerToken content;
    
    public EnvironmentToken(final FrozenSlice slice, final LaTeXMode latexMode,
            final BuiltinEnvironment environment, final ArgumentContainerToken content) {
        this(slice, latexMode, environment, null, ArgumentContainerToken.EMPTY_ARRAY, content);
    }
    
    public EnvironmentToken(final FrozenSlice slice, final LaTeXMode latexMode,
            final BuiltinEnvironment environment, final ArgumentContainerToken optionalArgument,
            final ArgumentContainerToken[] arguments, final ArgumentContainerToken content) {
        super(slice, TokenType.ENVIRONMENT, latexMode, environment.getTextFlowContext(), environment.getInterpretationMap());
        this.environment = environment;
        this.optionalArgument = optionalArgument;
        this.arguments = arguments;
        this.content = content;
    }

    public BuiltinEnvironment getEnvironment() {
        return environment;
    }
    
    @ObjectDumperOptions(DumpMode.DEEP)
    public ArgumentContainerToken getOptionalArgument() {
        return optionalArgument;
    }
    
    @ObjectDumperOptions(DumpMode.DEEP)
    public ArgumentContainerToken[] getArguments() {
        return arguments;
    }
    
    @ObjectDumperOptions(DumpMode.DEEP)
    public ArgumentContainerToken getContent() {
        return content;
    }
}
