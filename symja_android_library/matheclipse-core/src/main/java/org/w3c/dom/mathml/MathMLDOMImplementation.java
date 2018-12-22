/*
 * Copyright 2007 - 2007 JEuclid, http://jeuclid.sf.net
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.w3c.dom.mathml;

import org.w3c.dom.DOMImplementation;

/*
 * Please note: This file was automatically generated from the source of the
 * MathML specification. Do not edit it. If there are errors or missing
 * elements, please correct the stylesheet instead.
 */

/**
 * This interface extends the DOMImplementation interface by adding a method
 * to create a MathMLDocument.
 * 
 * 
 */
public interface MathMLDOMImplementation extends DOMImplementation {
    /**
     * Creates a MathMLDocument with a minimal tree containing only a
     * MathMLMathElement corresponding to a MathML math element. The
     * MathMLMathElement is empty, having no child elements or non-default
     * attributes; it is the root element of the document, and is the element
     * accessed via the documentElement attribute of the MathMLDocument. Note
     * that a MathMLDocument object should only be created for a stand-alone
     * MathML document.
     * 
     * @return The MathMLDocument created.
     */
    MathMLDocument createMathMLDocument();
};
