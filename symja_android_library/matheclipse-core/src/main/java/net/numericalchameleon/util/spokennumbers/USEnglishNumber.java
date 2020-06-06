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

public class USEnglishNumber extends UKEnglishNumber {

    private static final String[] 
        amountUS = {"thousand",
        "million", "billion",
        "trillion", "quadrillion",
        "quintillion", "sextillion",
        "septillion", "octillion",
        "nonillion", "decillion",
        "undecillion", "duodecillion",
        "tredecillion", "quattuordecillion",
        "quindecillion", "sexdecillion",
        "septendecillon", "octodecillion",
        "novemdecillion", "vigintillion",
        "unvigintillion", "dovigintillion",
        "trevigintillion", "quattuorvigintillion",
        "quinvigintillion", "sexvigintillion",
        "septenvigintillion", "octovigintillion",
        "novemvigintillion", "trigintillion"
        };
    
    @Override
    protected void convert2Syllables() throws Exception {
        convert2Syllables(amountUS);
    }

    public USEnglishNumber() {
        super();
    }

    public USEnglishNumber(long number) throws Exception {
        super(number);
    }

    public USEnglishNumber(String number) throws Exception {
        super(number);
    }

    @Override
    public String getSoundDir() {
        return "us_english";
    }

    @Override
    protected void triple(int index) {
        if (digits[index + 2] > 0) {
            syllables.add(field[0][digits[index + 2] - 1]);
            syllables.add(HUNDRED);
        }

        // no "AND" for integer values

        if ((digits[index + 1] == 1) && (digits[index] > 0)) {         // XXXX11
            syllables.add(field[1][digits[index] - 1]);   // elf, zwölf, ...
        } else {
            if ((digits[index + 1] >= 1) && (digits[index] == 0)) {       // XXXX10
                syllables.add(field[2][digits[index + 1] - 1]);   // zehn, zwanzig, ...
            } else {                                                // XXXX2X oder XXXX0X
                if (digits[index + 1] > 0) {                                 // XXXX2X
                    syllables.add(field[2][digits[index + 1] - 1]); // zehn, zwanzig,...
                    if (digits[index] > 0) {                                // XXXX21
                        syllables.add("-");                            // und
                    }
                }
                if (digits[index] > 0) {                                  // XXXX21
                    syllables.add(field[0][digits[index] - 1]); // ein, zwei, drei, ...
                }
            }
        }
    }

    /**
     * gibt Nummer in US-englischen Worten zurück.
     * @param number die zu konvertierende Zahl
     */
    public static String toString(long number) throws Exception {
        return (new USEnglishNumber(number)).toString();
    }

    public static String toString(String number) throws Exception {
        return (new USEnglishNumber(number)).toString();
    }
}
