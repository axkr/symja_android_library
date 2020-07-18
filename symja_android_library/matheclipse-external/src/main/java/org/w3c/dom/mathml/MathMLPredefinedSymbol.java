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
 * This interface supports all of the empty built-in operator, relation,
 * function, and constant and symbol elements that have the definitionURL and
 * encoding attributes in addition to the standard set of attributes. The
 * elements supported in order of their appearance in are: inverse, compose,
 * ident, domain, codomain, image, quotient, exp, factorial, divide, max, min,
 * minus, plus, power, rem, times, root, gcd, and, or, xor, not, implies,
 * forall, exists, abs, conjugate, arg, real, imaginary, lcm, floor, ceiling,
 * eq, neq, gt, lt, geq, leq, equivalent, approx, factorof, ln, log, int,
 * diff, partialdiff, divergence, grad, curl, laplacian, union, intersect, in,
 * notin, subset, prsubset, notsubset, notprsubset, setdiff, card,
 * cartesianproduct, sum, product, limit, sin, cos, tan, sec, csc, cot, sinh,
 * cosh, tanh, sech, csch, coth, arcsin, arccos, arctan, arcsec, arccsc,
 * arccot, arcsinh, arccosh, arctanh, arcsech, arccsch, arccoth, mean, sdev,
 * variance, median, mode, moment, determinant, transpose, selector,
 * vectorproduct, scalarproduct, outerproduct, integers, reals, rationals,
 * naturalnumbers, complexes, primes, exponentiale, imaginaryi, notanumber,
 * true, false, emptyset, pi, eulergamma, and infinity.
 * 
 * 
 */
public interface MathMLPredefinedSymbol extends MathMLContentElement {
    /**
     * A string that provides an override to the default semantics, or
     * provides a more specific definition
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
     * A string representing the number of arguments. Values include 0, 1, ...
     * and variable.
     * 
     * @return value of the arity attribute.
     */
    String getArity();

    /**
     * A string giving the name of the MathML element represented. This is a
     * convenience attribute only; accessing it should be synonymous with
     * accessing the Element::tagName attribute.
     * 
     * @return value of the symbolName attribute.
     */
    String getSymbolName();
};
