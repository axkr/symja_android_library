/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer sciences (Lab4Inf).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package de.lab4inf.math;

import java.util.Locale;

/**
 * Collection of some common Lab4Math constants and of unique
 * physical constants.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Constants.java,v 1.17 2015/04/13 16:20:21 nwulff Exp $
 * @since 27.12.2008
 */
public interface Constants {
    /**
     * Major version numbering schema of Lab4Math.
     */
    int VERSION_MAJOR = 2;
    /**
     * Minor version numbering schema of Lab4Math.
     */
    int VERSION_MINOR = 0;
    /**
     * Label version numbering schema of Lab4Math.
     */
    int VERSION_LABEL = 6;
    /**
     * global Lab4inf Logger name.
     */
    String L4MLOGGER = "de.lab4inf.math";
    /**
     * global Locale to use.
     */
    Locale DEF_LOCALE = Locale.US;
    /**
     * global debug flag used in some classes to disable
     * the logging messages within release mode.
     */
    boolean DEBUG = false;
    /**
     * Vacuum speed of light c in meter/second.
     */
    double SPEED_OF_LIGHT = 2.99792458E8;
    /**
     * Elementary charge of the electron e in Coulomb.
     */
    double ELEMENTARY_CHARGE = 1.602189E-19;
    /**
     * Rest mass of the electron in kg.
     */
    double ELECTRON_MASS = 9.10953E-31;
    /**
     * Rest mass of the electron in eV.
     */
    double ELECTRON_MASS_EV = 0.511003E6;
    /**
     * Rest mass of the proton in eV.
     */
    double PROTON_MASS_EV = 938.280E6;
    /**
     * Fine structure constant alpha.
     */
    double FINE_STRUCTURE = 1.0 / 137.0360;
    /**
     * Gravitational constant G in  N*(m/kg)**2.
     */
    double GRAVITATIONAL_CONST = 6.6732E-11;
    /**
     * Avogardo constant Na in 1/mol.
     */
    double AVOGARDO = 6.02214179E23;
    /**
     * Boltzmann constant k in Joule/Kelvin.
     */
    double BOLTZMANN = 1.3806504E-23;
    /**
     * Planck constant h in Joule*sec.
     */
    double PLANCK = 6.62606896E-34;
    /**
     * Light year in meters.
     */
    double LIGHT_YEAR = 9.4607304725808E15;
    /**
     * Parsec in meters.
     */
    double PARSEC = 30.856776E15;
    /**
     * The Euler-Mascheroni constant.
     */
    double EULER = 0.57721566490153286060651;

    /**
     * Get the version information of a Lab4Math object.
     *
     * @return version number
     **/
    String getVersion();
}
 