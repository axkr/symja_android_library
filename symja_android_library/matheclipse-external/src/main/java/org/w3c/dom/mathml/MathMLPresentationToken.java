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

/*
 * Please note: This file was automatically generated from the source of the
 * MathML specification. Do not edit it. If there are errors or missing
 * elements, please correct the stylesheet instead.
 */

/**
 * This interface extends the MathMLElement interface to include access for
 * attributes specific to text presentation. It serves as the base class for
 * all MathML presentation token elements. Access to the body of the element
 * is via the nodeValue attribute inherited from Node. Elements that expose
 * only the core presentation token attributes are directly supported by this
 * object. These elements are:
 * 
 * mi
 * 
 * 
 * mn
 * 
 * 
 * mtext
 * 
 * 
 * 
 * 
 * 
 */
public interface MathMLPresentationToken extends MathMLPresentationElement {
    /**
     * The mathvariant attribute for the element, if specified. One of the
     * values normal, bold, italic, bold-italic, double-struck, bold-fraktur,
     * script, bold-script, fraktur, sans-serif, bold-sans-serif,
     * sans-serif-italic, sans-serif-bold-italic, or monospace.
     * 
     * @return value of the mathvariant attribute.
     */
    String getMathvariant();

    /**
     * setter for the mathvariant attribute.
     * 
     * @param mathvariant
     *            new value for mathvariant.
     * @see #getMathvariant()
     */
    void setMathvariant(String mathvariant);

    /**
     * The mathsize attribute for the element, if specified. Either small,
     * normal or big, or of the form number v-unit.
     * 
     * @return value of the mathsize attribute.
     */
    String getMathsize();

    /**
     * setter for the mathsize attribute.
     * 
     * @param mathsize
     *            new value for mathsize.
     * @see #getMathsize()
     */
    void setMathsize(String mathsize);

    /**
     * The mathcolor attribute for the element, if specified. The DOMString
     * returned should be in one of the forms "#rgb" or "#rrggbb", or should
     * be an html-color-name, as specified in .
     * 
     * @return value of the mathcolor attribute.
     */
    String getMathcolor();

    /**
     * setter for the mathcolor attribute.
     * 
     * @param mathcolor
     *            new value for mathcolor.
     * @see #getMathcolor()
     */
    void setMathcolor(String mathcolor);

    /**
     * The mathbackground attribute for the element, if specified. The
     * DOMString returned should be in one of the forms "#rgb" or "#rrggbb",
     * or an html-color-name, as specified in , or the keyword "transparent".
     * 
     * @return value of the mathbackground attribute.
     */
    String getMathbackground();

    /**
     * setter for the mathbackground attribute.
     * 
     * @param mathbackground
     *            new value for mathbackground.
     * @see #getMathbackground()
     */
    void setMathbackground(String mathbackground);

    /**
     * Returns the child Nodes of the element. These should consist only of
     * Text nodes, MathMLGlyphElements, and MathMLAlignMarkElements. Should
     * behave the same as the base class's Node::childNodes attribute;
     * however, it is provided here for clarity.
     * 
     * @return value of the contents attribute.
     */
    MathMLNodeList getContents();
};
