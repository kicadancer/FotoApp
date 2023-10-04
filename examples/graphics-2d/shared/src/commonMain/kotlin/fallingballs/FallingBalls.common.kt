package fallingballs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun FallingBalls() {
    val game = remember { Game() }
    val density = LocalDensity.current
    Column {
        Text(
            "Catch balls!${if (game.finished) " Game over!" else ""}",
            fontSize = 20.sp,
            color = Color(218, 120, 91)
        )
        Text("Score: ${game.score} Time: ${game.elapsed / 1_000_000} Blocks: ${game.numBlocks.toInt()}", fontSize = 20.sp)
        Row {
            if (!game.started) {
                Slider(
                    value = game.numBlocks / 20f,
                    onValueChange = { game.numBlocks = (it * 20f).coerceAtLeast(1f) },
                    modifier = Modifier.width(250.dp)
                )
            }
            Button(
                modifier = Modifier
                    .border(2.dp, Color(255, 215, 0))
                    .background(Color.Yellow),
                onClick = {
                    game.started = !game.started
                    if (game.started) {
                        game.start()
                    }
                }
            ) {
                Text(if (game.started) "Stop" else "Start", fontSize = 25.sp)
            }
        }
        if (game.started) {
            Box(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(1f)
                .onSizeChanged {
                    with(density) {
                        game.width = it.width.toDp()
                        game.height = it.height.toDp()
                    }
                }
            ) {
                game.pieces.forEachIndexed { index, piece -> Piece(index, piece) }
            }
        }

        LaunchedEffect(Unit) {
            while (true) {
                var previousTimeNanos = withFrameNanos { it }
                withFrameNanos {
                    if (game.started && !game.paused && !game.finished) {
                        game.update((it - previousTimeNanos).coerceAtLeast(0))
                        previousTimeNanos = it
                    }
                }
            }
        }
    }
}

