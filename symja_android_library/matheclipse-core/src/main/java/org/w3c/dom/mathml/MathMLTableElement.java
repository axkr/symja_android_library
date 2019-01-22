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

import org.w3c.dom.DOMException;

/*
 * Please note: This file was automatically generated from the source of the
 * MathML specification. Do not edit it. If there are errors or missing
 * elements, please correct the stylesheet instead.
 */

/**
 * This interface extends the MathMLPresentationElement interface for the
 * MathML table or matrix element mtable.
 * 
 * 
 */
public interface MathMLTableElement extends MathMLPresentationElement {
    /**
     * A string representing the vertical alignment of the table with the
     * adjacent text. Allowed values are (top | bottom | center | baseline |
     * axis)[rownumber], where rownumber is between 1 and n (for a table with
     * n rows) or -1 and -n.
     * 
     * @return value of the align attribute.
     */
    String getAlign();

    /**
     * setter for the align attribute.
     * 
     * @param align
     *            new value for align.
     * @see #getAlign()
     */
    void setAlign(String align);

    /**
     * A string representing the alignment of entries in each row, consisting
     * of a space-separated sequence of alignment specifiers, each of which
     * can have the following values: top, bottom, center, baseline, or axis.
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
     * A string representing the alignment of entries in each column,
     * consisting of a space-separated sequence of alignment specifiers, each
     * of which can have the following values: left, center, or right.
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
     * A string specifying how the alignment groups within the cells of each
     * row are to be aligned with the corresponding items above or below them
     * in the same column. The string consists of a sequence of braced group
     * alignment lists. Each group alignment list is a space-separated
     * sequence, each of which can have the following values: left, right,
     * center, or decimalpoint.
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
     * A string consisting of the values true or false indicating, for each
     * column, whether it can be used as an alignment scope.
     * 
     * @return value of the alignmentscope attribute.
     */
    String getAlignmentscope();

    /**
     * setter for the alignmentscope attribute.
     * 
     * @param alignmentscope
     *            new value for alignmentscope.
     * @see #getAlignmentscope()
     */
    void setAlignmentscope(String alignmentscope);

    /**
     * A string consisting of a space-separated sequence of specifiers, each
     * of which can have one of the following forms: auto, number h-unit,
     * namedspace, or fit. (A value of the form namedspace is one of
     * veryverythinmathspace, verythinmathspace, thinmathspace,
     * mediummathspace, thickmathspace, verythickmathspace, or
     * veryverythickmathspace.) This represents the element's columnwidth
     * attribute.
     * 
     * @return value of the columnwidth attribute.
     */
    String getColumnwidth();

    /**
     * setter for the columnwidth attribute.
     * 
     * @param columnwidth
     *            new value for columnwidth.
     * @see #getColumnwidth()
     */
    void setColumnwidth(String columnwidth);

    /**
     * A string that is either of the form number h-unit or is the string
     * auto. This represents the element's width attribute.
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
     * A string consisting of a space-separated sequence of specifiers of the
     * form number v-unit representing the space to be added between rows.
     * 
     * @return value of the rowspacing attribute.
     */
    String getRowspacing();

    /**
     * setter for the rowspacing attribute.
     * 
     * @param rowspacing
     *            new value for rowspacing.
     * @see #getRowspacing()
     */
    void setRowspacing(String rowspacing);

    /**
     * A string consisting of a space-separated sequence of specifiers of the
     * form number h-unit representing the space to be added between columns.
     * 
     * @return value of the columnspacing attribute.
     */
    String getColumnspacing();

    /**
     * setter for the columnspacing attribute.
     * 
     * @param columnspacing
     *            new value for columnspacing.
     * @see #getColumnspacing()
     */
    void setColumnspacing(String columnspacing);

    /**
     * A string specifying whether and what kind of lines should be added
     * between each row. The string consists of a space-separated sequence of
     * specifiers, each of which can have the following values: none, solid,
     * or dashed.
     * 
     * @return value of the rowlines attribute.
     */
    String getRowlines();

    /**
     * setter for the rowlines attribute.
     * 
     * @param rowlines
     *            new value for rowlines.
     * @see #getRowlines()
     */
    void setRowlines(String rowlines);

    /**
     * A string specifying whether and what kind of lines should be added
     * between each column. The string consists of a space-separated sequence
     * of specifiers, each of which can have the following values: none,
     * solid, or dashed.
     * 
     * @return value of the columnlines attribute.
     */
    String getColumnlines();

    /**
     * setter for the columnlines attribute.
     * 
     * @param columnlines
     *            new value for columnlines.
     * @see #getColumnlines()
     */
    void setColumnlines(String columnlines);

    /**
     * A string specifying a frame around the table. Allowed values are (none |
     * solid | dashed).
     * 
     * @return value of the frame attribute.
     */
    String getFrame();

    /**
     * setter for the frame attribute.
     * 
     * @param frame
     *            new value for frame.
     * @see #getFrame()
     */
    void setFrame(String frame);

    /**
     * A string of the form number h-unit number v-unit specifying the spacing
     * between table and its frame.
     * 
     * @return value of the framespacing attribute.
     */
    String getFramespacing();

    /**
     * setter for the framespacing attribute.
     * 
     * @param framespacing
     *            new value for framespacing.
     * @see #getFramespacing()
     */
    void setFramespacing(String framespacing);

    /**
     * A string with the values true or false.
     * 
     * @return value of the equalrows attribute.
     */
    String getEqualrows();

    /**
     * setter for the equalrows attribute.
     * 
     * @param equalrows
     *            new value for equalrows.
     * @see #getEqualrows()
     */
    void setEqualrows(String equalrows);

    /**
     * A string with the values true or false.
     * 
     * @return value of the equalcolumns attribute.
     */
    String getEqualcolumns();

    /**
     * setter for the equalcolumns attribute.
     * 
     * @param equalcolumns
     *            new value for equalcolumns.
     * @see #getEqualcolumns()
     */
    void setEqualcolumns(String equalcolumns);

    /**
     * A string with the values true or false.
     * 
     * @return value of the displaystyle attribute.
     */
    String getDisplaystyle();

    /**
     * setter for the displaystyle attribute.
     * 
     * @param displaystyle
     *            new value for displaystyle.
     * @see #getDisplaystyle()
     */
    void setDisplaystyle(String displaystyle);

    /**
     * A string with the values left, right, leftoverlap, or rightoverlap.
     * 
     * @return value of the side attribute.
     */
    String getSide();

    /**
     * setter for the side attribute.
     * 
     * @param side
     *            new value for side.
     * @see #getSide()
     */
    void setSide(String side);

    /**
     * A string of the form number h-unit, specifying the minimum space
     * between a label and the adjacent entry in the labeled row.
     * 
     * @return value of the minlabelspacing attribute.
     */
    String getMinlabelspacing();

    /**
     * setter for the minlabelspacing attribute.
     * 
     * @param minlabelspacing
     *            new value for minlabelspacing.
     * @see #getMinlabelspacing()
     */
    void setMinlabelspacing(String minlabelspacing);

    /**
     * A MathMLNodeList consisting of MathMLTableRowElements and
     * MathMLLabeledRowElements representing the rows of the table. This is a
     * live object.
     * 
     * @return value of the rows attribute.
     */
    MathMLNodeList getRows();

    /**
     * A convenience method to insert a new (empty) row (mtr) in the table
     * before the current index-th row. If index is less than 0, the new row
     * is inserted before the -index-th row counting up from the current last
     * row; if index is equal to the current number of rows, the new row is
     * appended as the last row.
     * 
     * @param index
     *            Position before which to insert the new row, where 0
     *            represents the first row. Negative numbers are used to count
     *            backwards from the last row.
     * @return Returns the MathMLTableRowElement child of this
     *         MathMLTableElement that represents the new mtr element being
     *         inserted.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the current
     *             number of rows of this mtable element or less than minus
     *             this number.
     */
    MathMLTableRowElement insertEmptyRow(long index) throws DOMException;

    /**
     * A convenience method to insert a new (empty) labeled row (mlabeledtr)
     * in the table before the current index-th row. If index is less than 0,
     * the new row is inserted before the -index-th row counting up from the
     * current last row; if index is equal to the current number of rows, the
     * new row is appended as the last row.
     * 
     * @param index
     *            Position before which to insert the new row, where 0
     *            represents the first row. Negative numbers are used to count
     *            backwards from the last row.
     * @return Returns the MathMLLabeledRowElement child of this
     *         MathMLTableElement representing the mtr element being inserted.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the current
     *             number of rows of this mtable element or less than minus
     *             this number.
     */
    MathMLLabeledRowElement insertEmptyLabeledRow(long index)
            throws DOMException;

    /**
     * A convenience method to retrieve the index-th row from the table. If
     * index is less than 0, the -index-th row from the bottom of the table is
     * retrieved. (So, for instance, if index is -2, the next-to-last row is
     * retrieved.) If index is not a valid value (i.e. is greater than or
     * equal to the number of rows, or is less than minus the number of rows),
     * a null MathMLTableRowElement is returned.
     * 
     * @param index
     *            Index of the row to be returned, where 0 represents the
     *            first row. Negative numbers are used to count backwards from
     *            the last row.
     * @return Returns the MathMLTableRowElement representing the index-th row
     *         of the table.
     */
    MathMLTableRowElement getRow(long index);

    /**
     * A convenience method to insert the new row or labeled row (mtr or
     * mlabeledtr) represented by newRow in the table before the current
     * index-th row. If index is equal to the current number of rows, newRow
     * is appended as the last row in the table. If index is less than 0, the
     * new row is inserted before the -index-th row from the bottom of the
     * table. (So, for instance, if index is -2, the new row is inserted
     * before the next-to-last current row.)
     * 
     * @param index
     *            Index before which to insert newRow, where 0 represents the
     *            first row. Negative numbers are used to count backwards from
     *            the current last row.
     * @param newRow
     *            A MathMLTableRowElement or MathMLLabeledRowElement
     *            representing the row to be inserted.
     * @return The MathMLTableRowElement or MathMLLabeledRowElement child of
     *         this MathMLTableElement representing the mtr element being
     *         inserted.
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised if newRow is not a
     *             MathMLTableRowElement or MathMLLabeledRowElement.
     *             INDEX_SIZE_ERR: Raised if index is greater than the current
     *             number of rows or less than minus the current number of
     *             rows of this mtable element.
     */
    MathMLTableRowElement insertRow(long index, MathMLTableRowElement newRow)
            throws DOMException;

    /**
     * A method to set the value of the row in the table at the specified
     * index to the mtr or mlabeledtr represented by newRow. If index is less
     * than 0, the -index-th row counting up from the last is replaced by
     * newRow; if index is one more than the current number of rows, the new
     * row is appended as the last row in the table.
     * 
     * @param index
     *            Index of the row to be set to newRow, where 0 represents the
     *            first row. Negative numbers are used to count backwards from
     *            the last row.
     * @param newRow
     *            A MathMLTableRowElement representing the row that is to be
     *            the new index-th row.
     * @return Returns the MathMLTableRowElement or MathMLLabeledRowElement
     *         child of this element that represents the new row in the DOM.
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised if newRow is not a
     *             MathMLTableRowElement or MathMLLabeledRowElement.
     *             INDEX_SIZE_ERR: Raised if index is greater than the current
     *             number of rows of this mtable element or less than minus
     *             this number.
     */
    MathMLTableRowElement setRow(long index, MathMLTableRowElement newRow)
            throws DOMException;

    /**
     * A convenience method to delete the row of the table at the specified
     * index. If index is less than 0, the -index-th row from the bottom of
     * the table is deleted. (So, for instance, if index is -2, the
     * next-to-last row is deleted.)
     * 
     * @param index
     *            Index of row to be deleted, where 0 represents the first
     *            row.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than or equal to
     *             the current number of rows of this mtable element or less
     *             than minus this number.
     */
    void deleteRow(long index) throws DOMException;

    /**
     * A convenience method to delete the row of the table at the specified
     * index and return it to the caller. If index is less than 0, the
     * -index-th row from the bottom of the table is deleted. (So, for
     * instance, if index is -2, the next-to-last row is deleted.)
     * 
     * @param index
     *            Index of row to be removed, where 0 represents the first
     *            row.
     * @return A MathMLTableRowElement representing the row being deleted.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than or equal to
     *             the number of rows of this mtable element or less than
     *             minus this number.
     */
    MathMLTableRowElement removeRow(long index) throws DOMException;
};
