/* $Id: DOMPostProcessor.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex;

import uk.ac.ed.ph.snuggletex.utilities.StylesheetManager;

import org.w3c.dom.Document;

/**
 * This interface allows you to hook into the SnuggleTeX process immediately after the raw
 * DOM tree has been built. By implementing this interface, you may make modifications to the
 * DOM to suit your needs.
 * <ul>
 *   <li>
 *     The SnuggleTeX core contains a {@link DownConvertingPostProcessor}
 *   </li>
 *   <li>
 *     The SnuggleTeX Up-Conversion module also includes an "UpConvertingPostProcessor" that
 *     may be used here as well.
 *   </li>
 * </ul>
 * 
 * @see DownConvertingPostProcessor
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public interface DOMPostProcessor {
   
    /**
     * Implement this to post-process the raw DOM produced by SnuggleTeX.
     * <p>
     * You will be given an entire DOM Document to analyse, containing a root element
     * called <tt>root</tt> in the {@link SnuggleConstants#SNUGGLETEX_NAMESPACE} namespace.
     * <p>
     * You should create a new Document and return it. The children of the root element will
     * end up being the Nodes added to the final DOM.
     * <p>
     * You MUST NOT modify the workDocument that is passed to you.
     * 
     * @param workDocument incoming DOM to be analysed for post-processing
     * @param options {@link DOMOutputOptions} specified by caller
     * @param stylesheetManager Instance of {@link StylesheetManager} that can be used to
     *   load and manage XSLT stylesheets.
     */
    Document postProcessDOM(final Document workDocument, final DOMOutputOptions options,
            StylesheetManager stylesheetManager);

}
