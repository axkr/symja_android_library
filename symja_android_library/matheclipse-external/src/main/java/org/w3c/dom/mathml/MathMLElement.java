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

import org.w3c.dom.Element;

/*
 * Please note: This file was automatically generated from the source of the
 * MathML specification. Do not edit it. If there are errors or missing
 * elements, please correct the stylesheet instead.
 */

/**
 * All MathML element interfaces derive from this object, which derives from
 * the basic DOM interface Element.
 * 
 */
public interface MathMLElement extends Element {
    /**
     * The class attribute of the element. See the discussion elsewhere in
     * this document of the class attribute; see also the HTML definition of
     * this attribute.
     * 
     * @return value of the className attribute.
     */
    String getClassName();

    /**
     * setter for the className attribute.
     * 
     * @param className
     *            new value for className.
     * @see #getClassName()
     */
    void setClassName(String className);

    /**
     * A string identifying the element's style attribute.
     * 
     * @return value of the mathElementStyle attribute.
     */
    String getMathElementStyle();

    /**
     * setter for the mathElementStyle attribute.
     * 
     * @param mathElementStyle
     *            new value for mathElementStyle.
     * @see #getMathElementStyle()
     */
    void setMathElementStyle(String mathElementStyle);

    /**
     * The element's identifier. See the discussion elsewhere in this document
     * of the id attribute; see also the HTML definition.
     * 
     * @return value of the id attribute.
     */
    String getId();

    /**
     * setter for the id attribute.
     * 
     * @param id
     *            new value for id.
     * @see #getId()
     */
    void setId(String id);

    /**
     * The xref attribute of the element. See the discussion elsewhere in this
     * document of the xref attribute.
     * 
     * @return value of the xref attribute.
     */
    String getXref();

    /**
     * setter for the xref attribute.
     * 
     * @param xref
     *            new value for xref.
     * @see #getXref()
     */
    void setXref(String xref);

    /**
     * The xlink:href attribute of the element. See the discussion elsewhere
     * in this document of the xlink:href attribute; see also the definition
     * of this attribute in the XLink specification.
     * 
     * @return value of the href attribute.
     */
    String getHref();

    /**
     * setter for the href attribute.
     * 
     * @param href
     *            new value for href.
     * @see #getHref()
     */
    void setHref(String href);

    /**
     * The MathMLMathElement corresponding to the nearest math element
     * ancestor of this element. Should be null if this element is a top-level
     * math element.
     * 
     * @return value of the ownerMathElement attribute.
     */
    MathMLMathElement getOwnerMathElement();
};
