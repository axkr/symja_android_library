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

public class ItalianNumber extends SpokenNumber {

    private final static String field[][] = {
        {"uno", "due", "tre", "quattro", "cinque", "sei", "sette", "otto", "nove"}, // 1-9
        {"undici", "dodici", "tredici", "quattordici", "quindici", "sedici", "diciassette", "diciotto", "diciannove"}, // 11-19
        {"dieci", "venti", "trenta", "quaranta", "cinquanta", "sessanta", "settanta", "ottanta", "novanta"} // 10-90
    },
            NULL = "zero",
            MINUS = "meno",
            TRE_ACCENT = "trè",
            CENTO = "cento";

    public ItalianNumber() {
        super();
    }

    public ItalianNumber(long number) throws Exception {
        super(number);
    }

    public ItalianNumber(String number) throws Exception {
        super(number);
    }

    @Override
    protected int getSupportedDigits() {
        return 3;
    }

    @Override
    public String getSoundDir() {
        return "italian";
    }

    /**
     * wird zum Konvertieren der Zahl in Silben benötigt,
     * Ergebnis wird in einen Vektor geschrieben.
     */
    @Override
    protected void convert2Syllables() throws Exception {
        if (number.charAt(0) == '-') {
            number = number.substring(1);
            syllables.add(MINUS + " ");
        }

        // digits wanted
        if (numberType == DIGITS) {
            fillSyllables(NULL, field[0]);
            return;
        }

        // number wanted
        number2digits();
        if (number.equalsIgnoreCase("0")) {
            syllables.add(NULL);
        } else {
            //syllables.addElement(field[digits[0]-1]);
            decode(0);
        }
    }

    private void decode(int index) {
        // 100, 200, ... 900
        int x = (digits[index + 2] * 100);
        if (x >= 100) {
            if (x >= 200) {
                syllables.add(field[0][digits[index + 2] - 1]);
            }
            syllables.add(CENTO);
        }

        x = (digits[index + 1] * 10) + digits[index];
        if (x == 0) {
            return;
        }
        if (x < 10) {  // 1-9
            syllables.add(field[0][x - 1]);
        } else if ((x > 10) && (x < 20)) { // 11-19
            syllables.add(field[1][x - 11]);
        } else {
            // 10, 20-99
            if (digits[index] != 1 && (digits[index] != 8)) { // 20, 22, 23, 24, 25, 26, 27, 29, 30, 32, ...
                syllables.add(field[2][digits[index + 1] - 1]);
            } else {
                String tmp = field[2][digits[index + 1] - 1];
                syllables.add(tmp.substring(0, tmp.length() - 1)); // The numbers venti, trenta, and so on drop the final vowel before adding -uno or otto: ventuno, ventotto.
            }

            if (digits[index] > 0) {
                if (digits[index] == 3) { // when -tre is the last digit of a larger number, it takes an accent: ventitrè, trentatrè, quarantatrè, and so on.
                    syllables.add(TRE_ACCENT);
                } else {
                    syllables.add(field[0][digits[index] - 1]);
                }
            }
        }


    }

    /**
     * gibt Nummer in italienischen Worten zurück.
     * @param number die zu konvertierende Zahl
     */
    public static String toString(long number) throws Exception {
        return (new ItalianNumber(number)).toString();
    }

    public static String toString(String number) throws Exception {
        return (new ItalianNumber(number)).toString();
    }
}
