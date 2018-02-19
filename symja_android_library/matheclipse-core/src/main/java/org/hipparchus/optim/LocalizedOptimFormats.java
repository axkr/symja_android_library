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
package org.hipparchus.optim;

import org.hipparchus.exception.Localizable;
import org.hipparchus.exception.UTF8Control;

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
public enum LocalizedOptimFormats implements Localizable {

    // CHECKSTYLE: stop MultipleVariableDeclarations
    // CHECKSTYLE: stop JavadocVariable

    EQUAL_VERTICES_IN_SIMPLEX("equal vertices {0} and {1} in simplex configuration"),
    INVALID_IMPLEMENTATION("required functionality is missing in {0}"),
    NO_FEASIBLE_SOLUTION("no feasible solution"),
    SIMPLEX_NEED_ONE_POINT("simplex must contain at least one point"),
    TOO_SMALL_COST_RELATIVE_TOLERANCE("cost relative tolerance is too small ({0}), no further reduction in the sum of squares is possible"),
    TOO_SMALL_ORTHOGONALITY_TOLERANCE("orthogonality tolerance is too small ({0}), solution is orthogonal to the jacobian"),
    TOO_SMALL_PARAMETERS_RELATIVE_TOLERANCE("parameters relative tolerance is too small ({0}), no further improvement in the approximate solution is possible"),
    TRUST_REGION_STEP_FAILED("trust region step has failed to reduce Q"),
    UNABLE_TO_PERFORM_QR_DECOMPOSITION_ON_JACOBIAN("unable to perform Q.R decomposition on the {0}x{1} jacobian matrix"),
    UNABLE_TO_SOLVE_SINGULAR_PROBLEM("unable to solve: singular problem"),
    UNBOUNDED_SOLUTION("unbounded solution");

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
    LocalizedOptimFormats(final String sourceFormat) {
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
            final String path = LocalizedOptimFormats.class.getName().replaceAll("\\.", "/");
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
