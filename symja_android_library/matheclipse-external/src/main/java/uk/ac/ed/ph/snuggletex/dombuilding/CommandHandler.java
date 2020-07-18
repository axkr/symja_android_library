/* $Id: CommandHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import uk.ac.ed.ph.snuggletex.definitions.BuiltinCommand;
import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;

/**
 * Defines how a {@link BuiltinCommand} should append children to the outgoing
 * DOM tree.
 * <p>
 * An instance of this interface must be stateless once created.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public interface CommandHandler {
    
    /**
     * Called when a {@link CommandToken} is being handled by the {@link DOMBuilder}.
     * 
     * @param builder {@link DOMBuilder} running this process, which provides access
     *   to convenience method for appending Nodes to the DOM
     * @param parentElement parent Element that the resulting Nodes should (can) be
     *   added to
     * @param token Token representing the command being processed.
     * 
     * @throws SnuggleParseException to indicate a client error
     * @throws DOMException if the usual errors occur when building the DOM.
     */
    void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token)
        throws SnuggleParseException;

}
