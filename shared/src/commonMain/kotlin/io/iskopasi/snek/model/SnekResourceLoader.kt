package io.iskopasi.snek.model


class SnekResourceLoader {
    fun calculateImageNameMap(
        segments: List<Segment>,
        stepState: StepState,
    ): Map<Int, String> {
        val result = mutableMapOf<Int, String>()

        for (i in segments.indices) {
            val segment = segments[i]

            val characterName = charMap[i]
            val direction = segment.getDirection()

            result[i] =
                "${characterName}_${direction.name.lowercase()}_${stepState.name.lowercase()}"
        }

        return result
    }
}