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

// verified my implementation by using that excellent source at
// http://de.ungarischesprache.wikia.com/wiki/Die_Zahlen

public class HungarianNumber extends SpokenNumber {

    private final static String
            MINUS = "mínusz",
            NULL = "nulla",
            
            _1to9[] = { "egy", "kettő", "három", "négy", "öt", "hat", "hét", "nyolc", "kilenc" },
            _10to90[] = { "tíz", "húsz", "harminc", "negyven", "ötven", "hatvan", "hetven", "nyolcvan", "kilencven" },
            _11to99PREFIX[] = { "tizen", "huszon", _10to90[2], _10to90[3], _10to90[4], _10to90[5], _10to90[6], _10to90[7], _10to90[8] },
            _100and1000PREFIX[] = { _1to9[0], "két", _1to9[2], _1to9[3], _1to9[4], _1to9[5], _1to9[6], _1to9[7], _1to9[8] },                       
            HUNDRED = "száz",
            
            LARGE[] = {
                "ezer",
                "millió",
                "milliárd",
                "billió",
                "billiárd",
                "trillió",
                "trilliárd"
            };

    public HungarianNumber() {
        super();
    }

    public HungarianNumber(long number) throws Exception {
        super(number);
    }

    public HungarianNumber(String number) throws Exception {
        super(number);
    }

    @Override
    protected int getSupportedDigits() {
        return LARGE.length * 3 + 3;
    }

    @Override
    public String getSoundDir() {
        return "hungarian";
    }

    /**
     * wird zum Konvertieren der Zahl in Silben benötigt, Ergebnis wird in einen
     * Vektor geschrieben.
     */
    @Override
    protected void convert2Syllables() throws Exception {
        // prefix
        if (number.charAt(0) == '-') {
            number = number.substring(1);
            syllables.add(MINUS + " ");
        }

        // digits wanted?
        if (numberType == DIGITS) {
            fillSyllables(NULL, _1to9);
            return;
        }

        // number wanted?
        number2digits();
        if (number.equals("0")) {
            syllables.add(NULL);
            return;
        }
        
        int counter = LARGE.length - 1;
        for (int i = getSupportedDigits() - 3; i > 0; i -= 3) {
            xtriple(i, LARGE[counter]);
            counter--;
        }       
        triple(0);
        
    }

    private void xtriple(int index, String x) {
        int sum = digits[index + 2]*100 + digits[index + 1]*10 + digits[index];

        int ff = 0;
        for (int i = 0; i < index; i++) {
            ff += digits[i];
        }
        if (sum > 0) {
            triple(index);
            syllables.add(x);
            if ((sum > 1) && (ff > 0)) {
                syllables.add("-");
            }
        }
    }
    
    private void triple(int index) {
        String[] prefix;
        if (index == 0) {
            prefix = _1to9;
        } else {
            prefix = _100and1000PREFIX;
        }
        // Hunderter
        if (digits[index + 2] > 0) { // 1XX, 2XX, ..., 9XX
            syllables.add(_100and1000PREFIX[digits[index + 2] - 1]);
            syllables.add(HUNDRED);
        }
        // Zenhner
        if ((digits[index + 1] > 0) && (digits[index + 0] == 0)) { // 10, 20, ..., 90
            syllables.add(_10to90[digits[index + 1] - 1]);
        }
        if ((digits[index + 1] > 0) && (digits[index + 0] > 0)) { // 11, 12, ... 99
            syllables.add(_11to99PREFIX[digits[index + 1] - 1]);
            syllables.add(prefix[digits[index + 0] - 1]);
        }
        // Einer
        if ((digits[index + 1] == 0) && (digits[index + 0] > 0)) { // 01, ..., 09
            syllables.add(prefix[digits[index + 0] - 1]);
        }        
    }

    /**
     * gibt Nummer in ungarischen Worten zurück.
     *
     * @param number die zu konvertierende Zahl
     */
    public static String toString(long number) throws Exception {
        return (new HungarianNumber(number)).toString();
    }

    public static String toString(String number) throws Exception {
        return (new HungarianNumber(number)).toString();
    }
}
