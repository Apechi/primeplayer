package com.theprime.primegoplayer.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Sorting(
    val albumOrFolder: String?,
    val launchedBy: String,
    val songVisualization: String?,
    val sorting: Int
    )
