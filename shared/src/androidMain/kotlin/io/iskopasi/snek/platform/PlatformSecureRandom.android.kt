package io.iskopasi.snek.platform

actual fun IntRange.moreRandom(): Int {
    return this.random()
}