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
 * This interface represents the top-level MathML math element. It may become
 * useful for interfacing between the Document Object Model objects encoding
 * an enclosing document and the MathML DOM elements that are its children. It
 * could also be used for some purposes as a MathML DOM surrogate for a
 * Document object. For instance, MathML-specific factory methods could be
 * placed here, as could methods for creating MathML-specific Iterators or
 * TreeWalkers. However, this functionality is as yet undefined.
 */
public interface MathMLMathElement extends MathMLElement, MathMLContainer {
    /**
     * Represents the macros attribute of the math element. See .
     * 
     * @return value of the macros attribute.
     */
    String getMacros();

    /**
     * setter for the macros attribute.
     * 
     * @param macros
     *            new value for macros.
     * @see #getMacros()
     */
    void setMacros(String macros);

    /**
     * Represents the display attribute of the math element. This value is
     * either block or inline. See .
     * 
     * @return value of the display attribute.
     */
    String getDisplay();

    /**
     * setter for the display attribute.
     * 
     * @param display
     *            new value for display.
     * @see #getDisplay()
     */
    void setDisplay(String display);
};
