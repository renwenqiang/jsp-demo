//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core;

import java.util.UUID;

public class GuidGenerator {
    public GuidGenerator() {
    }

    public static String getGuid() {
        UUID guid = UUID.randomUUID();
        long mostSigBits = guid.getMostSignificantBits();
        long leastSigBits = guid.getLeastSignificantBits();
        long h1 = 4294967296L;
        long h2 = 65536L;
        long h5 = 281474976710656L;
        return Long.toHexString(h1 | mostSigBits >> 32 & h1 - 1L).substring(1) + Long.toHexString(h2 | mostSigBits >> 16 & h2 - 1L).substring(1) + Long.toHexString(h2 | mostSigBits & h2 - 1L).substring(1) + Long.toHexString(h2 | leastSigBits >> 48 & h2 - 1L).substring(1) + Long.toHexString(h5 | leastSigBits & h5 - 1L).substring(1);
    }
}
