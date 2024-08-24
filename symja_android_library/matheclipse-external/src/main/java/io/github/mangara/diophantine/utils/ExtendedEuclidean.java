/*
 * Copyright 2022 Sander Verdonschot <sander.verdonschot at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.mangara.diophantine.utils;

import io.github.mangara.diophantine.XYPair;
import java.math.BigInteger;

public class ExtendedEuclidean {

    /**
     * Returns a pair of numbers (x, y) such that
     *  x a - y b = gcd(a, b).
     * 
     * This is the "smallest" such pair, with {@literal |x| <= |b/gcd(a, b)|}
     * and {@literal |y| <= |a/gcd(a, b)|}.
     * 
     * @param a
     * @param b
     * @return a pair (x, y) such that x a - y b = gcd(a, b)
     */
    public static XYPair gcdPair(BigInteger a, BigInteger b) {
        BigInteger prevR = a, curR = b;
        BigInteger prevS = BigInteger.ONE, curS = BigInteger.ZERO;
        BigInteger prevT = BigInteger.ZERO, curT = BigInteger.ONE;
        BigInteger temp;
        
        while (curR != BigInteger.ZERO) {
            BigInteger q = prevR.divide(curR);
            
            temp = curR;
            curR = prevR.subtract(q.multiply(curR));
            prevR = temp;
            
            temp = curS;
            curS = prevS.subtract(q.multiply(curS));
            prevS = temp;
            
            temp = curT;
            curT = prevT.subtract(q.multiply(curT));
            prevT = temp;
        }
        
        return new XYPair(prevS, prevT);
    }

}
