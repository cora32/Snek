package io.iskopasi.snek.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import snek.shared.generated.resources.Res

// Usage: loadBitmapByName("cirno_sprite_down_idle")
suspend fun loadBitmapByName(resName: String): ImageBitmap? {
    return try {
        val bytes = Res.readBytes("drawable/$resName.png")
        bytes.decodeToImageBitmap()
    } catch (ex: Exception) {
        ex.printStackTrace()
        null
    }
}