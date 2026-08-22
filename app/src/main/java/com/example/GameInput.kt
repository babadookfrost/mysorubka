package com.example

import androidx.compose.ui.geometry.Offset
import kotlin.math.sqrt

object GameInput {
    fun updateJoystick(
        position: Offset,
        centerX: Float,
        centerY: Float,
        gameState: GameState
    ) {
        val dx = position.x - centerX
        val dy = position.y - centerY
        val dist = sqrt(dx * dx + dy * dy)
        val maxR = 50f
        if (dist > maxR) {
            gameState.joystickDX = dx / dist
            gameState.joystickDY = dy / dist
        } else if (dist > 5f) {
            gameState.joystickDX = dx / maxR
            gameState.joystickDY = dy / maxR
        } else {
            gameState.joystickDX = 0f
            gameState.joystickDY = 0f
        }
    }
}
