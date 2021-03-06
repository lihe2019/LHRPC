package com.lh.compress;

import com.lh.extension.SPI;

@SPI
public interface Compress {
    byte[] compress(byte[] bytes);
    byte[] decompress(byte[] bytes);
}
