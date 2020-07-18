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

import org.w3c.dom.Node;

/*
 * Please note: This file was automatically generated from the source of the
 * MathML specification. Do not edit it. If there are errors or missing
 * elements, please correct the stylesheet instead.
 */

/**
 * This is the interface from which the interfaces representing the MathML
 * Content token elements (ci, cn and csymbol) are derived. These elements may
 * contain MathML Presentation elements, Text nodes, or a combination of both.
 * Thus the getArgument and insertArgument methods have been provided to deal
 * with this distinction between these elements and other MathML Content
 * elements.
 * 
 * 
 */
public interface MathMLContentToken extends MathMLContentElement {
    /**
     * The arguments of this element, returned as a MathMLNodeList. Note that
     * this is not necessarily the same as Node::childNodes, particularly in
     * the case of the cn element. The reason is that the sep elements that
     * are used to separate the arguments of a cn are not returned.
     * 
     * @return value of the arguments attribute.
     */
    MathMLNodeList getArguments();

    /**
     * A URI pointing to a semantic definition for this content element. Note
     * that there is no stipulation about the form this definition may take!
     * 
     * @return value of the definitionURL attribute.
     */
    String getDefinitionURL();

    /**
     * setter for the definitionURL attribute.
     * 
     * @param definitionURL
     *            new value for definitionURL.
     * @see #getDefinitionURL()
     */
    void setDefinitionURL(String definitionURL);

    /**
     * A string describing the syntax in which the definition located at
     * definitionURL is given.
     * 
     * @return value of the encoding attribute.
     */
    String getEncoding();

    /**
     * setter for the encoding attribute.
     * 
     * @param encoding
     *            new value for encoding.
     * @see #getEncoding()
     */
    void setEncoding(String encoding);

    /**
     * A convenience method to retrieve the child argument at the position
     * referenced by index. Note that this is not necessarily the same as the
     * index-th child Node of this Element; in particular, sep elements will
     * not be counted.
     * 
     * @param index
     *            Position of desired argument in the list of arguments. The
     *            first argument is numbered 1.
     * @return The Node retrieved.
     */
    Node getArgument(int index);

    /**
     * A convenience method to insert newArgument before the current index-th
     * argument child of this element. If index is 0, newArgument is appended
     * as the last argument.
     * 
     * @param newArgument
     *            Node to be inserted as the index-th argument. This will
     *            either be a MathMLElement or a Text node.
     * @param index
     *            Position before which newArgument is to be inserted. The
     *            first argument is numbered 1.Note that this is not
     *            necessarily the index of the Node in the list of child
     *            nodes, as nodes representing such elements as sep are not
     *            counted as arguments.
     * @return The Node inserted. This is the element within the DOM.
     */
    Node insertArgument(Node newArgument, int index);

    /**
     * A convenience method to set an argument child at the position
     * referenced by index. If there is currently an argument at this
     * position, it is replaced by newArgument.
     * 
     * @param newArgument
     *            Node to be inserted as the argument. This will either be a
     *            MathMLElement or a Text node.
     * @param index
     *            Position of the argument that is to be set to newArgument in
     *            the list of arguments. The first argument is numbered 1.
     *            Note that this is not necessarily the index of the Node in
     *            the list of child nodes, as nodes representing such elements
     *            as sep are not counted as arguments.
     * @return The Node inserted. This is the element within the DOM.
     */
    Node setArgument(Node newArgument, int index);

    /**
     * A convenience method to delete the argument child located at the
     * position referenced by index.
     * 
     * @param index
     *            Position of the argument to be deleted from the list of
     *            arguments. The first argument is numbered 1.
     */
    void deleteArgument(int index);

    /**
     * A convenience method to delete the argument child located at the
     * position referenced by index, and to return it to the caller.
     * 
     * @param index
     *            Position of the argument to be deleted from the list of
     *            arguments. The first argument is numbered 1.
     * @return A Node representing the deleted argument.
     */
    Node removeArgument(int index);
};
