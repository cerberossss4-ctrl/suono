package com.cerberossss4.ctrl.suono.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL

class JamendoRepository(private val clientId: String) {
    suspend fun search(query: String): List<Track> = withContext(Dispatchers.IO) {
        require(clientId.isNotBlank()) {
            "Aggiungi il tuo Client ID Jamendo per cercare musica. Vedi README.md."
        }

        val params = buildList {
            add("client_id=${encode(clientId)}")
            add("format=json")
            add("limit=30")
            add("audioformat=mp31")
            add("include=musicinfo")
            add("imagesize=300")
            if (query.isBlank()) {
                add("order=popularity_total")
            } else {
                add("namesearch=${encode(query.trim())}")
            }
        }.joinToString("&")
        val connection = (URL("https://api.jamendo.com/v3.0/tracks/?$params").openConnection() as HttpURLConnection)
        connection.requestMethod = "GET"
        connection.connectTimeout = 12_000
        connection.readTimeout = 12_000
        connection.setRequestProperty("Accept", "application/json")

        try {
            val status = connection.responseCode
            val body = (if (status in 200..299) connection.inputStream else connection.errorStream)
                ?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (status !in 200..299) {
                throw IllegalStateException("Jamendo ha risposto con errore HTTP $status.")
            }
            val root = JSONObject(body)
            if (root.optString("headers").contains("error", ignoreCase = true)) {
                throw IllegalStateException("La richiesta al catalogo Jamendo non è riuscita.")
            }
            val results = root.optJSONArray("results") ?: return@withContext emptyList()
            buildList {
                for (index in 0 until results.length()) {
                    val item = results.optJSONObject(index) ?: continue
                    val audio = item.optString("audio")
                    if (audio.isBlank() || audio == "null") continue
                    add(
                        Track(
                            id = item.optString("id"),
                            title = item.optString("name", "Brano senza titolo"),
                            artist = item.optString("artist_name", "Artista sconosciuto"),
                            album = item.optString("album_name", "Singolo"),
                            durationSeconds = item.optInt("duration", 0),
                            coverUrl = item.optString("image", ""),
                            audioUrl = audio,
                            licenseUrl = item.optString("license_ccurl", "https://creativecommons.org/")
                        )
                    )
                }
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun encode(value: String): String = URLEncoder.encode(value, Charsets.UTF_8.name())
}
