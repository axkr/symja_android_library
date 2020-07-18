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
 * This interface extends the MathMLPresentationElement interface for the
 * MathML radical and square root elements mroot and msqrt.
 * 
 * 
 */
public interface MathMLRadicalElement extends MathMLPresentationElement {
    /**
     * The first child MathMLElement of the MathMLRadicalElement; represents
     * the base of the represented radical.
     * 
     * @return value of the radicand attribute.
     */
    MathMLElement getRadicand();

    /**
     * setter for the radicand attribute.
     * 
     * @param radicand
     *            new value for radicand.
     * @see #getRadicand()
     */
    void setRadicand(MathMLElement radicand);

    /**
     * The second child MathMLElement of the MathMLRadicalElement; represents
     * the index of the represented radical. This must be null for msqrt
     * elements.
     * 
     * @return value of the index attribute.
     */
    MathMLElement getIndex();

    /**
     * setter for the index attribute.
     * 
     * @param index
     *            new value for index.
     * @see #getIndex()
     */
    void setIndex(MathMLElement index);
};
