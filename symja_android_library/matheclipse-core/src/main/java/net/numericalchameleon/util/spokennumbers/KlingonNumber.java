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

public class KlingonNumber extends SpokenNumber {

    protected static final String field[] = {"wa'", "cha'", "wej", "loS", "vagh", "jav", "Soch", "chorgh", "Hut"}; // 1-9
    protected static final String amount[] = {"maH ", /* ten 10 */
        "vatlh ", /* hundred 100 */
        "SaD ", /* thousand 1000*/
        "netlh ", /* ten thousand 10000 */
        "bIp ", /*  hundred thousand 100000 */
        "'uy' "}, /* million 1000000 */
            NULL = "pagh",
            MINUS = "-",
            BLANK = " ",
            DASH = "-";

    public KlingonNumber() {
        super();
    }

    public KlingonNumber(long number) throws Exception {
        super(number);
    }

    public KlingonNumber(String number) throws Exception {
        super(number);
    }

    @Override
    public String getSoundDir() {
        return "klingon";
    }

    /**
     * gibt Nummer in US-englischen Worten zurück.
     * @param number die zu konvertierende Zahl
     */
    public static String toString(long number) throws Exception {
        return (new KlingonNumber(number)).toString();
    }

    public static String toString(String number) throws Exception {
        return (new KlingonNumber(number)).toString();
    }

    @Override
    protected void convert2Syllables() throws Exception {

        if (number.charAt(0) == '-') {
            number = number.substring(1);
            syllables.add(MINUS);
        }

        // we just would like the digits
        if (numberType == DIGITS) {
            fillSyllables(NULL, field);
            return;
        }
        number2digits();
        // die Zahlentriple abarbeiten
        if (number.equals("0")) {
            syllables.add(NULL);
        } else {
            for (int i = getSupportedDigits() - 1; i > 0; i--) { // all index
                if (digits[i] > 0) {
                    syllables.add(field[digits[i] - 1]);
                    syllables.add(amount[i - 1]);
                }
            }
            if (digits[0] > 0) {
                syllables.add(field[digits[0] - 1]);
            }
        }
    }

    @Override
    protected int getSupportedDigits() {
        return 7;
    }
}
