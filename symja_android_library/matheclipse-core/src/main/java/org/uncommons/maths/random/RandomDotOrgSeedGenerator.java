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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Connects to the <a href="http://www.random.org" target="_top">random.org</a>
 * website (via HTTPS) and downloads a set of random bits to use as seed data.  It
 * is generally better to use the {@link DevRandomSeedGenerator} where possible,
 * as it should be much quicker. This seed generator is most useful on Microsoft
 * Windows and other platforms that do not provide {@literal /dev/random}.
 * @author Daniel Dyer
 */
public class RandomDotOrgSeedGenerator implements SeedGenerator
{
    private static final String BASE_URL = "https://www.random.org";

    /** The URL from which the random bytes are retrieved. */
    private static final String RANDOM_URL = BASE_URL + "/integers/?num={0,number,0}&min=0&max=255&col=1&base=16&format=plain&rnd=new";

    /** Used to identify the client to the random.org service. */
    private static final String USER_AGENT = RandomDotOrgSeedGenerator.class.getName();

    /** Random.org does not allow requests for more than 10k integers at once. */
    private static final int MAX_REQUEST_SIZE = 10000;

    private static final Lock cacheLock = new ReentrantLock();
    private static byte[] cache = new byte[1024];
    private static int cacheOffset = cache.length;

    /**
     * {@inheritDoc}
     */    
    public byte[] generateSeed(int length) throws SeedException
    {
        byte[] seedData = new byte[length];
        try
        {
            cacheLock.lock();
            int count = 0;
            while (count < length)
            {
                if (cacheOffset < cache.length)
                {
                    int numberOfBytes = Math.min(length - count, cache.length - cacheOffset);
                    System.arraycopy(cache, cacheOffset, seedData, count, numberOfBytes);
                    count += numberOfBytes;
                    cacheOffset += numberOfBytes;
                }
                else
                {
                    refreshCache(length - count);
                }
            }
        }
        catch (IOException ex)
        {
            throw new SeedException("Failed downloading bytes from " + BASE_URL, ex);
        }
        catch (SecurityException ex)
        {
            // Might be thrown if resource access is restricted (such as in an applet sandbox).
            throw new SeedException("SecurityManager prevented access to " + BASE_URL, ex);
        }
        finally
        {
            cacheLock.unlock();
        }
        return seedData;
    }


    /**
     * @param requiredBytes The preferred number of bytes to request from random.org.
     * The implementation may request more and cache the excess (to avoid making lots
     * of small requests).  Alternatively, it may request fewer if the required number
     * is greater than that permitted by random.org for a single request.
     * @throws IOException If there is a problem downloading the random bits.
     */
    private void refreshCache(int requiredBytes) throws IOException
    {
        int numberOfBytes = Math.max(requiredBytes, cache.length);
        numberOfBytes = Math.min(numberOfBytes, MAX_REQUEST_SIZE);
        if (numberOfBytes != cache.length)
        {
            cache = new byte[numberOfBytes];
            cacheOffset = numberOfBytes;
        }
        URL url = new URL(MessageFormat.format(RANDOM_URL, numberOfBytes));
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", USER_AGENT);
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        
        try
        {
            int index = -1;
            for (String line = reader.readLine(); line != null; line = reader.readLine())
            {
                cache[++index] = (byte) Integer.parseInt(line, 16);
            }
            if (index < cache.length - 1)
            {
                throw new IOException("Insufficient data received.");
            }
            cacheOffset = 0;
        }
        finally
        {
            reader.close();
        }
    }


    @Override
    public String toString()
    {
        return BASE_URL;
    }
}
