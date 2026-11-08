package io.iskopasi.snek

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform