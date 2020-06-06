/**
 *
 * NumericalChameleon 3.0.0 - more than an unit converter - a NumericalChameleon
 * Copyright (c) 2001-2020 Dipl.-Inf. (FH) Johann Nepomuk Loefflmann, All Rights
 * Reserved, <http://www.numericalchameleon.net>.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *******************************************************************************
 *
 * @authors:
 * Johann N. Loefflmann, Germany in cooperation with
 * João Marcelo Pereira Alves, Brazil
 */
package net.numericalchameleon.util.spokennumbers;

public class PortugueseNumber extends PortugueseBrazilNumber {

    private static final String fieldfix[][] = {
        line1,
        // differences: 14 (catorze), 16 (dezasseis), 17 (dezassete), 19 (dezanove)       
        {line2[0], line2[1], line2[2], "catorze ", line2[4], "dezasseis ", "dezassete ", line2[7], "dezanove "},
        line3,
        line4
    };

    public PortugueseNumber() {
        super();
        field = fieldfix;
        appendBlank = false;
    }

    public PortugueseNumber(long number) throws Exception {
        super(number);
        field = fieldfix;
        appendBlank = false;
    }

    public PortugueseNumber(String number) throws Exception {
        super(number);
        field = fieldfix;
        appendBlank = false;
    }

    @Override
    public String getSoundDir() {
        return "portuguese";
    }
    
    public static String toString(long number) throws Exception {
        return (new PortugueseNumber(number)).toString();
    }

    public static String toString(String number) throws Exception {
        return (new PortugueseNumber(number)).toString();
    }
}
