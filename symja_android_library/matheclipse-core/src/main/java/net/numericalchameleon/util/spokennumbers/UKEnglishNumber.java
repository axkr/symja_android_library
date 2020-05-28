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

import java.util.Arrays;

public class UKEnglishNumber extends SpokenNumber {
    
    protected static final String[][] field = {
        {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine"},
        {"eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"},
        {"ten", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"}
    };
    
    protected static final String            
            NULL = "zero",
            MINUS = "minus",
            AND = "and",
            HUNDRED = "hundred",
            DASH = "-";

    private static final String[] amountUK = {"thousand",
        "million", "thousand million",
        "billion", "thousand billion",
        "trillion", "thousand trillion",
        "quadrillion", "thousand quadrillion",
        "quintillion", "thousand quintillion",
        "sextillion", "thousand sextillion",
        "septillion", "thousand septillion",
        "octillion", "thousand octillion",
        "nonillion", "thousand nonillion",
        "decillion", "thousand decillion",
        "undecillion", "thousand undecillion",
        "duodecillion", "thousand duodecillion",
        "tredecillion", "thousand tredecillion",
        "quattuordecillion", "thousand quattuordecillion",
        "quindecillion", "thousand quindecillion"
    };
      

    public UKEnglishNumber() {
        super();
    }

    public UKEnglishNumber(long number) throws Exception {
        super(number);
    }

    public UKEnglishNumber(String number) throws Exception {
        super(number);
    }

    @Override
    protected int getSupportedDigits() {
        return 96;
    }

    @Override
    public String getSoundDir() {
        return "uk_english";
    }

    /**
     * wird zum Konvertieren der Zahl in Silben benötigt,
     * Ergebnis wird in einen Vektor geschrieben.
     */
    @Override
    protected void convert2Syllables() throws Exception {
        convert2Syllables(amountUK);
    }
    
    protected void convert2Syllables(String[] amount) {
        if (number.charAt(0) == '-') {
            number = number.substring(1);
            syllables.add(MINUS);
        }

        if (numberType == NUMBER) {
            number2digits();
            // die Zahlentriple abarbeiten
            if (number.equals("0")) {
                syllables.add(NULL);
            } else {
                int counter = amount.length - 1;
                for (int i = MAX_DIGITS - 3; i > 0; i -= 3) {
                    xtriple(i, amount[counter--]);
                }
                triple(0);
            }
        } else if (numberType == DIGITS) {
            fillSyllables(NULL, field[0]);
        }
        
    }

    // bei Zahlen über 100 steht vor Zehnern und Einern "and"
    protected boolean isGreaterThan100() {
        for (int i = 2; i < MAX_DIGITS; i++) {
            if (digits[i] > 0) {
                return true;
            }
        }
        return false;
    }

    protected void triple(int index) {
        if (digits[index + 2] > 0) {
            syllables.add(field[0][digits[index + 2] - 1]);
            syllables.add(HUNDRED);
        }

        if ((index == 0) // only valid for index==0
                && ((digits[0] + digits[1]) != 0) // only if value > 0
                && isGreaterThan100() // and > 100
                ) {
            syllables.add(AND);
        }

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
                if (digits[index] > 0) {                                 // XXXX21
                    syllables.add(field[0][digits[index] - 1]); // ein, zwei, drei, ...
                }
            }
        }
    }

    protected void xtriple(int index, String singular) {
        if ((digits[index] + digits[index + 1] + digits[index + 2]) > 0) {
            triple(index);
            if (singular.indexOf(" ") > 0) {
                String[] array = singular.split(" ");
                syllables.addAll(Arrays.asList(array));
            } else
            syllables.add(singular);
        }
    }

    // add spaces between syllables
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean wasDash = false;
        for (int i = 0; i < syllables.size(); i++) {
            if (i > 0) {
                boolean isDash = syllables.get(i).equals(DASH);
                if (!wasDash && !isDash) {
                    sb.append(" ");
                }
                wasDash = isDash;
            }
            sb.append(syllables.get(i).toString());
        }
        return sb.toString();
    }

    /**
     * gibt Nummer in UK-englischen Worten zurück.
     * @param number die zu konvertierende Zahl
     */
    public static String toString(long number) throws Exception {
        return (new UKEnglishNumber(number)).toString();
    }

    public static String toString(String number) throws Exception {
        return (new UKEnglishNumber(number)).toString();
    }
}
