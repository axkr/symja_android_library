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
 * This interface extends the MathMLPresentationContainer interface for the
 * MathML fenced content element mfenced.
 * 
 * 
 */
public interface MathMLFencedElement extends MathMLPresentationContainer {
    /**
     * A string representing the opening-fence for the mfenced element, if
     * specified; this is the element's open attribute.
     * 
     * @return value of the open attribute.
     */
    String getOpen();

    /**
     * setter for the open attribute.
     * 
     * @param open
     *            new value for open.
     * @see #getOpen()
     */
    void setOpen(String open);

    /**
     * A string representing the closing-fence for the mfenced element, if
     * specified; this is the element's close attribute.
     * 
     * @return value of the close attribute.
     */
    String getClose();

    /**
     * setter for the close attribute.
     * 
     * @param close
     *            new value for close.
     * @see #getClose()
     */
    void setClose(String close);

    /**
     * A string representing any separating characters inside the mfenced
     * element, if specified; this is the element's separators attribute.
     * 
     * @return value of the separators attribute.
     */
    String getSeparators();

    /**
     * setter for the separators attribute.
     * 
     * @param separators
     *            new value for separators.
     * @see #getSeparators()
     */
    void setSeparators(String separators);
};
