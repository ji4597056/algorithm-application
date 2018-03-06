package com.github.ji4597056;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

/**
 * murmur_32_hash
 *
 * @author Jeffrey
 * @since 2018/03/06 9:54
 */
public class MurmurHash implements Hash{

    @Override
    public int getHash(String key) {
        return Hashing.murmur3_32().hashString(key, Charsets.UTF_8).asInt();
    }
}
