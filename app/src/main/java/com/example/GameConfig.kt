package com.example

import kotlin.math.ceil
import kotlin.math.min

// ─── Map / Tile Constants ───────────────────────────────────────────
object Cfg {
    const val TILE        = 64f
    const val ROOM_W      = 30        // tiles
    const val ROOM_H      = 30        // tiles
    const val ROOM_PX_W   = ROOM_W * TILE
    const val ROOM_PX_H   = ROOM_H * TILE
    const val WALL_BORDER = 1         // 1-tile wall border

    // Player
    const val PLAYER_R         = 13f
    const val PLAYER_BASE_SPD = 220f
    const val PLAYER_MAX_HP    = 10
    const val PLAYER_PICKUP_R  = 40f

    // Light
    const val BASE_LIGHT_R    = 220f
    const val LIGHT_STEP      = 18f   // per Time-skill rank

    // XP / levels
    const val XP_PER_LVL_BASE = 8
    const val XP_GROWTH       = 1.4f  // multiplier per level
    const val SKILL_POINTS_PER_LVL = 1

    // Bullet-time
    const val BT_BASE_SPEED_FACTOR = 0.04f  // how slow time gets when idle
    const val BT_MOVE_RECOVER_RATE = 1.0f   // time-scale when moving (before skills)

    // Spawn
    const val ENEMY_SPAWN_MIN_DIST = 260f
    const val ENEMY_SPAWN_MAX_DIST = 520f

    // Colors (ARGB hex as Long)
    const val COL_BG           = 0xFF0A0A0AL
    const val COL_WALL_A       = 0xFF1E1418L
    const val COL_WALL_B       = 0xFF1A1014L
    const val COL_FLOOR_A      = 0xFF0D0F14L
    const val COL_FLOOR_B      = 0xFF0B0D12L
    const val COL_PLAYER       = 0xFFE8DFD0L
    const val COL_PLAYER_LIGHT = 0xFFE8C840L
    const val COL_HP_BAR       = 0xFFC41E3AL
    const val COL_HP_BG        = 0xFF2A0A0AL
    const val COL_XP           = 0xFF5AD1FFL
    const val COL_BULLET_P     = 0xFFF5C518L
    const val COL_BULLET_E     = 0xFFFF4D6DL
    const val COL_AURA         = 0x33C41E3AL
    const val COL_PORTAL       = 0xFF8A5CF6L
    const val COL_SECRET       = 0xFFFFD700L
    const val COL_CHEST        = 0xFFCD8032L
    const val COL_RARITY_COMMON  = 0xFFAAAAL
    const val COL_RARITY_UNCOMMON = 0xFF4CAF50L
    const val COL_RARITY_RARE    = 0xFF2196F3L
    const val COL_RARITY_ARTIFACT = 0xFFFFD700L
}
