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
 * This interface extends the MathMLTableRowElement interface to represent the
 * mlabeledtr element . Note that the presence of a label causes the indexth
 * child node to differ from the index-th cell!
 * 
 * 
 */
public interface MathMLLabeledRowElement extends MathMLTableRowElement {
    /**
     * A MathMLElement representing the label of this row. Note that
     * retrieving this should have the same effect as a call to
     * Node::getfirstChild(), while setting it should have the same effect as
     * Node::replaceChild(Node::getfirstChild()).
     * 
     * @return value of the label attribute.
     */
    MathMLElement getLabel();

    /**
     * setter for the label attribute.
     * 
     * @param label
     *            new value for label.
     * @see #getLabel()
     */
    void setLabel(MathMLElement label);
};
