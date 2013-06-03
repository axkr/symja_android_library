/*
 * $Id$
 */

package edu.jas.application;


import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.List;

import edu.jas.arith.Rational;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.Complex;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.TermOrder;
import edu.jas.root.ComplexAlgebraicRing;
import edu.jas.root.Interval;
import edu.jas.root.RealAlgebraicRing;
import edu.jas.root.Rectangle;
import edu.jas.root.RootUtil;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.QuotientRing;


/**
 * Builder for extension field towers.
 * @author Heinz Kredel
 */
public class ExtensionFieldBuilder implements Serializable {


    /**
     * The current factory.
     */
    public final RingFactory factory; // must be a raw type


    /**
     * Constructor not for use.
     */
    protected ExtensionFieldBuilder() {
        throw new IllegalArgumentException("do not use this constructor");
    }


    /**
     * Constructor.
     * @param base the base field.
     */
    public ExtensionFieldBuilder(RingFactory base) {
        factory = base;
    }


    /**
     * Build the field tower. TODO: build at the end and optimize field tower
     * for faster computation.
     */
    public RingFactory build() {
        return factory;
    }


    /**
     * Set base field.
     * @param base the base field for the extensions.
     */
    public static ExtensionFieldBuilder baseField(RingFactory base) {
        return new ExtensionFieldBuilder(base);
    }


    /**
     * Transcendent field extension.
     * @param vars names for the transcendent generators.
     */
    public ExtensionFieldBuilder transcendentExtension(String vars) {
        String[] variables = GenPolynomialTokenizer.variableList(vars);
        GenPolynomialRing pfac = new GenPolynomialRing(factory, variables);
        QuotientRing qfac = new QuotientRing(pfac);
        RingFactory base = (RingFactory) qfac;
        return new ExtensionFieldBuilder(base);
    }


    /**
     * Polynomial ring extension.
     * @param vars names for the polynomial ring generators.
     */
    public ExtensionFieldBuilder polynomialExtension(String vars) {
        String[] variables = GenPolynomialTokenizer.variableList(vars);
        GenPolynomialRing pfac = new GenPolynomialRing(factory, variables);
        RingFactory base = (RingFactory) pfac;
        return new ExtensionFieldBuilder(base);
    }


    /**
     * Algebraic field extension.
     * @param var name(s) for the algebraic generator(s).
     * @param expr generating expresion, a univariate or multivariate polynomial
     *            in vars.
     */
    public ExtensionFieldBuilder algebraicExtension(String var, String expr) {
        String[] variables = GenPolynomialTokenizer.variableList(var);
        if (variables.length < 1) {
            variables = GenPolynomialTokenizer.expressionVariables(expr);
            if (variables.length < 1) {
                throw new IllegalArgumentException("no variables in '" + var + "' and '" + expr + "'" );
            }
        }
        GenPolynomialRing pfac = new GenPolynomialRing(factory, variables);
        if (variables.length == 1) { // simple extension
            GenPolynomial gen = pfac.parse(expr);
            AlgebraicNumberRing afac = new AlgebraicNumberRing(gen);
            RingFactory base = (RingFactory) afac;
            return new ExtensionFieldBuilder(base);
        }
        GenPolynomialTokenizer pt = new GenPolynomialTokenizer(pfac, new StringReader(expr));
        List<GenPolynomial> gen = null;
        try {
            gen = pt.nextPolynomialList();
        } catch (IOException e) { // should not happen
            throw new IllegalArgumentException(e);
        }
        Ideal agen = new Ideal(pfac, gen);
        if (agen.isONE()) {
            throw new IllegalArgumentException("ideal is 1: " + expr);
        }
        if (agen.isZERO()) { // transcendent extension
            QuotientRing qfac = new QuotientRing(pfac);
            RingFactory base = (RingFactory) qfac;
            return new ExtensionFieldBuilder(base);
        }
        // check if agen is prime?
        ResidueRing afac = new ResidueRing(agen);
        RingFactory base = (RingFactory) afac;
        return new ExtensionFieldBuilder(base);
    }


    /**
     * Real algebraic field extension.
     * @param var name for the algebraic generator.
     * @param expr generating expresion, a univariate polynomial in var.
     * @param root isolating interval for a real root.
     */
    public ExtensionFieldBuilder realAlgebraicExtension(String var, String expr, String root) {
        String[] variables = new String[] { var };
        RingElem one = (RingElem) factory.getONE();
        if (!(one instanceof Rational)) {
            throw new IllegalArgumentException("base field not instance of Rational");
        }
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        GenPolynomialRing pfac = new GenPolynomialRing(factory, to, variables);
        GenPolynomial gen = pfac.parse(expr);
        RingFactory cf = pfac.coFac;
        Interval iv = RootUtil.parseInterval(cf, root);
        //System.out.println("iv = " + iv);
        RealAlgebraicRing rfac = new RealAlgebraicRing(gen, iv);
        RingFactory base = (RingFactory) rfac;
        return new ExtensionFieldBuilder(base);
    }


    /**
     * Complex algebraic field extension.
     * @param var name for the algebraic generator.
     * @param expr generating expresion, a univariate polynomial in var.
     * @param root isolating rectangle for a complex root.
     */
    public ExtensionFieldBuilder complexAlgebraicExtension(String var, String expr, String root) {
        String[] variables = new String[] { var };
        RingElem one = (RingElem) factory.getONE();
        if (!(one instanceof Complex)) {
            throw new IllegalArgumentException("base field not instance of Complex");
        }
        GenPolynomialRing pfac = new GenPolynomialRing(factory, variables);
        //System.out.println("pfac = " + pfac);
        GenPolynomial gen = pfac.parse(expr);
        //System.out.println("gen  = " + gen);
        RingFactory cf = pfac.coFac;
        Rectangle rt = RootUtil.parseRectangle(cf, root);
        //System.out.println("rt = " + rt);
        ComplexAlgebraicRing rfac = new ComplexAlgebraicRing(gen, rt);
        RingFactory base = (RingFactory) rfac;
        return new ExtensionFieldBuilder(base);
    }


    /**
     * String representation of the ideal.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer(" ");
        s.append(factory.toString());
        s.append(" ");
        return s.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    public String toScript() {
        // Python case
        StringBuffer s = new StringBuffer(" ");
        s.append(factory.toScript());
        s.append(" ");
        return s.toString();
    }

}
