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

import org.w3c.dom.DOMException;

/*
 * Please note: This file was automatically generated from the source of the
 * MathML specification. Do not edit it. If there are errors or missing
 * elements, please correct the stylesheet instead.
 */

/**
 * This interface extends the MathMLPresentationElement interface for the
 * MathML subscript, superscript and subscript-superscript pair elements msub,
 * msup, and msubsup.
 * 
 * 
 */
public interface MathMLScriptElement extends MathMLPresentationElement {
    /**
     * A string representing the minimum amount to shift the baseline of the
     * subscript down, if specified; this is the element's subscriptshift
     * attribute. This must return null for an msup.
     * 
     * @return value of the subscriptshift attribute.
     */
    String getSubscriptshift();

    /**
     * setter for the subscriptshift attribute.
     * 
     * @param subscriptshift
     *            new value for subscriptshift.
     * @see #getSubscriptshift()
     */
    void setSubscriptshift(String subscriptshift);

    /**
     * A string representing the minimum amount to shift the baseline of the
     * superscript up, if specified; this is the element's superscriptshift
     * attribute. This must return null for a msub.
     * 
     * @return value of the superscriptshift attribute.
     */
    String getSuperscriptshift();

    /**
     * setter for the superscriptshift attribute.
     * 
     * @param superscriptshift
     *            new value for superscriptshift.
     * @see #getSuperscriptshift()
     */
    void setSuperscriptshift(String superscriptshift);

    /**
     * A MathMLElement representing the base of the script. This is the first
     * child of the element.
     * 
     * @return value of the base attribute.
     */
    MathMLElement getBase();

    /**
     * setter for the base attribute.
     * 
     * @param base
     *            new value for base.
     * @see #getBase()
     */
    void setBase(MathMLElement base);

    /**
     * A MathMLElement representing the subscript of the script. This is the
     * second child of a msub or msubsup; retrieval must return null for an
     * msup.
     * 
     * @return value of the subscript attribute.
     */
    MathMLElement getSubscript();

    /**
     * setter for the subscript attribute.
     * 
     * @param subscript
     *            new value for subscript.
     * @see #getSubscript()
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised when the element is a msup.
     */
    void setSubscript(MathMLElement subscript);

    /**
     * A MathMLElement representing the superscript of the script. This is the
     * second child of a msup or the third child of a msubsup; retrieval must
     * return null for an msub.
     * 
     * @return value of the superscript attribute.
     */
    MathMLElement getSuperscript();

    /**
     * setter for the superscript attribute.
     * 
     * @param superscript
     *            new value for superscript.
     * @see #getSuperscript()
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised when the element is a msub.
     */
    void setSuperscript(MathMLElement superscript);
};
