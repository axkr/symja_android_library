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

public class EsperantoNumber extends SpokenNumber {

    private final static String field[] = {"nulo", "unu", "du", "tri", "kvar", "kvin", "ses", "sep", "ok", "na\u016d", "dek"},
            HUNDRED = "cent",
            AND = " ",
            MINUS = "minus";

    public EsperantoNumber() {
        super();
    }

    public EsperantoNumber(long number) throws Exception {
        super(number);
    }

    public EsperantoNumber(String number) throws Exception {
        super(number);
    }

    @Override
    protected int getSupportedDigits() {
        return 3;
    }

    @Override
    public String getSoundDir() {
        return "esperanto";
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
            fillSyllables(field);
            return;
        }

        // number wanted
        number2digits();
        if (number.equals("0")) {
            syllables.add(field[0]);
        } else {
            decode(0);
        }
    }

    // 19 = 10AND9
    // 20 = 2MUL10
    // 21 = 2MUL10AND1
    private void decode(int index) {
        int x = (digits[index + 1] * 10) + digits[index];
        if (digits[index + 2] > 0) {
            if (digits[index + 2] > 1) {
                syllables.add(field[digits[index + 2]]);
            } // only "cent", not "unucent"
            if (x > 0) {
                syllables.add(HUNDRED + " ");
            } else {
                syllables.add(HUNDRED);
            }
        }
        if (x == 0) {
        } else if (x < 11) {  // 1-10
            syllables.add(field[x]);
        } else { // 11-99
            if (digits[index + 1] > 1) {
                syllables.add(field[digits[index + 1]]);
                syllables.add(field[10]);
            } else {
                syllables.add(field[10]);
            }

            if (digits[index] > 0) {
                syllables.add(AND + field[digits[index]]);
            }
        }
    }

    /**
     * returns esperanto number literally
     * @param number die zu konvertierende Zahl
     */
    public static String toString(long number) throws Exception {
        return (new EsperantoNumber(number)).toString();
    }

    public static String toString(String number) throws Exception {
        return (new EsperantoNumber(number)).toString();
    }
}
