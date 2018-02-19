/* Copyright 2002-2016 CS Systèmes d'Information
 * Licensed to CS Systèmes d'Information (CS) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * CS licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hipparchus.exception;

import java.util.Locale;

/**
 * This interface specified methods implemented by localized exception classes.
 * <p>
 * This interface has been copied from the interface with the same name
 * from Orekit.
 * </p>
 */
public interface LocalizedException {

    /**
     * Gets the message in a specified locale.
     *
     * @param locale Locale in which the message should be translated
     * @return localized message
     */
    String getMessage(final Locale locale);

    /**
     * Get the localizable specifier of the error message.
     *
     * @return localizable specifier of the error message
     */
    Localizable getSpecifier();

    /**
     * Get the variable parts of the error message.
     *
     * @return a copy of the variable parts of the error message
     */
    Object[] getParts();

}
