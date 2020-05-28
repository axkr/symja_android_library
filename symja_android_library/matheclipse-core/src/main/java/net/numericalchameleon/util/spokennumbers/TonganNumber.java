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

public class TonganNumber extends SpokenNumber {

    private final static String field[] = {"noa", "taha", "ua", "tolu", "fa", "nima", "ono", "fitu", "valu", "hiva"}, // 0-9

            MINUS = "-";

    public TonganNumber() {
        super();
    }

    public TonganNumber(long number) throws Exception {
        super(number);
    }

    public TonganNumber(String number) throws Exception {
        super(number);
    }

    @Override
    protected int getSupportedDigits() {
        return 100;
    }

    @Override
    public String getSoundDir() {
        return "tongan";
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
        fillSyllables(field);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < syllables.size(); i++) {
            sb.append(syllables.get(i).toString());
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * gibt Nummer in italienischen Worten zurück.
     * @param number die zu konvertierende Zahl
     */
    public static String toString(long number) throws Exception {
        return (new TonganNumber(number)).toString();
    }

    public static String toString(String number) throws Exception {
        return (new TonganNumber(number)).toString();
    }
}
