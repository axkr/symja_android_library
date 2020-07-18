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
 * MathML fraction element mfrac.
 * 
 * 
 */
public interface MathMLFractionElement extends MathMLPresentationElement {
    /**
     * A string representing the linethickness attribute of the mfrac, if
     * specified.
     * 
     * @return value of the linethickness attribute.
     */
    String getLinethickness();

    /**
     * setter for the linethickness attribute.
     * 
     * @param linethickness
     *            new value for linethickness.
     * @see #getLinethickness()
     */
    void setLinethickness(String linethickness);

    /**
     * One of the strings left, center and right. Represents the numalign
     * attribute of the mfrac, if specified.
     * 
     * @return value of the numalign attribute.
     */
    String getNumalign();

    /**
     * setter for the numalign attribute.
     * 
     * @param numalign
     *            new value for numalign.
     * @see #getNumalign()
     */
    void setNumalign(String numalign);

    /**
     * One of the strings left, center and right. Represents the denomalign
     * attribute of the mfrac, if specified.
     * 
     * @return value of the denomalign attribute.
     */
    String getDenomalign();

    /**
     * setter for the denomalign attribute.
     * 
     * @param denomalign
     *            new value for denomalign.
     * @see #getDenomalign()
     */
    void setDenomalign(String denomalign);

    /**
     * One of the strings true and false. Represents the bevelled attribute of
     * the mfrac, if specified.
     * 
     * @return value of the bevelled attribute.
     */
    String getBevelled();

    /**
     * setter for the bevelled attribute.
     * 
     * @param bevelled
     *            new value for bevelled.
     * @see #getBevelled()
     */
    void setBevelled(String bevelled);

    /**
     * The first child MathMLElement of the MathMLFractionElement; represents
     * the numerator of the represented fraction.
     * 
     * @return value of the numerator attribute.
     */
    MathMLElement getNumerator();

    /**
     * setter for the numerator attribute.
     * 
     * @param numerator
     *            new value for numerator.
     * @see #getNumerator()
     */
    void setNumerator(MathMLElement numerator);

    /**
     * The second child MathMLElement of the MathMLFractionElement; represents
     * the denominator of the represented fraction.
     * 
     * @return value of the denominator attribute.
     */
    MathMLElement getDenominator();

    /**
     * setter for the denominator attribute.
     * 
     * @param denominator
     *            new value for denominator.
     * @see #getDenominator()
     */
    void setDenominator(MathMLElement denominator);
};
