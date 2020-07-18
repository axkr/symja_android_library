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

import org.w3c.dom.NodeList;

/*
 * Please note: This file was automatically generated from the source of the
 * MathML specification. Do not edit it. If there are errors or missing
 * elements, please correct the stylesheet instead.
 */

/**
 * This interface is provided as a specialization of the NodeList interface.
 * The child Nodes of this NodeList must be MathMLElements or Text nodes. Note
 * that MathMLNodeLists are frequently used in the DOM as values of readonly
 * attributes, encapsulating, for instance, various collections of child
 * elements. When used in this way, these objects are always understood to be
 * live, in the sense that changes to the document are immediately reflected
 * in them.
 * 
 * 
 */
public interface MathMLNodeList extends NodeList {
};
