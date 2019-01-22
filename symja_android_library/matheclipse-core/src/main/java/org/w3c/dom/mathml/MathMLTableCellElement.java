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
 * MathML table or matrix cell element mtd.
 * 
 * 
 */
public interface MathMLTableCellElement extends MathMLPresentationContainer {
    /**
     * A string representing a positive integer that specifies the number of
     * rows spanned by this cell. The default is 1.
     * 
     * @return value of the rowspan attribute.
     */
    String getRowspan();

    /**
     * setter for the rowspan attribute.
     * 
     * @param rowspan
     *            new value for rowspan.
     * @see #getRowspan()
     */
    void setRowspan(String rowspan);

    /**
     * A string representing a positive integer that specifies the number of
     * columns spanned by this cell. The default is 1.
     * 
     * @return value of the columnspan attribute.
     */
    String getColumnspan();

    /**
     * setter for the columnspan attribute.
     * 
     * @param columnspan
     *            new value for columnspan.
     * @see #getColumnspan()
     */
    void setColumnspan(String columnspan);

    /**
     * A string specifying an override of the inherited vertical alignment of
     * this cell within the table row. Allowed values are top, bottom, center,
     * baseline, and axis.
     * 
     * @return value of the rowalign attribute.
     */
    String getRowalign();

    /**
     * setter for the rowalign attribute.
     * 
     * @param rowalign
     *            new value for rowalign.
     * @see #getRowalign()
     */
    void setRowalign(String rowalign);

    /**
     * A string specifying an override of the inherited horizontal alignment
     * of this cell within the table column. Allowed values are left, center,
     * and right.
     * 
     * @return value of the columnalign attribute.
     */
    String getColumnalign();

    /**
     * setter for the columnalign attribute.
     * 
     * @param columnalign
     *            new value for columnalign.
     * @see #getColumnalign()
     */
    void setColumnalign(String columnalign);

    /**
     * A string specifying how the alignment groups within the cell are to be
     * aligned with those in cells above or below this cell. The string
     * consists of a space-separated sequence of specifiers, each of which can
     * have the following values: left, right, center, or decimalpoint.
     * 
     * @return value of the groupalign attribute.
     */
    String getGroupalign();

    /**
     * setter for the groupalign attribute.
     * 
     * @param groupalign
     *            new value for groupalign.
     * @see #getGroupalign()
     */
    void setGroupalign(String groupalign);

    /**
     * A string with the values true or false indicating whether the cell
     * contains align groups.
     * 
     * @return value of the hasaligngroups attribute.
     */
    boolean getHasaligngroups();

    /**
     * A string representing the integer index (1-based?) of the cell in its
     * containing row. [What about spanning cells? How do these affect this
     * value?]
     * 
     * @return value of the cellindex attribute.
     */
    String getCellindex();
};
