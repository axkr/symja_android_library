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
package net.numericalchameleon.util.spokentime;

// Good Reference:
// http://www.artofmanliness.com/2012/10/11/how-to-tell-time-like-a-soldier/
class MilitaryTime {
    
    private MilitaryTimeInterface mti = null;

    public MilitaryTime(MilitaryTimeInterface mti, int hours, int minutes) {
        this.mti = mti;
        if (minutes == 0) {
            // hours
            if (hours > 0 && hours < 10) {
                // timeInWords = String.format("%s %s",  mti.numberToWords(0), mti.numberToWords(hours*100));
                mti.numberToWords(0);
                mti.separator();
                mti.numberToWords(hours);
                mti.separator();
                mti.hundredString();
            } else { // examples: 0000 or 1400
                // timeInWords = String.format("%s %s", mti.numberToWords(hours), mti.getHundredString());
                mti.numberToWords(hours);
                mti.separator();
                mti.hundredString();
            }
        } else {
            // hours
            if (hours < 10) {
                mti.numberToWords(0);
                mti.separator();
            }
            mti.numberToWords(hours);
            mti.separator();
                
            // minutes
            if (minutes < 10) {
                mti.numberToWords(0);
                mti.separator();
            }
            mti.numberToWords(minutes);
        }
    }
}
