/* $Id: BraceContainerToken.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.tokens;

import uk.ac.ed.ph.snuggletex.definitions.LaTeXMode;
import uk.ac.ed.ph.snuggletex.internal.FrozenSlice;

/**
 * Represents a literal braced section the incoming LaTeX document, i.e. something like
 * <tt>{ ... }</tt>.
 * <p>
 * Its content is represented by a single {@link ArgumentContainerToken}.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class BraceContainerToken extends FlowToken {
    
    private final ArgumentContainerToken braceContent;

    public BraceContainerToken(FrozenSlice slice, LaTeXMode latexMode, ArgumentContainerToken braceContent) {
        super(slice, TokenType.BRACE_CONTAINER, latexMode, null);
        this.braceContent = braceContent;
    }

    public ArgumentContainerToken getBraceContent() {
        return braceContent;
    }
}
