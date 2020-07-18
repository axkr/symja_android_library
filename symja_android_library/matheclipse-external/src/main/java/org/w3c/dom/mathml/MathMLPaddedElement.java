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
 * This interface extends the MathMLElement interface for the MathML spacing
 * adjustment element mpadded.
 * 
 * 
 */
public interface MathMLPaddedElement extends MathMLPresentationContainer {
    /**
     * A string representing the total width of the mpadded element, if
     * specified. See also the discussion of this attribute.
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
     * A string representing the lspace attribute - the additional space to
     * the left - of the mpadded element, if specified. See also the
     * discussion of this attribute.
     * 
     * @return value of the lspace attribute.
     */
    String getLspace();

    /**
     * setter for the lspace attribute.
     * 
     * @param lspace
     *            new value for lspace.
     * @see #getLspace()
     */
    void setLspace(String lspace);

    /**
     * A string representing the height above the baseline of the mpadded
     * element, if specified. See also the discussion of this attribute.
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
     * A string representing the depth beneath the baseline of the mpadded
     * element, if specified. See also the discussion of this attribute.
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
};
