/* $Id: LinkResolver.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex;

import uk.ac.ed.ph.snuggletex.dombuilding.HrefHandler;

import java.net.URI;

/**
 * This interface allows clients to have control over handling any hypertext links encountered
 * in commands like {@link HrefHandler}, making it possible to remap links as required.
 * <p>
 * This is entirely optional, though!
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public interface LinkResolver {
    
    /**
     * Called when a link URI has been encountered. Implementors should do whatever they
     * need to do to the URI and return a revised version to be used in the outgoing DOM and
     * web pages in its place.
     * 
     * @param href raw href as specified by client, which will be been checked to be a valid URI.
     * @param inputURI URI for the {@link SnuggleInput} containing the link, which may be null.
     * 
     * @return replacement href
     */
    URI mapLink(URI href, URI inputURI);

}
