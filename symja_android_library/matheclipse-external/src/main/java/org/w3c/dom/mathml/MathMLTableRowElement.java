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
 * MathML table or matrix row element mtr.
 * 
 * 
 */
public interface MathMLTableRowElement extends MathMLPresentationElement {
    /**
     * A string representing an override of the row alignment specified in the
     * containing mtable. Allowed values are top, bottom, center, baseline,
     * and axis.
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
     * A string representing an override of the column alignment specified in
     * the containing mtable. Allowed values are left, center, and right.
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
     * A MathMLNodeList consisting of the cells of the row. Note that this
     * does not include the label if this is a MathMLLabeledRowElement!
     * 
     * @return value of the cells attribute.
     */
    MathMLNodeList getCells();

    /**
     * A convenience method to insert a new (empty) cell in the row.
     * 
     * @param index
     *            Index of the cell before which the new cell is to be
     *            inserted, where the first cell is numbered 0. If index is
     *            equal to the current number of cells, the new cell is
     *            appended as the last cell of the row. Note that the index
     *            will differ from the index of the corresponding Node in the
     *            collection returned by Node::childNodes if this is a
     *            MathMLLabeledRowElement!
     * @return Returns the MathMLTableCellElement representing the mtd element
     *         being inserted.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the current
     *             number of cells of this mtr element.
     */
    MathMLTableCellElement insertEmptyCell(int index) throws DOMException;

    /**
     * A convenience method to insert a new cell in the row.
     * 
     * @param newCell
     *            A MathMLTableCellElement representing the new cell (mtd
     *            element) to be inserted.
     * @param index
     *            Index of the cell before which the new cell is to be
     *            inserted, where the first cell is numbered 0. If index
     *            equals the current number of cells, the new cell is appended
     *            as the last cell of the row. Note that the index will differ
     *            from the index of the corresponding Node in Node::childNodes
     *            if this is a MathMLLabeledRowElement!
     * @return The MathMLTableCellElement representing the mtd element being
     *         inserted.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the current
     *             number of cells of this mtr element.
     */
    MathMLTableCellElement insertCell(MathMLTableCellElement newCell,
            int index) throws DOMException;

    /**
     * A convenience method to set the value of a cell in the row to newCell.
     * If index is equal to the current number of cells, newCell is appended
     * as the last cell in the row.
     * 
     * @param newCell
     *            A MathMLTableCellElement representing the cell (mtd element)
     *            that is to be inserted.
     * @param index
     *            Index of the cell that is to be replaced by the new cell,
     *            where the first cell is numbered 0. Note that the index will
     *            differ from the index of the corresponding Node in the
     *            collection returned by Node::childNodes if this is a
     *            MathMLLabeledRowElement!
     * @return The MathMLTableCellElement child of this MathMLTableRowElement
     *         representing the new mtd element.
     */
    MathMLTableCellElement setCell(MathMLTableCellElement newCell, int index);

    /**
     * A convenience method to delete a cell in the row.
     * 
     * @param index
     *            Index of cell to be deleted. Note that the count will differ
     *            from the index-th child node if this is a
     *            MathMLLabeledRowElement!
     */
    void deleteCell(int index);
};
