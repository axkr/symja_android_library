/* $Id: BuiltinCommandOrEnvironment.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.definitions;

import uk.ac.ed.ph.snuggletex.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.snuggletex.semantics.Interpretation;
import uk.ac.ed.ph.snuggletex.semantics.InterpretationType;

import java.util.EnumMap;
import java.util.EnumSet;

/**
 * Partial base class for {@link BuiltinCommand} and {@link BuiltinEnvironment}.
 * 
 * @param <H> type of "handler" used to generate DOM subtrees.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
abstract class BuiltinCommandOrEnvironment<H> implements CommandOrEnvironment {
    
    protected final String texName;
    protected final boolean allowingOptionalArgument;
    protected final int argumentCount;
    protected final EnumSet<LaTeXMode> allowedModes;
    protected final EnumMap<InterpretationType, Interpretation> interpretationMap;
    protected final TextFlowContext textFlowContext;
    protected final H domBuildingHandler;
    
    protected BuiltinCommandOrEnvironment(final String texName, final boolean allowingOptionalArgument,
            final int argumentCount, final EnumSet<LaTeXMode> allowedModes,
            final EnumMap<InterpretationType, Interpretation> interpretationMap, final TextFlowContext textFlowContext,
            final H domBuildingHandler) {
        ConstraintUtilities.ensureNotNull(texName, "texName");
        ConstraintUtilities.ensureNotNull(allowedModes, "allowedModes");
        this.texName = texName;
        this.allowingOptionalArgument = allowingOptionalArgument;
        this.argumentCount = argumentCount;
        this.allowedModes = allowedModes;
        this.interpretationMap = interpretationMap;
        this.textFlowContext = textFlowContext;
        this.domBuildingHandler = domBuildingHandler;
    }

    public String getTeXName() {
        return texName;
    }
    
    public boolean isAllowingOptionalArgument() {
        return allowingOptionalArgument;
    }
    
    public int getArgumentCount() {
        return argumentCount;
    }
    
    public EnumSet<LaTeXMode> getAllowedModes() {
        return allowedModes;
    }
    
    public EnumMap<InterpretationType, Interpretation> getInterpretationMap() {
        return interpretationMap;
    }
    
    public boolean hasInterpretation(InterpretationType type) {
        return interpretationMap!=null && interpretationMap.containsKey(type);
    }
    
    public Interpretation getInterpretation(InterpretationType type) {
        return interpretationMap!=null ? interpretationMap.get(type) : null;
    }

    
    public TextFlowContext getTextFlowContext() {
        return textFlowContext;
    }
    
    public H getDOMBuildingHandler() {
        return domBuildingHandler;
    }
    
    @Override
    public final String toString() {
        return getClass().getSimpleName()
            + "("
            + (texName!=null ? texName : "@" + Integer.toHexString(hashCode()))
            + ")";
    }
}
