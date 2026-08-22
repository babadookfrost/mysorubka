package com.example

enum class ItemRarity { COMMON, UNCOMMON, RARE, ARTIFACT }

data class ItemDef(
    val id: String,
    val name: String,
    val desc: String,
    val rarity: ItemRarity,
    val icon: String
)

data class PlayerItem(
    val def: ItemDef,
    var count: Int = 1
)

object Items {
    private val defs = mutableListOf<ItemDef>()

    init {
        // COMMON
        r(ItemDef("hp_pill", "\u0422\u0430\u0431\u043b\u0435\u0442\u043a\u0430 HP", "+5 HP", ItemRarity.COMMON, "H"))
        r(ItemDef("dmg_pill", "\u0422\u0430\u0431\u043b\u0435\u0442\u043a\u0430 \u0443\u0440\u043e\u043d\u0430", "+8% \u0443\u0440\u043e\u043d", ItemRarity.COMMON, "D"))
        r(ItemDef("speed_pill", "\u0422\u0430\u0431\u043b\u0435\u0442\u043a\u0430 \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u0438", "+8% \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c", ItemRarity.COMMON, "S"))
        r(ItemDef("light_pill", "\u0422\u0430\u0431\u043b\u0435\u0442\u043a\u0430 \u0441\u0432\u0435\u0442\u0430", "+15% \u0440\u0430\u0434\u0438\u0443\u0441 \u0441\u0432\u0435\u0442\u0430", ItemRarity.COMMON, "L"))
        r(ItemDef("armor_pill", "\u0422\u0430\u0431\u043b\u0435\u0442\u043a\u0430 \u0431\u0440\u043e\u043d\u0438", "-8% \u0443\u0440\u043e\u043d", ItemRarity.COMMON, "A"))

        // UNCOMMON
        r(ItemDef("power_gloves", "\u0421\u0438\u043b\u043e\u0432\u044b\u0435 \u043f\u0435\u0440\u0447\u0430\u0442\u043a\u0438", "+20% \u0443\u0440\u043e\u043d \u0431\u043b\u0438\u0436\u043d\u0435\u0433\u043e \u0431\u043e\u044f", ItemRarity.UNCOMMON, "G"))
        r(ItemDef("speed_boots", "\u0411\u043e\u0442\u0438\u043d\u043a\u0438 \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u0438", "+15% \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c", ItemRarity.UNCOMMON, "B"))
        r(ItemDef("blood_amulet", "\u041a\u0440\u043e\u0432\u0430\u0432\u044b\u0439 \u0430\u043c\u0443\u043b\u0435\u0442", "+8% \u043a\u0440\u043e\u0432\u043e\u0441\u0442\u044f\u0436", ItemRarity.UNCOMMON, "M"))
        r(ItemDef("scope", "\u041f\u0440\u0438\u0446\u0435\u043b", "+25% \u0443\u0434\u0430\u043b\u0435\u043d\u0438\u0435 \u0434\u0430\u043b\u044c\u043d\u0435\u0433\u043e \u0431\u043e\u044f", ItemRarity.UNCOMMON, "C"))
        r(ItemDef("iron_ring", "\u0416\u0435\u043b\u0435\u0437\u043d\u044b\u0439 \u043f\u0430\u043b\u044c\u0446", "+20 HP", ItemRarity.UNCOMMON, "R"))

        // RARE
        r(ItemDef("vampire_ring", "\u041a\u043e\u043b\u044c\u0446\u043e \u0432\u0430\u043c\u043f\u0438\u0440\u0430", "+12% \u043a\u0440\u043e\u0432\u043e\u0441\u0442\u044f\u0436", ItemRarity.RARE, "V"))
        r(ItemDef("crimson_eye", "\u041f\u0443\u0441\u0442\u0430\u044f \u043a\u0440\u0430\u0441\u043d\u0430\u044f \u0433\u043b\u0430\u0437", "+30% \u0440\u0430\u0434\u0438\u0443\u0441 \u0441\u0432\u0435\u0442\u0430", ItemRarity.RARE, "E"))
        r(ItemDef("shadow_cloak", "\u041f\u043b\u0430\u0449 \u0442\u0435\u043d\u0438", "+15% \u0443\u043a\u043b\u043e\u043d\u0435\u043d\u0438\u0435", ItemRarity.RARE, "K"))
        r(ItemDef("berserker_belt", "\u041f\u043e\u044f\u0441 \u0431\u0435\u0440\u0441\u0435\u0440\u043a\u0435\u0440\u0430", "+25% \u0443\u0440\u043e\u043d \u043f\u0440\u0438 HP<50%", ItemRarity.RARE, "Z"))
        r(ItemDef("chrono_watch", "\u0427\u0430\u0441\u044b \u0445\u0440\u043e\u043d\u043e\u043c\u0430\u043d\u0442\u0430", "+20% bullet-time", ItemRarity.RARE, "W"))

        // ARTIFACT
        r(ItemDef("philosopher_stone", "\u041a\u0430\u043c\u0435\u043d\u044c \u0444\u0438\u043b\u043e\u0441\u043e\u0444\u0430", "x2 XP \u043f\u043e\u0441\u0442\u043e\u044f\u043d\u043d\u043e", ItemRarity.ARTIFACT, "P"))
        r(ItemDef("void_heart", "\u0421\u0435\u0440\u0434\u0446\u0435 \u043f\u0443\u0441\u0442\u043e\u0442\u044b", "\u0410\u0443\u0440\u0430 \u0432\u0440\u0430\u0433\u043e\u0432, -1 HP/\u0441", ItemRarity.ARTIFACT, "Q"))
        r(ItemDef("bone_crown", "\u041a\u043e\u0441\u0442\u044f\u043d\u0430\u044f \u043a\u043e\u0440\u043e\u043d\u0430", "+50 HP, -10% \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c", ItemRarity.ARTIFACT, "X"))
        r(ItemDef("soul_gem", "\u0413\u0435\u043c \u0434\u0443\u0448", "\u0421\u043b\u043e\u0442 \u043f\u043e\u0434\u0431\u043e\u0440\u0430 +2 \u044f\u0447\u0435\u0439 \u043e\u043f\u044b\u0442\u0430", ItemRarity.ARTIFACT, "J"))
    }

    private fun r(d: ItemDef) { defs.add(d) }

    fun all(): List<ItemDef> = defs.toList()

    fun rollForEnemy(roomLevel: Int): ItemDef? {
        val chance = 0.08f + roomLevel * 0.01f
        if (Math.random() > chance) return null
        val rarity = rollRarity()
        val pool = defs.filter { it.rarity == rarity }
        return pool.ifEmpty { null }?.random()
    }

    fun rollForChest(): ItemDef {
        val rarity = rollRarity()
        val pool = defs.filter { it.rarity == rarity }
        return pool.ifEmpty { defs.filter { it.rarity == ItemRarity.COMMON } }!!.random()
    }

    private fun rollRarity(): ItemRarity {
        val roll = Math.random()
        return when {
            roll < 0.03f -> ItemRarity.ARTIFACT
            roll < 0.15f -> ItemRarity.RARE
            roll < 0.40f -> ItemRarity.UNCOMMON
            else -> ItemRarity.COMMON
        }
    }

    fun rarityColor(r: ItemRarity): Long = when (r) {
        ItemRarity.COMMON   -> 0xFFAAAAL
        ItemRarity.UNCOMMON -> 0xFF4CAF50L
        ItemRarity.RARE     -> 0xFF2196F3L
        ItemRarity.ARTIFACT -> 0xFFFFD700L
    }
}
