/* $Id:Interpretation.java 179 2008-08-01 13:41:24Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.semantics;

import uk.ac.ed.ph.snuggletex.tokens.Token;

/**
 * This base interface allows some kind of "semantic interpretation" to be associated with a
 * particular {@link Token}. This makes it easy for Tokens of different types share the same
 * kind of interpretation, which is especially useful when parsing maths input.
 * 
 * <h2>Developer Notes</h2>
 * 
 * <ul>
 *   <li>
 *     These are useful when you want to do the same thing over a family of {@link Token}s that
 *     don't appear related as far as the {@link Token} hierarchy goes.
 *   </li>
 *   <li>
 *     I'm not aiming to provide semantics for every token - only when it appears useful.
 *   </li>
 * </ul>
 * 
 * @author  David McKain
 * @version $Revision:179 $
 */
public interface Interpretation {
    
    InterpretationType getType();
    
}
