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
 * MathML space element mspace. Note that this is not derived from
 * MathMLPresentationToken, despite the fact that mspace is classified as a
 * token element, since it does not carry the attributes declared for
 * MathMLPresentationToken.
 * 
 * 
 */
public interface MathMLSpaceElement extends MathMLPresentationElement {
    /**
     * A string of the form number h-unit; represents the width attribute for
     * the mspace element, if specified.
     * 
     * @return value of the width attribute.
     */
    String getWidth();

    /**
     * setter for the width attribute.
     * 
     * @param width
     *            new value for width.
     * @see #getWidth()
     */
    void setWidth(String width);

    /**
     * A string of the form number v-unit; represents the height attribute for
     * the mspace element, if specified.
     * 
     * @return value of the height attribute.
     */
    String getHeight();

    /**
     * setter for the height attribute.
     * 
     * @param height
     *            new value for height.
     * @see #getHeight()
     */
    void setHeight(String height);

    /**
     * A string of the form number v-unit; represents the depth attribute for
     * the mspace element, if specified.
     * 
     * @return value of the depth attribute.
     */
    String getDepth();

    /**
     * setter for the depth attribute.
     * 
     * @param depth
     *            new value for depth.
     * @see #getDepth()
     */
    void setDepth(String depth);

    /**
     * One of the strings auto, newline, indentingnewline, nobreak, goodbreak
     * and badbreak. This attribute gives a linebreaking hint to the renderer.
     * 
     * @return value of the linebreak attribute.
     */
    String getLinebreak();

    /**
     * setter for the linebreak attribute.
     * 
     * @param linebreak
     *            new value for linebreak.
     * @see #getLinebreak()
     */
    void setLinebreak(String linebreak);
};
