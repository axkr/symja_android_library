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
 */
package net.numericalchameleon.util.spokennumbers;

public class GermanSwitzerlandNumber extends GermanNumber {

    private static final String FIELD[][] = {
        line1,
        line2,
        // differences: dreissig (no ß for Switzerland)
        // In der Schweiz wird normalerweise kein ß verwendet.
        // "Das amtliche Regelwerk der deutschen Rechtschreibung" erwähnt in §25 E2:
        // "In der Schweiz kann man immer ss schreiben."        
        { line3[0], line3[1], "dreissig", line3[3], line3[4], line3[5], line3[6], line3[7], line3[8]}
    };
    
    @Override
    protected void triple(int index, String smallestNumber) {
        triple(FIELD, index, smallestNumber);
    }
    
    public GermanSwitzerlandNumber() {
        super();
    }

    public GermanSwitzerlandNumber(long number) throws Exception {
        super(number);
    }

    public GermanSwitzerlandNumber(String number) throws Exception {
        super(number);
    }
   
    /**
     * gibt Nummer in deutschen Worten zurück.
     *
     * @param number die zu konvertierende Zahl
     */
    public static String toString(long number) throws Exception {
        return (new GermanSwitzerlandNumber(number)).toString();
    }

    public static String toString(String number) throws Exception {
        return (new GermanSwitzerlandNumber(number)).toString();
    }
}
