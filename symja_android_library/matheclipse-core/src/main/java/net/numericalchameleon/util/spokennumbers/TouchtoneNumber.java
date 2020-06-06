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

import java.math.BigInteger;

public class TouchtoneNumber extends SpokenNumber {

    public TouchtoneNumber() {
        super();
    }

    public TouchtoneNumber(long number) throws Exception {
        super(number);
    }

    public TouchtoneNumber(String number) throws Exception {
        super(number);
    }

    @Override
    protected int getSupportedDigits() {
        return 1;
    }

    @Override
    public String getSoundDir() {
        return "touchtone";
    }

    @Override
    protected void init() {
        waitAfterASyllable=-600;
    }

    /**
     * wird zum Konvertieren der Zahl in Silben benötigt,
     * Ergebnis wird in einen Vektor geschrieben.
     */
    @Override
    protected void convert2Syllables() throws Exception {
        //syllables.clear();
        for (int i=0; i< number.length(); i++)
            syllables.add(number.charAt(i)+"");
    }

    /**
     * gibt Nummer in finnischen Worten zurück.
     * @param number die zu konvertierende Zahl
     */
    public static String toString(long number) throws Exception {
        return (new TouchtoneNumber(number)).toString();
    }

    public static String toString(String number) throws Exception {
        return (new TouchtoneNumber(number)).toString();
    }

    @Override
    protected void checkNumber(String number) throws Exception {
        BigInteger big = new BigInteger(number);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < syllables.size(); i++) {
            sb.append(syllables.get(i).toString());
        }
        return sb.toString().trim();
    }
}
