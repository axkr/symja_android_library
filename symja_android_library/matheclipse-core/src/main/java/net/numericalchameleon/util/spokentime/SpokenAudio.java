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

import java.util.ArrayList;
import net.numericalchameleon.util.spokennumbers.AudioThread;

public abstract class SpokenAudio {
    
    abstract public String getSoundDir();
    
    protected AudioThread audioThread = null;
    protected ArrayList<String> syllables = new ArrayList<String>(); // Silbenvektor
    protected int waitAfterASyllable = 0;
        
    public void play() {
        String dir = getSoundDir();
        if (dir != null) {
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
            audioThread = new AudioThread(syllables, dir);
            audioThread.setWaitAfterASyllable(waitAfterASyllable);
            audioThread.start();
        }
    }

    public void stopPlaying() {
        if (audioThread != null) {
            audioThread.interrupt();
        }
    }

}
