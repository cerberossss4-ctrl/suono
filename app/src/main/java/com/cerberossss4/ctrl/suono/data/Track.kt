package com.cerberossss4.ctrl.suono.data

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationSeconds: Int,
    val coverUrl: String,
    val audioUrl: String,
    val licenseUrl: String
) {
    val durationLabel: String
        get() = "%d:%02d".format(durationSeconds / 60, durationSeconds % 60)
}
