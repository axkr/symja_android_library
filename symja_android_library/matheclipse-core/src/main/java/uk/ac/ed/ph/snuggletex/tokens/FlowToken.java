/* $Id: FlowToken.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.tokens;

import uk.ac.ed.ph.snuggletex.definitions.LaTeXMode;
import uk.ac.ed.ph.snuggletex.definitions.TextFlowContext;
import uk.ac.ed.ph.snuggletex.internal.FrozenSlice;
import uk.ac.ed.ph.snuggletex.semantics.Interpretation;
import uk.ac.ed.ph.snuggletex.semantics.InterpretationType;

import java.util.EnumMap;

/**
 * Base class for {@link Token}s which represent the main "flow" of content, rather than things
 * like command arguments.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public abstract class FlowToken extends Token {
    
    protected final TextFlowContext textFlowContext;

    public FlowToken(final FrozenSlice slice, final TokenType type, final LaTeXMode latexMode,
            final TextFlowContext textFlowContext, final Interpretation... interpretations) {
        super(slice, type, latexMode, interpretations);
        this.textFlowContext = textFlowContext;
    }
    
    public FlowToken(final FrozenSlice slice, final TokenType type, final LaTeXMode latexMode,
            final TextFlowContext textFlowContext, final EnumMap<InterpretationType, Interpretation> interpretations) {
        super(slice, type, latexMode, interpretations);
        this.textFlowContext = textFlowContext;
    }
    
    /**
     * For {@link Token}s appearing inside TEXT flow, this indicates how this Token should flow against
     * its siblings.
     * <p>
     * For other types of {@link Token}, this returns null.
     */
    public TextFlowContext getTextFlowContext() {
        return textFlowContext;
    }
}
