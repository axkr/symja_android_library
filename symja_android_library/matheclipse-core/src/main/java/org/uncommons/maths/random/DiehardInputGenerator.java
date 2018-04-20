// ============================================================================
//   Copyright 2006-2012 Daniel W. Dyer
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// ============================================================================
package org.uncommons.maths.random;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Utility to generate an input file for the
 * <a href="http://stat.fsu.edu/pub/diehard/" target="_top">DIEHARD</a> suite of statistical
 * tests for random number generators.
 * @author Daniel Dyer
 */
public final class DiehardInputGenerator
{
    // How many 32-bit values should be written to the output file.
    private static final int INT_COUNT = 3000000;

    private DiehardInputGenerator()
    {
        // Prevents instantiation.
    }


    /**
     * @param args The first argument is the class name of the RNG, the second
     * is the file to use for output.
     * @throws Exception If there are problems setting up the RNG or writing to
     * the output file.
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception
    {
        if (args.length != 2)
        {
            System.out.println("Expected arguments:");
            System.out.println("\t<Fully-qualified RNG class name> <Output file>");
            System.exit(1);
        }
        Class<? extends Random> rngClass = (Class<? extends Random>) Class.forName(args[0]);
        File outputFile = new File(args[1]);
        generateOutputFile(rngClass.newInstance(), outputFile);
    }


    /**
     * Generates a file of random data in a format suitable for the DIEHARD test.
     * DIEHARD requires 3 million 32-bit integers.
     * @param rng The random number generator to use to generate the data.
     * @param outputFile The file that the random data is written to.
     * @throws IOException If there is a problem writing to the file.
     */
    public static void generateOutputFile(Random rng,
                                          File outputFile) throws IOException
    {
        DataOutputStream dataOutput = null;
        try
        {
            dataOutput = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
            for (int i = 0; i < INT_COUNT; i++)
            {
                dataOutput.writeInt(rng.nextInt());
            }
            dataOutput.flush();
        }
        finally
        {
            if (dataOutput != null)
            {
                dataOutput.close();
            }
        }
    }
}
