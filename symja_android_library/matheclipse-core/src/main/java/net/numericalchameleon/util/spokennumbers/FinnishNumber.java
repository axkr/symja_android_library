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

public class FinnishNumber extends SpokenNumber {

    private final static String field[] = {"yksi", "kaksi", "kolme", "neljä", "viisi", "kuusi", "seitsemän", "kahdeksan", "yhdeksän"}, // 1-9

            NULL = "nolla",
            MINUS = "miinus";

    public FinnishNumber() {
        super();
    }

    public FinnishNumber(long number) throws Exception {
        super(number);
    }

    public FinnishNumber(String number) throws Exception {
        super(number);
    }

    @Override
    protected int getSupportedDigits() {
        return 1;
    }

    @Override
    public String getSoundDir() {
        return "finnish";
    }

    /**
     * wird zum Konvertieren der Zahl in Silben benötigt,
     * Ergebnis wird in einen Vektor geschrieben.
     */
    @Override
    protected void convert2Syllables() throws Exception {
        // prefix
        if (number.charAt(0) == '-') {
            number = number.substring(1);
            syllables.add(MINUS + " ");
        }

        // digits wanted
        if (numberType == DIGITS) {
            fillSyllables(NULL, field);
            return;
        }

        // numbers wanted
        number2digits();
        if (number.equals("0")) {
            syllables.add(NULL);
        } else {
            syllables.add(field[digits[0] - 1]);
        }

    }

    /**
     * gibt Nummer in finnischen Worten zurück.
     * @param number die zu konvertierende Zahl
     */
    public static String toString(long number) throws Exception {
        return (new FinnishNumber(number)).toString();
    }

    public static String toString(String number) throws Exception {
        return (new FinnishNumber(number)).toString();
    }
}
