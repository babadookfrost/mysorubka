package com.example

import kotlin.math.cos
import kotlin.math.sin

// ─── Weapon definition ──────────────────────────────────────────────
enum class WpnCategory { MELEE, PISTOL, SHOTGUN, AUTO, SPECIAL }

data class WeaponDef(
    val id: String,
    val name: String,
    val icon: String,
    val category: WpnCategory,
    val cd: Float,          // cooldown between shots (sec)
    val pellets: Int,       // bullets per shot
    val spread: Float,      // total angular spread (rad)
    val jitter: Float,      // random aim offset
    val speed: Float,       // projectile speed (px/s)
    val dmg: Int,
    val ammoMax: Int,       // -1 = infinite
    val reload: Float,      // reload time (sec), 0 = no reload
    val pierce: Int,        // how many enemies a bullet penetrates
    val auto: Boolean,      // auto-fire while held
    val meleeRange: Float = 0f,   // 0 = ranged
    val meleeArc: Float = 0f,     // radians for melee swing
    val splash: Float = 0f,       // AoE radius, 0 = none
    val dot: Int = 0,             // damage over time ticks
    val dotDur: Float = 0f
)

// ─── All weapons ────────────────────────────────────────────────────
object Weapons {
    private val defs = mutableMapOf<String, WeaponDef>()

    init {
        register(WeaponDef(
            id = "knife", name = "Нож", icon = "🔪",
            category = WpnCategory.MELEE,
            cd = 0.28f, pellets = 1, spread = 0f, jitter = 0f,
            speed = 0f, dmg = 2, ammoMax = -1, reload = 0f,
            pierce = 1, auto = true, meleeRange = 42f, meleeArc = 1.2f
        ))
        register(WeaponDef(
            id = "knuckleduster", name = "Кастет", icon = "👊",
            category = WpnCategory.MELEE,
            cd = 0.18f, pellets = 1, spread = 0f, jitter = 0f,
            speed = 0f, dmg = 1, ammoMax = -1, reload = 0f,
            pierce = 1, auto = true, meleeRange = 34f, meleeArc = 0.9f
        ))
        register(WeaponDef(
            id = "chainsaw", name = "Бензопила", icon = "🪚",
            category = WpnCategory.MELEE,
            cd = 0.10f, pellets = 1, spread = 0f, jitter = 0f,
            speed = 0f, dmg = 3, ammoMax = -1, reload = 0f,
            pierce = 1, auto = true, meleeRange = 48f, meleeArc = 1.4f
        ))
        register(WeaponDef(
            id = "revolver", name = "Револьвер", icon = "🔫",
            category = WpnCategory.PISTOL,
            cd = 0.35f, pellets = 1, spread = 0.02f, jitter = 0.01f,
            speed = 1100f, dmg = 2, ammoMax = 6, reload = 1.2f,
            pierce = 1, auto = false
        ))
        register(WeaponDef(
            id = "magnum", name = "Магнум", icon = "💀",
            category = WpnCategory.PISTOL,
            cd = 0.60f, pellets = 1, spread = 0.01f, jitter = 0f,
            speed = 1200f, dmg = 5, ammoMax = 5, reload = 1.6f,
            pierce = 2, auto = false
        ))
        register(WeaponDef(
            id = "crossbow", name = "Арбалет", icon = "🏹",
            category = WpnCategory.PISTOL,
            cd = 0.70f, pellets = 1, spread = 0f, jitter = 0f,
            speed = 900f, dmg = 4, ammoMax = 3, reload = 1.4f,
            pierce = 99, auto = false
        ))
        register(WeaponDef(
            id = "shotgun", name = "Дробовик", icon = "💥",
            category = WpnCategory.SHOTGUN,
            cd = 0.85f, pellets = 7, spread = 0.30f, jitter = 0.04f,
            speed = 850f, dmg = 1, ammoMax = 2, reload = 1.8f,
            pierce = 1, auto = false
        ))
        register(WeaponDef(
            id = "smg", name = "Автомат", icon = "🔹",
            category = WpnCategory.AUTO,
            cd = 0.09f, pellets = 1, spread = 0.04f, jitter = 0.08f,
            speed = 1200f, dmg = 1, ammoMax = 30, reload = 1.6f,
            pierce = 1, auto = true
        ))
        register(WeaponDef(
            id = "lmg", name = "Пулемёт", icon = "⚙️",
            category = WpnCategory.AUTO,
            cd = 0.05f, pellets = 1, spread = 0.06f, jitter = 0.12f,
            speed = 1300f, dmg = 1, ammoMax = 80, reload = 2.4f,
            pierce = 1, auto = true
        ))
        register(WeaponDef(
            id = "molotov", name = "Молотов", icon = "🔥",
            category = WpnCategory.SPECIAL,
            cd = 1.0f, pellets = 1, spread = 0.05f, jitter = 0f,
            speed = 600f, dmg = 2, ammoMax = 3, reload = 1.6f,
            pierce = 1, auto = false, splash = 100f, dot = 2, dotDur = 3.0f
        ))
        register(WeaponDef(
            id = "plasma", name = "Плазмоган", icon = "⚡",
            category = WpnCategory.SPECIAL,
            cd = 0.50f, pellets = 1, spread = 0f, jitter = 0f,
            speed = 700f, dmg = 3, ammoMax = 12, reload = 1.4f,
            pierce = 3, auto = true, splash = 50f
        ))
        register(WeaponDef(
            id = "soul_eater", name = "Пожиратель душ", icon = "👻",
            category = WpnCategory.SPECIAL,
            cd = 0.80f, pellets = 3, spread = 0.15f, jitter = 0f,
            speed = 800f, dmg = 2, ammoMax = 8, reload = 1.8f,
            pierce = 2, auto = false
        ))
        register(WeaponDef(
            id = "scorpion_sting", name = "Жало скорпиона", icon = "🦂",
            category = WpnCategory.SPECIAL,
            cd = 0.40f, pellets = 2, spread = 0.10f, jitter = 0f,
            speed = 1000f, dmg = 2, ammoMax = 15, reload = 1.2f,
            pierce = 1, auto = true, dot = 1, dotDur = 2.0f
        ))
        register(WeaponDef(
            id = "reaper_scythe", name = "Коса Жнеца", icon = "☠️",
            category = WpnCategory.MELEE,
            cd = 0.65f, pellets = 1, spread = 0f, jitter = 0f,
            speed = 0f, dmg = 8, ammoMax = -1, reload = 0f,
            pierce = 99, auto = false, meleeRange = 70f, meleeArc = 2.2f
        ))
        register(WeaponDef(
            id = "flamethrower", name = "Огнемёт", icon = "✦",
            category = WpnCategory.SPECIAL,
            cd = 0.07f, pellets = 1, spread = 0.10f, jitter = 0.14f,
            speed = 700f, dmg = 1, ammoMax = 60, reload = 2.0f,
            pierce = 1, auto = true, splash = 55f, dot = 1, dotDur = 1.5f
        ))
        register(WeaponDef(
            id = "railgun", name = "Рельсотрон", icon = "✚",
            category = WpnCategory.SPECIAL,
            cd = 1.20f, pellets = 1, spread = 0f, jitter = 0f,
            speed = 2400f, dmg = 6, ammoMax = 4, reload = 2.2f,
            pierce = 99, auto = false, splash = 0f
        ))
    }

    private fun register(w: WeaponDef) { defs[w.id] = w }

    operator fun get(id: String): WeaponDef = defs.getValue(id)
    fun all(): List<WeaponDef> = defs.values.toList()
    fun starterIds(): List<String> = listOf("knife", "revolver")
    fun dropPool(): List<String> = listOf(
        "knuckleduster", "chainsaw", "magnum", "crossbow",
        "shotgun", "smg", "lmg", "molotov", "plasma",
        "soul_eater", "scorpion_sting", "reaper_scythe",
        "flamethrower", "railgun"
    )
}
