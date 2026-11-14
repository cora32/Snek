package io.iskopasi.snek.platform

import java.security.SecureRandom

actual fun IntRange.moreRandom(): Int {
    val secureRandom = SecureRandom()

    return secureRandom.nextInt((this.last - this.first) + 1) + this.first
}