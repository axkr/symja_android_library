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
 * This interface supports the mglyph element .
 * 
 * 
 */
public interface MathMLGlyphElement extends MathMLPresentationElement {
    /**
     * A string giving an alternate name for the character. Represents the
     * mglyph's alt attribute.
     * 
     * @return value of the alt attribute.
     */
    String getAlt();

    /**
     * setter for the alt attribute.
     * 
     * @param alt
     *            new value for alt.
     * @see #getAlt()
     */
    void setAlt(String alt);

    /**
     * A string representing the font family.
     * 
     * @return value of the fontfamily attribute.
     */
    String getFontfamily();

    /**
     * setter for the fontfamily attribute.
     * 
     * @param fontfamily
     *            new value for fontfamily.
     * @see #getFontfamily()
     */
    void setFontfamily(String fontfamily);

    /**
     * An unsigned integer giving the glyph's position within the font.
     * 
     * @return value of the index attribute.
     */
    int getIndex();

    /**
     * setter for the index attribute.
     * 
     * @param index
     *            new value for index.
     * @see #getIndex()
     */
    void setIndex(int index);
};
