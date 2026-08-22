package com.example

// ─── Enemy type definitions ─────────────────────────────────────────
data class EnemyDef(
    val id: String,
    val name: String,
    val hp: Int,
    val speed: Float,
    val r: Float,
    val dmg: Int = 1,
    val xp: Int = 1,
    val fireCd: Float = 0f,      // 0 = melee only
    val bulletSpeed: Float = 0f,
    val burstCount: Int = 0,     // boss burst shots
    val isBoss: Boolean = false,
    val color: Long = 0xFFC41E3AL
)

object Enemies {
    val SHAMBLER = EnemyDef(
        id = "shambler", name = "Шарлотт",
        hp = 2, speed = 90f, r = 14f, dmg = 1, xp = 1,
        color = 0xFF8B2020L
    )
    val RUNNER = EnemyDef(
        id = "runner", name = "Скиталец",
        hp = 1, speed = 200f, r = 12f, dmg = 1, xp = 2,
        color = 0xFFAA3030L
    )
    val CULTIST = EnemyDef(
        id = "cultist", name = "Культист",
        hp = 3, speed = 60f, r = 14f, dmg = 1, xp = 2,
        fireCd = 1.8f, bulletSpeed = 400f,
        color = 0xFF6A1B5EL
    )
    val TANK = EnemyDef(
        id = "tank", name = "\u0422\u0430\u043d\u043a",
        hp = 8, speed = 55f, r = 20f, dmg = 2, xp = 4,
        color = 0xFF3A2850L
    )
    val SPITTER = EnemyDef(
        id = "spitter", name = "Плевок",
        hp = 2, speed = 75f, r = 13f, dmg = 1, xp = 2,
        fireCd = 2.2f, bulletSpeed = 300f,
        color = 0xFF4A7A20L
    )
    val SWARM = EnemyDef(
        id = "swarm", name = "Рой",
        hp = 1, speed = 140f, r = 8f, dmg = 1, xp = 1,
        color = 0xFF666666L
    )
    val CHARGER = EnemyDef(
        id = "charger", name = "Волк",
        hp = 1, speed = 250f, r = 13f, dmg = 1, xp = 2,
        color = 0xFFC9901EL
    )
    val BRUTE = EnemyDef(
        id = "brute", name = "Громила",
        hp = 9, speed = 45f, r = 26f, dmg = 3, xp = 6,
        color = 0xFF7A5A3EL
    )

    // Elite modifiers
    data class EliteMod(
        val id: String, val name: String,
        val hpMul: Float = 1f, val spdMul: Float = 1f,
        val dmgMul: Float = 1f, val xpMul: Float = 2f,
        val dot: Int = 0, val dotDur: Float = 0f,
        val color: Long = 0xFFFFFFFFL
    )
    val ELITE_FIRE   = EliteMod("fire", "Огненный", hpMul=1.5f, color=0xFFFF6600L, dot=1, dotDur=3f)
    val ELITE_TOXIC  = EliteMod("toxic", "Ядовитый", hpMul=1.3f, color=0xFF00CC00L, dot=2, dotDur=2f)
    val ELITE_FAST   = EliteMod("fast", "Быстрый", spdMul=1.6f, color=0xFF00CCFFL)
    val ELITE_BERSERK = EliteMod("berserk", "Берсерк", dmgMul=2f, color=0xFFFF0000L)
    val ELITE_SHIELD = EliteMod("shield", "Щитовой", hpMul=2.5f, color=0xFFFFD700L)

    val ELITES = listOf(ELITE_FIRE, ELITE_TOXIC, ELITE_FAST, ELITE_BERSERK, ELITE_SHIELD)

    fun waveEnemies(roomLevel: Int): List<EnemyDef> {
        val pool = mutableListOf(SHAMBLER)
        if (roomLevel >= 1) pool += RUNNER
        if (roomLevel >= 2) pool += CULTIST
        if (roomLevel >= 3) pool += SPITTER
        if (roomLevel >= 4) pool += TANK
        if (roomLevel >= 5) pool += SWARM
        if (roomLevel >= 2) pool += CHARGER
        if (roomLevel >= 3) pool += BRUTE
        return pool
    }

    fun bossForRoom(roomIndex: Int): EnemyDef {
        val hp = 40 + roomIndex * 25
        return EnemyDef(
            id = "boss_$roomIndex", name = "БОСС",
            hp = hp, speed = 65f, r = 40f, dmg = 2, xp = 20,
            burstCount = 10, isBoss = true,
            color = 0xFFDD2040L
        )
    }

    fun randomElite(): EliteMod = ELITES[(Math.random() * ELITES.size).toInt()]
}
