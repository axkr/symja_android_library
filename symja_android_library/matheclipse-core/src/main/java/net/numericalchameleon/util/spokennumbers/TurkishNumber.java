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

public class TurkishNumber extends SpokenNumber {

    private final static String field[][] = {
        {"bir ", "iki ", "ü\u00E7 ", "dört ", "be\u015F ", "alt\u0131 ", "yedi ", "sekiz ", "dokuz "}, // 1-9
        {"on bir", "on iki", "on ü\u00E7", "on dört", "on be\u015F", "on alt\u0131", "on yedi", "on sekiz", "on dokuz"},
        {"on ", "yirmi ", "otuz ", "k\u0131rk ", "elli ", "altm\u0131\u015F ", "yetm\u0131\u015F ", "seksen ", "doksan "}
    },
            amount[] = {"bin ", "milyon ", "milyar ", "bilyon ", "bin bilyon", "trilyon ", "bin trilyon "},
            NULL = "s\u0131f\u0131r",
            MINUS = "eksi",
            HUNDRED = "yüz ";

    public TurkishNumber() {
        super();
        appendBlank = false;
    }

    public TurkishNumber(long number) throws Exception {
        super(number);
        appendBlank = false;
    }

    public TurkishNumber(String number) throws Exception {
        super(number);
        appendBlank = false;
    }

    @Override
    protected int getSupportedDigits() {
        return 24;
    }

    @Override
    public String getSoundDir() {
        return "turkish";
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

        // we just would like the digits
        if (numberType == DIGITS) {
            fillSyllables(NULL, field[0]);
            return;
        }

        number2digits();
        // die Zahlentriple abarbeiten
        if (number.equals("0")) {
            syllables.add(NULL);
        } else {
            //  syllables.addElement(field[digits[0]-1]);
            int counter = amount.length - 1;
            for (int i = MAX_DIGITS - 3; i > 0; i -= 3) {
                xtriple(i, amount[counter--]);
            }
            triple(0);
        }
    }

    /**
     * gibt Nummer in deutschen Worten zurück.
     * @param number die zu konvertierende Zahl
     */
    public static String toString(long number) throws Exception {
        return (new TurkishNumber(number)).toString();
    }

    public static String toString(String number) throws Exception {
        return (new TurkishNumber(number)).toString();
    }

    private void triple(int index) {
        if (digits[index + 2] > 0) {
            // Modifikation für "hundert"
            // falsch: bir yüz (einhundert)
            // richtig: yüz  (hundert), aber: iki yüz (zweihundert)
            if (digits[index + 2] > 1) {
                syllables.add(field[0][digits[index + 2] - 1]);
            }

            syllables.add(HUNDRED);
        }

        if ((digits[index + 1] == 1) && (digits[index] > 0)) // XXXX11
        {
            syllables.add(field[1][digits[index] - 1]);   // elf, zwölf, ...
        } else if ((digits[index + 1] >= 1) && (digits[index] == 0)) // XXXX10
        {
            syllables.add(field[2][digits[index + 1] - 1]);   // zehn, zwanzig, ...
        } else // XXXX2X oder XXXX0X
        {
            if (digits[index + 1] > 0) // XXXX2X
            {
                syllables.add(field[2][digits[index + 1] - 1]); // zehn, zwanzig,...
            }

            if (digits[index] > 0) // XXXX21
            {
                syllables.add(field[0][digits[index] - 1]); // ein, zwei, drei, ...
            }
        }
    }

    private void xtriple(int index, String singular) {
        if ((digits[index] + digits[index + 1] + digits[index + 2]) > 0) {
            // modifikation für "tausend"
            // falsch:  bir bin (eintausend)
            // richtig: bin (tausend), aber: iki bin (zweitausend)
            if (!((index == 3) && (digits[3] == 1) && (digits[4] == 0) && (digits[5] == 0))) {
                triple(index);
            }

            syllables.add(singular);
        }
    }
}
