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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import javax.sound.sampled.*;

public class AudioThread extends Thread {

    /**
     * Creates a new instance of AudioThread
     */
    private ArrayList<String> syllables = null;
    private String dir = null;
    private Properties props = null;
    int waitAfterASyllable = 0;

    public AudioThread(ArrayList<String> syllables, String dir) {
        this.syllables = syllables;
        this.dir = dir;
        loadProperties();
    }

    public void setWaitAfterASyllable(int wait) {
        waitAfterASyllable = wait;
    }

    @Override
    public void run() {
        for (String syllable : syllables) {
            if (syllable.equals("-") || syllable.equals(" ") || syllable.length() == 0) {
                continue;
            }
            if (isInterrupted()) {
                break;
            }
            String filename = syllable.trim();
            if (props.getProperty("allfilenamesarelowercase", "false").equals("true")) {
                filename = filename.toLowerCase(Locale.US);
            }
            playAndWait("/data/sounds/" + dir + "/" + filename + props.get("format"));
        }
    }

    private void loadProperties() {
        props = new Properties();
        props.setProperty("format", ".wav"); // default
        try {
            InputStream is = getClass().getResourceAsStream("/data/sounds/" + dir + "/info");
            if (is != null) {
                props.load(is);
            } else {
                props.setProperty("allfilenamesarelowercase", "true"); // default becomes true only if info-file is not there
            }
        } catch (IOException e) {            
            System.err.println(e);
        }
    }

    private void playAndWait(String sound) {
        try {
            // From file
            //System.out.println(sound);
            AudioInputStream stream = AudioSystem.getAudioInputStream(getClass().getResource(sound));

            // At present, ALAW and ULAW encodings must be converted
            // to PCM_SIGNED before it can be played
            AudioFormat format = stream.getFormat();
            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
//          System.out.println("convert to PCM_SIGNED");
                format = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        format.getSampleRate(),
                        format.getSampleSizeInBits() * 2,
                        format.getChannels(),
                        format.getFrameSize() * 2,
                        format.getFrameRate(),
                        true);        // big endian
                stream = AudioSystem.getAudioInputStream(format, stream);
            }

            // Create the clip
            Line.Info info = new Line.Info(Clip.class);
            Clip clip = (Clip) AudioSystem.getLine(info);

            // This method does not return until the audio file is completely loaded
            clip.open(stream);
            stream.close();

            // get rid of all the Direct Clip daemon threads
            // after the sound has been played
            clip.addLineListener(new LineListener() {

                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        event.getLine().close();
                    }
                }
            });
//          System.out.println("playing: " + sound);

            // set volume
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            double gain = 1.0;    // number between 0 and 1 (loudest)
            float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);

            // calculate the duration
            double durationInSecs = clip.getBufferSize()
                    / (clip.getFormat().getFrameSize() * clip.getFormat().getFrameRate());

            // Start playing
            clip.start();

            // wait until the sound has been played completely
            try {
                Thread.sleep((int) ((durationInSecs * 1000) + waitAfterASyllable));
            } catch (InterruptedException e) {
                interrupt();
            }
   
        } catch (IOException e) {
            System.out.println(e);
        } catch (LineUnavailableException e) {
            System.out.println(e);
        } catch (UnsupportedAudioFileException e) {
            System.out.println(e);
        }
    }
}
