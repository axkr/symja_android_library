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
 * MathML enlivening expression element maction.
 * 
 * 
 */
public interface MathMLActionElement extends MathMLPresentationContainer {
    /**
     * A string specifying the action. Possible values include toggle,
     * statusline, tooltip, and highlight, and menu.
     * 
     * @return value of the actiontype attribute.
     */
    String getActiontype();

    /**
     * setter for the actiontype attribute.
     * 
     * @param actiontype
     *            new value for actiontype.
     * @see #getActiontype()
     */
    void setActiontype(String actiontype);

    /**
     * A string specifying an integer that selects the current subject of the
     * action.
     * 
     * @return value of the selection attribute.
     */
    String getSelection();

    /**
     * setter for the selection attribute.
     * 
     * @param selection
     *            new value for selection.
     * @see #getSelection()
     */
    void setSelection(String selection);
};
