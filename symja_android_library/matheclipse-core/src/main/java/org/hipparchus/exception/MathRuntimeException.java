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
 * All exceptions thrown by the Hipparchus code inherit from this class.
 */
public class MathRuntimeException extends RuntimeException implements LocalizedException {

    /**
     * Serializable version Id.
     */
    private static final long serialVersionUID = 20160217L;

    /**
     * URL for reporting problems for internal errors.
     */
    private static final String REPORT_URL = "https://github.com/Hipparchus-Math/hipparchus/issues";

    /**
     * Format specifier (to be translated).
     */
    private final Localizable specifier;

    /**
     * Parts to insert in the format (no translation).
     */
    private final Object[] parts;

    /**
     * Simple constructor.
     *
     * @param specifier format specifier (to be translated).
     * @param parts     parts to insert in the format (no translation).
     */
    public MathRuntimeException(final Localizable specifier, final Object... parts) {
        this.specifier = specifier;
        this.parts = (parts == null) ? new Object[0] : parts.clone();
    }

    /**
     * Simple constructor.
     *
     * @param cause     root cause.
     * @param specifier format specifier (to be translated).
     * @param parts     parts to insert in the format (no translation).
     */
    public MathRuntimeException(final Throwable cause, final Localizable specifier,
                                final Object... parts) {
        super(cause);
        this.specifier = specifier;
        this.parts = (parts == null) ? new Object[0] : parts.clone();
    }

    /**
     * Create an exception for an internal error.
     *
     * @return a new runtime exception indicating an internal error
     */
    public static MathRuntimeException createInternalError() {
        return new MathRuntimeException(LocalizedCoreFormats.INTERNAL_ERROR, REPORT_URL);
    }

    /**
     * Create an exception for an internal error.
     *
     * @param cause root cause
     * @return a new runtime exception, indicating an internal error and wrapping the
     * given throwable
     */
    public static MathRuntimeException createInternalError(final Throwable cause) {
        return new MathRuntimeException(cause, LocalizedCoreFormats.INTERNAL_ERROR, REPORT_URL);
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
