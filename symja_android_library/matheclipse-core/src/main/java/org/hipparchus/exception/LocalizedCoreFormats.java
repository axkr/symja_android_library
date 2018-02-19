/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hipparchus.exception;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Enumeration for localized messages formats used in exceptions messages.
 * <p>
 * The constants in this enumeration represent the available
 * formats as localized strings. These formats are intended to be
 * localized using simple properties files, using the constant
 * name as the key and the property value as the message format.
 * The source English format is provided in the constants themselves
 * to serve both as a reminder for developers to understand the parameters
 * needed by each format, as a basis for translators to create
 * localized properties files, and as a default format if some
 * translation is missing.
 * </p>
 */
public enum LocalizedCoreFormats implements Localizable {

    // CHECKSTYLE: stop MultipleVariableDeclarations
    // CHECKSTYLE: stop JavadocVariable

    ARRAY_SIZE_EXCEEDS_MAX_VARIABLES("array size cannot be greater than {0}"),
    ARRAY_SIZES_SHOULD_HAVE_DIFFERENCE_1("array sizes should have difference 1 ({0} != {1} + 1)"),
    ARRAY_SUMS_TO_ZERO("array sums to zero"),
    AT_LEAST_ONE_COLUMN("matrix must have at least one column"),
    AT_LEAST_ONE_ROW("matrix must have at least one row"),
    BANDWIDTH("bandwidth ({0})"),
    BESSEL_FUNCTION_BAD_ARGUMENT("Bessel function of order {0} cannot be computed for x = {1}"),
    BESSEL_FUNCTION_FAILED_CONVERGENCE("Bessel function of order {0} failed to converge for x = {1}"),
    BINOMIAL_INVALID_PARAMETERS_ORDER("must have n >= k for binomial coefficient (n, k), got k = {0}, n = {1}"),
    BINOMIAL_NEGATIVE_PARAMETER("must have n >= 0 for binomial coefficient (n, k), got n = {0}"),
    CANNOT_COMPUTE_0TH_ROOT_OF_UNITY("cannot compute 0-th root of unity, indefinite result"),
    CANNOT_COMPUTE_BETA_DENSITY_AT_0_FOR_SOME_ALPHA("cannot compute beta density at 0 when alpha = {0,number}"),
    CANNOT_COMPUTE_BETA_DENSITY_AT_1_FOR_SOME_BETA("cannot compute beta density at 1 when beta = %.3g"),
    CANNOT_COMPUTE_NTH_ROOT_FOR_NEGATIVE_N("cannot compute nth root for null or negative n: {0}"),
    CANNOT_DISCARD_NEGATIVE_NUMBER_OF_ELEMENTS("cannot discard a negative number of elements ({0})"),
    CANNOT_FORMAT_INSTANCE_AS_COMPLEX("cannot format a {0} instance as a complex number"),
    CANNOT_FORMAT_OBJECT_TO_FRACTION("cannot format given object as a fraction number"),
    CANNOT_SUBSTITUTE_ELEMENT_FROM_EMPTY_ARRAY("cannot substitute an element from an empty array"),
    COLUMN_INDEX("column index ({0})"), /* keep */
    CONSTRAINT("constraint"), /* keep */
    CONTINUED_FRACTION_INFINITY_DIVERGENCE("Continued fraction convergents diverged to +/- infinity for value {0}"),
    CONTINUED_FRACTION_NAN_DIVERGENCE("Continued fraction diverged to NaN for value {0}"),
    CONTRACTION_CRITERIA_SMALLER_THAN_EXPANSION_FACTOR("contraction criteria ({0}) smaller than the expansion factor ({1}).  This would lead to a never ending loop of expansion and contraction as a newly expanded internal storage array would immediately satisfy the criteria for contraction."),
    CONTRACTION_CRITERIA_SMALLER_THAN_ONE("contraction criteria smaller than one ({0}).  This would lead to a never ending loop of expansion and contraction as an internal storage array length equal to the number of elements would satisfy the contraction criteria."),
    CONVERGENCE_FAILED("convergence failed"), /* keep */
    CUMULATIVE_PROBABILITY_RETURNED_NAN("Cumulative probability function returned NaN for argument {0} p = {1}"),
    DIFFERENT_ROWS_LENGTHS("some rows have length {0} while others have length {1}"),
    DIGEST_NOT_INITIALIZED("digest not initialized"),
    DIMENSIONS_MISMATCH_2x2("got {0}x{1} but expected {2}x{3}"), /* keep */
    DIMENSIONS_MISMATCH("inconsistent dimensions: {0} != {1}"), /* keep */
    DISCRETE_CUMULATIVE_PROBABILITY_RETURNED_NAN("Discrete cumulative probability function returned NaN for argument {0}"),
    DISTRIBUTION_NOT_LOADED("distribution not loaded"),
    DUPLICATED_ABSCISSA_DIVISION_BY_ZERO("duplicated abscissa {0} causes division by zero"),
    EMPTY_INTERPOLATION_SAMPLE("sample for interpolation is empty"),
    EMPTY_POLYNOMIALS_COEFFICIENTS_ARRAY("empty polynomials coefficients array"), /* keep */
    EMPTY_SELECTED_COLUMN_INDEX_ARRAY("empty selected column index array"),
    EMPTY_SELECTED_ROW_INDEX_ARRAY("empty selected row index array"),
    ENDPOINTS_NOT_AN_INTERVAL("endpoints do not specify an interval: [{0}, {1}]"),
    EVALUATION("evaluation"), /* keep */
    EXPANSION_FACTOR_SMALLER_THAN_ONE("expansion factor smaller than one ({0})"),
    FACTORIAL_NEGATIVE_PARAMETER("must have n >= 0 for n!, got n = {0}"),
    FAILED_BRACKETING("number of iterations={4}, maximum iterations={5}, initial={6}, lower bound={7}, upper bound={8}, final a value={0}, final b value={1}, f(a)={2}, f(b)={3}"),
    FAILED_FRACTION_CONVERSION("Unable to convert {0} to fraction after {1} iterations"),
    FIRST_COLUMNS_NOT_INITIALIZED_YET("first {0} columns are not initialized yet"),
    FIRST_ROWS_NOT_INITIALIZED_YET("first {0} rows are not initialized yet"),
    FRACTION_CONVERSION_OVERFLOW("Overflow trying to convert {0} to fraction ({1}/{2})"),
    GCD_OVERFLOW_32_BITS("overflow: gcd({0}, {1}) is 2^31"),
    GCD_OVERFLOW_64_BITS("overflow: gcd({0}, {1}) is 2^63"),
    ILL_CONDITIONED_OPERATOR("condition number {1} is too high "),
    INDEX_LARGER_THAN_MAX("the index specified: {0} is larger than the current maximal index {1}"),
    INDEX_NOT_POSITIVE("index ({0}) is not positive"),
    INDEX("index ({0})"), /* keep */
    NOT_FINITE_NUMBER("{0} is not a finite number"), /* keep */
    INFINITE_BOUND("interval bounds must be finite"),
    ARRAY_ELEMENT("value {0} at index {1}"), /* keep */
    INFINITE_ARRAY_ELEMENT("Array contains an infinite element, {0} at index {1}"),
    INFINITE_VALUE_CONVERSION("cannot convert infinite value"),
    INITIAL_CAPACITY_NOT_POSITIVE("initial capacity ({0}) is not positive"),
    INITIAL_COLUMN_AFTER_FINAL_COLUMN("initial column {1} after final column {0}"),
    INITIAL_ROW_AFTER_FINAL_ROW("initial row {1} after final row {0}"),
    INSUFFICIENT_DATA("insufficient data"),
    INSUFFICIENT_DATA_FOR_T_STATISTIC("insufficient data for t statistic, needs at least 2, got {0}"),
    INSUFFICIENT_DIMENSION("insufficient dimension {0}, must be at least {1}"),
    DIMENSION("dimension ({0})"), /* keep */
    INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE("sample contains {0} observed points, at least {1} are required"),
    INSUFFICIENT_ROWS_AND_COLUMNS("insufficient data: only {0} rows and {1} columns."),
    INTERNAL_ERROR("internal error, please fill a bug report at {0}"),
    INVALID_MAX_ITERATIONS("bad value for maximum iterations number: {0}"),
    NOT_ENOUGH_DATA_REGRESSION("the number of observations is not sufficient to conduct regression"),
    INVALID_REGRESSION_OBSERVATION("length of regressor array = {0} does not match the number of variables = {1} in the model"),
    INVALID_ROUNDING_METHOD("invalid rounding method {0}, valid methods: {1} ({2}), {3} ({4}), {5} ({6}), {7} ({8}), {9} ({10}), {11} ({12}), {13} ({14}), {15} ({16})"),
    ITERATIONS("iterations"), /* keep */
    LCM_OVERFLOW_32_BITS("overflow: lcm({0}, {1}) is 2^31"),
    LCM_OVERFLOW_64_BITS("overflow: lcm({0}, {1}) is 2^63"),
    LOWER_BOUND_NOT_BELOW_UPPER_BOUND("lower bound ({0}) must be strictly less than upper bound ({1})"), /* keep */
    LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT("lower endpoint ({0}) must be less than or equal to upper endpoint ({1})"),
    EVALUATIONS("evaluations"), /* keep */
    MAX_COUNT_EXCEEDED("maximal count ({0}) exceeded"), /* keep */
    NAN_ELEMENT_AT_INDEX("element {0} is NaN"),
    NAN_VALUE_CONVERSION("cannot convert NaN value"),
    NEGATIVE_COMPLEX_MODULE("negative complex module {0}"),
    NEGATIVE_ELEMENT_AT_INDEX("element {0} is negative: {1}"),
    NUMBER_OF_SUCCESSES("number of successes ({0})"), /* keep */
    NUMBER_OF_INTERPOLATION_POINTS("number of interpolation points ({0})"), /* keep */
    NUMBER_OF_TRIALS("number of trials ({0})"),
    ROBUSTNESS_ITERATIONS("number of robustness iterations ({0})"),
    START_POSITION("start position ({0})"), /* keep */
    NON_CONVERGENT_CONTINUED_FRACTION("Continued fraction convergents failed to converge (in less than {0} iterations) for value {1}"),
    NON_SQUARE_MATRIX("non square ({0}x{1}) matrix"),
    NORM("Norm ({0})"), /* keep */
    NORMALIZE_INFINITE("Cannot normalize to an infinite value"),
    NORMALIZE_NAN("Cannot normalize to NaN"),
    NOT_DECREASING_SEQUENCE("points {3} and {2} are not decreasing ({1} < {0})"), /* keep */
    NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS("not enough data ({0} rows) for this many predictors ({1} predictors)"),
    NOT_ENOUGH_POINTS_IN_SPLINE_PARTITION("spline partition must have at least {0} points, got {1}"),
    NOT_INCREASING_SEQUENCE("points {3} and {2} are not increasing ({1} > {0})"), /* keep */
    NOT_POSITIVE_DEFINITE_MATRIX("not positive definite matrix"), /* keep */
    NON_POSITIVE_DEFINITE_OPERATOR("non positive definite linear operator"), /* keep */
    NON_SELF_ADJOINT_OPERATOR("non self-adjoint linear operator"), /* keep */
    NON_SQUARE_OPERATOR("non square ({0}x{1}) linear operator"), /* keep */
    DEGREES_OF_FREEDOM("degrees of freedom ({0})"), /* keep */
    NOT_POSITIVE_EXPONENT("invalid exponent {0} (must be positive)"),
    NUMBER_OF_ELEMENTS_SHOULD_BE_POSITIVE("number of elements should be positive ({0})"),
    BASE("base ({0})"), /* keep */
    EXPONENT("exponent ({0})"), /* keep */
    LENGTH("length ({0})"), /* keep */
    MEAN("mean ({0})"), /* keep */
    NOT_POSITIVE_NUMBER_OF_SAMPLES("number of sample is not positive: {0}"),
    NUMBER_OF_SAMPLES("number of samples ({0})"), /* keep */
    PERMUTATION_SIZE("permutation size ({0}"), /* keep */
    POPULATION_SIZE("population size ({0})"), /* keep */
    NOT_POSITIVE_SCALE("scale must be positive ({0})"),
    SCALE("scale ({0})"), /* keep */
    SHAPE("shape ({0})"), /* keep */
    STANDARD_DEVIATION("standard deviation ({0})"), /* keep */
    NOT_POSITIVE_WINDOW_SIZE("window size must be positive ({0})"),
    NOT_STRICTLY_DECREASING_SEQUENCE("points {3} and {2} are not strictly decreasing ({1} <= {0})"), /* keep */
    NOT_STRICTLY_INCREASING_SEQUENCE("points {3} and {2} are not strictly increasing ({1} >= {0})"), /* keep */
    NOT_SUPPORTED_NAN_STRATEGY("NaN strategy {0} not supported"),
    NON_SYMMETRIC_MATRIX("non symmetric matrix: the difference between entries at ({0},{1}) and ({1},{0}) is larger than {2}"), /* keep */
    NO_CONVERGENCE_WITH_ANY_START_POINT("none of the {0} start points lead to convergence"), /* keep */
    NO_DATA("no data"), /* keep */
    NO_OPTIMUM_COMPUTED_YET("no optimum computed yet"), /* keep */
    NO_REGRESSORS("Regression model must include at least one regressor"),
    NAN_NOT_ALLOWED("NaN is not allowed"),
    NULL_NOT_ALLOWED("null is not allowed"), /* keep */
    ARRAY_ZERO_LENGTH_OR_NULL_NOT_ALLOWED("a null or zero length array not allowed"),
    COVARIANCE_MATRIX("covariance matrix"), /* keep */
    DENOMINATOR("denominator"), /* keep */
    DENOMINATOR_FORMAT("denominator format"), /* keep */
    FRACTION("fraction"), /* keep */
    FUNCTION("function"), /* keep */
    IMAGINARY_FORMAT("imaginary format"), /* keep */
    INPUT_ARRAY("input array"), /* keep */
    NUMERATOR("numerator"), /* keep */
    NUMERATOR_FORMAT("numerator format"), /* keep */
    REAL_FORMAT("real format"), /* keep */
    WHOLE_FORMAT("whole format"), /* keep */
    NUMBER_TOO_LARGE("{0} is larger than the maximum ({1})"), /* keep */
    NUMBER_TOO_SMALL("{0} is smaller than the minimum ({1})"), /* keep */
    NUMBER_TOO_LARGE_BOUND_EXCLUDED("{0} is larger than, or equal to, the maximum ({1})"), /* keep */
    NUMBER_TOO_SMALL_BOUND_EXCLUDED("{0} is smaller than, or equal to, the minimum ({1})"), /* keep */
    NUMBER_OF_SUCCESS_LARGER_THAN_POPULATION_SIZE("number of successes ({0}) must be less than or equal to population size ({1})"),
    NUMERATOR_OVERFLOW_AFTER_MULTIPLY("overflow, numerator too large after multiply: {0}"),
    OBSERVED_COUNTS_BOTTH_ZERO_FOR_ENTRY("observed counts are both zero for entry {0}"),
    OUT_OF_BOUNDS_QUANTILE_VALUE("out of bounds quantile value: {0}, must be in (0, 100]"),
    OUT_OF_BOUNDS_CONFIDENCE_LEVEL("out of bounds confidence level {0}, must be between {1} and {2}"),
    OUT_OF_BOUND_SIGNIFICANCE_LEVEL("out of bounds significance level {0}, must be between {1} and {2}"),
    SIGNIFICANCE_LEVEL("significance level ({0})"), /* keep */
    OUT_OF_RANGE_ROOT_OF_UNITY_INDEX("out of range root of unity index {0} (must be in [{1};{2}])"),
    OUT_OF_RANGE("out of range"), /* keep */
    OUT_OF_RANGE_SIMPLE("{0} out of [{1}, {2}] range"), /* keep */
    OUT_OF_RANGE_LEFT("{0} out of ({1}, {2}] range"),
    OVERFLOW("overflow"), /* keep */
    OVERFLOW_IN_FRACTION("overflow in fraction {0}/{1}, cannot negate"),
    OVERFLOW_IN_ADDITION("overflow in addition: {0} + {1}"),
    OVERFLOW_IN_SUBTRACTION("overflow in subtraction: {0} - {1}"),
    OVERFLOW_IN_MULTIPLICATION("overflow in multiplication: {0} * {1}"),
    PERCENTILE_IMPLEMENTATION_CANNOT_ACCESS_METHOD("cannot access {0} method in percentile implementation {1}"),
    PERCENTILE_IMPLEMENTATION_UNSUPPORTED_METHOD("percentile implementation {0} does not support {1}"),
    PERMUTATION_EXCEEDS_N("permutation size ({0}) exceeds permuation domain ({1})"), /* keep */
    POLYNOMIAL("polynomial"), /* keep */
    ROOTS_OF_UNITY_NOT_COMPUTED_YET("roots of unity have not been computed yet"),
    ROW_INDEX("row index ({0})"), /* keep */
    NOT_BRACKETING_INTERVAL("interval does not bracket a root: f({0}) = {2}, f({1}) = {3}"),
    START_POINT_NOT_IN_INTERVAL("The start point {0} is not in the interval [{1}, {2}]"),
    SAMPLE_SIZE_EXCEEDS_COLLECTION_SIZE("sample size ({0}) exceeds collection size ({1})"), /* keep */
    SAMPLE_SIZE_LARGER_THAN_POPULATION_SIZE("sample size ({0}) must be less than or equal to population size ({1})"),
    SIMPLE_MESSAGE("{0}"),
    SINGULAR_MATRIX("matrix is singular"), /* keep */
    SINGULAR_OPERATOR("operator is singular"),
    SUBARRAY_ENDS_AFTER_ARRAY_END("subarray ends after array end"),
    TOO_LARGE_CUTOFF_SINGULAR_VALUE("cutoff singular value is {0}, should be at most {1}"),
    TOO_MANY_ELEMENTS_TO_DISCARD_FROM_ARRAY("cannot discard {0} elements from a {1} elements array"),
    TOO_MANY_REGRESSORS("too many regressors ({0}) specified, only {1} in the model"),
    TWO_OR_MORE_CATEGORIES_REQUIRED("two or more categories required, got {0}"),
    TWO_OR_MORE_VALUES_IN_CATEGORY_REQUIRED("two or more values required in each category, one has {0}"),
    UNKNOWN_MODE("unknown mode {0}, known modes: {1} ({2}), {3} ({4}), {5} ({6}), {7} ({8}), {9} ({10}) and {11} ({12})"),
    CANNOT_PARSE_AS_TYPE("string \"{0}\" unparseable (from position {1}) as an object of type {2}"), /* keep */
    CANNOT_PARSE("string \"{0}\" unparseable (from position {1})"), /* keep */
    UNSUPPORTED_OPERATION("unsupported operation"), /* keep */
    ARITHMETIC_EXCEPTION("arithmetic exception"), /* keep */
    ILLEGAL_STATE("illegal state"), /* keep */
    USER_EXCEPTION("exception generated in user code"), /* keep */
    URL_CONTAINS_NO_DATA("URL {0} contains no data"),
    VALUES_ADDED_BEFORE_CONFIGURING_STATISTIC("{0} values have been added before statistic is configured"),
    VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT("vector must have at least one element"),
    WEIGHT_AT_LEAST_ONE_NON_ZERO("weight array must contain at least one non-zero value"),
    WRONG_NUMBER_OF_POINTS("{0} points are required, got only {1}"),
    NUMBER_OF_POINTS("number of points ({0})"), /* keep */
    ZERO_DENOMINATOR("denominator must be different from 0"), /* keep */
    ZERO_DENOMINATOR_IN_FRACTION("zero denominator in fraction {0}/{1}"),
    ZERO_FRACTION_TO_DIVIDE_BY("the fraction to divide by must not be zero: {0}/{1}"),
    ZERO_NORM("zero norm"),
    ZERO_NOT_ALLOWED("zero not allowed here");

    // CHECKSTYLE: resume JavadocVariable
    // CHECKSTYLE: resume MultipleVariableDeclarations


    /**
     * Source English format.
     */
    private final String sourceFormat;

    /**
     * Simple constructor.
     *
     * @param sourceFormat source English format to use when no
     *                     localized version is available
     */
    LocalizedCoreFormats(final String sourceFormat) {
        this.sourceFormat = sourceFormat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSourceString() {
        return sourceFormat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocalizedString(final Locale locale) {
        try {
            final String path = LocalizedCoreFormats.class.getName().replaceAll("\\.", "/");
            ResourceBundle bundle =
                    ResourceBundle.getBundle("assets/" + path, locale, new UTF8Control());
            if (bundle.getLocale().getLanguage().equals(locale.getLanguage())) {
                final String translated = bundle.getString(name());
                if ((translated != null) &&
                        (translated.length() > 0) &&
                        (!translated.toLowerCase().contains("missing translation"))) {
                    // the value of the resource is the translated format
                    return translated;
                }
            }

        } catch (MissingResourceException mre) { // NOPMD
            // do nothing here
        }

        // either the locale is not supported or the resource is unknown
        // don't translate and fall back to using the source format
        return sourceFormat;

    }

}
