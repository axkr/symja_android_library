/* $Id: MathMLPostProcessor.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.utilities;

import uk.ac.ed.ph.snuggletex.DOMOutputOptions;
import uk.ac.ed.ph.snuggletex.DOMPostProcessor;
import uk.ac.ed.ph.snuggletex.internal.util.XMLUtilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Convenient base for {@link DOMPostProcessor}s that might want to do interesting
 * things to MathML islands, leaving everything else unchanged.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public abstract class MathMLPostProcessor implements DOMPostProcessor {
    
    public final Document postProcessDOM(Document workDocument, final DOMOutputOptions options,
            StylesheetManager stylesheetManager) {
        Document resultDocument = XMLUtilities.createNSAwareDocumentBuilder().newDocument();
        new DocumentWalker(workDocument, resultDocument).run();
        return resultDocument;
    }
    
    /**
     * This method is called for each MathML <tt>math</tt> element discovered, in document order.
     * 
     * @param inputMathIsland MathML <tt>math</tt> element discovered
     * @param outputDocument resulting {@link Document} being built up
     * @param outputParentNode parent {@link Node} in the resulting {@link Document} that would
     *   own this MathML element if left unchanged.
     * @param mathmlCounter counter for this MathML element, starting at 0 for the first
     *   element in document order, 1 for the second, etc.
     */
    protected abstract void handleMathMLIsland(final Element inputMathIsland,
            Document outputDocument, Node outputParentNode, int mathmlCounter);
    
    /**
     * This inner class traverses a {@link Document} in document order, calling back on the
     * {@link MathMLPostProcessor#handleMathMLIsland(Element, Document, Node, int)} for
     * each MathML <tt>math</tt> element discovered.
     */
    private class DocumentWalker {
        
        private int mathmlCounter;
        private final Document inputDocument;
        private final Document outputDocument;
        private Node inputNode;
        private Node outputParentNode;
        
        public DocumentWalker(final Document inputDocument, final Document outputDocument) {
            this.inputDocument = inputDocument;
            this.outputDocument = outputDocument;
        }
        
        public void run() {
            outputParentNode = outputDocument;
            inputNode = inputDocument.getFirstChild();
            mathmlCounter = 0;
            while (inputNode!=null) {
                if (MathMLUtilities.isMathMLElement(inputNode, "math")) {
                    /* Let subclass decide what to do */
                    handleMathMLIsland((Element) inputNode, outputDocument, outputParentNode,
                            mathmlCounter++);
                }
                else {
                    /* Clone Node and add to outputDocument */
                    Node outputNode = outputDocument.adoptNode(inputNode.cloneNode(false));
                    outputParentNode.appendChild(outputNode);
                    if (inputNode.hasChildNodes()) {
                        /* Descend */
                        inputNode = inputNode.getFirstChild();
                        outputParentNode = outputNode;
                        continue;
                    }
                }
                /* Go to next sibling if available. Otherwise, go up and onto next sibling. Keep
                 * going in same way if required until we end up back at the top of the document */
                Node nextSibling = inputNode.getNextSibling();
                while (nextSibling==null) {
                    /* Up one level (if possible) then next sibling */
                    inputNode = inputNode.getParentNode();
                    if (inputNode==null) {
                        break;
                    }
                    nextSibling = inputNode.getNextSibling();
                    outputParentNode = outputParentNode.getParentNode();
                }
                inputNode = nextSibling;
            }
        }
    }
}