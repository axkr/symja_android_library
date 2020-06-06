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

public class PortugueseBrazilNumber extends SpokenNumber {

    protected static final String
            line1[] = {"um ", "dois ", "três ", "quatro ", "cinco ", "seis ", "sete ", "oito ", "nove "}, // 1-9
            line2[] = {"onze ", "doze ", "treze ", "quatorze ", "quinze ", "dezesseis ", "dezessete ", "dezoito ", "dezenove "}, // 11-19
            line3[] = {"dez ", "vinte ", "trinta ", "quarenta ", "cinqüenta ", "sessenta ", "setenta ", "oitenta ", "noventa "},
            line4[] = {"cento ", "duzentos ", "trezentos ", "quatrocentos ", "quinhentos ", "seiscentos ", "setecentos ", "oitocentos ", "novecentos "};
    private static final String fieldfix[][] = {
        line1, line2, line3, line4
    };
    protected String field[][];
    protected final static String amount[][] = {
        {"mil ", "mil "},
        {"milhão ", "milhões "},
        {"bilhão ", "bilhões "},
        {"trilhão ", "trilhões "},
        {"quatrilhão ", "quatrilhões "},
        {"quintilhão ", "quintilhões "},
        {"sextilhão ", "sextilhões "},
        {"setilhão ", "setilhões "},
        {"octilhão ", "octilhões "},
        {"nonilhão ", "nonilhões "},
        {"decilhão ", "decilhões "}
    },
            NULL = "zero",
            MINUS = "menos ",
            AND = "e ",
            HUNDERT = "cem ";
    protected boolean setprefix = false;

    public PortugueseBrazilNumber() {
        super();
        field = fieldfix;
        appendBlank = false;
    }

    public PortugueseBrazilNumber(long number) throws Exception {
        super(number);
        field = fieldfix;
        appendBlank = false;
    }

    public PortugueseBrazilNumber(String number) throws Exception {
        super(number);
        field = fieldfix;
        appendBlank = false;
    }

    @Override
    protected int getSupportedDigits() {
        return 36;
    }

    @Override
    public String getSoundDir() {
        return "portuguese_br";
    }

    /**
     * wird zum Konvertieren der Zahl in Silben benötigt, Ergebnis wird in einen
     * Vektor geschrieben.
     */
    @Override
    protected void convert2Syllables() throws Exception {

        setprefix = false;
        if (number.charAt(0) == '-') {
            number = number.substring(1);
            syllables.add(MINUS);
        }

        // we just would like the digits
        if (numberType == DIGITS) {
            fillSyllables(NULL, line1);
            return;
        }

        number2digits();
        // die Zahlentriple abarbeiten
        if (number.equals("0")) {
            syllables.add(NULL);
        } else {
            int counter = amount.length - 1;
            for (int i = MAX_DIGITS - 3; i > 0; i -= 3) {
                xtriple(i, amount[counter][0], amount[counter][1]);
                counter--;
            }
            xtriple(0, null, null);
        }
    }

    protected void triple(int index) {
        if (digits[index + 2] > 0) {
            if ((digits[index + 2] == 1) && (digits[index + 1] == 0 && digits[index] == 0)) {
                syllables.add(HUNDERT);
            } else {
                syllables.add(field[3][digits[index + 2] - 1]);
                if ((digits[index] + digits[index + 1]) != 0) {
                    syllables.add(AND);
                }
            }
        }

        if ((digits[index + 1] == 1) && (digits[index] > 0)) { // XXXX11

            syllables.add(field[1][digits[index] - 1]);   // elf, zwölf, ...
        } else if ((digits[index + 1] >= 1) && (digits[index] == 0)) { // XXXX10

            syllables.add(field[2][digits[index + 1] - 1]);   // zehn, zwanzig, ...
        } else { // XXXX2X oder XXXX0X

            if (digits[index + 1] > 0) { // XXXX2X

                syllables.add(field[2][digits[index + 1] - 1]); // zehn, zwanzig,...
                if (digits[index] > 0) { // XXXX21

                    syllables.add(AND);                            // und
                }
            }

            if (digits[index] > 0) // XXXX21
            {
                syllables.add(field[0][digits[index] - 1]); // ein, zwei, drei, ...
            }
        }
    }

    protected boolean onlyZeros(int index) {
        boolean onlyZeros = true;
        for (int i = 0; i <= index; i++) {
            if (digits[i] > 0) {
                onlyZeros = false;
                break;
            }
        }
        return onlyZeros;
    }
    // index = Begin des Tripels
    // singular = z. B. Million
    // plural = z. B. Millionen

    protected void xtriple(int index, String singular, String plural) {
        if ((digits[index] + digits[index + 1] + digits[index + 2]) > 0) {
            /**
             * Rule for the prefix "e" Between triples, there is "e" only when
             * the third position is 0 or when first and second are 0 in the
             * triple on the right side AND all the other digits in triples to
             * the right must also be 0. 
             * with 'e': 1009, 1090, 1900, 1099
             * no 'e': 1909, 1990, 1999
             */
            if (setprefix
                    && (digits[index + 2] == 0 || (digits[index] == 0 && digits[index + 1] == 0))
                    && onlyZeros(index - 1)) {
                syllables.add(AND);
            }

            int sum = digits[index] + digits[index + 1] * 10 + digits[index + 2] * 100;
            if (!((sum == 1) && (index == 3))) { // avoid "um mil"            
                triple(index);
            }
            setprefix = true;

            if (index != 0) { // we dont add nulls (the last triple)

                if (sum > 1) {
                    syllables.add(plural);
                } else {
                    syllables.add(singular);
                }
            }
        }
    }

    /**
     * gibt Nummer in portugiesischen Worten zurück.
     *
     * @param number die zu konvertierende Zahl
     */
    public static String toString(long number) throws Exception {
        return (new PortugueseBrazilNumber(number)).toString();
    }

    public static String toString(String number) throws Exception {
        return (new PortugueseBrazilNumber(number)).toString();
    }
}
