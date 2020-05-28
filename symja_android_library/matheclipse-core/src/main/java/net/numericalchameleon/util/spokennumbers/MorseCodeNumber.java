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

import java.util.ArrayList;
import net.numericalchameleon.util.phoneticalphabets.PhoneticAlphabet;

public class MorseCodeNumber extends SpokenNumber {

    private PhoneticAlphabet phoneticAlphabet;

    /** Creates a new instance of MorseCodeNumber */
    public MorseCodeNumber() {
        super();
    }

    public MorseCodeNumber(String number) throws Exception {
        super(number);  
    }
    
    public MorseCodeNumber(long number) throws Exception {
        super(number);
    }
    
    @Override
    protected void init() {
        super.init();
        appendBlank = true;
        phoneticAlphabet = new PhoneticAlphabet(PhoneticAlphabet.MORSE);        
    }

    @Override
    protected void convert2Syllables() throws Exception {
        if (number.charAt(0) == '-') {
            number = number.substring(1);
            syllables.add(phoneticAlphabet.get('-'));
        }

        if (number.equalsIgnoreCase("0")) {
            syllables.add(phoneticAlphabet.get('0'));
        } else {
            for (int i = 0; i < number.length(); i++) {
                String temp = phoneticAlphabet.get(number.charAt(i));
                syllables.add(temp);
            }
        }
    }

    @Override
    public String getSoundDir() {
        return "morse";
    }

    @Override
    protected int getSupportedDigits() {
        return 1000; // should be enough
    }

    @Override
    public void play() {
        ArrayList<String> playvec = new ArrayList<String>();
        int x;
        for (int i = 0; i < syllables.size(); i++) {
            String temp = (String) (syllables.get(i));
            for (x = 0; x < temp.length(); x++) {
                if (temp.charAt(x) == '.') {
                    playvec.add("dot");
                } else if (temp.charAt(x) == '-') {
                    playvec.add("dash");
                }
                // else do nothing with blanks
            }
        }

        if (audioThread != null) { // stop any old threads
            audioThread.interrupt();
            while (audioThread.isAlive()) {
                // wait for a while
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
            }
        }
        audioThread = new AudioThread(playvec, getSoundDir());
        audioThread.start();
    }
}
