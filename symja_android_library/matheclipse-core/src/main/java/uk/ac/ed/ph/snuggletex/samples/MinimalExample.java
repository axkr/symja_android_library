/* $Id: MinimalExample.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.samples;

import uk.ac.ed.ph.snuggletex.SnuggleInput;
import uk.ac.ed.ph.snuggletex.SnuggleEngine;
import uk.ac.ed.ph.snuggletex.SnuggleSession;

import java.io.IOException;

/**
 * Example demonstrating a minimal example use of SnuggleTeX.
 * <p>
 * This simply converts a fixed input String of LaTeX to XML. 
 * (In this case, the result is a fragment of MathML.)
 * 
 * @see XMLStringOutputExample
 * @see WebPageExample
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class MinimalExample {
    
    public static void main(String[] args) throws IOException {
        /* Create vanilla SnuggleEngine and new SnuggleSession */
        SnuggleEngine engine = new SnuggleEngine();
        SnuggleSession session = engine.createSession();
        
        /* Parse some very basic Math Mode input */
        SnuggleInput input = new SnuggleInput("$$ x+2=3 $$");
        session.parseInput(input);
        
        /* Convert the results to an XML String, which in this case will
         * be a single MathML <math>...</math> element. */
        String xmlString = session.buildXMLString();
        System.out.println("Input " + input.getString()
                + " was converted to:\n" + xmlString);
    }
}
