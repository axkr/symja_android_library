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
package org.hipparchus.transform;

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
public enum LocalizedFFTFormats implements Localizable {

    // CHECKSTYLE: stop MultipleVariableDeclarations
    // CHECKSTYLE: stop JavadocVariable

    FIRST_ELEMENT_NOT_ZERO("first element is not 0: {0}"),
    NOT_POWER_OF_TWO("{0} is not a power of 2"),
    NOT_POWER_OF_TWO_CONSIDER_PADDING("{0} is not a power of 2, consider padding for fix"),
    NOT_POWER_OF_TWO_PLUS_ONE("{0} is not a power of 2 plus one");

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
    LocalizedFFTFormats(final String sourceFormat) {
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
            final String path = LocalizedFFTFormats.class.getName().replaceAll("\\.", "/");
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
