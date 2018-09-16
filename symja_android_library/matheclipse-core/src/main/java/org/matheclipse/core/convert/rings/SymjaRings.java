package org.matheclipse.core.convert.rings;

import cc.redberry.rings.*;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.poly.MultipleFieldExtension;
import cc.redberry.rings.poly.MultivariateRing;
import cc.redberry.rings.poly.Util;
import cc.redberry.rings.poly.multivar.Monomial;
import cc.redberry.rings.poly.multivar.MultivariateConversions;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;
import cc.redberry.rings.poly.univar.UnivariateGCD;
import cc.redberry.rings.poly.univar.UnivariatePolynomial;
import cc.redberry.rings.util.ArraysUtil;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.*;

import java.util.*;
import java.util.function.Function;

import static cc.redberry.rings.Rings.*;

/**
 * Polynomial factorization, GCD and other methods via Rings library (http://ringsalgebra.io)
 *
 * @author Stanislav Poslavsky
 * @see <a href="https://github.com/PoslavskySV/rings">https://github.com/PoslavskySV/rings</a>
 */
public final class SymjaRings {
    private SymjaRings() {}

    /**
     * Converts Symja expressions to Rings and vice-versa. The type of Rings expressions is R.
     *
     * @param <R> type of Rings elements that will be used
     */
    public interface IConverter<R> {
        /** The ring of elements */
        Ring<R> getRing();

        /** Convert to element of ring */
        R toRingElement(IExpr expr);

        /** Convert to Symja */
        IExpr toIExpr(R expr);
    }

    /**
     * Creates Symja <> Rings converter for polynomial operations with either integer or mod coefficients.
     *
     * @param characteristic characteristic ("modulus")
     * @param expressions    expressions to convert
     */
    public static IConverter<Rational<MultivariatePolynomial<BigInteger>>>
    mkConverterForModOperations(IExpr[] expressions, java.math.BigInteger characteristic) {
        final RingsConversionHelper helper = mkConversionHelper(characteristic, expressions);
        return new IConverter<Rational<MultivariatePolynomial<BigInteger>>>() {
            @Override
            public Ring<Rational<MultivariatePolynomial<BigInteger>>> getRing() { return helper.qRing; }

            @Override
            public Rational<MultivariatePolynomial<BigInteger>> toRingElement(IExpr expr) { return toRationalFuncOrNull(expr, helper); }

            @Override
            public IExpr toIExpr(Rational<MultivariatePolynomial<BigInteger>> expr) { return SymjaRings.toIExprZ(expr, helper); }
        };
    }

    /**
     * Creates Symja <> Rings converter for polynomial operations with over field extensions
     */
    public static IConverter<Rational<MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>>>>
    mkConverterForOperationsInExtensions(IExpr[] expressions, IExpr[] extensions) {
        return mkExtensionHelper(expressions, extensions);
    }

    ///////////////////////////////////////////////////// Factor /////////////////////////////////////////////////////

    /**
     * Factorize expression
     *
     * @param expr       expression
     * @param squareFree whether to perform square free factorization only
     */
    public static IExpr FactorOverQ(IExpr expr, boolean squareFree) {
        return FactorOverZp(expr, java.math.BigInteger.ZERO, squareFree);
    }

    /**
     * Factorize expression over Zp
     *
     * @param expr           expression
     * @param characteristic modulus
     * @param squareFree     whether to perform square free factorization only
     */
    public static IExpr FactorOverZp(IExpr expr, java.math.BigInteger characteristic, boolean squareFree) {
        return Factor(expr, mkConverterForModOperations(new IExpr[]{expr}, characteristic), squareFree);
    }

    /**
     * Factorize expression over algebraic number field
     *
     * @param expr       expression
     * @param extensions algebraic numbers
     * @param automatic  try to guess extension automatically
     * @param squareFree whether to perform square free factorization only
     */
    public static IExpr FactorOverExtension(IExpr expr, IExpr[] extensions, boolean automatic, boolean squareFree) {
        IExpr[] algebraicNumbers = guessExtensions(automatic ? ArraysUtil.addAll(extensions, expr) : extensions);
        if (algebraicNumbers.length == 0)
            return FactorOverZp(expr, java.math.BigInteger.ZERO, squareFree);
        else
            return Factor(expr, mkConverterForOperationsInExtensions(new IExpr[]{expr}, algebraicNumbers), squareFree);
    }

    /**
     * Factorize expression
     *
     * @param expr       expression
     * @param squareFree perform square free factorization only
     */
    public static <R> IExpr Factor(IExpr expr, IConverter<R> converter, boolean squareFree) {
        if (converter == null)
            return null;
        R rExpr = converter.toRingElement(expr);
        if (rExpr == null)
            return null;

        Ring<R> ring = converter.getRing();
        FactorDecomposition<R> factors = squareFree
                ? ring.factorSquareFree(rExpr)
                : ring.factor(rExpr);
        List<IExpr> result = new ArrayList<>();
        for (int i = 0; i <= factors.size(); ++i) {
            IExpr factor = converter.toIExpr(i == factors.size() ? factors.unit : factors.get(i));
            int exponent = i == factors.size() ? 1 : factors.getExponent(i);

            if (factor.isOne())
                continue;
            if (exponent == 1)
                result.add(factor);
            else
                result.add(F.Power(factor, exponent));
        }
        if (result.isEmpty())
            result.add(F.integer(1));

        return result.size() == 1 ? result.get(0) : F.Times(result.toArray(new IExpr[result.size()]));
    }

    /**
     * Pick all algebraic numbers from given expressions
     */
    public static IExpr[] guessExtensions(IExpr... exprs) {
        return findSymbols(exprs).stream().filter(SymjaRings::isAlgebraicNumber).toArray(IExpr[]::new);
    }

    ///////////////////////////////////////////////////// Together /////////////////////////////////////////////////////

    /**
     * Together expression
     *
     * @param expr expression
     */
    public static IExpr TogetherOverQ(IExpr expr) {
        return TogetherOverZp(expr, java.math.BigInteger.ZERO);
    }

    /**
     * Together expression over Zp
     *
     * @param expr           expression
     * @param characteristic modulus
     */
    public static IExpr TogetherOverZp(IExpr expr, java.math.BigInteger characteristic) {
        return Together(expr, mkConverterForModOperations(new IExpr[]{expr}, characteristic));
    }

    /**
     * Together expression over algebraic number field
     *
     * @param expr       expression
     * @param extensions algebraic numbers
     * @param automatic  try to guess extension automatically
     */
    public static IExpr TogetherOverExtension(IExpr expr, IExpr[] extensions, boolean automatic) {
        IExpr[] algebraicNumbers = guessExtensions(automatic ? ArraysUtil.addAll(extensions, expr) : extensions);
        if (algebraicNumbers.length == 0)
            return TogetherOverZp(expr, java.math.BigInteger.ZERO);
        else
            return Together(expr, mkConverterForOperationsInExtensions(new IExpr[]{expr}, algebraicNumbers));
    }

    /** Together */
    public static <R> IExpr Together(IExpr a, IConverter<R> converter) {
        R ra = converter.toRingElement(a);
        if (ra == null)
            return null;
        return converter.toIExpr(ra);
    }

    ////////////////////////////////////////////////// PolynomialGCD //////////////////////////////////////////////////

    /**
     * Polynomial GCD
     */
    public static IExpr PolynomialGCDOverQ(IExpr a, IExpr b) {
        return PolynomialGCDOverZp(a, b, java.math.BigInteger.ZERO);
    }

    /**
     * Polynomial GCD over Zp
     *
     * @param a              expression
     * @param b              expression
     * @param characteristic modulus
     */
    public static IExpr PolynomialGCDOverZp(IExpr a, IExpr b, java.math.BigInteger characteristic) {
        return PolynomialGCD(a, b, mkConverterForModOperations(new IExpr[]{a, b}, characteristic));
    }

    /**
     * Polynomial GCD over algebraic extension
     *
     * @param a          expression
     * @param b          expression
     * @param extensions algebraic numbers
     * @param automatic  try to guess extension automatically
     */
    public static IExpr PolynomialGCDOverExtension(IExpr a, IExpr b, IExpr[] extensions, boolean automatic) {
        IExpr[] algebraicNumbers = guessExtensions(automatic ? ArraysUtil.addAll(extensions, a, b) : extensions);
        if (algebraicNumbers.length == 0)
            return PolynomialGCDOverQ(a, b);
        else
            return PolynomialGCD(a, b, mkConverterForOperationsInExtensions(new IExpr[]{a, b}, algebraicNumbers));
    }

    /** Polynomial GCD */
    public static <R> IExpr PolynomialGCD(IExpr a, IExpr b, IConverter<Rational<R>> converter) {
        Rational<R>
                ra = converter.toRingElement(a),
                rb = converter.toRingElement(b);
        if (ra == null || rb == null)
            return null;
        Rationals<R> ring = (Rationals<R>) converter.getRing();
        return converter.toIExpr(ring.mkNumerator(ring.ring.gcd(ra.numerator(), rb.numerator())));
    }

    /////////////////////////////////////////////// PolynomialExtendedGCD ///////////////////////////////////////////////

    /**
     * Polynomial extended GCD
     *
     * @param a   expression
     * @param b   expression
     * @param var variable
     */
    public static IExpr PolynomialExtendedGCDOverQ(IExpr a, IExpr b, IExpr var) {
        return PolynomialExtendedGCDOverZp(a, b, var, java.math.BigInteger.ZERO);
    }

    /**
     * Polynomial GCD over Zp
     *
     * @param a              expression
     * @param b              expression
     * @param var            variable
     * @param characteristic modulus
     */
    public static IExpr PolynomialExtendedGCDOverZp(IExpr a, IExpr b, IExpr var, java.math.BigInteger characteristic) {
        return PolynomialExtendedGCD(a, b, var, mkConverterForModOperations(new IExpr[]{a, b}, characteristic));
    }

    /**
     * Polynomial GCD
     *
     * @param a          expression
     * @param b          expression
     * @param var        variable
     * @param extensions algebraic numbers
     * @param automatic  try to guess extension automatically
     */
    public static IExpr PolynomialExtendedGCDOverExtension(IExpr a, IExpr b, IExpr var, IExpr[] extensions, boolean automatic) {
        IExpr[] algebraicNumbers = guessExtensions(automatic ? ArraysUtil.addAll(extensions, a, b) : extensions);
        if (algebraicNumbers.length == 0)
            return PolynomialExtendedGCDOverQ(a, b, var);
        else
            return PolynomialExtendedGCD(a, b, var, mkConverterForOperationsInExtensions(new IExpr[]{a, b}, algebraicNumbers));
    }

    /** Polynomial Extended GCD */
    @SuppressWarnings("unchecked")
    public static <E>
    IExpr PolynomialExtendedGCD(IExpr a, IExpr b, IExpr variable, IConverter<Rational<MultivariatePolynomial<E>>> converter) {
        Rational<MultivariatePolynomial<E>>
                ra = converter.toRingElement(a),
                rb = converter.toRingElement(b),
                rv = converter.toRingElement(variable);
        if (ra == null || rb == null || rv == null)
            return null;

        if (!rv.isIntegral() || !(
                rv.numerator().isMonomial()
                        && rv.numerator().degree() == 1
                        && rv.numerator().isMonic()))
            throw new IllegalArgumentException("simple variable should be specified");

        int iVariable = rv.numerator().univariateVariable();
        if (ra.denominator().degree(iVariable) != 0 || rb.denominator().degree(iVariable) != 0)
            throw new IllegalArgumentException("not a polynomial expressions");


        Rationals<MultivariatePolynomial<E>> ring = (Rationals<MultivariatePolynomial<E>>) converter.getRing();
        MultivariatePolynomial<E>
                raDen = ra.denominator().asUnivariateEliminate(iVariable).cc(),
                rbDen = rb.denominator().asUnivariateEliminate(iVariable).cc(),
                factory = raDen;

        Rationals<MultivariatePolynomial<E>> qRing = Frac(Rings.MultivariateRing(factory));
        UnivariatePolynomial<Rational<MultivariatePolynomial<E>>>
                ura = ra.numerator().asUnivariateEliminate(iVariable).mapCoefficients(qRing, cf -> qRing.mk(cf, raDen)),
                urb = rb.numerator().asUnivariateEliminate(iVariable).mapCoefficients(qRing, cf -> qRing.mk(cf, rbDen));

        UnivariatePolynomial<Rational<MultivariatePolynomial<E>>>[] xgcd;
        if (ura.stream().allMatch(p -> p.numerator().isConstant() && p.denominator().isConstant())
                && urb.stream().allMatch(p -> p.numerator().isConstant() && p.denominator().isConstant())) {
            // univariate polynomials
            Rationals<E> cfRing = Frac(ra.numerator().ring);
            UnivariatePolynomial<Rational<E>>
                    uraR = ura.mapCoefficients(cfRing, p -> cfRing.mk(p.numerator().cc(), p.denominator().cc())),
                    urbR = urb.mapCoefficients(cfRing, p -> cfRing.mk(p.numerator().cc(), p.denominator().cc()));

            xgcd = Arrays.stream(UnivariateGCD.PolynomialExtendedGCD(uraR, urbR))
                    .map(p -> p.mapCoefficients(qRing, cf ->
                            qRing.mk(factory.createConstant(cf.numerator()),
                                    factory.createConstant(cf.denominator()))))
                    .toArray(UnivariatePolynomial[]::new);
        } else
            xgcd = UnivariateGCD.PolynomialExtendedGCD(ura, urb);

        IExpr[] result = Arrays.stream(xgcd).map(p -> {
            Util.Tuple2<UnivariatePolynomial<MultivariatePolynomial<E>>, MultivariatePolynomial<E>> cd = Util.toCommonDenominator(p);
            return converter.toIExpr(ring.mk(MultivariatePolynomial.asMultivariate(cd._1, iVariable, true), cd._2.insertVariable(iVariable)));
        }).toArray(IExpr[]::new);

        return F.List(result[0], F.List(result[1], result[2]));
    }

    ///////////////////////////////////////////////////// Conversions /////////////////////////////////////////////////////


    /** Symja's I */
    private static final IExpr imaginaryUnit = F.complex(F.integer(0), F.integer(1));

    /** Returns a set of symbols that occur in the expression considered as multivariate polynomial */
    private static Set<IExpr> findSymbols(IExpr... expressions) {
        Set<IExpr> seen = new HashSet<>();
        for (IExpr expr : expressions)
            findSymbols(expr, seen);
        return seen;
    }

    /** Fills the set with symbols that occur in the expression considered as multivariate polynomial */
    private static void findSymbols(IExpr expr, Set<IExpr> seen) {
        if (expr.isPlus() || expr.isTimes())
            for (int i = 1; i < expr.size(); ++i)
                findSymbols(((IAST) expr).get(i), seen);
        else if (expr.isPower() && expr.exponent().isInteger())
            findSymbols(expr.base(), seen);
        else if (expr.isComplex())
            seen.add(imaginaryUnit);
        else if (!expr.isInteger() && !expr.isRational())
            seen.add(expr);
    }

    /**
     * Helper class for conversion between Symja and Rings
     */
    private static final class RingsConversionHelper {
        /** Expression -> Rings var */
        final Map<IExpr, Integer> fromExprToVar;
        /** Rings var -> Expression */
        final Map<Integer, IExpr> fromVarToExpr;
        /** Characteristic (that is "modulus") */
        final BigInteger characteristic;
        /** Polynomial ring */
        final MultivariateRing<MultivariatePolynomial<BigInteger>> pRing;
        /** Field of rational functions */
        final Rationals<MultivariatePolynomial<BigInteger>> qRing;

        RingsConversionHelper(Map<IExpr, Integer> fromExprToVar, Map<Integer, IExpr> fromVarToExpr, BigInteger characteristic) {
            this.fromExprToVar = fromExprToVar;
            this.fromVarToExpr = fromVarToExpr;
            this.characteristic = characteristic;
            this.pRing = MultivariateRing(fromExprToVar.size(), characteristic.isZero() ? Z : Rings.Zp(characteristic));
            this.qRing = Rings.Frac(pRing);
        }
    }

    /**
     * Creates {@link RingsConversionHelper}
     *
     * @param expressions expressions that appear in calculations
     */
    public static RingsConversionHelper mkConversionHelper(IExpr... expressions) {
        return mkConversionHelper(java.math.BigInteger.ZERO, expressions);
    }

    /**
     * Creates {@link RingsConversionHelper}
     *
     * @param characteristic the characteristic (that is "modulus")
     * @param expressions    expressions that appear in calculations
     */
    public static RingsConversionHelper mkConversionHelper(java.math.BigInteger characteristic, IExpr... expressions) {
        Set<IExpr> symbols = findSymbols(expressions);
        Map<IExpr, Integer> fromExprToVar = new HashMap<>();
        Map<Integer, IExpr> fromVarToExpr = new HashMap<>();
        int index = 0;
        for (IExpr symbol : symbols) {
            fromExprToVar.put(symbol, index);
            fromVarToExpr.put(index, symbol);
            ++index;
        }
        return new RingsConversionHelper(fromExprToVar, fromVarToExpr, new BigInteger(characteristic));
    }

    /**
     * Convert Symja expression to Rings rational function.
     */
    public static Rational<MultivariatePolynomial<BigInteger>>
    toRationalFuncOrNull(IExpr expr,
                         RingsConversionHelper helper) {
        if (expr.isPlus()) {
            IAST plus = (IAST) expr;
            Rational<MultivariatePolynomial<BigInteger>> result = helper.qRing.getZero();
            for (int i = 1; i < plus.size(); i++) {
                Rational<MultivariatePolynomial<BigInteger>> summand = toRationalFuncOrNull(plus.get(i), helper);
                if (summand == null)
                    return null;
                result = result.add(summand);
            }
            return result;
        } else if (expr.isTimes()) {
            IAST times = (IAST) expr;
            Rational<MultivariatePolynomial<BigInteger>> result = helper.qRing.getOne();
            for (int i = 1; i < times.size(); i++) {
                Rational<MultivariatePolynomial<BigInteger>> multiplier = toRationalFuncOrNull(times.get(i), helper);
                if (multiplier == null)
                    return null;
                result = result.multiply(multiplier);
            }
            return result;
        } else if (expr.isPower() && expr.exponent().isInteger()) {
            Rational<MultivariatePolynomial<BigInteger>> base = toRationalFuncOrNull(expr.base(), helper);
            if (base == null)
                return null;
            return helper.qRing.pow(base, expr.exponent().toIntDefault(Integer.MAX_VALUE));
        } else if (expr.isInteger())
            return helper.qRing.valueOfBigInteger(new BigInteger(((IInteger) expr).toBigNumerator()));
        else if (expr.isRational()) {
            java.math.BigInteger
                    num = ((IRational) expr).numerator().toBigNumerator(),
                    den = ((IRational) expr).denominator().toBigNumerator();
            return helper.qRing.divideExact(
                    helper.qRing.valueOfBigInteger(new BigInteger(num)),
                    helper.qRing.valueOfBigInteger(new BigInteger(den)));
        } else if (expr.isComplex()) {
            Integer i = helper.fromExprToVar.get(imaginaryUnit);
            if (i == null)
                return null;
            IComplex c = (IComplex) expr;
            Rational<MultivariatePolynomial<BigInteger>>
                    re = toRationalFuncOrNull(c.re(), helper),
                    im = toRationalFuncOrNull(c.im(), helper);
            if (re == null || im == null)
                return null;

            return helper.qRing.add(re, helper.qRing.multiply(im, helper.qRing.mkNumerator(helper.pRing.variable(i))));
        } else {
            Integer variable = helper.fromExprToVar.get(expr);
            if (variable == null)
                return null;
            return helper.qRing.mkNumerator(helper.pRing.variable(variable));
        }
    }

    /**
     * Converts multivariate polynomial to Symja expression
     *
     * @param poly        polynomial
     * @param helper      helper
     * @param cfConverter function that converts coefficients
     */
    public static <E> IExpr
    toIExpr(MultivariatePolynomial<E> poly,
            RingsConversionHelper helper,
            Function<E, IExpr> cfConverter) {
        IASTAppendable plus = F.PlusAlloc(poly.size());
        for (Monomial<E> monomial : poly) {
            E coeff = monomial.coefficient;
            int[] exponents = monomial.exponents;

            List<IExpr> parts = new ArrayList<>();
            if (!poly.ring.isOne(coeff) || monomial.totalDegree == 0)
                parts.add(cfConverter.apply(coeff));

            for (int i = 0; i < exponents.length; i++)
                if (exponents[i] != 0)
                    if (exponents[i] == 1)
                        parts.add(helper.fromVarToExpr.get(i));
                    else
                        parts.add(F.Power(helper.fromVarToExpr.get(i), exponents[i]));

            plus.append(parts.size() == 1 ? parts.get(0) : F.Times(parts.toArray(new IExpr[parts.size()])));
        }
        return plus.getOneIdentity(F.C0);
    }

    /**
     * Converts rational function to Symja expression
     *
     * @param rational    rational function
     * @param helper      helper
     * @param cfConverter function that converts coefficients
     */
    public static <E> IExpr
    toIExpr(Rational<MultivariatePolynomial<E>> rational,
            RingsConversionHelper helper,
            Function<E, IExpr> cfConverter) {
        IExpr num = toIExpr(rational.numerator(), helper, cfConverter);
        if (rational.isIntegral())
            return num;
        return F.Times(num, F.Power(toIExpr(rational.denominator(), helper, cfConverter), -1));
    }

    /** BigInteger -> IExpr */
    private static IExpr int2expr(BigInteger cf) {return F.integer(new java.math.BigInteger(cf.toByteArray()));}

    /** Rational[BigInteger] -> IExpr */
    private static IExpr rat2expr(Rational<BigInteger> cf) {
        return cf.isIntegral()
                ? int2expr(cf.numerator())
                : int2expr(cf.numerator()).divide(int2expr(cf.denominator()));
    }

    /** Rational[MultivariatePolynomial[BigInteger]] -> IExpr */
    private static IExpr
    toIExprZ(Rational<MultivariatePolynomial<BigInteger>> rational, RingsConversionHelper helper) {
        return toIExpr(rational, helper, SymjaRings::int2expr);
    }

    /** Rational[MultivariatePolynomial[Rational[BigInteger]]] -> IExpr */
    private static IExpr
    toIExprQ(Rational<MultivariatePolynomial<Rational<BigInteger>>> rational, RingsConversionHelper helper) {
        return toIExpr(rational, helper, SymjaRings::rat2expr);
    }


    /////////////////////////// Field extensions ///////////////////////////

    /** whether expression is an algebraic number */
    private static boolean isAlgebraicNumber(IExpr el) {
        return el.isImaginaryUnit() || isRootAlgebraicNumber(el);
    }

    /** whether expression  is simple root e.g. Sqrt[3/5] */
    private static boolean isRootAlgebraicNumber(IExpr el) {
        return el.isPower()
                && el.base().isRational()
                && el.exponent().isRational()
                && !((IRational) el.exponent()).denominator().isOne();
    }

    /** Helper for working in algebraic field extensions */
    private static class RingsExtensionConversionsHelper
            implements IConverter<Rational<MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>>>> {
        /** multiple field extension */
        final MultipleFieldExtension<
                Monomial<Rational<BigInteger>>,
                MultivariatePolynomial<Rational<BigInteger>>,
                UnivariatePolynomial<Rational<BigInteger>>
                > fieldExtension;
        /** conversion helper */
        final RingsConversionHelper helper;
        /** variables that correspond to algebraic elements */
        final int[] extensionVars;
        /** Polynomial ring over field extension */
        final MultivariateRing<MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>>> pRing;
        /** Rationals field over field extension */
        final Rationals<MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>>> qRing;

        RingsExtensionConversionsHelper(
                MultipleFieldExtension<
                        Monomial<Rational<BigInteger>>,
                        MultivariatePolynomial<Rational<BigInteger>>,
                        UnivariatePolynomial<Rational<BigInteger>>
                        > fieldExtension,
                RingsConversionHelper helper,
                int[] extensionVars,
                MultivariateRing<MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>>> pRing) {
            this.fieldExtension = fieldExtension;
            this.helper = helper;
            this.extensionVars = extensionVars;
            this.pRing = pRing;
            this.qRing = Frac(pRing);
        }

        @Override
        public Ring<Rational<MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>>>> getRing() {
            return qRing;
        }

        @Override
        public Rational<MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>>> toRingElement(IExpr expr) {
            return toRationalFuncOrNull(expr, helper).map(pRing,
                    p -> p.mapCoefficients(Q, Q::mkNumerator)
                            .asOverMultivariateEliminate(extensionVars)
                            .setRing(fieldExtension));
        }

        @Override
        public IExpr toIExpr(Rational<MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>>> r) {
            return toIExprQ(r.map(MultivariateRing(helper.pRing.nVariables(), Q), poly -> MultivariateConversions.merge(poly, extensionVars)), helper);
        }
    }

    /**
     * Create multiple field extension from the given algebraic elements or return null if algebraic elements are not
     * deducible
     */
    private static MultipleFieldExtension<
            Monomial<Rational<BigInteger>>,
            MultivariatePolynomial<Rational<BigInteger>>,
            UnivariatePolynomial<Rational<BigInteger>>>
    mkFieldExtension(IExpr... algebraicNumbers) {
        MultipleFieldExtension<
                Monomial<Rational<BigInteger>>,
                MultivariatePolynomial<Rational<BigInteger>>,
                UnivariatePolynomial<Rational<BigInteger>>
                > field = null;

        for (IExpr el : algebraicNumbers) {
            UnivariatePolynomial<Rational<BigInteger>> minPoly;
            if (el.isImaginaryUnit())
                minPoly = Rings.GaussianRationals.getMinimalPolynomial();
            else if (isRootAlgebraicNumber(el)) {
                // x = (a/b) ^ (c/d)
                // b^c x^d - a^c = 0
                BigInteger
                        a = new BigInteger(((IRational) el.base()).numerator().toBigNumerator()),
                        b = new BigInteger(((IRational) el.base()).denominator().toBigNumerator()),
                        c = new BigInteger(((IRational) el.exponent()).numerator().toBigNumerator()),
                        d = new BigInteger(((IRational) el.exponent()).denominator().toBigNumerator());

                if (!d.isInt() || d.intValueExact() < 0)
                    return null;

                minPoly = UnivariatePolynomial.zero(Q);
                minPoly.set(0, Q.mkNumerator(Z.pow(a, c).negate()));
                minPoly.set(d.intValueExact(), Q.mkNumerator(Z.pow(b, c)));
            } else
                return null;

            if (field == null)
                field = MultipleFieldExtension.mkMultipleExtension(minPoly);
            else
                field = field.joinAlgebraicElement(minPoly);
        }

        return field;
    }

    private static IExpr[] getPrimitiveAlgebraicNumbers(IExpr[] proposedExtensions) {
        return findSymbols(proposedExtensions)
                .stream().filter(SymjaRings::isAlgebraicNumber)
                .distinct()
                .toArray(IExpr[]::new);

    }

    @SuppressWarnings("unchecked")
    private static RingsExtensionConversionsHelper mkExtensionHelper(IExpr[] expressions,
                                                                     IExpr[] proposedExtensions) {
        // pick only algebraic elements from the proposed extensions
        IExpr[] extensions = getPrimitiveAlgebraicNumbers(proposedExtensions);
        // prepare helper for Symja <> Rings conversion
        RingsConversionHelper helper = mkConversionHelper(java.math.BigInteger.ZERO, ArraysUtil.addAll(proposedExtensions, expressions));

        // converted to rings
        Rational<MultivariatePolynomial<BigInteger>>[] ringsExpressions = Arrays
                .stream(expressions)
                .map(e -> toRationalFuncOrNull(e, helper))
                .toArray(Rational[]::new);

        if (Arrays.stream(ringsExpressions).anyMatch(Objects::isNull))
            // if any conversion failed
            return null;

        // variables used to designate algebraic elements
        int[] extensionVars = ArraysUtil
                .getSortedDistinct(Arrays.stream(extensions)
                        .mapToInt(helper.fromExprToVar::get).toArray());

        // multiple field extension
        MultipleFieldExtension<
                Monomial<Rational<BigInteger>>,
                MultivariatePolynomial<Rational<BigInteger>>,
                UnivariatePolynomial<Rational<BigInteger>>
                > fieldExtension = mkFieldExtension(extensions);

        if (fieldExtension == null)
            // non-trivial algebraic numbers
            return null;

        // actual polynomial ring with coefficients from extension
        MultivariateRing<MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>>>
                pRing = MultivariateRing(helper.pRing.nVariables() - extensions.length, fieldExtension);

        return new RingsExtensionConversionsHelper(fieldExtension, helper, extensionVars, pRing);
    }
}
