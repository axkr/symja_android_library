/* $Id:BuiltinCommand.java 179 2008-08-01 13:41:24Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.definitions;

import uk.ac.ed.ph.snuggletex.SnugglePackage;
import uk.ac.ed.ph.snuggletex.dombuilding.CommandHandler;
import uk.ac.ed.ph.snuggletex.semantics.Interpretation;
import uk.ac.ed.ph.snuggletex.semantics.InterpretationType;

import java.util.EnumMap;
import java.util.EnumSet;

/**
 * Represents a {@link Command} that has been defined internally via a {@link SnugglePackage}.
 * <p>
 * All of the core LaTeX macros have been defined in this way - see {@link CorePackageDefinitions}.
 * 
 * @see BuiltinEnvironment
 * @see CorePackageDefinitions
 * 
 * @author  David McKain
 * @version $Revision:179 $
 */
public final class BuiltinCommand extends BuiltinCommandOrEnvironment<CommandHandler>
        implements Command {
    
    private final CommandType type;
    
    private final CombinerTargetMatcher combinerTargetMatcher;
    
    /** 
     * Mode to use when parsing arguments. If not supplied, will preserve mode that Macro
     * is being used in.
     * 
     * (Main example: \mbox{...} will go into LR mode for its arguments)
     */
    private final LaTeXMode[] argumentModes;
    
    //--------------------------------------------------
    
    public BuiltinCommand(final String texName, final CommandType commandType,
            final boolean allowingOptionalArgument, final int argumentCount,
            final EnumSet<LaTeXMode> allowedModes, final LaTeXMode[] argumentModes,
            final EnumMap<InterpretationType, Interpretation> interpretations,
            final CommandHandler domBuilderHandler, final TextFlowContext textFlowContext,
            final CombinerTargetMatcher combinerTargetMatcher) {
        super(texName, allowingOptionalArgument, argumentCount, allowedModes,
                interpretations, textFlowContext, domBuilderHandler);
        this.type = commandType;
        this.argumentModes = argumentModes;
        this.combinerTargetMatcher = combinerTargetMatcher;
    }
    
    public CommandType getType() {
        return type;
    }
    
    public LaTeXMode getArgumentMode(int argumentIndex) {
        return argumentModes!=null ? argumentModes[argumentIndex] : null;
    }
    
    public CombinerTargetMatcher getCombinerTargetMatcher() {
        return combinerTargetMatcher;
    }
}
