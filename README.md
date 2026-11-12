package com.cerberossss4.ctrl.suono.data

object DemoCatalog {
    fun tracks(): List<Track> = listOf(
        Track(
            id = "night-drive",
            title = "Night Drive",
            artist = "Luna Vale",
            album = "Neon Skyline",
            durationLabel = "3:24",
            coverUrl = "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=800&q=80",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        ),
        Track(
            id = "sunrise-signal",
            title = "Sunrise Signal",
            artist = "Aster Bloom",
            album = "Pacific Echo",
            durationLabel = "4:12",
            coverUrl = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=800&q=80",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
        ),
        Track(
            id = "midnight-echo",
            title = "Midnight Echo",
            artist = "Nadir Echo",
            album = "Velvet Horizon",
            durationLabel = "2:48",
            coverUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?auto=format&fit=crop&w=800&q=80",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
        )
    )
}
