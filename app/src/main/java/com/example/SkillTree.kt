package com.example

enum class SkillBranch { BLOOD, SHADOW, TIME, SHIELD }

data class SkillDef(
    val id: String,
    val name: String,
    val icon: String,
    val branch: SkillBranch,
    val desc: String,
    val maxRank: Int,
    val requires: Map<String, Int> = emptyMap(),
    val levelReq: Int = 0
)

object SkillTree {
    private val allSkills = mutableListOf<SkillDef>()
    private val byId = mutableMapOf<String, SkillDef>()

    init {
        // ── BLOOD branch ──
        s(SkillDef("rage", "\u042f\u0440\u043e\u0441\u0442\u044c", "R", SkillBranch.BLOOD, "+15% \u0443\u0440\u043e\u043d \u0437\u0430 \u0443\u0434\u0430\u0440", 5))
        s(SkillDef("bloodlust", "\u041a\u0440\u043e\u0432\u043e\u0436\u0430\u0434\u043d\u043e\u0441\u0442\u044c", "B", SkillBranch.BLOOD, "+5% \u043a\u0440\u043e\u0432\u043e\u0441\u0442\u044f\u0436", 5, mapOf("rage" to 1)))
        s(SkillDef("blood_burst", "\u041a\u0440\u043e\u0432\u0430\u0432\u043e\u0439 \u0432\u0437\u0440\u044b\u0432", "X", SkillBranch.BLOOD, "\u0423\u0431\u0438\u0439\u0441\u0442\u0432\u043e = AoE \u0432\u0437\u0440\u044b\u0432", 3, mapOf("bloodlust" to 1)))
        s(SkillDef("hunger", "\u0416\u0430\u0436\u0434\u0430 \u043a\u0440\u043e\u0432\u0438", "H", SkillBranch.BLOOD, "+2% \u0443\u0440\u043e\u043d \u0437\u0430 10% \u043f\u043e\u0442\u0435\u0440\u044f\u043d\u043d\u043e\u0433\u043e HP", 3, mapOf("rage" to 3)))
        s(SkillDef("blood_ritual", "\u0420\u0438\u0442\u0443\u0430\u043b \u043a\u0440\u043e\u0432\u0438", "K", SkillBranch.BLOOD, "-20% HP, x3 \u0443\u0440\u043e\u043d 5\u0441", 1, mapOf("bloodlust" to 3)))
        s(SkillDef("blood_shield", "\u041a\u0440\u043e\u0432\u0430\u0432\u0430\u044f \u0431\u0430\u0440\u044c\u0435\u0440\u0430", "S", SkillBranch.BLOOD, "\u0423\u0431\u0438\u0439\u0441\u0442\u0432\u043e = \u0431\u0430\u0440\u044c\u0435\u0440\u0430", 2, mapOf("blood_burst" to 1)))
        s(SkillDef("berserker_rage", "\u042f\u0440\u043e\u0441\u0442\u044c \u0431\u0435\u0440\u0441\u0435\u0440\u043a\u0435\u0440\u0430", "Z", SkillBranch.BLOOD, "HP<30% +50% \u0443\u0440\u043e\u043d", 1, mapOf("hunger" to 2)))
        s(SkillDef("blood_rain", "\u041a\u0440\u043e\u0432\u0430\u0432\u043e\u0439 \u0434\u043e\u0436\u0434\u044c", "Q", SkillBranch.BLOOD, "\u041f\u043e\u043f\u0430\u0434\u043a\u0438 \u043a\u0440\u043e\u0432\u0438 = \u043e\u0433\u043d\u0435\u043d\u043d\u044b\u0439 AoE", 1, mapOf("blood_ritual" to 1)))

        // ── SHADOW branch ──
        s(SkillDef("shadow_step", "\u0422\u0435\u043d\u0435\u0432\u043e\u0439 \u0448\u0430\u0433", "T", SkillBranch.SHADOW, "\u0420\u044b\u0432\u043e\u043a (5\u0441-2\u0441 \u043a\u0434)", 5))
        s(SkillDef("evasion", "\u0423\u043a\u043b\u043e\u043d\u0435\u043d\u0438\u0435", "E", SkillBranch.SHADOW, "+5% \u0443\u043a\u043b\u043e\u043d\u0435\u043d\u0438\u0435", 5, mapOf("shadow_step" to 1)))
        s(SkillDef("shadow_clone", "\u0422\u0435\u043d\u0435\u0432\u043e\u0439 \u043a\u043b\u043e\u043d", "C", SkillBranch.SHADOW, "\u0423\u043a\u043b\u043e\u043d\u0435\u043d\u0438\u0435 = \u0441\u043e\u0437\u0434\u0430\u0435\u0442 \u043a\u043b\u043e\u043d\u0430", 3, mapOf("evasion" to 1)))
        s(SkillDef("dark_theft", "\u0422\u0451\u043c\u043d\u0430\u044f \u043a\u0440\u0430\u0436\u0430", "D", SkillBranch.SHADOW, "\u0421\u043c\u0435\u0440\u0442\u044c \u0432 \u043f\u0440\u0438\u0437\u0440\u0430\u043a\u0435 +50% XP", 3, mapOf("shadow_step" to 3)))
        s(SkillDef("invisibility", "\u041d\u0435\u0432\u0438\u0434\u0438\u043c\u043e\u0441\u0442\u044c", "I", SkillBranch.SHADOW, "3\u0441 \u043d\u0435\u0432\u0438\u0434\u0438\u043c\u043e\u0441\u0442\u0438 \u043f\u043e\u0441\u043b\u0435 \u0440\u044b\u0432\u043a\u0430", 2, mapOf("shadow_clone" to 1)))
        s(SkillDef("shadow_killer", "\u0422\u0435\u043d\u0435\u0432\u043e\u0439 \u0443\u0431\u0438\u0439\u0446\u0430", "K", SkillBranch.SHADOW, "\u0423\u0434\u0430\u0440 \u0438\u0437 \u0442\u0435\u043d\u0438 x3", 1, mapOf("dark_theft" to 2)))
        s(SkillDef("void_grasp", "\u041f\u043e\u0433\u043b\u043e\u0449\u0435\u043d\u0438\u0435 \u043f\u0443\u0441\u0442\u043e\u0442\u044b", "V", SkillBranch.SHADOW, "\u0423\u0431\u0438\u0439\u0441\u0442\u0432\u043e \u043f\u043e\u0434\u0431\u043e\u0440\u043e\u043c = \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u0435", 1, mapOf("invisibility" to 1)))
        s(SkillDef("dark_portal", "\u0422\u0451\u043c\u043d\u044b\u0439 \u043f\u043e\u0440\u0442\u0430\u043b", "P", SkillBranch.SHADOW, "\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442 \u043a \u0441\u043e\u043a\u0440\u043e\u0432\u0438\u0449\u0430\u043c", 1, mapOf("shadow_killer" to 1)))

        // ── TIME branch ──
        s(SkillDef("chronomancy", "\u0425\u0440\u043e\u043d\u043e\u043c\u0430\u043d\u0442\u0438\u043a\u0430", "W", SkillBranch.TIME, "+10% \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0438 \u0441\u0432\u0435\u0442\u043e\u0432\u043e\u0439 \u0440\u0430\u0434\u0438\u0443\u0441", 5))
        s(SkillDef("foresight", "\u041f\u0440\u043e\u0437\u043e\u0440\u043b\u043e\u0441\u0442\u044c", "F", SkillBranch.TIME, "\u0414\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c \u043f\u0440\u043e\u0437\u043e\u0440\u043b\u043e\u0441\u0442\u0438 \u043f\u043e\u0432\u044b\u0448\u0435\u043d\u0430", 3, mapOf("chronomancy" to 1)))
        s(SkillDef("time_slow", "\u0417\u0430\u043c\u0435\u0434\u043b\u0435\u043d\u0438\u0435 \u0432\u0440\u0435\u043c\u0435\u043d\u0438", "O", SkillBranch.TIME, "+15% \u043f\u0440\u043e\u0434\u043e\u043b\u0436\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c \u0437\u0430\u043c\u0435\u0434\u043b\u0435\u043d\u0438\u044f", 5, mapOf("foresight" to 1)))
        s(SkillDef("time_strike", "\u0425\u0440\u043e\u043d\u043e\u0443\u0434\u0430\u0440", "A", SkillBranch.TIME, "\u0423\u0434\u0430\u0440 \u0438\u0437 \u0437\u0430\u043c\u0435\u0434\u043b\u0435\u043d\u0438\u044f = \u0432\u0437\u0440\u044b\u0432", 3, mapOf("time_slow" to 1)))
        s(SkillDef("chrono_shield", "\u0425\u0440\u043e\u043d\u043e-\u0449\u0438\u0442", "N", SkillBranch.TIME, "\u0412 \u0437\u0430\u043c\u0435\u0434\u043b\u0435\u043d\u0438\u0438 -20% \u0443\u0440\u043e\u043d", 3, mapOf("time_strike" to 1)))
        s(SkillDef("time_rift", "\u0425\u0440\u043e\u043d\u043e\u0440\u0430\u0441\u0441\u0435\u043b\u044c\u0441\u043a\u0430\u044f \u043f\u0443\u0441\u0442\u043e\u0442\u0430", "R", SkillBranch.TIME, "\u0417\u0430\u043c\u0435\u0434\u043b\u044f\u0435\u0442 \u0432\u0441\u0435\u0445 \u0432\u0440\u0430\u0433\u043e\u0432", 1, mapOf("time_slow" to 3)))
        s(SkillDef("paradox", "\u041f\u0430\u0440\u0430\u0434\u043e\u043a\u0441", "P", SkillBranch.TIME, "\u0417\u0430\u043c\u0435\u0434\u043b\u0435\u043d\u0438\u0435 \u0432 \u0437\u0430\u043c\u0435\u0434\u043b\u0435\u043d\u0438\u0438 +1\u0441", 1, mapOf("chrono_shield" to 2)))
        s(SkillDef("time_stop", "\u041e\u0441\u0442\u0430\u043d\u043e\u0432\u043a\u0430 \u0432\u0440\u0435\u043c\u0435\u043d\u0438", "S", SkillBranch.TIME, "\u041e\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u044c \u0432\u0440\u0435\u043c\u044f \u043d\u0430 2\u0441", 1, mapOf("paradox" to 1)))

        // ── SHIELD branch ──
        s(SkillDef("aura", "\u0410\u0443\u0440\u0430 \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u044f", "A", SkillBranch.SHIELD, "AoE \u043e\u0440\u043e\u0436\u0438\u0435 \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u044f", 5))
        s(SkillDef("thick_skin", "\u041f\u043b\u043e\u0442\u043d\u0430\u044f \u043a\u043e\u0436\u0430", "T", SkillBranch.SHIELD, "-5% \u0443\u0440\u043e\u043d", 5, mapOf("aura" to 1)))
        s(SkillDef("bone_shield", "\u041a\u043e\u0441\u0442\u044f\u043d\u043e\u0439 \u0449\u0438\u0442", "B", SkillBranch.SHIELD, "\u0412\u043e\u0441\u0441\u0442\u0430\u043d\u0430\u0432\u043b\u0438\u0432\u0430\u0435\u0442 3 \u0431\u0430\u0440\u044c\u0435\u0440\u044b", 3, mapOf("thick_skin" to 1)))
        s(SkillDef("thorns", "\u0428\u0438\u043f\u044b", "H", SkillBranch.SHIELD, "\u041e\u0442\u0440\u0430\u0436\u0435\u043d\u0438\u0435 \u0443\u0434\u0430\u0440\u0430 \u043e\u0442\u0440\u0430\u0436\u0430\u043c\u0438", 3, mapOf("bone_shield" to 1)))
        s(SkillDef("regen", "\u0420\u0435\u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u044f", "G", SkillBranch.SHIELD, "+1 HP/3\u0441", 3, mapOf("thorns" to 1)))
        s(SkillDef("undying", "\u0411\u0435\u0441\u0441\u043c\u0435\u0440\u0442\u043d\u044b\u0439", "U", SkillBranch.SHIELD, "HP=1 \u0432\u043e\u0441\u043a\u0440\u0435\u0441\u0430\u043d\u0438\u0435 1 \u0440\u0430\u0437/\u043a\u043e\u043c\u043d\u0430\u0442\u0443", 1, mapOf("regen" to 2)))
        s(SkillDef("fear_aura", "\u0410\u0443\u0440\u0430 \u0441\u0442\u0440\u0430\u0445\u0430", "F", SkillBranch.SHIELD, "\u0412\u0440\u0430\u0433\u0438 \u0437\u0430\u043c\u0435\u0434\u043b\u0435\u043d\u044b 30%", 1, mapOf("thorns" to 2)))
        s(SkillDef("immortality", "\u0411\u0435\u0441\u0441\u043c\u0435\u0440\u0442\u0438\u0435", "M", SkillBranch.SHIELD, "5\u0441 \u043d\u0435\u0443\u044f\u0437\u0432\u0438\u043c\u043e\u0441\u0442\u0438 (60\u0441 \u043a\u0434)", 1, mapOf("undying" to 1)))
    }

    private fun s(sd: SkillDef) { allSkills.add(sd); byId[sd.id] = sd }
    operator fun get(id: String): SkillDef = byId.getValue(id)
    fun all(): List<SkillDef> = allSkills.toList()

    fun available(ranks: Map<String, Int>, playerLevel: Int): List<SkillDef> {
        return allSkills.filter { sk ->
            (ranks[sk.id] ?: 0) < sk.maxRank &&
            playerLevel >= sk.levelReq &&
            sk.requires.all { (reqId, reqRank) -> (ranks[reqId] ?: 0) >= reqRank }
        }
    }

    fun calcStats(ranks: Map<String, Int>): SkillStats {
        var dmgMul = 1f; var spdMul = 1f; var defMul = 1f
        var lifesteal = 0f; var auraDmg = 0f; var auraRadius = 0f
        var moveInSlow = 0f; var dodgeChance = 0f; var bulletTimeDur = 1f
        var xpMul = 1f; var regenRate = 0f
        var hasBloodRitual = false; var hasShadowStep = false; var shadowStepCd = 5f
        var hasTimeStop = false; var hasBloodBurst = false; var hasShadowClone = false
        var hasInvisibility = false; var hasFearAura = false; var hasParadox = false
        var hasUndying = false; var hasBerserkerRage = false; var hpBonus = 0; var lightBonus = 0f

        for ((id, rank) in ranks) {
            if (rank <= 0) continue
            when (id) {
                "rage" -> dmgMul += rank * 0.15f
                "bloodlust" -> lifesteal += rank * 0.05f
                "blood_burst" -> hasBloodBurst = true
                "blood_ritual" -> hasBloodRitual = true
                "berserker_rage" -> hasBerserkerRage = true
                "bone_shield" -> hpBonus += rank * 5
                "shadow_step" -> { hasShadowStep = true; shadowStepCd = 5f - rank * 0.6f }
                "evasion" -> dodgeChance += rank * 0.05f
                "shadow_clone" -> hasShadowClone = true
                "dark_theft" -> xpMul += rank * 0.5f
                "invisibility" -> hasInvisibility = true
                "chronomancy" -> { moveInSlow += rank * 0.10f; lightBonus += rank * 18f }
                "time_slow" -> bulletTimeDur += rank * 0.15f
                "chrono_shield" -> defMul -= rank * 0.20f
                "paradox" -> hasParadox = true
                "time_stop" -> hasTimeStop = true
                "aura" -> { auraDmg = 1f + rank * 0.5f; auraRadius = 50f + rank * 10f }
                "thick_skin" -> defMul -= rank * 0.05f
                "regen" -> regenRate += rank / 3f
                "undying" -> hasUndying = true
                "fear_aura" -> hasFearAura = true
            }
        }
        defMul = defMul.coerceAtLeast(0.1f)
        return SkillStats(dmgMul, spdMul, defMul, lifesteal, auraDmg, auraRadius,
            moveInSlow, dodgeChance, bulletTimeDur, xpMul, regenRate,
            hasBloodRitual, hasShadowStep, shadowStepCd, hasTimeStop,
            hasBloodBurst, hasShadowClone, hasInvisibility, hasFearAura,
            hasParadox, hasUndying, hasBerserkerRage, lightBonus, hpBonus)
    }
}

data class SkillStats(
    val dmgMul: Float = 1f, val spdMul: Float = 1f, val defMul: Float = 1f,
    val lifesteal: Float = 0f, val auraDmg: Float = 0f, val auraRadius: Float = 0f,
    val moveInSlow: Float = 0f, val dodgeChance: Float = 0f,
    val bulletTimeDur: Float = 1f, val xpMul: Float = 1f, val regenRate: Float = 0f,
    val hasBloodRitual: Boolean = false, val hasShadowStep: Boolean = false,
    val shadowStepCd: Float = 5f, val hasTimeStop: Boolean = false,
    val hasBloodBurst: Boolean = false, val hasShadowClone: Boolean = false,
    val hasInvisibility: Boolean = false, val hasFearAura: Boolean = false,
    val hasParadox: Boolean = false, val hasUndying: Boolean = false,
    val hasBerserkerRage: Boolean = false, val lightBonus: Float = 0f, val hpBonus: Int = 0
)
