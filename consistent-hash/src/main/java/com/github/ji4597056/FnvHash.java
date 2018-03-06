package com.github.ji4597056;

/**
 * fnv1_32_hash
 *
 * @author Jeffrey
 * @since 2018/03/06 1:20
 */
public class FnvHash implements Hash {

    @Override
    public int getHash(String key) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < key.length(); i++) {
            hash = (hash ^ key.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        // hash >= 0
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }
}
