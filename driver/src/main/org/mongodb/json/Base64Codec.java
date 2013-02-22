/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.json;

/**
 * Provides Base64 encoding and decoding </a>. <p/> <p> This class implements Base64 encoding <p/> Thanks to Apache
 * Commons project. This class refactored from org.apache.commons.codec.binary <p/> Original Thanks to "commons" project
 * in ws.apache.org for this code. http://svn.apache.org/repos/asf/webservices/commons/trunk/modules/util/ </p>
 */
class Base64Codec {

    private static final int BYTES_PER_UNENCODED_BLOCK = 3;
    private static final int BYTES_PER_ENCODED_BLOCK = 4;

    /**
     * Mask used to extract 6 bits, used when encoding
     */
    private static final int SIX_BIT_MASK = 0x3f;

    /**
     * padding char
     */
    private static final byte PAD = '=';

    /**
     * This array is a lookup table that translates 6-bit positive integer index values into their "Base64 Alphabet"
     * equivalents as specified in Table 1 of RFC 2045.
     */
    private static final byte[] ENCODE_TABLE = {'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
            'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
            't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', '+', '/'};


    public String encode(final byte[] in) {

        int modulus = 0;
        int bitWorkArea = 0;
        final int numEncodedBytes = (in.length / BYTES_PER_UNENCODED_BLOCK) * BYTES_PER_ENCODED_BLOCK
                + ((in.length % BYTES_PER_UNENCODED_BLOCK == 0) ? 0 : 4);

        final byte[] buffer = new byte[numEncodedBytes];
        int pos = 0;

        for (final byte anIn : in) {
            modulus = (modulus + 1) % BYTES_PER_UNENCODED_BLOCK;
            int b = anIn;

            if (b < 0) {
                b += 256;
            }

            bitWorkArea = (bitWorkArea << 8) + b; //  BITS_PER_BYTE
            if (0 == modulus) { // 3 bytes = 24 bits = 4 * 6 bits to extract
                buffer[pos++] = ENCODE_TABLE[(bitWorkArea >> 18) & SIX_BIT_MASK];
                buffer[pos++] = ENCODE_TABLE[(bitWorkArea >> 12) & SIX_BIT_MASK];
                buffer[pos++] = ENCODE_TABLE[(bitWorkArea >> 6) & SIX_BIT_MASK];
                buffer[pos++] = ENCODE_TABLE[bitWorkArea & SIX_BIT_MASK];
            }
        }

        if (modulus == 1) {
            buffer[pos++] = ENCODE_TABLE[(bitWorkArea >> 2) & SIX_BIT_MASK]; // top 6 bits
            buffer[pos++] = ENCODE_TABLE[(bitWorkArea << 4) & SIX_BIT_MASK]; // remaining 2
            buffer[pos++] = PAD;
            buffer[pos] = PAD;
        }
        else if (modulus == 2) {
            buffer[pos++] = ENCODE_TABLE[(bitWorkArea >> 10) & SIX_BIT_MASK];
            buffer[pos++] = ENCODE_TABLE[(bitWorkArea >> 4) & SIX_BIT_MASK];
            buffer[pos++] = ENCODE_TABLE[(bitWorkArea << 2) & SIX_BIT_MASK];
            buffer[pos] = PAD;
        }

        return new String(buffer);
    }
}
