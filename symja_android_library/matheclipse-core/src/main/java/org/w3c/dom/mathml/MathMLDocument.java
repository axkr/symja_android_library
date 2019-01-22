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

import org.w3c.dom.Document;

/*
 * Please note: This file was automatically generated from the source of the
 * MathML specification. Do not edit it. If there are errors or missing
 * elements, please correct the stylesheet instead.
 */

/**
 * This interface extends the Document interface to add access to document
 * properties relating to navigation. The documentElement attribute for a
 * MathMLDocument should be the MathMLMathElement representing the top-level
 * math element which is the root of the document.
 * 
 */
public interface MathMLDocument extends Document {
    /**
     * The URI of the page that linked to this document, if available. This is
     * null if the user navigated directly to the page. If this is not a
     * stand-alone MathML document (e.g. is embedded in an XHTML document),
     * this may be retrieved from the parent Document if available.
     * 
     * @return value of the referrer attribute.
     */
    String getReferrer();

    /**
     * The domain name of the server that served the document, or null if the
     * server cannot be identified by a domain name, or if it is not
     * available. If this is not a stand-alone MathML document (e.g. is
     * embedded in an XHTML document), this may be retrieved from the parent
     * Document if available.
     * 
     * @return value of the domain attribute.
     */
    String getDomain();

    /**
     * The complete URI of this document. This is null if this is not a
     * stand-alone MathML document.
     * 
     * @return value of the URI attribute.
     */
    String getURI();
};
