package com.curso.android.module2.stream.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.curso.android.module2.stream.data.model.Song
import com.curso.android.module2.stream.ui.components.SongCoverMock
import com.curso.android.module2.stream.ui.viewmodel.HomeUiState
import com.curso.android.module2.stream.ui.viewmodel.HomeViewModel
import androidx.compose.foundation.interaction.MutableInteractionSource

import androidx.compose.runtime.remember



/**
 * HighlightsScreen: Lista solo las canciones marcadas como favoritas.
 *
 * IMPORTANTE: Esta pantalla recibe el mismo HomeViewModel que HomeScreen
 * para compartir estado en tiempo real (favoritos sincronizados).
 */
@Composable
fun HighlightsScreen(
    viewModel: HomeViewModel,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is HomeUiState.Loading -> {
            // Puedes reutilizar tu LoadingContent() si es pública;
            // para mantener este archivo autónomo:
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))
                Text("Cargando favoritos…", style = MaterialTheme.typography.bodyMedium)
            }
        }

        is HomeUiState.Error -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Error: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        is HomeUiState.Success -> {
            // Aplana categorías y filtra solo favoritos
            val favoriteSongs = state.categories
                .flatMap { it.songs }
                .filter { it.isFavorite }

            if (favoriteSongs.isEmpty()) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Aún no tienes favoritos",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Marca canciones desde Home para verlas aquí.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = favoriteSongs,
                        key = { it.id }
                    ) { song ->
                        FavoriteSongRow(
                            song = song,
                            onClick = { onSongClick(song) }
                        )
                    }
                }
            }
        }
    }
}

/** Fila simple para mostrar favorito (portada + título + artista). */
@Composable
private fun FavoriteSongRow(
    song: Song,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp)
            .clickableNoRipple(onClick), // helper pequeño
    ) {
        // Portada
        SongCoverMock(colorSeed = song.colorSeed, size = 120.dp)

        Spacer(Modifier.height(8.dp))

        Text(
            text = song.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = song.artist,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** Clickable sin ripple visual (opcional). */


@Composable
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier {
    return this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}
