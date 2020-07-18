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
 * The matrixrow element is the container element for the elements of a
 * matrix.
 * 
 * 
 */
public interface MathMLMatrixrowElement extends MathMLContentElement {
    /**
     * The number of entries in the row.
     * 
     * @return value of the nEntries attribute.
     */
    int getNEntries();

    /**
     * A convenience method to retrieve the contents of an entry by index.
     * 
     * @param index
     *            Position of the entry in the row. The first entry is
     *            numbered 1.
     * @return The MathMLContentElement element representing the index-th
     *         entry in the row.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the number
     *             of entries in the row.
     */
    MathMLContentElement getEntry(int index) throws DOMException;

    /**
     * A convenience method to insert an entry before the current index-th
     * entry of the row. If index is 0, newEntry is appended as the last
     * entry. Note that this method increases the size of the matrixrow.
     * 
     * @param newEntry
     *            The MathMLContentElement to be representing the new entry to
     *            be inserted into the row.
     * @param index
     *            The index before which newEntry is to be inserted in the
     *            row. The first entry is numbered 1.
     * @return The MathMLContentElement child of this MathMLMatrixrowElement
     *         representing newEntry in the DOM.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the number
     *             of entries in the row.
     */
    MathMLContentElement insertEntry(MathMLContentElement newEntry, int index)
            throws DOMException;

    /**
     * A convenience method to set the contents of the entry at position index
     * in the row to newEntry. If there is already a entry at the specified
     * index, it is replaced by the new entry.
     * 
     * @param newEntry
     *            The MathMLContentElement representing the element that is to
     *            be the index-th entry.
     * @param index
     *            The index of the entry that is to be set equal to newEntry.
     *            The first entry is numbered 1.
     * @return The MathMLContentElement child of this MathMLMatrixRowElement
     *         representing newEntry in the DOM.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than one more
     *             than the number of elements in the row.
     */
    MathMLContentElement setEntry(MathMLContentElement newEntry, int index)
            throws DOMException;

    /**
     * A convenience method to delete an entry. The deletion changes the
     * indices of the following entries.
     * 
     * @param index
     *            Position of the entry to be deleted in the row. The first
     *            entry is numbered 1.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the number
     *             of entries in the row.
     */
    void deleteEntry(int index) throws DOMException;

    /**
     * A convenience method to remove an entry from the row and return the
     * removed entry to the caller.
     * 
     * @param index
     *            Position of the entry to be removed from the row. The first
     *            entry is numbered 1.
     * @return The MathMLContentElement being removed from the row.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the number
     *             of entries in the row.
     */
    MathMLContentElement removeEntry(int index) throws DOMException;
};
