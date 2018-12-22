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
 * MathML underscript, overscript and overscript-underscript pair elements
 * munder, mover and munderover.
 * 
 * 
 */
public interface MathMLUnderOverElement extends MathMLPresentationElement {
    /**
     * Either true or false if present; a string controlling whether
     * underscript is drawn as an accent or as a limit, if specified; this is
     * the element's accentunder attribute. This must return null for an
     * mover.
     * 
     * @return value of the accentunder attribute.
     */
    String getAccentunder();

    /**
     * setter for the accentunder attribute.
     * 
     * @param accentunder
     *            new value for accentunder.
     * @see #getAccentunder()
     */
    void setAccentunder(String accentunder);

    /**
     * Either true or false if present; a string controlling whether
     * overscript is drawn as an accent or as a limit, if specified; this is
     * the element's accent attribute. This must return null for an munder.
     * 
     * @return value of the accent attribute.
     */
    String getAccent();

    /**
     * setter for the accent attribute.
     * 
     * @param accent
     *            new value for accent.
     * @see #getAccent()
     */
    void setAccent(String accent);

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
     * A MathMLElement representing the underscript of the script. This is the
     * second child of a munder or munderover; retrieval must return null for
     * an mover.
     * 
     * @return value of the underscript attribute.
     */
    MathMLElement getUnderscript();

    /**
     * setter for the underscript attribute.
     * 
     * @param underscript
     *            new value for underscript.
     * @see #getUnderscript()
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised when the element is a mover.
     */
    void setUnderscript(MathMLElement underscript);

    /**
     * A MathMLElement representing the overscript of the script. This is the
     * second child of a mover or the third child of a munderover; retrieval
     * must return null for an munder.
     * 
     * @return value of the overscript attribute.
     */
    MathMLElement getOverscript();

    /**
     * setter for the overscript attribute.
     * 
     * @param overscript
     *            new value for overscript.
     * @see #getOverscript()
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised when the element is a munder.
     */
    void setOverscript(MathMLElement overscript);
};
