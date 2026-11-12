package com.cerberossss4.ctrl.suono

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.cerberossss4.ctrl.suono.data.JamendoRepository
import com.cerberossss4.ctrl.suono.data.Track
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { SuonoApp() } }
    }
}

@Composable
fun SuonoApp() {
    val context = LocalContext.current
    val repository = remember { JamendoRepository(BuildConfig.JAMENDO_CLIENT_ID) }
    val player = remember { ExoPlayer.Builder(context).build() }
    val scope = rememberCoroutineScope()
    var tracks by remember { mutableStateOf(emptyList<Track>()) }
    var selectedTrack by remember { mutableStateOf<Track?>(null) }
    var query by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    fun searchTracks(searchText: String) {
        scope.launch {
            isLoading = true
            message = null
            try {
                tracks = repository.search(searchText)
                if (tracks.isEmpty()) message = "Nessun brano trovato. Prova un altro artista o titolo."
            } catch (error: Exception) {
                message = error.message ?: "Impossibile caricare il catalogo. Controlla la connessione."
            } finally {
                isLoading = false
            }
        }
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) { isPlaying = playing }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(Unit) { searchTracks("") }

    Scaffold(
        containerColor = Color(0xFF0F172A),
        bottomBar = {
            selectedTrack?.let { track ->
                PlayerBar(
                    track = track,
                    isPlaying = isPlaying,
                    onPlayPause = { if (player.isPlaying) player.pause() else player.play() },
                    onPrevious = {
                        val index = tracks.indexOfFirst { it.id == track.id }
                        if (tracks.isNotEmpty()) playTrack(tracks[(index - 1 + tracks.size) % tracks.size], player) { selectedTrack = it }
                    },
                    onNext = {
                        val index = tracks.indexOfFirst { it.id == track.id }
                        if (tracks.isNotEmpty()) playTrack(tracks[(index + 1) % tracks.size], player) { selectedTrack = it }
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Suono", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text("Scopri musica indipendente su Jamendo", color = Color(0xFFCBD5E1))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Artista o brano") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
                Button(onClick = { searchTracks(query) }, enabled = !isLoading) { Text("Cerca") }
            }
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = Color(0xFFA78BFA))
            }
            message?.let {
                Text(it, color = if (BuildConfig.JAMENDO_CLIENT_ID.isBlank()) Color(0xFFFCA5A5) else Color(0xFFCBD5E1))
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(tracks, key = { it.id }) { track ->
                    TrackRow(track, selectedTrack?.id == track.id) {
                        playTrack(track, player) { selectedTrack = it }
                    }
                }
                item {
                    Text(
                        "Musica fornita dal catalogo Jamendo. Disponibilità e condizioni d’uso dipendono dalla licenza di ciascun brano e dai termini API.",
                        color = Color(0xFF94A3B8), fontSize = 11.sp,
                        modifier = Modifier.padding(vertical = 14.dp)
                    )
                }
            }
        }
    }
}

private fun playTrack(track: Track, player: ExoPlayer, onSelected: (Track) -> Unit) {
    onSelected(track)
    player.setMediaItem(MediaItem.fromUri(track.audioUrl))
    player.prepare()
    player.play()
}

@Composable
private fun TrackRow(track: Track, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = if (selected) Color(0xFF312E81) else Color(0xFF111827))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = track.coverUrl,
                contentDescription = "Copertina di ${track.album}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(58.dp).clip(RoundedCornerShape(12.dp))
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(track.title, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(track.artist, color = Color(0xFFCBD5E1), fontSize = 13.sp)
                Text(track.album, color = Color(0xFF94A3B8), fontSize = 12.sp)
            }
            Text(track.durationLabel, color = Color(0xFFCBD5E1), fontSize = 12.sp)
        }
    }
}

@Composable
private fun PlayerBar(
    track: Track,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AsyncImage(
                    model = track.coverUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(46.dp).clip(RoundedCornerShape(10.dp))
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(track.title, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(track.artist, color = Color(0xFFCBD5E1), fontSize = 12.sp)
                }
                IconButton(onClick = onPrevious) { Icon(Icons.Default.SkipPrevious, "Precedente", tint = Color.White) }
                IconButton(onClick = onPlayPause, modifier = Modifier.size(48.dp).background(Color(0xFF8B5CF6), CircleShape)) {
                    Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, "Riproduci o metti in pausa", tint = Color.White)
                }
                IconButton(onClick = onNext) { Icon(Icons.Default.SkipNext, "Successivo", tint = Color.White) }
            }
            TextButtonLink(track.licenseUrl)
        }
    }
}

@Composable
private fun TextButtonLink(url: String) {
    Text(
        text = "Licenza del brano: ${url.substringBefore("?")}",
        color = Color(0xFFA5B4FC),
        fontSize = 10.sp,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}
