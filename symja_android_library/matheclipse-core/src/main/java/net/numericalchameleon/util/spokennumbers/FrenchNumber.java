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

public class FrenchNumber extends SpokenNumber {

    private final static String field[][] = {
        {"un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf"}, // 1-9
        {"onze", "douze", "treize", "quatorze", "quinze", "seize", "dix-sept", "dix-huit", "dix-neuf"}, // 11-19
        {"dix", "vingt", "trente", "quarante", "cinquante", "soixante", "soixante-dix", "quatre-vingt", "quatre-vingt-dix"} // 10, 20, ..., 90
    },
            amount[] = {
        " mille",
        " million",
        " milliard",
        " billion",
        " billiard",
        " trillion",
        " trilliard",
        " quadrillion",
        " quadrilliard",
        " quintillion",
        " quintilliard",
        " sextillion",
        " sextilliard",
        " septillion",
        " septilliard",
        " octillion",
        " octilliard",
        " nonillion",
        " nonilliard",
        " decillion",
        " decilliard",
        " undecillion",
        " undecilliard",
        " duodecillion",
        " duodecilliard",
        " tredicillion",
        " tredicilliard",
        " quattuordecillion",
        " quattuordecilliard",
        " quindecillion",
        " quindecilliard",},
            ET = " et ",
            DASH = "-",
            NULL = "zéro",
            MINUS = "moins",
            CENT = " cent",
            CENTS = " cents",
            PLURAL = "s";

    public FrenchNumber() {
        super();
    }

    public FrenchNumber(long number) throws Exception {
        super(number);
    }

    public FrenchNumber(String number) throws Exception {
        super(number);
    }

    @Override
    protected int getSupportedDigits() {
        return 96;
    }

    @Override
    public String getSoundDir() {
        return "french";
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

        // we would like the real number in words
        number2digits();
/*        for (int digit : digits) {
            System.out.print(digit+" ");
        }
        System.out.println();*/
        
        if (number.equalsIgnoreCase("0") || number.matches("^0+$")) { // 0 or 00 or 000...
            syllables.add(NULL);
        } else {
            int counter = amount.length - 1;
            for (int i = MAX_DIGITS - 3; i > 0; i -= 3) {
                xtriple(i, amount[counter]);
                counter--;
            }
            triple(0);
        }
    }

    private void triple(int index) {
        // Hunderter
        if (digits[index + 2] > 0) { // centaines
        
            if (digits[index + 2] > 1) { // deux cent(s), trois cent(s), ...
                                         // 200, 300
            
                syllables.add(field[0][digits[index + 2] - 1]);
                String temp = CENT;
                int sum = digits[index] + digits[index + 1] * 10;
                if ((sum == 0) && (index != 3)) {
                    temp = CENTS;     // pluriel si aucun adjectif après cent
                }
                syllables.add(temp);
            } else { // cent, cent douze, ...            
                syllables.add(CENT);
            }
            syllables.add(" ");
        }
        // Ganze Zehner
        if (digits[index] == 0) {
            int value = digits[index + 1];
            if (value > 0) {
                String temp = field[2][value - 1]; // dix, vingt, trente, ...
                                                   // 10, 20, 30, ...
                if ((value == 8) && (index != 3)) {
                    temp += PLURAL;   // quatre-vingts
                                      // 80
                }
                syllables.add(temp);
            }
        // Zehner und Einer
        } else {
            switch (digits[index + 1]) { // Zehnerstelle
                case 0: // Zehnerstelle ist Null, d. h. es gibt nur Einer
                    syllables.add(field[0][digits[index] - 1]);
                    break;
                case 1: // Values 11-19
                    syllables.add(field[1][digits[index] - 1]);
                    break;
                case 2: // 21-29
                case 3:
                case 4:
                case 5:
                case 6:
                case 8: // 81-89
                    syllables.add(field[2][digits[index + 1] - 1]);
                    if ((digits[index] == 1) && (digits[index + 1] != 8)) {
                        syllables.add(ET);
                    } else {
                        syllables.add(DASH);
                    }
                    syllables.add(field[0][digits[index] - 1]);
                    break;
                case 7: // 71-79
                case 9: // 91-99
                    syllables.add(field[2][digits[index + 1] - 2]);
                    if ((digits[index] == 1) && (digits[index + 1] != 9)) {
                        syllables.add(ET);
                    } else {
                        syllables.add(DASH);
                    }
                    syllables.add(field[1][digits[index] - 1]);
                    break;
            }
        }
    }

    // index = Begin des Tripels
    // singular = z. B. million
    private void xtriple(int index, String singular) {
        if ((digits[index] + digits[index + 1] + digits[index + 2]) > 0) {
            triple(index);
            String temp = singular;            
            int sum = digits[index] + digits[index + 1] * 10 + digits[index + 2] * 100;
            if ((sum > 1) && (index != 3)) {
                temp += PLURAL;               // pluriel, sauf pour mille
            }
            syllables.add(temp);
            syllables.add(" ");
        }
    }

    /**
     * returns number in french words
     * @param number the number to be converted
     */
    public static String toString(long number) throws Exception {
        return (new FrenchNumber(number)).toString();
    }

    public static String toString(String number) throws Exception {
        return (new FrenchNumber(number)).toString();
    }
}
