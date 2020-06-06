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

public class RussianNumber extends SpokenNumber {

    private final static String postfix1 = "\u043d\u0430\u0434\u0446\u0430\u0442\u044c"; // 11-19
    private final static String postfix2 = "\u0434\u0435\u0441\u044f\u0442"; // 50-80
    private final static String field[][] = {
        {"\u043e\u0434\u0438\u043d", "\u0434\u0432\u0430", "\u0442\u0440\u0438", "\u0447\u0435\u0442\u044c\u0456\u0440\u0435", "\u043f\u044f\u0442\u044c", "\u0448\u0435\u0441\u0442\u044c", "\u0441\u0435\u043c\u044c", "\u0432\u043e\u0441\u0435\u043c\u044c", "\u0434\u0435\u0432\u044f\u0442\u044c"},
        {"\u043e\u0434\u0438\u043d" + postfix1, "\u0434\u0432\u0435" + postfix1, "\u0442\u0440\u0438" + postfix1, "\u0447\u0435\u0442\u044c\u0456\u0440" + postfix1, "\u043f\u044f\u0442" + postfix1, "\u0448\u0435\u0441\u0442" + postfix1, "\u0441\u0435\u043c" + postfix1, "\u0432\u043e\u0441\u0435\u043c" + postfix1, "\u0434\u0435\u0432\u044f\u0442" + postfix1},
        {"\u0434\u0435\u0441\u044f\u0442\u044c", "\u0434\u0432\u0430\u0434\u0446\u0430\u0442\u044c", "\u0442\u0440\u0438\u0434\u0446\u0430\u0442\u044c", "\u0441\u043e\u0440\u043e\u043a", "\u043f\u044f\u0442\u044c" + postfix2, "\u0448\u0435\u0441\u0442\u044c" + postfix2, "\u0441\u0435\u043c\u044c" + postfix2, "\u0432\u043e\u0441\u0435\u043c\u044c" + postfix2, "\u0434\u0435\u0432\u044f\u043d\u043e\u0441\u0442\u043e"}
    },
            NULL = "\u043d\u043e\u043b\u044c",
            MINUS = "минус";

    public RussianNumber() {
        super();
    }

    public RussianNumber(long number) throws Exception {
        super(number);
    }

    public RussianNumber(String number) throws Exception {
        super(number);
    }

    @Override
    protected int getSupportedDigits() {
        return 2;
    }

    @Override
    public String getSoundDir() {
        return "russian";
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
            decode(0);
        }
    }

    private void decode(int index) {
        int x = (digits[index + 1] * 10) + digits[index];
        if (x < 10) {  // 1-9
            syllables.add(field[0][x - 1]);
        } else if ((x > 10) && (x < 20)) { // 11-19
            syllables.add(field[1][x - 11]);
        } else { // 10, 20-99
            syllables.add(field[2][digits[index + 1] - 1]);

            if (digits[index] > 0) {
                syllables.add(" " + field[0][digits[index] - 1]);
            }
        }
    }

    /**
     * gibt Nummer in russischen Worten zurück.
     * @param number die zu konvertierende Zahl
     */
    public static String toString(long number) throws Exception {
        return (new RussianNumber(number)).toString();
    }

    public static String toString(String number) throws Exception {
        return (new RussianNumber(number)).toString();
    }
}
