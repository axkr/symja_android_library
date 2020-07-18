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
 * This interface extends the MathMLPresentationToken interface for the MathML
 * string literal element ms.
 * 
 * 
 */
public interface MathMLStringLitElement extends MathMLPresentationToken {
    /**
     * A string giving the opening delimiter for the string literal;
     * represents the lquote attribute for the ms element, if specified.
     * 
     * @return value of the lquote attribute.
     */
    String getLquote();

    /**
     * setter for the lquote attribute.
     * 
     * @param lquote
     *            new value for lquote.
     * @see #getLquote()
     */
    void setLquote(String lquote);

    /**
     * A string giving the closing delimiter for the string literal;
     * represents the rquote attribute for the ms element, if specified.
     * 
     * @return value of the rquote attribute.
     */
    String getRquote();

    /**
     * setter for the rquote attribute.
     * 
     * @param rquote
     *            new value for rquote.
     * @see #getRquote()
     */
    void setRquote(String rquote);
};
