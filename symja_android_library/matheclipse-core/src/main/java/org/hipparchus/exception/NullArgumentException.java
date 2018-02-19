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

import java.text.MessageFormat;
import java.util.Locale;

/**
 * All conditions checks that fail due to a {@code null} argument must throw
 * this exception.
 * This class is meant to signal a precondition violation ("null is an illegal
 * argument") and so does not extend the standard {@code NullPointerException}.
 * Propagation of {@code NullPointerException} from within Hipparchus is
 * construed to be a bug.
 * <p>
 * Note: from 1.0 onwards, this class extends {@link NullPointerException} instead
 * of {@link MathIllegalArgumentException}.
 */
public class NullArgumentException extends NullPointerException
        implements LocalizedException {

    /**
     * Serializable version Id.
     */
    private static final long serialVersionUID = 20160217L;

    /**
     * Format specifier (to be translated).
     */
    private final Localizable specifier;

    /**
     * Parts to insert in the format (no translation).
     */
    private final Object[] parts;

    /**
     * Default constructor.
     */
    public NullArgumentException() {
        this(LocalizedCoreFormats.NULL_NOT_ALLOWED);
    }

    /**
     * Simple constructor.
     *
     * @param specifier format specifier (to be translated).
     * @param parts     parts to insert in the format (no translation).
     */
    public NullArgumentException(final Localizable specifier, final Object... parts) {
        this.specifier = specifier;
        this.parts = (parts == null) ? new Object[0] : parts.clone();
    }

    /**
     * Builds a message string by from a pattern and its arguments.
     *
     * @param locale    Locale in which the message should be translated
     * @param specifier format specifier (to be translated)
     * @param parts     parts to insert in the format (no translation)
     * @return a message string
     */
    private static String buildMessage(final Locale locale, final Localizable specifier, final Object... parts) {
        return (specifier == null) ? "" : new MessageFormat(specifier.getLocalizedString(locale), locale).format(parts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(final Locale locale) {
        return buildMessage(locale, specifier, parts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return getMessage(Locale.US);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocalizedMessage() {
        return getMessage(Locale.getDefault());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Localizable getSpecifier() {
        return specifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getParts() {
        return parts.clone();
    }

}
