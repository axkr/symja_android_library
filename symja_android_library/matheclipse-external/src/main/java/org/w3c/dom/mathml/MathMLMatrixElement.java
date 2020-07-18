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
 * The matrix element is the container element for matrixrow elements.
 * 
 * 
 */
public interface MathMLMatrixElement extends MathMLContentElement {
    /**
     * The number of rows in the represented matrix.
     * 
     * @return value of the nrows attribute.
     */
    int getNrows();

    /**
     * The number of columns in the represented matrix.
     * 
     * @return value of the ncols attribute.
     */
    int getNcols();

    /**
     * The rows of the matrix, returned as a MathMLNodeList consisting of
     * MathMLMatrixrowElements.
     * 
     * @return value of the rows attribute.
     */
    MathMLNodeList getRows();

    /**
     * A convenience method to retrieve a specified row.
     * 
     * @param index
     *            Position of the row in the list of rows. The first row is
     *            numbered 1.
     * @return The MathMLMatrixrowElement representing the index-th row.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the number
     *             of rows in the matrix.
     */
    MathMLMatrixrowElement getRow(int index) throws DOMException;

    /**
     * A convenience method to insert a row before the row that is currently
     * the index-th row of this matrix. If index is 0, newRow is appended as
     * the last row of the matrix.
     * 
     * @param newRow
     *            MathMLMatrixrowElement to be inserted into the matrix.
     * @param index
     *            Unsigned integer giving the row position before which newRow
     *            is to be inserted. The first row is numbered 1.
     * @return The MathMLMatrixrowElement added. This is the new element
     *         within the DOM.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than one more
     *             than the number of rows in the matrix.
     *             HIERARCHY_REQUEST_ERR: Raised if the number of cells in
     *             newRow doesn't match the number of columns in the matrix.
     */
    MathMLMatrixrowElement insertRow(MathMLMatrixrowElement newRow, int index)
            throws DOMException;

    /**
     * A convenience method to set the value of the index-th child matrixrow
     * element of this element. If there is already a row at the specified
     * index, it is replaced by newRow.
     * 
     * @param newRow
     *            MathMLMatrixrowElement representing the matrixrow which is
     *            to become the index-th row of the matrix.
     * @param index
     *            Unsigned integer giving the row which is to be set to
     *            newRow. The first row is numbered 1.
     * @return The MathMLMatrixrowElement child of this MathMLMatrixrowElement
     *         representing newRow within the DOM.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the number
     *             of rows in the matrix. HIERARCHY_REQUEST_ERR: Raised if the
     *             number of cells in newRow doesn't match the number of
     *             columns in the matrix.
     */
    MathMLMatrixrowElement setRow(MathMLMatrixrowElement newRow, int index)
            throws DOMException;

    /**
     * A convenience method to delete a row. The deletion changes the indices
     * of the following rows.
     * 
     * @param index
     *            Position of the row to be deleted in the list of rows
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the number
     *             of rows in the matrix.
     */
    void deleteRow(int index) throws DOMException;

    /**
     * A convenience method to remove a row and return it to the caller. The
     * deletion changes the indices of the following rows.
     * 
     * @param index
     *            Position of the row to be removed in the list of rows. The
     *            first row is numbered 1.
     * @return The MathMLMatrixrowElement being removed.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the number
     *             of rows in the matrix.
     */
    MathMLMatrixrowElement removeRow(int index) throws DOMException;
};
