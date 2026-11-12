package com.cerberossss4.ctrl.suono.data

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationLabel: String,
    val coverUrl: String,
    val audioUrl: String
)
